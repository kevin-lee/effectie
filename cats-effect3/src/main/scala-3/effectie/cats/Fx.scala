package effectie.cats

import cats.effect.{IO, Sync}
import cats.{Applicative, Id, Monad}

import scala.concurrent.{ExecutionContext, Future}

object Fx {

  type Fx[F[*]] = effectie.core.Fx[F]

  def apply[F[_]: Fx]: Fx[F] = summon[Fx[F]]

  given ioFx: Fx[IO] with {

    inline override final def effectOf[A](a: => A): IO[A] = FxCtor.ioFxCtor.effectOf(a)

    inline override final def pureOf[A](a: A): IO[A] = FxCtor.ioFxCtor.pureOf(a)

    inline override final def unitOf: IO[Unit] = FxCtor.ioFxCtor.unitOf

    inline override final def errorOf[A](throwable: Throwable): IO[A] = FxCtor.ioFxCtor.errorOf(throwable)

    inline override final def mapFa[A, B](fa: IO[A])(f: A => B): IO[B] = fa.map(f)

    inline override final def catchNonFatalThrowable[A](fa: => IO[A]): IO[Either[Throwable, A]] =
      CanCatch.canCatchIo.catchNonFatalThrowable(fa)

    inline override final def handleNonFatalWith[A, AA >: A](fa: => IO[A])(handleError: Throwable => IO[AA]): IO[AA] =
      CanHandleError.ioCanHandleError.handleNonFatalWith(fa)(handleError)

    inline override final def handleNonFatal[A, AA >: A](fa: => IO[A])(handleError: Throwable => AA): IO[AA] =
      CanHandleError.ioCanHandleError.handleNonFatal(fa)(handleError)

    inline override final def recoverFromNonFatalWith[A, AA >: A](fa: => IO[A])(handleError: PartialFunction[Throwable, IO[AA]]): IO[AA] =
      CanRecover.ioCanRecover.recoverFromNonFatalWith(fa)(handleError)

    inline override final def recoverFromNonFatal[A, AA >: A](fa: => IO[A])(handleError: PartialFunction[Throwable, AA]): IO[AA] =
      CanRecover.ioCanRecover.recoverFromNonFatal(fa)(handleError)

  }

  given idFx: Fx[Id] with {

    inline override final def effectOf[A](a: => A): Id[A] = FxCtor.idFxCtor.effectOf(a)

    inline override final def pureOf[A](a: A): Id[A] = FxCtor.idFxCtor.pureOf(a)

    inline override final def unitOf: Id[Unit] = FxCtor.idFxCtor.unitOf

    inline override final def errorOf[A](throwable: Throwable): Id[A] = FxCtor.idFxCtor.errorOf(throwable)

    inline override final def mapFa[A, B](fa: Id[A])(f: A => B): Id[B] = f(fa)

    inline override final def catchNonFatalThrowable[A](fa: => Id[A]): Id[Either[Throwable, A]] =
      CanCatch.canCatchId.catchNonFatalThrowable(fa)

    inline override final def handleNonFatalWith[A, AA >: A](fa: => Id[A])(handleError: Throwable => Id[AA]): Id[AA] =
      CanHandleError.idCanHandleError.handleNonFatalWith(fa)(handleError)

    inline override final def handleNonFatal[A, AA >: A](fa: => Id[A])(handleError: Throwable => AA): Id[AA] =
      CanHandleError.idCanHandleError.handleNonFatal(fa)(handleError)

    inline override final def recoverFromNonFatalWith[A, AA >: A](fa: => Id[A])(handleError: PartialFunction[Throwable, Id[AA]]): Id[AA] =
      CanRecover.idCanRecover.recoverFromNonFatalWith(fa)(handleError)

    inline override final def recoverFromNonFatal[A, AA >: A](fa: => Id[A])(handleError: PartialFunction[Throwable, AA]): Id[AA] =
      CanRecover.idCanRecover.recoverFromNonFatal(fa)(handleError)

  }

}
