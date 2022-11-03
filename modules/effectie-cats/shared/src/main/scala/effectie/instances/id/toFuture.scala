package effectie.instances.id

import cats.Id
import effectie.core.ToFuture

import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2020-09-23
  */
object toFuture {

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def idToFuture(implicit executionContext: ExecutionContext): ToFuture[Id] =
    new ToFuture[Id] {
      override def unsafeToFuture[A](fa: Id[A]): Future[A] =
        Future(fa)
    }

}
