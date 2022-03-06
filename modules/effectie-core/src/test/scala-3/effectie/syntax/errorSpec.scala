package effectie.syntax

import cats.*
import cats.data.EitherT
import cats.syntax.all.*
import effectie.SomeControlThrowable
import effectie.core._
import effectie.syntax.error.*
import effectie.syntax.fx.{*, given}
import effectie.testing.types.SomeError
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog.*
import hedgehog.runner.*

/** @author Kevin Lee
  * @since 2022-02-13
  */
object errorSpec extends Properties {
  override def tests: List[Test] =
    CanCatchSyntaxSpec.tests ++ CanHandleErrorSyntaxSpec.tests ++ CanRecoverSyntaxSpec.tests
}

object CanCatchSyntaxSpec {

  def tests: List[Test] = List(
    /* Future */
    example(
      "test CanCatch[Future].catchNonFatalThrowable should catch NonFatal",
      FutureSpec.testCanCatch_Future_catchNonFatalThrowableShouldCatchNonFatal
    ),
    example(
      "test CanCatch[Future].catchNonFatalThrowable should return the successful result",
      FutureSpec.testCanCatch_Future_catchNonFatalThrowableShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[Future].catchNonFatal should catch NonFatal",
      FutureSpec.testCanCatch_Future_catchNonFatalShouldCatchNonFatal
    ),
    example(
      "test CanCatch[Future].catchNonFatal should return the successful result",
      FutureSpec.testCanCatch_Future_catchNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[Future].catchNonFatalEither should catch NonFatal",
      FutureSpec.testCanCatch_Future_catchNonFatalEitherShouldCatchNonFatal
    ),
    example(
      "test CanCatch[Future].catchNonFatalEither should return the successful result",
      FutureSpec.testCanCatch_Future_catchNonFatalEitherShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[Future].catchNonFatalEither should return the failed result",
      FutureSpec.testCanCatch_Future_catchNonFatalEitherShouldReturnFailedResult
    ),
  )

  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: Fx: Functor, A](a: => A): F[A] =
    effectOf[F](a)

  object FutureSpec {
    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration.*
    import scala.concurrent.{ExecutionContext, Future}

    private given errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

    private val waitFor = WaitFor(1.second)

    def testCanCatch_Future_catchNonFatalThrowableShouldCatchNonFatal: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      def fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = expectedExpcetion.asLeft[Int]
      val actual            = ConcurrentSupport.futureToValue(
        fa.catchNonFatalThrowable,
        waitFor
      )
      val actual2           = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(catchNonFatalThrowable(fa))

      actual ==== expected and actual2 ==== expected
    }

    def testCanCatch_Future_catchNonFatalThrowableShouldReturnSuccessfulResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      def fa       = run[Future, Int](1)
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValue(
        fa.catchNonFatalThrowable,
        waitFor
      )
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(catchNonFatalThrowable(fa))

      actual ==== expected and actual2 ==== expected
    }

    def testCanCatch_Future_catchNonFatalShouldCatchNonFatal: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      def fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = ConcurrentSupport.futureToValue(
        fa.catchNonFatal(SomeError.someThrowable),
        waitFor
      )
      val actual2           = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(catchNonFatal(fa)(SomeError.someThrowable))

      actual ==== expected and actual2 ==== expected
    }

    def testCanCatch_Future_catchNonFatalShouldReturnSuccessfulResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      def fa       = run[Future, Int](1)
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValue(
        fa.catchNonFatal(SomeError.someThrowable),
        waitFor
      )
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(catchNonFatal(fa)(SomeError.someThrowable))

      actual ==== expected and actual2 ==== expected
    }

    def testCanCatch_Future_catchNonFatalEitherShouldCatchNonFatal: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      def fa       = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual   = ConcurrentSupport.futureToValue(
        fa.catchNonFatalEither(SomeError.someThrowable),
        waitFor
      )
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(catchNonFatalEither(fa)(SomeError.someThrowable))

      actual ==== expected and actual2 ==== expected
    }

    def testCanCatch_Future_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      def fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValue(
        fa.catchNonFatalEither(SomeError.someThrowable),
        waitFor
      )
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(catchNonFatalEither(fa)(SomeError.someThrowable))

      actual ==== expected and actual2 ==== expected
    }

    def testCanCatch_Future_catchNonFatalEitherShouldReturnFailedResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      def fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = ConcurrentSupport.futureToValue(
        fa.catchNonFatalEither(SomeError.someThrowable),
        waitFor
      )
      val actual2         = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(catchNonFatalEither(fa)(SomeError.someThrowable))

      actual ==== expected and actual2 ==== expected
    }

  }

}

object CanHandleErrorSyntaxSpec {

  def tests: List[Test] = List(
    /* Future */
    example(
      "test CanHandleError[Future].handleNonFatalWith should handle NonFatal",
      FutureSpec.testCanHandleError_Future_handleNonFatalWithShouldHandleNonFatalWith
    ),
    example(
      "test CanHandleError[Future].handleNonFatalWith should return the successful result",
      FutureSpec.testCanHandleError_Future_handleNonFatalWithShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[Future].handleNonFatalWithEither should handle NonFatal",
      FutureSpec.testCanHandleError_Future_handleNonFatalWithEitherShouldHandleNonFatalWith
    ),
    example(
      "test CanHandleError[Future].handleNonFatalWithEither should return the successful result",
      FutureSpec.testCanHandleError_Future_handleNonFatalWithEitherShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[Future].handleNonFatalWithEither should return the failed result",
      FutureSpec.testCanHandleError_Future_handleNonFatalWithEitherShouldReturnFailedResult
    ),
    example(
      "test CanHandleError[Future].handleEitherNonFatalWith should handle NonFatal",
      FutureSpec.testCanHandleError_Future_handleEitherNonFatalWithShouldHandleNonFatalWith
    ),
    example(
      "test CanHandleError[Future].handleEitherNonFatalWith should return the successful result",
      FutureSpec.testCanHandleError_Future_handleEitherNonFatalWithShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[Future].handleEitherNonFatalWith should return the failed result",
      FutureSpec.testCanHandleError_Future_handleEitherNonFatalWithShouldReturnFailedResult
    ),
    example(
      "test CanHandleError[Future].handleNonFatal should handle NonFatal",
      FutureSpec.testCanHandleError_Future_handleNonFatalShouldHandleNonFatal
    ),
    example(
      "test CanHandleError[Future].handleNonFatal should return the successful result",
      FutureSpec.testCanHandleError_Future_handleNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[Future].handleNonFatalEither should handle NonFatal",
      FutureSpec.testCanHandleError_Future_handleNonFatalEitherShouldHandleNonFatal
    ),
    example(
      "test CanHandleError[Future].handleNonFatalEither should return the successful result",
      FutureSpec.testCanHandleError_Future_handleNonFatalEitherShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[Future].handleNonFatalEither should return the failed result",
      FutureSpec.testCanHandleError_Future_handleNonFatalEitherShouldReturnFailedResult
    ),
    example(
      "test CanHandleError[Future].handleEitherNonFatal should handle NonFatal",
      FutureSpec.testCanHandleError_Future_handleEitherNonFatalShouldHandleNonFatal
    ),
    example(
      "test CanHandleError[Future].handleEitherNonFatal should return the successful result",
      FutureSpec.testCanHandleError_Future_handleEitherNonFatalShouldReturnSuccessfulResult
    ),
  )

  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: Fx: Functor, A](a: => A): F[A] =
    effectOf[F](a)

  object FutureSpec {
    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration.*
    import scala.concurrent.{ExecutionContext, Future}

    private given errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

    private val waitFor = WaitFor(1.second)

    def testCanHandleError_Future_handleNonFatalWithShouldHandleNonFatalWith: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      def fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 1
      val actual            = ConcurrentSupport.futureToValue(
        fa.handleNonFatalWith(_ => Future(expected)),
        waitFor
      )
      val actual2           = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(handleNonFatalWith(fa)(_ => Future(expected)))

      actual ==== expected and actual2 ==== expected
    }

    def testCanHandleError_Future_handleNonFatalWithShouldReturnSuccessfulResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      def fa       = run[Future, Int](1)
      val expected = 1
      val actual   = ConcurrentSupport.futureToValue(
        fa.handleNonFatalWith(_ => Future(123)),
        waitFor
      )
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(handleNonFatalWith(fa)(_ => Future(123)))

      actual ==== expected and actual2 ==== expected
    }

    def testCanHandleError_Future_handleNonFatalWithEitherShouldHandleNonFatalWith: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   =
        ConcurrentSupport.futureToValue(
          fa.handleNonFatalWith(err => Future(SomeError.someThrowable(err).asLeft[Int])),
          waitFor
        )

      val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(fa2.handleNonFatalWith(_ => Future(expected)))

      expectedFailedResult ==== actualFailedResult and actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      def fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValue(
          fa.handleNonFatalWith(err => Future(SomeError.someThrowable(err).asLeft[Int])),
          waitFor
        )
      val actual2  =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(handleNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int])))

      actual ==== expected and actual2 ==== expected
    }

    def testCanHandleError_Future_handleNonFatalWithEitherShouldReturnFailedResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      def fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = ConcurrentSupport.futureToValue(
        fa.handleNonFatalWith(_ => Future(1.asRight[SomeError])),
        waitFor
      )
      val actual2         = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(handleNonFatalWith(fa)(_ => Future(1.asRight[SomeError])))

      actual ==== expected and actual2 ==== expected
    }

    def testCanHandleError_Future_handleEitherNonFatalWithShouldHandleNonFatalWith: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   = ConcurrentSupport.futureToValue(
        fa.handleEitherNonFatalWith(err => Future(SomeError.someThrowable(err).asLeft[Int])),
        waitFor
      )

      val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(fa2.handleEitherNonFatalWith(err => Future(expected)))

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanHandleError_Future_handleEitherNonFatalWithShouldReturnSuccessfulResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      def fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValue(
        fa.handleEitherNonFatalWith(err => Future(SomeError.someThrowable(err).asLeft[Int])),
        waitFor
      )
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(handleEitherNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int])))

      actual ==== expected and actual2 ==== expected
    }

    def testCanHandleError_Future_handleEitherNonFatalWithShouldReturnFailedResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      def fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        ConcurrentSupport.futureToValue(
          fa.handleEitherNonFatalWith(_ => Future(expected)),
          waitFor
        )
      val actual2         =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(handleEitherNonFatalWith(fa)(_ => Future(expected)))

      actual ==== expected and actual2 ==== expected
    }

    def testCanHandleError_Future_handleNonFatalShouldHandleNonFatal: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      def fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 1
      val actual            = ConcurrentSupport.futureToValue(
        fa.handleNonFatal(_ => expected),
        waitFor
      )
      val actual2           = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(handleNonFatal(fa)(_ => expected))

      actual ==== expected and actual2 ==== expected
    }

    def testCanHandleError_Future_handleNonFatalShouldReturnSuccessfulResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      def fa       = run[Future, Int](1)
      val expected = 1
      val actual   = ConcurrentSupport.futureToValue(
        fa.handleNonFatal(_ => 123),
        waitFor
      )
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(handleNonFatal(fa)(_ => 123))

      actual ==== expected and actual2 ==== expected
    }

    def testCanHandleError_Future_handleNonFatalEitherShouldHandleNonFatal: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   =
        ConcurrentSupport.futureToValue(
          fa.handleNonFatal(err => SomeError.someThrowable(err).asLeft[Int]),
          waitFor
        )

      val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(fa2.handleNonFatal(_ => expected))

      expectedFailedResult ==== actualFailedResult and actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalEitherShouldReturnSuccessfulResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      def fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValue(
          fa.handleNonFatal(err => SomeError.someThrowable(err).asLeft[Int]),
          waitFor
        )
      val actual2  =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(handleNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]))

      actual ==== expected and actual2 ==== expected
    }

    def testCanHandleError_Future_handleNonFatalEitherShouldReturnFailedResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      def fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = ConcurrentSupport.futureToValue(
        fa.handleNonFatal(_ => 1.asRight[SomeError]),
        waitFor
      )
      val actual2         = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(handleNonFatal(fa)(_ => 1.asRight[SomeError]))

      actual ==== expected and actual2 ==== expected
    }

    def testCanHandleError_Future_handleEitherNonFatalShouldHandleNonFatal: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   = ConcurrentSupport.futureToValue(
        fa.handleEitherNonFatal(err => SomeError.someThrowable(err).asLeft[Int]),
        waitFor
      )

      val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(fa2.handleEitherNonFatal(err => expected))

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanHandleError_Future_handleEitherNonFatalShouldReturnSuccessfulResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      def fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValue(
        fa.handleEitherNonFatal(err => SomeError.someThrowable(err).asLeft[Int]),
        waitFor
      )
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]))

      actual ==== expected and actual2 ==== expected
    }

  }

}

object CanRecoverSyntaxSpec {

  def tests: List[Test] = List(
    /* Future */
    example(
      "test CanRecover[Future].recoverFromNonFatalWith should catch NonFatal",
      FutureSpec.testCanRecover_Future_recoverFromNonFatalWithShouldRecoverFromNonFatal
    ),
    example(
      "test CanRecover[Future].recoverFromNonFatalWith should return the successful result",
      FutureSpec.testCanRecover_Future_recoverFromNonFatalWithShouldReturnSuccessfulResult
    ),
    example(
      "test CanRecover[Future].recoverFromNonFatalWithEither should catch NonFatal",
      FutureSpec.testCanRecover_Future_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal
    ),
    example(
      "test CanRecover[Future].recoverFromNonFatalWithEither should return the successful result",
      FutureSpec.testCanRecover_Future_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult
    ),
    example(
      "test CanRecover[Future].recoverFromNonFatalWithEither should return the failed result",
      FutureSpec.testCanRecover_Future_recoverFromNonFatalWithEitherShouldReturnFailedResult
    ),
    example(
      "test CanRecover[Future].recoverEitherFromNonFatalWith should catch NonFatal",
      FutureSpec.testCanRecover_Future_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal
    ),
    example(
      "test CanRecover[Future].recoverEitherFromNonFatalWith should return the successful result",
      FutureSpec.testCanRecover_Future_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult
    ),
    example(
      "test CanRecover[Future].recoverEitherFromNonFatalWith should return the failed result",
      FutureSpec.testCanRecover_Future_recoverEitherFromNonFatalWithShouldReturnFailedResult
    ),
    example(
      "test CanRecover[Future].recoverFromNonFatal should catch NonFatal",
      FutureSpec.testCanRecover_Future_recoverFromNonFatalShouldRecoverFromNonFatal
    ),
    example(
      "test CanRecover[Future].recoverFromNonFatal should return the successful result",
      FutureSpec.testCanRecover_Future_recoverFromNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanRecover[Future].recoverFromNonFatalEither should catch NonFatal",
      FutureSpec.testCanRecover_Future_recoverFromNonFatalEitherShouldRecoverFromNonFatal
    ),
    example(
      "test CanRecover[Future].recoverFromNonFatalEither should return the successful result",
      FutureSpec.testCanRecover_Future_recoverFromNonFatalEitherShouldReturnSuccessfulResult
    ),
    example(
      "test CanRecover[Future].recoverFromNonFatalEither should return the failed result",
      FutureSpec.testCanRecover_Future_recoverFromNonFatalEitherShouldReturnFailedResult
    ),
    example(
      "test CanRecover[Future].recoverEitherFromNonFatal should catch NonFatal",
      FutureSpec.testCanRecover_Future_recoverEitherFromNonFatalShouldRecoverFromNonFatal
    ),
    example(
      "test CanRecover[Future].recoverEitherFromNonFatal should return the successful result",
      FutureSpec.testCanRecover_Future_recoverEitherFromNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanRecover[Future].recoverEitherFromNonFatal should return the failed result",
      FutureSpec.testCanRecover_Future_recoverEitherFromNonFatalShouldReturnFailedResult
    ),
  )

  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: Fx: Functor, A](a: => A): F[A] =
    effectOf[F](a)

  object FutureSpec {
    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration.*
    import scala.concurrent.{ExecutionContext, Future}
    import scala.util.control.NonFatal

    private given errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

    private val waitFor = WaitFor(1.second)

    def testCanRecover_Future_recoverFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      def fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 1
      val actual            = ConcurrentSupport.futureToValue(
        fa.recoverFromNonFatalWith {
          case NonFatal(`expectedExpcetion`) => Future(expected)
        },
        waitFor
      )
      val actual2           = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) => Future(expected)
      })

      actual ==== expected and actual2 ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      def fa       = run[Future, Int](1)
      val expected = 1
      val actual   = ConcurrentSupport.futureToValue(
        fa.recoverFromNonFatalWith {
          case NonFatal(_) => Future(123)
        },
        waitFor
      )
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(recoverFromNonFatalWith(fa) {
        case NonFatal(_) => Future(123)
      })

      actual ==== expected and actual2 ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   =
        ConcurrentSupport.futureToValue(
          fa.recoverFromNonFatalWith {
            case err => Future(SomeError.someThrowable(err).asLeft[Int])
          },
          waitFor
        )

      val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(fa2.recoverFromNonFatalWith {
          case NonFatal(`expectedExpcetion`) => Future(expected)
        })

      expectedFailedResult ==== actualFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      def fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValue(
          fa.recoverFromNonFatalWith {
            case err => Future(SomeError.someThrowable(err).asLeft[Int])
          },
          waitFor
        )
      val actual2  =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(recoverFromNonFatalWith(fa) {
          case err => Future(SomeError.someThrowable(err).asLeft[Int])
        })

      actual ==== expected and actual2 ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalWithEitherShouldReturnFailedResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      def fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        ConcurrentSupport.futureToValue(
          fa.recoverFromNonFatalWith {
            case NonFatal(_) => Future(1.asRight[SomeError])
          },
          waitFor
        )
      val actual2         =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(recoverFromNonFatalWith(fa) {
          case NonFatal(_) => Future(1.asRight[SomeError])
        })

      actual ==== expected and actual2 ==== expected
    }

    def testCanRecover_Future_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   = ConcurrentSupport.futureToValue(
        fa.recoverEitherFromNonFatalWith {
          case err => Future(SomeError.someThrowable(err).asLeft[Int])
        },
        waitFor
      )

      val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(fa2.recoverEitherFromNonFatalWith {
          case err => Future(expected)
        })

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      def fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValue(
        fa.recoverEitherFromNonFatalWith {
          case err => Future(SomeError.someThrowable(err).asLeft[Int])
        },
        waitFor
      )
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(recoverEitherFromNonFatalWith(fa) {
        case err => Future(SomeError.someThrowable(err).asLeft[Int])
      })

      actual ==== expected and actual2 ==== expected
    }

    def testCanRecover_Future_recoverEitherFromNonFatalWithShouldReturnFailedResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      def fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        ConcurrentSupport.futureToValue(
          fa.recoverEitherFromNonFatalWith {
            case NonFatal(_) => Future(expected)
          },
          waitFor
        )
      val actual2         =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(recoverEitherFromNonFatalWith(fa) {
          case NonFatal(_) => Future(expected)
        })

      actual ==== expected and actual2 ==== expected
    }

    // /

    def testCanRecover_Future_recoverFromNonFatalShouldRecoverFromNonFatal: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      def fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 1
      val actual            = ConcurrentSupport.futureToValue(
        fa.recoverFromNonFatal { case NonFatal(`expectedExpcetion`) => expected },
        waitFor
      )
      val actual2           = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expected })

      actual ==== expected and actual2 ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalShouldReturnSuccessfulResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      def fa       = run[Future, Int](1)
      val expected = 1
      val actual   = ConcurrentSupport.futureToValue(
        fa.recoverFromNonFatal { case NonFatal(_) => 123 },
        waitFor
      )
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(recoverFromNonFatal(fa) { case NonFatal(_) => 123 })

      actual ==== expected and actual2 ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalEitherShouldRecoverFromNonFatal: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   =
        ConcurrentSupport.futureToValue(
          fa.recoverFromNonFatal {
            case err => SomeError.someThrowable(err).asLeft[Int]
          },
          waitFor
        )

      val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(fa2.recoverFromNonFatal { case NonFatal(`expectedExpcetion`) => expected })

      expectedFailedResult ==== actualFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalEitherShouldReturnSuccessfulResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      def fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValue(
          fa.recoverFromNonFatal {
            case err => SomeError.someThrowable(err).asLeft[Int]
          },
          waitFor
        )
      val actual2  =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(recoverFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        })

      actual ==== expected and actual2 ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalEitherShouldReturnFailedResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      def fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = ConcurrentSupport.futureToValue(
        fa.recoverFromNonFatal { case NonFatal(_) => 1.asRight[SomeError] },
        waitFor
      )
      val actual2         = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(recoverFromNonFatal(fa) { case NonFatal(_) => 1.asRight[SomeError] })

      actual ==== expected and actual2 ==== expected
    }

    def testCanRecover_Future_recoverEitherFromNonFatalShouldRecoverFromNonFatal: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   = ConcurrentSupport.futureToValue(
        fa.recoverEitherFromNonFatal {
          case err => SomeError.someThrowable(err).asLeft[Int]
        },
        waitFor
      )

      val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(fa2.recoverEitherFromNonFatal { case err => expected })

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverEitherFromNonFatalShouldReturnSuccessfulResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      def fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValue(
        fa.recoverEitherFromNonFatal {
          case err => SomeError.someThrowable(err).asLeft[Int]
        },
        waitFor
      )
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(recoverEitherFromNonFatal(fa) {
        case err => SomeError.someThrowable(err).asLeft[Int]
      })

      actual ==== expected and actual2 ==== expected
    }

    def testCanRecover_Future_recoverEitherFromNonFatalShouldReturnFailedResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      def fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        ConcurrentSupport.futureToValue(
          fa.recoverEitherFromNonFatal { case NonFatal(_) => expected },
          waitFor
        )
      val actual2         =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(recoverEitherFromNonFatal(fa) { case NonFatal(_) => expected })

      actual ==== expected and actual2 ==== expected
    }

  }

}
