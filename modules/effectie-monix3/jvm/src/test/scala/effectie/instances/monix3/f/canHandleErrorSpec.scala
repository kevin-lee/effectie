package effectie.instances.monix3.f

import effectie.instances.monix3.canHandleError.taskCanHandleError
import cats.data.EitherT

import cats.syntax.all._
import effectie.SomeControlThrowable
import effectie.core._
import effectie.syntax.error._
import effectie.syntax.fx._
import effectie.testing.types.SomeError
import extras.concurrent.testing.types.ErrorLogger
import effectie.instances.monix3.fxCtor.taskFxCtor
import hedgehog._
import hedgehog.runner._

import scala.util.control.{ControlThrowable, NonFatal}

import monix.eval.Task

/** @author Kevin Lee
  * @since 2020-08-17
  */
object canHandleErrorSpec extends Properties {
  implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = taskSpecs

  /* Task */
  private val taskSpecs = List(
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
      val actual            = CanHandleError[Task]
        .handleNonFatalWith(fa) {
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
        val actual = CanHandleError[Task].handleNonFatalWith(fa)(_ => Task.pure(123)).runSyncUnsafe()
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
      val actual   = CanHandleError[Task].handleNonFatalWith(fa)(_ => Task.pure(999)).runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleNonFatalWithEitherShouldHandleNonFatalWith: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
      val actualFailedResult   =
        CanHandleError[Task].handleNonFatalWith(fa)(_ => Task.pure(expectedFailedResult)).runSyncUnsafe()

      val expectedSuccessResult = 1.asRight[SomeError]
      val actualSuccessResult   =
        CanHandleError[Task].handleNonFatalWith(fa)(_ => Task.pure(1.asRight[SomeError])).runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Task_handleNonFatalWithEitherShouldNotHandleFatalWith: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual = CanHandleError[Task].handleNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError])).runSyncUnsafe()
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
      val actual   = CanHandleError[Task].handleNonFatalWith(fa)(_ => Task(999.asRight[SomeError])).runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleNonFatalWithEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual = CanHandleError[Task].handleNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError])).runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleEitherNonFatalWithShouldHandleNonFatalWith: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult    = CanHandleError[Task]
        .handleEitherNonFatalWith(fa)(err => Task.pure(SomeError.someThrowable(err).asLeft[Int]))
        .runSyncUnsafe()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        CanHandleError[Task].handleEitherNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError])).runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Task_handleEitherNonFatalWithShouldNotHandleFatalWith: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual =
          CanHandleError[Task]
            .handleEitherNonFatalWith(fa)(err => Task.pure(SomeError.someThrowable(err).asLeft[Int]))
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
        CanHandleError[Task].handleEitherNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError])).runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleEitherNonFatalWithShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        CanHandleError[Task].handleEitherNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError])).runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleEitherTNonFatalWithShouldHandleNonFatalWith: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult    = CanHandleError[Task]
        .handleEitherTNonFatalWith(fa)(err => Task.pure(SomeError.someThrowable(err).asLeft[Int]))
        .value
        .runSyncUnsafe()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        CanHandleError[Task].handleEitherTNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError])).value.runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Task_handleEitherTNonFatalWithShouldNotHandleFatalWith: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

      try {
        val actual =
          CanHandleError[Task]
            .handleEitherTNonFatalWith(fa)(err => Task.pure(SomeError.someThrowable(err).asLeft[Int]))
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
        CanHandleError[Task].handleEitherTNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError])).value.runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleEitherTNonFatalWithShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        CanHandleError[Task].handleEitherTNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError])).value.runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleNonFatalShouldHandleNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 123
      val actual            = CanHandleError[Task]
        .handleNonFatal(fa) {
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
        val actual = CanHandleError[Task].handleNonFatal(fa)(_ => 123).runSyncUnsafe()
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
      val actual   = CanHandleError[Task].handleNonFatal(fa)(_ => 999).runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleNonFatalEitherShouldHandleNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
      val actualFailedResult   = CanHandleError[Task].handleNonFatal(fa)(_ => expectedFailedResult).runSyncUnsafe()

      val expectedSuccessResult = 1.asRight[SomeError]
      val actualSuccessResult   = CanHandleError[Task].handleNonFatal(fa)(_ => 1.asRight[SomeError]).runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Task_handleNonFatalEitherShouldNotHandleFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual = CanHandleError[Task].handleNonFatal(fa)(_ => 123.asRight[SomeError]).runSyncUnsafe()
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
      val actual   = CanHandleError[Task].handleNonFatal(fa)(_ => 999.asRight[SomeError]).runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleNonFatalEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanHandleError[Task].handleNonFatal(fa)(_ => 123.asRight[SomeError]).runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleEitherNonFatalShouldHandleNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult    = CanHandleError[Task]
        .handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
        .runSyncUnsafe()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        CanHandleError[Task].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError]).runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Task_handleEitherNonFatalShouldNotHandleFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual =
          CanHandleError[Task]
            .handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
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
      val actual   = CanHandleError[Task].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError]).runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleEitherNonFatalShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        CanHandleError[Task].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError]).runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleEitherTNonFatalShouldHandleNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult    = CanHandleError[Task]
        .handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
        .value
        .runSyncUnsafe()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        CanHandleError[Task].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value.runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Task_handleEitherTNonFatalShouldNotHandleFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

      try {
        val actual =
          CanHandleError[Task]
            .handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
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
      val actual   = CanHandleError[Task].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value.runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleEitherTNonFatalShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        CanHandleError[Task].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value.runSyncUnsafe()

      actual ==== expected
    }

  }

}
