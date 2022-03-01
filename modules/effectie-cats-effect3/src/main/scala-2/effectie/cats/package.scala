package effectie

import _root_.cats.data.EitherT

/** @author Kevin Lee
  * @since 2021-11-23
  */
package object cats {

  implicit final class CanCatchOps[F[*]](private val canCatch: effectie.core.CanCatch[F]) extends AnyVal {

    def catchNonFatalEitherT[A, AA >: A, B](fab: => EitherT[F, A, B])(f: Throwable => AA): EitherT[F, AA, B] =
      EitherT(canCatch.catchNonFatalEither[A, AA, B](fab.value)(f))

  }

  implicit final class CanHandleErrorOps[F[*]](private val canHandleError: effectie.core.CanHandleError[F]) extends AnyVal {

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

  implicit final class CanRecoverOps[F[*]](private val canRecover: effectie.core.CanRecover[F]) extends AnyVal {

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

  implicit final class FxhOps[F[*]](private val fx: effectie.core.Fx[F]) extends AnyVal {

    def catchNonFatalEitherT[A, AA >: A, B](fab: => EitherT[F, A, B])(f: Throwable => AA): EitherT[F, AA, B] =
      EitherT(fx.catchNonFatalEither[A, AA, B](fab.value)(f))

  }

}
