package effectie.instances.future

import cats.data.EitherT
import cats.syntax.all._
import effectie.core._
import effectie.syntax.all._
import effectie.testing.FutureTools
import effectie.testing.types.SomeError
import munit.Assertions

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/** @author Kevin Lee
  * @since 2020-08-17
  */
class canHandleErrorFutureSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  override val munitTimeout: FiniteDuration = 200.milliseconds

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor, A](a: => A): F[A] =
    FxCtor[F].effectOf(a)

  import effectie.instances.future.canHandleError._
  import effectie.instances.future.fxCtor._

  import scala.concurrent.Future

  test("CanHandleError[Future].handleNonFatalWith should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 1
    CanHandleError[Future].handleNonFatalWith(fa)(_ => Future(expected)).map { actual =>
      Assertions.assertEquals(actual, expected)
    }

  }

  test("CanHandleError[Future].handleNonFatalWith should return successful result") {

    val fa       = run[Future, Int](1)
    val expected = 1
    CanHandleError[Future].handleNonFatalWith(fa)(_ => Future(123)).map { actual =>
      Assertions.assertEquals(actual, expected)
    }

  }

  test("CanHandleError[Future].handleNonFatalWith(Either) should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    val actualFailedResult =
      CanHandleError[Future].handleNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int]))

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected = 1.asRight[SomeError]
    val actualHandledResult = CanHandleError[Future].handleNonFatalWith(fa2)(_ => Future(expected))

    Future.sequence(
      List(
        actualFailedResult.map { actual =>
          Assertions.assertEquals(actual, expectedFailedResult)
        },
        actualHandledResult.map { actual =>
          Assertions.assertEquals(actual, expected)
        },
      )
    )
  }

  test("CanHandleError[Future].handleNonFatalWith(Either) should return successful result") {

    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    CanHandleError[Future]
      .handleNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int]))
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }

  }

  test("CanHandleError[Future].handleNonFatalWith(Either) should return failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    CanHandleError[Future]
      .handleNonFatalWith(fa)(_ => Future(1.asRight[SomeError]))
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }

  }

  test("CanHandleError[Future].handleEitherNonFatalWith should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    val actualFailedResult = CanHandleError[Future]
      .handleEitherNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int]))
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected = 1.asRight[SomeError]
    val actualHandledResult =
      CanHandleError[Future].handleEitherNonFatalWith(fa2)(_ => Future(expected)).map { actual =>
        Assertions.assertEquals(actual, expected)
      }

    Future.sequence(
      List(
        actualFailedResult,
        actualHandledResult,
      )
    )

  }

  test("CanHandleError[Future].handleEitherNonFatalWith should Return successful result") {

    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    CanHandleError[Future]
      .handleEitherNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int]))
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }

  }

  test("CanHandleError[Future].handleEitherNonFatalWith should return failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    CanHandleError[Future].handleEitherNonFatalWith(fa)(_ => Future(expected)).map { actual =>
      Assertions.assertEquals(actual, expected)
    }

  }

  test("CanHandleError[Future].handleEitherTNonFatalWith should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    val actualFailedResult = CanHandleError[Future]
      .handleEitherTNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int]))
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }

    val fa2 = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expected = 1.asRight[SomeError]

    val actualHandledResult =
      CanHandleError[Future].handleEitherTNonFatalWith(fa2)(_ => Future(expected)).value.map { actual =>
        Assertions.assertEquals(actual, expected)
      }

    Future.sequence(
      List(
        actualFailedResult,
        actualHandledResult,
      )
    )

  }

  test("CanHandleError[Future].handleEitherTNonFatalWith should return the successful result") {

    val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]
    CanHandleError[Future]
      .handleEitherTNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int]))
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }

  }

  test("CanHandleError[Future].handleEitherTNonFatalWith should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    CanHandleError[Future].handleEitherTNonFatalWith(fa)(_ => Future(expected)).value.map { actual =>
      Assertions.assertEquals(actual, expected)
    }

  }

  test("CanHandleError[Future].handleNonFatal should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 1

    CanHandleError[Future].handleNonFatal(fa)(_ => expected).map { actual =>
      Assertions.assertEquals(actual, expected)
    }

  }

  test("CanHandleError[Future].handleNonFatal should return successful result") {

    val fa       = run[Future, Int](1)
    val expected = 1

    CanHandleError[Future].handleNonFatal(fa)(_ => 123).map { actual =>
      Assertions.assertEquals(actual, expected)
    }

  }

  test("CanHandleError[Future].handleNonFatal(Either) should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    val actualFailedResult =
      CanHandleError[Future].handleNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]).map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected = 1.asRight[SomeError]
    val actualHandledResult = CanHandleError[Future].handleNonFatal(fa2)(_ => expected).map { actual =>
      Assertions.assertEquals(actual, expected)
    }

    Future.sequence(
      List(
        actualFailedResult,
        actualHandledResult,
      )
    )

  }

  test("CanHandleError[Future].handleNonFatal(Either) should return successful result") {

    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    CanHandleError[Future].handleNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]).map { actual =>
      Assertions.assertEquals(actual, expected)
    }

  }

  test("CanHandleError[Future].handleNonFatal(Either) should return failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    CanHandleError[Future].handleNonFatal(fa)(_ => 1.asRight[SomeError]).map { actual =>
      Assertions.assertEquals(actual, expected)
    }

  }

  test("CanHandleError[Future].handleEitherNonFatal should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    val actualFailedResult = CanHandleError[Future]
      .handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
      .map { actual =>
        Assertions.assertEquals(actual, expectedFailedResult)
      }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected = 1.asRight[SomeError]

    val actualHandledResult =
      CanHandleError[Future].handleEitherNonFatal(fa2)(_ => expected).map { actual =>
        Assertions.assertEquals(actual, expected)
      }

    Future.sequence(
      List(
        actualFailedResult,
        actualHandledResult,
      )
    )

  }

  test("CanHandleError[Future].handleEitherNonFatal should return successful result") {

    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    CanHandleError[Future].handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]).map { actual =>
      Assertions.assertEquals(actual, expected)
    }

  }

  test("CanHandleError[Future].handleEitherNonFatal should return failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    CanHandleError[Future].handleEitherNonFatal(fa)(_ => expected).map { actual =>
      Assertions.assertEquals(actual, expected)
    }

  }

  test("test CanHandleError[Future].handleEitherTNonFatal should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

    val actualFailedResult =
      CanHandleError[Future].handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]).value.map {
        actual =>
          Assertions.assertEquals(actual, expectedFailedResult)
      }

    val fa2 = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expected = 1.asRight[SomeError]

    val actualHandledResult =
      CanHandleError[Future].handleEitherTNonFatal(fa2)(_ => expected).value.map { actual =>
        Assertions.assertEquals(actual, expected)
      }

    Future.sequence(
      List(
        actualFailedResult,
        actualHandledResult,
      )
    )

  }

  test("test CanHandleError[Future].handleEitherTNonFatal should return the successful result") {

    val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    CanHandleError[Future].handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]).value.map {
      actual =>
        Assertions.assertEquals(actual, expected)
    }

  }

  test("test CanHandleError[Future].handleEitherTNonFatal should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    CanHandleError[Future].handleEitherTNonFatal(fa)(_ => expected).value.map { actual =>
      Assertions.assertEquals(actual, expected)
    }

  }

}
