package effectie.instances.tries

import effectie.core.ToFuture
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog.*
import hedgehog.runner.*

import java.util.concurrent.ExecutorService
import scala.concurrent.duration.*
import scala.concurrent.{ExecutionContext, Future}

import scala.util.Try

/** @author Kevin Lee
  * @since 2023-03-17
  */
object toFutureSpec extends Properties {
  private given errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = List(
    property(
      "test ToFuture[Try].unsafeToFuture",
      FutureSpec.testUnsafeToFuture,
    )
  )

  object FutureSpec {
    import effectie.instances.tries.toFuture._

    def testUnsafeToFuture: Property = for {
      a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
    } yield {
      given es: ExecutorService  = ConcurrentSupport.newExecutorService(2)
      given ec: ExecutionContext =
        ConcurrentSupport.newExecutionContextWithLogger(es, ErrorLogger.printlnExecutionContextErrorLogger)
      ConcurrentSupport.runAndShutdown(es, WaitFor(300.milliseconds)) {
        val expected = Future(a)
        val fa       = Try(a)

        val future = ToFuture[Try].unsafeToFuture(fa)
        val actual = ConcurrentSupport.futureToValueAndTerminate(es, WaitFor(300.milliseconds))(future)

        Result.all(
          List(
            Result
              .assert(future.isInstanceOf[Future[Int]]) // scalafix:ok DisableSyntax.isInstanceOf
              .log(s"future is not an instance of Future[Int]. future.getClass: ${future.getClass.toString}"),
            actual ==== ConcurrentSupport.futureToValueAndTerminate(es, WaitFor(300.milliseconds))(expected),
            actual ==== a,
          )
        )
      }
    }

  }

}
