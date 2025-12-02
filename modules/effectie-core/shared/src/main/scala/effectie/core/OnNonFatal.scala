package effectie.core

import scala.annotation.implicitNotFound

/** @author Kevin Lee
  * @since 2025-11-16
  */
@implicitNotFound(
  """
  Could not find an implicit OnNonFatal[${F}]. You can probably find it from the effectie.instances package.
  It is recommended to use an instance for Fx instead of an instance for OnNonFatal.""" +
    compileTimeMessages.ListOfFxInstances
)
trait OnNonFatal[F[*]] {

  def onNonFatalWith[A](fa: => F[A])(partialFunction: PartialFunction[Throwable, F[Unit]]): F[A]

}
object OnNonFatal {

  def apply[F[*]: FxCtor: CanHandleError]: OnNonFatal[F] = new OnNonFatalF[F]

  private final class OnNonFatalF[F[*]: FxCtor: CanHandleError] extends OnNonFatal[F] {

    @inline override final def onNonFatalWith[A](
      fa: => F[A]
    )(partialFunction: PartialFunction[Throwable, F[Unit]]): F[A] =
      CanHandleError[F].handleNonFatalWith(fa)(err =>
        FxCtor[F].flatMapFa(partialFunction.applyOrElse(err, (_: Throwable) => FxCtor[F].unitOf))(_ =>
          FxCtor[F].errorOf(err)
        )
      )

  }

}
