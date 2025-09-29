package effectie.instances.monix3.compat

import cats.effect.ContextShift
import effectie.testing.FutureTools
import monix.eval.Task

/** @author Kevin Lee
  * @since 2021-04-18
  */
final class CatsEffectIoCompatForFuture extends FutureTools {
  implicit val cs: ContextShift[Task] = Task.contextShift

}
