package effectie.core

import cats.syntax.all._
import effectie.testing.tools
import effectie.testing.types.SomeThrowableError
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog._
import hedgehog.runner._

import scala.util.Try

/** @author Kevin Lee
  * @since 2022-01-06
  */
object FxCtorSpec extends Properties {

  implicit private val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = futureSpecs

  val futureSpecs = List(
    property("test FxCtor[Future].effectOf", FutureSpec.testEffectOf),
    property("test FxCtor[Future].pureOf", FutureSpec.testPureOf),
    property("test FxCtor[Future].pureOrCatchNonFatal(success case)", FutureSpec.testPureOrErrorSuccessCase),
    example("test FxCtor[Future].pureOrCatchNonFatal(error case)", FutureSpec.testPureOrErrorErrorCase),
    example("test FxCtor[Future].unitOf", FutureSpec.testUnitOf),
    example("test FxCtor[Future].errorOf", FutureSpec.testErrorOf),
    property("test FxCtor[Future].fromEither(Right)", FutureSpec.testFromEitherRightCase),
    property("test FxCtor[Future].fromEither(Left)", FutureSpec.testFromEitherLeftCase),
    property("test FxCtor[Future].fromOption(Some)", FutureSpec.testFromOptionSomeCase),
    property("test FxCtor[Future].fromOption(None)", FutureSpec.testFromOptionNoneCase),
    property("test FxCtor[Future].fromTry(Success)", FutureSpec.testFromTrySuccessCase),
    property("test FxCtor[Future].fromTry(Failure)", FutureSpec.testFromTryFailureCase),
  )

  object FutureSpec {
    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration._
    import scala.concurrent.{ExecutionContext, Future}

    val waitFor = WaitFor(1.second)

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual               = before // scalafix:ok DisableSyntax.var
      val testBefore           = actual ==== before
      val future: Future[Unit] = FxCtor[Future].effectOf({ actual = after; () })
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
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual       = before // scalafix:ok DisableSyntax.var
      val testBefore   = actual ==== before
      val future       = FxCtor[Future].pureOf({ actual = after; () })
      ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)
      val testAfterRun = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfterRun.log("testAfterRun")
        )
      )
    }

    def testPureOrErrorSuccessCase: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual       = before // scalafix:ok DisableSyntax.var
      val testBefore   = actual ==== before
      val future       = FxCtor[Future].pureOrError({ actual = after; () })
      ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)
      val testAfterRun = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfterRun.log("testAfterRun")
        )
      )
    }

    def testPureOrErrorErrorCase: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val future = FxCtor[Future].pureOrError[Unit](throw expectedError) // scalafix:ok DisableSyntax.throw
      tools.expectThrowable(
        ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future),
        expectedError
      )
    }

    def testUnitOf: Result = {
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)
      val future                                    = FxCtor[Future].unitOf
      val expected: Unit                            = ()
      val actual: Unit = ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)
      actual ==== expected
    }

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val future = FxCtor[Future].errorOf[Unit](expectedError)
      tools.expectThrowable(
        ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future),
        expectedError
      )
    }

    def testFromEitherRightCase: Property = for {
      n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
    } yield {
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      val expected            = n.asRight[SomeThrowableError]
      val future: Future[Int] = FxCtor[Future].fromEither(expected)

      val actual = Try(ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)).toEither
      (actual ==== expected).log(s"$actual does not equal to $expected")
    }

    def testFromEitherLeftCase: Property = for {
      errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
    } yield {
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      val expected            = SomeThrowableError.message(errorMessage).asLeft[Int]
      val future: Future[Int] = FxCtor[Future].fromEither(expected)

      val actual = Try(ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)).toEither
      (actual ==== expected).log(s"$actual does not equal to $expected")
    }

    def testFromOptionSomeCase: Property = for {
      n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
    } yield {
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      val expected            = n.asRight[SomeThrowableError]
      val input               = n.some
      val future: Future[Int] =
        FxCtor[Future].fromOption(input)(SomeThrowableError.Message("This should never happen!"))

      val actual = Try(ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)).toEither
      (actual ==== expected).log(s"$actual does not equal to $expected")
    }

    def testFromOptionNoneCase: Property = for {
      errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
    } yield {
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      val expected            = SomeThrowableError.message(errorMessage).asLeft[Int]
      val future: Future[Int] = FxCtor[Future].fromOption(none[Int])(SomeThrowableError.Message(errorMessage))

      val actual = Try(ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)).toEither
      (actual ==== expected).log(s"$actual does not equal to $expected")
    }

    def testFromTrySuccessCase: Property = for {
      n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
    } yield {
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      val expected            = n.asRight[SomeThrowableError]
      val input: Try[Int]     = scala.util.Success(n)
      val future: Future[Int] = FxCtor[Future].fromTry(input)

      val actual = Try(ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)).toEither
      (actual ==== expected).log(s"$actual does not equal to $expected")
    }

    def testFromTryFailureCase: Property = for {
      errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
    } yield {
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      val expected            = SomeThrowableError.message(errorMessage).asLeft[Int]
      val input: Try[Int]     = scala.util.Failure(SomeThrowableError.message(errorMessage))
      val future: Future[Int] = FxCtor[Future].fromTry(input)

      val actual = Try(ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)).toEither
      (actual ==== expected).log(s"$actual does not equal to $expected")
    }

  }

}
