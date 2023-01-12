package effectie.instances.monix3

import cats.effect.{Blocker, ContextShift}
import effectie.core.CanBlock
import monix.eval.Task

/** @author Kevin Lee
  * @since 2023-01-01
  */
object canBlock {
  implicit def canBlockIo(implicit blocker: Blocker, contextShift: ContextShift[Task]): CanBlock[Task] =
    new CanBlockF(blocker)

  private final class CanBlockF(blocker: Blocker)(implicit contextShift: ContextShift[Task]) extends CanBlock[Task] {
    override def blockingOf[A](a: => A): Task[A] =
      blocker.delay[Task, A](a)
  }

}
