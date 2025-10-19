package effectie.instances.ce3

import canCatch.canCatchIo
import cats.data.EitherT
import cats.effect._
import cats.syntax.all._
import effectie.core._
import effectie.syntax.error._
import effectie.syntax.fx._
import effectie.testing.types._
import fxCtor.ioFxCtor
import munit.Assertions

/** @author Kevin Lee
  * @since 2020-07-31
  */
class canCatchSpec extends munit.CatsEffectSuite {

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A = throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor, A](a: => A): F[A] = effectOf[F](a)

  test("test CanCatch[IO]catchNonFatalThrowable should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = expectedExpcetion.asLeft[Int]
    val actual            = CanCatch[IO].catchNonFatalThrowable(fa)

    actual.map(Assertions.assertEquals(_, expected))
  }

//  test("test CanCatch[IO]catchNonFatalThrowable should not catch Fatal") {
//
//    val fatalExpcetion = SomeControlThrowable("Something's wrong")
//    val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))
//
//    try {
//      CanCatch[IO]
//        .catchNonFatalThrowable(fa)
//        .map(actual => Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}"))
//    } catch {
//      case ex: SomeControlThrowable =>
//        Assertions.assertEquals(ex, fatalExpcetion)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//
//  }

  test("test CanCatch[IO]catchNonFatalThrowable should return the successful result") {

    val fa: IO[Int] = run[IO, Int](1)
    val expected    = 1.asRight[Throwable]
    val actual      = CanCatch[IO].catchNonFatalThrowable(fa)

    actual.map(Assertions.assertEquals(_, expected))
  }

  test("test CanCatch[IO]catchNonFatal should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actual            = CanCatch[IO].catchNonFatal(fa)(SomeError.someThrowable)

    actual.map(Assertions.assertEquals(_, expected))
  }

//  test("test CanCatch[IO]catchNonFatal should not catch Fatal") {
//
//    val fatalExpcetion = SomeControlThrowable("Something's wrong")
//    val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))
//
//    try {
//      CanCatch[IO]
//        .catchNonFatal(fa)(SomeError.someThrowable)
//        .map(actual => Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}"))
//    } catch {
//      case ex: SomeControlThrowable =>
//        Assertions.assertEquals(ex, fatalExpcetion)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//
//  }

  test("test CanCatch[IO]catchNonFatal should return the successful result") {

    val fa: IO[Int] = run[IO, Int](1)
    val expected    = 1.asRight[SomeError]
    val actual      = CanCatch[IO].catchNonFatal(fa)(SomeError.someThrowable)

    actual.map(Assertions.assertEquals(_, expected))
  }

  test("test CanCatch[IO]catchNonFatalEither should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actual            = CanCatch[IO].catchNonFatalEither(fa)(SomeError.someThrowable)

    actual.map(Assertions.assertEquals(_, expected))
  }

//  test("test CanCatch[IO]catchNonFatalEither should not catch Fatal") {
//
//    val fatalExpcetion = SomeControlThrowable("Something's wrong")
//    val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))
//
//    try {
//      CanCatch[IO]
//        .catchNonFatalEither(fa)(SomeError.someThrowable)
//        .map(actual => Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}"))
//    } catch {
//      case ex: SomeControlThrowable =>
//        Assertions.assertEquals(ex, fatalExpcetion)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//
//  }

  test("test CanCatch[IO]catchNonFatalEither should return the successful result") {

    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    val actual   = CanCatch[IO].catchNonFatalEither(fa)(SomeError.someThrowable)

    actual.map(Assertions.assertEquals(_, expected))
  }

  test("test CanCatch[IO]catchNonFatalEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    val actual          = CanCatch[IO].catchNonFatalEither(fa)(SomeError.someThrowable)

    actual.map(Assertions.assertEquals(_, expected))
  }

  test("test CanCatch[IO]catchNonFatalEitherT should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa       = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actual   = CanCatch[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

    actual.map(Assertions.assertEquals(_, expected))
  }

//  test("test CanCatch[IO]catchNonFatalEitherT should not catch Fatal") {
//
//    val fatalExpcetion = SomeControlThrowable("Something's wrong")
//    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))
//
//    try {
//      CanCatch[IO]
//        .catchNonFatalEitherT(fa)(SomeError.someThrowable)
//        .value
//        .map(actual => Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}"))
//    } catch {
//      case ex: SomeControlThrowable =>
//        Assertions.assertEquals(ex, fatalExpcetion)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//
//  }

  test("test CanCatch[IO]catchNonFatalEitherT should return the successful result") {

    val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]
    val actual   = CanCatch[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

    actual.map(Assertions.assertEquals(_, expected))
  }

  test("test CanCatch[IO]catchNonFatalEitherT should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]
    val actual          = CanCatch[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

    actual.map(Assertions.assertEquals(_, expected))
  }

}
