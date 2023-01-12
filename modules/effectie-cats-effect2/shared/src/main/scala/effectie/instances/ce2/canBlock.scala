package effectie.instances.ce2

import cats.effect.{Blocker, ContextShift, IO}
import effectie.core.CanBlock

/** @author Kevin Lee
  * @since 2023-01-01
  */
trait canBlock {
  implicit def canBlockIo(implicit blocker: Blocker, contextShift: ContextShift[IO]): CanBlock[IO] =
    new CanBlockF(blocker)

  private final class CanBlockF(blocker: Blocker)(implicit contextShift: ContextShift[IO]) extends CanBlock[IO] {
    override def blockingOf[A](a: => A): IO[A] =
      blocker.delay[IO, A](a)
  }
}
object canBlock extends canBlock
