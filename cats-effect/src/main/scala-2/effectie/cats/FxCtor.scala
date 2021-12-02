package effectie.cats

import cats.Id
import cats.effect.IO

object FxCtor {
  type FxCtor[F[_]] = effectie.FxCtor[F]

  implicit object IoFxCtor extends FxCtor[IO] {

    @inline override final def effectOf[A](a: => A): IO[A] = IO(a)

    @inline override final def pureOf[A](a: A): IO[A] = IO.pure(a)

    @inline override final val unitOf: IO[Unit] = IO.unit

    @inline override final def errorOf[A](throwable: Throwable): IO[A] = IO.raiseError(throwable)

  }

  implicit object IdFxCtor extends FxCtor[Id] {

    @inline override final def effectOf[A](a: => A): Id[A] = a

    @inline override final def pureOf[A](a: A): Id[A] = a

    @inline override final val unitOf: Id[Unit] = ()

    @inline override final def errorOf[A](throwable: Throwable): Id[A] = throw throwable

  }

}
