package effectie.monix

import cats._
import cats.effect.IO
import effectie.core.ToFuture
import effectie.monix.toFuture._
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog._
import hedgehog.runner._
import monix.eval.Task
import monix.execution.Scheduler

import java.util.concurrent.ExecutorService
import scala.concurrent.Future
import scala.concurrent.duration._

/** @author Kevin Lee
  * @since 2020-09-23
  */
object toFutureSpec extends Properties {
  private implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = List(
    property(
      "test ToFuture[Task].unsafeToFuture",
      TaskSpec.testUnsafeToFuture
    ),
    property(
      "test ToFuture[IO].unsafeToFuture",
      IoSpec.testUnsafeToFuture
    ),
    property(
      "test ToFuture[Id].unsafeToFuture",
      IdSpec.testUnsafeToFuture
    )
  )

  object TaskSpec {
    @SuppressWarnings(Array("org.wartremover.warts.IsInstanceOf"))
    def testUnsafeToFuture: Property = for {
      a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
    } yield {
      val expected = a
      val fa       = Task(expected)

      implicit val es: ExecutorService  = ConcurrentSupport.newExecutorService(2)
      @SuppressWarnings(Array("org.wartremover.warts.ExplicitImplicitTypes"))
      implicit val ec                   =
        ConcurrentSupport.newExecutionContextWithLogger(es, ErrorLogger.printlnExecutionContextErrorLogger)
      implicit val scheduler: Scheduler = Scheduler(ec)

      ConcurrentSupport.runAndShutdown(es, WaitFor(300.milliseconds)) {
        val future     = ToFuture[Task].unsafeToFuture(fa)
        val taskResult = fa.runSyncUnsafe() ==== expected
        val actual     = ConcurrentSupport.futureToValueAndTerminate(es, WaitFor(300.milliseconds))(future)

        Result.all(
          List(
            taskResult,
            Result
              .assert(future.isInstanceOf[Future[Int]]) // scalafix:ok DisableSyntax.isInstanceOf
              .log(s"future is not an instance of Future[Int]. future.getClass: ${future.getClass.toString}"),
            actual ==== expected,
          )
        )
      }
    }

  }

  object IoSpec {
    @SuppressWarnings(Array("org.wartremover.warts.IsInstanceOf"))
    def testUnsafeToFuture: Property = for {
      a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
    } yield {
      val expected = a
      val fa       = IO(expected)

      implicit val es: ExecutorService  = ConcurrentSupport.newExecutorService(2)
      @SuppressWarnings(Array("org.wartremover.warts.ExplicitImplicitTypes"))
      implicit val ec                   =
        ConcurrentSupport.newExecutionContextWithLogger(es, ErrorLogger.printlnExecutionContextErrorLogger)
      implicit val scheduler: Scheduler = Scheduler(ec)

      ConcurrentSupport.runAndShutdown(es, WaitFor(300.milliseconds)) {
        val future   = ToFuture[IO].unsafeToFuture(fa)
        val ioResult = fa.unsafeRunSync() ==== expected
        val actual   = ConcurrentSupport.futureToValueAndTerminate(es, WaitFor(300.milliseconds))(future)

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
    @SuppressWarnings(Array("org.wartremover.warts.IsInstanceOf"))
    def testUnsafeToFuture: Property = for {
      a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
    } yield {
      implicit val es: ExecutorService = ConcurrentSupport.newExecutorService(2)

      val fa          = a
      @SuppressWarnings(Array("org.wartremover.warts.ExplicitImplicitTypes"))
      implicit val ec =
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
            actual ==== a
          )
        )
      }
    }
  }

}
