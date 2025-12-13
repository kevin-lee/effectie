package effectie.syntax

import cats.data.EitherT
import cats.syntax.all._
import effectie.core.Fx
import effectie.syntax.error._
import effectie.syntax.fx.effectOf
import effectie.testing.FutureTools
import effectie.testing.types.{SomeError, SomeThrowableError}
import munit.Assertions

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.scalajs.concurrent.JSExecutionContext
import scala.util.control.NonFatal

class errorSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  override val munitTimeout: FiniteDuration = 200.milliseconds

  val CanHandleError: effectie.core.CanHandleError.type = effectie.core.CanHandleError

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: Fx, A](a: => A): F[A] = effectOf[F](a)

  import effectie.instances.future.fx._

  import scala.concurrent.Future

  test("test CanCatch[Future].catchNonFatalThrowable should catch NonFatal") {

    implicit val ec: ExecutionContext = JSExecutionContext.queue

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedException))
    val expected          = expectedException.asLeft[Int]

    fa.catchNonFatalThrowable.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanCatch[Future].catchNonFatalThrowable should return the successful result") {

    implicit val ec: ExecutionContext = JSExecutionContext.queue

    val fa       = run[Future, Int](1)
    val expected = 1.asRight[Throwable]

    fa.catchNonFatalThrowable.map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test CanCatch[Future].catchNonFatal should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedException))
    val expected          = SomeError.someThrowable(expectedException).asLeft[Int]

    fa.catchNonFatal(SomeError.someThrowable).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanCatch[Future].catchNonFatal should return the successful result") {

    val fa       = run[Future, Int](1)
    val expected = 1.asRight[SomeError]

    fa.catchNonFatal(SomeError.someThrowable).map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test CanCatch[Future].catchNonFatalEither should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa       = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expected = SomeError.someThrowable(expectedException).asLeft[Int]

    fa.catchNonFatalEither(SomeError.someThrowable).map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test CanCatch[Future].catchNonFatalEither should return the successful result") {

    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    fa.catchNonFatalEither(SomeError.someThrowable).map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test CanCatch[Future].catchNonFatalEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    fa.catchNonFatalEither(SomeError.someThrowable).map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test CanHandleError[Future].handleNonFatalWith should handle NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedException))
    val expected          = 1

    fa.handleNonFatalWith(_ => Future(expected)).map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test CanHandleError[Future].handleNonFatalWith should return the successful result") {

    val fa       = run[Future, Int](1)
    val expected = 1

    fa.handleNonFatalWith(_ => Future(123)).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanHandleError[Future].handleNonFatalWithEither should handle NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]

    val failedResult =
      fa.handleNonFatalWith(err => Future(SomeError.someThrowable(err).asLeft[Int])).map { actualFailedResult =>
        assertEquals(actualFailedResult, expectedFailedResult)
      }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expected = 1.asRight[SomeError]

    val handledResult = fa2.handleNonFatalWith(_ => Future(expected)).map { actual =>
      assertEquals(actual, expected)
    }

    Future.sequence(List(failedResult, handledResult))
  }

  test("test CanHandleError[Future].handleNonFatalWithEither should return the successful result") {

    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]

    fa.handleNonFatalWith(err => Future(SomeError.someThrowable(err).asLeft[Int])).map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test CanHandleError[Future].handleNonFatalWithEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    fa.handleNonFatalWith(_ => Future(1.asRight[SomeError])).map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test CanHandleError[Future].handleEitherNonFatalWith should handle NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]

    val failedResult =
      fa.handleEitherNonFatalWith(err => Future(SomeError.someThrowable(err).asLeft[Int])).map { actualFailedResult =>
        assertEquals(actualFailedResult, expectedFailedResult)
      }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expected = 1.asRight[SomeError]

    val handledResult = fa2.handleEitherNonFatalWith(_ => Future(expected)).map { actual =>
      assertEquals(actual, expected)
    }

    Future.sequence(List(failedResult, handledResult))
  }

  test("test CanHandleError[Future].handleEitherNonFatalWith should return the successful result") {

    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    fa.handleEitherNonFatalWith(err => Future(SomeError.someThrowable(err).asLeft[Int])).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanHandleError[Future].handleEitherNonFatalWith should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    fa.handleEitherNonFatalWith(_ => Future(expected)).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanHandleError[Future].handleNonFatal should handle NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedException))
    val expected          = 1
    fa.handleNonFatal(_ => expected).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanHandleError[Future].handleNonFatal should return the successful result") {

    val fa       = run[Future, Int](1)
    val expected = 1
    fa.handleNonFatal(_ => 123).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanHandleError[Future].handleNonFatalEither should handle NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
    val failedResult = fa.handleNonFatal(err => SomeError.someThrowable(err).asLeft[Int]).map { actualFailedResult =>
      assertEquals(actualFailedResult, expectedFailedResult)
    }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expected = 1.asRight[SomeError]

    val handledResult = fa2.handleNonFatal(_ => expected).map { actual =>
      assertEquals(actual, expected)
    }

    Future.sequence(List(failedResult, handledResult))
  }

  test("test CanHandleError[Future].handleNonFatalEither should return the successful result") {

    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    fa.handleNonFatal(err => SomeError.someThrowable(err).asLeft[Int]).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanHandleError[Future].handleNonFatalEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    fa.handleNonFatal(_ => 1.asRight[SomeError]).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanHandleError[Future].handleEitherNonFatal should handle NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]

    val failedResult =
      fa.handleEitherNonFatal(err => SomeError.someThrowable(err).asLeft[Int]).map { actualFailedResult =>
        assertEquals(actualFailedResult, expectedFailedResult)
      }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expected = 1.asRight[SomeError]

    val handledResult = fa2.handleEitherNonFatal(_ => expected).map { actual =>
      assertEquals(actual, expected)
    }

    Future.sequence(List(failedResult, handledResult))
  }

  test("test CanHandleError[Future].handleEitherNonFatal should return the successful result") {

    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    fa.handleEitherNonFatal(err => SomeError.someThrowable(err).asLeft[Int]).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanHandleError[Future].handleEitherNonFatal should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    fa.handleEitherNonFatal(_ => expected).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test F[Either[A, B]](F(Right(b))).rethrowIfLeft should return the successful result") {

    val fa       = run[Future, Either[SomeThrowableError, Int]](1.asRight[SomeThrowableError])
    val expected = 1
    fa.rethrowIfLeft.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test F[Either[A, B]](F(Left(a))).rethrowIfLeft should return the failed result") {

    val expectedFailure = SomeThrowableError.message("Failed")
    val fa              = run[Future, Either[SomeThrowableError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure

    fa.rethrowIfLeft
      .onComplete {
        case scala.util.Success(result) =>
          fail(s"Expected SomeThrowableError to be thrown but got ${result.toString} instead")
        case scala.util.Failure(actual) =>
          assertEquals(actual, expected)
      }

  }

  test("test EitherT[F, A, B](F(Right(b))).rethrowTIfLeft should return the successful result") {

    val fa       = EitherT(run[Future, Either[SomeThrowableError, Int]](1.asRight[SomeThrowableError]))
    val expected = 1
    fa.rethrowTIfLeft.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test EitherT[F, A, B](F(Left(a))).rethrowTIfLeft should return the failed result") {

    val expectedFailure = SomeThrowableError.message("Failed")
    val fa              = EitherT(run[Future, Either[SomeThrowableError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure
    fa.rethrowTIfLeft.onComplete {
      case scala.util.Success(result) =>
        fail(s"Expected SomeThrowableError to be thrown but got ${result.toString} instead")
      case scala.util.Failure(actual) =>
        assertEquals(actual, expected)
    }

  }

  test("test CanRecover[Future].recoverFromNonFatalWith should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedException))
    val expected          = 1
    fa.recoverFromNonFatalWith {
      case NonFatal(`expectedException`) => Future(expected)
    }.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanRecover[Future].recoverFromNonFatalWith should return the successful result") {

    val fa       = run[Future, Int](1)
    val expected = 1
    fa.recoverFromNonFatalWith {
      case NonFatal(_) => Future(123)
    }.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanRecover[Future].recoverFromNonFatalWithEither should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]

    val failedResult = fa
      .recoverFromNonFatalWith {
        case err => Future(SomeError.someThrowable(err).asLeft[Int])
      }
      .map { actualFailedResult =>
        assertEquals(actualFailedResult, expectedFailedResult)
      }

    val fa2           = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expected      = 1.asRight[SomeError]
    val handledResult = fa2
      .recoverFromNonFatalWith {
        case NonFatal(`expectedException`) => Future(expected)
      }
      .map { actual =>
        assertEquals(actual, expected)
      }

    Future.sequence(List(failedResult, handledResult))
  }

  test("test CanRecover[Future].recoverFromNonFatalWithEither should return the successful result") {

    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    fa.recoverFromNonFatalWith {
      case err => Future(SomeError.someThrowable(err).asLeft[Int])
    }.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanRecover[Future].recoverFromNonFatalWithEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    fa.recoverFromNonFatalWith {
      case NonFatal(_) => Future(1.asRight[SomeError])
    }.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanRecover[Future].recoverEitherFromNonFatalWith should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]

    val failedResult = fa
      .recoverEitherFromNonFatalWith {
        case err => Future(SomeError.someThrowable(err).asLeft[Int])
      }
      .map { actualFailedResult =>
        assertEquals(actualFailedResult, expectedFailedResult)
      }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expected = 1.asRight[SomeError]

    val handleResult = fa2
      .recoverEitherFromNonFatalWith {
        case err @ _ => Future(expected)
      }
      .map { actual =>
        assertEquals(actual, expected)
      }

    Future.sequence(List(failedResult, handleResult))

  }

  test("test CanRecover[Future].recoverEitherFromNonFatalWith should return the successful result") {

    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    fa.recoverEitherFromNonFatalWith {
      case err => Future(SomeError.someThrowable(err).asLeft[Int])
    }.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanRecover[Future].recoverEitherFromNonFatalWith should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    fa.recoverEitherFromNonFatalWith {
      case NonFatal(_) => Future(expected)
    }.map { actual =>
      assertEquals(actual, expected)
    }

  }

  // /

  test("test CanRecover[Future].recoverFromNonFatal should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedException))
    val expected          = 1
    fa.recoverFromNonFatal { case NonFatal(`expectedException`) => expected }.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanRecover[Future].recoverFromNonFatal should return the successful result") {

    val fa       = run[Future, Int](1)
    val expected = 1
    fa.recoverFromNonFatal { case NonFatal(_) => 123 }.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanRecover[Future].recoverFromNonFatalEither should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]

    val failedResult = fa
      .recoverFromNonFatal {
        case err => SomeError.someThrowable(err).asLeft[Int]
      }
      .map { actualFailedResult =>
        assertEquals(actualFailedResult, expectedFailedResult)
      }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expected = 1.asRight[SomeError]

    val handleResult = fa2.recoverFromNonFatal { case NonFatal(`expectedException`) => expected }.map { actual =>
      assertEquals(actual, expected)
    }

    Future.sequence(List(failedResult, handleResult))

  }

  test("test CanRecover[Future].recoverFromNonFatalEither should return the successful result") {

    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    fa.recoverFromNonFatal {
      case err => SomeError.someThrowable(err).asLeft[Int]
    }.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanRecover[Future].recoverFromNonFatalEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    fa.recoverFromNonFatal { case NonFatal(_) => 1.asRight[SomeError] }.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanRecover[Future].recoverEitherFromNonFatal should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expectedFailedResult = SomeError.someThrowable(expectedException).asLeft[Int]
    val failedResult         = fa
      .recoverEitherFromNonFatal {
        case err => SomeError.someThrowable(err).asLeft[Int]
      }
      .map { actualFailedResult =>
        assertEquals(actualFailedResult, expectedFailedResult)
      }

    val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expected = 1.asRight[SomeError]

    val handleResult = fa2.recoverEitherFromNonFatal { case err @ _ => expected }.map { actual =>
      assertEquals(actual, expected)
    }

    Future.sequence(List(failedResult, handleResult))

  }

  test("test CanRecover[Future].recoverEitherFromNonFatal should return the successful result") {

    val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    fa.recoverEitherFromNonFatal {
      case err => SomeError.someThrowable(err).asLeft[Int]
    }.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test CanRecover[Future].recoverEitherFromNonFatal should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]
    fa.recoverEitherFromNonFatal { case NonFatal(_) => expected }.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test OnNonFatal[Future].onNonFatalWith should do something for NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedException))
    val expected          = 123.some
    var actual            = none[Int] // scalafix:ok DisableSyntax.var

    try {
      fa.onNonFatalWith {
        case NonFatal(`expectedException`) =>
          Future {
            actual = expected
          } *> Future.unit
      }.map { actual =>
        Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}"): Unit
      }.recover {
        case NonFatal(`expectedException`) =>
          Assertions.assertEquals(actual, expected)
      }
    } catch {
      case ex: Throwable =>
        ex
    }

  }

//  test("test OnNonFatal[Future].onNonFatalWith should not do anything for Fatal") {
//
//    val expectedException = SomeControlThrowable("Something's wrong")
//    val fa                = run[Future, Int](throwThrowable[Int](expectedException))
//    var actual            = none[Int] // scalafix:ok DisableSyntax.var
//
//    try {
//      fa.onNonFatalWith {
//        case NonFatal(`expectedException`) =>
//          Future {
//            actual = 123.some
//            ()
//          } *> Future.unit
//      }.map { actual =>
//        Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
//      }
//    } catch {
//      case ex: ControlThrowable =>
//        Assertions.assertEquals(ex, expectedException)
//
//      case ex: Throwable =>
//        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
//    }
//
//  }

  test("test OnNonFatal[Future].onNonFatalWith should not do anything for the successful result") {

    val expectedResult = 999
    val fa             = run[Future, Int](expectedResult)

    val expected = none[Int]
    var actual   = none[Int] // scalafix:ok DisableSyntax.var

    fa.onNonFatalWith {
      case NonFatal(_) =>
        Future {
          actual = 123.some
        } *> Future.unit
    }.map { actualResult =>
      Assertions.assertEquals(actualResult, expectedResult)
      Assertions.assertEquals(actual, expected)
    }

  }

  /////

  test("test Future[Either[A, B]].onNonFatalWith should do something for NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val expectedResult    = expectedException
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))

    val expected = 123.some
    var actual   = none[Int] // scalafix:ok DisableSyntax.var

    fa.onNonFatalWith {
      case NonFatal(`expectedException`) =>
        Future {
          actual = expected
        } *> Future.unit
    }.map { r =>
      Assertions.fail(s"Should have thrown an exception, but it was ${r.toString}.")
    }.recover {
      case actualFailedResult: RuntimeException =>
        Assertions.assertEquals(actual, expected)
        Assertions.assertEquals(actualFailedResult, expectedResult)
    }

  }

  test("test Future[Either[A, B]].onNonFatalWith should do nothing for success case with Right") {

    val expectedValue  = 1
    val expectedResult = expectedValue.asRight[SomeError]
    val fa             = run[Future, Either[SomeError, Int]](expectedResult)

    val expected = none[Int]
    var actual   = none[Int] // scalafix:ok DisableSyntax.var

    fa.onNonFatalWith {
      case NonFatal(_) =>
        Future {
          actual = 123.some
        } *> Future.unit
    }.map { actualResult =>
      Assertions.assertEquals(actual, expected)
      Assertions.assertEquals(actualResult, expectedResult)
    }.recover {
      case ex: Throwable =>
        Assertions.fail(s"Should not have thrown an exception, but it was ${ex.toString}.")
    }

  }

  test("test Future[Either[A, B]].onNonFatalWith should do nothing for success case with Left") {

    val expectedException = new RuntimeException("Something's wrong")
    val expectedResult    = SomeError.someThrowable(expectedException).asLeft[Int]

    val fa = run[Future, Int](throwThrowable(expectedException))
      .catchNonFatal {
        case err =>
          SomeError.someThrowable(err)
      }

    val expected = none[Int]
    var actual   = none[Int] // scalafix:ok DisableSyntax.var

    fa.onNonFatalWith {
      case NonFatal(`expectedException`) =>
        Future {
          actual = 123.some
        } *> Future.unit
    }.map { actualResult =>
      Assertions.assertEquals(actual, expected)
      Assertions.assertEquals(actualResult, expectedResult)

    }.recover {
      case ex: Throwable =>
        Assertions.fail(s"Should not have thrown an exception, but it was ${ex.toString}.")
    }

  }

  /////////

  test("test EitherT[F, A, B].onNonFatalWith should do something for NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val expectedResult    = expectedException
    val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))

    val expected = 123.some
    var actual   = none[Int] // scalafix:ok DisableSyntax.var

    fa.onNonFatalWith {
      case NonFatal(`expectedException`) =>
        Future {
          actual = expected
        } *> Future.unit
    }.value
      .map { r =>
        Assertions.fail(s"Should have thrown an exception, but it was ${r.toString}.")
      }
      .recover {
        case actualFailedResult: RuntimeException =>
          Assertions.assertEquals(actual, expected)
          Assertions.assertEquals(actualFailedResult, expectedResult)
      }

  }

  test("test EitherT[F, A, B](F(Right(b))).onNonFatalWith should do nothing for success case with Right") {

    val expectedValue  = 1
    val expectedResult = expectedValue.asRight[SomeError]
    val fa             = EitherT(run[Future, Either[SomeError, Int]](expectedResult))

    val expected = none[Int]
    var actual   = none[Int] // scalafix:ok DisableSyntax.var

    fa.onNonFatalWith {
      case NonFatal(_) =>
        Future {
          actual = 123.some
        } *> Future.unit
    }.value
      .map { actualResult =>
        Assertions.assertEquals(actual, expected)
        Assertions.assertEquals(actualResult, expectedResult)
      }
      .recover {
        case ex: Throwable =>
          Assertions.fail(s"Should not have thrown an exception, but it was ${ex.toString}.")
      }

  }

  test("test EitherT[F, A, B](F(Left(a))).onNonFatalWith should do nothing for success case with Left") {

    val expectedException = new RuntimeException("Something's wrong")
    val expectedResult    = SomeError.someThrowable(expectedException).asLeft[Int]

    val fa = EitherT(
      run[Future, Int](throwThrowable(expectedException))
        .catchNonFatal {
          case err => SomeError.someThrowable(err)
        }
    )

    val expected = none[Int]
    var actual   = none[Int] // scalafix:ok DisableSyntax.var

    fa.onNonFatalWith {
      case NonFatal(`expectedException`) =>
        Future {
          actual = 123.some
        } *> Future.unit
    }.value
      .map { actualResult =>
        Assertions.assertEquals(actual, expected)
        Assertions.assertEquals(actualResult, expectedResult)
      }
      .recover {
        case ex: Throwable =>
          throw new AssertionError(
            s"Should not have thrown an exception, but it was ${ex.toString}."
          ) // scalafix:ok DisableSyntax.throw
      }

  }

}
