package effectie

trait Effectful {

  def effect[F[_] : EffectConstructor, A](a: => A): F[A] = EffectConstructor[F].effect(a)

  def pureEffect[F[_] : EffectConstructor, A](a: A): F[A] = EffectConstructor[F].pureEffect(a)

  def effectUnit[F[_] : EffectConstructor]: F[Unit] = EffectConstructor[F].unit

}
