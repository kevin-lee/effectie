package effectie.specs.fxSpec

import cats.syntax.all._
import effectie.core.Fx
import effectie.testing.types.SomeThrowableError
import hedgehog._

import scala.util.Try

/** @author Kevin Lee
  * @since 2022-04-20
  */
object FxSpecs {

  def testEffectOf[F[*]: Fx](run: F[Unit] => Result): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var actual        = before // scalafix:ok DisableSyntax.var
    val testBefore    = actual ==== before
    val io            = Fx[F].effectOf({ actual = after; () })
    val testBeforeRun = actual ==== before
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

  def testFromEffect[F[*]: Fx](run: F[Unit] => Result): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var actual              = before // scalafix:ok DisableSyntax.var
    val testBefore          = (actual ==== before).log("before effectOf")
    val io                  = Fx[F].effectOf({ actual = after; () })
    val testBeforeRun       = (actual ==== before).log("after effectOf but before fromEffect")
    val fromIo              = Fx[F].fromEffect(io)
    val testAfterFromEffect = (actual ==== before).log("after fromEffect but before run")
    val runResult           = run(fromIo)
    val testAfterRun        = (actual ==== after).log("after fromEffect and run")
    Result.all(
      List(
        testBefore.log("testBefore"),
        testBeforeRun.log("testBeforeRun"),
        testAfterFromEffect.log("testAfterFromEffect"),
        runResult,
        testAfterRun.log("testAfterRun"),
      )
    )
  }

  def testFromEffectWithPure[F[*]: Fx](run: F[Unit] => Result): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var actual              = before // scalafix:ok DisableSyntax.var
    val testBefore          = (actual ==== before).log("before fromEffect")
    val fromPure            = Fx[F].fromEffect(Fx[F].pureOf({ actual = after; () }))
    val testAfterFromEffect = (actual ==== before).log("after fromEffect but before run")
    val runResult           = run(fromPure)
    val testAfterRun        = (actual ==== after).log("after fromEffect and run")
    Result.all(
      List(
        testBefore.log("testBefore"),
        testAfterFromEffect.log("testAfterFromEffect"),
        runResult,
        testAfterRun.log("testAfterRun"),
      )
    )
  }

  def testPureOf[F[*]: Fx](run: F[Unit] => Result): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var actual        = before // scalafix:ok DisableSyntax.var
    val testBefore    = actual ==== before
    val io            = Fx[F].pureOf({ actual = after; () })
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

  def testPureOrErrorSuccessCase[F[*]: Fx](run: F[Unit] => Result): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var actual        = before // scalafix:ok DisableSyntax.var
    val testBefore    = actual ==== before
    val io            = Fx[F].pureOrError({ actual = after; () })
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

  def testPureOrErrorErrorCase[F[*]: Fx](run: (F[Unit], Throwable) => Result): Result = {
    val expectedMessage = "This is a throwable test error."
    val expectedError   = SomeThrowableError.message(expectedMessage)

    val io = Fx[F].pureOrError[Unit](throw expectedError) // scalafix:ok DisableSyntax.throw
    run(io, expectedError)
  }

  def testUnitOf[F[*]: Fx](run: F[Unit] => Result): Result = {
    val io        = Fx[F].unitOf
    val runResult = run(io)
    runResult.log("runResult")
  }

  def testErrorOf[F[*]: Fx](run: (F[Unit], Throwable) => Result): Result = {
    val expectedMessage = "This is a throwable test error."
    val expectedError   = SomeThrowableError.message(expectedMessage)

    val io = Fx[F].errorOf[Unit](expectedError)
    run(io, expectedError)
  }

  def testFromEitherRightCase[F[*]: Fx](run: (F[Int], Either[Throwable, Int]) => Result): Property = for {
    n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
  } yield {

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected = n.asRight[SomeThrowableError]
    val ioA      = Fx[F].fromEither(expected)

    run(ioA, expected)
  }

  def testFromEitherLeftCase[F[*]: Fx](run: (F[Int], Either[Throwable, Int]) => Result): Property = for {
    errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected = SomeThrowableError.message(errorMessage).asLeft[Int]
    val ioA      = Fx[F].fromEither(expected)

    run(ioA, expected)
  }

  def testFromOptionSomeCase[F[*]: Fx](run: (F[Int], Either[Throwable, Int]) => Result): Property = for {
    n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected = n.asRight[SomeThrowableError]
    val input    = n.some
    val ioA      =
      Fx[F].fromOption(input)(SomeThrowableError.Message("This should never happen!"))

    run(ioA, expected)
  }

  def testFromOptionNoneCase[F[*]: Fx](run: (F[Int], Either[Throwable, Int]) => Result): Property = for {
    errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected = SomeThrowableError.message(errorMessage).asLeft[Int]
    val ioA      = Fx[F].fromOption(none[Int])(SomeThrowableError.Message(errorMessage))

    run(ioA, expected)
  }

  def testFromTrySuccessCase[F[*]: Fx](run: (F[Int], Either[Throwable, Int]) => Result): Property = for {
    n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected        = n.asRight[SomeThrowableError]
    val input: Try[Int] = scala.util.Success(n)
    val ioA             = Fx[F].fromTry(input)

    run(ioA, expected)
  }

  def testFromTryFailureCase[F[*]: Fx](run: (F[Int], Either[Throwable, Int]) => Result): Property = for {
    errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected        = SomeThrowableError.message(errorMessage).asLeft[Int]
    val input: Try[Int] = scala.util.Failure(SomeThrowableError.message(errorMessage))
    val ioA             = Fx[F].fromTry(input)

    run(ioA, expected)
  }

  def testFlatMapFx[F[*]: Fx](run: (F[String], String) => Result): Property =
    for {
      n      <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
      prefix <- Gen.constant("n is ").log("prefix")
    } yield {
      val expected = prefix + n.toString
      val fa       = Fx[F].pureOf(n)
      val fb       = Fx[F].flatMapFa(fa)(n => Fx[F].pureOf(prefix + n.toString))
      run(fb, expected)
    }

}
