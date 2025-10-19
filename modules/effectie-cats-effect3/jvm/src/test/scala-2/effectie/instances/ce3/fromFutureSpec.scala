package effectie.instances.ce3

import cats.effect._
import cats.effect.unsafe.IORuntime
import effectie.core.FromFuture
import effectie.instances.ce3.compat.CatsEffectIoCompatForFuture
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import fromFuture._
import hedgehog._
import hedgehog.runner._

import scala.concurrent.Future
import scala.concurrent.duration._

/** @author Kevin Lee
  * @since 2020-09-22
  */
object fromFutureSpec extends Properties {
  private implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = List(
    property("test FromFuture[IO].toEffect", testToEffect)
  )

  private val waitFor300Millis = WaitFor(300.milliseconds)

  def testToEffect: Property = for {
    a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
  } yield {
    val compat                 = new CatsEffectIoCompatForFuture
    import compat.ec
    implicit val rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

    ConcurrentSupport.runAndShutdown(compat.es, waitFor300Millis) {
      lazy val fa = Future(a)
      val actual  = FromFuture[IO].toEffect(fa).unsafeRunSync()

      actual ==== a
    }
  }

}
