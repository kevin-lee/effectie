package effectie.instances.ce3

import cats.Eq
import cats.data.EitherT
import cats.effect._
import cats.effect.unsafe.IORuntime
import cats.syntax.all._
import effectie.SomeControlThrowable
import effectie.core._
import effectie.instances.ce3.compat.CatsEffectIoCompatForFuture
import effectie.specs.MonadSpec
import effectie.specs.fxSpec.FxSpecs
import effectie.syntax.error._
import effectie.testing.types.SomeError
import extras.concurrent.testing.ConcurrentSupport
import extras.hedgehog.ce3.syntax.runner._
import fx._
import hedgehog._
import hedgehog.runner._

import java.util.concurrent.ExecutorService
import scala.util.control.{ControlThrowable, NonFatal}

/** @author Kevin Lee
  * @since 2020-12-06
  */
object fxSpec extends Properties {

  private val assertWithAttempt: (IO[Int], Either[Throwable, Int]) => Result = { (ioA, expected) =>
    withIO { implicit ticker =>
      ioA.attempt.completeThen { actual =>
        (actual ==== expected).log(s"$actual does not equal to $expected")
      }
    }
  }

  override def tests: List[Test] = List(
    /* IO */
    property(
      "test Fx[IO].effectOf",
      FxSpecs.testEffectOf[IO] { io =>
        withIO { implicit ticker =>
          io.completeAs(())
        }
      },
    ),
    property(
      "test Fx[IO].fromEffect(effectOf)",
      FxSpecs.testFromEffect[IO] { io =>
        withIO { implicit ticker =>
          io.completeAs(())
        }
      },
    ),
    property(
      "test Fx[IO].fromEffect(pureOf)",
      FxSpecs.testFromEffectWithPure[IO] { io =>
        withIO { implicit ticker =>
          io.completeAs(())
        }
      },
    ),
    property(
      "test Fx[IO].pureOf",
      FxSpecs.testPureOf[IO] { io =>
        withIO { implicit ticker =>
          io.completeAs(())
        }
      },
    ),
    property(
      "test Fx[IO].pureOrError(success case)",
      FxSpecs.testPureOrErrorSuccessCase[IO] { io =>
        withIO { implicit ticker =>
          io.completeAs(())
        }
      },
    ),
    example(
      "test Fx[IO].pureOrError(error case)",
      FxSpecs.testPureOrErrorErrorCase[IO] { (io, expectedError) =>
        withIO { implicit ticker =>
          io.expectError(expectedError)
        }
      },
    ),
    example(
      "test Fx[IO].unitOf",
      FxSpecs.testUnitOf[IO] { io =>
        withIO { implicit ticker =>
          io.completeAs(())
        }
      },
    ),
    example(
      "test Fx[IO].errorOf",
      FxSpecs.testErrorOf[IO] { (io, expectedError) =>
        withIO { implicit ticker =>
          io.expectError(expectedError)
        }
      },
    ),
    property("test Fx[IO].fromEither(Right)", FxSpecs.testFromEitherRightCase[IO](assertWithAttempt)),
    property("test Fx[IO].fromEither(Left)", FxSpecs.testFromEitherLeftCase[IO](assertWithAttempt)),
    property("test Fx[IO].fromOption(Some)", FxSpecs.testFromOptionSomeCase[IO](assertWithAttempt)),
    property("test Fx[IO].fromOption(None)", FxSpecs.testFromOptionNoneCase[IO](assertWithAttempt)),
    property("test Fx[IO].fromTry(Success)", FxSpecs.testFromTrySuccessCase[IO](assertWithAttempt)),
    property("test Fx[IO].fromTry(Failure)", FxSpecs.testFromTryFailureCase[IO](assertWithAttempt)),
    property(
      "test Fx[IO].flatMapFa(IO[A])(A => IO[B])",
      FxSpecs.testFlatMapFx[IO] { (fb, expected) =>
        runIO {
          fb.map(_ ==== expected)
        }
      },
    ),
    property("test Fx[IO] Monad laws - Identity", IoSpec.testMonadLaws1_Identity),
    property("test Fx[IO] Monad laws - Composition", IoSpec.testMonadLaws2_Composition),
    property("test Fx[IO] Monad laws - IdentityAp", IoSpec.testMonadLaws3_IdentityAp),
    property("test Fx[IO] Monad laws - Homomorphism", IoSpec.testMonadLaws4_Homomorphism),
    property("test Fx[IO] Monad laws - Interchange", IoSpec.testMonadLaws5_Interchange),
    property("test Fx[IO] Monad laws - CompositionAp", IoSpec.testMonadLaws6_CompositionAp),
    property("test Fx[IO] Monad laws - LeftIdentity", IoSpec.testMonadLaws7_LeftIdentity),
    property("test Fx[IO] Monad laws - RightIdentity", IoSpec.testMonadLaws8_RightIdentity),
    property("test Fx[IO] Monad laws - Associativity", IoSpec.testMonadLaws9_Associativity),
  ) ++
    List(
      example(
        "test CanCatch[IO]catchNonFatalThrowable should catch NonFatal",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalThrowableShouldCatchNonFatal,
      ),
      example(
        "test CanCatch[IO]catchNonFatalThrowable should not catch Fatal",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalThrowableShouldNotCatchFatal,
      ),
      example(
        "test CanCatch[IO]catchNonFatalThrowable should return the successful result",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalThrowableShouldReturnSuccessfulResult,
      ),
      example(
        "test CanCatch[IO]catchNonFatal should catch NonFatal",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalShouldCatchNonFatal,
      ),
      example(
        "test CanCatch[IO]catchNonFatal should not catch Fatal",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalShouldNotCatchFatal,
      ),
      example(
        "test CanCatch[IO]catchNonFatal should return the successful result",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test CanCatch[IO]catchNonFatalEither should catch NonFatal",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalEitherShouldCatchNonFatal,
      ),
      example(
        "test CanCatch[IO]catchNonFatalEither should not catch Fatal",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalEitherShouldNotCatchFatal,
      ),
      example(
        "test CanCatch[IO]catchNonFatalEither should return the successful result",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalEitherShouldReturnSuccessfulResult,
      ),
      example(
        "test CanCatch[IO]catchNonFatalEither should return the failed result",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalEitherShouldReturnFailedResult,
      ),
      example(
        "test CanCatch[IO]catchNonFatalEitherT should catch NonFatal",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalEitherTShouldCatchNonFatal,
      ),
      example(
        "test CanCatch[IO]catchNonFatalEitherT should not catch Fatal",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalEitherTShouldNotCatchFatal,
      ),
      example(
        "test CanCatch[IO]catchNonFatalEitherT should return the successful result",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalEitherTShouldReturnSuccessfulResult,
      ),
      example(
        "test CanCatch[IO]catchNonFatalEitherT should return the failed result",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalEitherTShouldReturnFailedResult,
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

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: Fx, A](a: => A): F[A] =
    Fx[F].effectOf(a)

  object IoSpec {

    def testMonadLaws1_Identity: Property = {
      import cats.syntax.eq._
      implicit val ticket: Ticker = Ticker.withNewTestContext()

      implicit val eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).completeAndEqualTo(true)

      MonadSpec.test1_Identity[IO]
    }

    def testMonadLaws2_Composition: Property = {
      import cats.syntax.eq._
      implicit val ticket: Ticker = Ticker.withNewTestContext()

      implicit val eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).completeAndEqualTo(true)

      MonadSpec.test2_Composition[IO]
    }

    def testMonadLaws3_IdentityAp: Property = {
      import cats.syntax.eq._
      implicit val ticket: Ticker = Ticker.withNewTestContext()

      implicit val eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).completeAndEqualTo(true)

      MonadSpec.test3_IdentityAp[IO]
    }

    def testMonadLaws4_Homomorphism: Property = {
      import cats.syntax.eq._
      implicit val ticket: Ticker = Ticker.withNewTestContext()

      implicit val eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).completeAndEqualTo(true)

      MonadSpec.test4_Homomorphism[IO]
    }

    def testMonadLaws5_Interchange: Property = {
      import cats.syntax.eq._
      implicit val ticket: Ticker = Ticker.withNewTestContext()

      implicit val eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).completeAndEqualTo(true)

      MonadSpec.test5_Interchange[IO]
    }

    def testMonadLaws6_CompositionAp: Property = {
      import cats.syntax.eq._
      implicit val ticket: Ticker = Ticker.withNewTestContext()

      implicit val eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).completeAndEqualTo(true)

      MonadSpec.test6_CompositionAp[IO]
    }

    def testMonadLaws7_LeftIdentity: Property = {
      import cats.syntax.eq._
      implicit val ticket: Ticker = Ticker.withNewTestContext()

      implicit val eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).completeAndEqualTo(true)

      MonadSpec.test7_LeftIdentity[IO]
    }

    def testMonadLaws8_RightIdentity: Property = {
      import cats.syntax.eq._
      implicit val ticket: Ticker = Ticker.withNewTestContext()

      implicit val eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).completeAndEqualTo(true)

      MonadSpec.test8_RightIdentity[IO]
    }

    def testMonadLaws9_Associativity: Property = {
      import cats.syntax.eq._
      implicit val ticket: Ticker = Ticker.withNewTestContext()

      implicit val eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).completeAndEqualTo(true)

//      implicit val ioFx: Fx[IO] = Fx.IoFx

      MonadSpec.test9_Associativity[IO]
    }

    object CanCatchSpec {

      def testCanCatch_IO_catchNonFatalThrowableShouldCatchNonFatal: Result = withIO { implicit ticker =>

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = expectedExpcetion.asLeft[Int]
        val actual            = Fx[IO].catchNonFatalThrowable(fa)

        actual.completeAs(expected)
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanCatch_IO_catchNonFatalThrowableShouldNotCatchFatal: Result = {

        val es: ExecutorService    = ConcurrentSupport.newExecutorService(2)
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

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

      def testCanCatch_IO_catchNonFatalThrowableShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

        val fa: IO[Int] = run[IO, Int](1)
        val expected    = 1.asRight[Throwable]
        val actual      = Fx[IO].catchNonFatalThrowable(fa)

        actual.completeAs(expected)
      }

      def testCanCatch_IO_catchNonFatalShouldCatchNonFatal: Result = withIO { implicit ticker =>

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual            = Fx[IO].catchNonFatal(fa)(SomeError.someThrowable)

        actual.completeAs(expected)
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanCatch_IO_catchNonFatalShouldNotCatchFatal: Result = {

        val es: ExecutorService    = ConcurrentSupport.newExecutorService(2)
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

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

      def testCanCatch_IO_catchNonFatalShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

        val fa: IO[Int] = run[IO, Int](1)
        val expected    = 1.asRight[SomeError]
        val actual      = Fx[IO].catchNonFatal(fa)(SomeError.someThrowable)

        actual.completeAs(expected)
      }

      def testCanCatch_IO_catchNonFatalEitherShouldCatchNonFatal: Result = withIO { implicit ticker =>

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa       = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual   = Fx[IO].catchNonFatalEither(fa)(SomeError.someThrowable)

        actual.completeAs(expected)
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanCatch_IO_catchNonFatalEitherShouldNotCatchFatal: Result = {

        val es: ExecutorService    = ConcurrentSupport.newExecutorService(2)
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

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

      def testCanCatch_IO_catchNonFatalEitherShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].catchNonFatalEither(fa)(SomeError.someThrowable)

        actual.completeAs(expected)
      }

      def testCanCatch_IO_catchNonFatalEitherShouldReturnFailedResult: Result = withIO { implicit ticker =>

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[IO].catchNonFatalEither(fa)(SomeError.someThrowable)

        actual.completeAs(expected)
      }

      def testCanCatch_IO_catchNonFatalEitherTShouldCatchNonFatal: Result = withIO { implicit ticker =>

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
        val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual   = Fx[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

        actual.completeAs(expected)
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanCatch_IO_catchNonFatalEitherTShouldNotCatchFatal: Result = {

        val es: ExecutorService    = ConcurrentSupport.newExecutorService(2)
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

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

      def testCanCatch_IO_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

        val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

        actual.completeAs(expected)
      }

      def testCanCatch_IO_catchNonFatalEitherTShouldReturnFailedResult: Result = withIO { implicit ticker =>

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

        actual.completeAs(expected)
      }

    }

    object CanHandleErrorSpec {

      def testCanHandleError_IO_handleNonFatalWithShouldHandleNonFatalWith: Result = withIO { implicit ticker =>

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

        actual.completeAs(expected)
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_IO_handleNonFatalWithShouldNotHandleFatalWith: Result = {

        val es: ExecutorService    = ConcurrentSupport.newExecutorService(2)
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

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

      def testCanHandleError_IO_handleNonFatalWithShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

        val fa       = run[IO, Int](1)
        val expected = 1
        val actual   = Fx[IO].handleNonFatalWith(fa)(_ => IO.pure(999))

        actual.completeAs(expected)
      }

      def testCanHandleError_IO_handleNonFatalWithEitherShouldHandleNonFatalWith: Result = withIO { implicit ticker =>

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
        val actualFailedResult   =
          Fx[IO].handleNonFatalWith(fa)(_ => IO.pure(expectedFailedResult))

        val expectedSuccessResult = 1.asRight[SomeError]
        val actualSuccessResult   =
          Fx[IO].handleNonFatalWith(fa)(_ => IO.pure(1.asRight[SomeError]))

        actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_IO_handleNonFatalWithEitherShouldNotHandleFatalWith: Result = {

        val es: ExecutorService    = ConcurrentSupport.newExecutorService(2)
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

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

      def testCanHandleError_IO_handleNonFatalWithEitherShouldReturnSuccessfulResult: Result =
        withIO { implicit ticker =>

          val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
          val expected = 1.asRight[SomeError]
          val actual   = Fx[IO].handleNonFatalWith(fa)(_ => IO(999.asRight[SomeError]))

          actual.completeAs(expected)
        }

      def testCanHandleError_IO_handleNonFatalWithEitherShouldReturnFailedResult: Result = withIO { implicit ticker =>

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[IO].handleNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError]))

        actual.completeAs(expected)
      }

      def testCanHandleError_IO_handleEitherNonFatalWithShouldHandleNonFatalWith: Result = withIO { implicit ticker =>

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   = Fx[IO]
          .handleEitherNonFatalWith(fa)(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))

        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[IO].handleEitherNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError]))

        actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_IO_handleEitherNonFatalWithShouldNotHandleFatalWith: Result = {

        val es: ExecutorService    = ConcurrentSupport.newExecutorService(2)
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

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

      def testCanHandleError_IO_handleEitherNonFatalWithShouldReturnSuccessfulResult: Result =
        withIO { implicit ticker =>

          val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
          val expected = 1.asRight[SomeError]
          val actual   =
            Fx[IO].handleEitherNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError]))

          actual.completeAs(expected)
        }

      def testCanHandleError_IO_handleEitherNonFatalWithShouldReturnFailedResult: Result = withIO { implicit ticker =>

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[IO].handleEitherNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError]))

        actual.completeAs(expected)
      }

      def testCanHandleError_IO_handleEitherTNonFatalWithShouldHandleNonFatalWith: Result = withIO { implicit ticker =>

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   = Fx[IO]
          .handleEitherTNonFatalWith(fa)(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
          .value

        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[IO].handleEitherTNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).value

        actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_IO_handleEitherTNonFatalWithShouldNotHandleFatalWith: Result = {

        val es: ExecutorService    = ConcurrentSupport.newExecutorService(2)
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

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

      def testCanHandleError_IO_handleEitherTNonFatalWithShouldReturnSuccessfulResult: Result =
        withIO { implicit ticker =>

          val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
          val expected = 1.asRight[SomeError]
          val actual   =
            Fx[IO].handleEitherTNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).value

          actual.completeAs(expected)
        }

      def testCanHandleError_IO_handleEitherTNonFatalWithShouldReturnFailedResult: Result = withIO { implicit ticker =>

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[IO].handleEitherTNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).value

        actual.completeAs(expected)
      }

      def testCanHandleError_IO_handleNonFatalShouldHandleNonFatal: Result = withIO { implicit ticker =>

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

        actual.completeAs(expected)
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_IO_handleNonFatalShouldNotHandleFatal: Result = {

        val es: ExecutorService    = ConcurrentSupport.newExecutorService(2)
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

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

      def testCanHandleError_IO_handleNonFatalShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

        val fa       = run[IO, Int](1)
        val expected = 1
        val actual   = Fx[IO].handleNonFatal(fa)(_ => 999)

        actual.completeAs(expected)
      }

      def testCanHandleError_IO_handleNonFatalEitherShouldHandleNonFatal: Result = withIO { implicit ticker =>

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
        val actualFailedResult   = Fx[IO].handleNonFatal(fa)(_ => expectedFailedResult)

        val expectedSuccessResult = 1.asRight[SomeError]
        val actualSuccessResult   = Fx[IO].handleNonFatal(fa)(_ => 1.asRight[SomeError])

        actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_IO_handleNonFatalEitherShouldNotHandleFatal: Result = {

        val es: ExecutorService    = ConcurrentSupport.newExecutorService(2)
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

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

      def testCanHandleError_IO_handleNonFatalEitherShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].handleNonFatal(fa)(_ => 999.asRight[SomeError])

        actual.completeAs(expected)
      }

      def testCanHandleError_IO_handleNonFatalEitherShouldReturnFailedResult: Result = withIO { implicit ticker =>

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[IO].handleNonFatal(fa)(_ => 123.asRight[SomeError])

        actual.completeAs(expected)
      }

      def testCanHandleError_IO_handleEitherNonFatalShouldHandleNonFatal: Result = withIO { implicit ticker =>

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   = Fx[IO]
          .handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])

        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[IO].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError])

        actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_IO_handleEitherNonFatalShouldNotHandleFatal: Result = {

        val es: ExecutorService    = ConcurrentSupport.newExecutorService(2)
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

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

      def testCanHandleError_IO_handleEitherNonFatalShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError])

        actual.completeAs(expected)
      }

      def testCanHandleError_IO_handleEitherNonFatalShouldReturnFailedResult: Result = withIO { implicit ticker =>

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[IO].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError])

        actual.completeAs(expected)
      }

      def testCanHandleError_IO_handleEitherTNonFatalShouldHandleNonFatal: Result = withIO { implicit ticker =>

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   = Fx[IO]
          .handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
          .value

        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[IO].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value

        actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_IO_handleEitherTNonFatalShouldNotHandleFatal: Result = {

        val es: ExecutorService    = ConcurrentSupport.newExecutorService(2)
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

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

      def testCanHandleError_IO_handleEitherTNonFatalShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

        val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value

        actual.completeAs(expected)
      }

      def testCanHandleError_IO_handleEitherTNonFatalShouldReturnFailedResult: Result = withIO { implicit ticker =>

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[IO].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value

        actual.completeAs(expected)
      }

    }

    object CanRecoverSpec {

      def testCanRecover_IO_recoverFromNonFatalWithShouldRecoverFromNonFatal: Result = withIO { implicit ticker =>

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 123
        val actual            = Fx[IO].recoverFromNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) =>
            IO.pure(expected)
        }
        actual.completeAs(expected)

      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_IO_recoverFromNonFatalWithShouldNotCatchFatal: Result = {

        val compat                 = new CatsEffectIoCompatForFuture
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

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

      def testCanRecover_IO_recoverFromNonFatalWithShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

        val expected = 1
        val fa       = run[IO, Int](expected)
        val actual   = Fx[IO]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(_) => IO.pure(999)
          }

        actual.completeAs(expected)
      }

      def testCanRecover_IO_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal: Result = withIO { implicit ticker =>

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
        val actualFailedResult   = Fx[IO]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) => IO.pure(expectedFailedResult)
          }

        val expectedSuccessResult = 1.asRight[SomeError]
        val actualSuccessResult   = Fx[IO]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) => IO.pure(1.asRight[SomeError])
          }

        actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_IO_recoverFromNonFatalWithEitherShouldNotCatchFatal: Result = {

        val compat                 = new CatsEffectIoCompatForFuture
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

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

      def testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult: Result =
        withIO { implicit ticker =>

          val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
          val expected = 1.asRight[SomeError]
          val actual   = Fx[IO]
            .recoverFromNonFatalWith(fa) {
              case NonFatal(_) => IO(999.asRight[SomeError])
            }

          actual.completeAs(expected)
        }

      def testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnFailedResult: Result = withIO { implicit ticker =>

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[IO]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(_) => IO.pure(123.asRight[SomeError])
          }

        actual.completeAs(expected)
      }

      def testCanRecover_IO_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal: Result = withIO { implicit ticker =>

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   = Fx[IO]
          .recoverEitherFromNonFatalWith(fa) {
            case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
          }

        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   = Fx[IO]
          .recoverEitherFromNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) => IO.pure(123.asRight[SomeError])
          }

        actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_IO_recoverEitherFromNonFatalWithShouldNotCatchFatal: Result = {

        val compat                 = new CatsEffectIoCompatForFuture
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

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

      def testCanRecover_IO_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult: Result =
        withIO { implicit ticker =>

          val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
          val expected = 1.asRight[SomeError]
          val actual   = Fx[IO]
            .recoverEitherFromNonFatalWith(fa) {
              case NonFatal(_) => IO.pure(123.asRight[SomeError])
            }

          actual.completeAs(expected)
        }

      def testCanRecover_IO_recoverEitherFromNonFatalWithShouldReturnFailedResult: Result = withIO { implicit ticker =>

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[IO]
            .recoverEitherFromNonFatalWith(fa) {
              case NonFatal(_) => IO.pure(123.asRight[SomeError])
            }

        actual.completeAs(expected)
      }

      def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal: Result =
        withIO { implicit ticker =>

          val expectedExpcetion = new RuntimeException("Something's wrong")
          val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
          val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
          val actualFailedResult   = Fx[IO]
            .recoverEitherTFromNonFatalWith(fa) {
              case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
            }
            .value

          val expectedSuccessResult = 123.asRight[SomeError]
          val actualSuccessResult   = Fx[IO]
            .recoverEitherTFromNonFatalWith(fa) {
              case NonFatal(`expectedExpcetion`) => IO.pure(123.asRight[SomeError])
            }
            .value

          actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
        }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldNotCatchFatal: Result = {

        val compat                 = new CatsEffectIoCompatForFuture
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

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

      def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult: Result =
        withIO { implicit ticker =>

          val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
          val expected = 1.asRight[SomeError]
          val actual   = Fx[IO]
            .recoverEitherTFromNonFatalWith(fa) {
              case NonFatal(_) => IO.pure(123.asRight[SomeError])
            }
            .value

          actual.completeAs(expected)
        }

      def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnFailedResult: Result = withIO { implicit ticker =>

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[IO]
            .recoverEitherTFromNonFatalWith(fa) {
              case NonFatal(_) => IO.pure(123.asRight[SomeError])
            }
            .value

        actual.completeAs(expected)
      }

      // /

      def testCanRecover_IO_recoverFromNonFatalShouldRecoverFromNonFatal: Result = withIO { implicit ticker =>

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 123
        val actual            = Fx[IO]
          .recoverFromNonFatal(fa) {
            case NonFatal(`expectedExpcetion`) =>
              expected
          }

        actual.completeAs(expected)
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_IO_recoverFromNonFatalShouldNotCatchFatal: Result = {

        val compat                 = new CatsEffectIoCompatForFuture
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

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

      def testCanRecover_IO_recoverFromNonFatalShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

        val fa       = run[IO, Int](1)
        val expected = 1
        val actual   = Fx[IO].recoverFromNonFatal(fa) { case NonFatal(_) => 999 }

        actual.completeAs(expected)
      }

      def testCanRecover_IO_recoverFromNonFatalEitherShouldRecoverFromNonFatal: Result = withIO { implicit ticker =>

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
        val actualFailedResult   = Fx[IO]
          .recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expectedFailedResult }

        val expectedSuccessResult = 1.asRight[SomeError]
        val actualSuccessResult   = Fx[IO]
          .recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }

        actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_IO_recoverFromNonFatalEitherShouldNotCatchFatal: Result = {

        val compat                 = new CatsEffectIoCompatForFuture
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

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

      def testCanRecover_IO_recoverFromNonFatalEitherShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].recoverFromNonFatal(fa) { case NonFatal(_) => 999.asRight[SomeError] }

        actual.completeAs(expected)
      }

      def testCanRecover_IO_recoverFromNonFatalEitherShouldReturnFailedResult: Result = withIO { implicit ticker =>

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[IO].recoverFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }

        actual.completeAs(expected)
      }

      def testCanRecover_IO_recoverEitherFromNonFatalShouldRecoverFromNonFatal: Result = withIO { implicit ticker =>

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   =
          Fx[IO]
            .recoverEitherFromNonFatal(fa) {
              case err => SomeError.someThrowable(err).asLeft[Int]
            }

        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[IO]
            .recoverEitherFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }

        actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_IO_recoverEitherFromNonFatalShouldNotCatchFatal: Result = {

        val compat                 = new CatsEffectIoCompatForFuture
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

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

      def testCanRecover_IO_recoverEitherFromNonFatalShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[IO]
            .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }

        actual.completeAs(expected)
      }

      def testCanRecover_IO_recoverEitherFromNonFatalShouldReturnFailedResult: Result = withIO { implicit ticker =>

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[IO]
            .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }

        actual.completeAs(expected)
      }

      def testCanRecover_IO_recoverEitherTFromNonFatalShouldRecoverFromNonFatal: Result = withIO { implicit ticker =>

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   =
          Fx[IO]
            .recoverEitherTFromNonFatal(fa) {
              case err => SomeError.someThrowable(err).asLeft[Int]
            }
            .value

        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[IO]
            .recoverEitherTFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }
            .value

        actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_IO_recoverEitherTFromNonFatalShouldNotCatchFatal: Result = {

        val compat                 = new CatsEffectIoCompatForFuture
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

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

      def testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

        val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[IO]
            .recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
            .value

        actual.completeAs(expected)
      }

      def testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnFailedResult: Result = withIO { implicit ticker =>

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[IO]
            .recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
            .value

        actual.completeAs(expected)
      }

    }

    object OnNonFatalSpec {

      def testOnNonFatal_IO_onNonFatalWithShouldRecoverFromNonFatal: Result = {

        val compat                 = new CatsEffectIoCompatForFuture
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

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

        val compat                 = new CatsEffectIoCompatForFuture
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

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

        val compat                 = new CatsEffectIoCompatForFuture
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

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
