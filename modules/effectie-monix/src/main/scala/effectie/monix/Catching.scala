package effectie.monix

import cats.data.EitherT

/** @author Kevin Lee
  * @since 2020-06-07
  */
trait Catching {

  import effectie.monix.Catching._

  final def catchNonFatalThrowable[F[*]]: CurriedCanCatchThrowable[F] =
    new CurriedCanCatchThrowable[F]

  final def catchNonFatal[F[*]]: CurriedCanCatch1[F] =
    new CurriedCanCatch1[F]

  final def catchNonFatalF[F[*]]: CurriedCanCatchF1[F] =
    new CurriedCanCatchF1[F]

  def catchNonFatalEither[F[*]]: CurriedCanCatchEither1[F] =
    new CurriedCanCatchEither1[F]

  def catchNonFatalEitherF[F[*]]: CurriedCanCatchEitherF1[F] =
    new CurriedCanCatchEitherF1[F]

  def catchNonFatalEitherT[F[*]]: CurriedCanCatchEitherT1[F] =
    new CurriedCanCatchEitherT1[F]

}

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
object Catching extends Catching {

  private[Catching] final class CurriedCanCatchThrowable[F[*]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[B](fb: => F[B])(implicit CC: effectie.core.CanCatch[F]): F[Either[Throwable, B]] =
      effectie.core.CanCatch[F].catchNonFatalThrowable[B](fb)
  }

  private[Catching] final class CurriedCanCatch1[F[*]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[B](fb: => F[B]): CurriedCanCatch2[F, B] =
      new CurriedCanCatch2[F, B](() => fb)
  }

  private[Catching] final class CurriedCanCatch2[F[*], B](
    private val fb: () => F[B]
  ) extends AnyVal {
    def apply[A](f: Throwable => A)(implicit CC: effectie.core.CanCatch[F]): F[Either[A, B]] =
      effectie.core.CanCatch[F].catchNonFatal(fb())(f)
  }

  @SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
  private[Catching] final class CurriedCanCatchF1[F[*]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[B](b: => B): CurriedCanCatchF2[F, B] =
      new CurriedCanCatchF2[F, B](() => b)
  }

  private[Catching] final class CurriedCanCatchF2[F[*], B](
    private val b: () => B
  ) extends AnyVal {
    def apply[A](f: Throwable => A)(implicit EC: effectie.core.FxCtor[F], CC: effectie.core.CanCatch[F]): F[Either[A, B]] =
      effectie.core.CanCatch[F].catchNonFatal(effectie.core.FxCtor[F].effectOf(b()))(f)
  }

  private[Catching] final class CurriedCanCatchEither1[F[*]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A, B](fab: => F[Either[A, B]]): CurriedCanCatchEither2[F, A, B] =
      new CurriedCanCatchEither2[F, A, B](() => fab)
  }

  private[Catching] final class CurriedCanCatchEither2[F[*], A, B](
    private val fab: () => F[Either[A, B]]
  ) extends AnyVal {
    def apply(f: Throwable => A)(implicit CC: effectie.core.CanCatch[F]): F[Either[A, B]] =
      effectie.core.CanCatch[F].catchNonFatalEither(fab())(f)
  }

  @SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
  private[Catching] final class CurriedCanCatchEitherF1[F[*]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A, B](ab: => Either[A, B]): CurriedCanCatchEitherF2[F, A, B] =
      new CurriedCanCatchEitherF2[F, A, B](() => ab)
  }

  private[Catching] final class CurriedCanCatchEitherF2[F[*], A, B](
    private val ab: () => Either[A, B]
  ) extends AnyVal {
    def apply(f: Throwable => A)(implicit EC: effectie.core.FxCtor[F], CC: effectie.core.CanCatch[F]): F[Either[A, B]] =
      effectie.core.CanCatch[F].catchNonFatalEither(effectie.core.FxCtor[F].effectOf(ab()))(f)
  }

  private[Catching] final class CurriedCanCatchEitherT1[F[*]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A, B](fab: => EitherT[F, A, B]): CurriedCanCatchEitherT2[F, A, B] =
      new CurriedCanCatchEitherT2[F, A, B](() => fab)
  }

  private[Catching] final class CurriedCanCatchEitherT2[F[*], A, B](
    private val fab: () => EitherT[F, A, B]
  ) extends AnyVal {
    def apply(f: Throwable => A)(implicit CC: effectie.core.CanCatch[F]): EitherT[F, A, B] =
      effectie.core.CanCatch[F].catchNonFatalEitherT(fab())(f)
  }

}
