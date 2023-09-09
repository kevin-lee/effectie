package effectie.instances.ce2.f

import cats.effect.Sync
import cats.syntax.all._
import effectie.core.CanRecover

/** @author Kevin Lee
  * @since 2020-08-17
  */
object canRecover {

  implicit def syncCanRecover[F[*]: Sync]: CanRecover[F] = new CanRecover[F] {

    @inline override final def recoverFromNonFatalWith[A, AA >: A](fa: => F[A])(
      handleError: PartialFunction[Throwable, F[AA]]
    ): F[AA] =
      Sync[F].recoverWith(fa.widen[AA])(handleError)

    @inline override final def recoverFromNonFatal[A, AA >: A](fa: => F[A])(
      handleError: PartialFunction[Throwable, AA]
    ): F[AA] =
      Sync[F].recover(fa.widen[AA])(handleError)

  }

}
