package effectie.specs.fxCtorSpec

import cats.syntax.all._
import effectie.core.FxCtor
import effectie.testing.types.SomeThrowableError
import hedgehog._
import hedgehog.runner._

import scala.util.{Failure, Success, Try}

/** @author Kevin Lee
  * @since 2022-04-20
  */
object TrySpecs {

  def trySpecs(implicit tryFxCtor: FxCtor[Try]): List[Test] = List(
    property("test FxCtor[Try].effectOf", TrySpecs.testEffectOf),
    property("test FxCtor[Try].fromEffect(effectOf)", TrySpecs.testFromEffect),
    property("test FxCtor[Try].fromEffect(pureOf)", TrySpecs.testFromEffectWithPure),
    property("test FxCtor[Try].pureOf", TrySpecs.testPureOf),
    property("test FxCtor[Try].pureOrError(success case)", TrySpecs.testPureOrErrorSuccessCase),
    example("test FxCtor[Try].pureOrError(error case)", TrySpecs.testPureOrErrorErrorCase),
    example("test FxCtor[Try].unitOf", TrySpecs.testUnitOf),
    example("test FxCtor[Try].errorOf", TrySpecs.testErrorOf),
    property("test FxCtor[Try].fromEither(Right)", TrySpecs.testFromEitherRightCase),
    property("test FxCtor[Try].fromEither(Left)", TrySpecs.testFromEitherLeftCase),
    property("test FxCtor[Try].fromOption(Some)", TrySpecs.testFromOptionSomeCase),
    property("test FxCtor[Try].fromOption(None)", TrySpecs.testFromOptionNoneCase),
    property("test FxCtor[Try].fromTry(Success)", TrySpecs.testFromTrySuccessCase),
    property("test FxCtor[Try].fromTry(Failure)", TrySpecs.testFromTryFailureCase),
  )

  private def testEffectOf(implicit tryFxCtor: FxCtor[Try]): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {

    var actual       = before // scalafix:ok DisableSyntax.var
    val testBefore   = (actual ==== before).log("before effectOf")
    val fa           = tryFxCtor.effectOf({ actual = after; () })
    val testAfterRun = (actual ==== after).log("after effectOf and run")
    Result.all(
      List(
        testBefore.log("testBefore"),
        testAfterRun.log("testAfterRun"),
      )
    )
  }

  private def testFromEffect(implicit tryFxCtor: FxCtor[Try]): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {

    var actual       = before // scalafix:ok DisableSyntax.var
    val testBefore   = (actual ==== before).log("before effectOf")
    val afterFrom    = tryFxCtor.fromEffect(tryFxCtor.effectOf({ actual = after; () }))
    val testAfterRun = (actual ==== after).log("after fromEffect")
    Result.all(
      List(
        testBefore.log("testBefore"),
        testAfterRun.log("testAfterRun"),
      )
    )
  }

  private def testFromEffectWithPure(implicit tryFxCtor: FxCtor[Try]): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {

    var actual       = before // scalafix:ok DisableSyntax.var
    val testBefore   = (actual ==== before).log("before effectOf")
    val afterFrom    = tryFxCtor.fromEffect(tryFxCtor.pureOf({ actual = after; () }))
    val testAfterRun = (actual ==== after).log("after fromEffect")
    Result.all(
      List(
        testBefore.log("testBefore"),
        testAfterRun.log("testAfterRun"),
      )
    )
  }

  private def testPureOf(implicit tryFxCtor: FxCtor[Try]): Property = for {
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

  private def testPureOrErrorSuccessCase(implicit tryFxCtor: FxCtor[Try]): Property = for {
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

  private def testPureOrErrorErrorCase(implicit tryFxCtor: FxCtor[Try]): Result = {
    val expectedMessage = "This is a throwable test error."
    val expectedError   = SomeThrowableError.message(expectedMessage)

    val fa = tryFxCtor.pureOrError((throw expectedError): Unit) // scalafix:ok DisableSyntax.throw
    fa ==== Failure(expectedError)
  }

  private def testUnitOf(implicit tryFxCtor: FxCtor[Try]): Result = {
    val fa = tryFxCtor.unitOf
    fa ==== Success(())
  }

  private def testErrorOf(implicit tryFxCtor: FxCtor[Try]): Result = {
    val expectedMessage = "This is a throwable test error."
    val expectedError   = SomeThrowableError.message(expectedMessage)

    val fa = tryFxCtor.errorOf[Unit](expectedError)
    fa ==== Failure(expectedError)
  }

  private def testFromEitherRightCase(implicit tryFxCtor: FxCtor[Try]): Property = for {
    n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
  } yield {

    val expected = n
    val input    = n.asRight[SomeThrowableError]
    val fa       = tryFxCtor.fromEither(input)

    fa ==== Success(expected)
  }

  private def testFromEitherLeftCase(implicit tryFxCtor: FxCtor[Try]): Property = for {
    errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
  } yield {

    val expected = SomeThrowableError.message(errorMessage)
    val input    = expected.asLeft[Int]
    val fa       = tryFxCtor.fromEither(input)

    fa ==== Failure(expected)
  }

  private def testFromOptionSomeCase(implicit tryFxCtor: FxCtor[Try]): Property = for {
    n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
  } yield {

    val expected = n
    val input    = n.some
    val fa       =
      tryFxCtor.fromOption(input)(SomeThrowableError.Message("This should never happen!"))

    fa ==== Success(expected)
  }

  private def testFromOptionNoneCase(implicit tryFxCtor: FxCtor[Try]): Property = for {
    errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
  } yield {

    val expected = SomeThrowableError.message(errorMessage)
    val input    = expected
    val fa       = tryFxCtor.fromOption(none[Int])(input)

    fa ==== Failure(expected)
  }

  private def testFromTrySuccessCase(implicit tryFxCtor: FxCtor[Try]): Property = for {
    n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
  } yield {

    val expected: Try[Int] = Success(n)

    val input = expected
    val fa    = tryFxCtor.fromTry(input)

    fa ==== expected
  }

  private def testFromTryFailureCase(implicit tryFxCtor: FxCtor[Try]): Property = for {
    errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
  } yield {

    val expected: Try[Int] = Failure(SomeThrowableError.message(errorMessage))

    val input = expected
    val fa    = tryFxCtor.fromTry(input)

    fa ==== expected
  }

  private def testFlatMapFx(implicit tryFxCtor: FxCtor[Try]): Property =
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
