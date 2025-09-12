package effectie.instances.ce2

import cats.effect.IO
import effectie.core.{CanCatch, FxCtor}

/** @author Kevin Lee
  * @since 2020-06-07
  */
object canCatch {

  given canCatchIo: CanCatch[IO] with {

    override implicit protected val fxCtor: FxCtor[IO] = effectie.instances.ce2.fxCtor.ioFxCtor

    inline override final def catchNonFatalThrowable[A](fa: => IO[A]): IO[Either[Throwable, A]] =
      fa.attempt
  }

}
