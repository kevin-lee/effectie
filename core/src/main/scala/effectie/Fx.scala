package effectie

import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2021-11-03
  */
trait Fx[F[_]] extends FxCtor[F] with CanCatch[F] with CanHandleError[F] with CanRecover[F]

object Fx {

  def apply[F[_]: Fx]: Fx[F] = implicitly[Fx[F]]

  trait FxOfFuture
      extends Fx[Future]
      with FxCtor.FutureFxCtor
      with CanCatch.FutureCanCatch
      with CanHandleError.FutureCanHandleError
      with CanRecover.FutureCanRecover

  final class FutureFx(implicit override val EC0: ExecutionContext) extends FxOfFuture

  implicit def futureFx(implicit EC: ExecutionContext): Fx[Future] = new FutureFx
}
