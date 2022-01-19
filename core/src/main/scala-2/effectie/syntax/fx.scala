package effectie.syntax

trait fx {

  import fx._

  def effectOf[F[_]]: CurriedEffectOf[F] = new CurriedEffectOf[F]

  def pureOf[F[_]]: CurriedEffectOfPure[F] = new CurriedEffectOfPure[F]

  def unitOf[F[_]: effectie.FxCtor]: F[Unit] = effectie.FxCtor[F].unitOf

  def errorOf[F[_]]: CurriedErrorOf[F] = new CurriedErrorOf[F]

}

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
object fx extends fx {

  private[fx] final class CurriedEffectOf[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: => A)(implicit EF: effectie.FxCtor[F]): F[A] =
      effectie.FxCtor[F].effectOf(a)
  }

  private[fx] final class CurriedEffectOfPure[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: A)(implicit EF: effectie.FxCtor[F]): F[A] =
      effectie.FxCtor[F].pureOf(a)
  }

  private[fx] final class CurriedErrorOf[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](throwable: Throwable)(implicit EF: effectie.FxCtor[F]): F[A] =
      effectie.FxCtor[F].errorOf(throwable)
  }

}