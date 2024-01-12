package effectie.instances.ce3

import cats.Monad
import cats.effect.Clock
import cats.syntax.all._
import effectie.time.TimeSource

import java.time.Instant
import scala.concurrent.duration.{FiniteDuration, TimeUnit}

/** @author Kevin Lee
  * @since 2024-01-09
  */
trait ClockBasedTimeSource[F[*]] extends TimeSource[F] {

  def clock: Clock[F]

  override val name: String = "ce3.ClockBasedTimeSource"

  override def currentTime(): F[Instant] = clock.realTimeInstant

  override def realTimeTo(unit: TimeUnit): F[FiniteDuration] =
    realTime.map(time => FiniteDuration(time.toUnit(unit).toLong, unit))

  override def monotonicTo(unit: TimeUnit): F[FiniteDuration] =
    monotonic.map(time => FiniteDuration(time.toUnit(unit).toLong, unit))

  override def realTime: F[FiniteDuration] = clock.realTime

  override def monotonic: F[FiniteDuration] = clock.monotonic

  override val toString: String = name
}
object ClockBasedTimeSource {
  def apply[F[*]](implicit clock: Clock[F], monad: Monad[F]): ClockBasedTimeSource[F] =
    new ClockBasedTimeSourceF[F](clock)(monad)

  private final class ClockBasedTimeSourceF[F[*]](
    override val clock: Clock[F]
  )(
    override implicit protected val M: Monad[F]
  ) extends ClockBasedTimeSource[F]
}
