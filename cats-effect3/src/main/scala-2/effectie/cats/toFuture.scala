package effectie.cats

import cats.Id
import cats.effect._
import effectie.core.ToFuture

import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2020-09-23
  */
object toFuture {

  implicit def ioToFuture(implicit runtime: unsafe.IORuntime): ToFuture[IO] = new ToFuture[IO] {

    override def unsafeToFuture[A](fa: IO[A]): Future[A] = fa.unsafeToFuture()
  }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def idToFuture(implicit executionContext: ExecutionContext): ToFuture[Id] =
    new ToFuture[Id] {
      override def unsafeToFuture[A](fa: Id[A]): Future[A] = Future(fa)
    }

}
