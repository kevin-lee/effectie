package effectie.syntax

import cats.data.EitherT
import cats.syntax.all._
import effectie.SomeControlThrowable
import effectie.core.FxCtor
import effectie.instances.monix3.fx._
import effectie.syntax.error._
import effectie.syntax.fx._
import effectie.testing.types._
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog._
import hedgehog.runner._

import scala.util.control.{ControlThrowable, NonFatal}
import monix.eval.Task

/** @author Kevin Lee
  * @since 2021-10-30
  */
object errorSpec extends Properties {

  override def tests: List[Test] =
    CanCatchSyntaxSpec.tests ++ CanHandleErrorSyntaxSpec.tests ++ CanRecoverSyntaxSpec.tests ++ OnNonFatalSyntaxSpec.tests

}

object CanCatchSyntaxSpec {

  def tests: List[Test] = List(
    /* Task */
    example(
      "test CanCatch[Task]catchNonFatalThrowable should catch NonFatal",
      TaskSpec.testCanCatch_Task_catchNonFatalThrowableShouldCatchNonFatal,
    ),
    example(
      "test CanCatch[Task]catchNonFatalThrowable should not catch Fatal",
      TaskSpec.testCanCatch_Task_catchNonFatalThrowableShouldNotCatchFatal,
    ),
    example(
      "test CanCatch[Task]catchNonFatalThrowable should return the successful result",
      TaskSpec.testCanCatch_Task_catchNonFatalThrowableShouldReturnSuccessfulResult,
    ),
    example(
      "test CanCatch[Task]catchNonFatal should catch NonFatal",
      TaskSpec.testCanCatch_Task_catchNonFatalShouldCatchNonFatal,
    ),
    example(
      "test CanCatch[Task]catchNonFatal should not catch Fatal",
      TaskSpec.testCanCatch_Task_catchNonFatalShouldNotCatchFatal,
    ),
    example(
      "test CanCatch[Task]catchNonFatal should return the successful result",
      TaskSpec.testCanCatch_Task_catchNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanCatch[Task]catchNonFatalEither should catch NonFatal",
      TaskSpec.testCanCatch_Task_catchNonFatalEitherShouldCatchNonFatal,
    ),
    example(
      "test CanCatch[Task]catchNonFatalEither should not catch Fatal",
      TaskSpec.testCanCatch_Task_catchNonFatalEitherShouldNotCatchFatal,
    ),
    example(
      "test CanCatch[Task]catchNonFatalEither should return the successful result",
      TaskSpec.testCanCatch_Task_catchNonFatalEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanCatch[Task]catchNonFatalEither should return the failed result",
      TaskSpec.testCanCatch_Task_catchNonFatalEitherShouldReturnFailedResult,
    ),
    example(
      "test CanCatch[Task]catchNonFatalEitherT should catch NonFatal",
      TaskSpec.testCanCatch_Task_catchNonFatalEitherTShouldCatchNonFatal,
    ),
    example(
      "test CanCatch[Task]catchNonFatalEitherT should not catch Fatal",
      TaskSpec.testCanCatch_Task_catchNonFatalEitherTShouldNotCatchFatal,
    ),
    example(
      "test CanCatch[Task]catchNonFatalEitherT should return the successful result",
      TaskSpec.testCanCatch_Task_catchNonFatalEitherTShouldReturnSuccessfulResult,
    ),
    example(
      "test CanCatch[Task]catchNonFatalEitherT should return the failed result",
      TaskSpec.testCanCatch_Task_catchNonFatalEitherTShouldReturnFailedResult,
    ),
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
    effectOf[F](a)

  object TaskSpec {
    import monix.execution.Scheduler.Implicits.global

    def testCanCatch_Task_catchNonFatalThrowableShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = expectedExpcetion.asLeft[Int]
      val actual            = fa.catchNonFatalThrowable.runSyncUnsafe()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanCatch_Task_catchNonFatalThrowableShouldNotCatchFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[Task, Int](throwThrowable[Int](fatalExpcetion))

      try {
        val actual = fa.catchNonFatalThrowable.runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: SomeControlThrowable =>
          ex.getMessage ==== fatalExpcetion.getMessage

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_Task_catchNonFatalThrowableShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Int](1)
      val expected = 1.asRight[Throwable]
      val actual   = fa.catchNonFatalThrowable.runSyncUnsafe()

      actual ==== expected
    }

    def testCanCatch_Task_catchNonFatalShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = fa.catchNonFatal(SomeError.someThrowable).runSyncUnsafe()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanCatch_Task_catchNonFatalShouldNotCatchFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[Task, Int](throwThrowable[Int](fatalExpcetion))

      try {
        val actual = fa.catchNonFatal(SomeError.someThrowable).runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: SomeControlThrowable =>
          ex.getMessage ==== fatalExpcetion.getMessage

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_Task_catchNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Int](1)
      val expected = 1.asRight[SomeError]
      val actual   = fa.catchNonFatal(SomeError.someThrowable).runSyncUnsafe()

      actual ==== expected
    }

    def testCanCatch_Task_catchNonFatalEitherShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa       = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual   = fa.catchNonFatalEither(SomeError.someThrowable).runSyncUnsafe()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanCatch_Task_catchNonFatalEitherShouldNotCatchFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual = fa.catchNonFatalEither(SomeError.someThrowable).runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: SomeControlThrowable =>
          ex.getMessage ==== fatalExpcetion.getMessage

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_Task_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = fa.catchNonFatalEither(SomeError.someThrowable).runSyncUnsafe()

      actual ==== expected
    }

    def testCanCatch_Task_catchNonFatalEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = fa.catchNonFatalEither(SomeError.someThrowable).runSyncUnsafe()

      actual ==== expected
    }

    def testCanCatch_Task_catchNonFatalEitherTShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual   = fa.catchNonFatalEitherT(SomeError.someThrowable).value.runSyncUnsafe()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanCatch_Task_catchNonFatalEitherTShouldNotCatchFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

      try {
        val actual = fa.catchNonFatalEitherT(SomeError.someThrowable).value.runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: SomeControlThrowable =>
          ex.getMessage ==== fatalExpcetion.getMessage

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_Task_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = {

      val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = fa.catchNonFatalEitherT(SomeError.someThrowable).value.runSyncUnsafe()

      actual ==== expected
    }

    def testCanCatch_Task_catchNonFatalEitherTShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          = fa.catchNonFatalEitherT(SomeError.someThrowable).value.runSyncUnsafe()

      actual ==== expected
    }

  }

  object FutureSpec {
    import effectie.instances.future.canCatch._
    import effectie.instances.future.fxCtor._

    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration._
    import scala.concurrent.{ExecutionContext, Future}

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
      )(fa.catchNonFatalEitherT(SomeError.someThrowable).value)

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
      )(fa.catchNonFatalEitherT(SomeError.someThrowable).value)

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
      )(fa.catchNonFatalEitherT(SomeError.someThrowable).value)

      actual ==== expected
    }
  }

}

object CanHandleErrorSyntaxSpec {

  def tests: List[Test] = List(
    /* Task */
    example(
      "test CanHandleError[Task].handleNonFatalWith should handle NonFatal",
      TaskSpec.testCanHandleError_Task_handleNonFatalWithShouldHandleNonFatalWith,
    ),
    example(
      "test CanHandleError[Task].handleNonFatalWith should not handle Fatal",
      TaskSpec.testCanHandleError_Task_handleNonFatalWithShouldNotHandleFatalWith,
    ),
    example(
      "test CanHandleError[Task].handleNonFatalWith should return the successful result",
      TaskSpec.testCanHandleError_Task_handleNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Task].handleNonFatalWithEither should handle NonFatal",
      TaskSpec.testCanHandleError_Task_handleNonFatalWithEitherShouldHandleNonFatalWith,
    ),
    example(
      "test CanHandleError[Task].handleNonFatalWithEither should not handle Fatal",
      TaskSpec.testCanHandleError_Task_handleNonFatalWithEitherShouldNotHandleFatalWith,
    ),
    example(
      "test CanHandleError[Task].handleNonFatalWithEither should return the successful result",
      TaskSpec.testCanHandleError_Task_handleNonFatalWithEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Task].handleNonFatalWithEither should return the failed result",
      TaskSpec.testCanHandleError_Task_handleNonFatalWithEitherShouldReturnFailedResult,
    ),
    example(
      "test CanHandleError[Task].handleEitherNonFatalWith should handle NonFatal",
      TaskSpec.testCanHandleError_Task_handleEitherNonFatalWithShouldHandleNonFatalWith,
    ),
    example(
      "test CanHandleError[Task].handleEitherNonFatalWith should not handle Fatal",
      TaskSpec.testCanHandleError_Task_handleEitherNonFatalWithShouldNotHandleFatalWith,
    ),
    example(
      "test CanHandleError[Task].handleEitherNonFatalWith should return the successful result",
      TaskSpec.testCanHandleError_Task_handleEitherNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Task].handleEitherNonFatalWith should return the failed result",
      TaskSpec.testCanHandleError_Task_handleEitherNonFatalWithShouldReturnFailedResult,
    ),
    example(
      "test CanHandleError[Task].handleEitherTNonFatalWith should handle NonFatal",
      TaskSpec.testCanHandleError_Task_handleEitherTNonFatalWithShouldHandleNonFatalWith,
    ),
    example(
      "test CanHandleError[Task].handleEitherTNonFatalWith should not handle Fatal",
      TaskSpec.testCanHandleError_Task_handleEitherTNonFatalWithShouldNotHandleFatalWith,
    ),
    example(
      "test CanHandleError[Task].handleEitherTNonFatalWith should return the successful result",
      TaskSpec.testCanHandleError_Task_handleEitherTNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Task].handleEitherTNonFatalWith should return the failed result",
      TaskSpec.testCanHandleError_Task_handleEitherTNonFatalWithShouldReturnFailedResult,
    ),
    example(
      "test CanHandleError[Task].handleNonFatal should handle NonFatal",
      TaskSpec.testCanHandleError_Task_handleNonFatalShouldHandleNonFatal,
    ),
    example(
      "test CanHandleError[Task].handleNonFatal should not handle Fatal",
      TaskSpec.testCanHandleError_Task_handleNonFatalShouldNotHandleFatal,
    ),
    example(
      "test CanHandleError[Task].handleNonFatal should return the successful result",
      TaskSpec.testCanHandleError_Task_handleNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Task].handleNonFatalEither should handle NonFatal",
      TaskSpec.testCanHandleError_Task_handleNonFatalEitherShouldHandleNonFatal,
    ),
    example(
      "test CanHandleError[Task].handleNonFatalEither should not handle Fatal",
      TaskSpec.testCanHandleError_Task_handleNonFatalEitherShouldNotHandleFatal,
    ),
    example(
      "test CanHandleError[Task].handleNonFatalEither should return the successful result",
      TaskSpec.testCanHandleError_Task_handleNonFatalEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Task].handleNonFatalEither should return the failed result",
      TaskSpec.testCanHandleError_Task_handleNonFatalEitherShouldReturnFailedResult,
    ),
    example(
      "test CanHandleError[Task].handleEitherNonFatal should handle NonFatal",
      TaskSpec.testCanHandleError_Task_handleEitherNonFatalShouldHandleNonFatal,
    ),
    example(
      "test CanHandleError[Task].handleEitherNonFatal should not handle Fatal",
      TaskSpec.testCanHandleError_Task_handleEitherNonFatalShouldNotHandleFatal,
    ),
    example(
      "test CanHandleError[Task].handleEitherNonFatal should return the successful result",
      TaskSpec.testCanHandleError_Task_handleEitherNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Task].handleEitherNonFatal should return the failed result",
      TaskSpec.testCanHandleError_Task_handleEitherNonFatalShouldReturnFailedResult,
    ),
    example(
      "test CanHandleError[Task].handleEitherTNonFatal should handle NonFatal",
      TaskSpec.testCanHandleError_Task_handleEitherTNonFatalShouldHandleNonFatal,
    ),
    example(
      "test CanHandleError[Task].handleEitherTNonFatal should not handle Fatal",
      TaskSpec.testCanHandleError_Task_handleEitherTNonFatalShouldNotHandleFatal,
    ),
    example(
      "test CanHandleError[Task].handleEitherTNonFatal should return the successful result",
      TaskSpec.testCanHandleError_Task_handleEitherTNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Task].handleEitherTNonFatal should return the failed result",
      TaskSpec.testCanHandleError_Task_handleEitherTNonFatalShouldReturnFailedResult,
    ),
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
      "test CanHandleError[Future].handleEitherTNonFatalWith should handle NonFatal",
      FutureSpec.testCanHandleError_Future_handleEitherTNonFatalWithShouldHandleNonFatalWith,
    ),
    example(
      "test CanHandleError[Future].handleEitherTNonFatalWith should return the successful result",
      FutureSpec.testCanHandleError_Future_handleEitherTNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Future].handleEitherTNonFatalWith should return the failed result",
      FutureSpec.testCanHandleError_Future_handleEitherTNonFatalWithShouldReturnFailedResult,
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
      "test CanHandleError[Future].handleEitherTNonFatal should handle NonFatal",
      FutureSpec.testCanHandleError_Future_handleEitherTNonFatalShouldHandleNonFatal,
    ),
    example(
      "test CanHandleError[Future].handleEitherTNonFatal should return the successful result",
      FutureSpec.testCanHandleError_Future_handleEitherTNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Future].handleEitherTNonFatal should return the failed result",
      FutureSpec.testCanHandleError_Future_handleEitherTNonFatalShouldReturnFailedResult,
    ),
  )

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor, A](a: => A): F[A] =
    effectOf[F](a)

  object TaskSpec {
    import monix.execution.Scheduler.Implicits.global

    def testCanHandleError_Task_handleNonFatalWithShouldHandleNonFatalWith: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 123
      val actual            = fa
        .handleNonFatalWith {
          case NonFatal(`expectedExpcetion`) =>
            Task.pure(expected)
          case err =>
            throw err // scalafix:ok DisableSyntax.throw
        }
        .runSyncUnsafe()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Task_handleNonFatalWithShouldNotHandleFatalWith: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[Task, Int](throwThrowable[Int](fatalExpcetion))

      try {
        val actual = fa.handleNonFatalWith(_ => Task.pure(123)).runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Task_handleNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Int](1)
      val expected = 1
      val actual   = fa.handleNonFatalWith(_ => Task.pure(999)).runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleNonFatalWithEitherShouldHandleNonFatalWith: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
      val actualFailedResult   =
        fa.handleNonFatalWith(_ => Task.pure(expectedFailedResult)).runSyncUnsafe()

      val expectedSuccessResult = 1.asRight[SomeError]
      val actualSuccessResult   =
        fa.handleNonFatalWith(_ => Task.pure(1.asRight[SomeError])).runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Task_handleNonFatalWithEitherShouldNotHandleFatalWith: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual = fa.handleNonFatalWith(_ => Task.pure(123.asRight[SomeError])).runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: SomeControlThrowable =>
          ex.getMessage ==== fatalExpcetion.getMessage

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Task_handleNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = fa.handleNonFatalWith(_ => Task(999.asRight[SomeError])).runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleNonFatalWithEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = fa.handleNonFatalWith(_ => Task.pure(123.asRight[SomeError])).runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleEitherNonFatalWithShouldHandleNonFatalWith: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult    = fa
        .handleEitherNonFatalWith(err => Task.pure(SomeError.someThrowable(err).asLeft[Int]))
        .runSyncUnsafe()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        fa.handleEitherNonFatalWith(_ => Task.pure(123.asRight[SomeError])).runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Task_handleEitherNonFatalWithShouldNotHandleFatalWith: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual =
          fa.handleEitherNonFatalWith(err => Task.pure(SomeError.someThrowable(err).asLeft[Int]))
            .runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Task_handleEitherNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   =
        fa.handleEitherNonFatalWith(_ => Task.pure(123.asRight[SomeError])).runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleEitherNonFatalWithShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        fa.handleEitherNonFatalWith(_ => Task.pure(123.asRight[SomeError])).runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleEitherTNonFatalWithShouldHandleNonFatalWith: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult    = fa
        .handleEitherTNonFatalWith(err => Task.pure(SomeError.someThrowable(err).asLeft[Int]))
        .value
        .runSyncUnsafe()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        fa.handleEitherTNonFatalWith(_ => Task.pure(123.asRight[SomeError])).value.runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Task_handleEitherTNonFatalWithShouldNotHandleFatalWith: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

      try {
        val actual =
          fa.handleEitherTNonFatalWith(err => Task.pure(SomeError.someThrowable(err).asLeft[Int]))
            .value
            .runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Task_handleEitherTNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   =
        fa.handleEitherTNonFatalWith(_ => Task.pure(123.asRight[SomeError])).value.runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleEitherTNonFatalWithShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        fa.handleEitherTNonFatalWith(_ => Task.pure(123.asRight[SomeError])).value.runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleNonFatalShouldHandleNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 123
      val actual            = fa
        .handleNonFatal {
          case NonFatal(`expectedExpcetion`) =>
            expected
          case err =>
            throw err // scalafix:ok DisableSyntax.throw
        }
        .runSyncUnsafe()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Task_handleNonFatalShouldNotHandleFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[Task, Int](throwThrowable[Int](fatalExpcetion))

      try {
        val actual = fa.handleNonFatal(_ => 123).runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Task_handleNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Int](1)
      val expected = 1
      val actual   = fa.handleNonFatal(_ => 999).runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleNonFatalEitherShouldHandleNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
      val actualFailedResult   = fa.handleNonFatal(_ => expectedFailedResult).runSyncUnsafe()

      val expectedSuccessResult = 1.asRight[SomeError]
      val actualSuccessResult   = fa.handleNonFatal(_ => 1.asRight[SomeError]).runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Task_handleNonFatalEitherShouldNotHandleFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual = fa.handleNonFatal(_ => 123.asRight[SomeError]).runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Task_handleNonFatalEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = fa.handleNonFatal(_ => 999.asRight[SomeError]).runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleNonFatalEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = fa.handleNonFatal(_ => 123.asRight[SomeError]).runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleEitherNonFatalShouldHandleNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult    = fa
        .handleEitherNonFatal(err => SomeError.someThrowable(err).asLeft[Int])
        .runSyncUnsafe()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        fa.handleEitherNonFatal(_ => 123.asRight[SomeError]).runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Task_handleEitherNonFatalShouldNotHandleFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual =
          fa.handleEitherNonFatal(err => SomeError.someThrowable(err).asLeft[Int])
            .runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Task_handleEitherNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = fa.handleEitherNonFatal(_ => 123.asRight[SomeError]).runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleEitherNonFatalShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        fa.handleEitherNonFatal(_ => 123.asRight[SomeError]).runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleEitherTNonFatalShouldHandleNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult    = fa
        .handleEitherTNonFatal(err => SomeError.someThrowable(err).asLeft[Int])
        .value
        .runSyncUnsafe()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        fa.handleEitherTNonFatal(_ => 123.asRight[SomeError]).value.runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Task_handleEitherTNonFatalShouldNotHandleFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

      try {
        val actual =
          fa.handleEitherTNonFatal(err => SomeError.someThrowable(err).asLeft[Int])
            .value
            .runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Task_handleEitherTNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = fa.handleEitherTNonFatal(_ => 123.asRight[SomeError]).value.runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleEitherTNonFatalShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        fa.handleEitherTNonFatal(_ => 123.asRight[SomeError]).value.runSyncUnsafe()

      actual ==== expected
    }

  }

  object FutureSpec {
    import effectie.instances.future.canHandleError._
    import effectie.instances.future.fxCtor._

    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration._
    import scala.concurrent.{ExecutionContext, Future}

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

    def testCanHandleError_Future_handleEitherTNonFatalWithShouldHandleNonFatalWith: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   = ConcurrentSupport.futureToValue(
        fa.handleEitherTNonFatalWith(err => Future(SomeError.someThrowable(err).asLeft[Int])).value,
        waitFor,
      )

      val fa2 = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(fa2.handleEitherTNonFatalWith(_ => Future(expected)).value)

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanHandleError_Future_handleEitherTNonFatalWithShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(fa.handleEitherTNonFatalWith(err => Future(SomeError.someThrowable(err).asLeft[Int])).value)

      actual ==== expected
    }

    def testCanHandleError_Future_handleEitherTNonFatalWithShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(fa.handleEitherTNonFatalWith(_ => Future(expected)).value)

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

    def testCanHandleError_Future_handleEitherTNonFatalShouldHandleNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   = ConcurrentSupport.futureToValue(
        fa.handleEitherTNonFatal(err => SomeError.someThrowable(err).asLeft[Int]).value,
        waitFor,
      )

      val fa2 = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(fa2.handleEitherTNonFatal(_ => expected).value)

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanHandleError_Future_handleEitherTNonFatalShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(fa.handleEitherTNonFatal(err => SomeError.someThrowable(err).asLeft[Int]).value)

      actual ==== expected
    }

    def testCanHandleError_Future_handleEitherTNonFatalShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(fa.handleEitherTNonFatal(_ => expected).value)

      actual ==== expected
    }

  }

}

object CanRecoverSyntaxSpec {

  def tests: List[Test] = List(
    /* Task */
    example(
      "test CanRecover[Task].recoverFromNonFatalWith should catch NonFatal",
      TaskSpec.testCanRecover_Task_recoverFromNonFatalWithShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Task].recoverFromNonFatalWith should not catch Fatal",
      TaskSpec.testCanRecover_Task_recoverFromNonFatalWithShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Task].recoverFromNonFatalWith should return the successful result",
      TaskSpec.testCanRecover_Task_recoverFromNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Task].recoverFromNonFatalWithEither should catch NonFatal",
      TaskSpec.testCanRecover_Task_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Task].recoverFromNonFatalWithEither should not catch Fatal",
      TaskSpec.testCanRecover_Task_recoverFromNonFatalWithEitherShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Task].recoverFromNonFatalWithEither should return the successful result",
      TaskSpec.testCanRecover_Task_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Task].recoverFromNonFatalWithEither should return the failed result",
      TaskSpec.testCanRecover_Task_recoverFromNonFatalWithEitherShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[Task].recoverEitherFromNonFatalWith should catch NonFatal",
      TaskSpec.testCanRecover_Task_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Task].recoverEitherFromNonFatalWith should not catch Fatal",
      TaskSpec.testCanRecover_Task_recoverEitherFromNonFatalWithShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Task].recoverEitherFromNonFatalWith should return the successful result",
      TaskSpec.testCanRecover_Task_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Task].recoverEitherFromNonFatalWith should return the failed result",
      TaskSpec.testCanRecover_Task_recoverEitherFromNonFatalWithShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[Task].recoverEitherTFromNonFatalWith should catch NonFatal",
      TaskSpec.testCanRecover_Task_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Task].recoverEitherTFromNonFatalWith should not catch Fatal",
      TaskSpec.testCanRecover_Task_recoverEitherTFromNonFatalWithShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Task].recoverEitherTFromNonFatalWith should return the successful result",
      TaskSpec.testCanRecover_Task_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Task].recoverEitherTFromNonFatalWith should return the failed result",
      TaskSpec.testCanRecover_Task_recoverEitherTFromNonFatalWithShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[Task].recoverFromNonFatal should catch NonFatal",
      TaskSpec.testCanRecover_Task_recoverFromNonFatalShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Task].recoverFromNonFatal should not catch Fatal",
      TaskSpec.testCanRecover_Task_recoverFromNonFatalShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Task].recoverFromNonFatal should return the successful result",
      TaskSpec.testCanRecover_Task_recoverFromNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Task].recoverFromNonFatalEither should catch NonFatal",
      TaskSpec.testCanRecover_Task_recoverFromNonFatalEitherShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Task].recoverFromNonFatalEither should not catch Fatal",
      TaskSpec.testCanRecover_Task_recoverFromNonFatalEitherShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Task].recoverFromNonFatalEither should return the successful result",
      TaskSpec.testCanRecover_Task_recoverFromNonFatalEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Task].recoverFromNonFatalEither should return the failed result",
      TaskSpec.testCanRecover_Task_recoverFromNonFatalEitherShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[Task].recoverEitherFromNonFatal should catch NonFatal",
      TaskSpec.testCanRecover_Task_recoverEitherFromNonFatalShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Task].recoverEitherFromNonFatal should not catch Fatal",
      TaskSpec.testCanRecover_Task_recoverEitherFromNonFatalShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Task].recoverEitherFromNonFatal should return the successful result",
      TaskSpec.testCanRecover_Task_recoverEitherFromNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Task].recoverEitherFromNonFatal should return the failed result",
      TaskSpec.testCanRecover_Task_recoverEitherFromNonFatalShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[Task].recoverEitherTFromNonFatal should catch NonFatal",
      TaskSpec.testCanRecover_Task_recoverEitherTFromNonFatalShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Task].recoverEitherTFromNonFatal should not catch Fatal",
      TaskSpec.testCanRecover_Task_recoverEitherTFromNonFatalShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Task].recoverEitherTFromNonFatal should return the successful result",
      TaskSpec.testCanRecover_Task_recoverEitherTFromNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Task].recoverEitherTFromNonFatal should return the failed result",
      TaskSpec.testCanRecover_Task_recoverEitherTFromNonFatalShouldReturnFailedResult,
    ),
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
      "test CanRecover[Future].recoverEitherTFromNonFatalWith should catch NonFatal",
      FutureSpec.testCanRecover_Future_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Future].recoverEitherTFromNonFatalWith should return the successful result",
      FutureSpec.testCanRecover_Future_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Future].recoverEitherTFromNonFatalWith should return the failed result",
      FutureSpec.testCanRecover_Future_recoverEitherTFromNonFatalWithShouldReturnFailedResult,
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
    example(
      "test CanRecover[Future].recoverEitherTFromNonFatal should catch NonFatal",
      FutureSpec.testCanRecover_Future_recoverEitherTFromNonFatalShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Future].recoverEitherTFromNonFatal should return the successful result",
      FutureSpec.testCanRecover_Future_recoverEitherTFromNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Future].recoverEitherTFromNonFatal should return the failed result",
      FutureSpec.testCanRecover_Future_recoverEitherTFromNonFatalShouldReturnFailedResult,
    ),
  )

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor, A](a: => A): F[A] =
    effectOf[F](a)

  object TaskSpec {
    import monix.execution.Scheduler.Implicits.global

    def testCanRecover_Task_recoverFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 123
      val actual            = fa
        .recoverFromNonFatalWith {
          case NonFatal(`expectedExpcetion`) =>
            Task.pure(expected)
        }
        .runSyncUnsafe()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_Task_recoverFromNonFatalWithShouldNotCatchFatal: Result = {

      val expectedExpcetion = SomeControlThrowable("Something's wrong")
      val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))

      val io = fa.recoverFromNonFatalWith { case NonFatal(`expectedExpcetion`) => Task.pure(123) }
      try {
        val actual = io.runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Task_recoverFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Int](1)
      val expected = 1
      val actual   = fa
        .recoverFromNonFatalWith {
          case NonFatal(_) => Task.pure(999)
        }
        .runSyncUnsafe()

      actual ==== expected
    }

    def testCanRecover_Task_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
      val actualFailedResult   = fa
        .recoverFromNonFatalWith {
          case NonFatal(`expectedExpcetion`) => Task.pure(expectedFailedResult)
        }
        .runSyncUnsafe()

      val expectedSuccessResult = 1.asRight[SomeError]
      val actualSuccessResult   = fa
        .recoverFromNonFatalWith {
          case NonFatal(`expectedExpcetion`) => Task.pure(1.asRight[SomeError])
        }
        .runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_Task_recoverFromNonFatalWithEitherShouldNotCatchFatal: Result = {

      val expectedExpcetion = SomeControlThrowable("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

      val io = fa.recoverFromNonFatalWith {
        case NonFatal(`expectedExpcetion`) => Task.pure(123.asRight[SomeError])
      }
      try {
        val actual = io.runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Task_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = fa
        .recoverFromNonFatalWith {
          case NonFatal(_) => Task(999.asRight[SomeError])
        }
        .runSyncUnsafe()

      actual ==== expected
    }

    def testCanRecover_Task_recoverFromNonFatalWithEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = fa
        .recoverFromNonFatalWith {
          case NonFatal(_) => Task.pure(123.asRight[SomeError])
        }
        .runSyncUnsafe()

      actual ==== expected
    }

    def testCanRecover_Task_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult    = fa
        .recoverEitherFromNonFatalWith {
          case err => Task.pure(SomeError.someThrowable(err).asLeft[Int])
        }
        .runSyncUnsafe()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   = fa
        .recoverEitherFromNonFatalWith {
          case NonFatal(`expectedExpcetion`) => Task.pure(123.asRight[SomeError])
        }
        .runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_Task_recoverEitherFromNonFatalWithShouldNotCatchFatal: Result = {

      val expectedExpcetion = SomeControlThrowable("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

      val io = fa.recoverEitherFromNonFatalWith {
        case err => Task.pure(SomeError.someThrowable(err).asLeft[Int])
      }
      try {
        val actual = io.runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Task_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = fa
        .recoverEitherFromNonFatalWith {
          case NonFatal(_) => Task.pure(123.asRight[SomeError])
        }
        .runSyncUnsafe()

      actual ==== expected
    }

    def testCanRecover_Task_recoverEitherFromNonFatalWithShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        fa.recoverEitherFromNonFatalWith {
          case NonFatal(_) => Task.pure(123.asRight[SomeError])
        }.runSyncUnsafe()

      actual ==== expected
    }

    def testCanRecover_Task_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult    = fa
        .recoverEitherTFromNonFatalWith {
          case err => Task.pure(SomeError.someThrowable(err).asLeft[Int])
        }
        .value
        .runSyncUnsafe()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   = fa
        .recoverEitherTFromNonFatalWith {
          case NonFatal(`expectedExpcetion`) => Task.pure(123.asRight[SomeError])
        }
        .value
        .runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_Task_recoverEitherTFromNonFatalWithShouldNotCatchFatal: Result = {

      val expectedExpcetion = SomeControlThrowable("Something's wrong")
      val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))

      val io = fa.recoverEitherTFromNonFatalWith {
        case err => Task.pure(SomeError.someThrowable(err).asLeft[Int])
      }
      try {
        val actual = io.value.runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Task_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = fa
        .recoverEitherTFromNonFatalWith {
          case NonFatal(_) => Task.pure(123.asRight[SomeError])
        }
        .value
        .runSyncUnsafe()

      actual ==== expected
    }

    def testCanRecover_Task_recoverEitherTFromNonFatalWithShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        fa.recoverEitherTFromNonFatalWith {
          case NonFatal(_) => Task.pure(123.asRight[SomeError])
        }.value
          .runSyncUnsafe()

      actual ==== expected
    }

    // /

    def testCanRecover_Task_recoverFromNonFatalShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 123
      val actual            = fa
        .recoverFromNonFatal {
          case NonFatal(`expectedExpcetion`) =>
            expected
        }
        .runSyncUnsafe()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_Task_recoverFromNonFatalShouldNotCatchFatal: Result = {

      val expectedExpcetion = SomeControlThrowable("Something's wrong")
      val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))

      val io = fa.recoverFromNonFatal { case NonFatal(`expectedExpcetion`) => 123 }
      try {
        val actual = io.runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Task_recoverFromNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Int](1)
      val expected = 1
      val actual   = fa.recoverFromNonFatal { case NonFatal(_) => 999 }.runSyncUnsafe()

      actual ==== expected
    }

    def testCanRecover_Task_recoverFromNonFatalEitherShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
      val actualFailedResult   = fa
        .recoverFromNonFatal { case NonFatal(`expectedExpcetion`) => expectedFailedResult }
        .runSyncUnsafe()

      val expectedSuccessResult = 1.asRight[SomeError]
      val actualSuccessResult   = fa
        .recoverFromNonFatal { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }
        .runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_Task_recoverFromNonFatalEitherShouldNotCatchFatal: Result = {

      val expectedExpcetion = SomeControlThrowable("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

      val io = fa.recoverFromNonFatal { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }
      try {
        val actual = io.runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Task_recoverFromNonFatalEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = fa.recoverFromNonFatal { case NonFatal(_) => 999.asRight[SomeError] }.runSyncUnsafe()

      actual ==== expected
    }

    def testCanRecover_Task_recoverFromNonFatalEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = fa.recoverFromNonFatal { case NonFatal(_) => 123.asRight[SomeError] }.runSyncUnsafe()

      actual ==== expected
    }

    def testCanRecover_Task_recoverEitherFromNonFatalShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult    =
        fa.recoverEitherFromNonFatal {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }.runSyncUnsafe()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        fa.recoverEitherFromNonFatal { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }
          .runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_Task_recoverEitherFromNonFatalShouldNotCatchFatal: Result = {

      val expectedExpcetion = SomeControlThrowable("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

      val io =
        fa.recoverEitherFromNonFatal {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
      try {
        val actual = io.runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Task_recoverEitherFromNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   =
        fa.recoverEitherFromNonFatal { case NonFatal(_) => 123.asRight[SomeError] }
          .runSyncUnsafe()

      actual ==== expected
    }

    def testCanRecover_Task_recoverEitherFromNonFatalShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        fa.recoverEitherFromNonFatal { case NonFatal(_) => 123.asRight[SomeError] }
          .runSyncUnsafe()

      actual ==== expected
    }

    def testCanRecover_Task_recoverEitherTFromNonFatalShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult    =
        fa.recoverEitherTFromNonFatal {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }.value
          .runSyncUnsafe()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        fa.recoverEitherTFromNonFatal { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }
          .value
          .runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_Task_recoverEitherTFromNonFatalShouldNotCatchFatal: Result = {

      val expectedExpcetion = SomeControlThrowable("Something's wrong")
      val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))

      val io =
        fa.recoverEitherTFromNonFatal {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
      try {
        val actual = io.value.runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Task_recoverEitherTFromNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   =
        fa.recoverEitherTFromNonFatal { case NonFatal(_) => 123.asRight[SomeError] }
          .value
          .runSyncUnsafe()

      actual ==== expected
    }

    def testCanRecover_Task_recoverEitherTFromNonFatalShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        fa.recoverEitherTFromNonFatal { case NonFatal(_) => 123.asRight[SomeError] }
          .value
          .runSyncUnsafe()

      actual ==== expected
    }

  }

  object FutureSpec {
    import effectie.instances.future.canRecover._
    import effectie.instances.future.fxCtor._

    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration._
    import scala.concurrent.{ExecutionContext, Future}
    import scala.util.control.NonFatal

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

    def testCanRecover_Future_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   = ConcurrentSupport.futureToValue(
        fa.recoverEitherTFromNonFatalWith {
          case err => Future(SomeError.someThrowable(err).asLeft[Int])
        }.value,
        waitFor,
      )

      val fa2 = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(fa2.recoverEitherTFromNonFatalWith {
          case err @ _ => Future(expected)
        }.value)

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(fa.recoverEitherTFromNonFatalWith {
        case err => Future(SomeError.someThrowable(err).asLeft[Int])
      }.value)

      actual ==== expected
    }

    def testCanRecover_Future_recoverEitherTFromNonFatalWithShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(fa.recoverEitherTFromNonFatalWith {
          case NonFatal(_) => Future(expected)
        }.value)

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
      val actualFailedResult   =
        ConcurrentSupport.futureToValue(
          fa.recoverFromNonFatal {
            case err => SomeError.someThrowable(err).asLeft[Int]
          },
          waitFor,
        )

      val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(fa2.recoverFromNonFatal { case NonFatal(`expectedExpcetion`) => expected })

      expectedFailedResult ==== actualFailedResult and actual ==== expected
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

    def testCanRecover_Future_recoverEitherTFromNonFatalShouldRecoverFromNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   = ConcurrentSupport.futureToValue(
        fa.recoverEitherTFromNonFatal {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }.value,
        waitFor,
      )

      val fa2 = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(fa2.recoverEitherTFromNonFatal { case err @ _ => expected }.value)

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverEitherTFromNonFatalShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(fa.recoverEitherTFromNonFatal {
        case err => SomeError.someThrowable(err).asLeft[Int]
      }.value)

      actual ==== expected
    }

    def testCanRecover_Future_recoverEitherTFromNonFatalShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(fa.recoverEitherTFromNonFatal { case NonFatal(_) => expected }.value)

      actual ==== expected
    }
  }

}

object OnNonFatalSyntaxSpec {

  def tests: List[Test] = List(
    /* Task */
    example(
      "test OnNonFatal[Task].onNonFatalWith should do something for NonFatal",
      TaskSpec.testOnNonFatal_Task_onNonFatalWithShouldDoSomethingForNonFatal,
    ),
    example(
      "test OnNonFatal[Task].onNonFatalWith should not do anything and not catch Fatal",
      TaskSpec.testOnNonFatal_Task_onNonFatalWithShouldNotCatchFatal,
    ),
    example(
      "test OnNonFatal[Task].onNonFatalWith should not do anything for the successful result",
      TaskSpec.testOnNonFatal_Task_onNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test Task[Either[A, B]].onNonFatalWith should do something for NonFatal",
      TaskSpec.testOnNonFatal_TaskEitherAB_onNonFatalWithEitherShouldDoSomethingForNonFatal,
    ),
    example(
      "test Task[Either[A, B]].onNonFatalWith should do nothing for success case with Right",
      TaskSpec.testOnNonFatal_TaskEitherAB_onNonFatalWithEitherShouldDoNothingForRightCase,
    ),
    example(
      "test Task[Either[A, B]].onNonFatalWith should do nothing for success case with Left",
      TaskSpec.testOnNonFatal_TaskEitherAB_onNonFatalWithEitherShouldDoNothingForLeftCase,
    ),
    example(
      "test EitherT[F, A, B].onNonFatalWith should do something for NonFatal",
      TaskSpec.testOnNonFatal_EitherT_onNonFatalWithEitherShouldDoSomethingForNonFatal,
    ),
    example(
      "test EitherT[F, A, B](F(Right(b))).onNonFatalWith should do nothing for success case with Right",
      TaskSpec.testOnNonFatal_EitherT_onNonFatalWithEitherShouldDoNothingForRightCase,
    ),
    example(
      "test EitherT[F, A, B](F(Left(a))).onNonFatalWith should do nothing for success case with Left",
      TaskSpec.testOnNonFatal_EitherT_onNonFatalWithEitherShouldDoNothingForLeftCase,
    ),
  )

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor, A](a: => A): F[A] =
    effectOf[F](a)

  object TaskSpec {
    import monix.execution.Scheduler.Implicits.global

    import effectie.instances.monix3.fx.taskFx

    def testOnNonFatal_Task_onNonFatalWithShouldDoSomethingForNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 123.some
      var actual            = none[Int] // scalafix:ok DisableSyntax.var

      val result =
        try {
          val r = fa
            .onNonFatalWith {
              case NonFatal(`expectedExpcetion`) =>
                Task {
                  actual = expected
                } *> Task.unit
            }
            .runSyncUnsafe()
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

    def testOnNonFatal_Task_onNonFatalWithShouldNotCatchFatal: Result = {

      val expectedExpcetion = SomeControlThrowable("Something's wrong")
      val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))
      var actual            = none[Int] // scalafix:ok DisableSyntax.var

      try {
        val r = fa
          .onNonFatalWith {
            case NonFatal(`expectedExpcetion`) =>
              Task {
                actual = 123.some
                ()
              } *> Task.unit
          }
          .runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${r.toString}")
      } catch {
        case ex: ControlThrowable =>
          Result.all(
            List(
              actual ==== none[Int],
              ex ==== expectedExpcetion,
            )
          )

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testOnNonFatal_Task_onNonFatalWithShouldReturnSuccessfulResult: Result = {

      val expectedResult = 999
      val fa             = run[Task, Int](expectedResult)
      val expected       = none[Int]
      var actual         = none[Int] // scalafix:ok DisableSyntax.var

      try {
        val result =
          fa.onNonFatalWith {
            case NonFatal(_) =>
              Task {
                actual = 123.some
              } *> Task.unit
          }.runSyncUnsafe()
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

    def testOnNonFatal_TaskEitherAB_onNonFatalWithEitherShouldDoSomethingForNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val expectedResult    = expectedExpcetion.asLeft[Int]
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

      val expected = 123.some
      var actual   = none[Int] // scalafix:ok DisableSyntax.var

      val actualFailedResult =
        try {
          val r =
            fa.onNonFatalWith {
              case NonFatal(`expectedExpcetion`) =>
                Task {
                  actual = expected
                } *> Task.unit
            }.runSyncUnsafe()
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

    def testOnNonFatal_TaskEitherAB_onNonFatalWithEitherShouldDoNothingForRightCase: Result = {

      val expectedValue  = 1
      val expectedResult = expectedValue.asRight[SomeError]
      val fa             = run[Task, Either[SomeError, Int]](expectedResult)

      val expected = none[Int]
      var actual   = none[Int] // scalafix:ok DisableSyntax.var

      val actualResult =
        try {
          fa.onNonFatalWith {
            case NonFatal(_) =>
              Task {
                actual = 123.some
              } *> Task.unit
          }.runSyncUnsafe()
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

    def testOnNonFatal_TaskEitherAB_onNonFatalWithEitherShouldDoNothingForLeftCase: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val expectedResult    = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

      val fa = run[Task, Int](throwThrowable(expectedExpcetion))
        .catchNonFatal {
          case err =>
            SomeError.someThrowable(err)
        }

      val expected = none[Int]
      var actual   = none[Int] // scalafix:ok DisableSyntax.var

      val actualResult =
        try {
          fa.onNonFatalWith {
            case NonFatal(`expectedExpcetion`) =>
              Task {
                actual = 123.some
              } *> Task.unit
          }.runSyncUnsafe()
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

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val expectedResult    = expectedExpcetion.asLeft[Int]
      val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))

      val expected = 123.some
      var actual   = none[Int] // scalafix:ok DisableSyntax.var

      val actualFailedResult =
        try {
          val r =
            fa.onNonFatalWith {
              case NonFatal(`expectedExpcetion`) =>
                Task {
                  actual = expected
                } *> Task.unit
            }.value
              .runSyncUnsafe()
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

      val expectedValue  = 1
      val expectedResult = expectedValue.asRight[SomeError]
      val fa             = EitherT(run[Task, Either[SomeError, Int]](expectedResult))

      val expected = none[Int]
      var actual   = none[Int] // scalafix:ok DisableSyntax.var

      val actualResult =
        try {
          fa.onNonFatalWith {
            case NonFatal(_) =>
              Task {
                actual = 123.some
              } *> Task.unit
          }.value
            .runSyncUnsafe()
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

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val expectedResult    = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

      val fa = EitherT(
        run[Task, Int](throwThrowable(expectedExpcetion))
          .catchNonFatal {
            case err => SomeError.someThrowable(err)
          }
      )

      val expected = none[Int]
      var actual   = none[Int] // scalafix:ok DisableSyntax.var

      val actualResult =
        try {
          fa.onNonFatalWith {
            case NonFatal(`expectedExpcetion`) =>
              Task {
                actual = 123.some
              } *> Task.unit
          }.value
            .runSyncUnsafe()
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
