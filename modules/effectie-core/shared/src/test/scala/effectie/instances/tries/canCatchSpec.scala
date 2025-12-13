package effectie.instances.tries

import cats.syntax.all._
import effectie.SomeControlThrowable
import effectie.core._
import effectie.testing.types._
import hedgehog._
import hedgehog.runner._

import scala.util.{Success, Try}

/** @author Kevin Lee
  * @since 2023-03-17
  */
object canCatchSpec extends Properties {

  override def tests: List[Test] = ioSpecs

  val ioSpecs = List(
    /* Try */
    example(
      "test CanCatch[Try]catchNonFatalThrowable should catch NonFatal",
      TrySpec.testCanCatch_Try_catchNonFatalThrowableShouldCatchNonFatal,
    ),
    example(
      "test CanCatch[Try]catchNonFatalThrowable should not catch Fatal",
      TrySpec.testCanCatch_Try_catchNonFatalThrowableShouldNotCatchFatal,
    ),
    example(
      "test CanCatch[Try]catchNonFatalThrowable should return the successful result",
      TrySpec.testCanCatch_Try_catchNonFatalThrowableShouldReturnSuccessfulResult,
    ),
    example(
      "test CanCatch[Try]catchNonFatal should catch NonFatal",
      TrySpec.testCanCatch_Try_catchNonFatalShouldCatchNonFatal,
    ),
    example(
      "test CanCatch[Try]catchNonFatal should not catch Fatal",
      TrySpec.testCanCatch_Try_catchNonFatalShouldNotCatchFatal,
    ),
    example(
      "test CanCatch[Try]catchNonFatal should return the successful result",
      TrySpec.testCanCatch_Try_catchNonFatalShouldReturnSuccessfulResult,
    ),
    example(
      "test CanCatch[Try]catchNonFatalEither should catch NonFatal",
      TrySpec.testCanCatch_Try_catchNonFatalEitherShouldCatchNonFatal,
    ),
    example(
      "test CanCatch[Try]catchNonFatalEither should not catch Fatal",
      TrySpec.testCanCatch_Try_catchNonFatalEitherShouldNotCatchFatal,
    ),
    example(
      "test CanCatch[Try]catchNonFatalEither should return the successful result",
      TrySpec.testCanCatch_Try_catchNonFatalEitherShouldReturnSuccessfulResult,
    ),
    example(
      "test CanCatch[Try]catchNonFatalEither should return the failed result",
      TrySpec.testCanCatch_Try_catchNonFatalEitherShouldReturnFailedResult,
    ),
  )

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor, A](a: => A): F[A] =
    FxCtor[F].effectOf(a)

  object TrySpec {

    import effectie.instances.tries.canCatch._
    import effectie.instances.tries.fxCtor._

    def testCanCatch_Try_catchNonFatalThrowableShouldCatchNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong 1")
      val fa                = run[Try, Int](throwThrowable[Int](expectedException))
      val expected          = expectedException.asLeft[Int]
      val actual            = CanCatch[Try].catchNonFatalThrowable(fa)

      actual ==== Success(expected)
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanCatch_Try_catchNonFatalThrowableShouldNotCatchFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong 2")

      try {
        val fa     = run[Try, Int](throwThrowable[Int](fatalExpcetion))
        val actual = CanCatch[Try].catchNonFatalThrowable(fa)
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: SomeControlThrowable =>
          ex.getMessage ==== fatalExpcetion.getMessage

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_Try_catchNonFatalThrowableShouldReturnSuccessfulResult: Result = {

      val fa       = run[Try, Int](1)
      val expected = 1.asRight[Throwable]
      val actual   = CanCatch[Try].catchNonFatalThrowable(fa)

      actual ==== Success(expected)
    }

    def testCanCatch_Try_catchNonFatalShouldCatchNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong 3")
      val fa                = run[Try, Int](throwThrowable[Int](expectedException))
      val expected          = SomeError.someThrowable(expectedException).asLeft[Int]
      val actual            = CanCatch[Try].catchNonFatal(fa)(SomeError.someThrowable)

      actual ==== Success(expected)
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanCatch_Try_catchNonFatalShouldNotCatchFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong 4")

      try {
        val fa     = run[Try, Int](throwThrowable[Int](fatalExpcetion))
        val actual = CanCatch[Try].catchNonFatal(fa)(SomeError.someThrowable)
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: SomeControlThrowable =>
          ex.getMessage ==== fatalExpcetion.getMessage

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_Try_catchNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = run[Try, Int](1)
      val expected = 1.asRight[SomeError]
      val actual   = CanCatch[Try].catchNonFatal(fa)(SomeError.someThrowable)

      actual ==== Success(expected)
    }

    def testCanCatch_Try_catchNonFatalEitherShouldCatchNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong 5")
      val fa       = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
      val expected = SomeError.someThrowable(expectedException).asLeft[Int]
      val actual   = CanCatch[Try].catchNonFatalEither(fa)(SomeError.someThrowable)

      actual ==== Success(expected)
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanCatch_Try_catchNonFatalEitherShouldNotCatchFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong 6")

      try {
        val fa     = run[Try, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))
        val actual = CanCatch[Try].catchNonFatalEither(fa)(SomeError.someThrowable)
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: SomeControlThrowable =>
          ex.getMessage ==== fatalExpcetion.getMessage

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_Try_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[Try, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanCatch[Try].catchNonFatalEither(fa)(SomeError.someThrowable)

      actual ==== Success(expected)
    }

    def testCanCatch_Try_catchNonFatalEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Try, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanCatch[Try].catchNonFatalEither(fa)(SomeError.someThrowable)

      actual ==== Success(expected)
    }

  }

}
