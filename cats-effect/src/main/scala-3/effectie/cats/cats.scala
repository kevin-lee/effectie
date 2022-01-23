package effectie.cats

import _root_.cats.data.EitherT
import effectie.core.Fx

/** @author Kevin Lee
  * @since 2021-11-25
  */
extension [F[*]](canCatch: effectie.core.CanCatch[F]) {

  def catchNonFatalEitherT[A, AA >: A, B](fab: => EitherT[F, A, B])(f: Throwable => AA): EitherT[F, AA, B] =
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

extension [F[_]](canRecover: effectie.core.CanRecover[F]) {

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

  def catchNonFatalEitherT[A, AA >: A, B](fab: => EitherT[F, A, B])(f: Throwable => AA): EitherT[F, AA, B] =
    EitherT(fx.catchNonFatalEither[A, AA, B](fab.value)(f))

}