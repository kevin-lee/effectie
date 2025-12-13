package effectie.instances.future

import cats.data.EitherT
import cats.syntax.all._
import effectie.core._
import effectie.syntax.all._
import effectie.testing.types.SomeError
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog._
import hedgehog.runner._

/** @author Kevin Lee
  * @since 2020-12-06
  */
object fxFutureSpec extends Properties {

  override def tests: List[Test] = futureSpecs

  private implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  /* Future */
  private val futureSpecs =
    List(
      example(
        "test Fx[Future]catchNonFatalEitherT should catch NonFatal",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalEitherTShouldCatchNonFatal,
      ),
      example(
        "test Fx[Future]catchNonFatalEitherT should return the successful result",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalEitherTShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Future]catchNonFatalEitherT should return the failed result",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalEitherTShouldReturnFailedResult,
      ),
    ) ++ List(
      example(
        "test Fx[Future].handleEitherTNonFatalWith should handle NonFatal",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleEitherTNonFatalWithShouldHandleNonFatalWith,
      ),
      example(
        "test Fx[Future].handleEitherTNonFatalWith should return the successful result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleEitherTNonFatalWithShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Future].handleEitherTNonFatalWith should return the failed result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleEitherTNonFatalWithShouldReturnFailedResult,
      ),
      example(
        "test Fx[Future].handleEitherTNonFatal should handle NonFatal",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleEitherTNonFatalShouldHandleNonFatal,
      ),
      example(
        "test Fx[Future].handleEitherTNonFatal should return the successful result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleEitherTNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Future].handleEitherTNonFatal should return the failed result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleEitherTNonFatalShouldReturnFailedResult,
      ),
    ) ++ List(
      example(
        "test Fx[Future].recoverEitherTFromNonFatalWith should catch NonFatal",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Future].recoverEitherTFromNonFatalWith should return the successful result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Future].recoverEitherTFromNonFatalWith should return the failed result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverEitherTFromNonFatalWithShouldReturnFailedResult,
      ),
      example(
        "test Fx[Future].recoverEitherTFromNonFatal should catch NonFatal",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverEitherTFromNonFatalShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Future].recoverEitherTFromNonFatal should return the successful result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverEitherTFromNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Future].recoverEitherTFromNonFatal should return the failed result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverEitherTFromNonFatalShouldReturnFailedResult,
      ),
    )

  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: Fx, A](a: => A): F[A] =
    Fx[F].effectOf(a)

  object FutureSpec {
    import effectie.instances.future.fx._

    import scala.concurrent.duration._

    val waitFor = WaitFor(1.second)

    object CanCatchSpec {
      import java.util.concurrent.{ExecutorService, Executors}
      import scala.concurrent.{ExecutionContext, Future}

      def testCanCatch_Future_catchNonFatalEitherTShouldCatchNonFatal: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedException = new RuntimeException("Something's wrong")
        val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
        val expected = SomeError.someThrowable(expectedException).asLeft[Int]
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(Fx[Future].catchNonFatalEitherT(fa)(SomeError.someThrowable).value)

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
        )(Fx[Future].catchNonFatalEitherT(fa)(SomeError.someThrowable).value)

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
        )(Fx[Future].catchNonFatalEitherT(fa)(SomeError.someThrowable).value)

        actual ==== expected
      }
    }

    object CanHandleErrorSpec {
      import java.util.concurrent.{ExecutorService, Executors}
      import scala.concurrent.{ExecutionContext, Future}

      def testCanHandleError_Future_handleEitherTNonFatalWithShouldHandleNonFatalWith: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedException = new RuntimeException("Something's wrong")
        val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
        val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
        val actualFailedResult   = ConcurrentSupport.futureToValue(
          Fx[Future]
            .handleEitherTNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int]))
            .value,
          waitFor,
        )

        val fa2      = EitherT(
          run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
        )
        val expected = 1.asRight[SomeError]
        val actual   =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor,
          )(Fx[Future].handleEitherTNonFatalWith(fa2)(_ => Future(expected)).value)

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
        )(
          Fx[Future]
            .handleEitherTNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int]))
            .value
        )

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
          )(Fx[Future].handleEitherTNonFatalWith(fa)(_ => Future(expected)).value)

        actual ==== expected
      }

      def testCanHandleError_Future_handleEitherTNonFatalShouldHandleNonFatal: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedException = new RuntimeException("Something's wrong")
        val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
        val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
        val actualFailedResult   = ConcurrentSupport.futureToValue(
          Fx[Future].handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]).value,
          waitFor,
        )

        val fa2      = EitherT(
          run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
        )
        val expected = 1.asRight[SomeError]
        val actual   =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor,
          )(Fx[Future].handleEitherTNonFatal(fa2)(_ => expected).value)

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
        )(Fx[Future].handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]).value)

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
          )(Fx[Future].handleEitherTNonFatal(fa)(_ => expected).value)

        actual ==== expected
      }

    }

    object CanRecoverSpec {
      import java.util.concurrent.{ExecutorService, Executors}
      import scala.concurrent.{ExecutionContext, Future}
      import scala.util.control.NonFatal

      def testCanRecover_Future_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedException = new RuntimeException("Something's wrong")
        val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
        val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
        val actualFailedResult   = ConcurrentSupport.futureToValue(
          Fx[Future]
            .recoverEitherTFromNonFatalWith(fa) {
              case err => Future(SomeError.someThrowable(err).asLeft[Int])
            }
            .value,
          waitFor,
        )

        val expected = 1.asRight[SomeError]

        val fa2    = EitherT(
          run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
        )
        val actual =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor,
          )(
            Fx[Future]
              .recoverEitherTFromNonFatalWith(fa2) {
                case err @ _ => Future(expected)
              }
              .value
          )

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
        )(
          Fx[Future]
            .recoverEitherTFromNonFatalWith(fa) {
              case err => Future(SomeError.someThrowable(err).asLeft[Int])
            }
            .value
        )

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
          )(
            Fx[Future]
              .recoverEitherTFromNonFatalWith(fa) {
                case NonFatal(_) => Future(expected)
              }
              .value
          )

        actual ==== expected
      }

      // /

      def testCanRecover_Future_recoverEitherTFromNonFatalShouldRecoverFromNonFatal: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedException = new RuntimeException("Something's wrong")
        val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
        val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
        val actualFailedResult   = ConcurrentSupport.futureToValue(
          Fx[Future]
            .recoverEitherTFromNonFatal(fa) {
              case err => SomeError.someThrowable(err).asLeft[Int]
            }
            .value,
          waitFor,
        )

        val fa2      = EitherT(
          run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
        )
        val expected = 1.asRight[SomeError]
        val actual   =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor,
          )(Fx[Future].recoverEitherTFromNonFatal(fa2) { case err @ _ => expected }.value)

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
        )(
          Fx[Future]
            .recoverEitherTFromNonFatal(fa) {
              case err => SomeError.someThrowable(err).asLeft[Int]
            }
            .value
        )

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
          )(Fx[Future].recoverEitherTFromNonFatal(fa) { case NonFatal(_) => expected }.value)

        actual ==== expected
      }
    }

  }

}
