package effectie.instances.ce3

import cats.Id
import cats.effect.*
import cats.effect.unsafe.IORuntime
import effectie.instances.ce3.compat.CatsEffectIoCompatForFuture
import effectie.instances.ce3.fxCtor.given
import effectie.core.FxCtor
import effectie.specs.fxCtorSpec.{FxCtorSpecs, IdSpecs}
import effectie.testing.tools
import effectie.testing.types.SomeThrowableError
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import extras.hedgehog.cats.effect.CatsEffectRunner
import hedgehog.*
import hedgehog.runner.*

/** @author Kevin Lee
  * @since 2020-12-06
  */
object FxCtorSpec extends Properties {

  private val assertWithAttempt: (IO[Int], Either[Throwable, Int]) => Result = { (ioA, expected) =>
    import CatsEffectRunner.*
    given ticket: Ticker = Ticker(TestContext())

    ioA.attempt.completeThen { actual =>
      (actual ==== expected).log(s"$actual does not equal to $expected")
    }
  }

  override def tests: List[Test] = ioSpecs ++ futureSpecs ++ idSpecs

  private val ioSpecs = List(
    property(
      "test FxCtor[IO].effectOf",
      FxCtorSpecs.testEffectOf[IO] { io =>
        import CatsEffectRunner.*
        given ticket: Ticker = Ticker(TestContext())
        io.completeAs(())
      },
    ),
    property(
      "test FxCtor[IO].pureOf",
      FxCtorSpecs.testPureOf[IO] { io =>
        import CatsEffectRunner.*
        given ticket: Ticker = Ticker(TestContext())
        io.completeAs(())
      },
    ),
    property(
      "test FxCtor[IO].pureOrError(success case)",
      FxCtorSpecs.testPureOrErrorSuccessCase[IO] { io =>
        import CatsEffectRunner.*
        given ticket: Ticker = Ticker(TestContext())
        io.completeAs(())
      },
    ),
    example(
      "test FxCtor[IO].pureOrError(error case)",
      FxCtorSpecs.testPureOrErrorErrorCase[IO] { (io, expectedError) =>
        import CatsEffectRunner.*
        given ticket: Ticker = Ticker(TestContext())

        io.expectError(expectedError)
      },
    ),
    example(
      "test FxCtor[IO].unitOf",
      FxCtorSpecs.testUnitOf[IO] { io =>
        import CatsEffectRunner.*
        given ticket: Ticker = Ticker(TestContext())
        io.completeAs(())
      },
    ),
    example(
      "test FxCtor[IO].errorOf",
      FxCtorSpecs.testErrorOf[IO] { (io, expectedError) =>
        import CatsEffectRunner.*
        given ticket: Ticker = Ticker(TestContext())
        io.expectError(expectedError)
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

  private val futureSpecs = effectie.core.FxCtorSpec.futureSpecs

  private val idSpecs = {
    import effectie.instances.id.fxCtor.*
    List(
      property("test FxCtor[Id].effectOf", IdSpecs.testEffectOf),
      property("test FxCtor[Id].pureOf", IdSpecs.testPureOf),
      property("test FxCtor[Id].pureOrError(success case)", IdSpecs.testPureOrErrorSuccessCase),
      example("test FxCtor[Id].pureOrError(error case)", IdSpecs.testPureOrErrorErrorCase),
      example("test FxCtor[Id].unitOf", IdSpecs.testUnitOf),
      example("test FxCtor[Id].testErrorOf", IdSpecs.testErrorOf),
      property("test FxCtor[Id].fromEither(Right)", IdSpecs.testFromEitherRightCase),
      property("test FxCtor[Id].fromEither(Left)", IdSpecs.testFromEitherLeftCase),
      property("test FxCtor[Id].fromOption(Some)", IdSpecs.testFromOptionSomeCase),
      property("test FxCtor[Id].fromOption(None)", IdSpecs.testFromOptionNoneCase),
      property("test FxCtor[Id].fromTry(Success)", IdSpecs.testFromTrySuccessCase),
      property("test FxCtor[Id].fromTry(Failure)", IdSpecs.testFromTryFailureCase),
    )
  }

}
