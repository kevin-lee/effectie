package effectie.instances.monix3

import cats.data.EitherT

import cats.syntax.all._
import effectie.SomeControlThrowable
import effectie.core._
import effectie.syntax.error._
import effectie.syntax.fx._
import effectie.testing.FutureTools
import effectie.testing.types.SomeError
import munit.Assertions

import scala.concurrent._
import scala.concurrent.duration._
import scala.util.control.{ControlThrowable, NonFatal}

import monix.eval.Task

/** @author Kevin Lee
  * @since 2020-08-17
  */
class canRecoverSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  import monix.execution.Scheduler.Implicits.global

  override val munitTimeout: FiniteDuration = 200.milliseconds

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A = throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor, A](a: => A): F[A] = effectOf[F](a)

  import fxCtor.taskFxCtor
  import canRecover.taskCanRecover

  test("test CanRecover[Task].recoverFromNonFatalWith should catch NonFatal") {
    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Int](throwThrowable[Int](expectedException))
    val expected          = 123

    CanRecover[Task]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedException`) =>
          Task.pure(expected)
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanRecover[Task].recoverFromNonFatalWith should not catch Fatal") {

    val expectedException = SomeControlThrowable("Something's wrong")
    val fa                = run[Task, Int](throwThrowable[Int](expectedException))

    val io = CanRecover[Task].recoverFromNonFatalWith(fa) { case NonFatal(`expectedException`) => Task.pure(123) }
    try {
      io.map { actual =>
        Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      }.runToFuture
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, expectedException)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test CanRecover[Task].recoverFromNonFatalWith should return the successful result") {

    val fa       = run[Task, Int](1)
    val expected = 1

    CanRecover[Task]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(_) => Task.pure(999)
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanRecover[Task].recoverFromNonFatalWithEither should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
    val actualFailedResult   = CanRecover[Task]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedException`) => Task.pure(expectedFailedResult)
      }
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }
      .runToFuture

    val expectedSuccessResult = 1.asRight[SomeError]
    val actualSuccessResult   = CanRecover[Task]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedException`) => Task.pure(1.asRight[SomeError])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expectedSuccessResult)
      }
      .runToFuture

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("test CanRecover[Task].recoverFromNonFatalWithEither should not catch Fatal") {

    val expectedException = SomeControlThrowable("Something's wrong")
    val fa                = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))

    val io = CanRecover[Task].recoverFromNonFatalWith(fa) {
      case NonFatal(`expectedException`) => Task.pure(123.asRight[SomeError])
    }
    try {
      io.map { actual =>
        Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      }.runToFuture
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, expectedException)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test CanRecover[Task].recoverFromNonFatalWithEither should return the successful result") {

    val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    CanRecover[Task]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(_) => Task(999.asRight[SomeError])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanRecover[Task].recoverFromNonFatalWithEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    CanRecover[Task]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(_) => Task.pure(123.asRight[SomeError])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanRecover[Task].recoverEitherFromNonFatalWith should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult   = CanRecover[Task]
      .recoverEitherFromNonFatalWith(fa) {
        case err => Task.pure(SomeError.someThrowable(err).asLeft[Int])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }
      .runToFuture

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   = CanRecover[Task]
      .recoverEitherFromNonFatalWith(fa) {
        case NonFatal(`expectedException`) => Task.pure(123.asRight[SomeError])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expectedSuccessResult)
      }
      .runToFuture

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("test CanRecover[Task].recoverEitherFromNonFatalWith should not catch Fatal") {

    val expectedException = SomeControlThrowable("Something's wrong")
    val fa                = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))

    val io = CanRecover[Task].recoverEitherFromNonFatalWith(fa) {
      case err => Task.pure(SomeError.someThrowable(err).asLeft[Int])
    }
    try {
      io.map { actual =>
        Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      }.runToFuture
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, expectedException)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  test("test CanRecover[Task].recoverEitherFromNonFatalWith should return the successful result") {

    val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    CanRecover[Task]
      .recoverEitherFromNonFatalWith(fa) {
        case NonFatal(_) => Task.pure(123.asRight[SomeError])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanRecover[Task].recoverEitherFromNonFatalWith should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    CanRecover[Task]
      .recoverEitherFromNonFatalWith(fa) {
        case NonFatal(_) => Task.pure(123.asRight[SomeError])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanRecover[Task].recoverEitherTFromNonFatalWith should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult   = CanRecover[Task]
      .recoverEitherTFromNonFatalWith(fa) {
        case err => Task.pure(SomeError.someThrowable(err).asLeft[Int])
      }
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }
      .runToFuture

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   = CanRecover[Task]
      .recoverEitherTFromNonFatalWith(fa) {
        case NonFatal(`expectedException`) => Task.pure(123.asRight[SomeError])
      }
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expectedSuccessResult)
      }
      .runToFuture

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("test CanRecover[Task].recoverEitherTFromNonFatalWith should not catch Fatal") {

    val expectedException = SomeControlThrowable("Something's wrong")
    val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))

    val io = CanRecover[Task].recoverEitherTFromNonFatalWith(fa) {
      case err => Task.pure(SomeError.someThrowable(err).asLeft[Int])
    }
    try {
      io.value
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .runToFuture
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, expectedException)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test CanRecover[Task].recoverEitherTFromNonFatalWith should return the successful result") {

    val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    CanRecover[Task]
      .recoverEitherTFromNonFatalWith(fa) {
        case NonFatal(_) => Task.pure(123.asRight[SomeError])
      }
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanRecover[Task].recoverEitherTFromNonFatalWith should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    CanRecover[Task]
      .recoverEitherTFromNonFatalWith(fa) {
        case NonFatal(_) => Task.pure(123.asRight[SomeError])
      }
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  // /

  test("test CanRecover[Task].recoverFromNonFatal should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Int](throwThrowable[Int](expectedException))
    val expected          = 123

    CanRecover[Task]
      .recoverFromNonFatal(fa) {
        case NonFatal(`expectedException`) =>
          expected
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanRecover[Task].recoverFromNonFatal should not catch Fatal") {

    val expectedException = SomeControlThrowable("Something's wrong")
    val fa                = run[Task, Int](throwThrowable[Int](expectedException))

    val io = CanRecover[Task].recoverFromNonFatal(fa) { case NonFatal(`expectedException`) => 123 }
    try {
      io.map { actual =>
        Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      }.runToFuture
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, expectedException)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test CanRecover[Task].recoverFromNonFatal should return the successful result") {

    val fa       = run[Task, Int](1)
    val expected = 1

    CanRecover[Task]
      .recoverFromNonFatal(fa) { case NonFatal(_) => 999 }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanRecover[Task].recoverFromNonFatalEither should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
    val actualFailedResult   = CanRecover[Task]
      .recoverFromNonFatal(fa) { case NonFatal(`expectedException`) => expectedFailedResult }
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }
      .runToFuture

    val expectedSuccessResult = 1.asRight[SomeError]
    val actualSuccessResult   = CanRecover[Task]
      .recoverFromNonFatal(fa) { case NonFatal(`expectedException`) => 1.asRight[SomeError] }
      .map { actual =>
        Assertions.assertEquals(actual, expectedSuccessResult)
      }
      .runToFuture

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("test CanRecover[Task].recoverFromNonFatalEither should not catch Fatal") {

    val expectedException = SomeControlThrowable("Something's wrong")
    val fa                = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))

    val io = CanRecover[Task].recoverFromNonFatal(fa) { case NonFatal(`expectedException`) => 123.asRight[SomeError] }
    try {
      io.map { actual =>
        Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      }.runToFuture
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, expectedException)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test CanRecover[Task].recoverFromNonFatalEither should return the successful result") {

    val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    CanRecover[Task]
      .recoverFromNonFatal(fa) { case NonFatal(_) => 999.asRight[SomeError] }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanRecover[Task].recoverFromNonFatalEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    CanRecover[Task]
      .recoverFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanRecover[Task].recoverEitherFromNonFatal should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult  = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult    =
      CanRecover[Task]
        .recoverEitherFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
        .map { actual =>
          Assertions.assertEquals(actual, expectedFailedResult)
        }
        .runToFuture
    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      CanRecover[Task]
        .recoverEitherFromNonFatal(fa) { case NonFatal(`expectedException`) => 123.asRight[SomeError] }
        .map { actual =>
          Assertions.assertEquals(actual, expectedSuccessResult)
        }
        .runToFuture

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("test CanRecover[Task].recoverEitherFromNonFatal should not catch Fatal") {

    val expectedException = SomeControlThrowable("Something's wrong")
    val fa                = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))

    val io =
      CanRecover[Task].recoverEitherFromNonFatal(fa) {
        case err => SomeError.someThrowable(err).asLeft[Int]
      }
    try {
      io.map { actual =>
        Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      }.runToFuture
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, expectedException)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test CanRecover[Task].recoverEitherFromNonFatal should return the successful result") {

    val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    CanRecover[Task]
      .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanRecover[Task].recoverEitherFromNonFatal should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    CanRecover[Task]
      .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanRecover[Task].recoverEitherTFromNonFatal should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult   =
      CanRecover[Task]
        .recoverEitherTFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
        .value
        .map { actual =>
          Assertions.assertEquals(actual, expectedFailedResult)
        }
        .runToFuture

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      CanRecover[Task]
        .recoverEitherTFromNonFatal(fa) { case NonFatal(`expectedException`) => 123.asRight[SomeError] }
        .value
        .map { actual =>
          Assertions.assertEquals(actual, expectedSuccessResult)
        }
        .runToFuture

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("test CanRecover[Task].recoverEitherTFromNonFatal should not catch Fatal") {

    val expectedException = SomeControlThrowable("Something's wrong")
    val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))

    val io =
      CanRecover[Task].recoverEitherTFromNonFatal(fa) {
        case err => SomeError.someThrowable(err).asLeft[Int]
      }
    try {
      io.value
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .runToFuture
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, expectedException)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test CanRecover[Task].recoverEitherTFromNonFatal should return the successful result") {

    val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    CanRecover[Task]
      .recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanRecover[Task].recoverEitherTFromNonFatal should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    CanRecover[Task]
      .recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  import effectie.instances.future.canRecover.futureCanRecover
  import effectie.instances.future.fxCtor.fxCtorFuture

  test("CanRecover[Future].recoverFromNonFatalWith should recover from NonFatal") {
    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedException))
    val expected          = 1

    CanRecover[Future]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedException`) => Future(expected)
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("CanRecover[Future].recoverFromNonFatalWith should return successful result") {
    val fa       = run[Future, Int](1)
    val expected = 1

    CanRecover[Future]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(_) => Future(123)
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("CanRecover[Future].recoverFromNonFatalWith(Future[Either]) should recover from NonFatal") {
    val expectedException = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult   =
      CanRecover[Future]
        .recoverFromNonFatalWith(fa) {
          case err => Future(SomeError.someThrowable(err).asLeft[Int])
        }
        .map { actual =>
          Assertions.assertEquals(actual, expectedFailedResult)
        }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expected = 1.asRight[SomeError]
    val actualSuccessResult =
      CanRecover[Future]
        .recoverFromNonFatalWith(fa2) {
          case NonFatal(`expectedException`) => Future(expected)
        }
        .map { actual =>
          Assertions.assertEquals(actual, expected)
        }

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("CanRecover[Future].recoverFromNonFatalWith(Future[Either]) should return successful result") {
    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    CanRecover[Future]
      .recoverFromNonFatalWith(fa) {
        case err => Future(SomeError.someThrowable(err).asLeft[Int])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("CanRecover[Future].recoverFromNonFatalWith(Future[Either]) should return failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    CanRecover[Future]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(_) => Future(1.asRight[SomeError])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("CanRecover[Future].recoverEitherFromNonFatalWith should recover from NonFatal") {
    val expectedException = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]

    val actualFailedResult =
      CanRecover[Future]
        .recoverEitherFromNonFatalWith(fa) {
          case err => Future(SomeError.someThrowable(err).asLeft[Int])
        }
        .map { actual =>
          Assertions.assertEquals(actual, expectedFailedResult)
        }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expected = 1.asRight[SomeError]
    val actualSuccessResult =
      CanRecover[Future]
        .recoverEitherFromNonFatalWith(fa2) {
          case err @ _ => Future(expected)
        }
        .map { actual =>
          Assertions.assertEquals(actual, expected)
        }

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("CanRecover[Future].recoverEitherFromNonFatalWith should return successful result") {
    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    CanRecover[Future]
      .recoverEitherFromNonFatalWith(fa) {
        case err => Future(SomeError.someThrowable(err).asLeft[Int])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("CanRecover[Future].recoverEitherFromNonFatalWith should return failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    CanRecover[Future]
      .recoverEitherFromNonFatalWith(fa) {
        case NonFatal(_) => Future(expected)
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanRecover[Future].recoverEitherTFromNonFatalWith should catch NonFatal") {
    val expectedException = new RuntimeException("Something's wrong")
    val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult   =
      CanRecover[Future]
        .recoverEitherTFromNonFatalWith(fa) {
          case err => Future(SomeError.someThrowable(err).asLeft[Int])
        }
        .value
        .map { actual =>
          Assertions.assertEquals(actual, expectedFailedResult)
        }

    val fa2 = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
    val expected            = 1.asRight[SomeError]
    val actualSuccessResult =
      CanRecover[Future]
        .recoverEitherTFromNonFatalWith(fa2) {
          case err @ _ => Future(expected)
        }
        .value
        .map { actual =>
          Assertions.assertEquals(actual, expected)
        }

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("test CanRecover[Future].recoverEitherTFromNonFatalWith should return the successful result") {
    val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    CanRecover[Future]
      .recoverEitherTFromNonFatalWith(fa) {
        case err => Future(SomeError.someThrowable(err).asLeft[Int])
      }
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanRecover[Future].recoverEitherTFromNonFatalWith should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    CanRecover[Future]
      .recoverEitherTFromNonFatalWith(fa) {
        case NonFatal(_) => Future(expected)
      }
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  // /

  test("CanRecover[Future].recoverFromNonFatal should recover from NonFatal") {
    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedException))
    val expected          = 1

    CanRecover[Future]
      .recoverFromNonFatal(fa) { case NonFatal(`expectedException`) => expected }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("CanRecover[Future].recoverFromNonFatal should return successful result") {
    val fa       = run[Future, Int](1)
    val expected = 1

    CanRecover[Future]
      .recoverFromNonFatal(fa) { case NonFatal(_) => 123 }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("CanRecover[Future].recoverFromNonFatal(Future[Either]) should recover from NonFatal") {
    val expectedException = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult   =
      CanRecover[Future]
        .recoverFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
        .map { actual =>
          Assertions.assertEquals(actual, expectedFailedResult)
        }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expected = 1.asRight[SomeError]
    val actualSuccessResult =
      CanRecover[Future]
        .recoverFromNonFatal(fa2) { case NonFatal(`expectedException`) => expected }
        .map { actual =>
          Assertions.assertEquals(actual, expected)
        }

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("CanRecover[Future].recoverFromNonFatal(Future[Either]) should return successful result") {
    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    CanRecover[Future]
      .recoverFromNonFatal(fa) {
        case err => SomeError.someThrowable(err).asLeft[Int]
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("CanRecover[Future].recoverFromNonFatal(Future[Either]) should return failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    CanRecover[Future]
      .recoverFromNonFatal(fa) { case NonFatal(_) => 1.asRight[SomeError] }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("CanRecover[Future].recoverEitherFromNonFatal should recover from NonFatal") {
    val expectedException = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult   =
      CanRecover[Future]
        .recoverEitherFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
        .map { actual =>
          Assertions.assertEquals(actual, expectedFailedResult)
        }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expected = 1.asRight[SomeError]
    val actualSuccessResult =
      CanRecover[Future]
        .recoverEitherFromNonFatal(fa2) { case err @ _ => expected }
        .map { actual =>
          Assertions.assertEquals(actual, expected)
        }

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("CanRecover[Future].recoverEitherFromNonFatal should return successful result") {
    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    CanRecover[Future]
      .recoverEitherFromNonFatal(fa) {
        case err => SomeError.someThrowable(err).asLeft[Int]
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("CanRecover[Future].recoverEitherFromNonFatal should return failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    CanRecover[Future]
      .recoverEitherFromNonFatal(fa) { case NonFatal(_) => expected }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanRecover[Future].recoverEitherTFromNonFatal should catch NonFatal") {
    val expectedException = new RuntimeException("Something's wrong")
    val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult   =
      CanRecover[Future]
        .recoverEitherTFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
        .value
        .map { actual =>
          Assertions.assertEquals(actual, expectedFailedResult)
        }

    val fa2 = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
    val expected            = 1.asRight[SomeError]
    val actualSuccessResult =
      CanRecover[Future]
        .recoverEitherTFromNonFatal(fa2) { case err @ _ => expected }
        .value
        .map { actual =>
          Assertions.assertEquals(actual, expected)
        }

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("test CanRecover[Future].recoverEitherTFromNonFatal should return the successful result") {
    val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    CanRecover[Future]
      .recoverEitherTFromNonFatal(fa) {
        case err => SomeError.someThrowable(err).asLeft[Int]
      }
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanRecover[Future].recoverEitherTFromNonFatal should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    CanRecover[Future]
      .recoverEitherTFromNonFatal(fa) { case NonFatal(_) => expected }
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

}
