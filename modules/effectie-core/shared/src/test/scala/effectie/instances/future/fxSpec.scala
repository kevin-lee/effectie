package effectie.instances.future

import cats.Eq
import cats.syntax.all._
import effectie.core.Fx
import effectie.testing.cats.MonadSpec
import effectie.testing.tools.expectThrowable
import effectie.testing.types.{SomeError, SomeThrowableError}
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog._
import hedgehog.runner._

import scala.concurrent.Await
import scala.util.Try

/** @author Kevin Lee
  * @since 2022-01-06
  */
object fxSpec extends Properties {
  implicit private val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = futureSpecs(getClass.getName.stripSuffix("$"))

  /* Future */
  def futureSpecs(name: String): List[Test] = List(
    property(s"from $name: test Fx[Future].effectOf", FutureSpec.testEffectOf),
    property(s"from $name: test Fx[Future].fromEffect(effectOf)", FutureSpec.testFromEffect),
    property(s"from $name: test Fx[Future].fromEffect(pureOf)", FutureSpec.testFromEffectWithPure),
    property(s"from $name: test Fx[Future].pureOf", FutureSpec.testPureOf),
    property(s"from $name: test Fx[Future].pureOrError(success case)", FutureSpec.testPureOrErrorSuccessCase),
    example(s"from $name: test Fx[Future].pureOrError(error case)", FutureSpec.testPureOrErrorErrorCase),
    example(s"from $name: test Fx[Future].unitOf", FutureSpec.testUnitOf),
    example(s"from $name: test Fx[Future].errorOf", FutureSpec.testErrorOf),
    property(s"from $name: test fx.pureOfOption[Future]", FutureSpec.testPureOfOption),
    property(s"from $name: test fx.pureOfSome[Future]", FutureSpec.testPureOfSome),
    example(s"from $name: test fx.pureOfNone[Future]", FutureSpec.testPureOfNone),
    property(s"from $name: test fx.pureOfRight[Future]", FutureSpec.testPureOfRight),
    property(s"from $name: test fx.pureOfLeft[Future]", FutureSpec.testPureOfLeft),
    property(s"from $name: test Fx[Future].fromEither(Right)", FutureSpec.testFromEitherRightCase),
    property(s"from $name: test Fx[Future].fromEither(Left)", FutureSpec.testFromEitherLeftCase),
    property(s"from $name: test Fx[Future].fromOption(Some)", FutureSpec.testFromOptionSomeCase),
    property(s"from $name: test Fx[Future].fromOption(None)", FutureSpec.testFromOptionNoneCase),
    property(s"from $name: test Fx[Future].fromTry(Success)", FutureSpec.testFromTrySuccessCase),
    property(s"from $name: test Fx[Future].fromTry(Failure)", FutureSpec.testFromTryFailureCase),
    property(s"from $name: test Fx[Future].flatMapFa(Future[A])(A => IO[B])", FutureSpec.testFlatMapFx),
  ) ++
    FutureSpec.testMonadLaws ++
    List(
      /* Future */
      example(
        "test Fx[Future]catchNonFatalThrowable should catch NonFatal",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalThrowableShouldCatchNonFatal,
      ),
      example(
        "test Fx[Future]catchNonFatalThrowable should return the successful result",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalThrowableShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Future]catchNonFatal should catch NonFatal",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalShouldCatchNonFatal,
      ),
      example(
        "test Fx[Future]catchNonFatal should return the successful result",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Future]catchNonFatalEither should catch NonFatal",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalEitherShouldCatchNonFatal,
      ),
      example(
        "test Fx[Future]catchNonFatalEither should return the successful result",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalEitherShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Future]catchNonFatalEither should return the failed result",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalEitherShouldReturnFailedResult,
      ),
    ) ++ List(
      example(
        "test Fx[Future].handleNonFatalWith should handle NonFatal",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleNonFatalWithShouldHandleNonFatalWith,
      ),
      example(
        "test Fx[Future].handleNonFatalWith should return the successful result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleNonFatalWithShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Future].handleNonFatalWithEither should handle NonFatal",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleNonFatalWithEitherShouldHandleNonFatalWith,
      ),
      example(
        "test Fx[Future].handleNonFatalWithEither should return the successful result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleNonFatalWithEitherShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Future].handleNonFatalWithEither should return the failed result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleNonFatalWithEitherShouldReturnFailedResult,
      ),
      example(
        "test Fx[Future].handleEitherNonFatalWith should handle NonFatal",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleEitherNonFatalWithShouldHandleNonFatalWith,
      ),
      example(
        "test Fx[Future].handleEitherNonFatalWith should return the successful result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleEitherNonFatalWithShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Future].handleEitherNonFatalWith should return the failed result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleEitherNonFatalWithShouldReturnFailedResult,
      ),
      example(
        "test Fx[Future].handleNonFatal should handle NonFatal",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleNonFatalShouldHandleNonFatal,
      ),
      example(
        "test Fx[Future].handleNonFatal should return the successful result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Future].handleNonFatalEither should handle NonFatal",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleNonFatalEitherShouldHandleNonFatal,
      ),
      example(
        "test Fx[Future].handleNonFatalEither should return the successful result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleNonFatalEitherShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Future].handleNonFatalEither should return the failed result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleNonFatalEitherShouldReturnFailedResult,
      ),
      example(
        "test Fx[Future].handleEitherNonFatal should handle NonFatal",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleEitherNonFatalShouldHandleNonFatal,
      ),
      example(
        "test Fx[Future].handleEitherNonFatal should return the successful result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleEitherNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Future].handleEitherNonFatal should return the failed result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleEitherNonFatalShouldReturnFailedResult,
      ),
    ) ++ List(
      example(
        "test Fx[Future].recoverFromNonFatalWith should catch NonFatal",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverFromNonFatalWithShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Future].recoverFromNonFatalWith should return the successful result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverFromNonFatalWithShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Future].recoverFromNonFatalWithEither should catch NonFatal",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Future].recoverFromNonFatalWithEither should return the successful result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Future].recoverFromNonFatalWithEither should return the failed result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverFromNonFatalWithEitherShouldReturnFailedResult,
      ),
      example(
        "test Fx[Future].recoverEitherFromNonFatalWith should catch NonFatal",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Future].recoverEitherFromNonFatalWith should return the successful result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Future].recoverEitherFromNonFatalWith should return the failed result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverEitherFromNonFatalWithShouldReturnFailedResult,
      ),
      example(
        "test Fx[Future].recoverFromNonFatal should catch NonFatal",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverFromNonFatalShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Future].recoverFromNonFatal should return the successful result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverFromNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Future].recoverFromNonFatalEither should catch NonFatal",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverFromNonFatalEitherShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Future].recoverFromNonFatalEither should return the successful result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverFromNonFatalEitherShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Future].recoverFromNonFatalEither should return the failed result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverFromNonFatalEitherShouldReturnFailedResult,
      ),
      example(
        "test Fx[Future].recoverEitherFromNonFatal should catch NonFatal",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverEitherFromNonFatalShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Future].recoverEitherFromNonFatal should return the successful result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverEitherFromNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Future].recoverEitherFromNonFatal should return the failed result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverEitherFromNonFatalShouldReturnFailedResult,
      ),
    )

  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: Fx, A](a: => A): F[A] =
    Fx[F].effectOf(a)

  object FutureSpec {
    import effectie.instances.future.fx._

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
      val future: Future[Unit] = Fx[Future].effectOf({ actual = after; () })
      ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)
      val testAfterRun         = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfterRun.log("testAfterRun"),
        )
      )
    }

    def testFromEffect: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual     = before // scalafix:ok DisableSyntax.var
      val testBefore = actual ==== before

      lazy val fromFuture = Fx[Future].fromEffect(Fx[Future].effectOf({ actual = after; () }))

      val testAfterFrom = actual ==== before
      ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(fromFuture)
      val testAfterRun  = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfterFrom.log("testAfterFrom"),
          testAfterRun.log("testAfterRun"),
        )
      )
    }

    def testFromEffectWithPure: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual       = before // scalafix:ok DisableSyntax.var
      val testBefore   = actual ==== before
      val fromFuture   = Fx[Future].fromEffect(Fx[Future].pureOf({ actual = after; () }))
      ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(fromFuture)
      val testAfterRun = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfterRun.log("testAfterRun"),
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
      val future       = Fx[Future].pureOf({ actual = after; () })
      ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)
      val testAfterRun = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfterRun.log("testAfterRun"),
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
      val future       = Fx[Future].pureOrError({ actual = after; () })
      ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)
      val testAfterRun = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfterRun.log("testAfterRun"),
        )
      )
    }

    def testPureOrErrorErrorCase: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val future = Fx[Future].pureOrError[Unit](throw expectedError) // scalafix:ok DisableSyntax.throw
      expectThrowable(ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future), expectedError)
    }

    def testUnitOf: Result = {
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)
      val future                                    = Fx[Future].unitOf
      val expected: Unit                            = ()
      val actual: Unit = ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)
      actual ==== expected
    }

    def testPureOfOption: Property =
      for {
        s <- Gen
               .string(Gen.unicode, Range.linear(1, 10))
               .option
               .log("s")
      } yield {
        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expected = s
        val input    = s.orNull
        val future   = Fx[Future].pureOfOption(input)
        val actual   = ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)

        Result.all(
          List(
            actual ==== expected
          )
        )
      }

    def testPureOfSome: Property = for {
      s <- Gen
             .string(Gen.unicode, Range.linear(1, 10))
             .log("s")
    } yield {
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expected = s.some
      val future   = Fx[Future].pureOfSome(s)
      val actual   = ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)

      Result.all(
        List(
          actual ==== expected
        )
      )
    }

    def testPureOfNone: Result = {
      val expected = none[String]

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val future = Fx[Future].pureOfNone[String]
      val actual = ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)

      actual ==== expected
    }

    def testPureOfRight: Property =
      for {
        n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
      } yield {
        val expected = n.asRight[String]

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val future = Fx[Future].pureOfRight[String](n)
        val actual = ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)

        Result.all(
          List(
            actual ==== expected
          )
        )
      }

    def testPureOfLeft: Property =
      for {
        s <- Gen
               .string(Gen.unicode, Range.linear(1, 10))
               .log("s")
      } yield {
        val expected = s.asLeft[Int]

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val future = Fx[Future].pureOfLeft[Int](s)
        val actual = ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)

        Result.all(
          List(
            actual ==== expected
          )
        )
      }

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val future = Fx[Future].errorOf[Unit](expectedError)
      expectThrowable(ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future), expectedError)
    }

    def testFromEitherRightCase: Property = for {
      n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
    } yield {
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      val expected            = n.asRight[SomeThrowableError]
      val future: Future[Int] = Fx[Future].fromEither(expected)

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
      val future: Future[Int] = Fx[Future].fromEither(expected)

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
      val future: Future[Int] = Fx[Future].fromOption(input)(SomeThrowableError.Message("This should never happen!"))

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
      val future: Future[Int] = Fx[Future].fromOption(none[Int])(SomeThrowableError.Message(errorMessage))

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
      val future: Future[Int] = Fx[Future].fromTry(input)

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
      val future: Future[Int] = Fx[Future].fromTry(input)

      val actual = Try(ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)).toEither
      (actual ==== expected).log(s"$actual does not equal to $expected")
    }

    def testFlatMapFx: Property =
      for {
        n      <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
        prefix <- Gen.constant("n is ").log("prefix")
      } yield {
        val expected = prefix + n.toString

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa     = Fx[Future].pureOf(n)
        val fb     = Fx[Future].flatMapFa(fa)(n => Fx[Future].pureOf(prefix + n.toString))
        val actual = Try(ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(fb)).toEither
        (actual ==== expected.asRight).log(s"$actual does not equal to $expected")
      }

    def testMonadLaws: List[Test] = {

      implicit val ec: scala.concurrent.ExecutionContext             = scala.concurrent.ExecutionContext.global
      implicit def futureEqual[A](implicit EQ: Eq[A]): Eq[Future[A]] = new Eq[Future[A]] {
        override def eqv(x: Future[A], y: Future[A]): Boolean =
          Await.result(x.flatMap(a => y.map(b => EQ.eqv(a, b))), 1.second)
      }

//      implicit val eqFuture: Eq[Future[Int]] =
//        (x, y) => {
//          val future = x.flatMap(xx => y.map(_ === xx))
//          Await.result(future, waitFor.waitFor)
//        }

      MonadSpec.testAllLaws[Future]("Fx[Future]")
    }

    object CanCatchSpec {
      import java.util.concurrent.{ExecutorService, Executors}
      import scala.concurrent.duration._
      import scala.concurrent.{ExecutionContext, Future}

      val waitFor = WaitFor(1.second)

      def testCanCatch_Future_catchNonFatalThrowableShouldCatchNonFatal: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion                       = new RuntimeException("Something's wrong")
        val expected: Either[RuntimeException, Int] = Left(expectedExpcetion)

        val fa     = run[Future, Int](throwThrowable[Int](expectedExpcetion))
        val actual = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(Fx[Future].catchNonFatalThrowable(fa))

        actual ==== expected
      }

      def testCanCatch_Future_catchNonFatalShouldCatchNonFatal: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion                = new RuntimeException("Something's wrong")
        val expected: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))

        val fa     = run[Future, Int](throwThrowable[Int](expectedExpcetion))
        val actual = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(Fx[Future].catchNonFatal(fa)(SomeError.someThrowable))

        actual ==== expected
      }

      def testCanCatch_Future_catchNonFatalThrowableShouldReturnSuccessfulResult: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa                               = run[Future, Int](1)
        val expected: Either[Throwable, Int] = Right(1)
        val actual                           = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(Fx[Future].catchNonFatalThrowable(fa))

        actual ==== expected
      }

      def testCanCatch_Future_catchNonFatalShouldReturnSuccessfulResult: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa                               = run[Future, Int](1)
        val expected: Either[SomeError, Int] = Right(1)
        val actual                           = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(Fx[Future].catchNonFatal(fa)(SomeError.someThrowable))

        actual ==== expected
      }

      def testCanCatch_Future_catchNonFatalEitherShouldCatchNonFatal: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))
        val actual                           = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(Fx[Future].catchNonFatalEither(fa)(SomeError.someThrowable))

        actual ==== expected
      }

      def testCanCatch_Future_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa                               = run[Future, Either[SomeError, Int]](Right(1))
        val expected: Either[SomeError, Int] = Right(1)
        val actual                           = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(Fx[Future].catchNonFatalEither(fa)(SomeError.someThrowable))

        actual ==== expected
      }

      def testCanCatch_Future_catchNonFatalEitherShouldReturnFailedResult: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedFailure                  = SomeError.message("Failed")
        val fa                               = run[Future, Either[SomeError, Int]](Left(expectedFailure))
        val expected: Either[SomeError, Int] = Left(expectedFailure)
        val actual                           = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(Fx[Future].catchNonFatalEither(fa)(SomeError.someThrowable))

        actual ==== expected
      }

    }

    object CanHandleErrorSpec {
      import java.util.concurrent.{ExecutorService, Executors}
      import scala.concurrent.duration._
      import scala.concurrent.{ExecutionContext, Future}

      val waitFor = WaitFor(1.second)

      def testCanHandleError_Future_handleNonFatalWithShouldHandleNonFatalWith: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 1
        val actual            = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(Fx[Future].handleNonFatalWith(fa)(_ => Future(expected)))

        actual ==== expected
      }

      def testCanHandleError_Future_handleNonFatalWithShouldReturnSuccessfulResult: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa       = run[Future, Int](1)
        val expected = 1
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(Fx[Future].handleNonFatalWith(fa)(_ => Future(123)))

        actual ==== expected
      }

      def testCanHandleError_Future_handleNonFatalWithEitherShouldHandleNonFatalWith: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))
        val actualFailedResult                           =
          ConcurrentSupport.futureToValue(
            Fx[Future].handleNonFatalWith(fa)(err => Future(Left(SomeError.someThrowable(err)))),
            waitFor,
          )

        val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected: Either[SomeError, Int] = Right(1)
        val actual                           = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(Fx[Future].handleNonFatalWith(fa2)(_ => Future(expected)))

        expectedFailedResult ==== actualFailedResult and actual ==== expected
      }

      def testCanHandleError_Future_handleNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa                               = run[Future, Either[SomeError, Int]](Right(1))
        val expected: Either[SomeError, Int] = Right(1)
        val actual                           =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor,
          )(Fx[Future].handleNonFatalWith(fa)(err => Future(Left(SomeError.someThrowable(err)))))

        actual ==== expected
      }

      def testCanHandleError_Future_handleNonFatalWithEitherShouldReturnFailedResult: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedFailure                  = SomeError.message("Failed")
        val fa                               = run[Future, Either[SomeError, Int]](Left(expectedFailure))
        val expected: Either[SomeError, Int] = Left(expectedFailure)
        val actual                           = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(Fx[Future].handleNonFatalWith(fa)(_ => Future(Right(1))))

        actual ==== expected
      }

      def testCanHandleError_Future_handleEitherNonFatalWithShouldHandleNonFatalWith: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))
        val actualFailedResult                           = ConcurrentSupport.futureToValue(
          Fx[Future]
            .handleEitherNonFatalWith(fa)(err => Future(Left(SomeError.someThrowable(err)))),
          waitFor,
        )

        val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected: Either[SomeError, Int] = Right(1)
        val actual                           =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor,
          )(Fx[Future].handleEitherNonFatalWith(fa2)(_ => Future(expected)))

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      def testCanHandleError_Future_handleEitherNonFatalWithShouldReturnSuccessfulResult: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa                               = run[Future, Either[SomeError, Int]](Right(1))
        val expected: Either[SomeError, Int] = Right(1)
        val actual                           = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(Fx[Future].handleEitherNonFatalWith(fa)(err => Future(Left(SomeError.someThrowable(err)))))

        actual ==== expected
      }

      def testCanHandleError_Future_handleEitherNonFatalWithShouldReturnFailedResult: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedFailure                  = SomeError.message("Failed")
        val fa                               = run[Future, Either[SomeError, Int]](Left(expectedFailure))
        val expected: Either[SomeError, Int] = Left(expectedFailure)
        val actual                           =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor,
          )(Fx[Future].handleEitherNonFatalWith(fa)(_ => Future(expected)))

        actual ==== expected
      }

      def testCanHandleError_Future_handleNonFatalShouldHandleNonFatal: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 1
        val actual            = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(Fx[Future].handleNonFatal(fa)(_ => expected))

        actual ==== expected
      }

      def testCanHandleError_Future_handleNonFatalShouldReturnSuccessfulResult: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa       = run[Future, Int](1)
        val expected = 1
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(Fx[Future].handleNonFatal(fa)(_ => 123))

        actual ==== expected
      }

      def testCanHandleError_Future_handleNonFatalEitherShouldHandleNonFatal: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))
        val actualFailedResult                           =
          ConcurrentSupport.futureToValue(
            Fx[Future].handleNonFatal(fa)(err => Left(SomeError.someThrowable(err))),
            waitFor,
          )

        val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected: Either[SomeError, Int] = Right(1)
        val actual                           = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(Fx[Future].handleNonFatal(fa2)(_ => expected))

        expectedFailedResult ==== actualFailedResult and actual ==== expected
      }

      def testCanHandleError_Future_handleNonFatalEitherShouldReturnSuccessfulResult: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa                               = run[Future, Either[SomeError, Int]](Right(1))
        val expected: Either[SomeError, Int] = Right(1)
        val actual                           =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor,
          )(Fx[Future].handleNonFatal(fa)(err => Left(SomeError.someThrowable(err))))

        actual ==== expected
      }

      def testCanHandleError_Future_handleNonFatalEitherShouldReturnFailedResult: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedFailure                  = SomeError.message("Failed")
        val fa                               = run[Future, Either[SomeError, Int]](Left(expectedFailure))
        val expected: Either[SomeError, Int] = Left(expectedFailure)
        val actual                           = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(Fx[Future].handleNonFatal(fa)(_ => Right(1)))

        actual ==== expected
      }

      def testCanHandleError_Future_handleEitherNonFatalShouldHandleNonFatal: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))
        val actualFailedResult                           = ConcurrentSupport.futureToValue(
          Fx[Future].handleEitherNonFatal(fa)(err => Left(SomeError.someThrowable(err))),
          waitFor,
        )

        val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected: Either[SomeError, Int] = Right(1)
        val actual                           =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor,
          )(Fx[Future].handleEitherNonFatal(fa2)(_ => expected))

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      def testCanHandleError_Future_handleEitherNonFatalShouldReturnSuccessfulResult: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa                               = run[Future, Either[SomeError, Int]](Right(1))
        val expected: Either[SomeError, Int] = Right(1)
        val actual                           = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(Fx[Future].handleEitherNonFatal(fa)(err => Left(SomeError.someThrowable(err))))

        actual ==== expected
      }

      def testCanHandleError_Future_handleEitherNonFatalShouldReturnFailedResult: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedFailure                  = SomeError.message("Failed")
        val fa                               = run[Future, Either[SomeError, Int]](Left(expectedFailure))
        val expected: Either[SomeError, Int] = Left(expectedFailure)
        val actual                           =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor,
          )(Fx[Future].handleEitherNonFatal(fa)(_ => expected))

        actual ==== expected
      }

    }

    object CanRecoverSpec {
      import java.util.concurrent.{ExecutorService, Executors}
      import scala.concurrent.duration._
      import scala.concurrent.{ExecutionContext, Future}
      import scala.util.control.NonFatal

      val waitFor = WaitFor(1.second)

      def testCanRecover_Future_recoverFromNonFatalWithShouldRecoverFromNonFatal: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 1
        val actual            = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(Fx[Future].recoverFromNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) => Future(expected)
        })

        actual ==== expected
      }

      def testCanRecover_Future_recoverFromNonFatalWithShouldReturnSuccessfulResult: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa       = run[Future, Int](1)
        val expected = 1
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(Fx[Future].recoverFromNonFatalWith(fa) {
          case NonFatal(_) => Future(123)
        })

        actual ==== expected
      }

      def testCanRecover_Future_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))
        val actualFailedResult                           =
          ConcurrentSupport.futureToValue(
            Fx[Future].recoverFromNonFatalWith(fa) {
              case err => Future(Left(SomeError.someThrowable(err)))
            },
            waitFor,
          )

        val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected: Either[SomeError, Int] = Right(1)
        val actual                           =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor,
          )(Fx[Future].recoverFromNonFatalWith(fa2) {
            case NonFatal(`expectedExpcetion`) => Future(expected)
          })

        expectedFailedResult ==== actualFailedResult and actual ==== expected
      }

      def testCanRecover_Future_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa                               = run[Future, Either[SomeError, Int]](Right(1))
        val expected: Either[SomeError, Int] = Right(1)
        val actual                           =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor,
          )(Fx[Future].recoverFromNonFatalWith(fa) {
            case err => Future(Left(SomeError.someThrowable(err)))
          })

        actual ==== expected
      }

      def testCanRecover_Future_recoverFromNonFatalWithEitherShouldReturnFailedResult: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedFailure                  = SomeError.message("Failed")
        val fa                               = run[Future, Either[SomeError, Int]](Left(expectedFailure))
        val expected: Either[SomeError, Int] = Left(expectedFailure)
        val actual                           =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor,
          )(Fx[Future].recoverFromNonFatalWith(fa) {
            case NonFatal(_) => Future(Right(1))
          })

        actual ==== expected
      }

      def testCanRecover_Future_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))
        val actualFailedResult                           = ConcurrentSupport.futureToValue(
          Fx[Future]
            .recoverEitherFromNonFatalWith(fa) {
              case err => Future(Left(SomeError.someThrowable(err)))
            },
          waitFor,
        )

        val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected: Either[SomeError, Int] = Right(1)
        val actual                           =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor,
          )(
            Fx[Future]
              .recoverEitherFromNonFatalWith(fa2) {
                case err @ _ => Future(expected)
              }
          )

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      def testCanRecover_Future_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa                               = run[Future, Either[SomeError, Int]](Right(1))
        val expected: Either[SomeError, Int] = Right(1)
        val actual                           = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(
          Fx[Future]
            .recoverEitherFromNonFatalWith(fa) {
              case err => Future(Left(SomeError.someThrowable(err)))
            }
        )

        actual ==== expected
      }

      def testCanRecover_Future_recoverEitherFromNonFatalWithShouldReturnFailedResult: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedFailure                  = SomeError.message("Failed")
        val fa                               = run[Future, Either[SomeError, Int]](Left(expectedFailure))
        val expected: Either[SomeError, Int] = Left(expectedFailure)
        val actual                           =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor,
          )(
            Fx[Future]
              .recoverEitherFromNonFatalWith(fa) {
                case NonFatal(_) => Future(expected)
              }
          )

        actual ==== expected
      }

      // /

      def testCanRecover_Future_recoverFromNonFatalShouldRecoverFromNonFatal: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 1
        val actual            = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(Fx[Future].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expected })

        actual ==== expected
      }

      def testCanRecover_Future_recoverFromNonFatalShouldReturnSuccessfulResult: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa       = run[Future, Int](1)
        val expected = 1
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(Fx[Future].recoverFromNonFatal(fa) { case NonFatal(_) => 123 })

        actual ==== expected
      }

      def testCanRecover_Future_recoverFromNonFatalEitherShouldRecoverFromNonFatal: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))
        val actualFailedResult                           =
          ConcurrentSupport.futureToValue(
            Fx[Future].recoverFromNonFatal(fa) {
              case err => Left(SomeError.someThrowable(err))
            },
            waitFor,
          )

        val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected: Either[SomeError, Int] = Right(1)
        val actual                           = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(Fx[Future].recoverFromNonFatal(fa2) { case NonFatal(`expectedExpcetion`) => expected })

        expectedFailedResult ==== actualFailedResult and actual ==== expected
      }

      def testCanRecover_Future_recoverFromNonFatalEitherShouldReturnSuccessfulResult: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa                               = run[Future, Either[SomeError, Int]](Right(1))
        val expected: Either[SomeError, Int] = Right(1)
        val actual                           =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor,
          )(Fx[Future].recoverFromNonFatal(fa) {
            case err => Left(SomeError.someThrowable(err))
          })

        actual ==== expected
      }

      def testCanRecover_Future_recoverFromNonFatalEitherShouldReturnFailedResult: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedFailure                  = SomeError.message("Failed")
        val fa                               = run[Future, Either[SomeError, Int]](Left(expectedFailure))
        val expected: Either[SomeError, Int] = Left(expectedFailure)
        val actual                           = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(Fx[Future].recoverFromNonFatal(fa) { case NonFatal(_) => Right(1) })

        actual ==== expected
      }

      def testCanRecover_Future_recoverEitherFromNonFatalShouldRecoverFromNonFatal: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))
        val actualFailedResult                           = ConcurrentSupport.futureToValue(
          Fx[Future]
            .recoverEitherFromNonFatal(fa) {
              case err => Left(SomeError.someThrowable(err))
            },
          waitFor,
        )

        val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected: Either[SomeError, Int] = Right(1)
        val actual                           =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor,
          )(Fx[Future].recoverEitherFromNonFatal(fa2) { case err @ _ => expected })

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      def testCanRecover_Future_recoverEitherFromNonFatalShouldReturnSuccessfulResult: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa                               = run[Future, Either[SomeError, Int]](Right(1))
        val expected: Either[SomeError, Int] = Right(1)
        val actual                           = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(
          Fx[Future]
            .recoverEitherFromNonFatal(fa) {
              case err => Left(SomeError.someThrowable(err))
            }
        )

        actual ==== expected
      }

      def testCanRecover_Future_recoverEitherFromNonFatalShouldReturnFailedResult: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedFailure                  = SomeError.message("Failed")
        val fa                               = run[Future, Either[SomeError, Int]](Left(expectedFailure))
        val expected: Either[SomeError, Int] = Left(expectedFailure)
        val actual                           =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor,
          )(Fx[Future].recoverEitherFromNonFatal(fa) { case NonFatal(_) => expected })

        actual ==== expected
      }

    }

  }

}
