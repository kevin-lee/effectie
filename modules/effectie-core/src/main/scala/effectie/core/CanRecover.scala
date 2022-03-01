package effectie.core

import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2020-08-17
  */
trait CanRecover[F[*]] {

  def recoverFromNonFatalWith[A, AA >: A](fa: => F[A])(handleError: PartialFunction[Throwable, F[AA]]): F[AA]

  @inline final def recoverEitherFromNonFatalWith[A, AA >: A, B, BB >: B](
    fab: => F[Either[A, B]]
  )(
    handleError: PartialFunction[Throwable, F[Either[AA, BB]]]
  ): F[Either[AA, BB]] =
    recoverFromNonFatalWith[Either[A, B], Either[AA, BB]](fab)(handleError)

  def recoverFromNonFatal[A, AA >: A](fa: => F[A])(handleError: PartialFunction[Throwable, AA]): F[AA]

  @inline final def recoverEitherFromNonFatal[A, AA >: A, B, BB >: B](
    fab: => F[Either[A, B]]
  )(
    handleError: PartialFunction[Throwable, Either[AA, BB]]
  ): F[Either[AA, BB]] =
    recoverFromNonFatal[Either[A, B], Either[AA, BB]](fab)(handleError)

}

object CanRecover {

  def apply[F[*]: CanRecover]: CanRecover[F] = implicitly[CanRecover[F]]

  implicit def futureCanRecover(implicit ec: ExecutionContext): CanRecover[Future] = new CanRecoverFuture

  trait FutureCanRecover extends CanRecover[Future] {
    implicit def EC0: ExecutionContext

    @inline override def recoverFromNonFatalWith[A, AA >: A](
      fa: => Future[A]
    )(
      handleError: PartialFunction[Throwable, Future[AA]]
    ): Future[AA] =
      fa.recoverWith(handleError)

    @SuppressWarnings(Array("org.wartremover.warts.Throw"))
    @inline override def recoverFromNonFatal[A, AA >: A](
      fa: => Future[A]
    )(
      handleError: PartialFunction[Throwable, AA]
    ): Future[AA] =
      recoverFromNonFatalWith[A, AA](fa)(
        handleError.andThen(Future(_))
      )
  }

  class CanRecoverFuture(override implicit val EC0: ExecutionContext) extends FutureCanRecover

}
