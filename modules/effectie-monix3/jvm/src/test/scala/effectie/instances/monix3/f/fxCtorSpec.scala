package effectie.instances.monix3.f

import effectie.specs.fxCtorSpec.FxCtorSpecs
import effectie.testing.tools
import effectie.instances.monix3.fxCtor.taskFxCtor
import hedgehog._
import hedgehog.runner._

import monix.eval.Task

/** @author Kevin Lee
  * @since 2020-12-06
  */
object fxCtorSpec extends Properties {
  import monix.execution.Scheduler.Implicits.global

  override def tests: List[Test] =
    taskSpecs

  private val assertWithAttempt: (Task[Int], Either[Throwable, Int]) => Result = { (io, expected) =>
    val actual = io.attempt.runSyncUnsafe()
    (actual ==== expected).log(s"$actual does not equal to $expected")
  }

  private val unit: Unit = ()

  private val taskSpecs = List(
    property("test FxCtor[Task].effectOf", FxCtorSpecs.testEffectOf[Task](_.runSyncUnsafe() ==== unit)),
    property("test FxCtor[Task].fromEffect(effectOf)", FxCtorSpecs.testFromEffect[Task](_.runSyncUnsafe() ==== unit)),
    property(
      "test FxCtor[Task].fromEffect(pureOf)",
      FxCtorSpecs.testFromEffectWithPure[Task](_.runSyncUnsafe() ==== unit),
    ),
    property("test FxCtor[Task].pureOf", FxCtorSpecs.testPureOf[Task](_.runSyncUnsafe() ==== unit)),
    property(
      "test FxCtor[Task].pureOrError(success case)",
      FxCtorSpecs.testPureOrErrorSuccessCase[Task](_.runSyncUnsafe() ==== unit),
    ),
    example(
      "test FxCtor[Task].pureOrError(error case)",
      FxCtorSpecs.testPureOrErrorErrorCase[Task] { (io, expected) =>
        tools.expectThrowable(io.runSyncUnsafe(), expected)
      },
    ),
    example("test FxCtor[Task].unitOf", FxCtorSpecs.testUnitOf[Task](_.runSyncUnsafe() ==== unit)),
    example(
      "test FxCtor[Task].errorOf",
      FxCtorSpecs.testErrorOf[Task] { (io, expected) =>
        tools.expectThrowable(io.runSyncUnsafe(), expected)
      },
    ),
    property(
      "test FxCtor[Task].fromEither(Right)",
      FxCtorSpecs.testFromEitherRightCase[Task](assertWithAttempt),
    ),
    property(
      "test FxCtor[Task].fromEither(Left)",
      FxCtorSpecs.testFromEitherLeftCase[Task](assertWithAttempt),
    ),
    property(
      "test FxCtor[Task].fromOption(Some)",
      FxCtorSpecs.testFromOptionSomeCase[Task](assertWithAttempt),
    ),
    property(
      "test FxCtor[Task].fromOption(None)",
      FxCtorSpecs.testFromOptionNoneCase[Task](assertWithAttempt),
    ),
    property(
      "test FxCtor[Task].fromTry(Success)",
      FxCtorSpecs.testFromTrySuccessCase[Task](assertWithAttempt),
    ),
    property(
      "test FxCtor[Task].fromTry(Failure)",
      FxCtorSpecs.testFromTryFailureCase[Task](assertWithAttempt),
    ),
  )

}
