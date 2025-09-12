package effectie.instances.ce3

import cats.effect.Clock

import java.time.Instant

/** @author Kevin Lee
 * @since 2025-09-11
 */
trait ClockBased {
  implicit class JvmClockOps[F[*]](private val self: Clock[F]) {

    def realTimeInstant: F[Instant] = {
      self.applicative.map(self.realTime)(d => Instant.ofEpochMilli(d.toMillis))
    }
  }
}
