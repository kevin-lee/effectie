package effectie.instances.ce2

import cats.Id
import cats.effect._
import effectie.core.FromFuture
import effectie.core.FromFuture.FromFutureToIdTimeout
import effectie.instances.ce2.compat.CatsEffectIoCompatForFuture
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import fromFuture._
import hedgehog._
import hedgehog.runner._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2020-09-22
  */
object fromFutureSpec extends Properties {
  implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  private val waitFor300Millis = WaitFor(300.milliseconds)

  override def tests: List[Test] = List(
    property("test FromFuture[IO].toEffect", IoSpec.testToEffect),
    property("test FromFuture[Id].toEffect", IdSpec.testToEffect),
  )

  object IoSpec {
    def testToEffect: Property = for {
      a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
    } yield {
      val compat = new CatsEffectIoCompatForFuture
      import compat._

      ConcurrentSupport.runAndShutdown(es, waitFor300Millis) {
        lazy val fa = Future(a)
        val actual  = FromFuture[IO].toEffect(fa).unsafeRunSync()

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
