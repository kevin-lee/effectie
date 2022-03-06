package effectie.monix

import cats._
import cats.data.EitherT
import cats.effect.IO
import cats.instances.all._
import cats.syntax.all._
import effectie.SomeControlThrowable
import effectie.core._
import effectie.monix.canCatch._
import effectie.monix.fxCtor._
import effectie.syntax.fx._
import effectie.testing.types.SomeError
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog._
import hedgehog.runner._
import monix.eval.Task

import scala.util.control.ControlThrowable

/** @author Kevin Lee
  * @since 2020-07-31
  */
object canCatchSpec extends Properties {

  implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] =
    taskSpecs ++
      ioSpecs ++
      futureSpecs ++
      idSpecs

  /* Task */
  val taskSpecs = List(
    example(
      "test CanCatch[Task].catchNonFatalThrowable should catch NonFatal",
      TaskSpec.testCanCatch_Task_catchNonFatalThrowableShouldCatchNonFatal
    ),
    example(
      "test CanCatch[Task].catchNonFatalThrowable should not catch Fatal",
      TaskSpec.testCanCatch_Task_catchNonFatalThrowableShouldNotCatchFatal
    ),
    example(
      "test CanCatch[Task].catchNonFatalThrowable should return the successful result",
      TaskSpec.testCanCatch_Task_catchNonFatalThrowableShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[Task].catchNonFatal should catch NonFatal",
      TaskSpec.testCanCatch_Task_catchNonFatalShouldCatchNonFatal
    ),
    example(
      "test CanCatch[Task].catchNonFatal should not catch Fatal",
      TaskSpec.testCanCatch_Task_catchNonFatalShouldNotCatchFatal
    ),
    example(
      "test CanCatch[Task].catchNonFatal should return the successful result",
      TaskSpec.testCanCatch_Task_catchNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[Task].catchNonFatalEither should catch NonFatal",
      TaskSpec.testCanCatch_Task_catchNonFatalEitherShouldCatchNonFatal
    ),
    example(
      "test CanCatch[Task].catchNonFatalEither should not catch Fatal",
      TaskSpec.testCanCatch_Task_catchNonFatalEitherShouldNotCatchFatal
    ),
    example(
      "test CanCatch[Task].catchNonFatalEither should return the successful result",
      TaskSpec.testCanCatch_Task_catchNonFatalEitherShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[Task].catchNonFatalEither should return the failed result",
      TaskSpec.testCanCatch_Task_catchNonFatalEitherShouldReturnFailedResult
    ),
    example(
      "test CanCatch[Task].catchNonFatalEitherT should catch NonFatal",
      TaskSpec.testCanCatch_Task_catchNonFatalEitherTShouldCatchNonFatal
    ),
    example(
      "test CanCatch[Task].catchNonFatalEitherT should not catch Fatal",
      TaskSpec.testCanCatch_Task_catchNonFatalEitherTShouldNotCatchFatal
    ),
    example(
      "test CanCatch[Task].catchNonFatalEitherT should return the successful result",
      TaskSpec.testCanCatch_Task_catchNonFatalEitherTShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[Task].catchNonFatalEitherT should return the failed result",
      TaskSpec.testCanCatch_Task_catchNonFatalEitherTShouldReturnFailedResult
    ),
  )

  /* IO */
  val ioSpecs     = List(
    example(
      "test CanCatch[IO].catchNonFatalThrowable should catch NonFatal",
      IoSpec.testCanCatch_IO_catchNonFatalThrowableShouldCatchNonFatal
    ),
    example(
      "test CanCatch[IO].catchNonFatalThrowable should not catch Fatal",
      IoSpec.testCanCatch_IO_catchNonFatalThrowableShouldNotCatchFatal
    ),
    example(
      "test CanCatch[IO].catchNonFatalThrowable should return the successful result",
      IoSpec.testCanCatch_IO_catchNonFatalThrowableShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[IO].catchNonFatal should catch NonFatal",
      IoSpec.testCanCatch_IO_catchNonFatalShouldCatchNonFatal
    ),
    example(
      "test CanCatch[IO].catchNonFatal should not catch Fatal",
      IoSpec.testCanCatch_IO_catchNonFatalShouldNotCatchFatal
    ),
    example(
      "test CanCatch[IO].catchNonFatal should return the successful result",
      IoSpec.testCanCatch_IO_catchNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[IO].catchNonFatalEither should catch NonFatal",
      IoSpec.testCanCatch_IO_catchNonFatalEitherShouldCatchNonFatal
    ),
    example(
      "test CanCatch[IO].catchNonFatalEither should not catch Fatal",
      IoSpec.testCanCatch_IO_catchNonFatalEitherShouldNotCatchFatal
    ),
    example(
      "test CanCatch[IO].catchNonFatalEither should return the successful result",
      IoSpec.testCanCatch_IO_catchNonFatalEitherShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[IO].catchNonFatalEither should return the failed result",
      IoSpec.testCanCatch_IO_catchNonFatalEitherShouldReturnFailedResult
    ),
    example(
      "test CanCatch[IO].catchNonFatalEitherT should catch NonFatal",
      IoSpec.testCanCatch_IO_catchNonFatalEitherTShouldCatchNonFatal
    ),
    example(
      "test CanCatch[IO].catchNonFatalEitherT should not catch Fatal",
      IoSpec.testCanCatch_IO_catchNonFatalEitherTShouldNotCatchFatal
    ),
    example(
      "test CanCatch[IO].catchNonFatalEitherT should return the successful result",
      IoSpec.testCanCatch_IO_catchNonFatalEitherTShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[IO].catchNonFatalEitherT should return the failed result",
      IoSpec.testCanCatch_IO_catchNonFatalEitherTShouldReturnFailedResult
    ),
  )
  val futureSpecs = effectie.core.CanCatchSpec.futureSpecs ++ List(
    /* Future */
    example(
      "test CanCatch[Future].catchNonFatalEitherT should catch NonFatal",
      FutureSpec.testCanCatch_Future_catchNonFatalEitherTShouldCatchNonFatal
    ),
    example(
      "test CanCatch[Future].catchNonFatalEitherT should return the successful result",
      FutureSpec.testCanCatch_Future_catchNonFatalEitherTShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[Future].catchNonFatalEitherT should return the failed result",
      FutureSpec.testCanCatch_Future_catchNonFatalEitherTShouldReturnFailedResult
    ),
  )

  /* Id */
  val idSpecs = List(
    example(
      "test CanCatch[Id].catchNonFatalThrowable should catch NonFatal",
      IdSpec.testCanCatch_Id_catchNonFatalThrowableShouldCatchNonFatal
    ),
    example(
      "test CanCatch[Id].catchNonFatalThrowable should not catch Fatal",
      IdSpec.testCanCatch_Id_catchNonFatalThrowableShouldNotCatchFatal
    ),
    example(
      "test CanCatch[Id].catchNonFatalThrowable should return the successful result",
      IdSpec.testCanCatch_Id_catchNonFatalThrowableShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[Id].catchNonFatal should catch NonFatal",
      IdSpec.testCanCatch_Id_catchNonFatalShouldCatchNonFatal
    ),
    example(
      "test CanCatch[Id].catchNonFatal should not catch Fatal",
      IdSpec.testCanCatch_Id_catchNonFatalShouldNotCatchFatal
    ),
    example(
      "test CanCatch[Id].catchNonFatal should return the successful result",
      IdSpec.testCanCatch_Id_catchNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[Id].catchNonFatalEither should catch NonFatal",
      IdSpec.testCanCatch_Id_catchNonFatalEitherShouldCatchNonFatal
    ),
    example(
      "test CanCatch[Id].catchNonFatalEither should not catch Fatal",
      IdSpec.testCanCatch_Id_catchNonFatalEitherShouldNotCatchFatal
    ),
    example(
      "test CanCatch[Id].catchNonFatalEither should return the successful result",
      IdSpec.testCanCatch_Id_catchNonFatalEitherShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[Id].catchNonFatalEither should return the failed result",
      IdSpec.testCanCatch_Id_catchNonFatalEitherShouldReturnFailedResult
    ),
    example(
      "test CanCatch[Id].catchNonFatalEitherT should catch NonFatal",
      IdSpec.testCanCatch_Id_catchNonFatalEitherTShouldCatchNonFatal
    ),
    example(
      "test CanCatch[Id].catchNonFatalEitherT should not catch Fatal",
      IdSpec.testCanCatch_Id_catchNonFatalEitherTShouldNotCatchFatal
    ),
    example(
      "test CanCatch[Id].catchNonFatalEitherT should return the successful result",
      IdSpec.testCanCatch_Id_catchNonFatalEitherTShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[Id].catchNonFatalEitherT should return the failed result",
      IdSpec.testCanCatch_Id_catchNonFatalEitherTShouldReturnFailedResult
    )
  )

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor: Functor, A](a: => A): F[A] =
    effectOf[F](a)

  object TaskSpec {
    import monix.execution.Scheduler.Implicits.global

    def testCanCatch_Task_catchNonFatalThrowableShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = expectedExpcetion.asLeft[Int]
      val actual            = CanCatch[Task].catchNonFatalThrowable(fa).runSyncUnsafe()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanCatch_Task_catchNonFatalThrowableShouldNotCatchFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[Task, Int](throwThrowable[Int](fatalExpcetion))

      try {
        val actual = CanCatch[Task].catchNonFatalThrowable(fa).runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_Task_catchNonFatalThrowableShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Int](1)
      val expected = 1.asRight[Throwable]
      val actual   = CanCatch[Task].catchNonFatalThrowable(fa).runSyncUnsafe()

      actual ==== expected
    }

    def testCanCatch_Task_catchNonFatalShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = CanCatch[Task].catchNonFatal(fa)(SomeError.someThrowable).runSyncUnsafe()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanCatch_Task_catchNonFatalShouldNotCatchFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[Task, Int](throwThrowable[Int](fatalExpcetion))

      try {
        val actual = CanCatch[Task].catchNonFatal(fa)(SomeError.someThrowable).runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_Task_catchNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Int](1)
      val expected = 1.asRight[SomeError]
      val actual   = CanCatch[Task].catchNonFatal(fa)(SomeError.someThrowable).runSyncUnsafe()

      actual ==== expected
    }

    def testCanCatch_Task_catchNonFatalEitherShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa       = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual   = CanCatch[Task].catchNonFatalEither(fa)(SomeError.someThrowable).runSyncUnsafe()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanCatch_Task_catchNonFatalEitherShouldNotCatchFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual = CanCatch[Task].catchNonFatalEither(fa)(SomeError.someThrowable).runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_Task_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanCatch[Task].catchNonFatalEither(fa)(SomeError.someThrowable).runSyncUnsafe()

      actual ==== expected
    }

    def testCanCatch_Task_catchNonFatalEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanCatch[Task].catchNonFatalEither(fa)(SomeError.someThrowable).runSyncUnsafe()

      actual ==== expected
    }

    def testCanCatch_Task_catchNonFatalEitherTShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual   = CanCatch[Task].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.runSyncUnsafe()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanCatch_Task_catchNonFatalEitherTShouldNotCatchFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

      try {
        val actual = CanCatch[Task].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_Task_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = {

      val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = CanCatch[Task].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.runSyncUnsafe()

      actual ==== expected
    }

    def testCanCatch_Task_catchNonFatalEitherTShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanCatch[Task].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.runSyncUnsafe()

      actual ==== expected
    }

  }

  object IoSpec {

    def testCanCatch_IO_catchNonFatalThrowableShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = expectedExpcetion.asLeft[Int]
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
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

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

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
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
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

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

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
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
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

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

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa       = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
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
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

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

    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration._
    import scala.concurrent.{ExecutionContext, Future}

    val waitFor = WaitFor(1.second)

//    def testCanCatch_Future_catchNonFatalThrowableShouldCatchNonFatal: Result = {
//
//      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
//      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)
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
//    def testCanCatch_Future_catchNonFatalThrowableShouldReturnSuccessfulResult: Result = {
//
//      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
//      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)
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
//    def testCanCatch_Future_catchNonFatalShouldCatchNonFatal: Result = {
//
//      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
//      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)
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
//    def testCanCatch_Future_catchNonFatalShouldReturnSuccessfulResult: Result = {
//
//      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
//      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)
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
//      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)
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
//      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)
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
//      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)
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
        waitFor
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
        waitFor
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
        waitFor
      )(CanCatch[Future].catchNonFatalEitherT(fa)(SomeError.someThrowable).value)

      actual ==== expected
    }
  }

  object IdSpec {

    def testCanCatch_Id_catchNonFatalThrowableShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = expectedExpcetion.asLeft[Int]
      val actual            = CanCatch[Id].catchNonFatalThrowable(fa)

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
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

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
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

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
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
      lazy val fa  = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual   = CanCatch[Id].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanCatch_Id_catchNonFatalEitherTShouldNotCatchFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

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
