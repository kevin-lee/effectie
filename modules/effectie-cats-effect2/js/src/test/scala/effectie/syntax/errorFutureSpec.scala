package effectie.syntax

import cats.data.EitherT
import cats.syntax.all._
import effectie.core.FxCtor
import effectie.syntax.error._
import effectie.syntax.fx._
import effectie.testing.FutureTools
import effectie.testing.types._
import munit.Assertions

import scala.concurrent._
import scala.concurrent.duration._
import scala.util.control.NonFatal

/** @author Kevin Lee
  * @since 2021-10-30
  */
class errorFutureSpec
    extends CommonErrorFutureSpec
    with CanCatchFutureSyntaxSpec
    with CanHandleErrorFutureSyntaxSpec
    with CanRecoverFutureSyntaxSpec
trait CommonErrorFutureSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  override val munitTimeout: FiniteDuration = 200.milliseconds

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A = throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor, A](a: => A): F[A] =
    effectOf[F](a)

}
trait CanCatchFutureSyntaxSpec extends CommonErrorFutureSpec {

  import effectie.instances.future.canCatch._
  import effectie.instances.future.fxCtor._

  test("test CanCatch[Future]catchNonFatalThrowable should catch NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = expectedExpcetion.asLeft[Int]

    fa.catchNonFatalThrowable.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanCatch[Future]catchNonFatal should catch NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    fa.catchNonFatal(SomeError.someThrowable).map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanCatch[Future]catchNonFatalThrowable should return the successful result") {
    val fa       = run[Future, Int](1)
    val expected = 1.asRight[Throwable]

    fa.catchNonFatalThrowable.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanCatch[Future]catchNonFatal should return the successful result") {
    val fa       = run[Future, Int](1)
    val expected = 1.asRight[SomeError]

    fa.catchNonFatal(SomeError.someThrowable).map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanCatch[Future]catchNonFatalEither should catch NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa       = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    fa.catchNonFatalEither(SomeError.someThrowable).map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanCatch[Future]catchNonFatalEither should return the successful result") {
    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    fa.catchNonFatalEither(SomeError.someThrowable).map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanCatch[Future]catchNonFatalEither should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    fa.catchNonFatalEither(SomeError.someThrowable).map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanCatch[Future]catchNonFatalEitherT should catch NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    fa.catchNonFatalEitherT(SomeError.someThrowable).value.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanCatch[Future]catchNonFatalEitherT should return the successful result") {
    val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    fa.catchNonFatalEitherT(SomeError.someThrowable).value.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanCatch[Future]catchNonFatalEitherT should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    fa.catchNonFatalEitherT(SomeError.someThrowable).value.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

}

trait CanHandleErrorFutureSyntaxSpec extends CommonErrorFutureSpec {

  import effectie.instances.future.canHandleError._
  import effectie.instances.future.fxCtor._

  test("test CanHandleError[Future].handleNonFatalWith should handle NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 1

    fa.handleNonFatalWith(_ => Future(expected)).map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanHandleError[Future].handleNonFatalWith should return the successful result") {
    val fa       = run[Future, Int](1)
    val expected = 1

    fa.handleNonFatalWith(_ => Future(expected)).map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanHandleError[Future].handleNonFatalWithEither should handle NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   =
      fa.handleNonFatalWith(err => Future(SomeError.someThrowable(err).asLeft[Int])).map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected = 1.asRight[SomeError]
    val actualSuccessResult =
      fa2.handleNonFatalWith(_ => Future(expected)).map { actual =>
        Assertions.assertEquals(actual, expected)
      }

    Future.sequence(List(actualFailedResult, actualSuccessResult))
  }

  test("test CanHandleError[Future].handleNonFatalWithEither should return the successful result") {
    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    fa.handleNonFatalWith(err => Future(SomeError.someThrowable(err).asLeft[Int])).map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanHandleError[Future].handleNonFatalWithEither should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    fa.handleNonFatalWith(_ => Future(1.asRight[SomeError])).map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanHandleError[Future].handleEitherNonFatalWith should handle NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   =
      fa.handleEitherNonFatalWith(err => Future(SomeError.someThrowable(err).asLeft[Int])).map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected = 1.asRight[SomeError]
    val actualSuccessResult =
      fa2.handleEitherNonFatalWith(_ => Future(expected)).map { actual =>
        Assertions.assertEquals(actual, expected)
      }

    Future.sequence(List(actualFailedResult, actualSuccessResult))
  }

  test("test CanHandleError[Future].handleEitherNonFatalWith should return the successful result") {
    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    fa.handleEitherNonFatalWith(err => Future(SomeError.someThrowable(err).asLeft[Int])).map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanHandleError[Future].handleEitherNonFatalWith should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    fa.handleEitherNonFatalWith(_ => Future(expected)).map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanHandleError[Future].handleEitherTNonFatalWith should handle NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   =
      fa.handleEitherTNonFatalWith(err => Future(SomeError.someThrowable(err).asLeft[Int])).value.map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }

    val fa2 = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expected            = 1.asRight[SomeError]
    val actualSuccessResult =
      fa2.handleEitherTNonFatalWith(_ => Future(expected)).value.map { actual =>
        Assertions.assertEquals(actual, expected)
      }

    Future.sequence(List(actualFailedResult, actualSuccessResult))
  }

  test("test CanHandleError[Future].handleEitherTNonFatalWith should return the successful result") {
    val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    fa.handleEitherTNonFatalWith(err => Future(SomeError.someThrowable(err).asLeft[Int])).value.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanHandleError[Future].handleEitherTNonFatalWith should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    fa.handleEitherTNonFatalWith(_ => Future(expected)).value.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanHandleError[Future].handleNonFatal should handle NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 1

    fa.handleNonFatal(_ => expected).map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanHandleError[Future].handleNonFatal should return the successful result") {
    val fa       = run[Future, Int](1)
    val expected = 1

    fa.handleNonFatal(_ => 123).map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanHandleError[Future].handleNonFatalEither should handle NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   =
      fa.handleNonFatal(err => SomeError.someThrowable(err).asLeft[Int]).map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected = 1.asRight[SomeError]
    val actualSuccessResult = fa2.handleNonFatal(_ => expected).map { actual =>
      Assertions.assertEquals(actual, expected)
    }

    Future.sequence(List(actualFailedResult, actualSuccessResult))
  }

  test("test CanHandleError[Future].handleNonFatalEither should return the successful result") {
    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    fa.handleNonFatal(err => SomeError.someThrowable(err).asLeft[Int]).map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanHandleError[Future].handleNonFatalEither should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    fa.handleNonFatal(_ => 1.asRight[SomeError]).map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanHandleError[Future].handleEitherNonFatal should handle NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   = fa.handleEitherNonFatal(err => SomeError.someThrowable(err).asLeft[Int]).map { actual =>
      Assertions.assertEquals(actual, expectedFailedResult)
    }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected = 1.asRight[SomeError]
    val actualSuccessResult = fa2.handleEitherNonFatal(_ => expected).map { actual =>
      Assertions.assertEquals(actual, expected)
    }

    Future.sequence(List(actualFailedResult, actualSuccessResult))
  }

  test("test CanHandleError[Future].handleEitherNonFatal should return the successful result") {
    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    fa.handleEitherNonFatal(err => SomeError.someThrowable(err).asLeft[Int]).map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanHandleError[Future].handleEitherNonFatal should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    fa.handleEitherNonFatal(_ => expected).map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanHandleError[Future].handleEitherTNonFatal should handle NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   =
      fa.handleEitherTNonFatal(err => SomeError.someThrowable(err).asLeft[Int]).value.map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }

    val fa2 = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expected            = 1.asRight[SomeError]
    val actualSuccessResult = fa2.handleEitherTNonFatal(_ => expected).value.map { actual =>
      Assertions.assertEquals(actual, expected)
    }

    Future.sequence(List(actualFailedResult, actualSuccessResult))
  }

  test("test CanHandleError[Future].handleEitherTNonFatal should return the successful result") {
    val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    fa.handleEitherTNonFatal(err => SomeError.someThrowable(err).asLeft[Int]).value.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanHandleError[Future].handleEitherTNonFatal should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    fa.handleEitherTNonFatal(_ => expected).value.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

}

trait CanRecoverFutureSyntaxSpec extends CommonErrorFutureSpec {

  import effectie.instances.future.canRecover._
  import effectie.instances.future.fxCtor._

  test("test CanRecover[Future].recoverFromNonFatalWith should catch NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 1

    fa.recoverFromNonFatalWith {
      case NonFatal(`expectedExpcetion`) => Future(expected)
    }.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanRecover[Future].recoverFromNonFatalWith should return the successful result") {
    val fa       = run[Future, Int](1)
    val expected = 1

    fa.recoverFromNonFatalWith {
      case NonFatal(_) => Future(123)
    }.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanRecover[Future].recoverFromNonFatalWithEither should catch NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   =
      fa.recoverFromNonFatalWith {
        case err => Future(SomeError.someThrowable(err).asLeft[Int])
      }.map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected = 1.asRight[SomeError]
    val actualSuccessResult =
      fa2
        .recoverFromNonFatalWith {
          case NonFatal(`expectedExpcetion`) => Future(expected)
        }
        .map { actual =>
          Assertions.assertEquals(actual, expected)
        }

    Future.sequence(List(actualFailedResult, actualSuccessResult))
  }

  test("test CanRecover[Future].recoverFromNonFatalWithEither should return the successful result") {
    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    fa.recoverFromNonFatalWith {
      case err => Future(SomeError.someThrowable(err).asLeft[Int])
    }.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanRecover[Future].recoverFromNonFatalWithEither should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    fa.recoverFromNonFatalWith {
      case NonFatal(_) => Future(1.asRight[SomeError])
    }.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanRecover[Future].recoverEitherFromNonFatalWith should catch NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   = fa
      .recoverEitherFromNonFatalWith {
        case err => Future(SomeError.someThrowable(err).asLeft[Int])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected = 1.asRight[SomeError]
    val actualSuccessResult =
      fa2
        .recoverEitherFromNonFatalWith {
          case err @ _ => Future(expected)
        }
        .map { actual =>
          Assertions.assertEquals(actual, expected)
        }

    Future.sequence(List(actualFailedResult, actualSuccessResult))
  }

  test("test CanRecover[Future].recoverEitherFromNonFatalWith should return the successful result") {
    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    fa.recoverEitherFromNonFatalWith {
      case err => Future(SomeError.someThrowable(err).asLeft[Int])
    }.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanRecover[Future].recoverEitherFromNonFatalWith should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    fa.recoverEitherFromNonFatalWith {
      case NonFatal(_) => Future(expected)
    }.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanRecover[Future].recoverEitherTFromNonFatalWith should catch NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   =
      fa.recoverEitherTFromNonFatalWith {
        case err => Future(SomeError.someThrowable(err).asLeft[Int])
      }.value
        .map { actual =>
          Assertions.assertEquals(actual, expectedFailedResult)
        }

    val fa2 = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expected            = 1.asRight[SomeError]
    val actualSuccessResult =
      fa2
        .recoverEitherTFromNonFatalWith {
          case err @ _ => Future(expected)
        }
        .value
        .map { actual =>
          Assertions.assertEquals(actual, expected)
        }

    Future.sequence(List(actualFailedResult, actualSuccessResult))
  }

  test("test CanRecover[Future].recoverEitherTFromNonFatalWith should return the successful result") {
    val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    fa.recoverEitherTFromNonFatalWith {
      case err => Future(SomeError.someThrowable(err).asLeft[Int])
    }.value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanRecover[Future].recoverEitherTFromNonFatalWith should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    fa.recoverEitherTFromNonFatalWith {
      case NonFatal(_) => Future(expected)
    }.value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  // /

  test("test CanRecover[Future].recoverFromNonFatal should catch NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 1

    fa.recoverFromNonFatal { case NonFatal(`expectedExpcetion`) => expected }.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanRecover[Future].recoverFromNonFatal should return the successful result") {
    val fa       = run[Future, Int](1)
    val expected = 1

    fa.recoverFromNonFatal { case NonFatal(_) => 123 }.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanRecover[Future].recoverFromNonFatalEither should catch NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   =
      fa.recoverFromNonFatal {
        case err => SomeError.someThrowable(err).asLeft[Int]
      }.map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected = 1.asRight[SomeError]
    val actualSuccessResult = fa2.recoverFromNonFatal { case NonFatal(`expectedExpcetion`) => expected }.map { actual =>
      Assertions.assertEquals(actual, expected)
    }

    Future.sequence(List(actualFailedResult, actualSuccessResult))
  }

  test("test CanRecover[Future].recoverFromNonFatalEither should return the successful result") {
    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    fa.recoverFromNonFatal {
      case err => SomeError.someThrowable(err).asLeft[Int]
    }.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanRecover[Future].recoverFromNonFatalEither should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    fa.recoverFromNonFatal { case NonFatal(_) => 1.asRight[SomeError] }.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanRecover[Future].recoverEitherFromNonFatal should catch NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   =
      fa.recoverEitherFromNonFatal {
        case err => SomeError.someThrowable(err).asLeft[Int]
      }.map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected = 1.asRight[SomeError]
    val actualSuccessResult =
      fa2.recoverEitherFromNonFatal { case err @ _ => expected }.map { actual =>
        Assertions.assertEquals(actual, expected)
      }

    Future.sequence(List(actualFailedResult, actualSuccessResult))
  }

  test("test CanRecover[Future].recoverEitherFromNonFatal should return the successful result") {
    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    fa.recoverEitherFromNonFatal {
      case err => SomeError.someThrowable(err).asLeft[Int]
    }.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanRecover[Future].recoverEitherFromNonFatal should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    fa.recoverEitherFromNonFatal { case NonFatal(_) => expected }.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test CanRecover[Future].recoverEitherTFromNonFatal should catch NonFatal") {
    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   = fa
      .recoverEitherTFromNonFatal {
        case err => SomeError.someThrowable(err).asLeft[Int]
      }
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }

    val fa2 = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expected            = 1.asRight[SomeError]
    val actualSuccessResult =
      fa2.recoverEitherTFromNonFatal { case err @ _ => expected }.value.map { actual =>
        Assertions.assertEquals(actual, expected)
      }

    Future.sequence(List(actualFailedResult, actualSuccessResult))
  }

  test("test CanRecover[Future].recoverEitherTFromNonFatal should return the successful result") {
    val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    fa.recoverEitherTFromNonFatal {
      case err => SomeError.someThrowable(err).asLeft[Int]
    }.value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanRecover[Future].recoverEitherTFromNonFatal should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    fa.recoverEitherTFromNonFatal { case NonFatal(_) => expected }.value.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

}
