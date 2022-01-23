package effectie.syntax

import effectie.core.FxCtor

/**
 * @author Kevin Lee
 * @since 2022-01-17
 */
trait fx {
  import effectie.syntax.fx.*

  def effectOf[F[_]]: CurriedEffectOf[F] = new CurriedEffectOf[F]

  def pureOf[F[_]]: CurriedEffectOfPure[F] = new CurriedEffectOfPure[F]

  inline final def unitOf[F[_]: FxCtor]: F[Unit] = FxCtor[F].unitOf

  def errorOf[F[_]]: CurriedErrorOf[F] = new CurriedErrorOf[F]

}

object fx extends fx {

  private[fx] final class CurriedEffectOf[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: => A)(using EF: FxCtor[F]): F[A] =
      FxCtor[F].effectOf(a)
  }

  private[fx] final class CurriedEffectOfPure[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: A)(using EF: FxCtor[F]): F[A] =
      FxCtor[F].pureOf(a)
  }

  private[fx] final class CurriedErrorOf[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](throwable: Throwable)(using EF: FxCtor[F]): F[A] =
      FxCtor[F].errorOf(throwable)
  }

}
