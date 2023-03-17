package effectie.instances.tries

import effectie.core.ToFuture
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog._
import hedgehog.runner._

import java.util.concurrent.ExecutorService
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.util.Try

/** @author Kevin Lee
  * @since 2023-03-17
  */
object toFutureSpec extends Properties {
  private implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = List(
    property(
      "test ToFuture[Try].unsafeToFuture",
      FutureSpec.testUnsafeToFuture,
    )
  )

  private val waitFor300Millis = WaitFor(300.milliseconds)

  object FutureSpec {
    import effectie.instances.tries.toFuture._

    @SuppressWarnings(Array("org.wartremover.warts.IsInstanceOf"))
    def testUnsafeToFuture: Property = for {
      a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
    } yield {
      implicit val es: ExecutorService  = ConcurrentSupport.newExecutorService(2)
      @SuppressWarnings(Array("org.wartremover.warts.ExplicitImplicitTypes"))
      implicit val ec: ExecutionContext =
        ConcurrentSupport.newExecutionContextWithLogger(es, ErrorLogger.printlnExecutionContextErrorLogger)
      ConcurrentSupport.runAndShutdown(es, waitFor300Millis) {
        val expected = Future(a)
        val fa       = Try(a)

        val future = ToFuture[Try].unsafeToFuture(fa)
        val actual = ConcurrentSupport.futureToValueAndTerminate(es, waitFor300Millis)(future)

        Result.all(
          List(
            Result
              .assert(future.isInstanceOf[Future[Int]]) // scalafix:ok DisableSyntax.isInstanceOf
              .log(s"future is not an instance of Future[Int]. future.getClass: ${future.getClass.toString}"),
            actual ==== ConcurrentSupport.futureToValueAndTerminate(es, waitFor300Millis)(expected),
            actual ==== a,
          )
        )
      }
    }

  }

}
