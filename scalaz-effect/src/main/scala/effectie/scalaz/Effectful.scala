package effectie.scalaz

import effectie.scalaz.Effectful._

trait Effectful {

  def effectOf[F[_]]: CurriedEffectOf[F] = new CurriedEffectOf[F]

  @deprecated(message = "Use pureOf instead.", since = "1.4.0")
  def effectOfPure[F[_]]: CurriedEffectOfPure[F] = pureOf[F]

  def pureOf[F[_]]: CurriedEffectOfPure[F] = new CurriedEffectOfPure[F]

  @deprecated(message = "Use unitOf instead", since = "1.4.0")
  def effectOfUnit[F[_]: EffectConstructor]: F[Unit] = unitOf[F]

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
