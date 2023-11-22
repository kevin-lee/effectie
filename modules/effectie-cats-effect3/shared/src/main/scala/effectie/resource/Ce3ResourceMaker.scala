package effectie.resource

import cats.Monad
import cats.effect.Sync
import cats.effect.kernel.MonadCancelThrow

/** @author Kevin Lee
  * @since 2022-11-06
  */
object Ce3ResourceMaker {

  @deprecated(message = "Please use Ce3ResourceMaker.maker instead", since = "2.0.0-beta10")
  def forAutoCloseable[F[*]: Sync: MonadCancelThrow]: ResourceMaker[F] = maker

  def maker[F[*]: Sync]: ResourceMaker[F] = new Ce3ResourceMaker[F]

  private final class Ce3ResourceMaker[F[*]: Sync] extends ResourceMaker[F] {

    override def forAutoCloseable[A <: AutoCloseable](fa: F[A]): ReleasableResource[F, A] =
      Ce3Resource.fromAutoCloseable[F, A](fa)

    override def make[A](fa: => F[A])(release: A => F[Unit]): ReleasableResource[F, A] =
      Ce3Resource.make[F, A](fa)(release)

    override def pure[A](a: A): ReleasableResource[F, A] = make(Monad[F].pure(a))(_ => Monad[F].unit)

    override def eval[A](fa: F[A]): ReleasableResource[F, A] = make(fa)(_ => Monad[F].unit)
  }

}
