package effectie.core

import scala.annotation.implicitNotFound
import scala.concurrent.Future

/** @author Kevin Lee
  * @since 2020-09-23
  */
@implicitNotFound(
  """
  Could not find an implicit ToFuture[${F}]. You can probably find it from the effectie.instance package.
  ---
  If you want to use IO from cats-effect 2, try effectie-cats-effect2.
    import effectie.instances.ce2.toFuture._
    // for Scala 3
    import effectie.instances.ce2.toFuture.given

  For cats-effect 3, try effectie-cats-effect3.
    import effectie.instances.ce3.toFuture._
    // for Scala 3
    import effectie.instances.ce3.toFuture.given

  If you want to use Task from Monix 3, try effectie-monix3.
    import effectie.instances.monix3.toFuture._
    // for Scala 3
    import effectie.instances.monix3.toFuture.given

  For Scala's Future, It is just
    import effectie.instances.future.toFuture._
    // for Scala 3
    import effectie.instances.future.toFuture.given

  If you don't want to use any effect but the raw data, you can use the instance for cats.Id
    import effectie.instances.id.toFuture._
    // for Scala 3
    import effectie.instances.id.toFuture.given
  ---
  """
)
trait ToFuture[F[*]] {

  def unsafeToFuture[A](fa: F[A]): Future[A]

}

object ToFuture {

  def apply[F[*]: ToFuture]: ToFuture[F] = implicitly[ToFuture[F]]

}
