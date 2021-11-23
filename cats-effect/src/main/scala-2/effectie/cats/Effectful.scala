package effectie.cats

import effectie.cats.Effectful._

import effectie.FxCtor


trait Effectful {

  def effectOf[F[_]]: CurriedEffectOf[F] = new CurriedEffectOf[F]

  def pureOf[F[_]]: CurriedEffectOfPure[F] = new CurriedEffectOfPure[F]

  def unitOf[F[_]: FxCtor]: F[Unit] = FxCtor[F].unitOf

  def errorOf[F[_]]: CurriedErrorOf[F] = new CurriedErrorOf[F]

  @deprecated(message = "Use pureOf instead.", since = "1.4.0")
  @inline final def effectOfPure[F[_]]: CurriedEffectOfPure[F] = pureOf[F]

  @deprecated(message = "Use unitOf instead", since = "1.4.0")
  @inline final def effectOfUnit[F[_]: FxCtor]: F[Unit] = unitOf[F]

}

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
object Effectful extends Effectful {

  private[Effectful] final class CurriedEffectOf[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: => A)(implicit EF: FxCtor[F]): F[A] =
      FxCtor[F].effectOf(a)
  }

  private[Effectful] final class CurriedEffectOfPure[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: A)(implicit EF: FxCtor[F]): F[A] =
      FxCtor[F].pureOf(a)
  }

  private[Effectful] final class CurriedErrorOf[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](throwable: Throwable)(implicit EF: FxCtor[F]): F[A] =
      FxCtor[F].errorOf(throwable)
  }

}
