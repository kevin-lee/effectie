package effectie.instances.ce2

import canCatch._
import cats.data.EitherT
import cats.effect._
import cats.syntax.all._
import effectie.SomeControlThrowable
import effectie.core._
import effectie.syntax.error._
import effectie.syntax.fx._
import effectie.testing.types._
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import fxCtor._
import hedgehog._
import hedgehog.runner._

/** @author Kevin Lee
  * @since 2020-07-31
  */
object canCatchSpec extends Properties {

  private implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = ioSpecs ++ futureSpecs

  val ioSpecs = List(
    /* IO */
    example(
      "test CanCatch[IO]catchNonFatalThrowable should catch NonFatal",
      IoSpec.testCanCatch_IO_catchNonFatalThrowableShouldCatchNonFatal,
    ),
    example(
      "test CanCatch[IO]catchNonFatalThrowable should not catch Fatal",
      IoSpec.testCanCatch_IO_catchNonFatalThrowableShouldNotCatchFatal,
    ),
    example(
      "test CanCatch[IO]catchNonFatalThrowable should return the successful result",
      IoSpec.testCanCatch_IO_catchNonFatalThrowableShouldReturnSuccessfulResult,
    ),
    example(
      "test CanCatch[IO]catchNonFatal should catch NonFatal",
      IoSpec.testCanCatch_IO_catchNonFatalShouldCatchNonFatal,
    ),
    example(
      "test CanCatch[IO]catchNonFatal should not catch Fatal",
      IoSpec.testCanCatch_IO_catchNonFatalShouldNotCatchFatal,
    ),
    example(
      "test CanCatch[IO]catchNonFatal should return the successful result",
      IoSpec.testCanCatch_IO_catchNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanCatch[IO]catchNonFatalEither should catch NonFatal",
      IoSpec.testCanCatch_IO_catchNonFatalEitherShouldCatchNonFatal,
    ),
    example(
      "test CanCatch[IO]catchNonFatalEither should not catch Fatal",
      IoSpec.testCanCatch_IO_catchNonFatalEitherShouldNotCatchFatal,
    ),
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
    example(
      "test CanCatch[IO]catchNonFatalEitherT should not catch Fatal",
      IoSpec.testCanCatch_IO_catchNonFatalEitherTShouldNotCatchFatal,
    ),
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
  val futureSpecs = effectie.instances.future.canCatchSpec.futureSpecs ++ List(
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

  object IoSpec {

    def testCanCatch_IO_catchNonFatalThrowableShouldCatchNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedException))
      val expected          = expectedException.asLeft[Int]
      val actual            = CanCatch[IO].catchNonFatalThrowable(fa).unsafeRunSync()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanCatch_IO_catchNonFatalThrowableShouldNotCatchFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))

      try {
        val actual = CanCatch[IO].catchNonFatalThrowable(fa).unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: SomeControlThrowable =>
          ex.getMessage ==== fatalExpcetion.getMessage

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_IO_catchNonFatalThrowableShouldReturnSuccessfulResult: Result = {

      val fa       = run[IO, Int](1)
      val expected = 1.asRight[Throwable]
      val actual   = CanCatch[IO].catchNonFatalThrowable(fa).unsafeRunSync()

      actual ==== expected
    }

    def testCanCatch_IO_catchNonFatalShouldCatchNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedException))
      val expected          = SomeError.someThrowable(expectedException).asLeft[Int]
      val actual            = CanCatch[IO].catchNonFatal(fa)(SomeError.someThrowable).unsafeRunSync()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanCatch_IO_catchNonFatalShouldNotCatchFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))

      try {
        val actual = CanCatch[IO].catchNonFatal(fa)(SomeError.someThrowable).unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: SomeControlThrowable =>
          ex.getMessage ==== fatalExpcetion.getMessage

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_IO_catchNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = run[IO, Int](1)
      val expected = 1.asRight[SomeError]
      val actual   = CanCatch[IO].catchNonFatal(fa)(SomeError.someThrowable).unsafeRunSync()

      actual ==== expected
    }

    def testCanCatch_IO_catchNonFatalEitherShouldCatchNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
      val expected          = SomeError.someThrowable(expectedException).asLeft[Int]
      val actual            = CanCatch[IO].catchNonFatalEither(fa)(SomeError.someThrowable).unsafeRunSync()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanCatch_IO_catchNonFatalEitherShouldNotCatchFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual = CanCatch[IO].catchNonFatalEither(fa)(SomeError.someThrowable).unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: SomeControlThrowable =>
          ex.getMessage ==== fatalExpcetion.getMessage

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_IO_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanCatch[IO].catchNonFatalEither(fa)(SomeError.someThrowable).unsafeRunSync()

      actual ==== expected
    }

    def testCanCatch_IO_catchNonFatalEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanCatch[IO].catchNonFatalEither(fa)(SomeError.someThrowable).unsafeRunSync()

      actual ==== expected
    }

    def testCanCatch_IO_catchNonFatalEitherTShouldCatchNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong")
      val fa       = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
      val expected = SomeError.someThrowable(expectedException).asLeft[Int]
      val actual   = CanCatch[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.unsafeRunSync()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanCatch_IO_catchNonFatalEitherTShouldNotCatchFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

      try {
        val actual = CanCatch[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: SomeControlThrowable =>
          ex.getMessage ==== fatalExpcetion.getMessage

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_IO_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = {

      val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = CanCatch[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.unsafeRunSync()

      actual ==== expected
    }

    def testCanCatch_IO_catchNonFatalEitherTShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanCatch[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.unsafeRunSync()

      actual ==== expected
    }

  }

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
//      val expectedException = new RuntimeException("Something's wrong")
//      val fa                = run[Future, Int](throwThrowable[Int](expectedException))
//      val expected          = expectedException.asLeft[Int]
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
//      val expectedException = new RuntimeException("Something's wrong")
//      val fa                = run[Future, Int](throwThrowable[Int](expectedException))
//      val expected          = SomeError.someThrowable(expectedException).asLeft[Int]
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
//      val expectedException = new RuntimeException("Something's wrong")
//      val fa       = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
//      val expected = SomeError.someThrowable(expectedException).asLeft[Int]
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

      val expectedException = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
      val expected = SomeError.someThrowable(expectedException).asLeft[Int]
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
