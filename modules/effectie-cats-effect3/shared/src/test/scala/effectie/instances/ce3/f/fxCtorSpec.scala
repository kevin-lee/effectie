package effectie.instances.ce3.f

import cats.effect._
import effectie.specs.fxCtorSpec.FxCtorSpecs
import extras.concurrent.testing.types.ErrorLogger
import extras.hedgehog.ce3.syntax.runner._
import fxCtor._
import hedgehog._
import hedgehog.runner._

/** @author Kevin Lee
  * @since 2020-12-06
  */
object fxCtorSpec extends Properties {
  private implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = ioSpecs

  private val assertWithAttempt: (IO[Int], Either[Throwable, Int]) => Result = { (ioA, expected) =>
    withIO { implicit ticker =>
      ioA.attempt.completeThen { actual =>
        (actual ==== expected).log(s"$actual does not equal to $expected")
      }
    }
  }

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
