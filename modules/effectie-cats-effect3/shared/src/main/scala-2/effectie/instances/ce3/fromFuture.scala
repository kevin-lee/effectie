package effectie.instances.ce3

import cats.effect.IO
import effectie.core.FromFuture

import scala.concurrent.Future

/** @author Kevin Lee
  * @since 2020-09-22
  */
object fromFuture {

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def fromFutureToIo: FromFuture[IO] =
    new FromFuture[IO] {
      override def toEffect[A](future: => Future[A]): IO[A] =
        IO.fromFuture[A](IO(future))
    }

}
