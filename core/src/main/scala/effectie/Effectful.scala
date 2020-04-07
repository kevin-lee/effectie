package effectie

trait Effectful {

  def effectOf[F[_] : EffectConstructor, A](a: => A): F[A] = EffectConstructor[F].effectOf(a)

  def effectOfPure[F[_] : EffectConstructor, A](a: A): F[A] = EffectConstructor[F].effectOfPure(a)

  def effectUnit[F[_] : EffectConstructor]: F[Unit] = EffectConstructor[F].unit

}

object Effectful extends Effectful
