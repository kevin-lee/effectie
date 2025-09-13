package effectie.instances.ce2

import cats.effect._
import cats.syntax.all._
import effectie.specs.fxCtorSpec.FxCtorSpecs4Js
import effectie.testing.FutureTools
import fxCtor.ioFxCtor
import munit.Assertions

import scala.concurrent._
import scala.concurrent.duration._

/** @author Kevin Lee
  * @since 2020-12-06
  */
class fxCtorSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  override val munitTimeout: FiniteDuration = 200.milliseconds

  private def assertWithAttempt[A](io: IO[A], expected: Either[Throwable, A]): IO[Unit] = {
    io.attempt.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test FxCtor[IO].effectOf")(
    FxCtorSpecs4Js
      .testEffectOf[IO]((fa, actual, expected, log) => fa.map(_ => Assertions.assertEquals(actual(0), expected, log)))
      .unsafeToFuture()
  )

  test("test FxCtor[IO].fromEffect(effectOf)")(
    FxCtorSpecs4Js
      .testFromEffect[IO]((fa, actual, expected, log) => fa.map(_ => Assertions.assertEquals(actual(0), expected, log)))
      .unsafeToFuture()
  )
  test("test FxCtor[IO].fromEffect(pureOf)")(
    FxCtorSpecs4Js
      .testFromEffectWithPure[IO]((fa, actual, expected, log) =>
        fa.map(_ => Assertions.assertEquals(actual(0), expected, log))
      )
      .unsafeToFuture()
  )
  test("test FxCtor[IO].pureOf")(
    FxCtorSpecs4Js
      .testPureOf[IO]((fa, actual, expected, log) => fa.map(_ => Assertions.assertEquals(actual, expected, log)))
      .unsafeToFuture()
  )
  test("test FxCtor[IO].pureOrError(success case)")(
    FxCtorSpecs4Js
      .testPureOrErrorSuccessCase[IO]((fa, actual, expected, log) =>
        fa.map(_ => Assertions.assertEquals(actual, expected, log))
      )
      .unsafeToFuture()
  )
  test("test FxCtor[IO].pureOrError(error case)")(
    FxCtorSpecs4Js
      .testPureOrErrorErrorCase[IO]((io, expected) => assertWithAttempt(io, expected.asLeft))
      .unsafeToFuture()
  )
  test("test FxCtor[IO].unitOf")(
    FxCtorSpecs4Js.testUnitOf[IO](io => io.map(actual => Assertions.assertEquals(actual, ()))).unsafeToFuture()
  )
  test("test FxCtor[IO].errorOf")(
    FxCtorSpecs4Js.testErrorOf[IO]((io, expected) => assertWithAttempt(io, expected.asLeft)).unsafeToFuture()
  )
  test("test FxCtor[IO].fromEither(Right)")(
    FxCtorSpecs4Js.testFromEitherRightCase[IO](assertWithAttempt).unsafeToFuture()
  )
  test("test FxCtor[IO].fromEither(Left)")(
    FxCtorSpecs4Js.testFromEitherLeftCase[IO](assertWithAttempt).unsafeToFuture()
  )
  test("test FxCtor[IO].fromOption(Some)")(
    FxCtorSpecs4Js.testFromOptionSomeCase[IO](assertWithAttempt).unsafeToFuture()
  )
  test("test FxCtor[IO].fromOption(None)")(
    FxCtorSpecs4Js.testFromOptionNoneCase[IO](assertWithAttempt).unsafeToFuture()
  )
  test("test FxCtor[IO].fromTry(Success)")(
    FxCtorSpecs4Js.testFromTrySuccessCase[IO](assertWithAttempt).unsafeToFuture()
  )
  test("test FxCtor[IO].fromTry(Failure)")(
    FxCtorSpecs4Js.testFromTryFailureCase[IO](assertWithAttempt).unsafeToFuture()
  )

}
