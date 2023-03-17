package effectie.instances.tries

import effectie.core.Fx

import scala.util.Try

/** @author Kevin Lee
  * @since 2023-03-17
  */
object fx {
  trait FxOfTry
      extends Fx[Try]
      with fxCtor.TryFxCtor
      with canCatch.TryCanCatch
      with canHandleError.TryCanHandleError
      with canRecover.TryCanRecover

  implicit object futureFx extends FxOfTry
}
