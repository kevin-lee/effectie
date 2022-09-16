package effectie.specs.fxCtorSpec

import cats.Id
import cats.syntax.all._
import effectie.core.FxCtor
import effectie.testing.tools
import effectie.testing.types.SomeThrowableError
import hedgehog._

import scala.util.Try

/** @author Kevin Lee
  * @since 2022-04-20
  */
object IdSpecs {

  def testEffectOf(implicit idFxCtor: FxCtor[Id]): Property = for {
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

  def testPureOf(implicit idFxCtor: FxCtor[Id]): Property = for {
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
        testAfter.log("testAfter")
      )
    )
  }

  def testPureOrErrorSuccessCase(implicit idFxCtor: FxCtor[Id]): Property = for {
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
        testAfter.log("testAfter")
      )
    )
  }

  def testPureOrErrorErrorCase(implicit idFxCtor: FxCtor[Id]): Result = {
    val expectedMessage = "This is a throwable test error."
    val expectedError   = SomeThrowableError.message(expectedMessage)

    lazy val actual = idFxCtor.pureOrError[Unit](throw expectedError) // scalafix:ok DisableSyntax.throw
    tools.expectThrowable(actual, expectedError)
  }

  def testUnitOf(implicit idFxCtor: FxCtor[Id]): Result = {
    val expected: Unit = ()
    val actual         = idFxCtor.unitOf
    actual ==== expected
  }

  def testErrorOf(implicit idFxCtor: FxCtor[Id]): Result = {
    val expectedMessage = "This is a throwable test error."
    val expectedError   = SomeThrowableError.message(expectedMessage)

    lazy val actual = idFxCtor.errorOf[Unit](expectedError)
    tools.expectThrowable(actual, expectedError)
  }

  def testFromEitherRightCase(implicit idFxCtor: FxCtor[Id]): Property = for {
    n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
  } yield {

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected = n.asRight[SomeThrowableError]
    lazy val ioA = idFxCtor.fromEither(expected)

    val actual = Try(ioA).toEither
    (actual ==== expected).log(s"$actual does not equal to $expected")
  }

  def testFromEitherLeftCase(implicit idFxCtor: FxCtor[Id]): Property = for {
    errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected = SomeThrowableError.message(errorMessage).asLeft[Int]
    lazy val ioA = idFxCtor.fromEither(expected)

    val actual = Try(ioA).toEither
    (actual ==== expected).log(s"$actual does not equal to $expected")
  }

  def testFromOptionSomeCase(implicit idFxCtor: FxCtor[Id]): Property = for {
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

  def testFromOptionNoneCase(implicit idFxCtor: FxCtor[Id]): Property = for {
    errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected = SomeThrowableError.message(errorMessage).asLeft[Int]
    lazy val ioA = idFxCtor.fromOption(none[Int])(SomeThrowableError.Message(errorMessage))

    val actual = Try(ioA).toEither
    (actual ==== expected).log(s"$actual does not equal to $expected")
  }

  def testFromTrySuccessCase(implicit idFxCtor: FxCtor[Id]): Property = for {
    n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected        = n.asRight[SomeThrowableError]
    val input: Try[Int] = scala.util.Success(n)
    lazy val ioA        = idFxCtor.fromTry(input)

    val actual = Try(ioA).toEither
    (actual ==== expected).log(s"$actual does not equal to $expected")
  }

  def testFromTryFailureCase(implicit idFxCtor: FxCtor[Id]): Property = for {
    errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
  } yield {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected        = SomeThrowableError.message(errorMessage).asLeft[Int]
    val input: Try[Int] = scala.util.Failure(SomeThrowableError.message(errorMessage))
    lazy val ioA        = idFxCtor.fromTry(input)

    val actual = Try(ioA).toEither
    (actual ==== expected).log(s"$actual does not equal to $expected")
  }

}
