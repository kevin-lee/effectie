package effectie.instances.ce2.compat

import cats.effect._
import effectie.testing.FutureTools

/** @author Kevin Lee
  * @since 2021-04-18
  */
final class CatsEffectIoCompatForFuture extends FutureTools {
  implicit val cs: ContextShift[IO] = IO.contextShift(globalExecutionContext)

}
