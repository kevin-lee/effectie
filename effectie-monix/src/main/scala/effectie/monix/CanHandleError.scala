package effectie.monix

import cats.Id
import cats.data.EitherT
import monix.eval.Task

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

/** @author Kevin Lee
  * @since 2020-08-17
  */
trait CanHandleError[F[_]] extends effectie.CanHandleError[F] {

  type XorT[A, B] = EitherT[F, A, B]

  @inline override final protected def xorT[A, B](fab: F[Either[A, B]]): EitherT[F, A, B] =
    EitherT(fab)

  @inline override final protected def xorT2FEither[A, B](efab: EitherT[F, A, B]): F[Either[A, B]] =
    efab.value

}

object CanHandleError {

  def apply[F[_]: CanHandleError]: CanHandleError[F] = implicitly[CanHandleError[F]]

  implicit object IoCanHandleError extends CanHandleError[Task] {

    override def handleNonFatalWith[A, AA >: A](fa: => Task[A])(handleError: Throwable => Task[AA]): Task[AA] =
      fa.onErrorHandleWith(handleError)

    override def handleNonFatal[A, AA >: A](fa: => Task[A])(handleError: Throwable => AA): Task[AA] =
      handleNonFatalWith[A, AA](fa)(err => Task.pure(handleError(err)))

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
