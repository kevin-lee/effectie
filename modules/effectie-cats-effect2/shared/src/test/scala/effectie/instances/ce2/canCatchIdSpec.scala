package effectie.instances.ce2

import cats._
import cats.data.EitherT
import cats.syntax.all._
import effectie.SomeControlThrowable
import effectie.core._
import effectie.syntax.error._
import effectie.syntax.fx._
import effectie.testing.types._
import hedgehog._
import hedgehog.runner._

import scala.util.control.ControlThrowable

/** @author Kevin Lee
  * @since 2020-07-31
  */
object canCatchIdSpec extends Properties {

  override def tests: List[Test] = List(
    example(
      "test CanCatch[Id]catchNonFatalThrowable should catch NonFatal",
      testCanCatch_Id_catchNonFatalThrowableShouldCatchNonFatal,
    ),
    example(
      "test CanCatch[Id]catchNonFatalThrowable should not catch Fatal",
      testCanCatch_Id_catchNonFatalThrowableShouldNotCatchFatal,
    ),
    example(
      "test CanCatch[Id]catchNonFatalThrowable should return the successful result",
      testCanCatch_Id_catchNonFatalThrowableShouldReturnSuccessfulResult,
    ),
    example(
      "test CanCatch[Id]catchNonFatal should catch NonFatal",
      testCanCatch_Id_catchNonFatalShouldCatchNonFatal,
    ),
    example(
      "test CanCatch[Id]catchNonFatal should not catch Fatal",
      testCanCatch_Id_catchNonFatalShouldNotCatchFatal,
    ),
    example(
      "test CanCatch[Id]catchNonFatal should return the successful result",
      testCanCatch_Id_catchNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanCatch[Id]catchNonFatalEither should catch NonFatal",
      testCanCatch_Id_catchNonFatalEitherShouldCatchNonFatal,
    ),
    example(
      "test CanCatch[Id]catchNonFatalEither should not catch Fatal",
      testCanCatch_Id_catchNonFatalEitherShouldNotCatchFatal,
    ),
    example(
      "test CanCatch[Id]catchNonFatalEither should return the successful result",
      testCanCatch_Id_catchNonFatalEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanCatch[Id]catchNonFatalEither should return the failed result",
      testCanCatch_Id_catchNonFatalEitherShouldReturnFailedResult,
    ),
    example(
      "test CanCatch[Id]catchNonFatalEitherT should catch NonFatal",
      testCanCatch_Id_catchNonFatalEitherTShouldCatchNonFatal,
    ),
    example(
      "test CanCatch[Id]catchNonFatalEitherT should not catch Fatal",
      testCanCatch_Id_catchNonFatalEitherTShouldNotCatchFatal,
    ),
    example(
      "test CanCatch[Id]catchNonFatalEitherT should return the successful result",
      testCanCatch_Id_catchNonFatalEitherTShouldReturnSuccessfulResult,
    ),
    example(
      "test CanCatch[Id]catchNonFatalEitherT should return the failed result",
      testCanCatch_Id_catchNonFatalEitherTShouldReturnFailedResult,
    ),
  )

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor, A](a: => A): F[A] =
    effectOf[F](a)

  import effectie.instances.id.canCatch._
  import effectie.instances.id.fxCtor._

  def testCanCatch_Id_catchNonFatalThrowableShouldCatchNonFatal: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = expectedExpcetion.asLeft[Int]
    val actual            = CanCatch[Id].catchNonFatalThrowable(fa)

    actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanCatch_Id_catchNonFatalThrowableShouldNotCatchFatal: Result = {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    lazy val fa        = run[Id, Int](throwThrowable[Int](fatalExpcetion))

    try {
      val actual = CanCatch[Id].catchNonFatalThrowable(fa)
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== fatalExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanCatch_Id_catchNonFatalThrowableShouldReturnSuccessfulResult: Result = {

    val fa       = run[Id, Int](1)
    val expected = 1.asRight[Throwable]
    val actual   = CanCatch[Id].catchNonFatalThrowable(fa)

    actual ==== expected
  }

  def testCanCatch_Id_catchNonFatalShouldCatchNonFatal: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actual            = CanCatch[Id].catchNonFatal(fa)(SomeError.someThrowable)

    actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanCatch_Id_catchNonFatalShouldNotCatchFatal: Result = {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    lazy val fa        = run[Id, Int](throwThrowable[Int](fatalExpcetion))

    try {
      val actual = CanCatch[Id].catchNonFatal(fa)(SomeError.someThrowable)
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== fatalExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanCatch_Id_catchNonFatalShouldReturnSuccessfulResult: Result = {

    val fa       = run[Id, Int](1)
    val expected = 1.asRight[SomeError]
    val actual   = CanCatch[Id].catchNonFatal(fa)(SomeError.someThrowable)

    actual ==== expected
  }

  def testCanCatch_Id_catchNonFatalEitherShouldCatchNonFatal: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    lazy val fa           = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actual            = CanCatch[Id].catchNonFatalEither(fa)(SomeError.someThrowable)

    actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanCatch_Id_catchNonFatalEitherShouldNotCatchFatal: Result = {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    lazy val fa        = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

    try {
      val actual = CanCatch[Id].catchNonFatalEither(fa)(SomeError.someThrowable)
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== fatalExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanCatch_Id_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

    val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    val actual   = CanCatch[Id].catchNonFatalEither(fa)(SomeError.someThrowable)

    actual ==== expected
  }

  def testCanCatch_Id_catchNonFatalEitherShouldReturnFailedResult: Result = {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    val actual          = CanCatch[Id].catchNonFatalEither(fa)(SomeError.someThrowable)

    actual ==== expected
  }

  def testCanCatch_Id_catchNonFatalEitherTShouldCatchNonFatal: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    lazy val fa  = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actual   = CanCatch[Id].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

    actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanCatch_Id_catchNonFatalEitherTShouldNotCatchFatal: Result = {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

    try {
      val actual = CanCatch[Id].catchNonFatalEitherT(fa)(SomeError.someThrowable).value
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== fatalExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanCatch_Id_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = {

    val fa       = EitherT(run[Id, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]
    val actual   = CanCatch[Id].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

    actual ==== expected
  }

  def testCanCatch_Id_catchNonFatalEitherTShouldReturnFailedResult: Result = {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]
    val actual          = CanCatch[Id].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

    actual ==== expected
  }

}
