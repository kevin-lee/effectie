package effectie.syntax

import cats.data.EitherT
import cats.syntax.all._
import effectie.core.Fx
import effectie.syntax.error._
import effectie.syntax.fx.effectOf
import effectie.testing.types.{SomeError, SomeThrowableError}
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog._
import hedgehog.runner._

import scala.util.control.NonFatal

/** @author Kevin Lee
  * @since 2022-02-26
  */
object errorSpec extends Properties {
  override def tests: List[Test] =
    CanCatchSyntaxSpec.tests ++ CanHandleErrorSyntaxSpec.tests ++ CanRecoverSyntaxSpec.tests ++ OnNonFatalSyntaxSpec.tests
}

object CanCatchSyntaxSpec {

  val CanHandleError: effectie.core.CanHandleError.type = effectie.core.CanHandleError

  def tests: List[Test] = List(
    /* Future */
    example(
      "test CanCatch[Future]catchNonFatalThrowable should catch NonFatal",
      FutureSpec.testCanCatch_Future_catchNonFatalThrowableShouldCatchNonFatal,
    ),
    example(
      "test CanCatch[Future]catchNonFatalThrowable should return the successful result",
      FutureSpec.testCanCatch_Future_catchNonFatalThrowableShouldReturnSuccessfulResult,
    ),
    example(
      "test CanCatch[Future]catchNonFatal should catch NonFatal",
      FutureSpec.testCanCatch_Future_catchNonFatalShouldCatchNonFatal,
    ),
    example(
      "test CanCatch[Future]catchNonFatal should return the successful result",
      FutureSpec.testCanCatch_Future_catchNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanCatch[Future]catchNonFatalEither should catch NonFatal",
      FutureSpec.testCanCatch_Future_catchNonFatalEitherShouldCatchNonFatal,
    ),
    example(
      "test CanCatch[Future]catchNonFatalEither should return the successful result",
      FutureSpec.testCanCatch_Future_catchNonFatalEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanCatch[Future]catchNonFatalEither should return the failed result",
      FutureSpec.testCanCatch_Future_catchNonFatalEitherShouldReturnFailedResult,
    ),
  )

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: Fx, A](a: => A): F[A] =
    effectOf[F](a)

  object FutureSpec {
    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration._
    import scala.concurrent.{ExecutionContext, Future}
    import effectie.instances.future.fx._

    private implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

    private val waitFor = WaitFor(1.second)

    def testCanCatch_Future_catchNonFatalThrowableShouldCatchNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = expectedExpcetion.asLeft[Int]
      val actual            = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(fa.catchNonFatalThrowable)

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalThrowableShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = run[Future, Int](1)
      val expected = 1.asRight[Throwable]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(fa.catchNonFatalThrowable)

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalShouldCatchNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(fa.catchNonFatal(SomeError.someThrowable))

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = run[Future, Int](1)
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(fa.catchNonFatal(SomeError.someThrowable))

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalEitherShouldCatchNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa       = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(fa.catchNonFatalEither(SomeError.someThrowable))

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(fa.catchNonFatalEither(SomeError.someThrowable))

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalEitherShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(fa.catchNonFatalEither(SomeError.someThrowable))

      actual ==== expected
    }

  }

}

object CanHandleErrorSyntaxSpec {

  def tests: List[Test] = List(
    /* IO */
    /* Future */
    example(
      "test CanHandleError[Future].handleNonFatalWith should handle NonFatal",
      FutureSpec.testCanHandleError_Future_handleNonFatalWithShouldHandleNonFatalWith,
    ),
    example(
      "test CanHandleError[Future].handleNonFatalWith should return the successful result",
      FutureSpec.testCanHandleError_Future_handleNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Future].handleNonFatalWithEither should handle NonFatal",
      FutureSpec.testCanHandleError_Future_handleNonFatalWithEitherShouldHandleNonFatalWith,
    ),
    example(
      "test CanHandleError[Future].handleNonFatalWithEither should return the successful result",
      FutureSpec.testCanHandleError_Future_handleNonFatalWithEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Future].handleNonFatalWithEither should return the failed result",
      FutureSpec.testCanHandleError_Future_handleNonFatalWithEitherShouldReturnFailedResult,
    ),
    example(
      "test CanHandleError[Future].handleEitherNonFatalWith should handle NonFatal",
      FutureSpec.testCanHandleError_Future_handleEitherNonFatalWithShouldHandleNonFatalWith,
    ),
    example(
      "test CanHandleError[Future].handleEitherNonFatalWith should return the successful result",
      FutureSpec.testCanHandleError_Future_handleEitherNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Future].handleEitherNonFatalWith should return the failed result",
      FutureSpec.testCanHandleError_Future_handleEitherNonFatalWithShouldReturnFailedResult,
    ),
    example(
      "test CanHandleError[Future].handleNonFatal should handle NonFatal",
      FutureSpec.testCanHandleError_Future_handleNonFatalShouldHandleNonFatal,
    ),
    example(
      "test CanHandleError[Future].handleNonFatal should return the successful result",
      FutureSpec.testCanHandleError_Future_handleNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Future].handleNonFatalEither should handle NonFatal",
      FutureSpec.testCanHandleError_Future_handleNonFatalEitherShouldHandleNonFatal,
    ),
    example(
      "test CanHandleError[Future].handleNonFatalEither should return the successful result",
      FutureSpec.testCanHandleError_Future_handleNonFatalEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Future].handleNonFatalEither should return the failed result",
      FutureSpec.testCanHandleError_Future_handleNonFatalEitherShouldReturnFailedResult,
    ),
    example(
      "test CanHandleError[Future].handleEitherNonFatal should handle NonFatal",
      FutureSpec.testCanHandleError_Future_handleEitherNonFatalShouldHandleNonFatal,
    ),
    example(
      "test CanHandleError[Future].handleEitherNonFatal should return the successful result",
      FutureSpec.testCanHandleError_Future_handleEitherNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Future].handleEitherNonFatal should return the failed result",
      FutureSpec.testCanHandleError_Future_handleEitherNonFatalShouldReturnFailedResult,
    ),
    example(
      "test F[Either[A, B]](F(Right(b))).rethrowIfLeft should return the successful result",
      FutureSpec.testFEitherAB_Future_rethrowIfLeftShouldReturnSuccessfulResult,
    ),
    example(
      "test F[Either[A, B]](F(Left(a))).rethrowIfLeft should return the failed result",
      FutureSpec.testFEitherAB_Future_rethrowIfLeftShouldReturnFailedResult,
    ),
    example(
      "test EitherT[F, A, B](F(Right(b))).rethrowTIfLeft should return the successful result",
      FutureSpec.testEitherTFAB_Future_rethrowTIfLeftShouldReturnSuccessfulResult,
    ),
    example(
      "test EitherT[F, A, B](F(Left(a))).rethrowTIfLeft should return the failed result",
      FutureSpec.testEitherTFAB_Future_rethrowTIfLeftShouldReturnFailedResult,
    ),
  )

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: Fx, A](a: => A): F[A] =
    effectOf[F](a)

  object FutureSpec {
    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration._
    import scala.concurrent.{ExecutionContext, Future}
    import effectie.instances.future.fx._

    private implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

    private val waitFor = WaitFor(1.second)

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
      )(fa.handleNonFatalWith(_ => Future(expected)))

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
      )(fa.handleNonFatalWith(_ => Future(123)))

      actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalWithEitherShouldHandleNonFatalWith: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   =
        ConcurrentSupport.futureToValue(
          fa.handleNonFatalWith(err => Future(SomeError.someThrowable(err).asLeft[Int])),
          waitFor,
        )

      val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(fa2.handleNonFatalWith(_ => Future(expected)))

      expectedFailedResult ==== actualFailedResult and actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(fa.handleNonFatalWith(err => Future(SomeError.someThrowable(err).asLeft[Int])))

      actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalWithEitherShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(fa.handleNonFatalWith(_ => Future(1.asRight[SomeError])))

      actual ==== expected
    }

    def testCanHandleError_Future_handleEitherNonFatalWithShouldHandleNonFatalWith: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   = ConcurrentSupport.futureToValue(
        fa.handleEitherNonFatalWith(err => Future(SomeError.someThrowable(err).asLeft[Int])),
        waitFor,
      )

      val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(fa2.handleEitherNonFatalWith(_ => Future(expected)))

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanHandleError_Future_handleEitherNonFatalWithShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(fa.handleEitherNonFatalWith(err => Future(SomeError.someThrowable(err).asLeft[Int])))

      actual ==== expected
    }

    def testCanHandleError_Future_handleEitherNonFatalWithShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(fa.handleEitherNonFatalWith(_ => Future(expected)))

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
      )(fa.handleNonFatal(_ => expected))

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
      )(fa.handleNonFatal(_ => 123))

      actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalEitherShouldHandleNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   =
        ConcurrentSupport.futureToValue(
          fa.handleNonFatal(err => SomeError.someThrowable(err).asLeft[Int]),
          waitFor,
        )

      val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(fa2.handleNonFatal(_ => expected))

      expectedFailedResult ==== actualFailedResult and actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalEitherShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(fa.handleNonFatal(err => SomeError.someThrowable(err).asLeft[Int]))

      actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalEitherShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(fa.handleNonFatal(_ => 1.asRight[SomeError]))

      actual ==== expected
    }

    def testCanHandleError_Future_handleEitherNonFatalShouldHandleNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   = ConcurrentSupport.futureToValue(
        fa.handleEitherNonFatal(err => SomeError.someThrowable(err).asLeft[Int]),
        waitFor,
      )

      val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(fa2.handleEitherNonFatal(_ => expected))

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanHandleError_Future_handleEitherNonFatalShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(fa.handleEitherNonFatal(err => SomeError.someThrowable(err).asLeft[Int]))

      actual ==== expected
    }

    def testCanHandleError_Future_handleEitherNonFatalShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(fa.handleEitherNonFatal(_ => expected))

      actual ==== expected
    }

    def testFEitherAB_Future_rethrowIfLeftShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = run[Future, Either[SomeThrowableError, Int]](1.asRight[SomeThrowableError])
      val expected = 1
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(fa.rethrowIfLeft)

      actual ==== expected
    }

    def testFEitherAB_Future_rethrowIfLeftShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeThrowableError.message("Failed")
      val fa              = run[Future, Either[SomeThrowableError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure
      try {
        val result = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(fa.rethrowIfLeft)
        Result.failure.log(s"Expected SomeThrowableError to be thrown but got ${result.toString} instead")
      } catch {
        case actual: SomeThrowableError =>
          actual ==== expected
      }

    }

    def testEitherTFAB_Future_rethrowTIfLeftShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = EitherT(run[Future, Either[SomeThrowableError, Int]](1.asRight[SomeThrowableError]))
      val expected = 1
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(fa.rethrowTIfLeft)

      actual ==== expected
    }

    def testEitherTFAB_Future_rethrowTIfLeftShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeThrowableError.message("Failed")
      val fa              = EitherT(run[Future, Either[SomeThrowableError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure
      try {
        val result = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(fa.rethrowTIfLeft)
        Result.failure.log(s"Expected SomeThrowableError to be thrown but got ${result.toString} instead")
      } catch {
        case actual: SomeThrowableError =>
          actual ==== expected
      }

    }

  }

}

object CanRecoverSyntaxSpec {

  def tests: List[Test] = List(
    /* Future */
    example(
      "test CanRecover[Future].recoverFromNonFatalWith should catch NonFatal",
      FutureSpec.testCanRecover_Future_recoverFromNonFatalWithShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Future].recoverFromNonFatalWith should return the successful result",
      FutureSpec.testCanRecover_Future_recoverFromNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Future].recoverFromNonFatalWithEither should catch NonFatal",
      FutureSpec.testCanRecover_Future_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Future].recoverFromNonFatalWithEither should return the successful result",
      FutureSpec.testCanRecover_Future_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Future].recoverFromNonFatalWithEither should return the failed result",
      FutureSpec.testCanRecover_Future_recoverFromNonFatalWithEitherShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[Future].recoverEitherFromNonFatalWith should catch NonFatal",
      FutureSpec.testCanRecover_Future_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Future].recoverEitherFromNonFatalWith should return the successful result",
      FutureSpec.testCanRecover_Future_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Future].recoverEitherFromNonFatalWith should return the failed result",
      FutureSpec.testCanRecover_Future_recoverEitherFromNonFatalWithShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[Future].recoverFromNonFatal should catch NonFatal",
      FutureSpec.testCanRecover_Future_recoverFromNonFatalShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Future].recoverFromNonFatal should return the successful result",
      FutureSpec.testCanRecover_Future_recoverFromNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Future].recoverFromNonFatalEither should catch NonFatal",
      FutureSpec.testCanRecover_Future_recoverFromNonFatalEitherShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Future].recoverFromNonFatalEither should return the successful result",
      FutureSpec.testCanRecover_Future_recoverFromNonFatalEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Future].recoverFromNonFatalEither should return the failed result",
      FutureSpec.testCanRecover_Future_recoverFromNonFatalEitherShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[Future].recoverEitherFromNonFatal should catch NonFatal",
      FutureSpec.testCanRecover_Future_recoverEitherFromNonFatalShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Future].recoverEitherFromNonFatal should return the successful result",
      FutureSpec.testCanRecover_Future_recoverEitherFromNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Future].recoverEitherFromNonFatal should return the failed result",
      FutureSpec.testCanRecover_Future_recoverEitherFromNonFatalShouldReturnFailedResult,
    ),
  )

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: Fx, A](a: => A): F[A] =
    effectOf[F](a)

  object FutureSpec {
    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration._
    import scala.concurrent.{ExecutionContext, Future}
    import scala.util.control.NonFatal
    import effectie.instances.future.fx._

    private implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

    private val waitFor = WaitFor(1.second)

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
      )(fa.recoverFromNonFatalWith {
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
      )(fa.recoverFromNonFatalWith {
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
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   =
        ConcurrentSupport.futureToValue(
          fa.recoverFromNonFatalWith {
            case err => Future(SomeError.someThrowable(err).asLeft[Int])
          },
          waitFor,
        )

      val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(fa2.recoverFromNonFatalWith {
          case NonFatal(`expectedExpcetion`) => Future(expected)
        })

      expectedFailedResult ==== actualFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(fa.recoverFromNonFatalWith {
          case err => Future(SomeError.someThrowable(err).asLeft[Int])
        })

      actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalWithEitherShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(fa.recoverFromNonFatalWith {
          case NonFatal(_) => Future(1.asRight[SomeError])
        })

      actual ==== expected
    }

    def testCanRecover_Future_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   = ConcurrentSupport.futureToValue(
        fa.recoverEitherFromNonFatalWith {
          case err => Future(SomeError.someThrowable(err).asLeft[Int])
        },
        waitFor,
      )

      val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(fa2.recoverEitherFromNonFatalWith {
          case err @ _ => Future(expected)
        })

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(fa.recoverEitherFromNonFatalWith {
        case err => Future(SomeError.someThrowable(err).asLeft[Int])
      })

      actual ==== expected
    }

    def testCanRecover_Future_recoverEitherFromNonFatalWithShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(fa.recoverEitherFromNonFatalWith {
          case NonFatal(_) => Future(expected)
        })

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
      )(fa.recoverFromNonFatal { case NonFatal(`expectedExpcetion`) => expected })

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
      )(fa.recoverFromNonFatal { case NonFatal(_) => 123 })

      actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalEitherShouldRecoverFromNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

      ConcurrentSupport.runAndShutdown(executorService, waitFor) {
        val actualFailedResult =
          ConcurrentSupport.futureToValue(
            fa.recoverFromNonFatal {
              case err => SomeError.someThrowable(err).asLeft[Int]
            },
            waitFor,
          )

        val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected = 1.asRight[SomeError]
        val actual   = ConcurrentSupport.futureToValue(
          fa2.recoverFromNonFatal { case NonFatal(`expectedExpcetion`) => expected },
          waitFor,
        )

        expectedFailedResult ==== actualFailedResult and actual ==== expected
      }
    }

    def testCanRecover_Future_recoverFromNonFatalEitherShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(fa.recoverFromNonFatal {
          case err => SomeError.someThrowable(err).asLeft[Int]
        })

      actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalEitherShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(fa.recoverFromNonFatal { case NonFatal(_) => 1.asRight[SomeError] })

      actual ==== expected
    }

    def testCanRecover_Future_recoverEitherFromNonFatalShouldRecoverFromNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   = ConcurrentSupport.futureToValue(
        fa.recoverEitherFromNonFatal {
          case err => SomeError.someThrowable(err).asLeft[Int]
        },
        waitFor,
      )

      val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(fa2.recoverEitherFromNonFatal { case err @ _ => expected })

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverEitherFromNonFatalShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(fa.recoverEitherFromNonFatal {
        case err => SomeError.someThrowable(err).asLeft[Int]
      })

      actual ==== expected
    }

    def testCanRecover_Future_recoverEitherFromNonFatalShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(fa.recoverEitherFromNonFatal { case NonFatal(_) => expected })

      actual ==== expected
    }

  }

}

object OnNonFatalSyntaxSpec {

  def tests: List[Test] = List(
    /* Future */
    example(
      "test OnNonFatal[Future].onNonFatalWith should do something for NonFatal",
      FutureSpec.testOnNonFatal_Future_onNonFatalWithShouldDoSomethingForNonFatal,
    ),
    //    example(
    //      "test OnNonFatal[Future].onNonFatalWith should not do anything and not catch Fatal",
    //      FutureSpec.testOnNonFatal_Future_onNonFatalWithShouldNotCatchFatal,
    //    ),
    example(
      "test OnNonFatal[Future].onNonFatalWith should not do anything for the successful result",
      FutureSpec.testOnNonFatal_Future_onNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test Future[Either[A, B]].onNonFatalWith should do something for NonFatal",
      FutureSpec.testOnNonFatal_FutureEitherAB_onNonFatalWithEitherShouldDoSomethingForNonFatal,
    ),
    example(
      "test Future[Either[A, B]].onNonFatalWith should do nothing for success case with Right",
      FutureSpec.testOnNonFatal_FutureEitherAB_onNonFatalWithEitherShouldDoNothingForRightCase,
    ),
    example(
      "test Future[Either[A, B]].onNonFatalWith should do nothing for success case with Left",
      FutureSpec.testOnNonFatal_FutureEitherAB_onNonFatalWithEitherShouldDoNothingForLeftCase,
    ),
    example(
      "test EitherT[F, A, B].onNonFatalWith should do something for NonFatal",
      FutureSpec.testOnNonFatal_EitherT_onNonFatalWithEitherShouldDoSomethingForNonFatal,
    ),
    example(
      "test EitherT[F, A, B](F(Right(b))).onNonFatalWith should do nothing for success case with Right",
      FutureSpec.testOnNonFatal_EitherT_onNonFatalWithEitherShouldDoNothingForRightCase,
    ),
    example(
      "test EitherT[F, A, B](F(Left(a))).onNonFatalWith should do nothing for success case with Left",
      FutureSpec.testOnNonFatal_EitherT_onNonFatalWithEitherShouldDoNothingForLeftCase,
    ),
  )

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: Fx, A](a: => A): F[A] =
    effectOf[F](a)

  object FutureSpec {
    import effectie.instances.future.fx._

    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration._
    import scala.concurrent.{ExecutionContext, Future}

    private implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

    private val waitFor = WaitFor(1.second)

    def testOnNonFatal_Future_onNonFatalWithShouldDoSomethingForNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 123.some
      var actual            = none[Int] // scalafix:ok DisableSyntax.var

      val result =
        try {
          val r =
            ConcurrentSupport.futureToValueAndTerminate(
              executorService,
              waitFor,
            )(fa.onNonFatalWith {
              case NonFatal(`expectedExpcetion`) =>
                Future {
                  actual = expected
                } *> Future.unit
            })
          new AssertionError(s"Should have thrown an exception, but it was ${r.toString}.")
        } catch {
          case ex: Throwable =>
            ex
        }

      Result.all(
        List(
          result ==== expectedExpcetion,
          actual ==== expected,
        )
      )
    }

    //    def testOnNonFatal_Future_onNonFatalWithShouldNotCatchFatal: Result = {
    //
    //      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
    //      implicit val ec: ExecutionContext             =
    //        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)
    //
    //      val expectedExpcetion = SomeControlThrowable("Something's wrong")
    //      val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
    //      var actual            = none[Int] // scalafix:ok DisableSyntax.var
    //
    //      try {
    //        val r = ConcurrentSupport.futureToValueAndTerminate(
    //          executorService,
    //          waitFor,
    //        )(fa.onNonFatalWith {
    //          case NonFatal(`expectedExpcetion`) =>
    //            Future {
    //              actual = 123.some
    //              ()
    //            } *> Future.unit
    //        })
    //        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${r.toString}")
    //      } catch {
    //        case ex: ControlThrowable =>
    //          Result.all(
    //            List(
    //              actual ==== none[Int],
    //              ex ==== expectedExpcetion,
    //            )
    //          )
    //
    //        case ex: Throwable =>
    //          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    //      }
    //
    //    }

    def testOnNonFatal_Future_onNonFatalWithShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedResult = 999
      val fa             = run[Future, Int](expectedResult)
      val expected       = none[Int]
      var actual         = none[Int] // scalafix:ok DisableSyntax.var

      try {
        val result =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor,
          )(fa.onNonFatalWith {
            case NonFatal(_) =>
              Future {
                actual = 123.some
              } *> Future.unit
          })
        Result.all(
          List(
            result ==== expectedResult,
            actual ==== expected,
          )
        )
      } catch {
        case ex: Throwable =>
          Result.failure.log(s"No exception was expected but ${ex.getClass.getName} was thrown. ${ex.toString}")
      }

    }

    /////

    def testOnNonFatal_FutureEitherAB_onNonFatalWithEitherShouldDoSomethingForNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val expectedResult    = expectedExpcetion.asLeft[Int]
      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

      val expected = 123.some
      var actual   = none[Int] // scalafix:ok DisableSyntax.var

      val actualFailedResult =
        try {
          val r =
            ConcurrentSupport.futureToValue(
              fa.onNonFatalWith {
                case NonFatal(`expectedExpcetion`) =>
                  Future {
                    actual = expected
                  } *> Future.unit
              },
              waitFor,
            )
          throw new AssertionError(
            s"Should have thrown an exception, but it was ${r.toString}."
          ) // scalafix:ok DisableSyntax.throw
        } catch {
          case ex: Throwable =>
            ex.asLeft[Int]
        }

      Result.all(
        List(
          actual ==== expected,
          actualFailedResult ==== expectedResult,
        )
      )
    }

    def testOnNonFatal_FutureEitherAB_onNonFatalWithEitherShouldDoNothingForRightCase: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedValue  = 1
      val expectedResult = expectedValue.asRight[SomeError]
      val fa             = run[Future, Either[SomeError, Int]](expectedResult)

      val expected = none[Int]
      var actual   = none[Int] // scalafix:ok DisableSyntax.var

      val actualResult =
        try {
          ConcurrentSupport.futureToValue(
            fa.onNonFatalWith {
              case NonFatal(_) =>
                Future {
                  actual = 123.some
                } *> Future.unit
            },
            waitFor,
          )
        } catch {
          case ex: Throwable =>
            throw new AssertionError(
              s"Should not have thrown an exception, but it was ${ex.toString}."
            ) // scalafix:ok DisableSyntax.throw
        }

      Result.all(
        List(
          actual ==== expected,
          actualResult ==== expectedResult,
        )
      )
    }

    def testOnNonFatal_FutureEitherAB_onNonFatalWithEitherShouldDoNothingForLeftCase: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val expectedResult    = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

      val fa = run[Future, Int](throwThrowable(expectedExpcetion))
        .catchNonFatal {
          case err =>
            SomeError.someThrowable(err)
        }

      val expected = none[Int]
      var actual   = none[Int] // scalafix:ok DisableSyntax.var

      val actualResult =
        try {
          ConcurrentSupport.futureToValue(
            fa.onNonFatalWith {
              case NonFatal(`expectedExpcetion`) =>
                Future {
                  actual = 123.some
                } *> Future.unit
            },
            waitFor,
          )
        } catch {
          case ex: Throwable =>
            throw new AssertionError(
              s"Should not have thrown an exception, but it was ${ex.toString}."
            ) // scalafix:ok DisableSyntax.throw
        }

      Result.all(
        List(
          actual ==== expected,
          actualResult ==== expectedResult,
        )
      )
    }

    /////////

    def testOnNonFatal_EitherT_onNonFatalWithEitherShouldDoSomethingForNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val expectedResult    = expectedExpcetion.asLeft[Int]
      val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))

      val expected = 123.some
      var actual   = none[Int] // scalafix:ok DisableSyntax.var

      val actualFailedResult =
        try {
          val r =
            ConcurrentSupport.futureToValue(
              fa.onNonFatalWith {
                case NonFatal(`expectedExpcetion`) =>
                  Future {
                    actual = expected
                  } *> Future.unit
              }.value,
              waitFor,
            )
          throw new AssertionError(
            s"Should have thrown an exception, but it was ${r.toString}."
          ) // scalafix:ok DisableSyntax.throw
        } catch {
          case ex: Throwable =>
            ex.asLeft[Int]
        }

      Result.all(
        List(
          actual ==== expected,
          actualFailedResult ==== expectedResult,
        )
      )
    }

    def testOnNonFatal_EitherT_onNonFatalWithEitherShouldDoNothingForRightCase: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedValue  = 1
      val expectedResult = expectedValue.asRight[SomeError]
      val fa             = EitherT(run[Future, Either[SomeError, Int]](expectedResult))

      val expected = none[Int]
      var actual   = none[Int] // scalafix:ok DisableSyntax.var

      val actualResult =
        try {
          ConcurrentSupport.futureToValue(
            fa.onNonFatalWith {
              case NonFatal(_) =>
                Future {
                  actual = 123.some
                } *> Future.unit
            }.value,
            waitFor,
          )
        } catch {
          case ex: Throwable =>
            throw new AssertionError(
              s"Should not have thrown an exception, but it was ${ex.toString}."
            ) // scalafix:ok DisableSyntax.throw
        }

      Result.all(
        List(
          actual ==== expected,
          actualResult ==== expectedResult,
        )
      )
    }

    def testOnNonFatal_EitherT_onNonFatalWithEitherShouldDoNothingForLeftCase: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val expectedResult    = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

      val fa = EitherT(
        run[Future, Int](throwThrowable(expectedExpcetion))
          .catchNonFatal {
            case err => SomeError.someThrowable(err)
          }
      )

      val expected = none[Int]
      var actual   = none[Int] // scalafix:ok DisableSyntax.var

      val actualResult =
        try {
          ConcurrentSupport.futureToValue(
            fa.onNonFatalWith {
              case NonFatal(`expectedExpcetion`) =>
                Future {
                  actual = 123.some
                } *> Future.unit
            }.value,
            waitFor,
          )
        } catch {
          case ex: Throwable =>
            throw new AssertionError(
              s"Should not have thrown an exception, but it was ${ex.toString}."
            ) // scalafix:ok DisableSyntax.throw
        }

      Result.all(
        List(
          actual ==== expected,
          actualResult ==== expectedResult,
        )
      )
    }

  }

}
