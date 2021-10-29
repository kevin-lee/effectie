package effectie

import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2020-08-17
  */
trait CanHandleError[F[_]] {

  type Xor[+A, +B]
  type XorT[A, B]

  protected def xorT[A, B](fab: F[Xor[A, B]]): XorT[A, B]
  protected def xorT2FXor[A, B](efab: XorT[A, B]): F[Xor[A, B]]

  def handleNonFatalWith[A, AA >: A](fa: => F[A])(handleError: Throwable => F[AA]): F[AA]

  final def handleEitherNonFatalWith[A, AA >: A, B, BB >: B](
    fab: => F[Xor[A, B]]
  )(
    handleError: Throwable => F[Xor[AA, BB]]
  ): F[Xor[AA, BB]] =
    handleNonFatalWith[Xor[A, B], Xor[AA, BB]](fab)(handleError)

  final def handleEitherTNonFatalWith[A, AA >: A, B, BB >: B](
    efab: => XorT[A, B]
  )(
    handleError: Throwable => F[Xor[AA, BB]]
  ): XorT[AA, BB] =
    xorT(handleNonFatalWith[Xor[A, B], Xor[AA, BB]](xorT2FXor(efab))(handleError))

  def handleNonFatal[A, AA >: A](fa: => F[A])(handleError: Throwable => AA): F[AA]

  final def handleEitherNonFatal[A, AA >: A, B, BB >: B](
    fab: => F[Xor[A, B]]
  )(
    handleError: Throwable => Xor[AA, BB]
  ): F[Xor[AA, BB]] =
    handleNonFatal[Xor[A, B], Xor[AA, BB]](fab)(handleError)

  final def handleEitherTNonFatal[A, AA >: A, B, BB >: B](
    efab: => XorT[A, B]
  )(
    handleError: Throwable => Xor[AA, BB]
  ): XorT[AA, BB] =
    xorT(handleNonFatal[Xor[A, B], Xor[AA, BB]](xorT2FXor(efab))(handleError))

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
