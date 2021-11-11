package effectie.cats

import cats.effect.{IO, Sync}
import cats.{Applicative, Id, Monad}

import scala.concurrent.{ExecutionContext, Future}

trait Fx[F[_]] extends effectie.Fx[F] with FxCtor[F] with effectie.FxCtor[F] with CanCatch[F]

object Fx {

  def apply[F[_]: Fx]: Fx[F] = summon[Fx[F]]

  given ioFx: Fx[IO] with {

    inline override final def effectOf[A](a: => A): IO[A] = IO(a)

    inline override final def pureOf[A](a: A): IO[A] = IO.pure(a)

    inline override final def unitOf: IO[Unit] = IO.unit

    inline override final def errorOf[A](throwable: Throwable): IO[A] = IO.raiseError(throwable)

    inline override final def mapFa[A, B](fa: IO[A])(f: A => B): IO[B] = fa.map(f)

    inline override def catchNonFatalThrowable[A](fa: => IO[A]): IO[Either[Throwable, A]] =
      fa.attempt

  }

  given futureFx(using EC: ExecutionContext): Fx[Future] =
    new FutureFx

  final class FutureFx(using override val EC0: ExecutionContext)
      extends Fx[Future]
      with FxCtor[Future]
      with effectie.FxCtor.FutureFxCtor
      with effectie.CanCatch.CanCatchFuture {

    override def catchNonFatalThrowable[A](fa: => Future[A]): Future[Either[Throwable, A]] =
      CanCatch.canCatchFuture.catchNonFatalThrowable(fa)
  }

  given idFx: Fx[Id] with {

    inline override final def effectOf[A](a: => A): Id[A] = a

    inline override final def pureOf[A](a: A): Id[A] = a

    inline override final def unitOf: Id[Unit] = ()

    inline override final def errorOf[A](throwable: Throwable): Id[A] = throw throwable

    inline override def mapFa[A, B](fa: Id[A])(f: A => B): Id[B] =
      CanCatch.canCatchId.mapFa(fa)(f)

    inline override def catchNonFatalThrowable[A](fa: => Id[A]): Id[Either[Throwable, A]] =
      CanCatch.canCatchId.catchNonFatalThrowable(fa)
  }

}
