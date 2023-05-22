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

    override def map[B](f: A => B): ReleasableResource[Try, B] =
      BindUsingResource(this, (a: A) => UsingResource.pure(f(a)))

    override def flatMap[B](f: A => ReleasableResource[Try, B]): ReleasableResource[Try, B] =
      BindUsingResource[A, B](this, f)
  }

  private[resource] final case class BindUsingResource[A, B](
    source: ReleasableResource[Try, A],
    nextF: A => ReleasableResource[Try, B],
  ) extends UsingResource[B] {
    override def use[C](f: B => Try[C]): Try[C] =
      source.use { a =>
        nextF(a).use(f)
      }

    override def map[C](f: B => C): ReleasableResource[Try, C] =
      new BindUsingResource[B, C](this, b => UsingResource.pure(f(b)))

    override def flatMap[C](f: B => ReleasableResource[Try, C]): ReleasableResource[Try, C] =
      new BindUsingResource[B, C](this, f)
  }

  def apply[A <: AutoCloseable](resource: => A): ReleasableResource[Try, A] = fromTry(Try(resource))

  def pure[A](resource: A): ReleasableResource[Try, A] = AllocatedUsingResource[A](Try(resource))(_ => ())

  def fromTry[A <: AutoCloseable](resource: Try[A]): ReleasableResource[Try, A] =
    UsingResource.make(resource)(a => Try(a.close()))

  def make[A](resource: => Try[A])(release: A => Try[Unit]): ReleasableResource[Try, A] =
    AllocatedUsingResource[A](resource)(release(_).getOrElse(()))
}
