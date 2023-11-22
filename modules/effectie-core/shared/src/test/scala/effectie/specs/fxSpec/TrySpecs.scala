package effectie.specs.fxSpec

import cats.syntax.all._
import effectie.core.Fx
import effectie.testing.types.SomeThrowableError
import hedgehog._
import hedgehog.runner._

import scala.util.{Failure, Success, Try}

/** @author Kevin Lee
  * @since 2022-04-20
  */
object TrySpecs {

  def trySpecs(implicit tryFx: Fx[Try]): List[Test] = List(
    property("test Fx[Try].effectOf", TrySpecs.testEffectOf),
    property("test Fx[Try].fromEffect(effectOf)", TrySpecs.testFromEffect),
    property("test Fx[Try].fromEffect(pureOf)", TrySpecs.testFromEffectWithPure),
    property("test Fx[Try].pureOf", TrySpecs.testPureOf),
    property("test Fx[Try].pureOrError(success case)", TrySpecs.testPureOrErrorSuccessCase),
    example("test Fx[Try].pureOrError(error case)", TrySpecs.testPureOrErrorErrorCase),
    example("test Fx[Try].unitOf", TrySpecs.testUnitOf),
    example("test Fx[Try].errorOf", TrySpecs.testErrorOf),
    property("test Fx[Try].fromEither(Right)", TrySpecs.testFromEitherRightCase),
    property("test Fx[Try].fromEither(Left)", TrySpecs.testFromEitherLeftCase),
    property("test Fx[Try].fromOption(Some)", TrySpecs.testFromOptionSomeCase),
    property("test Fx[Try].fromOption(None)", TrySpecs.testFromOptionNoneCase),
    property("test Fx[Try].fromTry(Success)", TrySpecs.testFromTrySuccessCase),
    property("test Fx[Try].fromTry(Failure)", TrySpecs.testFromTryFailureCase),
    property("test Fx[Try].flatMapFa(Try[A])(A => Try[B])", TrySpecs.testFlatMapFx),
  )

  private def testEffectOf(implicit tryFx: Fx[Try]): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {

    var actual       = before // scalafix:ok DisableSyntax.var
    val testBefore   = actual ==== before
    val _            = tryFx.effectOf({ actual = after; () })
    val testAfterRun = actual ==== after
    Result.all(
      List(
        testBefore.log("testBefore"),
        testAfterRun.log("testAfterRun"),
      )
    )
  }

  private def testFromEffect(implicit tryFx: Fx[Try]): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {

    var actual       = before // scalafix:ok DisableSyntax.var
    val testBefore   = (actual ==== before).log("before effectOf")
    val _            = tryFx.fromEffect(tryFx.effectOf({ actual = after; () }))
    val testAfterRun = (actual ==== after).log("after fromEffect")
    Result.all(
      List(
        testBefore.log("testBefore"),
        testAfterRun.log("testAfterRun"),
      )
    )
  }

  private def testFromEffectWithPure(implicit tryFx: Fx[Try]): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {

    var actual       = before // scalafix:ok DisableSyntax.var
    val testBefore   = (actual ==== before).log("before effectOf")
    val _            = tryFx.fromEffect(tryFx.pureOf({ actual = after; () }))
    val testAfterRun = (actual ==== after).log("after fromEffect")
    Result.all(
      List(
        testBefore.log("testBefore"),
        testAfterRun.log("testAfterRun"),
      )
    )
  }

  private def testPureOf(implicit tryFx: Fx[Try]): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {

    var actual       = before // scalafix:ok DisableSyntax.var
    val testBefore   = actual ==== before
    val _            = tryFx.pureOf({ actual = after; () })
    val testAfterRun = actual ==== after
    Result.all(
      List(
        testBefore.log("testBefore"),
        testAfterRun.log("testAfterRun"),
      )
    )
  }

  private def testPureOrErrorSuccessCase(implicit tryFx: Fx[Try]): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {

    var actual       = before // scalafix:ok DisableSyntax.var
    val testBefore   = actual ==== before
    val _            = tryFx.pureOrError({ actual = after; () })
    val testAfterRun = actual ==== after
    Result.all(
      List(
        testBefore.log("testBefore"),
        testAfterRun.log("testAfterRun"),
      )
    )
  }

  private def testPureOrErrorErrorCase(implicit tryFx: Fx[Try]): Result = {
    val expectedMessage = "This is a throwable test error."
    val expectedError   = SomeThrowableError.message(expectedMessage)

    val fa = tryFx.pureOrError[Unit](throw expectedError) // scalafix:ok DisableSyntax.throw
    fa ==== Failure(expectedError)
  }

  private def testUnitOf(implicit tryFx: Fx[Try]): Result = {
    val fa = tryFx.unitOf
    fa ==== Success(())
  }

  private def testErrorOf(implicit tryFx: Fx[Try]): Result = {
    val expectedMessage = "This is a throwable test error."
    val expectedError   = SomeThrowableError.message(expectedMessage)

    val fa = tryFx.errorOf[Unit](expectedError)
    fa ==== Failure(expectedError)
  }

  private def testFromEitherRightCase(implicit tryFx: Fx[Try]): Property = for {
    n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
  } yield {
    val expected = n

    val input = n.asRight[SomeThrowableError]
    val fa    = tryFx.fromEither(input)

    fa ==== Success(expected)
  }

  private def testFromEitherLeftCase(implicit tryFx: Fx[Try]): Property = for {
    errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
  } yield {

    val expected = SomeThrowableError.message(errorMessage)

    val input = expected.asLeft[Int]
    val fa    = tryFx.fromEither(input)

    fa ==== Failure(expected)
  }

  private def testFromOptionSomeCase(implicit tryFx: Fx[Try]): Property = for {
    n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
  } yield {

    val expected = n
    val input    = n.some

    val fa =
      tryFx.fromOption(input)(SomeThrowableError.Message("This should never happen!"))

    fa ==== Success(expected)
  }

  private def testFromOptionNoneCase(implicit tryFx: Fx[Try]): Property = for {
    errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
  } yield {

    val expected = SomeThrowableError.message(errorMessage)

    val input = expected

    val fa = tryFx.fromOption(none[Int])(input)

    fa ==== Failure(expected)
  }

  private def testFromTrySuccessCase(implicit tryFx: Fx[Try]): Property = for {
    n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
  } yield {

    val expected: Try[Int] = Success(n)

    val input = expected
    val fa    = tryFx.fromTry(input)

    fa ==== expected
  }

  private def testFromTryFailureCase(implicit tryFx: Fx[Try]): Property = for {
    errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
  } yield {

    val expected: Try[Int] = Failure(SomeThrowableError.message(errorMessage))

    val input = expected
    val fa    = tryFx.fromTry(input)

    fa ==== expected
  }

  private def testFlatMapFx(implicit tryFx: Fx[Try]): Property =
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
