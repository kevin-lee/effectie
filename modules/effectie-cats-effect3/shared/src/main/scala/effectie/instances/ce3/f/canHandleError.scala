package effectie.instances.ce3.f

import cats.effect.Sync
import cats.syntax.all._
import effectie.core.CanHandleError

/** @author Kevin Lee
  * @since 2020-08-17
  */
object canHandleError {

  implicit def syncCanHandleError[F[*]: Sync]: CanHandleError[F] = new CanHandleError[F] {

    @inline override final def handleNonFatalWith[A, AA >: A](fa: => F[A])(handleError: Throwable => F[AA]): F[AA] =
      Sync[F].handleErrorWith(fa.widen[AA])(handleError)

    @inline override final def handleNonFatal[A, AA >: A](fa: => F[A])(handleError: Throwable => AA): F[AA] =
      Sync[F].handleError(fa.widen[AA])(handleError)

  }

}
