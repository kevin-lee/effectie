package effectie.monix

import cats.effect.IO
import cats.Id
import monix.eval.Task

/** @author Kevin Lee
  * @since 2021-05-16
  */
object Fx {
  type Fx[F[_]] = effectie.core.Fx[F]

  implicit object TaskFx extends Fx[Task] {

    @inline override final def effectOf[A](a: => A): Task[A] = FxCtor.TaskFxCtor.effectOf(a)

    @inline override final def pureOf[A](a: A): Task[A] = FxCtor.TaskFxCtor.pureOf(a)

    @inline override final val unitOf: Task[Unit] = FxCtor.TaskFxCtor.unitOf

    @inline override final def errorOf[A](throwable: Throwable): Task[A] = FxCtor.TaskFxCtor.errorOf(throwable)

    @inline override final def mapFa[A, B](fa: Task[A])(f: A => B): Task[B] = fa.map(f)

    @inline override final def catchNonFatalThrowable[A](fa: => Task[A]): Task[Either[Throwable, A]] =
      CanCatch.CanCatchTask.catchNonFatalThrowable(fa)

    @inline override final def handleNonFatalWith[A, AA >: A](fa: => Task[A])(
      handleError: Throwable => Task[AA]
    ): Task[AA] =
      CanHandleError.TaskCanHandleError.handleNonFatalWith[A, AA](fa)(handleError)

    @inline override final def handleNonFatal[A, AA >: A](fa: => Task[A])(handleError: Throwable => AA): Task[AA] =
      CanHandleError.TaskCanHandleError.handleNonFatal[A, AA](fa)(handleError)

    @inline override final def recoverFromNonFatalWith[A, AA >: A](fa: => Task[A])(
      handleError: PartialFunction[Throwable, Task[AA]]
    ): Task[AA] =
      CanRecover.TaskCanRecover.recoverFromNonFatalWith[A, AA](fa)(handleError)

    @inline override final def recoverFromNonFatal[A, AA >: A](fa: => Task[A])(
      handleError: PartialFunction[Throwable, AA]
    ): Task[AA] =
      CanRecover.TaskCanRecover.recoverFromNonFatal[A, AA](fa)(handleError)
  }

  implicit object IoFx extends Fx[IO] {

    @inline override final def effectOf[A](a: => A): IO[A] = FxCtor.IoFxCtor.effectOf(a)

    @inline override final def pureOf[A](a: A): IO[A] = FxCtor.IoFxCtor.pureOf(a)

    @inline override final val unitOf: IO[Unit] = FxCtor.IoFxCtor.unitOf

    @inline override final def errorOf[A](throwable: Throwable): IO[A] = FxCtor.IoFxCtor.errorOf(throwable)

    @inline override final def mapFa[A, B](fa: IO[A])(f: A => B): IO[B] = fa.map(f)

    @inline override final def catchNonFatalThrowable[A](fa: => IO[A]): IO[Either[Throwable, A]] =
      CanCatch.CanCatchIo.catchNonFatalThrowable(fa)

    @inline override final def handleNonFatalWith[A, AA >: A](fa: => IO[A])(handleError: Throwable => IO[AA]): IO[AA] =
      CanHandleError.IoCanHandleError.handleNonFatalWith[A, AA](fa)(handleError)

    @inline override final def handleNonFatal[A, AA >: A](fa: => IO[A])(handleError: Throwable => AA): IO[AA] =
      CanHandleError.IoCanHandleError.handleNonFatal[A, AA](fa)(handleError)

    @inline override final def recoverFromNonFatalWith[A, AA >: A](fa: => IO[A])(
      handleError: PartialFunction[Throwable, IO[AA]]
    ): IO[AA] =
      CanRecover.IoCanRecover.recoverFromNonFatalWith[A, AA](fa)(handleError)

    @inline override final def recoverFromNonFatal[A, AA >: A](fa: => IO[A])(
      handleError: PartialFunction[Throwable, AA]
    ): IO[AA] =
      CanRecover.IoCanRecover.recoverFromNonFatal[A, AA](fa)(handleError)
  }

  implicit object IdFx extends Fx[Id] {

    @inline override final def effectOf[A](a: => A): Id[A] = FxCtor.IdFxCtor.effectOf(a)

    @inline override final def pureOf[A](a: A): Id[A] = FxCtor.IdFxCtor.pureOf(a)

    @inline override final val unitOf: Id[Unit] = FxCtor.IdFxCtor.unitOf

    @inline override final def errorOf[A](throwable: Throwable): Id[A] = FxCtor.IdFxCtor.errorOf(throwable)

    @inline override final def mapFa[A, B](fa: Id[A])(f: A => B): Id[B] = f(fa)

    @inline override final def catchNonFatalThrowable[A](fa: => Id[A]): Id[Either[Throwable, A]] =
      CanCatch.CanCatchId.catchNonFatalThrowable(fa)

    @inline override final def handleNonFatalWith[A, AA >: A](fa: => Id[A])(handleError: Throwable => Id[AA]): Id[AA] =
      CanHandleError.IdCanHandleError.handleNonFatalWith[A, AA](fa)(handleError)

    @inline override final def handleNonFatal[A, AA >: A](fa: => Id[A])(handleError: Throwable => AA): Id[AA] =
      CanHandleError.IdCanHandleError.handleNonFatal[A, AA](fa)(handleError)

    @inline override final def recoverFromNonFatalWith[A, AA >: A](fa: => Id[A])(
      handleError: PartialFunction[Throwable, Id[AA]]
    ): Id[AA] =
      CanRecover.IdCanRecover.recoverFromNonFatalWith[A, AA](fa)(handleError)

    @inline override final def recoverFromNonFatal[A, AA >: A](fa: => Id[A])(
      handleError: PartialFunction[Throwable, AA]
    ): Id[AA] =
      CanRecover.IdCanRecover.recoverFromNonFatal[A, AA](fa)(handleError)
  }

}
