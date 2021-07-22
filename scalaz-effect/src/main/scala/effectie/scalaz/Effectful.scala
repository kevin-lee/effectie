package effectie.scalaz

import effectie.scalaz.Effectful._

trait Effectful {

  def effectOf[F[_]]: CurriedEffectOf[F] = new CurriedEffectOf[F]

  @deprecated(message = "Use pureOf instead.", since = "1.4.0")
  @inline def effectOfPure[F[_]]: CurriedEffectOfPure[F] = pureOf[F]

  def pureOf[F[_]]: CurriedEffectOfPure[F] = new CurriedEffectOfPure[F]

  @deprecated(message = "Use unitOf instead", since = "1.4.0")
  @inline def effectOfUnit[F[_]: Fx]: F[Unit] = unitOf[F]

  def unitOf[F[_]: Fx]: F[Unit] = Fx[F].unitOf

}

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
object Effectful extends Effectful {

  private[Effectful] final class CurriedEffectOf[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: => A)(implicit EF: Fx[F]): F[A] =
      Fx[F].effectOf(a)
  }

  private[Effectful] final class CurriedEffectOfPure[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: A)(implicit EF: Fx[F]): F[A] =
      Fx[F].pureOf(a)
  }

}
