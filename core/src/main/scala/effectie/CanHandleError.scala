package effectie

import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2020-08-17
  */
trait CanHandleError[F[_]] {

  def handleNonFatalWith[A, AA >: A](fa: => F[A])(handleError: Throwable => F[AA]): F[AA]

  @inline final def handleEitherNonFatalWith[A, AA >: A, B, BB >: B](
    fab: => F[Either[A, B]]
  )(
    handleError: Throwable => F[Either[AA, BB]]
  ): F[Either[AA, BB]] =
    handleNonFatalWith[Either[A, B], Either[AA, BB]](fab)(handleError)

  def handleNonFatal[A, AA >: A](fa: => F[A])(handleError: Throwable => AA): F[AA]

  @inline final def handleEitherNonFatal[A, AA >: A, B, BB >: B](
    fab: => F[Either[A, B]]
  )(
    handleError: Throwable => Either[AA, BB]
  ): F[Either[AA, BB]] =
    handleNonFatal[Either[A, B], Either[AA, BB]](fab)(handleError)

}

object CanHandleError {

  def apply[F[_]: CanHandleError]: CanHandleError[F] = implicitly[CanHandleError[F]]

  trait FutureCanHandleError extends CanHandleError[Future] {
    implicit def EC0: ExecutionContext

    @inline override def handleNonFatalWith[A, AA >: A](
      fa: => Future[A]
    )(handleError: Throwable => Future[AA]): Future[AA] =
      fa.recoverWith {
        case throwable: Throwable =>
          handleError(throwable)
      }

    @inline override def handleNonFatal[A, AA >: A](fa: => Future[A])(handleError: Throwable => AA): Future[AA] =
      handleNonFatalWith[A, AA](fa)(err => Future(handleError(err)))
  }

  final class CanHandleErrorFuture(override implicit val EC0: ExecutionContext) extends FutureCanHandleError

  implicit def canHandleErrorFuture(implicit ec: ExecutionContext): CanHandleError[Future] =
    new CanHandleErrorFuture

}
