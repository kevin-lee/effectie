package effectie

trait EffectConstructor[F[_]] {
  def effectOf[A](a: => A): F[A]
  def effectOfPure[A](a: A): F[A]
  def unit: F[Unit]
}

object EffectConstructor {
  def apply[F[_] : EffectConstructor]: EffectConstructor[F] = implicitly[EffectConstructor[F]]
}