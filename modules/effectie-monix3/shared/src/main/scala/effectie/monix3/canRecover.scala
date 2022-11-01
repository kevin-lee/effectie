package effectie.monix3

import cats._
import cats.effect.IO
import effectie.core.CanRecover
import monix.eval.Task

/** @author Kevin Lee
  * @since 2020-08-17
  */
object canRecover {

  implicit object taskCanRecover extends CanRecover[Task] {
    @inline override final def recoverFromNonFatalWith[A, AA >: A](
      fa: => Task[A]
    )(handleError: PartialFunction[Throwable, Task[AA]]): Task[AA] =
      fa.onErrorRecoverWith(handleError)

    @inline override final def recoverFromNonFatal[A, AA >: A](
      fa: => Task[A]
    )(handleError: PartialFunction[Throwable, AA]): Task[AA] =
      recoverFromNonFatalWith[A, AA](fa)(handleError.andThen(Task.pure(_)))

  }

  implicit object ioCanRecover extends CanRecover[IO] {
    @inline override final def recoverFromNonFatalWith[A, AA >: A](
      fa: => IO[A]
    )(handleError: PartialFunction[Throwable, IO[AA]]): IO[AA] =
      fa.handleErrorWith(err => handleError.applyOrElse(err, ApplicativeError[IO, Throwable].raiseError[AA]))

    @inline override final def recoverFromNonFatal[A, AA >: A](
      fa: => IO[A]
    )(handleError: PartialFunction[Throwable, AA]): IO[AA] =
      recoverFromNonFatalWith[A, AA](fa)(handleError.andThen(IO.pure(_)))

  }

}
