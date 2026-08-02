package effectie.instances.ce3

import cats.effect.IO
import effectie.resource.{Ce3UseResource, UseResource}

/** @author Kevin Lee
  * @since 2026-08-02
  */
object resource {

  implicit val ioUseResource: UseResource[IO] = Ce3UseResource.useResource[IO]

}
