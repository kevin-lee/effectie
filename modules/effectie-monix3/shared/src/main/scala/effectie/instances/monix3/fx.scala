package effectie.instances.monix3

import effectie.core.{Fx, FxCtor, OnNonFatal}
import monix.eval.Task

import scala.util.Try

/** @author Kevin Lee
  * @since 2021-05-16
  */
object fx {

  implicit object taskFx extends Fx[Task] {

    override implicit protected val fxCtor: FxCtor[Task] = effectie.instances.monix3.fxCtor.taskFxCtor

    @inline override final def effectOf[A](a: => A): Task[A] = fxCtor.effectOf(a)

    @inline override final def fromEffect[A](fa: => Task[A]): Task[A] = fxCtor.fromEffect(fa)

    @inline override final def pureOf[A](a: A): Task[A] = fxCtor.pureOf(a)

    @inline override final def pureOrError[A](a: => A): Task[A] = fxCtor.pureOrError(a)

    @inline override val unitOf: Task[Unit] = fxCtor.unitOf

    @inline override final def errorOf[A](throwable: Throwable): Task[A] = fxCtor.errorOf(throwable)

    @inline override final def fromEither[A](either: Either[Throwable, A]): Task[A] =
      fxCtor.fromEither(either)

    @inline override final def fromOption[A](option: Option[A])(orElse: => Throwable): Task[A] =
      fxCtor.fromOption(option)(orElse)

    @inline override final def fromTry[A](tryA: Try[A]): Task[A] = fxCtor.fromTry(tryA)

    @inline override final def flatMapFa[A, B](fa: Task[A])(f: A => Task[B]): Task[B] = fa.flatMap(f)

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

    @inline override final def onNonFatalWith[A](
      fa: => Task[A]
    )(partialFunction: PartialFunction[Throwable, Task[Unit]]): Task[A] =
      OnNonFatal[Task](fxCtor, canHandleError.taskCanHandleError).onNonFatalWith(fa)(partialFunction)

  }

}
