package effectie.cats

import cats.effect.IO
import cats.Id

object Fx {
  type Fx[F[_]] = effectie.Fx[F]

  implicit object IoFx extends Fx[IO] {

    @inline override final def effectOf[A](a: => A): IO[A] = IO(a)

    @inline override final def pureOf[A](a: A): IO[A] = IO.pure(a)

    @inline override final val unitOf: IO[Unit] = IO.unit

    @inline override final def errorOf[A](throwable: Throwable): IO[A] = IO.raiseError(throwable)

    @inline override final def mapFa[A, B](fa: IO[A])(f: A => B): IO[B] =
      CanCatch.CanCatchIo.mapFa(fa)(f)

    override def catchNonFatalThrowable[A](fa: => IO[A]): IO[Either[Throwable, A]] =
      CanCatch.CanCatchIo.catchNonFatalThrowable(fa)
  }

  implicit object IdFx extends Fx[Id] {

    @inline override final def effectOf[A](a: => A): Id[A] = a

    @inline override final def pureOf[A](a: A): Id[A] = a

    @inline override final val unitOf: Id[Unit] = ()

    @inline override final def errorOf[A](throwable: Throwable): Id[A] = throw throwable

    @inline override def mapFa[A, B](fa: Id[A])(f: A => B): Id[B] =
      CanCatch.CanCatchId.mapFa(fa)(f)

    @inline override def catchNonFatalThrowable[A](fa: => Id[A]): Id[Either[Throwable, A]] =
      CanCatch.CanCatchId.catchNonFatalThrowable(fa)
  }

}
