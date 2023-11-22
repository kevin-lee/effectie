package effectie.instances.tries

import cats.syntax.all._
import cats.Eq
import effectie.SomeControlThrowable
import effectie.core._
import effectie.specs.MonadSpec
import effectie.specs
import effectie.testing.types.SomeError
import hedgehog._
import hedgehog.runner._

import scala.util.control.{ControlThrowable, NonFatal}
import scala.util.{Success, Try}

/** @author Kevin Lee
  * @since 2023-03-17
  */
object fxSpec extends Properties {

  override def tests: List[Test] = trySpecs

  import effectie.instances.tries.fx._

  /* Try */
  private val trySpecs = specs.fxSpec.TrySpecs.trySpecs ++
    TrySpec.testMonadLaws ++
    List(
      example(
        "test Fx[Try].catchNonFatalThrowable should catch NonFatal",
        TrySpec.CanCatchSpec.testFx_Try_catchNonFatalThrowableShouldCatchNonFatal,
      ),
      example(
        "test Fx[Try].catchNonFatalThrowable should not catch Fatal",
        TrySpec.CanCatchSpec.testFx_Try_catchNonFatalThrowableShouldNotCatchFatal,
      ),
      example(
        "test Fx[Try].catchNonFatalThrowable should return the successful result",
        TrySpec.CanCatchSpec.testFx_Try_catchNonFatalThrowableShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Try].catchNonFatal should catch NonFatal",
        TrySpec.CanCatchSpec.testFx_Try_catchNonFatalShouldCatchNonFatal,
      ),
      example(
        "test Fx[Try].catchNonFatal should not catch Fatal",
        TrySpec.CanCatchSpec.testFx_Try_catchNonFatalShouldNotCatchFatal,
      ),
      example(
        "test Fx[Try].catchNonFatal should return the successful result",
        TrySpec.CanCatchSpec.testFx_Try_catchNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Try].catchNonFatalEither should catch NonFatal",
        TrySpec.CanCatchSpec.testFx_Try_catchNonFatalEitherShouldCatchNonFatal,
      ),
      example(
        "test Fx[Try].catchNonFatalEither should not catch Fatal",
        TrySpec.CanCatchSpec.testFx_Try_catchNonFatalEitherShouldNotCatchFatal,
      ),
      example(
        "test Fx[Try].catchNonFatalEither should return the successful result",
        TrySpec.CanCatchSpec.testFx_Try_catchNonFatalEitherShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Try].catchNonFatalEither should return the failed result",
        TrySpec.CanCatchSpec.testFx_Try_catchNonFatalEitherShouldReturnFailedResult,
      ),
    ) ++
    List(
      /* Try */
      example(
        "test Fx[Try].handleNonFatalWith should handle NonFatal",
        TrySpec.CanHandleErrorSpec.testCanHandleError_Try_handleNonFatalWithShouldHandleNonFatalWith,
      ),
      example(
        "test Fx[Try].handleNonFatalWith should not handle Fatal",
        TrySpec.CanHandleErrorSpec.testCanHandleError_Try_handleNonFatalWithShouldNotHandleFatalWith,
      ),
      example(
        "test Fx[Try].handleNonFatalWith should return the successful result",
        TrySpec.CanHandleErrorSpec.testCanHandleError_Try_handleNonFatalWithShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Try].handleNonFatalWithEither should handle NonFatal",
        TrySpec.CanHandleErrorSpec.testCanHandleError_Try_handleNonFatalWithEitherShouldHandleNonFatalWith,
      ),
      example(
        "test Fx[Try].handleNonFatalWithEither should not handle Fatal",
        TrySpec.CanHandleErrorSpec.testCanHandleError_Try_handleNonFatalWithEitherShouldNotHandleFatalWith,
      ),
      example(
        "test Fx[Try].handleNonFatalWithEither should return the successful result",
        TrySpec.CanHandleErrorSpec.testCanHandleError_Try_handleNonFatalWithEitherShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Try].handleNonFatalWithEither should return the failed result",
        TrySpec.CanHandleErrorSpec.testCanHandleError_Try_handleNonFatalWithEitherShouldReturnFailedResult,
      ),
      example(
        "test Fx[Try].handleEitherNonFatalWith should handle NonFatal",
        TrySpec.CanHandleErrorSpec.testCanHandleError_Try_handleEitherNonFatalWithShouldHandleNonFatalWith,
      ),
      example(
        "test Fx[Try].handleEitherNonFatalWith should not handle Fatal",
        TrySpec.CanHandleErrorSpec.testCanHandleError_Try_handleEitherNonFatalWithShouldNotHandleFatalWith,
      ),
      example(
        "test Fx[Try].handleEitherNonFatalWith should return the successful result",
        TrySpec.CanHandleErrorSpec.testCanHandleError_Try_handleEitherNonFatalWithShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Try].handleEitherNonFatalWith should return the failed result",
        TrySpec.CanHandleErrorSpec.testCanHandleError_Try_handleEitherNonFatalWithShouldReturnFailedResult,
      ),
      example(
        "test Fx[Try].handleNonFatal should handle NonFatal",
        TrySpec.CanHandleErrorSpec.testCanHandleError_Try_handleNonFatalShouldHandleNonFatal,
      ),
      example(
        "test Fx[Try].handleNonFatal should not handle Fatal",
        TrySpec.CanHandleErrorSpec.testCanHandleError_Try_handleNonFatalShouldNotHandleFatal,
      ),
      example(
        "test Fx[Try].handleNonFatal should return the successful result",
        TrySpec.CanHandleErrorSpec.testCanHandleError_Try_handleNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Try].handleNonFatalEither should handle NonFatal",
        TrySpec.CanHandleErrorSpec.testCanHandleError_Try_handleNonFatalEitherShouldHandleNonFatal,
      ),
      example(
        "test Fx[Try].handleNonFatalEither should not handle Fatal",
        TrySpec.CanHandleErrorSpec.testCanHandleError_Try_handleNonFatalEitherShouldNotHandleFatal,
      ),
      example(
        "test Fx[Try].handleNonFatalEither should return the successful result",
        TrySpec.CanHandleErrorSpec.testCanHandleError_Try_handleNonFatalEitherShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Try].handleNonFatalEither should return the failed result",
        TrySpec.CanHandleErrorSpec.testCanHandleError_Try_handleNonFatalEitherShouldReturnFailedResult,
      ),
      example(
        "test Fx[Try].handleEitherNonFatal should handle NonFatal",
        TrySpec.CanHandleErrorSpec.testCanHandleError_Try_handleEitherNonFatalShouldHandleNonFatal,
      ),
      example(
        "test Fx[Try].handleEitherNonFatal should not handle Fatal",
        TrySpec.CanHandleErrorSpec.testCanHandleError_Try_handleEitherNonFatalShouldNotHandleFatal,
      ),
      example(
        "test Fx[Try].handleEitherNonFatal should return the successful result",
        TrySpec.CanHandleErrorSpec.testCanHandleError_Try_handleEitherNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Try].handleEitherNonFatal should return the failed result",
        TrySpec.CanHandleErrorSpec.testCanHandleError_Try_handleEitherNonFatalShouldReturnFailedResult,
      ),
    ) ++ List(
      example(
        "test Fx[Try].recoverFromNonFatalWith should catch NonFatal",
        TrySpec.CanRecoverSpec.testCanRecover_Try_recoverFromNonFatalWithShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Try].recoverFromNonFatalWith should not catch Fatal",
        TrySpec.CanRecoverSpec.testCanRecover_Try_recoverFromNonFatalWithShouldNotCatchFatal,
      ),
      example(
        "test Fx[Try].recoverFromNonFatalWith should return the successful result",
        TrySpec.CanRecoverSpec.testCanRecover_Try_recoverFromNonFatalWithShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Try].recoverFromNonFatalWithEither should catch NonFatal",
        TrySpec.CanRecoverSpec.testCanRecover_Try_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Try].recoverFromNonFatalWithEither should not catch Fatal",
        TrySpec.CanRecoverSpec.testCanRecover_Try_recoverFromNonFatalWithEitherShouldNotCatchFatal,
      ),
      example(
        "test Fx[Try].recoverFromNonFatalWithEither should return the successful result",
        TrySpec.CanRecoverSpec.testCanRecover_Try_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Try].recoverFromNonFatalWithEither should return the failed result",
        TrySpec.CanRecoverSpec.testCanRecover_Try_recoverFromNonFatalWithEitherShouldReturnFailedResult,
      ),
      example(
        "test Fx[Try].recoverEitherFromNonFatalWith should catch NonFatal",
        TrySpec.CanRecoverSpec.testCanRecover_Try_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Try].recoverEitherFromNonFatalWith should not catch Fatal",
        TrySpec.CanRecoverSpec.testCanRecover_Try_recoverEitherFromNonFatalWithShouldNotCatchFatal,
      ),
      example(
        "test Fx[Try].recoverEitherFromNonFatalWith should return the successful result",
        TrySpec.CanRecoverSpec.testCanRecover_Try_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Try].recoverEitherFromNonFatalWith should return the failed result",
        TrySpec.CanRecoverSpec.testCanRecover_Try_recoverEitherFromNonFatalWithShouldReturnFailedResult,
      ),
      example(
        "test Fx[Try].recoverFromNonFatal should catch NonFatal",
        TrySpec.CanRecoverSpec.testCanRecover_Try_recoverFromNonFatalShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Try].recoverFromNonFatal should not catch Fatal",
        TrySpec.CanRecoverSpec.testCanRecover_Try_recoverFromNonFatalShouldNotCatchFatal,
      ),
      example(
        "test Fx[Try].recoverFromNonFatal should return the successful result",
        TrySpec.CanRecoverSpec.testCanRecover_Try_recoverFromNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Try].recoverFromNonFatalEither should catch NonFatal",
        TrySpec.CanRecoverSpec.testCanRecover_Try_recoverFromNonFatalEitherShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Try].recoverFromNonFatalEither should not catch Fatal",
        TrySpec.CanRecoverSpec.testCanRecover_Try_recoverFromNonFatalEitherShouldNotCatchFatal,
      ),
      example(
        "test Fx[Try].recoverFromNonFatalEither should return the successful result",
        TrySpec.CanRecoverSpec.testCanRecover_Try_recoverFromNonFatalEitherShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Try].recoverFromNonFatalEither should return the failed result",
        TrySpec.CanRecoverSpec.testCanRecover_Try_recoverFromNonFatalEitherShouldReturnFailedResult,
      ),
      example(
        "test Fx[Try].recoverEitherFromNonFatal should catch NonFatal",
        TrySpec.CanRecoverSpec.testCanRecover_Try_recoverEitherFromNonFatalShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Try].recoverEitherFromNonFatal should not catch Fatal",
        TrySpec.CanRecoverSpec.testCanRecover_Try_recoverEitherFromNonFatalShouldNotCatchFatal,
      ),
      example(
        "test Fx[Try].recoverEitherFromNonFatal should return the successful result",
        TrySpec.CanRecoverSpec.testCanRecover_Try_recoverEitherFromNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Try].recoverEitherFromNonFatal should return the failed result",
        TrySpec.CanRecoverSpec.testCanRecover_Try_recoverEitherFromNonFatalShouldReturnFailedResult,
      ),
    )

  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: Fx, A](a: => A): F[A] =
    Fx[F].effectOf(a)

  object TrySpec {

    def testMonadLaws: List[Test] = {

      implicit val eqIo: Eq[Try[Int]] = Eq.fromUniversalEquals

      MonadSpec.testMonadLaws[Try]("Try")
    }

    object CanCatchSpec {

      def testFx_Try_catchNonFatalThrowableShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong 1")
        val fa                = run[Try, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = expectedExpcetion.asLeft[Int]
        val actual            = Fx[Try].catchNonFatalThrowable(fa)

        actual ==== Success(expected)
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testFx_Try_catchNonFatalThrowableShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong 2")

        try {
          val fa     = run[Try, Int](throwThrowable[Int](fatalExpcetion))
          val actual = Fx[Try].catchNonFatalThrowable(fa)
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: SomeControlThrowable =>
            ex.getMessage ==== fatalExpcetion.getMessage

          case ex: Throwable =>
            Result
              .failure
              .log(s"Unexpected Throwable for testing Fx[Try].catchNonFatalThrowable(FatalException): ${ex.toString}")
        }

      }

      def testFx_Try_catchNonFatalThrowableShouldReturnSuccessfulResult: Result = {

        val fa       = run[Try, Int](1)
        val expected = 1.asRight[Throwable]
        val actual   = Fx[Try].catchNonFatalThrowable(fa)

        actual ==== Success(expected)
      }

      def testFx_Try_catchNonFatalShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong 3")
        val fa                = run[Try, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual            = Fx[Try].catchNonFatal(fa)(SomeError.someThrowable)

        actual ==== Success(expected)
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testFx_Try_catchNonFatalShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong 4")

        try {
          val fa     = run[Try, Int](throwThrowable[Int](fatalExpcetion))
          val actual = Fx[Try].catchNonFatal(fa)(SomeError.someThrowable)
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: SomeControlThrowable =>
            ex.getMessage ==== fatalExpcetion.getMessage

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable for testing Fx[Try].catchNonFatal: ${ex.toString}")
        }

      }

      def testFx_Try_catchNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = run[Try, Int](1)
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Try].catchNonFatal(fa)(SomeError.someThrowable)

        actual ==== Success(expected)
      }

      def testFx_Try_catchNonFatalEitherShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong 5")
        val fa       = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual   = Fx[Try].catchNonFatalEither(fa)(SomeError.someThrowable)

        actual ==== Success(expected)
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testFx_Try_catchNonFatalEitherShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong 6")

        try {
          val fa     = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))
          val actual = Fx[Try].catchNonFatalEither(fa)(SomeError.someThrowable)
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: SomeControlThrowable =>
            ex.getMessage ==== fatalExpcetion.getMessage

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testFx_Try_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[Try, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Try].catchNonFatalEither(fa)(SomeError.someThrowable)

        actual ==== Success(expected)
      }

      def testFx_Try_catchNonFatalEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Try, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Try].catchNonFatalEither(fa)(SomeError.someThrowable)

        actual ==== Success(expected)
      }

    }

    object CanHandleErrorSpec {

      def testCanHandleError_Try_handleNonFatalWithShouldHandleNonFatalWith: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong 7")
        val fa                = run[Try, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 123
        val actual            = Fx[Try]
          .handleNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) =>
              Fx[Try].pureOf(expected)

            case err =>
              throw err // scalafix:ok DisableSyntax.throw
          }

        actual ==== Success(expected)
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_Try_handleNonFatalWithShouldNotHandleFatalWith: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong 8")

        try {
          val fa     = run[Try, Int](throwThrowable[Int](fatalExpcetion))
          val actual = Fx[Try].handleNonFatalWith(fa)(_ => Fx[Try].pureOf(123))
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
        val actual   = Fx[Try].handleNonFatalWith(fa)(_ => Fx[Try].pureOf(999))

        actual ==== Success(expected)
      }

      def testCanHandleError_Try_handleNonFatalWithEitherShouldHandleNonFatalWith: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong 9")
        val fa = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
        val actualFailedResult   =
          Fx[Try].handleNonFatalWith(fa)(_ => Fx[Try].pureOf(expectedFailedResult))

        val expectedSuccessResult = 1.asRight[SomeError]
        val actualSuccessResult   =
          Fx[Try].handleNonFatalWith(fa)(_ => Fx[Try].pureOf(1.asRight[SomeError]))

        actualFailedResult ==== Success(expectedFailedResult) and actualSuccessResult ==== Success(
          expectedSuccessResult
        )
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_Try_handleNonFatalWithEitherShouldNotHandleFatalWith: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong 10")

        try {
          val fa     = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))
          val actual = Fx[Try].handleNonFatalWith(fa)(_ => Fx[Try].pureOf(123.asRight[SomeError]))
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
        val actual   = Fx[Try].handleNonFatalWith(fa)(_ => Try(999.asRight[SomeError]))

        actual ==== Success(expected)
      }

      def testCanHandleError_Try_handleNonFatalWithEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Try, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Try].handleNonFatalWith(fa)(_ => Fx[Try].pureOf(123.asRight[SomeError]))

        actual ==== Success(expected)
      }

      def testCanHandleError_Try_handleEitherNonFatalWithShouldHandleNonFatalWith: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong 11")
        val fa = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   = Fx[Try]
          .handleEitherNonFatalWith(fa)(err => Fx[Try].pureOf(SomeError.someThrowable(err).asLeft[Int]))

        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[Try].handleEitherNonFatalWith(fa)(_ => Fx[Try].pureOf(123.asRight[SomeError]))

        actualFailedResult ==== Success(expectedFailedResult) and actualSuccessResult ==== Success(
          expectedSuccessResult
        )
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_Try_handleEitherNonFatalWithShouldNotHandleFatalWith: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong 12")

        try {
          val fa     = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))
          val actual =
            Fx[Try]
              .handleEitherNonFatalWith(fa)(err => Fx[Try].pureOf(SomeError.someThrowable(err).asLeft[Int]))

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
          Fx[Try].handleEitherNonFatalWith(fa)(_ => Fx[Try].pureOf(123.asRight[SomeError]))

        actual ==== Success(expected)
      }

      def testCanHandleError_Try_handleEitherNonFatalWithShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Try, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[Try].handleEitherNonFatalWith(fa)(_ => Fx[Try].pureOf(123.asRight[SomeError]))

        actual ==== Success(expected)
      }

      def testCanHandleError_Try_handleNonFatalShouldHandleNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong 13")
        val fa                = run[Try, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 123
        val actual            = Fx[Try]
          .handleNonFatal(fa) {
            case NonFatal(`expectedExpcetion`) =>
              expected
            case err =>
              throw err // scalafix:ok DisableSyntax.throw
          }

        actual ==== Success(expected)
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_Try_handleNonFatalShouldNotHandleFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong 14")

        try {
          val fa     = run[Try, Int](throwThrowable[Int](fatalExpcetion))
          val actual = Fx[Try].handleNonFatal(fa)(_ => 123)
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
        val actual   = Fx[Try].handleNonFatal(fa)(_ => 999)

        actual ==== Success(expected)
      }

      def testCanHandleError_Try_handleNonFatalEitherShouldHandleNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong 15")
        val fa = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
        val actualFailedResult   = Fx[Try].handleNonFatal(fa)(_ => expectedFailedResult)

        val expectedSuccessResult = 1.asRight[SomeError]
        val actualSuccessResult   = Fx[Try].handleNonFatal(fa)(_ => 1.asRight[SomeError])

        actualFailedResult ==== Success(expectedFailedResult) and actualSuccessResult ==== Success(
          expectedSuccessResult
        )
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_Try_handleNonFatalEitherShouldNotHandleFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong 16")

        try {
          val fa     = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))
          val actual = Fx[Try].handleNonFatal(fa)(_ => 123.asRight[SomeError])
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
        val actual   = Fx[Try].handleNonFatal(fa)(_ => 999.asRight[SomeError])

        actual ==== Success(expected)
      }

      def testCanHandleError_Try_handleNonFatalEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Try, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Try].handleNonFatal(fa)(_ => 123.asRight[SomeError])

        actual ==== Success(expected)
      }

      def testCanHandleError_Try_handleEitherNonFatalShouldHandleNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong 17")
        val fa = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   = Fx[Try]
          .handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])

        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[Try].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError])

        actualFailedResult ==== Success(expectedFailedResult) and actualSuccessResult ==== Success(
          expectedSuccessResult
        )
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_Try_handleEitherNonFatalShouldNotHandleFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong 18")

        try {
          val fa     = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))
          val actual =
            Fx[Try]
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
        val actual   = Fx[Try].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError])

        actual ==== Success(expected)
      }

      def testCanHandleError_Try_handleEitherNonFatalShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Try, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[Try].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError])

        actual ==== Success(expected)
      }

    }

    object CanRecoverSpec {

      def testCanRecover_Try_recoverFromNonFatalWithShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong 19")
        val fa                = run[Try, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 123
        val actual            = Fx[Try]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) =>
              Fx[Try].pureOf(expected)
          }

        actual ==== Success(expected)
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_Try_recoverFromNonFatalWithShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong 20")

        try {
          val fa     = run[Try, Int](throwThrowable[Int](expectedExpcetion))
          val io     = Fx[Try].recoverFromNonFatalWith(fa) { case `expectedExpcetion` => Fx[Try].pureOf(123) }
          val actual = io
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
        val actual   = Fx[Try]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(_) => Fx[Try].pureOf(999)
          }

        actual ==== Success(expected)
      }

      def testCanRecover_Try_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong 21")
        val fa = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
        val actualFailedResult   = Fx[Try]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) => Fx[Try].pureOf(expectedFailedResult)
          }

        val expectedSuccessResult = 1.asRight[SomeError]
        val actualSuccessResult   = Fx[Try]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) => Fx[Try].pureOf(1.asRight[SomeError])
          }

        actualFailedResult ==== Success(expectedFailedResult) and actualSuccessResult ==== Success(
          expectedSuccessResult
        )
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_Try_recoverFromNonFatalWithEitherShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong 22")
        try {
          val fa = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

          val actual = Fx[Try].recoverFromNonFatalWith(fa) {
            case `expectedExpcetion` => Fx[Try].pureOf(123.asRight[SomeError])
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
        val actual   = Fx[Try]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(_) => Try(999.asRight[SomeError])
          }

        actual ==== Success(expected)
      }

      def testCanRecover_Try_recoverFromNonFatalWithEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Try, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Try]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(_) => Fx[Try].pureOf(123.asRight[SomeError])
          }

        actual ==== Success(expected)
      }

      def testCanRecover_Try_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong 23")
        val fa = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   = Fx[Try]
          .recoverEitherFromNonFatalWith(fa) {
            case err => Fx[Try].pureOf(SomeError.someThrowable(err).asLeft[Int])
          }

        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   = Fx[Try]
          .recoverEitherFromNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) => Fx[Try].pureOf(123.asRight[SomeError])
          }

        actualFailedResult ==== Success(expectedFailedResult) and actualSuccessResult ==== Success(
          expectedSuccessResult
        )
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_Try_recoverEitherFromNonFatalWithShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong 24")
        try {
          val fa = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

          val actual = Fx[Try].recoverEitherFromNonFatalWith(fa) {
            case err => Fx[Try].pureOf(SomeError.someThrowable(err).asLeft[Int])
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
        val actual   = Fx[Try]
          .recoverEitherFromNonFatalWith(fa) {
            case NonFatal(_) => Fx[Try].pureOf(123.asRight[SomeError])
          }

        actual ==== Success(expected)
      }

      def testCanRecover_Try_recoverEitherFromNonFatalWithShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Try, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[Try]
            .recoverEitherFromNonFatalWith(fa) {
              case NonFatal(_) => Fx[Try].pureOf(123.asRight[SomeError])
            }

        actual ==== Success(expected)
      }

      // /

      def testCanRecover_Try_recoverFromNonFatalShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong 25")
        val fa                = run[Try, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 123
        val actual            = Fx[Try]
          .recoverFromNonFatal(fa) {
            case NonFatal(`expectedExpcetion`) =>
              expected
          }

        actual ==== Success(expected)
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_Try_recoverFromNonFatalShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong 26")
        try {
          val fa     = run[Try, Int](throwThrowable[Int](expectedExpcetion))
          val actual = Fx[Try].recoverFromNonFatal(fa) { case `expectedExpcetion` => 123 }
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
        val actual   = Fx[Try].recoverFromNonFatal(fa) { case NonFatal(_) => 999 }

        actual ==== Success(expected)
      }

      def testCanRecover_Try_recoverFromNonFatalEitherShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong 27")
        val fa = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
        val actualFailedResult   = Fx[Try]
          .recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expectedFailedResult }

        val expectedSuccessResult = 1.asRight[SomeError]
        val actualSuccessResult   = Fx[Try]
          .recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }

        actualFailedResult ==== Success(expectedFailedResult) and actualSuccessResult ==== Success(
          expectedSuccessResult
        )
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_Try_recoverFromNonFatalEitherShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong 28")
        try {
          val fa     = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
          val actual = Fx[Try].recoverFromNonFatal(fa) { case `expectedExpcetion` => 123.asRight[SomeError] }
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
        val actual   = Fx[Try].recoverFromNonFatal(fa) { case NonFatal(_) => 999.asRight[SomeError] }

        actual ==== Success(expected)
      }

      def testCanRecover_Try_recoverFromNonFatalEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Try, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Try].recoverFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }

        actual ==== Success(expected)
      }

      def testCanRecover_Try_recoverEitherFromNonFatalShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong 29")
        val fa = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   =
          Fx[Try]
            .recoverEitherFromNonFatal(fa) {
              case err => SomeError.someThrowable(err).asLeft[Int]
            }

        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[Try]
            .recoverEitherFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }

        actualFailedResult ==== Success(expectedFailedResult) and actualSuccessResult ==== Success(
          expectedSuccessResult
        )
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_Try_recoverEitherFromNonFatalShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong 30")

        try {
          val fa = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

          val actual = Fx[Try].recoverEitherFromNonFatal(fa) {
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
          Fx[Try]
            .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }

        actual ==== Success(expected)
      }

      def testCanRecover_Try_recoverEitherFromNonFatalShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Try, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[Try]
            .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }

        actual ==== Success(expected)
      }

    }

  }

}
