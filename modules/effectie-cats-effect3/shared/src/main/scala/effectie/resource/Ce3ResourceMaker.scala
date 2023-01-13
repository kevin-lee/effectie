package effectie.resource

import cats.effect.Sync
import cats.effect.kernel.MonadCancelThrow

/** @author Kevin Lee
  * @since 2022-11-06
  */
object Ce3ResourceMaker {
  def forAutoCloseable[F[*]: Sync: MonadCancelThrow]: ResourceMaker[F] = new Ce3ResourceMaker[F]

  private final class Ce3ResourceMaker[F[*]: Sync: MonadCancelThrow] extends ResourceMaker[F] {

    override def forAutoCloseable[A <: AutoCloseable](fa: F[A]): ReleasableResource[F, A] =
      Ce3Resource.fromAutoCloseable[F, A](fa)

  }

}
