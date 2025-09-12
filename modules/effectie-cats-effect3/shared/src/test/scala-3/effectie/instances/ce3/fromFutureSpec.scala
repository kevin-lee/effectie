package effectie.instances.ce3

import cats.Id
import cats.effect.*
import cats.effect.unsafe.IORuntime
import effectie.core.FromFuture
import effectie.core.FromFuture.FromFutureToIdTimeout
import effectie.instances.ce3.compat.CatsEffectIoCompatForFuture
import effectie.instances.ce3.fromFuture.given
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog.*
import hedgehog.runner.*

import scala.concurrent.duration.*
import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2020-09-22
  */
object fromFutureSpec extends Properties {

  private given errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = List(
    property("test FromFuture[IO].toEffect", IoSpec.testToEffect),
    property("test FromFuture[Id].toEffect", IdSpec.testToEffect),
  )

  object IoSpec {

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
