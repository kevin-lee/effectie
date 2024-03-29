package effectie.instances.id

import cats.Id
import effectie.core.FromFuture
import effectie.core.FromFuture.FromFutureToIdTimeout
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog._
import hedgehog.runner._

import java.util.concurrent.ExecutorService
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2020-09-22
  */
object fromFutureSpec extends Properties {
  implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  private val waitFor300Millis = WaitFor(300.milliseconds)

  override def tests: List[Test] = List(
    property("test FromFuture[Future].toEffect", FutureSpec.testToEffect),
    property("test FromFuture[Id].toEffect", IdSpec.testToEffect),
  )

  object FutureSpec {
    import effectie.instances.future.fromFuture._

    def testToEffect: Property = for {
      a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
    } yield {
      implicit val es: ExecutorService  = ConcurrentSupport.newExecutorService(2)
      implicit val ec: ExecutionContext =
        ConcurrentSupport.newExecutionContextWithLogger(es, ErrorLogger.printlnExecutionContextErrorLogger)

      ConcurrentSupport.runAndShutdown(es, waitFor300Millis) {
        lazy val fa = Future(a)
        val actual  = ConcurrentSupport.futureToValueAndTerminate(es, waitFor300Millis)(FromFuture[Future].toEffect(fa))

        actual ==== a
      }
    }
  }

  object IdSpec {
    import effectie.instances.id.fromFuture._

    def testToEffect: Property = for {
      a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
    } yield {
      val es                            = ConcurrentSupport.newExecutorService(2)
      implicit val ec: ExecutionContext =
        ConcurrentSupport.newExecutionContextWithLogger(es, ErrorLogger.printlnExecutionContextErrorLogger)

      ConcurrentSupport.runAndShutdown(es, waitFor300Millis) {
        implicit val timeout: FromFutureToIdTimeout =
          FromFutureToIdTimeout(waitFor300Millis.waitFor)

        lazy val fa = Future(a)
        val actual  = FromFuture[Id].toEffect(fa)

        actual ==== a
      }
    }
  }

}
