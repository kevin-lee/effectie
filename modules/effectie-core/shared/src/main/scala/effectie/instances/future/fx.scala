package effectie.instances.future

import effectie.core.Fx

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

  final class FutureFx(implicit override val EC0: ExecutionContext) extends FxOfFuture

  implicit def futureFx(implicit EC: ExecutionContext): Fx[Future] = new FutureFx
}
