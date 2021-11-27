package effectie.cats

import cats.Id
import cats.effect.*

import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2020-09-23
  */
trait ToFuture[F[*]] {

  def unsafeToFuture[A](fa: F[A]): Future[A]

}

object ToFuture {

  def apply[F[*]: ToFuture]: ToFuture[F] = summon[ToFuture[F]]

  given ioToFuture: ToFuture[IO] with {

    override def unsafeToFuture[A](fa: IO[A]): Future[A] =
      fa.unsafeToFuture()
  }

  given futureToFuture: ToFuture[Future] with {

    override def unsafeToFuture[A](fa: Future[A]): Future[A] =
      fa
  }

  given idToFuture(using executionContext: ExecutionContext): ToFuture[Id] with {
    override def unsafeToFuture[A](fa: Id[A]): Future[A] =
      Future(fa)
  }

}
