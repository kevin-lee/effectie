package effectie.instances.monix3.f

import effectie.core.FromFuture
import effectie.instances.monix3.compat.CatsEffectIoCompatForFuture
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import effectie.instances.monix3.fromFuture.fromFutureToTask
import hedgehog._
import hedgehog.runner._

import scala.concurrent.duration._
import scala.concurrent.Future

import monix.eval.Task

/** @author Kevin Lee
  * @since 2020-09-22
  */
object fromFutureSpec extends Properties {
  implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  private val waitFor300Millis = WaitFor(300.milliseconds)

  override def tests: List[Test] = List(
    property("test FromFuture[Task].toEffect", TaskSpec.testToEffect)
  )

  object TaskSpec {
    import monix.execution.Scheduler.Implicits.global

    def testToEffect: Property = for {
      a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
    } yield {
      val compat = new CatsEffectIoCompatForFuture
      import compat.es

      ConcurrentSupport.runAndShutdown(es, waitFor300Millis) {
        lazy val fa = Future(a)
        val actual  = FromFuture[Task].toEffect(fa).runSyncUnsafe()

        actual ==== a
      }
    }
  }

}
