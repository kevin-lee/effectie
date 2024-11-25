package effectie.instances.future

import effectie.core.{CanRecover, FxCtor}
import effectie.testing.FutureTools
import effectie.testing.types.SomeError

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/** @author Kevin Lee
  * @since 2022-01-06
  */
class canRecoverSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  override val munitTimeout: FiniteDuration = 200.milliseconds

  /* Future */

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor, A](a: => A): F[A] =
    FxCtor[F].effectOf(a)

  import effectie.instances.future.canRecover._
  import effectie.instances.future.fxCtor._

  import scala.concurrent.Future
  import scala.util.control.NonFatal

  test("test CanRecover[Future].recoverFromNonFatalWith should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 1

    CanRecover[Future]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) => Future(expected)
      }
      .map { actual =>
        assertEquals(actual, expected)
      }

  }

  test("test CanRecover[Future].recoverFromNonFatalWith should return the successful result") {

    val fa       = run[Future, Int](1)
    val expected = 1

    CanRecover[Future]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(_) => Future(123)
      }
      .map { actual =>
        assertEquals(actual, expected)
      }

  }

  test("test CanRecover[Future].recoverFromNonFatalWithEither should catch NonFatal") {

    val expectedExpcetion                            = new RuntimeException("Something's wrong")
    val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))

    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

    val failedResult = CanRecover[Future]
      .recoverFromNonFatalWith(fa) {
        case err => Future(Left(SomeError.someThrowable(err)))
      }
      .map { actual =>
        assertEquals(actual, expectedFailedResult)
      }

    val expected: Either[SomeError, Int] = Right(1)

    val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

    val handledResult = CanRecover[Future]
      .recoverFromNonFatalWith(fa2) {
        case NonFatal(`expectedExpcetion`) => Future(expected)
      }
      .map { actual =>
        assertEquals(actual, expected)
      }

    Future.sequence(
      List(
        failedResult,
        handledResult,
      )
    )

  }

  test("test CanRecover[Future].recoverFromNonFatalWithEither should return the successful result") {

    val expected: Either[SomeError, Int] = Right(1)

    val fa = run[Future, Either[SomeError, Int]](Right(1))

    CanRecover[Future]
      .recoverFromNonFatalWith(fa) {
        case err => Future(Left(SomeError.someThrowable(err)))
      }
      .map { actual =>
        assertEquals(actual, expected)
      }

  }

  test("test CanRecover[Future].recoverFromNonFatalWithEither should return the failed result") {

    val expectedFailure                  = SomeError.message("Failed")
    val expected: Either[SomeError, Int] = Left(expectedFailure)

    val fa = run[Future, Either[SomeError, Int]](Left(expectedFailure))

    CanRecover[Future]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(_) => Future(Right(1))
      }
      .map { actual =>
        assertEquals(actual, expected)
      }

  }

  test("test CanRecover[Future].recoverEitherFromNonFatalWith should catch NonFatal") {

    val expectedExpcetion                            = new RuntimeException("Something's wrong")
    val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))

    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

    val failedResult = CanRecover[Future]
      .recoverEitherFromNonFatalWith(fa) {
        case err => Future(Left(SomeError.someThrowable(err)))
      }
      .map { actual =>
        assertEquals(actual, expectedFailedResult)
      }

    val expected: Either[SomeError, Int] = Right(1)

    val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

    val handledResult = CanRecover[Future]
      .recoverEitherFromNonFatalWith(fa2) {
        case _ => Future(expected)
      }
      .map { actual =>
        assertEquals(actual, expected)
      }

    Future.sequence(
      List(
        failedResult,
        handledResult,
      )
    )
  }

  test("test CanRecover[Future].recoverEitherFromNonFatalWith should return the successful result") {

    val expected: Either[SomeError, Int] = Right(1)

    val fa = run[Future, Either[SomeError, Int]](Right(1))

    CanRecover[Future]
      .recoverEitherFromNonFatalWith(fa) {
        case err => Future(Left(SomeError.someThrowable(err)))
      }
      .map { actual =>
        assertEquals(actual, expected)
      }

  }

  test("test CanRecover[Future].recoverEitherFromNonFatalWith should return the failed result") {

    val expectedFailure                  = SomeError.message("Failed")
    val expected: Either[SomeError, Int] = Left(expectedFailure)

    val fa = run[Future, Either[SomeError, Int]](Left(expectedFailure))

    CanRecover[Future]
      .recoverEitherFromNonFatalWith(fa) {
        case NonFatal(_) => Future(expected)
      }
      .map { actual =>
        assertEquals(actual, expected)
      }

  }

  // /

  test("test CanRecover[Future].recoverFromNonFatal should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 1

    CanRecover[Future].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expected }.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanRecover[Future].recoverFromNonFatal should return the successful result") {

    val fa       = run[Future, Int](1)
    val expected = 1

    CanRecover[Future].recoverFromNonFatal(fa) { case NonFatal(_) => 123 }.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanRecover[Future].recoverFromNonFatalEither should catch NonFatal") {

    val expectedExpcetion                            = new RuntimeException("Something's wrong")
    val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))

    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

    val failedResult = CanRecover[Future]
      .recoverFromNonFatal(fa) {
        case err => Left(SomeError.someThrowable(err))
      }
      .map { actual =>
        assertEquals(actual, expectedFailedResult)
      }

    val expected: Either[SomeError, Int] = Right(1)

    val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

    val handledResult =
      CanRecover[Future].recoverFromNonFatal(fa2) { case NonFatal(`expectedExpcetion`) => expected }.map { actual =>
        assertEquals(actual, expected)
      }

    Future.sequence(
      List(
        failedResult,
        handledResult,
      )
    )
  }

  test("test CanRecover[Future].recoverFromNonFatalEither should return the successful result") {

    val expected: Either[SomeError, Int] = Right(1)

    val fa = run[Future, Either[SomeError, Int]](Right(1))

    CanRecover[Future]
      .recoverFromNonFatal(fa) {
        case err => Left(SomeError.someThrowable(err))
      }
      .map { actual =>
        assertEquals(actual, expected)
      }

  }

  test("test CanRecover[Future].recoverFromNonFatalEither should return the failed result") {

    val expectedFailure                  = SomeError.message("Failed")
    val expected: Either[SomeError, Int] = Left(expectedFailure)

    val fa = run[Future, Either[SomeError, Int]](Left(expectedFailure))

    CanRecover[Future].recoverFromNonFatal(fa) { case NonFatal(_) => Right(1) }.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanRecover[Future].recoverEitherFromNonFatal should catch NonFatal") {

    val expectedExpcetion                            = new RuntimeException("Something's wrong")
    val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))

    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

    val failedResult = CanRecover[Future]
      .recoverEitherFromNonFatal(fa) {
        case err => Left(SomeError.someThrowable(err))
      }
      .map { actual =>
        assertEquals(actual, expectedFailedResult)
      }

    val expected: Either[SomeError, Int] = Right(1)

    val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

    val handledResult = CanRecover[Future].recoverEitherFromNonFatal(fa2) { case _ => expected }.map { actual =>
      assertEquals(actual, expected)
    }

    Future.sequence(
      List(
        failedResult,
        handledResult,
      )
    )
  }

  test("test CanRecover[Future].recoverEitherFromNonFatal should return the successful result") {

    val expected: Either[SomeError, Int] = Right(1)

    val fa = run[Future, Either[SomeError, Int]](Right(1))

    CanRecover[Future]
      .recoverEitherFromNonFatal(fa) {
        case err => Left(SomeError.someThrowable(err))
      }
      .map { actual =>
        assertEquals(actual, expected)
      }

  }

  test("test CanRecover[Future].recoverEitherFromNonFatal should return the failed result") {

    val expectedFailure                  = SomeError.message("Failed")
    val expected: Either[SomeError, Int] = Left(expectedFailure)

    val fa = run[Future, Either[SomeError, Int]](Left(expectedFailure))

    CanRecover[Future].recoverEitherFromNonFatal(fa) { case NonFatal(_) => expected }.map { actual =>
      assertEquals(actual, expected)
    }

  }

}
