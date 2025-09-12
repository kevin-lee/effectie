package effectie.instances.ce2

import cats.effect.{IO, Timer}
import cats.syntax.all._
import effectie.time.syntax._
import hedgehog._
import hedgehog.runner._

import java.time.Instant
import scala.concurrent.duration._

/** @author Kevin Lee
  * @since 2024-01-09
  */
object ClockBasedTimeSourceSpec extends Properties with ClockBased {

  type F[A] = IO[A]
  val F = IO

  @SuppressWarnings(Array("org.wartremover.warts.GlobalExecutionContext"))
  implicit val timer: Timer[F] = F.timer(scala.concurrent.ExecutionContext.global)

  override def tests: List[Test] = List(
    example("ClockBasedTimeSource[F].currentTime", testCurrentTime),
    example("ClockBasedTimeSource[F].realTime should return the current time", testRealTime),
    example(
      "ClockBasedTimeSource[F].realTimeTo(MILLISECONDS) should return the current time in milliseconds",
      testRealTimeMilliseconds,
    ),
    example(
      "ClockBasedTimeSource[F].realTimeTo(NANOSECONDS) should return the current time in nanoseconds",
      testRealTimeNanoseconds,
    ),
    example("ClockBasedTimeSource[F].monotonic should return the System.nanoTime() in nanoseconds", testMonotonic),
    example(
      "ClockBasedTimeSource[F].monotonicTo(MILLISECONDS) should return the System.nanoTime() in milliseconds",
      testMonotonicMilliseconds,
    ),
    example(
      "ClockBasedTimeSource[F].monotonicTo(NANOSECONDS) should return the System.nanoTime() in nanoseconds",
      testMonotonicNanoseconds,
    ),
    property("ClockBasedTimeSource[F].timeSpent should return the time spent", testTimeSpent).withTests(count = 5),
  )

  def testCurrentTime: Result = {
    ClockBasedTimeSource[F]
      .currentTime()
      .flatMap(actual =>
        timer
          .clock
          .instantNow
          .map { expected =>
            Result.diff(actual.toEpochMilli.milliseconds, (expected.toEpochMilli.milliseconds +- 500.milliseconds))(
              _.isWithIn(_)
            )
          }
      )
      .unsafeRunSync()

  }

  def testRealTime: Result = {

    val now            = Instant.now()
    val expectedNanos  = now.getEpochSecond * 1000000000L + now.getNano
    val expectedMillis = expectedNanos / 1000000L

    val timeSource = ClockBasedTimeSource[F]

    timeSource
      .realTime
      .map(time =>
        Result
          .diff(time, (expectedMillis.milliseconds +- 1000.milliseconds))(_.isWithIn(_))
          .log("timeSource.realTime"),
      )
      .unsafeRunSync()
  }

  def testRealTimeMilliseconds: Result = {

    val now            = Instant.now()
    val expectedNanos  = now.getEpochSecond * 1000000000L + now.getNano
    val expectedMillis = expectedNanos / 1000000L

    val timeSource = ClockBasedTimeSource[F]

    timeSource
      .realTimeTo(MILLISECONDS)
      .map(time =>
        Result
          .diff(time, (expectedMillis.milliseconds +- 1000.milliseconds))(_.isWithIn(_))
          .log("timeSource.realTimeTo(MILLISECONDS)"),
      )
      .unsafeRunSync()
  }

  def testRealTimeNanoseconds: Result = {

    val now           = Instant.now()
    val expectedNanos = now.getEpochSecond * 1000000000L + now.getNano

    val timeSource = ClockBasedTimeSource[F]

    timeSource
      .realTimeTo(NANOSECONDS)
      .map(time =>
        Result
          .diff(time, (expectedNanos.nanoseconds +- 1000.milliseconds))(_.isWithIn(_))
          .log("timeSource.realTimeTo(NANOSECONDS)"),
      )
      .unsafeRunSync()
  }

  def testMonotonic: Result = {

    val expectedNanos = System.nanoTime()

    val timeSource = ClockBasedTimeSource[F]

    timeSource
      .monotonic
      .map(time =>
        Result
          .diff(time, (expectedNanos.nanoseconds +- 1000.milliseconds))(_.isWithIn(_))
          .log("timeSource.monotonic"),
      )
      .unsafeRunSync()
  }

  def testMonotonicMilliseconds: Result = {

    val expectedNanos  = System.nanoTime()
    val expectedMillis = expectedNanos / 1000000L

    val timeSource = ClockBasedTimeSource[F]

    timeSource
      .monotonicTo(MILLISECONDS)
      .map(time =>
        Result
          .diff(time, (expectedMillis.milliseconds +- 1000.milliseconds))(_.isWithIn(_))
          .log("timeSource.monotonicTo(MILLISECONDS)"),
      )
      .unsafeRunSync()
  }

  def testMonotonicNanoseconds: Result = {

    val expectedNanos = System.nanoTime()

    val timeSource = ClockBasedTimeSource[F]

    timeSource
      .monotonicTo(NANOSECONDS)
      .map(time =>
        Result
          .diff(time, (expectedNanos.nanoseconds +- 1000.milliseconds))(_.isWithIn(_))
          .log("timeSource.monotonicTo(NANOSECONDS)"),
      )
      .unsafeRunSync()
  }

  def testTimeSpent: Property =
    for {
      waitFor <- Gen.int(Range.linear(200, 700)).map(_.milliseconds).log("waitFor")
      diff    <- Gen.constant(180.milliseconds).log("diff")
    } yield {
      val timeSource = ClockBasedTimeSource[F]

      for {
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
        Result.all(
          List(
            result ==== "Done",
            Result
              .diffNamed(
                s"timeSpent (${timeSpent.timeSpent.toMillis.show} milliseconds) should be " +
                  s"within ${(waitFor - diff).show} to ${(waitFor + diff).show}.",
                timeSpent,
                (waitFor +- diff),
              )(_.timeSpent.isWithIn(_))
              .log(
                s"""--- diff test log ---
                   |>         actual: ${timeSpent.timeSpent.toMillis.show} milliseconds
                   |> expected range: ${(waitFor - diff).show} to ${(waitFor + diff).show}
                   |>        waitFor: ${waitFor.show}
                   |>  expected diff: +- ${diff.show})
                   |>    actual diff: ${(timeSpent.timeSpent - waitFor).toMillis.show} milliseconds
                   |""".stripMargin
              ),
          )
        )
      }
    }
      .unsafeRunSync()

}
