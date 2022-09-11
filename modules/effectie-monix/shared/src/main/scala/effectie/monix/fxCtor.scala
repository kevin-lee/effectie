package effectie.monix

import effectie.core.FxCtor
import monix.eval.Task

import scala.util.Try

/** @author Kevin Lee
  * @since 2021-05-16
  */
object fxCtor {

  implicit object taskFxCtor extends FxCtor[Task] {

    @inline override final def effectOf[A](a: => A): Task[A] = Task(a)

    @inline override final def pureOf[A](a: A): Task[A] = Task.now(a)

    @inline override val unitOf: Task[Unit] = Task.unit

    @inline override final def errorOf[A](throwable: Throwable): Task[A] = Task.raiseError(throwable)

    @inline override final def fromEither[A](either: Either[Throwable, A]): Task[A] = Task.fromEither(either)

    @inline override final def fromOption[A](option: Option[A])(orElse: => Throwable): Task[A] =
      option.fold(errorOf[A](orElse))(pureOf)

    @inline override final def fromTry[A](tryA: Try[A]): Task[A] = Task.fromTry(tryA)

  }

}
