package effectie.instances.ce3

import cats.Id
import cats.effect.*
import cats.effect.unsafe.IORuntime
import effectie.core.FxCtor
import effectie.instances.ce3.compat.CatsEffectIoCompatForFuture
import effectie.instances.ce3.fxCtor.given
import effectie.specs.fxCtorSpec.{FxCtorSpecs, IdSpecs}
import effectie.testing.tools
import effectie.testing.types.SomeThrowableError
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import extras.hedgehog.ce3.syntax.runner.*
import hedgehog.*
import hedgehog.runner.*

/** @author Kevin Lee
  * @since 2020-12-06
  */
object FxCtorSpec extends Properties {

  private val assertWithAttempt: (IO[Int], Either[Throwable, Int]) => Result = { (ioA, expected) =>
    withIO { implicit ticker =>
      ioA.attempt.completeThen { actual =>
        (actual ==== expected).log(s"$actual does not equal to $expected")
      }
    }
  }

  override def tests: List[Test] = ioSpecs

  private val ioSpecs = List(
    property(
      "test FxCtor[IO].effectOf",
      FxCtorSpecs.testEffectOf[IO] { io =>
        withIO { implicit ticker =>
          io.completeAs(())
        }
      },
    ),
    property(
      "test FxCtor[IO].fromEffect(effectOf)",
      FxCtorSpecs.testFromEffect[IO] { io =>
        withIO { implicit ticker =>
          io.completeAs(())
        }
      },
    ),
    property(
      "test FxCtor[IO].fromEffect(pureOf)",
      FxCtorSpecs.testFromEffectWithPure[IO] { io =>
        withIO { implicit ticker =>
          io.completeAs(())
        }
      },
    ),
    property(
      "test FxCtor[IO].pureOf",
      FxCtorSpecs.testPureOf[IO] { io =>
        withIO { implicit ticker =>
          io.completeAs(())
        }
      },
    ),
    property(
      "test FxCtor[IO].pureOrError(success case)",
      FxCtorSpecs.testPureOrErrorSuccessCase[IO] { io =>
        withIO { implicit ticker =>
          io.completeAs(())
        }
      },
    ),
    example(
      "test FxCtor[IO].pureOrError(error case)",
      FxCtorSpecs.testPureOrErrorErrorCase[IO] { (io, expectedError) =>
        withIO { implicit ticker =>
          io.expectError(expectedError)
        }
      },
    ),
    example(
      "test FxCtor[IO].unitOf",
      FxCtorSpecs.testUnitOf[IO] { io =>
        withIO { implicit ticker =>
          io.completeAs(())
        }
      },
    ),
    example(
      "test FxCtor[IO].errorOf",
      FxCtorSpecs.testErrorOf[IO] { (io, expectedError) =>
        withIO { implicit ticker =>
          io.expectError(expectedError)
        }
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
    property(
      "test FxCtor[IO].flatMapFa(IO[A])(A => IO[B])",
      FxCtorSpecs.testFlatMapFx[IO] { (fb, expected) =>
        runIO {
          fb.map(_ ==== expected)
        }
      },
    ),
  )

}
