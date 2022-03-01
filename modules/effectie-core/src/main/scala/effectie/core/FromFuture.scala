package effectie.core

import scala.concurrent.Future
import scala.concurrent.duration.Duration

/** @author Kevin Lee
  * @since 2020-09-22
  */
trait FromFuture[F[*]] {
  def toEffect[A](future: => Future[A]): F[A]
}

object FromFuture {

  def apply[F[*]: FromFuture]: FromFuture[F] = implicitly[FromFuture[F]]

  implicit val fromFutureToFuture: FromFuture[Future] =
    new FromFuture[Future] {
      override def toEffect[A](future: => Future[A]): Future[A] =
        future
    }

  final case class FromFutureToIdTimeout(fromFutureToIdTimeout: Duration)

}
