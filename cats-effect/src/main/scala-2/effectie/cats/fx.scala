package effectie.cats

import cats.effect.IO
import cats.Id

object fx {
  private type Fx[F[_]] = effectie.core.Fx[F]

  implicit object IoFx extends Fx[IO] {

    @inline override final def effectOf[A](a: => A): IO[A] = fxCtor.IoFxCtor.effectOf(a)

    @inline override final def pureOf[A](a: A): IO[A] = fxCtor.IoFxCtor.pureOf(a)

    @inline override final val unitOf: IO[Unit] = fxCtor.IoFxCtor.unitOf

    @inline override final def errorOf[A](throwable: Throwable): IO[A] = fxCtor.IoFxCtor.errorOf(throwable)

    @inline override final def mapFa[A, B](fa: IO[A])(f: A => B): IO[B] = fa.map(f)

    @inline override final def catchNonFatalThrowable[A](fa: => IO[A]): IO[Either[Throwable, A]] =
      canCatch.CanCatchIo.catchNonFatalThrowable(fa)

    @inline override final def handleNonFatalWith[A, AA >: A](fa: => IO[A])(handleError: Throwable => IO[AA]): IO[AA] =
      canHandleError.IoCanHandleError.handleNonFatalWith[A, AA](fa)(handleError)

    @inline override final def handleNonFatal[A, AA >: A](fa: => IO[A])(handleError: Throwable => AA): IO[AA] =
      canHandleError.IoCanHandleError.handleNonFatal[A, AA](fa)(handleError)

    @inline override final def recoverFromNonFatalWith[A, AA >: A](fa: => IO[A])(
      handleError: PartialFunction[Throwable, IO[AA]]
    ): IO[AA] =
      canRecover.IoCanRecover.recoverFromNonFatalWith[A, AA](fa)(handleError)

    @inline override final def recoverFromNonFatal[A, AA >: A](fa: => IO[A])(
      handleError: PartialFunction[Throwable, AA]
    ): IO[AA] =
      canRecover.IoCanRecover.recoverFromNonFatal[A, AA](fa)(handleError)
  }

  implicit object IdFx extends Fx[Id] {

    @inline override final def effectOf[A](a: => A): Id[A] = fxCtor.IdFxCtor.effectOf(a)

    @inline override final def pureOf[A](a: A): Id[A] = fxCtor.IdFxCtor.pureOf(a)

    @inline override final val unitOf: Id[Unit] = fxCtor.IdFxCtor.unitOf

    @inline override final def errorOf[A](throwable: Throwable): Id[A] = fxCtor.IdFxCtor.errorOf(throwable)

    @inline override final def mapFa[A, B](fa: Id[A])(f: A => B): Id[B] = f(fa)

    @inline override final def catchNonFatalThrowable[A](fa: => Id[A]): Id[Either[Throwable, A]] =
      canCatch.CanCatchId.catchNonFatalThrowable(fa)

    @inline override final def handleNonFatalWith[A, AA >: A](fa: => Id[A])(handleError: Throwable => Id[AA]): Id[AA] =
      canHandleError.IdCanHandleError.handleNonFatalWith[A, AA](fa)(handleError)

    @inline override final def handleNonFatal[A, AA >: A](fa: => Id[A])(handleError: Throwable => AA): Id[AA] =
      canHandleError.IdCanHandleError.handleNonFatal[A, AA](fa)(handleError)

    @inline override final def recoverFromNonFatalWith[A, AA >: A](fa: => Id[A])(
      handleError: PartialFunction[Throwable, Id[AA]]
    ): Id[AA] =
      canRecover.IdCanRecover.recoverFromNonFatalWith[A, AA](fa)(handleError)

    @inline override final def recoverFromNonFatal[A, AA >: A](fa: => Id[A])(
      handleError: PartialFunction[Throwable, AA]
    ): Id[AA] =
      canRecover.IdCanRecover.recoverFromNonFatal[A, AA](fa)(handleError)
  }

}
