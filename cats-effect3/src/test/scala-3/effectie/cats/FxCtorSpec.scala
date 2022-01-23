package effectie.cats

import cats.Id
import cats.effect.*
import effectie.cats.CatsEffectRunner.TestContext
import cats.effect.unsafe.IORuntime
import effectie.core.FxCtor
import effectie.cats.compat.CatsEffectIoCompatForFuture
import effectie.testing.tools
import effectie.testing.types.SomeThrowableError
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog.*
import hedgehog.runner.*

/** @author Kevin Lee
  * @since 2020-12-06
  */
object FxCtorSpec extends Properties {
  override def tests: List[Test] = ioSpecs ++ futureSpecs ++ idSpecs
  val ioSpecs = List(
    property("test FxCtor[IO].effectOf", IoSpec.testEffectOf),
    property("test FxCtor[IO].pureOf", IoSpec.testPureOf),
    example("test FxCtor[IO].unitOf", IoSpec.testUnitOf),
    example("test FxCtor[IO].errorOf", IoSpec.testErrorOf),
  )
  
  val futureSpecs = effectie.core.FxCtorSpec.futureSpecs

  val idSpecs = List(
    property("test FxCtor[Id].effectOf", IdSpec.testEffectOf),
    property("test FxCtor[Id].pureOf", IdSpec.testPureOf),
    example("test FxCtor[Id].unitOf", IdSpec.testUnitOf),
    example("test FxCtor[Id].errorOf", IdSpec.testErrorOf)
  )

  object IoSpec {
    import effectie.cats.Fx.given

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {

      var actual        = before
      val testBefore    = actual ==== before
      val io            = FxCtor[IO].effectOf({
        actual = after; ()
      })
      val testBeforeRun = actual ==== before

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())
      val runResult = io.completeAs(())

      val testAfterRun  = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testBeforeRun.log("testBeforeRun"),
          runResult.log("runResult"),
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
      val io            = FxCtor[IO].pureOf({
        actual = after; ()
      })
      val testBeforeRun = actual ==== after
      import effectie.cats.CatsEffectRunner.*

      given ticket: Ticker = Ticker(TestContext())
      val runResult = io.completeAs(())

      val testAfterRun  = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testBeforeRun.log("testBeforeRun"),
          runResult.log("runResult"),
          testAfterRun.log("testAfterRun")
        )
      )
    }

    def testUnitOf: Result = {
      val actual             = FxCtor[IO].unitOf
      val expected: Unit = ()

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())
      actual.completeAs(expected)
    }


    def testErrorOf: Result = {
      import CatsEffectRunner.*

      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      given ticket: Ticker = Ticker(TestContext())

      val io = FxCtor[IO].errorOf(expectedError)

      io.expectError(expectedError)
    }

  }

  object IdSpec {
    import effectie.cats.Fx.given

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      var actual     = before
      val testBefore = actual ==== before
      FxCtor[Id].effectOf({
        actual = after; ()
      })
      val testAfter  = actual ==== after
      testBefore.log("testBefore") ==== testAfter.log("testAfter")
    }

    def testPureOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      var actual     = before
      val testBefore = actual ==== before
      FxCtor[Id].pureOf({
        actual = after; ()
      })
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
