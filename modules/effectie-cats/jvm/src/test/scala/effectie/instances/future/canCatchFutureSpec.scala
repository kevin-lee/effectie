package effectie.instances.future

import cats.data.EitherT
import cats.syntax.all._
import effectie.core._
import effectie.syntax.all._
import effectie.testing.types._
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog._
import hedgehog.runner._

/** @author Kevin Lee
  * @since 2020-07-31
  */
object canCatchFutureSpec extends Properties {

  private implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = futureSpecs

  /* Future */
  val futureSpecs = List(
    example(
      "test CanCatch[Future]catchNonFatalEitherT should catch NonFatal",
      FutureSpec.testCanCatch_Future_catchNonFatalEitherTShouldCatchNonFatal,
    ),
    example(
      "test CanCatch[Future]catchNonFatalEitherT should return the successful result",
      FutureSpec.testCanCatch_Future_catchNonFatalEitherTShouldReturnSuccessfulResult,
    ),
    example(
      "test CanCatch[Future]catchNonFatalEitherT should return the failed result",
      FutureSpec.testCanCatch_Future_catchNonFatalEitherTShouldReturnFailedResult,
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

//    def testCanCatch_Future_catchNonFatalThrowableShouldCatchNonFatal: Result = {
//
//      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
//      implicit val ec: ExecutionContext             =
//        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)
//
//      val expectedExpcetion = new RuntimeException("Something's wrong")
//      val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
//      val expected          = expectedExpcetion.asLeft[Int]
//      val actual            = ConcurrentSupport.futureToValueAndTerminate(
//        executorService,
//        waitFor
//      )(CanCatch[Future].catchNonFatalThrowable(fa))
//
//      actual ==== expected
//    }
//
//    def testCanCatch_Future_catchNonFatalShouldCatchNonFatal: Result = {
//
//      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
//      implicit val ec: ExecutionContext             =
//        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)
//
//      val expectedExpcetion = new RuntimeException("Something's wrong")
//      val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
//      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
//      val actual            = ConcurrentSupport.futureToValueAndTerminate(
//        executorService,
//        waitFor
//      )(CanCatch[Future].catchNonFatal(fa)(SomeError.someThrowable))
//
//      actual ==== expected
//    }
//
//    def testCanCatch_Future_catchNonFatalThrowableShouldReturnSuccessfulResult: Result = {
//
//      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
//      implicit val ec: ExecutionContext             =
//        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)
//
//      val fa       = run[Future, Int](1)
//      val expected = 1.asRight[Throwable]
//      val actual   = ConcurrentSupport.futureToValueAndTerminate(
//        executorService,
//        waitFor
//      )(CanCatch[Future].catchNonFatalThrowable(fa))
//
//      actual ==== expected
//    }
//
//    def testCanCatch_Future_catchNonFatalShouldReturnSuccessfulResult: Result = {
//
//      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
//      implicit val ec: ExecutionContext             =
//        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)
//
//      val fa       = run[Future, Int](1)
//      val expected = 1.asRight[SomeError]
//      val actual   = ConcurrentSupport.futureToValueAndTerminate(
//        executorService,
//        waitFor
//      )(CanCatch[Future].catchNonFatal(fa)(SomeError.someThrowable))
//
//      actual ==== expected
//    }
//
//    def testCanCatch_Future_catchNonFatalEitherShouldCatchNonFatal: Result = {
//
//      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
//      implicit val ec: ExecutionContext             =
//        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)
//
//      val expectedExpcetion = new RuntimeException("Something's wrong")
//      val fa       = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
//      val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
//      val actual   = ConcurrentSupport.futureToValueAndTerminate(
//        executorService,
//        waitFor
//      )(CanCatch[Future].catchNonFatalEither(fa)(SomeError.someThrowable))
//
//      actual ==== expected
//    }
//
//    def testCanCatch_Future_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {
//
//      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
//      implicit val ec: ExecutionContext             =
//        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)
//
//      val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
//      val expected = 1.asRight[SomeError]
//      val actual   = ConcurrentSupport.futureToValueAndTerminate(
//        executorService,
//        waitFor
//      )(CanCatch[Future].catchNonFatalEither(fa)(SomeError.someThrowable))
//
//      actual ==== expected
//    }
//
//    def testCanCatch_Future_catchNonFatalEitherShouldReturnFailedResult: Result = {
//
//      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
//      implicit val ec: ExecutionContext             =
//        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)
//
//      val expectedFailure = SomeError.message("Failed")
//      val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
//      val expected        = expectedFailure.asLeft[Int]
//      val actual          = ConcurrentSupport.futureToValueAndTerminate(
//        executorService,
//        waitFor
//      )(CanCatch[Future].catchNonFatalEither(fa)(SomeError.someThrowable))
//
//      actual ==== expected
//    }

    def testCanCatch_Future_catchNonFatalEitherTShouldCatchNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(CanCatch[Future].catchNonFatalEitherT(fa)(SomeError.someThrowable).value)

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(CanCatch[Future].catchNonFatalEitherT(fa)(SomeError.someThrowable).value)

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalEitherTShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(CanCatch[Future].catchNonFatalEitherT(fa)(SomeError.someThrowable).value)

      actual ==== expected
    }
  }

}
