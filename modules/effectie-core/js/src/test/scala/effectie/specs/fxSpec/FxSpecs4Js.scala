package effectie.specs.fxSpec

import cats.syntax.all._
import effectie.core.Fx
import effectie.testing.RandomGens
import effectie.testing.types.SomeThrowableError
import munit.Assertions

import scala.util.Try

/** @author Kevin Lee
  * @since 2022-04-20
  */
object FxSpecs4Js {

  def testEffectOf[F[*]: Fx](run: (F[Unit], Array[Int], Int, String) => F[Unit]): F[Unit] = {
    val before = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)
    val after  = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue) + before

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val actual = Array(before)
    Assertions.assertEquals(actual(0), before, "before effectOf")

    val io = Fx[F].effectOf({ actual(0) = after; () })
    Assertions.assertEquals(actual(0), before, "after effectOf but before run")

    run(io, actual, after, "after effectOf and run")
  }

  def testFromEffect[F[*]: Fx](run: (F[Unit], Array[Int], Int, String) => F[Unit]): F[Unit] = {
    val before = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)
    val after  = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue) + before

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val actual = Array(before)
    Assertions.assertEquals(actual(0), before, "before effectOf")

    val io = Fx[F].effectOf({ actual(0) = after; () })
    Assertions.assertEquals(actual(0), before, "after effectOf but before fromEffect")

    val fromIo = Fx[F].fromEffect(io)
    Assertions.assertEquals(actual(0), before, "after fromEffect but before run")

    run(fromIo, actual, after, "after fromEffect and run")
  }

  def testFromEffectWithPure[F[*]: Fx](run: (F[Unit], Array[Int], Int, String) => F[Unit]): F[Unit] = {
    val before = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)
    val after  = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue) + before

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val actual = Array(before)
    Assertions.assertEquals(actual(0), before, "before fromEffect")

    val fromPure = Fx[F].fromEffect(Fx[F].pureOf({ actual(0) = after; () }))
    Assertions.assertEquals(actual(0), before, "after fromEffect but before run")

    run(fromPure, actual, after, "after fromEffect and run")
  }

  def testPureOf[F[*]: Fx](run: (F[Unit], Array[Int], Int, String) => F[Unit]): F[Unit] = {
    val before = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)
    val after  = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue) + before

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val actual = Array(before)
    Assertions.assertEquals(actual(0), before, "before pureOf")

    val io = Fx[F].pureOf({ actual(0) = after; () })
    Assertions.assertEquals(actual(0), after, "after pureOf but before run")

    run(io, actual, after, "after pureOf and run")
  }

  def testPureOrErrorSuccessCase[F[*]: Fx](run: (F[Unit], Array[Int], Int, String) => F[Unit]): F[Unit] = {
    val before = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)
    val after  = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue) + before

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val actual = Array(before)
    Assertions.assertEquals(actual(0), before, "before pureOrError")

    val io = Fx[F].pureOrError({ actual(0) = after; () })
    Assertions.assertEquals(actual(0), after, "after pureOrError but before run")

    run(io, actual, after, "after pureOrError and run")
  }

  def testPureOrErrorErrorCase[F[*]: Fx](run: (F[Unit], Throwable) => F[Unit]): F[Unit] = {
    val expectedMessage = "This is a throwable test error."
    val expectedError   = SomeThrowableError.message(expectedMessage)

    val io = Fx[F].pureOrError[Unit](throw expectedError) // scalafix:ok DisableSyntax.throw
    run(io, expectedError)
  }

  def testUnitOf[F[*]: Fx](run: F[Unit] => F[Unit]): F[Unit] = {
    val io = Fx[F].unitOf
    run(io)
  }

  def testErrorOf[F[*]: Fx](run: (F[Unit], Throwable) => F[Unit]): F[Unit] = {
    val expectedMessage = "This is a throwable test error."
    val expectedError   = SomeThrowableError.message(expectedMessage)

    val io = Fx[F].errorOf[Unit](expectedError)
    run(io, expectedError)
  }

  def testFromEitherRightCase[F[*]: Fx](run: (F[Int], Either[Throwable, Int]) => F[Unit]): F[Unit] = {
    val n = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected = n.asRight[SomeThrowableError]
    val ioA      = Fx[F].fromEither(expected)

    run(ioA, expected)
  }

  def testFromEitherLeftCase[F[*]: Fx](run: (F[Int], Either[Throwable, Int]) => F[Unit]): F[Unit] = {
    val errorMessage = RandomGens.genAlphaString(10)

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected = SomeThrowableError.message(errorMessage).asLeft[Int]
    val ioA      = Fx[F].fromEither(expected)

    run(ioA, expected)
  }

  def testFromOptionSomeCase[F[*]: Fx](run: (F[Int], Either[Throwable, Int]) => F[Unit]): F[Unit] = {
    val n = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected = n.asRight[SomeThrowableError]
    val input    = n.some
    val ioA      =
      Fx[F].fromOption(input)(SomeThrowableError.Message("This should never happen!"))

    run(ioA, expected)
  }

  def testFromOptionNoneCase[F[*]: Fx](run: (F[Int], Either[Throwable, Int]) => F[Unit]): F[Unit] = {
    val errorMessage = RandomGens.genAlphaString(10)

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected = SomeThrowableError.message(errorMessage).asLeft[Int]
    val ioA      = Fx[F].fromOption(none[Int])(SomeThrowableError.Message(errorMessage))

    run(ioA, expected)
  }

  def testFromTrySuccessCase[F[*]: Fx](run: (F[Int], Either[Throwable, Int]) => F[Unit]): F[Unit] = {
    val n = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected        = n.asRight[SomeThrowableError]
    val input: Try[Int] = scala.util.Success(n)
    val ioA             = Fx[F].fromTry(input)

    run(ioA, expected)
  }

  def testFromTryFailureCase[F[*]: Fx](run: (F[Int], Either[Throwable, Int]) => F[Unit]): F[Unit] = {
    val errorMessage = RandomGens.genAlphaString(10)

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected        = SomeThrowableError.message(errorMessage).asLeft[Int]
    val input: Try[Int] = scala.util.Failure(SomeThrowableError.message(errorMessage))
    val ioA             = Fx[F].fromTry(input)

    run(ioA, expected)
  }

  def testFlatMapFx[F[*]: Fx](run: (F[String], String) => F[Unit]): F[Unit] = {
    val n      = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)
    val prefix = "n is "

    val expected = prefix + n.toString
    val fa       = Fx[F].pureOf(n)
    val fb       = Fx[F].flatMapFa(fa)(n => Fx[F].pureOf(prefix + n.toString))
    run(fb, expected)
  }

}
