package effectie.time

import cats.effect.{IO, Timer}
import cats.syntax.all._
import effectie.syntax.all._
import effectie.testing.{FutureTools, RandomGens}
import effectie.time.syntax._
import munit.Assertions

import scala.concurrent._
import scala.concurrent.duration._

/** @author Kevin Lee
  * @since 2024-01-08
  */
class syntaxSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  override val munitTimeout: FiniteDuration = 200.milliseconds

  import effectie.instances.ce2.fx.ioFx

  type F[A] = IO[A]
  val F = IO

  implicit val timer: Timer[F] = F.timer(globalExecutionContext)

  test("test FiniteDuration +- FiniteDuration to create ApproxFiniteDuration") {
    val tolerance = RandomGens.genRandomIntWithMinMax(0, Int.MaxValue >> 1)
    val base      = RandomGens.genRandomIntWithMinMax(tolerance, Int.MaxValue >> 1)

    val expected          = ApproxFiniteDuration(base.seconds, tolerance.seconds)
    val baseDuration      = base.seconds
    val toleranceDuration = tolerance.seconds

    val actual = baseDuration +- toleranceDuration
    Assertions.assertEquals(actual, expected)
  }

  test("test FiniteDuration.isWithIn(ApproxFiniteDuration) with valid FiniteDuration") {
    val tolerance = RandomGens.genRandomIntWithMinMax(0, Int.MaxValue >> 1)
    val base      = RandomGens.genRandomIntWithMinMax(tolerance, Int.MaxValue >> 1)

    val approx = ApproxFiniteDuration(base.seconds, tolerance.seconds)

    {
      val min = base - tolerance
      Assertions.assert(min.seconds.isWithIn(approx), show"${min.toString}.seconds.isWithIn($approx) should be true")
    }
    {
      Assertions.assert(base.seconds.isWithIn(approx), show"${base.toString}.seconds.isWithIn($approx) should be true")
    }
    {
      val max = base + tolerance
      Assertions.assert(max.seconds.isWithIn(approx), show"${max.toString}.seconds.isWithIn($approx) should be true")
    }
  }

  test("test FiniteDuration.isWithIn(ApproxFiniteDuration) with invalid FiniteDuration") {
    val tolerance = RandomGens.genRandomIntWithMinMax(0, (Int.MaxValue >> 1) - 3)
    val base      = RandomGens.genRandomIntWithMinMax(tolerance + 1, (Int.MaxValue >> 1) - 2)

    val approx = ApproxFiniteDuration(base.seconds, tolerance.seconds)

    {
      val min = base - tolerance - 1
      Assertions.assert(
        !min.seconds.isWithIn(approx),
        show"${min.toString}.seconds.isWithIn($approx) should be false",
      )
    }
    {
      val max = base + tolerance + 2
      Assertions.assert(
        !max.seconds.isWithIn(approx),
        show"${max.toString}.seconds.isWithIn($approx) should be false",
      )
    }
  }

  test("test F[A].withTimeSpent") {

    val waitFor = RandomGens.genRandomIntWithMinMax(200, 700).milliseconds
    val diff    = 180.milliseconds

    val approx = waitFor +- diff

    implicit val timeSource: TimeSource[F] = TimeSource.default[F]("Test")

    (for {
      _                  <- F.sleep(500.milliseconds) // warm up
      resultAndTimeSpent <- {
                              F.sleep(waitFor) *>
                                pureOf("Done")
                            }.withTimeSpent
      (result, timeSpent) = resultAndTimeSpent
      _ <- effectOf(
             println(
               show"""    >>> ===================================
                     |    >>> TimeSource.default[F].withTimeSpent
                     |    >>> -----------------------------------
                     |    >>>        waitFor: $waitFor
                     |    >>>      timeSpent: ${timeSpent.timeSpent.toMillis} milliseconds
                     |    >>>           diff: ${(timeSpent.timeSpent - waitFor).toMillis} milliseconds
                     |    >>> expected range: $approx
                     |    >>>
                     |""".stripMargin
             )
           )
    } yield {
      Assertions.assertEquals(result, "Done")

      Assertions.assert(
        timeSpent.timeSpent.isWithIn(approx),
        show"""> ===================
              |  timeSpent (${timeSpent.timeSpent.toMillis} milliseconds) should be within $approx.
              |  -------------------
              |--- diff test log ---
              |>         actual: ${timeSpent.timeSpent.toMillis} milliseconds
              |> expected range: $approx
              |>        waitFor: $waitFor
              |>  expected diff: +- $diff)
              |>    actual diff: ${(timeSpent.timeSpent - waitFor).toMillis} milliseconds
              |""".stripMargin,
      )
    }).unsafeToFuture()
  }

}
