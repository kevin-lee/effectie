package effectie.instances.ce3

import cats.effect.IO
import effectie.core.CanBlock

/** @author Kevin Lee
  * @since 2023-01-01
  */
object canBlock {
  implicit object canBlockIo extends CanBlock[IO] {
    override def blockingOf[A](a: => A): IO[A] =
      IO.blocking(a)
  }
}
