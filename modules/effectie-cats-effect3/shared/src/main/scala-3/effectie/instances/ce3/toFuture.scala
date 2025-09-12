package effectie.instances.ce3

import cats.effect.*
import effectie.core.ToFuture

import scala.concurrent.Future

/** @author Kevin Lee
  * @since 2020-09-23
  */
object toFuture {

  given ioToFuture(using unsafe.IORuntime): ToFuture[IO] with {
    override def unsafeToFuture[A](fa: IO[A]): Future[A] = fa.unsafeToFuture()
  }

}
