package effectie.cats

import cats.Id
import cats.effect.{ContextShift, IO}
import effectie.core.FromFuture
import effectie.core.FromFuture.FromFutureToIdTimeout

import scala.concurrent.{Await, Future}

/** @author Kevin Lee
  * @since 2020-09-22
  */
object fromFuture {

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def fromFutureToIo(implicit cs: ContextShift[IO]): FromFuture[IO] =
    new FromFuture[IO] {
      override def toEffect[A](future: => Future[A]): IO[A] =
        IO.fromFuture[A](IO(future))
    }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def fromFutureToId(implicit timeout: FromFutureToIdTimeout): FromFuture[Id] =
    new FromFuture[Id] {
      override def toEffect[A](future: => Future[A]): Id[A] =
        Await.result[A](future, timeout.fromFutureToIdTimeout)
    }

}
