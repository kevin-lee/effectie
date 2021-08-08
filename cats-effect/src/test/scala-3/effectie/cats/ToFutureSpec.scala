package effectie.cats

import cats.*
import cats.effect.*
import effectie.ConcurrentSupport
import hedgehog.*
import hedgehog.runner.*

import java.util.concurrent.ExecutorService
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.*

/** @author Kevin Lee
  * @since 2020-09-23
  */
object ToFutureSpec extends Properties {
  override def tests: List[Test] = List(
    property(
      "test ToFuture[IO].unsafeToFuture",
      IoSpec.testUnsafeToFuture
    ),
    property(
      "test ToFuture[Future].unsafeToFuture",
      FutureSpec.testUnsafeToFuture
    ),
    property(
      "test ToFuture[Id].unsafeToFuture",
      IdSpec.testUnsafeToFuture
    )
  )

  object IoSpec {
    def testUnsafeToFuture: Property = for {
      a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
    } yield {
      val fa = IO(a)

      given es: ExecutorService  = ConcurrentSupport.newExecutorService()
      given ec: ExecutionContext = ConcurrentSupport.newExecutionContextWithLogger(es, println(_))
      ConcurrentSupport.runAndShutdown(es, 800.milliseconds) {
        val future   = ToFuture[IO].unsafeToFuture(fa)
        val expected = a
        val actual   = ConcurrentSupport.futureToValueAndTerminate(future, 500.milliseconds)

        Result.all(
          List(
            Result
              .assert(future.isInstanceOf[Future[Int]])
              .log(s"future is not an instance of Future[Int]. future.getClass: ${future.getClass.toString}"),
            actual ==== expected
          )
        )
      }
    }

  }

  object FutureSpec {
    def testUnsafeToFuture: Property = for {
      a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
    } yield {
      given es: ExecutorService  = ConcurrentSupport.newExecutorService()
      given ec: ExecutionContext = ConcurrentSupport.newExecutionContextWithLogger(es, println(_))
      ConcurrentSupport.runAndShutdown(es, 300.milliseconds) {
        val expected = Future(a)
        val fa       = Future(a)

        val future = ToFuture[Future].unsafeToFuture(fa)
        val actual = ConcurrentSupport.futureToValueAndTerminate(future, 300.milliseconds)

        Result.all(
          List(
            Result
              .assert(future.isInstanceOf[Future[Int]])
              .log(s"future is not an instance of Future[Int]. future.getClass: ${future.getClass.toString}"),
            actual ==== ConcurrentSupport.futureToValueAndTerminate(expected, 300.milliseconds),
            actual ==== a
          )
        )
      }
    }

  }

  object IdSpec {
    def testUnsafeToFuture: Property = for {
      a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
    } yield {
      given es: ExecutorService = ConcurrentSupport.newExecutorService()

      val fa                     = a
      given ec: ExecutionContext = ConcurrentSupport.newExecutionContextWithLogger(es, println(_))
      ConcurrentSupport.runAndShutdown(es, 300.milliseconds) {
        val expected = Future(a)

        val future = ToFuture[Id].unsafeToFuture(fa)
        val actual = ConcurrentSupport.futureToValueAndTerminate(future, 300.milliseconds)

        Result.all(
          List(
            Result
              .assert(future.isInstanceOf[Future[Int]])
              .log(s"future is not an instance of Future[Int]. future.getClass: ${future.getClass.toString}"),
            actual ==== ConcurrentSupport.futureToValueAndTerminate(expected, 300.milliseconds),
            actual ==== a
          )
        )
      }
    }
  }

}
