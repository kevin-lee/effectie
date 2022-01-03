package effectie.cats

import Catching._
import cats._
import cats.data.EitherT
import cats.effect._
import cats.effect.unsafe.IORuntime
import cats.instances.all._
import cats.syntax.all._
import effectie.cats.Effectful._
import effectie.cats.compat.CatsEffectIoCompatForFuture
import effectie.testing.types.SomeError
import effectie.{ConcurrentSupport, SomeControlThrowable}
import hedgehog._
import hedgehog.runner._

import scala.util.control.ControlThrowable

/** @author Kevin Lee
  * @since 2020-07-31
  */
object CatchingSpec extends Properties {
  type Fx[F[_]]     = effectie.Fx[F]
  type FxCtor[F[_]] = effectie.FxCtor[F]

  override def tests: List[Test] = List(
    /* IO */
    example(
      "test Catching.catchNonFatal[IO] should catch NonFatal",
      IoSpec.testCatching_IO_catchNonFatalShouldCatchNonFatal
    ),
    example(
      "test Catching.catchNonFatal[IO] should not catch Fatal",
      IoSpec.testCatching_IO_catchNonFatalShouldNotCatchFatal
    ),
    example(
      "test Catching.catchNonFatal[IO] should return the successful result",
      IoSpec.testCatching_IO_catchNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test Catching.catchNonFatalF[IO] should catch NonFatal",
      IoSpec.testCatching_IO_catchNonFatalFShouldCatchNonFatal
    ),
    example(
      "test Catching.catchNonFatalF[IO] should not catch Fatal",
      IoSpec.testCatching_IO_catchNonFatalFShouldNotCatchFatal
    ),
    example(
      "test Catching.catchNonFatalF[IO] should return the successful result",
      IoSpec.testCatching_IO_catchNonFatalFShouldReturnSuccessfulResult
    ),
    example(
      "test Catching.catchNonFatalEither[IO] should catch NonFatal",
      IoSpec.testCatching_IO_catchNonFatalEitherShouldCatchNonFatal
    ),
    example(
      "test Catching.catchNonFatalEither[IO] should not catch Fatal",
      IoSpec.testCatching_IO_catchNonFatalEitherShouldNotCatchFatal
    ),
    example(
      "test Catching.catchNonFatalEither[IO] should return the successful result",
      IoSpec.testCatching_IO_catchNonFatalEitherShouldReturnSuccessfulResult
    ),
    example(
      "test Catching.catchNonFatalEither[IO] should return the failed result",
      IoSpec.testCatching_IO_catchNonFatalEitherShouldReturnFailedResult
    ),
    example(
      "test Catching.catchNonFatalEitherF[IO] should catch NonFatal",
      IoSpec.testCatching_IO_catchNonFatalEitherFShouldCatchNonFatal
    ),
    example(
      "test Catching.catchNonFatalEitherF[IO] should not catch Fatal",
      IoSpec.testCatching_IO_catchNonFatalEitherFShouldNotCatchFatal
    ),
    example(
      "test Catching.catchNonFatalEitherF[IO] should return the successful result",
      IoSpec.testCatching_IO_catchNonFatalEitherFShouldReturnSuccessfulResult
    ),
    example(
      "test Catching.catchNonFatalEitherF[IO] should return the failed result",
      IoSpec.testCatching_IO_catchNonFatalEitherFShouldReturnFailedResult
    ),
    example(
      "test Catching.catchNonFatalEitherT[IO] should catch NonFatal",
      IoSpec.testCatching_IO_catchNonFatalEitherTShouldCatchNonFatal
    ),
    example(
      "test Catching.catchNonFatalEitherT[IO] should not catch Fatal",
      IoSpec.testCatching_IO_catchNonFatalEitherTShouldNotCatchFatal
    ),
    example(
      "test Catching.catchNonFatalEitherT[IO] should return the successful result",
      IoSpec.testCatching_IO_catchNonFatalEitherTShouldReturnSuccessfulResult
    ),
    example(
      "test Catching.catchNonFatalEitherT[IO] should return the failed result",
      IoSpec.testCatching_IO_catchNonFatalEitherTShouldReturnFailedResult
    ),
    /* Future */

    example(
      "test Catching.catchNonFatal[Future] should catch NonFatal",
      FutureSpec.testCatching_Future_catchNonFatalShouldCatchNonFatal
    ),
    example(
      "test Catching.catchNonFatal[Future] should return the successful result",
      FutureSpec.testCatching_Future_catchNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test Catching.catchNonFatalF[Future] should catch NonFatal",
      FutureSpec.testCatching_Future_catchNonFatalFShouldCatchNonFatal
    ),
    example(
      "test Catching.catchNonFatalF[Future] should return the successful result",
      FutureSpec.testCatching_Future_catchNonFatalFShouldReturnSuccessfulResult
    ),
    example(
      "test Catching.catchNonFatalEither[Future] should catch NonFatal",
      FutureSpec.testCatching_Future_catchNonFatalEitherShouldCatchNonFatal
    ),
    example(
      "test Catching.catchNonFatalEither[Future] should return the successful result",
      FutureSpec.testCatching_Future_catchNonFatalEitherShouldReturnSuccessfulResult
    ),
    example(
      "test Catching.catchNonFatalEither[Future] should return the failed result",
      FutureSpec.testCatching_Future_catchNonFatalEitherShouldReturnFailedResult
    ),
    example(
      "test Catching.catchNonFatalEitherF[Future] should catch NonFatal",
      FutureSpec.testCatching_Future_catchNonFatalEitherFShouldCatchNonFatal
    ),
    example(
      "test Catching.catchNonFatalEitherF[Future] should return the successful result",
      FutureSpec.testCatching_Future_catchNonFatalEitherFShouldReturnSuccessfulResult
    ),
    example(
      "test Catching.catchNonFatalEitherF[Future] should return the failed result",
      FutureSpec.testCatching_Future_catchNonFatalEitherFShouldReturnFailedResult
    ),
    example(
      "test Catching.catchNonFatalEitherT[Future] should catch NonFatal",
      FutureSpec.testCatching_Future_catchNonFatalEitherTShouldCatchNonFatal
    ),
    example(
      "test Catching.catchNonFatalEitherT[Future] should return the successful result",
      FutureSpec.testCatching_Future_catchNonFatalEitherTShouldReturnSuccessfulResult
    ),
    example(
      "test Catching.catchNonFatalEitherT[Future] should return the failed result",
      FutureSpec.testCatching_Future_catchNonFatalEitherTShouldReturnFailedResult
    ),
    /* Id */
    example(
      "test Catching.catchNonFatal[Id] should catch NonFatal",
      IdSpec.testCatching_Id_catchNonFatalShouldCatchNonFatal
    ),
    example(
      "test Catching.catchNonFatal[Id] should not catch Fatal",
      IdSpec.testCatching_Id_catchNonFatalShouldNotCatchFatal
    ),
    example(
      "test Catching.catchNonFatal[Id] should return the successful result",
      IdSpec.testCatching_Id_catchNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test Catching.catchNonFatalF[Id] should catch NonFatal",
      IdSpec.testCatching_Id_catchNonFatalFShouldCatchNonFatal
    ),
    example(
      "test Catching.catchNonFatalF[Id] should not catch Fatal",
      IdSpec.testCatching_Id_catchNonFatalFShouldNotCatchFatal
    ),
    example(
      "test Catching.catchNonFatalF[Id] should return the successful result",
      IdSpec.testCatching_Id_catchNonFatalFShouldReturnSuccessfulResult
    ),
    example(
      "test Catching.catchNonFatalEither[Id] should catch NonFatal",
      IdSpec.testCatching_Id_catchNonFatalEitherShouldCatchNonFatal
    ),
    example(
      "test Catching.catchNonFatalEither[Id] should not catch Fatal",
      IdSpec.testCatching_Id_catchNonFatalEitherShouldNotCatchFatal
    ),
    example(
      "test Catching.catchNonFatalEither[Id] should return the successful result",
      IdSpec.testCatching_Id_catchNonFatalEitherShouldReturnSuccessfulResult
    ),
    example(
      "test Catching.catchNonFatalEither[Id] should return the failed result",
      IdSpec.testCatching_Id_catchNonFatalEitherShouldReturnFailedResult
    ),
    example(
      "test Catching.catchNonFatalEitherF[Id] should catch NonFatal",
      IdSpec.testCatching_Id_catchNonFatalEitherFShouldCatchNonFatal
    ),
    example(
      "test Catching.catchNonFatalEitherF[Id] should not catch Fatal",
      IdSpec.testCatching_Id_catchNonFatalEitherFShouldNotCatchFatal
    ),
    example(
      "test Catching.catchNonFatalEitherF[Id] should return the successful result",
      IdSpec.testCatching_Id_catchNonFatalEitherFShouldReturnSuccessfulResult
    ),
    example(
      "test Catching.catchNonFatalEitherF[Id] should return the failed result",
      IdSpec.testCatching_Id_catchNonFatalEitherFShouldReturnFailedResult
    ),
    example(
      "test Catching.catchNonFatalEitherT[Id] should catch NonFatal",
      IdSpec.testCatching_Id_catchNonFatalEitherTShouldCatchNonFatal
    ),
    example(
      "test Catching.catchNonFatalEitherT[Id] should not catch Fatal",
      IdSpec.testCatching_Id_catchNonFatalEitherTShouldNotCatchFatal
    ),
    example(
      "test Catching.catchNonFatalEitherT[Id] should return the successful result",
      IdSpec.testCatching_Id_catchNonFatalEitherTShouldReturnSuccessfulResult
    ),
    example(
      "test Catching.catchNonFatalEitherT[Id] should return the failed result",
      IdSpec.testCatching_Id_catchNonFatalEitherTShouldReturnFailedResult
    )
  )

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable

  def run[F[_]: FxCtor: Functor, A](a: => A): F[A] =
    effectOf[F](a)

  object IoSpec {

    import effectie.cats.Fx._

    def testCatching_IO_catchNonFatalShouldCatchNonFatal: Result = {

      import CatsEffectRunner._
      implicit val ticket: Ticker = Ticker(TestContext())

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = catchNonFatal(fa)(SomeError.someThrowable)

      actual.completeAs(expected)
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCatching_IO_catchNonFatalShouldNotCatchFatal: Result = {

      val compat                 = new CatsEffectIoCompatForFuture
      implicit val rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))

      try {
        val actual = catchNonFatal(fa)(SomeError.someThrowable).unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCatching_IO_catchNonFatalShouldReturnSuccessfulResult: Result = {

      import CatsEffectRunner._
      implicit val ticket: Ticker = Ticker(TestContext())

      val fa       = run[IO, Int](1)
      val expected = 1.asRight[SomeError]
      val actual   = catchNonFatal(fa)(SomeError.someThrowable)

      actual.completeAs(expected)
    }

    def testCatching_IO_catchNonFatalFShouldCatchNonFatal: Result = {

      import CatsEffectRunner._
      implicit val ticket: Ticker = Ticker(TestContext())

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = catchNonFatalF[IO](throwThrowable[Int](expectedExpcetion))(SomeError.someThrowable)

      actual.completeAs(expected)
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCatching_IO_catchNonFatalFShouldNotCatchFatal: Result = {

      val compat                 = new CatsEffectIoCompatForFuture
      implicit val rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

      val fatalExpcetion = SomeControlThrowable("Something's wrong")

      try {
        val actual = catchNonFatalF[IO](throwThrowable[Int](fatalExpcetion))(SomeError.someThrowable).unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCatching_IO_catchNonFatalFShouldReturnSuccessfulResult: Result = {

      import CatsEffectRunner._
      implicit val ticket: Ticker = Ticker(TestContext())

      val expected = 1.asRight[SomeError]
      val actual   = catchNonFatalF[IO](1)(SomeError.someThrowable)

      actual.completeAs(expected)
    }

    def testCatching_IO_catchNonFatalEitherShouldCatchNonFatal: Result = {

      import CatsEffectRunner._
      implicit val ticket: Ticker = Ticker(TestContext())

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = catchNonFatalEither(fa)(SomeError.someThrowable)

      actual.completeAs(expected)
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCatching_IO_catchNonFatalEitherShouldNotCatchFatal: Result = {

      val compat                 = new CatsEffectIoCompatForFuture
      implicit val rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual = catchNonFatalEither(fa)(SomeError.someThrowable).unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCatching_IO_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

      import CatsEffectRunner._
      implicit val ticket: Ticker = Ticker(TestContext())

      val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = catchNonFatalEither(fa)(SomeError.someThrowable)

      actual.completeAs(expected)
    }

    def testCatching_IO_catchNonFatalEitherShouldReturnFailedResult: Result = {

      import CatsEffectRunner._
      implicit val ticket: Ticker = Ticker(TestContext())

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = catchNonFatalEither(fa)(SomeError.someThrowable)

      actual.completeAs(expected)
    }

    def testCatching_IO_catchNonFatalEitherFShouldCatchNonFatal: Result = {

      import CatsEffectRunner._
      implicit val ticket: Ticker = Ticker(TestContext())

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            =
        catchNonFatalEitherF[IO](throwThrowable[Either[SomeError, Int]](expectedExpcetion))(SomeError.someThrowable)

      actual.completeAs(expected)
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCatching_IO_catchNonFatalEitherFShouldNotCatchFatal: Result = {

      val compat                 = new CatsEffectIoCompatForFuture
      implicit val rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

      val fatalExpcetion = SomeControlThrowable("Something's wrong")

      try {
        val actual =
          catchNonFatalEitherF[IO](throwThrowable[Either[SomeError, Int]](fatalExpcetion))(SomeError.someThrowable)
            .unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCatching_IO_catchNonFatalEitherFShouldReturnSuccessfulResult: Result = {

      import CatsEffectRunner._
      implicit val ticket: Ticker = Ticker(TestContext())

      val expected = 1.asRight[SomeError]
      val actual   = catchNonFatalEitherF[IO](1.asRight[SomeError])(SomeError.someThrowable)

      actual.completeAs(expected)
    }

    def testCatching_IO_catchNonFatalEitherFShouldReturnFailedResult: Result = {

      import CatsEffectRunner._
      implicit val ticket: Ticker = Ticker(TestContext())

      val expectedFailure = SomeError.message("Failed")
      val expected        = expectedFailure.asLeft[Int]
      val actual          = catchNonFatalEitherF[IO](expectedFailure.asLeft[Int])(SomeError.someThrowable)

      actual.completeAs(expected)
    }

    def testCatching_IO_catchNonFatalEitherTShouldCatchNonFatal: Result = {

      import CatsEffectRunner._
      implicit val ticket: Ticker = Ticker(TestContext())

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa       = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual   =
        catchNonFatalEitherT[IO](fa)(SomeError.someThrowable).value

      actual.completeAs(expected)
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCatching_IO_catchNonFatalEitherTShouldNotCatchFatal: Result = {

      val compat                 = new CatsEffectIoCompatForFuture
      implicit val rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

      try {
        val actual = catchNonFatalEitherT[IO](fa)(SomeError.someThrowable).value.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCatching_IO_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = {

      import CatsEffectRunner._
      implicit val ticket: Ticker = Ticker(TestContext())

      val expected = 1.asRight[SomeError]
      val fa       = EitherT(run[IO, Either[SomeError, Int]](expected))
      val actual   = catchNonFatalEitherT[IO](fa)(SomeError.someThrowable).value

      actual.completeAs(expected)
    }

    def testCatching_IO_catchNonFatalEitherTShouldReturnFailedResult: Result = {

      import CatsEffectRunner._
      implicit val ticket: Ticker = Ticker(TestContext())

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          = catchNonFatalEitherT[IO](fa)(SomeError.someThrowable).value

      actual.completeAs(expected)
    }
  }

  // ///////

  object FutureSpec {
    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration._
    import scala.concurrent.{ExecutionContext, Future}

    val waitFor: FiniteDuration = 1.second

    def testCatching_Future_catchNonFatalShouldCatchNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = ConcurrentSupport.futureToValueAndTerminate(
        catchNonFatal(fa)(SomeError.someThrowable),
        waitFor
      )

      actual ==== expected
    }

    def testCatching_Future_catchNonFatalShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val fa       = run[Future, Int](1)
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        catchNonFatal(fa)(SomeError.someThrowable),
        waitFor
      )

      actual ==== expected
    }

    def testCatching_Future_catchNonFatalFShouldCatchNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = ConcurrentSupport.futureToValueAndTerminate(
        catchNonFatalF[Future](throwThrowable[Int](expectedExpcetion))(SomeError.someThrowable),
        waitFor
      )

      actual ==== expected
    }

    def testCatching_Future_catchNonFatalFShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        catchNonFatalF[Future](1)(SomeError.someThrowable),
        waitFor
      )

      actual ==== expected
    }

    def testCatching_Future_catchNonFatalEitherShouldCatchNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa       = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        catchNonFatalEither(fa)(SomeError.someThrowable),
        waitFor
      )

      actual ==== expected
    }

    def testCatching_Future_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        catchNonFatalEither(fa)(SomeError.someThrowable),
        waitFor
      )

      actual ==== expected
    }

    def testCatching_Future_catchNonFatalEitherShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = ConcurrentSupport.futureToValueAndTerminate(
        catchNonFatalEither(fa)(SomeError.someThrowable),
        waitFor
      )

      actual ==== expected
    }

    def testCatching_Future_catchNonFatalEitherFShouldCatchNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            =
        ConcurrentSupport.futureToValueAndTerminate(
          catchNonFatalEitherF[Future](
            throwThrowable[Either[SomeError, Int]](expectedExpcetion)
          )(SomeError.someThrowable),
          waitFor
        )

      actual ==== expected
    }

    def testCatching_Future_catchNonFatalEitherFShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          catchNonFatalEitherF[Future](1.asRight[SomeError])(SomeError.someThrowable),
          waitFor
        )

      actual ==== expected
    }

    def testCatching_Future_catchNonFatalEitherFShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val expectedFailure = SomeError.message("Failed")
      val expected        = expectedFailure.asLeft[Int]
      val actual          = ConcurrentSupport.futureToValueAndTerminate(
        catchNonFatalEitherF[Future](expectedFailure.asLeft[Int])(SomeError.someThrowable),
        waitFor
      )

      actual ==== expected
    }

    def testCatching_Future_catchNonFatalEitherTShouldCatchNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        catchNonFatalEitherT[Future](fa)(SomeError.someThrowable).value,
        waitFor
      )

      actual ==== expected
    }

    def testCatching_Future_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        catchNonFatalEitherT[Future](fa)(SomeError.someThrowable).value,
        waitFor
      )

      actual ==== expected
    }

    def testCatching_Future_catchNonFatalEitherTShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          = ConcurrentSupport.futureToValueAndTerminate(
        catchNonFatalEitherT[Future](fa)(SomeError.someThrowable).value,
        waitFor
      )

      actual ==== expected
    }
  }

  object IdSpec {

    import effectie.cats.Fx._

    def testCatching_Id_catchNonFatalShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = catchNonFatal(fa)(SomeError.someThrowable)

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCatching_Id_catchNonFatalShouldNotCatchFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      lazy val fa        = run[Id, Int](throwThrowable[Int](fatalExpcetion))

      try {
        val actual = catchNonFatal(fa)(SomeError.someThrowable)
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCatching_Id_catchNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = run[Id, Int](1)
      val expected = 1.asRight[SomeError]
      val actual   = catchNonFatal(fa)(SomeError.someThrowable)

      actual ==== expected
    }

    def testCatching_Id_catchNonFatalFShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = catchNonFatalF[Id](throwThrowable[Int](expectedExpcetion))(SomeError.someThrowable)

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCatching_Id_catchNonFatalFShouldNotCatchFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")

      try {
        val actual = catchNonFatalF[Id](throwThrowable[Int](fatalExpcetion))(SomeError.someThrowable)
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCatching_Id_catchNonFatalFShouldReturnSuccessfulResult: Result = {

      val expected = 1.asRight[SomeError]
      val actual   = catchNonFatalF[Id](1)(SomeError.someThrowable)

      actual ==== expected
    }

    def testCatching_Id_catchNonFatalEitherShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa           = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = catchNonFatalEither(fa)(SomeError.someThrowable)

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCatching_Id_catchNonFatalEitherShouldNotCatchFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      lazy val fa        = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual = catchNonFatalEither(fa)(SomeError.someThrowable)
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCatching_Id_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = catchNonFatalEither(fa)(SomeError.someThrowable)

      actual ==== expected
    }

    def testCatching_Id_catchNonFatalEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = catchNonFatalEither(fa)(SomeError.someThrowable)

      actual ==== expected
    }

    def testCatching_Id_catchNonFatalEitherFShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            =
        catchNonFatalEitherF[Id](throwThrowable[Either[SomeError, Int]](expectedExpcetion))(SomeError.someThrowable)

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCatching_Id_catchNonFatalEitherFShouldNotCatchFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")

      try {
        val actual =
          catchNonFatalEitherF[Id](throwThrowable[Either[SomeError, Int]](fatalExpcetion))(SomeError.someThrowable)
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCatching_Id_catchNonFatalEitherFShouldReturnSuccessfulResult: Result = {

      val expected = 1.asRight[SomeError]
      val actual   = catchNonFatalEitherF[Id](1.asRight[SomeError])(SomeError.someThrowable)

      actual ==== expected
    }

    def testCatching_Id_catchNonFatalEitherFShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val expected        = expectedFailure.asLeft[Int]
      val actual          = catchNonFatalEitherF[Id](expectedFailure.asLeft[Int])(SomeError.someThrowable)

      actual ==== expected
    }

    def testCatching_Id_catchNonFatalEitherTShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa  = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual   = catchNonFatalEitherT[Id](fa)(SomeError.someThrowable).value

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCatching_Id_catchNonFatalEitherTShouldNotCatchFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

      try {
        val actual = catchNonFatalEitherT[Id](fa)(SomeError.someThrowable).value
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCatching_Id_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = {

      val fa       = EitherT(run[Id, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = catchNonFatalEitherT[Id](fa)(SomeError.someThrowable).value

      actual ==== expected
    }

    def testCatching_Id_catchNonFatalEitherTShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          = catchNonFatalEitherT[Id](fa)(SomeError.someThrowable).value

      actual ==== expected
    }

  }

}
