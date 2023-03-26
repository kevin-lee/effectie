package effectie.resource

import scala.util.Using.Releasable
import scala.util.{Try, Using}

/** @author Kevin Lee
  * @since 2022-10-30
  */
private[resource] final class UsingResource[A] private (acquire: Try[A])(release: Releasable[A])
    extends ReleasableResource[Try, A] {

  private def underlying[B](f: A => Try[B]): Try[B] =
    acquire.flatMap(Using.resource(_)(f)(release))

  override def use[B](f: A => Try[B]): Try[B] = underlying(f)
}

private[resource] object UsingResource {

  def apply[A <: AutoCloseable](resource: => A): ReleasableResource[Try, A] = fromTry(Try(resource))

  def fromTry[A <: AutoCloseable](resource: Try[A]): ReleasableResource[Try, A] =
    UsingResource.make(resource)(a => Try(a.close()))

  def make[A](resource: => Try[A])(release: A => Try[Unit]): ReleasableResource[Try, A] =
    new UsingResource[A](resource)(release(_).getOrElse(()))
}
