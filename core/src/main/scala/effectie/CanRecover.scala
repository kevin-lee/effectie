package effectie

import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2020-08-17
  */
trait CanRecover[F[_]] {
  type Xor[+A, +B]
  type XorT[A, B]

  protected def xorT[A, B](fab: => F[Xor[A, B]]): XorT[A, B]
  protected def xorT2FXor[A, B](efab: => XorT[A, B]): F[Xor[A, B]]

  def recoverFromNonFatalWith[A, AA >: A](fa: => F[A])(handleError: PartialFunction[Throwable, F[AA]]): F[AA]

  final def recoverEitherFromNonFatalWith[A, AA >: A, B, BB >: B](
    fab: => F[Xor[A, B]]
  )(
    handleError: PartialFunction[Throwable, F[Xor[AA, BB]]]
  ): F[Xor[AA, BB]] =
    recoverFromNonFatalWith[Xor[A, B], Xor[AA, BB]](fab)(handleError)

  final def recoverEitherTFromNonFatalWith[A, AA >: A, B, BB >: B](
    efab: => XorT[A, B]
  )(
    handleError: PartialFunction[Throwable, F[Xor[AA, BB]]]
  ): XorT[AA, BB] =
    xorT(recoverFromNonFatalWith[Xor[A, B], Xor[AA, BB]](xorT2FXor(efab))(handleError))

  def recoverFromNonFatal[A, AA >: A](fa: => F[A])(handleError: PartialFunction[Throwable, AA]): F[AA]

  final def recoverEitherFromNonFatal[A, AA >: A, B, BB >: B](
    fab: => F[Xor[A, B]]
  )(
    handleError: PartialFunction[Throwable, Xor[AA, BB]]
  ): F[Xor[AA, BB]] =
    recoverFromNonFatal[Xor[A, B], Xor[AA, BB]](fab)(handleError)

  final def recoverEitherTFromNonFatal[A, AA >: A, B, BB >: B](
    efab: => XorT[A, B]
  )(
    handleError: PartialFunction[Throwable, Xor[AA, BB]]
  ): XorT[AA, BB] =
    xorT(recoverFromNonFatal[Xor[A, B], Xor[AA, BB]](xorT2FXor(efab))(handleError))

}

object CanRecover {

  abstract class FutureCanRecover(val ec: ExecutionContext) extends CanRecover[Future] {

    override def recoverFromNonFatalWith[A, AA >: A](
      fa: => Future[A]
    )(
      handleError: PartialFunction[Throwable, Future[AA]]
    ): Future[AA] =
      fa.recoverWith(handleError)(ec)

    @SuppressWarnings(Array("org.wartremover.warts.Throw"))
    override def recoverFromNonFatal[A, AA >: A](
      fa: => Future[A]
    )(
      handleError: PartialFunction[Throwable, AA]
    ): Future[AA] =
      recoverFromNonFatalWith[A, AA](fa)(
        handleError.andThen(Future(_)(ec))
      )
  }

}
