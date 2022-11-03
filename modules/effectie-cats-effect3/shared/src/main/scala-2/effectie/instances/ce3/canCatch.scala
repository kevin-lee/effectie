package effectie.instances.ce3

import cats.effect.IO
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

}
