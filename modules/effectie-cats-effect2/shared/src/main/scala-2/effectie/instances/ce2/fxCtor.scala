package effectie.instances.ce2

import cats.MonadThrow
import cats.effect.IO
import effectie.core.FxCtor

import scala.util.Try

object fxCtor {

  implicit object ioFxCtor extends FxCtor[IO] {

    @inline override final def effectOf[A](a: => A): IO[A] = IO(a)

    @inline override final def fromEffect[A](fa: => IO[A]): IO[A] = IO.defer(fa)

    @inline override final def pureOf[A](a: A): IO[A] = IO.pure(a)

    @inline override final def pureOrError[A](a: => A): IO[A] = MonadThrow[IO].catchNonFatal(a)

    @inline override val unitOf: IO[Unit] = IO.unit

    @inline override final def errorOf[A](throwable: Throwable): IO[A] = IO.raiseError(throwable)

    @inline override final def fromEither[A](either: Either[Throwable, A]): IO[A] = IO.fromEither(either)

    @inline override final def fromOption[A](option: Option[A])(orElse: => Throwable): IO[A] =
      IO.fromOption(option)(orElse)

    @inline override final def fromTry[A](tryA: Try[A]): IO[A] = IO.fromTry(tryA)

    @inline override final def flatMapFa[A, B](fa: IO[A])(f: A => IO[B]): IO[B] = fa.flatMap(f)
  }

}
