package effectie.instances.ce3

import cats._
import cats.data.EitherT
import cats.syntax.all._
import effectie.SomeControlThrowable
import effectie.core._
import effectie.syntax.error._
import effectie.syntax.fx._
import effectie.testing.types.SomeError
import hedgehog._
import hedgehog.runner._

import scala.util.control.ControlThrowable

/** @author Kevin Lee
  * @since 2020-08-17
  */
object CanHandleErrorIdSpec extends Properties {
  import effectie.instances.id.canHandleError._
  import effectie.instances.id.fxCtor._

  override def tests: List[Test] = List(
    example(
      "test CanHandleError[Id].handleNonFatalWith should handle NonFatal",
      testCanHandleError_Id_handleNonFatalWithShouldHandleNonFatalWith,
    ),
    example(
      "test CanHandleError[Id].handleNonFatalWith should not handle Fatal",
      testCanHandleError_Id_handleNonFatalWithShouldNotHandleFatalWith,
    ),
    example(
      "test CanHandleError[Id].handleNonFatalWith should return the successful result",
      testCanHandleError_Id_handleNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Id].handleNonFatalWithEither should handle NonFatal",
      testCanHandleError_Id_handleNonFatalWithEitherShouldHandleNonFatalWith,
    ),
    example(
      "test CanHandleError[Id].handleNonFatalWithEither should not handle Fatal",
      testCanHandleError_Id_handleNonFatalWithEitherShouldNotHandleFatalWith,
    ),
    example(
      "test CanHandleError[Id].handleNonFatalWithEither should return the successful result",
      testCanHandleError_Id_handleNonFatalWithEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Id].handleNonFatalWithEither should return the failed result",
      testCanHandleError_Id_handleNonFatalWithEitherShouldReturnFailedResult,
    ),
    example(
      "test CanHandleError[Id].handleEitherNonFatalWith should handle NonFatal",
      testCanHandleError_Id_handleEitherNonFatalWithShouldHandleNonFatalWith,
    ),
    example(
      "test CanHandleError[Id].handleEitherNonFatalWith should not handle Fatal",
      testCanHandleError_Id_handleEitherNonFatalWithShouldNotHandleFatalWith,
    ),
    example(
      "test CanHandleError[Id].handleEitherNonFatalWith should return the successful result",
      testCanHandleError_Id_handleEitherNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Id].handleEitherNonFatalWith should return the failed result",
      testCanHandleError_Id_handleEitherNonFatalWithShouldReturnFailedResult,
    ),
    example(
      "test CanHandleError[Id].handleEitherTNonFatalWith should handle NonFatal",
      testCanHandleError_Id_handleEitherTNonFatalWithShouldHandleNonFatalWith,
    ),
    example(
      "test CanHandleError[Id].handleEitherTNonFatalWith should not handle Fatal",
      testCanHandleError_Id_handleEitherTNonFatalWithShouldNotHandleFatalWith,
    ),
    example(
      "test CanHandleError[Id].handleEitherTNonFatalWith should return the successful result",
      testCanHandleError_Id_handleEitherTNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Id].handleEitherTNonFatalWith should return the failed result",
      testCanHandleError_Id_handleEitherTNonFatalWithShouldReturnFailedResult,
    ),
    example(
      "test CanHandleError[Id].handleNonFatal should handle NonFatal",
      testCanHandleError_Id_handleNonFatalShouldHandleNonFatal,
    ),
    example(
      "test CanHandleError[Id].handleNonFatal should not handle Fatal",
      testCanHandleError_Id_handleNonFatalShouldNotHandleFatal,
    ),
    example(
      "test CanHandleError[Id].handleNonFatal should return the successful result",
      testCanHandleError_Id_handleNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Id].handleNonFatalEither should handle NonFatal",
      testCanHandleError_Id_handleNonFatalEitherShouldHandleNonFatal,
    ),
    example(
      "test CanHandleError[Id].handleNonFatalEither should not handle Fatal",
      testCanHandleError_Id_handleNonFatalEitherShouldNotHandleFatal,
    ),
    example(
      "test CanHandleError[Id].handleNonFatalEither should return the successful result",
      testCanHandleError_Id_handleNonFatalEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Id].handleNonFatalEither should return the failed result",
      testCanHandleError_Id_handleNonFatalEitherShouldReturnFailedResult,
    ),
    example(
      "test CanHandleError[Id].handleEitherNonFatal should handle NonFatal",
      testCanHandleError_Id_handleEitherNonFatalShouldHandleNonFatal,
    ),
    example(
      "test CanHandleError[Id].handleEitherNonFatal should not handle Fatal",
      testCanHandleError_Id_handleEitherNonFatalShouldNotHandleFatal,
    ),
    example(
      "test CanHandleError[Id].handleEitherNonFatal should return the successful result",
      testCanHandleError_Id_handleEitherNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Id].handleEitherNonFatal should return the failed result",
      testCanHandleError_Id_handleEitherNonFatalShouldReturnFailedResult,
    ),
    example(
      "test CanHandleError[Id].handleEitherTNonFatal should handle NonFatal",
      testCanHandleError_Id_handleEitherTNonFatalShouldHandleNonFatal,
    ),
    example(
      "test CanHandleError[Id].handleEitherTNonFatal should not handle Fatal",
      testCanHandleError_Id_handleEitherTNonFatalShouldNotHandleFatal,
    ),
    example(
      "test CanHandleError[Id].handleEitherTNonFatal should return the successful result",
      testCanHandleError_Id_handleEitherTNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[Id].handleEitherTNonFatal should return the failed result",
      testCanHandleError_Id_handleEitherTNonFatalShouldReturnFailedResult,
    ),
  )

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A = throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor, A](a: => A): F[A] = effectOf[F](a)

  def testCanHandleError_Id_handleNonFatalWithShouldHandleNonFatalWith: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 1
    val actual: Id[Int]   = CanHandleError[Id].handleNonFatalWith(fa)(_ => expected)

    actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanHandleError_Id_handleNonFatalWithShouldNotHandleFatalWith: Result = {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    lazy val fa        = run[Id, Int](throwThrowable[Int](fatalExpcetion))

    try {
      val actual: Id[Int] = CanHandleError[Id].handleNonFatalWith(fa)(_ => 1)
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== fatalExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanHandleError_Id_handleNonFatalWithShouldReturnSuccessfulResult: Result = {

    val fa              = run[Id, Int](1)
    val expected        = 1
    val actual: Id[Int] = CanHandleError[Id].handleNonFatalWith(fa)(_ => 123)

    actual ==== expected
  }

  def testCanHandleError_Id_handleNonFatalWithEitherShouldHandleNonFatalWith: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    lazy val fa           = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   =
      CanHandleError[Id].handleNonFatalWith(fa)(err => SomeError.someThrowable(err).asLeft[Int])

    lazy val fa2 = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected = 1.asRight[SomeError]
    val actual   = CanHandleError[Id].handleNonFatalWith(fa2)(_ => expected)

    actualFailedResult ==== expectedFailedResult and actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanHandleError_Id_handleNonFatalWithEitherShouldNotHandleFatalWith: Result = {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    lazy val fa        = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

    try {
      val actual = CanHandleError[Id].handleNonFatalWith(fa)(_ => 1.asRight[SomeError])
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== fatalExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanHandleError_Id_handleNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

    val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    val actual   = CanHandleError[Id].handleNonFatalWith(fa)(err => SomeError.someThrowable(err).asLeft[Int])

    actual ==== expected
  }

  def testCanHandleError_Id_handleNonFatalWithEitherShouldReturnFailedResult: Result = {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    val actual          = CanHandleError[Id].handleNonFatalWith(fa)(_ => 1.asRight[SomeError])

    actual ==== expected
  }

  def testCanHandleError_Id_handleEitherNonFatalWithShouldHandleNonFatalWith: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    lazy val fa           = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   =
      CanHandleError[Id].handleEitherNonFatalWith(fa)(err => SomeError.someThrowable(err).asLeft[Int])

    lazy val fa2 = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected = 1.asRight[SomeError]
    val actual   = CanHandleError[Id].handleEitherNonFatalWith(fa2)(_ => expected)

    actualFailedResult ==== expectedFailedResult and actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanHandleError_Id_handleEitherNonFatalWithShouldNotHandleFatalWith: Result = {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    lazy val fa        = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

    try {
      val actual = CanHandleError[Id].handleEitherNonFatalWith(fa)(_ => 1.asRight[SomeError])
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== fatalExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanHandleError_Id_handleEitherNonFatalWithShouldReturnSuccessfulResult: Result = {

    val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    val actual   =
      CanHandleError[Id].handleEitherNonFatalWith(fa)(err => SomeError.someThrowable(err).asLeft[Int])

    actual ==== expected
  }

  def testCanHandleError_Id_handleEitherNonFatalWithShouldReturnFailedResult: Result = {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    val actual          = CanHandleError[Id].handleEitherNonFatalWith(fa)(_ => 1.asRight[SomeError])

    actual ==== expected
  }

  def testCanHandleError_Id_handleEitherTNonFatalWithShouldHandleNonFatalWith: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   =
      CanHandleError[Id].handleEitherTNonFatalWith(fa)(err => SomeError.someThrowable(err).asLeft[Int]).value

    lazy val fa2 = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expected = 1.asRight[SomeError]
    val actual   = CanHandleError[Id].handleEitherTNonFatalWith(fa2)(_ => expected).value

    actualFailedResult ==== expectedFailedResult and actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanHandleError_Id_handleEitherTNonFatalWithShouldNotHandleFatalWith: Result = {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

    try {
      val actual = CanHandleError[Id].handleEitherTNonFatalWith(fa)(_ => 1.asRight[SomeError]).value
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== fatalExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanHandleError_Id_handleEitherTNonFatalWithShouldReturnSuccessfulResult: Result = {

    val fa       = EitherT(run[Id, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]
    val actual   =
      CanHandleError[Id].handleEitherTNonFatalWith(fa)(err => SomeError.someThrowable(err).asLeft[Int]).value

    actual ==== expected
  }

  def testCanHandleError_Id_handleEitherTNonFatalWithShouldReturnFailedResult: Result = {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]
    val actual          = CanHandleError[Id].handleEitherTNonFatalWith(fa)(_ => 1.asRight[SomeError]).value

    actual ==== expected
  }

  def testCanHandleError_Id_handleNonFatalShouldHandleNonFatal: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 1
    val actual: Id[Int]   = CanHandleError[Id].handleNonFatal(fa)(_ => expected)

    actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanHandleError_Id_handleNonFatalShouldNotHandleFatal: Result = {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    lazy val fa        = run[Id, Int](throwThrowable[Int](fatalExpcetion))

    try {
      val actual: Id[Int] = CanHandleError[Id].handleNonFatal(fa)(_ => 1)
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== fatalExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanHandleError_Id_handleNonFatalShouldReturnSuccessfulResult: Result = {

    val fa              = run[Id, Int](1)
    val expected        = 1
    val actual: Id[Int] = CanHandleError[Id].handleNonFatal(fa)(_ => 123)

    actual ==== expected
  }

  def testCanHandleError_Id_handleNonFatalEitherShouldHandleNonFatal: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    lazy val fa           = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   = CanHandleError[Id].handleNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])

    lazy val fa2 = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected = 1.asRight[SomeError]
    val actual   = CanHandleError[Id].handleNonFatal(fa2)(_ => expected)

    actualFailedResult ==== expectedFailedResult and actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanHandleError_Id_handleNonFatalEitherShouldNotHandleFatal: Result = {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    lazy val fa        = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

    try {
      val actual = CanHandleError[Id].handleNonFatal(fa)(_ => 1.asRight[SomeError])
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== fatalExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanHandleError_Id_handleNonFatalEitherShouldReturnSuccessfulResult: Result = {

    val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    val actual   = CanHandleError[Id].handleNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])

    actual ==== expected
  }

  def testCanHandleError_Id_handleNonFatalEitherShouldReturnFailedResult: Result = {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    val actual          = CanHandleError[Id].handleNonFatal(fa)(_ => 1.asRight[SomeError])

    actual ==== expected
  }

  def testCanHandleError_Id_handleEitherNonFatalShouldHandleNonFatal: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    lazy val fa           = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   =
      CanHandleError[Id].handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])

    lazy val fa2 = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected = 1.asRight[SomeError]
    val actual   = CanHandleError[Id].handleEitherNonFatal(fa2)(_ => expected)

    actualFailedResult ==== expectedFailedResult and actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanHandleError_Id_handleEitherNonFatalShouldNotHandleFatal: Result = {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    lazy val fa        = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

    try {
      val actual = CanHandleError[Id].handleEitherNonFatal(fa)(_ => 1.asRight[SomeError])
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== fatalExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanHandleError_Id_handleEitherNonFatalShouldReturnSuccessfulResult: Result = {

    val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    val actual   = CanHandleError[Id].handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])

    actual ==== expected
  }

  def testCanHandleError_Id_handleEitherNonFatalShouldReturnFailedResult: Result = {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    val actual          = CanHandleError[Id].handleEitherNonFatal(fa)(_ => 1.asRight[SomeError])

    actual ==== expected
  }

  def testCanHandleError_Id_handleEitherTNonFatalShouldHandleNonFatal: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   =
      CanHandleError[Id].handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]).value

    lazy val fa2 = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expected = 1.asRight[SomeError]
    val actual   = CanHandleError[Id].handleEitherTNonFatal(fa2)(_ => expected).value

    actualFailedResult ==== expectedFailedResult and actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanHandleError_Id_handleEitherTNonFatalShouldNotHandleFatal: Result = {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

    try {
      val actual = CanHandleError[Id].handleEitherTNonFatal(fa)(_ => 1.asRight[SomeError]).value
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== fatalExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanHandleError_Id_handleEitherTNonFatalShouldReturnSuccessfulResult: Result = {

    val fa       = EitherT(run[Id, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]
    val actual   = CanHandleError[Id].handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]).value

    actual ==== expected
  }

  def testCanHandleError_Id_handleEitherTNonFatalShouldReturnFailedResult: Result = {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]
    val actual          = CanHandleError[Id].handleEitherTNonFatal(fa)(_ => 1.asRight[SomeError]).value

    actual ==== expected
  }

}
