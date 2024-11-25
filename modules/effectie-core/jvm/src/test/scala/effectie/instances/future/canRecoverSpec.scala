package effectie.instances.future

import effectie.core.{CanRecover, FxCtor}
import effectie.testing.FutureTools
import effectie.testing.types.SomeError

import hedgehog._
import hedgehog.runner._

/** @author Kevin Lee
  * @since 2022-01-06
  */
object canRecoverSpec extends Properties {

  override def tests: List[Test] = futureSpecs

  /* Future */
  val futureSpecs = List(
    example(
      "test CanRecover[Future].recoverFromNonFatalWith should catch NonFatal",
      FutureSpec.testCanRecover_Future_recoverFromNonFatalWithShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Future].recoverFromNonFatalWith should return the successful result",
      FutureSpec.testCanRecover_Future_recoverFromNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Future].recoverFromNonFatalWithEither should catch NonFatal",
      FutureSpec.testCanRecover_Future_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Future].recoverFromNonFatalWithEither should return the successful result",
      FutureSpec.testCanRecover_Future_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Future].recoverFromNonFatalWithEither should return the failed result",
      FutureSpec.testCanRecover_Future_recoverFromNonFatalWithEitherShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[Future].recoverEitherFromNonFatalWith should catch NonFatal",
      FutureSpec.testCanRecover_Future_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Future].recoverEitherFromNonFatalWith should return the successful result",
      FutureSpec.testCanRecover_Future_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Future].recoverEitherFromNonFatalWith should return the failed result",
      FutureSpec.testCanRecover_Future_recoverEitherFromNonFatalWithShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[Future].recoverFromNonFatal should catch NonFatal",
      FutureSpec.testCanRecover_Future_recoverFromNonFatalShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Future].recoverFromNonFatal should return the successful result",
      FutureSpec.testCanRecover_Future_recoverFromNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Future].recoverFromNonFatalEither should catch NonFatal",
      FutureSpec.testCanRecover_Future_recoverFromNonFatalEitherShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Future].recoverFromNonFatalEither should return the successful result",
      FutureSpec.testCanRecover_Future_recoverFromNonFatalEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Future].recoverFromNonFatalEither should return the failed result",
      FutureSpec.testCanRecover_Future_recoverFromNonFatalEitherShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[Future].recoverEitherFromNonFatal should catch NonFatal",
      FutureSpec.testCanRecover_Future_recoverEitherFromNonFatalShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Future].recoverEitherFromNonFatal should return the successful result",
      FutureSpec.testCanRecover_Future_recoverEitherFromNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Future].recoverEitherFromNonFatal should return the failed result",
      FutureSpec.testCanRecover_Future_recoverEitherFromNonFatalShouldReturnFailedResult,
    ),
  )

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor, A](a: => A): F[A] =
    FxCtor[F].effectOf(a)

  object FutureSpec extends FutureTools {
    import effectie.instances.future.canRecover._
    import effectie.instances.future.fxCtor._

    import scala.concurrent.duration._
    import scala.concurrent.{ExecutionContext, Future}
    import scala.util.control.NonFatal

    implicit val ec: ExecutionContext = globalExecutionContext

    val waitFor = WaitFor(1.second)

    def testCanRecover_Future_recoverFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 1
      val actual            = futureToValue(
        CanRecover[Future].recoverFromNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) => Future(expected)
        },
        waitFor,
      )

      actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = run[Future, Int](1)
      val expected = 1
      val actual   = futureToValue(
        CanRecover[Future].recoverFromNonFatalWith(fa) {
          case NonFatal(_) => Future(123)
        },
        waitFor,
      )

      actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion                            = new RuntimeException("Something's wrong")
      val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))

      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val actualFailedResult =
        futureToValue(
          CanRecover[Future].recoverFromNonFatalWith(fa) {
            case err => Future(Left(SomeError.someThrowable(err)))
          },
          waitFor,
        )

      val expected: Either[SomeError, Int] = Right(1)

      val fa2    = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val actual =
        futureToValue(
          CanRecover[Future].recoverFromNonFatalWith(fa2) {
            case NonFatal(`expectedExpcetion`) => Future(expected)
          },
          waitFor,
        )

      expectedFailedResult ==== actualFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

      val expected: Either[SomeError, Int] = Right(1)

      val fa     = run[Future, Either[SomeError, Int]](Right(1))
      val actual =
        futureToValue(
          CanRecover[Future].recoverFromNonFatalWith(fa) {
            case err => Future(Left(SomeError.someThrowable(err)))
          },
          waitFor,
        )

      actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalWithEitherShouldReturnFailedResult: Result = {

      val expectedFailure                  = SomeError.message("Failed")
      val expected: Either[SomeError, Int] = Left(expectedFailure)

      val fa     = run[Future, Either[SomeError, Int]](Left(expectedFailure))
      val actual =
        futureToValue(
          CanRecover[Future].recoverFromNonFatalWith(fa) {
            case NonFatal(_) => Future(Right(1))
          },
          waitFor,
        )

      actual ==== expected
    }

    def testCanRecover_Future_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion                            = new RuntimeException("Something's wrong")
      val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))

      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val actualFailedResult = futureToValue(
        CanRecover[Future]
          .recoverEitherFromNonFatalWith(fa) {
            case err => Future(Left(SomeError.someThrowable(err)))
          },
        waitFor,
      )

      val expected: Either[SomeError, Int] = Right(1)

      val fa2    = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val actual =
        futureToValue(
          CanRecover[Future]
            .recoverEitherFromNonFatalWith(fa2) {
              case _ => Future(expected)
            },
          waitFor,
        )

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      val expected: Either[SomeError, Int] = Right(1)

      val fa     = run[Future, Either[SomeError, Int]](Right(1))
      val actual = futureToValue(
        CanRecover[Future]
          .recoverEitherFromNonFatalWith(fa) {
            case err => Future(Left(SomeError.someThrowable(err)))
          },
        waitFor,
      )

      actual ==== expected
    }

    def testCanRecover_Future_recoverEitherFromNonFatalWithShouldReturnFailedResult: Result = {

      val expectedFailure                  = SomeError.message("Failed")
      val expected: Either[SomeError, Int] = Left(expectedFailure)

      val fa     = run[Future, Either[SomeError, Int]](Left(expectedFailure))
      val actual =
        futureToValue(
          CanRecover[Future]
            .recoverEitherFromNonFatalWith(fa) {
              case NonFatal(_) => Future(expected)
            },
          waitFor,
        )

      actual ==== expected
    }

    // /

    def testCanRecover_Future_recoverFromNonFatalShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 1
      val actual            = futureToValue(
        CanRecover[Future].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expected },
        waitFor,
      )

      actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = run[Future, Int](1)
      val expected = 1
      val actual   = futureToValue(
        CanRecover[Future].recoverFromNonFatal(fa) { case NonFatal(_) => 123 },
        waitFor,
      )

      actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalEitherShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion                            = new RuntimeException("Something's wrong")
      val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))

      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val actualFailedResult =
        futureToValue(
          CanRecover[Future].recoverFromNonFatal(fa) {
            case err => Left(SomeError.someThrowable(err))
          },
          waitFor,
        )

      val expected: Either[SomeError, Int] = Right(1)

      val fa2    = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val actual = futureToValue(
        CanRecover[Future].recoverFromNonFatal(fa2) { case NonFatal(`expectedExpcetion`) => expected },
        waitFor,
      )

      expectedFailedResult ==== actualFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalEitherShouldReturnSuccessfulResult: Result = {

      val expected: Either[SomeError, Int] = Right(1)

      val fa     = run[Future, Either[SomeError, Int]](Right(1))
      val actual =
        futureToValue(
          CanRecover[Future].recoverFromNonFatal(fa) {
            case err => Left(SomeError.someThrowable(err))
          },
          waitFor,
        )

      actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalEitherShouldReturnFailedResult: Result = {

      val expectedFailure                  = SomeError.message("Failed")
      val expected: Either[SomeError, Int] = Left(expectedFailure)

      val fa     = run[Future, Either[SomeError, Int]](Left(expectedFailure))
      val actual = futureToValue(
        CanRecover[Future].recoverFromNonFatal(fa) { case NonFatal(_) => Right(1) },
        waitFor,
      )

      actual ==== expected
    }

    def testCanRecover_Future_recoverEitherFromNonFatalShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion                            = new RuntimeException("Something's wrong")
      val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))

      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val actualFailedResult = futureToValue(
        CanRecover[Future]
          .recoverEitherFromNonFatal(fa) {
            case err => Left(SomeError.someThrowable(err))
          },
        waitFor,
      )

      val expected: Either[SomeError, Int] = Right(1)

      val fa2    = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val actual =
        futureToValue(CanRecover[Future].recoverEitherFromNonFatal(fa2) { case _ => expected }, waitFor)

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverEitherFromNonFatalShouldReturnSuccessfulResult: Result = {

      val expected: Either[SomeError, Int] = Right(1)

      val fa     = run[Future, Either[SomeError, Int]](Right(1))
      val actual = futureToValue(
        CanRecover[Future]
          .recoverEitherFromNonFatal(fa) {
            case err => Left(SomeError.someThrowable(err))
          },
        waitFor,
      )

      actual ==== expected
    }

    def testCanRecover_Future_recoverEitherFromNonFatalShouldReturnFailedResult: Result = {

      val expectedFailure                  = SomeError.message("Failed")
      val expected: Either[SomeError, Int] = Left(expectedFailure)

      val fa     = run[Future, Either[SomeError, Int]](Left(expectedFailure))
      val actual =
        futureToValue(CanRecover[Future].recoverEitherFromNonFatal(fa) { case NonFatal(_) => expected }, waitFor)

      actual ==== expected
    }

  }

}
