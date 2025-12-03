package effectie.instances.monix3

import cats.Eq
import cats.data.EitherT
import cats.syntax.all._
import effectie.SomeControlThrowable
import effectie.core._
import effectie.instances.monix3.fx._
import effectie.specs.MonadSpec
import effectie.specs.fxSpec.FxSpecs
import effectie.syntax.error._
import effectie.testing.tools
import effectie.testing.types.SomeError
import hedgehog._
import hedgehog.runner._
import monix.eval.Task

import scala.util.control.{ControlThrowable, NonFatal}

/** @author Kevin Lee
  * @since 2020-12-06
  */
object fxSpec extends Properties {
  import monix.execution.Scheduler.Implicits.global

  override def tests: List[Test] = taskSpecs

  private val assertWithAttempt: (Task[Int], Either[Throwable, Int]) => Result = { (io, expected) =>
    val actual = io.attempt.runSyncUnsafe()
    (actual ==== expected).log(s"$actual does not equal to $expected")
  }

  private val unit: Unit = ()

  /* Task */
  private val taskSpecs = List(
    property("test Fx[Task].effectOf", FxSpecs.testEffectOf[Task](_.runSyncUnsafe() ==== unit)),
    property("test Fx[Task].fromEffect(effectOf)", FxSpecs.testFromEffect[Task](_.runSyncUnsafe() ==== unit)),
    property("test Fx[Task].fromEffect(pureOf)", FxSpecs.testFromEffectWithPure[Task](_.runSyncUnsafe() ==== unit)),
    property("test Fx[Task].pureOf", FxSpecs.testPureOf[Task](_.runSyncUnsafe() ==== unit)),
    property(
      "test Fx[Task].pureOrError(success case)",
      FxSpecs.testPureOrErrorSuccessCase[Task](_.runSyncUnsafe() ==== unit),
    ),
    example(
      "test Fx[Task].pureOrError(error case)",
      FxSpecs.testPureOrErrorErrorCase[Task] { (io, expected) =>
        tools.expectThrowable(io.runSyncUnsafe(), expected)
      },
    ),
    example("test Fx[Task].unitOf", FxSpecs.testUnitOf[Task](_.runSyncUnsafe() ==== unit)),
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
    TaskSpec.testMonadLaws ++
    List(
      example(
        "test Fx[Task]catchNonFatalThrowable should catch NonFatal",
        TaskSpec.CanCatchSpec.testFx_Task_catchNonFatalThrowableShouldCatchNonFatal,
      ),
      example(
        "test Fx[Task]catchNonFatalThrowable should not catch Fatal",
        TaskSpec.CanCatchSpec.testFx_Task_catchNonFatalThrowableShouldNotCatchFatal,
      ),
      example(
        "test Fx[Task]catchNonFatalThrowable should return the successful result",
        TaskSpec.CanCatchSpec.testFx_Task_catchNonFatalThrowableShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task]catchNonFatal should catch NonFatal",
        TaskSpec.CanCatchSpec.testFx_Task_catchNonFatalShouldCatchNonFatal,
      ),
      example(
        "test Fx[Task]catchNonFatal should not catch Fatal",
        TaskSpec.CanCatchSpec.testFx_Task_catchNonFatalShouldNotCatchFatal,
      ),
      example(
        "test Fx[Task]catchNonFatal should return the successful result",
        TaskSpec.CanCatchSpec.testFx_Task_catchNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task]catchNonFatalEither should catch NonFatal",
        TaskSpec.CanCatchSpec.testFx_Task_catchNonFatalEitherShouldCatchNonFatal,
      ),
      example(
        "test Fx[Task]catchNonFatalEither should not catch Fatal",
        TaskSpec.CanCatchSpec.testFx_Task_catchNonFatalEitherShouldNotCatchFatal,
      ),
      example(
        "test Fx[Task]catchNonFatalEither should return the successful result",
        TaskSpec.CanCatchSpec.testFx_Task_catchNonFatalEitherShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task]catchNonFatalEither should return the failed result",
        TaskSpec.CanCatchSpec.testFx_Task_catchNonFatalEitherShouldReturnFailedResult,
      ),
      example(
        "test Fx[Task]catchNonFatalEitherT should catch NonFatal",
        TaskSpec.CanCatchSpec.testFx_Task_catchNonFatalEitherTShouldCatchNonFatal,
      ),
      example(
        "test Fx[Task]catchNonFatalEitherT should not catch Fatal",
        TaskSpec.CanCatchSpec.testFx_Task_catchNonFatalEitherTShouldNotCatchFatal,
      ),
      example(
        "test Fx[Task]catchNonFatalEitherT should return the successful result",
        TaskSpec.CanCatchSpec.testFx_Task_catchNonFatalEitherTShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task]catchNonFatalEitherT should return the failed result",
        TaskSpec.CanCatchSpec.testFx_Task_catchNonFatalEitherTShouldReturnFailedResult,
      ),
    ) ++
    List(
      /* Task */
      example(
        "test Fx[Task].handleNonFatalWith should handle NonFatal",
        TaskSpec.CanHandleErrorSpec.testCanHandleError_Task_handleNonFatalWithShouldHandleNonFatalWith,
      ),
      example(
        "test Fx[Task].handleNonFatalWith should not handle Fatal",
        TaskSpec.CanHandleErrorSpec.testCanHandleError_Task_handleNonFatalWithShouldNotHandleFatalWith,
      ),
      example(
        "test Fx[Task].handleNonFatalWith should return the successful result",
        TaskSpec.CanHandleErrorSpec.testCanHandleError_Task_handleNonFatalWithShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].handleNonFatalWithEither should handle NonFatal",
        TaskSpec.CanHandleErrorSpec.testCanHandleError_Task_handleNonFatalWithEitherShouldHandleNonFatalWith,
      ),
      example(
        "test Fx[Task].handleNonFatalWithEither should not handle Fatal",
        TaskSpec.CanHandleErrorSpec.testCanHandleError_Task_handleNonFatalWithEitherShouldNotHandleFatalWith,
      ),
      example(
        "test Fx[Task].handleNonFatalWithEither should return the successful result",
        TaskSpec.CanHandleErrorSpec.testCanHandleError_Task_handleNonFatalWithEitherShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].handleNonFatalWithEither should return the failed result",
        TaskSpec.CanHandleErrorSpec.testCanHandleError_Task_handleNonFatalWithEitherShouldReturnFailedResult,
      ),
      example(
        "test Fx[Task].handleEitherNonFatalWith should handle NonFatal",
        TaskSpec.CanHandleErrorSpec.testCanHandleError_Task_handleEitherNonFatalWithShouldHandleNonFatalWith,
      ),
      example(
        "test Fx[Task].handleEitherNonFatalWith should not handle Fatal",
        TaskSpec.CanHandleErrorSpec.testCanHandleError_Task_handleEitherNonFatalWithShouldNotHandleFatalWith,
      ),
      example(
        "test Fx[Task].handleEitherNonFatalWith should return the successful result",
        TaskSpec.CanHandleErrorSpec.testCanHandleError_Task_handleEitherNonFatalWithShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].handleEitherNonFatalWith should return the failed result",
        TaskSpec.CanHandleErrorSpec.testCanHandleError_Task_handleEitherNonFatalWithShouldReturnFailedResult,
      ),
      example(
        "test Fx[Task].handleEitherTNonFatalWith should handle NonFatal",
        TaskSpec.CanHandleErrorSpec.testCanHandleError_Task_handleEitherTNonFatalWithShouldHandleNonFatalWith,
      ),
      example(
        "test Fx[Task].handleEitherTNonFatalWith should not handle Fatal",
        TaskSpec.CanHandleErrorSpec.testCanHandleError_Task_handleEitherTNonFatalWithShouldNotHandleFatalWith,
      ),
      example(
        "test Fx[Task].handleEitherTNonFatalWith should return the successful result",
        TaskSpec.CanHandleErrorSpec.testCanHandleError_Task_handleEitherTNonFatalWithShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].handleEitherTNonFatalWith should return the failed result",
        TaskSpec.CanHandleErrorSpec.testCanHandleError_Task_handleEitherTNonFatalWithShouldReturnFailedResult,
      ),
      example(
        "test Fx[Task].handleNonFatal should handle NonFatal",
        TaskSpec.CanHandleErrorSpec.testCanHandleError_Task_handleNonFatalShouldHandleNonFatal,
      ),
      example(
        "test Fx[Task].handleNonFatal should not handle Fatal",
        TaskSpec.CanHandleErrorSpec.testCanHandleError_Task_handleNonFatalShouldNotHandleFatal,
      ),
      example(
        "test Fx[Task].handleNonFatal should return the successful result",
        TaskSpec.CanHandleErrorSpec.testCanHandleError_Task_handleNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].handleNonFatalEither should handle NonFatal",
        TaskSpec.CanHandleErrorSpec.testCanHandleError_Task_handleNonFatalEitherShouldHandleNonFatal,
      ),
      example(
        "test Fx[Task].handleNonFatalEither should not handle Fatal",
        TaskSpec.CanHandleErrorSpec.testCanHandleError_Task_handleNonFatalEitherShouldNotHandleFatal,
      ),
      example(
        "test Fx[Task].handleNonFatalEither should return the successful result",
        TaskSpec.CanHandleErrorSpec.testCanHandleError_Task_handleNonFatalEitherShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].handleNonFatalEither should return the failed result",
        TaskSpec.CanHandleErrorSpec.testCanHandleError_Task_handleNonFatalEitherShouldReturnFailedResult,
      ),
      example(
        "test Fx[Task].handleEitherNonFatal should handle NonFatal",
        TaskSpec.CanHandleErrorSpec.testCanHandleError_Task_handleEitherNonFatalShouldHandleNonFatal,
      ),
      example(
        "test Fx[Task].handleEitherNonFatal should not handle Fatal",
        TaskSpec.CanHandleErrorSpec.testCanHandleError_Task_handleEitherNonFatalShouldNotHandleFatal,
      ),
      example(
        "test Fx[Task].handleEitherNonFatal should return the successful result",
        TaskSpec.CanHandleErrorSpec.testCanHandleError_Task_handleEitherNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].handleEitherNonFatal should return the failed result",
        TaskSpec.CanHandleErrorSpec.testCanHandleError_Task_handleEitherNonFatalShouldReturnFailedResult,
      ),
      example(
        "test Fx[Task].handleEitherTNonFatal should handle NonFatal",
        TaskSpec.CanHandleErrorSpec.testCanHandleError_Task_handleEitherTNonFatalShouldHandleNonFatal,
      ),
      example(
        "test Fx[Task].handleEitherTNonFatal should not handle Fatal",
        TaskSpec.CanHandleErrorSpec.testCanHandleError_Task_handleEitherTNonFatalShouldNotHandleFatal,
      ),
      example(
        "test Fx[Task].handleEitherTNonFatal should return the successful result",
        TaskSpec.CanHandleErrorSpec.testCanHandleError_Task_handleEitherTNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].handleEitherTNonFatal should return the failed result",
        TaskSpec.CanHandleErrorSpec.testCanHandleError_Task_handleEitherTNonFatalShouldReturnFailedResult,
      ),
    ) ++ List(
      example(
        "test Fx[Task].recoverFromNonFatalWith should catch NonFatal",
        TaskSpec.CanRecoverSpec.testCanRecover_Task_recoverFromNonFatalWithShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Task].recoverFromNonFatalWith should not catch Fatal",
        TaskSpec.CanRecoverSpec.testCanRecover_Task_recoverFromNonFatalWithShouldNotCatchFatal,
      ),
      example(
        "test Fx[Task].recoverFromNonFatalWith should return the successful result",
        TaskSpec.CanRecoverSpec.testCanRecover_Task_recoverFromNonFatalWithShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].recoverFromNonFatalWithEither should catch NonFatal",
        TaskSpec.CanRecoverSpec.testCanRecover_Task_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Task].recoverFromNonFatalWithEither should not catch Fatal",
        TaskSpec.CanRecoverSpec.testCanRecover_Task_recoverFromNonFatalWithEitherShouldNotCatchFatal,
      ),
      example(
        "test Fx[Task].recoverFromNonFatalWithEither should return the successful result",
        TaskSpec.CanRecoverSpec.testCanRecover_Task_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].recoverFromNonFatalWithEither should return the failed result",
        TaskSpec.CanRecoverSpec.testCanRecover_Task_recoverFromNonFatalWithEitherShouldReturnFailedResult,
      ),
      example(
        "test Fx[Task].recoverEitherFromNonFatalWith should catch NonFatal",
        TaskSpec.CanRecoverSpec.testCanRecover_Task_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Task].recoverEitherFromNonFatalWith should not catch Fatal",
        TaskSpec.CanRecoverSpec.testCanRecover_Task_recoverEitherFromNonFatalWithShouldNotCatchFatal,
      ),
      example(
        "test Fx[Task].recoverEitherFromNonFatalWith should return the successful result",
        TaskSpec.CanRecoverSpec.testCanRecover_Task_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].recoverEitherFromNonFatalWith should return the failed result",
        TaskSpec.CanRecoverSpec.testCanRecover_Task_recoverEitherFromNonFatalWithShouldReturnFailedResult,
      ),
      example(
        "test Fx[Task].recoverEitherTFromNonFatalWith should catch NonFatal",
        TaskSpec.CanRecoverSpec.testCanRecover_Task_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Task].recoverEitherTFromNonFatalWith should not catch Fatal",
        TaskSpec.CanRecoverSpec.testCanRecover_Task_recoverEitherTFromNonFatalWithShouldNotCatchFatal,
      ),
      example(
        "test Fx[Task].recoverEitherTFromNonFatalWith should return the successful result",
        TaskSpec.CanRecoverSpec.testCanRecover_Task_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].recoverEitherTFromNonFatalWith should return the failed result",
        TaskSpec.CanRecoverSpec.testCanRecover_Task_recoverEitherTFromNonFatalWithShouldReturnFailedResult,
      ),
      example(
        "test Fx[Task].recoverFromNonFatal should catch NonFatal",
        TaskSpec.CanRecoverSpec.testCanRecover_Task_recoverFromNonFatalShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Task].recoverFromNonFatal should not catch Fatal",
        TaskSpec.CanRecoverSpec.testCanRecover_Task_recoverFromNonFatalShouldNotCatchFatal,
      ),
      example(
        "test Fx[Task].recoverFromNonFatal should return the successful result",
        TaskSpec.CanRecoverSpec.testCanRecover_Task_recoverFromNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].recoverFromNonFatalEither should catch NonFatal",
        TaskSpec.CanRecoverSpec.testCanRecover_Task_recoverFromNonFatalEitherShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Task].recoverFromNonFatalEither should not catch Fatal",
        TaskSpec.CanRecoverSpec.testCanRecover_Task_recoverFromNonFatalEitherShouldNotCatchFatal,
      ),
      example(
        "test Fx[Task].recoverFromNonFatalEither should return the successful result",
        TaskSpec.CanRecoverSpec.testCanRecover_Task_recoverFromNonFatalEitherShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].recoverFromNonFatalEither should return the failed result",
        TaskSpec.CanRecoverSpec.testCanRecover_Task_recoverFromNonFatalEitherShouldReturnFailedResult,
      ),
      example(
        "test Fx[Task].recoverEitherFromNonFatal should catch NonFatal",
        TaskSpec.CanRecoverSpec.testCanRecover_Task_recoverEitherFromNonFatalShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Task].recoverEitherFromNonFatal should not catch Fatal",
        TaskSpec.CanRecoverSpec.testCanRecover_Task_recoverEitherFromNonFatalShouldNotCatchFatal,
      ),
      example(
        "test Fx[Task].recoverEitherFromNonFatal should return the successful result",
        TaskSpec.CanRecoverSpec.testCanRecover_Task_recoverEitherFromNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].recoverEitherFromNonFatal should return the failed result",
        TaskSpec.CanRecoverSpec.testCanRecover_Task_recoverEitherFromNonFatalShouldReturnFailedResult,
      ),
      example(
        "test Fx[Task].recoverEitherTFromNonFatal should catch NonFatal",
        TaskSpec.CanRecoverSpec.testCanRecover_Task_recoverEitherTFromNonFatalShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Task].recoverEitherTFromNonFatal should not catch Fatal",
        TaskSpec.CanRecoverSpec.testCanRecover_Task_recoverEitherTFromNonFatalShouldNotCatchFatal,
      ),
      example(
        "test Fx[Task].recoverEitherTFromNonFatal should return the successful result",
        TaskSpec.CanRecoverSpec.testCanRecover_Task_recoverEitherTFromNonFatalShouldReturnSuccessfulResult,
      ),
      example(
        "test Fx[Task].recoverEitherTFromNonFatal should return the failed result",
        TaskSpec.CanRecoverSpec.testCanRecover_Task_recoverEitherTFromNonFatalShouldReturnFailedResult,
      ),
    ) ++
    List(
      example(
        "test Fx[Task].onNonFatalWith should do something for NonFatal",
        TaskSpec.OnNonFatalSpec.testOnNonFatal_Task_onNonFatalWithShouldRecoverFromNonFatal,
      ),
      example(
        "test Fx[Task].onNonFatalWith should do nothing for Fatal",
        TaskSpec.OnNonFatalSpec.testOnNonFatal_Task_onNonFatalWithShouldNotCatchFatal,
      ),
      example(
        "test Fx[Task].onNonFatalWith should do nothing for the successful result",
        TaskSpec.OnNonFatalSpec.testOnNonFatal_Task_onNonFatalWithShouldReturnSuccessfulResult,
      ),
    )

  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: Fx, A](a: => A): F[A] =
    Fx[F].effectOf(a)

  object TaskSpec {
    import monix.execution.Scheduler.Implicits.global

    def testMonadLaws: List[Test] = {
      import cats.syntax.eq._

      implicit val eqIo: Eq[Task[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).runSyncUnsafe()

      MonadSpec.testMonadLaws[Task]("Task")
    }

    object CanCatchSpec {

      def testFx_Task_catchNonFatalThrowableShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = expectedExpcetion.asLeft[Int]
        val actual            = Fx[Task].catchNonFatalThrowable(fa).runSyncUnsafe()

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testFx_Task_catchNonFatalThrowableShouldNotCatchFatal: Result = {

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

      def testFx_Task_catchNonFatalThrowableShouldReturnSuccessfulResult: Result = {

        val fa       = run[Task, Int](1)
        val expected = 1.asRight[Throwable]
        val actual   = Fx[Task].catchNonFatalThrowable(fa).runSyncUnsafe()

        actual ==== expected
      }

      def testFx_Task_catchNonFatalShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual            = Fx[Task].catchNonFatal(fa)(SomeError.someThrowable).runSyncUnsafe()

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testFx_Task_catchNonFatalShouldNotCatchFatal: Result = {

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

      def testFx_Task_catchNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = run[Task, Int](1)
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Task].catchNonFatal(fa)(SomeError.someThrowable).runSyncUnsafe()

        actual ==== expected
      }

      def testFx_Task_catchNonFatalEitherShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa       = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual   = Fx[Task].catchNonFatalEither(fa)(SomeError.someThrowable).runSyncUnsafe()

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testFx_Task_catchNonFatalEitherShouldNotCatchFatal: Result = {

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

      def testFx_Task_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Task].catchNonFatalEither(fa)(SomeError.someThrowable).runSyncUnsafe()

        actual ==== expected
      }

      def testFx_Task_catchNonFatalEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Task].catchNonFatalEither(fa)(SomeError.someThrowable).runSyncUnsafe()

        actual ==== expected
      }

      def testFx_Task_catchNonFatalEitherTShouldCatchNonFatal: Result = {

        val expectedExpcetion                 = new RuntimeException("Something's wrong")
        val fa: EitherT[Task, SomeError, Int] = EitherT(
          run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        )
        val expected                          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual = Fx[Task].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.runSyncUnsafe()

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testFx_Task_catchNonFatalEitherTShouldNotCatchFatal: Result = {

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

      def testFx_Task_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Task].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.runSyncUnsafe()

        actual ==== expected
      }

      def testFx_Task_catchNonFatalEitherTShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Task].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.runSyncUnsafe()

        actual ==== expected
      }

    }

    object CanHandleErrorSpec {

      def testCanHandleError_Task_handleNonFatalWithShouldHandleNonFatalWith: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 123
        val actual            = Fx[Task]
          .handleNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) =>
              Task.pure(expected)
            case err =>
              throw err // scalafix:ok DisableSyntax.throw
          }
          .runSyncUnsafe()

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_Task_handleNonFatalWithShouldNotHandleFatalWith: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[Task, Int](throwThrowable[Int](fatalExpcetion))

        try {
          val actual = Fx[Task].handleNonFatalWith(fa)(_ => Task.pure(123)).runSyncUnsafe()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_Task_handleNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = run[Task, Int](1)
        val expected = 1
        val actual   = Fx[Task].handleNonFatalWith(fa)(_ => Task.pure(999)).runSyncUnsafe()

        actual ==== expected
      }

      def testCanHandleError_Task_handleNonFatalWithEitherShouldHandleNonFatalWith: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
        val actualFailedResult   =
          Fx[Task].handleNonFatalWith(fa)(_ => Task.pure(expectedFailedResult)).runSyncUnsafe()

        val expectedSuccessResult = 1.asRight[SomeError]
        val actualSuccessResult   =
          Fx[Task].handleNonFatalWith(fa)(_ => Task.pure(1.asRight[SomeError])).runSyncUnsafe()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_Task_handleNonFatalWithEitherShouldNotHandleFatalWith: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

        try {
          val actual = Fx[Task].handleNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError])).runSyncUnsafe()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: SomeControlThrowable =>
            ex.getMessage ==== fatalExpcetion.getMessage

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_Task_handleNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Task].handleNonFatalWith(fa)(_ => Task(999.asRight[SomeError])).runSyncUnsafe()

        actual ==== expected
      }

      def testCanHandleError_Task_handleNonFatalWithEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Task].handleNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError])).runSyncUnsafe()

        actual ==== expected
      }

      def testCanHandleError_Task_handleEitherNonFatalWithShouldHandleNonFatalWith: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult    = Fx[Task]
          .handleEitherNonFatalWith(fa)(err => Task.pure(SomeError.someThrowable(err).asLeft[Int]))
          .runSyncUnsafe()
        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[Task].handleEitherNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError])).runSyncUnsafe()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_Task_handleEitherNonFatalWithShouldNotHandleFatalWith: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

        try {
          val actual =
            Fx[Task]
              .handleEitherNonFatalWith(fa)(err => Task.pure(SomeError.someThrowable(err).asLeft[Int]))
              .runSyncUnsafe()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_Task_handleEitherNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[Task].handleEitherNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError])).runSyncUnsafe()

        actual ==== expected
      }

      def testCanHandleError_Task_handleEitherNonFatalWithShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[Task].handleEitherNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError])).runSyncUnsafe()

        actual ==== expected
      }

      def testCanHandleError_Task_handleEitherTNonFatalWithShouldHandleNonFatalWith: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
        val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult    = Fx[Task]
          .handleEitherTNonFatalWith(fa)(err => Task.pure(SomeError.someThrowable(err).asLeft[Int]))
          .value
          .runSyncUnsafe()
        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[Task].handleEitherTNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError])).value.runSyncUnsafe()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_Task_handleEitherTNonFatalWithShouldNotHandleFatalWith: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

        try {
          val actual =
            Fx[Task]
              .handleEitherTNonFatalWith(fa)(err => Task.pure(SomeError.someThrowable(err).asLeft[Int]))
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

      def testCanHandleError_Task_handleEitherTNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[Task].handleEitherTNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError])).value.runSyncUnsafe()

        actual ==== expected
      }

      def testCanHandleError_Task_handleEitherTNonFatalWithShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[Task].handleEitherTNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError])).value.runSyncUnsafe()

        actual ==== expected
      }

      def testCanHandleError_Task_handleNonFatalShouldHandleNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 123
        val actual            = Fx[Task]
          .handleNonFatal(fa) {
            case NonFatal(`expectedExpcetion`) =>
              expected
            case err =>
              throw err // scalafix:ok DisableSyntax.throw
          }
          .runSyncUnsafe()

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_Task_handleNonFatalShouldNotHandleFatal: Result = {

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

      def testCanHandleError_Task_handleNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = run[Task, Int](1)
        val expected = 1
        val actual   = Fx[Task].handleNonFatal(fa)(_ => 999).runSyncUnsafe()

        actual ==== expected
      }

      def testCanHandleError_Task_handleNonFatalEitherShouldHandleNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
        val actualFailedResult   = Fx[Task].handleNonFatal(fa)(_ => expectedFailedResult).runSyncUnsafe()

        val expectedSuccessResult = 1.asRight[SomeError]
        val actualSuccessResult   = Fx[Task].handleNonFatal(fa)(_ => 1.asRight[SomeError]).runSyncUnsafe()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_Task_handleNonFatalEitherShouldNotHandleFatal: Result = {

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

      def testCanHandleError_Task_handleNonFatalEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Task].handleNonFatal(fa)(_ => 999.asRight[SomeError]).runSyncUnsafe()

        actual ==== expected
      }

      def testCanHandleError_Task_handleNonFatalEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Task].handleNonFatal(fa)(_ => 123.asRight[SomeError]).runSyncUnsafe()

        actual ==== expected
      }

      def testCanHandleError_Task_handleEitherNonFatalShouldHandleNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult    = Fx[Task]
          .handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
          .runSyncUnsafe()
        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[Task].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError]).runSyncUnsafe()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_Task_handleEitherNonFatalShouldNotHandleFatal: Result = {

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

      def testCanHandleError_Task_handleEitherNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Task].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError]).runSyncUnsafe()

        actual ==== expected
      }

      def testCanHandleError_Task_handleEitherNonFatalShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[Task].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError]).runSyncUnsafe()

        actual ==== expected
      }

      def testCanHandleError_Task_handleEitherTNonFatalShouldHandleNonFatal: Result = {

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

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanHandleError_Task_handleEitherTNonFatalShouldNotHandleFatal: Result = {

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

      def testCanHandleError_Task_handleEitherTNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Task].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value.runSyncUnsafe()

        actual ==== expected
      }

      def testCanHandleError_Task_handleEitherTNonFatalShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[Task].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value.runSyncUnsafe()

        actual ==== expected
      }

    }

    object CanRecoverSpec {

      def testCanRecover_Task_recoverFromNonFatalWithShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 123
        val actual            = Fx[Task]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) =>
              Task.pure(expected)
          }
          .runSyncUnsafe()

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_Task_recoverFromNonFatalWithShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))

        val io = Fx[Task].recoverFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => Task.pure(123) }
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

      def testCanRecover_Task_recoverFromNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = run[Task, Int](1)
        val expected = 1
        val actual   = Fx[Task]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(_) => Task.pure(999)
          }
          .runSyncUnsafe()

        actual ==== expected
      }

      def testCanRecover_Task_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
        val actualFailedResult   = Fx[Task]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) => Task.pure(expectedFailedResult)
          }
          .runSyncUnsafe()

        val expectedSuccessResult = 1.asRight[SomeError]
        val actualSuccessResult   = Fx[Task]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) => Task.pure(1.asRight[SomeError])
          }
          .runSyncUnsafe()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_Task_recoverFromNonFatalWithEitherShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

        val io = Fx[Task].recoverFromNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) => Task.pure(123.asRight[SomeError])
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

      def testCanRecover_Task_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Task]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(_) => Task(999.asRight[SomeError])
          }
          .runSyncUnsafe()

        actual ==== expected
      }

      def testCanRecover_Task_recoverFromNonFatalWithEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Task]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(_) => Task.pure(123.asRight[SomeError])
          }
          .runSyncUnsafe()

        actual ==== expected
      }

      def testCanRecover_Task_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult    = Fx[Task]
          .recoverEitherFromNonFatalWith(fa) {
            case err => Task.pure(SomeError.someThrowable(err).asLeft[Int])
          }
          .runSyncUnsafe()
        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   = Fx[Task]
          .recoverEitherFromNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) => Task.pure(123.asRight[SomeError])
          }
          .runSyncUnsafe()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_Task_recoverEitherFromNonFatalWithShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

        val io = Fx[Task].recoverEitherFromNonFatalWith(fa) {
          case err => Task.pure(SomeError.someThrowable(err).asLeft[Int])
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

      def testCanRecover_Task_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Task]
          .recoverEitherFromNonFatalWith(fa) {
            case NonFatal(_) => Task.pure(123.asRight[SomeError])
          }
          .runSyncUnsafe()

        actual ==== expected
      }

      def testCanRecover_Task_recoverEitherFromNonFatalWithShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[Task]
            .recoverEitherFromNonFatalWith(fa) {
              case NonFatal(_) => Task.pure(123.asRight[SomeError])
            }
            .runSyncUnsafe()

        actual ==== expected
      }

      def testCanRecover_Task_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
        val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult    = Fx[Task]
          .recoverEitherTFromNonFatalWith(fa) {
            case err => Task.pure(SomeError.someThrowable(err).asLeft[Int])
          }
          .value
          .runSyncUnsafe()
        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   = Fx[Task]
          .recoverEitherTFromNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) => Task.pure(123.asRight[SomeError])
          }
          .value
          .runSyncUnsafe()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_Task_recoverEitherTFromNonFatalWithShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))

        val io = Fx[Task].recoverEitherTFromNonFatalWith(fa) {
          case err => Task.pure(SomeError.someThrowable(err).asLeft[Int])
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

      def testCanRecover_Task_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Task]
          .recoverEitherTFromNonFatalWith(fa) {
            case NonFatal(_) => Task.pure(123.asRight[SomeError])
          }
          .value
          .runSyncUnsafe()

        actual ==== expected
      }

      def testCanRecover_Task_recoverEitherTFromNonFatalWithShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[Task]
            .recoverEitherTFromNonFatalWith(fa) {
              case NonFatal(_) => Task.pure(123.asRight[SomeError])
            }
            .value
            .runSyncUnsafe()

        actual ==== expected
      }

      // /

      def testCanRecover_Task_recoverFromNonFatalShouldRecoverFromNonFatal: Result = {

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

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_Task_recoverFromNonFatalShouldNotCatchFatal: Result = {

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

      def testCanRecover_Task_recoverFromNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = run[Task, Int](1)
        val expected = 1
        val actual   = Fx[Task].recoverFromNonFatal(fa) { case NonFatal(_) => 999 }.runSyncUnsafe()

        actual ==== expected
      }

      def testCanRecover_Task_recoverFromNonFatalEitherShouldRecoverFromNonFatal: Result = {

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

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_Task_recoverFromNonFatalEitherShouldNotCatchFatal: Result = {

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

      def testCanRecover_Task_recoverFromNonFatalEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Task].recoverFromNonFatal(fa) { case NonFatal(_) => 999.asRight[SomeError] }.runSyncUnsafe()

        actual ==== expected
      }

      def testCanRecover_Task_recoverFromNonFatalEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual = Fx[Task].recoverFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }.runSyncUnsafe()

        actual ==== expected
      }

      def testCanRecover_Task_recoverEitherFromNonFatalShouldRecoverFromNonFatal: Result = {

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

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_Task_recoverEitherFromNonFatalShouldNotCatchFatal: Result = {

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

      def testCanRecover_Task_recoverEitherFromNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[Task]
            .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
            .runSyncUnsafe()

        actual ==== expected
      }

      def testCanRecover_Task_recoverEitherFromNonFatalShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[Task]
            .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
            .runSyncUnsafe()

        actual ==== expected
      }

      def testCanRecover_Task_recoverEitherTFromNonFatalShouldRecoverFromNonFatal: Result = {

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

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanRecover_Task_recoverEitherTFromNonFatalShouldNotCatchFatal: Result = {

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

      def testCanRecover_Task_recoverEitherTFromNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[Task]
            .recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
            .value
            .runSyncUnsafe()

        actual ==== expected
      }

      def testCanRecover_Task_recoverEitherTFromNonFatalShouldReturnFailedResult: Result = {

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

    object OnNonFatalSpec {

      def testOnNonFatal_Task_onNonFatalWithShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 123.some
        var actual            = none[Int] // scalafix:ok DisableSyntax.var

        val result =
          try {
            val r = Fx[Task]
              .onNonFatalWith(fa) {
                case NonFatal(`expectedExpcetion`) =>
                  Task.delay {
                    actual = expected
                  } *> Task.unit
              }
              .runSyncUnsafe()
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
      def testOnNonFatal_Task_onNonFatalWithShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))
        var actual            = none[Int] // scalafix:ok DisableSyntax.var

        val io = Fx[Task].onNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) =>
            Task.delay {
              actual = 123.some
              ()
            } *> Task.unit
        }
        try {
          val actual = io.runSyncUnsafe()
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

      def testOnNonFatal_Task_onNonFatalWithShouldReturnSuccessfulResult: Result = {

        val expectedResult = 999
        val fa             = run[Task, Int](expectedResult)

        val expected = none[Int]
        var actual   = none[Int] // scalafix:ok DisableSyntax.var

        val result = Fx[Task]
          .onNonFatalWith(fa) {
            case NonFatal(_) =>
              Task.delay {
                actual = 123.some
              } *> Task.unit
          }
          .runSyncUnsafe()

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
