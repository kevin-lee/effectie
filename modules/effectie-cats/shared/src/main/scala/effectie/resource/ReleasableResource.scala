package effectie.resource

import cats.MonadError
import cats.syntax.all._
import effectie.core.FxCtor

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/** A pure description of resource acquisition and release.
  *
  * Constructing a `ReleasableResource` performs no effects. Acquisition happens each time [[use]] is run, and release
  * is guaranteed to run afterwards, on success and on error, in LIFO order.
  *
  * Running [[use]] requires an implicit [[UseResource]] instance for the effect type. Instances for `Try` and `Future`
  * are provided automatically; for cats-effect and Monix, import the instance from the corresponding
  * `effectie.instances` package.
  *
  * @author Kevin Lee
  * @since 2022-10-30
  */
sealed abstract class ReleasableResource[F[*], +A] {

  import ReleasableResource._

  final def use[B](f: A => F[B])(implicit useResource: UseResource[F]): F[B] =
    useResource.use(this)(f)

  final def use_(implicit useResource: UseResource[F]): F[Unit] =
    use(_ => useResource.unitOf)

  final def surround[B](fb: => F[B])(implicit useResource: UseResource[F]): F[B] =
    use(_ => fb)

  final def map[B](f: A => B): ReleasableResource[F, B] =
    flatMap(a => ReleasableResource.pure(f(a)))

  final def flatMap[B](f: A => ReleasableResource[F, B]): ReleasableResource[F, B] =
    Bind(this, f)

  final def ap[B](ff: ReleasableResource[F, A => B]): ReleasableResource[F, B] =
    ff.flatMap(f => map(f))

  final def evalMap[B](f: A => F[B]): ReleasableResource[F, B] =
    flatMap(a => ReleasableResource.eval(f(a)))

  final def evalTap(f: A => F[Unit]): ReleasableResource[F, A] =
    flatMap(a => ReleasableResource.eval(f(a)).map(_ => a))

  final def flatTap[B](f: A => ReleasableResource[F, B]): ReleasableResource[F, A] =
    flatMap(a => f(a).map(_ => a))

  final def onFinalize(finalizer: => F[Unit]): ReleasableResource[F, A] =
    onFinalizeCase(_ => finalizer)

  /** The given finalizer runs when this resource's scope is closed, after this resource's own release actions (LIFO),
    * with the [[ReleasableResource.ExitCase]] of the whole scope.
    */
  final def onFinalizeCase(finalizer: ExitCase => F[Unit]): ReleasableResource[F, A] =
    OnFinalizeCase(this, finalizer)

  /** Attempts this resource's own effects (acquisition and eval). Errors raised by the `use` function or by release
    * actions are not covered.
    */
  final def attempt: ReleasableResource[F, Either[Throwable, A]] =
    map(a => (a: A).asRight[Throwable]).handleErrorWith(err => ReleasableResource.pure(err.asLeft[A]))

  /** Handles errors raised by this resource's own effects (acquisition and eval). Errors raised by the `use` function
    * or by release actions are not covered. Resources already acquired inside this resource before the failure keep
    * their finalizers registered in the surrounding scope; they are released when the whole scope closes.
    */
  final def handleErrorWith[AA >: A](f: Throwable => ReleasableResource[F, AA]): ReleasableResource[F, AA] =
    HandleErrorWith[F, AA](this, f)

}

object ReleasableResource {

  def pure[F[*], A](a: A): ReleasableResource[F, A] = Pure(a)

  def eval[F[*], A](fa: => F[A]): ReleasableResource[F, A] = Eval(() => fa)

  def make[F[*], A](acquire: => F[A])(release: A => F[Unit]): ReleasableResource[F, A] =
    makeCase(acquire)((a, _) => release(a))

  def makeCase[F[*], A](acquire: => F[A])(release: (A, ExitCase) => F[Unit]): ReleasableResource[F, A] =
    Allocate(() => acquire, release)

  def fromAutoCloseable[F[*], A <: AutoCloseable](acquire: => F[A])(
    implicit fxCtor: FxCtor[F]
  ): ReleasableResource[F, A] =
    makeCase(acquire)((a, _) => fxCtor.effectOf(a.close()))

  def raiseError[F[*]](error: Throwable): ReleasableResource[F, Nothing] = Fail(error)

  def unit[F[*]]: ReleasableResource[F, Unit] = pure(())

  @deprecated(
    message = "Use ReleasableResource.fromAutoCloseable[Try, A](Try(acquire)) instead. " +
      "The UseResource[Try] instance for running .use(...) is provided automatically.",
    since = "2.5.0",
  )
  def usingResource[A <: AutoCloseable](acquire: => A): ReleasableResource[Try, A] =
    fromAutoCloseable[Try, A](Try(acquire))(effectie.instances.tries.fxCtor.fxCtorTry)

  @deprecated(
    message = "Use ReleasableResource.fromAutoCloseable[Try, A](resource) instead. " +
      "The UseResource[Try] instance for running .use(...) is provided automatically.",
    since = "2.5.0",
  )
  def usingResourceFromTry[A <: AutoCloseable](resource: Try[A]): ReleasableResource[Try, A] =
    fromAutoCloseable[Try, A](resource)(effectie.instances.tries.fxCtor.fxCtorTry)

  @deprecated(
    message = "Use ReleasableResource.fromAutoCloseable[Future, A](acquire) instead. " +
      "The UseResource[Future] instance for running .use(...) is provided automatically " +
      "(an implicit ExecutionContext is required).",
    since = "2.5.0",
  )
  def futureResource[A <: AutoCloseable](acquire: Future[A])(
    implicit ec: ExecutionContext
  ): ReleasableResource[Future, A] =
    fromAutoCloseable[Future, A](acquire)(effectie.instances.future.fxCtor.fxCtorFuture)

  @deprecated(
    message = "Use ReleasableResource.make[Try, A](resource)(release) instead.",
    since = "2.5.0",
  )
  def makeTry[A](resource: => Try[A])(release: A => Try[Unit]): ReleasableResource[Try, A] =
    make[Try, A](resource)(release)

  @deprecated(
    message = "Use ReleasableResource.pure[Try, A](resource) instead.",
    since = "2.5.0",
  )
  def pureTry[A](resource: A): ReleasableResource[Try, A] =
    pure[Try, A](resource)

  @deprecated(
    message = "Use ReleasableResource.make[Future, A](acquire)(release) instead.",
    since = "2.5.0",
  )
  def makeFuture[A](acquire: Future[A])(release: A => Future[Unit])(
    implicit ec: ExecutionContext
  ): ReleasableResource[Future, A] =
    make[Future, A](acquire)(release)

  @deprecated(
    message = "Use ReleasableResource.pure[Future, A](acquire) instead.",
    since = "2.5.0",
  )
  def pureFuture[A](acquire: A)(
    implicit ec: ExecutionContext
  ): ReleasableResource[Future, A] =
    pure[Future, A](acquire)

  /** The outcome of a [[ReleasableResource]]'s scope, passed to release actions registered with
    * [[ReleasableResource.makeCase]] and [[ReleasableResource!.onFinalizeCase]].
    *
    * [[ExitCase.Canceled]] can only occur with effect types that support cancellation (e.g. cats-effect `IO`, Monix
    * `Task`); it never occurs for `Try` or `Future`.
    */
  sealed trait ExitCase
  object ExitCase {
    case object Completed extends ExitCase
    final case class Errored(error: Throwable) extends ExitCase
    case object Canceled extends ExitCase

    def completed: ExitCase = Completed

    def errored(error: Throwable): ExitCase = Errored(error)

    def canceled: ExitCase = Canceled
  }

  private[resource] final case class Pure[F[*], A](a: A) extends ReleasableResource[F, A]

  private[resource] final case class Eval[F[*], A](fa: () => F[A]) extends ReleasableResource[F, A]

  private[resource] final case class Fail[F[*]](error: Throwable) extends ReleasableResource[F, Nothing]

  private[resource] final case class Allocate[F[*], A](
    acquire: () => F[A],
    release: (A, ExitCase) => F[Unit],
  ) extends ReleasableResource[F, A]

  private[resource] final case class Bind[F[*], S, A](
    source: ReleasableResource[F, S],
    f: S => ReleasableResource[F, A],
  ) extends ReleasableResource[F, A]

  private[resource] final case class HandleErrorWith[F[*], A](
    source: ReleasableResource[F, A],
    f: Throwable => ReleasableResource[F, A],
  ) extends ReleasableResource[F, A]

  private[resource] final case class OnFinalizeCase[F[*], A](
    source: ReleasableResource[F, A],
    finalizer: ExitCase => F[Unit],
  ) extends ReleasableResource[F, A]

  implicit def monadErrorReleasableResource[F[*]]: MonadError[ReleasableResource[F, *], Throwable] =
    new MonadError[ReleasableResource[F, *], Throwable] {

      override def pure[A](a: A): ReleasableResource[F, A] = ReleasableResource.pure(a)

      override def flatMap[A, B](fa: ReleasableResource[F, A])(
        f: A => ReleasableResource[F, B]
      ): ReleasableResource[F, B] = fa.flatMap(f)

      override def tailRecM[A, B](a: A)(f: A => ReleasableResource[F, Either[A, B]]): ReleasableResource[F, B] =
        f(a).flatMap {
          case Left(nextA) => tailRecM(nextA)(f)
          case Right(b) => ReleasableResource.pure(b)
        }

      override def raiseError[A](e: Throwable): ReleasableResource[F, A] = ReleasableResource.raiseError(e)

      override def handleErrorWith[A](fa: ReleasableResource[F, A])(
        f: Throwable => ReleasableResource[F, A]
      ): ReleasableResource[F, A] = fa.handleErrorWith(f)
    }

}
