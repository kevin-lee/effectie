package effectie.core

import effectie.testing.types.SomeError
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog._
import hedgehog.runner._

/** @author Kevin Lee
  * @since 2022-01-06
  */
object CanRecoverSpec extends Properties {
  implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = futureSpecs

  /* Future */
  val futureSpecs = List(
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

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable

  def run[F[_]: FxCtor, A](a: => A): F[A] =
    FxCtor[F].effectOf(a)

  object FutureSpec {
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
        waitFor
      )(CanRecover[Future].recoverFromNonFatalWith(fa) {
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
        waitFor
      )(CanRecover[Future].recoverFromNonFatalWith(fa) {
        case NonFatal(_) => Future(123)
      })

      actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion                            = new RuntimeException("Something's wrong")
      val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))

      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val actualFailedResult =
        ConcurrentSupport.futureToValue(
          CanRecover[Future].recoverFromNonFatalWith(fa) {
            case err => Future(Left(SomeError.someThrowable(err)))
          },
          waitFor
        )

      val expected: Either[SomeError, Int] = Right(1)

      val fa2    = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val actual =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(CanRecover[Future].recoverFromNonFatalWith(fa2) {
          case NonFatal(`expectedExpcetion`) => Future(expected)
        })

      expectedFailedResult ==== actualFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expected: Either[SomeError, Int] = Right(1)

      val fa     = run[Future, Either[SomeError, Int]](Right(1))
      val actual =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(CanRecover[Future].recoverFromNonFatalWith(fa) {
          case err => Future(Left(SomeError.someThrowable(err)))
        })

      actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalWithEitherShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure                  = SomeError.message("Failed")
      val expected: Either[SomeError, Int] = Left(expectedFailure)

      val fa     = run[Future, Either[SomeError, Int]](Left(expectedFailure))
      val actual =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(CanRecover[Future].recoverFromNonFatalWith(fa) {
          case NonFatal(_) => Future(Right(1))
        })

      actual ==== expected
    }

    def testCanRecover_Future_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion                            = new RuntimeException("Something's wrong")
      val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))

      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val actualFailedResult = ConcurrentSupport.futureToValue(
        CanRecover[Future]
          .recoverEitherFromNonFatalWith(fa) {
            case err => Future(Left(SomeError.someThrowable(err)))
          },
        waitFor
      )

      val expected: Either[SomeError, Int] = Right(1)

      val fa2    = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val actual =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(
          CanRecover[Future]
            .recoverEitherFromNonFatalWith(fa2) {
              case _ => Future(expected)
            }
        )

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expected: Either[SomeError, Int] = Right(1)

      val fa     = run[Future, Either[SomeError, Int]](Right(1))
      val actual = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(
        CanRecover[Future]
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
      val expected: Either[SomeError, Int] = Left(expectedFailure)

      val fa     = run[Future, Either[SomeError, Int]](Left(expectedFailure))
      val actual =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(
          CanRecover[Future]
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
        waitFor
      )(CanRecover[Future].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expected })

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
        waitFor
      )(CanRecover[Future].recoverFromNonFatal(fa) { case NonFatal(_) => 123 })

      actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalEitherShouldRecoverFromNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion                            = new RuntimeException("Something's wrong")
      val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))

      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val actualFailedResult =
        ConcurrentSupport.futureToValue(
          CanRecover[Future].recoverFromNonFatal(fa) {
            case err => Left(SomeError.someThrowable(err))
          },
          waitFor
        )

      val expected: Either[SomeError, Int] = Right(1)

      val fa2    = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val actual = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(CanRecover[Future].recoverFromNonFatal(fa2) { case NonFatal(`expectedExpcetion`) => expected })

      expectedFailedResult ==== actualFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalEitherShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expected: Either[SomeError, Int] = Right(1)

      val fa     = run[Future, Either[SomeError, Int]](Right(1))
      val actual =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(CanRecover[Future].recoverFromNonFatal(fa) {
          case err => Left(SomeError.someThrowable(err))
        })

      actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalEitherShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure                  = SomeError.message("Failed")
      val expected: Either[SomeError, Int] = Left(expectedFailure)

      val fa     = run[Future, Either[SomeError, Int]](Left(expectedFailure))
      val actual = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(CanRecover[Future].recoverFromNonFatal(fa) { case NonFatal(_) => Right(1) })

      actual ==== expected
    }

    def testCanRecover_Future_recoverEitherFromNonFatalShouldRecoverFromNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion                            = new RuntimeException("Something's wrong")
      val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))

      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val actualFailedResult = ConcurrentSupport.futureToValue(
        CanRecover[Future]
          .recoverEitherFromNonFatal(fa) {
            case err => Left(SomeError.someThrowable(err))
          },
        waitFor
      )

      val expected: Either[SomeError, Int] = Right(1)

      val fa2    = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val actual =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(CanRecover[Future].recoverEitherFromNonFatal(fa2) { case _ => expected })

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverEitherFromNonFatalShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expected: Either[SomeError, Int] = Right(1)

      val fa     = run[Future, Either[SomeError, Int]](Right(1))
      val actual = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(
        CanRecover[Future]
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
      val expected: Either[SomeError, Int] = Left(expectedFailure)

      val fa     = run[Future, Either[SomeError, Int]](Left(expectedFailure))
      val actual =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(CanRecover[Future].recoverEitherFromNonFatal(fa) { case NonFatal(_) => expected })

      actual ==== expected
    }

  }

}
