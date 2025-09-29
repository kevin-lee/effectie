package effectie.instances.monix3

import cats.data.EitherT

import cats.syntax.all.*
import cats.{Eq, Functor}
import effectie.core.Fx
import effectie.instances.monix3.fx.given
import effectie.{SomeControlThrowable, specs}
import effectie.specs.MonadSpec
import effectie.specs.fxSpec.FxSpecs
import effectie.syntax.error.*
import effectie.testing.tools
import effectie.testing.types.SomeError
import hedgehog.*
import hedgehog.runner.*

import scala.util.control.{ControlThrowable, NonFatal}

/** @author Kevin Lee
  * @since 2020-12-06
  */
object FxSpec extends Properties {

  private val assertWithAttempt: (IO[Int], Either[Throwable, Int]) => Result = { (io, expected) =>
    val actual = io.attempt.runSyncUnsafe()
    (actual ==== expected).log(s"$actual does not equal to $expected")
  }

  override def tests: List[Test] = ioSpecs

  /* IO */
  private val ioSpecs = List(
    property("test Fx[Task].effectOf", FxSpecs.testEffectOf[Task](_.runSyncUnsafe() ==== ())),
    property("test Fx[Task].fromEffect(effectOf)", FxSpecs.testFromEffect[Task](_.runSyncUnsafe() ==== ())),
    property("test Fx[Task].fromEffect(pureOf)", FxSpecs.testFromEffectWithPure[Task](_.runSyncUnsafe() ==== ())),
    property("test Fx[Task].pureOf", FxSpecs.testPureOf[Task](_.runSyncUnsafe() ==== ())),
    property(
      "test Fx[Task].pureOrError(success case)",
      FxSpecs.testPureOrErrorSuccessCase[Task](_.runSyncUnsafe() ==== ()),
    ),
    example(
      "test Fx[Task].pureOrError(error case)",
      FxSpecs.testPureOrErrorErrorCase[Task] { (io, expected) =>
        tools.expectThrowable(io.runSyncUnsafe(), expected)
      },
    ),
    example("test Fx[Task].unitOf", FxSpecs.testUnitOf[Task](_.runSyncUnsafe() ==== ())),
    example(
      "test Fx[Task].errorOf",
      FxSpecs.testErrorOf[Task] { (io, expected) =>
        tools.expectThrowable(io.runSyncUnsafe(), expected)
      },
    ),
    property("test Fx[Task].fromEither(Right)", FxSpecs.testFromEitherRightCase[Task](assertWithAttempt)),
    property("test Fx[Task].fromEither(Left)", FxSpecs.testFromEitherLeftCase[Task](assertWithAttempt)),
    property("test Fx[Task].fromOption(Some)", FxSpecs.testFromOptionSomeCase[Task](assertWithAttempt)),
    property("test Fx[Task].fromOption(None)", FxSpecs.testFromOptionNoneCase[Task](assertWithAttempt)),
    property("test Fx[Task].fromTry(Success)", FxSpecs.testFromTrySuccessCase[Task](assertWithAttempt)),
    property("test Fx[Task].fromTry(Failure)", FxSpecs.testFromTryFailureCase[Task](assertWithAttempt)),
  ) ++
    IoSpec.testMonadLaws ++
    List(
      example(
        "test Fx[Task]catchNonFatalThrowable should catch NonFatal",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalThrowableShouldCatchNonFatal,
      ),
      example(
        "test Fx[Task]catchNonFatalThrowable should not catch Fatal",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalThrowableShouldNotCatchFatal,
      ),
      example(
        "test Fx[Task]catchNonFatalThrowable should return the successful result",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalThrowableShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task]catchNonFatal should catch NonFatal",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalShouldCatchNonFatal,
      ),
      example(
        "test Fx[Task]catchNonFatal should not catch Fatal",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalShouldNotCatchFatal,
      ),
      example(
        "test Fx[Task]catchNonFatal should return the successful result",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task]catchNonFatalEither should catch NonFatal",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalEitherShouldCatchNonFatal,
      ),
      example(
        "test Fx[Task]catchNonFatalEither should not catch Fatal",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalEitherShouldNotCatchFatal,
      ),
      example(
        "test Fx[Task]catchNonFatalEither should return the successful result",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalEitherShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task]catchNonFatalEither should return the failed result",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalEitherShouldReturnFailedResult,
      ),
      example(
        "test Fx[Task]catchNonFatalEitherT should catch NonFatal",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalEitherTShouldCatchNonFatal,
      ),
      example(
        "test Fx[Task]catchNonFatalEitherT should not catch Fatal",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalEitherTShouldNotCatchFatal,
      ),
      example(
        "test Fx[Task]catchNonFatalEitherT should return the successful result",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalEitherTShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task]catchNonFatalEitherT should return the failed result",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalEitherTShouldReturnFailedResult,
      ),
    ) ++
    /* IO */
    List(
      example(
        "test Fx[Task].handleNonFatalWith should handle NonFatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalWithShouldHandleNonFatalWith,
      ),
      example(
        "test Fx[Task].handleNonFatalWith should not handle Fatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalWithShouldNotHandleFatalWith,
      ),
      example(
        "test Fx[Task].handleNonFatalWith should return the successful result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalWithShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].handleNonFatalWithEither should handle NonFatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalWithEitherShouldHandleNonFatalWith,
      ),
      example(
        "test Fx[Task].handleNonFatalWithEither should not handle Fatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalWithEitherShouldNotHandleFatalWith,
      ),
      example(
        "test Fx[Task].handleNonFatalWithEither should return the successful result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalWithEitherShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].handleNonFatalWithEither should return the failed result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalWithEitherShouldReturnFailedResult,
      ),
      example(
        "test Fx[Task].handleEitherNonFatalWith should handle NonFatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherNonFatalWithShouldHandleNonFatalWith,
      ),
      example(
        "test Fx[Task].handleEitherNonFatalWith should not handle Fatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherNonFatalWithShouldNotHandleFatalWith,
      ),
      example(
        "test Fx[Task].handleEitherNonFatalWith should return the successful result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherNonFatalWithShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].handleEitherNonFatalWith should return the failed result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherNonFatalWithShouldReturnFailedResult,
      ),
      example(
        "test Fx[Task].handleEitherTNonFatalWith should handle NonFatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherTNonFatalWithShouldHandleNonFatalWith,
      ),
      example(
        "test Fx[Task].handleEitherTNonFatalWith should not handle Fatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherTNonFatalWithShouldNotHandleFatalWith,
      ),
      example(
        "test Fx[Task].handleEitherTNonFatalWith should return the successful result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherTNonFatalWithShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].handleEitherTNonFatalWith should return the failed result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherTNonFatalWithShouldReturnFailedResult,
      ),
      example(
        "test Fx[Task].handleNonFatal should handle NonFatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalShouldHandleNonFatal,
      ),
      example(
        "test Fx[Task].handleNonFatal should not handle Fatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalShouldNotHandleFatal,
      ),
      example(
        "test Fx[Task].handleNonFatal should return the successful result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].handleNonFatalEither should handle NonFatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalEitherShouldHandleNonFatal,
      ),
      example(
        "test Fx[Task].handleNonFatalEither should not handle Fatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalEitherShouldNotHandleFatal,
      ),
      example(
        "test Fx[Task].handleNonFatalEither should return the successful result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalEitherShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].handleNonFatalEither should return the failed result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalEitherShouldReturnFailedResult,
      ),
      example(
        "test Fx[Task].handleEitherNonFatal should handle NonFatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherNonFatalShouldHandleNonFatal,
      ),
      example(
        "test Fx[Task].handleEitherNonFatal should not handle Fatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherNonFatalShouldNotHandleFatal,
      ),
      example(
        "test Fx[Task].handleEitherNonFatal should return the successful result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].handleEitherNonFatal should return the failed result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherNonFatalShouldReturnFailedResult,
      ),
      example(
        "test Fx[Task].handleEitherTNonFatal should handle NonFatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherTNonFatalShouldHandleNonFatal,
      ),
      example(
        "test Fx[Task].handleEitherTNonFatal should not handle Fatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherTNonFatalShouldNotHandleFatal,
      ),
      example(
        "test Fx[Task].handleEitherTNonFatal should return the successful result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherTNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].handleEitherTNonFatal should return the failed result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherTNonFatalShouldReturnFailedResult,
      ),
    ) ++ List(
      example(
        "test Fx[Task].recoverFromNonFatalWith should catch NonFatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalWithShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Task].recoverFromNonFatalWith should not catch Fatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalWithShouldNotCatchFatal,
      ),
      example(
        "test Fx[Task].recoverFromNonFatalWith should return the successful result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalWithShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].recoverFromNonFatalWithEither should catch NonFatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Task].recoverFromNonFatalWithEither should not catch Fatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldNotCatchFatal,
      ),
      example(
        "test Fx[Task].recoverFromNonFatalWithEither should return the successful result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].recoverFromNonFatalWithEither should return the failed result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnFailedResult,
      ),
      example(
        "test Fx[Task].recoverEitherFromNonFatalWith should catch NonFatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Task].recoverEitherFromNonFatalWith should not catch Fatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherFromNonFatalWithShouldNotCatchFatal,
      ),
      example(
        "test Fx[Task].recoverEitherFromNonFatalWith should return the successful result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].recoverEitherFromNonFatalWith should return the failed result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherFromNonFatalWithShouldReturnFailedResult,
      ),
      example(
        "test Fx[Task].recoverEitherTFromNonFatalWith should catch NonFatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Task].recoverEitherTFromNonFatalWith should not catch Fatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldNotCatchFatal,
      ),
      example(
        "test Fx[Task].recoverEitherTFromNonFatalWith should return the successful result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].recoverEitherTFromNonFatalWith should return the failed result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnFailedResult,
      ),
      example(
        "test Fx[Task].recoverFromNonFatal should catch NonFatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Task].recoverFromNonFatal should not catch Fatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalShouldNotCatchFatal,
      ),
      example(
        "test Fx[Task].recoverFromNonFatal should return the successful result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].recoverFromNonFatalEither should catch NonFatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Task].recoverFromNonFatalEither should not catch Fatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldNotCatchFatal,
      ),
      example(
        "test Fx[Task].recoverFromNonFatalEither should return the successful result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].recoverFromNonFatalEither should return the failed result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldReturnFailedResult,
      ),
      example(
        "test Fx[Task].recoverEitherFromNonFatal should catch NonFatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherFromNonFatalShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Task].recoverEitherFromNonFatal should not catch Fatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherFromNonFatalShouldNotCatchFatal,
      ),
      example(
        "test Fx[Task].recoverEitherFromNonFatal should return the successful result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherFromNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].recoverEitherFromNonFatal should return the failed result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherFromNonFatalShouldReturnFailedResult,
      ),
      example(
        "test Fx[Task].recoverEitherTFromNonFatal should catch NonFatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Task].recoverEitherTFromNonFatal should not catch Fatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldNotCatchFatal,
      ),
      example(
        "test Fx[Task].recoverEitherTFromNonFatal should return the successful result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].recoverEitherTFromNonFatal should return the failed result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnFailedResult,
      ),
    )

  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: Fx: Functor, A](a: => A): F[A] =
    Fx[F].effectOf(a)

  object IoSpec {

    def testMonadLaws: List[Test] = {
      import cats.syntax.eq.*

      given eqIo: Eq[Task[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).runSyncUnsafe()

//      given ioFx: Fx[Task] = fx.ioFx

      MonadSpec.testMonadLaws[Task]("IO")
    }

    object CanCatchSpec {

      def testCanCatch_IO_catchNonFatalThrowableShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = expectedExpcetion.asLeft[Int]
        val actual            = Fx[Task].catchNonFatalThrowable(fa).runSyncUnsafe()

        actual ==== expected
      }

      def testCanCatch_IO_catchNonFatalThrowableShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[Task, Int](throwThrowable[Int](fatalExpcetion))

        try {
          val actual = Fx[Task].catchNonFatalThrowable(fa).runSyncUnsafe()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: SomeControlThrowable =>
            ex.getMessage ==== fatalExpcetion.getMessage

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanCatch_IO_catchNonFatalThrowableShouldReturnSuccessfulResult: Result = {

        val fa       = run[Task, Int](1)
        val expected = 1.asRight[Throwable]
        val actual   = Fx[Task].catchNonFatalThrowable(fa).runSyncUnsafe()

        actual ==== expected
      }

      def testCanCatch_IO_catchNonFatalShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual            = Fx[Task].catchNonFatal(fa)(SomeError.someThrowable).runSyncUnsafe()

        actual ==== expected
      }

      def testCanCatch_IO_catchNonFatalShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[Task, Int](throwThrowable[Int](fatalExpcetion))

        try {
          val actual = Fx[Task].catchNonFatal(fa)(SomeError.someThrowable).runSyncUnsafe()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: SomeControlThrowable =>
            ex.getMessage ==== fatalExpcetion.getMessage

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanCatch_IO_catchNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = run[Task, Int](1)
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Task].catchNonFatal(fa)(SomeError.someThrowable).runSyncUnsafe()

        actual ==== expected
      }

      def testCanCatch_IO_catchNonFatalEitherShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa       = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual   = Fx[Task].catchNonFatalEither(fa)(SomeError.someThrowable).runSyncUnsafe()

        actual ==== expected
      }

      def testCanCatch_IO_catchNonFatalEitherShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

        try {
          val actual = Fx[Task].catchNonFatalEither(fa)(SomeError.someThrowable).runSyncUnsafe()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: SomeControlThrowable =>
            ex.getMessage ==== fatalExpcetion.getMessage

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanCatch_IO_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Task].catchNonFatalEither(fa)(SomeError.someThrowable).runSyncUnsafe()

        actual ==== expected
      }

      def testCanCatch_IO_catchNonFatalEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Task].catchNonFatalEither(fa)(SomeError.someThrowable).runSyncUnsafe()

        actual ==== expected
      }

      def testCanCatch_IO_catchNonFatalEitherTShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
        val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual   = Fx[Task].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.runSyncUnsafe()

        actual ==== expected
      }

      def testCanCatch_IO_catchNonFatalEitherTShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

        try {
          val actual = Fx[Task].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.runSyncUnsafe()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: SomeControlThrowable =>
            ex.getMessage ==== fatalExpcetion.getMessage

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanCatch_IO_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Task].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.runSyncUnsafe()

        actual ==== expected
      }

      def testCanCatch_IO_catchNonFatalEitherTShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Task].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.runSyncUnsafe()

        actual ==== expected
      }

    }

    object CanHandleErrorSpec {

      def testCanHandleError_IO_handleNonFatalWithShouldHandleNonFatalWith: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 123
        val actual            = Fx[Task]
          .handleNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) =>
              IO.pure(expected)
          }
          .runSyncUnsafe()

        actual ==== expected
      }

      def testCanHandleError_IO_handleNonFatalWithShouldNotHandleFatalWith: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[Task, Int](throwThrowable[Int](fatalExpcetion))

        try {
          val actual = Fx[Task].handleNonFatalWith(fa)(_ => IO.pure(123)).runSyncUnsafe()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_IO_handleNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = run[Task, Int](1)
        val expected = 1
        val actual   = Fx[Task].handleNonFatalWith(fa)(_ => IO.pure(999)).runSyncUnsafe()

        actual ==== expected
      }

      def testCanHandleError_IO_handleNonFatalWithEitherShouldHandleNonFatalWith: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
        val actualFailedResult   =
          Fx[Task].handleNonFatalWith(fa)(_ => IO.pure(expectedFailedResult)).runSyncUnsafe()

        val expectedSuccessResult = 1.asRight[SomeError]
        val actualSuccessResult   =
          Fx[Task].handleNonFatalWith(fa)(_ => IO.pure(1.asRight[SomeError])).runSyncUnsafe()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      def testCanHandleError_IO_handleNonFatalWithEitherShouldNotHandleFatalWith: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

        try {
          val actual = Fx[Task].handleNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).runSyncUnsafe()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: SomeControlThrowable =>
            ex.getMessage ==== fatalExpcetion.getMessage

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_IO_handleNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Task].handleNonFatalWith(fa)(_ => IO(999.asRight[SomeError])).runSyncUnsafe()

        actual ==== expected
      }

      def testCanHandleError_IO_handleNonFatalWithEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Task].handleNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).runSyncUnsafe()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherNonFatalWithShouldHandleNonFatalWith: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult    = Fx[Task]
          .handleEitherNonFatalWith(fa)(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
          .runSyncUnsafe()
        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[Task].handleEitherNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).runSyncUnsafe()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      def testCanHandleError_IO_handleEitherNonFatalWithShouldNotHandleFatalWith: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

        try {
          val actual =
            Fx[Task]
              .handleEitherNonFatalWith(fa)(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
              .runSyncUnsafe()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_IO_handleEitherNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[Task].handleEitherNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).runSyncUnsafe()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherNonFatalWithShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[Task].handleEitherNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).runSyncUnsafe()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherTNonFatalWithShouldHandleNonFatalWith: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
        val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult    = Fx[Task]
          .handleEitherTNonFatalWith(fa)(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
          .value
          .runSyncUnsafe()
        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[Task].handleEitherTNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).value.runSyncUnsafe()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      def testCanHandleError_IO_handleEitherTNonFatalWithShouldNotHandleFatalWith: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

        try {
          val actual =
            Fx[Task]
              .handleEitherTNonFatalWith(fa)(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
              .value
              .runSyncUnsafe()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_IO_handleEitherTNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[Task].handleEitherTNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).value.runSyncUnsafe()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherTNonFatalWithShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[Task].handleEitherTNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).value.runSyncUnsafe()

        actual ==== expected
      }

      def testCanHandleError_IO_handleNonFatalShouldHandleNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 123
        val actual            = Fx[Task]
          .handleNonFatal(fa) {
            case NonFatal(`expectedExpcetion`) =>
              expected
          }
          .runSyncUnsafe()

        actual ==== expected
      }

      def testCanHandleError_IO_handleNonFatalShouldNotHandleFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[Task, Int](throwThrowable[Int](fatalExpcetion))

        try {
          val actual = Fx[Task].handleNonFatal(fa)(_ => 123).runSyncUnsafe()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_IO_handleNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = run[Task, Int](1)
        val expected = 1
        val actual   = Fx[Task].handleNonFatal(fa)(_ => 999).runSyncUnsafe()

        actual ==== expected
      }

      def testCanHandleError_IO_handleNonFatalEitherShouldHandleNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
        val actualFailedResult   = Fx[Task].handleNonFatal(fa)(_ => expectedFailedResult).runSyncUnsafe()

        val expectedSuccessResult = 1.asRight[SomeError]
        val actualSuccessResult   = Fx[Task].handleNonFatal(fa)(_ => 1.asRight[SomeError]).runSyncUnsafe()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      def testCanHandleError_IO_handleNonFatalEitherShouldNotHandleFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

        try {
          val actual = Fx[Task].handleNonFatal(fa)(_ => 123.asRight[SomeError]).runSyncUnsafe()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_IO_handleNonFatalEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Task].handleNonFatal(fa)(_ => 999.asRight[SomeError]).runSyncUnsafe()

        actual ==== expected
      }

      def testCanHandleError_IO_handleNonFatalEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Task].handleNonFatal(fa)(_ => 123.asRight[SomeError]).runSyncUnsafe()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherNonFatalShouldHandleNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult    = Fx[Task]
          .handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
          .runSyncUnsafe()
        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[Task]
            .handleEitherNonFatal(fa)(_ => 123.asRight[SomeError])
            .runSyncUnsafe()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      def testCanHandleError_IO_handleEitherNonFatalShouldNotHandleFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

        try {
          val actual =
            Fx[Task]
              .handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
              .runSyncUnsafe()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_IO_handleEitherNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Task].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError]).runSyncUnsafe()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherNonFatalShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[Task].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError]).runSyncUnsafe()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherTNonFatalShouldHandleNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
        val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult    = Fx[Task]
          .handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
          .value
          .runSyncUnsafe()
        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[Task].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value.runSyncUnsafe()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      def testCanHandleError_IO_handleEitherTNonFatalShouldNotHandleFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

        try {
          val actual =
            Fx[Task]
              .handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
              .value
              .runSyncUnsafe()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_IO_handleEitherTNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Task].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value.runSyncUnsafe()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherTNonFatalShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[Task].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value.runSyncUnsafe()

        actual ==== expected
      }

    }

    object CanRecoverSpec {

      def testCanRecover_IO_recoverFromNonFatalWithShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 123
        val actual            = Fx[Task]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) =>
              IO.pure(expected)
          }
          .runSyncUnsafe()

        actual ==== expected
      }

      def testCanRecover_IO_recoverFromNonFatalWithShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))

        val io = Fx[Task].recoverFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => IO.pure(123) }
        try {
          val actual = io.runSyncUnsafe()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_IO_recoverFromNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = run[Task, Int](1)
        val expected = 1
        val actual   = Fx[Task]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(_) => IO.pure(999)
          }
          .runSyncUnsafe()

        actual ==== expected
      }

      def testCanRecover_IO_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
        val actualFailedResult   = Fx[Task]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) => IO.pure(expectedFailedResult)
          }
          .runSyncUnsafe()

        val expectedSuccessResult = 1.asRight[SomeError]
        val actualSuccessResult   = Fx[Task]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) => IO.pure(1.asRight[SomeError])
          }
          .runSyncUnsafe()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      def testCanRecover_IO_recoverFromNonFatalWithEitherShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

        val io = Fx[Task].recoverFromNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) => IO.pure(123.asRight[SomeError])
        }
        try {
          val actual = io.runSyncUnsafe()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Task]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(_) => IO(999.asRight[SomeError])
          }
          .runSyncUnsafe()

        actual ==== expected
      }

      def testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Task]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(_) => IO.pure(123.asRight[SomeError])
          }
          .runSyncUnsafe()

        actual ==== expected
      }

      def testCanRecover_IO_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult    = Fx[Task]
          .recoverEitherFromNonFatalWith(fa) {
            case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
          }
          .runSyncUnsafe()
        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   = Fx[Task]
          .recoverEitherFromNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) => IO.pure(123.asRight[SomeError])
          }
          .runSyncUnsafe()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      def testCanRecover_IO_recoverEitherFromNonFatalWithShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

        val io = Fx[Task].recoverEitherFromNonFatalWith(fa) {
          case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
        }
        try {
          val actual = io.runSyncUnsafe()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_IO_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Task]
          .recoverEitherFromNonFatalWith(fa) {
            case NonFatal(_) => IO.pure(123.asRight[SomeError])
          }
          .runSyncUnsafe()

        actual ==== expected
      }

      def testCanRecover_IO_recoverEitherFromNonFatalWithShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[Task]
            .recoverEitherFromNonFatalWith(fa) {
              case NonFatal(_) => IO.pure(123.asRight[SomeError])
            }
            .runSyncUnsafe()

        actual ==== expected
      }

      def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
        val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult    = Fx[Task]
          .recoverEitherTFromNonFatalWith(fa) {
            case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
          }
          .value
          .runSyncUnsafe()
        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   = Fx[Task]
          .recoverEitherTFromNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) => IO.pure(123.asRight[SomeError])
          }
          .value
          .runSyncUnsafe()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))

        val io = Fx[Task].recoverEitherTFromNonFatalWith(fa) {
          case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
        }
        try {
          val actual = io.value.runSyncUnsafe()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Task]
          .recoverEitherTFromNonFatalWith(fa) {
            case NonFatal(_) => IO.pure(123.asRight[SomeError])
          }
          .value
          .runSyncUnsafe()

        actual ==== expected
      }

      def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[Task]
            .recoverEitherTFromNonFatalWith(fa) {
              case NonFatal(_) => IO.pure(123.asRight[SomeError])
            }
            .value
            .runSyncUnsafe()

        actual ==== expected
      }

      // /

      def testCanRecover_IO_recoverFromNonFatalShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 123
        val actual            = Fx[Task]
          .recoverFromNonFatal(fa) {
            case NonFatal(`expectedExpcetion`) =>
              expected
          }
          .runSyncUnsafe()

        actual ==== expected
      }

      def testCanRecover_IO_recoverFromNonFatalShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))

        val io = Fx[Task].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123 }
        try {
          val actual = io.runSyncUnsafe()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_IO_recoverFromNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = run[Task, Int](1)
        val expected = 1
        val actual   = Fx[Task].recoverFromNonFatal(fa) { case NonFatal(_) => 999 }.runSyncUnsafe()

        actual ==== expected
      }

      def testCanRecover_IO_recoverFromNonFatalEitherShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
        val actualFailedResult   = Fx[Task]
          .recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expectedFailedResult }
          .runSyncUnsafe()

        val expectedSuccessResult = 1.asRight[SomeError]
        val actualSuccessResult   = Fx[Task]
          .recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }
          .runSyncUnsafe()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      def testCanRecover_IO_recoverFromNonFatalEitherShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

        val io = Fx[Task].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }
        try {
          val actual = io.runSyncUnsafe()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_IO_recoverFromNonFatalEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Task].recoverFromNonFatal(fa) { case NonFatal(_) => 999.asRight[SomeError] }.runSyncUnsafe()

        actual ==== expected
      }

      def testCanRecover_IO_recoverFromNonFatalEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual = Fx[Task].recoverFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }.runSyncUnsafe()

        actual ==== expected
      }

      def testCanRecover_IO_recoverEitherFromNonFatalShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult    =
          Fx[Task]
            .recoverEitherFromNonFatal(fa) {
              case err => SomeError.someThrowable(err).asLeft[Int]
            }
            .runSyncUnsafe()
        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[Task]
            .recoverEitherFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }
            .runSyncUnsafe()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      def testCanRecover_IO_recoverEitherFromNonFatalShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

        val io =
          Fx[Task].recoverEitherFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }
        try {
          val actual = io.runSyncUnsafe()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_IO_recoverEitherFromNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[Task]
            .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
            .runSyncUnsafe()

        actual ==== expected
      }

      def testCanRecover_IO_recoverEitherFromNonFatalShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[Task]
            .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
            .runSyncUnsafe()

        actual ==== expected
      }

      def testCanRecover_IO_recoverEitherTFromNonFatalShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
        val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult    =
          Fx[Task]
            .recoverEitherTFromNonFatal(fa) {
              case err => SomeError.someThrowable(err).asLeft[Int]
            }
            .value
            .runSyncUnsafe()
        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[Task]
            .recoverEitherTFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }
            .value
            .runSyncUnsafe()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      def testCanRecover_IO_recoverEitherTFromNonFatalShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))

        val io =
          Fx[Task].recoverEitherTFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }
        try {
          val actual = io.value.runSyncUnsafe()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[Task]
            .recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
            .value
            .runSyncUnsafe()

        actual ==== expected
      }

      def testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[Task]
            .recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
            .value
            .runSyncUnsafe()

        actual ==== expected
      }

    }

  }

}
