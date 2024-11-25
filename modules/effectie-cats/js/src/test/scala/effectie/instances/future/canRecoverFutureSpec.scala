package effectie.instances.future

import cats.data.EitherT
import cats.syntax.all._
import effectie.core._
import effectie.syntax.all._
import effectie.testing.FutureTools
import effectie.testing.types.SomeError
import munit.Assertions

import concurrent.duration._
import scala.concurrent._

/** @author Kevin Lee
  * @since 2020-08-17
  */
class canRecoverFutureSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  override val munitTimeout: FiniteDuration = 200.milliseconds

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor, A](a: => A): F[A] =
    FxCtor[F].effectOf(a)

  import effectie.instances.future.canRecover._
  import effectie.instances.future.fxCtor._

  import scala.concurrent.Future
  import scala.util.control.NonFatal

  test("CanRecover[Future].recoverFromNonFatalWith should recover from NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 1

    CanRecover[Future]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) => Future(expected)
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

  test("CanRecover[Future].recoverFromNonFatalWith(Either) should recover from NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   =
      CanRecover[Future]
        .recoverFromNonFatalWith(fa) {
          case err => Future(SomeError.someThrowable(err).asLeft[Int])
        }
        .map { actual =>
          Assertions.assertEquals(actual, expectedFailedResult)
        }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected = 1.asRight[SomeError]
    val actualHandledResult =
      CanRecover[Future]
        .recoverFromNonFatalWith(fa2) {
          case NonFatal(`expectedExpcetion`) => Future(expected)
        }
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

  test("CanRecover[Future].recoverFromNonFatalWith(Either) should return successful result") {

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

  test("CanRecover[Future].recoverFromNonFatalWith(Either) should return failed result") {

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

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    val actualFailedResult = CanRecover[Future]
      .recoverEitherFromNonFatalWith(fa) {
        case err => Future(SomeError.someThrowable(err).asLeft[Int])
      }
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected = 1.asRight[SomeError]

    val actualHandledResult =
      CanRecover[Future]
        .recoverEitherFromNonFatalWith(fa2) {
          case err @ _ => Future(expected)
        }
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

  test("CanRecover[Future].recoverEitherTFromNonFatalWith should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   =
      CanRecover[Future]
        .recoverEitherTFromNonFatalWith(fa) {
          case err => Future(SomeError.someThrowable(err).asLeft[Int])
        }
        .value
        .map { actual =>
          Assertions.assertEquals(actual, expectedFailedResult)
        }

    val fa2 = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expected = 1.asRight[SomeError]

    val actualHandledResult =
      CanRecover[Future]
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

  test("CanRecover[Future].recoverEitherTFromNonFatalWith should return the successful result") {

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

  test("CanRecover[Future].recoverEitherTFromNonFatalWith should return the failed result") {

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

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 1

    CanRecover[Future]
      .recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expected }
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

  test("CanRecover[Future].recoverFromNonFatal(Either) should recover from NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    val actualFailedResult =
      CanRecover[Future]
        .recoverFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
        .map { actual =>
          Assertions.assertEquals(actual, expectedFailedResult)
        }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected = 1.asRight[SomeError]

    val actualHandledResult =
      CanRecover[Future].recoverFromNonFatal(fa2) { case NonFatal(`expectedExpcetion`) => expected }.map { actual =>
        Assertions.assertEquals(actual, expected)
      }

    Future.sequence(
      List(
        actualFailedResult,
        actualHandledResult,
      )
    )

  }

  test("CanRecover[Future].recoverFromNonFatal(Either) should return successful result") {

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

  test("CanRecover[Future].recoverFromNonFatal(Either) should return failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    CanRecover[Future].recoverFromNonFatal(fa) { case NonFatal(_) => 1.asRight[SomeError] }.map { actual =>
      Assertions.assertEquals(actual, expected)
    }

  }

  test("CanRecover[Future].recoverEitherFromNonFatal should recover from NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    val actualFailedResult =
      CanRecover[Future]
        .recoverEitherFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
        .map { actual =>
          Assertions.assertEquals(actual, expectedFailedResult)
        }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected = 1.asRight[SomeError]

    val actualHandledResult =
      CanRecover[Future].recoverEitherFromNonFatal(fa2) { case err @ _ => expected }.map { actual =>
        Assertions.assertEquals(actual, expected)
      }

    Future.sequence(
      List(
        actualFailedResult,
        actualHandledResult,
      )
    )
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

    CanRecover[Future].recoverEitherFromNonFatal(fa) { case NonFatal(_) => expected }.map { actual =>
      Assertions.assertEquals(actual, expected)
    }

  }

  test("CanRecover[Future].recoverEitherTFromNonFatal should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    val actualFailedResult =
      CanRecover[Future]
        .recoverEitherTFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
        .value
        .map { actual =>
          Assertions.assertEquals(actual, expectedFailedResult)
        }

    val fa2 = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expected = 1.asRight[SomeError]

    val actualHandledResult =
      CanRecover[Future].recoverEitherTFromNonFatal(fa2) { case err @ _ => expected }.value.map { actual =>
        Assertions.assertEquals(actual, expected)
      }

    Future.sequence(
      List(
        actualFailedResult,
        actualHandledResult,
      )
    )
  }

  test("CanRecover[Future].recoverEitherTFromNonFatal should return the successful result") {

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

  test("CanRecover[Future].recoverEitherTFromNonFatal should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    CanRecover[Future].recoverEitherTFromNonFatal(fa) { case NonFatal(_) => expected }.value.map { actual =>
      Assertions.assertEquals(actual, expected)
    }

  }

}
