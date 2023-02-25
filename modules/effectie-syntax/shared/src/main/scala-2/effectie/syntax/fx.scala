package effectie.syntax

import effectie.core.FxCtor

/** @author Kevin Lee
  * @since 2022-09-08
  */
trait fx {

  import fx._

  def effectOf[F[*]]: CurriedEffectOf[F] = new CurriedEffectOf[F]

  def pureOf[F[*]]: CurriedEffectOfPure[F] = new CurriedEffectOfPure[F]

  def pureOrError[F[*]]: CurriedEffectOfPureOrError[F] = new CurriedEffectOfPureOrError[F]

  @inline def unitOf[F[*]: FxCtor]: F[Unit] = FxCtor[F].unitOf

  def errorOf[F[*]]: CurriedErrorOf[F] = new CurriedErrorOf[F]

  def pureOfOption[F[*]]: CurriedPureOfOption[F] = new CurriedPureOfOption[F]

  def pureOfSome[F[*]]: CurriedPureOfSome[F] = new CurriedPureOfSome[F]

  @inline def pureOfNone[F[*], A](implicit EF: FxCtor[F]): F[Option[A]] = EF.pureOfNone

  def pureOfRight[F[*], A]: CurriedPureOfRightA[F, A] = new CurriedPureOfRightA[F, A]

  def pureOfLeft[F[*], B]: CurriedPureOfLeftB[F, B] = new CurriedPureOfLeftB[F, B]

  implicit final def fxOps[A](a: A): FxOps[A] = new FxOps[A](a)

}
object fx extends fx {

  private[fx] final class CurriedEffectOf[F[*]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: => A)(implicit EF: FxCtor[F]): F[A] =
      EF.effectOf(a)
  }

  private[fx] final class CurriedEffectOfPure[F[*]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: A)(implicit EF: FxCtor[F]): F[A] =
      EF.pureOf(a)
  }

  private[fx] final class CurriedEffectOfPureOrError[F[*]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: => A)(implicit EF: FxCtor[F]): F[A] =
      EF.pureOrError(a)
  }

  private[fx] final class CurriedErrorOf[F[*]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](throwable: Throwable)(implicit EF: FxCtor[F]): F[A] =
      EF.errorOf(throwable)
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

  final class FxOps[A](private val a: A) extends AnyVal {
    def pureOfOption[F[*]](implicit EF: FxCtor[F]): F[Option[A]] = EF.pureOfOption(a)

    def pureOfSome[F[*]](implicit EF: FxCtor[F]): F[Option[A]] = EF.pureOfSome(a)

    def pureOfRight[F[*], B](implicit EF: FxCtor[F]): F[Either[B, A]] = EF.pureOfRight[B](a)

    def pureOfLeft[F[*], B](implicit EF: FxCtor[F]): F[Either[A, B]] = EF.pureOfLeft[B](a)

  }

}
