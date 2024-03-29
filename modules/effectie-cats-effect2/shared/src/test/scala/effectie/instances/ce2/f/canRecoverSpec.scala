package effectie.instances.ce2.f

import cats.data.EitherT
import cats.effect.IO
import cats.syntax.all._
import effectie.SomeControlThrowable
import effectie.core._
import effectie.instances.ce2.f.fxCtor._
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
object canRecoverSpec extends Properties {
  implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = ioSpecs

  /* IO */
  val ioSpecs = List(
    example(
      "test CanRecover[IO].recoverFromNonFatalWith should catch NonFatal",
      IOSpec.testCanRecover_IO_recoverFromNonFatalWithShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatalWith should not catch Fatal",
      IOSpec.testCanRecover_IO_recoverFromNonFatalWithShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatalWith should return the successful result",
      IOSpec.testCanRecover_IO_recoverFromNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatalWithEither should catch NonFatal",
      IOSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatalWithEither should not catch Fatal",
      IOSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatalWithEither should return the successful result",
      IOSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatalWithEither should return the failed result",
      IOSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[IO].recoverEitherFromNonFatalWith should catch NonFatal",
      IOSpec.testCanRecover_IO_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[IO].recoverEitherFromNonFatalWith should not catch Fatal",
      IOSpec.testCanRecover_IO_recoverEitherFromNonFatalWithShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[IO].recoverEitherFromNonFatalWith should return the successful result",
      IOSpec.testCanRecover_IO_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[IO].recoverEitherFromNonFatalWith should return the failed result",
      IOSpec.testCanRecover_IO_recoverEitherFromNonFatalWithShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[IO].recoverEitherTFromNonFatalWith should catch NonFatal",
      IOSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[IO].recoverEitherTFromNonFatalWith should not catch Fatal",
      IOSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[IO].recoverEitherTFromNonFatalWith should return the successful result",
      IOSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[IO].recoverEitherTFromNonFatalWith should return the failed result",
      IOSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatal should catch NonFatal",
      IOSpec.testCanRecover_IO_recoverFromNonFatalShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatal should not catch Fatal",
      IOSpec.testCanRecover_IO_recoverFromNonFatalShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatal should return the successful result",
      IOSpec.testCanRecover_IO_recoverFromNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatalEither should catch NonFatal",
      IOSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatalEither should not catch Fatal",
      IOSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatalEither should return the successful result",
      IOSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatalEither should return the failed result",
      IOSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[IO].recoverEitherFromNonFatal should catch NonFatal",
      IOSpec.testCanRecover_IO_recoverEitherFromNonFatalShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[IO].recoverEitherFromNonFatal should not catch Fatal",
      IOSpec.testCanRecover_IO_recoverEitherFromNonFatalShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[IO].recoverEitherFromNonFatal should return the successful result",
      IOSpec.testCanRecover_IO_recoverEitherFromNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[IO].recoverEitherFromNonFatal should return the failed result",
      IOSpec.testCanRecover_IO_recoverEitherFromNonFatalShouldReturnFailedResult,
    ),
    example(
      "test CanRecover[IO].recoverEitherTFromNonFatal should catch NonFatal",
      IOSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldRecoverFromNonFatal,
    ),
    example(
      "test CanRecover[IO].recoverEitherTFromNonFatal should not catch Fatal",
      IOSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldNotCatchFatal,
    ),
    example(
      "test CanRecover[IO].recoverEitherTFromNonFatal should return the successful result",
      IOSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanRecover[IO].recoverEitherTFromNonFatal should return the failed result",
      IOSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnFailedResult,
    ),
  )

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor, A](a: => A): F[A] =
    effectOf[F](a)

  object IOSpec {
    import canRecover._

    def testCanRecover_IO_recoverFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 123
      val actual            = CanRecover[IO]
        .recoverFromNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) =>
            IO.pure(expected)
        }
        .unsafeRunSync()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_IO_recoverFromNonFatalWithShouldNotCatchFatal: Result = {

      val expectedExpcetion = SomeControlThrowable("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))

      val io = CanRecover[IO].recoverFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => IO.pure(123) }
      try {
        val actual = io.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = run[IO, Int](1)
      val expected = 1
      val actual   = CanRecover[IO]
        .recoverFromNonFatalWith(fa) {
          case NonFatal(_) => IO.pure(999)
        }
        .unsafeRunSync()

      actual ==== expected
    }

    def testCanRecover_IO_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
      val actualFailedResult   = CanRecover[IO]
        .recoverFromNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) => IO.pure(expectedFailedResult)
        }
        .unsafeRunSync()

      val expectedSuccessResult = 1.asRight[SomeError]
      val actualSuccessResult   = CanRecover[IO]
        .recoverFromNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) => IO.pure(1.asRight[SomeError])
        }
        .unsafeRunSync()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_IO_recoverFromNonFatalWithEitherShouldNotCatchFatal: Result = {

      val expectedExpcetion = SomeControlThrowable("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

      val io = CanRecover[IO].recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) => IO.pure(123.asRight[SomeError])
      }
      try {
        val actual = io.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanRecover[IO]
        .recoverFromNonFatalWith(fa) {
          case NonFatal(_) => IO(999.asRight[SomeError])
        }
        .unsafeRunSync()

      actual ==== expected
    }

    def testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanRecover[IO]
        .recoverFromNonFatalWith(fa) {
          case NonFatal(_) => IO.pure(123.asRight[SomeError])
        }
        .unsafeRunSync()

      actual ==== expected
    }

    def testCanRecover_IO_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult    = CanRecover[IO]
        .recoverEitherFromNonFatalWith(fa) {
          case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
        }
        .unsafeRunSync()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   = CanRecover[IO]
        .recoverEitherFromNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) => IO.pure(123.asRight[SomeError])
        }
        .unsafeRunSync()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_IO_recoverEitherFromNonFatalWithShouldNotCatchFatal: Result = {

      val expectedExpcetion = SomeControlThrowable("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

      val io = CanRecover[IO].recoverEitherFromNonFatalWith(fa) {
        case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
      }
      try {
        val actual = io.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanRecover[IO]
        .recoverEitherFromNonFatalWith(fa) {
          case NonFatal(_) => IO.pure(123.asRight[SomeError])
        }
        .unsafeRunSync()

      actual ==== expected
    }

    def testCanRecover_IO_recoverEitherFromNonFatalWithShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        CanRecover[IO]
          .recoverEitherFromNonFatalWith(fa) {
            case NonFatal(_) => IO.pure(123.asRight[SomeError])
          }
          .unsafeRunSync()

      actual ==== expected
    }

    def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult    = CanRecover[IO]
        .recoverEitherTFromNonFatalWith(fa) {
          case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
        }
        .value
        .unsafeRunSync()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   = CanRecover[IO]
        .recoverEitherTFromNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) => IO.pure(123.asRight[SomeError])
        }
        .value
        .unsafeRunSync()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldNotCatchFatal: Result = {

      val expectedExpcetion = SomeControlThrowable("Something's wrong")
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))

      val io = CanRecover[IO].recoverEitherTFromNonFatalWith(fa) {
        case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
      }
      try {
        val actual = io.value.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = CanRecover[IO]
        .recoverEitherTFromNonFatalWith(fa) {
          case NonFatal(_) => IO.pure(123.asRight[SomeError])
        }
        .value
        .unsafeRunSync()

      actual ==== expected
    }

    def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        CanRecover[IO]
          .recoverEitherTFromNonFatalWith(fa) {
            case NonFatal(_) => IO.pure(123.asRight[SomeError])
          }
          .value
          .unsafeRunSync()

      actual ==== expected
    }

    // /

    def testCanRecover_IO_recoverFromNonFatalShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 123
      val actual            = CanRecover[IO]
        .recoverFromNonFatal(fa) {
          case NonFatal(`expectedExpcetion`) =>
            expected
        }
        .unsafeRunSync()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_IO_recoverFromNonFatalShouldNotCatchFatal: Result = {

      val expectedExpcetion = SomeControlThrowable("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))

      val io = CanRecover[IO].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123 }
      try {
        val actual = io.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverFromNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = run[IO, Int](1)
      val expected = 1
      val actual   = CanRecover[IO].recoverFromNonFatal(fa) { case NonFatal(_) => 999 }.unsafeRunSync()

      actual ==== expected
    }

    def testCanRecover_IO_recoverFromNonFatalEitherShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
      val actualFailedResult   = CanRecover[IO]
        .recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expectedFailedResult }
        .unsafeRunSync()

      val expectedSuccessResult = 1.asRight[SomeError]
      val actualSuccessResult   = CanRecover[IO]
        .recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }
        .unsafeRunSync()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_IO_recoverFromNonFatalEitherShouldNotCatchFatal: Result = {

      val expectedExpcetion = SomeControlThrowable("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

      val io = CanRecover[IO].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }
      try {
        val actual = io.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverFromNonFatalEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual = CanRecover[IO].recoverFromNonFatal(fa) { case NonFatal(_) => 999.asRight[SomeError] }.unsafeRunSync()

      actual ==== expected
    }

    def testCanRecover_IO_recoverFromNonFatalEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual = CanRecover[IO].recoverFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }.unsafeRunSync()

      actual ==== expected
    }

    def testCanRecover_IO_recoverEitherFromNonFatalShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult    =
        CanRecover[IO]
          .recoverEitherFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }
          .unsafeRunSync()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        CanRecover[IO]
          .recoverEitherFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }
          .unsafeRunSync()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_IO_recoverEitherFromNonFatalShouldNotCatchFatal: Result = {

      val expectedExpcetion = SomeControlThrowable("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

      val io =
        CanRecover[IO].recoverEitherFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
      try {
        val actual = io.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverEitherFromNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   =
        CanRecover[IO]
          .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
          .unsafeRunSync()

      actual ==== expected
    }

    def testCanRecover_IO_recoverEitherFromNonFatalShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        CanRecover[IO]
          .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
          .unsafeRunSync()

      actual ==== expected
    }

    def testCanRecover_IO_recoverEitherTFromNonFatalShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult    =
        CanRecover[IO]
          .recoverEitherTFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }
          .value
          .unsafeRunSync()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        CanRecover[IO]
          .recoverEitherTFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }
          .value
          .unsafeRunSync()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_IO_recoverEitherTFromNonFatalShouldNotCatchFatal: Result = {

      val expectedExpcetion = SomeControlThrowable("Something's wrong")
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))

      val io =
        CanRecover[IO].recoverEitherTFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
      try {
        val actual = io.value.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   =
        CanRecover[IO]
          .recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
          .value
          .unsafeRunSync()

      actual ==== expected
    }

    def testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        CanRecover[IO]
          .recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
          .value
          .unsafeRunSync()

      actual ==== expected
    }

  }

}
