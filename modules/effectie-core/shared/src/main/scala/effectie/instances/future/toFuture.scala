package effectie.instances.future

import effectie.core.ToFuture

import scala.concurrent.Future

/** @author Kevin Lee
  * @since 2021-11-03
  */
object toFuture {

  implicit val futureToFuture: ToFuture[Future] = new ToFuture[Future] {

    override def unsafeToFuture[A](fa: Future[A]): Future[A] =
      fa
  }

}
