package effectie.instances.tries

import effectie.core.ToFuture

import scala.concurrent.Future
import scala.util.Try

/** @author Kevin Lee
  * @since 2023-03-17
  */
object toFuture {

  implicit val futureToTry: ToFuture[Try] = new ToFuture[Try] {

    @inline override def unsafeToFuture[A](fa: Try[A]): Future[A] =
      Future.fromTry(fa)
  }

}
