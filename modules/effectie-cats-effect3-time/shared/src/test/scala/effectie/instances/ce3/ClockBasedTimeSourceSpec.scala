package effectie.instances.ce3

import cats.effect.{Clock, IO}
import cats.syntax.all._
import effectie.time.syntax._
import hedgehog._
import hedgehog.runner._

import java.time.Instant
import scala.concurrent.duration._

/** @author Kevin Lee
  * @since 2024-01-09
  */
object ClockBasedTimeSourceSpec extends Properties {

  type F[A] = IO[A]
  val F = IO

  override def tests: List[Test] = List(
    example("test currentTime", testCurrentTime),
    example("test realTime", testRealTime),
    example("test monotonic", testMonotonic),
    property("test timeSpent", testTimeSpent).withTests(count = 5),
  )

  import cats.effect.unsafe.implicits.global

  def testCurrentTime: Result = {
    ClockBasedTimeSource[F]
      .currentTime()
      .flatMap(actual =>
        Clock[F]
          .realTimeInstant
          .map { expected =>
            Result.diff(
              actual.toEpochMilli.milliseconds,
              (expected.toEpochMilli.milliseconds +- 500.milliseconds),
            )(
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

    List(
      timeSource
        .realTime
        .map(time =>
          Result
            .diff(time, (expectedMillis.milliseconds +- 1000.milliseconds))(_.isWithIn(_))
            .log("timeSource.realTime"),
        ),
      timeSource
        .realTimeTo(MILLISECONDS)
        .map(time =>
          Result
            .diff(time, (expectedMillis.milliseconds +- 1000.milliseconds))(_.isWithIn(_))
            .log("timeSource.realTimeTo(MILLISECONDS)"),
        ),
      timeSource
        .realTimeTo(NANOSECONDS)
        .map(time =>
          Result
            .diff(time, (expectedNanos.nanoseconds +- 1000.milliseconds))(_.isWithIn(_))
            .log("timeSource.realTimeTo(NANOSECONDS)"),
        ),
    ).sequence
      .map(Result.all)
      .unsafeRunSync()
  }

  def testMonotonic: Result = {

    val expectedNanos  = System.nanoTime()
    val expectedMillis = expectedNanos / 1000000L

    val timeSource = ClockBasedTimeSource[F]

    List(
      timeSource
        .monotonic
        .map(time =>
          Result
            .diff(time, (expectedNanos.nanoseconds +- 1000.milliseconds))(_.isWithIn(_))
            .log("timeSource.monotonic"),
        ),
      timeSource
        .monotonicTo(MILLISECONDS)
        .map(time =>
          Result
            .diff(time, (expectedMillis.milliseconds +- 1000.milliseconds))(_.isWithIn(_))
            .log("timeSource.monotonicTo(MILLISECONDS)"),
        ),
      timeSource
        .monotonicTo(NANOSECONDS)
        .map(time =>
          Result
            .diff(time, (expectedNanos.nanoseconds +- 1000.milliseconds))(_.isWithIn(_))
            .log("timeSource.monotonicTo(NANOSECONDS)"),
        ),
    ).sequence
      .map(Result.all)
      .unsafeRunSync()
  }

  def testTimeSpent: Property = {
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
                 show""">>>   waitFor: $waitFor
                       |>>> timeSpent: ${timeSpent.timeSpent.toMillis} milliseconds
                       |>>>      diff: ${(timeSpent.timeSpent - waitFor).toMillis} milliseconds
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
    }.unsafeRunSync()
  }

}
