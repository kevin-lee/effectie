package effectie.ce2

import cats.effect._
import fxCtor._
import effectie.specs.fxCtorSpec.FxCtorSpecs
import effectie.specs.fxCtorSpec.IdSpecs
import effectie.testing.tools
import extras.concurrent.testing.types.ErrorLogger

import hedgehog._
import hedgehog.runner._

/** @author Kevin Lee
  * @since 2020-12-06
  */
object fxCtorSpec extends Properties {

  override def tests: List[Test] =
    ioSpecs ++
      futureSpecs ++
      idSpecs

  private val assertWithAttempt: (IO[Int], Either[Throwable, Int]) => Result = { (io, expected) =>
    val actual = io.attempt.unsafeRunSync()
    (actual ==== expected).log(s"$actual does not equal to $expected")
  }

  implicit private val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  private val unit: Unit = ()

  private val ioSpecs = List(
    property("test FxCtor[IO].effectOf", FxCtorSpecs.testEffectOf[IO](_.unsafeRunSync() ==== unit)),
    property("test FxCtor[IO].pureOf", FxCtorSpecs.testPureOf[IO](_.unsafeRunSync() ==== unit)),
    property(
      "test FxCtor[IO].pureOrError(success case)",
      FxCtorSpecs.testPureOrErrorSuccessCase[IO](_.unsafeRunSync() ==== unit)
    ),
    example(
      "test FxCtor[IO].pureOrError(error case)",
      FxCtorSpecs.testPureOrErrorErrorCase[IO] { (io, expected) =>
        tools.expectThrowable(io.unsafeRunSync(), expected)
      }
    ),
    example("test FxCtor[IO].unitOf", FxCtorSpecs.testUnitOf[IO](_.unsafeRunSync() ==== unit)),
    example(
      "test FxCtor[IO].errorOf",
      FxCtorSpecs.testErrorOf[IO] { (io, expected) =>
        tools.expectThrowable(io.unsafeRunSync(), expected)
      }
    ),
    property(
      "test FxCtor[IO].fromEither(Right)",
      FxCtorSpecs.testFromEitherRightCase[IO](assertWithAttempt)
    ),
    property(
      "test FxCtor[IO].fromEither(Left)",
      FxCtorSpecs.testFromEitherLeftCase[IO](assertWithAttempt)
    ),
    property(
      "test FxCtor[IO].fromOption(Some)",
      FxCtorSpecs.testFromOptionSomeCase[IO](assertWithAttempt)
    ),
    property(
      "test FxCtor[IO].fromOption(None)",
      FxCtorSpecs.testFromOptionNoneCase[IO](assertWithAttempt)
    ),
    property(
      "test FxCtor[IO].fromTry(Success)",
      FxCtorSpecs.testFromTrySuccessCase[IO](assertWithAttempt)
    ),
    property(
      "test FxCtor[IO].fromTry(Failure)",
      FxCtorSpecs.testFromTryFailureCase[IO](assertWithAttempt)
    ),
  )

  private val futureSpecs = effectie.core.FxCtorSpec.futureSpecs

  private val idSpecs = List(
    property("test FxCtor[Id].effectOf", IdSpecs.testEffectOf),
    property("test FxCtor[Id].pureOf", IdSpecs.testPureOf),
    property("test FxCtor[Id].pureOrError(success case)", IdSpecs.testPureOrErrorSuccessCase),
    example("test FxCtor[Id].pureOrError(error case)", IdSpecs.testPureOrErrorErrorCase),
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
