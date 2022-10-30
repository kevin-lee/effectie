package effectie.ce2

import cats.effect.IO
import effectie.core.Fx

import scala.util.Try

object fx {

  implicit object ioFx extends Fx[IO] {

    @inline override final def effectOf[A](a: => A): IO[A] = fxCtor.ioFxCtor.effectOf(a)

    @inline override final def pureOf[A](a: A): IO[A] = fxCtor.ioFxCtor.pureOf(a)

    @inline override val unitOf: IO[Unit] = fxCtor.ioFxCtor.unitOf

    @inline override final def pureOrError[A](a: => A): IO[A] = fxCtor.ioFxCtor.pureOrError(a)

    @inline override final def errorOf[A](throwable: Throwable): IO[A] = fxCtor.ioFxCtor.errorOf(throwable)

    @inline override final def fromEither[A](either: Either[Throwable, A]): IO[A] = fxCtor.ioFxCtor.fromEither(either)

    @inline override final def fromOption[A](option: Option[A])(orElse: => Throwable): IO[A] =
      fxCtor.ioFxCtor.fromOption(option)(orElse)

    @inline override final def fromTry[A](tryA: Try[A]): IO[A] = fxCtor.ioFxCtor.fromTry(tryA)

    @inline override final def mapFa[A, B](fa: IO[A])(f: A => B): IO[B] = fa.map(f)

    @inline override final def catchNonFatalThrowable[A](fa: => IO[A]): IO[Either[Throwable, A]] =
      canCatch.canCatchIo.catchNonFatalThrowable(fa)

    @inline override final def handleNonFatalWith[A, AA >: A](fa: => IO[A])(handleError: Throwable => IO[AA]): IO[AA] =
      canHandleError.ioCanHandleError.handleNonFatalWith[A, AA](fa)(handleError)

    @inline override final def handleNonFatal[A, AA >: A](fa: => IO[A])(handleError: Throwable => AA): IO[AA] =
      canHandleError.ioCanHandleError.handleNonFatal[A, AA](fa)(handleError)

    @inline override final def recoverFromNonFatalWith[A, AA >: A](fa: => IO[A])(
      handleError: PartialFunction[Throwable, IO[AA]]
    ): IO[AA] =
      canRecover.ioCanRecover.recoverFromNonFatalWith[A, AA](fa)(handleError)

    @inline override final def recoverFromNonFatal[A, AA >: A](fa: => IO[A])(
      handleError: PartialFunction[Throwable, AA]
    ): IO[AA] =
      canRecover.ioCanRecover.recoverFromNonFatal[A, AA](fa)(handleError)
  }

}
