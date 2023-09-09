package effectie.instances.ce3.f

import cats.effect.Sync
import effectie.core.{CanCatch, FxCtor}

/** @author Kevin Lee
  * @since 2020-06-07
  */
object canCatch {

  implicit def syncCanCatch[F[*]: Sync]: CanCatch[F] = new CanCatch[F] {

    override implicit protected val fxCtor: FxCtor[F] = effectie.instances.ce3.f.fxCtor.syncFxCtor

    @inline override final def catchNonFatalThrowable[A](fa: => F[A]): F[Either[Throwable, A]] =
      Sync[F].attempt(fa)

  }

}
