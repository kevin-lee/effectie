package effectie.instances.future

import cats.syntax.all._
import effectie.core.FxCtor
import effectie.testing.FutureTools
import effectie.testing.types.SomeThrowableError

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.Try

/** @author Kevin Lee
  * @since 2022-01-06
  */
class fxCtorSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  override val munitTimeout: FiniteDuration = 200.milliseconds

  import effectie.instances.future.fxCtor._

  import scala.concurrent.Future

  test("test FxCtor[Future].effectOf") {
    val before = 123
    val after  = 999

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var actual = before // scalafix:ok DisableSyntax.var
    assertEquals(actual, before)

    val future: Future[Unit] = FxCtor[Future].effectOf({
      actual = after; ()
    })
    future.map { _ => assertEquals(actual, after) }
  }

  test("test FxCtor[Future].fromEffect(effectOf)") {
    val before = 123
    val after  = 999

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var actual = before // scalafix:ok DisableSyntax.var
    assertEquals(actual, before)

    lazy val fromFuture = FxCtor[Future].fromEffect(FxCtor[Future].effectOf({ actual = after; () }))

    assertEquals(actual, before)

    fromFuture.map { _ => assertEquals(actual, after) }
  }

  test("test FxCtor[Future].fromEffect(pureOf)") {
    val before = 123
    val after  = 999

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var actual     = before // scalafix:ok DisableSyntax.var
    assertEquals(actual, before)
    val fromFuture = FxCtor[Future].fromEffect(FxCtor[Future].pureOf({ actual = after; () }))
    fromFuture.map { _ => assertEquals(actual, after) }
  }

  test("test FxCtor[Future].pureOf") {
    val before = 123
    val after  = 999

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var actual = before // scalafix:ok DisableSyntax.var
    assertEquals(actual, before)
    val future = FxCtor[Future].pureOf({ actual = after; () })
    future.map { _ => assertEquals(actual, after) }
  }

  test("test FxCtor[Future].pureOrError(success case)") {
    val before = 123
    val after  = 999

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var actual = before // scalafix:ok DisableSyntax.var
    assertEquals(actual, before)

    val future = FxCtor[Future].pureOrError({ actual = after; () })
    future.map { _ => assertEquals(actual, after) }

  }

  test("test FxCtor[Future].pureOrError(error case)") {
    val expectedMessage = "This is a throwable test error."
    val expectedError   = SomeThrowableError.message(expectedMessage)

    val future = FxCtor[Future].pureOrError[Unit](throw expectedError) // scalafix:ok DisableSyntax.throw

    future
      .map { _ =>
        fail("This should not be called.")
      }
      .recover {
        case ex: Throwable =>
          assertEquals(ex, expectedError)
      }
  }

  test("test FxCtor[Future].unitOf") {

    val future         = FxCtor[Future].unitOf
    val expected: Unit = ()

    future.map { actual => assertEquals(actual, expected) }
  }

  test("test FxCtor[Future].errorOf") {
    val expectedMessage = "This is a throwable test error."
    val expectedError   = SomeThrowableError.message(expectedMessage)

    val future = FxCtor[Future].errorOf[Unit](expectedError)

    future
      .map { _ =>
        fail("This should not be called.")
      }
      .recover {
        case ex: Throwable =>
          assertEquals(ex, expectedError)
      }
  }

  test("test fx.pureOfOption[Future] with Some") {
    val s = "blah blah".some

    val expected = s
    val input    = s.orNull
    val future   = FxCtor[Future].pureOfOption(input)
    future.map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test fx.pureOfOption[Future] with None") {
    val s = none[String]

    val expected = s
    val input    = s.orNull
    val future   = FxCtor[Future].pureOfOption(input)
    future.map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test fx.pureOfSome[Future]") {
    val s = "blah blah"

    val expected = s.some
    val future   = FxCtor[Future].pureOfSome(s)

    future.map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test fx.pureOfNone[Future]") {
    val expected = none[String]

    val future = FxCtor[Future].pureOfNone[String]

    future.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test fx.pureOfRight[Future]") {
    val n = 123

    val expected = n.asRight[String]

    val future = FxCtor[Future].pureOfRight[String](n)

    future.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test fx.pureOfLeft[Future]") {
    val s        = "blah blah"
    val expected = s.asLeft[Int]

    val future = FxCtor[Future].pureOfLeft[Int](s)

    future.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test FxCtor[Future].fromEither(Right)") {
    val n = 123

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected            = n.asRight[SomeThrowableError]
    val future: Future[Int] = FxCtor[Future].fromEither(expected)

    future.onComplete { actual =>
      assertEquals(actual.toEither, expected)
    }

  }

  test("test FxCtor[Future].fromEither(Left)") {
    val errorMessage = "This is a throwable test error."

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected            = SomeThrowableError.message(errorMessage).asLeft[Int]
    val future: Future[Int] = FxCtor[Future].fromEither(expected)

    future.onComplete { actual =>
      assertEquals(actual.toEither, expected)
    }
  }

  test("test FxCtor[Future].fromOption(Some)") {
    val n = 123

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected            = n.asRight[SomeThrowableError]
    val input               = n.some
    val future: Future[Int] =
      FxCtor[Future].fromOption(input)(SomeThrowableError.Message("This should never happen!"))

    future.onComplete { actual =>
      assertEquals(actual.toEither, expected)
    }

  }

  test("test FxCtor[Future].fromOption(None)") {
    val errorMessage = "This is a throwable test error."

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected            = SomeThrowableError.message(errorMessage).asLeft[Int]
    val future: Future[Int] = FxCtor[Future].fromOption(none[Int])(SomeThrowableError.Message(errorMessage))

    future.onComplete { actual =>
      assertEquals(actual.toEither, expected)
    }
  }

  test("test FxCtor[Future].fromTry(Success)") {
    val n = 123

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected            = n.asRight[SomeThrowableError]
    val input: Try[Int]     = scala.util.Success(n)
    val future: Future[Int] = FxCtor[Future].fromTry(input)

    future.onComplete { actual =>
      assertEquals(actual.toEither, expected)
    }

  }

  test("test FxCtor[Future].fromTry(Failure)") {
    val errorMessage = "This is a throwable test error."

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected            = SomeThrowableError.message(errorMessage).asLeft[Int]
    val input: Try[Int]     = scala.util.Failure(SomeThrowableError.message(errorMessage))
    val future: Future[Int] = FxCtor[Future].fromTry(input)

    future.onComplete { actual =>
      assertEquals(actual.toEither, expected)
    }

  }

}
