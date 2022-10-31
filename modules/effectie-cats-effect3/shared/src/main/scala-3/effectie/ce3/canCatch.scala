package effectie.ce3

import cats.data.EitherT
import cats.effect.IO
import cats.syntax.all.*
import effectie.core.CanCatch

import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2020-06-07
  */
object canCatch {

  given canCatchIo: CanCatch[IO] with {

    inline override final def mapFa[A, B](fa: IO[A])(f: A => B): IO[B] = fa.map(f)

    inline override def catchNonFatalThrowable[A](fa: => IO[A]): IO[Either[Throwable, A]] =
      fa.attempt

  }

}
