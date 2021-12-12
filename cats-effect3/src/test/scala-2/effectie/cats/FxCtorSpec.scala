package effectie.cats

import cats.Id
import cats.effect._
import effectie.ConcurrentSupport
import effectie.testing.tools
import effectie.testing.types.SomeThrowableError
import hedgehog._
import hedgehog.runner._

/** @author Kevin Lee
  * @since 2020-12-06
  */
object FxCtorSpec extends Properties {
  override def tests: List[Test] = List(
    property("test FxCtor[IO].effectOf", IoSpec.testEffectOf),
    property("test FxCtor[IO].pureOf", IoSpec.testPureOf),
    example("test FxCtor[IO].unitOf", IoSpec.testUnitOf),
    example("test FxCtor[IO].errorOf", IoSpec.testErrorOf),
    property("test FxCtor[Future].effectOf", FutureSpec.testEffectOf),
    property("test FxCtor[Future].pureOf", FutureSpec.testPureOf),
    example("test FxCtor[Future].unitOf", FutureSpec.testUnitOf),
    example("test FxCtor[Future].testErrorOf", FutureSpec.testErrorOf),
    property("test FxCtor[Id].effectOf", IdSpec.testEffectOf),
    property("test FxCtor[Id].pureOf", IdSpec.testPureOf),
    example("test FxCtor[Id].unitOf", IdSpec.testUnitOf),
    example("test FxCtor[Id].testErrorOf", IdSpec.testErrorOf)
  )

  object IoSpec {
    import effectie.cats.Fx._

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual        = before
      val testBefore    = actual ==== before
      val io            = FxCtor[IO].effectOf({ actual = after; () })
      val testBeforeRun = actual ==== before

      import CatsEffectRunner._
      implicit val ticket: Ticker = Ticker(TestContext())
      io.completeAs(())

      val testAfterRun = actual ==== after
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

      import CatsEffectRunner._
      implicit val ticket: Ticker = Ticker(TestContext())
      io.completeAs(())

      val testAfterRun = actual ==== after
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

      import CatsEffectRunner._
      implicit val ticket: Ticker = Ticker(TestContext())

      io.completeAs(expected)
    }

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      import CatsEffectRunner._
      implicit val ticket: Ticker = Ticker(TestContext())

      val io = FxCtor[IO].errorOf[Unit](expectedError)
      io.expectError(expectedError)
    }

  }

  object FutureSpec {
    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration._
    import scala.concurrent.{ExecutionContext, Future}

    val waitFor: FiniteDuration = 1.second

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual               = before
      val testBefore           = actual ==== before
      val future: Future[Unit] = FxCtor[Future].effectOf({ actual = after; () })
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
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual       = before
      val testBefore   = actual ==== before
      val future       = FxCtor[Future].pureOf({ actual = after; () })
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
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)
      val future                                    = FxCtor[Future].unitOf
      val expected: Unit                            = ()
      val actual: Unit                              = ConcurrentSupport.futureToValueAndTerminate(future, waitFor)
      actual ==== expected
    }

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val future = FxCtor[Future].errorOf[Unit](expectedError)
      tools.expectThrowable(ConcurrentSupport.futureToValueAndTerminate(future, waitFor), expectedError)
    }

  }

  object IdSpec {

    import effectie.cats.Fx._

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
