package effectie.cats

import effectie.FxCtor
import effectie.cats.Effectful.*

trait Effectful {

  def effectOf[F[*]]: CurriedEffectOf[F] = new CurriedEffectOf[F]

  def pureOf[F[*]]: CurriedEffectOfPure[F] = new CurriedEffectOfPure[F]

  inline final def unitOf[F[*]: FxCtor]: F[Unit] = FxCtor[F].unitOf

  def errorOf[F[*]]: CurriedErrorOf[F] = new CurriedErrorOf[F]

  @deprecated(message = "Use pureOf instead.", since = "1.4.0")
  inline final def effectOfPure[F[*]]: CurriedEffectOfPure[F] = pureOf[F]

  @deprecated(message = "Use unitOf instead", since = "1.4.0")
  inline final def effectOfUnit[F[*]: FxCtor]: F[Unit] = unitOf[F]

}

object Effectful extends Effectful {

  private[Effectful] final class CurriedEffectOf[F[*]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: => A)(using EF: FxCtor[F]): F[A] =
      FxCtor[F].effectOf(a)
  }

  private[Effectful] final class CurriedEffectOfPure[F[*]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: A)(using EF: FxCtor[F]): F[A] =
      FxCtor[F].pureOf(a)
  }

  private[Effectful] final class CurriedErrorOf[F[*]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](throwable: Throwable)(using EF: FxCtor[F]): F[A] =
      FxCtor[F].errorOf(throwable)
  }

}
