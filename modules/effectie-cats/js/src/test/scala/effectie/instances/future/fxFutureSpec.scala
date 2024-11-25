package effectie.instances.future

import cats.data.EitherT
import cats.syntax.all._
import effectie.core._
import effectie.syntax.all._
import effectie.testing.FutureTools
import effectie.testing.types.SomeError
import munit.Assertions

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

/** @author Kevin Lee
  * @since 2020-12-06
  */
class fxFutureSpec extends CommonFxFutureSpec with CanCatchSpec with CanHandleErrorSpec with CanRecoverSpec

trait CommonFxFutureSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  override val munitTimeout: FiniteDuration = 200.milliseconds

  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: Fx, A](a: => A): F[A] =
    Fx[F].effectOf(a)

  implicit val futureFx: Fx[Future] = effectie.instances.future.fx.futureFx

  val waitFor = WaitFor(1.second)

}

trait CanCatchSpec {
  self: CommonFxFutureSpec =>

  import scala.concurrent.Future

  test("Fx[Future].catchNonFatalEitherT should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    Fx[Future].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.map { actual =>
      Assertions.assertEquals(actual, expected)

    }

  }

  test("Fx[Future].catchNonFatalEitherT should return the successful result") {

    val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    Fx[Future].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("Fx[Future].catchNonFatalEitherT should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    Fx[Future].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }
}

trait CanHandleErrorSpec {
  self: CommonFxFutureSpec =>

  import scala.concurrent.Future

  test("Fx[Future].handleEitherTNonFatalWith should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    val actualFailedResult =
      Fx[Future]
        .handleEitherTNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int]))
        .value
        .map { actual =>
          Assertions.assertEquals(actual, expectedFailedResult)
        }

    val fa2      = EitherT(
      run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    )
    val expected = 1.asRight[SomeError]

    val actualHandledResult =
      Fx[Future].handleEitherTNonFatalWith(fa2)(_ => Future(expected)).value.map { actual =>
        Assertions.assertEquals(actual, expected)
      }

    Future.sequence(
      List(
        actualFailedResult,
        actualHandledResult,
      )
    )
  }

  test("Fx[Future].handleEitherTNonFatalWith should return the successful result") {

    val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    Fx[Future]
      .handleEitherTNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int]))
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("Fx[Future].handleEitherTNonFatalWith should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    Fx[Future].handleEitherTNonFatalWith(fa)(_ => Future(expected)).value.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("Fx[Future].handleEitherTNonFatal should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    val actualFailedResult =
      Fx[Future].handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]).value.map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }

    val fa2      = EitherT(
      run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    )
    val expected = 1.asRight[SomeError]

    val actualHandledResult =
      Fx[Future].handleEitherTNonFatal(fa2)(_ => expected).value.map { actual =>
        Assertions.assertEquals(actual, expected)
      }

    Future.sequence(
      List(
        actualFailedResult,
        actualHandledResult,
      )
    )
  }

  test("Fx[Future].handleEitherTNonFatal should return the successful result") {

    val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    Fx[Future].handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]).value.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("Fx[Future].handleEitherTNonFatal should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    Fx[Future].handleEitherTNonFatal(fa)(_ => expected).value.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

}

trait CanRecoverSpec {
  self: CommonFxFutureSpec =>

  test("Fx[Future].recoverEitherTFromNonFatalWith should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    val actualFailedResult =
      Fx[Future]
        .recoverEitherTFromNonFatalWith(fa) {
          case err => Future(SomeError.someThrowable(err).asLeft[Int])
        }
        .value
        .map { actual =>
          Assertions.assertEquals(actual, expectedFailedResult)
        }

    val expected = 1.asRight[SomeError]

    val fa2 = EitherT(
      run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    )

    val actualHandledResult =
      Fx[Future]
        .recoverEitherTFromNonFatalWith(fa2) {
          case err @ _ => Future(expected)
        }
        .value
        .map { actual =>
          Assertions.assertEquals(actual, expected)
        }

    Future.sequence(
      List(
        actualFailedResult,
        actualHandledResult,
      )
    )
  }

  test("Fx[Future].recoverEitherTFromNonFatalWith should return the successful result") {

    val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    Fx[Future]
      .recoverEitherTFromNonFatalWith(fa) {
        case err => Future(SomeError.someThrowable(err).asLeft[Int])
      }
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("Fx[Future].recoverEitherTFromNonFatalWith should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    Fx[Future]
      .recoverEitherTFromNonFatalWith(fa) {
        case NonFatal(_) => Future(expected)
      }
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  // /

  test("Fx[Future].recoverEitherTFromNonFatal should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    val actualFailedResult =
      Fx[Future]
        .recoverEitherTFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
        .value
        .map { actual =>
          Assertions.assertEquals(actual, expectedFailedResult)
        }

    val fa2      = EitherT(
      run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    )
    val expected = 1.asRight[SomeError]

    val actualHandledResult =
      Fx[Future].recoverEitherTFromNonFatal(fa2) { case err @ _ => expected }.value.map { actual =>
        Assertions.assertEquals(actual, expected)
      }

    Future.sequence(
      List(
        actualFailedResult,
        actualHandledResult,
      )
    )
  }

  test("Fx[Future].recoverEitherTFromNonFatal should return the successful result") {

    val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    Fx[Future]
      .recoverEitherTFromNonFatal(fa) {
        case err => SomeError.someThrowable(err).asLeft[Int]
      }
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("Fx[Future].recoverEitherTFromNonFatal should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    Fx[Future].recoverEitherTFromNonFatal(fa) { case NonFatal(_) => expected }.value.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }
}
