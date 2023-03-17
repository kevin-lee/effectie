package effectie.instances.ce3

import cats.effect._
import fxCtor._
import effectie.specs.fxCtorSpec.FxCtorSpecs
import effectie.specs.fxCtorSpec.IdSpecs
import extras.concurrent.testing.types.ErrorLogger
import extras.hedgehog.ce3.syntax.runner._
import hedgehog._
import hedgehog.runner._

/** @author Kevin Lee
  * @since 2020-12-06
  */
object fxCtorSpec extends Properties {
  private implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = ioSpecs ++ futureSpecs ++ idSpecs

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

  private val futureSpecs = effectie.instances.future.fxCtorSpec.futureSpecs

  private val idSpecs = {
    import effectie.instances.id.fxCtor._
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
      property("test FxCtor[Id].flatMapFa(Id[A])", IdSpecs.testFlatMapFa),
    )
  }

}
