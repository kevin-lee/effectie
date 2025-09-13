package effectie.specs.fxCtorSpec

import cats.syntax.all._
import effectie.core.FxCtor
import effectie.testing.RandomGens
import effectie.testing.types.SomeThrowableError
import munit.Assertions

import scala.util.Try

/** @author Kevin Lee
  * @since 2022-04-20
  */
object FxCtorSpecs4Js {

  def testEffectOf[F[*]: FxCtor](run: (F[Unit], Array[Int], Int, String) => F[Unit]): F[Unit] = {
    val before = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)
    val after  = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue) + before

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val actual = Array(before)
    Assertions.assertEquals(actual(0), before, "before effectOf")

    val io = FxCtor[F].effectOf({ actual(0) = after; () })
    Assertions.assertEquals(actual(0), before, "after effectOf but before run")

    run(io, actual, after, "after effectOf and run")
  }

  def testFromEffect[F[*]: FxCtor](run: (F[Unit], Array[Int], Int, String) => F[Unit]): F[Unit] = {
    val before = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)
    val after  = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue) + before

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val actual = Array(before)
    Assertions.assertEquals(actual(0), before, "before effectOf")

    val io = FxCtor[F].effectOf({ actual(0) = after; () })
    Assertions.assertEquals(actual(0), before, "after effectOf but before fromEffect")

    val fromIo = FxCtor[F].fromEffect(io)
    Assertions.assertEquals(actual(0), before, "after fromEffect but before run")

    run(fromIo, actual, after, "after fromEffect and run")
  }

  def testFromEffectWithPure[F[*]: FxCtor](run: (F[Unit], Array[Int], Int, String) => F[Unit]): F[Unit] = {
    val before = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)
    val after  = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue) + before

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val actual = Array(before)
    Assertions.assertEquals(actual(0), before, "before fromEffect")

    val fromPure = FxCtor[F].fromEffect(FxCtor[F].pureOf({ actual(0) = after; () }))
    Assertions.assertEquals(actual(0), before, "after fromEffect but before run")

    run(fromPure, actual, after, "after fromEffect and run")
  }

  def testPureOf[F[*]: FxCtor](run: (F[Unit], Int, Int, String) => F[Unit]): F[Unit] = {
    val before = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)
    val after  = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue) + before

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var actual = before // scalafix:ok DisableSyntax.var
    Assertions.assertEquals(actual, before, "before pureOrError")

    val io = FxCtor[F].pureOf({ actual = after; () })
    Assertions.assertEquals(actual, after, "after pureOrError but before run")

    run(io, actual, after, "after pureOrError and run")
  }

  def testPureOrErrorSuccessCase[F[*]: FxCtor](run: (F[Unit], Int, Int, String) => F[Unit]): F[Unit] = {
    val before = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)
    val after  = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue) + before

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var actual = before // scalafix:ok DisableSyntax.var
    Assertions.assertEquals(actual, before, "before pureOrError")

    val io = FxCtor[F].pureOrError({ actual = after; () })
    Assertions.assertEquals(actual, after, "after pureOrError but before run")

    run(io, actual, after, "after pureOrError and run")
  }

  def testPureOrErrorErrorCase[F[*]: FxCtor](run: (F[Unit], Throwable) => F[Unit]): F[Unit] = {
    val expectedMessage = "This is a throwable test error."
    val expectedError   = SomeThrowableError.message(expectedMessage)

    val fa = FxCtor[F].pureOrError((throw expectedError): Unit) // scalafix:ok DisableSyntax.throw

    run(fa, expectedError)
  }

  def testUnitOf[F[*]: FxCtor](run: F[Unit] => F[Unit]): F[Unit] = {
    val io = FxCtor[F].unitOf
    run(io)
  }

  def testErrorOf[F[*]: FxCtor](run: (F[Unit], Throwable) => F[Unit]): F[Unit] = {
    val expectedMessage = "This is a throwable test error."
    val expectedError   = SomeThrowableError.message(expectedMessage)

    val fa = FxCtor[F].errorOf[Unit](expectedError)

    run(fa, expectedError)
  }

  def testFromEitherRightCase[F[*]: FxCtor](run: (F[Int], Either[Throwable, Int]) => F[Unit]): F[Unit] = {
    val n = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected = n.asRight[SomeThrowableError]
    val input    = expected
    val fa       = FxCtor[F].fromEither(input)
    run(fa, expected)
  }

  def testFromEitherLeftCase[F[*]: FxCtor](run: (F[Int], Either[Throwable, Int]) => F[Unit]): F[Unit] = {
    val errorMessage = RandomGens.genAlphaString(10)

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected = SomeThrowableError.message(errorMessage).asLeft[Int]
    val fa       = FxCtor[F].fromEither(expected)

    run(fa, expected)
  }

  def testFromOptionSomeCase[F[*]: FxCtor](run: (F[Int], Either[Throwable, Int]) => F[Unit]): F[Unit] = {
    // TODO: Start again from here
    val n = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected = n.asRight[SomeThrowableError]
    val input    = n.some
    val fa       =
      FxCtor[F].fromOption(input)(SomeThrowableError.Message("This should never happen!"))

    run(fa, expected)
  }

  def testFromOptionNoneCase[F[*]: FxCtor](run: (F[Int], Either[Throwable, Int]) => F[Unit]): F[Unit] = {
    val errorMessage = RandomGens.genAlphaString(10)

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected = SomeThrowableError.message(errorMessage).asLeft[Int]
    val fa       = FxCtor[F].fromOption(none[Int])(SomeThrowableError.Message(errorMessage))

    run(fa, expected)
  }

  def testFromTrySuccessCase[F[*]: FxCtor](run: (F[Int], Either[Throwable, Int]) => F[Unit]): F[Unit] = {
    val n = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected        = n.asRight[Throwable]
    val input: Try[Int] = scala.util.Success(n)
    val fa              = FxCtor[F].fromTry(input)

    run(fa, expected)
  }

  def testFromTryFailureCase[F[*]: FxCtor](run: (F[Int], Either[Throwable, Int]) => F[Unit]): F[Unit] = {
    val errorMessage = RandomGens.genAlphaString(10)

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected        = SomeThrowableError.message(errorMessage).asLeft[Int]
    val input: Try[Int] = scala.util.Failure(SomeThrowableError.message(errorMessage))
    val fa              = FxCtor[F].fromTry(input)

    run(
      FxCtor[F].flatMapFa(fa)(actual =>
        Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      ),
      expected,
    )
  }

  def testFlatMapFx[F[*]: FxCtor](run: (F[String], String) => F[Unit]): F[Unit] = {
    val n      = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)
    val prefix = "n is "

    val expected = prefix + n.toString
    val fa       = FxCtor[F].pureOf(n)
    val fb       = FxCtor[F].flatMapFa(fa)(n => FxCtor[F].pureOf(prefix + n.toString))
    run(fb, expected)
  }

}
