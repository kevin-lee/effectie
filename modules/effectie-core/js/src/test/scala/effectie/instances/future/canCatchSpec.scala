package effectie.instances.future

import effectie.core.{CanCatch, FxCtor}
import effectie.testing.FutureTools
import effectie.testing.types.SomeError

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{FiniteDuration, _}

/** @author Kevin Lee
  * @since 2022-01-05
  */
class canCatchSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  override val munitTimeout: FiniteDuration = 200.milliseconds

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor, A](a: => A): F[A] =
    FxCtor[F].effectOf(a)

  import effectie.instances.future.canCatch._
  import effectie.instances.future.fxCtor._

  import scala.concurrent.Future

  test("test CanCatch[Future].catchNonFatalThrowable should catch NonFatal") {

    val expectedExpcetion                       = new RuntimeException("Something's wrong")
    val expected: Either[RuntimeException, Int] = Left(expectedExpcetion)

    val fa = run[Future, Int](throwThrowable[Int](expectedExpcetion))

    CanCatch[Future].catchNonFatalThrowable(fa).map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test CanCatch[Future].catchNonFatal should catch NonFatal") {

    val expectedExpcetion                = new RuntimeException("Something's wrong")
    val expected: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))

    val fa = run[Future, Int](throwThrowable[Int](expectedExpcetion))

    CanCatch[Future].catchNonFatal(fa)(SomeError.someThrowable).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanCatch[Future].catchNonFatalThrowable should return the successful result") {

    val expected: Either[Throwable, Int] = Right(1)

    val fa = run[Future, Int](1)

    CanCatch[Future].catchNonFatalThrowable(fa).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanCatch[Future].catchNonFatal should return the successful result") {

    val expected: Either[SomeError, Int] = Right(1)

    val fa = run[Future, Int](1)
    CanCatch[Future].catchNonFatal(fa)(SomeError.someThrowable).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanCatch[Future].catchNonFatalEither should catch NonFatal") {

    val expectedExpcetion                = new RuntimeException("Something's wrong")
    val expected: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))

    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    CanCatch[Future].catchNonFatalEither(fa)(SomeError.someThrowable).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanCatch[Future].catchNonFatalEither should return the successful result") {

    val expected: Either[SomeError, Int] = Right(1)

    val fa = run[Future, Either[SomeError, Int]](Right(1))
    CanCatch[Future].catchNonFatalEither(fa)(SomeError.someThrowable).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanCatch[Future].catchNonFatalEither should return the failed result") {

    val expectedFailure                  = SomeError.message("Failed")
    val expected: Either[SomeError, Int] = Left(expectedFailure)

    val fa = run[Future, Either[SomeError, Int]](Left(expectedFailure))
    CanCatch[Future].catchNonFatalEither(fa)(SomeError.someThrowable).map { actual =>
      assertEquals(actual, expected)
    }

  }

}
