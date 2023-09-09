package effectie.instances.ce2.f

import cats.effect._
import effectie.specs.fxCtorSpec.FxCtorSpecs
import effectie.testing.tools
import effectie.instances.ce2.f.fxCtor.syncFxCtor
import extras.concurrent.testing.types.ErrorLogger
import hedgehog._
import hedgehog.runner._

/** @author Kevin Lee
  * @since 2020-12-06
  */
object fxCtorSpec extends Properties {

  override def tests: List[Test] =
    ioSpecs

  private val assertWithAttempt: (IO[Int], Either[Throwable, Int]) => Result = { (io, expected) =>
    val actual = io.attempt.unsafeRunSync()
    (actual ==== expected).log(s"$actual does not equal to $expected")
  }

  implicit private val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  private val unit: Unit = ()

  private val ioSpecs = List(
    property("test FxCtor[IO].effectOf", FxCtorSpecs.testEffectOf[IO](_.unsafeRunSync() ==== unit)),
    property("test FxCtor[IO].fromEffect(effectOf)", FxCtorSpecs.testFromEffect[IO](_.unsafeRunSync() ==== unit)),
    property("test FxCtor[IO].fromEffect(pureOf)", FxCtorSpecs.testFromEffectWithPure[IO](_.unsafeRunSync() ==== unit)),
    property("test FxCtor[IO].pureOf", FxCtorSpecs.testPureOf[IO](_.unsafeRunSync() ==== unit)),
    property(
      "test FxCtor[IO].pureOrError(success case)",
      FxCtorSpecs.testPureOrErrorSuccessCase[IO](_.unsafeRunSync() ==== unit),
    ),
    example(
      "test FxCtor[IO].pureOrError(error case)",
      FxCtorSpecs.testPureOrErrorErrorCase[IO] { (io, expected) =>
        tools.expectThrowable(io.unsafeRunSync(), expected)
      },
    ),
    example("test FxCtor[IO].unitOf", FxCtorSpecs.testUnitOf[IO](_.unsafeRunSync() ==== unit)),
    example(
      "test FxCtor[IO].errorOf",
      FxCtorSpecs.testErrorOf[IO] { (io, expected) =>
        tools.expectThrowable(io.unsafeRunSync(), expected)
      },
    ),
    property(
      "test FxCtor[IO].fromEither(Right)",
      FxCtorSpecs.testFromEitherRightCase[IO](assertWithAttempt),
    ),
    property(
      "test FxCtor[IO].fromEither(Left)",
      FxCtorSpecs.testFromEitherLeftCase[IO](assertWithAttempt),
    ),
    property(
      "test FxCtor[IO].fromOption(Some)",
      FxCtorSpecs.testFromOptionSomeCase[IO](assertWithAttempt),
    ),
    property(
      "test FxCtor[IO].fromOption(None)",
      FxCtorSpecs.testFromOptionNoneCase[IO](assertWithAttempt),
    ),
    property(
      "test FxCtor[IO].fromTry(Success)",
      FxCtorSpecs.testFromTrySuccessCase[IO](assertWithAttempt),
    ),
    property(
      "test FxCtor[IO].fromTry(Failure)",
      FxCtorSpecs.testFromTryFailureCase[IO](assertWithAttempt),
    ),
  )

}
