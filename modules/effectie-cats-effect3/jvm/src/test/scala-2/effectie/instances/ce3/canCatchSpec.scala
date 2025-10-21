package effectie.instances.ce3

import canCatch._
import cats.data.EitherT
import cats.effect._
import cats.instances.all._
import cats.syntax.all._
import effectie.SomeControlThrowable
import effectie.core._
import effectie.syntax.error._
import effectie.syntax.fx._
import effectie.testing.types._
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.ErrorLogger
import extras.hedgehog.ce3.syntax.runner._
import fxCtor._
import hedgehog._
import hedgehog.runner._

import java.util.concurrent.ExecutorService

/** @author Kevin Lee
  * @since 2020-07-31
  */
object canCatchSpec extends Properties {

  implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = List(
    /* IO */
    example(
      "test CanCatch[IO]catchNonFatalThrowable should catch NonFatal",
      testCanCatch_IO_catchNonFatalThrowableShouldCatchNonFatal,
    ),
    example(
      "test CanCatch[IO]catchNonFatalThrowable should not catch Fatal",
      testCanCatch_IO_catchNonFatalThrowableShouldNotCatchFatal,
    ),
    example(
      "test CanCatch[IO]catchNonFatalThrowable should return the successful result",
      testCanCatch_IO_catchNonFatalThrowableShouldReturnSuccessfulResult,
    ),
    example(
      "test CanCatch[IO]catchNonFatal should catch NonFatal",
      testCanCatch_IO_catchNonFatalShouldCatchNonFatal,
    ),
    example(
      "test CanCatch[IO]catchNonFatal should not catch Fatal",
      testCanCatch_IO_catchNonFatalShouldNotCatchFatal,
    ),
    example(
      "test CanCatch[IO]catchNonFatal should return the successful result",
      testCanCatch_IO_catchNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanCatch[IO]catchNonFatalEither should catch NonFatal",
      testCanCatch_IO_catchNonFatalEitherShouldCatchNonFatal,
    ),
    example(
      "test CanCatch[IO]catchNonFatalEither should not catch Fatal",
      testCanCatch_IO_catchNonFatalEitherShouldNotCatchFatal,
    ),
    example(
      "test CanCatch[IO]catchNonFatalEither should return the successful result",
      testCanCatch_IO_catchNonFatalEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanCatch[IO]catchNonFatalEither should return the failed result",
      testCanCatch_IO_catchNonFatalEitherShouldReturnFailedResult,
    ),
    example(
      "test CanCatch[IO]catchNonFatalEitherT should catch NonFatal",
      testCanCatch_IO_catchNonFatalEitherTShouldCatchNonFatal,
    ),
    example(
      "test CanCatch[IO]catchNonFatalEitherT should not catch Fatal",
      testCanCatch_IO_catchNonFatalEitherTShouldNotCatchFatal,
    ),
    example(
      "test CanCatch[IO]catchNonFatalEitherT should return the successful result",
      testCanCatch_IO_catchNonFatalEitherTShouldReturnSuccessfulResult,
    ),
    example(
      "test CanCatch[IO]catchNonFatalEitherT should return the failed result",
      testCanCatch_IO_catchNonFatalEitherTShouldReturnFailedResult,
    ),
  )

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor, A](a: => A): F[A] =
    effectOf[F](a)

  def testCanCatch_IO_catchNonFatalThrowableShouldCatchNonFatal: Result = withIO { implicit ticker =>

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = expectedExpcetion.asLeft[Int]
    val actual            = CanCatch[IO].catchNonFatalThrowable(fa)

    actual.completeAs(expected)
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanCatch_IO_catchNonFatalThrowableShouldNotCatchFatal: Result = {

    val es: ExecutorService = ConcurrentSupport.newExecutorService(2)

    testing.IoAppUtils.runWithRuntime(testing.IoAppUtils.runtime(es)) { implicit rt =>

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))

      try {
        val actual = CanCatch[IO].catchNonFatalThrowable(fa).unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: SomeControlThrowable =>
          ex.getMessage ==== fatalExpcetion.getMessage

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }
    }

  }

  def testCanCatch_IO_catchNonFatalThrowableShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

    val fa: IO[Int] = run[IO, Int](1)
    val expected    = 1.asRight[Throwable]
    val actual      = CanCatch[IO].catchNonFatalThrowable(fa)

    actual.completeAs(expected)
  }

  def testCanCatch_IO_catchNonFatalShouldCatchNonFatal: Result = withIO { implicit ticker =>

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actual            = CanCatch[IO].catchNonFatal(fa)(SomeError.someThrowable)

    actual.completeAs(expected)
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanCatch_IO_catchNonFatalShouldNotCatchFatal: Result = {

    val es: ExecutorService = ConcurrentSupport.newExecutorService(2)
    testing.IoAppUtils.runWithRuntime(testing.IoAppUtils.runtime(es)) { implicit rt =>

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))

      try {
        val actual = CanCatch[IO].catchNonFatal(fa)(SomeError.someThrowable).unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: SomeControlThrowable =>
          ex.getMessage ==== fatalExpcetion.getMessage

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }
    }

  }

  def testCanCatch_IO_catchNonFatalShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

    val fa: IO[Int] = run[IO, Int](1)
    val expected    = 1.asRight[SomeError]
    val actual      = CanCatch[IO].catchNonFatal(fa)(SomeError.someThrowable)

    actual.completeAs(expected)
  }

  def testCanCatch_IO_catchNonFatalEitherShouldCatchNonFatal: Result = withIO { implicit ticker =>

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actual            = CanCatch[IO].catchNonFatalEither(fa)(SomeError.someThrowable)

    actual.completeAs(expected)
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanCatch_IO_catchNonFatalEitherShouldNotCatchFatal: Result = {

    val es: ExecutorService = ConcurrentSupport.newExecutorService(2)
    testing.IoAppUtils.runWithRuntime(testing.IoAppUtils.runtime(es)) { implicit rt =>

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual = CanCatch[IO].catchNonFatalEither(fa)(SomeError.someThrowable).unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: SomeControlThrowable =>
          ex.getMessage ==== fatalExpcetion.getMessage

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }
    }
  }

  def testCanCatch_IO_catchNonFatalEitherShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

    val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    val actual   = CanCatch[IO].catchNonFatalEither(fa)(SomeError.someThrowable)

    actual.completeAs(expected)
  }

  def testCanCatch_IO_catchNonFatalEitherShouldReturnFailedResult: Result = withIO { implicit ticker =>

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    val actual          = CanCatch[IO].catchNonFatalEither(fa)(SomeError.someThrowable)

    actual.completeAs(expected)
  }

  def testCanCatch_IO_catchNonFatalEitherTShouldCatchNonFatal: Result = withIO { implicit ticker =>

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa       = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actual   = CanCatch[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

    actual.completeAs(expected)
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanCatch_IO_catchNonFatalEitherTShouldNotCatchFatal: Result = {

    val es: ExecutorService = ConcurrentSupport.newExecutorService(2)
    testing.IoAppUtils.runWithRuntime(testing.IoAppUtils.runtime(es)) { implicit rt =>

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

      try {
        val actual = CanCatch[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: SomeControlThrowable =>
          ex.getMessage ==== fatalExpcetion.getMessage

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }
    }

  }

  def testCanCatch_IO_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

    val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]
    val actual   = CanCatch[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

    actual.completeAs(expected)
  }

  def testCanCatch_IO_catchNonFatalEitherTShouldReturnFailedResult: Result = withIO { implicit ticker =>

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]
    val actual          = CanCatch[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

    actual.completeAs(expected)
  }

}
