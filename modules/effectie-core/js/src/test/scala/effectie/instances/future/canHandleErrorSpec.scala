package effectie.instances.future

import effectie.core.{CanHandleError, FxCtor}
import effectie.testing.FutureTools
import effectie.testing.types.SomeError

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/** @author Kevin Lee
  * @since 2022-01-06
  */
class canHandleErrorSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  override val munitTimeout: FiniteDuration = 200.milliseconds

  /* Future */

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  private def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  private def run[F[*]: FxCtor, A](a: => A): F[A] =
    FxCtor[F].effectOf(a)

  import effectie.instances.future.canHandleError._
  import effectie.instances.future.fxCtor._

  import scala.concurrent.Future

  test("test CanHandleError[Future].handleNonFatalWith should handle NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedException))
    val expected          = 1

    CanHandleError[Future].handleNonFatalWith(fa)(_ => Future(expected)).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanHandleError[Future].handleNonFatalWith should return the successful result") {

    val fa       = run[Future, Int](1)
    val expected = 1

    CanHandleError[Future].handleNonFatalWith(fa)(_ => Future(123)).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanHandleError[Future].handleNonFatalWithEither should handle NonFatal") {

    val expectedException                            = new RuntimeException("Something's wrong")
    val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedException))

    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))

    val failedResult =
      CanHandleError[Future].handleNonFatalWith(fa)(err => Future(Left(SomeError.someThrowable(err)))).map { actual =>
        assertEquals(actual, expectedFailedResult)
      }

    val expected: Either[SomeError, Int] = Right(1)

    val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))

    val handledResult = CanHandleError[Future].handleNonFatalWith(fa2)(_ => Future(expected)).map { actual =>
      assertEquals(actual, expected)
    }

    Future.sequence(
      List(
        failedResult,
        handledResult,
      )
    )

  }

  test("test CanHandleError[Future].handleNonFatalWithEither should return the successful result") {

    val expected: Either[SomeError, Int] = Right(1)

    val fa = run[Future, Either[SomeError, Int]](Right(1))

    CanHandleError[Future].handleNonFatalWith(fa)(err => Future(Left(SomeError.someThrowable(err)))).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanHandleError[Future].handleNonFatalWithEither should return the failed result") {

    val expectedFailure                  = SomeError.message("Failed")
    val expected: Either[SomeError, Int] = Left(expectedFailure)

    val fa = run[Future, Either[SomeError, Int]](Left(expectedFailure))

    CanHandleError[Future].handleNonFatalWith(fa)(_ => Future(Right(1))).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanHandleError[Future].handleEitherNonFatalWith should handle NonFatal") {

    val expectedException                            = new RuntimeException("Something's wrong")
    val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedException))

    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))

    val failedResult = CanHandleError[Future]
      .handleEitherNonFatalWith(fa)(err => Future(Left(SomeError.someThrowable(err))))
      .map { actual =>
        assertEquals(actual, expectedFailedResult)
      }

    val expected: Either[SomeError, Int] = Right(1)

    val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))

    val handledResult = CanHandleError[Future].handleEitherNonFatalWith(fa2)(_ => Future(expected)).map { actual =>
      assertEquals(actual, expected)
    }

    Future.sequence(
      List(
        failedResult,
        handledResult,
      )
    )
  }

  test("test CanHandleError[Future].handleEitherNonFatalWith should return the successful result") {

    val expected: Either[SomeError, Int] = Right(1)

    val fa = run[Future, Either[SomeError, Int]](Right(1))

    CanHandleError[Future]
      .handleEitherNonFatalWith(fa)(err => Future(Left(SomeError.someThrowable(err))))
      .map { actual =>
        assertEquals(actual, expected)
      }

  }

  test("test CanHandleError[Future].handleEitherNonFatalWith should return the failed result") {

    val expectedFailure                  = SomeError.message("Failed")
    val expected: Either[SomeError, Int] = Left(expectedFailure)

    val fa = run[Future, Either[SomeError, Int]](Left(expectedFailure))

    CanHandleError[Future].handleEitherNonFatalWith(fa)(_ => Future(expected)).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanHandleError[Future].handleNonFatal should handle NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedException))
    val expected          = 1
    CanHandleError[Future].handleNonFatal(fa)(_ => expected).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanHandleError[Future].handleNonFatal should return the successful result") {

    val fa       = run[Future, Int](1)
    val expected = 1

    CanHandleError[Future].handleNonFatal(fa)(_ => 123).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanHandleError[Future].handleNonFatalEither should handle NonFatal") {

    val expectedException                            = new RuntimeException("Something's wrong")
    val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedException))

    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))

    val failedResult =
      CanHandleError[Future].handleNonFatal(fa)(err => Left(SomeError.someThrowable(err))).map { actual =>
        assertEquals(actual, expectedFailedResult)
      }

    val expected: Either[SomeError, Int] = Right(1)

    val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))

    val handledResult =
      CanHandleError[Future].handleNonFatal(fa2)(_ => expected).map { actual =>
        assertEquals(actual, expected)
      }

    Future.sequence(
      List(
        failedResult,
        handledResult,
      )
    )

  }

  test("test CanHandleError[Future].handleNonFatalEither should return the successful result") {

    val expected: Either[SomeError, Int] = Right(1)

    val fa = run[Future, Either[SomeError, Int]](Right(1))

    CanHandleError[Future].handleNonFatal(fa)(err => Left(SomeError.someThrowable(err))).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanHandleError[Future].handleNonFatalEither should return the failed result") {

    val expectedFailure                  = SomeError.message("Failed")
    val expected: Either[SomeError, Int] = Left(expectedFailure)

    val fa = run[Future, Either[SomeError, Int]](Left(expectedFailure))

    CanHandleError[Future].handleNonFatal(fa)(_ => Right(1)).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanHandleError[Future].handleEitherNonFatal should handle NonFatal") {

    val expectedException                            = new RuntimeException("Something's wrong")
    val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedException))

    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))

    val failedResult =
      CanHandleError[Future].handleEitherNonFatal(fa)(err => Left(SomeError.someThrowable(err))).map { actual =>
        assertEquals(actual, expectedFailedResult)
      }

    val expected: Either[SomeError, Int] = Right(1)

    val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))

    val handledResult =
      CanHandleError[Future].handleEitherNonFatal(fa2)(_ => expected).map { actual =>
        assertEquals(actual, expected)
      }

    Future.sequence(
      List(
        failedResult,
        handledResult,
      )
    )
  }

  test("test CanHandleError[Future].handleEitherNonFatal should return the successful result") {

    val expected: Either[SomeError, Int] = Right(1)

    val fa = run[Future, Either[SomeError, Int]](Right(1))

    CanHandleError[Future].handleEitherNonFatal(fa)(err => Left(SomeError.someThrowable(err))).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanHandleError[Future].handleEitherNonFatal should return the failed result") {

    val expectedFailure                  = SomeError.message("Failed")
    val expected: Either[SomeError, Int] = Left(expectedFailure)

    val fa = run[Future, Either[SomeError, Int]](Left(expectedFailure))

    CanHandleError[Future].handleEitherNonFatal(fa)(_ => expected).map { actual =>
      assertEquals(actual, expected)
    }

  }

}
