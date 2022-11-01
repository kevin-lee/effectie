package effectie.ce3

import cats.effect.IO
import effectie.core.FromFuture
import effectie.core.FromFuture.FromFutureToIdTimeout

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/** @author Kevin Lee
  * @since 2020-09-22
  */
object fromFuture {

  given fromFutureToIo: FromFuture[IO] with {
    override def toEffect[A](future: => Future[A]): IO[A] = IO.fromFuture[A](IO(future))
  }

}
