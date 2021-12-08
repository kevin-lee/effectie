package effectie.monix

import cats.Id
import cats.effect.{ContextShift, IO}
import effectie.ConcurrentSupport
import hedgehog._
import hedgehog.runner._
import monix.eval.Task
import monix.execution.Scheduler

import java.util.concurrent.ExecutorService
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2020-09-22
  */
object FromFutureSpec extends Properties {
  override def tests: List[Test] = List(
    property("test FromFuture[Task].toEffect", TaskSpec.testToEffect),
    property("test FromFuture[IO].toEffect", IoSpec.testToEffect),
    property("test FromFuture[Future].toEffect", FutureSpec.testToEffect),
    property("test FromFuture[Id].toEffect", IdSpec.testToEffect)
  )

  object TaskSpec {
    def testToEffect: Property = for {
      a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
    } yield {
      val es                            = ConcurrentSupport.newExecutorService()
      implicit val ec: ExecutionContext = ConcurrentSupport.newExecutionContextWithLogger(es, println(_))
      implicit val scheduler: Scheduler = Scheduler(ec)

      ConcurrentSupport.runAndShutdown(es, 300.milliseconds) {
        lazy val fa = Future(a)
        val actual  = FromFuture[Task].toEffect(fa).runSyncUnsafe()

        actual ==== a
      }
    }
  }

  object IoSpec {
    def testToEffect: Property = for {
      a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
    } yield {
      val es                            = ConcurrentSupport.newExecutorService()
      implicit val ec: ExecutionContext = ConcurrentSupport.newExecutionContextWithLogger(es, println(_))
      implicit val cs: ContextShift[IO] = IO.contextShift(ec)

      ConcurrentSupport.runAndShutdown(es, 300.milliseconds) {
        lazy val fa = Future(a)
        val actual  = FromFuture[IO].toEffect(fa).unsafeRunSync()

        actual ==== a
      }
    }
  }

  object FutureSpec {
    def testToEffect: Property = for {
      a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
    } yield {
      implicit val es: ExecutorService  = ConcurrentSupport.newExecutorService()
      implicit val ec: ExecutionContext = ConcurrentSupport.newExecutionContext(es)

      ConcurrentSupport.runAndShutdown(es, 300.milliseconds) {
        lazy val fa = Future(a)
        val actual  = ConcurrentSupport.futureToValueAndTerminate(FromFuture[Future].toEffect(fa), 300.milliseconds)

        actual ==== a
      }
    }
  }

  object IdSpec {
    def testToEffect: Property = for {
      a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
    } yield {
      val es                            = ConcurrentSupport.newExecutorService()
      implicit val ec: ExecutionContext = ConcurrentSupport.newExecutionContextWithLogger(es, println(_))

      ConcurrentSupport.runAndShutdown(es, 300.milliseconds) {
        implicit val timeout: FromFuture.FromFutureToIdTimeout =
          FromFuture.FromFutureToIdTimeout(300.milliseconds)
        lazy val fa                                            = Future(a)
        val actual                                             = FromFuture[Id].toEffect(fa)

        actual ==== a
      }
    }
  }

}
