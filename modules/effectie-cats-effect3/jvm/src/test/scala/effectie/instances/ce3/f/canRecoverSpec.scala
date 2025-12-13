package effectie.instances.ce3.f

import canRecover._
import cats.data.EitherT
import cats.effect.IO
import cats.effect.unsafe.IORuntime
import cats.instances.all._
import cats.syntax.all._
import effectie.SomeControlThrowable
import effectie.core._
import effectie.instances.ce3.compat.CatsEffectIoCompatForFuture
import effectie.syntax.error._
import effectie.syntax.fx._
import effectie.instances.ce3.testing
import effectie.testing.types.SomeError
import extras.concurrent.testing.types.ErrorLogger
import extras.hedgehog.ce3.syntax.runner._
import fxCtor._
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

    def testCanRecover_IO_recoverFromNonFatalWithShouldRecoverFromNonFatal: Result = withIO { implicit ticker =>

      val expectedException = new RuntimeException("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedException))
      val expected          = 123
      val actual            = CanRecover[IO].recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedException`) =>
          IO.pure(expected)
      }
      actual.completeAs(expected)

    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_IO_recoverFromNonFatalWithShouldNotCatchFatal: Result = {

      val compat                 = new CatsEffectIoCompatForFuture
      implicit val rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

      val expectedException = SomeControlThrowable("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedException))

      val io = CanRecover[IO].recoverFromNonFatalWith(fa) { case NonFatal(`expectedException`) => IO.pure(123) }
      try {
        val actual = io.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedException

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverFromNonFatalWithShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

      val expected = 1
      val fa       = run[IO, Int](expected)
      val actual   = CanRecover[IO]
        .recoverFromNonFatalWith(fa) {
          case NonFatal(_) => IO.pure(999)
        }

      actual.completeAs(expected)
    }

    def testCanRecover_IO_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal: Result = withIO { implicit ticker =>

      val expectedException = new RuntimeException("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
      val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
      val actualFailedResult   = CanRecover[IO]
        .recoverFromNonFatalWith(fa) {
          case NonFatal(`expectedException`) => IO.pure(expectedFailedResult)
        }

      val expectedSuccessResult = 1.asRight[SomeError]
      val actualSuccessResult   = CanRecover[IO]
        .recoverFromNonFatalWith(fa) {
          case NonFatal(`expectedException`) => IO.pure(1.asRight[SomeError])
        }

      actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_IO_recoverFromNonFatalWithEitherShouldNotCatchFatal: Result = {

      val compat                 = new CatsEffectIoCompatForFuture
      implicit val rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

      val expectedException = SomeControlThrowable("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))

      val io = CanRecover[IO].recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedException`) => IO.pure(123.asRight[SomeError])
      }
      try {
        val actual = io.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedException

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult: Result =
      withIO { implicit ticker =>

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = CanRecover[IO]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(_) => IO(999.asRight[SomeError])
          }

        actual.completeAs(expected)
      }

    def testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnFailedResult: Result = withIO { implicit ticker =>

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanRecover[IO]
        .recoverFromNonFatalWith(fa) {
          case NonFatal(_) => IO.pure(123.asRight[SomeError])
        }

      actual.completeAs(expected)
    }

    def testCanRecover_IO_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal: Result = withIO { implicit ticker =>

      val expectedException = new RuntimeException("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
      val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
      val actualFailedResult   = CanRecover[IO]
        .recoverEitherFromNonFatalWith(fa) {
          case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
        }

      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   = CanRecover[IO]
        .recoverEitherFromNonFatalWith(fa) {
          case NonFatal(`expectedException`) => IO.pure(123.asRight[SomeError])
        }

      actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_IO_recoverEitherFromNonFatalWithShouldNotCatchFatal: Result = {

      val compat                 = new CatsEffectIoCompatForFuture
      implicit val rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

      val expectedException = SomeControlThrowable("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))

      val io = CanRecover[IO].recoverEitherFromNonFatalWith(fa) {
        case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
      }
      try {
        val actual = io.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedException

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult: Result =
      withIO { implicit ticker =>

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = CanRecover[IO]
          .recoverEitherFromNonFatalWith(fa) {
            case NonFatal(_) => IO.pure(123.asRight[SomeError])
          }

        actual.completeAs(expected)
      }

    def testCanRecover_IO_recoverEitherFromNonFatalWithShouldReturnFailedResult: Result = withIO { implicit ticker =>

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        CanRecover[IO]
          .recoverEitherFromNonFatalWith(fa) {
            case NonFatal(_) => IO.pure(123.asRight[SomeError])
          }

      actual.completeAs(expected)
    }

    def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal: Result = withIO { implicit ticker =>

      val expectedException = new RuntimeException("Something's wrong")
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
      val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
      val actualFailedResult   = CanRecover[IO]
        .recoverEitherTFromNonFatalWith(fa) {
          case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
        }
        .value

      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   = CanRecover[IO]
        .recoverEitherTFromNonFatalWith(fa) {
          case NonFatal(`expectedException`) => IO.pure(123.asRight[SomeError])
        }
        .value

      actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldNotCatchFatal: Result = {

      val compat                 = new CatsEffectIoCompatForFuture
      implicit val rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

      val expectedException = SomeControlThrowable("Something's wrong")
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))

      val io = CanRecover[IO].recoverEitherTFromNonFatalWith(fa) {
        case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
      }
      try {
        val actual = io.value.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedException

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult: Result =
      withIO { implicit ticker =>

        val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = CanRecover[IO]
          .recoverEitherTFromNonFatalWith(fa) {
            case NonFatal(_) => IO.pure(123.asRight[SomeError])
          }
          .value

        actual.completeAs(expected)
      }

    def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnFailedResult: Result = withIO { implicit ticker =>

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        CanRecover[IO]
          .recoverEitherTFromNonFatalWith(fa) {
            case NonFatal(_) => IO.pure(123.asRight[SomeError])
          }
          .value

      actual.completeAs(expected)
    }

    // /

    def testCanRecover_IO_recoverFromNonFatalShouldRecoverFromNonFatal: Result = withIO { implicit ticker =>

      val expectedException = new RuntimeException("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedException))
      val expected          = 123
      val actual            = CanRecover[IO]
        .recoverFromNonFatal(fa) {
          case NonFatal(`expectedException`) =>
            expected
        }

      actual.completeAs(expected)
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_IO_recoverFromNonFatalShouldNotCatchFatal: Result = {

      val compat                 = new CatsEffectIoCompatForFuture
      implicit val rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

      val expectedException = SomeControlThrowable("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedException))

      val io = CanRecover[IO].recoverFromNonFatal(fa) { case NonFatal(`expectedException`) => 123 }
      try {
        val actual = io.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedException

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverFromNonFatalShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

      val fa       = run[IO, Int](1)
      val expected = 1
      val actual   = CanRecover[IO].recoverFromNonFatal(fa) { case NonFatal(_) => 999 }

      actual.completeAs(expected)
    }

    def testCanRecover_IO_recoverFromNonFatalEitherShouldRecoverFromNonFatal: Result = withIO { implicit ticker =>

      val expectedException = new RuntimeException("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
      val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
      val actualFailedResult   = CanRecover[IO]
        .recoverFromNonFatal(fa) { case NonFatal(`expectedException`) => expectedFailedResult }

      val expectedSuccessResult = 1.asRight[SomeError]
      val actualSuccessResult   = CanRecover[IO]
        .recoverFromNonFatal(fa) { case NonFatal(`expectedException`) => 1.asRight[SomeError] }

      actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_IO_recoverFromNonFatalEitherShouldNotCatchFatal: Result = {

      val compat                 = new CatsEffectIoCompatForFuture
      implicit val rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

      val expectedException = SomeControlThrowable("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))

      val io = CanRecover[IO].recoverFromNonFatal(fa) { case NonFatal(`expectedException`) => 123.asRight[SomeError] }
      try {
        val actual = io.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedException

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverFromNonFatalEitherShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

      val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanRecover[IO].recoverFromNonFatal(fa) { case NonFatal(_) => 999.asRight[SomeError] }

      actual.completeAs(expected)
    }

    def testCanRecover_IO_recoverFromNonFatalEitherShouldReturnFailedResult: Result = withIO { implicit ticker =>

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanRecover[IO].recoverFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }

      actual.completeAs(expected)
    }

    def testCanRecover_IO_recoverEitherFromNonFatalShouldRecoverFromNonFatal: Result = withIO { implicit ticker =>

      val expectedException = new RuntimeException("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
      val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
      val actualFailedResult   =
        CanRecover[IO]
          .recoverEitherFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }

      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        CanRecover[IO]
          .recoverEitherFromNonFatal(fa) { case NonFatal(`expectedException`) => 123.asRight[SomeError] }

      actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_IO_recoverEitherFromNonFatalShouldNotCatchFatal: Result = {

      val compat                 = new CatsEffectIoCompatForFuture
      implicit val rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

      val expectedException = SomeControlThrowable("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))

      val io =
        CanRecover[IO].recoverEitherFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
      try {
        val actual = io.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedException

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverEitherFromNonFatalShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

      val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   =
        CanRecover[IO]
          .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }

      actual.completeAs(expected)
    }

    def testCanRecover_IO_recoverEitherFromNonFatalShouldReturnFailedResult: Result = withIO { implicit ticker =>

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        CanRecover[IO]
          .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }

      actual.completeAs(expected)
    }

    def testCanRecover_IO_recoverEitherTFromNonFatalShouldRecoverFromNonFatal: Result = withIO { implicit ticker =>

      val expectedException = new RuntimeException("Something's wrong")
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
      val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
      val actualFailedResult   =
        CanRecover[IO]
          .recoverEitherTFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }
          .value

      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        CanRecover[IO]
          .recoverEitherTFromNonFatal(fa) { case NonFatal(`expectedException`) => 123.asRight[SomeError] }
          .value

      actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_IO_recoverEitherTFromNonFatalShouldNotCatchFatal: Result = {

      val compat                 = new CatsEffectIoCompatForFuture
      implicit val rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

      val expectedException = SomeControlThrowable("Something's wrong")
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))

      val io =
        CanRecover[IO].recoverEitherTFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
      try {
        val actual = io.value.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedException

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

      val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   =
        CanRecover[IO]
          .recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
          .value

      actual.completeAs(expected)
    }

    def testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnFailedResult: Result = withIO { implicit ticker =>

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        CanRecover[IO]
          .recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
          .value

      actual.completeAs(expected)
    }

  }

}
