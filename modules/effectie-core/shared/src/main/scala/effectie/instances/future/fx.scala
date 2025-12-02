package effectie.instances.future

import effectie.core.{Fx, OnNonFatal}

import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2021-11-03
  */
object fx {
  trait FxOfFuture
      extends Fx[Future]
      with fxCtor.FutureFxCtor
      with canCatch.FutureCanCatch
      with canHandleError.FutureCanHandleError
      with canRecover.FutureCanRecover
      with OnNonFatal[Future]

  final class FutureFx(implicit override val EC0: ExecutionContext) extends FxOfFuture {
    override def onNonFatalWith[A](fa: => Future[A])(
      partialFunction: PartialFunction[Throwable, Future[Unit]]
    ): Future[A] =
      OnNonFatal[Future](fxCtor, canHandleError.canHandleErrorFuture).onNonFatalWith(fa)(partialFunction)
  }

  implicit def futureFx(implicit EC: ExecutionContext): Fx[Future] = new FutureFx
}
