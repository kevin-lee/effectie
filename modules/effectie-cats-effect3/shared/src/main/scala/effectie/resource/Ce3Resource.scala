package effectie.resource

import cats.effect.kernel.MonadCancelThrow
import cats.effect.{Resource, Sync}

/** @author Kevin Lee
  * @since 2022-11-06
  */
trait Ce3Resource[F[*], A] extends ReleasableResource[F, A, MonadCancelThrow] {
  def underlying: Resource[F, A]
}
object Ce3Resource {

  def fromAutoCloseable[F[*]: Sync, A <: AutoCloseable](acquire: F[A]): Ce3Resource[F, A] =
    new Ce3ResourceF[F, A](acquire)

  private final class Ce3ResourceF[F[*]: Sync, A <: AutoCloseable](acquire: F[A]) extends Ce3Resource[F, A] {

    override val underlying: Resource[F, A] = Resource.fromAutoCloseable[F, A](acquire)

    override def use[B](f: A => F[B])(implicit handleError: MonadCancelThrow[F]): F[B] =
      underlying.use(f)(handleError)
  }

}
