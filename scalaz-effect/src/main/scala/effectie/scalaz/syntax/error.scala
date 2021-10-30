package effectie.scalaz.syntax


import scalaz._
import effectie.scalaz.syntax.error.{EitherTFABErrorHandlingOps, FAErrorHandlingOps, FEitherABErrorHandlingOps}
import effectie.scalaz.{CanCatch, CanHandleError, CanRecover}

/** @author Kevin Lee
 * @since 2021-10-16
 */
trait error {

  implicit def fAErrorHandlingOps[F[_], B](fb: F[B]): FAErrorHandlingOps[F, B] = new FAErrorHandlingOps(fb)

  implicit def fEitherABErrorHandlingOps[F[_], A, B](fab: F[A \/ B]): FEitherABErrorHandlingOps[F, A, B] =
    new FEitherABErrorHandlingOps(fab)

  implicit def eitherTFABErrorHandlingOps[F[_], A, B](efab: EitherT[F, A, B]): EitherTFABErrorHandlingOps[F, A, B] =
    new EitherTFABErrorHandlingOps(efab)

}

object error extends error {

  final class FAErrorHandlingOps[F[_], B](private val fb: F[B]) extends AnyVal {

    def catchNonFatalThrowable[A](
      implicit canCatch: CanCatch[F]
    ): F[Throwable \/ B] =
      canCatch.catchNonFatalThrowable[B](fb)

    def catchNonFatal[A](
      f: Throwable => A
    )(
      implicit canCatch: CanCatch[F]
    ): F[A \/ B] =
      canCatch.catchNonFatal[A, B](fb)(f)

    def handleNonFatalWith[BB >: B](
      handleError: Throwable => F[BB]
    )(
      implicit canHandleError: CanHandleError[F]
    ): F[BB] =
      canHandleError.handleNonFatalWith[B, BB](fb)(handleError)

    def handleNonFatal[BB >: B](handleError: Throwable => BB)(
      implicit canHandleError: CanHandleError[F]
    ): F[BB] =
      canHandleError.handleNonFatal[B, BB](fb)(handleError)

    def recoverFromNonFatalWith[BB >: B](
      handleError: PartialFunction[Throwable, F[BB]]
    )(
      implicit canRecover: CanRecover[F]
    ): F[BB] =
      canRecover.recoverFromNonFatalWith[B, BB](fb)(handleError)

    def recoverFromNonFatal[BB >: B](handleError: PartialFunction[Throwable, BB])(
      implicit canRecover: CanRecover[F]
    ): F[BB] =
      canRecover.recoverFromNonFatal[B, BB](fb)(handleError)
  }

  final class FEitherABErrorHandlingOps[F[_], A, B](private val fab: F[A \/ B]) extends AnyVal {

    def catchNonFatalEither[AA >: A](
      f: Throwable => AA
    )(
      implicit canCatch: CanCatch[F]
    ): F[AA \/ B] =
      canCatch.catchNonFatalEither[A, AA, B](fab)(f)

    def handleEitherNonFatalWith[AA >: A, BB >: B](
      handleError: Throwable => F[AA \/ BB]
    )(
      implicit canHandleError: CanHandleError[F]
    ): F[AA \/ BB] =
      canHandleError.handleEitherNonFatalWith[A, AA, B, BB](fab)(handleError)

    def handleEitherNonFatal[AA >: A, BB >: B](
      handleError: Throwable => AA \/ BB
    )(
      implicit canHandleError: CanHandleError[F]
    ): F[AA \/ BB] =
      canHandleError.handleEitherNonFatal[A, AA, B, BB](fab)(handleError)

    def recoverEitherFromNonFatalWith[AA >: A, BB >: B](
      handleError: PartialFunction[Throwable, F[AA \/ BB]]
    )(
      implicit canRecover: CanRecover[F]
    ): F[AA \/ BB] =
      canRecover.recoverEitherFromNonFatalWith[A, AA, B, BB](fab)(handleError)

    def recoverEitherFromNonFatal[AA >: A, BB >: B](
      handleError: PartialFunction[Throwable, AA \/ BB]
    )(
      implicit canRecover: CanRecover[F]
    ): F[AA \/ BB] =
      canRecover.recoverEitherFromNonFatal[A, AA, B, BB](fab)(handleError)
  }

  final class EitherTFABErrorHandlingOps[F[_], A, B](private val efab: EitherT[F, A, B]) extends AnyVal {

    def catchNonFatalEitherT[AA >: A](
      f: Throwable => AA
    )(
      implicit canCatch: CanCatch[F]
    ): EitherT[F, AA, B] =
      canCatch.catchNonFatalEitherT[A, AA, B](efab)(f)

    def handleEitherTNonFatalWith[AA >: A, BB >: B](
      handleError: Throwable => F[AA \/ BB]
    )(
      implicit canHandleError: CanHandleError[F]
    ): EitherT[F, AA, BB] =
      canHandleError.handleEitherTNonFatalWith[A, AA, B, BB](efab)(handleError)

    def handleEitherTNonFatal[AA >: A, BB >: B](
      handleError: Throwable => AA \/ BB
    )(
      implicit canHandleError: CanHandleError[F]
    ): EitherT[F, AA, BB] =
      canHandleError.handleEitherTNonFatal[A, AA, B, BB](efab)(handleError)

    def recoverEitherTFromNonFatalWith[AA >: A, BB >: B](
      handleError: PartialFunction[Throwable, F[AA \/ BB]]
    )(
      implicit canRecover: CanRecover[F]
    ): EitherT[F, AA, BB] =
      canRecover.recoverEitherTFromNonFatalWith[A, AA, B, BB](efab)(handleError)

    def recoverEitherTFromNonFatal[AA >: A, BB >: B](
      handleError: PartialFunction[Throwable, AA \/ BB]
    )(
      implicit canRecover: CanRecover[F]
    ): EitherT[F, AA, BB] =
      canRecover.recoverEitherTFromNonFatal[A, AA, B, BB](efab)(handleError)
  }

}
