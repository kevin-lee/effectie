package effectie

import effectie.Effectful._

trait Effectful {

  @deprecated(
    message = "Use effectie.cats.Effectful.effectOf or effectie.scalaz.Effectful.effectOf instead.",
    since = "1.4.0"
  )
  def effectOf[F[_]]: CurriedEffectOf[F] = new CurriedEffectOf[F]


  @deprecated(
    message = "Use effectie.cats.Effectful.pureOf or effectie.scalaz.Effectful.pureOf instead.",
    since = "1.4.0"
  )
  def effectOfPure[F[_]]: CurriedEffectOfPure[F] = new CurriedEffectOfPure[F]


  @deprecated(
    message = "Use effectie.cats.Effectful.unitOf or effectie.scalaz.Effectful.unitOf instead.",
    since = "1.4.0"
  )
  def effectOfUnit[F[_]: EffectConstructor]: F[Unit] = EffectConstructor[F].effectOfUnit

}

@deprecated(
  message = "Use effectie.cats.Effectful or effectie.scalaz.Effectful instead.",
  since = "1.4.0"
)
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
