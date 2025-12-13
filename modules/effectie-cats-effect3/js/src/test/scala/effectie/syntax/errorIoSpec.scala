package effectie.syntax

import cats.data.EitherT
import cats.effect.IO
import cats.syntax.all._
import effectie.core.FxCtor
import effectie.instances.ce3.fx.ioFx
import effectie.syntax.error._
import effectie.syntax.fx._
import effectie.testing.types.SomeError
import munit.Assertions

import scala.util.control.NonFatal

/** @author Kevin Lee
  * @since 2021-10-30
  */
class errorIoSpec
    extends CommonErrorIoSpec
    with CanCatchIoSyntaxSpec
    with CanHandleErrorIoSyntaxSpec
    with CanRecoverIoSyntaxSpec
    with OnNonFatalIoSyntaxSpec
trait CommonErrorIoSpec extends munit.CatsEffectSuite {

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A = throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor, A](a: => A): F[A] = effectOf[F](a)

}
trait CanCatchIoSyntaxSpec extends CommonErrorIoSpec {

  test("test CanCatch[IO].catchNonFatalThrowable should catch NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = expectedExpcetion.asLeft[Int]

    fa.catchNonFatalThrowable
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

//  test("test CanCatch[IO].catchNonFatalThrowable should not catch Fatal") {
//    val fatalExpcetion = SomeControlThrowable("Something's wrong")
//    val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))
//
//    try {
//      fa.catchNonFatalThrowable
//        .map { actual =>
//          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
//        }
//    } catch {
//      case ex: SomeControlThrowable =>
//        Assertions.assertEquals(ex.getMessage, fatalExpcetion.getMessage)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//  }

  test("test CanCatch[IO].catchNonFatalThrowable should return the successful result") {
    val fa       = run[IO, Int](1)
    val expected = 1.asRight[Throwable]

    fa.catchNonFatalThrowable
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanCatch[IO].catchNonFatal should catch NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    fa.catchNonFatal(SomeError.someThrowable)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

//  test("test CanCatch[IO].catchNonFatal should not catch Fatal") {
//    val fatalExpcetion = SomeControlThrowable("Something's wrong")
//    val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))
//
//    try {
//      fa.catchNonFatal(SomeError.someThrowable)
//        .map { actual =>
//          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
//        }
//    } catch {
//      case ex: SomeControlThrowable =>
//        Assertions.assertEquals(ex.getMessage, fatalExpcetion.getMessage)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//
//  }

  test("test CanCatch[IO].catchNonFatal should return the successful result") {
    val fa       = run[IO, Int](1)
    val expected = 1.asRight[SomeError]

    fa.catchNonFatal(SomeError.someThrowable)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanCatch[IO].catchNonFatalEither should catch NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    fa.catchNonFatalEither(SomeError.someThrowable)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

//  test("test CanCatch[IO].catchNonFatalEither should not catch Fatal") {
//    val fatalExpcetion = SomeControlThrowable("Something's wrong")
//    val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))
//
//    try {
//      fa.catchNonFatalEither(SomeError.someThrowable)
//        .map { actual =>
//          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
//        }
//    } catch {
//      case ex: SomeControlThrowable =>
//        Assertions.assertEquals(ex.getMessage, fatalExpcetion.getMessage)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//  }

  test("test CanCatch[IO].catchNonFatalEither should return the successful result") {
    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    fa.catchNonFatalEither(SomeError.someThrowable)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanCatch[IO].catchNonFatalEither should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    fa.catchNonFatalEither(SomeError.someThrowable)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanCatch[IO].catchNonFatalEitherT should catch NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa       = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    fa.catchNonFatalEitherT(SomeError.someThrowable)
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

//  test("test CanCatch[IO].catchNonFatalEitherT should not catch Fatal") {
//    val fatalExpcetion = SomeControlThrowable("Something's wrong")
//    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))
//
//    try {
//      fa.catchNonFatalEitherT(SomeError.someThrowable)
//        .value
//        .map { actual =>
//          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
//        }
//    } catch {
//      case ex: SomeControlThrowable =>
//        Assertions.assertEquals(ex.getMessage, fatalExpcetion.getMessage)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//  }

  test("test CanCatch[IO].catchNonFatalEitherT should return the successful result") {
    val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    fa.catchNonFatalEitherT(SomeError.someThrowable)
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanCatch[IO].catchNonFatalEitherT should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    fa.catchNonFatalEitherT(SomeError.someThrowable)
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

}

trait CanHandleErrorIoSyntaxSpec extends CommonErrorIoSpec {

  test("test CanHandleError[IO].handleNonFatalWith should handle NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 123

    fa.handleNonFatalWith {
      case NonFatal(`expectedExpcetion`) =>
        IO.pure(expected)
      case err =>
        throw err // scalafix:ok DisableSyntax.throw
    }.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

//  test("test CanHandleError[IO].handleNonFatalWith should not handle Fatal") {
//    val fatalExpcetion = SomeControlThrowable("Something's wrong")
//    val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))
//
//    try {
//      fa.handleNonFatalWith(_ => IO.pure(123))
//        .map { actual =>
//          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
//        }
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

    fa.handleNonFatalWith(_ => IO.pure(999))
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanHandleError[IO].handleNonFatalWith(IO[Either]) should handle NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
    val actualFailedResult   =
      fa.handleNonFatalWith(_ => IO.pure(expectedFailedResult))
        .map { actual =>
          Assertions.assertEquals(actual, expectedFailedResult)
        }

    val expectedSuccessResult = 1.asRight[SomeError]
    val actualSuccessResult   =
      fa.handleNonFatalWith(_ => IO.pure(1.asRight[SomeError]))
        .map { actual =>
          Assertions.assertEquals(actual, expectedSuccessResult)
        }

    actualFailedResult *> actualSuccessResult
  }

//  test("test CanHandleError[IO].handleNonFatalWith(IO[Either]) should not handle Fatal") {
//    val fatalExpcetion = SomeControlThrowable("Something's wrong")
//    val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))
//
//    try {
//      fa.handleNonFatalWith(_ => IO.pure(123.asRight[SomeError]))
//        .map { actual =>
//          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
//        }
//    } catch {
//      case ex: SomeControlThrowable =>
//        Assertions.assertEquals(ex.getMessage, fatalExpcetion.getMessage)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//
//  }

  test("test CanHandleError[IO].handleNonFatalWith(IO[Either]) should return the successful result") {
    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    fa.handleNonFatalWith(_ => IO(999.asRight[SomeError]))
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanHandleError[IO].handleNonFatalWith(IO[Either]) should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    fa.handleNonFatalWith(_ => IO.pure(123.asRight[SomeError]))
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanHandleError[IO].handleEitherNonFatalWith should handle NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   = fa
      .handleEitherNonFatalWith(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      fa.handleEitherNonFatalWith(_ => IO.pure(123.asRight[SomeError]))
        .map { actual =>
          Assertions.assertEquals(actual, expectedSuccessResult)
        }

    actualFailedResult *> actualSuccessResult
  }

//  test("test CanHandleError[IO].handleEitherNonFatalWith should not handle Fatal") {
//    val fatalExpcetion = SomeControlThrowable("Something's wrong")
//    val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))
//
//    try {
//      fa.handleEitherNonFatalWith(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
//        .map { actual =>
//          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
//        }
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

    fa.handleEitherNonFatalWith(_ => IO.pure(123.asRight[SomeError]))
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanHandleError[IO].handleEitherNonFatalWith should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    fa.handleEitherNonFatalWith(_ => IO.pure(123.asRight[SomeError]))
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanHandleError[IO].handleEitherTNonFatalWith should handle NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   = fa
      .handleEitherTNonFatalWith(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      fa.handleEitherTNonFatalWith(_ => IO.pure(123.asRight[SomeError]))
        .value
        .map { actual =>
          Assertions.assertEquals(actual, expectedSuccessResult)
        }

    actualFailedResult *> actualSuccessResult
  }

//  test("test CanHandleError[IO].handleEitherTNonFatalWith should not handle Fatal") {
//    val fatalExpcetion = SomeControlThrowable("Something's wrong")
//    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))
//
//    try {
//      fa.handleEitherTNonFatalWith(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
//        .value
//        .map { actual =>
//          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
//        }
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

    fa.handleEitherTNonFatalWith(_ => IO.pure(123.asRight[SomeError]))
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanHandleError[IO].handleEitherTNonFatalWith should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    fa.handleEitherTNonFatalWith(_ => IO.pure(123.asRight[SomeError]))
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanHandleError[IO].handleNonFatal should handle NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 123

    fa.handleNonFatal {
      case NonFatal(`expectedExpcetion`) =>
        expected
      case err =>
        throw err // scalafix:ok DisableSyntax.throw
    }.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

//  test("test CanHandleError[IO].handleNonFatal should not handle Fatal") {
//    val fatalExpcetion = SomeControlThrowable("Something's wrong")
//    val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))
//
//    try {
//      fa.handleNonFatal(_ => 123)
//        .map { actual =>
//          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
//        }
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

    fa.handleNonFatal(_ => 999)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanHandleError[IO].handleNonFatalEither should handle NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
    val actualFailedResult   = fa
      .handleNonFatal(_ => expectedFailedResult)
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }

    val expectedSuccessResult = 1.asRight[SomeError]
    val actualSuccessResult   = fa
      .handleNonFatal(_ => 1.asRight[SomeError])
      .map { actual =>
        Assertions.assertEquals(actual, expectedSuccessResult)
      }

    actualFailedResult *> actualSuccessResult
  }

//  test("test CanHandleError[IO].handleNonFatalEither should not handle Fatal") {
//    val fatalExpcetion = SomeControlThrowable("Something's wrong")
//    val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))
//
//    try {
//      fa.handleNonFatal(_ => 123.asRight[SomeError])
//        .map { actual =>
//          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
//        }
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

    fa.handleNonFatal(_ => 999.asRight[SomeError])
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanHandleError[IO].handleNonFatalEither should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    fa.handleNonFatal(_ => 123.asRight[SomeError])
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanHandleError[IO].handleEitherNonFatal should handle NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   = fa
      .handleEitherNonFatal(err => SomeError.someThrowable(err).asLeft[Int])
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      fa.handleEitherNonFatal(_ => 123.asRight[SomeError])
        .map { actual =>
          Assertions.assertEquals(actual, expectedSuccessResult)
        }

    actualFailedResult *> actualSuccessResult
  }

//  test("test CanHandleError[IO].handleEitherNonFatal should not handle Fatal") {
//    val fatalExpcetion = SomeControlThrowable("Something's wrong")
//    val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))
//
//    try {
//      fa.handleEitherNonFatal(err => SomeError.someThrowable(err).asLeft[Int])
//        .map { actual =>
//          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
//        }
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

    fa.handleEitherNonFatal(_ => 123.asRight[SomeError])
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanHandleError[IO].handleEitherNonFatal should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    fa.handleEitherNonFatal(_ => 123.asRight[SomeError])
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanHandleError[IO].handleEitherTNonFatal should handle NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   = fa
      .handleEitherTNonFatal(err => SomeError.someThrowable(err).asLeft[Int])
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      fa.handleEitherTNonFatal(_ => 123.asRight[SomeError])
        .value
        .map { actual =>
          Assertions.assertEquals(actual, expectedSuccessResult)
        }

    actualFailedResult *> actualSuccessResult
  }

//  test("test CanHandleError[IO].handleEitherTNonFatal should not handle Fatal") {
//    val fatalExpcetion = SomeControlThrowable("Something's wrong")
//    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))
//
//    try {
//      fa.handleEitherTNonFatal(err => SomeError.someThrowable(err).asLeft[Int])
//        .value
//        .map { actual =>
//          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
//        }
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

    fa.handleEitherTNonFatal(_ => 123.asRight[SomeError])
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanHandleError[IO].handleEitherTNonFatal should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    fa.handleEitherTNonFatal(_ => 123.asRight[SomeError])
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

}

trait CanRecoverIoSyntaxSpec extends CommonErrorIoSpec {

  test("test CanRecover[IO].recoverFromNonFatalWith should catch NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 123

    fa
      .recoverFromNonFatalWith {
        case NonFatal(`expectedExpcetion`) =>
          IO.pure(expected)
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

//  test("test CanRecover[IO].recoverFromNonFatalWith should not catch Fatal") {
//    val expectedExpcetion = SomeControlThrowable("Something's wrong")
//    val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
//
//    val io = fa.recoverFromNonFatalWith { case NonFatal(`expectedExpcetion`) => IO.pure(123) }
//    try {
//      io
//        .map { actual =>
//          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
//        }
//    } catch {
//      case ex: ControlThrowable =>
//        Assertions.assertEquals(ex, expectedExpcetion)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//
//  }

  test("test CanRecover[IO].recoverFromNonFatalWith should return the successful result") {
    val fa       = run[IO, Int](1)
    val expected = 1

    fa
      .recoverFromNonFatalWith {
        case NonFatal(_) => IO.pure(999)
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanRecover[IO].recoverFromNonFatalWithEither should catch NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
    val actualFailedResult   = fa
      .recoverFromNonFatalWith {
        case NonFatal(`expectedExpcetion`) => IO.pure(expectedFailedResult)
      }
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }

    val expectedSuccessResult = 1.asRight[SomeError]
    val actualSuccessResult   = fa
      .recoverFromNonFatalWith {
        case NonFatal(`expectedExpcetion`) => IO.pure(1.asRight[SomeError])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expectedSuccessResult)
      }

    actualFailedResult *> actualSuccessResult
  }

//  test("test CanRecover[IO].recoverFromNonFatalWithEither should not catch Fatal") {
//    val expectedExpcetion = SomeControlThrowable("Something's wrong")
//    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
//
//    val io = fa.recoverFromNonFatalWith {
//      case NonFatal(`expectedExpcetion`) => IO.pure(123.asRight[SomeError])
//    }
//    try {
//      io.map { actual =>
//        Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
//      }
//    } catch {
//      case ex: ControlThrowable =>
//        Assertions.assertEquals(ex, expectedExpcetion)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//  }

  test("test CanRecover[IO].recoverFromNonFatalWithEither should return the successful result") {
    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    fa
      .recoverFromNonFatalWith {
        case NonFatal(_) => IO(999.asRight[SomeError])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanRecover[IO].recoverFromNonFatalWithEither should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    fa
      .recoverFromNonFatalWith {
        case NonFatal(_) => IO.pure(123.asRight[SomeError])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanRecover[IO].recoverEitherFromNonFatalWith should catch NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   = fa
      .recoverEitherFromNonFatalWith {
        case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   = fa
      .recoverEitherFromNonFatalWith {
        case NonFatal(`expectedExpcetion`) => IO.pure(123.asRight[SomeError])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expectedSuccessResult)
      }

    actualFailedResult *> actualSuccessResult
  }

//  test("test CanRecover[IO].recoverEitherFromNonFatalWith should not catch Fatal") {
//    val expectedExpcetion = SomeControlThrowable("Something's wrong")
//    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
//
//    val io = fa.recoverEitherFromNonFatalWith {
//      case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
//    }
//    try {
//      io.map { actual =>
//        Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
//      }
//    } catch {
//      case ex: ControlThrowable =>
//        Assertions.assertEquals(ex, expectedExpcetion)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//  }

  test("test CanRecover[IO].recoverEitherFromNonFatalWith should return the successful result") {
    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    fa
      .recoverEitherFromNonFatalWith {
        case NonFatal(_) => IO.pure(123.asRight[SomeError])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanRecover[IO].recoverEitherFromNonFatalWith should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    fa
      .recoverEitherFromNonFatalWith {
        case NonFatal(_) => IO.pure(123.asRight[SomeError])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanRecover[IO].recoverEitherTFromNonFatalWith should catch NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   = fa
      .recoverEitherTFromNonFatalWith {
        case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
      }
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   = fa
      .recoverEitherTFromNonFatalWith {
        case NonFatal(`expectedExpcetion`) => IO.pure(123.asRight[SomeError])
      }
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expectedSuccessResult)
      }

    actualFailedResult *> actualSuccessResult
  }

//  test("test CanRecover[IO].recoverEitherTFromNonFatalWith should not catch Fatal") {
//    val expectedExpcetion = SomeControlThrowable("Something's wrong")
//    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
//
//    val io = fa.recoverEitherTFromNonFatalWith {
//      case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
//    }
//    try {
//      io.value
//        .map { actual =>
//          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
//        }
//    } catch {
//      case ex: ControlThrowable =>
//        Assertions.assertEquals(ex, expectedExpcetion)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//  }

  test("test CanRecover[IO].recoverEitherTFromNonFatalWith should return the successful result") {
    val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    fa
      .recoverEitherTFromNonFatalWith {
        case NonFatal(_) => IO.pure(123.asRight[SomeError])
      }
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanRecover[IO].recoverEitherTFromNonFatalWith should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    fa
      .recoverEitherTFromNonFatalWith {
        case NonFatal(_) => IO.pure(123.asRight[SomeError])
      }
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  // /

  test("test CanRecover[IO].recoverFromNonFatal should catch NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 123

    fa
      .recoverFromNonFatal {
        case NonFatal(`expectedExpcetion`) =>
          expected
      }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

//  test("test CanRecover[IO].recoverFromNonFatal should not catch Fatal") {
//    val expectedExpcetion = SomeControlThrowable("Something's wrong")
//    val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
//
//    val io = fa.recoverFromNonFatal { case NonFatal(`expectedExpcetion`) => 123 }
//    try {
//      io.map { actual =>
//        Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
//      }
//    } catch {
//      case ex: ControlThrowable =>
//        Assertions.assertEquals(ex, expectedExpcetion)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//  }

  test("test CanRecover[IO].recoverFromNonFatal should return the successful result") {
    val fa       = run[IO, Int](1)
    val expected = 1

    fa
      .recoverFromNonFatal { case NonFatal(_) => 999 }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanRecover[IO].recoverFromNonFatalEither should catch NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
    val actualFailedResult   = fa
      .recoverFromNonFatal { case NonFatal(`expectedExpcetion`) => expectedFailedResult }
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }

    val expectedSuccessResult = 1.asRight[SomeError]
    val actualSuccessResult   = fa
      .recoverFromNonFatal { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }
      .map { actual =>
        Assertions.assertEquals(actual, expectedSuccessResult)
      }

    actualFailedResult *> actualSuccessResult
  }

//  test("test CanRecover[IO].recoverFromNonFatalEither should not catch Fatal") {
//    val expectedExpcetion = SomeControlThrowable("Something's wrong")
//    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
//
//    val io = fa.recoverFromNonFatal { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }
//    try {
//      io.map { actual =>
//        Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
//      }
//    } catch {
//      case ex: ControlThrowable =>
//        Assertions.assertEquals(ex, expectedExpcetion)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//  }

  test("test CanRecover[IO].recoverFromNonFatalEither should return the successful result") {
    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    fa
      .recoverFromNonFatal { case NonFatal(_) => 999.asRight[SomeError] }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanRecover[IO].recoverFromNonFatalEither should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    fa
      .recoverFromNonFatal { case NonFatal(_) => 123.asRight[SomeError] }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanRecover[IO].recoverEitherFromNonFatal should catch NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   =
      fa.recoverEitherFromNonFatal {
        case err => SomeError.someThrowable(err).asLeft[Int]
      }.map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      fa.recoverEitherFromNonFatal { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }
        .map { actual =>
          Assertions.assertEquals(actual, expectedSuccessResult)
        }

    actualFailedResult *> actualSuccessResult
  }

//  test("test CanRecover[IO].recoverEitherFromNonFatal should not catch Fatal") {
//    val expectedExpcetion = SomeControlThrowable("Something's wrong")
//    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
//
//    val io =
//      fa.recoverEitherFromNonFatal {
//        case err => SomeError.someThrowable(err).asLeft[Int]
//      }
//    try {
//      io.map { actual =>
//        Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
//      }
//    } catch {
//      case ex: ControlThrowable =>
//        Assertions.assertEquals(ex, expectedExpcetion)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//  }

  test("test CanRecover[IO].recoverEitherFromNonFatal should return the successful result") {
    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    fa.recoverEitherFromNonFatal { case NonFatal(_) => 123.asRight[SomeError] }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanRecover[IO].recoverEitherFromNonFatal should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    fa.recoverEitherFromNonFatal { case NonFatal(_) => 123.asRight[SomeError] }
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanRecover[IO].recoverEitherTFromNonFatal should catch NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   =
      fa.recoverEitherTFromNonFatal {
        case err => SomeError.someThrowable(err).asLeft[Int]
      }.value
        .map { actual =>
          Assertions.assertEquals(actual, expectedFailedResult)
        }

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      fa.recoverEitherTFromNonFatal { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }
        .value
        .map { actual =>
          Assertions.assertEquals(actual, expectedSuccessResult)
        }

    actualFailedResult *> actualSuccessResult
  }

//  test("test CanRecover[IO].recoverEitherTFromNonFatal should not catch Fatal") {
//    val expectedExpcetion = SomeControlThrowable("Something's wrong")
//    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
//
//    val io =
//      fa.recoverEitherTFromNonFatal {
//        case err => SomeError.someThrowable(err).asLeft[Int]
//      }
//    try {
//      io.value
//        .map { actual =>
//          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
//        }
//    } catch {
//      case ex: ControlThrowable =>
//        Assertions.assertEquals(ex, expectedExpcetion)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//  }

  test("test CanRecover[IO].recoverEitherTFromNonFatal should return the successful result") {
    val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    fa.recoverEitherTFromNonFatal { case NonFatal(_) => 123.asRight[SomeError] }
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanRecover[IO].recoverEitherTFromNonFatal should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    fa.recoverEitherTFromNonFatal { case NonFatal(_) => 123.asRight[SomeError] }
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

}

trait OnNonFatalIoSyntaxSpec extends CommonErrorIoSpec {

  test("test OnNonFatal[IO].onNonFatalWith should do something for NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 123.some
    var actual            = none[Int] // scalafix:ok DisableSyntax.var

    fa.onNonFatalWith {
      case NonFatal(`expectedExpcetion`) =>
        IO.delay {
          actual = expected
        } *> IO.unit
    }.map { actual =>
      Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}"): Unit
    }.recover {
      case NonFatal(`expectedExpcetion`) =>
        Assertions.assertEquals(actual, expected)
    }

  }

//  test("test OnNonFatal[IO].onNonFatalWith should not do anything for Fatal") {
//
//    val expectedExpcetion = SomeControlThrowable("Something's wrong")
//    val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
//    var actual            = none[Int] // scalafix:ok DisableSyntax.var
//
//    try {
//      fa.onNonFatalWith {
//        case NonFatal(`expectedExpcetion`) =>
//          IO.delay {
//            actual = 123.some
//            ()
//          } *> IO.unit
//      }.map { actual =>
//        Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
//      }
//    } catch {
//      case ex: ControlThrowable =>
//        Assertions.assertEquals(ex, expectedExpcetion)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//
//  }

  test("test OnNonFatal[IO].onNonFatalWith should not do anything for the successful result") {

    val expectedResult = 999
    val fa             = run[IO, Int](expectedResult)

    val expected = none[Int]
    var actual   = none[Int] // scalafix:ok DisableSyntax.var

    fa.onNonFatalWith {
      case NonFatal(_) =>
        IO.delay {
          actual = 123.some
        } *> IO.unit
    }.map { actualResult =>
      Assertions.assertEquals(actualResult, expectedResult)
      Assertions.assertEquals(actual, expected)
    }

  }

  /////

  test("test IO[Either[A, B]].onNonFatalWith should do something for NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val expectedResult    = expectedExpcetion
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

    val expected = 123.some
    var actual   = none[Int] // scalafix:ok DisableSyntax.var

    fa.onNonFatalWith {
      case NonFatal(`expectedExpcetion`) =>
        IO.delay {
          actual = expected
        } *> IO.unit
    }.map { r =>
      Assertions.fail(s"Should have thrown an exception, but it was ${r.toString}.")
    }.recover {
      case actualFailedResult: RuntimeException =>
        Assertions.assertEquals(actual, expected)
        Assertions.assertEquals(actualFailedResult, expectedResult)
    }

  }

  test("test IO[Either[A, B]].onNonFatalWith should do nothing for success case with Right") {

    val expectedValue  = 1
    val expectedResult = expectedValue.asRight[SomeError]
    val fa             = run[IO, Either[SomeError, Int]](expectedResult)

    val expected = none[Int]
    var actual   = none[Int] // scalafix:ok DisableSyntax.var

    fa.onNonFatalWith {
      case NonFatal(_) =>
        IO.delay {
          actual = 123.some
        } *> IO.unit
    }.map { actualResult =>
      Assertions.assertEquals(actual, expected)
      Assertions.assertEquals(actualResult, expectedResult)
    }.recover {
      case ex: Throwable =>
        Assertions.fail(s"Should not have thrown an exception, but it was ${ex.toString}.")
    }

  }

  test("test IO[Either[A, B]].onNonFatalWith should do nothing for success case with Left") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val expectedResult    = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    val fa = run[IO, Int](throwThrowable(expectedExpcetion))
      .catchNonFatal {
        case err =>
          SomeError.someThrowable(err)
      }

    val expected = none[Int]
    var actual   = none[Int] // scalafix:ok DisableSyntax.var

    fa.onNonFatalWith {
      case NonFatal(`expectedExpcetion`) =>
        IO.delay {
          actual = 123.some
        } *> IO.unit
    }.map { actualResult =>
      Assertions.assertEquals(actual, expected)
      Assertions.assertEquals(actualResult, expectedResult)

    }.recover {
      case ex: Throwable =>
        Assertions.fail(s"Should not have thrown an exception, but it was ${ex.toString}.")
    }

  }

  /////////

  test("test EitherT[F, A, B].onNonFatalWith should do something for NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val expectedResult    = expectedExpcetion
    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))

    val expected = 123.some
    var actual   = none[Int] // scalafix:ok DisableSyntax.var

    fa.onNonFatalWith {
      case NonFatal(`expectedExpcetion`) =>
        IO.delay {
          actual = expected
        } *> IO.unit
    }.value
      .map { r =>
        Assertions.fail(s"Should have thrown an exception, but it was ${r.toString}.")
      }
      .recover {
        case actualFailedResult: RuntimeException =>
          Assertions.assertEquals(actual, expected)
          Assertions.assertEquals(actualFailedResult, expectedResult)
      }

  }

  test("test EitherT[F, A, B](F(Right(b))).onNonFatalWith should do nothing for success case with Right") {

    val expectedValue  = 1
    val expectedResult = expectedValue.asRight[SomeError]
    val fa             = EitherT(run[IO, Either[SomeError, Int]](expectedResult))

    val expected = none[Int]
    var actual   = none[Int] // scalafix:ok DisableSyntax.var

    fa.onNonFatalWith {
      case NonFatal(_) =>
        IO.delay {
          actual = 123.some
        } *> IO.unit
    }.value
      .map { actualResult =>
        Assertions.assertEquals(actual, expected)
        Assertions.assertEquals(actualResult, expectedResult)
      }
      .recover {
        case ex: Throwable =>
          Assertions.fail(s"Should not have thrown an exception, but it was ${ex.toString}.")
      }

  }

  test("test EitherT[F, A, B](F(Left(a))).onNonFatalWith should do nothing for success case with Left") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val expectedResult    = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    val fa = EitherT(
      run[IO, Int](throwThrowable(expectedExpcetion))
        .catchNonFatal {
          case err => SomeError.someThrowable(err)
        }
    )

    val expected = none[Int]
    var actual   = none[Int] // scalafix:ok DisableSyntax.var

    fa.onNonFatalWith {
      case NonFatal(`expectedExpcetion`) =>
        IO.delay {
          actual = 123.some
        } *> IO.unit
    }.value
      .map { actualResult =>
        Assertions.assertEquals(actual, expected)
        Assertions.assertEquals(actualResult, expectedResult)
      }
      .recover {
        case ex: Throwable =>
          throw new AssertionError(
            s"Should not have thrown an exception, but it was ${ex.toString}."
          ) // scalafix:ok DisableSyntax.throw
      }

  }

}
