package effectie.syntax

import cats.Id
import cats.effect.IO
import effectie.cats.Fx.given
import effectie.syntax.fx.*
import effectie.testing.tools.*
import effectie.testing.types.SomeThrowableError
import effectie.{Fx, FxCtor}
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog.*
import hedgehog.runner.*

/** @author Kevin Lee
  * @since 2021-05-16
  */
object fxSpec extends Properties {
  override def tests: List[Test] = List(
    property("test fx.{effectOf, pureOf, unitOf} for IO", IoSpec.testAll),
    property("test fx.effectOf[IO]", IoSpec.testEffectOf),
    property("test fx.pureOf[IO]", IoSpec.testPureOf),
    example("test fx.unitOf[IO]", IoSpec.testUnitOf),
    example("test fx.errorOf[IO]", IoSpec.testErrorOf),
    property("test fx.{effectOf, pureOf, unitOf} for Future", FutureSpec.testAll),
    property("test fx.effectOf[Future]", FutureSpec.testEffectOf),
    property("test fx.pureOf[Future]", FutureSpec.testPureOf),
    example("test fx.unitOf[Future]", FutureSpec.testUnitOf),
    example("test fx.errorOf[Future]", FutureSpec.testErrorOf),
    property("test fx.{effectOf, pureOf, unitOf} for Id", IdSpec.testAll),
    property("test fx.effectOf[Id]", IdSpec.testEffectOf),
    property("test fx.pureOf[Id]", IdSpec.testPureOf),
    example("test fx.unitOf[Id]", IdSpec.testUnitOf),
    example("test fx.errorOf[Id]", IdSpec.testErrorOf)
  )

  trait FxCtorClient[F[*]] {
    def eftOf[A](a: A): F[A]
    def of[A](a: A): F[A]
    def unit: F[Unit]
  }
  object FxCtorClient {
    def apply[F[*]: FxCtorClient]: FxCtorClient[F] = summon[FxCtorClient[F]]
    given eftClientF[F[*]: FxCtor]: FxCtorClient[F] with {
      override def eftOf[A](a: A): F[A] = effectOf(a)
      override def of[A](a: A): F[A]    = pureOf(a)
      override def unit: F[Unit]        = unitOf
    }
  }

  trait FxClient[F[*]] {
    def eftOf[A](a: A): F[A]
    def of[A](a: A): F[A]
    def unit: F[Unit]
  }
  object FxClient {
    def apply[F[*]: FxClient]: FxClient[F] =
      summon[FxClient[F]]
    given eftClientF[F[*]: Fx]: FxClient[F] with {
      override def eftOf[A](a: A): F[A] = effectOf(a)
      override def of[A](a: A): F[A]    = pureOf(a)
      override def unit: F[Unit]        = unitOf
    }
  }

  object IoSpec {

    def testAll: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      var actual                  = before
      var actual2                 = before
      val testBefore              = actual ==== before
      val testBefore2             = actual2 ==== before
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
      val testBeforeRun           = actual ==== before
      val testBeforeRun2          = actual2 ==== before
      io.unsafeRunSync()
      val testAfterRun            = actual ==== after
      val testAfterRun2           = actual2 ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testBeforeRun.log("testBeforeRun"),
          testAfterRun.log("testAfterRun"),
          testBefore2.log("testBefore2"),
          testBeforeRun2.log("testBeforeRun2"),
          testAfterRun2.log("testAfterRun2")
        )
      )
    }

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      var actual        = before
      val testBefore    = actual ==== before
      val io            = effectOf[IO]({ actual = after; () })
      val testBeforeRun = actual ==== before
      io.unsafeRunSync()
      val testAfterRun  = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testBeforeRun.log("testBeforeRun"),
          testAfterRun.log("testAfterRun")
        )
      )
    }

    def testPureOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      var actual        = before
      val testBefore    = actual ==== before
      val io            = pureOf[IO]({ actual = after; () })
      val testBeforeRun = actual ==== after
      io.unsafeRunSync()
      val testAfterRun  = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testBeforeRun.log("testBeforeRun"),
          testAfterRun.log("testAfterRun")
        )
      )
    }

    def testUnitOf: Result = {
      val io             = unitOf[IO]
      val expected: Unit = ()
      val actual: Unit   = io.unsafeRunSync()
      actual ==== expected
    }

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      val io = errorOf[IO][Unit](expectedError)

      expectThrowable(
        io.unsafeRunSync(),
        expectedError
      )
    }

  }

  object FutureSpec {

    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration.*
    import scala.concurrent.{ExecutionContext, Future}

    private implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

    private val waitFor = WaitFor(1.second)

    def testAll: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

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
      ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)
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
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      var actual               = before
      val testBefore           = actual ==== before
      val future: Future[Unit] = effectOf[Future]({ actual = after; () })
      ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)
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
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      var actual       = before
      val testBefore   = actual ==== before
      val future       = pureOf[Future]({ actual = after; () })
      ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)
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
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)
      val future                             = unitOf[Future]
      val expected: Unit                     = ()
      val actual: Unit = ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)
      actual ==== expected
    }

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)
      val future                             = errorOf[Future][Unit](expectedError)

      expectThrowable(
        ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future),
        expectedError
      )
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
