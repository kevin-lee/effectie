package effectie.monix

import cats.effect.IO
import effectie.core._
import effectie.monix.fxCtor._
import effectie.specs.fxCtorSpec.{FxCtorSpecs, IdSpecs}
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

  override def tests: List[Test] = taskSpecs ++ ioSpecs ++ futureSpecs ++ idSpecs

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

  import effectie.cats.fxCtor.ioFxCtor

  private val ioSpecs = List(
    property(
      "test FxCtor[IO].effectOf",
      effectie.specs.fxCtorSpec.FxCtorSpecs.testEffectOf[IO](_.unsafeRunSync() ==== unit)
    ),
    property(
      "test FxCtor[IO].pureOf",
      effectie.specs.fxCtorSpec.FxCtorSpecs.testPureOf[IO](_.unsafeRunSync() ==== unit)
    ),
    example(
      "test FxCtor[IO].unitOf",
      effectie.specs.fxCtorSpec.FxCtorSpecs.testUnitOf[IO](_.unsafeRunSync() ==== unit)
    ),
    example(
      "test FxCtor[IO].errorOf",
      effectie.specs.fxCtorSpec.FxCtorSpecs.testErrorOf[IO] { (io, expectedError) =>
        tools.expectThrowable(io.unsafeRunSync(), expectedError)
      },
    ),
    property(
      "test FxCtor[IO].fromEither(Right)",
      FxCtorSpecs.testFromEitherRightCase[IO](assertWithAttempt[IO](_.attempt.unsafeRunSync()))
    ),
    property(
      "test FxCtor[IO].fromEither(Left)",
      FxCtorSpecs.testFromEitherLeftCase[IO](assertWithAttempt[IO](_.attempt.unsafeRunSync()))
    ),
    property(
      "test FxCtor[IO].fromOption(Some)",
      FxCtorSpecs.testFromOptionSomeCase[IO](assertWithAttempt[IO](_.attempt.unsafeRunSync()))
    ),
    property(
      "test FxCtor[IO].fromOption(None)",
      FxCtorSpecs.testFromOptionNoneCase[IO](assertWithAttempt[IO](_.attempt.unsafeRunSync()))
    ),
    property(
      "test FxCtor[IO].fromTry(Success)",
      FxCtorSpecs.testFromTrySuccessCase[IO](assertWithAttempt[IO](_.attempt.unsafeRunSync()))
    ),
    property(
      "test FxCtor[IO].fromTry(Failure)",
      FxCtorSpecs.testFromTryFailureCase[IO](assertWithAttempt[IO](_.attempt.unsafeRunSync()))
    )
  )

  private val futureSpecs = effectie.core.FxCtorSpec.futureSpecs

  import effectie.cats.fxCtor.idFxCtor

  private val idSpecs = List(
    property("test FxCtor[Id].effectOf", IdSpecs.testEffectOf),
    property("test FxCtor[Id].pureOf", IdSpecs.testPureOf),
    example("test FxCtor[Id].unitOf", IdSpecs.testUnitOf),
    example("test FxCtor[Id].errorOf", IdSpecs.testErrorOf),
    property("test FxCtor[Id].fromEither(Right)", IdSpecs.testFromEitherRightCase),
    property("test FxCtor[Id].fromEither(Left)", IdSpecs.testFromEitherLeftCase),
    property("test FxCtor[Id].fromOption(Some)", IdSpecs.testFromOptionSomeCase),
    property("test FxCtor[Id].fromOption(None)", IdSpecs.testFromOptionNoneCase),
    property("test FxCtor[Id].fromTry(Success)", IdSpecs.testFromTrySuccessCase),
    property("test FxCtor[Id].fromTry(Failure)", IdSpecs.testFromTryFailureCase),
  )

}
