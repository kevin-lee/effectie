package effectie.cats

import cats.Id
import cats.effect.IO
import effectie.core.FxCtor

object fxCtor {

  implicit object ioFxCtor extends FxCtor[IO] {

    @inline override final def effectOf[A](a: => A): IO[A] = IO(a)

    @inline override final def pureOf[A](a: A): IO[A] = IO.pure(a)

    @inline override val unitOf: IO[Unit] = IO.unit

    @inline override final def errorOf[A](throwable: Throwable): IO[A] = IO.raiseError(throwable)

  }

  implicit object idFxCtor extends FxCtor[Id] {

    @inline override final def effectOf[A](a: => A): Id[A] = a

    @inline override final def pureOf[A](a: A): Id[A] = a

    @inline override val unitOf: Id[Unit] = ()

    @inline override final def errorOf[A](throwable: Throwable): Id[A] =
      throw throwable // scalafix:ok DisableSyntax.throw

  }

}
