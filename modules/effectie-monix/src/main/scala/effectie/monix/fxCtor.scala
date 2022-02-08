package effectie.monix

import cats.Id
import cats.effect.IO
import effectie.core.FxCtor
import monix.eval.Task

/** @author Kevin Lee
  * @since 2021-05-16
  */
object fxCtor {

  implicit object taskFxCtor extends FxCtor[Task] {

    @inline override final def effectOf[A](a: => A): Task[A] = Task(a)

    @inline override final def pureOf[A](a: A): Task[A] = Task.now(a)

    @inline override final val unitOf: Task[Unit] = Task.unit

    @inline override final def errorOf[A](throwable: Throwable): Task[A] = Task.raiseError(throwable)

  }

  implicit object ioFxCtor extends FxCtor[IO] {

    @inline override final def effectOf[A](a: => A): IO[A] = IO(a)

    @inline override final def pureOf[A](a: A): IO[A] = IO.pure(a)

    @inline override final val unitOf: IO[Unit] = IO.unit

    @inline override final def errorOf[A](throwable: Throwable): IO[A] = IO.raiseError(throwable)

  }

  implicit object idFxCtor extends FxCtor[Id] {

    @inline override final def effectOf[A](a: => A): Id[A] = a

    @inline override final def pureOf[A](a: A): Id[A] = a

    @inline override final val unitOf: Id[Unit] = ()

    @inline override final def errorOf[A](throwable: Throwable): Id[A] = throw throwable

  }

}
