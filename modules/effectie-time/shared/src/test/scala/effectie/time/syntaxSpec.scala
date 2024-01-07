package effectie.time

import cats.syntax.all._
import effectie.time.syntax._
import hedgehog._
import hedgehog.runner._

import scala.concurrent.duration._

/** @author Kevin Lee
  * @since 2024-01-08
  */
object syntaxSpec extends Properties {

  override def tests: List[Test] = List(
    property("test FiniteDuration +- FiniteDuration to create ApproxFiniteDuration", testPlusMinus),
    property("test FiniteDuration.isWithIn(ApproxFiniteDuration) with valid FiniteDuration", testIsWithInValid),
    property("test FiniteDuration.isWithIn(ApproxFiniteDuration) with invalid FiniteDuration", testIsWithInInvalid),
  )

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

}
