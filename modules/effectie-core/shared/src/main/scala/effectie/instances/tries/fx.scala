package effectie.instances.tries

import effectie.core.{Fx, OnNonFatal}

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
      with OnNonFatal[Try]

  implicit object tryFx extends FxOfTry {

    override def onNonFatalWith[A](fa: => Try[A])(partialFunction: PartialFunction[Throwable, Try[Unit]]): Try[A] =
      OnNonFatal[Try](effectie.instances.tries.fxCtor.fxCtorTry, canHandleError.canHandleErrorTry)
        .onNonFatalWith(fa)(partialFunction)
  }
}
