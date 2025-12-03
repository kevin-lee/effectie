package effectie.instances.ce2

import cats.data.EitherT
import cats.effect._
import cats.syntax.all._
import cats.Eq
import effectie.SomeControlThrowable
import effectie.core._
import effectie.instances.ce2.fx._
import effectie.specs.MonadSpec
import effectie.specs.fxSpec.FxSpecs
import effectie.syntax.error._
import effectie.testing.tools
import effectie.testing.types.SomeError
import hedgehog._
import hedgehog.runner._

import scala.util.control.{ControlThrowable, NonFatal}

/** @author Kevin Lee
  * @since 2020-12-06
  */
object fxSpec extends Properties {

  override def tests: List[Test] = ioSpecs

  private val assertWithAttempt: (IO[Int], Either[Throwable, Int]) => Result = { (io, expected) =>
    val actual = io.attempt.unsafeRunSync()
    (actual ==== expected).log(s"$actual does not equal to $expected")
  }

  private val unit: Unit = ()

  /* IO */
  private val ioSpecs = List(
    property("test Fx[IO].effectOf", FxSpecs.testEffectOf[IO](_.unsafeRunSync() ==== unit)),
    property("test Fx[IO].fromEffect(effectOf)", FxSpecs.testFromEffect[IO](_.unsafeRunSync() ==== unit)),
    property("test Fx[IO].fromEffect(pureOf)", FxSpecs.testFromEffectWithPure[IO](_.unsafeRunSync() ==== unit)),
    property("test Fx[IO].pureOf", FxSpecs.testPureOf[IO](_.unsafeRunSync() ==== unit)),
    property(
      "test Fx[IO].pureOrError(success case)",
      FxSpecs.testPureOrErrorSuccessCase[IO](_.unsafeRunSync() ==== unit),
    ),
    example(
      "test Fx[IO].pureOrError(error case)",
      FxSpecs.testPureOrErrorErrorCase[IO] { (io, expected) =>
        tools.expectThrowable(io.unsafeRunSync(), expected)
      },
    ),
    example("test Fx[IO].unitOf", FxSpecs.testUnitOf[IO](_.unsafeRunSync() ==== unit)),
    example(
      "test Fx[IO].errorOf",
      FxSpecs.testErrorOf[IO] { (io, expected) =>
        tools.expectThrowable(io.unsafeRunSync(), expected)
      },
    ),
    property("test Fx[IO].fromEither(Right)", FxSpecs.testFromEitherRightCase[IO](assertWithAttempt)),
    property("test Fx[IO].fromEither(Left)", FxSpecs.testFromEitherLeftCase[IO](assertWithAttempt)),
    property("test Fx[IO].fromOption(Some)", FxSpecs.testFromOptionSomeCase[IO](assertWithAttempt)),
    property("test Fx[IO].fromOption(None)", FxSpecs.testFromOptionNoneCase[IO](assertWithAttempt)),
    property("test Fx[IO].fromTry(Success)", FxSpecs.testFromTrySuccessCase[IO](assertWithAttempt)),
    property("test Fx[IO].fromTry(Failure)", FxSpecs.testFromTryFailureCase[IO](assertWithAttempt)),
  ) ++
    IoSpec.testMonadLaws ++
    List(
      example(
        "test Fx[IO]catchNonFatalThrowable should catch NonFatal",
        IoSpec.CanCatchSpec.testFx_IO_catchNonFatalThrowableShouldCatchNonFatal,
      ),
      example(
        "test Fx[IO]catchNonFatalThrowable should not catch Fatal",
        IoSpec.CanCatchSpec.testFx_IO_catchNonFatalThrowableShouldNotCatchFatal,
      ),
      example(
        "test Fx[IO]catchNonFatalThrowable should return the successful result",
        IoSpec.CanCatchSpec.testFx_IO_catchNonFatalThrowableShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[IO]catchNonFatal should catch NonFatal",
        IoSpec.CanCatchSpec.testFx_IO_catchNonFatalShouldCatchNonFatal,
      ),
      example(
        "test Fx[IO]catchNonFatal should not catch Fatal",
        IoSpec.CanCatchSpec.testFx_IO_catchNonFatalShouldNotCatchFatal,
      ),
      example(
        "test Fx[IO]catchNonFatal should return the successful result",
        IoSpec.CanCatchSpec.testFx_IO_catchNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[IO]catchNonFatalEither should catch NonFatal",
        IoSpec.CanCatchSpec.testFx_IO_catchNonFatalEitherShouldCatchNonFatal,
      ),
      example(
        "test Fx[IO]catchNonFatalEither should not catch Fatal",
        IoSpec.CanCatchSpec.testFx_IO_catchNonFatalEitherShouldNotCatchFatal,
      ),
      example(
        "test Fx[IO]catchNonFatalEither should return the successful result",
        IoSpec.CanCatchSpec.testFx_IO_catchNonFatalEitherShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[IO]catchNonFatalEither should return the failed result",
        IoSpec.CanCatchSpec.testFx_IO_catchNonFatalEitherShouldReturnFailedResult,
      ),
      example(
        "test Fx[IO]catchNonFatalEitherT should catch NonFatal",
        IoSpec.CanCatchSpec.testFx_IO_catchNonFatalEitherTShouldCatchNonFatal,
      ),
      example(
        "test Fx[IO]catchNonFatalEitherT should not catch Fatal",
        IoSpec.CanCatchSpec.testFx_IO_catchNonFatalEitherTShouldNotCatchFatal,
      ),
      example(
        "test Fx[IO]catchNonFatalEitherT should return the successful result",
        IoSpec.CanCatchSpec.testFx_IO_catchNonFatalEitherTShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[IO]catchNonFatalEitherT should return the failed result",
        IoSpec.CanCatchSpec.testFx_IO_catchNonFatalEitherTShouldReturnFailedResult,
      ),
    ) ++
    List(
      /* IO */
      example(
        "test Fx[IO].handleNonFatalWith should handle NonFatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalWithShouldHandleNonFatalWith,
      ),
      example(
        "test Fx[IO].handleNonFatalWith should not handle Fatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalWithShouldNotHandleFatalWith,
      ),
      example(
        "test Fx[IO].handleNonFatalWith should return the successful result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalWithShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[IO].handleNonFatalWithEither should handle NonFatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalWithEitherShouldHandleNonFatalWith,
      ),
      example(
        "test Fx[IO].handleNonFatalWithEither should not handle Fatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalWithEitherShouldNotHandleFatalWith,
      ),
      example(
        "test Fx[IO].handleNonFatalWithEither should return the successful result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalWithEitherShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[IO].handleNonFatalWithEither should return the failed result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalWithEitherShouldReturnFailedResult,
      ),
      example(
        "test Fx[IO].handleEitherNonFatalWith should handle NonFatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherNonFatalWithShouldHandleNonFatalWith,
      ),
      example(
        "test Fx[IO].handleEitherNonFatalWith should not handle Fatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherNonFatalWithShouldNotHandleFatalWith,
      ),
      example(
        "test Fx[IO].handleEitherNonFatalWith should return the successful result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherNonFatalWithShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[IO].handleEitherNonFatalWith should return the failed result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherNonFatalWithShouldReturnFailedResult,
      ),
      example(
        "test Fx[IO].handleEitherTNonFatalWith should handle NonFatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherTNonFatalWithShouldHandleNonFatalWith,
      ),
      example(
        "test Fx[IO].handleEitherTNonFatalWith should not handle Fatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherTNonFatalWithShouldNotHandleFatalWith,
      ),
      example(
        "test Fx[IO].handleEitherTNonFatalWith should return the successful result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherTNonFatalWithShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[IO].handleEitherTNonFatalWith should return the failed result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherTNonFatalWithShouldReturnFailedResult,
      ),
      example(
        "test Fx[IO].handleNonFatal should handle NonFatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalShouldHandleNonFatal,
      ),
      example(
        "test Fx[IO].handleNonFatal should not handle Fatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalShouldNotHandleFatal,
      ),
      example(
        "test Fx[IO].handleNonFatal should return the successful result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[IO].handleNonFatalEither should handle NonFatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalEitherShouldHandleNonFatal,
      ),
      example(
        "test Fx[IO].handleNonFatalEither should not handle Fatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalEitherShouldNotHandleFatal,
      ),
      example(
        "test Fx[IO].handleNonFatalEither should return the successful result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalEitherShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[IO].handleNonFatalEither should return the failed result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalEitherShouldReturnFailedResult,
      ),
      example(
        "test Fx[IO].handleEitherNonFatal should handle NonFatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherNonFatalShouldHandleNonFatal,
      ),
      example(
        "test Fx[IO].handleEitherNonFatal should not handle Fatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherNonFatalShouldNotHandleFatal,
      ),
      example(
        "test Fx[IO].handleEitherNonFatal should return the successful result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[IO].handleEitherNonFatal should return the failed result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherNonFatalShouldReturnFailedResult,
      ),
      example(
        "test Fx[IO].handleEitherTNonFatal should handle NonFatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherTNonFatalShouldHandleNonFatal,
      ),
      example(
        "test Fx[IO].handleEitherTNonFatal should not handle Fatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherTNonFatalShouldNotHandleFatal,
      ),
      example(
        "test Fx[IO].handleEitherTNonFatal should return the successful result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherTNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[IO].handleEitherTNonFatal should return the failed result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherTNonFatalShouldReturnFailedResult,
      ),
    ) ++ List(
      example(
        "test Fx[IO].recoverFromNonFatalWith should catch NonFatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalWithShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[IO].recoverFromNonFatalWith should not catch Fatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalWithShouldNotCatchFatal,
      ),
      example(
        "test Fx[IO].recoverFromNonFatalWith should return the successful result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalWithShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[IO].recoverFromNonFatalWithEither should catch NonFatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[IO].recoverFromNonFatalWithEither should not catch Fatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldNotCatchFatal,
      ),
      example(
        "test Fx[IO].recoverFromNonFatalWithEither should return the successful result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[IO].recoverFromNonFatalWithEither should return the failed result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnFailedResult,
      ),
      example(
        "test Fx[IO].recoverEitherFromNonFatalWith should catch NonFatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[IO].recoverEitherFromNonFatalWith should not catch Fatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherFromNonFatalWithShouldNotCatchFatal,
      ),
      example(
        "test Fx[IO].recoverEitherFromNonFatalWith should return the successful result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[IO].recoverEitherFromNonFatalWith should return the failed result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherFromNonFatalWithShouldReturnFailedResult,
      ),
      example(
        "test Fx[IO].recoverEitherTFromNonFatalWith should catch NonFatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[IO].recoverEitherTFromNonFatalWith should not catch Fatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldNotCatchFatal,
      ),
      example(
        "test Fx[IO].recoverEitherTFromNonFatalWith should return the successful result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[IO].recoverEitherTFromNonFatalWith should return the failed result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnFailedResult,
      ),
      example(
        "test Fx[IO].recoverFromNonFatal should catch NonFatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[IO].recoverFromNonFatal should not catch Fatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalShouldNotCatchFatal,
      ),
      example(
        "test Fx[IO].recoverFromNonFatal should return the successful result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[IO].recoverFromNonFatalEither should catch NonFatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[IO].recoverFromNonFatalEither should not catch Fatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldNotCatchFatal,
      ),
      example(
        "test Fx[IO].recoverFromNonFatalEither should return the successful result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[IO].recoverFromNonFatalEither should return the failed result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldReturnFailedResult,
      ),
      example(
        "test Fx[IO].recoverEitherFromNonFatal should catch NonFatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherFromNonFatalShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[IO].recoverEitherFromNonFatal should not catch Fatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherFromNonFatalShouldNotCatchFatal,
      ),
      example(
        "test Fx[IO].recoverEitherFromNonFatal should return the successful result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherFromNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[IO].recoverEitherFromNonFatal should return the failed result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherFromNonFatalShouldReturnFailedResult,
      ),
      example(
        "test Fx[IO].recoverEitherTFromNonFatal should catch NonFatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[IO].recoverEitherTFromNonFatal should not catch Fatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldNotCatchFatal,
      ),
      example(
        "test Fx[IO].recoverEitherTFromNonFatal should return the successful result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[IO].recoverEitherTFromNonFatal should return the failed result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnFailedResult,
      ),
    ) ++
    List(
      example(
        "test Fx[IO].onNonFatalWith should do something for NonFatal",
        IoSpec.OnNonFatalSpec.testOnNonFatal_IO_onNonFatalWithShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[IO].onNonFatalWith should do nothing for Fatal",
        IoSpec.OnNonFatalSpec.testOnNonFatal_IO_onNonFatalWithShouldNotCatchFatal,
      ),
      example(
        "test Fx[IO].onNonFatalWith should do nothing for the successful result",
        IoSpec.OnNonFatalSpec.testOnNonFatal_IO_onNonFatalWithShouldReturnSuccessfulResult,
      ),
    )

  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: Fx, A](a: => A): F[A] =
    Fx[F].effectOf(a)

  object IoSpec {

    def testMonadLaws: List[Test] = {
      import cats.syntax.eq._

      implicit val eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).unsafeRunSync()

      MonadSpec.testMonadLaws[IO]("IO")
    }

    object CanCatchSpec {

      def testFx_IO_catchNonFatalThrowableShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = expectedExpcetion.asLeft[Int]
        val actual            = Fx[IO].catchNonFatalThrowable(fa).unsafeRunSync()

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testFx_IO_catchNonFatalThrowableShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))

        try {
          val actual = Fx[IO].catchNonFatalThrowable(fa).unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: SomeControlThrowable =>
            ex.getMessage ==== fatalExpcetion.getMessage

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testFx_IO_catchNonFatalThrowableShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Int](1)
        val expected = 1.asRight[Throwable]
        val actual   = Fx[IO].catchNonFatalThrowable(fa).unsafeRunSync()

        actual ==== expected
      }

      def testFx_IO_catchNonFatalShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual            = Fx[IO].catchNonFatal(fa)(SomeError.someThrowable).unsafeRunSync()

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testFx_IO_catchNonFatalShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))

        try {
          val actual = Fx[IO].catchNonFatal(fa)(SomeError.someThrowable).unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: SomeControlThrowable =>
            ex.getMessage ==== fatalExpcetion.getMessage

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testFx_IO_catchNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Int](1)
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].catchNonFatal(fa)(SomeError.someThrowable).unsafeRunSync()

        actual ==== expected
      }

      def testFx_IO_catchNonFatalEitherShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa       = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual   = Fx[IO].catchNonFatalEither(fa)(SomeError.someThrowable).unsafeRunSync()

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testFx_IO_catchNonFatalEitherShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

        try {
          val actual = Fx[IO].catchNonFatalEither(fa)(SomeError.someThrowable).unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: SomeControlThrowable =>
            ex.getMessage ==== fatalExpcetion.getMessage

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testFx_IO_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].catchNonFatalEither(fa)(SomeError.someThrowable).unsafeRunSync()

        actual ==== expected
      }

      def testFx_IO_catchNonFatalEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[IO].catchNonFatalEither(fa)(SomeError.someThrowable).unsafeRunSync()

        actual ==== expected
      }

      def testFx_IO_catchNonFatalEitherTShouldCatchNonFatal: Result = {

        val expectedExpcetion               = new RuntimeException("Something's wrong")
        val fa: EitherT[IO, SomeError, Int] = EitherT(
          run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        )
        val expected                        = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual = Fx[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.unsafeRunSync()

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testFx_IO_catchNonFatalEitherTShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

        try {
          val actual = Fx[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: SomeControlThrowable =>
            ex.getMessage ==== fatalExpcetion.getMessage

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testFx_IO_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.unsafeRunSync()

        actual ==== expected
      }

      def testFx_IO_catchNonFatalEitherTShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.unsafeRunSync()

        actual ==== expected
      }

    }

    object CanHandleErrorSpec {

      def testCanHandleError_IO_handleNonFatalWithShouldHandleNonFatalWith: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 123
        val actual            = Fx[IO]
          .handleNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) =>
              IO.pure(expected)
            case err =>
              throw err // scalafix:ok DisableSyntax.throw
          }
          .unsafeRunSync()

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_IO_handleNonFatalWithShouldNotHandleFatalWith: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))

        try {
          val actual = Fx[IO].handleNonFatalWith(fa)(_ => IO.pure(123)).unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_IO_handleNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Int](1)
        val expected = 1
        val actual   = Fx[IO].handleNonFatalWith(fa)(_ => IO.pure(999)).unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleNonFatalWithEitherShouldHandleNonFatalWith: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
        val actualFailedResult   =
          Fx[IO].handleNonFatalWith(fa)(_ => IO.pure(expectedFailedResult)).unsafeRunSync()

        val expectedSuccessResult = 1.asRight[SomeError]
        val actualSuccessResult   =
          Fx[IO].handleNonFatalWith(fa)(_ => IO.pure(1.asRight[SomeError])).unsafeRunSync()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_IO_handleNonFatalWithEitherShouldNotHandleFatalWith: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

        try {
          val actual = Fx[IO].handleNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: SomeControlThrowable =>
            ex.getMessage ==== fatalExpcetion.getMessage

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_IO_handleNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].handleNonFatalWith(fa)(_ => IO(999.asRight[SomeError])).unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleNonFatalWithEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[IO].handleNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherNonFatalWithShouldHandleNonFatalWith: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult    = Fx[IO]
          .handleEitherNonFatalWith(fa)(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
          .unsafeRunSync()
        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[IO].handleEitherNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).unsafeRunSync()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_IO_handleEitherNonFatalWithShouldNotHandleFatalWith: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

        try {
          val actual =
            Fx[IO]
              .handleEitherNonFatalWith(fa)(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
              .unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_IO_handleEitherNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[IO].handleEitherNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherNonFatalWithShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[IO].handleEitherNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherTNonFatalWithShouldHandleNonFatalWith: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
        val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult    = Fx[IO]
          .handleEitherTNonFatalWith(fa)(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
          .value
          .unsafeRunSync()
        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[IO].handleEitherTNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).value.unsafeRunSync()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_IO_handleEitherTNonFatalWithShouldNotHandleFatalWith: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

        try {
          val actual =
            Fx[IO]
              .handleEitherTNonFatalWith(fa)(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
              .value
              .unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_IO_handleEitherTNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[IO].handleEitherTNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).value.unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherTNonFatalWithShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[IO].handleEitherTNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).value.unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleNonFatalShouldHandleNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 123
        val actual            = Fx[IO]
          .handleNonFatal(fa) {
            case NonFatal(`expectedExpcetion`) =>
              expected
            case err =>
              throw err // scalafix:ok DisableSyntax.throw
          }
          .unsafeRunSync()

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_IO_handleNonFatalShouldNotHandleFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))

        try {
          val actual = Fx[IO].handleNonFatal(fa)(_ => 123).unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_IO_handleNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Int](1)
        val expected = 1
        val actual   = Fx[IO].handleNonFatal(fa)(_ => 999).unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleNonFatalEitherShouldHandleNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
        val actualFailedResult   = Fx[IO].handleNonFatal(fa)(_ => expectedFailedResult).unsafeRunSync()

        val expectedSuccessResult = 1.asRight[SomeError]
        val actualSuccessResult   = Fx[IO].handleNonFatal(fa)(_ => 1.asRight[SomeError]).unsafeRunSync()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_IO_handleNonFatalEitherShouldNotHandleFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

        try {
          val actual = Fx[IO].handleNonFatal(fa)(_ => 123.asRight[SomeError]).unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_IO_handleNonFatalEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].handleNonFatal(fa)(_ => 999.asRight[SomeError]).unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleNonFatalEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[IO].handleNonFatal(fa)(_ => 123.asRight[SomeError]).unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherNonFatalShouldHandleNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult    = Fx[IO]
          .handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
          .unsafeRunSync()
        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[IO].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError]).unsafeRunSync()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_IO_handleEitherNonFatalShouldNotHandleFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

        try {
          val actual =
            Fx[IO]
              .handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
              .unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_IO_handleEitherNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError]).unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherNonFatalShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[IO].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError]).unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherTNonFatalShouldHandleNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
        val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult    = Fx[IO]
          .handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
          .value
          .unsafeRunSync()
        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[IO].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value.unsafeRunSync()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_IO_handleEitherTNonFatalShouldNotHandleFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

        try {
          val actual =
            Fx[IO]
              .handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
              .value
              .unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_IO_handleEitherTNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value.unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherTNonFatalShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[IO].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value.unsafeRunSync()

        actual ==== expected
      }

    }

    object CanRecoverSpec {

      def testCanRecover_IO_recoverFromNonFatalWithShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 123
        val actual            = Fx[IO]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) =>
              IO.pure(expected)
          }
          .unsafeRunSync()

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_IO_recoverFromNonFatalWithShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))

        val io = Fx[IO].recoverFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => IO.pure(123) }
        try {
          val actual = io.unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_IO_recoverFromNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Int](1)
        val expected = 1
        val actual   = Fx[IO]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(_) => IO.pure(999)
          }
          .unsafeRunSync()

        actual ==== expected
      }

      def testCanRecover_IO_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
        val actualFailedResult   = Fx[IO]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) => IO.pure(expectedFailedResult)
          }
          .unsafeRunSync()

        val expectedSuccessResult = 1.asRight[SomeError]
        val actualSuccessResult   = Fx[IO]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) => IO.pure(1.asRight[SomeError])
          }
          .unsafeRunSync()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_IO_recoverFromNonFatalWithEitherShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

        val io = Fx[IO].recoverFromNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) => IO.pure(123.asRight[SomeError])
        }
        try {
          val actual = io.unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(_) => IO(999.asRight[SomeError])
          }
          .unsafeRunSync()

        actual ==== expected
      }

      def testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[IO]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(_) => IO.pure(123.asRight[SomeError])
          }
          .unsafeRunSync()

        actual ==== expected
      }

      def testCanRecover_IO_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult    = Fx[IO]
          .recoverEitherFromNonFatalWith(fa) {
            case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
          }
          .unsafeRunSync()
        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   = Fx[IO]
          .recoverEitherFromNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) => IO.pure(123.asRight[SomeError])
          }
          .unsafeRunSync()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_IO_recoverEitherFromNonFatalWithShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

        val io = Fx[IO].recoverEitherFromNonFatalWith(fa) {
          case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
        }
        try {
          val actual = io.unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_IO_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO]
          .recoverEitherFromNonFatalWith(fa) {
            case NonFatal(_) => IO.pure(123.asRight[SomeError])
          }
          .unsafeRunSync()

        actual ==== expected
      }

      def testCanRecover_IO_recoverEitherFromNonFatalWithShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[IO]
            .recoverEitherFromNonFatalWith(fa) {
              case NonFatal(_) => IO.pure(123.asRight[SomeError])
            }
            .unsafeRunSync()

        actual ==== expected
      }

      def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
        val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult    = Fx[IO]
          .recoverEitherTFromNonFatalWith(fa) {
            case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
          }
          .value
          .unsafeRunSync()
        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   = Fx[IO]
          .recoverEitherTFromNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) => IO.pure(123.asRight[SomeError])
          }
          .value
          .unsafeRunSync()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))

        val io = Fx[IO].recoverEitherTFromNonFatalWith(fa) {
          case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
        }
        try {
          val actual = io.value.unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO]
          .recoverEitherTFromNonFatalWith(fa) {
            case NonFatal(_) => IO.pure(123.asRight[SomeError])
          }
          .value
          .unsafeRunSync()

        actual ==== expected
      }

      def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[IO]
            .recoverEitherTFromNonFatalWith(fa) {
              case NonFatal(_) => IO.pure(123.asRight[SomeError])
            }
            .value
            .unsafeRunSync()

        actual ==== expected
      }

      // /

      def testCanRecover_IO_recoverFromNonFatalShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 123
        val actual            = Fx[IO]
          .recoverFromNonFatal(fa) {
            case NonFatal(`expectedExpcetion`) =>
              expected
          }
          .unsafeRunSync()

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_IO_recoverFromNonFatalShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))

        val io = Fx[IO].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123 }
        try {
          val actual = io.unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_IO_recoverFromNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Int](1)
        val expected = 1
        val actual   = Fx[IO].recoverFromNonFatal(fa) { case NonFatal(_) => 999 }.unsafeRunSync()

        actual ==== expected
      }

      def testCanRecover_IO_recoverFromNonFatalEitherShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
        val actualFailedResult   = Fx[IO]
          .recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expectedFailedResult }
          .unsafeRunSync()

        val expectedSuccessResult = 1.asRight[SomeError]
        val actualSuccessResult   = Fx[IO]
          .recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }
          .unsafeRunSync()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_IO_recoverFromNonFatalEitherShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

        val io = Fx[IO].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }
        try {
          val actual = io.unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_IO_recoverFromNonFatalEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].recoverFromNonFatal(fa) { case NonFatal(_) => 999.asRight[SomeError] }.unsafeRunSync()

        actual ==== expected
      }

      def testCanRecover_IO_recoverFromNonFatalEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual = Fx[IO].recoverFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }.unsafeRunSync()

        actual ==== expected
      }

      def testCanRecover_IO_recoverEitherFromNonFatalShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult    =
          Fx[IO]
            .recoverEitherFromNonFatal(fa) {
              case err => SomeError.someThrowable(err).asLeft[Int]
            }
            .unsafeRunSync()
        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[IO]
            .recoverEitherFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }
            .unsafeRunSync()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_IO_recoverEitherFromNonFatalShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

        val io =
          Fx[IO].recoverEitherFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }
        try {
          val actual = io.unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_IO_recoverEitherFromNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[IO]
            .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
            .unsafeRunSync()

        actual ==== expected
      }

      def testCanRecover_IO_recoverEitherFromNonFatalShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[IO]
            .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
            .unsafeRunSync()

        actual ==== expected
      }

      def testCanRecover_IO_recoverEitherTFromNonFatalShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
        val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult    =
          Fx[IO]
            .recoverEitherTFromNonFatal(fa) {
              case err => SomeError.someThrowable(err).asLeft[Int]
            }
            .value
            .unsafeRunSync()
        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[IO]
            .recoverEitherTFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }
            .value
            .unsafeRunSync()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_IO_recoverEitherTFromNonFatalShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))

        val io =
          Fx[IO].recoverEitherTFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }
        try {
          val actual = io.value.unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[IO]
            .recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
            .value
            .unsafeRunSync()

        actual ==== expected
      }

      def testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[IO]
            .recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
            .value
            .unsafeRunSync()

        actual ==== expected
      }

    }

    object OnNonFatalSpec {

      def testOnNonFatal_IO_onNonFatalWithShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 123.some
        var actual            = none[Int] // scalafix:ok DisableSyntax.var

        val result =
          try {
            val r = Fx[IO]
              .onNonFatalWith(fa) {
                case NonFatal(`expectedExpcetion`) =>
                  IO.delay {
                    actual = expected
                  } *> IO.unit
              }
              .unsafeRunSync()
            new AssertionError(s"Should have thrown an exception, but it was ${r.toString}.")
          } catch {
            case ex: Throwable =>
              ex
          }

        Result.all(
          List(
            result ==== expectedExpcetion,
            actual ==== expected,
          )
        )
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testOnNonFatal_IO_onNonFatalWithShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
        var actual            = none[Int] // scalafix:ok DisableSyntax.var

        val io = Fx[IO].onNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) =>
            IO.delay {
              actual = 123.some
              ()
            } *> IO.unit
        }
        try {
          val actual = io.unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            Result.all(
              List(
                actual ==== none[Int],
                ex ==== expectedExpcetion,
              )
            )

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testOnNonFatal_IO_onNonFatalWithShouldReturnSuccessfulResult: Result = {

        val expectedResult = 999
        val fa             = run[IO, Int](expectedResult)

        val expected = none[Int]
        var actual   = none[Int] // scalafix:ok DisableSyntax.var

        val result = Fx[IO]
          .onNonFatalWith(fa) {
            case NonFatal(_) =>
              IO.delay {
                actual = 123.some
              } *> IO.unit
          }
          .unsafeRunSync()

        Result.all(
          List(
            result ==== expectedResult,
            actual ==== expected,
          )
        )
      }

    }

  }

}
