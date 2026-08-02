package effectie.instances.ce2

import cats.effect.IO
import effectie.resource.{Ce2UseResource, UseResource}

/** @author Kevin Lee
  * @since 2026-08-02
  */
object resource {

  implicit val ioUseResource: UseResource[IO] = Ce2UseResource.useResource[IO]

}
