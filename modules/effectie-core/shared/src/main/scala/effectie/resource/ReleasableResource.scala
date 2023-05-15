package effectie.resource

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/** @author Kevin Lee
  * @since 2022-10-30
  */
trait ReleasableResource[F[*], A] {

  def use[B](f: A => F[B]): F[B]

  def map[B](f: A => B): ReleasableResource[F, B]

  def flatMap[B](f: A => ReleasableResource[F, B]): ReleasableResource[F, B]

}

object ReleasableResource {
  def usingResource[A <: AutoCloseable](acquire: => A): ReleasableResource[Try, A] =
    UsingResource(acquire)

  def usingResourceFromTry[A <: AutoCloseable](resource: Try[A]): ReleasableResource[Try, A] =
    UsingResource.fromTry(resource)

  def futureResource[A <: AutoCloseable](acquire: Future[A])(
    implicit ec: ExecutionContext
  ): ReleasableResource[Future, A] = ReleasableFutureResource(acquire)

  def makeTry[A](resource: => Try[A])(release: A => Try[Unit]): ReleasableResource[Try, A] =
    UsingResource.make(resource)(release(_))

  def pureTry[A](resource: A): ReleasableResource[Try, A] =
    UsingResource.pure(resource)

  def makeFuture[A](acquire: Future[A])(release: A => Future[Unit])(
    implicit ec: ExecutionContext
  ): ReleasableResource[Future, A] = ReleasableFutureResource.make(acquire)(release)

  def pureFuture[A](acquire: A)(
    implicit ec: ExecutionContext
  ): ReleasableResource[Future, A] = ReleasableFutureResource.pure(acquire)

}
