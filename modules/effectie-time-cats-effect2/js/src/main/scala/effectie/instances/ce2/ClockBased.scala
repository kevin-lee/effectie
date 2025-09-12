package effectie.instances.ce2

import cats.Functor
import cats.effect.Clock

import java.time.Instant
import scala.concurrent.duration.NANOSECONDS

/** @author Kevin Lee
  * @since 2025-09-11
  */
trait ClockBased {
  implicit def jvmClockOps[F[*]](clock: Clock[F]): ClockBased.JvmClockOps[F] = new ClockBased.JvmClockOps(clock)
}
object ClockBased {
  class JvmClockOps[F[*]](private val clock: Clock[F]) extends AnyVal {

    /** Creates a `java.time.Instant` derived from the clock's `realTime` in milliseconds
      * for any `F` that has `Functor` defined.
      */
    def instantNow(implicit F: Functor[F]): F[Instant] =
      F.map(clock.realTime(NANOSECONDS)) { ns =>
        Instant.EPOCH.plusNanos(ns)
      }
  }

}
