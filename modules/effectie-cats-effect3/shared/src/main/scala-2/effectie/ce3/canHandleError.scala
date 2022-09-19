package effectie.ce3

import cats.Id
import cats.effect.IO
import effectie.core.CanHandleError

import scala.util.control.NonFatal

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

  implicit object idCanHandleError extends CanHandleError[Id] {

    @inline override final def handleNonFatalWith[A, AA >: A](fa: => Id[A])(handleError: Throwable => Id[AA]): Id[AA] =
      try (fa)
      catch {
        case NonFatal(ex) =>
          handleError(ex)
      }

    @inline override final def handleNonFatal[A, AA >: A](fa: => Id[A])(handleError: Throwable => AA): Id[AA] =
      handleNonFatalWith[A, AA](fa)(err => handleError(err))

  }

}
