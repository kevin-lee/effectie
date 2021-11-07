package effectie.cats

import cats.effect.{IO, Sync}
import cats.{Applicative, Id, Monad}

import scala.concurrent.{ExecutionContext, Future}

trait Fx[F[_]] extends effectie.Fx[F] with FxCtor[F] with effectie.FxCtor[F]

object Fx {

  def apply[F[_]: Fx]: Fx[F] = summon[Fx[F]]

  given ioFx: Fx[IO] with {

    inline override final def effectOf[A](a: => A): IO[A] = IO(a)

    inline override final def pureOf[A](a: A): IO[A] = IO.pure(a)

    inline override final def unitOf: IO[Unit] = IO.unit

    inline override final def errorOf[A](throwable: Throwable): IO[A] = IO.raiseError(throwable)

  }

  given futureFx(using EC: ExecutionContext): Fx[Future] =
    new FutureFx

  final class FutureFx(using override val EC0: ExecutionContext)
      extends Fx[Future]
      with FxCtor[Future]
      with effectie.FxCtor.FutureFxCtor

  given idFx: Fx[Id] with {

    inline override final def effectOf[A](a: => A): Id[A] = a

    inline override final def pureOf[A](a: A): Id[A] = a

    inline override final def unitOf: Id[Unit] = ()

    inline override final def errorOf[A](throwable: Throwable): Id[A] = throw throwable
  }

}
