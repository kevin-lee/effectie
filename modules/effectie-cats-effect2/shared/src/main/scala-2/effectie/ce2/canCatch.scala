package effectie.ce2

import cats.Id
import cats.effect.IO
import cats.syntax.all._
import effectie.core.CanCatch

/** @author Kevin Lee
  * @since 2020-06-07
  */
object canCatch {

  implicit object canCatchIo extends CanCatch[IO] {

    @inline override final def mapFa[A, B](fa: IO[A])(f: A => B): IO[B] = fa.map(f)

    @inline override def catchNonFatalThrowable[A](fa: => IO[A]): IO[Either[Throwable, A]] =
      fa.attempt

  }

  implicit object canCatchId extends CanCatch[Id] {

    @inline override final def mapFa[A, B](fa: Id[A])(f: A => B): Id[B] = f(fa)

    @inline override def catchNonFatalThrowable[A](fa: => Id[A]): Id[Either[Throwable, A]] =
      scala.util.Try(fa) match {
        case scala.util.Success(a) =>
          a.asRight[Throwable]

        case scala.util.Failure(scala.util.control.NonFatal(ex)) =>
          ex.asLeft[A]

        case scala.util.Failure(ex) =>
          throw ex // scalafix:ok DisableSyntax.throw
      }

  }

}
