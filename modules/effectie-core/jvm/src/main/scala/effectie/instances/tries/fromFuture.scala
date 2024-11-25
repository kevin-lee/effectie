package effectie.instances.tries

import effectie.core.FromFuture
import effectie.core.FromFuture.FromFutureToTryTimeout

import scala.concurrent.{Await, Future}
import scala.util.Try

/** @author Kevin Lee
  * @since 2023-03-17
  */
object fromFuture {

  implicit def fromFutureToTry(implicit timeout: FromFutureToTryTimeout): FromFuture[Try] =
    new FromFuture[Try] {
      override def toEffect[A](future: => Future[A]): Try[A] =
        Try(Await.result[A](future, timeout.fromFutureToTryTimeout))
    }

}
