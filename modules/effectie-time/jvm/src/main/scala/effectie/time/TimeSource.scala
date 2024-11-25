package effectie.time

import cats._
import cats.syntax.all._
import effectie.core._
import effectie.syntax.all._
import effectie.time.TimeSource.TimeSpent

import java.time.Instant
import scala.concurrent.duration.{FiniteDuration, MILLISECONDS, NANOSECONDS, SECONDS, TimeUnit}

/** @author Kevin Lee
  */
trait TimeSource[F[*]] {

  implicit protected def M: Monad[F]

  def name: String

  def currentTime(): F[Instant]

  def realTimeTo(unit: TimeUnit): F[FiniteDuration]

  def monotonicTo(unit: TimeUnit): F[FiniteDuration]

  def realTime: F[FiniteDuration]

  def monotonic: F[FiniteDuration]

  def timeSpent[A](fa: => F[A]): F[(A, TimeSpent)] =
    for {
      start <- monotonic
      a     <- fa
      end   <- monotonic
    } yield (a, TimeSpent(end - start))

  override def toString: String = s"TimeSource(name=$name)"
}

object TimeSource {

  def apply[F[*]: TimeSource]: TimeSource[F] = implicitly[TimeSource[F]]

  implicit def showTimeSource[F[*]]: Show[TimeSource[F]] = Show.fromToString

  def withSources[F[*]: Fx: Monad](
    timeSourceName: String,
    realTimeSource: F[Instant],
    monotonicSource: F[Long],
  ): TimeSource[F] =
    new DefaultTimeSource[F](timeSourceName, realTimeSource, monotonicSource)

  private class DefaultTimeSource[F[_]: Fx](
    timeSourceName: String,
    realTimeSource: F[Instant],
    monotonicSource: F[Long],
  )(implicit protected val M: Monad[F])
      extends TimeSource[F] {

    override val name: String = timeSourceName

    override val toString: String = s"DefaultTimeSource(name=$name)"

    override def currentTime(): F[Instant] = realTimeSource

    override def realTimeTo(unit: TimeUnit): F[FiniteDuration] =
      for {
        now  <- currentTime()
        real <- effectOf(unit.convert(now.getEpochSecond, SECONDS) + unit.convert(now.getNano.toLong, NANOSECONDS))
      } yield FiniteDuration(real, unit)

    override def monotonicTo(unit: TimeUnit): F[FiniteDuration] =
      monotonicSource.map(nano => FiniteDuration(unit.convert(nano, NANOSECONDS), unit))

    override def realTime: F[FiniteDuration] = realTimeTo(MILLISECONDS)

    override def monotonic: F[FiniteDuration] = monotonicTo(NANOSECONDS)
  }

  /** The default behaviours of TimeSource depend on Instant and System.
    * For realTime, it uses Instant.now() to get the epoch seconds and nano seconds.
    * For monotonic, it uses System.nanoTime() to get the nano seconds.
    *
    * NOTE: To get the current time (Instant.now()), you should use realTime not monotonic.
    */
  def default[F[*]: Fx: Monad](timeSourceName: String): TimeSource[F] =
    withSources(
      timeSourceName,
      effectOf(Instant.now()),
      effectOf(System.nanoTime()),
    )

  final case class TimeSpent(timeSpent: FiniteDuration) extends AnyVal
}
