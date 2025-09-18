package effectie.instances.ce2

import cats._
import cats.data.EitherT
import cats.syntax.all._
import effectie.SomeControlThrowable
import effectie.core._
import effectie.syntax.error._
import effectie.syntax.fx._
import effectie.testing.types.SomeError
import extras.concurrent.testing.types.ErrorLogger
import hedgehog._
import hedgehog.runner._

import scala.util.control.{ControlThrowable, NonFatal}

/** @author Kevin Lee
  * @since 2020-08-17
  */
object canRecoverIdSpec extends Properties {

  implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = List(
    /* Id */
    example(
      "test CanRecover[Id].recoverFromNonFatalWith should catch NonFatal",
      testCanRecover_Id_recoverFromNonFatalWithShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatalWith should not catch Fatal",
      testCanRecover_Id_recoverFromNonFatalWithShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatalWith should return the successful result",
      testCanRecover_Id_recoverFromNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatalWithEither should catch NonFatal",
      testCanRecover_Id_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatalWithEither should not catch Fatal",
      testCanRecover_Id_recoverFromNonFatalWithEitherShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatalWithEither should return the successful result",
      testCanRecover_Id_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatalWithEither should return the failed result",
      testCanRecover_Id_recoverFromNonFatalWithEitherShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[Id].recoverEitherFromNonFatalWith should catch NonFatal",
      testCanRecover_Id_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Id].recoverEitherFromNonFatalWith should not catch Fatal",
      testCanRecover_Id_recoverEitherFromNonFatalWithShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Id].recoverEitherFromNonFatalWith should return the successful result",
      testCanRecover_Id_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Id].recoverEitherFromNonFatalWith should return the failed result",
      testCanRecover_Id_recoverEitherFromNonFatalWithShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[Id].recoverEitherTFromNonFatalWith should catch NonFatal",
      testCanRecover_Id_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Id].recoverEitherTFromNonFatalWith should not catch Fatal",
      testCanRecover_Id_recoverEitherTFromNonFatalWithShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Id].recoverEitherTFromNonFatalWith should return the successful result",
      testCanRecover_Id_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Id].recoverEitherTFromNonFatalWith should return the failed result",
      testCanRecover_Id_recoverEitherTFromNonFatalWithShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatal should catch NonFatal",
      testCanRecover_Id_recoverFromNonFatalShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatal should not catch Fatal",
      testCanRecover_Id_recoverFromNonFatalShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatal should return the successful result",
      testCanRecover_Id_recoverFromNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatalEither should catch NonFatal",
      testCanRecover_Id_recoverFromNonFatalEitherShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatalEither should not catch Fatal",
      testCanRecover_Id_recoverFromNonFatalEitherShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatalEither should return the successful result",
      testCanRecover_Id_recoverFromNonFatalEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatalEither should return the failed result",
      testCanRecover_Id_recoverFromNonFatalEitherShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[Id].recoverEitherFromNonFatal should catch NonFatal",
      testCanRecover_Id_recoverEitherFromNonFatalShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Id].recoverEitherFromNonFatal should not catch Fatal",
      testCanRecover_Id_recoverEitherFromNonFatalShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Id].recoverEitherFromNonFatal should return the successful result",
      testCanRecover_Id_recoverEitherFromNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Id].recoverEitherFromNonFatal should return the failed result",
      testCanRecover_Id_recoverEitherFromNonFatalShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[Id].recoverEitherTFromNonFatal should catch NonFatal",
      testCanRecover_Id_recoverEitherTFromNonFatalShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[Id].recoverEitherTFromNonFatal should not catch Fatal",
      testCanRecover_Id_recoverEitherTFromNonFatalShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[Id].recoverEitherTFromNonFatal should return the successful result",
      testCanRecover_Id_recoverEitherTFromNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[Id].recoverEitherTFromNonFatal should return the failed result",
      testCanRecover_Id_recoverEitherTFromNonFatalShouldReturnFailedResult,
    ),
  )

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor, A](a: => A): F[A] =
    effectOf[F](a)

  import effectie.instances.id.canRecover._
  import effectie.instances.id.fxCtor._

  def testCanRecover_Id_recoverFromNonFatalWithShouldRecoverFromNonFatal: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 1
    val actual: Id[Int]   = CanRecover[Id].recoverFromNonFatalWith(fa) {
      case NonFatal(`expectedExpcetion`) => expected
    }

    actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanRecover_Id_recoverFromNonFatalWithShouldNotCatchFatal: Result = {

    val expectedExpcetion = SomeControlThrowable("Something's wrong")
    lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))

    try {
      val actual: Id[Int] = CanRecover[Id].recoverFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => 1 }
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== expectedExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanRecover_Id_recoverFromNonFatalWithShouldReturnSuccessfulResult: Result = {

    val fa              = run[Id, Int](1)
    val expected        = 1
    val actual: Id[Int] = CanRecover[Id].recoverFromNonFatalWith(fa) { case NonFatal(_) => 123 }

    actual ==== expected
  }

  def testCanRecover_Id_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    lazy val fa           = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   = CanRecover[Id].recoverFromNonFatalWith(fa) {
      case err => SomeError.someThrowable(err).asLeft[Int]
    }

    val expected = 1.asRight[SomeError]
    val actual   = CanRecover[Id].recoverFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => expected }

    actualFailedResult ==== expectedFailedResult and actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanRecover_Id_recoverFromNonFatalWithEitherShouldNotCatchFatal: Result = {

    val expectedExpcetion = SomeControlThrowable("Something's wrong")
    lazy val fa           = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

    try {
      val actual = CanRecover[Id].recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError]
      }
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== expectedExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanRecover_Id_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

    val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    val actual   =
      CanRecover[Id].recoverFromNonFatalWith(fa) {
        case err => SomeError.someThrowable(err).asLeft[Int]
      }

    actual ==== expected
  }

  def testCanRecover_Id_recoverFromNonFatalWithEitherShouldReturnFailedResult: Result = {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    val actual          = CanRecover[Id].recoverFromNonFatalWith(fa) { case NonFatal(_) => 1.asRight[SomeError] }

    actual ==== expected
  }

  def testCanRecover_Id_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    lazy val fa           = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   =
      CanRecover[Id]
        .recoverEitherFromNonFatalWith(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }

    val expected = 1.asRight[SomeError]
    val actual   =
      CanRecover[Id].recoverEitherFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => expected }

    actualFailedResult ==== expectedFailedResult and actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanRecover_Id_recoverEitherFromNonFatalWithShouldNotCatchFatal: Result = {

    val expectedExpcetion = SomeControlThrowable("Something's wrong")
    lazy val fa           = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

    try {
      val actual = CanRecover[Id]
        .recoverEitherFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== expectedExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanRecover_Id_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult: Result = {

    val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    val actual   = CanRecover[Id]
      .recoverEitherFromNonFatalWith(fa) {
        case err => SomeError.someThrowable(err).asLeft[Int]
      }

    actual ==== expected
  }

  def testCanRecover_Id_recoverEitherFromNonFatalWithShouldReturnFailedResult: Result = {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    val actual          =
      CanRecover[Id].recoverEitherFromNonFatalWith(fa) { case NonFatal(_) => 1.asRight[SomeError] }

    actual ==== expected
  }

  def testCanRecover_Id_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   =
      CanRecover[Id]
        .recoverEitherTFromNonFatalWith(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
        .value

    val expected = 1.asRight[SomeError]
    val actual   =
      CanRecover[Id].recoverEitherTFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => expected }.value

    actualFailedResult ==== expectedFailedResult and actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanRecover_Id_recoverEitherTFromNonFatalWithShouldNotCatchFatal: Result = {

    val expectedExpcetion = SomeControlThrowable("Something's wrong")
    lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))

    try {
      val actual = CanRecover[Id]
        .recoverEitherTFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }
        .value
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== expectedExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanRecover_Id_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult: Result = {

    val fa       = EitherT(run[Id, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]
    val actual   = CanRecover[Id]
      .recoverEitherTFromNonFatalWith(fa) {
        case err => SomeError.someThrowable(err).asLeft[Int]
      }
      .value

    actual ==== expected
  }

  def testCanRecover_Id_recoverEitherTFromNonFatalWithShouldReturnFailedResult: Result = {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]
    val actual          =
      CanRecover[Id].recoverEitherTFromNonFatalWith(fa) { case NonFatal(_) => 1.asRight[SomeError] }.value

    actual ==== expected
  }

  // /

  def testCanRecover_Id_recoverFromNonFatalShouldRecoverFromNonFatal: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 1
    val actual: Id[Int]   = CanRecover[Id].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expected }

    actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanRecover_Id_recoverFromNonFatalShouldNotCatchFatal: Result = {

    val expectedExpcetion = SomeControlThrowable("Something's wrong")
    lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))

    try {
      val actual: Id[Int] = CanRecover[Id].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 1 }
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== expectedExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanRecover_Id_recoverFromNonFatalShouldReturnSuccessfulResult: Result = {

    val fa              = run[Id, Int](1)
    val expected        = 1
    val actual: Id[Int] = CanRecover[Id].recoverFromNonFatal(fa) { case NonFatal(_) => 123 }

    actual ==== expected
  }

  def testCanRecover_Id_recoverFromNonFatalEitherShouldRecoverFromNonFatal: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    lazy val fa           = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   =
      CanRecover[Id].recoverFromNonFatal(fa) {
        case err => SomeError.someThrowable(err).asLeft[Int]
      }

    val expected = 1.asRight[SomeError]
    val actual   = CanRecover[Id].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expected }

    actualFailedResult ==== expectedFailedResult and actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanRecover_Id_recoverFromNonFatalEitherShouldNotCatchFatal: Result = {

    val expectedExpcetion = SomeControlThrowable("Something's wrong")
    lazy val fa           = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

    try {
      val actual = CanRecover[Id].recoverFromNonFatal(fa) {
        case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError]
      }
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== expectedExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanRecover_Id_recoverFromNonFatalEitherShouldReturnSuccessfulResult: Result = {

    val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    val actual   =
      CanRecover[Id].recoverFromNonFatal(fa) {
        case err => SomeError.someThrowable(err).asLeft[Int]
      }

    actual ==== expected
  }

  def testCanRecover_Id_recoverFromNonFatalEitherShouldReturnFailedResult: Result = {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    val actual          = CanRecover[Id].recoverFromNonFatal(fa) { case NonFatal(_) => 1.asRight[SomeError] }

    actual ==== expected
  }

  def testCanRecover_Id_recoverEitherFromNonFatalShouldRecoverFromNonFatal: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    lazy val fa           = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   =
      CanRecover[Id]
        .recoverEitherFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }

    val expected = 1.asRight[SomeError]
    val actual   =
      CanRecover[Id].recoverEitherFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expected }

    actualFailedResult ==== expectedFailedResult and actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanRecover_Id_recoverEitherFromNonFatalShouldNotCatchFatal: Result = {

    val expectedExpcetion = SomeControlThrowable("Something's wrong")
    lazy val fa           = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

    try {
      val actual = CanRecover[Id]
        .recoverEitherFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }

      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== expectedExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanRecover_Id_recoverEitherFromNonFatalShouldReturnSuccessfulResult: Result = {

    val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    val actual   = CanRecover[Id]
      .recoverEitherFromNonFatal(fa) {
        case err => SomeError.someThrowable(err).asLeft[Int]
      }

    actual ==== expected
  }

  def testCanRecover_Id_recoverEitherFromNonFatalShouldReturnFailedResult: Result = {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    val actual          = CanRecover[Id].recoverEitherFromNonFatal(fa) { case NonFatal(_) => 1.asRight[SomeError] }

    actual ==== expected
  }

  def testCanRecover_Id_recoverEitherTFromNonFatalShouldRecoverFromNonFatal: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actualFailedResult   =
      CanRecover[Id]
        .recoverEitherTFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
        .value

    val expected = 1.asRight[SomeError]
    val actual   =
      CanRecover[Id].recoverEitherTFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expected }.value

    actualFailedResult ==== expectedFailedResult and actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanRecover_Id_recoverEitherTFromNonFatalShouldNotCatchFatal: Result = {

    val expectedExpcetion = SomeControlThrowable("Something's wrong")
    lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))

    try {
      val actual = CanRecover[Id]
        .recoverEitherTFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }
        .value
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== expectedExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanRecover_Id_recoverEitherTFromNonFatalShouldReturnSuccessfulResult: Result = {

    val fa       = EitherT(run[Id, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]
    val actual   = CanRecover[Id]
      .recoverEitherTFromNonFatal(fa) {
        case err => SomeError.someThrowable(err).asLeft[Int]
      }
      .value

    actual ==== expected
  }

  def testCanRecover_Id_recoverEitherTFromNonFatalShouldReturnFailedResult: Result = {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]
    val actual = CanRecover[Id].recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 1.asRight[SomeError] }.value

    actual ==== expected
  }

}
