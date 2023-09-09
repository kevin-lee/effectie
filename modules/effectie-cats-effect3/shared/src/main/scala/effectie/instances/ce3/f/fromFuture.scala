package effectie.instances.ce3.f

import cats.effect.Async
import effectie.core.FromFuture

import scala.concurrent.Future

/** @author Kevin Lee
  * @since 2020-09-22
  */
object fromFuture {

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def fromFutureToAsync[F[*]: Async]: FromFuture[F] =
    new FromFuture[F] {
      override def toEffect[A](future: => Future[A]): F[A] =
        Async[F].fromFuture[A](Async[F].delay(future))
    }

}
