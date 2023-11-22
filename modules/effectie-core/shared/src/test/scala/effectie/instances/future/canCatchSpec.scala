package effectie.instances.future

import effectie.core.{CanCatch, FxCtor}
import effectie.testing.types.SomeError
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog._
import hedgehog.runner._

/** @author Kevin Lee
  * @since 2022-01-05
  */
object canCatchSpec extends Properties {
  private implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = futureSpecs

  val futureSpecs = List(
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

  def run[F[*]: FxCtor, A](a: => A): F[A] =
    FxCtor[F].effectOf(a)

  object FutureSpec {
    import effectie.instances.future.canCatch._
    import effectie.instances.future.fxCtor._

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
      )(CanCatch[Future].catchNonFatalThrowable(fa))

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
      )(CanCatch[Future].catchNonFatal(fa)(SomeError.someThrowable))

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalThrowableShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expected: Either[Throwable, Int] = Right(1)

      val fa     = run[Future, Int](1)
      val actual = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(CanCatch[Future].catchNonFatalThrowable(fa))

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expected: Either[SomeError, Int] = Right(1)

      val fa     = run[Future, Int](1)
      val actual = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(CanCatch[Future].catchNonFatal(fa)(SomeError.someThrowable))

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalEitherShouldCatchNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion                = new RuntimeException("Something's wrong")
      val expected: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))

      val fa     = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val actual = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(CanCatch[Future].catchNonFatalEither(fa)(SomeError.someThrowable))

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expected: Either[SomeError, Int] = Right(1)

      val fa     = run[Future, Either[SomeError, Int]](Right(1))
      val actual = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(CanCatch[Future].catchNonFatalEither(fa)(SomeError.someThrowable))

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalEitherShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure                  = SomeError.message("Failed")
      val expected: Either[SomeError, Int] = Left(expectedFailure)

      val fa     = run[Future, Either[SomeError, Int]](Left(expectedFailure))
      val actual = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(CanCatch[Future].catchNonFatalEither(fa)(SomeError.someThrowable))

      actual ==== expected
    }

  }

}
