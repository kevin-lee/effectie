package effectie.specs.fxCtorSpec

import cats.syntax.all._
import effectie.core.FxCtor
import effectie.testing.types.SomeThrowableError
import hedgehog._

import scala.util.{Failure, Success, Try}

/** @author Kevin Lee
  * @since 2022-04-20
  */
object TrySpecs {

  def testEffectOf(implicit tryFxCtor: FxCtor[Try]): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {

    var actual       = before // scalafix:ok DisableSyntax.var
    val testBefore   = (actual ==== before).log("before pureOrError")
    val fa           = tryFxCtor.effectOf({ actual = after; () })
    val testAfterRun = (actual ==== after).log("after pureOrError and run")
    Result.all(
      List(
        testBefore.log("testBefore"),
        testAfterRun.log("testAfterRun"),
      )
    )
  }

  def testPureOf(implicit tryFxCtor: FxCtor[Try]): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {

    var actual       = before // scalafix:ok DisableSyntax.var
    val testBefore   = (actual ==== before).log("before pureOrError")
    val fa           = tryFxCtor.pureOf({ actual = after; () })
    val testAfterRun = actual ==== after
    Result.all(
      List(
        testBefore.log("testBefore"),
        testAfterRun.log("testAfterRun"),
      )
    )
  }

  def testPureOrErrorSuccessCase(implicit tryFxCtor: FxCtor[Try]): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {

    var actual       = before // scalafix:ok DisableSyntax.var
    val testBefore   = (actual ==== before).log("before pureOrError")
    val fa           = tryFxCtor.pureOrError({ actual = after; () })
    val testAfterRun = (actual ==== after).log("after pureOrError and run")
    Result.all(
      List(
        testBefore.log("testBefore"),
        testAfterRun.log("testAfterRun"),
      )
    )
  }

  def testPureOrErrorErrorCase(implicit tryFxCtor: FxCtor[Try]): Result = {
    val expectedMessage = "This is a throwable test error."
    val expectedError   = SomeThrowableError.message(expectedMessage)

    val fa = tryFxCtor.pureOrError((throw expectedError): Unit) // scalafix:ok DisableSyntax.throw
    fa ==== Failure(expectedError)
  }

  def testUnitOf(implicit tryFxCtor: FxCtor[Try]): Result = {
    val fa = tryFxCtor.unitOf
    fa ==== Success(())
  }

  def testErrorOf(implicit tryFxCtor: FxCtor[Try]): Result = {
    val expectedMessage = "This is a throwable test error."
    val expectedError   = SomeThrowableError.message(expectedMessage)

    val fa = tryFxCtor.errorOf[Unit](expectedError)
    fa ==== Failure(expectedError)
  }

  def testFromEitherRightCase(implicit tryFxCtor: FxCtor[Try]): Property = for {
    n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
  } yield {

    val expected = n
    val input    = n.asRight[SomeThrowableError]
    val fa       = tryFxCtor.fromEither(input)

    fa ==== Success(expected)
  }

  def testFromEitherLeftCase(implicit tryFxCtor: FxCtor[Try]): Property = for {
    errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
  } yield {

    val expected = SomeThrowableError.message(errorMessage)
    val input    = expected.asLeft[Int]
    val fa       = tryFxCtor.fromEither(input)

    fa ==== Failure(expected)
  }

  def testFromOptionSomeCase(implicit tryFxCtor: FxCtor[Try]): Property = for {
    n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
  } yield {

    val expected = n
    val input    = n.some
    val fa       =
      tryFxCtor.fromOption(input)(SomeThrowableError.Message("This should never happen!"))

    fa ==== Success(expected)
  }

  def testFromOptionNoneCase(implicit tryFxCtor: FxCtor[Try]): Property = for {
    errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
  } yield {

    val expected = SomeThrowableError.message(errorMessage)
    val input    = expected
    val fa       = tryFxCtor.fromOption(none[Int])(input)

    fa ==== Failure(expected)
  }

  def testFromTrySuccessCase(implicit tryFxCtor: FxCtor[Try]): Property = for {
    n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
  } yield {

    val expected: Try[Int] = Success(n)

    val input = expected
    val fa    = tryFxCtor.fromTry(input)

    fa ==== expected
  }

  def testFromTryFailureCase(implicit tryFxCtor: FxCtor[Try]): Property = for {
    errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
  } yield {

    val expected: Try[Int] = Failure(SomeThrowableError.message(errorMessage))

    val input = expected
    val fa    = tryFxCtor.fromTry(input)

    fa ==== expected
  }

  def testFlatMapFx(implicit tryFxCtor: FxCtor[Try]): Property =
    for {
      n      <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
      prefix <- Gen.constant("n is ").log("prefix")
    } yield {
      val expected = prefix + n.toString

      val fa = tryFxCtor.pureOf(n)
      val fb = tryFxCtor.flatMapFa(fa)(n => tryFxCtor.pureOf(prefix + n.toString))

      fb ==== Success(expected)
    }

}
