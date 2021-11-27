package effectie

import _root_.cats.data.EitherT

/** @author Kevin Lee
  * @since 2021-11-23
  */
package object cats {

  implicit final class CanCatchOps[F[_]](private val canCatch: effectie.CanCatch[F]) extends AnyVal {

    def catchNonFatalEitherT[A, AA >: A, B](fab: => EitherT[F, A, B])(f: Throwable => AA): EitherT[F, AA, B] =
      EitherT(canCatch.catchNonFatalEither[A, AA, B](fab.value)(f))

  }

  implicit final class FxhOps[F[_]](private val fx: Fx[F]) extends AnyVal {

    def catchNonFatalEitherT[A, AA >: A, B](fab: => EitherT[F, A, B])(f: Throwable => AA): EitherT[F, AA, B] =
      EitherT(fx.catchNonFatalEither[A, AA, B](fab.value)(f))

  }

}
