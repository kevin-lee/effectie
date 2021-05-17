package effectie.monix

import cats.data.EitherT

/** @author Kevin Lee
  * @since 2020-06-07
  */
trait Catching {

  import Catching._

  final def catchNonFatal[F[_]]: CurriedCanCatch1[F] =
    new CurriedCanCatch1[F]

  final def catchNonFatalF[F[_]]: CurriedCanCatchF1[F] =
    new CurriedCanCatchF1[F]

  def catchNonFatalEither[F[_]]: CurriedCanCatchEither1[F] =
    new CurriedCanCatchEither1[F]

  def catchNonFatalEitherF[F[_]]: CurriedCanCatchEitherF1[F] =
    new CurriedCanCatchEitherF1[F]

  def catchNonFatalEitherT[F[_]]: CurriedCanCatchEitherT1[F] =
    new CurriedCanCatchEitherT1[F]

//  def catchNonFatalEitherT[F[_], A, B](fab: => EitherT[F, A, B])(f: Throwable => A)(implicit CC: CanCatch[F]): EitherT[F, A, B] =
//    CanCatch[F].catchNonFatalEitherT(fab)(f)
}

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
object Catching extends Catching {

  private[Catching] final class CurriedCanCatch1[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[B](fb: => F[B]): CurriedCanCatch2[F, B] =
      new CurriedCanCatch2[F, B](() => fb)
  }

  private[Catching] final class CurriedCanCatch2[F[_], B](
    private val fb: () => F[B]
  ) extends AnyVal {
    def apply[A](f: Throwable => A)(implicit CC: CanCatch[F]): F[Either[A, B]] =
      CanCatch[F].catchNonFatal(fb())(f)
  }

  @SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
  private[Catching] final class CurriedCanCatchF1[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[B](b: => B): CurriedCanCatchF2[F, B] =
      new CurriedCanCatchF2[F, B](() => b)
  }

  private[Catching] final class CurriedCanCatchF2[F[_], B](
    private val b: () => B
  ) extends AnyVal {
    def apply[A](f: Throwable => A)(implicit EC: Eft[F], CC: CanCatch[F]): F[Either[A, B]] =
      CanCatch[F].catchNonFatal(Eft[F].effectOf(b()))(f)
  }

  private[Catching] final class CurriedCanCatchEither1[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A, B](fab: => F[Either[A, B]]): CurriedCanCatchEither2[F, A, B] =
      new CurriedCanCatchEither2[F, A, B](() => fab)
  }

  private[Catching] final class CurriedCanCatchEither2[F[_], A, B](
    private val fab: () => F[Either[A, B]]
  ) extends AnyVal {
    def apply(f: Throwable => A)(implicit CC: CanCatch[F]): F[Either[A, B]] =
      CanCatch[F].catchNonFatalEither(fab())(f)
  }

  @SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
  private[Catching] final class CurriedCanCatchEitherF1[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A, B](ab: => Either[A, B]): CurriedCanCatchEitherF2[F, A, B] =
      new CurriedCanCatchEitherF2[F, A, B](() => ab)
  }

  private[Catching] final class CurriedCanCatchEitherF2[F[_], A, B](
    private val ab: () => Either[A, B]
  ) extends AnyVal {
    def apply(f: Throwable => A)(implicit EC: Eft[F], CC: CanCatch[F]): F[Either[A, B]] =
      CanCatch[F].catchNonFatalEither(Eft[F].effectOf(ab()))(f)
  }

  private[Catching] final class CurriedCanCatchEitherT1[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A, B](fab: => EitherT[F, A, B]): CurriedCanCatchEitherT2[F, A, B] =
      new CurriedCanCatchEitherT2[F, A, B](() => fab)
  }

  private[Catching] final class CurriedCanCatchEitherT2[F[_], A, B](
    private val fab: () => EitherT[F, A, B]
  ) extends AnyVal {
    def apply(f: Throwable => A)(implicit CC: CanCatch[F]): EitherT[F, A, B] =
      CanCatch[F].catchNonFatalEitherT(fab())(f)
  }

}
