package effectie.cats

import cats.effect.{IO, Sync}
import cats.{Applicative, Id, Monad}

import scala.concurrent.{ExecutionContext, Future}

object Fx {
  type Fx[F[*]] = effectie.Fx[F]

  given ioFx: Fx[IO] with {

    inline override final def effectOf[A](a: => A): IO[A] = IO(a)

    inline override final def pureOf[A](a: A): IO[A] = IO.pure(a)

    inline override final def unitOf: IO[Unit] = IO.unit

    inline override final def errorOf[A](throwable: Throwable): IO[A] = IO.raiseError(throwable)

    inline override final def mapFa[A, B](fa: IO[A])(f: A => B): IO[B] = fa.map(f)

    inline override def catchNonFatalThrowable[A](fa: => IO[A]): IO[Either[Throwable, A]] =
      fa.attempt

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
