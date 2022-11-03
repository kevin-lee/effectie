package effectie.instances.ce2

import cats.effect.IO
import effectie.core.CanHandleError

/** @author Kevin Lee
  * @since 2020-08-17
  */
object canHandleError {

  implicit object ioCanHandleError extends CanHandleError[IO] {

    @inline override final def handleNonFatalWith[A, AA >: A](fa: => IO[A])(handleError: Throwable => IO[AA]): IO[AA] =
      fa.handleErrorWith(handleError)

    @inline override final def handleNonFatal[A, AA >: A](fa: => IO[A])(handleError: Throwable => AA): IO[AA] =
      handleNonFatalWith[A, AA](fa)(err => IO.pure(handleError(err)))

  }

}
