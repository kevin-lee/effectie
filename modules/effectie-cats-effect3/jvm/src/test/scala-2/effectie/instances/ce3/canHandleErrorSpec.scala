package effectie.instances.ce3

import canHandleError._
import cats.data.EitherT
import cats.effect.IO
import cats.effect.unsafe.IORuntime
import cats.instances.all._
import cats.syntax.all._
import effectie.SomeControlThrowable
import effectie.core._
import effectie.syntax.error._
import effectie.syntax.fx._
import effectie.testing.types.SomeError
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.ErrorLogger
import extras.hedgehog.ce3.syntax.runner._
import fxCtor._
import hedgehog._
import hedgehog.runner._

import java.util.concurrent.ExecutorService
import scala.util.control.{ControlThrowable, NonFatal}

/** @author Kevin Lee
  * @since 2020-08-17
  */
object canHandleErrorSpec extends Properties {

  implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = List(
    /* IO */
    example(
      "test CanHandleError[IO].handleNonFatalWith should handle NonFatal",
      testCanHandleError_IO_handleNonFatalWithShouldHandleNonFatalWith,
    ),
    example(
      "test CanHandleError[IO].handleNonFatalWith should not handle Fatal",
      testCanHandleError_IO_handleNonFatalWithShouldNotHandleFatalWith,
    ),
    example(
      "test CanHandleError[IO].handleNonFatalWith should return the successful result",
      testCanHandleError_IO_handleNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[IO].handleNonFatalWithEither should handle NonFatal",
      testCanHandleError_IO_handleNonFatalWithEitherShouldHandleNonFatalWith,
    ),
    example(
      "test CanHandleError[IO].handleNonFatalWithEither should not handle Fatal",
      testCanHandleError_IO_handleNonFatalWithEitherShouldNotHandleFatalWith,
    ),
    example(
      "test CanHandleError[IO].handleNonFatalWithEither should return the successful result",
      testCanHandleError_IO_handleNonFatalWithEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[IO].handleNonFatalWithEither should return the failed result",
      testCanHandleError_IO_handleNonFatalWithEitherShouldReturnFailedResult,
    ),
    example(
      "test CanHandleError[IO].handleEitherNonFatalWith should handle NonFatal",
      testCanHandleError_IO_handleEitherNonFatalWithShouldHandleNonFatalWith,
    ),
    example(
      "test CanHandleError[IO].handleEitherNonFatalWith should not handle Fatal",
      testCanHandleError_IO_handleEitherNonFatalWithShouldNotHandleFatalWith,
    ),
    example(
      "test CanHandleError[IO].handleEitherNonFatalWith should return the successful result",
      testCanHandleError_IO_handleEitherNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[IO].handleEitherNonFatalWith should return the failed result",
      testCanHandleError_IO_handleEitherNonFatalWithShouldReturnFailedResult,
    ),
    example(
      "test CanHandleError[IO].handleEitherTNonFatalWith should handle NonFatal",
      testCanHandleError_IO_handleEitherTNonFatalWithShouldHandleNonFatalWith,
    ),
    example(
      "test CanHandleError[IO].handleEitherTNonFatalWith should not handle Fatal",
      testCanHandleError_IO_handleEitherTNonFatalWithShouldNotHandleFatalWith,
    ),
    example(
      "test CanHandleError[IO].handleEitherTNonFatalWith should return the successful result",
      testCanHandleError_IO_handleEitherTNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[IO].handleEitherTNonFatalWith should return the failed result",
      testCanHandleError_IO_handleEitherTNonFatalWithShouldReturnFailedResult,
    ),
    example(
      "test CanHandleError[IO].handleNonFatal should handle NonFatal",
      testCanHandleError_IO_handleNonFatalShouldHandleNonFatal,
    ),
    example(
      "test CanHandleError[IO].handleNonFatal should not handle Fatal",
      testCanHandleError_IO_handleNonFatalShouldNotHandleFatal,
    ),
    example(
      "test CanHandleError[IO].handleNonFatal should return the successful result",
      testCanHandleError_IO_handleNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[IO].handleNonFatalEither should handle NonFatal",
      testCanHandleError_IO_handleNonFatalEitherShouldHandleNonFatal,
    ),
    example(
      "test CanHandleError[IO].handleNonFatalEither should not handle Fatal",
      testCanHandleError_IO_handleNonFatalEitherShouldNotHandleFatal,
    ),
    example(
      "test CanHandleError[IO].handleNonFatalEither should return the successful result",
      testCanHandleError_IO_handleNonFatalEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[IO].handleNonFatalEither should return the failed result",
      testCanHandleError_IO_handleNonFatalEitherShouldReturnFailedResult,
    ),
    example(
      "test CanHandleError[IO].handleEitherNonFatal should handle NonFatal",
      testCanHandleError_IO_handleEitherNonFatalShouldHandleNonFatal,
    ),
    example(
      "test CanHandleError[IO].handleEitherNonFatal should not handle Fatal",
      testCanHandleError_IO_handleEitherNonFatalShouldNotHandleFatal,
    ),
    example(
      "test CanHandleError[IO].handleEitherNonFatal should return the successful result",
      testCanHandleError_IO_handleEitherNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[IO].handleEitherNonFatal should return the failed result",
      testCanHandleError_IO_handleEitherNonFatalShouldReturnFailedResult,
    ),
    example(
      "test CanHandleError[IO].handleEitherTNonFatal should handle NonFatal",
      testCanHandleError_IO_handleEitherTNonFatalShouldHandleNonFatal,
    ),
    example(
      "test CanHandleError[IO].handleEitherTNonFatal should not handle Fatal",
      testCanHandleError_IO_handleEitherTNonFatalShouldNotHandleFatal,
    ),
    example(
      "test CanHandleError[IO].handleEitherTNonFatal should return the successful result",
      testCanHandleError_IO_handleEitherTNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanHandleError[IO].handleEitherTNonFatal should return the failed result",
      testCanHandleError_IO_handleEitherTNonFatalShouldReturnFailedResult,
    ),
  )

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A = throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor, A](a: => A): F[A] = effectOf[F](a)

  def testCanHandleError_IO_handleNonFatalWithShouldHandleNonFatalWith: Result = withIO { implicit ticker =>

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedException))
    val expected          = 123
    val actual            = CanHandleError[IO]
      .handleNonFatalWith(fa) {
        case NonFatal(`expectedException`) =>
          IO.pure(expected)
        case err =>
          throw err // scalafix:ok DisableSyntax.throw
      }

    actual.completeAs(expected)
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanHandleError_IO_handleNonFatalWithShouldNotHandleFatalWith: Result = {

    val es: ExecutorService    = ConcurrentSupport.newExecutorService(2)
    implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))

    try {
      val actual = CanHandleError[IO].handleNonFatalWith(fa)(_ => IO.pure(123)).unsafeRunSync()
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== fatalExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanHandleError_IO_handleNonFatalWithShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

    val fa       = run[IO, Int](1)
    val expected = 1
    val actual   = CanHandleError[IO].handleNonFatalWith(fa)(_ => IO.pure(999))

    actual.completeAs(expected)
  }

  def testCanHandleError_IO_handleNonFatalWithEitherShouldHandleNonFatalWith: Result = withIO { implicit ticker =>

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
    val actualFailedResult   =
      CanHandleError[IO].handleNonFatalWith(fa)(_ => IO.pure(expectedFailedResult))

    val expectedSuccessResult = 1.asRight[SomeError]
    val actualSuccessResult   =
      CanHandleError[IO].handleNonFatalWith(fa)(_ => IO.pure(1.asRight[SomeError]))

    actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanHandleError_IO_handleNonFatalWithEitherShouldNotHandleFatalWith: Result = {

    val es: ExecutorService    = ConcurrentSupport.newExecutorService(2)
    implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

    try {
      val actual = CanHandleError[IO].handleNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).unsafeRunSync()
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: SomeControlThrowable =>
        ex.getMessage ==== fatalExpcetion.getMessage

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanHandleError_IO_handleNonFatalWithEitherShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    val actual   = CanHandleError[IO].handleNonFatalWith(fa)(_ => IO(999.asRight[SomeError]))

    actual.completeAs(expected)
  }

  def testCanHandleError_IO_handleNonFatalWithEitherShouldReturnFailedResult: Result = withIO { implicit ticker =>

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    val actual          = CanHandleError[IO].handleNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError]))

    actual.completeAs(expected)
  }

  def testCanHandleError_IO_handleEitherNonFatalWithShouldHandleNonFatalWith: Result = withIO { implicit ticker =>

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult   = CanHandleError[IO]
      .handleEitherNonFatalWith(fa)(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      CanHandleError[IO].handleEitherNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError]))

    actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanHandleError_IO_handleEitherNonFatalWithShouldNotHandleFatalWith: Result = {

    val es: ExecutorService    = ConcurrentSupport.newExecutorService(2)
    implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

    try {
      val actual =
        CanHandleError[IO]
          .handleEitherNonFatalWith(fa)(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
          .unsafeRunSync()
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== fatalExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanHandleError_IO_handleEitherNonFatalWithShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    val actual   =
      CanHandleError[IO].handleEitherNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError]))

    actual.completeAs(expected)
  }

  def testCanHandleError_IO_handleEitherNonFatalWithShouldReturnFailedResult: Result = withIO { implicit ticker =>

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    val actual          =
      CanHandleError[IO].handleEitherNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError]))

    actual.completeAs(expected)
  }

  def testCanHandleError_IO_handleEitherTNonFatalWithShouldHandleNonFatalWith: Result = withIO { implicit ticker =>

    val expectedException = new RuntimeException("Something's wrong")
    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult   = CanHandleError[IO]
      .handleEitherTNonFatalWith(fa)(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
      .value

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      CanHandleError[IO].handleEitherTNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).value

    actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanHandleError_IO_handleEitherTNonFatalWithShouldNotHandleFatalWith: Result = {

    val es: ExecutorService    = ConcurrentSupport.newExecutorService(2)
    implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

    try {
      val actual =
        CanHandleError[IO]
          .handleEitherTNonFatalWith(fa)(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
          .value
          .unsafeRunSync()
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== fatalExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanHandleError_IO_handleEitherTNonFatalWithShouldReturnSuccessfulResult: Result =
    withIO { implicit ticker =>

      val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   =
        CanHandleError[IO].handleEitherTNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).value

      actual.completeAs(expected)
    }

  def testCanHandleError_IO_handleEitherTNonFatalWithShouldReturnFailedResult: Result = withIO { implicit ticker =>

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]
    val actual          =
      CanHandleError[IO].handleEitherTNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).value

    actual.completeAs(expected)
  }

  def testCanHandleError_IO_handleNonFatalShouldHandleNonFatal: Result = withIO { implicit ticker =>

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedException))
    val expected          = 123
    val actual            = CanHandleError[IO]
      .handleNonFatal(fa) {
        case NonFatal(`expectedException`) =>
          expected
        case err =>
          throw err // scalafix:ok DisableSyntax.throw
      }

    actual.completeAs(expected)
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanHandleError_IO_handleNonFatalShouldNotHandleFatal: Result = {

    val es: ExecutorService    = ConcurrentSupport.newExecutorService(2)
    implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))

    try {
      val actual = CanHandleError[IO].handleNonFatal(fa)(_ => 123).unsafeRunSync()
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== fatalExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanHandleError_IO_handleNonFatalShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

    val fa       = run[IO, Int](1)
    val expected = 1
    val actual   = CanHandleError[IO].handleNonFatal(fa)(_ => 999)

    actual.completeAs(expected)
  }

  def testCanHandleError_IO_handleNonFatalEitherShouldHandleNonFatal: Result = withIO { implicit ticker =>

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
    val actualFailedResult   = CanHandleError[IO].handleNonFatal(fa)(_ => expectedFailedResult)

    val expectedSuccessResult = 1.asRight[SomeError]
    val actualSuccessResult   = CanHandleError[IO].handleNonFatal(fa)(_ => 1.asRight[SomeError])

    actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanHandleError_IO_handleNonFatalEitherShouldNotHandleFatal: Result = {

    val es: ExecutorService    = ConcurrentSupport.newExecutorService(2)
    implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

    try {
      val actual = CanHandleError[IO].handleNonFatal(fa)(_ => 123.asRight[SomeError]).unsafeRunSync()
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== fatalExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanHandleError_IO_handleNonFatalEitherShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    val actual   = CanHandleError[IO].handleNonFatal(fa)(_ => 999.asRight[SomeError])

    actual.completeAs(expected)
  }

  def testCanHandleError_IO_handleNonFatalEitherShouldReturnFailedResult: Result = withIO { implicit ticker =>

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    val actual          = CanHandleError[IO].handleNonFatal(fa)(_ => 123.asRight[SomeError])

    actual.completeAs(expected)
  }

  def testCanHandleError_IO_handleEitherNonFatalShouldHandleNonFatal: Result = withIO { implicit ticker =>

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult   = CanHandleError[IO]
      .handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      CanHandleError[IO].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError])

    actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanHandleError_IO_handleEitherNonFatalShouldNotHandleFatal: Result = {

    val es: ExecutorService    = ConcurrentSupport.newExecutorService(2)
    implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

    try {
      val actual =
        CanHandleError[IO]
          .handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
          .unsafeRunSync()
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== fatalExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanHandleError_IO_handleEitherNonFatalShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    val actual   = CanHandleError[IO].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError])

    actual.completeAs(expected)
  }

  def testCanHandleError_IO_handleEitherNonFatalShouldReturnFailedResult: Result = withIO { implicit ticker =>

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    val actual          =
      CanHandleError[IO].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError])

    actual.completeAs(expected)
  }

  def testCanHandleError_IO_handleEitherTNonFatalShouldHandleNonFatal: Result = withIO { implicit ticker =>

    val expectedException = new RuntimeException("Something's wrong")
    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
    val actualFailedResult   = CanHandleError[IO]
      .handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
      .value

    val expectedSuccessResult = 123.asRight[SomeError]
    val actualSuccessResult   =
      CanHandleError[IO].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value

    actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanHandleError_IO_handleEitherTNonFatalShouldNotHandleFatal: Result = {

    val es: ExecutorService    = ConcurrentSupport.newExecutorService(2)
    implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

    try {
      val actual =
        CanHandleError[IO]
          .handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
          .value
          .unsafeRunSync()
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== fatalExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanHandleError_IO_handleEitherTNonFatalShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

    val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]
    val actual   = CanHandleError[IO].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value

    actual.completeAs(expected)
  }

  def testCanHandleError_IO_handleEitherTNonFatalShouldReturnFailedResult: Result = withIO { implicit ticker =>

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]
    val actual          =
      CanHandleError[IO].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value

    actual.completeAs(expected)
  }

}
