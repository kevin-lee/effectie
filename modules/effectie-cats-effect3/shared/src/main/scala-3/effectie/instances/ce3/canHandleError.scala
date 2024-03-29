package effectie.instances.ce3

import cats.data.EitherT
import cats.effect.IO
import effectie.core.CanHandleError

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

/** @author Kevin Lee
  * @since 2020-08-17
  */
object canHandleError {

  given ioCanHandleError: CanHandleError[IO] with {

    inline override def handleNonFatalWith[A, AA >: A](fa: => IO[A])(handleError: Throwable => IO[AA]): IO[AA] =
      fa.handleErrorWith(handleError)

    inline override def handleNonFatal[A, AA >: A](fa: => IO[A])(handleError: Throwable => AA): IO[AA] =
      handleNonFatalWith[A, AA](fa)(err => IO.pure(handleError(err)))

  }

}
