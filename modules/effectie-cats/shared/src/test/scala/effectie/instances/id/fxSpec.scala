package effectie.instances.id

import cats.Id
import cats.data.EitherT
import cats.syntax.all._
import effectie.core._
import effectie.instances.id.fx._
import effectie.specs.MonadSpec
import effectie.syntax.all._
import effectie.testing.types.SomeError
import effectie.{SomeControlThrowable, specs}
import hedgehog._
import hedgehog.runner._

import scala.util.control.{ControlThrowable, NonFatal}

/** @author Kevin Lee
  * @since 2020-12-06
  */
object fxSpec extends Properties {

  override def tests: List[Test] = idSpecs

  /* Id */
  private val idSpecs =
    specs.fxSpec.IdSpecs.idSpecs ++
      IdSpec.testMonadLaws ++
      List(
        /* Id */
        example(
          "test Fx[Id]catchNonFatalThrowable should catch NonFatal",
          IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalThrowableShouldCatchNonFatal,
        ),
        example(
          "test Fx[Id]catchNonFatalThrowable should not catch Fatal",
          IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalThrowableShouldNotCatchFatal,
        ),
        example(
          "test Fx[Id]catchNonFatalThrowable should return the successful result",
          IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalThrowableShouldReturnSuccessfulResult,
        ),
        example(
          "test Fx[Id]catchNonFatal should catch NonFatal",
          IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalShouldCatchNonFatal,
        ),
        example(
          "test Fx[Id]catchNonFatal should not catch Fatal",
          IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalShouldNotCatchFatal,
        ),
        example(
          "test Fx[Id]catchNonFatal should return the successful result",
          IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalShouldReturnSuccessfulResult,
        ),
        example(
          "test Fx[Id]catchNonFatalEither should catch NonFatal",
          IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalEitherShouldCatchNonFatal,
        ),
        example(
          "test Fx[Id]catchNonFatalEither should not catch Fatal",
          IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalEitherShouldNotCatchFatal,
        ),
        example(
          "test Fx[Id]catchNonFatalEither should return the successful result",
          IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalEitherShouldReturnSuccessfulResult,
        ),
        example(
          "test Fx[Id]catchNonFatalEither should return the failed result",
          IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalEitherShouldReturnFailedResult,
        ),
        example(
          "test Fx[Id]catchNonFatalEitherT should catch NonFatal",
          IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalEitherTShouldCatchNonFatal,
        ),
        example(
          "test Fx[Id]catchNonFatalEitherT should not catch Fatal",
          IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalEitherTShouldNotCatchFatal,
        ),
        example(
          "test Fx[Id]catchNonFatalEitherT should return the successful result",
          IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalEitherTShouldReturnSuccessfulResult,
        ),
        example(
          "test Fx[Id]catchNonFatalEitherT should return the failed result",
          IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalEitherTShouldReturnFailedResult,
        ),
      ) ++
      List(
        example(
          "test Fx[Id].handleNonFatalWith should handle NonFatal",
          IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleNonFatalWithShouldHandleNonFatalWith,
        ),
        example(
          "test Fx[Id].handleNonFatalWith should not handle Fatal",
          IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleNonFatalWithShouldNotHandleFatalWith,
        ),
        example(
          "test Fx[Id].handleNonFatalWith should return the successful result",
          IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleNonFatalWithShouldReturnSuccessfulResult,
        ),
        example(
          "test Fx[Id].handleNonFatalWithEither should handle NonFatal",
          IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleNonFatalWithEitherShouldHandleNonFatalWith,
        ),
        example(
          "test Fx[Id].handleNonFatalWithEither should not handle Fatal",
          IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleNonFatalWithEitherShouldNotHandleFatalWith,
        ),
        example(
          "test Fx[Id].handleNonFatalWithEither should return the successful result",
          IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleNonFatalWithEitherShouldReturnSuccessfulResult,
        ),
        example(
          "test Fx[Id].handleNonFatalWithEither should return the failed result",
          IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleNonFatalWithEitherShouldReturnFailedResult,
        ),
        example(
          "test Fx[Id].handleEitherNonFatalWith should handle NonFatal",
          IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherNonFatalWithShouldHandleNonFatalWith,
        ),
        example(
          "test Fx[Id].handleEitherNonFatalWith should not handle Fatal",
          IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherNonFatalWithShouldNotHandleFatalWith,
        ),
        example(
          "test Fx[Id].handleEitherNonFatalWith should return the successful result",
          IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherNonFatalWithShouldReturnSuccessfulResult,
        ),
        example(
          "test Fx[Id].handleEitherNonFatalWith should return the failed result",
          IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherNonFatalWithShouldReturnFailedResult,
        ),
        example(
          "test Fx[Id].handleEitherTNonFatalWith should handle NonFatal",
          IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherTNonFatalWithShouldHandleNonFatalWith,
        ),
        example(
          "test Fx[Id].handleEitherTNonFatalWith should not handle Fatal",
          IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherTNonFatalWithShouldNotHandleFatalWith,
        ),
        example(
          "test Fx[Id].handleEitherTNonFatalWith should return the successful result",
          IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherTNonFatalWithShouldReturnSuccessfulResult,
        ),
        example(
          "test Fx[Id].handleEitherTNonFatalWith should return the failed result",
          IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherTNonFatalWithShouldReturnFailedResult,
        ),
        example(
          "test Fx[Id].handleNonFatal should handle NonFatal",
          IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleNonFatalShouldHandleNonFatal,
        ),
        example(
          "test Fx[Id].handleNonFatal should not handle Fatal",
          IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleNonFatalShouldNotHandleFatal,
        ),
        example(
          "test Fx[Id].handleNonFatal should return the successful result",
          IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleNonFatalShouldReturnSuccessfulResult,
        ),
        example(
          "test Fx[Id].handleNonFatalEither should handle NonFatal",
          IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleNonFatalEitherShouldHandleNonFatal,
        ),
        example(
          "test Fx[Id].handleNonFatalEither should not handle Fatal",
          IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleNonFatalEitherShouldNotHandleFatal,
        ),
        example(
          "test Fx[Id].handleNonFatalEither should return the successful result",
          IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleNonFatalEitherShouldReturnSuccessfulResult,
        ),
        example(
          "test Fx[Id].handleNonFatalEither should return the failed result",
          IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleNonFatalEitherShouldReturnFailedResult,
        ),
        example(
          "test Fx[Id].handleEitherNonFatal should handle NonFatal",
          IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherNonFatalShouldHandleNonFatal,
        ),
        example(
          "test Fx[Id].handleEitherNonFatal should not handle Fatal",
          IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherNonFatalShouldNotHandleFatal,
        ),
        example(
          "test Fx[Id].handleEitherNonFatal should return the successful result",
          IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherNonFatalShouldReturnSuccessfulResult,
        ),
        example(
          "test Fx[Id].handleEitherNonFatal should return the failed result",
          IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherNonFatalShouldReturnFailedResult,
        ),
        example(
          "test Fx[Id].handleEitherTNonFatal should handle NonFatal",
          IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherTNonFatalShouldHandleNonFatal,
        ),
        example(
          "test Fx[Id].handleEitherTNonFatal should not handle Fatal",
          IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherTNonFatalShouldNotHandleFatal,
        ),
        example(
          "test Fx[Id].handleEitherTNonFatal should return the successful result",
          IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherTNonFatalShouldReturnSuccessfulResult,
        ),
        example(
          "test Fx[Id].handleEitherTNonFatal should return the failed result",
          IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherTNonFatalShouldReturnFailedResult,
        ),
      ) ++ List(
        /* Id */
        example(
          "test Fx[Id].recoverFromNonFatalWith should catch NonFatal",
          IdSpec.CanRecoverSpec.testCanRecover_Id_recoverFromNonFatalWithShouldRecoverFromNonFatal,
        ),
        example(
          "test Fx[Id].recoverFromNonFatalWith should not catch Fatal",
          IdSpec.CanRecoverSpec.testCanRecover_Id_recoverFromNonFatalWithShouldNotCatchFatal,
        ),
        example(
          "test Fx[Id].recoverFromNonFatalWith should return the successful result",
          IdSpec.CanRecoverSpec.testCanRecover_Id_recoverFromNonFatalWithShouldReturnSuccessfulResult,
        ),
        example(
          "test Fx[Id].recoverFromNonFatalWithEither should catch NonFatal",
          IdSpec.CanRecoverSpec.testCanRecover_Id_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal,
        ),
        example(
          "test Fx[Id].recoverFromNonFatalWithEither should not catch Fatal",
          IdSpec.CanRecoverSpec.testCanRecover_Id_recoverFromNonFatalWithEitherShouldNotCatchFatal,
        ),
        example(
          "test Fx[Id].recoverFromNonFatalWithEither should return the successful result",
          IdSpec.CanRecoverSpec.testCanRecover_Id_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult,
        ),
        example(
          "test Fx[Id].recoverFromNonFatalWithEither should return the failed result",
          IdSpec.CanRecoverSpec.testCanRecover_Id_recoverFromNonFatalWithEitherShouldReturnFailedResult,
        ),
        example(
          "test Fx[Id].recoverEitherFromNonFatalWith should catch NonFatal",
          IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal,
        ),
        example(
          "test Fx[Id].recoverEitherFromNonFatalWith should not catch Fatal",
          IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherFromNonFatalWithShouldNotCatchFatal,
        ),
        example(
          "test Fx[Id].recoverEitherFromNonFatalWith should return the successful result",
          IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult,
        ),
        example(
          "test Fx[Id].recoverEitherFromNonFatalWith should return the failed result",
          IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherFromNonFatalWithShouldReturnFailedResult,
        ),
        example(
          "test Fx[Id].recoverEitherTFromNonFatalWith should catch NonFatal",
          IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal,
        ),
        example(
          "test Fx[Id].recoverEitherTFromNonFatalWith should not catch Fatal",
          IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherTFromNonFatalWithShouldNotCatchFatal,
        ),
        example(
          "test Fx[Id].recoverEitherTFromNonFatalWith should return the successful result",
          IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult,
        ),
        example(
          "test Fx[Id].recoverEitherTFromNonFatalWith should return the failed result",
          IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherTFromNonFatalWithShouldReturnFailedResult,
        ),
        example(
          "test Fx[Id].recoverFromNonFatal should catch NonFatal",
          IdSpec.CanRecoverSpec.testCanRecover_Id_recoverFromNonFatalShouldRecoverFromNonFatal,
        ),
        example(
          "test Fx[Id].recoverFromNonFatal should not catch Fatal",
          IdSpec.CanRecoverSpec.testCanRecover_Id_recoverFromNonFatalShouldNotCatchFatal,
        ),
        example(
          "test Fx[Id].recoverFromNonFatal should return the successful result",
          IdSpec.CanRecoverSpec.testCanRecover_Id_recoverFromNonFatalShouldReturnSuccessfulResult,
        ),
        example(
          "test Fx[Id].recoverFromNonFatalEither should catch NonFatal",
          IdSpec.CanRecoverSpec.testCanRecover_Id_recoverFromNonFatalEitherShouldRecoverFromNonFatal,
        ),
        example(
          "test Fx[Id].recoverFromNonFatalEither should not catch Fatal",
          IdSpec.CanRecoverSpec.testCanRecover_Id_recoverFromNonFatalEitherShouldNotCatchFatal,
        ),
        example(
          "test Fx[Id].recoverFromNonFatalEither should return the successful result",
          IdSpec.CanRecoverSpec.testCanRecover_Id_recoverFromNonFatalEitherShouldReturnSuccessfulResult,
        ),
        example(
          "test Fx[Id].recoverFromNonFatalEither should return the failed result",
          IdSpec.CanRecoverSpec.testCanRecover_Id_recoverFromNonFatalEitherShouldReturnFailedResult,
        ),
        example(
          "test Fx[Id].recoverEitherFromNonFatal should catch NonFatal",
          IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherFromNonFatalShouldRecoverFromNonFatal,
        ),
        example(
          "test Fx[Id].recoverEitherFromNonFatal should not catch Fatal",
          IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherFromNonFatalShouldNotCatchFatal,
        ),
        example(
          "test Fx[Id].recoverEitherFromNonFatal should return the successful result",
          IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherFromNonFatalShouldReturnSuccessfulResult,
        ),
        example(
          "test Fx[Id].recoverEitherFromNonFatal should return the failed result",
          IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherFromNonFatalShouldReturnFailedResult,
        ),
        example(
          "test Fx[Id].recoverEitherTFromNonFatal should catch NonFatal",
          IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherTFromNonFatalShouldRecoverFromNonFatal,
        ),
        example(
          "test Fx[Id].recoverEitherTFromNonFatal should not catch Fatal",
          IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherTFromNonFatalShouldNotCatchFatal,
        ),
        example(
          "test Fx[Id].recoverEitherTFromNonFatal should return the successful result",
          IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherTFromNonFatalShouldReturnSuccessfulResult,
        ),
        example(
          "test Fx[Id].recoverEitherTFromNonFatal should return the failed result",
          IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherTFromNonFatalShouldReturnFailedResult,
        ),
      )

  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: Fx, A](a: => A): F[A] =
    Fx[F].effectOf(a)

  object IdSpec {

    def testMonadLaws: List[Test] = {
      MonadSpec.testMonadLaws[Id]("Id")
    }

    object CanCatchSpec {

      def testCanCatch_Id_catchNonFatalThrowableShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = expectedExpcetion.asLeft[Int]
        val actual            = Fx[Id].catchNonFatalThrowable(fa)

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanCatch_Id_catchNonFatalThrowableShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa        = run[Id, Int](throwThrowable[Int](fatalExpcetion))

        try {
          val actual = Fx[Id].catchNonFatalThrowable(fa)
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanCatch_Id_catchNonFatalThrowableShouldReturnSuccessfulResult: Result = {

        val fa       = run[Id, Int](1)
        val expected = 1.asRight[Throwable]
        val actual   = Fx[Id].catchNonFatalThrowable(fa)

        actual ==== expected
      }

      def testCanCatch_Id_catchNonFatalShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual            = Fx[Id].catchNonFatal(fa)(SomeError.someThrowable)

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanCatch_Id_catchNonFatalShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa        = run[Id, Int](throwThrowable[Int](fatalExpcetion))

        try {
          val actual = Fx[Id].catchNonFatal(fa)(SomeError.someThrowable)
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanCatch_Id_catchNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = run[Id, Int](1)
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id].catchNonFatal(fa)(SomeError.someThrowable)

        actual ==== expected
      }

      def testCanCatch_Id_catchNonFatalEitherShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa  = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual   = Fx[Id].catchNonFatalEither(fa)(SomeError.someThrowable)

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanCatch_Id_catchNonFatalEitherShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa        = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

        try {
          val actual = Fx[Id].catchNonFatalEither(fa)(SomeError.someThrowable)
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanCatch_Id_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id].catchNonFatalEither(fa)(SomeError.someThrowable)

        actual ==== expected
      }

      def testCanCatch_Id_catchNonFatalEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Id].catchNonFatalEither(fa)(SomeError.someThrowable)

        actual ==== expected
      }

      def testCanCatch_Id_catchNonFatalEitherTShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa           = EitherT(
          run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        )
        val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual            = Fx[Id].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanCatch_Id_catchNonFatalEitherTShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

        try {
          val actual = Fx[Id].catchNonFatalEitherT(fa)(SomeError.someThrowable).value
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanCatch_Id_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[Id, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

        actual ==== expected
      }

      def testCanCatch_Id_catchNonFatalEitherTShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Id].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

        actual ==== expected
      }

    }

    object CanHandleErrorSpec {

      def testCanHandleError_Id_handleNonFatalWithShouldHandleNonFatalWith: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 1
        val actual: Id[Int]   = Fx[Id].handleNonFatalWith(fa)(_ => expected)

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_Id_handleNonFatalWithShouldNotHandleFatalWith: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa        = run[Id, Int](throwThrowable[Int](fatalExpcetion))

        try {
          val actual: Id[Int] = Fx[Id].handleNonFatalWith(fa)(_ => 1)
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_Id_handleNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa              = run[Id, Int](1)
        val expected        = 1
        val actual: Id[Int] = Fx[Id].handleNonFatalWith(fa)(_ => 123)

        actual ==== expected
      }

      def testCanHandleError_Id_handleNonFatalWithEitherShouldHandleNonFatalWith: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   =
          Fx[Id].handleNonFatalWith(fa)(err => SomeError.someThrowable(err).asLeft[Int])

        lazy val fa2 = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id].handleNonFatalWith(fa2)(_ => expected)

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_Id_handleNonFatalWithEitherShouldNotHandleFatalWith: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa        = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

        try {
          val actual = Fx[Id].handleNonFatalWith(fa)(_ => 1.asRight[SomeError])
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_Id_handleNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id].handleNonFatalWith(fa)(err => SomeError.someThrowable(err).asLeft[Int])

        actual ==== expected
      }

      def testCanHandleError_Id_handleNonFatalWithEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Id].handleNonFatalWith(fa)(_ => 1.asRight[SomeError])

        actual ==== expected
      }

      def testCanHandleError_Id_handleEitherNonFatalWithShouldHandleNonFatalWith: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   =
          Fx[Id].handleEitherNonFatalWith(fa)(err => SomeError.someThrowable(err).asLeft[Int])

        lazy val fa2 = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id].handleEitherNonFatalWith(fa2)(_ => expected)

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_Id_handleEitherNonFatalWithShouldNotHandleFatalWith: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa        = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

        try {
          val actual = Fx[Id].handleEitherNonFatalWith(fa)(_ => 1.asRight[SomeError])
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_Id_handleEitherNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[Id].handleEitherNonFatalWith(fa)(err => SomeError.someThrowable(err).asLeft[Int])

        actual ==== expected
      }

      def testCanHandleError_Id_handleEitherNonFatalWithShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Id].handleEitherNonFatalWith(fa)(_ => 1.asRight[SomeError])

        actual ==== expected
      }

      def testCanHandleError_Id_handleEitherTNonFatalWithShouldHandleNonFatalWith: Result = {

        val expectedExpcetion    = new RuntimeException("Something's wrong")
        lazy val fa              = EitherT(
          run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        )
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   =
          Fx[Id].handleEitherTNonFatalWith(fa)(err => SomeError.someThrowable(err).asLeft[Int]).value

        lazy val fa2 = EitherT(
          run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        )
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id].handleEitherTNonFatalWith(fa2)(_ => expected).value

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_Id_handleEitherTNonFatalWithShouldNotHandleFatalWith: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

        try {
          val actual = Fx[Id].handleEitherTNonFatalWith(fa)(_ => 1.asRight[SomeError]).value
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_Id_handleEitherTNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[Id, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[Id].handleEitherTNonFatalWith(fa)(err => SomeError.someThrowable(err).asLeft[Int]).value

        actual ==== expected
      }

      def testCanHandleError_Id_handleEitherTNonFatalWithShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Id].handleEitherTNonFatalWith(fa)(_ => 1.asRight[SomeError]).value

        actual ==== expected
      }

      def testCanHandleError_Id_handleNonFatalShouldHandleNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 1
        val actual: Id[Int]   = Fx[Id].handleNonFatal(fa)(_ => expected)

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_Id_handleNonFatalShouldNotHandleFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa        = run[Id, Int](throwThrowable[Int](fatalExpcetion))

        try {
          val actual: Id[Int] = Fx[Id].handleNonFatal(fa)(_ => 1)
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_Id_handleNonFatalShouldReturnSuccessfulResult: Result = {

        val fa              = run[Id, Int](1)
        val expected        = 1
        val actual: Id[Int] = Fx[Id].handleNonFatal(fa)(_ => 123)

        actual ==== expected
      }

      def testCanHandleError_Id_handleNonFatalEitherShouldHandleNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   = Fx[Id].handleNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])

        lazy val fa2 = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id].handleNonFatal(fa2)(_ => expected)

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_Id_handleNonFatalEitherShouldNotHandleFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa        = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

        try {
          val actual = Fx[Id].handleNonFatal(fa)(_ => 1.asRight[SomeError])
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_Id_handleNonFatalEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id].handleNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])

        actual ==== expected
      }

      def testCanHandleError_Id_handleNonFatalEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Id].handleNonFatal(fa)(_ => 1.asRight[SomeError])

        actual ==== expected
      }

      def testCanHandleError_Id_handleEitherNonFatalShouldHandleNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   =
          Fx[Id].handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])

        lazy val fa2 = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id].handleEitherNonFatal(fa2)(_ => expected)

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_Id_handleEitherNonFatalShouldNotHandleFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa        = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

        try {
          val actual = Fx[Id].handleEitherNonFatal(fa)(_ => 1.asRight[SomeError])
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_Id_handleEitherNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id].handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])

        actual ==== expected
      }

      def testCanHandleError_Id_handleEitherNonFatalShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Id].handleEitherNonFatal(fa)(_ => 1.asRight[SomeError])

        actual ==== expected
      }

      def testCanHandleError_Id_handleEitherTNonFatalShouldHandleNonFatal: Result = {

        val expectedExpcetion    = new RuntimeException("Something's wrong")
        lazy val fa              = EitherT(
          run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        )
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   =
          Fx[Id].handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]).value

        lazy val fa2 = EitherT(
          run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        )
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id].handleEitherTNonFatal(fa2)(_ => expected).value

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_Id_handleEitherTNonFatalShouldNotHandleFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

        try {
          val actual = Fx[Id].handleEitherTNonFatal(fa)(_ => 1.asRight[SomeError]).value
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_Id_handleEitherTNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[Id, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id].handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]).value

        actual ==== expected
      }

      def testCanHandleError_Id_handleEitherTNonFatalShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Id].handleEitherTNonFatal(fa)(_ => 1.asRight[SomeError]).value

        actual ==== expected
      }

    }

    object CanRecoverSpec {

      def testCanRecover_Id_recoverFromNonFatalWithShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 1
        val actual: Id[Int]   = Fx[Id].recoverFromNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) => expected
        }

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_Id_recoverFromNonFatalWithShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))

        try {
          val actual: Id[Int] = Fx[Id].recoverFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => 1 }
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_Id_recoverFromNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa              = run[Id, Int](1)
        val expected        = 1
        val actual: Id[Int] = Fx[Id].recoverFromNonFatalWith(fa) { case NonFatal(_) => 123 }

        actual ==== expected
      }

      def testCanRecover_Id_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   = Fx[Id].recoverFromNonFatalWith(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }

        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id].recoverFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => expected }

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_Id_recoverFromNonFatalWithEitherShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

        try {
          val actual = Fx[Id].recoverFromNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError]
          }
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_Id_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[Id].recoverFromNonFatalWith(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }

        actual ==== expected
      }

      def testCanRecover_Id_recoverFromNonFatalWithEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Id].recoverFromNonFatalWith(fa) { case NonFatal(_) => 1.asRight[SomeError] }

        actual ==== expected
      }

      def testCanRecover_Id_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   =
          Fx[Id]
            .recoverEitherFromNonFatalWith(fa) {
              case err => SomeError.someThrowable(err).asLeft[Int]
            }

        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[Id].recoverEitherFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => expected }

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_Id_recoverEitherFromNonFatalWithShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

        try {
          val actual = Fx[Id]
            .recoverEitherFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_Id_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id]
          .recoverEitherFromNonFatalWith(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }

        actual ==== expected
      }

      def testCanRecover_Id_recoverEitherFromNonFatalWithShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[Id].recoverEitherFromNonFatalWith(fa) { case NonFatal(_) => 1.asRight[SomeError] }

        actual ==== expected
      }

      def testCanRecover_Id_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion    = new RuntimeException("Something's wrong")
        lazy val fa              = EitherT(
          run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        )
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   =
          Fx[Id]
            .recoverEitherTFromNonFatalWith(fa) {
              case err => SomeError.someThrowable(err).asLeft[Int]
            }
            .value

        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[Id].recoverEitherTFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => expected }.value

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_Id_recoverEitherTFromNonFatalWithShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa           = EitherT(
          run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        )

        try {
          val actual = Fx[Id]
            .recoverEitherTFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }
            .value
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_Id_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[Id, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id]
          .recoverEitherTFromNonFatalWith(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }
          .value

        actual ==== expected
      }

      def testCanRecover_Id_recoverEitherTFromNonFatalWithShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[Id].recoverEitherTFromNonFatalWith(fa) { case NonFatal(_) => 1.asRight[SomeError] }.value

        actual ==== expected
      }

      // /

      def testCanRecover_Id_recoverFromNonFatalShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 1
        val actual: Id[Int]   = Fx[Id].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expected }

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_Id_recoverFromNonFatalShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))

        try {
          val actual: Id[Int] = Fx[Id].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 1 }
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_Id_recoverFromNonFatalShouldReturnSuccessfulResult: Result = {

        val fa              = run[Id, Int](1)
        val expected        = 1
        val actual: Id[Int] = Fx[Id].recoverFromNonFatal(fa) { case NonFatal(_) => 123 }

        actual ==== expected
      }

      def testCanRecover_Id_recoverFromNonFatalEitherShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   =
          Fx[Id].recoverFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }

        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expected }

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_Id_recoverFromNonFatalEitherShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

        try {
          val actual = Fx[Id].recoverFromNonFatal(fa) {
            case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError]
          }
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_Id_recoverFromNonFatalEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[Id].recoverFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }

        actual ==== expected
      }

      def testCanRecover_Id_recoverFromNonFatalEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Id].recoverFromNonFatal(fa) { case NonFatal(_) => 1.asRight[SomeError] }

        actual ==== expected
      }

      def testCanRecover_Id_recoverEitherFromNonFatalShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   =
          Fx[Id]
            .recoverEitherFromNonFatal(fa) {
              case err => SomeError.someThrowable(err).asLeft[Int]
            }

        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[Id].recoverEitherFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expected }

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_Id_recoverEitherFromNonFatalShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

        try {
          val actual = Fx[Id]
            .recoverEitherFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }

          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_Id_recoverEitherFromNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id]
          .recoverEitherFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }

        actual ==== expected
      }

      def testCanRecover_Id_recoverEitherFromNonFatalShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Id].recoverEitherFromNonFatal(fa) { case NonFatal(_) => 1.asRight[SomeError] }

        actual ==== expected
      }

      def testCanRecover_Id_recoverEitherTFromNonFatalShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion    = new RuntimeException("Something's wrong")
        lazy val fa              = EitherT(
          run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        )
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   =
          Fx[Id]
            .recoverEitherTFromNonFatal(fa) {
              case err => SomeError.someThrowable(err).asLeft[Int]
            }
            .value

        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[Id].recoverEitherTFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expected }.value

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_Id_recoverEitherTFromNonFatalShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa           = EitherT(
          run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        )

        try {
          val actual = Fx[Id]
            .recoverEitherTFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }
            .value
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_Id_recoverEitherTFromNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[Id, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id]
          .recoverEitherTFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }
          .value

        actual ==== expected
      }

      def testCanRecover_Id_recoverEitherTFromNonFatalShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Id].recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 1.asRight[SomeError] }.value

        actual ==== expected
      }

    }

  }

}
