package effectie.instances.future

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

/** @author Kevin Lee
  * @since 2023-03-25
  */
private[future] trait Compat {

  private final val futureOfUnit: Future[Unit] = Future.fromTry(Success(())) // scalafix:ok DisableSyntax.noFinalVal

  @inline final def newFuture[T](body: => T)(implicit executor: ExecutionContext): Future[T] =
    futureOfUnit.map(_ => body)

  @inline final def delegateFuture[T](body: => Future[T])(implicit executor: ExecutionContext): Future[T] =
    futureOfUnit.flatMap(_ => body)
}
