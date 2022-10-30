package effectie.core

import scala.annotation.implicitNotFound
import scala.concurrent.Future

/** @author Kevin Lee
  * @since 2020-09-22
  */
@implicitNotFound(
  """
  Could not find an implicit FromFuture[${F}]. You can probably find it from the effectie.instance package.
  ---
  If you want to use IO from cats-effect 2, try effectie-cats-effect2.
    import effectie.instances.ce2.fromFuture._

  For cats-effect 3, try effectie-cats-effect3.
    import effectie.instances.ce3.fromFuture._

  If you want to use Task from Monix 3, try effectie-monix3.
    import effectie.instances.monix3.fromFuture._

  For Scala's Future, It is just
    import effectie.instances.future.fromFuture._

  If you don't want to use any effect but the raw data, you can use the instance for cats.Id
    import effectie.instances.id.fromFuture._
  ---
  """
)
trait FromFuture[F[*]] {
  def toEffect[A](future: => Future[A]): F[A]
}

object FromFuture {

  def apply[F[*]: FromFuture]: FromFuture[F] = implicitly[FromFuture[F]]

}
