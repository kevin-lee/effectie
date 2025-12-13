package effectie.instances.ce3

import cats.effect._
import effectie.core.ToFuture
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import extras.hedgehog.ce3.syntax.runner._
import hedgehog._
import hedgehog.runner._
import toFuture._

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
      "test ToFuture[IO].unsafeToFuture",
      testUnsafeToFuture,
    )
  )

  @SuppressWarnings(Array("org.wartremover.warts.IsInstanceOf"))
  def testUnsafeToFuture: Property = for {
    a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
  } yield {
    val expected = a
    val fa       = IO(expected)

    implicit val es: ExecutorService = ConcurrentSupport.newExecutorService(2)
    ConcurrentSupport.runAndShutdown(es, WaitFor(800.milliseconds)) {
      implicit val ticket: Ticker = Ticker.withNewTestContext()

      val future   = ToFuture[IO].unsafeToFuture(fa)
      val ioResult = fa.completeAs(expected)
      val actual   = ConcurrentSupport.futureToValueAndTerminate(es, WaitFor(500.milliseconds))(future)

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
