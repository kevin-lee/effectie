package effectie.resource

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/** @author Kevin Lee
  * @since 2022-10-30
  */
trait ReleasableResource[F[*], A] {

  def use[B](f: A => F[B]): F[B]
}

object ReleasableResource {
  def usingResource[A <: AutoCloseable](acquire: => A): ReleasableResource[Try, A] =
    UsingResource(acquire)

  def usingResourceFromTry[A <: AutoCloseable](resource: Try[A]): ReleasableResource[Try, A] =
    UsingResource.fromTry(resource)

  def futureResource[A <: AutoCloseable](acquire: Future[A])(
    implicit ec: ExecutionContext
  ): ReleasableResource[Future, A] = ReleasableFutureResource(acquire)

}
