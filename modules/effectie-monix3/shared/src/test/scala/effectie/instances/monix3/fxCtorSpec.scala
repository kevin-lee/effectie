package effectie.instances.monix3

import effectie.core._
import fxCtor._
import effectie.specs.fxCtorSpec.FxCtorSpecs
import effectie.testing.tools
import extras.concurrent.testing.types.ErrorLogger
import hedgehog._
import hedgehog.runner._
import monix.eval.Task

/** @author Kevin Lee
  * @since 2020-12-06
  */
object fxCtorSpec extends Properties {
  private implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = taskSpecs

  def assertWithAttempt[F[*]: FxCtor](
    run: F[Int] => Either[Throwable, Int]
  ): (F[Int], Either[Throwable, Int]) => Result = { (io, expected) =>
    val actual = run(io)
    (actual ==== expected).log(s"$actual does not equal to $expected")
  }

  private val unit: Unit = ()

  private val taskSpecs = {
    import monix.execution.Scheduler.Implicits.global
    List(
      property(
        "test FxCtor[Task].effectOf",
        effectie.specs.fxCtorSpec.FxCtorSpecs.testEffectOf[Task](_.runSyncUnsafe() ==== unit)
      ),
      property(
        "test FxCtor[Task].pureOf",
        effectie.specs.fxCtorSpec.FxCtorSpecs.testPureOf[Task](_.runSyncUnsafe() ==== unit)
      ),
      property(
        "test FxCtor[Task].pureOrError(success case)",
        effectie.specs.fxCtorSpec.FxCtorSpecs.testPureOrErrorSuccessCase[Task](_.runSyncUnsafe() ==== unit)
      ),
      example(
        "test FxCtor[Task].pureOrError(error case)",
        effectie.specs.fxCtorSpec.FxCtorSpecs.testPureOrErrorErrorCase[Task] { (io, expectedError) =>
          tools.expectThrowable(io.runSyncUnsafe(), expectedError)
        }
      ),
      example(
        "test FxCtor[Task].unitOf",
        effectie.specs.fxCtorSpec.FxCtorSpecs.testUnitOf[Task](_.runSyncUnsafe() ==== unit)
      ),
      example(
        "test FxCtor[Task].errorOf",
        effectie.specs.fxCtorSpec.FxCtorSpecs.testErrorOf[Task] { (io, expectedError) =>
          tools.expectThrowable(io.runSyncUnsafe(), expectedError)
        }
      ),
      property(
        "test FxCtor[Task].fromEither(Right)",
        FxCtorSpecs.testFromEitherRightCase[Task](assertWithAttempt[Task](_.attempt.runSyncUnsafe()))
      ),
      property(
        "test FxCtor[Task].fromEither(Left)",
        FxCtorSpecs.testFromEitherLeftCase[Task](assertWithAttempt[Task](_.attempt.runSyncUnsafe()))
      ),
      property(
        "test FxCtor[Task].fromOption(Some)",
        FxCtorSpecs.testFromOptionSomeCase[Task](assertWithAttempt[Task](_.attempt.runSyncUnsafe()))
      ),
      property(
        "test FxCtor[Task].fromOption(None)",
        FxCtorSpecs.testFromOptionNoneCase[Task](assertWithAttempt[Task](_.attempt.runSyncUnsafe()))
      ),
      property(
        "test FxCtor[Task].fromTry(Success)",
        FxCtorSpecs.testFromTrySuccessCase[Task](assertWithAttempt[Task](_.attempt.runSyncUnsafe()))
      ),
      property(
        "test FxCtor[Task].fromTry(Failure)",
        FxCtorSpecs.testFromTryFailureCase[Task](assertWithAttempt[Task](_.attempt.runSyncUnsafe()))
      ),
    )
  }

}
