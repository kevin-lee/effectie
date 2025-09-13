package effectie.instances.ce2.f

import cats.Monad
import cats.data.EitherT
import cats.effect._
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

/** @author Kevin Lee
  * @since 2020-12-06
  */
class fxSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  override val munitTimeout: FiniteDuration = 200.milliseconds

  private def assertWithAttempt[A](io: IO[A], expected: Either[Throwable, A]): IO[Unit] = {
    io.attempt.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test Fx[IO].effectOf")(
    FxSpecs4Js
      .testEffectOf[IO]((fa, actual, expected, log) => fa.map(_ => Assertions.assertEquals(actual(0), expected, log)))
      .unsafeToFuture()
  )
  test("test Fx[IO].fromEffect(effectOf)")(
    FxSpecs4Js
      .testFromEffect[IO]((fa, actual, expected, log) => fa.map(_ => Assertions.assertEquals(actual(0), expected, log)))
      .unsafeToFuture()
  )
  test("test Fx[IO].fromEffect(pureOf)")(
    FxSpecs4Js
      .testFromEffectWithPure[IO]((fa, actual, expected, log) =>
        fa.map(_ => Assertions.assertEquals(actual(0), expected, log))
      )
      .unsafeToFuture()
  )
  test("test Fx[IO].pureOf")(
    FxSpecs4Js
      .testPureOf[IO]((fa, actual, expected, log) => fa.map(_ => Assertions.assertEquals(actual(0), expected, log)))
      .unsafeToFuture()
  )
  test("test Fx[IO].pureOrError(success case)")(
    FxSpecs4Js
      .testPureOrErrorSuccessCase[IO]((fa, actual, expected, log) =>
        fa.map(_ => Assertions.assertEquals(actual(0), expected, log))
      )
      .unsafeToFuture()
  )
  test("test Fx[IO].pureOrError(error case)")(
    FxSpecs4Js.testPureOrErrorErrorCase[IO]((io, expected) => assertWithAttempt(io, expected.asLeft)).unsafeToFuture()
  )
  test("test Fx[IO].unitOf")(
    FxSpecs4Js.testUnitOf[IO](fa => fa.map(actual => Assertions.assertEquals(actual, ()))).unsafeToFuture()
  )
  test("test Fx[IO].errorOf")(
    FxSpecs4Js.testErrorOf[IO]((io, expected) => assertWithAttempt(io, expected.asLeft)).unsafeToFuture()
  )
  test("test Fx[IO].fromEither(Right)")(FxSpecs4Js.testFromEitherRightCase[IO](assertWithAttempt).unsafeToFuture())
  test("test Fx[IO].fromEither(Left)")(FxSpecs4Js.testFromEitherLeftCase[IO](assertWithAttempt).unsafeToFuture())
  test("test Fx[IO].fromOption(Some)")(FxSpecs4Js.testFromOptionSomeCase[IO](assertWithAttempt).unsafeToFuture())
  test("test Fx[IO].fromOption(None)")(FxSpecs4Js.testFromOptionNoneCase[IO](assertWithAttempt).unsafeToFuture())
  test("test Fx[IO].fromTry(Success)")(FxSpecs4Js.testFromTrySuccessCase[IO](assertWithAttempt).unsafeToFuture())
  test("test Fx[IO].fromTry(Failure)")(FxSpecs4Js.testFromTryFailureCase[IO](assertWithAttempt).unsafeToFuture())

  /* Test MonadLaws */
  MonadSpec4Js.testMonadLaws[IO]("IO").foreach {
    case (name, testF) =>
      test(name) {
        testF().unsafeToFuture()
      }
  }

  def throwThrowable[A](throwable: => Throwable): A = throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: Fx, A](a: => A): F[A] = Fx[F].effectOf(a)

  implicit def eqF[F[*]: Monad]: EqF[F, Int] =
    (a, b) => a.flatMap(aVal => b.map(aVal === _))

  test("test Fx[IO].catchNonFatalThrowable should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = expectedExpcetion.asLeft[Int]

    Fx[IO]
      .catchNonFatalThrowable(fa)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].catchNonFatalThrowable should not catch Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))

    try {
      Fx[IO]
        .catchNonFatalThrowable(fa)
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .unsafeToFuture()
    } catch {
      case ex: SomeControlThrowable =>
        Assertions.assertEquals(ex.getMessage, fatalExpcetion.getMessage)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  test("test Fx[IO].catchNonFatalThrowable should return the successful result") {

    val fa       = run[IO, Int](1)
    val expected = 1.asRight[Throwable]

    Fx[IO]
      .catchNonFatalThrowable(fa)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].catchNonFatal should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    Fx[IO]
      .catchNonFatal(fa)(SomeError.someThrowable)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].catchNonFatal should not catch Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))

    try {
      Fx[IO]
        .catchNonFatal(fa)(SomeError.someThrowable)
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .unsafeToFuture()
    } catch {
      case ex: SomeControlThrowable =>
        Assertions.assertEquals(ex.getMessage, fatalExpcetion.getMessage)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  test("test Fx[IO].catchNonFatal should return the successful result") {

    val fa       = run[IO, Int](1)
    val expected = 1.asRight[SomeError]

    Fx[IO]
      .catchNonFatal(fa)(SomeError.someThrowable)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].catchNonFatalEither should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    Fx[IO]
      .catchNonFatalEither(fa)(SomeError.someThrowable)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].catchNonFatalEither should not catch Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

    try {
      Fx[IO]
        .catchNonFatalEither(fa)(SomeError.someThrowable)
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .unsafeToFuture()
    } catch {
      case ex: SomeControlThrowable =>
        Assertions.assertEquals(ex.getMessage, fatalExpcetion.getMessage)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test Fx[IO].catchNonFatalEither should return the successful result") {

    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    Fx[IO]
      .catchNonFatalEither(fa)(SomeError.someThrowable)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].catchNonFatalEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    Fx[IO]
      .catchNonFatalEither(fa)(SomeError.someThrowable)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].catchNonFatalEitherT should catch NonFatal") {

    val expectedExpcetion               = new RuntimeException("Something's wrong")
    val fa: EitherT[IO, SomeError, Int] = EitherT(
      run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    )
    val expected                        = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    Fx[IO]
      .catchNonFatalEitherT(fa)(SomeError.someThrowable)
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].catchNonFatalEitherT should not catch Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

    try {
      Fx[IO]
        .catchNonFatalEitherT(fa)(SomeError.someThrowable)
        .value
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .unsafeToFuture()
    } catch {
      case ex: SomeControlThrowable =>
        Assertions.assertEquals(ex.getMessage, fatalExpcetion.getMessage)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  test("test Fx[IO].catchNonFatalEitherT should return the successful result") {

    val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    Fx[IO]
      .catchNonFatalEitherT(fa)(SomeError.someThrowable)
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].catchNonFatalEitherT should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    Fx[IO]
      .catchNonFatalEitherT(fa)(SomeError.someThrowable)
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].handleNonFatalWith should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 123

    Fx[IO]
      .handleNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) =>
          IO.pure(expected)
        case err =>
          throw err // scalafix:ok DisableSyntax.throw
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].handleNonFatalWith should not handle Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))

    try {
      Fx[IO]
        .handleNonFatalWith(fa)(_ => IO.pure(123))
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .unsafeToFuture()
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, fatalExpcetion)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test Fx[IO].handleNonFatalWith should return the successful result") {

    val fa       = run[IO, Int](1)
    val expected = 1

    Fx[IO]
      .handleNonFatalWith(fa)(_ => IO.pure(999))
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].handleNonFatalWithEither should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
    val actualFailedResult   =
      Fx[IO]
        .handleNonFatalWith(fa)(_ => IO.pure(expectedFailedResult))
        .map { actual =>
          Assertions.assertEquals(actual, expectedFailedResult)
        }
        .unsafeToFuture()

    val expectedSuccessResult = 1.asRight[SomeError]
    val actualSuccessResult   =
      Fx[IO]
        .handleNonFatalWith(fa)(_ => IO.pure(1.asRight[SomeError]))
        .map { actual =>
          Assertions.assertEquals(actual, expectedSuccessResult)
        }
        .unsafeToFuture()

    Future.sequence(List(actualFailedResult, actualSuccessResult))
  }

  test("test Fx[IO].handleNonFatalWithEither should not handle Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

    try {
      Fx[IO]
        .handleNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError]))
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .unsafeToFuture()
    } catch {
      case ex: SomeControlThrowable =>
        Assertions.assertEquals(ex.getMessage, fatalExpcetion.getMessage)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test Fx[IO].handleNonFatalWithEither should return the successful result") {

    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    Fx[IO]
      .handleNonFatalWith(fa)(_ => IO(999.asRight[SomeError]))
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].handleNonFatalWithEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    Fx[IO]
      .handleNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError]))
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].handleEitherNonFatalWith should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult    = Fx[IO]
      .handleEitherNonFatalWith(fa)(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }
      .unsafeToFuture()
    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      Fx[IO]
        .handleEitherNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError]))
        .map { actual =>
          Assertions.assertEquals(actual, expectedSuccessResult)
        }
        .unsafeToFuture()

    Future.sequence(List(actualFailedResult, actualSuccessResult))
  }

  test("test Fx[IO].handleEitherNonFatalWith should not handle Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

    try {
      Fx[IO]
        .handleEitherNonFatalWith(fa)(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .unsafeToFuture()
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, fatalExpcetion)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test Fx[IO].handleEitherNonFatalWith should return the successful result") {

    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    Fx[IO]
      .handleEitherNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError]))
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].handleEitherNonFatalWith should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    Fx[IO]
      .handleEitherNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError]))
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].handleEitherTNonFatalWith should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult    = Fx[IO]
      .handleEitherTNonFatalWith(fa)(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }
      .unsafeToFuture()
    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      Fx[IO]
        .handleEitherTNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError]))
        .value
        .map { actual =>
          Assertions.assertEquals(actual, expectedSuccessResult)
        }
        .unsafeToFuture()

    Future.sequence(List(actualFailedResult, actualSuccessResult))
  }

  test("test Fx[IO].handleEitherTNonFatalWith should not handle Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

    try {
      Fx[IO]
        .handleEitherTNonFatalWith(fa)(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
        .value
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .unsafeToFuture()
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, fatalExpcetion)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test Fx[IO].handleEitherTNonFatalWith should return the successful result") {

    val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    Fx[IO]
      .handleEitherTNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError]))
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].handleEitherTNonFatalWith should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    Fx[IO]
      .handleEitherTNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError]))
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].handleNonFatal should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 123

    Fx[IO]
      .handleNonFatal(fa) {
        case NonFatal(`expectedExpcetion`) =>
          expected

        case err =>
          throw err // scalafix:ok DisableSyntax.throw
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].handleNonFatal should not handle Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))

    try {
      Fx[IO]
        .handleNonFatal(fa)(_ => 123)
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .unsafeToFuture()
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, fatalExpcetion)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test Fx[IO].handleNonFatal should return the successful result") {

    val fa       = run[IO, Int](1)
    val expected = 1

    Fx[IO]
      .handleNonFatal(fa)(_ => 999)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].handleNonFatalEither should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
    val actualFailedResult   = Fx[IO]
      .handleNonFatal(fa)(_ => expectedFailedResult)
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }
      .unsafeToFuture()

    val expectedSuccessResult = 1.asRight[SomeError]
    val actualSuccessResult   = Fx[IO]
      .handleNonFatal(fa)(_ => 1.asRight[SomeError])
      .map { actual =>
        Assertions.assertEquals(actual, expectedSuccessResult)
      }
      .unsafeToFuture()

    Future.sequence(List(actualFailedResult, actualSuccessResult))
  }

  test("test Fx[IO].handleNonFatalEither should not handle Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

    try {
      Fx[IO]
        .handleNonFatal(fa)(_ => 123.asRight[SomeError])
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .unsafeToFuture()
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, fatalExpcetion)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test Fx[IO].handleNonFatalEither should return the successful result") {

    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    Fx[IO]
      .handleNonFatal(fa)(_ => 999.asRight[SomeError])
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].handleNonFatalEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    Fx[IO]
      .handleNonFatal(fa)(_ => 123.asRight[SomeError])
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].handleEitherNonFatal should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   = Fx[IO]
      .handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }
      .unsafeToFuture()

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      Fx[IO]
        .handleEitherNonFatal(fa)(_ => 123.asRight[SomeError])
        .map { actual =>
          Assertions.assertEquals(actual, expectedSuccessResult)
        }
        .unsafeToFuture()

    Future.sequence(List(actualFailedResult, actualSuccessResult))
  }

  test("test Fx[IO].handleEitherNonFatal should not handle Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

    try {
      Fx[IO]
        .handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .unsafeToFuture()
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, fatalExpcetion)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  test("test Fx[IO].handleEitherNonFatal should return the successful result") {

    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    Fx[IO]
      .handleEitherNonFatal(fa)(_ => 123.asRight[SomeError])
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].handleEitherNonFatal should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    Fx[IO]
      .handleEitherNonFatal(fa)(_ => 123.asRight[SomeError])
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].handleEitherTNonFatal should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   = Fx[IO]
      .handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }
      .unsafeToFuture()

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      Fx[IO]
        .handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError])
        .value
        .map { actual =>
          Assertions.assertEquals(actual, expectedSuccessResult)
        }
        .unsafeToFuture()

    Future.sequence(List(actualFailedResult, actualSuccessResult))
  }

  test("test Fx[IO].handleEitherTNonFatal should not handle Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

    try {
      Fx[IO]
        .handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
        .value
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .unsafeToFuture()
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, fatalExpcetion)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  test("test Fx[IO].handleEitherTNonFatal should return the successful result") {

    val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    Fx[IO]
      .handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError])
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].handleEitherTNonFatal should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    Fx[IO]
      .handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError])
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].recoverFromNonFatalWith should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 123

    Fx[IO]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) =>
          IO.pure(expected)
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].recoverFromNonFatalWith should not catch Fatal") {

    val expectedExpcetion = SomeControlThrowable("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))

    try {
      Fx[IO]
        .recoverFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => IO.pure(123) }
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .unsafeToFuture()
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, expectedExpcetion)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test Fx[IO].recoverFromNonFatalWith should return the successful result") {

    val fa       = run[IO, Int](1)
    val expected = 1

    Fx[IO]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(_) => IO.pure(999)
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].recoverFromNonFatalWithEither should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
    val actualFailedResult   = Fx[IO]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) => IO.pure(expectedFailedResult)
      }
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }
      .unsafeToFuture()

    val expectedSuccessResult = 1.asRight[SomeError]
    val actualSuccessResult   = Fx[IO]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) => IO.pure(1.asRight[SomeError])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expectedSuccessResult)
      }
      .unsafeToFuture()

    Future.sequence(List(actualFailedResult, actualSuccessResult))
  }

  test("test Fx[IO].recoverFromNonFatalWithEither should not catch Fatal") {

    val expectedExpcetion = SomeControlThrowable("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

    try {
      Fx[IO]
        .recoverFromNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) => IO.pure(123.asRight[SomeError])
        }
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .unsafeToFuture()
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, expectedExpcetion)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test Fx[IO].recoverFromNonFatalWithEither should return the successful result") {

    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    Fx[IO]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(_) => IO(999.asRight[SomeError])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].recoverFromNonFatalWithEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    Fx[IO]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(_) => IO.pure(123.asRight[SomeError])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].recoverEitherFromNonFatalWith should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   = Fx[IO]
      .recoverEitherFromNonFatalWith(fa) {
        case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
      }
      .map { actualFailedResult =>
        Assertions.assertEquals(actualFailedResult, expectedFailedResult)
      }
      .unsafeToFuture()

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   = Fx[IO]
      .recoverEitherFromNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) => IO.pure(123.asRight[SomeError])
      }
      .map { actualSuccessResult =>
        Assertions.assertEquals(actualSuccessResult, expectedSuccessResult)
      }
      .unsafeToFuture()

    Future.sequence(List(actualFailedResult, actualSuccessResult))
  }

  test("test Fx[IO].recoverEitherFromNonFatalWith should not catch Fatal") {

    val expectedExpcetion = SomeControlThrowable("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

    try {
      Fx[IO]
        .recoverEitherFromNonFatalWith(fa) {
          case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
        }
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .unsafeToFuture()
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, expectedExpcetion)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test Fx[IO].recoverEitherFromNonFatalWith should return the successful result") {

    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    Fx[IO]
      .recoverEitherFromNonFatalWith(fa) {
        case NonFatal(_) => IO.pure(123.asRight[SomeError])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].recoverEitherFromNonFatalWith should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    Fx[IO]
      .recoverEitherFromNonFatalWith(fa) {
        case NonFatal(_) => IO.pure(123.asRight[SomeError])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].recoverEitherTFromNonFatalWith should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   = Fx[IO]
      .recoverEitherTFromNonFatalWith(fa) {
        case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
      }
      .value
      .map { actualFailedResult =>
        Assertions.assertEquals(actualFailedResult, expectedFailedResult)
      }
      .unsafeToFuture()

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   = Fx[IO]
      .recoverEitherTFromNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) => IO.pure(123.asRight[SomeError])
      }
      .value
      .map { actualSuccessResult =>
        Assertions.assertEquals(actualSuccessResult, expectedSuccessResult)
      }
      .unsafeToFuture()

    Future.sequence(List(actualFailedResult, actualSuccessResult))
  }

  test("test Fx[IO].recoverEitherTFromNonFatalWith should not catch Fatal") {

    val expectedExpcetion = SomeControlThrowable("Something's wrong")
    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))

    try {
      Fx[IO]
        .recoverEitherTFromNonFatalWith(fa) {
          case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
        }
        .value
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .unsafeToFuture()
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, expectedExpcetion)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test Fx[IO].recoverEitherTFromNonFatalWith should return the successful result") {

    val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]
    Fx[IO]
      .recoverEitherTFromNonFatalWith(fa) {
        case NonFatal(_) => IO.pure(123.asRight[SomeError])
      }
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].recoverEitherTFromNonFatalWith should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    Fx[IO]
      .recoverEitherTFromNonFatalWith(fa) {
        case NonFatal(_) => IO.pure(123.asRight[SomeError])
      }
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  // /

  test("test Fx[IO].recoverFromNonFatal should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 123

    Fx[IO]
      .recoverFromNonFatal(fa) {
        case NonFatal(`expectedExpcetion`) =>
          expected
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].recoverFromNonFatal should not catch Fatal") {

    val expectedExpcetion = SomeControlThrowable("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))

    try {
      Fx[IO]
        .recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123 }
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .unsafeToFuture()
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, expectedExpcetion)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  test("test Fx[IO].recoverFromNonFatal should return the successful result") {

    val fa       = run[IO, Int](1)
    val expected = 1
    Fx[IO]
      .recoverFromNonFatal(fa) { case NonFatal(_) => 999 }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].recoverFromNonFatalEither should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]

    val actualFailedResult = Fx[IO]
      .recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expectedFailedResult }
      .map { actualFailedResult =>
        Assertions.assertEquals(actualFailedResult, expectedFailedResult)
      }
      .unsafeToFuture()

    val expectedSuccessResult = 1.asRight[SomeError]
    val actualSuccessResult   = Fx[IO]
      .recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }
      .map { actualSuccessResult =>
        Assertions.assertEquals(actualSuccessResult, expectedSuccessResult)
      }
      .unsafeToFuture()

    Future.sequence(List(actualFailedResult, actualSuccessResult))
  }

  test("test Fx[IO].recoverFromNonFatalEither should not catch Fatal") {

    val expectedExpcetion = SomeControlThrowable("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

    try {
      Fx[IO]
        .recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .unsafeToFuture()
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, expectedExpcetion)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test Fx[IO].recoverFromNonFatalEither should return the successful result") {

    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    Fx[IO]
      .recoverFromNonFatal(fa) { case NonFatal(_) => 999.asRight[SomeError] }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].recoverFromNonFatalEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    Fx[IO]
      .recoverFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].recoverEitherFromNonFatal should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   =
      Fx[IO]
        .recoverEitherFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
        .map { actualFailedResult =>
          Assertions.assertEquals(actualFailedResult, expectedFailedResult)
        }
        .unsafeToFuture()

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      Fx[IO]
        .recoverEitherFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }
        .map { actualSuccessResult =>
          Assertions.assertEquals(actualSuccessResult, expectedSuccessResult)
        }
        .unsafeToFuture()

    Future.sequence(List(actualFailedResult, actualSuccessResult))
  }

  test("test Fx[IO].recoverEitherFromNonFatal should not catch Fatal") {

    val expectedExpcetion = SomeControlThrowable("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

    try {
      Fx[IO]
        .recoverEitherFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .unsafeToFuture()
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, expectedExpcetion)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test Fx[IO].recoverEitherFromNonFatal should return the successful result") {

    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    Fx[IO]
      .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].recoverEitherFromNonFatal should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    Fx[IO]
      .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].recoverEitherTFromNonFatal should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult    =
      Fx[IO]
        .recoverEitherTFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
        .value
        .map { actualFailedResult =>
          Assertions.assertEquals(actualFailedResult, expectedFailedResult)
        }
        .unsafeToFuture()
    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      Fx[IO]
        .recoverEitherTFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }
        .value
        .map { actualSuccessResult =>
          Assertions.assertEquals(actualSuccessResult, expectedSuccessResult)
        }
        .unsafeToFuture()

    Future.sequence(List(actualFailedResult, actualSuccessResult))
  }

  test("test Fx[IO].recoverEitherTFromNonFatal should not catch Fatal") {

    val expectedExpcetion = SomeControlThrowable("Something's wrong")
    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))

    try {
      Fx[IO]
        .recoverEitherTFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
        .value
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .unsafeToFuture()
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, expectedExpcetion)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test Fx[IO].recoverEitherTFromNonFatal should return the successful result") {

    val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    Fx[IO]
      .recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test Fx[IO].recoverEitherTFromNonFatal should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    Fx[IO]
      .recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

}
