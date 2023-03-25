package effectie.core

import scala.annotation.implicitNotFound
import scala.concurrent.Future
import scala.concurrent.duration.Duration

/** @author Kevin Lee
  * @since 2020-09-22
  */
@implicitNotFound(
  """
  Could not find an implicit FromFuture[${F}]. You can probably find it from the effectie.instances package.
  ---
  If you want to use IO from cats-effect 2, try effectie-cats-effect2.
    import effectie.instances.ce2.fromFuture._
    // for Scala 3
    import effectie.instances.ce2.fromFuture.given

  For cats-effect 3, try effectie-cats-effect3.
    import effectie.instances.ce3.fromFuture._
    // for Scala 3
    import effectie.instances.ce3.fromFuture.given

  If you want to use Task from Monix 3, try effectie-monix3.
    import effectie.instances.monix3.fromFuture._
    // for Scala 3
    import effectie.instances.monix3.fromFuture.given

  For Scala's Future, It is just
    import effectie.instances.future.fromFuture._
    // for Scala 3
    import effectie.instances.future.fromFuture.given

  If you don't want to use any effect but the raw data, you can use the instance for cats.Id
    import effectie.instances.id.fromFuture._
    // for Scala 3
    import effectie.instances.id.fromFuture.given
  ---
  """
)
trait FromFuture[F[*]] {
  def toEffect[A](future: => Future[A]): F[A]
}

object FromFuture {

  def apply[F[*]: FromFuture]: FromFuture[F] = implicitly[FromFuture[F]]

  final case class FromFutureToIdTimeout(fromFutureToIdTimeout: Duration) extends AnyVal

  final case class FromFutureToTryTimeout(fromFutureToTryTimeout: Duration) extends AnyVal
}
