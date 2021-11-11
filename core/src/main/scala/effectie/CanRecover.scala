package effectie

import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2020-08-17
  */
trait CanRecover[F[_]] {

  type XorT[A, B]

  protected def xorT[A, B](fab: F[Either[A, B]]): XorT[A, B]
  protected def xorT2FEither[A, B](efab: XorT[A, B]): F[Either[A, B]]

  def recoverFromNonFatalWith[A, AA >: A](fa: => F[A])(handleError: PartialFunction[Throwable, F[AA]]): F[AA]

  final def recoverEitherFromNonFatalWith[A, AA >: A, B, BB >: B](
    fab: => F[Either[A, B]]
  )(
    handleError: PartialFunction[Throwable, F[Either[AA, BB]]]
  ): F[Either[AA, BB]] =
    recoverFromNonFatalWith[Either[A, B], Either[AA, BB]](fab)(handleError)

  final def recoverEitherTFromNonFatalWith[A, AA >: A, B, BB >: B](
    efab: => XorT[A, B]
  )(
    handleError: PartialFunction[Throwable, F[Either[AA, BB]]]
  ): XorT[AA, BB] =
    xorT(recoverFromNonFatalWith[Either[A, B], Either[AA, BB]](xorT2FEither(efab))(handleError))

  def recoverFromNonFatal[A, AA >: A](fa: => F[A])(handleError: PartialFunction[Throwable, AA]): F[AA]

  final def recoverEitherFromNonFatal[A, AA >: A, B, BB >: B](
    fab: => F[Either[A, B]]
  )(
    handleError: PartialFunction[Throwable, Either[AA, BB]]
  ): F[Either[AA, BB]] =
    recoverFromNonFatal[Either[A, B], Either[AA, BB]](fab)(handleError)

  final def recoverEitherTFromNonFatal[A, AA >: A, B, BB >: B](
    efab: => XorT[A, B]
  )(
    handleError: PartialFunction[Throwable, Either[AA, BB]]
  ): XorT[AA, BB] =
    xorT(recoverFromNonFatal[Either[A, B], Either[AA, BB]](xorT2FEither(efab))(handleError))

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
