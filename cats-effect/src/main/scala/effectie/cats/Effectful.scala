package effectie.cats

import effectie.cats.Effectful._

trait Effectful {

  def effectOf[F[_]]: CurriedEffectOf[F] = new CurriedEffectOf[F]

  def effectOfPure[F[_]]: CurriedEffectOfPure[F] = new CurriedEffectOfPure[F]

  def effectOfUnit[F[_]: EffectConstructor]: F[Unit] = EffectConstructor[F].effectOfUnit

}

object Effectful extends Effectful {

  private[Effectful] final class CurriedEffectOf[F[_]] {
    def apply[A](a: => A)(implicit EF: EffectConstructor[F]): F[A] =
      EffectConstructor[F].effectOf(a)
  }

  private[Effectful] final class CurriedEffectOfPure[F[_]] {
    def apply[A](a: A)(implicit EF: EffectConstructor[F]): F[A] =
      EffectConstructor[F].effectOfPure(a)
  }

}
