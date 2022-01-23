package effectie.monix.syntax

import cats.data.EitherT
import effectie.monix.{CanCatchOps, CanHandleErrorOps, CanRecoverOps}
import effectie.core.{CanCatch, CanHandleError, CanRecover}

/** @author Kevin Lee
  * @since 2021-10-16
  */
trait error {
  import effectie.monix.syntax.error.EitherTFABErrorHandlingOps

  implicit def eitherTFABErrorHandlingOps[F[_], A, B](efab: EitherT[F, A, B]): EitherTFABErrorHandlingOps[F, A, B] =
    new EitherTFABErrorHandlingOps(efab)

}

object error extends error {

  final class EitherTFABErrorHandlingOps[F[_], A, B](private val efab: EitherT[F, A, B]) extends AnyVal {

    def catchNonFatalEitherT[AA >: A](
      f: Throwable => AA
    )(
      implicit canCatch: CanCatch[F]
    ): EitherT[F, AA, B] =
      canCatch.catchNonFatalEitherT[A, AA, B](efab)(f)

    def handleEitherTNonFatalWith[AA >: A, BB >: B](
      handleError: Throwable => F[Either[AA, BB]]
    )(
      implicit canHandleError: CanHandleError[F]
    ): EitherT[F, AA, BB] =
      canHandleError.handleEitherTNonFatalWith[A, AA, B, BB](efab)(handleError)

    def handleEitherTNonFatal[AA >: A, BB >: B](
      handleError: Throwable => Either[AA, BB]
    )(
      implicit canHandleError: CanHandleError[F]
    ): EitherT[F, AA, BB] =
      canHandleError.handleEitherTNonFatal[A, AA, B, BB](efab)(handleError)

    def recoverEitherTFromNonFatalWith[AA >: A, BB >: B](
      handleError: PartialFunction[Throwable, F[Either[AA, BB]]]
    )(
      implicit canRecover: CanRecover[F]
    ): EitherT[F, AA, BB] =
      canRecover.recoverEitherTFromNonFatalWith[A, AA, B, BB](efab)(handleError)

    def recoverEitherTFromNonFatal[AA >: A, BB >: B](
      handleError: PartialFunction[Throwable, Either[AA, BB]]
    )(
      implicit canRecover: CanRecover[F]
    ): EitherT[F, AA, BB] =
      canRecover.recoverEitherTFromNonFatal[A, AA, B, BB](efab)(handleError)
  }

}
