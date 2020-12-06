package effectie.monix

import effectie.monix.Effectful._

trait Effectful {

  def effectOf[F[_]]: CurriedEffectOf[F] = new CurriedEffectOf[F]

  def pureOf[F[_]]: CurriedEffectOfPure[F] = new CurriedEffectOfPure[F]

  def unitOf[F[_]: EffectConstructor]: F[Unit] = EffectConstructor[F].unitOf

}

object Effectful extends Effectful {

  private[Effectful] final class CurriedEffectOf[F[_]] {
    def apply[A](a: => A)(implicit EF: EffectConstructor[F]): F[A] =
      EffectConstructor[F].effectOf(a)
  }

  private[Effectful] final class CurriedEffectOfPure[F[_]] {
    def apply[A](a: A)(implicit EF: EffectConstructor[F]): F[A] =
      EffectConstructor[F].pureOf(a)
  }

}
