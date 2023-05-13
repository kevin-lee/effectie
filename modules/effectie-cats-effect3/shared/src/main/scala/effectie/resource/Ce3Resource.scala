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
  }

}
