package effectie.instances.id

import cats.Id
import effectie.core.CanBlock

/** @author Kevin Lee
  * @since 2023-01-01
  */
object canBlock {
  implicit object canBlock extends CanBlock[Id] {
    override def blockingOf[A](a: => A): Id[A] = a
  }
}
