package effectie.instances.future

import effectie.core.FromFuture
import effectie.testing.FutureTools
import hedgehog._
import hedgehog.runner._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2020-09-22
  */
object fromFutureSpec extends Properties {

  override def tests: List[Test] = List(
    property("test FromFuture[Future].toEffect", FutureSpec.testToEffect)
  )

  object FutureSpec extends FutureTools {
    implicit val ec: ExecutionContext = globalExecutionContext

    private val waitFor300Millis = WaitFor(3000.milliseconds)

    import effectie.instances.future.fromFuture._

    def testToEffect: Property = for {
      a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
    } yield {
      lazy val fa = Future(a)
      val actual  = futureToValue(FromFuture[Future].toEffect(fa), waitFor300Millis)

      actual ==== a
    }
  }

}
