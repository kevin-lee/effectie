package effectie.instances.tries

import effectie.core.FromFuture
import effectie.core.FromFuture.FromFutureToTryTimeout
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog._
import hedgehog.runner._

import java.util.concurrent.ExecutorService
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

/** @author Kevin Lee
  * @since 2023-03-17
  */
object fromFutureSpec extends Properties {
  implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  private val waitFor300Millis = WaitFor(300.milliseconds)

  override def tests: List[Test] = List(
    property("test FromFuture[Try].toEffect", FutureSpec.testToEffect)
  )

  object FutureSpec {
    import effectie.instances.tries.fromFuture._

    def testToEffect: Property = for {
      a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
    } yield {
      implicit val es: ExecutorService  = ConcurrentSupport.newExecutorService(2)
      implicit val ec: ExecutionContext =
        ConcurrentSupport.newExecutionContextWithLogger(es, ErrorLogger.printlnExecutionContextErrorLogger)

      implicit val timeout: FromFutureToTryTimeout =
        FromFutureToTryTimeout(waitFor300Millis.waitFor)

      val expected = a
      lazy val fa  = Future(a)

      val actual = FromFuture[Try].toEffect(fa)

      actual ==== Success(expected)
    }
  }

}
