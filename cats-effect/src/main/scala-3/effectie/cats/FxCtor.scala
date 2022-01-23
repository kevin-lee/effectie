package effectie.cats

import cats.Id
import cats.effect.IO

import scala.concurrent.{ExecutionContext, Future}

object FxCtor {
  type FxCtor[F[*]] = effectie.core.FxCtor[F]

  given ioFxCtor: FxCtor[IO] with {

    inline override final def effectOf[A](a: => A): IO[A] = IO(a)

    inline override final def pureOf[A](a: A): IO[A] = IO.pure(a)

    inline override final def unitOf: IO[Unit] = IO.unit

    inline override final def errorOf[A](throwable: Throwable): IO[A] = IO.raiseError(throwable)

  }

  given idFxCtor: FxCtor[Id] with {

    inline override final def effectOf[A](a: => A): Id[A] = a

    inline override final def pureOf[A](a: A): Id[A] = a

    inline override final def unitOf: Id[Unit] = ()

    inline override final def errorOf[A](throwable: Throwable): Id[A] = throw throwable

  }

}
