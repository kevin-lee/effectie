package effectie.syntax

import effectie.core.FxCtor

trait fx {

  import fx._

  def effectOf[F[*]]: CurriedEffectOf[F] = new CurriedEffectOf[F]

  def pureOf[F[*]]: CurriedEffectOfPure[F] = new CurriedEffectOfPure[F]

  def pureOrError[F[*]]: CurriedEffectOfPureOrError[F] = new CurriedEffectOfPureOrError[F]

  def unitOf[F[*]: FxCtor]: F[Unit] = FxCtor[F].unitOf

  def errorOf[F[*]]: CurriedErrorOf[F] = new CurriedErrorOf[F]

}

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
object fx extends fx {

  private[fx] final class CurriedEffectOf[F[*]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: => A)(implicit EF: FxCtor[F]): F[A] =
      FxCtor[F].effectOf(a)
  }

  private[fx] final class CurriedEffectOfPure[F[*]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: A)(implicit EF: FxCtor[F]): F[A] =
      FxCtor[F].pureOf(a)
  }

  private[fx] final class CurriedEffectOfPureOrError[F[*]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: => A)(implicit EF: FxCtor[F]): F[A] =
      FxCtor[F].pureOrError(a)
  }

  private[fx] final class CurriedErrorOf[F[*]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](throwable: Throwable)(implicit EF: FxCtor[F]): F[A] =
      FxCtor[F].errorOf(throwable)
  }

}
