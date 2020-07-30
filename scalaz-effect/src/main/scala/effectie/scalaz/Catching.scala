package effectie.scalaz

import scalaz._

/**
 * @author Kevin Lee
 * @since 2020-06-07
 */
trait Catching {

  import Catching._

  final def catchNonFatal[F[_], A]: CurriedCanCatch1[F, A] =
    new CurriedCanCatch1[F, A]

  final def catchNonFatalF[A]: CurriedCanCatchF1[A] =
    new CurriedCanCatchF1[A]

}

object Catching extends Catching {

  private[Catching] final class CurriedCanCatch1[F[_], A] {
    def apply[B](fb: => F[B]): CurriedCanCatch2[F, A, B] =
      new CurriedCanCatch2[F, A, B](() => fb)
  }

  private[Catching] final class CurriedCanCatch2[F[_], A, B](
    private val fb: () => F[B]
  ) extends AnyVal {
    def apply(f: Throwable => A)(implicit CC: CanCatch[F]): F[A \/ B] =
      CanCatch[F].catchNonFatal(fb())(f)
  }

  @SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
  private[Catching] final class CurriedCanCatchF1[A] {
    def apply[F[_], B](b: => B): CurriedCanCatchF2[F, A, B] =
      new CurriedCanCatchF2[F, A, B](() => b)
  }

  private[Catching] final class CurriedCanCatchF2[F[_], A, B](
    private val b: () => B
  ) extends AnyVal {
    def apply(f: Throwable => A)(implicit EC: EffectConstructor[F], CC: CanCatch[F]): F[A \/ B] =
      CanCatch[F].catchNonFatal(EffectConstructor[F].effectOf(b()))(f)
  }

}
