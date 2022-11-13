package effectie.resource

import effectie.resource.ReleasableResource.UnusedHandleError

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success, Try}

/** @author Kevin Lee
  * @since 2022-11-12
  */
final class ReleasableFutureResource[A <: AutoCloseable](resource: Future[A])(implicit ec: ExecutionContext)
    extends ReleasableResource[Future, A, UnusedHandleError] {
  override def use[B](f: A => Future[B])(implicit unusedHandleError: UnusedHandleError[Future]): Future[B] = {
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

object ReleasableFutureResource {
  def apply[A <: AutoCloseable](acquire: Future[A])(implicit ec: ExecutionContext): ReleasableFutureResource[A] =
    new ReleasableFutureResource(acquire)
}
