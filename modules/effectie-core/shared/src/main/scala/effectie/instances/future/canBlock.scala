package effectie.instances.future

import effectie.core.CanBlock

import scala.concurrent.{ExecutionContext, Future, blocking}

/** @author Kevin Lee
  * @since 2023-01-01
  */
object canBlock {
  implicit def canBlockFuture(implicit EC: ExecutionContext): CanBlock[Future] = new CanBlockFuture

  private final class CanBlockFuture(implicit EC: ExecutionContext) extends CanBlock[Future] {
    override def blockingOf[A](a: => A): Future[A] = Future(blocking(a))
  }
}
