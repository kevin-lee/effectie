package effectie.cats

import cats._
import cats.effect._
import cats.effect.testkit.TestContext
import effectie.ConcurrentSupport
import hedgehog._
import hedgehog.runner._

import java.util.concurrent.ExecutorService
import scala.concurrent.Future
import scala.concurrent.duration._

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

    @SuppressWarnings(Array("org.wartremover.warts.IsInstanceOf"))
    def testUnsafeToFuture: Property = for {
      a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
    } yield {
      val expected = a
      val fa       = IO(expected)

      implicit val es: ExecutorService = ConcurrentSupport.newExecutorService()
      @SuppressWarnings(Array("org.wartremover.warts.ExplicitImplicitTypes"))
      implicit val ec = ConcurrentSupport.newExecutionContextWithLogger(es, println(_))
      ConcurrentSupport.runAndShutdown(es, 800.milliseconds) {
        import CatsEffectRunner._
        implicit val ticket: Ticker = Ticker(TestContext())

        val future = ToFuture[IO].unsafeToFuture(fa)
        val ioResult = fa.completeAs(expected)
        val actual = ConcurrentSupport.futureToValueAndTerminate(future, 500.milliseconds)

        Result.all(
          List(
            ioResult,
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
    @SuppressWarnings(Array("org.wartremover.warts.IsInstanceOf"))
    def testUnsafeToFuture: Property = for {
      a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
    } yield {
      implicit val es: ExecutorService = ConcurrentSupport.newExecutorService()
      @SuppressWarnings(Array("org.wartremover.warts.ExplicitImplicitTypes"))
      implicit val ec = ConcurrentSupport.newExecutionContextWithLogger(es, println(_))
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
    @SuppressWarnings(Array("org.wartremover.warts.IsInstanceOf"))
    def testUnsafeToFuture: Property = for {
      a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
    } yield {
      implicit val es: ExecutorService = ConcurrentSupport.newExecutorService()

      val fa = a
      @SuppressWarnings(Array("org.wartremover.warts.ExplicitImplicitTypes"))
      implicit val ec = ConcurrentSupport.newExecutionContextWithLogger(es, println(_))
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
