package effectie.cats

import cats.Id
import cats.effect.{ContextShift, IO}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/** @author Kevin Lee
  * @since 2020-09-22
  */
trait FromFuture[F[*]] {
  def toEffect[A](future: => Future[A]): F[A]
}

object FromFuture {

  def apply[F[*]: FromFuture]: FromFuture[F] = summon[FromFuture[F]]

  given fromFutureToIo(using cs: ContextShift[IO]): FromFuture[IO] with {
    override def toEffect[A](future: => Future[A]): IO[A] =
      IO.fromFuture[A](IO(future))
  }

  given fromFutureToFuture: FromFuture[Future] with {
    override def toEffect[A](future: => Future[A]): Future[A] =
      future
  }

  final case class FromFutureToIdTimeout(fromFutureToIdTimeout: Duration)

  given fromFutureToId(using timeout: FromFutureToIdTimeout): FromFuture[Id] with {
    override def toEffect[A](future: => Future[A]): Id[A] =
      Await.result[A](future, timeout.fromFutureToIdTimeout)
  }

}
