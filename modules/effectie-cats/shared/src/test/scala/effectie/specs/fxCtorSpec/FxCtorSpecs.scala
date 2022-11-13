package effectie.specs.fxCtorSpec

import cats.syntax.all._
import effectie.core.FxCtor
import effectie.testing.types.SomeThrowableError
import hedgehog._

import scala.util.Try

/** @author Kevin Lee
  * @since 2022-04-20
  */
object FxCtorSpecs {

  def testEffectOf[F[*]: FxCtor](run: F[Unit] => Result): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var actual        = before // scalafix:ok DisableSyntax.var
    val testBefore    = (actual ==== before).log("before pureOrError")
    val io            = FxCtor[F].effectOf({ actual = after; () })
    val testBeforeRun = (actual ==== before).log("after pureOrError but before run")
    val runResult     = run(io)
    val testAfterRun  = (actual ==== after).log("after pureOrError and run")
    Result.all(
      List(
        testBefore.log("testBefore"),
        testBeforeRun.log("testBeforeRun"),
        runResult,
        testAfterRun.log("testAfterRun"),
      )
    )
  }

  def testPureOf[F[*]: FxCtor](run: F[Unit] => Result): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var actual        = before // scalafix:ok DisableSyntax.var
    val testBefore    = (actual ==== before).log("before pureOrError")
    val io            = FxCtor[F].pureOf({ actual = after; () })
    val testBeforeRun = actual ==== after
    val runResult     = run(io)
    val testAfterRun  = actual ==== after
    Result.all(
      List(
        testBefore.log("testBefore"),
        testBeforeRun.log("testBeforeRun"),
        runResult.log("runResult"),
        testAfterRun.log("testAfterRun"),
      )
    )
  }

  def testPureOrErrorSuccessCase[F[*]: FxCtor](run: F[Unit] => Result): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var actual        = before // scalafix:ok DisableSyntax.var
    val testBefore    = (actual ==== before).log("before pureOrError")
    val io            = FxCtor[F].pureOrError({ actual = after; () })
    val testBeforeRun = (actual ==== after).log("after pureOrError but before run")
    val runResult     = run(io)
    val testAfterRun  = (actual ==== after).log("after pureOrError and run")
    Result.all(
      List(
        testBefore.log("testBefore"),
        testBeforeRun.log("testBeforeRun"),
        runResult.log("runResult"),
        testAfterRun.log("testAfterRun"),
      )
    )
  }

  def testPureOrErrorErrorCase[F[*]: FxCtor](run: (F[Unit], Throwable) => Result): Result = {
    val expectedMessage = "This is a throwable test error."
    val expectedError   = SomeThrowableError.message(expectedMessage)

    val io = FxCtor[F].pureOrError((throw expectedError): Unit) // scalafix:ok DisableSyntax.throw
    run(io, expectedError)
  }

  def testUnitOf[F[*]: FxCtor](run: F[Unit] => Result): Result = {
    val io        = FxCtor[F].unitOf
    val runResult = run(io)
    runResult.log("runResult")
  }

  def testErrorOf[F[*]: FxCtor](run: (F[Unit], Throwable) => Result): Result = {
    val expectedMessage = "This is a throwable test error."
    val expectedError   = SomeThrowableError.message(expectedMessage)

    val io = FxCtor[F].errorOf[Unit](expectedError)
    run(io, expectedError)
  }

  def testFromEitherRightCase[F[*]: FxCtor](run: (F[Int], Either[Throwable, Int]) => Result): Property = for {
    n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
  } yield {

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected = n.asRight[SomeThrowableError]
    val ioA      = FxCtor[F].fromEither(expected)

    run(ioA, expected)
  }

  def testFromEitherLeftCase[F[*]: FxCtor](run: (F[Int], Either[Throwable, Int]) => Result): Property = for {
    errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected = SomeThrowableError.message(errorMessage).asLeft[Int]
    val ioA      = FxCtor[F].fromEither(expected)

    run(ioA, expected)
  }

  def testFromOptionSomeCase[F[*]: FxCtor](run: (F[Int], Either[Throwable, Int]) => Result): Property = for {
    n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected = n.asRight[SomeThrowableError]
    val input    = n.some
    val ioA      =
      FxCtor[F].fromOption(input)(SomeThrowableError.Message("This should never happen!"))

    run(ioA, expected)
  }

  def testFromOptionNoneCase[F[*]: FxCtor](run: (F[Int], Either[Throwable, Int]) => Result): Property = for {
    errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected = SomeThrowableError.message(errorMessage).asLeft[Int]
    val ioA      = FxCtor[F].fromOption(none[Int])(SomeThrowableError.Message(errorMessage))

    run(ioA, expected)
  }

  def testFromTrySuccessCase[F[*]: FxCtor](run: (F[Int], Either[Throwable, Int]) => Result): Property = for {
    n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected        = n.asRight[SomeThrowableError]
    val input: Try[Int] = scala.util.Success(n)
    val ioA             = FxCtor[F].fromTry(input)

    run(ioA, expected)
  }

  def testFromTryFailureCase[F[*]: FxCtor](run: (F[Int], Either[Throwable, Int]) => Result): Property = for {
    errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected        = SomeThrowableError.message(errorMessage).asLeft[Int]
    val input: Try[Int] = scala.util.Failure(SomeThrowableError.message(errorMessage))
    val ioA             = FxCtor[F].fromTry(input)

    run(ioA, expected)
  }

}
