package effectie.resource

import cats.effect.kernel.MonadCancelThrow
import cats.effect.{Resource, Sync}

/** @author Kevin Lee
  * @since 2022-11-06
  */
object Ce3Resource {

  @deprecated(
    message = "Use ReleasableResource.fromAutoCloseable(acquire) instead " +
      "(FxCtor[F] is available via import effectie.instances.ce3.f.fxCtor._). " +
      "To run .use(...), import effectie.instances.ce3.resource._ or effectie.instances.ce3.f.resource._ for UseResource[F] (for Scala 3, use .given instead of ._).",
    since = "2.5.0",
  )
  def fromAutoCloseable[F[*]: Sync, A <: AutoCloseable](acquire: F[A]): ReleasableResource[F, A] =
    ReleasableResource.fromAutoCloseable(acquire)(effectie.instances.ce3.f.fxCtor.syncFxCtor)

  @deprecated(
    message = "Use ReleasableResource.make(acquire)(release) instead. " +
      "To run .use(...), import effectie.instances.ce3.resource._ or effectie.instances.ce3.f.resource._ for UseResource[F] (for Scala 3, use .given instead of ._).",
    since = "2.5.0",
  )
  def make[F[*]: Sync, A](acquire: F[A])(release: A => F[Unit]): ReleasableResource[F, A] =
    ReleasableResource.make(acquire)(release)

  @deprecated(
    message = "Use Ce3UseResource.fromCatsEffectResource(underlying) instead. " +
      "To run .use(...), import effectie.instances.ce3.resource._ or effectie.instances.ce3.f.resource._ for UseResource[F] (for Scala 3, use .given instead of ._).",
    since = "2.5.0",
  )
  def apply[F[*]: MonadCancelThrow, A](underlying: Resource[F, A]): ReleasableResource[F, A] =
    Ce3UseResource.fromCatsEffectResource(underlying)

  @deprecated(
    message = "Use ReleasableResource.pure(acquire) instead. " +
      "To run .use(...), import effectie.instances.ce3.resource._ or effectie.instances.ce3.f.resource._ for UseResource[F] (for Scala 3, use .given instead of ._).",
    since = "2.5.0",
  )
  def pure[F[*]: MonadCancelThrow, A](acquire: A): ReleasableResource[F, A] =
    ReleasableResource.pure(acquire)

}
