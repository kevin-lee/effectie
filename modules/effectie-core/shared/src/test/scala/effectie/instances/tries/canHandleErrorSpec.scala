package effectie.instances.tries

import cats.syntax.all._
import effectie.SomeControlThrowable
import effectie.core._
import effectie.testing.types.SomeError
import hedgehog._
import hedgehog.runner._

import scala.util.control.{ControlThrowable, NonFatal}
import scala.util.{Success, Try}

/** @author Kevin Lee
  * @since 2023-03-17
  */
object canHandleErrorSpec extends Properties {

  override def tests: List[Test] = ioSpecs

  /* Try */
  private val ioSpecs = List(
    example(
      "test CanHandleError[Try].handleNonFatalWith should handle NonFatal",
      TrySpec.testCanHandleError_Try_handleNonFatalWithShouldHandleNonFatalWith,
    ),
    example(
      "test CanHandleError[Try].handleNonFatalWith should not handle Fatal",
      TrySpec.testCanHandleError_Try_handleNonFatalWithShouldNotHandleFatalWith,
    ),
    example(
      "test CanHandleError[Try].handleNonFatalWith should return the successful result",
      TrySpec.testCanHandleError_Try_handleNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Try].handleNonFatalWithEither should handle NonFatal",
      TrySpec.testCanHandleError_Try_handleNonFatalWithEitherShouldHandleNonFatalWith,
    ),
    example(
      "test CanHandleError[Try].handleNonFatalWithEither should not handle Fatal",
      TrySpec.testCanHandleError_Try_handleNonFatalWithEitherShouldNotHandleFatalWith,
    ),
    example(
      "test CanHandleError[Try].handleNonFatalWithEither should return the successful result",
      TrySpec.testCanHandleError_Try_handleNonFatalWithEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Try].handleNonFatalWithEither should return the failed result",
      TrySpec.testCanHandleError_Try_handleNonFatalWithEitherShouldReturnFailedResult,
    ),
    example(
      "test CanHandleError[Try].handleEitherNonFatalWith should handle NonFatal",
      TrySpec.testCanHandleError_Try_handleEitherNonFatalWithShouldHandleNonFatalWith,
    ),
    example(
      "test CanHandleError[Try].handleEitherNonFatalWith should not handle Fatal",
      TrySpec.testCanHandleError_Try_handleEitherNonFatalWithShouldNotHandleFatalWith,
    ),
    example(
      "test CanHandleError[Try].handleEitherNonFatalWith should return the successful result",
      TrySpec.testCanHandleError_Try_handleEitherNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Try].handleEitherNonFatalWith should return the failed result",
      TrySpec.testCanHandleError_Try_handleEitherNonFatalWithShouldReturnFailedResult,
    ),
    example(
      "test CanHandleError[Try].handleNonFatal should handle NonFatal",
      TrySpec.testCanHandleError_Try_handleNonFatalShouldHandleNonFatal,
    ),
    example(
      "test CanHandleError[Try].handleNonFatal should not handle Fatal",
      TrySpec.testCanHandleError_Try_handleNonFatalShouldNotHandleFatal,
    ),
    example(
      "test CanHandleError[Try].handleNonFatal should return the successful result",
      TrySpec.testCanHandleError_Try_handleNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Try].handleNonFatalEither should handle NonFatal",
      TrySpec.testCanHandleError_Try_handleNonFatalEitherShouldHandleNonFatal,
    ),
    example(
      "test CanHandleError[Try].handleNonFatalEither should not handle Fatal",
      TrySpec.testCanHandleError_Try_handleNonFatalEitherShouldNotHandleFatal,
    ),
    example(
      "test CanHandleError[Try].handleNonFatalEither should return the successful result",
      TrySpec.testCanHandleError_Try_handleNonFatalEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Try].handleNonFatalEither should return the failed result",
      TrySpec.testCanHandleError_Try_handleNonFatalEitherShouldReturnFailedResult,
    ),
    example(
      "test CanHandleError[Try].handleEitherNonFatal should handle NonFatal",
      TrySpec.testCanHandleError_Try_handleEitherNonFatalShouldHandleNonFatal,
    ),
    example(
      "test CanHandleError[Try].handleEitherNonFatal should not handle Fatal",
      TrySpec.testCanHandleError_Try_handleEitherNonFatalShouldNotHandleFatal,
    ),
    example(
      "test CanHandleError[Try].handleEitherNonFatal should return the successful result",
      TrySpec.testCanHandleError_Try_handleEitherNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Try].handleEitherNonFatal should return the failed result",
      TrySpec.testCanHandleError_Try_handleEitherNonFatalShouldReturnFailedResult,
    ),
  )

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor, A](a: => A): F[A] =
    FxCtor[F].effectOf(a)

  object TrySpec {
    import effectie.instances.tries.canHandleError._
    import effectie.instances.tries.fxCtor._

    def testCanHandleError_Try_handleNonFatalWithShouldHandleNonFatalWith: Result = {

      val expectedException = new RuntimeException("Something's wrong 1")
      val fa                = run[Try, Int](throwThrowable[Int](expectedException))
      val expected          = 123
      val actual            = CanHandleError[Try]
        .handleNonFatalWith(fa) {
          case NonFatal(`expectedException`) =>
            FxCtor[Try].pureOf(expected)

          case err =>
            throw err // scalafix:ok DisableSyntax.throw
        }

      actual ==== Success(expected)
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Try_handleNonFatalWithShouldNotHandleFatalWith: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong 2")

      try {
        val fa     = run[Try, Int](throwThrowable[Int](fatalExpcetion))
        val actual = CanHandleError[Try].handleNonFatalWith(fa)(_ => FxCtor[Try].pureOf(123))
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Try_handleNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = run[Try, Int](1)
      val expected = 1
      val actual   = CanHandleError[Try].handleNonFatalWith(fa)(_ => FxCtor[Try].pureOf(999))

      actual ==== Success(expected)
    }

    def testCanHandleError_Try_handleNonFatalWithEitherShouldHandleNonFatalWith: Result = {

      val expectedException = new RuntimeException("Something's wrong 3")
      val fa = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
      val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
      val actualFailedResult   =
        CanHandleError[Try].handleNonFatalWith(fa)(_ => FxCtor[Try].pureOf(expectedFailedResult))

      val expectedSuccessResult = 1.asRight[SomeError]
      val actualSuccessResult   =
        CanHandleError[Try].handleNonFatalWith(fa)(_ => FxCtor[Try].pureOf(1.asRight[SomeError]))

      actualFailedResult ==== Success(expectedFailedResult) and actualSuccessResult ==== Success(expectedSuccessResult)
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Try_handleNonFatalWithEitherShouldNotHandleFatalWith: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong 4")

      try {
        val fa     = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))
        val actual = CanHandleError[Try].handleNonFatalWith(fa)(_ => FxCtor[Try].pureOf(123.asRight[SomeError]))
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: SomeControlThrowable =>
          ex.getMessage ==== fatalExpcetion.getMessage

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Try_handleNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[Try, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanHandleError[Try].handleNonFatalWith(fa)(_ => Try(999.asRight[SomeError]))

      actual ==== Success(expected)
    }

    def testCanHandleError_Try_handleNonFatalWithEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Try, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanHandleError[Try].handleNonFatalWith(fa)(_ => FxCtor[Try].pureOf(123.asRight[SomeError]))

      actual ==== Success(expected)
    }

    def testCanHandleError_Try_handleEitherNonFatalWithShouldHandleNonFatalWith: Result = {

      val expectedException = new RuntimeException("Something's wrong 5")
      val fa = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
      val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
      val actualFailedResult   = CanHandleError[Try]
        .handleEitherNonFatalWith(fa)(err => FxCtor[Try].pureOf(SomeError.someThrowable(err).asLeft[Int]))

      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        CanHandleError[Try].handleEitherNonFatalWith(fa)(_ => FxCtor[Try].pureOf(123.asRight[SomeError]))

      actualFailedResult ==== Success(expectedFailedResult) and actualSuccessResult ==== Success(expectedSuccessResult)
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Try_handleEitherNonFatalWithShouldNotHandleFatalWith: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong 6")

      try {
        val fa     = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))
        val actual =
          CanHandleError[Try]
            .handleEitherNonFatalWith(fa)(err => FxCtor[Try].pureOf(SomeError.someThrowable(err).asLeft[Int]))

        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Try_handleEitherNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = run[Try, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   =
        CanHandleError[Try].handleEitherNonFatalWith(fa)(_ => FxCtor[Try].pureOf(123.asRight[SomeError]))

      actual ==== Success(expected)
    }

    def testCanHandleError_Try_handleEitherNonFatalWithShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Try, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        CanHandleError[Try].handleEitherNonFatalWith(fa)(_ => FxCtor[Try].pureOf(123.asRight[SomeError]))

      actual ==== Success(expected)
    }

    def testCanHandleError_Try_handleNonFatalShouldHandleNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong 7")
      val fa                = run[Try, Int](throwThrowable[Int](expectedException))
      val expected          = 123
      val actual            = CanHandleError[Try]
        .handleNonFatal(fa) {
          case NonFatal(`expectedException`) =>
            expected

          case err =>
            throw err // scalafix:ok DisableSyntax.throw
        }

      actual ==== Success(expected)
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Try_handleNonFatalShouldNotHandleFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong 8")

      try {
        val fa     = run[Try, Int](throwThrowable[Int](fatalExpcetion))
        val actual = CanHandleError[Try].handleNonFatal(fa)(_ => 123)
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Try_handleNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = run[Try, Int](1)
      val expected = 1
      val actual   = CanHandleError[Try].handleNonFatal(fa)(_ => 999)

      actual ==== Success(expected)
    }

    def testCanHandleError_Try_handleNonFatalEitherShouldHandleNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong 9")
      val fa = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
      val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
      val actualFailedResult   = CanHandleError[Try].handleNonFatal(fa)(_ => expectedFailedResult)

      val expectedSuccessResult = 1.asRight[SomeError]
      val actualSuccessResult   = CanHandleError[Try].handleNonFatal(fa)(_ => 1.asRight[SomeError])

      actualFailedResult ==== Success(expectedFailedResult) and actualSuccessResult ==== Success(expectedSuccessResult)
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Try_handleNonFatalEitherShouldNotHandleFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong 10")

      try {
        val fa     = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))
        val actual = CanHandleError[Try].handleNonFatal(fa)(_ => 123.asRight[SomeError])
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Try_handleNonFatalEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[Try, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanHandleError[Try].handleNonFatal(fa)(_ => 999.asRight[SomeError])

      actual ==== Success(expected)
    }

    def testCanHandleError_Try_handleNonFatalEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Try, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanHandleError[Try].handleNonFatal(fa)(_ => 123.asRight[SomeError])

      actual ==== Success(expected)
    }

    def testCanHandleError_Try_handleEitherNonFatalShouldHandleNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong 11")
      val fa = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
      val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
      val actualFailedResult   = CanHandleError[Try]
        .handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])

      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        CanHandleError[Try].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError])

      actualFailedResult ==== Success(expectedFailedResult) and actualSuccessResult ==== Success(expectedSuccessResult)
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Try_handleEitherNonFatalShouldNotHandleFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong 12")

      try {
        val fa     = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))
        val actual =
          CanHandleError[Try]
            .handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])

        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Try_handleEitherNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = run[Try, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanHandleError[Try].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError])

      actual ==== Success(expected)
    }

    def testCanHandleError_Try_handleEitherNonFatalShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Try, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        CanHandleError[Try].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError])

      actual ==== Success(expected)
    }

  }

}
