package effectie.resource

import cats.effect.{BracketThrow, Resource, Sync}

/** @author Kevin Lee
  * @since 2022-11-06
  */
object Ce2Resource {

  @deprecated(
    message = "Use ReleasableResource.fromAutoCloseable(acquire) instead " +
      "(FxCtor[F] is available via import effectie.instances.ce2.f.fxCtor._). " +
      "To run .use(...), import effectie.instances.ce2.resource._ or effectie.instances.ce2.f.resource._ for UseResource[F] (for Scala 3, use .given instead of ._).",
    since = "2.5.0",
  )
  def fromAutoCloseable[F[*]: Sync, A <: AutoCloseable](acquire: F[A]): ReleasableResource[F, A] =
    ReleasableResource.fromAutoCloseable(acquire)(effectie.instances.ce2.f.fxCtor.syncFxCtor)

  @deprecated(
    message = "Use ReleasableResource.make(acquire)(release) instead. " +
      "To run .use(...), import effectie.instances.ce2.resource._ or effectie.instances.ce2.f.resource._ for UseResource[F] (for Scala 3, use .given instead of ._).",
    since = "2.5.0",
  )
  def make[F[*]: BracketThrow, A](acquire: F[A])(release: A => F[Unit]): ReleasableResource[F, A] =
    ReleasableResource.make(acquire)(release)

  @deprecated(
    message = "Use Ce2UseResource.fromCatsEffectResource(underlying) instead. " +
      "To run .use(...), import effectie.instances.ce2.resource._ or effectie.instances.ce2.f.resource._ for UseResource[F] (for Scala 3, use .given instead of ._).",
    since = "2.5.0",
  )
  def apply[F[*]: BracketThrow, A](underlying: Resource[F, A]): ReleasableResource[F, A] =
    Ce2UseResource.fromCatsEffectResource(underlying)

  @deprecated(
    message = "Use ReleasableResource.pure(acquire) instead. " +
      "To run .use(...), import effectie.instances.ce2.resource._ or effectie.instances.ce2.f.resource._ for UseResource[F] (for Scala 3, use .given instead of ._).",
    since = "2.5.0",
  )
  def pure[F[*]: BracketThrow, A](acquire: A): ReleasableResource[F, A] =
    ReleasableResource.pure(acquire)

}
