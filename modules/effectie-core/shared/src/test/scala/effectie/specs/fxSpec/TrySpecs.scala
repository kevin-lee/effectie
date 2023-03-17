package effectie.specs.fxSpec

import cats.syntax.all._
import effectie.core.Fx
import effectie.testing.types.SomeThrowableError
import hedgehog._

import scala.util.{Failure, Success, Try}

/** @author Kevin Lee
  * @since 2022-04-20
  */
object TrySpecs {

  def testEffectOf(implicit tryFx: Fx[Try]): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {

    var actual       = before // scalafix:ok DisableSyntax.var
    val testBefore   = actual ==== before
    val fa           = tryFx.effectOf({ actual = after; () })
    val testAfterRun = actual ==== after
    Result.all(
      List(
        testBefore.log("testBefore"),
        testAfterRun.log("testAfterRun"),
      )
    )
  }

  def testPureOf(implicit tryFx: Fx[Try]): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {

    var actual       = before // scalafix:ok DisableSyntax.var
    val testBefore   = actual ==== before
    val fa           = tryFx.pureOf({ actual = after; () })
    val testAfterRun = actual ==== after
    Result.all(
      List(
        testBefore.log("testBefore"),
        testAfterRun.log("testAfterRun"),
      )
    )
  }

  def testPureOrErrorSuccessCase(implicit tryFx: Fx[Try]): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {

    var actual       = before // scalafix:ok DisableSyntax.var
    val testBefore   = actual ==== before
    val fa           = tryFx.pureOrError({ actual = after; () })
    val testAfterRun = actual ==== after
    Result.all(
      List(
        testBefore.log("testBefore"),
        testAfterRun.log("testAfterRun"),
      )
    )
  }

  def testPureOrErrorErrorCase(implicit tryFx: Fx[Try]): Result = {
    val expectedMessage = "This is a throwable test error."
    val expectedError   = SomeThrowableError.message(expectedMessage)

    val fa = tryFx.pureOrError[Unit](throw expectedError) // scalafix:ok DisableSyntax.throw
    fa ==== Failure(expectedError)
  }

  def testUnitOf(implicit tryFx: Fx[Try]): Result = {
    val fa = tryFx.unitOf
    fa ==== Success(())
  }

  def testErrorOf(implicit tryFx: Fx[Try]): Result = {
    val expectedMessage = "This is a throwable test error."
    val expectedError   = SomeThrowableError.message(expectedMessage)

    val fa = tryFx.errorOf[Unit](expectedError)
    fa ==== Failure(expectedError)
  }

  def testFromEitherRightCase(implicit tryFx: Fx[Try]): Property = for {
    n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
  } yield {
    val expected = n

    val input = n.asRight[SomeThrowableError]
    val fa    = tryFx.fromEither(input)

    fa ==== Success(expected)
  }

  def testFromEitherLeftCase(implicit tryFx: Fx[Try]): Property = for {
    errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
  } yield {

    val expected = SomeThrowableError.message(errorMessage)

    val input = expected.asLeft[Int]
    val fa    = tryFx.fromEither(input)

    fa ==== Failure(expected)
  }

  def testFromOptionSomeCase(implicit tryFx: Fx[Try]): Property = for {
    n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
  } yield {

    val expected = n
    val input    = n.some

    val fa =
      tryFx.fromOption(input)(SomeThrowableError.Message("This should never happen!"))

    fa ==== Success(expected)
  }

  def testFromOptionNoneCase(implicit tryFx: Fx[Try]): Property = for {
    errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
  } yield {

    val expected = SomeThrowableError.message(errorMessage)

    val input = expected

    val fa = tryFx.fromOption(none[Int])(input)

    fa ==== Failure(expected)
  }

  def testFromTrySuccessCase(implicit tryFx: Fx[Try]): Property = for {
    n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
  } yield {

    val expected: Try[Int] = Success(n)

    val input = expected
    val fa    = tryFx.fromTry(input)

    fa ==== expected
  }

  def testFromTryFailureCase(implicit tryFx: Fx[Try]): Property = for {
    errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
  } yield {

    val expected: Try[Int] = Failure(SomeThrowableError.message(errorMessage))

    val input = expected
    val fa    = tryFx.fromTry(input)

    fa ==== expected
  }

  def testFlatMapFx(implicit tryFx: Fx[Try]): Property =
    for {
      n      <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
      prefix <- Gen.constant("n is ").log("prefix")
    } yield {
      val expected: Try[String] = Success(prefix + n.toString)

      val fa = tryFx.pureOf(n)
      val fb = tryFx.flatMapFa(fa)(n => tryFx.pureOf(prefix + n.toString))

      fb ==== expected
    }

}
