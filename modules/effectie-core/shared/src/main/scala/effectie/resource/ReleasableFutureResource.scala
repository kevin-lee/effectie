package effectie.resource

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success}

/** @author Kevin Lee
  * @since 2022-11-12
  */
private[resource] final class ReleasableFutureResource[A] private (resource: Future[A])(release: A => Future[Unit])(
  implicit ec: ExecutionContext
) extends ReleasableResource[Future, A] {
  override def use[B](f: A => Future[B]): Future[B] = {
    val promise = Promise[B]()
    val future  = promise.future
    resource.flatMap { a =>
      f(a).onComplete {
        case Success(r) =>
          release(a).onComplete {
            case Success(_) =>
              promise.success(r)
            case Failure(err) =>
              println(
                s"ReleasableFutureResource> Operation on the resource succeeded but closing resource failed with the error: ${err.getMessage}\n" +
                  "You can probably ignore this message."
              )
              promise.success(r)
          }
        case Failure(exception) =>
          release(a).onComplete {
            case Success(_) =>
              promise.failure(exception)
            case Failure(err) =>
              println(
                s"ReleasableFutureResource> Operation on the resource failed and closing resource failed with the error: ${err.getMessage}\n" +
                  "You can probably ignore this message."
              )
              promise.failure(exception)
          }
      }
      future
    }
  }

}

private[resource] object ReleasableFutureResource {
  def apply[A <: AutoCloseable](acquire: Future[A])(implicit ec: ExecutionContext): ReleasableFutureResource[A] =
    new ReleasableFutureResource(acquire)(a => Future(a.close()))

  def make[A](acquire: Future[A])(release: A => Future[Unit])(
    implicit ec: ExecutionContext
  ): ReleasableFutureResource[A] =
    new ReleasableFutureResource(acquire)(release)
}
