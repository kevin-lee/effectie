package effectie.instances.ce2.f

import cats.effect._
import effectie.core.FromFuture
import effectie.instances.ce2.compat.CatsEffectIoCompatForFuture
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import effectie.instances.ce2.f.fromFuture.fromFutureToAsync
import hedgehog._
import hedgehog.runner._

import scala.concurrent.duration._
import scala.concurrent.Future

/** @author Kevin Lee
  * @since 2020-09-22
  */
object fromFutureSpec extends Properties {
  implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  private val waitFor300Millis = WaitFor(300.milliseconds)

  override def tests: List[Test] = List(
    property("test FromFuture[IO].toEffect", IoSpec.testToEffect)
  )

  object IoSpec {
    def testToEffect: Property = for {
      a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
    } yield {
      val compat = new CatsEffectIoCompatForFuture
      import compat.{es, ec, cs}

      ConcurrentSupport.runAndShutdown(es, waitFor300Millis) {
        lazy val fa = Future(a)
        val actual  = FromFuture[IO].toEffect(fa).unsafeRunSync()

        actual ==== a
      }
    }
  }

}
