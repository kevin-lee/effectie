package effectie.instances.tries

import effectie.core.FxCtor

import scala.util.{Failure, Success, Try}

/** @author Kevin Lee
  * @since 2023-03-17
  */
object fxCtor {

  trait TryFxCtor extends FxCtor[Try] {

    @inline override final def effectOf[A](a: => A): Try[A] = Try(a)

    @inline override final def fromEffect[A](fa: => Try[A]): Try[A] = Try(fa).flatten

    @inline override final def pureOf[A](a: A): Try[A] = Success(a)

    @inline override def pureOrError[A](a: => A): Try[A] = Try(a)

    @inline override final def unitOf: Try[Unit] = pureOf(())

    @inline override final def errorOf[A](throwable: Throwable): Try[A] = Failure[A](throwable)

    @inline override final def fromEither[A](either: Either[Throwable, A]): Try[A] = either.fold(errorOf, pureOf)

    @inline override final def fromOption[A](option: Option[A])(orElse: => Throwable): Try[A] =
      option.fold(errorOf[A](orElse))(pureOf)

    @inline override final def fromTry[A](tryA: Try[A]): Try[A] = tryA

    @inline override final def flatMapFa[A, B](fa: Try[A])(f: A => Try[B]): Try[B] = fa.flatMap(f)
  }

  implicit object fxCtorTry extends TryFxCtor

}
