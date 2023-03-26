package effectie.resource

import cats.effect.Sync
import cats.effect.kernel.MonadCancelThrow

/** @author Kevin Lee
  * @since 2022-11-06
  */
object Ce3ResourceMaker {

  @deprecated(message = "Please use withResource instead", since = "2.0.0-beta10")
  def forAutoCloseable[F[*]: Sync: MonadCancelThrow]: ResourceMaker[F] = withResource

  def withResource[F[*]: Sync: MonadCancelThrow]: ResourceMaker[F] = new Ce3ResourceMaker[F]

  private final class Ce3ResourceMaker[F[*]: Sync: MonadCancelThrow] extends ResourceMaker[F] {

    override def forAutoCloseable[A <: AutoCloseable](fa: F[A]): ReleasableResource[F, A] =
      Ce3Resource.fromAutoCloseable[F, A](fa)

    override def make[A](fa: => F[A])(release: A => F[Unit]): ReleasableResource[F, A] =
      Ce3Resource.make[F, A](fa)(release)
  }

}
