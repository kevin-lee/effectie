package effectie.resource

import scala.util.Using.Releasable
import scala.util.{Try, Using}

/** @author Kevin Lee
  * @since 2022-10-30
  */
private[resource] trait UsingResource[A] extends ReleasableResource[Try, A]
private[resource] object UsingResource {
  private[resource] final case class AllocatedUsingResource[A](acquire: Try[A])(release: Releasable[A])
      extends UsingResource[A] {

    private def underlying[B](f: A => Try[B]): Try[B] =
      acquire.flatMap(Using.resource(_)(f)(release))

    override def use[B](f: A => Try[B]): Try[B] = underlying(f)
  }

  def apply[A <: AutoCloseable](resource: => A): ReleasableResource[Try, A] = fromTry(Try(resource))

  def pure[A](resource: A): ReleasableResource[Try, A] = AllocatedUsingResource[A](Try(resource))(_ => ())

  def fromTry[A <: AutoCloseable](resource: Try[A]): ReleasableResource[Try, A] =
    UsingResource.make(resource)(a => Try(a.close()))

  def make[A](resource: => Try[A])(release: A => Try[Unit]): ReleasableResource[Try, A] =
    AllocatedUsingResource[A](resource)(release(_).getOrElse(()))
}
