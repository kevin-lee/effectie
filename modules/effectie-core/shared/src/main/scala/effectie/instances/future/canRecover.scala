package effectie.instances.future

import effectie.core.CanRecover

import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2021-11-03
  */
object canRecover {

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
