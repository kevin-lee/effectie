package effectie.specs.fxCtorSpec

import cats.Id
import cats.syntax.all._
import effectie.core.FxCtor
import effectie.testing.tools
import effectie.testing.types.SomeThrowableError
import hedgehog._
import hedgehog.runner._

import scala.util.Try

/** @author Kevin Lee
  * @since 2022-04-20
  */
object IdSpecs {

  def idSpecs(implicit idFxCtor: FxCtor[Id]): List[Test] = {
    List(
      property("test FxCtor[Id].effectOf", IdSpecs.testEffectOf),
      property("test FxCtor[Id].fromEffect(effectOf)", IdSpecs.testFromEffect),
      property("test FxCtor[Id].fromEffect(pureOf)", IdSpecs.testFromEffectWithPure),
      property("test FxCtor[Id].pureOf", IdSpecs.testPureOf),
      property("test FxCtor[Id].pureOrError(success case)", IdSpecs.testPureOrErrorSuccessCase),
      example("test FxCtor[Id].pureOrError(error case)", IdSpecs.testPureOrErrorErrorCase),
      example("test FxCtor[Id].unitOf", IdSpecs.testUnitOf),
      example("test FxCtor[Id].errorOf", IdSpecs.testErrorOf),
      property("test FxCtor[Id].fromEither(Right)", IdSpecs.testFromEitherRightCase),
      property("test FxCtor[Id].fromEither(Left)", IdSpecs.testFromEitherLeftCase),
      property("test FxCtor[Id].fromOption(Some)", IdSpecs.testFromOptionSomeCase),
      property("test FxCtor[Id].fromOption(None)", IdSpecs.testFromOptionNoneCase),
      property("test FxCtor[Id].fromTry(Success)", IdSpecs.testFromTrySuccessCase),
      property("test FxCtor[Id].fromTry(Failure)", IdSpecs.testFromTryFailureCase),
      property("test FxCtor[Id].testFlatMapFa(Id[A])(A => Id[B])", IdSpecs.testFlatMapFa),
    )
  }

  private def testEffectOf(implicit idFxCtor: FxCtor[Id]): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var actual     = before // scalafix:ok DisableSyntax.var
    val testBefore = actual ==== before
    idFxCtor.effectOf({ actual = after; () })
    val testAfter  = actual ==== after
    testBefore.log("testBefore") ==== testAfter.log("testAfter")
  }

  private def testFromEffect(implicit idFxCtor: FxCtor[Id]): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var actual     = before // scalafix:ok DisableSyntax.var
    val testBefore = actual ==== before
    idFxCtor.fromEffect(idFxCtor.effectOf({ actual = after; () }))
    val testAfter  = actual ==== after
    testBefore.log("testBefore") ==== testAfter.log("testAfter")
  }

  private def testFromEffectWithPure(implicit idFxCtor: FxCtor[Id]): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var actual     = before // scalafix:ok DisableSyntax.var
    val testBefore = actual ==== before
    idFxCtor.fromEffect(idFxCtor.pureOf({ actual = after; () }))
    val testAfter  = actual ==== after
    testBefore.log("testBefore") ==== testAfter.log("testAfter")
  }

  private def testPureOf(implicit idFxCtor: FxCtor[Id]): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var actual     = before // scalafix:ok DisableSyntax.var
    val testBefore = actual ==== before
    idFxCtor.pureOf({ actual = after; () })
    val testAfter  = actual ==== after
    Result.all(
      List(
        testBefore.log("testBefore"),
        testAfter.log("testAfter"),
      )
    )
  }

  private def testPureOrErrorSuccessCase(implicit idFxCtor: FxCtor[Id]): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var actual     = before // scalafix:ok DisableSyntax.var
    val testBefore = actual ==== before
    idFxCtor.pureOrError({ actual = after; () })
    val testAfter  = actual ==== after
    Result.all(
      List(
        testBefore.log("testBefore"),
        testAfter.log("testAfter"),
      )
    )
  }

  private def testPureOrErrorErrorCase(implicit idFxCtor: FxCtor[Id]): Result = {
    val expectedMessage = "This is a throwable test error."
    val expectedError   = SomeThrowableError.message(expectedMessage)

    lazy val actual = idFxCtor.pureOrError[Unit](throw expectedError) // scalafix:ok DisableSyntax.throw
    tools.expectThrowable(actual, expectedError)
  }

  private def testUnitOf(implicit idFxCtor: FxCtor[Id]): Result = {
    val expected: Unit = ()
    val actual         = idFxCtor.unitOf
    actual ==== expected
  }

  private def testErrorOf(implicit idFxCtor: FxCtor[Id]): Result = {
    val expectedMessage = "This is a throwable test error."
    val expectedError   = SomeThrowableError.message(expectedMessage)

    lazy val actual = idFxCtor.errorOf[Unit](expectedError)
    tools.expectThrowable(actual, expectedError)
  }

  private def testFromEitherRightCase(implicit idFxCtor: FxCtor[Id]): Property = for {
    n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
  } yield {

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected = n.asRight[SomeThrowableError]
    lazy val ioA = idFxCtor.fromEither(expected)

    val actual = Try(ioA).toEither
    (actual ==== expected).log(s"$actual does not equal to $expected")
  }

  private def testFromEitherLeftCase(implicit idFxCtor: FxCtor[Id]): Property = for {
    errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected = SomeThrowableError.message(errorMessage).asLeft[Int]
    lazy val ioA = idFxCtor.fromEither(expected)

    val actual = Try(ioA).toEither
    (actual ==== expected).log(s"$actual does not equal to $expected")
  }

  private def testFromOptionSomeCase(implicit idFxCtor: FxCtor[Id]): Property = for {
    n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected = n.asRight[SomeThrowableError]
    val input    = n.some
    lazy val ioA =
      idFxCtor.fromOption(input)(SomeThrowableError.Message("This should never happen!"))

    val actual = Try(ioA).toEither
    (actual ==== expected).log(s"$actual does not equal to $expected")
  }

  private def testFromOptionNoneCase(implicit idFxCtor: FxCtor[Id]): Property = for {
    errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected = SomeThrowableError.message(errorMessage).asLeft[Int]
    lazy val ioA = idFxCtor.fromOption(none[Int])(SomeThrowableError.Message(errorMessage))

    val actual = Try(ioA).toEither
    (actual ==== expected).log(s"$actual does not equal to $expected")
  }

  private def testFromTrySuccessCase(implicit idFxCtor: FxCtor[Id]): Property = for {
    n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected        = n.asRight[SomeThrowableError]
    val input: Try[Int] = scala.util.Success(n)
    lazy val ioA        = idFxCtor.fromTry(input)

    val actual = Try(ioA).toEither
    (actual ==== expected).log(s"$actual does not equal to $expected")
  }

  private def testFromTryFailureCase(implicit idFxCtor: FxCtor[Id]): Property = for {
    errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected        = SomeThrowableError.message(errorMessage).asLeft[Int]
    val input: Try[Int] = scala.util.Failure(SomeThrowableError.message(errorMessage))
    lazy val ioA        = idFxCtor.fromTry(input)

    val actual = Try(ioA).toEither
    (actual ==== expected).log(s"$actual does not equal to $expected")
  }

  private def testFlatMapFa(implicit idFxCtor: FxCtor[Id]): Property =
    for {
      n      <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
      prefix <- Gen.constant("n is ").log("prefix")
    } yield {
      val expected = prefix + n.toString
      val fa       = idFxCtor.pureOf(n)
      val actual   = idFxCtor.flatMapFa(fa)(n => idFxCtor.pureOf(prefix + n.toString))
      actual ==== expected
    }
}
