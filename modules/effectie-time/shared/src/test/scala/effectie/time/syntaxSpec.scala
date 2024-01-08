package effectie.time

import cats.effect.{IO, Timer}
import cats.syntax.all._
import effectie.syntax.all._
import effectie.time.syntax._
import hedgehog._
import hedgehog.runner._

import scala.concurrent.duration._

/** @author Kevin Lee
  * @since 2024-01-08
  */
object syntaxSpec extends Properties {

  import effectie.instances.ce2.fx.ioFx

  type F[A] = IO[A]
  val F = IO

  implicit val timer: Timer[F] = F.timer(scala.concurrent.ExecutionContext.global)

  override def tests: List[Test] = List(
    property("test FiniteDuration +- FiniteDuration to create ApproxFiniteDuration", testPlusMinus),
    property("test FiniteDuration.isWithIn(ApproxFiniteDuration) with valid FiniteDuration", testIsWithInValid),
    property("test FiniteDuration.isWithIn(ApproxFiniteDuration) with invalid FiniteDuration", testIsWithInInvalid),
    property("test F[A].withTimeSpent", testWithTimeSpent).withTests(count = 5),
  )

  private def runIO(test: F[Result]): Result = test.unsafeRunSync()

  def testPlusMinus: Property =
    for {
      tolerance <- Gen.int(Range.linear(0, Int.MaxValue >> 1)).log("tolerance")
      base      <- Gen.int(Range.linear(tolerance, Int.MaxValue >> 1)).log("base")

      expected <- Gen
                    .constant(
                      ApproxFiniteDuration(base.seconds, tolerance.seconds)
                    )
                    .log("approxFiniteDuration")
    } yield {
      val baseDuration      = base.seconds
      val toleranceDuration = tolerance.seconds

      val actual = baseDuration +- toleranceDuration
      actual ==== expected
    }

  def testIsWithInValid: Property =
    for {
      tolerance <- Gen.int(Range.linear(0, Int.MaxValue >> 1)).log("tolerance")
      base      <- Gen.int(Range.linear(tolerance, Int.MaxValue >> 1)).log("base")

      approx <- Gen
                  .constant(
                    ApproxFiniteDuration(base.seconds, tolerance.seconds)
                  )
                  .log("approxFiniteDuration")
    } yield {
      Result.all(
        List(
          {
            val min = base - tolerance
            Result.diffNamed(show"${min.toString}.seconds.isWithIn($approx) should be true", min.seconds, approx)(
              _.isWithIn(_)
            )
          }, {
            Result.diffNamed(show"${base.toString}.seconds.isWithIn($approx) should be true", base.seconds, approx)(
              _.isWithIn(_)
            )
          }, {
            val max = base + tolerance
            Result.diffNamed(show"${max.toString}.seconds.isWithIn($approx) should be true", max.seconds, approx)(
              _.isWithIn(_)
            )
          },
        )
      )
    }

  def testIsWithInInvalid: Property =
    for {
      tolerance <- Gen.int(Range.linear(0, Int.MaxValue >> 1 - 3)).log("tolerance")
      base      <- Gen.int(Range.linear(tolerance + 1, Int.MaxValue >> 1 - 2)).log("base")

      approx <- Gen
                  .constant(
                    ApproxFiniteDuration(base.seconds, tolerance.seconds)
                  )
                  .log("approxFiniteDuration")
    } yield {
      Result.all(
        List(
          {
            val min = base - tolerance - 1
            Result.diffNamed(show"${min.toString}.seconds.isWithIn($approx) should be false", min.seconds, approx)(
              !_.isWithIn(_)
            )
          }, {
            val max = base + tolerance + 2
            Result.diffNamed(show"${max.toString}.seconds.isWithIn($approx) should be false", max.seconds, approx)(
              !_.isWithIn(_)
            )
          },
        )
      )
    }

  def testWithTimeSpent: Property = {
    for {
      waitFor <- Gen.int(Range.linear(200, 700)).map(_.milliseconds).log("waitFor")
      diff    <- Gen.constant(180.milliseconds).log("diff")
      approx  <- Gen.constant(waitFor +- diff).log("approx")
    } yield runIO {
      implicit val timeSource: TimeSource[F] = TimeSource.default[F]("Test")

      for {
        _                  <- F.sleep(500.milliseconds) // warm up
        resultAndTimeSpent <- {
                                F.sleep(waitFor) *>
                                  pureOf("Done")
                              }.withTimeSpent
        (result, timeSpent) = resultAndTimeSpent
        _ <- effectOf(
               println(
                 show""">>>        waitFor: $waitFor
                       |>>>      timeSpent: ${timeSpent.timeSpent.toMillis} milliseconds
                       |>>>           diff: ${(timeSpent.timeSpent - waitFor).toMillis} milliseconds
                       |>>> expected range: $approx
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
                  show"within $approx.",
                timeSpent,
                approx,
              )(_.timeSpent.isWithIn(_))
              .log(
                show"""--- diff test log ---
                      |>         actual: ${timeSpent.timeSpent.toMillis.show} milliseconds
                      |> expected range: $approx
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
