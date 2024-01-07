package effectie.time

import cats.{Eq, Show}

import scala.concurrent.duration.FiniteDuration

/** @author Kevin Lee
  * @since 2024-01-07
  */
final case class ApproxFiniteDuration(base: FiniteDuration, tolerance: FiniteDuration)
object ApproxFiniteDuration {
  implicit val approxFiniteDurationEq: Eq[ApproxFiniteDuration] = Eq.fromUniversalEquals

  implicit val approxFiniteDurationShow: Show[ApproxFiniteDuration] = {
    case ApproxFiniteDuration(base, tolerance) =>
      s"(${(base.minus(tolerance)).toString} to ${(base.plus(tolerance)).toString})"

  }

  implicit final class ApproxFiniteDurationOps(private val approxFiniteDuration: ApproxFiniteDuration) extends AnyVal {
    def min: FiniteDuration = approxFiniteDuration.base.minus(approxFiniteDuration.tolerance)
    def max: FiniteDuration = approxFiniteDuration.base.plus(approxFiniteDuration.tolerance)
  }
}
