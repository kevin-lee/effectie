package effectie.syntax

import cats.data.EitherT
import cats.effect.IO
import cats.effect.unsafe.IORuntime
import cats.syntax.all.*
import cats.Functor
import effectie.instances.ce3.compat.CatsEffectIoCompatForFuture
import effectie.syntax.fx.*
import effectie.syntax.error.*
import effectie.instances.ce3.testing
import effectie.testing.types.*
import effectie.core.Fx
import effectie.SomeControlThrowable
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import extras.hedgehog.ce3.syntax.runner._
import hedgehog.*
import hedgehog.runner.*

import java.util.concurrent.ExecutorService
import scala.util.control.{ControlThrowable, NonFatal}

/** @author Kevin Lee
  * @since 2021-10-23
  */
object errorSpec extends Properties {
  override def tests: List[Prop] =
    CanCatchSyntaxSpec.tests ++ CanHandleErrorSyntaxSpec.tests ++ CanRecoverSyntaxSpec.tests ++ OnNonFatalSyntaxSpec.tests
}

object CanCatchSyntaxSpec {

  def tests: List[Test] = ioSpecs ++ futureSpecs

  /* IO */
  val ioSpecs = List(
    example(
      "test CanCatch[IO]catchNonFatalThrowable should catch NonFatal",
      IoSpec.testCanCatch_IO_catchNonFatalThrowableShouldCatchNonFatal,
    ),
//    example(
//      "test CanCatch[IO]catchNonFatalThrowable should not catch Fatal",
//      IoSpec.testCanCatch_IO_catchNonFatalThrowableShouldNotCatchFatal,
//    ),
    example(
      "test CanCatch[IO]catchNonFatalThrowable should return the successful result",
      IoSpec.testCanCatch_IO_catchNonFatalThrowableShouldReturnSuccessfulResult,
    ),
    example(
      "test CanCatch[IO]catchNonFatal should catch NonFatal",
      IoSpec.testCanCatch_IO_catchNonFatalShouldCatchNonFatal,
    ),
//    example(
//      "test CanCatch[IO]catchNonFatal should not catch Fatal",
//      IoSpec.testCanCatch_IO_catchNonFatalShouldNotCatchFatal,
//    ),
    example(
      "test CanCatch[IO]catchNonFatal should return the successful result",
      IoSpec.testCanCatch_IO_catchNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanCatch[IO]catchNonFatalEither should catch NonFatal",
      IoSpec.testCanCatch_IO_catchNonFatalEitherShouldCatchNonFatal,
    ),
//    example(
//      "test CanCatch[IO]catchNonFatalEither should not catch Fatal",
//      IoSpec.testCanCatch_IO_catchNonFatalEitherShouldNotCatchFatal,
//    ),
    example(
      "test CanCatch[IO]catchNonFatalEither should return the successful result",
      IoSpec.testCanCatch_IO_catchNonFatalEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanCatch[IO]catchNonFatalEither should return the failed result",
      IoSpec.testCanCatch_IO_catchNonFatalEitherShouldReturnFailedResult,
    ),
    example(
      "test CanCatch[IO]catchNonFatalEitherT should catch NonFatal",
      IoSpec.testCanCatch_IO_catchNonFatalEitherTShouldCatchNonFatal,
    ),
//    example(
//      "test CanCatch[IO]catchNonFatalEitherT should not catch Fatal",
//      IoSpec.testCanCatch_IO_catchNonFatalEitherTShouldNotCatchFatal,
//    ),
    example(
      "test CanCatch[IO]catchNonFatalEitherT should return the successful result",
      IoSpec.testCanCatch_IO_catchNonFatalEitherTShouldReturnSuccessfulResult,
    ),
    example(
      "test CanCatch[IO]catchNonFatalEitherT should return the failed result",
      IoSpec.testCanCatch_IO_catchNonFatalEitherTShouldReturnFailedResult,
    ),
  )

  /* Future */
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

  def throwThrowable[A](throwable: => Throwable): A = throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: Fx: Functor, A](a: => A): F[A] = effectOf[F](a)

  object IoSpec {
    import effectie.instances.ce3.fx.given

    def testCanCatch_IO_catchNonFatalThrowableShouldCatchNonFatal: Result = withIO { implicit ticker =>

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = expectedExpcetion.asLeft[Int]
      val actual            = fa.catchNonFatalThrowable
      val actual2           = catchNonFatalThrowable(fa)

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanCatch_IO_catchNonFatalThrowableShouldNotCatchFatal: Result = {

      val es: ExecutorService = ConcurrentSupport.newExecutorService(2)
      given rt: IORuntime     = testing.IoAppUtils.runtime(es)

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))

      try {
        val actual = fa.catchNonFatalThrowable.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: SomeControlThrowable =>
          ex.getMessage ==== fatalExpcetion.getMessage

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_IO_catchNonFatalThrowableShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

      val fa: IO[Int] = run[IO, Int](1)
      val expected    = 1.asRight[Throwable]
      val actual      = fa.catchNonFatalThrowable
      val actual2     = catchNonFatalThrowable(fa)

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanCatch_IO_catchNonFatalShouldCatchNonFatal: Result = withIO { implicit ticker =>

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = fa.catchNonFatal(SomeError.someThrowable)
      val actual2           = catchNonFatal(fa)(SomeError.someThrowable)

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanCatch_IO_catchNonFatalShouldNotCatchFatal: Result = {

      val es: ExecutorService = ConcurrentSupport.newExecutorService(2)
      given rt: IORuntime     = testing.IoAppUtils.runtime(es)

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))

      try {
        val actual = fa.catchNonFatal(SomeError.someThrowable).unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: SomeControlThrowable =>
          ex.getMessage ==== fatalExpcetion.getMessage

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_IO_catchNonFatalShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

      val fa: IO[Int] = run[IO, Int](1)
      val expected    = 1.asRight[SomeError]
      val actual      = fa.catchNonFatal(SomeError.someThrowable)
      val actual2     = catchNonFatal(fa)(SomeError.someThrowable)

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanCatch_IO_catchNonFatalEitherShouldCatchNonFatal: Result = withIO { implicit ticker =>

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = fa.catchNonFatalEither(SomeError.someThrowable)
      val actual2           = catchNonFatalEither(fa)(SomeError.someThrowable)

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanCatch_IO_catchNonFatalEitherShouldNotCatchFatal: Result = {

      val es: ExecutorService = ConcurrentSupport.newExecutorService(2)
      given rt: IORuntime     = testing.IoAppUtils.runtime(es)

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual = fa.catchNonFatalEither(SomeError.someThrowable).unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: SomeControlThrowable =>
          ex.getMessage ==== fatalExpcetion.getMessage

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_IO_catchNonFatalEitherShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

      val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = fa.catchNonFatalEither(SomeError.someThrowable)
      val actual2  = catchNonFatalEither(fa)(SomeError.someThrowable)

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanCatch_IO_catchNonFatalEitherShouldReturnFailedResult: Result = withIO { implicit ticker =>

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = fa.catchNonFatalEither(SomeError.someThrowable)
      val actual2         = catchNonFatalEither(fa)(SomeError.someThrowable)

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanCatch_IO_catchNonFatalEitherTShouldCatchNonFatal: Result = withIO { implicit ticker =>

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa       = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual   = fa.catchNonFatalEitherT(SomeError.someThrowable).value
      val actual2  = catchNonFatalEitherT(fa)(SomeError.someThrowable).value

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanCatch_IO_catchNonFatalEitherTShouldNotCatchFatal: Result = {

      val es: ExecutorService = ConcurrentSupport.newExecutorService(2)
      given rt: IORuntime     = testing.IoAppUtils.runtime(es)

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

      try {
        val actual = fa.catchNonFatalEitherT(SomeError.someThrowable).value.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: SomeControlThrowable =>
          ex.getMessage ==== fatalExpcetion.getMessage

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_IO_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

      val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = fa.catchNonFatalEitherT(SomeError.someThrowable).value
      val actual2  = catchNonFatalEitherT(fa)(SomeError.someThrowable).value

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanCatch_IO_catchNonFatalEitherTShouldReturnFailedResult: Result = withIO { implicit ticker =>

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          = fa.catchNonFatalEitherT(SomeError.someThrowable).value
      val actual2         = catchNonFatalEitherT(fa)(SomeError.someThrowable).value

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

  }

  object FutureSpec {
    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration.*
    import scala.concurrent.{ExecutionContext, Future}
    import effectie.instances.future.fx._

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
        waitFor,
      )
      val actual2           = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
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
        waitFor,
      )
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
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
        waitFor,
      )
      val actual2           = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
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
        waitFor,
      )
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
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
        waitFor,
      )
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
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
        waitFor,
      )
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
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
        waitFor,
      )
      val actual2         = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(catchNonFatalEither(fa)(SomeError.someThrowable))

      actual ==== expected and actual2 ==== expected
    }

    def testCanCatch_Future_catchNonFatalEitherTShouldCatchNonFatal: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      def fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual   = ConcurrentSupport.futureToValue(
        fa.catchNonFatalEitherT(SomeError.someThrowable).value,
        waitFor,
      )
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(catchNonFatalEitherT(fa)(SomeError.someThrowable).value)

      actual ==== expected and actual2 ==== expected
    }

    def testCanCatch_Future_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      def fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValue(
        fa.catchNonFatalEitherT(SomeError.someThrowable).value,
        waitFor,
      )
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(catchNonFatalEitherT(fa)(SomeError.someThrowable).value)

      actual ==== expected and actual2 ==== expected
    }

    def testCanCatch_Future_catchNonFatalEitherTShouldReturnFailedResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      def fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          = ConcurrentSupport.futureToValue(
        fa.catchNonFatalEitherT(SomeError.someThrowable).value,
        waitFor,
      )
      val actual2         = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(catchNonFatalEitherT(fa)(SomeError.someThrowable).value)

      actual ==== expected and actual2 ==== expected
    }
  }

}

object CanHandleErrorSyntaxSpec {

  def tests: List[Test] = ioSpecs ++ futureSpecs

  /* IO */
  val ioSpecs = List(
    example(
      "test CanHandleError[IO].handleNonFatalWith should handle NonFatal",
      IoSpec.testCanHandleError_IO_handleNonFatalWithShouldHandleNonFatalWith,
    ),
//    example(
//      "test CanHandleError[IO].handleNonFatalWith should not handle Fatal",
//      IoSpec.testCanHandleError_IO_handleNonFatalWithShouldNotHandleFatalWith,
//    ),
    example(
      "test CanHandleError[IO].handleNonFatalWith should return the successful result",
      IoSpec.testCanHandleError_IO_handleNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[IO].handleNonFatalWithEither should handle NonFatal",
      IoSpec.testCanHandleError_IO_handleNonFatalWithEitherShouldHandleNonFatalWith,
    ),
//    example(
//      "test CanHandleError[IO].handleNonFatalWithEither should not handle Fatal",
//      IoSpec.testCanHandleError_IO_handleNonFatalWithEitherShouldNotHandleFatalWith,
//    ),
    example(
      "test CanHandleError[IO].handleNonFatalWithEither should return the successful result",
      IoSpec.testCanHandleError_IO_handleNonFatalWithEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[IO].handleNonFatalWithEither should return the failed result",
      IoSpec.testCanHandleError_IO_handleNonFatalWithEitherShouldReturnFailedResult,
    ),
    example(
      "test CanHandleError[IO].handleEitherNonFatalWith should handle NonFatal",
      IoSpec.testCanHandleError_IO_handleEitherNonFatalWithShouldHandleNonFatalWith,
    ),
//    example(
//      "test CanHandleError[IO].handleEitherNonFatalWith should not handle Fatal",
//      IoSpec.testCanHandleError_IO_handleEitherNonFatalWithShouldNotHandleFatalWith,
//    ),
    example(
      "test CanHandleError[IO].handleEitherNonFatalWith should return the successful result",
      IoSpec.testCanHandleError_IO_handleEitherNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[IO].handleEitherNonFatalWith should return the failed result",
      IoSpec.testCanHandleError_IO_handleEitherNonFatalWithShouldReturnFailedResult,
    ),
    example(
      "test CanHandleError[IO].handleEitherTNonFatalWith should handle NonFatal",
      IoSpec.testCanHandleError_IO_handleEitherTNonFatalWithShouldHandleNonFatalWith,
    ),
//    example(
//      "test CanHandleError[IO].handleEitherTNonFatalWith should not handle Fatal",
//      IoSpec.testCanHandleError_IO_handleEitherTNonFatalWithShouldNotHandleFatalWith,
//    ),
    example(
      "test CanHandleError[IO].handleEitherTNonFatalWith should return the successful result",
      IoSpec.testCanHandleError_IO_handleEitherTNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[IO].handleEitherTNonFatalWith should return the failed result",
      IoSpec.testCanHandleError_IO_handleEitherTNonFatalWithShouldReturnFailedResult,
    ),
    example(
      "test CanHandleError[IO].handleNonFatal should handle NonFatal",
      IoSpec.testCanHandleError_IO_handleNonFatalShouldHandleNonFatal,
    ),
//    example(
//      "test CanHandleError[IO].handleNonFatal should not handle Fatal",
//      IoSpec.testCanHandleError_IO_handleNonFatalShouldNotHandleFatal,
//    ),
    example(
      "test CanHandleError[IO].handleNonFatal should return the successful result",
      IoSpec.testCanHandleError_IO_handleNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[IO].handleNonFatalEither should handle NonFatal",
      IoSpec.testCanHandleError_IO_handleNonFatalEitherShouldHandleNonFatal,
    ),
//    example(
//      "test CanHandleError[IO].handleNonFatalEither should not handle Fatal",
//      IoSpec.testCanHandleError_IO_handleNonFatalEitherShouldNotHandleFatal,
//    ),
    example(
      "test CanHandleError[IO].handleNonFatalEither should return the successful result",
      IoSpec.testCanHandleError_IO_handleNonFatalEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[IO].handleNonFatalEither should return the failed result",
      IoSpec.testCanHandleError_IO_handleNonFatalEitherShouldReturnFailedResult,
    ),
    example(
      "test CanHandleError[IO].handleEitherNonFatal should handle NonFatal",
      IoSpec.testCanHandleError_IO_handleEitherNonFatalShouldHandleNonFatal,
    ),
//    example(
//      "test CanHandleError[IO].handleEitherNonFatal should not handle Fatal",
//      IoSpec.testCanHandleError_IO_handleEitherNonFatalShouldNotHandleFatal,
//    ),
    example(
      "test CanHandleError[IO].handleEitherNonFatal should return the successful result",
      IoSpec.testCanHandleError_IO_handleEitherNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[IO].handleEitherNonFatal should return the failed result",
      IoSpec.testCanHandleError_IO_handleEitherNonFatalShouldReturnFailedResult,
    ),
    example(
      "test CanHandleError[IO].handleEitherTNonFatal should handle NonFatal",
      IoSpec.testCanHandleError_IO_handleEitherTNonFatalShouldHandleNonFatal,
    ),
//    example(
//      "test CanHandleError[IO].handleEitherTNonFatal should not handle Fatal",
//      IoSpec.testCanHandleError_IO_handleEitherTNonFatalShouldNotHandleFatal,
//    ),
    example(
      "test CanHandleError[IO].handleEitherTNonFatal should return the successful result",
      IoSpec.testCanHandleError_IO_handleEitherTNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[IO].handleEitherTNonFatal should return the failed result",
      IoSpec.testCanHandleError_IO_handleEitherTNonFatalShouldReturnFailedResult,
    ),
  )

  val futureSpecs = List(
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

  def throwThrowable[A](throwable: => Throwable): A = throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: Fx: Functor, A](a: => A): F[A] = effectOf[F](a)

  object IoSpec {
    import effectie.instances.ce3.fx.given

    def testCanHandleError_IO_handleNonFatalWithShouldHandleNonFatalWith: Result = withIO { implicit ticker =>

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 123
      val actual            = fa.handleNonFatalWith {
        case NonFatal(`expectedExpcetion`) =>
          IO.pure(expected)
      }
      val actual2           = handleNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) =>
          IO.pure(expected)
      }

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanHandleError_IO_handleNonFatalWithShouldNotHandleFatalWith: Result = {

      val es: ExecutorService = ConcurrentSupport.newExecutorService(2)
      given rt: IORuntime     = testing.IoAppUtils.runtime(es)

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))

      try {
        val actual = fa.handleNonFatalWith(_ => IO.pure(123)).unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_IO_handleNonFatalWithShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

      val fa       = run[IO, Int](1)
      val expected = 1
      val actual   = fa.handleNonFatalWith(_ => IO.pure(999))
      val actual2  = handleNonFatalWith(fa)(_ => IO.pure(999))

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanHandleError_IO_handleNonFatalWithEitherShouldHandleNonFatalWith: Result = withIO { implicit ticker =>

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
      val actualFailedResult   =
        fa.handleNonFatalWith(_ => IO.pure(expectedFailedResult))

      val expectedSuccessResult = 1.asRight[SomeError]
      val actualSuccessResult   =
        fa.handleNonFatalWith(_ => IO.pure(1.asRight[SomeError]))

      actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
    }

    def testCanHandleError_IO_handleNonFatalWithEitherShouldNotHandleFatalWith: Result = {

      val es: ExecutorService = ConcurrentSupport.newExecutorService(2)
      given rt: IORuntime     = testing.IoAppUtils.runtime(es)

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual = fa.handleNonFatalWith(_ => IO.pure(123.asRight[SomeError])).unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: SomeControlThrowable =>
          ex.getMessage ==== fatalExpcetion.getMessage

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_IO_handleNonFatalWithEitherShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

      val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = fa.handleNonFatalWith(_ => IO(999.asRight[SomeError]))
      val actual2  = handleNonFatalWith(fa)(_ => IO(999.asRight[SomeError]))

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanHandleError_IO_handleNonFatalWithEitherShouldReturnFailedResult: Result = withIO { implicit ticker =>

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = fa.handleNonFatalWith(_ => IO.pure(123.asRight[SomeError]))
      val actual2         = handleNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError]))

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanHandleError_IO_handleEitherNonFatalWithShouldHandleNonFatalWith: Result = withIO { implicit ticker =>

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   = fa
        .handleEitherNonFatalWith(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))

      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        fa.handleEitherNonFatalWith(_ => IO.pure(123.asRight[SomeError]))

      actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
    }

    def testCanHandleError_IO_handleEitherNonFatalWithShouldNotHandleFatalWith: Result = {

      val es: ExecutorService = ConcurrentSupport.newExecutorService(2)
      given rt: IORuntime     = testing.IoAppUtils.runtime(es)

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual =
          fa.handleEitherNonFatalWith(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
            .unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_IO_handleEitherNonFatalWithShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

      val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   =
        fa.handleEitherNonFatalWith(_ => IO.pure(123.asRight[SomeError]))
      val actual2  =
        handleEitherNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError]))

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanHandleError_IO_handleEitherNonFatalWithShouldReturnFailedResult: Result = withIO { implicit ticker =>

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        fa.handleEitherNonFatalWith(_ => IO.pure(123.asRight[SomeError]))
      val actual2         =
        handleEitherNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError]))

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanHandleError_IO_handleEitherTNonFatalWithShouldHandleNonFatalWith: Result = withIO { implicit ticker =>

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   = fa
        .handleEitherTNonFatalWith(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
        .value

      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        fa.handleEitherTNonFatalWith(_ => IO.pure(123.asRight[SomeError])).value

      actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
    }

    def testCanHandleError_IO_handleEitherTNonFatalWithShouldNotHandleFatalWith: Result = {

      val es: ExecutorService = ConcurrentSupport.newExecutorService(2)
      given rt: IORuntime     = testing.IoAppUtils.runtime(es)

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

      try {
        val actual =
          fa.handleEitherTNonFatalWith(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
            .value
            .unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_IO_handleEitherTNonFatalWithShouldReturnSuccessfulResult: Result =
      withIO { implicit ticker =>

        val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   =
          fa.handleEitherTNonFatalWith(_ => IO.pure(123.asRight[SomeError])).value
        val actual2  =
          handleEitherTNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).value

        actual.completeAs(expected) and actual2.completeAs(expected)
      }

    def testCanHandleError_IO_handleEitherTNonFatalWithShouldReturnFailedResult: Result = withIO { implicit ticker =>

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        fa.handleEitherTNonFatalWith(_ => IO.pure(123.asRight[SomeError])).value
      val actual2         =
        handleEitherTNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).value

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanHandleError_IO_handleNonFatalShouldHandleNonFatal: Result = withIO { implicit ticker =>

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 123
      val actual            = fa.handleNonFatal {
        case NonFatal(`expectedExpcetion`) =>
          expected
      }
      val actual2           = handleNonFatal(fa) {
        case NonFatal(`expectedExpcetion`) =>
          expected
      }

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanHandleError_IO_handleNonFatalShouldNotHandleFatal: Result = {

      val es: ExecutorService = ConcurrentSupport.newExecutorService(2)
      given rt: IORuntime     = testing.IoAppUtils.runtime(es)

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))

      try {
        val actual = fa.handleNonFatal(_ => 123).unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_IO_handleNonFatalShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

      val fa       = run[IO, Int](1)
      val expected = 1
      val actual   = fa.handleNonFatal(_ => 999)
      val actual2  = handleNonFatal(fa)(_ => 999)

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanHandleError_IO_handleNonFatalEitherShouldHandleNonFatal: Result = withIO { implicit ticker =>

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
      val actualFailedResult   = fa.handleNonFatal(_ => expectedFailedResult)

      val expectedSuccessResult = 1.asRight[SomeError]
      val actualSuccessResult   = fa.handleNonFatal(_ => 1.asRight[SomeError])

      actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
    }

    def testCanHandleError_IO_handleNonFatalEitherShouldNotHandleFatal: Result = {

      val es: ExecutorService = ConcurrentSupport.newExecutorService(2)
      given rt: IORuntime     = testing.IoAppUtils.runtime(es)

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual = fa.handleNonFatal(_ => 123.asRight[SomeError]).unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_IO_handleNonFatalEitherShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

      val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = fa.handleNonFatal(_ => 999.asRight[SomeError])
      val actual2  = handleNonFatal(fa)(_ => 999.asRight[SomeError])

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanHandleError_IO_handleNonFatalEitherShouldReturnFailedResult: Result = withIO { implicit ticker =>

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = fa.handleNonFatal(_ => 123.asRight[SomeError])
      val actual2         = handleNonFatal(fa)(_ => 123.asRight[SomeError])

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanHandleError_IO_handleEitherNonFatalShouldHandleNonFatal: Result = withIO { implicit ticker =>

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   = fa
        .handleEitherNonFatal(err => SomeError.someThrowable(err).asLeft[Int])

      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        fa.handleEitherNonFatal(_ => 123.asRight[SomeError])

      actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
    }

    def testCanHandleError_IO_handleEitherNonFatalShouldNotHandleFatal: Result = {

      val es: ExecutorService = ConcurrentSupport.newExecutorService(2)
      given rt: IORuntime     = testing.IoAppUtils.runtime(es)

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual =
          fa.handleEitherNonFatal(err => SomeError.someThrowable(err).asLeft[Int])
            .unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_IO_handleEitherNonFatalShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

      val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = fa.handleEitherNonFatal(_ => 123.asRight[SomeError])
      val actual2  = handleEitherNonFatal(fa)(_ => 123.asRight[SomeError])

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanHandleError_IO_handleEitherNonFatalShouldReturnFailedResult: Result = withIO { implicit ticker =>

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        fa.handleEitherNonFatal(_ => 123.asRight[SomeError])
      val actual2         =
        handleEitherNonFatal(fa)(_ => 123.asRight[SomeError])

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanHandleError_IO_handleEitherTNonFatalShouldHandleNonFatal: Result = withIO { implicit ticker =>

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   = fa
        .handleEitherTNonFatal(err => SomeError.someThrowable(err).asLeft[Int])
        .value

      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        fa.handleEitherTNonFatal(_ => 123.asRight[SomeError]).value

      actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
    }

    def testCanHandleError_IO_handleEitherTNonFatalShouldNotHandleFatal: Result = {

      val es: ExecutorService = ConcurrentSupport.newExecutorService(2)
      given rt: IORuntime     = testing.IoAppUtils.runtime(es)

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

      try {
        val actual =
          fa.handleEitherTNonFatal(err => SomeError.someThrowable(err).asLeft[Int])
            .value
            .unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_IO_handleEitherTNonFatalShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

      val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = fa.handleEitherTNonFatal(_ => 123.asRight[SomeError]).value
      val actual2  = handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanHandleError_IO_handleEitherTNonFatalShouldReturnFailedResult: Result = withIO { implicit ticker =>

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        fa.handleEitherTNonFatal(_ => 123.asRight[SomeError]).value
      val actual2         =
        handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

  }

  object FutureSpec {
    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration.*
    import scala.concurrent.{ExecutionContext, Future}
    import effectie.instances.future.fx._

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
        waitFor,
      )
      val actual2           = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
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
        waitFor,
      )
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
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

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      def fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValue(
          fa.handleNonFatalWith(err => Future(SomeError.someThrowable(err).asLeft[Int])),
          waitFor,
        )
      val actual2  =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
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
        waitFor,
      )
      val actual2         = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
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
        waitFor,
      )

      val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
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
        waitFor,
      )
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
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
          waitFor,
        )
      val actual2         =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(handleEitherNonFatalWith(fa)(_ => Future(expected)))

      actual ==== expected and actual2 ==== expected
    }

    def testCanHandleError_Future_handleEitherTNonFatalWithShouldHandleNonFatalWith: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
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
        )(fa2.handleEitherTNonFatalWith(err => Future(expected)).value)

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanHandleError_Future_handleEitherTNonFatalWithShouldReturnSuccessfulResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      def fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValue(
        fa.handleEitherTNonFatalWith(err => Future(SomeError.someThrowable(err).asLeft[Int])).value,
        waitFor,
      )
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(handleEitherTNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int])).value)

      actual ==== expected and actual2 ==== expected
    }

    def testCanHandleError_Future_handleEitherTNonFatalWithShouldReturnFailedResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      def fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        ConcurrentSupport.futureToValue(
          fa.handleEitherTNonFatalWith(_ => Future(expected)).value,
          waitFor,
        )
      val actual2         =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(handleEitherTNonFatalWith(fa)(_ => Future(expected)).value)

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
        waitFor,
      )
      val actual2           = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
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
        waitFor,
      )
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
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

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      def fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValue(
          fa.handleNonFatal(err => SomeError.someThrowable(err).asLeft[Int]),
          waitFor,
        )
      val actual2  =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
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
        waitFor,
      )
      val actual2         = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
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
        waitFor,
      )

      val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
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
        waitFor,
      )
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]))

      actual ==== expected and actual2 ==== expected
    }

    def testCanHandleError_Future_handleEitherNonFatalShouldReturnFailedResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      def fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        ConcurrentSupport.futureToValue(
          fa.handleEitherNonFatal(_ => expected),
          waitFor,
        )
      val actual2         =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(handleEitherNonFatal(fa)(_ => expected))

      actual ==== expected and actual2 ==== expected
    }

    def testCanHandleError_Future_handleEitherTNonFatalShouldHandleNonFatal: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
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
        )(fa2.handleEitherTNonFatal(err => expected).value)

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanHandleError_Future_handleEitherTNonFatalShouldReturnSuccessfulResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      def fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValue(
        fa.handleEitherTNonFatal(err => SomeError.someThrowable(err).asLeft[Int]).value,
        waitFor,
      )
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]).value)

      actual ==== expected and actual2 ==== expected
    }

    def testCanHandleError_Future_handleEitherTNonFatalShouldReturnFailedResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      def fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        ConcurrentSupport.futureToValue(
          fa.handleEitherTNonFatal(_ => expected).value,
          waitFor,
        )
      val actual2         =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(handleEitherTNonFatal(fa)(_ => expected).value)

      actual ==== expected and actual2 ==== expected
    }

  }

}

object CanRecoverSyntaxSpec {

  def tests: List[Test] = ioSpecs ++ futureSpecs

  /* IO */
  val ioSpecs = List(
    example(
      "test CanRecover[IO].recoverFromNonFatalWith should catch NonFatal",
      IOSpec.testCanRecover_IO_recoverFromNonFatalWithShouldRecoverFromNonFatal,
    ),
//    example(
//      "test CanRecover[IO].recoverFromNonFatalWith should not catch Fatal",
//      IOSpec.testCanRecover_IO_recoverFromNonFatalWithShouldNotCatchFatal,
//    ),
    example(
      "test CanRecover[IO].recoverFromNonFatalWith should return the successful result",
      IOSpec.testCanRecover_IO_recoverFromNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatalWithEither should catch NonFatal",
      IOSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal,
    ),
//    example(
//      "test CanRecover[IO].recoverFromNonFatalWithEither should not catch Fatal",
//      IOSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldNotCatchFatal,
//    ),
    example(
      "test CanRecover[IO].recoverFromNonFatalWithEither should return the successful result",
      IOSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatalWithEither should return the failed result",
      IOSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[IO].recoverEitherFromNonFatalWith should catch NonFatal",
      IOSpec.testCanRecover_IO_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal,
    ),
//    example(
//      "test CanRecover[IO].recoverEitherFromNonFatalWith should not catch Fatal",
//      IOSpec.testCanRecover_IO_recoverEitherFromNonFatalWithShouldNotCatchFatal,
//    ),
    example(
      "test CanRecover[IO].recoverEitherFromNonFatalWith should return the successful result",
      IOSpec.testCanRecover_IO_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[IO].recoverEitherFromNonFatalWith should return the failed result",
      IOSpec.testCanRecover_IO_recoverEitherFromNonFatalWithShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[IO].recoverEitherTFromNonFatalWith should catch NonFatal",
      IOSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal,
    ),
//    example(
//      "test CanRecover[IO].recoverEitherTFromNonFatalWith should not catch Fatal",
//      IOSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldNotCatchFatal,
//    ),
    example(
      "test CanRecover[IO].recoverEitherTFromNonFatalWith should return the successful result",
      IOSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[IO].recoverEitherTFromNonFatalWith should return the failed result",
      IOSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatal should catch NonFatal",
      IOSpec.testCanRecover_IO_recoverFromNonFatalShouldRecoverFromNonFatal,
    ),
//    example(
//      "test CanRecover[IO].recoverFromNonFatal should not catch Fatal",
//      IOSpec.testCanRecover_IO_recoverFromNonFatalShouldNotCatchFatal,
//    ),
    example(
      "test CanRecover[IO].recoverFromNonFatal should return the successful result",
      IOSpec.testCanRecover_IO_recoverFromNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatalEither should catch NonFatal",
      IOSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldRecoverFromNonFatal,
    ),
//    example(
//      "test CanRecover[IO].recoverFromNonFatalEither should not catch Fatal",
//      IOSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldNotCatchFatal,
//    ),
    example(
      "test CanRecover[IO].recoverFromNonFatalEither should return the successful result",
      IOSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatalEither should return the failed result",
      IOSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[IO].recoverEitherFromNonFatal should catch NonFatal",
      IOSpec.testCanRecover_IO_recoverEitherFromNonFatalShouldRecoverFromNonFatal,
    ),
//    example(
//      "test CanRecover[IO].recoverEitherFromNonFatal should not catch Fatal",
//      IOSpec.testCanRecover_IO_recoverEitherFromNonFatalShouldNotCatchFatal,
//    ),
    example(
      "test CanRecover[IO].recoverEitherFromNonFatal should return the successful result",
      IOSpec.testCanRecover_IO_recoverEitherFromNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[IO].recoverEitherFromNonFatal should return the failed result",
      IOSpec.testCanRecover_IO_recoverEitherFromNonFatalShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[IO].recoverEitherTFromNonFatal should catch NonFatal",
      IOSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldRecoverFromNonFatal,
    ),
//    example(
//      "test CanRecover[IO].recoverEitherTFromNonFatal should not catch Fatal",
//      IOSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldNotCatchFatal,
//    ),
    example(
      "test CanRecover[IO].recoverEitherTFromNonFatal should return the successful result",
      IOSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[IO].recoverEitherTFromNonFatal should return the failed result",
      IOSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnFailedResult,
    ),
  )

  /* Future */
  val futureSpecs = List(
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

  def throwThrowable[A](throwable: => Throwable): A = throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: Fx: Functor, A](a: => A): F[A] = effectOf[F](a)

  object IOSpec {
    import effectie.instances.ce3.fx.given

    def testCanRecover_IO_recoverFromNonFatalWithShouldRecoverFromNonFatal: Result = withIO { implicit ticker =>

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 123

      val actual  = fa.recoverFromNonFatalWith {
        case NonFatal(`expectedExpcetion`) =>
          IO.pure(expected)
      }
      val actual2 = recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) =>
          IO.pure(expected)
      }
      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanRecover_IO_recoverFromNonFatalWithShouldNotCatchFatal: Result = {

      val compat          = new CatsEffectIoCompatForFuture
      given rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

      val expectedExpcetion = SomeControlThrowable("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))

      val io = fa.recoverFromNonFatalWith { case NonFatal(`expectedExpcetion`) => IO.pure(123) }
      try {
        val actual = io.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverFromNonFatalWithShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

      val fa       = run[IO, Int](1)
      val expected = 1
      val actual   = fa.recoverFromNonFatalWith {
        case NonFatal(_) => IO.pure(999)
      }
      val actual2  = recoverFromNonFatalWith(fa) {
        case NonFatal(_) => IO.pure(999)
      }

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanRecover_IO_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal: Result = withIO { implicit ticker =>

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
      val actualFailedResult   = fa
        .recoverFromNonFatalWith {
          case NonFatal(`expectedExpcetion`) => IO.pure(expectedFailedResult)
        }

      val expectedSuccessResult = 1.asRight[SomeError]
      val actualSuccessResult   = fa
        .recoverFromNonFatalWith {
          case NonFatal(`expectedExpcetion`) => IO.pure(1.asRight[SomeError])
        }

      actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
    }

    def testCanRecover_IO_recoverFromNonFatalWithEitherShouldNotCatchFatal: Result = {

      val compat          = new CatsEffectIoCompatForFuture
      given rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

      val expectedExpcetion = SomeControlThrowable("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

      val io = fa.recoverFromNonFatalWith {
        case NonFatal(`expectedExpcetion`) => IO.pure(123.asRight[SomeError])
      }
      try {
        val actual = io.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult: Result =
      withIO { implicit ticker =>

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = fa.recoverFromNonFatalWith {
          case NonFatal(_) => IO(999.asRight[SomeError])
        }
        val actual2  = recoverFromNonFatalWith(fa) {
          case NonFatal(_) => IO(999.asRight[SomeError])
        }

        actual.completeAs(expected) and actual2.completeAs(expected)
      }

    def testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnFailedResult: Result = withIO { implicit ticker =>

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = fa.recoverFromNonFatalWith {
        case NonFatal(_) => IO.pure(123.asRight[SomeError])
      }
      val actual2         = recoverFromNonFatalWith(fa) {
        case NonFatal(_) => IO.pure(123.asRight[SomeError])
      }

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanRecover_IO_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal: Result = withIO { implicit ticker =>

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   = fa
        .recoverEitherFromNonFatalWith {
          case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
        }

      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   = fa
        .recoverEitherFromNonFatalWith {
          case NonFatal(`expectedExpcetion`) => IO.pure(123.asRight[SomeError])
        }

      actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
    }

    def testCanRecover_IO_recoverEitherFromNonFatalWithShouldNotCatchFatal: Result = {

      val compat          = new CatsEffectIoCompatForFuture
      given rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

      val expectedExpcetion = SomeControlThrowable("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

      val io = fa.recoverEitherFromNonFatalWith {
        case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
      }
      try {
        val actual = io.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult: Result =
      withIO { implicit ticker =>

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = fa.recoverEitherFromNonFatalWith {
          case NonFatal(_) => IO.pure(123.asRight[SomeError])
        }
        val actual2  = recoverEitherFromNonFatalWith(fa) {
          case NonFatal(_) => IO.pure(123.asRight[SomeError])
        }

        actual.completeAs(expected) and actual2.completeAs(expected)
      }

    def testCanRecover_IO_recoverEitherFromNonFatalWithShouldReturnFailedResult: Result = withIO { implicit ticker =>

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        fa.recoverEitherFromNonFatalWith {
          case NonFatal(_) => IO.pure(123.asRight[SomeError])
        }
      val actual2         =
        recoverEitherFromNonFatalWith(fa) {
          case NonFatal(_) => IO.pure(123.asRight[SomeError])
        }

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal: Result = withIO { implicit ticker =>

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   = fa.recoverEitherTFromNonFatalWith {
        case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
      }.value

      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   = fa.recoverEitherTFromNonFatalWith {
        case NonFatal(`expectedExpcetion`) => IO.pure(123.asRight[SomeError])
      }.value

      actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
    }

    def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldNotCatchFatal: Result = {

      val compat          = new CatsEffectIoCompatForFuture
      given rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

      val expectedExpcetion = SomeControlThrowable("Something's wrong")
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))

      val io = fa.recoverEitherTFromNonFatalWith {
        case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
      }
      try {
        val actual = io.value.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult: Result =
      withIO { implicit ticker =>

        val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = fa.recoverEitherTFromNonFatalWith {
          case NonFatal(_) => IO.pure(123.asRight[SomeError])
        }.value
        val actual2  = recoverEitherTFromNonFatalWith(fa) {
          case NonFatal(_) => IO.pure(123.asRight[SomeError])
        }.value

        actual.completeAs(expected) and actual2.completeAs(expected)
      }

    def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnFailedResult: Result = withIO { implicit ticker =>

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        fa.recoverEitherTFromNonFatalWith {
          case NonFatal(_) => IO.pure(123.asRight[SomeError])
        }.value
      val actual2         =
        recoverEitherTFromNonFatalWith(fa) {
          case NonFatal(_) => IO.pure(123.asRight[SomeError])
        }.value

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    ///

    def testCanRecover_IO_recoverFromNonFatalShouldRecoverFromNonFatal: Result = withIO { implicit ticker =>

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 123
      val actual            = fa.recoverFromNonFatal {
        case NonFatal(`expectedExpcetion`) =>
          expected
      }
      val actual2           = recoverFromNonFatal(fa) {
        case NonFatal(`expectedExpcetion`) =>
          expected
      }

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanRecover_IO_recoverFromNonFatalShouldNotCatchFatal: Result = {

      val compat          = new CatsEffectIoCompatForFuture
      given rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

      val expectedExpcetion = SomeControlThrowable("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))

      val io = fa.recoverFromNonFatal { case NonFatal(`expectedExpcetion`) => 123 }
      try {
        val actual = io.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverFromNonFatalShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

      val fa       = run[IO, Int](1)
      val expected = 1
      val actual   = fa.recoverFromNonFatal { case NonFatal(_) => 999 }
      val actual2  = recoverFromNonFatal(fa) { case NonFatal(_) => 999 }

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanRecover_IO_recoverFromNonFatalEitherShouldRecoverFromNonFatal: Result = withIO { implicit ticker =>

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
      val actualFailedResult   = fa
        .recoverFromNonFatal { case NonFatal(`expectedExpcetion`) => expectedFailedResult }

      val expectedSuccessResult = 1.asRight[SomeError]
      val actualSuccessResult   = fa
        .recoverFromNonFatal { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }

      actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
    }

    def testCanRecover_IO_recoverFromNonFatalEitherShouldNotCatchFatal: Result = {

      val compat          = new CatsEffectIoCompatForFuture
      given rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

      val expectedExpcetion = SomeControlThrowable("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

      val io = fa.recoverFromNonFatal { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }
      try {
        val actual = io.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverFromNonFatalEitherShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

      val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = fa.recoverFromNonFatal { case NonFatal(_) => 999.asRight[SomeError] }
      val actual2  = recoverFromNonFatal(fa) { case NonFatal(_) => 999.asRight[SomeError] }

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanRecover_IO_recoverFromNonFatalEitherShouldReturnFailedResult: Result = withIO { implicit ticker =>

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = fa.recoverFromNonFatal { case NonFatal(_) => 123.asRight[SomeError] }
      val actual2         = recoverFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanRecover_IO_recoverEitherFromNonFatalShouldRecoverFromNonFatal: Result = withIO { implicit ticker =>

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   =
        fa.recoverEitherFromNonFatal {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }

      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        fa.recoverEitherFromNonFatal { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }

      actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
    }

    def testCanRecover_IO_recoverEitherFromNonFatalShouldNotCatchFatal: Result = {

      val compat          = new CatsEffectIoCompatForFuture
      given rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

      val expectedExpcetion = SomeControlThrowable("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

      val io =
        fa.recoverEitherFromNonFatal {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
      try {
        val actual = io.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverEitherFromNonFatalShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

      val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   =
        fa.recoverEitherFromNonFatal { case NonFatal(_) => 123.asRight[SomeError] }
      val actual2  =
        recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanRecover_IO_recoverEitherFromNonFatalShouldReturnFailedResult: Result = withIO { implicit ticker =>

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        fa.recoverEitherFromNonFatal { case NonFatal(_) => 123.asRight[SomeError] }
      val actual2         =
        recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanRecover_IO_recoverEitherTFromNonFatalShouldRecoverFromNonFatal: Result = withIO { implicit ticker =>

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   =
        fa.recoverEitherTFromNonFatal {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }.value

      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        fa.recoverEitherTFromNonFatal { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }.value

      actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
    }

    def testCanRecover_IO_recoverEitherTFromNonFatalShouldNotCatchFatal: Result = {

      val compat          = new CatsEffectIoCompatForFuture
      given rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

      val expectedExpcetion = SomeControlThrowable("Something's wrong")
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))

      val io =
        fa.recoverEitherTFromNonFatal {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
      try {
        val actual = io.value.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

      val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   =
        fa.recoverEitherTFromNonFatal { case NonFatal(_) => 123.asRight[SomeError] }.value
      val actual2  =
        recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }.value

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

    def testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnFailedResult: Result = withIO { implicit ticker =>

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        fa.recoverEitherTFromNonFatal { case NonFatal(_) => 123.asRight[SomeError] }.value
      val actual2         =
        recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }.value

      actual.completeAs(expected) and actual2.completeAs(expected)
    }

  }

  object FutureSpec {
    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration.*
    import scala.concurrent.{ExecutionContext, Future}
    import scala.util.control.NonFatal
    import effectie.instances.future.fx.*

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
        waitFor,
      )
      val actual2           = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
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
        waitFor,
      )
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
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
          waitFor,
        )
      val actual2  =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
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
          waitFor,
        )
      val actual2         =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
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
        waitFor,
      )

      val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
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
        waitFor,
      )
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
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
      val actual          = ConcurrentSupport.futureToValue(
        fa.recoverEitherFromNonFatalWith {
          case NonFatal(_) => Future(expected)
        },
        waitFor,
      )
      val actual2         = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(recoverEitherFromNonFatalWith(fa) {
        case NonFatal(_) => Future(expected)
      })

      actual ==== expected and actual2 ==== expected
    }

    def testCanRecover_Future_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
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
          case err => Future(expected)
        }.value)

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      def fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValue(
        fa.recoverEitherTFromNonFatalWith {
          case err => Future(SomeError.someThrowable(err).asLeft[Int])
        }.value,
        waitFor,
      )
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(recoverEitherTFromNonFatalWith(fa) {
        case err => Future(SomeError.someThrowable(err).asLeft[Int])
      }.value)

      actual ==== expected and actual2 ==== expected
    }

    def testCanRecover_Future_recoverEitherTFromNonFatalWithShouldReturnFailedResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      def fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          = ConcurrentSupport.futureToValue(
        fa.recoverEitherTFromNonFatalWith {
          case NonFatal(_) => Future(expected)
        }.value,
        waitFor,
      )
      val actual2         = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(recoverEitherTFromNonFatalWith(fa) {
        case NonFatal(_) => Future(expected)
      }.value)

      actual ==== expected and actual2 ==== expected
    }

    ///

    def testCanRecover_Future_recoverFromNonFatalShouldRecoverFromNonFatal: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      def fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 1
      val actual            = ConcurrentSupport.futureToValue(
        fa.recoverFromNonFatal { case NonFatal(`expectedExpcetion`) => expected },
        waitFor,
      )
      val actual2           = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
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
        waitFor,
      )
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
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
          waitFor,
        )
      val actual2  =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
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
        waitFor,
      )
      val actual2         = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
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
        waitFor,
      )

      val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
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
        waitFor,
      )
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
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
          waitFor,
        )
      val actual2         =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(recoverEitherFromNonFatal(fa) { case NonFatal(_) => expected })

      actual ==== expected and actual2 ==== expected
    }

    def testCanRecover_Future_recoverEitherTFromNonFatalShouldRecoverFromNonFatal: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
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
        )(fa2.recoverEitherTFromNonFatal { case err => expected }.value)

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverEitherTFromNonFatalShouldReturnSuccessfulResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      def fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValue(
        fa.recoverEitherTFromNonFatal {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }.value,
        waitFor,
      )
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(recoverEitherTFromNonFatal(fa) {
        case err => SomeError.someThrowable(err).asLeft[Int]
      }.value)

      actual ==== expected and actual2 ==== expected
    }

    def testCanRecover_Future_recoverEitherTFromNonFatalShouldReturnFailedResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      def fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          = ConcurrentSupport.futureToValue(
        fa.recoverEitherTFromNonFatal { case NonFatal(_) => expected }.value,
        waitFor,
      )
      val actual2         = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(recoverEitherTFromNonFatal(fa) { case NonFatal(_) => expected }.value)

      actual ==== expected and actual2 ==== expected
    }

  }

}

object OnNonFatalSyntaxSpec {

  def tests: List[Test] = List(
    /* IO */
    example(
      "test OnNonFatal[IO].onNonFatalWith should do something for NonFatal",
      IOSpec.testOnNonFatal_IO_onNonFatalWithShouldDoSomethingForNonFatal,
    ),
//    example(
//      "test OnNonFatal[IO].onNonFatalWith should not do anything and not catch Fatal",
//      IOSpec.testOnNonFatal_IO_onNonFatalWithShouldNotCatchFatal,
//    ),
    example(
      "test OnNonFatal[IO].onNonFatalWith should not do anything for the successful result",
      IOSpec.testOnNonFatal_IO_onNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test IO[Either[A, B]].onNonFatalWith should do something for NonFatal",
      IOSpec.testOnNonFatal_IOEitherAB_onNonFatalWithEitherShouldDoSomethingForNonFatal,
    ),
    example(
      "test IO[Either[A, B]].onNonFatalWith should do nothing for success case with Right",
      IOSpec.testOnNonFatal_IOEitherAB_onNonFatalWithEitherShouldDoNothingForRightCase,
    ),
    example(
      "test IO[Either[A, B]].onNonFatalWith should do nothing for success case with Left",
      IOSpec.testOnNonFatal_IOEitherAB_onNonFatalWithEitherShouldDoNothingForLeftCase,
    ),
    example(
      "test EitherT[F, A, B].onNonFatalWith should do something for NonFatal",
      IOSpec.testOnNonFatal_EitherT_onNonFatalWithEitherShouldDoSomethingForNonFatal,
    ),
    example(
      "test EitherT[F, A, B](F(Right(b))).onNonFatalWith should do nothing for success case with Right",
      IOSpec.testOnNonFatal_EitherT_onNonFatalWithEitherShouldDoNothingForRightCase,
    ),
    example(
      "test EitherT[F, A, B](F(Left(a))).onNonFatalWith should do nothing for success case with Left",
      IOSpec.testOnNonFatal_EitherT_onNonFatalWithEitherShouldDoNothingForLeftCase,
    ),
  )

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: Fx, A](a: => A): F[A] =
    effectOf[F](a)

  object IOSpec {
    import effectie.instances.ce3.fx.ioFx

    def testOnNonFatal_IO_onNonFatalWithShouldDoSomethingForNonFatal: Result = runIO {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 123.some
      var actual            = none[Int] // scalafix:ok DisableSyntax.var

      fa
        .onNonFatalWith {
          case NonFatal(`expectedExpcetion`) =>
            IO {
              actual = expected
            } *> IO.unit
        }
        .attempt
        .map {
          case Right(r) =>
            Result.failure.log(s"Should have thrown an exception, but it was ${r.toString}.")
          case Left(actualException) =>
            Result.all(
              List(
                actualException ==== expectedExpcetion,
                actual ==== expected,
              )
            )
        }

    }

    def testOnNonFatal_IO_onNonFatalWithShouldNotCatchFatal: Result = {

      val es: ExecutorService    = ConcurrentSupport.newExecutorService(2)
      implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

      val expectedExpcetion = SomeControlThrowable("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
      var actual            = none[Int] // scalafix:ok DisableSyntax.var

      try {
        val r = fa
          .onNonFatalWith {
            case NonFatal(`expectedExpcetion`) =>
              IO {
                actual = 123.some
                ()
              } *> IO.unit
          }
          .unsafeRunSync()
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

    def testOnNonFatal_IO_onNonFatalWithShouldReturnSuccessfulResult: Result = runIO {

      val expectedResult = 999
      val fa             = run[IO, Int](expectedResult)
      val expected       = none[Int]
      var actual         = none[Int] // scalafix:ok DisableSyntax.var

      fa.onNonFatalWith {
        case NonFatal(_) =>
          IO {
            actual = 123.some
          } *> IO.unit
      }.attempt
        .map {
          case Right(result) =>
            Result.all(
              List(
                result ==== expectedResult,
                actual ==== expected,
              )
            )
          case Left(ex) =>
            Result.failure.log(s"No exception was expected but ${ex.getClass.getName} was thrown. ${ex.toString}")
        }

    }

    def testOnNonFatal_IOEitherAB_onNonFatalWithEitherShouldDoSomethingForNonFatal: Result = runIO {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val expectedResult    = expectedExpcetion
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

      val expected = 123.some
      var actual   = none[Int] // scalafix:ok DisableSyntax.var

      fa.onNonFatalWith {
        case NonFatal(`expectedExpcetion`) =>
          IO {
            actual = expected
          } *> IO.unit
      }.attempt
        .map {
          case Right(r) =>
            Result.failure.log(s"Should have thrown an exception, but it was ${r.toString}.")
          case Left(actualFailedResult) =>
            Result.all(
              List(
                actual ==== expected,
                actualFailedResult ==== expectedResult,
              )
            )
        }

    }

    def testOnNonFatal_IOEitherAB_onNonFatalWithEitherShouldDoNothingForRightCase: Result = runIO {

      val expectedValue  = 1
      val expectedResult = expectedValue.asRight[SomeError]
      val fa             = run[IO, Either[SomeError, Int]](expectedResult)

      val expected = none[Int]
      var actual   = none[Int] // scalafix:ok DisableSyntax.var

      fa.onNonFatalWith {
        case NonFatal(_) =>
          IO {
            actual = 123.some
          } *> IO.unit
      }.attempt
        .map {
          case Right(actualResult) =>
            Result.all(
              List(
                actual ==== expected,
                actualResult ==== expectedResult,
              )
            )

          case Left(ex) =>
            Result.failure.log(s"Should not have thrown an exception, but it was ${ex.toString}.")
        }

    }

    def testOnNonFatal_IOEitherAB_onNonFatalWithEitherShouldDoNothingForLeftCase: Result = runIO {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val expectedResult    = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

      val fa = run[IO, Int](throwThrowable(expectedExpcetion))
        .catchNonFatal {
          case err =>
            SomeError.someThrowable(err)
        }

      val expected = none[Int]
      var actual   = none[Int] // scalafix:ok DisableSyntax.var

      fa.onNonFatalWith {
        case NonFatal(`expectedExpcetion`) =>
          IO {
            actual = 123.some
          } *> IO.unit
      }.attempt
        .map {
          case Right(actualResult) =>
            Result.all(
              List(
                actual ==== expected,
                actualResult ==== expectedResult,
              )
            )
          case Left(ex) =>
            Result.failure.log(s"Should not have thrown an exception, but it was ${ex.toString}.")
        }

    }

    /////////

    def testOnNonFatal_EitherT_onNonFatalWithEitherShouldDoSomethingForNonFatal: Result = runIO {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val expectedResult    = expectedExpcetion
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))

      val expected = 123.some
      var actual   = none[Int] // scalafix:ok DisableSyntax.var

      fa.onNonFatalWith {
        case NonFatal(`expectedExpcetion`) =>
          IO {
            actual = expected
          } *> IO.unit
      }.value
        .attempt
        .map {
          case Right(r) =>
            Result.failure.log(s"Should have thrown an exception, but it was ${r.toString}.")

          case Left(actualFailedResult) =>
            Result.all(
              List(
                actual ==== expected,
                actualFailedResult ==== expectedResult,
              )
            )
        }

    }

    def testOnNonFatal_EitherT_onNonFatalWithEitherShouldDoNothingForRightCase: Result = runIO {

      val expectedValue  = 1
      val expectedResult = expectedValue.asRight[SomeError]
      val fa             = EitherT(run[IO, Either[SomeError, Int]](expectedResult))

      val expected = none[Int]
      var actual   = none[Int] // scalafix:ok DisableSyntax.var

      fa.onNonFatalWith {
        case NonFatal(_) =>
          IO {
            actual = 123.some
          } *> IO.unit
      }.value
        .attempt
        .map {
          case Right(actualResult) =>
            Result.all(
              List(
                actual ==== expected,
                actualResult ==== expectedResult,
              )
            )
          case Left(ex) =>
            Result.failure.log(s"Should not have thrown an exception, but it was ${ex.toString}.")
        }

    }

    def testOnNonFatal_EitherT_onNonFatalWithEitherShouldDoNothingForLeftCase: Result = runIO {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val expectedResult    = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

      val fa = EitherT(
        run[IO, Int](throwThrowable(expectedExpcetion))
          .catchNonFatal {
            case err => SomeError.someThrowable(err)
          }
      )

      val expected = none[Int]
      var actual   = none[Int] // scalafix:ok DisableSyntax.var

      fa.onNonFatalWith {
        case NonFatal(`expectedExpcetion`) =>
          IO {
            actual = 123.some
          } *> IO.unit
      }.value
        .attempt
        .map {
          case Right(actualResult) =>
            Result.all(
              List(
                actual ==== expected,
                actualResult ==== expectedResult,
              )
            )
          case Left(ex) =>
            Result.failure.log(s"Should not have thrown an exception, but it was ${ex.toString}.")
        }

    }

  }

}
