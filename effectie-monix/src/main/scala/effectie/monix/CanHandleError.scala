package effectie.monix

import cats.Id
import cats.effect.IO
import monix.eval.Task

import scala.util.control.NonFatal

/** @author Kevin Lee
  * @since 2020-08-17
  */
object CanHandleError {

  type CanHandleError[F[_]] = effectie.CanHandleError[F]

  implicit object TaskCanHandleError extends CanHandleError[Task] {

    @inline override final def handleNonFatalWith[A, AA >: A](fa: => Task[A])(
      handleError: Throwable => Task[AA]
    ): Task[AA] =
      fa.onErrorHandleWith(handleError)

    @inline override final def handleNonFatal[A, AA >: A](fa: => Task[A])(handleError: Throwable => AA): Task[AA] =
      handleNonFatalWith[A, AA](fa)(err => Task.pure(handleError(err)))

  }

  implicit object IoCanHandleError extends CanHandleError[IO] {

    @inline override final def handleNonFatalWith[A, AA >: A](fa: => IO[A])(handleError: Throwable => IO[AA]): IO[AA] =
      fa.handleErrorWith(handleError)

    @inline override final def handleNonFatal[A, AA >: A](fa: => IO[A])(handleError: Throwable => AA): IO[AA] =
      handleNonFatalWith[A, AA](fa)(err => IO.pure(handleError(err)))

  }

  implicit object IdCanHandleError extends CanHandleError[Id] {

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
