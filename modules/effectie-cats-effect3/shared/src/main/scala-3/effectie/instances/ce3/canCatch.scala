package effectie.instances.ce3

import cats.data.EitherT
import cats.effect.IO
import cats.syntax.all.*
import effectie.core.{CanCatch, FxCtor}

import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2020-06-07
  */
object canCatch {

  given canCatchIo: CanCatch[IO] with {

    override implicit protected val fxCtor: FxCtor[IO] = effectie.instances.ce3.fxCtor.ioFxCtor

    inline override final def catchNonFatalThrowable[A](fa: => IO[A]): IO[Either[Throwable, A]] =
      fa.attempt

  }

}
