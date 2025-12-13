package effectie.instances.future

import effectie.core.{CanHandleError, FxCtor}
import effectie.testing.FutureTools
import effectie.testing.types.SomeError
import hedgehog._
import hedgehog.runner._

/** @author Kevin Lee
  * @since 2022-01-06
  */
object canHandleErrorSpec extends Properties {

  override def tests: List[Test] = futureSpecs

  /* Future */
  val futureSpecs = List(
    example(
      "test CanHandleError[Future].handleNonFatalWith should handle NonFatal",
      FutureSpec.testCanHandleError_Future_handleNonFatalWithShouldHandleNonFatalWith,
    ),
    example(
      "test CanHandleError[Future].handleNonFatalWith should return the successful result",
      FutureSpec.testCanHandleError_Future_handleNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Future].handleNonFatalWithEither should handle NonFatal",
      FutureSpec.testCanHandleError_Future_handleNonFatalWithEitherShouldHandleNonFatalWith,
    ),
    example(
      "test CanHandleError[Future].handleNonFatalWithEither should return the successful result",
      FutureSpec.testCanHandleError_Future_handleNonFatalWithEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Future].handleNonFatalWithEither should return the failed result",
      FutureSpec.testCanHandleError_Future_handleNonFatalWithEitherShouldReturnFailedResult,
    ),
    example(
      "test CanHandleError[Future].handleEitherNonFatalWith should handle NonFatal",
      FutureSpec.testCanHandleError_Future_handleEitherNonFatalWithShouldHandleNonFatalWith,
    ),
    example(
      "test CanHandleError[Future].handleEitherNonFatalWith should return the successful result",
      FutureSpec.testCanHandleError_Future_handleEitherNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Future].handleEitherNonFatalWith should return the failed result",
      FutureSpec.testCanHandleError_Future_handleEitherNonFatalWithShouldReturnFailedResult,
    ),
    example(
      "test CanHandleError[Future].handleNonFatal should handle NonFatal",
      FutureSpec.testCanHandleError_Future_handleNonFatalShouldHandleNonFatal,
    ),
    example(
      "test CanHandleError[Future].handleNonFatal should return the successful result",
      FutureSpec.testCanHandleError_Future_handleNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Future].handleNonFatalEither should handle NonFatal",
      FutureSpec.testCanHandleError_Future_handleNonFatalEitherShouldHandleNonFatal,
    ),
    example(
      "test CanHandleError[Future].handleNonFatalEither should return the successful result",
      FutureSpec.testCanHandleError_Future_handleNonFatalEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Future].handleNonFatalEither should return the failed result",
      FutureSpec.testCanHandleError_Future_handleNonFatalEitherShouldReturnFailedResult,
    ),
    example(
      "test CanHandleError[Future].handleEitherNonFatal should handle NonFatal",
      FutureSpec.testCanHandleError_Future_handleEitherNonFatalShouldHandleNonFatal,
    ),
    example(
      "test CanHandleError[Future].handleEitherNonFatal should return the successful result",
      FutureSpec.testCanHandleError_Future_handleEitherNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Future].handleEitherNonFatal should return the failed result",
      FutureSpec.testCanHandleError_Future_handleEitherNonFatalShouldReturnFailedResult,
    ),
  )

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  private def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  private def run[F[*]: FxCtor, A](a: => A): F[A] =
    FxCtor[F].effectOf(a)

  object FutureSpec extends FutureTools {
    import effectie.instances.future.canHandleError._
    import effectie.instances.future.fxCtor._

    import scala.concurrent.duration._
    import scala.concurrent.{ExecutionContext, Future}

    implicit val ec: ExecutionContext = globalExecutionContext

    val waitFor = WaitFor(1.second)

    def testCanHandleError_Future_handleNonFatalWithShouldHandleNonFatalWith: Result = {

      val expectedException = new RuntimeException("Something's wrong")
      val fa                = run[Future, Int](throwThrowable[Int](expectedException))
      val expected          = 1
      val actual            = futureToValue(
        CanHandleError[Future].handleNonFatalWith(fa)(_ => Future(expected)),
        waitFor,
      )

      actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = run[Future, Int](1)
      val expected = 1
      val actual   = futureToValue(
        CanHandleError[Future].handleNonFatalWith(fa)(_ => Future(123)),
        waitFor,
      )

      actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalWithEitherShouldHandleNonFatalWith: Result = {

      val expectedException                            = new RuntimeException("Something's wrong")
      val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedException))

      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
      val actualFailedResult =
        futureToValue(
          CanHandleError[Future].handleNonFatalWith(fa)(err => Future(Left(SomeError.someThrowable(err)))),
          waitFor,
        )

      val expected: Either[SomeError, Int] = Right(1)

      val fa2    = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
      val actual = futureToValue(CanHandleError[Future].handleNonFatalWith(fa2)(_ => Future(expected)), waitFor)

      expectedFailedResult ==== actualFailedResult and actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

      val expected: Either[SomeError, Int] = Right(1)

      val fa     = run[Future, Either[SomeError, Int]](Right(1))
      val actual =
        futureToValue(
          CanHandleError[Future].handleNonFatalWith(fa)(err => Future(Left(SomeError.someThrowable(err)))),
          waitFor,
        )

      actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalWithEitherShouldReturnFailedResult: Result = {

      val expectedFailure                  = SomeError.message("Failed")
      val expected: Either[SomeError, Int] = Left(expectedFailure)

      val fa     = run[Future, Either[SomeError, Int]](Left(expectedFailure))
      val actual = futureToValue(CanHandleError[Future].handleNonFatalWith(fa)(_ => Future(Right(1))), waitFor)

      actual ==== expected
    }

    def testCanHandleError_Future_handleEitherNonFatalWithShouldHandleNonFatalWith: Result = {

      val expectedException                            = new RuntimeException("Something's wrong")
      val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedException))

      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
      val actualFailedResult = futureToValue(
        CanHandleError[Future]
          .handleEitherNonFatalWith(fa)(err => Future(Left(SomeError.someThrowable(err)))),
        waitFor,
      )

      val expected: Either[SomeError, Int] = Right(1)

      val fa2    = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
      val actual =
        futureToValue(CanHandleError[Future].handleEitherNonFatalWith(fa2)(_ => Future(expected)), waitFor)

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanHandleError_Future_handleEitherNonFatalWithShouldReturnSuccessfulResult: Result = {

      val expected: Either[SomeError, Int] = Right(1)

      val fa     = run[Future, Either[SomeError, Int]](Right(1))
      val actual = futureToValue(
        CanHandleError[Future]
          .handleEitherNonFatalWith(fa)(err => Future(Left(SomeError.someThrowable(err)))),
        waitFor,
      )

      actual ==== expected
    }

    def testCanHandleError_Future_handleEitherNonFatalWithShouldReturnFailedResult: Result = {

      val expectedFailure                  = SomeError.message("Failed")
      val expected: Either[SomeError, Int] = Left(expectedFailure)

      val fa     = run[Future, Either[SomeError, Int]](Left(expectedFailure))
      val actual =
        futureToValue(CanHandleError[Future].handleEitherNonFatalWith(fa)(_ => Future(expected)), waitFor)

      actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalShouldHandleNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong")
      val fa                = run[Future, Int](throwThrowable[Int](expectedException))
      val expected          = 1
      val actual            = futureToValue(CanHandleError[Future].handleNonFatal(fa)(_ => expected), waitFor)

      actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = run[Future, Int](1)
      val expected = 1
      val actual   = futureToValue(CanHandleError[Future].handleNonFatal(fa)(_ => 123), waitFor)

      actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalEitherShouldHandleNonFatal: Result = {

      val expectedException                            = new RuntimeException("Something's wrong")
      val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedException))

      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
      val actualFailedResult =
        futureToValue(
          CanHandleError[Future].handleNonFatal(fa)(err => Left(SomeError.someThrowable(err))),
          waitFor,
        )

      val expected: Either[SomeError, Int] = Right(1)

      val fa2    = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
      val actual = futureToValue(CanHandleError[Future].handleNonFatal(fa2)(_ => expected), waitFor)

      expectedFailedResult ==== actualFailedResult and actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalEitherShouldReturnSuccessfulResult: Result = {

      val expected: Either[SomeError, Int] = Right(1)

      val fa     = run[Future, Either[SomeError, Int]](Right(1))
      val actual =
        futureToValue(CanHandleError[Future].handleNonFatal(fa)(err => Left(SomeError.someThrowable(err))), waitFor)

      actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalEitherShouldReturnFailedResult: Result = {

      val expectedFailure                  = SomeError.message("Failed")
      val expected: Either[SomeError, Int] = Left(expectedFailure)

      val fa     = run[Future, Either[SomeError, Int]](Left(expectedFailure))
      val actual = futureToValue(CanHandleError[Future].handleNonFatal(fa)(_ => Right(1)), waitFor)

      actual ==== expected
    }

    def testCanHandleError_Future_handleEitherNonFatalShouldHandleNonFatal: Result = {

      val expectedException                            = new RuntimeException("Something's wrong")
      val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedException))

      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
      val actualFailedResult = futureToValue(
        CanHandleError[Future].handleEitherNonFatal(fa)(err => Left(SomeError.someThrowable(err))),
        waitFor,
      )

      val expected: Either[SomeError, Int] = Right(1)

      val fa2    = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
      val actual =
        futureToValue(CanHandleError[Future].handleEitherNonFatal(fa2)(_ => expected), waitFor)

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanHandleError_Future_handleEitherNonFatalShouldReturnSuccessfulResult: Result = {

      val expected: Either[SomeError, Int] = Right(1)

      val fa     = run[Future, Either[SomeError, Int]](Right(1))
      val actual = futureToValue(
        CanHandleError[Future].handleEitherNonFatal(fa)(err => Left(SomeError.someThrowable(err))),
        waitFor,
      )

      actual ==== expected
    }

    def testCanHandleError_Future_handleEitherNonFatalShouldReturnFailedResult: Result = {

      val expectedFailure                  = SomeError.message("Failed")
      val expected: Either[SomeError, Int] = Left(expectedFailure)

      val fa     = run[Future, Either[SomeError, Int]](Left(expectedFailure))
      val actual =
        futureToValue(CanHandleError[Future].handleEitherNonFatal(fa)(_ => expected), waitFor)

      actual ==== expected
    }

  }

}
