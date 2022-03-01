package effectie.core

import scala.concurrent.Future


/** @author Kevin Lee
  * @since 2020-09-23
  */
trait ToFuture[F[*]] {

  def unsafeToFuture[A](fa: F[A]): Future[A]

}

object ToFuture {

  def apply[F[*] : ToFuture]: ToFuture[F] = implicitly[ToFuture[F]]

  implicit val futureToFuture: ToFuture[Future] = new ToFuture[Future] {

    override def unsafeToFuture[A](fa: Future[A]): Future[A] =
      fa
  }

}
