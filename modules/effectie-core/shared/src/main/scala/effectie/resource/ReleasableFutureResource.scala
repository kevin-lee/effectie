package effectie.resource

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success, Try}

/** @author Kevin Lee
  * @since 2022-11-12
  */
private[resource] final class ReleasableFutureResource[A <: AutoCloseable] private (resource: Future[A])(
  implicit ec: ExecutionContext
) extends ReleasableResource[Future, A] {
  override def use[B](f: A => Future[B]): Future[B] = {
    val promise = Promise[B]()
    val future  = promise.future
    resource.flatMap { a =>
      f(a).onComplete {
        case Success(r) =>
          Try(a.close())
          promise.success(r)
        case Failure(exception) =>
          Try(a.close())
          promise.failure(exception)
      }
      future
    }
  }
}

private[resource] object ReleasableFutureResource {
  def apply[A <: AutoCloseable](acquire: Future[A])(implicit ec: ExecutionContext): ReleasableFutureResource[A] =
    new ReleasableFutureResource(acquire)
}
