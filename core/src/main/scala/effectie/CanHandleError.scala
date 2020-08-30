package effectie

import scala.concurrent.{ExecutionContext, Future}

/**
 * @author Kevin Lee
 * @since 2020-08-17
 */
trait CanHandleError[F[_]] {
  type Xor[A, B]
  type XorT[A, B]

  def handleNonFatalWith[A, AA >: A](fa: => F[A])(handleError: Throwable => F[AA]): F[AA]

  def handleEitherTNonFatalWith[A, AA >: A, B, BB >: B](
      efab: => XorT[A, B]
    )(
      handleError: Throwable => F[Xor[AA, BB]]
    ): XorT[AA, BB]

  def handleNonFatal[A, AA >: A](fa: => F[A])(handleError: Throwable => AA): F[AA]

  def handleEitherTNonFatal[A, AA >: A, B, BB >: B](
      efab: => XorT[A, B]
    )(
      handleError: Throwable => Xor[AA, BB]
    ): XorT[AA, BB]

}

object CanHandleError {

  abstract class FutureCanHandleError(val ec: ExecutionContext) extends CanHandleError[Future] {

    override def handleNonFatalWith[A, AA >: A](fa: => Future[A])(handleError: Throwable => Future[AA]): Future[AA] =
      fa.recoverWith {
        case throwable: Throwable =>
          handleError(throwable)
      }(ec)

    override def handleNonFatal[A, AA >: A](fa: => Future[A])(handleError: Throwable => AA): Future[AA] =
      handleNonFatalWith[A, AA](fa)(err => Future(handleError(err))(ec))
  }

}