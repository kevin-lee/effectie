package effectie.instances.monix3

import effectie.resource.{Ce2UseResource, UseResource}
import monix.eval.Task

/** @author Kevin Lee
  * @since 2026-08-02
  */
object resource {

  implicit val taskUseResource: UseResource[Task] = Ce2UseResource.useResource[Task]

}
