package effectie.instances.ce3.f

import canRecover._
import cats.data.EitherT
import cats.effect.IO
import cats.instances.all._
import cats.syntax.all._
import effectie.core._
import effectie.syntax.error._
import effectie.syntax.fx._
import effectie.testing.types.SomeError
import fxCtor._
import munit.Assertions

import scala.util.control.NonFatal

/** @author Kevin Lee
  * @since 2020-08-17
  */
class canRecoverSpec extends munit.CatsEffectSuite {

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A = throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor, A](a: => A): F[A] = effectOf[F](a)

  test("test CanRecover[IO].recoverFromNonFatalWith should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedException))
    val expected          = 123
    val actual            = CanRecover[IO].recoverFromNonFatalWith(fa) {
      case NonFatal(`expectedException`) =>
        IO.pure(expected)
    }
    actual.map(Assertions.assertEquals(_, expected))

  }

//  test("test CanRecover[IO].recoverFromNonFatalWith should not catch Fatal") {
//
//    val expectedException = SomeControlThrowable("Something's wrong")
//    val fa                = run[IO, Int](throwThrowable[Int](expectedException))
//
//    val io = CanRecover[IO].recoverFromNonFatalWith(fa) { case NonFatal(`expectedException`) => IO.pure(123) }
//    try {
//      io.map(actual => Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}"))
//    } catch {
//      case ex: ControlThrowable =>
//        Assertions.assertEquals(ex, expectedException)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//
//  }

  test("test CanRecover[IO].recoverFromNonFatalWith should return the successful result") {

    val expected = 1
    val fa       = run[IO, Int](expected)
    val actual   = CanRecover[IO]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(_) => IO.pure(999)
      }

    actual.map(Assertions.assertEquals(_, expected))
  }

  test("test CanRecover[IO].recoverFromNonFatalWithEither should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
    val actualFailedResult   = CanRecover[IO]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedException`) => IO.pure(expectedFailedResult)
      }

    val expectedSuccessResult = 1.asRight[SomeError]
    val actualSuccessResult   = CanRecover[IO]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedException`) => IO.pure(1.asRight[SomeError])
      }

    List(
      actualFailedResult.map(Assertions.assertEquals(_, expectedFailedResult)),
      actualSuccessResult.map(Assertions.assertEquals(_, expectedSuccessResult)),
    ).sequence
  }

//  test("test CanRecover[IO].recoverFromNonFatalWithEither should not catch Fatal") {
//
//    val expectedException = SomeControlThrowable("Something's wrong")
//    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
//
//    val io = CanRecover[IO].recoverFromNonFatalWith(fa) {
//      case NonFatal(`expectedException`) => IO.pure(123.asRight[SomeError])
//    }
//    try {
//      io.map(actual => Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}"))
//    } catch {
//      case ex: ControlThrowable =>
//        Assertions.assertEquals(ex, expectedException)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//
//  }

  test("test CanRecover[IO].recoverFromNonFatalWithEither should return the successful result") {

    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    val actual   = CanRecover[IO]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(_) => IO(999.asRight[SomeError])
      }

    actual.map(Assertions.assertEquals(_, expected))
  }

  test("test CanRecover[IO].recoverFromNonFatalWithEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    val actual          = CanRecover[IO]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(_) => IO.pure(123.asRight[SomeError])
      }

    actual.map(Assertions.assertEquals(_, expected))
  }

  test("test CanRecover[IO].recoverEitherFromNonFatalWith should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult   = CanRecover[IO]
      .recoverEitherFromNonFatalWith(fa) {
        case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
      }

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   = CanRecover[IO]
      .recoverEitherFromNonFatalWith(fa) {
        case NonFatal(`expectedException`) => IO.pure(123.asRight[SomeError])
      }

    List(
      actualFailedResult.map(Assertions.assertEquals(_, expectedFailedResult)),
      actualSuccessResult.map(Assertions.assertEquals(_, expectedSuccessResult)),
    ).sequence
  }

//  test("test CanRecover[IO].recoverEitherFromNonFatalWith should not catch Fatal") {
//
//    val expectedException = SomeControlThrowable("Something's wrong")
//    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
//
//    val io = CanRecover[IO].recoverEitherFromNonFatalWith(fa) {
//      case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
//    }
//    try {
//      io.map(actual => Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}"))
//    } catch {
//      case ex: ControlThrowable =>
//        Assertions.assertEquals(ex, expectedException)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//
//  }

  test("test CanRecover[IO].recoverEitherFromNonFatalWith should return the successful result") {

    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    val actual   = CanRecover[IO]
      .recoverEitherFromNonFatalWith(fa) {
        case NonFatal(_) => IO.pure(123.asRight[SomeError])
      }

    actual.map(Assertions.assertEquals(_, expected))
  }

  test("test CanRecover[IO].recoverEitherFromNonFatalWith should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    val actual          =
      CanRecover[IO]
        .recoverEitherFromNonFatalWith(fa) {
          case NonFatal(_) => IO.pure(123.asRight[SomeError])
        }

    actual.map(Assertions.assertEquals(_, expected))
  }

  test("test CanRecover[IO].recoverEitherTFromNonFatalWith should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult   = CanRecover[IO]
      .recoverEitherTFromNonFatalWith(fa) {
        case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
      }
      .value

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   = CanRecover[IO]
      .recoverEitherTFromNonFatalWith(fa) {
        case NonFatal(`expectedException`) => IO.pure(123.asRight[SomeError])
      }
      .value

    List(
      actualFailedResult.map(Assertions.assertEquals(_, expectedFailedResult)),
      actualSuccessResult.map(Assertions.assertEquals(_, expectedSuccessResult)),
    ).sequence
  }

//  test("test CanRecover[IO].recoverEitherTFromNonFatalWith should not catch Fatal") {
//
//    val expectedException = SomeControlThrowable("Something's wrong")
//    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
//
//    val io = CanRecover[IO].recoverEitherTFromNonFatalWith(fa) {
//      case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
//    }
//    try {
//      io.value
//        .map(actual => Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}"))
//    } catch {
//      case ex: ControlThrowable =>
//        Assertions.assertEquals(ex, expectedException)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//
//  }

  test("test CanRecover[IO].recoverEitherTFromNonFatalWith should return the successful result") {

    val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]
    val actual   = CanRecover[IO]
      .recoverEitherTFromNonFatalWith(fa) {
        case NonFatal(_) => IO.pure(123.asRight[SomeError])
      }
      .value

    actual.map(Assertions.assertEquals(_, expected))
  }

  test("test CanRecover[IO].recoverEitherTFromNonFatalWith should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]
    val actual          =
      CanRecover[IO]
        .recoverEitherTFromNonFatalWith(fa) {
          case NonFatal(_) => IO.pure(123.asRight[SomeError])
        }
        .value

    actual.map(Assertions.assertEquals(_, expected))
  }

  // /

  test("test CanRecover[IO].recoverFromNonFatal should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedException))
    val expected          = 123
    val actual            = CanRecover[IO]
      .recoverFromNonFatal(fa) {
        case NonFatal(`expectedException`) =>
          expected
      }

    actual.map(Assertions.assertEquals(_, expected))
  }

//  test("test CanRecover[IO].recoverFromNonFatal should not catch Fatal") {
//
//    val expectedException = SomeControlThrowable("Something's wrong")
//    val fa                = run[IO, Int](throwThrowable[Int](expectedException))
//
//    val io = CanRecover[IO].recoverFromNonFatal(fa) { case NonFatal(`expectedException`) => 123 }
//    try {
//      io.map(actual => Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}"))
//    } catch {
//      case ex: ControlThrowable =>
//        Assertions.assertEquals(ex, expectedException)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//
//  }

  test("test CanRecover[IO].recoverFromNonFatal should return the successful result") {

    val fa       = run[IO, Int](1)
    val expected = 1
    val actual   = CanRecover[IO].recoverFromNonFatal(fa) { case NonFatal(_) => 999 }

    actual.map(Assertions.assertEquals(_, expected))
  }

  test("test CanRecover[IO].recoverFromNonFatalEither should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
    val actualFailedResult   = CanRecover[IO]
      .recoverFromNonFatal(fa) { case NonFatal(`expectedException`) => expectedFailedResult }

    val expectedSuccessResult = 1.asRight[SomeError]
    val actualSuccessResult   = CanRecover[IO]
      .recoverFromNonFatal(fa) { case NonFatal(`expectedException`) => 1.asRight[SomeError] }

    List(
      actualFailedResult.map(Assertions.assertEquals(_, expectedFailedResult)),
      actualSuccessResult.map(Assertions.assertEquals(_, expectedSuccessResult)),
    ).sequence
  }

//  test("test CanRecover[IO].recoverFromNonFatalEither should not catch Fatal") {
//
//    val expectedException = SomeControlThrowable("Something's wrong")
//    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
//
//    val io = CanRecover[IO].recoverFromNonFatal(fa) { case NonFatal(`expectedException`) => 123.asRight[SomeError] }
//    try {
//      io.map(actual => Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}"))
//    } catch {
//      case ex: ControlThrowable =>
//        Assertions.assertEquals(ex, expectedException)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//
//  }

  test("test CanRecover[IO].recoverFromNonFatalEither should return the successful result") {

    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    val actual   = CanRecover[IO].recoverFromNonFatal(fa) { case NonFatal(_) => 999.asRight[SomeError] }

    actual.map(Assertions.assertEquals(_, expected))
  }

  test("test CanRecover[IO].recoverFromNonFatalEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    val actual          = CanRecover[IO].recoverFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }

    actual.map(Assertions.assertEquals(_, expected))
  }

  test("test CanRecover[IO].recoverEitherFromNonFatal should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult   =
      CanRecover[IO]
        .recoverEitherFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      CanRecover[IO]
        .recoverEitherFromNonFatal(fa) { case NonFatal(`expectedException`) => 123.asRight[SomeError] }

    List(
      actualFailedResult.map(Assertions.assertEquals(_, expectedFailedResult)),
      actualSuccessResult.map(Assertions.assertEquals(_, expectedSuccessResult)),
    ).sequence
  }

//  test("test CanRecover[IO].recoverEitherFromNonFatal should not catch Fatal") {
//
//    val expectedException = SomeControlThrowable("Something's wrong")
//    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
//
//    val io =
//      CanRecover[IO].recoverEitherFromNonFatal(fa) {
//        case err => SomeError.someThrowable(err).asLeft[Int]
//      }
//    try {
//      io.map(actual => Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}"))
//    } catch {
//      case ex: ControlThrowable =>
//        Assertions.assertEquals(ex, expectedException)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//
//  }

  test("test CanRecover[IO].recoverEitherFromNonFatal should return the successful result") {

    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    val actual   =
      CanRecover[IO]
        .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }

    actual.map(Assertions.assertEquals(_, expected))
  }

  test("test CanRecover[IO].recoverEitherFromNonFatal should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    val actual          =
      CanRecover[IO]
        .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }

    actual.map(Assertions.assertEquals(_, expected))
  }

  test("test CanRecover[IO].recoverEitherTFromNonFatal should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult   =
      CanRecover[IO]
        .recoverEitherTFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
        .value

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      CanRecover[IO]
        .recoverEitherTFromNonFatal(fa) { case NonFatal(`expectedException`) => 123.asRight[SomeError] }
        .value

    List(
      actualFailedResult.map(Assertions.assertEquals(_, expectedFailedResult)),
      actualSuccessResult.map(Assertions.assertEquals(_, expectedSuccessResult)),
    ).sequence
  }

//  test("test CanRecover[IO].recoverEitherTFromNonFatal should not catch Fatal") {
//
//    val expectedException = SomeControlThrowable("Something's wrong")
//    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
//
//    val io =
//      CanRecover[IO].recoverEitherTFromNonFatal(fa) {
//        case err => SomeError.someThrowable(err).asLeft[Int]
//      }
//    try {
//      io.value
//        .map(actual => Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}"))
//    } catch {
//      case ex: ControlThrowable =>
//        Assertions.assertEquals(ex, expectedException)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//
//  }

  test("test CanRecover[IO].recoverEitherTFromNonFatal should return the successful result") {

    val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]
    val actual   =
      CanRecover[IO]
        .recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
        .value

    actual.map(Assertions.assertEquals(_, expected))
  }

  test("test CanRecover[IO].recoverEitherTFromNonFatal should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]
    val actual          =
      CanRecover[IO]
        .recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
        .value

    actual.map(Assertions.assertEquals(_, expected))
  }

}
