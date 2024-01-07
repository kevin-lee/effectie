package effectie.time

import scala.concurrent.duration.FiniteDuration

/** @author Kevin Lee
  * @since 2024-01-07
  */
trait syntax {
  implicit def FiniteDurationExtraOps(finiteDuration: FiniteDuration): syntax.FiniteDurationExtraOps =
    new syntax.FiniteDurationExtraOps(finiteDuration)
}
object syntax extends syntax {
  final class FiniteDurationExtraOps(private val finiteDuration: FiniteDuration) extends AnyVal {
    def +-(tolerance: FiniteDuration): ApproxFiniteDuration =
      ApproxFiniteDuration(finiteDuration, tolerance)

    def isWithIn(approxFiniteDuration: ApproxFiniteDuration): Boolean =
      finiteDuration >= approxFiniteDuration.min && finiteDuration <= approxFiniteDuration.max
  }

}
