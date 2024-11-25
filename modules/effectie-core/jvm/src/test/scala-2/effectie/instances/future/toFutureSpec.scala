package effectie.instances.future

import effectie.core.ToFuture
import effectie.testing.FutureTools
import hedgehog._
import hedgehog.runner._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

/** @author Kevin Lee
  * @since 2020-09-23
  */
object toFutureSpec extends Properties {

  override def tests: List[Test] = List(
    property(
      "test ToFuture[Future].unsafeToFuture",
      FutureSpec.testUnsafeToFuture,
    )
  )

  object FutureSpec extends FutureTools {
    implicit val ec: ExecutionContext = globalExecutionContext

    private val waitFor300Millis = WaitFor(300.milliseconds)

    import effectie.instances.future.toFuture._

    @SuppressWarnings(Array("org.wartremover.warts.IsInstanceOf"))
    def testUnsafeToFuture: Property = for {
      a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
    } yield {

      val expected = Future(a)
      val fa       = Future(a)

      val future = ToFuture[Future].unsafeToFuture(fa)
      val actual = futureToValue(future, waitFor300Millis)

      Result.all(
        List(
          Result
            .assert(future.isInstanceOf[Future[Int]]) // scalafix:ok DisableSyntax.isInstanceOf
            .log(s"future is not an instance of Future[Int]. future.getClass: ${future.getClass.toString}"),
          actual ==== futureToValue(expected, waitFor300Millis),
          actual ==== a,
        )
      )
    }

  }

}
