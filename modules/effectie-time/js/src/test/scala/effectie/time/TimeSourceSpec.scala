package effectie.time

import cats.effect._
import cats.syntax.all._
import effectie.syntax.all._
import effectie.testing.{FutureTools, RandomGens}
import effectie.time.syntax._
import munit.Assertions

import java.time.Instant
import scala.concurrent._
import scala.concurrent.duration._

class TimeSourceSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  override val munitTimeout: FiniteDuration = 200.milliseconds

  import effectie.instances.ce2.fx.ioFx

  type F[A] = IO[A]
  val F = IO

  implicit val timer: Timer[F] = F.timer(globalExecutionContext)

  test("test Show[TimeSource]") {
    val name     = "test TimeSource"
    val expected = s"DefaultTimeSource(name=$name)"
    val actual   = TimeSource.default[F](name).show
    Assertions.assertEquals(actual, expected)
  }

  test("example test TimeSource[F].realTime and TimeSource[F].monotonic") {
    val epochSeconds = 123456789L
    val nanoSeconds  = 123456789L
    val fullNanos    = epochSeconds * 1000000000L + nanoSeconds

    val expectedMillis = fullNanos / 1000000L
    val expectedNanos  = fullNanos

    implicit val sourcedTimeSource: TimeSource[F] = TimeSource.withSources[F](
      "Test",
      pureOf(Instant.ofEpochSecond(epochSeconds, nanoSeconds)),
      pureOf(fullNanos),
    )

    val timeSource = TimeSource[F]

    import scala.concurrent.duration._
    Future.sequence(
      List(
        timeSource
          .realTime
          .map(time => Assertions.assertEquals(time, expectedMillis.milliseconds, "timeSource.realTime"))
          .unsafeToFuture(),
        timeSource
          .realTimeTo(MILLISECONDS)
          .map(time =>
            Assertions.assertEquals(time, expectedMillis.milliseconds, "timeSource.realTimeTo(MILLISECONDS)")
          )
          .unsafeToFuture(),
        timeSource
          .realTimeTo(NANOSECONDS)
          .map(time => Assertions.assertEquals(time, expectedNanos.nanoseconds, "timeSource.realTimeTo(NANOSECONDS)"))
          .unsafeToFuture(),
        timeSource
          .monotonic
          .map(time => Assertions.assertEquals(time, expectedNanos.nanoseconds, "timeSource.monotonic"))
          .unsafeToFuture(),
        timeSource
          .monotonicTo(MILLISECONDS)
          .map(time =>
            Assertions.assertEquals(time, expectedMillis.milliseconds, "timeSource.monotonicTo(MILLISECONDS)")
          )
          .unsafeToFuture(),
        timeSource
          .monotonicTo(NANOSECONDS)
          .map(time => Assertions.assertEquals(time, expectedNanos.nanoseconds, "timeSource.monotonicTo(NANOSECONDS)"))
          .unsafeToFuture(),
      )
    )
  }

  test("property test TimeSource[F].realTime and TimeSource[F].monotonic") {
    val epochSeconds = RandomGens.genRandomLongWithMinMax(1L, 999999999L)
    val nanoSeconds  = RandomGens.genRandomLongWithMinMax(1L, 999999999L)
    val fullNanos    = RandomGens.genRandomLongWithMinMax(
      epochSeconds * 1000000000L + nanoSeconds,
      epochSeconds * 1000000000L + nanoSeconds,
    )

    val expectedMillis = fullNanos / 1000000L
    val expectedNanos  = fullNanos

    val timeSource = TimeSource.withSources[F](
      "Test",
      pureOf(Instant.ofEpochSecond(epochSeconds, nanoSeconds)),
      pureOf(fullNanos),
    )

    import scala.concurrent.duration._
    Future.sequence(
      List(
        timeSource
          .realTime
          .map(time => Assertions.assertEquals(time, expectedMillis.milliseconds, "timeSource.realTime"))
          .unsafeToFuture(),
        timeSource
          .realTimeTo(MILLISECONDS)
          .map(time =>
            Assertions.assertEquals(time, expectedMillis.milliseconds, "timeSource.realTimeTo(MILLISECONDS)")
          )
          .unsafeToFuture(),
        timeSource
          .realTimeTo(NANOSECONDS)
          .map(time => Assertions.assertEquals(time, expectedNanos.nanoseconds, "timeSource.realTimeTo(NANOSECONDS)"))
          .unsafeToFuture(),
        timeSource
          .monotonic
          .map(time => Assertions.assertEquals(time, expectedNanos.nanoseconds, "timeSource.monotonic"))
          .unsafeToFuture(),
        timeSource
          .monotonicTo(MILLISECONDS)
          .map(time =>
            Assertions.assertEquals(time, expectedMillis.milliseconds, "timeSource.monotonicTo(MILLISECONDS)")
          )
          .unsafeToFuture(),
        timeSource
          .monotonicTo(NANOSECONDS)
          .map(time => Assertions.assertEquals(time, expectedNanos.nanoseconds, "timeSource.monotonicTo(NANOSECONDS)"))
          .unsafeToFuture(),
      )
    )
  }

  test("test default TimeSource - realTime") {

    val now            = Instant.now()
    val expectedNanos  = now.getEpochSecond * 1000000000L + now.getNano
    val expectedMillis = expectedNanos / 1000000L

    val timeSource = TimeSource.default[F](
      "Test"
    )

    Future.sequence(
      List(
        timeSource
          .realTime
          .map(time =>
            Assertions
              .assert(time.isWithIn(expectedMillis.milliseconds +- 1000.milliseconds), "timeSource.realTime")
          )
          .unsafeToFuture(),
        timeSource
          .realTimeTo(MILLISECONDS)
          .map(time =>
            Assertions.assert(
              time.isWithIn(expectedMillis.milliseconds +- 1000.milliseconds),
              "timeSource.realTimeTo(MILLISECONDS)",
            )
          )
          .unsafeToFuture(),
        timeSource
          .realTimeTo(NANOSECONDS)
          .map(time =>
            Assertions.assert(
              time.isWithIn(expectedNanos.nanoseconds +- 1000.milliseconds),
              "timeSource.realTimeTo(NANOSECONDS)",
            )
          )
          .unsafeToFuture(),
      )
    )
  }

  test("test default TimeSource - monotonic") {

    val expectedNanos  = System.nanoTime()
    val expectedMillis = expectedNanos / 1000000L

    val timeSource = TimeSource.default[F](
      "Test"
    )

    Future.sequence(
      List(
        timeSource
          .monotonic
          .map(time =>
            Assertions
              .assert(time.isWithIn(expectedNanos.nanoseconds +- 1000.milliseconds), "timeSource.monotonic"),
          )
          .unsafeToFuture(),
        timeSource
          .monotonicTo(MILLISECONDS)
          .map(time =>
            Assertions.assert(
              time.isWithIn(expectedMillis.milliseconds +- 1000.milliseconds),
              "timeSource.monotonicTo(MILLISECONDS)",
            ),
          )
          .unsafeToFuture(),
        timeSource
          .monotonicTo(NANOSECONDS)
          .map(time =>
            Assertions.assert(
              time.isWithIn(expectedNanos.nanoseconds +- 1000.milliseconds),
              "timeSource.monotonicTo(NANOSECONDS)",
            ),
          )
          .unsafeToFuture(),
      )
    )
  }

  test("test default TimeSource - timeSpent") {
    val waitFor = RandomGens.genRandomIntWithMinMax(200, 700).milliseconds
    val diff    = 180.milliseconds

    val timeSource = TimeSource.default[F]("Test")

    (for {
      _                  <- F.sleep(500.milliseconds) // warm up
      resultAndTimeSpent <- timeSource.timeSpent {
                              F.sleep(waitFor) *>
                                pureOf("Done")
                            }
      (result, timeSpent) = resultAndTimeSpent
      _ <- effectOf(
             println(
               show"""    >>> ===============================
                     |    >>> TimeSource.default[F].timeSpent
                     |    >>> -------------------------------
                     |    >>>   waitFor: $waitFor
                     |    >>> timeSpent: ${timeSpent.timeSpent.toMillis} milliseconds
                     |    >>>      diff: ${(timeSpent.timeSpent - waitFor).toMillis} milliseconds
                     |    >>>
                     |""".stripMargin
             )
           )
    } yield {

      Assertions.assertEquals(result, "Done")

      Assertions.assert(
        timeSpent.timeSpent.isWithIn(waitFor +- diff),
        show"""  timeSpent (${timeSpent.timeSpent.toMillis} milliseconds) should be
              |   within ${(waitFor - diff)} to ${(waitFor + diff)}.
              |--- diff test log ---
              |>         actual: ${timeSpent.timeSpent.toMillis} milliseconds
              |> expected range: ${(waitFor - diff)} to ${(waitFor + diff)}
              |>        waitFor: $waitFor
              |>  expected diff: +- $diff)
              |>    actual diff: ${(timeSpent.timeSpent - waitFor).toMillis.show} milliseconds
              |""".stripMargin,
      )
    }).unsafeToFuture()
  }

}
