package effectie.resource

import cats.effect.{MonadCancelThrow, Resource, Sync}

/** @author Kevin Lee
  * @since 2022-11-06
  */
trait Ce3Resource[F[*], A] extends ReleasableResource[F, A]
object Ce3Resource {

  def fromAutoCloseable[F[*]: Sync, A <: AutoCloseable](acquire: F[A]): Ce3Resource[F, A] =
    new Ce3ResourceF[F, A](Resource.fromAutoCloseable(acquire))

  def make[F[*]: Sync, A](acquire: F[A])(release: A => F[Unit]): Ce3Resource[F, A] =
    new Ce3ResourceF[F, A](Resource.make(acquire)(release))

  def apply[F[*]: MonadCancelThrow, A](underlying: Resource[F, A]): ReleasableResource[F, A] =
    new Ce3ResourceF[F, A](underlying)

  def pure[F[*]: MonadCancelThrow, A](acquire: A): ReleasableResource[F, A] =
    new Ce3ResourceF(Resource.pure(acquire))

  private final class Ce3ResourceF[F[*]: MonadCancelThrow, A](val underlying: Resource[F, A])
      extends Ce3Resource[F, A] {

    override def use[B](f: A => F[B]): F[B] = underlying.use(f)

    override def map[B](f: A => B): ReleasableResource[F, B] = new Ce3ResourceF(underlying.map(f))

    override def flatMap[B](f: A => ReleasableResource[F, B]): ReleasableResource[F, B] =
      new BindCe3ResourceF(this, f)
  }

  private final class BindCe3ResourceF[F[*]: MonadCancelThrow, A, B](
    val resource: ReleasableResource[F, A],
    nextF: A => ReleasableResource[F, B],
  ) extends Ce3Resource[F, B] {
    override def use[C](f: B => F[C]): F[C] =
      resource.use { a =>
        nextF(a).use(f)
      }

    override def map[C](f: B => C): ReleasableResource[F, C] = flatMap(b => pure(f(b)))

    override def flatMap[C](f: B => ReleasableResource[F, C]): ReleasableResource[F, C] =
      new BindCe3ResourceF[F, B, C](this, f)
  }

}
