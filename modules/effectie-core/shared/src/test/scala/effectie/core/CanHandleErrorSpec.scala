package effectie.core

import effectie.testing.types.SomeError
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog._
import hedgehog.runner._

/** @author Kevin Lee
  * @since 2022-01-06
  */
object CanHandleErrorSpec extends Properties {

  implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = futureSpecs

  /* Future */
  val futureSpecs = List(
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
  )

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  private def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  private def run[F[*]: FxCtor, A](a: => A): F[A] =
    FxCtor[F].effectOf(a)

  object FutureSpec {
    import effectie.instances.future.canHandleError._
    import effectie.instances.future.fxCtor._

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
      )(CanHandleError[Future].handleNonFatalWith(fa)(_ => Future(expected)))

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
      )(CanHandleError[Future].handleNonFatalWith(fa)(_ => Future(123)))

      actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalWithEitherShouldHandleNonFatalWith: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion                            = new RuntimeException("Something's wrong")
      val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))

      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val actualFailedResult =
        ConcurrentSupport.futureToValue(
          CanHandleError[Future].handleNonFatalWith(fa)(err => Future(Left(SomeError.someThrowable(err)))),
          waitFor,
        )

      val expected: Either[SomeError, Int] = Right(1)

      val fa2    = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val actual = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(CanHandleError[Future].handleNonFatalWith(fa2)(_ => Future(expected)))

      expectedFailedResult ==== actualFailedResult and actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expected: Either[SomeError, Int] = Right(1)

      val fa     = run[Future, Either[SomeError, Int]](Right(1))
      val actual =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(CanHandleError[Future].handleNonFatalWith(fa)(err => Future(Left(SomeError.someThrowable(err)))))

      actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalWithEitherShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure                  = SomeError.message("Failed")
      val expected: Either[SomeError, Int] = Left(expectedFailure)

      val fa     = run[Future, Either[SomeError, Int]](Left(expectedFailure))
      val actual = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(CanHandleError[Future].handleNonFatalWith(fa)(_ => Future(Right(1))))

      actual ==== expected
    }

    def testCanHandleError_Future_handleEitherNonFatalWithShouldHandleNonFatalWith: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion                            = new RuntimeException("Something's wrong")
      val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))

      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val actualFailedResult = ConcurrentSupport.futureToValue(
        CanHandleError[Future]
          .handleEitherNonFatalWith(fa)(err => Future(Left(SomeError.someThrowable(err)))),
        waitFor,
      )

      val expected: Either[SomeError, Int] = Right(1)

      val fa2    = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val actual =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(CanHandleError[Future].handleEitherNonFatalWith(fa2)(_ => Future(expected)))

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanHandleError_Future_handleEitherNonFatalWithShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expected: Either[SomeError, Int] = Right(1)

      val fa     = run[Future, Either[SomeError, Int]](Right(1))
      val actual = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(
        CanHandleError[Future]
          .handleEitherNonFatalWith(fa)(err => Future(Left(SomeError.someThrowable(err))))
      )

      actual ==== expected
    }

    def testCanHandleError_Future_handleEitherNonFatalWithShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure                  = SomeError.message("Failed")
      val expected: Either[SomeError, Int] = Left(expectedFailure)

      val fa     = run[Future, Either[SomeError, Int]](Left(expectedFailure))
      val actual =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(CanHandleError[Future].handleEitherNonFatalWith(fa)(_ => Future(expected)))

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
      )(CanHandleError[Future].handleNonFatal(fa)(_ => expected))

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
      )(CanHandleError[Future].handleNonFatal(fa)(_ => 123))

      actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalEitherShouldHandleNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion                            = new RuntimeException("Something's wrong")
      val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))

      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val actualFailedResult =
        ConcurrentSupport.futureToValue(
          CanHandleError[Future].handleNonFatal(fa)(err => Left(SomeError.someThrowable(err))),
          waitFor,
        )

      val expected: Either[SomeError, Int] = Right(1)

      val fa2    = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val actual = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(CanHandleError[Future].handleNonFatal(fa2)(_ => expected))

      expectedFailedResult ==== actualFailedResult and actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalEitherShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expected: Either[SomeError, Int] = Right(1)

      val fa     = run[Future, Either[SomeError, Int]](Right(1))
      val actual =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(CanHandleError[Future].handleNonFatal(fa)(err => Left(SomeError.someThrowable(err))))

      actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalEitherShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure                  = SomeError.message("Failed")
      val expected: Either[SomeError, Int] = Left(expectedFailure)

      val fa     = run[Future, Either[SomeError, Int]](Left(expectedFailure))
      val actual = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(CanHandleError[Future].handleNonFatal(fa)(_ => Right(1)))

      actual ==== expected
    }

    def testCanHandleError_Future_handleEitherNonFatalShouldHandleNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion                            = new RuntimeException("Something's wrong")
      val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))

      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val actualFailedResult = ConcurrentSupport.futureToValue(
        CanHandleError[Future].handleEitherNonFatal(fa)(err => Left(SomeError.someThrowable(err))),
        waitFor,
      )

      val expected: Either[SomeError, Int] = Right(1)

      val fa2    = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val actual =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(CanHandleError[Future].handleEitherNonFatal(fa2)(_ => expected))

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanHandleError_Future_handleEitherNonFatalShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expected: Either[SomeError, Int] = Right(1)

      val fa     = run[Future, Either[SomeError, Int]](Right(1))
      val actual = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(CanHandleError[Future].handleEitherNonFatal(fa)(err => Left(SomeError.someThrowable(err))))

      actual ==== expected
    }

    def testCanHandleError_Future_handleEitherNonFatalShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure                  = SomeError.message("Failed")
      val expected: Either[SomeError, Int] = Left(expectedFailure)

      val fa     = run[Future, Either[SomeError, Int]](Left(expectedFailure))
      val actual =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(CanHandleError[Future].handleEitherNonFatal(fa)(_ => expected))

      actual ==== expected
    }

  }

}
