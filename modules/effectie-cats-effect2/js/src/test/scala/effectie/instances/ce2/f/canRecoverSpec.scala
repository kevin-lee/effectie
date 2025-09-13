package effectie.instances.ce2.f

import cats.data.EitherT
import cats.effect.IO
import cats.syntax.all._
import effectie.SomeControlThrowable
import effectie.core._
import effectie.instances.ce2.f.fxCtor._
import effectie.syntax.error._
import effectie.syntax.fx._
import effectie.testing.FutureTools
import effectie.testing.types.SomeError
import munit.Assertions

import scala.concurrent._
import scala.concurrent.duration._
import scala.util.control.{ControlThrowable, NonFatal}

/** @author Kevin Lee
  * @since 2020-08-17
  */
class canRecoverSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  override val munitTimeout: FiniteDuration = 200.milliseconds

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A = throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor, A](a: => A): F[A] = effectOf[F](a)

  import canRecover._

  test("test CanRecover[IO].recoverFromNonFatalWith should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 123

    CanRecover[IO]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) =>
          IO.pure(expected)
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()

  }

  test("test CanRecover[IO].recoverFromNonFatalWith should not catch Fatal") {

    val expectedExpcetion = SomeControlThrowable("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))

    val io = CanRecover[IO].recoverFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => IO.pure(123) }
    try {
      io.map { actual =>
        Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      }.unsafeToFuture()
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, expectedExpcetion)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  test("test CanRecover[IO].recoverFromNonFatalWith should return the successful result") {

    val fa       = run[IO, Int](1)
    val expected = 1

    CanRecover[IO]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(_) => IO.pure(999)
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test CanRecover[IO].recoverFromNonFatalWithEither should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]

    val actualFailedResult = CanRecover[IO]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) => IO.pure(expectedFailedResult)
      }
      .map { actualFailedResult =>
        Assertions.assertEquals(actualFailedResult, expectedFailedResult)
      }
      .unsafeToFuture()

    val expectedSuccessResult = 1.asRight[SomeError]
    val actualSuccessResult   = CanRecover[IO]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) => IO.pure(1.asRight[SomeError])
      }
      .map { actualSuccessResult =>
        Assertions.assertEquals(actualSuccessResult, expectedSuccessResult)
      }
      .unsafeToFuture()

    Future.sequence(List(actualFailedResult, actualSuccessResult))
  }

  test("test CanRecover[IO].recoverFromNonFatalWithEither should not catch Fatal") {

    val expectedExpcetion = SomeControlThrowable("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

    val io = CanRecover[IO].recoverFromNonFatalWith(fa) {
      case NonFatal(`expectedExpcetion`) => IO.pure(123.asRight[SomeError])
    }
    try {
      io.map { actual =>
        Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      }.unsafeToFuture()
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, expectedExpcetion)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  test("test CanRecover[IO].recoverFromNonFatalWithEither should return the successful result") {

    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    CanRecover[IO]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(_) => IO(999.asRight[SomeError])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test CanRecover[IO].recoverFromNonFatalWithEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    CanRecover[IO]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(_) => IO.pure(123.asRight[SomeError])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test CanRecover[IO].recoverEitherFromNonFatalWith should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    val actualFailedResult = CanRecover[IO]
      .recoverEitherFromNonFatalWith(fa) {
        case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
      }
      .map { actualFailedResult =>
        Assertions.assertEquals(actualFailedResult, expectedFailedResult)
      }
      .unsafeToFuture()

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   = CanRecover[IO]
      .recoverEitherFromNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) => IO.pure(123.asRight[SomeError])
      }
      .map { actualSuccessResult =>
        Assertions.assertEquals(actualSuccessResult, expectedSuccessResult)
      }
      .unsafeToFuture()

    Future.sequence(List(actualFailedResult, actualSuccessResult))
  }

  test("test CanRecover[IO].recoverEitherFromNonFatalWith should not catch Fatal") {

    val expectedExpcetion = SomeControlThrowable("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

    val io = CanRecover[IO].recoverEitherFromNonFatalWith(fa) {
      case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
    }
    try {
      io.map { actual =>
        Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      }.unsafeToFuture()
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, expectedExpcetion)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  test("test CanRecover[IO].recoverEitherFromNonFatalWith should return the successful result") {

    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    CanRecover[IO]
      .recoverEitherFromNonFatalWith(fa) {
        case NonFatal(_) => IO.pure(123.asRight[SomeError])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test CanRecover[IO].recoverEitherFromNonFatalWith should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    CanRecover[IO]
      .recoverEitherFromNonFatalWith(fa) {
        case NonFatal(_) => IO.pure(123.asRight[SomeError])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test CanRecover[IO].recoverEitherTFromNonFatalWith should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   = CanRecover[IO]
      .recoverEitherTFromNonFatalWith(fa) {
        case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
      }
      .value
      .map { actualFailedResult =>
        Assertions.assertEquals(actualFailedResult, expectedFailedResult)
      }
      .unsafeToFuture()

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   = CanRecover[IO]
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

  test("test CanRecover[IO].recoverEitherTFromNonFatalWith should not catch Fatal") {

    val expectedExpcetion = SomeControlThrowable("Something's wrong")
    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))

    val io = CanRecover[IO].recoverEitherTFromNonFatalWith(fa) {
      case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
    }
    try {
      io.value
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

  test("test CanRecover[IO].recoverEitherTFromNonFatalWith should return the successful result") {

    val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    CanRecover[IO]
      .recoverEitherTFromNonFatalWith(fa) {
        case NonFatal(_) => IO.pure(123.asRight[SomeError])
      }
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test CanRecover[IO].recoverEitherTFromNonFatalWith should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    CanRecover[IO]
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

  test("test CanRecover[IO].recoverFromNonFatal should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 123

    CanRecover[IO]
      .recoverFromNonFatal(fa) {
        case NonFatal(`expectedExpcetion`) =>
          expected
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test CanRecover[IO].recoverFromNonFatal should not catch Fatal") {

    val expectedExpcetion = SomeControlThrowable("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))

    val io = CanRecover[IO].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123 }
    try {
      io.map { actual =>
        Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      }.unsafeToFuture()
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, expectedExpcetion)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  test("test CanRecover[IO].recoverFromNonFatal should return the successful result") {

    val fa       = run[IO, Int](1)
    val expected = 1

    CanRecover[IO]
      .recoverFromNonFatal(fa) { case NonFatal(_) => 999 }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test CanRecover[IO].recoverFromNonFatalEither should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
    val actualFailedResult   = CanRecover[IO]
      .recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expectedFailedResult }
      .map { actualFailedResult =>
        Assertions.assertEquals(actualFailedResult, expectedFailedResult)
      }
      .unsafeToFuture()

    val expectedSuccessResult = 1.asRight[SomeError]
    val actualSuccessResult   = CanRecover[IO]
      .recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }
      .map { actualSuccessResult =>
        Assertions.assertEquals(actualSuccessResult, expectedSuccessResult)
      }
      .unsafeToFuture()

    Future.sequence(List(actualFailedResult, actualSuccessResult))
  }

  test("test CanRecover[IO].recoverFromNonFatalEither should not catch Fatal") {

    val expectedExpcetion = SomeControlThrowable("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

    val io = CanRecover[IO].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }
    try {
      io.map { actual =>
        Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      }.unsafeToFuture()
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, expectedExpcetion)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  test("test CanRecover[IO].recoverFromNonFatalEither should return the successful result") {

    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    CanRecover[IO]
      .recoverFromNonFatal(fa) { case NonFatal(_) => 999.asRight[SomeError] }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test CanRecover[IO].recoverFromNonFatalEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    CanRecover[IO]
      .recoverFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test CanRecover[IO].recoverEitherFromNonFatal should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   =
      CanRecover[IO]
        .recoverEitherFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
        .map { actualFailedResult =>
          Assertions.assertEquals(actualFailedResult, expectedFailedResult)
        }
        .unsafeToFuture()

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      CanRecover[IO]
        .recoverEitherFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }
        .map { actualSuccessResult =>
          Assertions.assertEquals(actualSuccessResult, expectedSuccessResult)
        }
        .unsafeToFuture()

    Future.sequence(List(actualFailedResult, actualSuccessResult))
  }

  test("test CanRecover[IO].recoverEitherFromNonFatal should not catch Fatal") {

    val expectedExpcetion = SomeControlThrowable("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

    val io =
      CanRecover[IO].recoverEitherFromNonFatal(fa) {
        case err => SomeError.someThrowable(err).asLeft[Int]
      }
    try {
      io.map { actual =>
        Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      }.unsafeToFuture()
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, expectedExpcetion)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  test("test CanRecover[IO].recoverEitherFromNonFatal should return the successful result") {

    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    CanRecover[IO]
      .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test CanRecover[IO].recoverEitherFromNonFatal should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    CanRecover[IO]
      .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test CanRecover[IO].recoverEitherTFromNonFatal should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult    =
      CanRecover[IO]
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
      CanRecover[IO]
        .recoverEitherTFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }
        .value
        .map { actualSuccessResult =>
          Assertions.assertEquals(actualSuccessResult, expectedSuccessResult)
        }
        .unsafeToFuture()

    Future.sequence(List(actualFailedResult, actualSuccessResult))
  }

  test("test CanRecover[IO].recoverEitherTFromNonFatal should not catch Fatal") {

    val expectedExpcetion = SomeControlThrowable("Something's wrong")
    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))

    val io =
      CanRecover[IO].recoverEitherTFromNonFatal(fa) {
        case err => SomeError.someThrowable(err).asLeft[Int]
      }
    try {
      io.value
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

  test("test CanRecover[IO].recoverEitherTFromNonFatal should return the successful result") {

    val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    CanRecover[IO]
      .recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

  test("test CanRecover[IO].recoverEitherTFromNonFatal should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    CanRecover[IO]
      .recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()
  }

}
