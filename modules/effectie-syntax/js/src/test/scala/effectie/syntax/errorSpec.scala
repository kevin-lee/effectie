package effectie.syntax

import cats.data.EitherT
import cats.syntax.all._
import effectie.core.Fx
import effectie.syntax.error._
import effectie.syntax.fx.effectOf
import effectie.testing.FutureTools
import effectie.testing.types.{SomeError, SomeThrowableError}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.scalajs.concurrent.JSExecutionContext
import scala.util.control.NonFatal

class errorSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  override val munitTimeout: FiniteDuration = 200.milliseconds

  val CanHandleError: effectie.core.CanHandleError.type = effectie.core.CanHandleError

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: Fx, A](a: => A): F[A] = effectOf[F](a)

  import effectie.instances.future.fx._

  import scala.concurrent.Future

  test("test CanCatch[Future].catchNonFatalThrowable should catch NonFatal") {

    implicit val ec: ExecutionContext = JSExecutionContext.queue

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = expectedExpcetion.asLeft[Int]

    fa.catchNonFatalThrowable.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanCatch[Future].catchNonFatalThrowable should return the successful result") {

    implicit val ec: ExecutionContext = JSExecutionContext.queue

    val fa       = run[Future, Int](1)
    val expected = 1.asRight[Throwable]

    fa.catchNonFatalThrowable.map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test CanCatch[Future].catchNonFatal should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    fa.catchNonFatal(SomeError.someThrowable).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanCatch[Future].catchNonFatal should return the successful result") {

    val fa       = run[Future, Int](1)
    val expected = 1.asRight[SomeError]

    fa.catchNonFatal(SomeError.someThrowable).map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test CanCatch[Future].catchNonFatalEither should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa       = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    fa.catchNonFatalEither(SomeError.someThrowable).map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test CanCatch[Future].catchNonFatalEither should return the successful result") {

    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    fa.catchNonFatalEither(SomeError.someThrowable).map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test CanCatch[Future].catchNonFatalEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    fa.catchNonFatalEither(SomeError.someThrowable).map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test CanHandleError[Future].handleNonFatalWith should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 1

    fa.handleNonFatalWith(_ => Future(expected)).map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test CanHandleError[Future].handleNonFatalWith should return the successful result") {

    val fa       = run[Future, Int](1)
    val expected = 1

    fa.handleNonFatalWith(_ => Future(123)).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanHandleError[Future].handleNonFatalWithEither should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    val failedResult =
      fa.handleNonFatalWith(err => Future(SomeError.someThrowable(err).asLeft[Int])).map { actualFailedResult =>
        assertEquals(actualFailedResult, expectedFailedResult)
      }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected = 1.asRight[SomeError]

    val handledResult = fa2.handleNonFatalWith(_ => Future(expected)).map { actual =>
      assertEquals(actual, expected)
    }

    Future.sequence(List(failedResult, handledResult))
  }

  test("test CanHandleError[Future].handleNonFatalWithEither should return the successful result") {

    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    fa.handleNonFatalWith(err => Future(SomeError.someThrowable(err).asLeft[Int])).map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test CanHandleError[Future].handleNonFatalWithEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    fa.handleNonFatalWith(_ => Future(1.asRight[SomeError])).map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test CanHandleError[Future].handleEitherNonFatalWith should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    val failedResult =
      fa.handleEitherNonFatalWith(err => Future(SomeError.someThrowable(err).asLeft[Int])).map { actualFailedResult =>
        assertEquals(actualFailedResult, expectedFailedResult)
      }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected = 1.asRight[SomeError]

    val handledResult = fa2.handleEitherNonFatalWith(_ => Future(expected)).map { actual =>
      assertEquals(actual, expected)
    }

    Future.sequence(List(failedResult, handledResult))
  }

  test("test CanHandleError[Future].handleEitherNonFatalWith should return the successful result") {

    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    fa.handleEitherNonFatalWith(err => Future(SomeError.someThrowable(err).asLeft[Int])).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanHandleError[Future].handleEitherNonFatalWith should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    fa.handleEitherNonFatalWith(_ => Future(expected)).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanHandleError[Future].handleNonFatal should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 1
    fa.handleNonFatal(_ => expected).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanHandleError[Future].handleNonFatal should return the successful result") {

    val fa       = run[Future, Int](1)
    val expected = 1
    fa.handleNonFatal(_ => 123).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanHandleError[Future].handleNonFatalEither should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val failedResult = fa.handleNonFatal(err => SomeError.someThrowable(err).asLeft[Int]).map { actualFailedResult =>
      assertEquals(actualFailedResult, expectedFailedResult)
    }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected = 1.asRight[SomeError]

    val handledResult = fa2.handleNonFatal(_ => expected).map { actual =>
      assertEquals(actual, expected)
    }

    Future.sequence(List(failedResult, handledResult))
  }

  test("test CanHandleError[Future].handleNonFatalEither should return the successful result") {

    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    fa.handleNonFatal(err => SomeError.someThrowable(err).asLeft[Int]).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanHandleError[Future].handleNonFatalEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    fa.handleNonFatal(_ => 1.asRight[SomeError]).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanHandleError[Future].handleEitherNonFatal should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    val failedResult =
      fa.handleEitherNonFatal(err => SomeError.someThrowable(err).asLeft[Int]).map { actualFailedResult =>
        assertEquals(actualFailedResult, expectedFailedResult)
      }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected = 1.asRight[SomeError]

    val handledResult = fa2.handleEitherNonFatal(_ => expected).map { actual =>
      assertEquals(actual, expected)
    }

    Future.sequence(List(failedResult, handledResult))
  }

  test("test CanHandleError[Future].handleEitherNonFatal should return the successful result") {

    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    fa.handleEitherNonFatal(err => SomeError.someThrowable(err).asLeft[Int]).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanHandleError[Future].handleEitherNonFatal should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    fa.handleEitherNonFatal(_ => expected).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test F[Either[A, B]](F(Right(b))).rethrowIfLeft should return the successful result") {

    val fa       = run[Future, Either[SomeThrowableError, Int]](1.asRight[SomeThrowableError])
    val expected = 1
    fa.rethrowIfLeft.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test F[Either[A, B]](F(Left(a))).rethrowIfLeft should return the failed result") {

    val expectedFailure = SomeThrowableError.message("Failed")
    val fa              = run[Future, Either[SomeThrowableError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure

    fa.rethrowIfLeft
      .onComplete {
        case scala.util.Success(result) =>
          fail(s"Expected SomeThrowableError to be thrown but got ${result.toString} instead")
        case scala.util.Failure(actual) =>
          assertEquals(actual, expected)
      }

  }

  test("test EitherT[F, A, B](F(Right(b))).rethrowTIfLeft should return the successful result") {

    val fa       = EitherT(run[Future, Either[SomeThrowableError, Int]](1.asRight[SomeThrowableError]))
    val expected = 1
    fa.rethrowTIfLeft.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test EitherT[F, A, B](F(Left(a))).rethrowTIfLeft should return the failed result") {

    val expectedFailure = SomeThrowableError.message("Failed")
    val fa              = EitherT(run[Future, Either[SomeThrowableError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure
    fa.rethrowTIfLeft.onComplete {
      case scala.util.Success(result) =>
        fail(s"Expected SomeThrowableError to be thrown but got ${result.toString} instead")
      case scala.util.Failure(actual) =>
        assertEquals(actual, expected)
    }

  }

  test("test CanRecover[Future].recoverFromNonFatalWith should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 1
    fa.recoverFromNonFatalWith {
      case NonFatal(`expectedExpcetion`) => Future(expected)
    }.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanRecover[Future].recoverFromNonFatalWith should return the successful result") {

    val fa       = run[Future, Int](1)
    val expected = 1
    fa.recoverFromNonFatalWith {
      case NonFatal(_) => Future(123)
    }.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanRecover[Future].recoverFromNonFatalWithEither should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    val failedResult = fa
      .recoverFromNonFatalWith {
        case err => Future(SomeError.someThrowable(err).asLeft[Int])
      }
      .map { actualFailedResult =>
        assertEquals(actualFailedResult, expectedFailedResult)
      }

    val fa2           = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected      = 1.asRight[SomeError]
    val handledResult = fa2
      .recoverFromNonFatalWith {
        case NonFatal(`expectedExpcetion`) => Future(expected)
      }
      .map { actual =>
        assertEquals(actual, expected)
      }

    Future.sequence(List(failedResult, handledResult))
  }

  test("test CanRecover[Future].recoverFromNonFatalWithEither should return the successful result") {

    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    fa.recoverFromNonFatalWith {
      case err => Future(SomeError.someThrowable(err).asLeft[Int])
    }.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanRecover[Future].recoverFromNonFatalWithEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    fa.recoverFromNonFatalWith {
      case NonFatal(_) => Future(1.asRight[SomeError])
    }.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanRecover[Future].recoverEitherFromNonFatalWith should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    val failedResult = fa
      .recoverEitherFromNonFatalWith {
        case err => Future(SomeError.someThrowable(err).asLeft[Int])
      }
      .map { actualFailedResult =>
        assertEquals(actualFailedResult, expectedFailedResult)
      }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected = 1.asRight[SomeError]

    val handleResult = fa2
      .recoverEitherFromNonFatalWith {
        case err @ _ => Future(expected)
      }
      .map { actual =>
        assertEquals(actual, expected)
      }

    Future.sequence(List(failedResult, handleResult))

  }

  test("test CanRecover[Future].recoverEitherFromNonFatalWith should return the successful result") {

    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    fa.recoverEitherFromNonFatalWith {
      case err => Future(SomeError.someThrowable(err).asLeft[Int])
    }.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanRecover[Future].recoverEitherFromNonFatalWith should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    fa.recoverEitherFromNonFatalWith {
      case NonFatal(_) => Future(expected)
    }.map { actual =>
      assertEquals(actual, expected)
    }

  }

  // /

  test("test CanRecover[Future].recoverFromNonFatal should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 1
    fa.recoverFromNonFatal { case NonFatal(`expectedExpcetion`) => expected }.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanRecover[Future].recoverFromNonFatal should return the successful result") {

    val fa       = run[Future, Int](1)
    val expected = 1
    fa.recoverFromNonFatal { case NonFatal(_) => 123 }.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanRecover[Future].recoverFromNonFatalEither should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    val failedResult = fa
      .recoverFromNonFatal {
        case err => SomeError.someThrowable(err).asLeft[Int]
      }
      .map { actualFailedResult =>
        assertEquals(actualFailedResult, expectedFailedResult)
      }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected = 1.asRight[SomeError]

    val handleResult = fa2.recoverFromNonFatal { case NonFatal(`expectedExpcetion`) => expected }.map { actual =>
      assertEquals(actual, expected)
    }

    Future.sequence(List(failedResult, handleResult))

  }

  test("test CanRecover[Future].recoverFromNonFatalEither should return the successful result") {

    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    fa.recoverFromNonFatal {
      case err => SomeError.someThrowable(err).asLeft[Int]
    }.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanRecover[Future].recoverFromNonFatalEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    fa.recoverFromNonFatal { case NonFatal(_) => 1.asRight[SomeError] }.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanRecover[Future].recoverEitherFromNonFatal should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val failedResult         = fa
      .recoverEitherFromNonFatal {
        case err => SomeError.someThrowable(err).asLeft[Int]
      }
      .map { actualFailedResult =>
        assertEquals(actualFailedResult, expectedFailedResult)
      }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected = 1.asRight[SomeError]

    val handleResult = fa2.recoverEitherFromNonFatal { case err @ _ => expected }.map { actual =>
      assertEquals(actual, expected)
    }

    Future.sequence(List(failedResult, handleResult))

  }

  test("test CanRecover[Future].recoverEitherFromNonFatal should return the successful result") {

    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    fa.recoverEitherFromNonFatal {
      case err => SomeError.someThrowable(err).asLeft[Int]
    }.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanRecover[Future].recoverEitherFromNonFatal should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    fa.recoverEitherFromNonFatal { case NonFatal(_) => expected }.map { actual =>
      assertEquals(actual, expected)
    }

  }

}
