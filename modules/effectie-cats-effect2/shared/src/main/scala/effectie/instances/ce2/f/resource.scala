package effectie.instances.ce2.f

import cats.effect.BracketThrow
import effectie.resource.{Ce2UseResource, UseResource}

/** @author Kevin Lee
  * @since 2026-08-02
  */
object resource {

  implicit def bracketThrowUseResource[F[*]: BracketThrow]: UseResource[F] =
    Ce2UseResource.useResource[F]

}
