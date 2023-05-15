package effectie.resource

import cats.effect.{BracketThrow, Resource, Sync}

/** @author Kevin Lee
  * @since 2022-11-06
  */
trait Ce2Resource[F[*], A] extends ReleasableResource[F, A]
object Ce2Resource {
  def fromAutoCloseable[F[*]: Sync, A <: AutoCloseable](acquire: F[A]): ReleasableResource[F, A] =
    new Ce2ResourceF[F, A](Resource.fromAutoCloseable(acquire))

  def make[F[*]: BracketThrow, A](acquire: F[A])(release: A => F[Unit]): ReleasableResource[F, A] =
    new Ce2ResourceF[F, A](Resource.make(acquire)(release))

  def apply[F[*]: BracketThrow, A](underlying: Resource[F, A]): ReleasableResource[F, A] =
    new Ce2ResourceF(underlying)

  def pure[F[*]: BracketThrow, A](acquire: A): ReleasableResource[F, A] =
    new Ce2ResourceF(Resource.pure(acquire))

  private final class Ce2ResourceF[F[*]: BracketThrow, A](val underlying: Resource[F, A]) extends Ce2Resource[F, A] {

    override def use[B](f: A => F[B]): F[B] = underlying.use(f)

    override def map[B](f: A => B): ReleasableResource[F, B] = new Ce2ResourceF(underlying.map(f))

    override def flatMap[B](f: A => ReleasableResource[F, B]): ReleasableResource[F, B] =
      new BindCe2ResourceF[F, A, B](this, f)
  }

  private final class BindCe2ResourceF[F[*]: BracketThrow, A, B](
    val resource: ReleasableResource[F, A],
    nextF: A => ReleasableResource[F, B],
  ) extends Ce2Resource[F, B] {
    override def use[C](f: B => F[C]): F[C] =
      resource.use { a =>
        nextF(a).use(f)
      }

    override def map[C](f: B => C): ReleasableResource[F, C] = flatMap(b => pure(f(b)))

    override def flatMap[C](f: B => ReleasableResource[F, C]): ReleasableResource[F, C] =
      new BindCe2ResourceF[F, B, C](this, f)
  }
}
