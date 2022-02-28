package effectie.syntax

import effectie.core.{CanCatch, CanHandleError, CanRecover}

/**
 * @author Kevin Lee
 * @since 2022-01-17
 */
trait error {

  extension [F[*], B](fb: => F[B]) {

    def catchNonFatalThrowable(
      using canCatch: CanCatch[F]
    ): F[Either[Throwable, B]] =
      canCatch.catchNonFatalThrowable[B](fb)

    def catchNonFatal[A](
      f: Throwable => A
    )(
      using canCatch: CanCatch[F]
    ): F[Either[A, B]] =
      canCatch.catchNonFatal[A, B](fb)(f)

    def handleNonFatalWith[BB >: B](
      handleError: Throwable => F[BB]
    )(
      using canHandleError: CanHandleError[F]
    ): F[BB] =
      canHandleError.handleNonFatalWith[B, BB](fb)(handleError)

    def handleNonFatal[BB >: B](handleError: Throwable => BB)(
      using canHandleError: CanHandleError[F]
    ): F[BB] =
      canHandleError.handleNonFatal[B, BB](fb)(handleError)

    def recoverFromNonFatalWith[BB >: B](
      handleError: PartialFunction[Throwable, F[BB]]
    )(
      using canRecover: CanRecover[F]
    ): F[BB] =
      canRecover.recoverFromNonFatalWith[B, BB](fb)(handleError)

    def recoverFromNonFatal[BB >: B](handleError: PartialFunction[Throwable, BB])(
      using canRecover: CanRecover[F]
    ): F[BB] =
      canRecover.recoverFromNonFatal[B, BB](fb)(handleError)
  }

  extension [F[*], A, B](fab: => F[Either[A, B]]) {

    def catchNonFatalEither[AA >: A](
      f: Throwable => AA
    )(
      using canCatch: CanCatch[F]
    ): F[Either[AA, B]] =
      canCatch.catchNonFatalEither[A, AA, B](fab)(f)

    def handleEitherNonFatalWith[AA >: A, BB >: B](
      handleError: Throwable => F[Either[AA, BB]]
    )(
      using canHandleError: CanHandleError[F]
    ): F[Either[AA, BB]] =
      canHandleError.handleEitherNonFatalWith[A, AA, B, BB](fab)(handleError)

    def handleEitherNonFatal[AA >: A, BB >: B](
      handleError: Throwable => Either[AA, BB]
    )(
      using canHandleError: CanHandleError[F]
    ): F[Either[AA, BB]] =
      canHandleError.handleEitherNonFatal[A, AA, B, BB](fab)(handleError)

    def recoverEitherFromNonFatalWith[AA >: A, BB >: B](
      handleError: PartialFunction[Throwable, F[Either[AA, BB]]]
    )(
      using canRecover: CanRecover[F]
    ): F[Either[AA, BB]] =
      canRecover.recoverEitherFromNonFatalWith[A, AA, B, BB](fab)(handleError)

    def recoverEitherFromNonFatal[AA >: A, BB >: B](
      handleError: PartialFunction[Throwable, Either[AA, BB]]
    )(
      using canRecover: CanRecover[F]
    ): F[Either[AA, BB]] =
      canRecover.recoverEitherFromNonFatal[A, AA, B, BB](fab)(handleError)
  }

}

object error extends error
