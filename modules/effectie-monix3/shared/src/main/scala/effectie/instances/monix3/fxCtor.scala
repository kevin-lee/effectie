package effectie.instances.monix3

import effectie.core.FxCtor
import monix.eval.Task

import scala.util.Try
import scala.util.control.NonFatal

/** @author Kevin Lee
  * @since 2021-05-16
  */
object fxCtor {

  implicit object taskFxCtor extends FxCtor[Task] {

    @inline override final def effectOf[A](a: => A): Task[A] = Task(a)

    @inline override final def fromEffect[A](fa: => Task[A]): Task[A] = Task.defer(fa)

    @inline override final def pureOf[A](a: A): Task[A] = Task.now(a)

    /** It cannot be done by
      * {{{
      * MonadThrow[Task].catchNonFatal(1)
      * }}}
      * as it returns Eval(thunk) which is the same as Task.delay whereas
      * {{{
      * MonadThrow[IO].catchNonFatal(1)
      * }}}
      * returns Pure(1)
      * So the implementation of fxCtor.pureOrError for Task doesn't use MonadThrow.
      */
    @inline override final def pureOrError[A](a: => A): Task[A] =
      try pureOf(a)
      catch {
        case NonFatal(e) => errorOf(e)
      }

    @inline override val unitOf: Task[Unit] = Task.unit

    @inline override final def errorOf[A](throwable: Throwable): Task[A] = Task.raiseError(throwable)

    @inline override final def fromEither[A](either: Either[Throwable, A]): Task[A] = Task.fromEither(either)

    @inline override final def fromOption[A](option: Option[A])(orElse: => Throwable): Task[A] =
      option.fold(errorOf[A](orElse))(pureOf)

    @inline override final def fromTry[A](tryA: Try[A]): Task[A] = Task.fromTry(tryA)

    @inline override final def flatMapFa[A, B](fa: Task[A])(f: A => Task[B]): Task[B] = fa.flatMap(f)
  }

}
