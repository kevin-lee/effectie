package effectie.cats

import _root_.cats.data.EitherT
import effectie.Fx

/** @author Kevin Lee
  * @since 2021-11-25
  */
extension [F[_]](canCatch: effectie.CanCatch[F]) {

  def catchNonFatalEitherT[A, AA >: A, B](fab: => EitherT[F, A, B])(f: Throwable => AA): EitherT[F, AA, B] =
    EitherT(canCatch.catchNonFatalEither[A, AA, B](fab.value)(f))

}

extension [F[_]](fx: Fx[F]) {

  def catchNonFatalEitherT[A, AA >: A, B](fab: => EitherT[F, A, B])(f: Throwable => AA): EitherT[F, AA, B] =
    EitherT(fx.catchNonFatalEither[A, AA, B](fab.value)(f))

}