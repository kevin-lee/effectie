package effectie.instances.ce3.f

import cats.effect._
import cats.syntax.all._
import effectie.specs.fxCtorSpec.FxCtorSpecs4Js
import fxCtor._
import munit.Assertions

/** @author Kevin Lee
  * @since 2020-12-06
  */
class fxCtorSpec extends munit.CatsEffectSuite {

  private def assertWithAttempt[A](io: IO[A], expected: Either[Throwable, A]): IO[Unit] = {
    io.attempt.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test FxCtor[IO].effectOf") {
    FxCtorSpecs4Js
      .testEffectOf[IO]((fa, actual, expected, log) => fa.map(_ => Assertions.assertEquals(actual(0), expected, log)))
  }

  test("test FxCtor[IO].fromEffect(effectOf)") {
    FxCtorSpecs4Js
      .testFromEffect[IO]((fa, actual, expected, log) => fa.map(_ => Assertions.assertEquals(actual(0), expected, log)))
  }

  test("test FxCtor[IO].fromEffect(pureOf)") {
    FxCtorSpecs4Js
      .testFromEffectWithPure[IO]((fa, actual, expected, log) =>
        fa.map(_ => Assertions.assertEquals(actual(0), expected, log))
      )
  }

  test("test FxCtor[IO].pureOf") {
    FxCtorSpecs4Js
      .testPureOf[IO]((fa, actual, expected, log) => fa.map(_ => Assertions.assertEquals(actual, expected, log)))
  }

  test("test FxCtor[IO].pureOrError(success case)") {
    FxCtorSpecs4Js
      .testPureOrErrorSuccessCase[IO]((fa, actual, expected, log) =>
        fa.map(_ => Assertions.assertEquals(actual, expected, log))
      )
  }

  test("test FxCtor[IO].pureOrError(error case)") {
    FxCtorSpecs4Js
      .testPureOrErrorErrorCase[IO]((io, expected) => assertWithAttempt(io, expected.asLeft))
  }

  test("test FxCtor[IO].unitOf") {
    FxCtorSpecs4Js.testUnitOf[IO](io => io.map(actual => Assertions.assertEquals(actual, ())))
  }
  test("test FxCtor[IO].errorOf") {
    FxCtorSpecs4Js.testErrorOf[IO]((io, expected) => assertWithAttempt(io, expected.asLeft))
  }
  test("test FxCtor[IO].fromEither(Right)") {
    FxCtorSpecs4Js.testFromEitherRightCase[IO](assertWithAttempt)
  }
  test("test FxCtor[IO].fromEither(Left)") {
    FxCtorSpecs4Js.testFromEitherLeftCase[IO](assertWithAttempt)
  }
  test("test FxCtor[IO].fromOption(Some)") {
    FxCtorSpecs4Js.testFromOptionSomeCase[IO](assertWithAttempt)
  }
  test("test FxCtor[IO].fromOption(None)") {
    FxCtorSpecs4Js.testFromOptionNoneCase[IO](assertWithAttempt)
  }
  test("test FxCtor[IO].fromTry(Success)") {
    FxCtorSpecs4Js.testFromTrySuccessCase[IO](assertWithAttempt)
  }
  test(
    "test FxCtor[IO].fromTry(Failure)"
  ) {
    FxCtorSpecs4Js.testFromTryFailureCase[IO](assertWithAttempt)
  }

}
