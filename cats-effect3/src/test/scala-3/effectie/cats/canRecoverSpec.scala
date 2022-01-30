package effectie.cats

import cats.*
import cats.data.EitherT
import cats.effect.IO
import effectie.cats.CatsEffectRunner.TestContext
import cats.effect.unsafe.IORuntime
import cats.instances.all.*
import cats.syntax.all.*
import effectie.core.*
import effectie.cats.fxCtor.*
import effectie.cats.canRecover.*
import effectie.syntax.fx.*
import effectie.cats.compat.CatsEffectIoCompatForFuture
import effectie.testing.types.SomeError
import effectie.SomeControlThrowable
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog.*
import hedgehog.runner.*

import scala.util.control.{ControlThrowable, NonFatal}

/** @author Kevin Lee
  * @since 2020-08-17
  */
object canRecoverSpec extends Properties {

  override def tests: List[Test] = ioSpecs ++ futureSpecs ++ idSpecs

  /* IO */
  val ioSpecs = List(
    example(
      "test CanRecover[IO].recoverFromNonFatalWith should catch NonFatal",
      IoSpec.testCanRecover_IO_recoverFromNonFatalWithShouldRecoverFromNonFatal
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatalWith should not catch Fatal",
      IoSpec.testCanRecover_IO_recoverFromNonFatalWithShouldNotCatchFatal
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatalWith should return the successful result",
      IoSpec.testCanRecover_IO_recoverFromNonFatalWithShouldReturnSuccessfulResult
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatalWithEither should catch NonFatal",
      IoSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatalWithEither should not catch Fatal",
      IoSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldNotCatchFatal
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatalWithEither should return the successful result",
      IoSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatalWithEither should return the failed result",
      IoSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnFailedResult
    ),
    example(
      "test CanRecover[IO].recoverEitherFromNonFatalWith should catch NonFatal",
      IoSpec.testCanRecover_IO_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal
    ),
    example(
      "test CanRecover[IO].recoverEitherFromNonFatalWith should not catch Fatal",
      IoSpec.testCanRecover_IO_recoverEitherFromNonFatalWithShouldNotCatchFatal
    ),
    example(
      "test CanRecover[IO].recoverEitherFromNonFatalWith should return the successful result",
      IoSpec.testCanRecover_IO_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult
    ),
    example(
      "test CanRecover[IO].recoverEitherFromNonFatalWith should return the failed result",
      IoSpec.testCanRecover_IO_recoverEitherFromNonFatalWithShouldReturnFailedResult
    ),
    example(
      "test CanRecover[IO].recoverEitherTFromNonFatalWith should catch NonFatal",
      IoSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal
    ),
    example(
      "test CanRecover[IO].recoverEitherTFromNonFatalWith should not catch Fatal",
      IoSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldNotCatchFatal
    ),
    example(
      "test CanRecover[IO].recoverEitherTFromNonFatalWith should return the successful result",
      IoSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult
    ),
    example(
      "test CanRecover[IO].recoverEitherTFromNonFatalWith should return the failed result",
      IoSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnFailedResult
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatal should catch NonFatal",
      IoSpec.testCanRecover_IO_recoverFromNonFatalShouldRecoverFromNonFatal
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatal should not catch Fatal",
      IoSpec.testCanRecover_IO_recoverFromNonFatalShouldNotCatchFatal
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatal should return the successful result",
      IoSpec.testCanRecover_IO_recoverFromNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatalEither should catch NonFatal",
      IoSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldRecoverFromNonFatal
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatalEither should not catch Fatal",
      IoSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldNotCatchFatal
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatalEither should return the successful result",
      IoSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldReturnSuccessfulResult
    ),
    example(
      "test CanRecover[IO].recoverFromNonFatalEither should return the failed result",
      IoSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldReturnFailedResult
    ),
    example(
      "test CanRecover[IO].recoverEitherFromNonFatal should catch NonFatal",
      IoSpec.testCanRecover_IO_recoverEitherFromNonFatalShouldRecoverFromNonFatal
    ),
    example(
      "test CanRecover[IO].recoverEitherFromNonFatal should not catch Fatal",
      IoSpec.testCanRecover_IO_recoverEitherFromNonFatalShouldNotCatchFatal
    ),
    example(
      "test CanRecover[IO].recoverEitherFromNonFatal should return the successful result",
      IoSpec.testCanRecover_IO_recoverEitherFromNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanRecover[IO].recoverEitherFromNonFatal should return the failed result",
      IoSpec.testCanRecover_IO_recoverEitherFromNonFatalShouldReturnFailedResult
    ),
    example(
      "test CanRecover[IO].recoverEitherTFromNonFatal should catch NonFatal",
      IoSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldRecoverFromNonFatal
    ),
    example(
      "test CanRecover[IO].recoverEitherTFromNonFatal should not catch Fatal",
      IoSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldNotCatchFatal
    ),
    example(
      "test CanRecover[IO].recoverEitherTFromNonFatal should return the successful result",
      IoSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanRecover[IO].recoverEitherTFromNonFatal should return the failed result",
      IoSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnFailedResult
    ),
  )

  /* Future */
  val futureSpecs = effectie.core.CanRecoverSpec.futureSpecs ++ List(
    example(
      "test CanRecover[Future].recoverEitherTFromNonFatalWith should catch NonFatal",
      FutureSpec.testCanRecover_Future_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal
    ),
    example(
      "test CanRecover[Future].recoverEitherTFromNonFatalWith should return the successful result",
      FutureSpec.testCanRecover_Future_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult
    ),
    example(
      "test CanRecover[Future].recoverEitherTFromNonFatalWith should return the failed result",
      FutureSpec.testCanRecover_Future_recoverEitherTFromNonFatalWithShouldReturnFailedResult
    ),
    example(
      "test CanRecover[Future].recoverEitherTFromNonFatal should catch NonFatal",
      FutureSpec.testCanRecover_Future_recoverEitherTFromNonFatalShouldRecoverFromNonFatal
    ),
    example(
      "test CanRecover[Future].recoverEitherTFromNonFatal should return the successful result",
      FutureSpec.testCanRecover_Future_recoverEitherTFromNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanRecover[Future].recoverEitherTFromNonFatal should return the failed result",
      FutureSpec.testCanRecover_Future_recoverEitherTFromNonFatalShouldReturnFailedResult
    ),
  )

  val idSpecs = List(
    /* Id */
    example(
      "test CanRecover[Id].recoverFromNonFatalWith should catch NonFatal",
      IdSpec.testCanRecover_Id_recoverFromNonFatalWithShouldRecoverFromNonFatal
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatalWith should not catch Fatal",
      IdSpec.testCanRecover_Id_recoverFromNonFatalWithShouldNotCatchFatal
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatalWith should return the successful result",
      IdSpec.testCanRecover_Id_recoverFromNonFatalWithShouldReturnSuccessfulResult
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatalWithEither should catch NonFatal",
      IdSpec.testCanRecover_Id_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatalWithEither should not catch Fatal",
      IdSpec.testCanRecover_Id_recoverFromNonFatalWithEitherShouldNotCatchFatal
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatalWithEither should return the successful result",
      IdSpec.testCanRecover_Id_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatalWithEither should return the failed result",
      IdSpec.testCanRecover_Id_recoverFromNonFatalWithEitherShouldReturnFailedResult
    ),
    example(
      "test CanRecover[Id].recoverEitherFromNonFatalWith should catch NonFatal",
      IdSpec.testCanRecover_Id_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal
    ),
    example(
      "test CanRecover[Id].recoverEitherFromNonFatalWith should not catch Fatal",
      IdSpec.testCanRecover_Id_recoverEitherFromNonFatalWithShouldNotCatchFatal
    ),
    example(
      "test CanRecover[Id].recoverEitherFromNonFatalWith should return the successful result",
      IdSpec.testCanRecover_Id_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult
    ),
    example(
      "test CanRecover[Id].recoverEitherFromNonFatalWith should return the failed result",
      IdSpec.testCanRecover_Id_recoverEitherFromNonFatalWithShouldReturnFailedResult
    ),
    example(
      "test CanRecover[Id].recoverEitherTFromNonFatalWith should catch NonFatal",
      IdSpec.testCanRecover_Id_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal
    ),
    example(
      "test CanRecover[Id].recoverEitherTFromNonFatalWith should not catch Fatal",
      IdSpec.testCanRecover_Id_recoverEitherTFromNonFatalWithShouldNotCatchFatal
    ),
    example(
      "test CanRecover[Id].recoverEitherTFromNonFatalWith should return the successful result",
      IdSpec.testCanRecover_Id_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult
    ),
    example(
      "test CanRecover[Id].recoverEitherTFromNonFatalWith should return the failed result",
      IdSpec.testCanRecover_Id_recoverEitherTFromNonFatalWithShouldReturnFailedResult
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatal should catch NonFatal",
      IdSpec.testCanRecover_Id_recoverFromNonFatalShouldRecoverFromNonFatal
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatal should not catch Fatal",
      IdSpec.testCanRecover_Id_recoverFromNonFatalShouldNotCatchFatal
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatal should return the successful result",
      IdSpec.testCanRecover_Id_recoverFromNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatalEither should catch NonFatal",
      IdSpec.testCanRecover_Id_recoverFromNonFatalEitherShouldRecoverFromNonFatal
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatalEither should not catch Fatal",
      IdSpec.testCanRecover_Id_recoverFromNonFatalEitherShouldNotCatchFatal
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatalEither should return the successful result",
      IdSpec.testCanRecover_Id_recoverFromNonFatalEitherShouldReturnSuccessfulResult
    ),
    example(
      "test CanRecover[Id].recoverFromNonFatalEither should return the failed result",
      IdSpec.testCanRecover_Id_recoverFromNonFatalEitherShouldReturnFailedResult
    ),
    example(
      "test CanRecover[Id].recoverEitherFromNonFatal should catch NonFatal",
      IdSpec.testCanRecover_Id_recoverEitherFromNonFatalShouldRecoverFromNonFatal
    ),
    example(
      "test CanRecover[Id].recoverEitherFromNonFatal should not catch Fatal",
      IdSpec.testCanRecover_Id_recoverEitherFromNonFatalShouldNotCatchFatal
    ),
    example(
      "test CanRecover[Id].recoverEitherFromNonFatal should return the successful result",
      IdSpec.testCanRecover_Id_recoverEitherFromNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanRecover[Id].recoverEitherFromNonFatal should return the failed result",
      IdSpec.testCanRecover_Id_recoverEitherFromNonFatalShouldReturnFailedResult
    ),
    example(
      "test CanRecover[Id].recoverEitherTFromNonFatal should catch NonFatal",
      IdSpec.testCanRecover_Id_recoverEitherTFromNonFatalShouldRecoverFromNonFatal
    ),
    example(
      "test CanRecover[Id].recoverEitherTFromNonFatal should not catch Fatal",
      IdSpec.testCanRecover_Id_recoverEitherTFromNonFatalShouldNotCatchFatal
    ),
    example(
      "test CanRecover[Id].recoverEitherTFromNonFatal should return the successful result",
      IdSpec.testCanRecover_Id_recoverEitherTFromNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanRecover[Id].recoverEitherTFromNonFatal should return the failed result",
      IdSpec.testCanRecover_Id_recoverEitherTFromNonFatalShouldReturnFailedResult
    )
  )

  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable

  def run[F[*]: FxCtor: Functor, A](a: => A): F[A] =
    effectOf[F](a)

  object IoSpec {
    import effectie.cats.fx.given
    import effectie.cats.canRecover.given

    def testCanRecover_IO_recoverFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 123

      val actual = CanRecover[IO].recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) =>
          IO.pure(expected)
      }

      actual.completeAs(expected)
    }

    def testCanRecover_IO_recoverFromNonFatalWithShouldNotCatchFatal: Result = {

      val compat          = new CatsEffectIoCompatForFuture
      given rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

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

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val fa       = run[IO, Int](1)
      val expected = 1
      val actual   = CanRecover[IO]
        .recoverFromNonFatalWith(fa) {
          case NonFatal(_) => IO.pure(999)
        }

      actual.completeAs(expected)
    }

    def testCanRecover_IO_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal: Result = {
      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
      val actualFailedResult   = CanRecover[IO]
        .recoverFromNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) => IO.pure(expectedFailedResult)
        }

      val expectedSuccessResult = 1.asRight[SomeError]
      val actualSuccessResult   = CanRecover[IO]
        .recoverFromNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) => IO.pure(1.asRight[SomeError])
        }

      actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
    }

    def testCanRecover_IO_recoverFromNonFatalWithEitherShouldNotCatchFatal: Result = {

      val compat          = new CatsEffectIoCompatForFuture
      given rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

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

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanRecover[IO]
        .recoverFromNonFatalWith(fa) {
          case NonFatal(_) => IO(999.asRight[SomeError])
        }

      actual.completeAs(expected)
    }

    def testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnFailedResult: Result = {

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanRecover[IO]
        .recoverFromNonFatalWith(fa) {
          case NonFatal(_) => IO.pure(123.asRight[SomeError])
        }

      actual.completeAs(expected)
    }

    def testCanRecover_IO_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   = CanRecover[IO]
        .recoverEitherFromNonFatalWith(fa) {
          case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
        }

      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   = CanRecover[IO]
        .recoverEitherFromNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) => IO.pure(123.asRight[SomeError])
        }

      actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
    }

    def testCanRecover_IO_recoverEitherFromNonFatalWithShouldNotCatchFatal: Result = {

      val compat          = new CatsEffectIoCompatForFuture
      given rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

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

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanRecover[IO]
        .recoverEitherFromNonFatalWith(fa) {
          case NonFatal(_) => IO.pure(123.asRight[SomeError])
        }

      actual.completeAs(expected)
    }

    def testCanRecover_IO_recoverEitherFromNonFatalWithShouldReturnFailedResult: Result = {

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

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

    def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

      val actualFailedResult = CanRecover[IO]
        .recoverEitherTFromNonFatalWith(fa) {
          case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
        }
        .value

      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   = CanRecover[IO]
        .recoverEitherTFromNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) => IO.pure(123.asRight[SomeError])
        }
        .value

      actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
    }

    def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldNotCatchFatal: Result = {

      val compat          = new CatsEffectIoCompatForFuture
      given rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

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

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = CanRecover[IO]
        .recoverEitherTFromNonFatalWith(fa) {
          case NonFatal(_) => IO.pure(123.asRight[SomeError])
        }
        .value

      actual.completeAs(expected)
    }

    def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnFailedResult: Result = {

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

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

    def testCanRecover_IO_recoverFromNonFatalShouldRecoverFromNonFatal: Result = {

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 123
      val actual            = CanRecover[IO]
        .recoverFromNonFatal(fa) {
          case NonFatal(`expectedExpcetion`) =>
            expected
        }

      actual.completeAs(expected)
    }

    def testCanRecover_IO_recoverFromNonFatalShouldNotCatchFatal: Result = {

      val compat          = new CatsEffectIoCompatForFuture
      given rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

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

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val fa       = run[IO, Int](1)
      val expected = 1
      val actual   = CanRecover[IO].recoverFromNonFatal(fa) { case NonFatal(_) => 999 }

      actual.completeAs(expected)
    }

    def testCanRecover_IO_recoverFromNonFatalEitherShouldRecoverFromNonFatal: Result = {

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
      val actualFailedResult   = CanRecover[IO]
        .recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expectedFailedResult }

      val expectedSuccessResult = 1.asRight[SomeError]
      val actualSuccessResult   = CanRecover[IO]
        .recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }

      actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
    }

    def testCanRecover_IO_recoverFromNonFatalEitherShouldNotCatchFatal: Result = {

      val compat          = new CatsEffectIoCompatForFuture
      given rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

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

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanRecover[IO].recoverFromNonFatal(fa) { case NonFatal(_) => 999.asRight[SomeError] }

      actual.completeAs(expected)
    }

    def testCanRecover_IO_recoverFromNonFatalEitherShouldReturnFailedResult: Result = {

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanRecover[IO].recoverFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }

      actual.completeAs(expected)
    }

    def testCanRecover_IO_recoverEitherFromNonFatalShouldRecoverFromNonFatal: Result = {

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   =
        CanRecover[IO]
          .recoverEitherFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }

      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        CanRecover[IO]
          .recoverEitherFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }

      actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
    }

    def testCanRecover_IO_recoverEitherFromNonFatalShouldNotCatchFatal: Result = {

      val compat          = new CatsEffectIoCompatForFuture
      given rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

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

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   =
        CanRecover[IO]
          .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }

      actual.completeAs(expected)
    }

    def testCanRecover_IO_recoverEitherFromNonFatalShouldReturnFailedResult: Result = {

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        CanRecover[IO]
          .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }

      actual.completeAs(expected)
    }

    def testCanRecover_IO_recoverEitherTFromNonFatalShouldRecoverFromNonFatal: Result = {

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   =
        CanRecover[IO]
          .recoverEitherTFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }
          .value

      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        CanRecover[IO]
          .recoverEitherTFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }
          .value

      actualFailedResult.completeAs(expectedFailedResult) and actualSuccessResult.completeAs(expectedSuccessResult)
    }

    def testCanRecover_IO_recoverEitherTFromNonFatalShouldNotCatchFatal: Result = {

      val compat          = new CatsEffectIoCompatForFuture
      given rt: IORuntime = testing.IoAppUtils.runtime(compat.es)

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

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   =
        CanRecover[IO]
          .recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
          .value

      actual.completeAs(expected)
    }

    def testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnFailedResult: Result = {

      import effectie.cats.CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

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

  object FutureSpec {
    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration.*
    import scala.concurrent.{ExecutionContext, Future}
    import scala.util.control.NonFatal

    private given errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

    private val waitFor = WaitFor(1.second)

    def testCanRecover_Future_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   = ConcurrentSupport.futureToValue(
        CanRecover[Future]
          .recoverEitherTFromNonFatalWith(fa) {
            case err => Future(SomeError.someThrowable(err).asLeft[Int])
          }
          .value,
        waitFor
      )

      val fa2 = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(
          CanRecover[Future]
            .recoverEitherTFromNonFatalWith(fa2) {
              case err => Future(expected)
            }
            .value
        )

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(
        CanRecover[Future]
          .recoverEitherTFromNonFatalWith(fa) {
            case err => Future(SomeError.someThrowable(err).asLeft[Int])
          }
          .value
      )

      actual ==== expected
    }

    def testCanRecover_Future_recoverEitherTFromNonFatalWithShouldReturnFailedResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(
          CanRecover[Future]
            .recoverEitherTFromNonFatalWith(fa) {
              case NonFatal(_) => Future(expected)
            }
            .value
        )

      actual ==== expected
    }

    // /

    def testCanRecover_Future_recoverEitherTFromNonFatalShouldRecoverFromNonFatal: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   = ConcurrentSupport.futureToValue(
        CanRecover[Future]
          .recoverEitherTFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }
          .value,
        waitFor
      )

      val fa2 = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(CanRecover[Future].recoverEitherTFromNonFatal(fa2) { case err => expected }.value)

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverEitherTFromNonFatalShouldReturnSuccessfulResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(
        CanRecover[Future]
          .recoverEitherTFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }
          .value
      )

      actual ==== expected
    }

    def testCanRecover_Future_recoverEitherTFromNonFatalShouldReturnFailedResult: Result = {

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(CanRecover[Future].recoverEitherTFromNonFatal(fa) { case NonFatal(_) => expected }.value)

      actual ==== expected
    }

  }

  object IdSpec {
    import effectie.cats.fx.given
    import effectie.cats.canRecover.given

    def testCanRecover_Id_recoverFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 1
      val actual: Id[Int]   = CanRecover[Id].recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) => expected
      }

      actual ==== expected
    }

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

}
