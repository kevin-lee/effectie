package effectie.instances.future

import effectie.core.FromFuture

import scala.concurrent.Future

/** @author Kevin Lee
  * @since 2021-11-03
  */
object fromFuture {

  implicit val fromFutureToFuture: FromFuture[Future] =
    new FromFuture[Future] {
      override def toEffect[A](future: => Future[A]): Future[A] =
        future
    }

}
