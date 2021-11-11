package effectie.cats

import cats.Id
import cats.data.EitherT
import cats.effect.IO

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

/** @author Kevin Lee
  * @since 2020-08-17
  */
trait CanHandleError[F[_]] extends effectie.CanHandleError[F] {

  type XorT[A, B]  = EitherT[F, A, B]

  @inline override final protected def xorT[A, B](fab: F[Either[A, B]]): EitherT[F, A, B] =
    EitherT(fab)

  @inline override final protected def xorT2FEither[A, B](efab: EitherT[F, A, B]): F[Either[A, B]] =
    efab.value

}

object CanHandleError {

  def apply[F[_]: CanHandleError]: CanHandleError[F] = implicitly[CanHandleError[F]]

  implicit object IoCanHandleError extends CanHandleError[IO] {

    override def handleNonFatalWith[A, AA >: A](fa: => IO[A])(handleError: Throwable => IO[AA]): IO[AA] =
      fa.handleErrorWith(handleError)

    override def handleNonFatal[A, AA >: A](fa: => IO[A])(handleError: Throwable => AA): IO[AA] =
      handleNonFatalWith[A, AA](fa)(err => IO.pure(handleError(err)))

  }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def futureCanHandleError(implicit ec: ExecutionContext): CanHandleError[Future] =
    new effectie.CanHandleError.FutureCanHandleError(ec) with CanHandleError[Future]

  implicit object IdCanHandleError extends CanHandleError[Id] {

    override def handleNonFatalWith[A, AA >: A](fa: => Id[A])(handleError: Throwable => Id[AA]): Id[AA] =
      try (fa)
      catch {
        case NonFatal(ex) =>
          handleError(ex)
      }

    override def handleNonFatal[A, AA >: A](fa: => Id[A])(handleError: Throwable => AA): Id[AA] =
      handleNonFatalWith[A, AA](fa)(err => handleError(err))

  }

}
