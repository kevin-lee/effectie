package effectie.instances.tries

import effectie.core.{CanCatch, FxCtor}

import scala.util.{Failure, Success, Try}

/** @author Kevin Lee
  * @since 2023-03-17
  */
object canCatch {

  trait TryCanCatch extends CanCatch[Try] {

    override implicit protected val fxCtor: FxCtor[Try] = effectie.instances.tries.fxCtor.fxCtorTry

    @inline override final def catchNonFatalThrowable[A](fa: => Try[A]): Try[Either[Throwable, A]] =
      fa match {
        case Success(a) =>
          Try[Either[Throwable, A]](Right(a))

        case Failure(ex) =>
          Try[Either[Throwable, A]](Left(ex))

      }

  }

  implicit object canCatchTry extends TryCanCatch
}
