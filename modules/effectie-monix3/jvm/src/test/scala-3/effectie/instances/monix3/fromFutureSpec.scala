package effectie.instances.monix3

import cats.Id

import effectie.core.FromFuture
import effectie.core.FromFuture.FromFutureToIdTimeout
import effectie.instances.monix3.compat.CatsEffectIoCompatForFuture
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog.*
import hedgehog.runner.*

import monix.eval.Task

import scala.concurrent.duration.*
import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2020-09-22
  */
object fromFutureSpec extends Properties {

  import monix.execution.Scheduler.Implicits.global

  private given errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = List(
    property("test FromFuture[Task].toEffect", TaskSpec.testToEffect),
    property("test FromFuture[Id].toEffect", IdSpec.testToEffect),
  )

  object TaskSpec {
    import effectie.instances.monix3.fromFuture.given

    def testToEffect: Property = for {
      a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
    } yield {
      val compat = new CatsEffectIoCompatForFuture
      import compat.{*, given}

      ConcurrentSupport.runAndShutdown(es, WaitFor(300.milliseconds)) {
        lazy val fa = Future(a)
        val actual  = FromFuture[Task].toEffect(fa).runSyncUnsafe()

        actual ==== a
      }
    }
  }

  object IdSpec {
    import effectie.instances.id.fromFuture.*

    def testToEffect: Property = for {
      a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
    } yield {
      val es                     = ConcurrentSupport.newExecutorService(2)
      given ec: ExecutionContext =
        ConcurrentSupport.newExecutionContextWithLogger(es, ErrorLogger.printlnExecutionContextErrorLogger)

      ConcurrentSupport.runAndShutdown(es, WaitFor(300.milliseconds)) {
        given timeout: FromFutureToIdTimeout =
          FromFutureToIdTimeout(300.milliseconds)

        lazy val fa = Future(a)
        val actual  = FromFuture[Id].toEffect(fa)

        actual ==== a
      }
    }
  }

}
