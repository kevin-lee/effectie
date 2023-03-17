package effectie.instances.tries

import cats._
import cats.instances.all._
import cats.syntax.all._
import effectie.SomeControlThrowable
import effectie.core._
import effectie.testing.types.SomeError
import extras.concurrent.testing.types.ErrorLogger
import hedgehog._
import hedgehog.runner._

import scala.util.control.{ControlThrowable, NonFatal}
import scala.util.{Success, Try}

/** @author Kevin Lee
  * @since 2023-03-17
  */
object canRecoverSpec extends Properties {
  implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] =
    trySpecs

  /* Try */
  val trySpecs = List(
    example(
      "test CanRecover[Try].recoverFromNonFatalWith should catch NonFatal",
      TrySpec.testCanRecover_Try_recoverFromNonFatalWithShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Try].recoverFromNonFatalWith should not catch Fatal",
      TrySpec.testCanRecover_Try_recoverFromNonFatalWithShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Try].recoverFromNonFatalWith should return the successful result",
      TrySpec.testCanRecover_Try_recoverFromNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Try].recoverFromNonFatalWithEither should catch NonFatal",
      TrySpec.testCanRecover_Try_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Try].recoverFromNonFatalWithEither should not catch Fatal",
      TrySpec.testCanRecover_Try_recoverFromNonFatalWithEitherShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Try].recoverFromNonFatalWithEither should return the successful result",
      TrySpec.testCanRecover_Try_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Try].recoverFromNonFatalWithEither should return the failed result",
      TrySpec.testCanRecover_Try_recoverFromNonFatalWithEitherShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[Try].recoverEitherFromNonFatalWith should catch NonFatal",
      TrySpec.testCanRecover_Try_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Try].recoverEitherFromNonFatalWith should not catch Fatal",
      TrySpec.testCanRecover_Try_recoverEitherFromNonFatalWithShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Try].recoverEitherFromNonFatalWith should return the successful result",
      TrySpec.testCanRecover_Try_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Try].recoverEitherFromNonFatalWith should return the failed result",
      TrySpec.testCanRecover_Try_recoverEitherFromNonFatalWithShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[Try].recoverFromNonFatal should catch NonFatal",
      TrySpec.testCanRecover_Try_recoverFromNonFatalShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Try].recoverFromNonFatal should not catch Fatal",
      TrySpec.testCanRecover_Try_recoverFromNonFatalShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Try].recoverFromNonFatal should return the successful result",
      TrySpec.testCanRecover_Try_recoverFromNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Try].recoverFromNonFatalEither should catch NonFatal",
      TrySpec.testCanRecover_Try_recoverFromNonFatalEitherShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Try].recoverFromNonFatalEither should not catch Fatal",
      TrySpec.testCanRecover_Try_recoverFromNonFatalEitherShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Try].recoverFromNonFatalEither should return the successful result",
      TrySpec.testCanRecover_Try_recoverFromNonFatalEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Try].recoverFromNonFatalEither should return the failed result",
      TrySpec.testCanRecover_Try_recoverFromNonFatalEitherShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[Try].recoverEitherFromNonFatal should catch NonFatal",
      TrySpec.testCanRecover_Try_recoverEitherFromNonFatalShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Try].recoverEitherFromNonFatal should not catch Fatal",
      TrySpec.testCanRecover_Try_recoverEitherFromNonFatalShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Try].recoverEitherFromNonFatal should return the successful result",
      TrySpec.testCanRecover_Try_recoverEitherFromNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Try].recoverEitherFromNonFatal should return the failed result",
      TrySpec.testCanRecover_Try_recoverEitherFromNonFatalShouldReturnFailedResult,
    ),
  )

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor: Functor, A](a: => A): F[A] =
    FxCtor[F].effectOf(a)

  object TrySpec {
    import effectie.instances.tries.canRecover._
    import effectie.instances.tries.fxCtor._

    def testCanRecover_Try_recoverFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong 1")
      val fa                = run[Try, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 123
      val actual            = CanRecover[Try]
        .recoverFromNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) =>
            FxCtor[Try].pureOf(expected)
        }

      actual ==== Success(expected)
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_Try_recoverFromNonFatalWithShouldNotCatchFatal: Result = {

      val expectedExpcetion = SomeControlThrowable("Something's wrong 2")

      try {
        val fa     = run[Try, Int](throwThrowable[Int](expectedExpcetion))
        val actual = CanRecover[Try].recoverFromNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) => FxCtor[Try].pureOf(123)
        }
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Try_recoverFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = run[Try, Int](1)
      val expected = 1
      val actual   = CanRecover[Try]
        .recoverFromNonFatalWith(fa) {
          case NonFatal(_) => FxCtor[Try].pureOf(999)
        }

      actual ==== Success(expected)
    }

    def testCanRecover_Try_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong 3")
      val fa = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
      val actualFailedResult   = CanRecover[Try]
        .recoverFromNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) => FxCtor[Try].pureOf(expectedFailedResult)
        }

      val expectedSuccessResult = 1.asRight[SomeError]
      val actualSuccessResult   = CanRecover[Try]
        .recoverFromNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) => FxCtor[Try].pureOf(1.asRight[SomeError])
        }

      actualFailedResult ==== Success(expectedFailedResult) and actualSuccessResult ==== Success(expectedSuccessResult)
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_Try_recoverFromNonFatalWithEitherShouldNotCatchFatal: Result = {

      val expectedExpcetion = SomeControlThrowable("Something's wrong 4")
      try {
        val fa = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

        val actual = CanRecover[Try].recoverFromNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) => FxCtor[Try].pureOf(123.asRight[SomeError])
        }
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Try_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[Try, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanRecover[Try]
        .recoverFromNonFatalWith(fa) {
          case NonFatal(_) => Try(999.asRight[SomeError])
        }

      actual ==== Success(expected)
    }

    def testCanRecover_Try_recoverFromNonFatalWithEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Try, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanRecover[Try]
        .recoverFromNonFatalWith(fa) {
          case NonFatal(_) => FxCtor[Try].pureOf(123.asRight[SomeError])
        }

      actual ==== Success(expected)
    }

    def testCanRecover_Try_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong 5")
      val fa = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   = CanRecover[Try]
        .recoverEitherFromNonFatalWith(fa) {
          case err => FxCtor[Try].pureOf(SomeError.someThrowable(err).asLeft[Int])
        }

      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   = CanRecover[Try]
        .recoverEitherFromNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) => FxCtor[Try].pureOf(123.asRight[SomeError])
        }

      actualFailedResult ==== Success(expectedFailedResult) and actualSuccessResult ==== Success(expectedSuccessResult)
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_Try_recoverEitherFromNonFatalWithShouldNotCatchFatal: Result = {

      val expectedExpcetion = SomeControlThrowable("Something's wrong 6")
      try {
        val fa = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

        val actual = CanRecover[Try].recoverEitherFromNonFatalWith(fa) {
          case err => FxCtor[Try].pureOf(SomeError.someThrowable(err).asLeft[Int])
        }
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Try_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = run[Try, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanRecover[Try]
        .recoverEitherFromNonFatalWith(fa) {
          case NonFatal(_) => FxCtor[Try].pureOf(123.asRight[SomeError])
        }

      actual ==== Success(expected)
    }

    def testCanRecover_Try_recoverEitherFromNonFatalWithShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Try, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        CanRecover[Try]
          .recoverEitherFromNonFatalWith(fa) {
            case NonFatal(_) => FxCtor[Try].pureOf(123.asRight[SomeError])
          }

      actual ==== Success(expected)
    }

    // /

    def testCanRecover_Try_recoverFromNonFatalShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong 7")
      val fa                = run[Try, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 123
      val actual            = CanRecover[Try]
        .recoverFromNonFatal(fa) {
          case NonFatal(`expectedExpcetion`) =>
            expected
        }

      actual ==== Success(expected)
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_Try_recoverFromNonFatalShouldNotCatchFatal: Result = {

      val expectedExpcetion = SomeControlThrowable("Something's wrong 8")
      try {
        val fa = run[Try, Int](throwThrowable[Int](expectedExpcetion))

        val actual = CanRecover[Try].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123 }
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Try_recoverFromNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = run[Try, Int](1)
      val expected = 1
      val actual   = CanRecover[Try].recoverFromNonFatal(fa) { case NonFatal(_) => 999 }

      actual ==== Success(expected)
    }

    def testCanRecover_Try_recoverFromNonFatalEitherShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong 9")
      val fa = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
      val actualFailedResult   = CanRecover[Try]
        .recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expectedFailedResult }

      val expectedSuccessResult = 1.asRight[SomeError]
      val actualSuccessResult   = CanRecover[Try]
        .recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }

      actualFailedResult ==== Success(expectedFailedResult) and actualSuccessResult ==== Success(expectedSuccessResult)
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_Try_recoverFromNonFatalEitherShouldNotCatchFatal: Result = {

      val expectedExpcetion = SomeControlThrowable("Something's wrong 10")
      try {
        val fa = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

        val actual = CanRecover[Try].recoverFromNonFatal(fa) {
          case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError]
        }
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Try_recoverFromNonFatalEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[Try, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanRecover[Try].recoverFromNonFatal(fa) { case NonFatal(_) => 999.asRight[SomeError] }

      actual ==== Success(expected)
    }

    def testCanRecover_Try_recoverFromNonFatalEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Try, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanRecover[Try].recoverFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }

      actual ==== Success(expected)
    }

    def testCanRecover_Try_recoverEitherFromNonFatalShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong 11")
      val fa = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   =
        CanRecover[Try]
          .recoverEitherFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }

      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        CanRecover[Try]
          .recoverEitherFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }

      actualFailedResult ==== Success(expectedFailedResult) and actualSuccessResult ==== Success(expectedSuccessResult)
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_Try_recoverEitherFromNonFatalShouldNotCatchFatal: Result = {

      val expectedExpcetion = SomeControlThrowable("Something's wrong 12")
      try {
        val fa = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

        val actual =
          CanRecover[Try].recoverEitherFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }

        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Try_recoverEitherFromNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = run[Try, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   =
        CanRecover[Try]
          .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }

      actual ==== Success(expected)
    }

    def testCanRecover_Try_recoverEitherFromNonFatalShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Try, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        CanRecover[Try]
          .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }

      actual ==== Success(expected)
    }

  }

}
