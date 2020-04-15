package effectie

trait Effectful {

  def effectOf[F[_] : EffectConstructor, A](a: => A): F[A] = EffectConstructor[F].effectOf(a)

  def effectOfPure[F[_] : EffectConstructor, A](a: A): F[A] = EffectConstructor[F].effectOfPure(a)

  def effectOfUnit[F[_] : EffectConstructor]: F[Unit] = EffectConstructor[F].effectOfUnit

}

object Effectful extends Effectful
