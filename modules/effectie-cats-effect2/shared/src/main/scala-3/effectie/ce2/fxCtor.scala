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

  given idFxCtor: FxCtor[Id] with {

    inline override final def effectOf[A](a: => A): Id[A] = a

    inline override final def pureOf[A](a: A): Id[A] = a

    inline override final def pureOrError[A](a: => A): Id[A] =
      try pureOf(a)
      catch {
        case NonFatal(ex) => throw ex
      }

    inline override final def unitOf: Id[Unit] = ()

    inline override final def errorOf[A](throwable: Throwable): Id[A] =
      throw throwable // scalafix:ok DisableSyntax.throw

    inline override final def fromEither[A](either: Either[Throwable, A]): Id[A] = either.fold(errorOf(_), pureOf(_))

    inline override final def fromOption[A](option: Option[A])(orElse: => Throwable): Id[A] =
      option.fold(errorOf(orElse))(pureOf(_))

    inline override final def fromTry[A](tryA: Try[A]): Id[A] = tryA.fold(errorOf(_), pureOf(_))

  }

}
