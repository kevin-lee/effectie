package effectie.monix

import cats.Id
import cats.effect.IO
import effectie.core.FxCtor
import monix.eval.Task

import scala.util.Try

/** @author Kevin Lee
  * @since 2021-05-16
  */
object fxCtor {

  implicit object taskFxCtor extends FxCtor[Task] {

    @inline override final def effectOf[A](a: => A): Task[A] = Task(a)

    @inline override final def pureOf[A](a: A): Task[A] = Task.now(a)

    @inline override val unitOf: Task[Unit] = Task.unit

    @inline override final def errorOf[A](throwable: Throwable): Task[A] = Task.raiseError(throwable)

    @inline override final def fromEither[A](either: Either[Throwable, A]): Task[A] = Task.fromEither(either)

    @inline override final def fromOption[A](option: Option[A])(orElse: => Throwable): Task[A] =
      option.fold(errorOf[A](orElse))(pureOf)

    @inline override final def fromTry[A](tryA: Try[A]): Task[A] = Task.fromTry(tryA)

  }

  implicit object ioFxCtor extends FxCtor[IO] {

    @inline override final def effectOf[A](a: => A): IO[A] = IO(a)

    @inline override final def pureOf[A](a: A): IO[A] = IO.pure(a)

    @inline override val unitOf: IO[Unit] = IO.unit

    @inline override final def errorOf[A](throwable: Throwable): IO[A] = IO.raiseError(throwable)

    @inline override final def fromEither[A](either: Either[Throwable, A]): IO[A] = IO.fromEither(either)

    @inline override final def fromOption[A](option: Option[A])(orElse: => Throwable): IO[A] =
      IO.fromOption(option)(orElse)

    @inline override final def fromTry[A](tryA: Try[A]): IO[A] = IO.fromTry(tryA)

  }

  implicit object idFxCtor extends FxCtor[Id] {

    @inline override final def effectOf[A](a: => A): Id[A] = a

    @inline override final def pureOf[A](a: A): Id[A] = a

    @inline override val unitOf: Id[Unit] = ()

    @inline override final def errorOf[A](throwable: Throwable): Id[A] =
      throw throwable // scalafix:ok DisableSyntax.throw

    @inline override final def fromEither[A](either: Either[Throwable, A]): Id[A] = either.fold(errorOf, pureOf)

    @inline override final def fromOption[A](option: Option[A])(orElse: => Throwable): Id[A] =
      option.fold(errorOf(orElse))(pureOf)

    @inline override final def fromTry[A](tryA: Try[A]): Id[A] = tryA.fold(errorOf, pureOf)

  }

}
