package effectie.instances.ce2

import cats.effect.IO
import effectie.core.{CanCatch, FxCtor}

/** @author Kevin Lee
  * @since 2020-06-07
  */
object canCatch {

  implicit object canCatchIo extends CanCatch[IO] {

    override implicit protected val fxCtor: FxCtor[IO] = effectie.instances.ce2.fxCtor.ioFxCtor

    @inline override final def flatMapFa[A, B](fa: IO[A])(f: A => IO[B]): IO[B] = fa.flatMap(f)

    @inline override final def catchNonFatalThrowable[A](fa: => IO[A]): IO[Either[Throwable, A]] =
      fa.attempt
  }

}
