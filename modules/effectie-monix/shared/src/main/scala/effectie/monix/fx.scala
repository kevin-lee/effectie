package effectie.monix

import cats.Id
import cats.effect.IO
import effectie.core.Fx
import monix.eval.Task

/** @author Kevin Lee
  * @since 2021-05-16
  */
object fx {

  implicit object taskFx extends Fx[Task] {

    @inline override final def effectOf[A](a: => A): Task[A] = fxCtor.taskFxCtor.effectOf(a)

    @inline override final def pureOf[A](a: A): Task[A] = fxCtor.taskFxCtor.pureOf(a)

    @inline override val unitOf: Task[Unit] = fxCtor.taskFxCtor.unitOf

    @inline override final def errorOf[A](throwable: Throwable): Task[A] = fxCtor.taskFxCtor.errorOf(throwable)

    @inline override final def mapFa[A, B](fa: Task[A])(f: A => B): Task[B] = fa.map(f)

    @inline override final def catchNonFatalThrowable[A](fa: => Task[A]): Task[Either[Throwable, A]] =
      canCatch.canCatchTask.catchNonFatalThrowable(fa)

    @inline override final def handleNonFatalWith[A, AA >: A](fa: => Task[A])(
      handleError: Throwable => Task[AA]
    ): Task[AA] =
      canHandleError.taskCanHandleError.handleNonFatalWith[A, AA](fa)(handleError)

    @inline override final def handleNonFatal[A, AA >: A](fa: => Task[A])(handleError: Throwable => AA): Task[AA] =
      canHandleError.taskCanHandleError.handleNonFatal[A, AA](fa)(handleError)

    @inline override final def recoverFromNonFatalWith[A, AA >: A](fa: => Task[A])(
      handleError: PartialFunction[Throwable, Task[AA]]
    ): Task[AA] =
      canRecover.taskCanRecover.recoverFromNonFatalWith[A, AA](fa)(handleError)

    @inline override final def recoverFromNonFatal[A, AA >: A](fa: => Task[A])(
      handleError: PartialFunction[Throwable, AA]
    ): Task[AA] =
      canRecover.taskCanRecover.recoverFromNonFatal[A, AA](fa)(handleError)
  }

  implicit object ioFx extends Fx[IO] {

    @inline override final def effectOf[A](a: => A): IO[A] = fxCtor.ioFxCtor.effectOf(a)

    @inline override final def pureOf[A](a: A): IO[A] = fxCtor.ioFxCtor.pureOf(a)

    @inline override val unitOf: IO[Unit] = fxCtor.ioFxCtor.unitOf

    @inline override final def errorOf[A](throwable: Throwable): IO[A] = fxCtor.ioFxCtor.errorOf(throwable)

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

  implicit object idFx extends Fx[Id] {

    @inline override final def effectOf[A](a: => A): Id[A] = fxCtor.idFxCtor.effectOf(a)

    @inline override final def pureOf[A](a: A): Id[A] = fxCtor.idFxCtor.pureOf(a)

    @inline override val unitOf: Id[Unit] = fxCtor.idFxCtor.unitOf

    @inline override final def errorOf[A](throwable: Throwable): Id[A] = fxCtor.idFxCtor.errorOf(throwable)

    @inline override final def mapFa[A, B](fa: Id[A])(f: A => B): Id[B] = f(fa)

    @inline override final def catchNonFatalThrowable[A](fa: => Id[A]): Id[Either[Throwable, A]] =
      canCatch.canCatchId.catchNonFatalThrowable(fa)

    @inline override final def handleNonFatalWith[A, AA >: A](fa: => Id[A])(handleError: Throwable => Id[AA]): Id[AA] =
      canHandleError.idCanHandleError.handleNonFatalWith[A, AA](fa)(handleError)

    @inline override final def handleNonFatal[A, AA >: A](fa: => Id[A])(handleError: Throwable => AA): Id[AA] =
      canHandleError.idCanHandleError.handleNonFatal[A, AA](fa)(handleError)

    @inline override final def recoverFromNonFatalWith[A, AA >: A](fa: => Id[A])(
      handleError: PartialFunction[Throwable, Id[AA]]
    ): Id[AA] =
      canRecover.idCanRecover.recoverFromNonFatalWith[A, AA](fa)(handleError)

    @inline override final def recoverFromNonFatal[A, AA >: A](fa: => Id[A])(
      handleError: PartialFunction[Throwable, AA]
    ): Id[AA] =
      canRecover.idCanRecover.recoverFromNonFatal[A, AA](fa)(handleError)
  }

}