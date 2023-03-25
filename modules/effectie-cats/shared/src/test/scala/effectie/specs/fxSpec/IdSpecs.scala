package effectie.specs.fxSpec

import cats.Id
import cats.syntax.all._
import effectie.core.Fx
import effectie.testing.tools
import effectie.testing.types.SomeThrowableError
import hedgehog._

import hedgehog.runner._

import scala.util.Try

/** @author Kevin Lee
  * @since 2022-04-20
  */
object IdSpecs {

  def idSpecs(implicit idFx: Fx[Id]): List[Test] =
    List(
      property("test Fx[Id].effectOf", IdSpecs.testEffectOf),
      property("test Fx[Id].fromEffect(effectOf)", IdSpecs.testFromEffect),
      property("test Fx[Id].fromEffect(pureOf)", IdSpecs.testFromEffectWithPure),
      property("test Fx[Id].pureOf", IdSpecs.testPureOf),
      property("test Fx[Id].pureOrError(success case)", IdSpecs.testPureOrErrorSuccessCase),
      example("test Fx[Id].pureOrError(error case)", IdSpecs.testPureOrErrorErrorCase),
      example("test Fx[Id].unitOf", IdSpecs.testUnitOf),
      example("test Fx[Id].errorOf", IdSpecs.testErrorOf),
      property("test Fx[Id].fromEither(Right)", IdSpecs.testFromEitherRightCase),
      property("test Fx[Id].fromEither(Left)", IdSpecs.testFromEitherLeftCase),
      property("test Fx[Id].fromOption(Some)", IdSpecs.testFromOptionSomeCase),
      property("test Fx[Id].fromOption(None)", IdSpecs.testFromOptionNoneCase),
      property("test Fx[Id].fromTry(Success)", IdSpecs.testFromTrySuccessCase),
      property("test Fx[Id].fromTry(Failure)", IdSpecs.testFromTryFailureCase),
      property("test Fx[Id].testFlatMapFa(Id[A])(A => Id[B])", IdSpecs.testFlatMapFa),
    )

  private def testEffectOf(implicit idFx: Fx[Id]): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var actual     = before // scalafix:ok DisableSyntax.var
    val testBefore = actual ==== before
    idFx.effectOf({ actual = after; () })
    val testAfter  = actual ==== after
    testBefore.log("testBefore") ==== testAfter.log("testAfter")
  }

  private def testFromEffect(implicit idFx: Fx[Id]): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var actual     = before // scalafix:ok DisableSyntax.var
    val testBefore = actual ==== before
    idFx.fromEffect(idFx.effectOf({ actual = after; () }))
    val testAfter  = actual ==== after
    testBefore.log("testBefore") ==== testAfter.log("testAfter")
  }

  private def testFromEffectWithPure(implicit idFx: Fx[Id]): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var actual     = before // scalafix:ok DisableSyntax.var
    val testBefore = actual ==== before
    idFx.fromEffect(idFx.pureOf({ actual = after; () }))
    val testAfter  = actual ==== after
    testBefore.log("testBefore") ==== testAfter.log("testAfter")
  }

  private def testPureOf(implicit idFx: Fx[Id]): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var actual     = before // scalafix:ok DisableSyntax.var
    val testBefore = actual ==== before
    idFx.pureOf({ actual = after; () })
    val testAfter  = actual ==== after
    Result.all(
      List(
        testBefore.log("testBefore"),
        testAfter.log("testAfter"),
      )
    )
  }

  private def testPureOrErrorSuccessCase(implicit idFx: Fx[Id]): Property = for {
    before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
    after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var actual     = before // scalafix:ok DisableSyntax.var
    val testBefore = actual ==== before
    idFx.pureOrError({ actual = after; () })
    val testAfter  = actual ==== after
    Result.all(
      List(
        testBefore.log("testBefore"),
        testAfter.log("testAfter"),
      )
    )
  }

  private def testPureOrErrorErrorCase(implicit idFx: Fx[Id]): Result = {
    val expectedMessage = "This is a throwable test error."
    val expectedError   = SomeThrowableError.message(expectedMessage)

    lazy val actual = idFx.pureOrError[Unit](throw expectedError) // scalafix:ok DisableSyntax.throw
    tools.expectThrowable(actual, expectedError)
  }

  private def testUnitOf(implicit idFx: Fx[Id]): Result = {
    val expected: Unit = ()
    val actual         = idFx.unitOf
    actual ==== expected
  }

  private def testErrorOf(implicit idFx: Fx[Id]): Result = {
    val expectedMessage = "This is a throwable test error."
    val expectedError   = SomeThrowableError.message(expectedMessage)

    lazy val actual = idFx.errorOf[Unit](expectedError)
    tools.expectThrowable(actual, expectedError)
  }

  private def testFromEitherRightCase(implicit idFx: Fx[Id]): Property = for {
    n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
  } yield {

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected = n.asRight[SomeThrowableError]
    lazy val ioA = idFx.fromEither(expected)

    val actual = Try(ioA).toEither
    (actual ==== expected).log(s"$actual does not equal to $expected")
  }

  private def testFromEitherLeftCase(implicit idFx: Fx[Id]): Property = for {
    errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected = SomeThrowableError.message(errorMessage).asLeft[Int]
    lazy val ioA = idFx.fromEither(expected)

    val actual = Try(ioA).toEither
    (actual ==== expected).log(s"$actual does not equal to $expected")
  }

  private def testFromOptionSomeCase(implicit idFx: Fx[Id]): Property = for {
    n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected = n.asRight[SomeThrowableError]
    val input    = n.some
    lazy val ioA =
      idFx.fromOption(input)(SomeThrowableError.Message("This should never happen!"))

    val actual = Try(ioA).toEither
    (actual ==== expected).log(s"$actual does not equal to $expected")
  }

  private def testFromOptionNoneCase(implicit idFx: Fx[Id]): Property = for {
    errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected = SomeThrowableError.message(errorMessage).asLeft[Int]
    lazy val ioA = idFx.fromOption(none[Int])(SomeThrowableError.Message(errorMessage))

    val actual = Try(ioA).toEither
    (actual ==== expected).log(s"$actual does not equal to $expected")
  }

  private def testFromTrySuccessCase(implicit idFx: Fx[Id]): Property = for {
    n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected        = n.asRight[SomeThrowableError]
    val input: Try[Int] = scala.util.Success(n)
    lazy val ioA        = idFx.fromTry(input)

    val actual = Try(ioA).toEither
    (actual ==== expected).log(s"$actual does not equal to $expected")
  }

  private def testFromTryFailureCase(implicit idFx: Fx[Id]): Property = for {
    errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected        = SomeThrowableError.message(errorMessage).asLeft[Int]
    val input: Try[Int] = scala.util.Failure(SomeThrowableError.message(errorMessage))
    lazy val ioA        = idFx.fromTry(input)

    val actual = Try(ioA).toEither
    (actual ==== expected).log(s"$actual does not equal to $expected")
  }

  private def testFlatMapFa(implicit idFx: Fx[Id]): Property =
    for {
      n      <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
      prefix <- Gen.constant("n is ").log("prefix")
    } yield {
      val expected = prefix + n.toString
      val fa       = idFx.pureOf(n)
      val actual   = idFx.flatMapFa(fa)(n => idFx.pureOf(prefix + n.toString))
      actual ==== expected
    }

}
