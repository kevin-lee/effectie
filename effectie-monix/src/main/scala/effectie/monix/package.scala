package effectie

import cats.data.EitherT

/** @author Kevin Lee
  * @since 2021-11-23
  */
package object monix {
  implicit final class CanCatchOps[F[_]](private val canCatch: effectie.CanCatch[F]) extends AnyVal {

    def catchNonFatalEitherT[A, AA >: A, B](fab: => EitherT[F, A, B])(f: Throwable => AA): EitherT[F, AA, B] =
      EitherT(canCatch.catchNonFatalEither[A, AA, B](fab.value)(f))

  }

  implicit final class CanHandleErrorOps[F[_]](private val canHandleError: effectie.CanHandleError[F]) extends AnyVal {

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

  implicit final class FxhOps[F[_]](private val fx: Fx[F]) extends AnyVal {

    def catchNonFatalEitherT[A, AA >: A, B](fab: => EitherT[F, A, B])(f: Throwable => AA): EitherT[F, AA, B] =
      EitherT(fx.catchNonFatalEither[A, AA, B](fab.value)(f))

  }
}
