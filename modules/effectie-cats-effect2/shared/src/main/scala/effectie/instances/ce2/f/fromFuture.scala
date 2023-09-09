package effectie.instances.ce2.f

import cats.effect.{Async, ContextShift}
import effectie.core.FromFuture

import scala.concurrent.Future

/** @author Kevin Lee
  * @since 2020-09-22
  */
object fromFuture {

  implicit def fromFutureToAsync[F[*]: Async: ContextShift]: FromFuture[F] =
    new FromFuture[F] {
      @inline override def toEffect[A](future: => Future[A]): F[A] =
        Async.fromFuture(Async[F].delay(future))
    }

}
