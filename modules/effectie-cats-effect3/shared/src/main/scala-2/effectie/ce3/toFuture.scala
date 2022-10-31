package effectie.ce3

import cats.effect._
import effectie.core.ToFuture

import scala.concurrent.Future

/** @author Kevin Lee
  * @since 2020-09-23
  */
object toFuture {

  implicit def ioToFuture(implicit runtime: unsafe.IORuntime): ToFuture[IO] = new ToFuture[IO] {

    override def unsafeToFuture[A](fa: IO[A]): Future[A] = fa.unsafeToFuture()
  }

}
