package effectie.resource

import scala.util.{Try, Using}

/** @author Kevin Lee
  * @since 2022-10-30
  */
private[resource] final class UsingResource[A <: AutoCloseable] private (acquire: Try[A]) extends ReleasableResource[Try, A] {

  private def underlying[B](f: A => Try[B]): Try[B] =
    acquire.flatMap(Using(_)(f)).flatten

  override def use[B](f: A => Try[B]): Try[B] = underlying(f)
}

private[resource] object UsingResource {

  def apply[A <: AutoCloseable](resource: => A): UsingResource[A] = fromTry(Try(resource))

  def fromTry[A <: AutoCloseable](resource: Try[A]): UsingResource[A] = new UsingResource[A](resource)
}
