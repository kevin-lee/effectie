package effectie.instances.ce3

import cats.effect.IO
import effectie.core.{Fx, FxCtor, OnNonFatal}

import scala.util.Try

object fx {

  given ioFx: Fx[IO] with {

    override implicit protected val fxCtor: FxCtor[IO] = effectie.instances.ce3.fxCtor.ioFxCtor

    inline override final def effectOf[A](a: => A): IO[A] = fxCtor.effectOf(a)

    inline override final def fromEffect[A](fa: => IO[A]): IO[A] = fxCtor.fromEffect(fa)

    inline override final def pureOf[A](a: A): IO[A] = fxCtor.pureOf(a)

    inline override final def pureOrError[A](a: => A): IO[A] = fxCtor.pureOrError(a)

    inline override final def unitOf: IO[Unit] = fxCtor.unitOf

    inline override final def errorOf[A](throwable: Throwable): IO[A] = fxCtor.errorOf(throwable)

    inline override final def fromEither[A](either: Either[Throwable, A]): IO[A] = fxCtor.fromEither(either)

    inline override final def fromOption[A](option: Option[A])(orElse: => Throwable): IO[A] =
      fxCtor.fromOption(option)(orElse)

    inline override final def fromTry[A](tryA: Try[A]): IO[A] = fxCtor.fromTry(tryA)

    inline override final def flatMapFa[A, B](fa: IO[A])(f: A => IO[B]): IO[B] = fa.flatMap(f)

    inline override final def catchNonFatalThrowable[A](fa: => IO[A]): IO[Either[Throwable, A]] =
      canCatch.canCatchIo.catchNonFatalThrowable(fa)

    inline override final def handleNonFatalWith[A, AA >: A](fa: => IO[A])(handleError: Throwable => IO[AA]): IO[AA] =
      canHandleError.ioCanHandleError.handleNonFatalWith(fa)(handleError)

    inline override final def handleNonFatal[A, AA >: A](fa: => IO[A])(handleError: Throwable => AA): IO[AA] =
      canHandleError.ioCanHandleError.handleNonFatal(fa)(handleError)

    inline override final def recoverFromNonFatalWith[A, AA >: A](fa: => IO[A])(
      handleError: PartialFunction[Throwable, IO[AA]]
    ): IO[AA] =
      canRecover.ioCanRecover.recoverFromNonFatalWith(fa)(handleError)

    inline override final def recoverFromNonFatal[A, AA >: A](fa: => IO[A])(
      handleError: PartialFunction[Throwable, AA]
    ): IO[AA] =
      canRecover.ioCanRecover.recoverFromNonFatal(fa)(handleError)

    inline override final def onNonFatalWith[A](fa: => IO[A])(
      partialFunction: PartialFunction[Throwable, IO[Unit]]
    ): IO[A] =
      OnNonFatal[IO](fxCtor, canHandleError.ioCanHandleError).onNonFatalWith(fa)(partialFunction)

  }

}
