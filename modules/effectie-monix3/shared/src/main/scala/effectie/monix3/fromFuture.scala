package effectie.monix3

import cats.effect.{ContextShift, IO}
import effectie.core.FromFuture
import monix.eval.Task

import scala.concurrent.Future

/** @author Kevin Lee
  * @since 2020-09-22
  */
object fromFuture {

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit object fromFutureToTask extends FromFuture[Task] {
    override def toEffect[A](future: => Future[A]): Task[A] =
      Task.fromFuture[A](future)
  }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def fromFutureToIo(implicit cs: ContextShift[IO]): FromFuture[IO] =
    new FromFuture[IO] {
      override def toEffect[A](future: => Future[A]): IO[A] =
        IO.fromFuture[A](IO(future))
    }

}
