package effectie.instances.ce2

import cats.Monad
import cats.effect.Clock
import cats.syntax.all._
import effectie.time.TimeSource

import java.time.Instant
import scala.concurrent.duration.{FiniteDuration, MILLISECONDS, NANOSECONDS, TimeUnit}

/** @author Kevin Lee
  * @since 2024-01-09
  */
trait ClockBasedTimeSource[F[*]] extends TimeSource[F] {

  def clock: Clock[F]

  override val name: String = "ce2.ClockBasedTimeSource"

  override def currentTime(): F[Instant] = clock.instantNow

  override def realTimeTo(unit: TimeUnit): F[FiniteDuration] =
    clock.realTime(unit).map(FiniteDuration(_, unit))

  override def monotonicTo(unit: TimeUnit): F[FiniteDuration] =
    clock.monotonic(unit).map(FiniteDuration(_, unit))

  override def realTime: F[FiniteDuration] = realTimeTo(MILLISECONDS)

  override def monotonic: F[FiniteDuration] = monotonicTo(NANOSECONDS)

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
