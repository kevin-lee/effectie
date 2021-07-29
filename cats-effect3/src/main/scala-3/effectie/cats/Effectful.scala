package effectie.cats

import effectie.cats.Effectful.*

trait Effectful {

  def effectOf[F[_]]: CurriedEffectOf[F] = new CurriedEffectOf[F]

  def pureOf[F[_]]: CurriedEffectOfPure[F] = new CurriedEffectOfPure[F]

  @deprecated(message = "Use pureOf instead.", since = "1.4.0")
  @inline def effectOfPure[F[_]]: CurriedEffectOfPure[F] = pureOf[F]

  def unitOf[F[_]: Fx]: F[Unit] = Fx[F].unitOf

  @deprecated(message = "Use unitOf instead", since = "1.4.0")
  @inline def effectOfUnit[F[_]: Fx]: F[Unit] = unitOf[F]

}

object Effectful extends Effectful {

  private[Effectful] final class CurriedEffectOf[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: => A)(using EF: Fx[F]): F[A] =
      Fx[F].effectOf(a)
  }

  private[Effectful] final class CurriedEffectOfPure[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: A)(using EF: Fx[F]): F[A] =
      Fx[F].pureOf(a)
  }

}
