package effectie.time

import effectie.time.TimeSource.TimeSpent

import scala.concurrent.duration.FiniteDuration

/** @author Kevin Lee
  * @since 2024-01-07
  */
trait syntax {
  implicit def FiniteDurationExtraOps(finiteDuration: FiniteDuration): syntax.FiniteDurationExtraOps =
    new syntax.FiniteDurationExtraOps(finiteDuration)

  implicit def fAWithTimeOps[F[*], A](fa: F[A]): syntax.FAWithTimeOps[F[*], A] = new syntax.FAWithTimeOps[F[*], A](fa)

}
object syntax extends syntax {
  final class FiniteDurationExtraOps(private val finiteDuration: FiniteDuration) extends AnyVal {
    def +-(tolerance: FiniteDuration): ApproxFiniteDuration =
      ApproxFiniteDuration(finiteDuration, tolerance)

    def isWithIn(approxFiniteDuration: ApproxFiniteDuration): Boolean =
      finiteDuration >= approxFiniteDuration.min && finiteDuration <= approxFiniteDuration.max
  }

  final class FAWithTimeOps[F[*], A](private val fa: F[A]) extends AnyVal {
    def withTimeSpent(implicit timeSource: TimeSource[F]): F[(A, TimeSpent)] = timeSource.timeSpent(fa)
  }

}
