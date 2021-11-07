package effectie.cats

import cats.Id
import cats.effect.IO
import cats.effect.testkit.TestContext
import cats.effect.unsafe.IORuntime
import effectie.ConcurrentSupport
import effectie.cats.compat.CatsEffectIoCompatForFuture
import effectie.testing.tools.*
import effectie.testing.types.*
import hedgehog.*
import hedgehog.runner.*

/** @author Kevin Lee
  * @since 2021-05-16
  */
object EffectfulSpec extends Properties {
  override def tests: List[Test] = List(
    property("test Effectful.{effectOf, pureOf, unitOf} for IO", IoSpec.testAll),
    property("test Effectful.effectOf[IO]", IoSpec.testEffectOf),
    property("test Effectful.pureOf[IO]", IoSpec.testPureOf),
    example("test Effectful.unitOf[IO]", IoSpec.testUnitOf),
    example("test Effectful.errorOf[IO]", IoSpec.testErrorOf),
    property("test Effectful.{effectOf, pureOf, unitOf} for Future", FutureSpec.testAll),
    property("test Effectful.effectOf[Future]", FutureSpec.testEffectOf),
    property("test Effectful.pureOf[Future]", FutureSpec.testPureOf),
    example("test Effectful.unitOf[Future]", FutureSpec.testUnitOf),
    example("test Effectful.errorOf[Future]", FutureSpec.testErrorOf),
    property("test Effectful.{effectOf, pureOf, unitOf} for Id", IdSpec.testAll),
    property("test Effectful.effectOf[Id]", IdSpec.testEffectOf),
    property("test Effectful.pureOf[Id]", IdSpec.testPureOf),
    example("test Effectful.unitOf[Id]", IdSpec.testUnitOf),
    example("test Effectful.errorOf[Id]", IdSpec.testErrorOf)
  )

  import Effectful.*

  trait FxCtorClient[F[_]] {
    def eftOf[A](a: A): F[A]
    def of[A](a: A): F[A]
    def unit: F[Unit]
  }
  object FxCtorClient      {
    def apply[F[_]: FxCtorClient]: FxCtorClient[F]         = implicitly[FxCtorClient[F]]
    implicit def eftClientF[F[_]: FxCtor]: FxCtorClient[F] = new FxCtorClientF[F]
    final class FxCtorClientF[F[_]: FxCtor] extends FxCtorClient[F] {
      override def eftOf[A](a: A): F[A] = effectOf(a)
      override def of[A](a: A): F[A]    = pureOf(a)
      override def unit: F[Unit]        = unitOf
    }
  }

  trait FxClient[F[_]] {
    def eftOf[A](a: A): F[A]
    def of[A](a: A): F[A]
    def unit: F[Unit]
  }
  object FxClient      {
    def apply[F[_]: FxClient]: FxClient[F]         =
      implicitly[FxClient[F]]
    implicit def eftClientF[F[_]: Fx]: FxClient[F] = new FxClientF[F]
    final class FxClientF[F[_]: Fx] extends FxClient[F] {
      override def eftOf[A](a: A): F[A] = effectOf(a)
      override def of[A](a: A): F[A]    = pureOf(a)
      override def unit: F[Unit]        = unitOf
    }
  }

  object IoSpec {

    val compat          = new CatsEffectIoCompatForFuture
    given rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

    def testAll: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      var actual                  = before
      var actual2                 = before
      val testBefore              = (actual ==== before).log(s"actual testBefore should be $before but was $actual")
      val testBefore2             = (actual2 ==== before).log(s"actual2 testBefore2 should be $before but was $actual2")
      val eftClient               = FxCtorClient[IO]
      val effectConstructorClient = FxClient[IO]
      val io                      =
        for {
          _  <- effectOf[IO]({ actual = after; () })
          _  <- pureOf[IO]({ actual2 = after; () })
          n  <- eftClient.eftOf(1)
          n2 <- eftClient.of(n)
          i  <- effectConstructorClient.eftOf(1)
          i2 <- effectConstructorClient.of(1)
          _  <- eftClient.unit
          _  <- effectConstructorClient.unit
        } yield ()
      val testBeforeRun           = (actual ==== before).log(s"actual testBeforeRun should be $before but was $actual")
      val testBeforeRun2          = (actual2 ==== before).log(s"actual2 testBeforeRun2 should be $before but was $actual2")

      import CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val runResult     = io.completeAs(())
      val testAfterRun  = (actual ==== after).log(s"actual testAfterRun should be $after but was $actual")
      val testAfterRun2 = (actual2 ==== after).log(s"actual2 testAfterRun2 should be $after but was $actual2")
      Result.all(
        List(
          testBefore.log("testBefore"),
          testBefore2.log("testBefore2"),
          testBeforeRun.log("testBeforeRun"),
          testBeforeRun2.log("testBeforeRun2"),
          runResult,
          testAfterRun.log("testAfterRun"),
          testAfterRun2.log("testAfterRun2")
        )
      )
    }

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      var actual        = before
      val testBefore    = (actual ==== before).log(s"actual before should be $before but was $actual")
      val io            = effectOf[IO]({ actual = after; () })
      val testBeforeRun = (actual ==== before).log(s"actual beforeRun should be $before but was $actual")

      import CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val runResult    = io.completeAs(())
      val testAfterRun = (actual ==== after).log(s"actual afterRun should be $after but was $actual")
      Result.all(
        List(
          testBefore.log("testBefore"),
          testBeforeRun.log("testBeforeRun"),
          runResult,
          testAfterRun.log("testAfterRun")
        )
      )
    }

    def testPureOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      import CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      var actual        = before
      val testBefore    = (actual ==== before).log(s"actual before should be $before but was $actual")
      val io            = pureOf[IO]({ actual = after; () })
      val testBeforeRun = (actual ==== after).log(s"actual beforeRun should be $after but was $actual")

      val runResult = io.completeAs(())

      val testAfterRun = (actual ==== after).log(s"actual afterRun should be $after but was $actual")
      Result.all(
        List(
          testBefore.log("testBefore"),
          testBeforeRun.log("testBeforeRun"),
          runResult,
          testAfterRun.log("testAfterRun")
        )
      )
    }

    def testUnitOf: Result = {
      import CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val io             = unitOf[IO]
      val expected: Unit = ()
      io.completeAs(expected)
    }

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      import CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val io = errorOf[IO][Unit](expectedError)
      io.expectError(expectedError)
    }

  }

  object FutureSpec {

    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration.*
    import scala.concurrent.{ExecutionContext, Future}

    val waitFor: FiniteDuration = 1.second

    def testAll: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ExecutionContext                 = ConcurrentSupport.newExecutionContext(executorService)

      var actual                  = before
      var actual2                 = before
      val testBefore              = actual ==== before
      val testBefore2             = actual2 ==== before
      val eftClient               = FxCtorClient[Future]
      val effectConstructorClient = FxClient[Future]
      val future                  =
        for {
          _  <- effectOf[Future]({ actual = after; () })
          _  <- pureOf[Future]({ actual2 = after; () })
          n  <- eftClient.eftOf(1)
          n2 <- eftClient.of(n)
          i  <- effectConstructorClient.eftOf(1)
          i2 <- effectConstructorClient.of(1)
          _  <- eftClient.unit
          _  <- effectConstructorClient.unit
        } yield ()
      ConcurrentSupport.futureToValueAndTerminate(future, waitFor)
      val testAfterRun            = actual ==== after
      val testAfterRun2           = actual2 ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfterRun.log("testAfterRun"),
          testBefore2.log("testBefore2"),
          testAfterRun2.log("testAfterRun2")
        )
      )
    }

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ExecutionContext                 = ConcurrentSupport.newExecutionContext(executorService)

      var actual               = before
      val testBefore           = actual ==== before
      val future: Future[Unit] = effectOf[Future]({ actual = after; () })
      ConcurrentSupport.futureToValueAndTerminate(future, waitFor)
      val testAfterRun         = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfterRun.log("testAfterRun")
        )
      )
    }

    def testPureOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ExecutionContext                 = ConcurrentSupport.newExecutionContext(executorService)

      var actual       = before
      val testBefore   = actual ==== before
      val future       = pureOf[Future]({ actual = after; () })
      ConcurrentSupport.futureToValueAndTerminate(future, waitFor)
      val testAfterRun = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfterRun.log("testAfterRun")
        )
      )
    }

    def testUnitOf: Result = {
      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ExecutionContext                 = ConcurrentSupport.newExecutionContext(executorService)
      val future                             = unitOf[Future]
      val expected: Unit                     = ()
      val actual: Unit                       = ConcurrentSupport.futureToValueAndTerminate(future, waitFor)
      actual ==== expected
    }

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ExecutionContext                 = ConcurrentSupport.newExecutionContext(executorService)

      val future = errorOf[Future][Unit](expectedError)
      expectThrowable(ConcurrentSupport.futureToValueAndTerminate(future, waitFor), expectedError)
    }

  }

  object IdSpec {

    def testAll: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      var actual                  = before
      var actual2                 = before
      val testBefore              = actual ==== before
      val testBefore2             = actual2 ==== before
      val eftClient               = FxCtorClient[Id]
      val effectConstructorClient = FxClient[Id]
      effectOf[Id]({ actual = after; () })
      pureOf[Id]({ actual2 = after; () })
      val n: Int                  = eftClient.eftOf(1)

      dropResult {
        eftClient.of(n)
      }
      dropResult {
        effectConstructorClient.eftOf(1)
      }
      dropResult {
        effectConstructorClient.of(1)
      }
      eftClient.unit
      effectConstructorClient.unit
      val testAfter  = actual ==== after
      val testAfter2 = actual2 ==== after
      testBefore.log("testBefore") ==== testAfter.log("testAfter") and
        testBefore2.log("testBefore2") ==== testAfter2.log("testAfter2")
    }

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      var actual     = before
      val testBefore = actual ==== before
      effectOf[Id]({ actual = after; () })
      val testAfter  = actual ==== after
      testBefore.log("testBefore") ==== testAfter.log("testAfter")
    }

    def testPureOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      var actual     = before
      val testBefore = actual ==== before
      pureOf[Id]({ actual = after; () })
      val testAfter  = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfter.log("testAfter")
        )
      )
    }

    def testUnitOf: Result = {
      val expected: Unit = ()
      val actual         = unitOf[Id]
      actual ==== expected
    }

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      lazy val actual = errorOf[Id][Unit](expectedError)

      expectThrowable(actual, expectedError)
    }

  }

}
