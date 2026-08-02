package effectie.resource

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/** @author Kevin Lee
  * @since 2023-01-14
  */
@deprecated(
  message = "ResourceMaker is deprecated and will be removed in a future release.\n" +
    "ReleasableResource no longer needs a maker - construct it directly:\n" +
    "  ResourceMaker[F].make(fa)(release)    => ReleasableResource.make(fa)(release)\n" +
    "  ResourceMaker[F].eval(fa)             => ReleasableResource.eval(fa)\n" +
    "  ResourceMaker[F].pure(a)              => ReleasableResource.pure(a)\n" +
    "  ResourceMaker[F].forAutoCloseable(fa) => ReleasableResource.fromAutoCloseable(fa) (requires FxCtor[F])\n" +
    "Running .use(...) requires an implicit effectie.resource.UseResource[F]:\n" +
    "  Try / Future  - provided automatically (companion object instances)\n" +
    "  cats-effect 3 - import effectie.instances.ce3.resource._ (IO) or effectie.instances.ce3.f.resource._ (any F[_]: MonadCancelThrow)\n" +
    "  cats-effect 2 - import effectie.instances.ce2.resource._ (IO) or effectie.instances.ce2.f.resource._ (any F[_]: BracketThrow)\n" +
    "  Monix 3 Task  - import effectie.instances.monix3.resource._\n" +
    "  (for Scala 3, use .given instead of ._ e.g. import effectie.instances.ce3.resource.given)",
  since = "2.5.0",
)
trait ResourceMaker[F[*]] {
  def forAutoCloseable[A <: AutoCloseable](fa: F[A]): ReleasableResource[F, A]

  def make[A](fa: => F[A])(release: A => F[Unit]): ReleasableResource[F, A]

  def pure[A](a: A): ReleasableResource[F, A]

  def eval[A](fa: F[A]): ReleasableResource[F, A]
}
object ResourceMaker {

  @deprecated(
    message = "ResourceMaker is deprecated. Construct ReleasableResource directly instead " +
      "(ReleasableResource.make/eval/pure/fromAutoCloseable).",
    since = "2.5.0",
  )
  def apply[F[*]: ResourceMaker]: ResourceMaker[F] = implicitly[ResourceMaker[F]]

  @deprecated(
    message = "ResourceMaker is deprecated. Construct ReleasableResource directly instead " +
      "(ReleasableResource.make/eval/pure/fromAutoCloseable). " +
      "The UseResource[Try] instance for running .use(...) is provided automatically.",
    since = "2.5.0",
  )
  val tryResourceMaker: ResourceMaker[Try] = new UsingResourceMaker

  @deprecated(message = "Please use ResourceMaker.tryResourceMaker instead.", since = "2.0.0-beta10")
  @inline val usingResourceMaker: ResourceMaker[Try] = tryResourceMaker

  @deprecated(message = "Use ReleasableResource directly instead of ResourceMaker.", since = "2.5.0")
  private final class UsingResourceMaker extends ResourceMaker[Try] {
    override def forAutoCloseable[A <: AutoCloseable](fa: Try[A]): ReleasableResource[Try, A] =
      ReleasableResource.fromAutoCloseable[Try, A](fa)(effectie.instances.tries.fxCtor.fxCtorTry)

    override def make[A](fa: => Try[A])(release: A => Try[Unit]): ReleasableResource[Try, A] =
      ReleasableResource.make[Try, A](fa)(release)

    override def pure[A](a: A): ReleasableResource[Try, A] = ReleasableResource.pure(a)

    override def eval[A](fa: Try[A]): ReleasableResource[Try, A] = ReleasableResource.eval(fa)
  }

  @deprecated(
    message = "ResourceMaker is deprecated. Construct ReleasableResource directly instead " +
      "(ReleasableResource.make/eval/pure/fromAutoCloseable). " +
      "The UseResource[Future] instance for running .use(...) is provided automatically " +
      "(an implicit ExecutionContext is required).",
    since = "2.5.0",
  )
  def futureResourceMaker(implicit ec: ExecutionContext): ResourceMaker[Future] =
    new FutureResourceMaker

  @deprecated(message = "Use ReleasableResource directly instead of ResourceMaker.", since = "2.5.0")
  private final class FutureResourceMaker(implicit ec: ExecutionContext) extends ResourceMaker[Future] {
    override def forAutoCloseable[A <: AutoCloseable](fa: Future[A]): ReleasableResource[Future, A] =
      ReleasableResource.fromAutoCloseable[Future, A](fa)(effectie.instances.future.fxCtor.fxCtorFuture)

    override def make[A](fa: => Future[A])(release: A => Future[Unit]): ReleasableResource[Future, A] =
      ReleasableResource.make[Future, A](fa)(release)

    override def pure[A](a: A): ReleasableResource[Future, A] = ReleasableResource.pure(a)

    override def eval[A](fa: Future[A]): ReleasableResource[Future, A] = ReleasableResource.eval(fa)
  }
}
