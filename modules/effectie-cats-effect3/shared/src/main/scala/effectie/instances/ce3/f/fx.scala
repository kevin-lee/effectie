package effectie.instances.ce3.f

import cats.effect.Sync
import effectie.core.{Fx, FxCtor, OnNonFatal}

import scala.util.Try

object fx {

  implicit def syncFx[F[*]: Sync]: Fx[F] = new Fx[F] {

    override implicit protected val fxCtor: FxCtor[F] = effectie.instances.ce3.f.fxCtor.syncFxCtor

    @inline override final def effectOf[A](a: => A): F[A] = fxCtor.effectOf(a)

    @inline override final def fromEffect[A](fa: => F[A]): F[A] = fxCtor.fromEffect(fa)

    @inline override final def pureOf[A](a: A): F[A] = fxCtor.pureOf(a)

    @inline override final def pureOrError[A](a: => A): F[A] = fxCtor.pureOrError(a)

    @inline override val unitOf: F[Unit] = fxCtor.unitOf

    @inline override final def errorOf[A](throwable: Throwable): F[A] = fxCtor.errorOf(throwable)

    @inline override final def fromEither[A](either: Either[Throwable, A]): F[A] = fxCtor.fromEither(either)

    @inline override final def fromOption[A](option: Option[A])(orElse: => Throwable): F[A] =
      fxCtor.fromOption(option)(orElse)

    @inline override final def fromTry[A](tryA: Try[A]): F[A] = fxCtor.fromTry(tryA)

    @inline override final def flatMapFa[A, B](fa: F[A])(f: A => F[B]): F[B] = Sync[F].flatMap(fa)(f)

    @inline override final def catchNonFatalThrowable[A](fa: => F[A]): F[Either[Throwable, A]] =
      canCatch.syncCanCatch.catchNonFatalThrowable(fa)

    @inline override final def handleNonFatalWith[A, AA >: A](fa: => F[A])(handleError: Throwable => F[AA]): F[AA] =
      canHandleError.syncCanHandleError.handleNonFatalWith[A, AA](fa)(handleError)

    @inline override final def handleNonFatal[A, AA >: A](fa: => F[A])(handleError: Throwable => AA): F[AA] =
      canHandleError.syncCanHandleError.handleNonFatal[A, AA](fa)(handleError)

    @inline override final def recoverFromNonFatalWith[A, AA >: A](fa: => F[A])(
      handleError: PartialFunction[Throwable, F[AA]]
    ): F[AA] =
      canRecover.syncCanRecover.recoverFromNonFatalWith[A, AA](fa)(handleError)

    @inline override final def recoverFromNonFatal[A, AA >: A](fa: => F[A])(
      handleError: PartialFunction[Throwable, AA]
    ): F[AA] =
      canRecover.syncCanRecover.recoverFromNonFatal[A, AA](fa)(handleError)

    @inline override final def onNonFatalWith[A](
      fa: => F[A]
    )(partialFunction: PartialFunction[Throwable, F[Unit]]): F[A] =
      OnNonFatal[F](fxCtor, canHandleError.syncCanHandleError).onNonFatalWith(fa)(partialFunction)

  }

}
