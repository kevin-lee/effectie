package effectie.instances.ce3

import cats.Id
import cats.effect.*
import cats.effect.unsafe.IORuntime
import effectie.instances.ce3.compat.CatsEffectIoCompatForFuture
import effectie.instances.ce3.toFuture.given
import effectie.core.ToFuture
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import extras.hedgehog.ce3.syntax.runner._
import hedgehog.*
import hedgehog.runner.*

import java.util.concurrent.ExecutorService
import scala.concurrent.duration.*
import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2020-09-23
  */
object toFutureSpec extends Properties {
  private given errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = List(
    property(
      "test ToFuture[IO].unsafeToFuture",
      IoSpec.testUnsafeToFuture,
    ),
    property(
      "test ToFuture[Id].unsafeToFuture",
      IdSpec.testUnsafeToFuture,
    ),
  )

  object IoSpec {
    def testUnsafeToFuture: Property = for {
      a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
    } yield {
      val expected = a
      val fa       = IO(expected)

      given es: ExecutorService  = ConcurrentSupport.newExecutorService(2)
      given ec: ExecutionContext =
        ConcurrentSupport.newExecutionContextWithLogger(es, ErrorLogger.printlnExecutionContextErrorLogger)

      ConcurrentSupport.runAndShutdown(es, WaitFor(800.milliseconds)) {
        given ticket: Ticker = Ticker.withNewTestContext()

        val future   = ToFuture[IO].unsafeToFuture(fa)
        val ioResult = fa.completeAs(expected)

        val actual = ConcurrentSupport.futureToValueAndTerminate(es, WaitFor(500.milliseconds))(future)

        Result.all(
          List(
            ioResult,
            Result
              .assert(future.isInstanceOf[Future[Int]]) // scalafix:ok DisableSyntax.isInstanceOf
              .log(s"future is not an instance of Future[Int]. future.getClass: ${future.getClass.toString}"),
            actual ==== expected,
          )
        )
      }
    }

  }

  object IdSpec {
    import effectie.instances.id.toFuture.*

    def testUnsafeToFuture: Property = for {
      a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
    } yield {
      given es: ExecutorService = ConcurrentSupport.newExecutorService(2)

      val fa                     = a
      given ec: ExecutionContext =
        ConcurrentSupport.newExecutionContextWithLogger(es, ErrorLogger.printlnExecutionContextErrorLogger)
      ConcurrentSupport.runAndShutdown(es, WaitFor(300.milliseconds)) {
        val expected = Future(a)

        val future = ToFuture[Id].unsafeToFuture(fa)
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
