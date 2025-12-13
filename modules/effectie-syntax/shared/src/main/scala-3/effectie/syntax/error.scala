package effectie.syntax

import cats.FlatMap
import cats.data.EitherT
import effectie.core.{CanCatch, CanHandleError, CanRecover, Fx, FxCtor, OnNonFatal}

/** @author Kevin Lee
  * @since 2021-10-16
  */
trait error {

  extension [F[*], B](fb: => F[B]) {

    def catchNonFatalThrowable(
      using canCatch: CanCatch[F]
    ): F[Either[Throwable, B]] =
      canCatch.catchNonFatalThrowable[B](fb)

    def catchNonFatal[A](
      f: PartialFunction[Throwable, A]
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

    def onNonFatalWith(
      partialFunction: PartialFunction[Throwable, F[Unit]]
    )(
      using onNonFatal: OnNonFatal[F]
    ): F[B] =
      onNonFatal.onNonFatalWith(fb)(partialFunction)

  }

  extension [F[*], A, B](fab: => F[Either[A, B]]) {

    def catchNonFatalEither[AA >: A](
      f: PartialFunction[Throwable, AA]
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

    def rethrowIfLeft[AA >: A](using ev: AA <:< Throwable, fxCtor: FxCtor[F], M: FlatMap[F]): F[B] =
      M.flatMap(fab) {
        case Left(a) => fxCtor.errorOf(ev(a))
        case Right(b) => fxCtor.pureOf(b)
      }

  }

  extension [F[*], A, B](efab: => EitherT[F, A, B]) {

    def catchNonFatalEitherT[AA >: A](
      f: PartialFunction[Throwable, AA]
    )(
      using canCatch: CanCatch[F]
    ): EitherT[F, AA, B] =
      effectie.syntax.error.catchNonFatalEitherT(canCatch)[A, AA, B](efab)(f)

    def handleEitherTNonFatalWith[AA >: A, BB >: B](
      handleError: Throwable => F[Either[AA, BB]]
    )(
      using canHandleError: CanHandleError[F]
    ): EitherT[F, AA, BB] = {
      effectie.syntax.error.handleEitherTNonFatalWith(canHandleError)[A, AA, B, BB](efab)(handleError)
    }

    def handleEitherTNonFatal[AA >: A, BB >: B](
      handleError: Throwable => Either[AA, BB]
    )(
      using canHandleError: CanHandleError[F]
    ): EitherT[F, AA, BB] =
      effectie.syntax.error.handleEitherTNonFatal(canHandleError)[A, AA, B, BB](efab)(handleError)

    def recoverEitherTFromNonFatalWith[AA >: A, BB >: B](
      handleError: PartialFunction[Throwable, F[Either[AA, BB]]]
    )(
      using canRecover: CanRecover[F]
    ): EitherT[F, AA, BB] =
      effectie.syntax.error.recoverEitherTFromNonFatalWith(canRecover)[A, AA, B, BB](efab)(handleError)

    def recoverEitherTFromNonFatal[AA >: A, BB >: B](
      handleError: PartialFunction[Throwable, Either[AA, BB]]
    )(
      using canRecover: CanRecover[F]
    ): EitherT[F, AA, BB] =
      effectie.syntax.error.recoverEitherTFromNonFatal(canRecover)[A, AA, B, BB](efab)(handleError)

    def rethrowTIfLeft[AA >: A](using ev: AA <:< Throwable, fxCtor: FxCtor[F], M: FlatMap[F]): F[B] =
      efab.foldF(
        err => fxCtor.errorOf[B](ev(err)),
        fxCtor.pureOf,
      )

    def onNonFatalWith(
      partialFunction: PartialFunction[Throwable, F[Unit]]
    )(
      using onNonFatal: OnNonFatal[F]
    ): EitherT[F, A, B] =
      EitherT(onNonFatal.onNonFatalWith(efab.value)(partialFunction))

  }

  extension [F[*]](canCatch: effectie.core.CanCatch[F]) {

    def catchNonFatalEitherT[A, AA >: A, B](fab: => EitherT[F, A, B])(
      f: PartialFunction[Throwable, AA]
    ): EitherT[F, AA, B] =
      EitherT(canCatch.catchNonFatalEither[A, AA, B](fab.value)(f))

  }

  extension [F[*]](canHandleError: effectie.core.CanHandleError[F]) {

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

  extension [F[*]](canRecover: effectie.core.CanRecover[F]) {

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

  extension [F[*]](fx: Fx[F]) {

    def catchNonFatalEitherT[A, AA >: A, B](fab: => EitherT[F, A, B])(
      f: PartialFunction[Throwable, AA]
    ): EitherT[F, AA, B] =
      EitherT(fx.catchNonFatalEither[A, AA, B](fab.value)(f))

  }

}

object error extends error
