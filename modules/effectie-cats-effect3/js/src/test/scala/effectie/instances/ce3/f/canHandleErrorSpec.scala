package effectie.instances.ce3.f

import canHandleError._
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
class canHandleErrorSpec extends munit.CatsEffectSuite {

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A = throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor, A](a: => A): F[A] = effectOf[F](a)

  test("test CanHandleError[IO].handleNonFatalWith should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 123
    val actual            = CanHandleError[IO]
      .handleNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) =>
          IO.pure(expected)
        case err =>
          throw err // scalafix:ok DisableSyntax.throw
      }

    actual.map(Assertions.assertEquals(_, expected))
  }

//  test("test CanHandleError[IO].handleNonFatalWith should not handle Fatal") {
//
//    val fatalExpcetion = SomeControlThrowable("Something's wrong")
//    val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))
//
//    try {
//      CanHandleError[IO]
//        .handleNonFatalWith(fa)(_ => IO.pure(123))
//        .map(actual => Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}"))
//    } catch {
//      case ex: ControlThrowable =>
//        Assertions.assertEquals(ex, fatalExpcetion)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//
//  }

  test("test CanHandleError[IO].handleNonFatalWith should return the successful result") {

    val fa       = run[IO, Int](1)
    val expected = 1
    val actual   = CanHandleError[IO].handleNonFatalWith(fa)(_ => IO.pure(999))

    actual.map(Assertions.assertEquals(_, expected))
  }

  test("test CanHandleError[IO].handleNonFatalWithEither should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
    val actualFailedResult   =
      CanHandleError[IO].handleNonFatalWith(fa)(_ => IO.pure(expectedFailedResult))

    val expectedSuccessResult = 1.asRight[SomeError]
    val actualSuccessResult   =
      CanHandleError[IO].handleNonFatalWith(fa)(_ => IO.pure(1.asRight[SomeError]))

    List(
      actualFailedResult.map(Assertions.assertEquals(_, expectedFailedResult)),
      actualSuccessResult.map(Assertions.assertEquals(_, expectedSuccessResult)),
    ).sequence
  }

//  test("test CanHandleError[IO].handleNonFatalWithEither should not handle Fatal") {
//
//    val fatalExpcetion = SomeControlThrowable("Something's wrong")
//    val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))
//
//    try {
//      CanHandleError[IO]
//        .handleNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError]))
//        .map(actual => Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}"))
//    } catch {
//      case ex: SomeControlThrowable =>
//        Assertions.assertEquals(ex.getMessage, fatalExpcetion.getMessage)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//
//  }

  test("test CanHandleError[IO].handleNonFatalWithEither should return the successful result") {

    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    val actual   = CanHandleError[IO].handleNonFatalWith(fa)(_ => IO(999.asRight[SomeError]))

    actual.map(Assertions.assertEquals(_, expected))
  }

  test("test CanHandleError[IO].handleNonFatalWithEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    val actual          = CanHandleError[IO].handleNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError]))

    actual.map(Assertions.assertEquals(_, expected))
  }

  test("test CanHandleError[IO].handleEitherNonFatalWith should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   = CanHandleError[IO]
      .handleEitherNonFatalWith(fa)(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      CanHandleError[IO].handleEitherNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError]))

    List(
      actualFailedResult.map(Assertions.assertEquals(_, expectedFailedResult)),
      actualSuccessResult.map(Assertions.assertEquals(_, expectedSuccessResult)),
    ).sequence
  }

//  test("test CanHandleError[IO].handleEitherNonFatalWith should not handle Fatal") {
//
//    val fatalExpcetion = SomeControlThrowable("Something's wrong")
//    val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))
//
//    try {
//      CanHandleError[IO]
//        .handleEitherNonFatalWith(fa)(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
//        .map(actual => Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}"))
//    } catch {
//      case ex: ControlThrowable =>
//        Assertions.assertEquals(ex, fatalExpcetion)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//
//  }

  test("test CanHandleError[IO].handleEitherNonFatalWith should return the successful result") {

    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    val actual   =
      CanHandleError[IO].handleEitherNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError]))

    actual.map(Assertions.assertEquals(_, expected))
  }

  test("test CanHandleError[IO].handleEitherNonFatalWith should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    val actual          =
      CanHandleError[IO].handleEitherNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError]))

    actual.map(Assertions.assertEquals(_, expected))
  }

  test("test CanHandleError[IO].handleEitherTNonFatalWith should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   = CanHandleError[IO]
      .handleEitherTNonFatalWith(fa)(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
      .value

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      CanHandleError[IO].handleEitherTNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).value

    List(
      actualFailedResult.map(Assertions.assertEquals(_, expectedFailedResult)),
      actualSuccessResult.map(Assertions.assertEquals(_, expectedSuccessResult)),
    ).sequence
  }

//  test("test CanHandleError[IO].handleEitherTNonFatalWith should not handle Fatal") {
//
//    val fatalExpcetion = SomeControlThrowable("Something's wrong")
//    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))
//
//    try {
//      CanHandleError[IO]
//        .handleEitherTNonFatalWith(fa)(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
//        .value
//        .map(actual => Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}"))
//    } catch {
//      case ex: ControlThrowable =>
//        Assertions.assertEquals(ex, fatalExpcetion)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//
//  }

  test("test CanHandleError[IO].handleEitherTNonFatalWith should return the successful result") {

    val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]
    val actual   =
      CanHandleError[IO].handleEitherTNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).value

    actual.map(Assertions.assertEquals(_, expected))
  }

  test("test CanHandleError[IO].handleEitherTNonFatalWith should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]
    val actual          =
      CanHandleError[IO].handleEitherTNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).value

    actual.map(Assertions.assertEquals(_, expected))
  }

  test("test CanHandleError[IO].handleNonFatal should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 123
    val actual            = CanHandleError[IO]
      .handleNonFatal(fa) {
        case NonFatal(`expectedExpcetion`) =>
          expected
        case err =>
          throw err // scalafix:ok DisableSyntax.throw
      }

    actual.map(Assertions.assertEquals(_, expected))
  }

//  test("test CanHandleError[IO].handleNonFatal should not handle Fatal") {
//
//    val fatalExpcetion = SomeControlThrowable("Something's wrong")
//    val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))
//
//    try {
//      CanHandleError[IO]
//        .handleNonFatal(fa)(_ => 123)
//        .map(actual => Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}"))
//    } catch {
//      case ex: ControlThrowable =>
//        Assertions.assertEquals(ex, fatalExpcetion)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//
//  }

  test("test CanHandleError[IO].handleNonFatal should return the successful result") {

    val fa       = run[IO, Int](1)
    val expected = 1
    val actual   = CanHandleError[IO].handleNonFatal(fa)(_ => 999)

    actual.map(Assertions.assertEquals(_, expected))
  }

  test("test CanHandleError[IO].handleNonFatalEither should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
    val actualFailedResult   = CanHandleError[IO].handleNonFatal(fa)(_ => expectedFailedResult)

    val expectedSuccessResult = 1.asRight[SomeError]
    val actualSuccessResult   = CanHandleError[IO].handleNonFatal(fa)(_ => 1.asRight[SomeError])

    List(
      actualFailedResult.map(Assertions.assertEquals(_, expectedFailedResult)),
      actualSuccessResult.map(Assertions.assertEquals(_, expectedSuccessResult)),
    ).sequence
  }

//  test("test CanHandleError[IO].handleNonFatalEither should not handle Fatal") {
//
//    val fatalExpcetion = SomeControlThrowable("Something's wrong")
//    val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))
//
//    try {
//      CanHandleError[IO]
//        .handleNonFatal(fa)(_ => 123.asRight[SomeError])
//        .map(actual => Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}"))
//    } catch {
//      case ex: ControlThrowable =>
//        Assertions.assertEquals(ex, fatalExpcetion)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//
//  }

  test("test CanHandleError[IO].handleNonFatalEither should return the successful result") {

    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    val actual   = CanHandleError[IO].handleNonFatal(fa)(_ => 999.asRight[SomeError])

    actual.map(Assertions.assertEquals(_, expected))
  }

  test("test CanHandleError[IO].handleNonFatalEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    val actual          = CanHandleError[IO].handleNonFatal(fa)(_ => 123.asRight[SomeError])

    actual.map(Assertions.assertEquals(_, expected))
  }

  test("test CanHandleError[IO].handleEitherNonFatal should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   = CanHandleError[IO]
      .handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      CanHandleError[IO].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError])

    List(
      actualFailedResult.map(Assertions.assertEquals(_, expectedFailedResult)),
      actualSuccessResult.map(Assertions.assertEquals(_, expectedSuccessResult)),
    ).sequence
  }

//  test("test CanHandleError[IO].handleEitherNonFatal should not handle Fatal") {
//
//    val fatalExpcetion = SomeControlThrowable("Something's wrong")
//    val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))
//
//    try {
//      CanHandleError[IO]
//        .handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
//        .map(actual => Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}"))
//    } catch {
//      case ex: ControlThrowable =>
//        Assertions.assertEquals(ex, fatalExpcetion)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//
//  }

  test("test CanHandleError[IO].handleEitherNonFatal should return the successful result") {

    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    val actual   = CanHandleError[IO].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError])

    actual.map(Assertions.assertEquals(_, expected))
  }

  test("test CanHandleError[IO].handleEitherNonFatal should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    val actual          =
      CanHandleError[IO].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError])

    actual.map(Assertions.assertEquals(_, expected))
  }

  test("test CanHandleError[IO].handleEitherTNonFatal should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   = CanHandleError[IO]
      .handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
      .value

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      CanHandleError[IO].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value

    List(
      actualFailedResult.map(Assertions.assertEquals(_, expectedFailedResult)),
      actualSuccessResult.map(Assertions.assertEquals(_, expectedSuccessResult)),
    ).sequence
  }

//  test("test CanHandleError[IO].handleEitherTNonFatal should not handle Fatal") {
//
//    val fatalExpcetion = SomeControlThrowable("Something's wrong")
//    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))
//
//    try {
//      CanHandleError[IO]
//        .handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
//        .value
//        .map(actual => Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}"))
//    } catch {
//      case ex: ControlThrowable =>
//        Assertions.assertEquals(ex, fatalExpcetion)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//
//  }

  test("test CanHandleError[IO].handleEitherTNonFatal should return the successful result") {

    val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]
    val actual   = CanHandleError[IO].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value

    actual.map(Assertions.assertEquals(_, expected))
  }

  test("test CanHandleError[IO].handleEitherTNonFatal should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]
    val actual          =
      CanHandleError[IO].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value

    actual.map(Assertions.assertEquals(_, expected))
  }

}
