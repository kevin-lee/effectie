package effectie.monix

import cats.Id
import cats.effect.IO
import cats.syntax.all._
import monix.eval.Task

/** @author Kevin Lee
  * @since 2020-06-07
  */
object CanCatch {
  type CanCatch[F[_]] = effectie.CanCatch[F]

  implicit object CanCatchTask extends CanCatch[Task] {

    @inline override final def mapFa[A, B](fa: Task[A])(f: A => B): Task[B] = fa.map(f)

    override def catchNonFatalThrowable[A](fa: => Task[A]): Task[Either[Throwable, A]] =
      fa.attempt

  }

  implicit object CanCatchIo extends CanCatch[IO] {

    @inline override final def mapFa[A, B](fa: IO[A])(f: A => B): IO[B] = fa.map(f)

    override def catchNonFatalThrowable[A](fa: => IO[A]): IO[Either[Throwable, A]] =
      fa.attempt

  }

  implicit object CanCatchId extends CanCatch[Id] {

    @inline override final def mapFa[A, B](fa: Id[A])(f: A => B): Id[B] = f(fa)

    override def catchNonFatalThrowable[A](fa: => Id[A]): Id[Either[Throwable, A]] =
      scala.util.Try(fa) match {
        case scala.util.Success(a) =>
          a.asRight[Throwable]

        case scala.util.Failure(scala.util.control.NonFatal(ex)) =>
          ex.asLeft[A]

        case scala.util.Failure(ex) =>
          throw ex
      }

  }

}
