package effectie.instances.monix3.f

import cats.Monad
import cats.data.EitherT

import cats.syntax.all._
import effectie.SomeControlThrowable
import effectie.core._
import effectie.instances.ce2.f.fx.syncFx
import effectie.specs._
import effectie.specs.fxSpec._
import effectie.syntax.error._
import effectie.testing._
import effectie.testing.cats.LawsF.EqF
import effectie.testing.types.SomeError
import munit.Assertions

import scala.concurrent._
import scala.concurrent.duration._
import scala.util.control.{ControlThrowable, NonFatal}

import monix.eval.Task

/** @author Kevin Lee
  * @since 2020-12-06
  */
class fxSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  import monix.execution.Scheduler.Implicits.global

  override val munitTimeout: FiniteDuration = 200.milliseconds

  private def assertWithAttempt[A](io: Task[A], expected: Either[Throwable, A]): Task[Unit] = {
    io.attempt.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test Fx[Task].effectOf")(
    FxSpecs4Js
      .testEffectOf[Task]((fa, actual, expected, log) => fa.map(_ => Assertions.assertEquals(actual(0), expected, log)))
      .runToFuture
  )
  test("test Fx[Task].fromEffect(effectOf)")(
    FxSpecs4Js
      .testFromEffect[Task]((fa, actual, expected, log) =>
        fa.map(_ => Assertions.assertEquals(actual(0), expected, log))
      )
      .runToFuture
  )
  test("test Fx[Task].fromEffect(pureOf)")(
    FxSpecs4Js
      .testFromEffectWithPure[Task]((fa, actual, expected, log) =>
        fa.map(_ => Assertions.assertEquals(actual(0), expected, log))
      )
      .runToFuture
  )
  test("test Fx[Task].pureOf")(
    FxSpecs4Js
      .testPureOf[Task]((fa, actual, expected, log) => fa.map(_ => Assertions.assertEquals(actual(0), expected, log)))
      .runToFuture
  )
  test("test Fx[Task].pureOrError(success case)")(
    FxSpecs4Js
      .testPureOrErrorSuccessCase[Task]((fa, actual, expected, log) =>
        fa.map(_ => Assertions.assertEquals(actual(0), expected, log))
      )
      .runToFuture
  )
  test("test Fx[Task].pureOrError(error case)")(
    FxSpecs4Js.testPureOrErrorErrorCase[Task]((io, expected) => assertWithAttempt(io, expected.asLeft)).runToFuture
  )
  test("test Fx[Task].unitOf")(
    FxSpecs4Js.testUnitOf[Task](fa => fa.map(actual => Assertions.assertEquals(actual, ()))).runToFuture
  )
  test("test Fx[Task].errorOf")(
    FxSpecs4Js.testErrorOf[Task]((io, expected) => assertWithAttempt(io, expected.asLeft)).runToFuture
  )
  test("test Fx[Task].fromEither(Right)")(FxSpecs4Js.testFromEitherRightCase[Task](assertWithAttempt).runToFuture)
  test("test Fx[Task].fromEither(Left)")(FxSpecs4Js.testFromEitherLeftCase[Task](assertWithAttempt).runToFuture)
  test("test Fx[Task].fromOption(Some)")(FxSpecs4Js.testFromOptionSomeCase[Task](assertWithAttempt).runToFuture)
  test("test Fx[Task].fromOption(None)")(FxSpecs4Js.testFromOptionNoneCase[Task](assertWithAttempt).runToFuture)
  test("test Fx[Task].fromTry(Success)")(FxSpecs4Js.testFromTrySuccessCase[Task](assertWithAttempt).runToFuture)
  test("test Fx[Task].fromTry(Failure)")(FxSpecs4Js.testFromTryFailureCase[Task](assertWithAttempt).runToFuture)

  /* Test MonadLaws */
  MonadSpec4Js.testMonadLaws[Task]("Task").foreach {
    case (name, testF) =>
      test(name) {
        testF().runToFuture
      }
  }

  def throwThrowable[A](throwable: => Throwable): A = throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: Fx, A](a: => A): F[A] = Fx[F].effectOf(a)

  implicit def eqF[F[*]: Monad]: EqF[F, Int] =
    (a, b) => a.flatMap(aVal => b.map(aVal === _))

  test("test Fx[Task].catchNonFatalThrowable should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Int](throwThrowable[Int](expectedException))
    val expected          = expectedException.asLeft[Int]

    Fx[Task]
      .catchNonFatalThrowable(fa)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].catchNonFatalThrowable should not catch Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[Task, Int](throwThrowable[Int](fatalExpcetion))

    try {
      Fx[Task]
        .catchNonFatalThrowable(fa)
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

  test("test Fx[Task].catchNonFatalThrowable should return the successful result") {

    val fa       = run[Task, Int](1)
    val expected = 1.asRight[Throwable]

    Fx[Task]
      .catchNonFatalThrowable(fa)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].catchNonFatal should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Int](throwThrowable[Int](expectedException))
    val expected          = SomeError.someThrowable(expectedException).asLeft[Int]

    Fx[Task]
      .catchNonFatal(fa)(SomeError.someThrowable)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].catchNonFatal should not catch Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[Task, Int](throwThrowable[Int](fatalExpcetion))

    try {
      Fx[Task]
        .catchNonFatal(fa)(SomeError.someThrowable)
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

  test("test Fx[Task].catchNonFatal should return the successful result") {

    val fa       = run[Task, Int](1)
    val expected = 1.asRight[SomeError]

    Fx[Task]
      .catchNonFatal(fa)(SomeError.someThrowable)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].catchNonFatalEither should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expected          = SomeError.someThrowable(expectedException).asLeft[Int]

    Fx[Task]
      .catchNonFatalEither(fa)(SomeError.someThrowable)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].catchNonFatalEither should not catch Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

    try {
      Fx[Task]
        .catchNonFatalEither(fa)(SomeError.someThrowable)
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

  test("test Fx[Task].catchNonFatalEither should return the successful result") {

    val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    Fx[Task]
      .catchNonFatalEither(fa)(SomeError.someThrowable)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].catchNonFatalEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    Fx[Task]
      .catchNonFatalEither(fa)(SomeError.someThrowable)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].catchNonFatalEitherT should catch NonFatal") {

    val expectedException                 = new RuntimeException("Something's wrong")
    val fa: EitherT[Task, SomeError, Int] = EitherT(
      run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    )
    val expected                          = SomeError.someThrowable(expectedException).asLeft[Int]

    Fx[Task]
      .catchNonFatalEitherT(fa)(SomeError.someThrowable)
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].catchNonFatalEitherT should not catch Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

    try {
      Fx[Task]
        .catchNonFatalEitherT(fa)(SomeError.someThrowable)
        .value
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

  test("test Fx[Task].catchNonFatalEitherT should return the successful result") {

    val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    Fx[Task]
      .catchNonFatalEitherT(fa)(SomeError.someThrowable)
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].catchNonFatalEitherT should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    Fx[Task]
      .catchNonFatalEitherT(fa)(SomeError.someThrowable)
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].handleNonFatalWith should handle NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Int](throwThrowable[Int](expectedException))
    val expected          = 123

    Fx[Task]
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

  test("test Fx[Task].handleNonFatalWith should not handle Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[Task, Int](throwThrowable[Int](fatalExpcetion))

    try {
      Fx[Task]
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

  test("test Fx[Task].handleNonFatalWith should return the successful result") {

    val fa       = run[Task, Int](1)
    val expected = 1

    Fx[Task]
      .handleNonFatalWith(fa)(_ => Task.pure(999))
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].handleNonFatalWithEither should handle NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
    val actualFailedResult   =
      Fx[Task]
        .handleNonFatalWith(fa)(_ => Task.pure(expectedFailedResult))
        .map { actual =>
          Assertions.assertEquals(actual, expectedFailedResult)
        }
        .runToFuture

    val expectedSuccessResult = 1.asRight[SomeError]
    val actualSuccessResult   =
      Fx[Task]
        .handleNonFatalWith(fa)(_ => Task.pure(1.asRight[SomeError]))
        .map { actual =>
          Assertions.assertEquals(actual, expectedSuccessResult)
        }
        .runToFuture

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("test Fx[Task].handleNonFatalWithEither should not handle Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

    try {
      Fx[Task]
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

  test("test Fx[Task].handleNonFatalWithEither should return the successful result") {

    val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    Fx[Task]
      .handleNonFatalWith(fa)(_ => Task(999.asRight[SomeError]))
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].handleNonFatalWithEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    Fx[Task]
      .handleNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError]))
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].handleEitherNonFatalWith should handle NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult  = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult    = Fx[Task]
      .handleEitherNonFatalWith(fa)(err => Task.pure(SomeError.someThrowable(err).asLeft[Int]))
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }
      .runToFuture
    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      Fx[Task]
        .handleEitherNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError]))
        .map { actual =>
          Assertions.assertEquals(actual, expectedSuccessResult)
        }
        .runToFuture

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("test Fx[Task].handleEitherNonFatalWith should not handle Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

    try {
      Fx[Task]
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

  test("test Fx[Task].handleEitherNonFatalWith should return the successful result") {

    val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    Fx[Task]
      .handleEitherNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError]))
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].handleEitherNonFatalWith should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    Fx[Task]
      .handleEitherNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError]))
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].handleEitherTNonFatalWith should handle NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
    val expectedFailedResult  = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult    = Fx[Task]
      .handleEitherTNonFatalWith(fa)(err => Task.pure(SomeError.someThrowable(err).asLeft[Int]))
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }
      .runToFuture
    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      Fx[Task]
        .handleEitherTNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError]))
        .value
        .map { actual =>
          Assertions.assertEquals(actual, expectedSuccessResult)
        }
        .runToFuture

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("test Fx[Task].handleEitherTNonFatalWith should not handle Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

    try {
      Fx[Task]
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

  test("test Fx[Task].handleEitherTNonFatalWith should return the successful result") {

    val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    Fx[Task]
      .handleEitherTNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError]))
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].handleEitherTNonFatalWith should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    Fx[Task]
      .handleEitherTNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError]))
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].handleNonFatal should handle NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Int](throwThrowable[Int](expectedException))
    val expected          = 123

    Fx[Task]
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

  test("test Fx[Task].handleNonFatal should not handle Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[Task, Int](throwThrowable[Int](fatalExpcetion))

    try {
      Fx[Task]
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

  test("test Fx[Task].handleNonFatal should return the successful result") {

    val fa       = run[Task, Int](1)
    val expected = 1

    Fx[Task]
      .handleNonFatal(fa)(_ => 999)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].handleNonFatalEither should handle NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
    val actualFailedResult   = Fx[Task]
      .handleNonFatal(fa)(_ => expectedFailedResult)
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }
      .runToFuture

    val expectedSuccessResult = 1.asRight[SomeError]
    val actualSuccessResult   = Fx[Task]
      .handleNonFatal(fa)(_ => 1.asRight[SomeError])
      .map { actual =>
        Assertions.assertEquals(actual, expectedSuccessResult)
      }
      .runToFuture

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("test Fx[Task].handleNonFatalEither should not handle Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

    try {
      Fx[Task]
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

  test("test Fx[Task].handleNonFatalEither should return the successful result") {

    val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    Fx[Task]
      .handleNonFatal(fa)(_ => 999.asRight[SomeError])
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].handleNonFatalEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    Fx[Task]
      .handleNonFatal(fa)(_ => 123.asRight[SomeError])
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].handleEitherNonFatal should handle NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult   = Fx[Task]
      .handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }
      .runToFuture

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      Fx[Task]
        .handleEitherNonFatal(fa)(_ => 123.asRight[SomeError])
        .map { actual =>
          Assertions.assertEquals(actual, expectedSuccessResult)
        }
        .runToFuture

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("test Fx[Task].handleEitherNonFatal should not handle Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

    try {
      Fx[Task]
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

  test("test Fx[Task].handleEitherNonFatal should return the successful result") {

    val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    Fx[Task]
      .handleEitherNonFatal(fa)(_ => 123.asRight[SomeError])
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].handleEitherNonFatal should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    Fx[Task]
      .handleEitherNonFatal(fa)(_ => 123.asRight[SomeError])
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].handleEitherTNonFatal should handle NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult   = Fx[Task]
      .handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }
      .runToFuture

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      Fx[Task]
        .handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError])
        .value
        .map { actual =>
          Assertions.assertEquals(actual, expectedSuccessResult)
        }
        .runToFuture

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("test Fx[Task].handleEitherTNonFatal should not handle Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

    try {
      Fx[Task]
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

  test("test Fx[Task].handleEitherTNonFatal should return the successful result") {

    val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    Fx[Task]
      .handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError])
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].handleEitherTNonFatal should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    Fx[Task]
      .handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError])
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].recoverFromNonFatalWith should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Int](throwThrowable[Int](expectedException))
    val expected          = 123

    Fx[Task]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedException`) =>
          Task.pure(expected)
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].recoverFromNonFatalWith should not catch Fatal") {

    val expectedException = SomeControlThrowable("Something's wrong")
    val fa                = run[Task, Int](throwThrowable[Int](expectedException))

    try {
      Fx[Task]
        .recoverFromNonFatalWith(fa) { case NonFatal(`expectedException`) => Task.pure(123) }
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

  test("test Fx[Task].recoverFromNonFatalWith should return the successful result") {

    val fa       = run[Task, Int](1)
    val expected = 1

    Fx[Task]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(_) => Task.pure(999)
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].recoverFromNonFatalWithEither should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
    val actualFailedResult   = Fx[Task]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedException`) => Task.pure(expectedFailedResult)
      }
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }
      .runToFuture

    val expectedSuccessResult = 1.asRight[SomeError]
    val actualSuccessResult   = Fx[Task]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedException`) => Task.pure(1.asRight[SomeError])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expectedSuccessResult)
      }
      .runToFuture

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("test Fx[Task].recoverFromNonFatalWithEither should not catch Fatal") {

    val expectedException = SomeControlThrowable("Something's wrong")
    val fa                = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))

    try {
      Fx[Task]
        .recoverFromNonFatalWith(fa) {
          case NonFatal(`expectedException`) => Task.pure(123.asRight[SomeError])
        }
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

  test("test Fx[Task].recoverFromNonFatalWithEither should return the successful result") {

    val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    Fx[Task]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(_) => Task(999.asRight[SomeError])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].recoverFromNonFatalWithEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    Fx[Task]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(_) => Task.pure(123.asRight[SomeError])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].recoverEitherFromNonFatalWith should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult   = Fx[Task]
      .recoverEitherFromNonFatalWith(fa) {
        case err => Task.pure(SomeError.someThrowable(err).asLeft[Int])
      }
      .map { actualFailedResult =>
        Assertions.assertEquals(actualFailedResult, expectedFailedResult)
      }
      .runToFuture

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   = Fx[Task]
      .recoverEitherFromNonFatalWith(fa) {
        case NonFatal(`expectedException`) => Task.pure(123.asRight[SomeError])
      }
      .map { actualSuccessResult =>
        Assertions.assertEquals(actualSuccessResult, expectedSuccessResult)
      }
      .runToFuture

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("test Fx[Task].recoverEitherFromNonFatalWith should not catch Fatal") {

    val expectedException = SomeControlThrowable("Something's wrong")
    val fa                = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))

    try {
      Fx[Task]
        .recoverEitherFromNonFatalWith(fa) {
          case err => Task.pure(SomeError.someThrowable(err).asLeft[Int])
        }
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

  test("test Fx[Task].recoverEitherFromNonFatalWith should return the successful result") {

    val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    Fx[Task]
      .recoverEitherFromNonFatalWith(fa) {
        case NonFatal(_) => Task.pure(123.asRight[SomeError])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].recoverEitherFromNonFatalWith should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    Fx[Task]
      .recoverEitherFromNonFatalWith(fa) {
        case NonFatal(_) => Task.pure(123.asRight[SomeError])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].recoverEitherTFromNonFatalWith should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult   = Fx[Task]
      .recoverEitherTFromNonFatalWith(fa) {
        case err => Task.pure(SomeError.someThrowable(err).asLeft[Int])
      }
      .value
      .map { actualFailedResult =>
        Assertions.assertEquals(actualFailedResult, expectedFailedResult)
      }
      .runToFuture

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   = Fx[Task]
      .recoverEitherTFromNonFatalWith(fa) {
        case NonFatal(`expectedException`) => Task.pure(123.asRight[SomeError])
      }
      .value
      .map { actualSuccessResult =>
        Assertions.assertEquals(actualSuccessResult, expectedSuccessResult)
      }
      .runToFuture

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("test Fx[Task].recoverEitherTFromNonFatalWith should not catch Fatal") {

    val expectedException = SomeControlThrowable("Something's wrong")
    val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))

    try {
      Fx[Task]
        .recoverEitherTFromNonFatalWith(fa) {
          case err => Task.pure(SomeError.someThrowable(err).asLeft[Int])
        }
        .value
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

  test("test Fx[Task].recoverEitherTFromNonFatalWith should return the successful result") {

    val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]
    Fx[Task]
      .recoverEitherTFromNonFatalWith(fa) {
        case NonFatal(_) => Task.pure(123.asRight[SomeError])
      }
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].recoverEitherTFromNonFatalWith should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    Fx[Task]
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

  test("test Fx[Task].recoverFromNonFatal should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Int](throwThrowable[Int](expectedException))
    val expected          = 123

    Fx[Task]
      .recoverFromNonFatal(fa) {
        case NonFatal(`expectedException`) =>
          expected
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].recoverFromNonFatal should not catch Fatal") {

    val expectedException = SomeControlThrowable("Something's wrong")
    val fa                = run[Task, Int](throwThrowable[Int](expectedException))

    try {
      Fx[Task]
        .recoverFromNonFatal(fa) { case NonFatal(`expectedException`) => 123 }
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

  test("test Fx[Task].recoverFromNonFatal should return the successful result") {

    val fa       = run[Task, Int](1)
    val expected = 1
    Fx[Task]
      .recoverFromNonFatal(fa) { case NonFatal(_) => 999 }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].recoverFromNonFatalEither should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]

    val actualFailedResult = Fx[Task]
      .recoverFromNonFatal(fa) { case NonFatal(`expectedException`) => expectedFailedResult }
      .map { actualFailedResult =>
        Assertions.assertEquals(actualFailedResult, expectedFailedResult)
      }
      .runToFuture

    val expectedSuccessResult = 1.asRight[SomeError]
    val actualSuccessResult   = Fx[Task]
      .recoverFromNonFatal(fa) { case NonFatal(`expectedException`) => 1.asRight[SomeError] }
      .map { actualSuccessResult =>
        Assertions.assertEquals(actualSuccessResult, expectedSuccessResult)
      }
      .runToFuture

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("test Fx[Task].recoverFromNonFatalEither should not catch Fatal") {

    val expectedException = SomeControlThrowable("Something's wrong")
    val fa                = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))

    try {
      Fx[Task]
        .recoverFromNonFatal(fa) { case NonFatal(`expectedException`) => 123.asRight[SomeError] }
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

  test("test Fx[Task].recoverFromNonFatalEither should return the successful result") {

    val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    Fx[Task]
      .recoverFromNonFatal(fa) { case NonFatal(_) => 999.asRight[SomeError] }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].recoverFromNonFatalEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    Fx[Task]
      .recoverFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].recoverEitherFromNonFatal should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult   =
      Fx[Task]
        .recoverEitherFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
        .map { actualFailedResult =>
          Assertions.assertEquals(actualFailedResult, expectedFailedResult)
        }
        .runToFuture

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      Fx[Task]
        .recoverEitherFromNonFatal(fa) { case NonFatal(`expectedException`) => 123.asRight[SomeError] }
        .map { actualSuccessResult =>
          Assertions.assertEquals(actualSuccessResult, expectedSuccessResult)
        }
        .runToFuture

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("test Fx[Task].recoverEitherFromNonFatal should not catch Fatal") {

    val expectedException = SomeControlThrowable("Something's wrong")
    val fa                = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))

    try {
      Fx[Task]
        .recoverEitherFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
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

  test("test Fx[Task].recoverEitherFromNonFatal should return the successful result") {

    val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    Fx[Task]
      .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].recoverEitherFromNonFatal should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    Fx[Task]
      .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].recoverEitherTFromNonFatal should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
    val expectedFailedResult  = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult    =
      Fx[Task]
        .recoverEitherTFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
        .value
        .map { actualFailedResult =>
          Assertions.assertEquals(actualFailedResult, expectedFailedResult)
        }
        .runToFuture
    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      Fx[Task]
        .recoverEitherTFromNonFatal(fa) { case NonFatal(`expectedException`) => 123.asRight[SomeError] }
        .value
        .map { actualSuccessResult =>
          Assertions.assertEquals(actualSuccessResult, expectedSuccessResult)
        }
        .runToFuture

    List(actualFailedResult, actualSuccessResult).toSequence
  }

  test("test Fx[Task].recoverEitherTFromNonFatal should not catch Fatal") {

    val expectedException = SomeControlThrowable("Something's wrong")
    val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))

    try {
      Fx[Task]
        .recoverEitherTFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
        .value
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

  test("test Fx[Task].recoverEitherTFromNonFatal should return the successful result") {

    val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    Fx[Task]
      .recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].recoverEitherTFromNonFatal should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    Fx[Task]
      .recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test Fx[Task].onNonFatalWith should do something for NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Int](throwThrowable[Int](expectedException))
    val expected          = 123.some
    var actual            = none[Int] // scalafix:ok DisableSyntax.var

    try {
      Fx[Task]
        .onNonFatalWith(fa) {
          case NonFatal(`expectedException`) =>
            Task.delay {
              actual = expected
            } *> Task.unit
        }
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}"): Unit
        }
        .recover {
          case NonFatal(`expectedException`) =>
            Assertions.assertEquals(actual, expected)
        }
        .runToFuture
    } catch {
      case ex: Throwable =>
        ex
    }

  }

  test("test Fx[Task].onNonFatalWith should not do anything for Fatal") {

    val expectedException = SomeControlThrowable("Something's wrong")
    val fa                = run[Task, Int](throwThrowable[Int](expectedException))
    var actual            = none[Int] // scalafix:ok DisableSyntax.var

    try {
      Fx[Task]
        .onNonFatalWith(fa) {
          case NonFatal(`expectedException`) =>
            Task.delay {
              actual = 123.some
              ()
            } *> Task.unit
        }
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

  test("test Fx[Task].onNonFatalWith should not do anything for the successful result") {

    val expectedResult = 999
    val fa             = run[Task, Int](expectedResult)

    val expected = none[Int]
    var actual   = none[Int] // scalafix:ok DisableSyntax.var

    Fx[Task]
      .onNonFatalWith(fa) {
        case NonFatal(_) =>
          Task.delay {
            actual = 123.some
          } *> Task.unit
      }
      .map { actualResult =>
        Assertions.assertEquals(actualResult, expectedResult)
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture

  }

}
