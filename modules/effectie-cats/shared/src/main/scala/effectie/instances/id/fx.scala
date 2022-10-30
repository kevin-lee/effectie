package effectie.instances.id

import cats.Id
import effectie.core.Fx

import scala.util.Try

object fx {

  implicit object idFx extends Fx[Id] {

    @inline override final def effectOf[A](a: => A): Id[A] = fxCtor.idFxCtor.effectOf(a)

    @inline override final def pureOf[A](a: A): Id[A] = fxCtor.idFxCtor.pureOf(a)

    @inline override final def pureOrError[A](a: => A): Id[A] = fxCtor.idFxCtor.pureOrError(a)

    @inline override val unitOf: Id[Unit] = fxCtor.idFxCtor.unitOf

    @inline override final def errorOf[A](throwable: Throwable): Id[A] = fxCtor.idFxCtor.errorOf(throwable)

    @inline override final def fromEither[A](either: Either[Throwable, A]): Id[A] = fxCtor.idFxCtor.fromEither(either)

    @inline override final def fromOption[A](option: Option[A])(orElse: => Throwable): Id[A] =
      fxCtor.idFxCtor.fromOption(option)(orElse)

    @inline override final def fromTry[A](tryA: Try[A]): Id[A] = fxCtor.idFxCtor.fromTry(tryA)

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
