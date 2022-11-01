package effectie.monix3

import cats.effect.IO
import effectie.core.ToFuture
import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.Future

/** @author Kevin Lee
  * @since 2020-09-23
  */
object toFuture {

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def taskToFuture(implicit scheduler: Scheduler): ToFuture[Task] = new ToFuture[Task] {

    override def unsafeToFuture[A](fa: Task[A]): Future[A] =
      fa.runToFuture
  }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit object ioToFuture extends ToFuture[IO] {

    override def unsafeToFuture[A](fa: IO[A]): Future[A] =
      fa.unsafeToFuture()
  }

}
