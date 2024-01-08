package effectie.time

import cats.effect._
import cats.syntax.all._
import effectie.syntax.all._
import effectie.time.syntax._
import hedgehog._
import hedgehog.runner._

import java.time.Instant
import scala.concurrent.duration._

object TimeSourceSpec extends Properties {
  import effectie.instances.ce2.fx.ioFx

  type F[A] = IO[A]
  val F = IO

  implicit val timer: Timer[F] = F.timer(scala.concurrent.ExecutionContext.global)

  override def tests: List[Test] = List(
    example("test Show[TimeSource]", testShowTimeSource),
    example("example test realTime and monotonic", testRealTimeAndMonotonicExample),
    property("property test realTime and monotonic", testRealTimeAndMonotonicProperty),
    example("test default TimeSource - realTime", testDefaultTimeSourceRealTime),
    example("test default TimeSource - monotonic", testDefaultTimeSourceMonotonic),
    property("test default TimeSource - timeSpent", testDefaultTimeSourceTimeSpent).withTests(count = 5),
  )

  private def runIO(test: F[Result]): Result = test.unsafeRunSync()

  def testShowTimeSource: Result = {
    val name     = "test TimeSource"
    val expected = s"DefaultTimeSource(name=$name)"
    val actual   = TimeSource.default[F](name).show
    actual ==== expected
  }

  def testRealTimeAndMonotonicExample: Result = runIO {
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
    List(
      timeSource
        .realTime
        .map(time => (time ==== expectedMillis.milliseconds).log("timeSource.realTime")),
      timeSource
        .realTimeTo(MILLISECONDS)
        .map(time => (time ==== expectedMillis.milliseconds).log("timeSource.realTimeTo(MILLISECONDS)")),
      timeSource
        .realTimeTo(NANOSECONDS)
        .map(time => (time ==== expectedNanos.nanoseconds).log("timeSource.realTimeTo(NANOSECONDS)")),
      timeSource
        .monotonic
        .map(time => (time ==== expectedNanos.nanoseconds).log("timeSource.monotonic")),
      timeSource
        .monotonicTo(MILLISECONDS)
        .map(time => (time ==== expectedMillis.milliseconds).log("timeSource.monotonicTo(MILLISECONDS)")),
      timeSource
        .monotonicTo(NANOSECONDS)
        .map(time => (time ==== expectedNanos.nanoseconds).log("timeSource.monotonicTo(NANOSECONDS)")),
    ).sequence
      .map(Result.all)
  }

  def testRealTimeAndMonotonicProperty: Property = {
    for {
      epochSeconds <- Gen.long(Range.linear(1L, 999999999L)).log("epochSeconds")
      nanoSeconds  <- Gen.long(Range.linear(1L, 999999999L)).log("nanoSeconds")
      fullNanos    <- Gen.constant(epochSeconds * 1000000000L + nanoSeconds).log("fullNanos")

    } yield runIO {
      val expectedMillis = fullNanos / 1000000L
      val expectedNanos  = fullNanos

      val timeSource = TimeSource.withSources[F](
        "Test",
        pureOf(Instant.ofEpochSecond(epochSeconds, nanoSeconds)),
        pureOf(fullNanos),
      )

      import scala.concurrent.duration._
      List(
        timeSource
          .realTime
          .map(time => (time ==== expectedMillis.milliseconds).log("timeSource.realTime")),
        timeSource
          .realTimeTo(MILLISECONDS)
          .map(time => (time ==== expectedMillis.milliseconds).log("timeSource.realTimeTo(MILLISECONDS)")),
        timeSource
          .realTimeTo(NANOSECONDS)
          .map(time => (time ==== expectedNanos.nanoseconds).log("timeSource.realTimeTo(NANOSECONDS)")),
        timeSource
          .monotonic
          .map(time => (time ==== expectedNanos.nanoseconds).log("timeSource.monotonic")),
        timeSource
          .monotonicTo(MILLISECONDS)
          .map(time => (time ==== expectedMillis.milliseconds).log("timeSource.monotonicTo(MILLISECONDS)")),
        timeSource
          .monotonicTo(NANOSECONDS)
          .map(time => (time ==== expectedNanos.nanoseconds).log("timeSource.monotonicTo(NANOSECONDS)")),
      ).sequence
        .map(Result.all)
    }
  }

  def testDefaultTimeSourceRealTime: Result = runIO {

    val now            = Instant.now()
    val expectedNanos  = now.getEpochSecond * 1000000000L + now.getNano
    val expectedMillis = expectedNanos / 1000000L

    val timeSource = TimeSource.default[F](
      "Test"
    )

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
  }

  def testDefaultTimeSourceMonotonic: Result = runIO {

    val expectedNanos  = System.nanoTime()
    val expectedMillis = expectedNanos / 1000000L

    val timeSource = TimeSource.default[F](
      "Test"
    )

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
  }

  def testDefaultTimeSourceTimeSpent: Property = {
    for {
      waitFor <- Gen.int(Range.linear(200, 700)).map(_.milliseconds).log("waitFor")
      diff    <- Gen.constant(180.milliseconds).log("diff")
    } yield runIO {
      val timeSource = TimeSource.default[F](
        "Test"
      )

      for {
        _                  <- F.sleep(500.milliseconds) // warm up
        resultAndTimeSpent <- timeSource.timeSpent {
                                F.sleep(waitFor) *>
                                  pureOf("Done")
                              }
        (result, timeSpent) = resultAndTimeSpent
        _ <- effectOf(
               println(
                 s""">>>   waitFor: ${waitFor.show}
                    |>>> timeSpent: ${timeSpent.timeSpent.toMillis.show} milliseconds
                    |>>>      diff: ${(timeSpent.timeSpent - waitFor).toMillis.show} milliseconds
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
  }

}
