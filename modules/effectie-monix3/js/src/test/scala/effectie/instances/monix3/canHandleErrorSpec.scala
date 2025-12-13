package effectie.instances.monix3

import canHandleError.taskCanHandleError
import cats.data.EitherT

import cats.syntax.all._
import effectie.SomeControlThrowable
import effectie.core._
import effectie.syntax.error._
import effectie.syntax.fx._
import effectie.testing.FutureTools
import effectie.testing.types.SomeError
import fxCtor.taskFxCtor
import munit.Assertions

import scala.concurrent._
import scala.concurrent.duration._
import scala.util.control.{ControlThrowable, NonFatal}

import monix.eval.Task

/** @author Kevin Lee
  * @since 2020-08-17
  */
class canHandleErrorSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  import monix.execution.Scheduler.Implicits.global

  override val munitTimeout: FiniteDuration = 200.milliseconds

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A = throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor, A](a: => A): F[A] = effectOf[F](a)

  test("test CanHandleError[Task].handleNonFatalWith should handle NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Int](throwThrowable[Int](expectedException))
    val expected          = 123

    CanHandleError[Task]
      .handleNonFatalWith(fa) {
        case NonFatal(`expectedException`) =>
          Task.pure(expected)
        case err =>
          throw err // scalafix:ok DisableSyntax.throw
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanHandleError[Task].handleNonFatalWith should not handle Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[Task, Int](throwThrowable[Int](fatalExpcetion))

    try {
      CanHandleError[Task]
        .handleNonFatalWith(fa)(_ => Task.pure(123))
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .runToFuture
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, fatalExpcetion)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  test("test CanHandleError[Task].handleNonFatalWith should return the successful result") {

    val fa       = run[Task, Int](1)
    val expected = 1

    CanHandleError[Task]
      .handleNonFatalWith(fa)(_ => Task.pure(999))
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanHandleError[Task].handleNonFatalWithEither should handle NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
    val actualFailedResult   =
      CanHandleError[Task]
        .handleNonFatalWith(fa)(_ => Task.pure(expectedFailedResult))
        .map { actual =>
          Assertions.assertEquals(actual, expectedFailedResult)
        }
        .runToFuture

    val expectedSuccessResult = 1.asRight[SomeError]
    val actualSuccessResult   =
      CanHandleError[Task]
        .handleNonFatalWith(fa)(_ => Task.pure(1.asRight[SomeError]))
        .map { actual =>
          Assertions.assertEquals(actual, expectedSuccessResult)
        }
        .runToFuture

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("test CanHandleError[Task].handleNonFatalWithEither should not handle Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

    try {
      CanHandleError[Task]
        .handleNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError]))
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .runToFuture
    } catch {
      case ex: SomeControlThrowable =>
        Assertions.assertEquals(ex.getMessage, fatalExpcetion.getMessage)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test CanHandleError[Task].handleNonFatalWithEither should return the successful result") {

    val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    CanHandleError[Task]
      .handleNonFatalWith(fa)(_ => Task(999.asRight[SomeError]))
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanHandleError[Task].handleNonFatalWithEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    CanHandleError[Task]
      .handleNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError]))
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanHandleError[Task].handleEitherNonFatalWith should handle NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult  = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult    = CanHandleError[Task]
      .handleEitherNonFatalWith(fa)(err => Task.pure(SomeError.someThrowable(err).asLeft[Int]))
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }
      .runToFuture
    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      CanHandleError[Task]
        .handleEitherNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError]))
        .map { actualSuccessResult =>
          Assertions.assertEquals(actualSuccessResult, expectedSuccessResult)
        }
        .runToFuture

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("test CanHandleError[Task].handleEitherNonFatalWith should not handle Fatal") {
    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

    try {
      CanHandleError[Task]
        .handleEitherNonFatalWith(fa)(err => Task.pure(SomeError.someThrowable(err).asLeft[Int]))
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .runToFuture
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, fatalExpcetion)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  test("test CanHandleError[Task].handleEitherNonFatalWith should return the successful result") {

    val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    CanHandleError[Task]
      .handleEitherNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError]))
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanHandleError[Task].handleEitherNonFatalWith should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    CanHandleError[Task]
      .handleEitherNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError]))
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanHandleError[Task].handleEitherTNonFatalWith should handle NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
    val expectedFailedResult  = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult    = CanHandleError[Task]
      .handleEitherTNonFatalWith(fa)(err => Task.pure(SomeError.someThrowable(err).asLeft[Int]))
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }
      .runToFuture
    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      CanHandleError[Task]
        .handleEitherTNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError]))
        .value
        .map { actualSuccessResult =>
          Assertions.assertEquals(actualSuccessResult, expectedSuccessResult)
        }
        .runToFuture

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("test CanHandleError[Task].handleEitherTNonFatalWith should not handle Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

    try {
      CanHandleError[Task]
        .handleEitherTNonFatalWith(fa)(err => Task.pure(SomeError.someThrowable(err).asLeft[Int]))
        .value
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .runToFuture
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, fatalExpcetion)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test CanHandleError[Task].handleEitherTNonFatalWith should return the successful result") {

    val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    CanHandleError[Task]
      .handleEitherTNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError]))
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanHandleError[Task].handleEitherTNonFatalWith should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    CanHandleError[Task]
      .handleEitherTNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError]))
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanHandleError[Task].handleNonFatal should handle NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Int](throwThrowable[Int](expectedException))
    val expected          = 123

    CanHandleError[Task]
      .handleNonFatal(fa) {
        case NonFatal(`expectedException`) =>
          expected
        case err =>
          throw err // scalafix:ok DisableSyntax.throw
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanHandleError[Task].handleNonFatal should not handle Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[Task, Int](throwThrowable[Int](fatalExpcetion))

    try {
      CanHandleError[Task]
        .handleNonFatal(fa)(_ => 123)
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .runToFuture
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, fatalExpcetion)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test CanHandleError[Task].handleNonFatal should return the successful result") {

    val fa       = run[Task, Int](1)
    val expected = 1
    CanHandleError[Task]
      .handleNonFatal(fa)(_ => 999)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanHandleError[Task].handleNonFatalEither should handle NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
    val actualFailedResult   = CanHandleError[Task]
      .handleNonFatal(fa)(_ => expectedFailedResult)
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }
      .runToFuture

    val expectedSuccessResult = 1.asRight[SomeError]
    val actualSuccessResult   = CanHandleError[Task]
      .handleNonFatal(fa)(_ => 1.asRight[SomeError])
      .map { actual =>
        Assertions.assertEquals(actual, expectedSuccessResult)
      }
      .runToFuture

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("test CanHandleError[Task].handleNonFatalEither should not handle Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

    try {
      CanHandleError[Task]
        .handleNonFatal(fa)(_ => 123.asRight[SomeError])
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .runToFuture
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, fatalExpcetion)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test CanHandleError[Task].handleNonFatalEither should return the successful result") {

    val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    CanHandleError[Task]
      .handleNonFatal(fa)(_ => 999.asRight[SomeError])
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanHandleError[Task].handleNonFatalEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    CanHandleError[Task]
      .handleNonFatal(fa)(_ => 123.asRight[SomeError])
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanHandleError[Task].handleEitherNonFatal should handle NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult  = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult    = CanHandleError[Task]
      .handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }
      .runToFuture
    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   = CanHandleError[Task]
      .handleEitherNonFatal(fa)(_ => 123.asRight[SomeError])
      .map { actual =>
        Assertions.assertEquals(actual, expectedSuccessResult)
      }
      .runToFuture

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("test CanHandleError[Task].handleEitherNonFatal should not handle Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

    try {
      CanHandleError[Task]
        .handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .runToFuture
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, fatalExpcetion)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test CanHandleError[Task].handleEitherNonFatal should return the successful result") {

    val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    CanHandleError[Task]
      .handleEitherNonFatal(fa)(_ => 123.asRight[SomeError])
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanHandleError[Task].handleEitherNonFatal should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    CanHandleError[Task]
      .handleEitherNonFatal(fa)(_ => 123.asRight[SomeError])
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanHandleError[Task].handleEitherTNonFatal should handle NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult   = CanHandleError[Task]
      .handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }
      .runToFuture

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   = CanHandleError[Task]
      .handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError])
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expectedSuccessResult)
      }
      .runToFuture

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("test CanHandleError[Task].handleEitherTNonFatal should not handle Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

    try {
      CanHandleError[Task]
        .handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
        .value
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .runToFuture
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, fatalExpcetion)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test CanHandleError[Task].handleEitherTNonFatal should return the successful result") {

    val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    CanHandleError[Task]
      .handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError])
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanHandleError[Task].handleEitherTNonFatal should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    CanHandleError[Task]
      .handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError])
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  /* Future */

  import effectie.instances.future.canHandleError.canHandleErrorFuture
  import effectie.instances.future.fxCtor.fxCtorFuture

  test("CanHandleError[Future].handleNonFatalWith should handle NonFatal") {
    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedException))
    val expected          = 1

    CanHandleError[Future].handleNonFatalWith(fa)(_ => Future(expected)).map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("CanHandleError[Future].handleNonFatalWith should return successful result") {
    val fa       = run[Future, Int](1)
    val expected = 1

    CanHandleError[Future].handleNonFatalWith(fa)(_ => Future(123)).map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("CanHandleError[Future].handleNonFatalWith(Future[Either]) should handle NonFatal") {
    val expectedException = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult   =
      CanHandleError[Future].handleNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int])).map {
        actual =>
          Assertions.assertEquals(actual, expectedFailedResult)
      }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expected = 1.asRight[SomeError]
    val actualSuccessResult = CanHandleError[Future].handleNonFatalWith(fa2)(_ => Future(expected)).map { actual =>
      Assertions.assertEquals(actual, expected)
    }

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("CanHandleError[Future].handleNonFatalWith(Future[Either]) should return successful result") {
    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    CanHandleError[Future].handleNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int])).map {
      actual =>
        Assertions.assertEquals(actual, expected)
    }
  }

  test("CanHandleError[Future].handleNonFatalWith(Future[Either]) should return failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    CanHandleError[Future].handleNonFatalWith(fa)(_ => Future(1.asRight[SomeError])).map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("CanHandleError[Future].handleEitherNonFatalWith should handle NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult   =
      CanHandleError[Future]
        .handleEitherNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int]))
        .map { actual =>
          Assertions.assertEquals(actual, expectedFailedResult)
        }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expected = 1.asRight[SomeError]
    val actualSuccessResult =
      CanHandleError[Future].handleEitherNonFatalWith(fa2)(_ => Future(expected)).map { actual =>
        Assertions.assertEquals(actual, expected)
      }

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("CanHandleError[Future].handleEitherNonFatalWith should return successful result") {
    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    CanHandleError[Future]
      .handleEitherNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int]))
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("CanHandleError[Future].handleEitherNonFatalWith should return failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    CanHandleError[Future].handleEitherNonFatalWith(fa)(_ => Future(expected)).map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanHandleError[Future].handleEitherTNonFatalWith should handle NonFatal") {
    val expectedException = new RuntimeException("Something's wrong")
    val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult   =
      CanHandleError[Future]
        .handleEitherTNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int]))
        .value
        .map { actual =>
          Assertions.assertEquals(actual, expectedFailedResult)
        }

    val fa2 = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
    val expected            = 1.asRight[SomeError]
    val actualSuccessResult =
      CanHandleError[Future].handleEitherTNonFatalWith(fa2)(_ => Future(expected)).value.map { actual =>
        Assertions.assertEquals(actual, expected)
      }

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("test CanHandleError[Future].handleEitherTNonFatalWith should return the successful result") {
    val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    CanHandleError[Future]
      .handleEitherTNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int]))
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanHandleError[Future].handleEitherTNonFatalWith should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]
    CanHandleError[Future].handleEitherTNonFatalWith(fa)(_ => Future(expected)).value.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("CanHandleError[Future].handleNonFatal should handle NonFatal") {
    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedException))
    val expected          = 1

    CanHandleError[Future].handleNonFatal(fa)(_ => expected).map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("CanHandleError[Future].handleNonFatal should return successful result") {
    val fa       = run[Future, Int](1)
    val expected = 1

    CanHandleError[Future].handleNonFatal(fa)(_ => 123).map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("CanHandleError[Future].handleNonFatal(Future[Either]) should handle NonFatal") {
    val expectedException = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult   =
      CanHandleError[Future].handleNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]).map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expected = 1.asRight[SomeError]
    val actualSuccessResult =
      CanHandleError[Future].handleNonFatal(fa2)(_ => expected).map { actual =>
        Assertions.assertEquals(actual, expected)
      }

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("CanHandleError[Future].handleNonFatal(Future[Either]) should return successful result") {
    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    CanHandleError[Future].handleNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]).map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("CanHandleError[Future].handleNonFatal(Future[Either]) should return failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    CanHandleError[Future].handleNonFatal(fa)(_ => 1.asRight[SomeError]).map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("CanHandleError[Future].handleEitherNonFatal should handle NonFatal") {
    val expectedException = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult   =
      CanHandleError[Future].handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]).map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expected = 1.asRight[SomeError]
    val actualSuccessResult =
      CanHandleError[Future].handleEitherNonFatal(fa2)(_ => expected).map { actual =>
        Assertions.assertEquals(actual, expected)
      }

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("CanHandleError[Future].handleEitherNonFatal should return successful result") {
    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    CanHandleError[Future].handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]).map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("CanHandleError[Future].handleEitherNonFatal should return failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    CanHandleError[Future].handleEitherNonFatal(fa)(_ => expected).map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanHandleError[Future].handleEitherTNonFatal should handle NonFatal") {
    val expectedException = new RuntimeException("Something's wrong")
    val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult   =
      CanHandleError[Future].handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]).value.map {
        actual =>
          Assertions.assertEquals(actual, expectedFailedResult)
      }

    val fa2 = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
    val expected            = 1.asRight[SomeError]
    val actualSuccessResult =
      CanHandleError[Future].handleEitherTNonFatal(fa2)(_ => expected).value.map { actual =>
        Assertions.assertEquals(actual, expected)
      }

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("test CanHandleError[Future].handleEitherTNonFatal should return the successful result") {
    val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    CanHandleError[Future].handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]).value.map {
      actual =>
        Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanHandleError[Future].handleEitherTNonFatal should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    CanHandleError[Future].handleEitherTNonFatal(fa)(_ => expected).value.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

}
