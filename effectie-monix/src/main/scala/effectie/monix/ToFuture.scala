package effectie.monix

import cats.Id
import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2020-09-23
  */
trait ToFuture[F[_]] {

  def unsafeToFuture[A](fa: F[A]): Future[A]

}

object ToFuture {

  def apply[F[_]: ToFuture]: ToFuture[F] = implicitly[ToFuture[F]]

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def ioToFuture(implicit scheduler: Scheduler): ToFuture[Task] = new ToFuture[Task] {

    override def unsafeToFuture[A](fa: Task[A]): Future[A] =
      fa.runToFuture
  }

  implicit val futureToFuture: ToFuture[Future] = new ToFuture[Future] {

    override def unsafeToFuture[A](fa: Future[A]): Future[A] =
      fa
  }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def idToFuture(implicit executionContext: ExecutionContext): ToFuture[Id] =
    new ToFuture[Id] {
      override def unsafeToFuture[A](fa: Id[A]): Future[A] =
        Future(fa)
    }

}
