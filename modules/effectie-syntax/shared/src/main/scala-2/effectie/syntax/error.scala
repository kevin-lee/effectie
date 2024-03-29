package effectie.syntax

import _root_.cats.data.EitherT
import cats.FlatMap
import effectie.core.{CanCatch, CanHandleError, CanRecover, FxCtor}

/** @author Kevin Lee
  * @since 2021-10-16
  */
trait error {
  import effectie.syntax.error._

  implicit def fAErrorHandlingOps[F[*], B](fb: => F[B]): FAErrorHandlingOps[F, B] = new FAErrorHandlingOps(() => fb)

  implicit def fEitherABErrorHandlingOps[F[*], A, B](fab: => F[Either[A, B]]): FEitherABErrorHandlingOps[F, A, B] =
    new FEitherABErrorHandlingOps(() => fab)

  implicit def eitherTFABErrorHandlingOps[F[*], A, B](efab: => EitherT[F, A, B]): EitherTFABErrorHandlingOps[F, A, B] =
    new EitherTFABErrorHandlingOps(() => efab)

  implicit def canCatchOps[F[*]](canCatch: effectie.core.CanCatch[F]): CanCatchOps[F[*]] = new CanCatchOps(canCatch)

  implicit def canHandleErrorOps[F[*]](canHandleError: effectie.core.CanHandleError[F]): CanHandleErrorOps[F[*]] =
    new CanHandleErrorOps(canHandleError)

  implicit def canRecoverOps[F[*]](canRecover: effectie.core.CanRecover[F]): CanRecoverOps[F[*]] =
    new CanRecoverOps(canRecover)

  implicit def fxOps[F[*]](fx: effectie.core.Fx[F]): FxOps[F[*]] = new FxOps(fx)

}

object error extends error {

  final class FAErrorHandlingOps[F[*], B](private val fb: () => F[B]) extends AnyVal {

    def catchNonFatalThrowable(
      implicit canCatch: CanCatch[F]
    ): F[Either[Throwable, B]] =
      canCatch.catchNonFatalThrowable[B](fb())

    def catchNonFatal[A](
      f: PartialFunction[Throwable, A]
    )(
      implicit canCatch: CanCatch[F]
    ): F[Either[A, B]] =
      canCatch.catchNonFatal[A, B](fb())(f)

    def handleNonFatalWith[BB >: B](
      handleError: Throwable => F[BB]
    )(
      implicit canHandleError: CanHandleError[F]
    ): F[BB] =
      canHandleError.handleNonFatalWith[B, BB](fb())(handleError)

    def handleNonFatal[BB >: B](handleError: Throwable => BB)(
      implicit canHandleError: CanHandleError[F]
    ): F[BB] =
      canHandleError.handleNonFatal[B, BB](fb())(handleError)

    def recoverFromNonFatalWith[BB >: B](
      handleError: PartialFunction[Throwable, F[BB]]
    )(
      implicit canRecover: CanRecover[F]
    ): F[BB] =
      canRecover.recoverFromNonFatalWith[B, BB](fb())(handleError)

    def recoverFromNonFatal[BB >: B](handleError: PartialFunction[Throwable, BB])(
      implicit canRecover: CanRecover[F]
    ): F[BB] =
      canRecover.recoverFromNonFatal[B, BB](fb())(handleError)
  }

  final class FEitherABErrorHandlingOps[F[*], A, B](private val fab: () => F[Either[A, B]]) extends AnyVal {

    def catchNonFatalEither[AA >: A](
      f: PartialFunction[Throwable, AA]
    )(
      implicit canCatch: CanCatch[F]
    ): F[Either[AA, B]] =
      canCatch.catchNonFatalEither[A, AA, B](fab())(f)

    def handleEitherNonFatalWith[AA >: A, BB >: B](
      handleError: Throwable => F[Either[AA, BB]]
    )(
      implicit canHandleError: CanHandleError[F]
    ): F[Either[AA, BB]] =
      canHandleError.handleEitherNonFatalWith[A, AA, B, BB](fab())(handleError)

    def handleEitherNonFatal[AA >: A, BB >: B](
      handleError: Throwable => Either[AA, BB]
    )(
      implicit canHandleError: CanHandleError[F]
    ): F[Either[AA, BB]] =
      canHandleError.handleEitherNonFatal[A, AA, B, BB](fab())(handleError)

    def recoverEitherFromNonFatalWith[AA >: A, BB >: B](
      handleError: PartialFunction[Throwable, F[Either[AA, BB]]]
    )(
      implicit canRecover: CanRecover[F]
    ): F[Either[AA, BB]] =
      canRecover.recoverEitherFromNonFatalWith[A, AA, B, BB](fab())(handleError)

    def recoverEitherFromNonFatal[AA >: A, BB >: B](
      handleError: PartialFunction[Throwable, Either[AA, BB]]
    )(
      implicit canRecover: CanRecover[F]
    ): F[Either[AA, BB]] =
      canRecover.recoverEitherFromNonFatal[A, AA, B, BB](fab())(handleError)

    def rethrowIfLeft[AA >: A](implicit ev: AA <:< Throwable, fxCtor: FxCtor[F], M: FlatMap[F]): F[B] =
      M.flatMap(fab()) {
        case Left(a) => fxCtor.errorOf(a)
        case Right(b) => fxCtor.pureOf(b)
      }

  }

  final class EitherTFABErrorHandlingOps[F[*], A, B](private val efab: () => EitherT[F, A, B]) extends AnyVal {

    def catchNonFatalEitherT[AA >: A](
      f: PartialFunction[Throwable, AA]
    )(
      implicit canCatch: CanCatch[F]
    ): EitherT[F, AA, B] =
      canCatch.catchNonFatalEitherT[A, AA, B](efab())(f)

    def handleEitherTNonFatalWith[AA >: A, BB >: B](
      handleError: Throwable => F[Either[AA, BB]]
    )(
      implicit canHandleError: CanHandleError[F]
    ): EitherT[F, AA, BB] =
      canHandleError.handleEitherTNonFatalWith[A, AA, B, BB](efab())(handleError)

    def handleEitherTNonFatal[AA >: A, BB >: B](
      handleError: Throwable => Either[AA, BB]
    )(
      implicit canHandleError: CanHandleError[F]
    ): EitherT[F, AA, BB] =
      canHandleError.handleEitherTNonFatal[A, AA, B, BB](efab())(handleError)

    def recoverEitherTFromNonFatalWith[AA >: A, BB >: B](
      handleError: PartialFunction[Throwable, F[Either[AA, BB]]]
    )(
      implicit canRecover: CanRecover[F]
    ): EitherT[F, AA, BB] =
      canRecover.recoverEitherTFromNonFatalWith[A, AA, B, BB](efab())(handleError)

    def recoverEitherTFromNonFatal[AA >: A, BB >: B](
      handleError: PartialFunction[Throwable, Either[AA, BB]]
    )(
      implicit canRecover: CanRecover[F]
    ): EitherT[F, AA, BB] =
      canRecover.recoverEitherTFromNonFatal[A, AA, B, BB](efab())(handleError)

    def rethrowTIfLeft[AA >: A](implicit ev: AA <:< Throwable, fxCtor: FxCtor[F], M: FlatMap[F]): F[B] =
      efab().foldF(
        fxCtor.errorOf[B](_),
        fxCtor.pureOf,
      )

  }

  final class CanCatchOps[F[*]](private val canCatch: effectie.core.CanCatch[F]) extends AnyVal {

    def catchNonFatalEitherT[A, AA >: A, B](fab: => EitherT[F, A, B])(
      f: PartialFunction[Throwable, AA]
    ): EitherT[F, AA, B] =
      EitherT(canCatch.catchNonFatalEither[A, AA, B](fab.value)(f))

  }

  final class CanHandleErrorOps[F[*]](private val canHandleError: effectie.core.CanHandleError[F]) extends AnyVal {

    def handleEitherTNonFatalWith[A, AA >: A, B, BB >: B](
      efab: => EitherT[F, A, B]
    )(
      handleError: Throwable => F[Either[AA, BB]]
    ): EitherT[F, AA, BB] =
      EitherT(canHandleError.handleNonFatalWith[Either[A, B], Either[AA, BB]](efab.value)(handleError))

    def handleEitherTNonFatal[A, AA >: A, B, BB >: B](
      efab: => EitherT[F, A, B]
    )(
      handleError: Throwable => Either[AA, BB]
    ): EitherT[F, AA, BB] =
      EitherT(canHandleError.handleNonFatal[Either[A, B], Either[AA, BB]](efab.value)(handleError))

  }

  final class CanRecoverOps[F[*]](private val canRecover: effectie.core.CanRecover[F]) extends AnyVal {

    final def recoverEitherTFromNonFatalWith[A, AA >: A, B, BB >: B](
      efab: => EitherT[F, A, B]
    )(
      handleError: PartialFunction[Throwable, F[Either[AA, BB]]]
    ): EitherT[F, AA, BB] =
      EitherT(canRecover.recoverFromNonFatalWith[Either[A, B], Either[AA, BB]](efab.value)(handleError))

    final def recoverEitherTFromNonFatal[A, AA >: A, B, BB >: B](
      efab: => EitherT[F, A, B]
    )(
      handleError: PartialFunction[Throwable, Either[AA, BB]]
    ): EitherT[F, AA, BB] =
      EitherT(canRecover.recoverFromNonFatal[Either[A, B], Either[AA, BB]](efab.value)(handleError))

  }

  final class FxOps[F[*]](private val fx: effectie.core.Fx[F]) extends AnyVal {

    def catchNonFatalEitherT[A, AA >: A, B](fab: => EitherT[F, A, B])(
      f: PartialFunction[Throwable, AA]
    ): EitherT[F, AA, B] =
      EitherT(fx.catchNonFatalEither[A, AA, B](fab.value)(f))

  }

}
