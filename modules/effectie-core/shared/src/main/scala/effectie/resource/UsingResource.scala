package effectie.resource

import scala.util.{Try, Using}

/** @author Kevin Lee
  * @since 2022-10-30
  */
final class UsingResource[A <: AutoCloseable](acquire: Try[A])
    extends ReleasableResource[Try, A, ReleasableResource.UnusedHandleError] {

  private def underlying[B](f: A => Try[B]): Try[B] =
    acquire.flatMap(Using(_)(f)).flatten

  override def use[B](f: A => Try[B])(implicit handleError: ReleasableResource.UnusedHandleError[Try]): Try[B] =
    underlying(f)
}

object UsingResource {

  def apply[A <: AutoCloseable](resource: => A): UsingResource[A] = new UsingResource[A](Try(resource))

  def fromTry[A <: AutoCloseable](resource: Try[A]): UsingResource[A] = new UsingResource[A](resource)
}
