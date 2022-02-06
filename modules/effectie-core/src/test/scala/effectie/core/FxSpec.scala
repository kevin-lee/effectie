package effectie.core

import cats.Eq
import cats.syntax.eq._
import effectie.testing.cats.MonadSpec
import effectie.testing.tools.expectThrowable
import effectie.testing.types.{SomeError, SomeThrowableError}
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog._
import hedgehog.runner._

import scala.concurrent.Await

/** @author Kevin Lee
  * @since 2022-01-06
  */
object FxSpec extends Properties {
  implicit private val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = futureSpecs

  /* Future */
  val futureSpecs = List(
    property("test Fx[Future].effectOf", FutureSpec.testEffectOf),
    property("test Fx[Future].pureOf", FutureSpec.testPureOf),
    example("test Fx[Future].unitOf", FutureSpec.testUnitOf),
    example("test Fx[Future].errorOf", FutureSpec.testErrorOf),
  ) ++
    FutureSpec.testMonadLaws ++
    List(
      /* Future */
      example(
        "test Fx[Future]catchNonFatalThrowable should catch NonFatal",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalThrowableShouldCatchNonFatal
      ),
      example(
        "test Fx[Future]catchNonFatalThrowable should return the successful result",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalThrowableShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future]catchNonFatal should catch NonFatal",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalShouldCatchNonFatal
      ),
      example(
        "test Fx[Future]catchNonFatal should return the successful result",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future]catchNonFatalEither should catch NonFatal",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalEitherShouldCatchNonFatal
      ),
      example(
        "test Fx[Future]catchNonFatalEither should return the successful result",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalEitherShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future]catchNonFatalEither should return the failed result",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalEitherShouldReturnFailedResult
      ),
    ) ++ List(
      example(
        "test Fx[Future].handleNonFatalWith should handle NonFatal",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleNonFatalWithShouldHandleNonFatalWith
      ),
      example(
        "test Fx[Future].handleNonFatalWith should return the successful result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleNonFatalWithShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future].handleNonFatalWithEither should handle NonFatal",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleNonFatalWithEitherShouldHandleNonFatalWith
      ),
      example(
        "test Fx[Future].handleNonFatalWithEither should return the successful result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleNonFatalWithEitherShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future].handleNonFatalWithEither should return the failed result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleNonFatalWithEitherShouldReturnFailedResult
      ),
      example(
        "test Fx[Future].handleEitherNonFatalWith should handle NonFatal",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleEitherNonFatalWithShouldHandleNonFatalWith
      ),
      example(
        "test Fx[Future].handleEitherNonFatalWith should return the successful result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleEitherNonFatalWithShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future].handleEitherNonFatalWith should return the failed result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleEitherNonFatalWithShouldReturnFailedResult
      ),
      example(
        "test Fx[Future].handleNonFatal should handle NonFatal",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleNonFatalShouldHandleNonFatal
      ),
      example(
        "test Fx[Future].handleNonFatal should return the successful result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleNonFatalShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future].handleNonFatalEither should handle NonFatal",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleNonFatalEitherShouldHandleNonFatal
      ),
      example(
        "test Fx[Future].handleNonFatalEither should return the successful result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleNonFatalEitherShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future].handleNonFatalEither should return the failed result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleNonFatalEitherShouldReturnFailedResult
      ),
      example(
        "test Fx[Future].handleEitherNonFatal should handle NonFatal",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleEitherNonFatalShouldHandleNonFatal
      ),
      example(
        "test Fx[Future].handleEitherNonFatal should return the successful result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleEitherNonFatalShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future].handleEitherNonFatal should return the failed result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleEitherNonFatalShouldReturnFailedResult
      ),
    ) ++ List(
      example(
        "test Fx[Future].recoverFromNonFatalWith should catch NonFatal",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverFromNonFatalWithShouldRecoverFromNonFatal
      ),
      example(
        "test Fx[Future].recoverFromNonFatalWith should return the successful result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverFromNonFatalWithShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future].recoverFromNonFatalWithEither should catch NonFatal",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal
      ),
      example(
        "test Fx[Future].recoverFromNonFatalWithEither should return the successful result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future].recoverFromNonFatalWithEither should return the failed result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverFromNonFatalWithEitherShouldReturnFailedResult
      ),
      example(
        "test Fx[Future].recoverEitherFromNonFatalWith should catch NonFatal",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal
      ),
      example(
        "test Fx[Future].recoverEitherFromNonFatalWith should return the successful result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future].recoverEitherFromNonFatalWith should return the failed result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverEitherFromNonFatalWithShouldReturnFailedResult
      ),
      example(
        "test Fx[Future].recoverFromNonFatal should catch NonFatal",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverFromNonFatalShouldRecoverFromNonFatal
      ),
      example(
        "test Fx[Future].recoverFromNonFatal should return the successful result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverFromNonFatalShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future].recoverFromNonFatalEither should catch NonFatal",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverFromNonFatalEitherShouldRecoverFromNonFatal
      ),
      example(
        "test Fx[Future].recoverFromNonFatalEither should return the successful result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverFromNonFatalEitherShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future].recoverFromNonFatalEither should return the failed result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverFromNonFatalEitherShouldReturnFailedResult
      ),
      example(
        "test Fx[Future].recoverEitherFromNonFatal should catch NonFatal",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverEitherFromNonFatalShouldRecoverFromNonFatal
      ),
      example(
        "test Fx[Future].recoverEitherFromNonFatal should return the successful result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverEitherFromNonFatalShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future].recoverEitherFromNonFatal should return the failed result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverEitherFromNonFatalShouldReturnFailedResult
      ),
    )

  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable

  def run[F[_]: Fx, A](a: => A): F[A] =
    Fx[F].effectOf(a)

  object FutureSpec {
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
      var actual               = before
      val testBefore           = actual ==== before
      val future: Future[Unit] = Fx[Future].effectOf({ actual = after; () })
      ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)
      val testAfterRun         = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfterRun.log("testAfterRun")
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
      var actual       = before
      val testBefore   = actual ==== before
      val future       = Fx[Future].pureOf({ actual = after; () })
      ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)
      val testAfterRun = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfterRun.log("testAfterRun")
        )
      )
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

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val future = Fx[Future].errorOf[Unit](expectedError)
      expectThrowable(ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future), expectedError)
    }

    def testMonadLaws: List[Test] = {

      implicit val ec: scala.concurrent.ExecutionContext             = scala.concurrent.ExecutionContext.global
      implicit def futureEqual[A](implicit EQ: Eq[A]): Eq[Future[A]] = new Eq[Future[A]] {
        override def eqv(x: Future[A], y: Future[A]): Boolean =
          Await.result(x.flatMap(a => y.map(b => EQ.eqv(a, b))), 1.second)
      }

      implicit val eqFuture: Eq[Future[Int]] =
        (x, y) => {
          val future = x.flatMap(xx => y.map(_ === xx))
          Await.result(future, waitFor.waitFor)
        }

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
          waitFor
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
          waitFor
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
          waitFor
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
          waitFor
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
          waitFor
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
          waitFor
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
          waitFor
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
          waitFor
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
          waitFor
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
            waitFor
          )

        val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected: Either[SomeError, Int] = Right(1)
        val actual                           = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
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
            waitFor
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
          waitFor
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
          waitFor
        )

        val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected: Either[SomeError, Int] = Right(1)
        val actual                           =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor
          )(Fx[Future].handleEitherNonFatalWith(fa2)(err => Future(expected)))

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
          waitFor
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
            waitFor
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
          waitFor
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
          waitFor
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
            waitFor
          )

        val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected: Either[SomeError, Int] = Right(1)
        val actual                           = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
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
            waitFor
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
          waitFor
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
          waitFor
        )

        val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected: Either[SomeError, Int] = Right(1)
        val actual                           =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor
          )(Fx[Future].handleEitherNonFatal(fa2)(err => expected))

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
          waitFor
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
            waitFor
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
          waitFor
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
          waitFor
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
            waitFor
          )

        val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected: Either[SomeError, Int] = Right(1)
        val actual                           =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor
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
            waitFor
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
            waitFor
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
          waitFor
        )

        val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected: Either[SomeError, Int] = Right(1)
        val actual                           =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor
          )(
            Fx[Future]
              .recoverEitherFromNonFatalWith(fa2) {
                case err => Future(expected)
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
          waitFor
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
            waitFor
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
          waitFor
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
          waitFor
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
            waitFor
          )

        val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected: Either[SomeError, Int] = Right(1)
        val actual                           = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
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
            waitFor
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
          waitFor
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
          waitFor
        )

        val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected: Either[SomeError, Int] = Right(1)
        val actual                           =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor
          )(Fx[Future].recoverEitherFromNonFatal(fa2) { case err => expected })

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
          waitFor
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
            waitFor
          )(Fx[Future].recoverEitherFromNonFatal(fa) { case NonFatal(_) => expected })

        actual ==== expected
      }

    }

  }

}
