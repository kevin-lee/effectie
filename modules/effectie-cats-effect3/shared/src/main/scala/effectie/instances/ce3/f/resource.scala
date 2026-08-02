package effectie.instances.ce3.f

import cats.effect.kernel.MonadCancelThrow
import effectie.resource.{Ce3UseResource, UseResource}

/** @author Kevin Lee
  * @since 2026-08-02
  */
object resource {

  implicit def monadCancelThrowUseResource[F[*]: MonadCancelThrow]: UseResource[F] =
    Ce3UseResource.useResource[F]

}
