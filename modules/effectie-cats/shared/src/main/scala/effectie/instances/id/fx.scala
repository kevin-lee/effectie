package effectie.instances.id

import cats.Id
import effectie.core.{Fx, FxCtor, OnNonFatal}

import scala.util.Try

object fx {

  implicit object idFx extends Fx[Id] {

    override implicit protected val fxCtor: FxCtor[Id] = effectie.instances.id.fxCtor.idFxCtor

    @inline override final def effectOf[A](a: => A): Id[A] = fxCtor.effectOf(a)

    @inline override final def fromEffect[A](fa: => Id[A]): Id[A] = fxCtor.fromEffect(fa)

    @inline override final def pureOf[A](a: A): Id[A] = fxCtor.pureOf(a)

    @inline override final def pureOrError[A](a: => A): Id[A] = fxCtor.pureOrError(a)

    @inline override val unitOf: Id[Unit] = fxCtor.unitOf

    @inline override final def errorOf[A](throwable: Throwable): Id[A] = fxCtor.errorOf(throwable)

    @inline override final def fromEither[A](either: Either[Throwable, A]): Id[A] = fxCtor.fromEither(either)

    @inline override final def fromOption[A](option: Option[A])(orElse: => Throwable): Id[A] =
      fxCtor.fromOption(option)(orElse)

    @inline override final def fromTry[A](tryA: Try[A]): Id[A] = fxCtor.fromTry(tryA)

    @inline override final def flatMapFa[A, B](fa: Id[A])(f: A => Id[B]): Id[B] = f(fa)

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

    @inline override final def onNonFatalWith[A](fa: => Id[A])(
      partialFunction: PartialFunction[Throwable, Id[Unit]]
    ): Id[A] =
      OnNonFatal[Id](fxCtor, canHandleError.idCanHandleError).onNonFatalWith(fa)(partialFunction)

  }

}
