package effectie.instances.monix3.f

import cats.syntax.all._
import effectie.instances.ce2.f.fxCtor.syncFxCtor
import effectie.specs.fxCtorSpec.FxCtorSpecs4Js
import effectie.testing.FutureTools
import munit.Assertions

import scala.concurrent._
import scala.concurrent.duration._

import monix.eval.Task

/** @author Kevin Lee
  * @since 2020-12-06
  */
class fxCtorSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  import monix.execution.Scheduler.Implicits.global

  override val munitTimeout: FiniteDuration = 200.milliseconds

  private def assertWithAttempt[A](io: Task[A], expected: Either[Throwable, A]): Task[Unit] = {
    io.attempt.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test FxCtor[Task].effectOf") {
    FxCtorSpecs4Js
      .testEffectOf[Task]((fa, actual, expected, log) => fa.map(_ => Assertions.assertEquals(actual(0), expected, log)))
      .runToFuture
  }
  test("test FxCtor[Task].fromEffect(effectOf)") {
    FxCtorSpecs4Js
      .testFromEffect[Task]((fa, actual, expected, log) =>
        fa.map(_ => Assertions.assertEquals(actual(0), expected, log))
      )
      .runToFuture
  }
  test("test FxCtor[Task].fromEffect(pureOf)") {
    FxCtorSpecs4Js
      .testFromEffectWithPure[Task]((fa, actual, expected, log) =>
        fa.map(_ => Assertions.assertEquals(actual(0), expected, log))
      )
      .runToFuture
  }
  test("test FxCtor[Task].pureOf") {
    FxCtorSpecs4Js
      .testPureOf[Task]((fa, actual, expected, log) => fa.map(_ => Assertions.assertEquals(actual, expected, log)))
      .runToFuture
  }
  test("test FxCtor[Task].pureOrError(success case)") {
    FxCtorSpecs4Js
      .testPureOrErrorSuccessCase[Task]((fa, actual, expected, log) =>
        fa.map(_ => Assertions.assertEquals(actual, expected, log))
      )
      .runToFuture
  }
  test("test FxCtor[Task].pureOrError(error case)") {
    FxCtorSpecs4Js
      .testPureOrErrorErrorCase[Task]((io, expected) => assertWithAttempt(io, expected.asLeft))
      .runToFuture
  }
  test("test FxCtor[Task].unitOf") {
    FxCtorSpecs4Js.testUnitOf[Task](io => io.map(actual => Assertions.assertEquals(actual, ()))).runToFuture
  }
  test("test FxCtor[Task].errorOf") {
    FxCtorSpecs4Js.testErrorOf[Task]((io, expected) => assertWithAttempt(io, expected.asLeft)).runToFuture
  }
  test("test FxCtor[Task].fromEither(Right)") {
    FxCtorSpecs4Js.testFromEitherRightCase[Task](assertWithAttempt).runToFuture
  }
  test("test FxCtor[Task].fromEither(Left)") {
    FxCtorSpecs4Js.testFromEitherLeftCase[Task](assertWithAttempt).runToFuture
  }
  test("test FxCtor[Task].fromOption(Some)") {
    FxCtorSpecs4Js.testFromOptionSomeCase[Task](assertWithAttempt).runToFuture
  }
  test("test FxCtor[Task].fromOption(None)") {
    FxCtorSpecs4Js.testFromOptionNoneCase[Task](assertWithAttempt).runToFuture
  }
  test("test FxCtor[Task].fromTry(Success)") {
    FxCtorSpecs4Js.testFromTrySuccessCase[Task](assertWithAttempt).runToFuture
  }
  test(
    "test FxCtor[Task].fromTry(Failure)"
  ) {
    FxCtorSpecs4Js.testFromTryFailureCase[Task](assertWithAttempt).runToFuture
  }

}
