package effectie.resource

import cats.effect.{MonadCancelThrow, Resource, Sync}

/** @author Kevin Lee
  * @since 2022-11-06
  */
trait Ce3Resource[F[*], A] extends ReleasableResource[F, A] {
  def underlying: Resource[F, A]
}
object Ce3Resource {

  def fromAutoCloseable[F[*]: Sync, A <: AutoCloseable](acquire: F[A]): Ce3Resource[F, A] =
    new Ce3ResourceF[F, A](acquire)(a => Sync[F].delay(a.close()))

  def make[F[*]: Sync, A](acquire: F[A])(release: A => F[Unit]): Ce3Resource[F, A] =
    new Ce3ResourceF[F, A](acquire)(release)

  private final class Ce3ResourceF[F[*]: Sync: MonadCancelThrow, A](acquire: F[A])(release: A => F[Unit])
      extends Ce3Resource[F, A] {

    override val underlying: Resource[F, A] = Resource.make[F, A](acquire)(release)

    override def use[B](f: A => F[B]): F[B] = underlying.use(f)
  }

}
