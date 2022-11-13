package effectie.resource

import cats.effect.{BracketThrow, Resource, Sync}

/** @author Kevin Lee
  * @since 2022-11-06
  */
trait Ce2Resource[F[*], A] extends ReleasableResource[F, A, BracketThrow] {
  def underlying: Resource[F, A]
}
object Ce2Resource {
  def fromAutoCloseable[F[*]: Sync, A <: AutoCloseable](acquire: F[A]): Ce2Resource[F, A] =
    new Ce2ResourceF[F, A](acquire)

  private final class Ce2ResourceF[F[*]: Sync, A <: AutoCloseable](acquire: F[A]) extends Ce2Resource[F, A] {

    override val underlying: Resource[F, A] = Resource.fromAutoCloseable[F, A](acquire)

    override def use[B](f: A => F[B])(implicit handleError: BracketThrow[F]): F[B] =
      underlying.use(f)(handleError)
  }

}
