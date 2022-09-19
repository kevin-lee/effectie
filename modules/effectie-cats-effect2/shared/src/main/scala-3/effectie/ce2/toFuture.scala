package effectie.ce2

import cats.Id
import cats.effect.*
import effectie.core.ToFuture

import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2020-09-23
  */
object toFuture {

  given ioToFuture: ToFuture[IO] with {

    override def unsafeToFuture[A](fa: IO[A]): Future[A] =
      fa.unsafeToFuture()
  }

  given idToFuture(using executionContext: ExecutionContext): ToFuture[Id] with {
    override def unsafeToFuture[A](fa: Id[A]): Future[A] =
      Future(fa)
  }

}
