package effectie.instances.tries

import effectie.core.FromFuture
import effectie.core.FromFuture.FromFutureToTryTimeout
import effectie.testing.FutureTools
import hedgehog._
import hedgehog.runner._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

/** @author Kevin Lee
  * @since 2023-03-17
  */
object fromFutureSpec extends Properties {

  override def tests: List[Test] = List(
    property("test FromFuture[Try].toEffect", FutureSpec.testToEffect)
  )

  object FutureSpec extends FutureTools {
    implicit val ec: ExecutionContext = globalExecutionContext

    private val waitFor300Millis = WaitFor(300.milliseconds)

    import effectie.instances.tries.fromFuture._

    def testToEffect: Property = for {
      a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
    } yield {

      implicit val timeout: FromFutureToTryTimeout =
        FromFutureToTryTimeout(waitFor300Millis.waitFor)

      val expected = a
      lazy val fa  = Future(a)

      val actual = FromFuture[Try].toEffect(fa)

      actual ==== Success(expected)
    }
  }

}
