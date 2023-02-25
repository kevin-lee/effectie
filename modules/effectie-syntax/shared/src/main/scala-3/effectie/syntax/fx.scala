package effectie.syntax

import effectie.core.FxCtor

/** @author Kevin Lee
  * @since 2022-09-08
  */
trait fx {
  import effectie.syntax.fx.*

  def effectOf[F[*]]: CurriedEffectOf[F] = new CurriedEffectOf[F]

  def pureOf[F[*]]: CurriedEffectOfPure[F] = new CurriedEffectOfPure[F]

  def pureOrError[F[*]]: CurriedEffectOfPureOrError[F] = new CurriedEffectOfPureOrError[F]

  inline final def unitOf[F[*]: FxCtor]: F[Unit] = FxCtor[F].unitOf

  def errorOf[F[*]]: CurriedErrorOf[F] = new CurriedErrorOf[F]

  inline def pureOfNone[F[*], A](using EF: FxCtor[F]): F[Option[A]] = EF.pureOfNone

  extension [A](a: A) {
    def pureOfOption[F[*]](using EF: FxCtor[F]): F[Option[A]] = EF.pureOfOption(a)

    def pureOfSome[F[*]](using EF: FxCtor[F]): F[Option[A]] = EF.pureOfSome(a)

    def pureOfRight[F[*], B](using EF: FxCtor[F]): F[Either[B, A]] = EF.pureOfRight[B](a)

    def pureOfLeft[F[*], B](using EF: FxCtor[F]): F[Either[A, B]] = EF.pureOfLeft[B](a)

  }

}
object fx extends fx {

  private[fx] final class CurriedEffectOf[F[*]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: => A)(using EF: FxCtor[F]): F[A] =
      FxCtor[F].effectOf(a)
  }

  private[fx] final class CurriedEffectOfPure[F[*]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: A)(using EF: FxCtor[F]): F[A] =
      FxCtor[F].pureOf(a)
  }

  private[fx] final class CurriedEffectOfPureOrError[F[*]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: => A)(using EF: FxCtor[F]): F[A] =
      FxCtor[F].pureOrError(a)
  }

  private[fx] final class CurriedErrorOf[F[*]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](throwable: Throwable)(using EF: FxCtor[F]): F[A] =
      FxCtor[F].errorOf(throwable)
  }

  private[fx] final class CurriedPureOfOption[F[*]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: A)(implicit EF: FxCtor[F]): F[Option[A]] =
      EF.pureOfOption(a)
  }

  private[fx] final class CurriedPureOfSome[F[*]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: A)(implicit EF: FxCtor[F]): F[Option[A]] =
      EF.pureOfSome(a)
  }

  private[fx] final class CurriedPureOfRightA[F[*], A](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[B](b: B)(implicit EF: FxCtor[F]): F[Either[A, B]] =
      EF.pureOfRight(b)
  }

  private[fx] final class CurriedPureOfLeftB[F[*], B](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: A)(implicit EF: FxCtor[F]): F[Either[A, B]] =
      EF.pureOfLeft(a)
  }

}
