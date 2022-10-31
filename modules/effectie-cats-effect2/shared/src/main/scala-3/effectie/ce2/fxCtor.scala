package effectie.ce2

import cats.{Id, MonadThrow}
import cats.effect.IO
import effectie.core.FxCtor

import scala.util.Try
import scala.util.control.NonFatal

object fxCtor {

  given ioFxCtor: FxCtor[IO] with {

    inline override final def effectOf[A](a: => A): IO[A] = IO(a)

    inline override final def pureOf[A](a: A): IO[A] = IO.pure(a)

    inline override final def pureOrError[A](a: => A): IO[A] = MonadThrow[IO].catchNonFatal(a)

    inline override final def unitOf: IO[Unit] = IO.unit

    inline override final def errorOf[A](throwable: Throwable): IO[A] = IO.raiseError(throwable)

    inline override final def fromEither[A](either: Either[Throwable, A]): IO[A] = IO.fromEither(either)

    inline override final def fromOption[A](option: Option[A])(orElse: => Throwable): IO[A] =
      IO.fromOption(option)(orElse)

    inline override final def fromTry[A](tryA: Try[A]): IO[A] = IO.fromTry(tryA)

  }

}
