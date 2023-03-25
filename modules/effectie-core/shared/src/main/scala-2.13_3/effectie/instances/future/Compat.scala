package effectie.instances.future

import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2023-03-25
  */
private[future] trait Compat {

  @inline final def newFuture[T](body: => T)(implicit executor: ExecutionContext): Future[T] =
    Future(body)

  @inline final def delegateFuture[T](body: => Future[T])(implicit executor: ExecutionContext): Future[T] =
    Future.delegate(body)
}
