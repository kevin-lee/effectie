package effectie.cats

import effectie.cats.Effectful.*

trait Effectful {

  def effectOf[F[_]]: CurriedEffectOf[F] = new CurriedEffectOf[F]

  def pureOf[F[_]]: CurriedEffectOfPure[F] = new CurriedEffectOfPure[F]

  inline def unitOf[F[_]: FxCtor]: F[Unit] = FxCtor[F].unitOf

  def errorOf[F[_]]: CurriedErrorOf[F] = new CurriedErrorOf[F]

  @deprecated(message = "Use pureOf instead.", since = "1.4.0")
  inline def effectOfPure[F[_]]: CurriedEffectOfPure[F] = pureOf[F]

  @deprecated(message = "Use unitOf instead", since = "1.4.0")
  inline def effectOfUnit[F[_]: FxCtor]: F[Unit] = unitOf[F]

}

object Effectful extends Effectful {

  private[Effectful] final class CurriedEffectOf[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: => A)(using EF: FxCtor[F]): F[A] =
      FxCtor[F].effectOf(a)
  }

  private[Effectful] final class CurriedEffectOfPure[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: A)(using EF: FxCtor[F]): F[A] =
      FxCtor[F].pureOf(a)
  }

  private[Effectful] final class CurriedErrorOf[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](throwable: Throwable)(using EF: FxCtor[F]): F[A] =
      FxCtor[F].errorOf(throwable)
  }

}
