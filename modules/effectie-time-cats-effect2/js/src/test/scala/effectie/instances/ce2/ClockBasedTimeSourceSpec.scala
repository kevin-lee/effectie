package effectie.instances.ce2

import cats.effect.{IO, Timer}
import cats.syntax.all._
import effectie.testing.FutureTools
import effectie.time.syntax._
import munit.Assertions

import java.time.Instant
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/** @author Kevin Lee
  * @since 2024-01-09
  */
class ClockBasedTimeSourceSpec extends munit.FunSuite with ClockBased with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  override val munitTimeout: FiniteDuration = 200.milliseconds

  type F[A] = IO[A]
  val F = IO

  @SuppressWarnings(Array("org.wartremover.warts.GlobalExecutionContext"))
  implicit val timer: Timer[F] = F.timer(globalExecutionContext)

  test("ClockBasedTimeSource[F].currentTime should return the current time") {
    ClockBasedTimeSource[F]
      .currentTime()
      .flatMap(actual =>
        timer
          .clock
          .instantNow
          .map { expected =>
            Assertions.assert(
              actual.toEpochMilli.milliseconds.isWithIn(expected.toEpochMilli.milliseconds +- 500.milliseconds),
              s"actual.toEpochMilli.milliseconds=${actual.toEpochMilli.milliseconds} is not withIn expected=${expected.toEpochMilli.milliseconds +- 500.milliseconds}",
            )
          }
      )
      .unsafeToFuture()

  }

  test("ClockBasedTimeSource[F].realTime should return the current time") {

    val now                 = Instant.now()
    val expectedNanosInLong = now.getEpochSecond * 1000000000L + now.getNano
    val expectedMillis      = (expectedNanosInLong / 1000000L).milliseconds +- 1000.milliseconds

    val timeSource = ClockBasedTimeSource[F]

    timeSource
      .realTime
      .map(time =>
        Assertions.assert(
          time.isWithIn(expectedMillis),
          s"timeSource.realTime=${time.toString} is not within expected=$expectedMillis",
        ),
      )
      .unsafeToFuture()
  }

  test("ClockBasedTimeSource[F].realTimeTo(MILLISECONDS) should return the current time in milliseconds") {

    val now                 = Instant.now()
    val expectedNanosInLong = now.getEpochSecond * 1000000000L + now.getNano
    val expectedMillis      = (expectedNanosInLong / 1000000L).milliseconds +- 1000.milliseconds

    val timeSource = ClockBasedTimeSource[F]

    timeSource
      .realTimeTo(MILLISECONDS)
      .map(time =>
        Assertions.assert(
          time.isWithIn(expectedMillis),
          s"timeSource.realTimeTo(MILLISECONDS)=${time.toString} is not within expected=$expectedMillis",
        )
      )
      .unsafeToFuture()
  }

  test("ClockBasedTimeSource[F].realTimeTo(NANOSECONDS) should return the current time in nanoseconds") {

    val now                 = Instant.now()
    val expectedNanosInLong = now.getEpochSecond * 1000000000L + now.getNano
    val expectedNanos       = expectedNanosInLong.nanoseconds +- 1000.milliseconds

    val timeSource = ClockBasedTimeSource[F]

    timeSource
      .realTimeTo(NANOSECONDS)
      .map(time =>
        Assertions.assert(
          time.isWithIn(expectedNanos),
          s"timeSource.realTimeTo(NANOSECONDS)=${time.toString} is not within expected=$expectedNanos",
        )
      )
      .unsafeToFuture()
  }

  test("ClockBasedTimeSource[F].monotonic should return the System.nanoTime() in nanoseconds") {

    val expectedNanos = System.nanoTime().nanoseconds +- 1000.milliseconds

    val timeSource = ClockBasedTimeSource[F]

    timeSource
      .monotonic
      .map(time =>
        Assertions.assert(
          time.isWithIn(expectedNanos),
          "timeSource.monotonic",
        )
      )
      .unsafeToFuture()

  }

  test("ClockBasedTimeSource[F].monotonicTo(MILLISECONDS) should return the System.nanoTime() in milliseconds") {

    val expectedNanosInLong = System.nanoTime()
    val expectedMillis      = (expectedNanosInLong / 1000000L).milliseconds +- 1000.milliseconds

    val timeSource = ClockBasedTimeSource[F]

    timeSource
      .monotonicTo(MILLISECONDS)
      .map(time => Assertions.assert(time.isWithIn(expectedMillis), "timeSource.monotonicTo(MILLISECONDS)"))
      .unsafeToFuture()

  }

  test("ClockBasedTimeSource[F].monotonicTo(NANOSECONDS) should return the System.nanoTime() in nanoseconds") {

    val expectedNanos = System.nanoTime().nanoseconds +- 1000.milliseconds

    val timeSource = ClockBasedTimeSource[F]

    timeSource
      .monotonicTo(NANOSECONDS)
      .map(time => Assertions.assert(time.isWithIn(expectedNanos), "timeSource.monotonicTo(NANOSECONDS)"))
      .unsafeToFuture()

  }

  test("ClockBasedTimeSource[F].timeSpent should return the time spent") {
    val waitFor = 700.milliseconds
    val diff    = 180.milliseconds

    val timeSource = ClockBasedTimeSource[F]

    (for {
      resultAndTimeSpent <- timeSource.timeSpent {
                              F.sleep(waitFor) *>
                                F.pure("Done")
                            }
      (result, timeSpent) = resultAndTimeSpent
      _ <- F.delay(
             println(
               show"""         >>> =================================
                     |         >>> ClockBasedTimeSource[F].timeSpent
                     |         >>> ---------------------------------
                     |         >>>   waitFor: $waitFor
                     |         >>> timeSpent: ${timeSpent.timeSpent.toMillis} milliseconds
                     |         >>>      diff: ${(timeSpent.timeSpent - waitFor).toMillis} milliseconds
                     |         >>>
                     |""".stripMargin
             )
           )
    } yield {
      Assertions.assertEquals(result, "Done")
      Assertions.assert(
        timeSpent.timeSpent.isWithIn(waitFor +- diff),
        show""">>> timeSpent (${timeSpent.timeSpent.toMillis} milliseconds) should be
              |    within ${waitFor - diff} to ${waitFor + diff}.
              |--- diff test log ---
              |>         actual: ${timeSpent.timeSpent.toMillis.show} milliseconds
              |> expected range: ${(waitFor - diff).show} to ${(waitFor + diff).show}
              |>        waitFor: ${waitFor.show}
              |>  expected diff: +- ${diff.show})
              |>    actual diff: ${(timeSpent.timeSpent - waitFor).toMillis.show} milliseconds
              |""".stripMargin,
      )
    }).unsafeToFuture()
  }

}
