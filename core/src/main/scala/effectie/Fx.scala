package effectie

/** @author Kevin Lee
  * @since 2021-11-03
  */
trait Fx[F[_]] extends FxCtor[F] with CanCatch[F]
