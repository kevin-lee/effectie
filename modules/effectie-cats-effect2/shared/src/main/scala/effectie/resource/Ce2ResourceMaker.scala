package effectie.resource

import cats.effect.{BracketThrow, Sync}

/** @author Kevin Lee
  * @since 2022-11-06
  */
object Ce2ResourceMaker {

  @deprecated(message = "Please use withResource instead", since = "2.0.0-beta10")
  def forAutoCloseable[F[*]: Sync: BracketThrow]: ResourceMaker[F] = maker

  def maker[F[*]: Sync: BracketThrow]: ResourceMaker[F] = new Ce2ResourceMaker[F]

  private final class Ce2ResourceMaker[F[*]: Sync: BracketThrow] extends ResourceMaker[F] {

    override def forAutoCloseable[A <: AutoCloseable](fa: F[A]): ReleasableResource[F, A] =
      Ce2Resource.fromAutoCloseable[F, A](fa)

    override def make[A](fa: => F[A])(release: A => F[Unit]): ReleasableResource[F, A] =
      Ce2Resource.make[F, A](fa)(release)
  }

}
