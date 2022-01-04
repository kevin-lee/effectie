package effectie.cats

import cats.*
import cats.data.EitherT
import cats.effect.*
import effectie.cats.CatsEffectRunner.TestContext
import cats.effect.unsafe.IORuntime
import cats.instances.all.*
import cats.syntax.all.*
import effectie.cats.Effectful.*
import effectie.CanCatch
import effectie.testing.types.SomeError
import effectie.{FxCtor, SomeControlThrowable}
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog.*
import hedgehog.runner.*

import java.util.concurrent.ExecutorService
import scala.util.control.ControlThrowable

/** @author Kevin Lee
  * @since 2020-07-31
  */
object CanCatchSpec extends Properties {

  override def tests: List[Test] = ioSpecs ++ futureSpecs ++ idSpecs

  /* IO */
  val ioSpecs = List(
    example(
      "test CanCatch[IO]catchNonFatalThrowable should catch NonFatal",
      IoSpec.testCanCatch_IO_catchNonFatalThrowableShouldCatchNonFatal
    ),
    example(
      "test CanCatch[IO]catchNonFatalThrowable should not catch Fatal",
      IoSpec.testCanCatch_IO_catchNonFatalThrowableShouldNotCatchFatal
    ),
    example(
      "test CanCatch[IO]catchNonFatalThrowable should return the successful result",
      IoSpec.testCanCatch_IO_catchNonFatalThrowableShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[IO]catchNonFatal should catch NonFatal",
      IoSpec.testCanCatch_IO_catchNonFatalShouldCatchNonFatal
    ),
    example(
      "test CanCatch[IO]catchNonFatal should not catch Fatal",
      IoSpec.testCanCatch_IO_catchNonFatalShouldNotCatchFatal
    ),
    example(
      "test CanCatch[IO]catchNonFatal should return the successful result",
      IoSpec.testCanCatch_IO_catchNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[IO]catchNonFatalEither should catch NonFatal",
      IoSpec.testCanCatch_IO_catchNonFatalEitherShouldCatchNonFatal
    ),
    example(
      "test CanCatch[IO]catchNonFatalEither should not catch Fatal",
      IoSpec.testCanCatch_IO_catchNonFatalEitherShouldNotCatchFatal
    ),
    example(
      "test CanCatch[IO]catchNonFatalEither should return the successful result",
      IoSpec.testCanCatch_IO_catchNonFatalEitherShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[IO]catchNonFatalEither should return the failed result",
      IoSpec.testCanCatch_IO_catchNonFatalEitherShouldReturnFailedResult
    ),
    example(
      "test CanCatch[IO]catchNonFatalEitherT should catch NonFatal",
      IoSpec.testCanCatch_IO_catchNonFatalEitherTShouldCatchNonFatal
    ),
    example(
      "test CanCatch[IO]catchNonFatalEitherT should not catch Fatal",
      IoSpec.testCanCatch_IO_catchNonFatalEitherTShouldNotCatchFatal
    ),
    example(
      "test CanCatch[IO]catchNonFatalEitherT should return the successful result",
      IoSpec.testCanCatch_IO_catchNonFatalEitherTShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[IO]catchNonFatalEitherT should return the failed result",
      IoSpec.testCanCatch_IO_catchNonFatalEitherTShouldReturnFailedResult
    ),
  )

  /* Future */
  val futureSpecs = List(
    example(
      "test CanCatch[Future]catchNonFatalThrowable should catch NonFatal",
      FutureSpec.testCanCatch_Future_catchNonFatalThrowableShouldCatchNonFatal
    ),
    example(
      "test CanCatch[Future]catchNonFatalThrowable should return the successful result",
      FutureSpec.testCanCatch_Future_catchNonFatalThrowableShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[Future]catchNonFatal should catch NonFatal",
      FutureSpec.testCanCatch_Future_catchNonFatalShouldCatchNonFatal
    ),
    example(
      "test CanCatch[Future]catchNonFatal should return the successful result",
      FutureSpec.testCanCatch_Future_catchNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[Future]catchNonFatalEither should catch NonFatal",
      FutureSpec.testCanCatch_Future_catchNonFatalEitherShouldCatchNonFatal
    ),
    example(
      "test CanCatch[Future]catchNonFatalEither should return the successful result",
      FutureSpec.testCanCatch_Future_catchNonFatalEitherShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[Future]catchNonFatalEither should return the failed result",
      FutureSpec.testCanCatch_Future_catchNonFatalEitherShouldReturnFailedResult
    ),
    example(
      "test CanCatch[Future]catchNonFatalEitherT should catch NonFatal",
      FutureSpec.testCanCatch_Future_catchNonFatalEitherTShouldCatchNonFatal
    ),
    example(
      "test CanCatch[Future]catchNonFatalEitherT should return the successful result",
      FutureSpec.testCanCatch_Future_catchNonFatalEitherTShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[Future]catchNonFatalEitherT should return the failed result",
      FutureSpec.testCanCatch_Future_catchNonFatalEitherTShouldReturnFailedResult
    ),
  )

  /* Id */
  val idSpecs = List(
    example(
      "test CanCatch[Id]catchNonFatalThrowable should catch NonFatal",
      IdSpec.testCanCatch_Id_catchNonFatalThrowableShouldCatchNonFatal
    ),
    example(
      "test CanCatch[Id]catchNonFatalThrowable should not catch Fatal",
      IdSpec.testCanCatch_Id_catchNonFatalThrowableShouldNotCatchFatal
    ),
    example(
      "test CanCatch[Id]catchNonFatalThrowable should return the successful result",
      IdSpec.testCanCatch_Id_catchNonFatalThrowableShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[Id]catchNonFatal should catch NonFatal",
      IdSpec.testCanCatch_Id_catchNonFatalShouldCatchNonFatal
    ),
    example(
      "test CanCatch[Id]catchNonFatal should not catch Fatal",
      IdSpec.testCanCatch_Id_catchNonFatalShouldNotCatchFatal
    ),
    example(
      "test CanCatch[Id]catchNonFatal should return the successful result",
      IdSpec.testCanCatch_Id_catchNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[Id]catchNonFatalEither should catch NonFatal",
      IdSpec.testCanCatch_Id_catchNonFatalEitherShouldCatchNonFatal
    ),
    example(
      "test CanCatch[Id]catchNonFatalEither should not catch Fatal",
      IdSpec.testCanCatch_Id_catchNonFatalEitherShouldNotCatchFatal
    ),
    example(
      "test CanCatch[Id]catchNonFatalEither should return the successful result",
      IdSpec.testCanCatch_Id_catchNonFatalEitherShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[Id]catchNonFatalEither should return the failed result",
      IdSpec.testCanCatch_Id_catchNonFatalEitherShouldReturnFailedResult
    ),
    example(
      "test CanCatch[Id]catchNonFatalEitherT should catch NonFatal",
      IdSpec.testCanCatch_Id_catchNonFatalEitherTShouldCatchNonFatal
    ),
    example(
      "test CanCatch[Id]catchNonFatalEitherT should not catch Fatal",
      IdSpec.testCanCatch_Id_catchNonFatalEitherTShouldNotCatchFatal
    ),
    example(
      "test CanCatch[Id]catchNonFatalEitherT should return the successful result",
      IdSpec.testCanCatch_Id_catchNonFatalEitherTShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[Id]catchNonFatalEitherT should return the failed result",
      IdSpec.testCanCatch_Id_catchNonFatalEitherTShouldReturnFailedResult
    )
  )

  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable

  def run[F[*]: FxCtor: Functor, A](a: => A): F[A] =
    effectOf[F](a)

  object IoSpec {
    import effectie.cats.Fx.given

    def testCanCatch_IO_catchNonFatalThrowableShouldCatchNonFatal: Result = {

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = expectedExpcetion.asLeft[Int]
      val actual            = CanCatch[IO].catchNonFatalThrowable(fa)

      actual.completeAs(expected)
    }

    def testCanCatch_IO_catchNonFatalThrowableShouldNotCatchFatal: Result = {

      val es: ExecutorService = ConcurrentSupport.newExecutorService(2)
      given rt: IORuntime     = testing.IoAppUtils.runtime(es)

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

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val fa: IO[Int] = run[IO, Int](1)
      val expected    = 1.asRight[Throwable]
      val actual      = CanCatch[IO].catchNonFatalThrowable(fa)

      actual.completeAs(expected)
    }

    def testCanCatch_IO_catchNonFatalShouldCatchNonFatal: Result = {

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = CanCatch[IO].catchNonFatal(fa)(SomeError.someThrowable)

      actual.completeAs(expected)
    }

    def testCanCatch_IO_catchNonFatalShouldNotCatchFatal: Result = {

      val es: ExecutorService = ConcurrentSupport.newExecutorService(2)
      given rt: IORuntime     = testing.IoAppUtils.runtime(es)

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

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val fa: IO[Int] = run[IO, Int](1)
      val expected    = 1.asRight[SomeError]
      val actual      = CanCatch[IO].catchNonFatal(fa)(SomeError.someThrowable)

      actual.completeAs(expected)
    }

    def testCanCatch_IO_catchNonFatalEitherShouldCatchNonFatal: Result = {

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = CanCatch[IO].catchNonFatalEither(fa)(SomeError.someThrowable)

      actual.completeAs(expected)
    }

    def testCanCatch_IO_catchNonFatalEitherShouldNotCatchFatal: Result = {

      val es: ExecutorService = ConcurrentSupport.newExecutorService(2)
      given rt: IORuntime     = testing.IoAppUtils.runtime(es)

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

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanCatch[IO].catchNonFatalEither(fa)(SomeError.someThrowable)

      actual.completeAs(expected)
    }

    def testCanCatch_IO_catchNonFatalEitherShouldReturnFailedResult: Result = {

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanCatch[IO].catchNonFatalEither(fa)(SomeError.someThrowable)

      actual.completeAs(expected)
    }

    def testCanCatch_IO_catchNonFatalEitherTShouldCatchNonFatal: Result = {

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = CanCatch[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

      actual.completeAs(expected)
    }

    def testCanCatch_IO_catchNonFatalEitherTShouldNotCatchFatal: Result = {

      val es: ExecutorService = ConcurrentSupport.newExecutorService(2)
      given rt: IORuntime     = testing.IoAppUtils.runtime(es)

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

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

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = CanCatch[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

      actual.completeAs(expected)
    }

    def testCanCatch_IO_catchNonFatalEitherTShouldReturnFailedResult: Result = {

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanCatch[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

      actual.completeAs(expected)
    }

  }

  object FutureSpec {
    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration.*
    import scala.concurrent.{ExecutionContext, Future}

    private given errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

    private val waitFor = WaitFor(1.second)

    def testCanCatch_Future_catchNonFatalThrowableShouldCatchNonFatal: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = expectedExpcetion.asLeft[Int]
      val actual            = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(CanCatch[Future].catchNonFatalThrowable(fa))

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalThrowableShouldReturnSuccessfulResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = run[Future, Int](1)
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(CanCatch[Future].catchNonFatalThrowable(fa))

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalShouldCatchNonFatal: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(CanCatch[Future].catchNonFatal(fa)(SomeError.someThrowable))

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalShouldReturnSuccessfulResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = run[Future, Int](1)
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(CanCatch[Future].catchNonFatal(fa)(SomeError.someThrowable))

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalEitherShouldCatchNonFatal: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(CanCatch[Future].catchNonFatalEither(fa)(SomeError.someThrowable))

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(CanCatch[Future].catchNonFatalEither(fa)(SomeError.someThrowable))

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalEitherShouldReturnFailedResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(CanCatch[Future].catchNonFatalEither(fa)(SomeError.someThrowable))

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalEitherTShouldCatchNonFatal: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(CanCatch[Future].catchNonFatalEitherT(fa)(SomeError.someThrowable).value)

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(CanCatch[Future].catchNonFatalEitherT(fa)(SomeError.someThrowable).value)

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalEitherTShouldReturnFailedResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(CanCatch[Future].catchNonFatalEitherT(fa)(SomeError.someThrowable).value)

      actual ==== expected
    }
  }

  object IdSpec {
    import effectie.cats.Fx.given

    def testCanCatch_Id_catchNonFatalThrowableShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = expectedExpcetion.asLeft[Int]
      val actual            = CanCatch[Id].catchNonFatalThrowable(fa)

      actual ==== expected
    }

    def testCanCatch_Id_catchNonFatalThrowableShouldNotCatchFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      lazy val fa        = run[Id, Int](throwThrowable[Int](fatalExpcetion))

      try {
        val actual = CanCatch[Id].catchNonFatalThrowable(fa)
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_Id_catchNonFatalThrowableShouldReturnSuccessfulResult: Result = {

      val fa       = run[Id, Int](1)
      val expected = 1.asRight[Throwable]
      val actual   = CanCatch[Id].catchNonFatalThrowable(fa)

      actual ==== expected
    }

    def testCanCatch_Id_catchNonFatalShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = CanCatch[Id].catchNonFatal(fa)(SomeError.someThrowable)

      actual ==== expected
    }

    def testCanCatch_Id_catchNonFatalShouldNotCatchFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      lazy val fa        = run[Id, Int](throwThrowable[Int](fatalExpcetion))

      try {
        val actual = CanCatch[Id].catchNonFatal(fa)(SomeError.someThrowable)
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_Id_catchNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = run[Id, Int](1)
      val expected = 1.asRight[SomeError]
      val actual   = CanCatch[Id].catchNonFatal(fa)(SomeError.someThrowable)

      actual ==== expected
    }

    def testCanCatch_Id_catchNonFatalEitherShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa           = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = CanCatch[Id].catchNonFatalEither(fa)(SomeError.someThrowable)

      actual ==== expected
    }

    def testCanCatch_Id_catchNonFatalEitherShouldNotCatchFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      lazy val fa        = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual = CanCatch[Id].catchNonFatalEither(fa)(SomeError.someThrowable)
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_Id_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanCatch[Id].catchNonFatalEither(fa)(SomeError.someThrowable)

      actual ==== expected
    }

    def testCanCatch_Id_catchNonFatalEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanCatch[Id].catchNonFatalEither(fa)(SomeError.someThrowable)

      actual ==== expected
    }

    def testCanCatch_Id_catchNonFatalEitherTShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa           = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = CanCatch[Id].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

      actual ==== expected
    }

    def testCanCatch_Id_catchNonFatalEitherTShouldNotCatchFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      lazy val fa        = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

      try {
        val actual = CanCatch[Id].catchNonFatalEitherT(fa)(SomeError.someThrowable).value
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_Id_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = {

      val fa       = EitherT(run[Id, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = CanCatch[Id].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

      actual ==== expected
    }

    def testCanCatch_Id_catchNonFatalEitherTShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanCatch[Id].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

      actual ==== expected
    }

  }

}
