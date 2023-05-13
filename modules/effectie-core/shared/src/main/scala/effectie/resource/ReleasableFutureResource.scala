package effectie.resource

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success}

/** @author Kevin Lee
  * @since 2022-11-12
  */
private[resource] trait ReleasableFutureResource[A] extends ReleasableResource[Future, A]
private[resource] object ReleasableFutureResource {
  private final case class AllocatedReleasableFutureResource[A](resource: Future[A])(
    release: A => Future[Unit]
  )(
    implicit ec: ExecutionContext
  ) extends ReleasableFutureResource[A] {
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

  def make[A](acquire: Future[A])(release: A => Future[Unit])(
    implicit ec: ExecutionContext
  ): ReleasableResource[Future, A] =
    AllocatedReleasableFutureResource(acquire)(release)

  def apply[A <: AutoCloseable](acquire: Future[A])(implicit ec: ExecutionContext): ReleasableResource[Future, A] =
    AllocatedReleasableFutureResource(acquire)(a => Future(a.close()))

  def pure[A](acquire: => A)(implicit ec: ExecutionContext): ReleasableResource[Future, A] =
    AllocatedReleasableFutureResource(Future.successful(acquire))((_: A) => Future.unit)
}
