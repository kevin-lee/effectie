package effectie.instances.ce3

import cats.*
import cats.data.EitherT
import cats.effect.*
import cats.instances.all.*
import cats.syntax.all.*
import effectie.SomeControlThrowable
import effectie.core.*
import effectie.syntax.error.*
import effectie.syntax.fx.*
import effectie.testing.types.SomeError
import extras.hedgehog.ce3.syntax.runner._
import hedgehog.*
import hedgehog.runner.*

/** @author Kevin Lee
  * @since 2020-07-31
  */
object canCatchSpec extends Properties {

  override def tests: List[Test] = List(
    /* IO */
    example(
      "test CanCatch[IO]catchNonFatalThrowable should catch NonFatal",
      IoSpec.testCanCatch_IO_catchNonFatalThrowableShouldCatchNonFatal,
    ),
    example(
      "test CanCatch[IO]catchNonFatalThrowable should not catch Fatal",
      IoSpec.testCanCatch_IO_catchNonFatalThrowableShouldNotCatchFatal,
    ),
    example(
      "test CanCatch[IO]catchNonFatalThrowable should return the successful result",
      IoSpec.testCanCatch_IO_catchNonFatalThrowableShouldReturnSuccessfulResult,
    ),
    example(
      "test CanCatch[IO]catchNonFatal should catch NonFatal",
      IoSpec.testCanCatch_IO_catchNonFatalShouldCatchNonFatal,
    ),
    example(
      "test CanCatch[IO]catchNonFatal should not catch Fatal",
      IoSpec.testCanCatch_IO_catchNonFatalShouldNotCatchFatal,
    ),
    example(
      "test CanCatch[IO]catchNonFatal should return the successful result",
      IoSpec.testCanCatch_IO_catchNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanCatch[IO]catchNonFatalEither should catch NonFatal",
      IoSpec.testCanCatch_IO_catchNonFatalEitherShouldCatchNonFatal,
    ),
    example(
      "test CanCatch[IO]catchNonFatalEither should not catch Fatal",
      IoSpec.testCanCatch_IO_catchNonFatalEitherShouldNotCatchFatal,
    ),
    example(
      "test CanCatch[IO]catchNonFatalEither should return the successful result",
      IoSpec.testCanCatch_IO_catchNonFatalEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanCatch[IO]catchNonFatalEither should return the failed result",
      IoSpec.testCanCatch_IO_catchNonFatalEitherShouldReturnFailedResult,
    ),
    example(
      "test CanCatch[IO]catchNonFatalEitherT should catch NonFatal",
      IoSpec.testCanCatch_IO_catchNonFatalEitherTShouldCatchNonFatal,
    ),
    example(
      "test CanCatch[IO]catchNonFatalEitherT should not catch Fatal",
      IoSpec.testCanCatch_IO_catchNonFatalEitherTShouldNotCatchFatal,
    ),
    example(
      "test CanCatch[IO]catchNonFatalEitherT should return the successful result",
      IoSpec.testCanCatch_IO_catchNonFatalEitherTShouldReturnSuccessfulResult,
    ),
    example(
      "test CanCatch[IO]catchNonFatalEitherT should return the failed result",
      IoSpec.testCanCatch_IO_catchNonFatalEitherTShouldReturnFailedResult,
    ),
  )

  def throwThrowable[A](throwable: => Throwable): A = throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor: Functor, A](a: => A): F[A] = effectOf[F](a)

  object IoSpec {
    import effectie.instances.ce3.fx.given

    def testCanCatch_IO_catchNonFatalThrowableShouldCatchNonFatal: Result = withIO { implicit ticker =>

      val expectedException = new RuntimeException("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedException))
      val expected          = expectedException.asLeft[Int]
      val actual            = CanCatch[IO].catchNonFatalThrowable(fa)

      actual.completeAs(expected)
    }

    def testCanCatch_IO_catchNonFatalThrowableShouldNotCatchFatal: Result = {

      val fatalException = SomeControlThrowable("Something's wrong")

      try {
        /* `fa` is by-name, so throwing the fatal here happens synchronously while constructing the IO
         * (before any IO value exists), the instant catchNonFatalThrowable forces it. The fatal therefore
         * never enters cats-effect's run loop, so it cannot trip the process-global onFatalFailure latch
         * (which otherwise hangs every fatal test after the first when run on a live IORuntime).
         */
        val actual = CanCatch[IO].catchNonFatalThrowable[Int](throwThrowable[IO[Int]](fatalException))
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: SomeControlThrowable =>
          ex.getMessage ==== fatalException.getMessage

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_IO_catchNonFatalThrowableShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

      val fa: IO[Int] = run[IO, Int](1)
      val expected    = 1.asRight[Throwable]
      val actual      = CanCatch[IO].catchNonFatalThrowable(fa)

      actual.completeAs(expected)
    }

    def testCanCatch_IO_catchNonFatalShouldCatchNonFatal: Result = withIO { implicit ticker =>

      val expectedException = new RuntimeException("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedException))
      val expected          = SomeError.someThrowable(expectedException).asLeft[Int]
      val actual            = CanCatch[IO].catchNonFatal(fa)(SomeError.someThrowable)

      actual.completeAs(expected)
    }

    def testCanCatch_IO_catchNonFatalShouldNotCatchFatal: Result = {

      val fatalException = SomeControlThrowable("Something's wrong")

      try {
        /* See testCanCatch_IO_catchNonFatalThrowableShouldNotCatchFatal: the by-name `fb` is forced
         * synchronously, so the fatal is thrown during construction and never reaches the run loop.
         */
        val actual = CanCatch[IO].catchNonFatal(throwThrowable[IO[Int]](fatalException))(SomeError.someThrowable)
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: SomeControlThrowable =>
          ex.getMessage ==== fatalException.getMessage

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_IO_catchNonFatalShouldReturnSuccessfulResult: Result = withIO { implicit ticker =>

      val fa: IO[Int] = run[IO, Int](1)
      val expected    = 1.asRight[SomeError]
      val actual      = CanCatch[IO].catchNonFatal(fa)(SomeError.someThrowable)

      actual.completeAs(expected)
    }

    def testCanCatch_IO_catchNonFatalEitherShouldCatchNonFatal: Result = withIO { implicit ticker =>

      val expectedException = new RuntimeException("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
      val expected          = SomeError.someThrowable(expectedException).asLeft[Int]
      val actual            = CanCatch[IO].catchNonFatalEither(fa)(SomeError.someThrowable)

      actual.completeAs(expected)
    }

    def testCanCatch_IO_catchNonFatalEitherShouldNotCatchFatal: Result = {

      val fatalException = SomeControlThrowable("Something's wrong")

      try {
        /* See testCanCatch_IO_catchNonFatalThrowableShouldNotCatchFatal: the by-name `fab` is forced
         * synchronously, so the fatal is thrown during construction and never reaches the run loop.
         */
        val actual = CanCatch[IO]
          .catchNonFatalEither(throwThrowable[IO[Either[SomeError, Int]]](fatalException))(SomeError.someThrowable)
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: SomeControlThrowable =>
          ex.getMessage ==== fatalException.getMessage

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
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

      val expectedException = new RuntimeException("Something's wrong")
      val fa       = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
      val expected = SomeError.someThrowable(expectedException).asLeft[Int]
      val actual   = CanCatch[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

      actual.completeAs(expected)
    }

    def testCanCatch_IO_catchNonFatalEitherTShouldNotCatchFatal: Result = {

      val fatalException = SomeControlThrowable("Something's wrong")

      try {
        /* See testCanCatch_IO_catchNonFatalThrowableShouldNotCatchFatal: EitherT.apply evaluates its argument
         * eagerly, so the fatal is thrown on this line during construction and never reaches the run loop.
         */
        val fab    = EitherT(throwThrowable[IO[Either[SomeError, Int]]](fatalException))
        val actual = CanCatch[IO].catchNonFatalEitherT(fab)(SomeError.someThrowable).value
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: SomeControlThrowable =>
          ex.getMessage ==== fatalException.getMessage

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
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

}
