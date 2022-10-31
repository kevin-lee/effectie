package effectie.core

import scala.annotation.implicitNotFound

/** @author Kevin Lee
  * @since 2021-11-03
  */
@implicitNotFound(
  """
  Could not find an implicit Fx[${F}]. You can probably find it from the effectie.instance package.
  ---
  If you want to use IO from cats-effect 2, try effectie-cats-effect2.
    import effectie.instances.ce2.fx._
    // for Scala 3
    import effectie.instances.ce2.fx.*

  For cats-effect 3, try effectie-cats-effect3.
    import effectie.instances.ce3.fx._
    // for Scala 3
    import effectie.instances.ce3.fx.*

  If you want to use Task from Monix 3, try effectie-monix3.
    import effectie.instances.monix3.fx._
    // for Scala 3
    import effectie.instances.monix3.fx.*

  For Scala's Future, It is just
    import effectie.instances.future.fx._
    // for Scala 3
    import effectie.instances.future.fx.*

  If you don't want to use any effect but the raw data, you can use the instance for cats.Id
    import effectie.instances.id.fx._
    // for Scala 3
    import effectie.instances.id.fx.*
  ---
  """
)
trait Fx[F[*]] extends FxCtor[F] with CanCatch[F] with CanHandleError[F] with CanRecover[F]

object Fx {

  def apply[F[*]: Fx]: Fx[F] = implicitly[Fx[F]]

}
