package effectie.instances.future

import effectie.core.CanHandleError

import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2021-11-03
  */
object canHandleError {

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
