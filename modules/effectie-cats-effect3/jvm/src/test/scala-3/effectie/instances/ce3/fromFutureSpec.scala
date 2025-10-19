package effectie.instances.ce3

import cats.effect.*
import cats.effect.unsafe.IORuntime
import effectie.core.FromFuture
import effectie.instances.ce3.compat.CatsEffectIoCompatForFuture
import effectie.instances.ce3.fromFuture.given
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog.*
import hedgehog.runner.*

import scala.concurrent.*
import scala.concurrent.duration.*

/** @author Kevin Lee
  * @since 2020-09-22
  */
object fromFutureSpec extends Properties {

  private given errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = List(
    property("test FromFuture[IO].toEffect", testToEffect)
  )

  def testToEffect: Property = for {
    a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
  } yield {
    val compat          = new CatsEffectIoCompatForFuture
    import compat.ec
    given rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

    ConcurrentSupport.runAndShutdown(compat.es, WaitFor(300.milliseconds)) {
      lazy val fa = Future(a)
      val actual  = FromFuture[IO].toEffect(fa).unsafeRunSync()

      actual ==== a
    }
  }

}
