package effectie.time

import hedgehog._
import hedgehog.runner._

import scala.concurrent.duration._

/** @author Kevin Lee
  * @since 2024-01-08
  */
object ApproxFiniteDurationSpec extends Properties {
  override def tests: List[Test] = List(
    property("test ApproxFiniteDuration.min and ApproxFiniteDuration.max", testMinMax)
  )

  def testMinMax: Property =
    for {
      base      <- Gen.int(Range.linear(0, Int.MaxValue >> 1)).log("base")
      tolerance <- Gen.int(Range.linear(0, Int.MaxValue >> 1)).log("tolerance")

      approxFiniteDuration <- Gen
                                .constant(
                                  ApproxFiniteDuration(base.seconds, tolerance.seconds)
                                )
                                .log("approxFiniteDuration")
    } yield {
      val expectedMin = (base - tolerance).seconds
      val expectedMax = (base + tolerance).seconds
      Result.all(
        List(
          (approxFiniteDuration.min ==== expectedMin).log(
            s"""approxFiniteDuration.min is not equal to expectedMin
               |approxFiniteDuration.min: ${approxFiniteDuration.min.toString}
               |             expectedMin: ${expectedMin.toString}
               |""".stripMargin
          ),
          (approxFiniteDuration.max ==== expectedMax).log(
            s"""approxFiniteDuration.max is not equal to expectedMax
               |approxFiniteDuration.max: ${approxFiniteDuration.max.toString}
               |             expectedMax: ${expectedMax.toString}
               |""".stripMargin
          ),
        )
      )
    }
}
