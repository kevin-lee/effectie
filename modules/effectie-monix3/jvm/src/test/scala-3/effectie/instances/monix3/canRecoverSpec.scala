package effectie.instances.monix3

import cats.*
import cats.data.EitherT

import cats.instances.all.*
import cats.syntax.all.*
import effectie.core.CanRecover
import effectie.instances.monix3.fx.given
import effectie.syntax.error.*
import effectie.syntax.fx.*
import effectie.testing.types.SomeError
import effectie.core.FxCtor
import effectie.SomeControlThrowable
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog.*
import hedgehog.runner.*
import monix.eval.Task

import scala.util.control.{ControlThrowable, NonFatal}

/** @author Kevin Lee
  * @since 2020-08-17
  */
object CanRecoverSpec extends Properties {

  import monix.execution.Scheduler.Implicits.global

  override def tests: List[Test] = ioSpecs ++ futureSpecs ++ idSpecs

  /* IO */
  val ioSpecs = List(
    example(
      "test CanRecover[Task].recoverFromNonFatalWith should catch NonFatal",
      IOSpec.testCanRecover_Task_recoverFromNonFatalWithShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Task].recoverFromNonFatalWith should not catch Fatal",
      IOSpec.testCanRecover_Task_recoverFromNonFatalWithShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Task].recoverFromNonFatalWith should return the successful result",
      IOSpec.testCanRecover_Task_recoverFromNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Task].recoverFromNonFatalWithEither should catch NonFatal",
      IOSpec.testCanRecover_Task_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Task].recoverFromNonFatalWithEither should not catch Fatal",
      IOSpec.testCanRecover_Task_recoverFromNonFatalWithEitherShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Task].recoverFromNonFatalWithEither should return the successful result",
      IOSpec.testCanRecover_Task_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Task].recoverFromNonFatalWithEither should return the failed result",
      IOSpec.testCanRecover_Task_recoverFromNonFatalWithEitherShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[Task].recoverEitherFromNonFatalWith should catch NonFatal",
      IOSpec.testCanRecover_Task_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Task].recoverEitherFromNonFatalWith should not catch Fatal",
      IOSpec.testCanRecover_Task_recoverEitherFromNonFatalWithShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Task].recoverEitherFromNonFatalWith should return the successful result",
      IOSpec.testCanRecover_Task_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Task].recoverEitherFromNonFatalWith should return the failed result",
      IOSpec.testCanRecover_Task_recoverEitherFromNonFatalWithShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[Task].recoverEitherTFromNonFatalWith should catch NonFatal",
      IOSpec.testCanRecover_Task_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Task].recoverEitherTFromNonFatalWith should not catch Fatal",
      IOSpec.testCanRecover_Task_recoverEitherTFromNonFatalWithShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Task].recoverEitherTFromNonFatalWith should return the successful result",
      IOSpec.testCanRecover_Task_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Task].recoverEitherTFromNonFatalWith should return the failed result",
      IOSpec.testCanRecover_Task_recoverEitherTFromNonFatalWithShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[Task].recoverFromNonFatal should catch NonFatal",
      IOSpec.testCanRecover_Task_recoverFromNonFatalShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Task].recoverFromNonFatal should not catch Fatal",
      IOSpec.testCanRecover_Task_recoverFromNonFatalShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Task].recoverFromNonFatal should return the successful result",
      IOSpec.testCanRecover_Task_recoverFromNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Task].recoverFromNonFatalEither should catch NonFatal",
      IOSpec.testCanRecover_Task_recoverFromNonFatalEitherShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Task].recoverFromNonFatalEither should not catch Fatal",
      IOSpec.testCanRecover_Task_recoverFromNonFatalEitherShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Task].recoverFromNonFatalEither should return the successful result",
      IOSpec.testCanRecover_Task_recoverFromNonFatalEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Task].recoverFromNonFatalEither should return the failed result",
      IOSpec.testCanRecover_Task_recoverFromNonFatalEitherShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[Task].recoverEitherFromNonFatal should catch NonFatal",
      IOSpec.testCanRecover_Task_recoverEitherFromNonFatalShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Task].recoverEitherFromNonFatal should not catch Fatal",
      IOSpec.testCanRecover_Task_recoverEitherFromNonFatalShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Task].recoverEitherFromNonFatal should return the successful result",
      IOSpec.testCanRecover_Task_recoverEitherFromNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Task].recoverEitherFromNonFatal should return the failed result",
      IOSpec.testCanRecover_Task_recoverEitherFromNonFatalShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[Task].recoverEitherTFromNonFatal should catch NonFatal",
      IOSpec.testCanRecover_Task_recoverEitherTFromNonFatalShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Task].recoverEitherTFromNonFatal should not catch Fatal",
      IOSpec.testCanRecover_Task_recoverEitherTFromNonFatalShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Task].recoverEitherTFromNonFatal should return the successful result",
      IOSpec.testCanRecover_Task_recoverEitherTFromNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Task].recoverEitherTFromNonFatal should return the failed result",
      IOSpec.testCanRecover_Task_recoverEitherTFromNonFatalShouldReturnFailedResult,
    ),
  )

  /* Future */
  val futureSpecs = effectie.instances.future.canRecoverSpec.futureSpecs ++ List(
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

  /* Id */
  val idSpecs = List(
    example(
      "test CanRecover[Id].recoverFromNonFatalWith should catch NonFatal",
      IdSpec.testCanRecover_Id_recoverFromNonFatalWithShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatalWith should not catch Fatal",
      IdSpec.testCanRecover_Id_recoverFromNonFatalWithShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatalWith should return the successful result",
      IdSpec.testCanRecover_Id_recoverFromNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatalWithEither should catch NonFatal",
      IdSpec.testCanRecover_Id_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatalWithEither should not catch Fatal",
      IdSpec.testCanRecover_Id_recoverFromNonFatalWithEitherShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatalWithEither should return the successful result",
      IdSpec.testCanRecover_Id_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatalWithEither should return the failed result",
      IdSpec.testCanRecover_Id_recoverFromNonFatalWithEitherShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[Id].recoverEitherFromNonFatalWith should catch NonFatal",
      IdSpec.testCanRecover_Id_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Id].recoverEitherFromNonFatalWith should not catch Fatal",
      IdSpec.testCanRecover_Id_recoverEitherFromNonFatalWithShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Id].recoverEitherFromNonFatalWith should return the successful result",
      IdSpec.testCanRecover_Id_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Id].recoverEitherFromNonFatalWith should return the failed result",
      IdSpec.testCanRecover_Id_recoverEitherFromNonFatalWithShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[Id].recoverEitherTFromNonFatalWith should catch NonFatal",
      IdSpec.testCanRecover_Id_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Id].recoverEitherTFromNonFatalWith should not catch Fatal",
      IdSpec.testCanRecover_Id_recoverEitherTFromNonFatalWithShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Id].recoverEitherTFromNonFatalWith should return the successful result",
      IdSpec.testCanRecover_Id_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Id].recoverEitherTFromNonFatalWith should return the failed result",
      IdSpec.testCanRecover_Id_recoverEitherTFromNonFatalWithShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatal should catch NonFatal",
      IdSpec.testCanRecover_Id_recoverFromNonFatalShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatal should not catch Fatal",
      IdSpec.testCanRecover_Id_recoverFromNonFatalShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatal should return the successful result",
      IdSpec.testCanRecover_Id_recoverFromNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatalEither should catch NonFatal",
      IdSpec.testCanRecover_Id_recoverFromNonFatalEitherShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatalEither should not catch Fatal",
      IdSpec.testCanRecover_Id_recoverFromNonFatalEitherShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatalEither should return the successful result",
      IdSpec.testCanRecover_Id_recoverFromNonFatalEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatalEither should return the failed result",
      IdSpec.testCanRecover_Id_recoverFromNonFatalEitherShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[Id].recoverEitherFromNonFatal should catch NonFatal",
      IdSpec.testCanRecover_Id_recoverEitherFromNonFatalShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Id].recoverEitherFromNonFatal should not catch Fatal",
      IdSpec.testCanRecover_Id_recoverEitherFromNonFatalShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Id].recoverEitherFromNonFatal should return the successful result",
      IdSpec.testCanRecover_Id_recoverEitherFromNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Id].recoverEitherFromNonFatal should return the failed result",
      IdSpec.testCanRecover_Id_recoverEitherFromNonFatalShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[Id].recoverEitherTFromNonFatal should catch NonFatal",
      IdSpec.testCanRecover_Id_recoverEitherTFromNonFatalShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Id].recoverEitherTFromNonFatal should not catch Fatal",
      IdSpec.testCanRecover_Id_recoverEitherTFromNonFatalShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Id].recoverEitherTFromNonFatal should return the successful result",
      IdSpec.testCanRecover_Id_recoverEitherTFromNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Id].recoverEitherTFromNonFatal should return the failed result",
      IdSpec.testCanRecover_Id_recoverEitherTFromNonFatalShouldReturnFailedResult,
    ),
  )

  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor: Functor, A](a: => A): F[A] =
    effectOf[F](a)

  object IOSpec {

    def testCanRecover_Task_recoverFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong")
      val fa                = run[Task, Int](throwThrowable[Int](expectedException))
      val expected          = 123
      val actual            = CanRecover[Task]
        .recoverFromNonFatalWith(fa) {
          case NonFatal(`expectedException`) =>
            Task.pure(expected)
        }
        .runSyncUnsafe()

      actual ==== expected
    }

    def testCanRecover_Task_recoverFromNonFatalWithShouldNotCatchFatal: Result = {

      val expectedException = SomeControlThrowable("Something's wrong")
      val fa                = run[Task, Int](throwThrowable[Int](expectedException))

      val io = CanRecover[Task].recoverFromNonFatalWith(fa) { case NonFatal(`expectedException`) => Task.pure(123) }
      try {
        val actual = io.runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedException

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Task_recoverFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Int](1)
      val expected = 1
      val actual   = CanRecover[Task]
        .recoverFromNonFatalWith(fa) {
          case NonFatal(_) => Task.pure(999)
        }
        .runSyncUnsafe()

      actual ==== expected
    }

    def testCanRecover_Task_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
      val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
      val actualFailedResult   = CanRecover[Task]
        .recoverFromNonFatalWith(fa) {
          case NonFatal(`expectedException`) => Task.pure(expectedFailedResult)
        }
        .runSyncUnsafe()

      val expectedSuccessResult = 1.asRight[SomeError]
      val actualSuccessResult   = CanRecover[Task]
        .recoverFromNonFatalWith(fa) {
          case NonFatal(`expectedException`) => Task.pure(1.asRight[SomeError])
        }
        .runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    def testCanRecover_Task_recoverFromNonFatalWithEitherShouldNotCatchFatal: Result = {

      val expectedException = SomeControlThrowable("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))

      val io = CanRecover[Task].recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedException`) => Task.pure(123.asRight[SomeError])
      }
      try {
        val actual = io.runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedException

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Task_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanRecover[Task]
        .recoverFromNonFatalWith(fa) {
          case NonFatal(_) => Task(999.asRight[SomeError])
        }
        .runSyncUnsafe()

      actual ==== expected
    }

    def testCanRecover_Task_recoverFromNonFatalWithEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanRecover[Task]
        .recoverFromNonFatalWith(fa) {
          case NonFatal(_) => Task.pure(123.asRight[SomeError])
        }
        .runSyncUnsafe()

      actual ==== expected
    }

    def testCanRecover_Task_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
      val expectedFailedResult  = SomeError.someThrowable(expectedException).asLeft[Int]
      val actualFailedResult    = CanRecover[Task]
        .recoverEitherFromNonFatalWith(fa) {
          case err => Task.pure(SomeError.someThrowable(err).asLeft[Int])
        }
        .runSyncUnsafe()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   = CanRecover[Task]
        .recoverEitherFromNonFatalWith(fa) {
          case NonFatal(`expectedException`) => Task.pure(123.asRight[SomeError])
        }
        .runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    def testCanRecover_Task_recoverEitherFromNonFatalWithShouldNotCatchFatal: Result = {

      val expectedException = SomeControlThrowable("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))

      val io = CanRecover[Task].recoverEitherFromNonFatalWith(fa) {
        case err => Task.pure(SomeError.someThrowable(err).asLeft[Int])
      }
      try {
        val actual = io.runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedException

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Task_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanRecover[Task]
        .recoverEitherFromNonFatalWith(fa) {
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
        CanRecover[Task]
          .recoverEitherFromNonFatalWith(fa) {
            case NonFatal(_) => Task.pure(123.asRight[SomeError])
          }
          .runSyncUnsafe()

      actual ==== expected
    }

    def testCanRecover_Task_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
      val expectedFailedResult  = SomeError.someThrowable(expectedException).asLeft[Int]
      val actualFailedResult    = CanRecover[Task]
        .recoverEitherTFromNonFatalWith(fa) {
          case err => Task.pure(SomeError.someThrowable(err).asLeft[Int])
        }
        .value
        .runSyncUnsafe()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   = CanRecover[Task]
        .recoverEitherTFromNonFatalWith(fa) {
          case NonFatal(`expectedException`) => Task.pure(123.asRight[SomeError])
        }
        .value
        .runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    def testCanRecover_Task_recoverEitherTFromNonFatalWithShouldNotCatchFatal: Result = {

      val expectedException = SomeControlThrowable("Something's wrong")
      val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))

      val io = CanRecover[Task].recoverEitherTFromNonFatalWith(fa) {
        case err => Task.pure(SomeError.someThrowable(err).asLeft[Int])
      }
      try {
        val actual = io.value.runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedException

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Task_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = CanRecover[Task]
        .recoverEitherTFromNonFatalWith(fa) {
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
        CanRecover[Task]
          .recoverEitherTFromNonFatalWith(fa) {
            case NonFatal(_) => Task.pure(123.asRight[SomeError])
          }
          .value
          .runSyncUnsafe()

      actual ==== expected
    }

    // /

    def testCanRecover_Task_recoverFromNonFatalShouldRecoverFromNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong")
      val fa                = run[Task, Int](throwThrowable[Int](expectedException))
      val expected          = 123
      val actual            = CanRecover[Task]
        .recoverFromNonFatal(fa) {
          case NonFatal(`expectedException`) =>
            expected
        }
        .runSyncUnsafe()

      actual ==== expected
    }

    def testCanRecover_Task_recoverFromNonFatalShouldNotCatchFatal: Result = {

      val expectedException = SomeControlThrowable("Something's wrong")
      val fa                = run[Task, Int](throwThrowable[Int](expectedException))

      val io = CanRecover[Task].recoverFromNonFatal(fa) { case NonFatal(`expectedException`) => 123 }
      try {
        val actual = io.runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedException

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Task_recoverFromNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Int](1)
      val expected = 1
      val actual   = CanRecover[Task].recoverFromNonFatal(fa) { case NonFatal(_) => 999 }.runSyncUnsafe()

      actual ==== expected
    }

    def testCanRecover_Task_recoverFromNonFatalEitherShouldRecoverFromNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
      val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
      val actualFailedResult   = CanRecover[Task]
        .recoverFromNonFatal(fa) { case NonFatal(`expectedException`) => expectedFailedResult }
        .runSyncUnsafe()

      val expectedSuccessResult = 1.asRight[SomeError]
      val actualSuccessResult   = CanRecover[Task]
        .recoverFromNonFatal(fa) { case NonFatal(`expectedException`) => 1.asRight[SomeError] }
        .runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    def testCanRecover_Task_recoverFromNonFatalEitherShouldNotCatchFatal: Result = {

      val expectedException = SomeControlThrowable("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))

      val io = CanRecover[Task].recoverFromNonFatal(fa) { case NonFatal(`expectedException`) => 123.asRight[SomeError] }
      try {
        val actual = io.runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedException

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Task_recoverFromNonFatalEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   =
        CanRecover[Task].recoverFromNonFatal(fa) { case NonFatal(_) => 999.asRight[SomeError] }.runSyncUnsafe()

      actual ==== expected
    }

    def testCanRecover_Task_recoverFromNonFatalEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        CanRecover[Task].recoverFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }.runSyncUnsafe()

      actual ==== expected
    }

    def testCanRecover_Task_recoverEitherFromNonFatalShouldRecoverFromNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
      val expectedFailedResult  = SomeError.someThrowable(expectedException).asLeft[Int]
      val actualFailedResult    =
        CanRecover[Task]
          .recoverEitherFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }
          .runSyncUnsafe()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        CanRecover[Task]
          .recoverEitherFromNonFatal(fa) { case NonFatal(`expectedException`) => 123.asRight[SomeError] }
          .runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    def testCanRecover_Task_recoverEitherFromNonFatalShouldNotCatchFatal: Result = {

      val expectedException = SomeControlThrowable("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))

      val io =
        CanRecover[Task].recoverEitherFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
      try {
        val actual = io.runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedException

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Task_recoverEitherFromNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   =
        CanRecover[Task]
          .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
          .runSyncUnsafe()

      actual ==== expected
    }

    def testCanRecover_Task_recoverEitherFromNonFatalShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        CanRecover[Task]
          .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
          .runSyncUnsafe()

      actual ==== expected
    }

    def testCanRecover_Task_recoverEitherTFromNonFatalShouldRecoverFromNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
      val expectedFailedResult  = SomeError.someThrowable(expectedException).asLeft[Int]
      val actualFailedResult    =
        CanRecover[Task]
          .recoverEitherTFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }
          .value
          .runSyncUnsafe()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        CanRecover[Task]
          .recoverEitherTFromNonFatal(fa) { case NonFatal(`expectedException`) => 123.asRight[SomeError] }
          .value
          .runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    def testCanRecover_Task_recoverEitherTFromNonFatalShouldNotCatchFatal: Result = {

      val expectedException = SomeControlThrowable("Something's wrong")
      val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))

      val io =
        CanRecover[Task].recoverEitherTFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
      try {
        val actual = io.value.runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedException

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Task_recoverEitherTFromNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   =
        CanRecover[Task]
          .recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
          .value
          .runSyncUnsafe()

      actual ==== expected
    }

    def testCanRecover_Task_recoverEitherTFromNonFatalShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        CanRecover[Task]
          .recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
          .value
          .runSyncUnsafe()

      actual ==== expected
    }

  }

  object FutureSpec {
    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration.*
    import scala.concurrent.{ExecutionContext, Future}
    import scala.util.control.NonFatal
    import effectie.instances.future.fxCtor.*
    import effectie.instances.future.canRecover.*

    private implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

    private val waitFor = WaitFor(1.second)

    def testCanRecover_Future_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedException = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
      val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
      val actualFailedResult   = ConcurrentSupport.futureToValue(
        CanRecover[Future]
          .recoverEitherTFromNonFatalWith(fa) {
            case err => Future(SomeError.someThrowable(err).asLeft[Int])
          }
          .value,
        waitFor,
      )

      val fa2 = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(
          CanRecover[Future]
            .recoverEitherTFromNonFatalWith(fa2) {
              case err => Future(expected)
            }
            .value
        )

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(
        CanRecover[Future]
          .recoverEitherTFromNonFatalWith(fa) {
            case err => Future(SomeError.someThrowable(err).asLeft[Int])
          }
          .value
      )

      actual ==== expected
    }

    def testCanRecover_Future_recoverEitherTFromNonFatalWithShouldReturnFailedResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(
          CanRecover[Future]
            .recoverEitherTFromNonFatalWith(fa) {
              case NonFatal(_) => Future(expected)
            }
            .value
        )

      actual ==== expected
    }

    // /

    def testCanRecover_Future_recoverEitherTFromNonFatalShouldRecoverFromNonFatal: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedException = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
      val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
      val actualFailedResult   = ConcurrentSupport.futureToValue(
        CanRecover[Future]
          .recoverEitherTFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }
          .value,
        waitFor,
      )

      val fa2 = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(CanRecover[Future].recoverEitherTFromNonFatal(fa2) { case err => expected }.value)

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverEitherTFromNonFatalShouldReturnSuccessfulResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(
        CanRecover[Future]
          .recoverEitherTFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }
          .value
      )

      actual ==== expected
    }

    def testCanRecover_Future_recoverEitherTFromNonFatalShouldReturnFailedResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        )(CanRecover[Future].recoverEitherTFromNonFatal(fa) { case NonFatal(_) => expected }.value)

      actual ==== expected
    }

  }

  object IdSpec {
    import effectie.instances.id.fxCtor.*
    import effectie.instances.id.canRecover.*

    def testCanRecover_Id_recoverFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong")
      lazy val fa           = run[Id, Int](throwThrowable[Int](expectedException))
      val expected          = 1
      val actual: Id[Int]   = CanRecover[Id].recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedException`) => expected
      }

      actual ==== expected
    }

    def testCanRecover_Id_recoverFromNonFatalWithShouldNotCatchFatal: Result = {

      val expectedException = SomeControlThrowable("Something's wrong")
      lazy val fa           = run[Id, Int](throwThrowable[Int](expectedException))

      try {
        val actual: Id[Int] = CanRecover[Id].recoverFromNonFatalWith(fa) { case NonFatal(`expectedException`) => 1 }
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedException

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Id_recoverFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa              = run[Id, Int](1)
      val expected        = 1
      val actual: Id[Int] = CanRecover[Id].recoverFromNonFatalWith(fa) { case NonFatal(_) => 123 }

      actual ==== expected
    }

    def testCanRecover_Id_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong")
      lazy val fa           = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
      val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
      val actualFailedResult   = CanRecover[Id].recoverFromNonFatalWith(fa) {
        case err => SomeError.someThrowable(err).asLeft[Int]
      }

      val expected = 1.asRight[SomeError]
      val actual   = CanRecover[Id].recoverFromNonFatalWith(fa) { case NonFatal(`expectedException`) => expected }

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanRecover_Id_recoverFromNonFatalWithEitherShouldNotCatchFatal: Result = {

      val expectedException = SomeControlThrowable("Something's wrong")
      lazy val fa           = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))

      try {
        val actual = CanRecover[Id].recoverFromNonFatalWith(fa) {
          case NonFatal(`expectedException`) => 1.asRight[SomeError]
        }
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedException

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Id_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   =
        CanRecover[Id].recoverFromNonFatalWith(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }

      actual ==== expected
    }

    def testCanRecover_Id_recoverFromNonFatalWithEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanRecover[Id].recoverFromNonFatalWith(fa) { case NonFatal(_) => 1.asRight[SomeError] }

      actual ==== expected
    }

    def testCanRecover_Id_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong")
      lazy val fa           = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
      val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
      val actualFailedResult   =
        CanRecover[Id]
          .recoverEitherFromNonFatalWith(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }

      val expected = 1.asRight[SomeError]
      val actual   =
        CanRecover[Id].recoverEitherFromNonFatalWith(fa) { case NonFatal(`expectedException`) => expected }

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanRecover_Id_recoverEitherFromNonFatalWithShouldNotCatchFatal: Result = {

      val expectedException = SomeControlThrowable("Something's wrong")
      lazy val fa           = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))

      try {
        val actual = CanRecover[Id]
          .recoverEitherFromNonFatalWith(fa) { case NonFatal(`expectedException`) => 1.asRight[SomeError] }

        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedException

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Id_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanRecover[Id]
        .recoverEitherFromNonFatalWith(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }

      actual ==== expected
    }

    def testCanRecover_Id_recoverEitherFromNonFatalWithShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        CanRecover[Id].recoverEitherFromNonFatalWith(fa) { case NonFatal(_) => 1.asRight[SomeError] }

      actual ==== expected
    }

    def testCanRecover_Id_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong")
      lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
      val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
      val actualFailedResult   =
        CanRecover[Id]
          .recoverEitherTFromNonFatalWith(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }
          .value

      val expected = 1.asRight[SomeError]
      val actual   =
        CanRecover[Id].recoverEitherTFromNonFatalWith(fa) { case NonFatal(`expectedException`) => expected }.value

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanRecover_Id_recoverEitherTFromNonFatalWithShouldNotCatchFatal: Result = {

      val expectedException = SomeControlThrowable("Something's wrong")
      lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))

      try {
        val actual = CanRecover[Id]
          .recoverEitherTFromNonFatalWith(fa) { case NonFatal(`expectedException`) => 1.asRight[SomeError] }
          .value
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedException

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Id_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = EitherT(run[Id, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = CanRecover[Id]
        .recoverEitherTFromNonFatalWith(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
        .value

      actual ==== expected
    }

    def testCanRecover_Id_recoverEitherTFromNonFatalWithShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        CanRecover[Id].recoverEitherTFromNonFatalWith(fa) { case NonFatal(_) => 1.asRight[SomeError] }.value

      actual ==== expected
    }

    // /

    def testCanRecover_Id_recoverFromNonFatalShouldRecoverFromNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong")
      lazy val fa           = run[Id, Int](throwThrowable[Int](expectedException))
      val expected          = 1
      val actual: Id[Int]   = CanRecover[Id].recoverFromNonFatal(fa) { case NonFatal(`expectedException`) => expected }

      actual ==== expected
    }

    def testCanRecover_Id_recoverFromNonFatalShouldNotCatchFatal: Result = {

      val expectedException = SomeControlThrowable("Something's wrong")
      lazy val fa           = run[Id, Int](throwThrowable[Int](expectedException))

      try {
        val actual: Id[Int] = CanRecover[Id].recoverFromNonFatal(fa) { case NonFatal(`expectedException`) => 1 }
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedException

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Id_recoverFromNonFatalShouldReturnSuccessfulResult: Result = {

      val fa              = run[Id, Int](1)
      val expected        = 1
      val actual: Id[Int] = CanRecover[Id].recoverFromNonFatal(fa) { case NonFatal(_) => 123 }

      actual ==== expected
    }

    def testCanRecover_Id_recoverFromNonFatalEitherShouldRecoverFromNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong")
      lazy val fa           = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
      val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
      val actualFailedResult   =
        CanRecover[Id].recoverFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }

      val expected = 1.asRight[SomeError]
      val actual   = CanRecover[Id].recoverFromNonFatal(fa) { case NonFatal(`expectedException`) => expected }

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanRecover_Id_recoverFromNonFatalEitherShouldNotCatchFatal: Result = {

      val expectedException = SomeControlThrowable("Something's wrong")
      lazy val fa           = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))

      try {
        val actual = CanRecover[Id].recoverFromNonFatal(fa) {
          case NonFatal(`expectedException`) => 1.asRight[SomeError]
        }
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedException

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Id_recoverFromNonFatalEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   =
        CanRecover[Id].recoverFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }

      actual ==== expected
    }

    def testCanRecover_Id_recoverFromNonFatalEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanRecover[Id].recoverFromNonFatal(fa) { case NonFatal(_) => 1.asRight[SomeError] }

      actual ==== expected
    }

    def testCanRecover_Id_recoverEitherFromNonFatalShouldRecoverFromNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong")
      lazy val fa           = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
      val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
      val actualFailedResult   =
        CanRecover[Id]
          .recoverEitherFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }

      val expected = 1.asRight[SomeError]
      val actual   =
        CanRecover[Id].recoverEitherFromNonFatal(fa) { case NonFatal(`expectedException`) => expected }

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanRecover_Id_recoverEitherFromNonFatalShouldNotCatchFatal: Result = {

      val expectedException = SomeControlThrowable("Something's wrong")
      lazy val fa           = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))

      try {
        val actual = CanRecover[Id]
          .recoverEitherFromNonFatal(fa) { case NonFatal(`expectedException`) => 1.asRight[SomeError] }

        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedException

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Id_recoverEitherFromNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanRecover[Id]
        .recoverEitherFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }

      actual ==== expected
    }

    def testCanRecover_Id_recoverEitherFromNonFatalShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanRecover[Id].recoverEitherFromNonFatal(fa) { case NonFatal(_) => 1.asRight[SomeError] }

      actual ==== expected
    }

    def testCanRecover_Id_recoverEitherTFromNonFatalShouldRecoverFromNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong")
      lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
      val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
      val actualFailedResult   =
        CanRecover[Id]
          .recoverEitherTFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }
          .value

      val expected = 1.asRight[SomeError]
      val actual   =
        CanRecover[Id].recoverEitherTFromNonFatal(fa) { case NonFatal(`expectedException`) => expected }.value

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanRecover_Id_recoverEitherTFromNonFatalShouldNotCatchFatal: Result = {

      val expectedException = SomeControlThrowable("Something's wrong")
      lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))

      try {
        val actual = CanRecover[Id]
          .recoverEitherTFromNonFatal(fa) { case NonFatal(`expectedException`) => 1.asRight[SomeError] }
          .value
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedException

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Id_recoverEitherTFromNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = EitherT(run[Id, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = CanRecover[Id]
        .recoverEitherTFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
        .value

      actual ==== expected
    }

    def testCanRecover_Id_recoverEitherTFromNonFatalShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual = CanRecover[Id].recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 1.asRight[SomeError] }.value

      actual ==== expected
    }

  }

}
