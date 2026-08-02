package effectie.resource

import cats.effect.Sync
import cats.effect.kernel.MonadCancelThrow

/** @author Kevin Lee
  * @since 2022-11-06
  */
object Ce3ResourceMaker {

  @deprecated(message = "Please use Ce3ResourceMaker.maker instead", since = "2.0.0-beta10")
  def forAutoCloseable[F[*]: Sync: MonadCancelThrow]: ResourceMaker[F] = maker

  @deprecated(
    message = "ResourceMaker is deprecated. Construct ReleasableResource directly instead " +
      "(ReleasableResource.make/eval/pure/fromAutoCloseable, with FxCtor[F] via import effectie.instances.ce3.f.fxCtor._). " +
      "To run .use(...), import effectie.instances.ce3.resource._ or effectie.instances.ce3.f.resource._ for UseResource[F] (for Scala 3, use .given instead of ._).",
    since = "2.5.0",
  )
  def maker[F[*]: Sync]: ResourceMaker[F] = new Ce3ResourceMaker[F]

  @deprecated(message = "Use ReleasableResource directly instead of ResourceMaker.", since = "2.5.0")
  private final class Ce3ResourceMaker[F[*]: Sync] extends ResourceMaker[F] {

    override def forAutoCloseable[A <: AutoCloseable](fa: F[A]): ReleasableResource[F, A] =
      ReleasableResource.fromAutoCloseable(fa)(effectie.instances.ce3.f.fxCtor.syncFxCtor)

    override def make[A](fa: => F[A])(release: A => F[Unit]): ReleasableResource[F, A] =
      ReleasableResource.make(fa)(release)

    override def pure[A](a: A): ReleasableResource[F, A] = ReleasableResource.pure(a)

    override def eval[A](fa: F[A]): ReleasableResource[F, A] = ReleasableResource.eval(fa)
  }

}
