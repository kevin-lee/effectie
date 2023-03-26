package effectie.resource

import cats.effect.{BracketThrow, Resource, Sync}

/** @author Kevin Lee
  * @since 2022-11-06
  */
trait Ce2Resource[F[*], A] extends ReleasableResource[F, A] {
  def underlying: Resource[F, A]
}
object Ce2Resource {
  def fromAutoCloseable[F[*]: Sync: BracketThrow, A <: AutoCloseable](acquire: F[A]): Ce2Resource[F, A] =
    new Ce2ResourceF[F, A](acquire)(a => Sync[F].delay(a.close()))

  def make[F[*]: Sync: BracketThrow, A](acquire: F[A])(release: A => F[Unit]): Ce2Resource[F, A] =
    new Ce2ResourceF[F, A](acquire)(release)

  private final class Ce2ResourceF[F[*]: Sync: BracketThrow, A](acquire: F[A])(release: A => F[Unit])
      extends Ce2Resource[F, A] {

    override val underlying: Resource[F, A] = Resource.make[F, A](acquire)(release)

    override def use[B](f: A => F[B]): F[B] = underlying.use(f)
  }

}
