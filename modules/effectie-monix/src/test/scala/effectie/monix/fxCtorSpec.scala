package effectie.monix

import cats.Id
import cats.effect.IO
import effectie.core._
import effectie.monix.fxCtor._
import effectie.testing.tools
import effectie.testing.types.SomeThrowableError
import extras.concurrent.testing.types.ErrorLogger
import hedgehog._
import hedgehog.runner._
import monix.eval.Task

/** @author Kevin Lee
  * @since 2020-12-06
  */
object fxCtorSpec extends Properties {
  private implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = ioSpecs ++ futureSpecs ++ idSpecs

  private val ioSpecs = List(
    property("test FxCtor[Task].effectOf", TaskSpec.testEffectOf),
    property("test FxCtor[Task].pureOf", TaskSpec.testPureOf),
    example("test FxCtor[Task].unitOf", TaskSpec.testUnitOf),
    example("test FxCtor[Task].errorOf", TaskSpec.testErrorOf),
    property("test FxCtor[IO].effectOf", IoSpec.testEffectOf),
    property("test FxCtor[IO].pureOf", IoSpec.testPureOf),
    example("test FxCtor[IO].unitOf", IoSpec.testUnitOf),
    example("test FxCtor[IO].errorOf", IoSpec.testErrorOf),
  )

  private val futureSpecs = effectie.core.FxCtorSpec.futureSpecs

  private val idSpecs = List(
    property("test FxCtor[Id].effectOf", IdSpec.testEffectOf),
    property("test FxCtor[Id].pureOf", IdSpec.testPureOf),
    example("test FxCtor[Id].unitOf", IdSpec.testUnitOf),
    example("test FxCtor[Id].errorOf", IdSpec.testErrorOf)
  )

  object TaskSpec {
    import monix.execution.Scheduler.Implicits.global

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual        = before
      val testBefore    = actual ==== before
      val task          = FxCtor[Task].effectOf({ actual = after; () })
      val testBeforeRun = actual ==== before
      task.runSyncUnsafe()
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
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual        = before
      val testBefore    = actual ==== before
      val task          = FxCtor[Task].pureOf({ actual = after; () })
      val testBeforeRun = actual ==== after
      task.runSyncUnsafe()
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
      val task           = FxCtor[Task].unitOf
      val expected: Unit = ()
      val actual: Unit   = task.runSyncUnsafe()
      actual ==== expected
    }

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      val task = FxCtor[Task].errorOf[Unit](expectedError)
      tools.expectThrowable(task.runSyncUnsafe(), expectedError)
    }

  }

  object IoSpec {

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual        = before
      val testBefore    = actual ==== before
      val io            = FxCtor[IO].effectOf({ actual = after; () })
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
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual        = before
      val testBefore    = actual ==== before
      val io            = FxCtor[IO].pureOf({ actual = after; () })
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
      val io             = FxCtor[IO].unitOf
      val expected: Unit = ()
      val actual: Unit   = io.unsafeRunSync()
      actual ==== expected
    }

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      val io = FxCtor[IO].errorOf[Unit](expectedError)

      tools.expectThrowable(io.unsafeRunSync(), expectedError)
    }

  }

  object IdSpec {

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual     = before
      val testBefore = actual ==== before
      FxCtor[Id].effectOf({ actual = after; () })
      val testAfter  = actual ==== after
      testBefore.log("testBefore") ==== testAfter.log("testAfter")
    }

    def testPureOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual     = before
      val testBefore = actual ==== before
      FxCtor[Id].pureOf({ actual = after; () })
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
      val actual         = FxCtor[Id].unitOf
      actual ==== expected
    }

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      lazy val actual = FxCtor[Id].errorOf[Unit](expectedError)
      tools.expectThrowable(actual, expectedError)
    }

  }

}
