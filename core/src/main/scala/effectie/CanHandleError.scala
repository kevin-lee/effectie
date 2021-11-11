package effectie

import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2020-08-17
  */
trait CanHandleError[F[_]] {

  type XorT[A, B]

  protected def xorT[A, B](fab: F[Either[A, B]]): XorT[A, B]
  protected def xorT2FEither[A, B](efab: XorT[A, B]): F[Either[A, B]]

  def handleNonFatalWith[A, AA >: A](fa: => F[A])(handleError: Throwable => F[AA]): F[AA]

  final def handleEitherNonFatalWith[A, AA >: A, B, BB >: B](
    fab: => F[Either[A, B]]
  )(
    handleError: Throwable => F[Either[AA, BB]]
  ): F[Either[AA, BB]] =
    handleNonFatalWith[Either[A, B], Either[AA, BB]](fab)(handleError)

  final def handleEitherTNonFatalWith[A, AA >: A, B, BB >: B](
    efab: => XorT[A, B]
  )(
    handleError: Throwable => F[Either[AA, BB]]
  ): XorT[AA, BB] =
    xorT(handleNonFatalWith[Either[A, B], Either[AA, BB]](xorT2FEither(efab))(handleError))

  def handleNonFatal[A, AA >: A](fa: => F[A])(handleError: Throwable => AA): F[AA]

  final def handleEitherNonFatal[A, AA >: A, B, BB >: B](
    fab: => F[Either[A, B]]
  )(
    handleError: Throwable => Either[AA, BB]
  ): F[Either[AA, BB]] =
    handleNonFatal[Either[A, B], Either[AA, BB]](fab)(handleError)

  final def handleEitherTNonFatal[A, AA >: A, B, BB >: B](
    efab: => XorT[A, B]
  )(
    handleError: Throwable => Either[AA, BB]
  ): XorT[AA, BB] =
    xorT(handleNonFatal[Either[A, B], Either[AA, BB]](xorT2FEither(efab))(handleError))

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
