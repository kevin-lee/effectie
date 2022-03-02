package effectie.cats.syntax

import cats.data.EitherT
import effectie.core.{CanCatch, CanHandleError, CanRecover}
import effectie.cats.*

/** @author Kevin Lee
  * @since 2021-10-16
  */
trait error {

  extension [F[*], A, B](efab: => EitherT[F, A, B]) {

    def catchNonFatalEitherT[AA >: A](
      f: Throwable => AA
    )(
      using canCatch: CanCatch[F]
    ): EitherT[F, AA, B] =
      effectie.cats.catchNonFatalEitherT(canCatch)[A, AA, B](efab)(f)


    def handleEitherTNonFatalWith[AA >: A, BB >: B](
      handleError: Throwable => F[Either[AA, BB]]
    )(
      using canHandleError: CanHandleError[F]
    ): EitherT[F, AA, BB] = {
      effectie.cats.handleEitherTNonFatalWith(canHandleError)[A, AA, B, BB](efab)(handleError)
    }

    def handleEitherTNonFatal[AA >: A, BB >: B](
      handleError: Throwable => Either[AA, BB]
    )(
      using canHandleError: CanHandleError[F]
    ): EitherT[F, AA, BB] =
      effectie.cats.handleEitherTNonFatal(canHandleError)[A, AA, B, BB](efab)(handleError)

    def recoverEitherTFromNonFatalWith[AA >: A, BB >: B](
      handleError: PartialFunction[Throwable, F[Either[AA, BB]]]
    )(
      using canRecover: CanRecover[F]
    ): EitherT[F, AA, BB] =
      effectie.cats.recoverEitherTFromNonFatalWith(canRecover)[A, AA, B, BB](efab)(handleError)

    def recoverEitherTFromNonFatal[AA >: A, BB >: B](
      handleError: PartialFunction[Throwable, Either[AA, BB]]
    )(
      using canRecover: CanRecover[F]
    ): EitherT[F, AA, BB] =
      effectie.cats.recoverEitherTFromNonFatal(canRecover)[A, AA, B, BB](efab)(handleError)
  }

}

object error extends error
