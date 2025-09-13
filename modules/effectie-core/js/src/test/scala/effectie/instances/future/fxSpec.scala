package effectie.instances.future

import cats.Monad
import cats.syntax.all._
import effectie.core.Fx
import effectie.specs.MonadSpec4Js
import effectie.testing.FutureTools
import effectie.testing.cats.LawsF.EqF
//import effectie.testing.cats.MonadSpec
import effectie.instances.future.fx.futureFx
import effectie.testing.types.{SomeError, SomeThrowableError}

import scala.concurrent._
import scala.concurrent.duration._
import scala.util.Try
import scala.util.control.NonFatal

/** @author Kevin Lee
  * @since 2022-01-06
  */
class fxSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  override val munitTimeout: FiniteDuration = 200.milliseconds

  /* Future */

  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: Fx, A](a: => A): F[A] =
    Fx[F].effectOf(a)

  test("test Fx[Future].effectOf") {
    val before = 123
    val after  = 999

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var actual               = before // scalafix:ok DisableSyntax.var
    assertEquals(actual, before)
    val future: Future[Unit] = Fx[Future].effectOf({ actual = after; () })

    future.map { _ => assertEquals(actual, after) }
  }

  test("test Fx[Future].fromEffect(effectOf)") {
    val before = 123
    val after  = 999

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var actual = before // scalafix:ok DisableSyntax.var
    assertEquals(actual, before)

    lazy val fromFuture = Fx[Future].fromEffect(Fx[Future].effectOf({ actual = after; () }))

    assertEquals(actual, before)

    fromFuture.map { _ => assertEquals(actual, after) }

  }

  test("test Fx[Future].fromEffect(pureOf)") {
    val before = 123
    val after  = 999

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var actual = before // scalafix:ok DisableSyntax.var
    assertEquals(actual, before)

    val fromFuture = Fx[Future].fromEffect(Fx[Future].pureOf({ actual = after; () }))
    assertEquals(actual, before)

    fromFuture.map { _ => assertEquals(actual, after) }

  }

  test("test Fx[Future].pureOf") {
    val before = 123
    val after  = 999

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var actual = before // scalafix:ok DisableSyntax.var
    assertEquals(actual, before)
    val future = Fx[Future].pureOf({ actual = after; () })

    future.map { _ => assertEquals(actual, after) }

  }

  test("test Fx[Future].pureOrError(success case)") {
    val before = 123
    val after  = 999

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var actual = before // scalafix:ok DisableSyntax.var
    assertEquals(actual, before)

    val future = Fx[Future].pureOrError({ actual = after; () })
    future.map { _ => assertEquals(actual, after) }

  }

  test("test Fx[Future].pureOrError(error case)") {
    val expectedMessage = "This is a throwable test error."
    val expectedError   = SomeThrowableError.message(expectedMessage)

    val future = Fx[Future].pureOrError[Unit](throw expectedError) // scalafix:ok DisableSyntax.throw

    future.onComplete {
      case scala.util.Failure(actual) =>
        assertEquals(actual, expectedError)

      case scala.util.Success(_) =>
        fail("This should not be called.")
    }

  }

  test("test Fx[Future].unitOf") {
    val future         = Fx[Future].unitOf
    val expected: Unit = ()

    future.map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test fx.pureOfOption[Future] with Some") {
    val s = "blah blah".some

    val expected = s
    val input    = s.orNull
    val future   = Fx[Future].pureOfOption(input)

    future.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test fx.pureOfOption[Future] with None") {
    val s = none[String]

    val expected = s
    val input    = s.orNull
    val future   = Fx[Future].pureOfOption(input)

    future.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test fx.pureOfSome[Future]") {
    val s = "blah blah"

    val expected = s.some
    val future   = Fx[Future].pureOfSome(s)

    future.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test fx.pureOfNone[Future]") {
    val expected = none[String]

    val future = Fx[Future].pureOfNone[String]

    future.map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test fx.pureOfRight[Future]") {
    val n = 123

    val expected = n.asRight[String]

    val future = Fx[Future].pureOfRight[String](n)

    future.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test fx.pureOfLeft[Future]") {
    val s = "This is a test error."

    val expected = s.asLeft[Int]

    val future = Fx[Future].pureOfLeft[Int](s)

    future.map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test Fx[Future].errorOf") {
    val expectedMessage = "This is a throwable test error."
    val expectedError   = SomeThrowableError.message(expectedMessage)

    val future = Fx[Future].errorOf[Unit](expectedError)

    future.onComplete {
      case scala.util.Failure(actual) =>
        assertEquals(actual, expectedError)

      case scala.util.Success(_) =>
        fail("This should not be called.")
    }

  }

  test("test Fx[Future].fromEither(Right)") {
    val n = 123

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected            = n.asRight[SomeThrowableError]
    val future: Future[Int] = Fx[Future].fromEither(expected)

    future.onComplete { actual =>
      assertEquals(actual.toEither, expected)
    }
  }

  test("test Fx[Future].fromEither(Left)") {
    val errorMessage = "This is a throwable test error."

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected            = SomeThrowableError.message(errorMessage).asLeft[Int]
    val future: Future[Int] = Fx[Future].fromEither(expected)

    future.onComplete { actual =>
      assertEquals(actual.toEither, expected)
    }

  }

  test("test Fx[Future].fromOption(Some)") {
    val n = 123

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected            = n.asRight[SomeThrowableError]
    val input               = n.some
    val future: Future[Int] = Fx[Future].fromOption(input)(SomeThrowableError.Message("This should never happen!"))

    future.onComplete { actual =>
      assertEquals(actual.toEither, expected)
    }

  }

  test("test Fx[Future].fromOption(None)") {
    val errorMessage = "This is a throwable test error."

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected            = SomeThrowableError.message(errorMessage).asLeft[Int]
    val future: Future[Int] = Fx[Future].fromOption(none[Int])(SomeThrowableError.Message(errorMessage))

    future.onComplete { actual =>
      assertEquals(actual.toEither, expected)
    }

  }

  test("test Fx[Future].fromTry(Success)") {
    val n = 123

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected            = n.asRight[SomeThrowableError]
    val input: Try[Int]     = scala.util.Success(n)
    val future: Future[Int] = Fx[Future].fromTry(input)

    future.onComplete { actual =>
      assertEquals(actual.toEither, expected)
    }

  }

  test("test Fx[Future].fromTry(Failure)") {
    val errorMessage = "This is a throwable test error."

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    val expected            = SomeThrowableError.message(errorMessage).asLeft[Int]
    val input: Try[Int]     = scala.util.Failure(SomeThrowableError.message(errorMessage))
    val future: Future[Int] = Fx[Future].fromTry(input)

    future.onComplete { actual =>
      assertEquals(actual.toEither, expected)
    }
  }

  test("test Fx[Future].flatMapFa(Future[A])(A => IO[B])") {
    val n      = 123
    val prefix = "n is "

    val expected = prefix + n.toString

    val fa = Fx[Future].pureOf(n)
    val fb = Fx[Future].flatMapFa(fa)(n => Fx[Future].pureOf(prefix + n.toString))

    fb.onComplete { actual =>
      assertEquals(actual.toEither, expected.asRight)
    }

  }

//      implicit val ec: scala.concurrent.ExecutionContext             = scala.concurrent.ExecutionContext.global
//    implicit val ec: ExecutionContext = globalExecutionContext

//    implicit def futureEqual[A](implicit EQ: Eq[A]): Eq[Future[A]] = new Eq[Future[A]] {
//      override def eqv(x: Future[A], y: Future[A]): Boolean =
//        futureToValue(x.flatMap(a => y.map(b => EQ.eqv(a, b))), WaitFor(1.second))
//    }

  implicit def eqF[F[*]: Monad]: EqF[F, Int] =
    (a, b) => a.flatMap(aVal => b.map(aVal === _))

//      implicit val eqFuture: Eq[Future[Int]] =
//        (x, y) => {
//          val future = x.flatMap(xx => y.map(_ === xx))
//          Await.result(future, waitFor.waitFor)
//        }

  MonadSpec4Js.testAllLaws[Future]("Fx[Future]").foreach {
    case (name, testF) =>
      test(name) {
        testF()
      }
  }

//      import java.util.concurrent.{ Executors}
  import scala.concurrent.Future

  test("test Fx[Future]catchNonFatalThrowable should catch NonFatal") {

    val expectedExpcetion                       = new RuntimeException("Something's wrong")
    val expected: Either[RuntimeException, Int] = Left(expectedExpcetion)

    val fa = run[Future, Int](throwThrowable[Int](expectedExpcetion))

    Fx[Future].catchNonFatalThrowable(fa).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test Fx[Future]catchNonFatal should catch NonFatal") {

    val expectedExpcetion                = new RuntimeException("Something's wrong")
    val expected: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))

    val fa = run[Future, Int](throwThrowable[Int](expectedExpcetion))

    Fx[Future].catchNonFatal(fa)(SomeError.someThrowable).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test Fx[Future]catchNonFatalThrowable should return the successful result") {

    val fa                               = run[Future, Int](1)
    val expected: Either[Throwable, Int] = Right(1)

    Fx[Future].catchNonFatalThrowable(fa).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test Fx[Future]catchNonFatal should return the successful result") {

    val fa                               = run[Future, Int](1)
    val expected: Either[SomeError, Int] = Right(1)

    Fx[Future].catchNonFatal(fa)(SomeError.someThrowable).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test Fx[Future]catchNonFatalEither should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))

    Fx[Future].catchNonFatalEither(fa)(SomeError.someThrowable).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test Fx[Future]catchNonFatalEither should return the successful result") {

    val fa                               = run[Future, Either[SomeError, Int]](Right(1))
    val expected: Either[SomeError, Int] = Right(1)
    Fx[Future].catchNonFatalEither(fa)(SomeError.someThrowable).map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test Fx[Future]catchNonFatalEither should return the failed result") {

    val expectedFailure                  = SomeError.message("Failed")
    val fa                               = run[Future, Either[SomeError, Int]](Left(expectedFailure))
    val expected: Either[SomeError, Int] = Left(expectedFailure)
    Fx[Future].catchNonFatalEither(fa)(SomeError.someThrowable).map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test Fx[Future].handleNonFatalWith should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
    val expected          = 1
    Fx[Future].handleNonFatalWith(fa)(_ => Future(expected)).map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test Fx[Future].handleNonFatalWith should return the successful result") {

    val fa       = run[Future, Int](1)
    val expected = 1

    Fx[Future].handleNonFatalWith(fa)(_ => Future(123)).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test Fx[Future].handleNonFatalWithEither should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))

    val failedResult =
      Fx[Future].handleNonFatalWith(fa)(err => Future(Left(SomeError.someThrowable(err)))).map { actualFailedResult =>
        assertEquals(actualFailedResult, expectedFailedResult)
      }

    val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected: Either[SomeError, Int] = Right(1)

    val handledResult = Fx[Future].handleNonFatalWith(fa2)(_ => Future(expected)).map { actual =>
      assertEquals(actual, expected)
    }

    Future.sequence(
      List(
        failedResult,
        handledResult,
      )
    )
  }

  test("test Fx[Future].handleNonFatalWithEither should return the successful result") {

    val fa                               = run[Future, Either[SomeError, Int]](Right(1))
    val expected: Either[SomeError, Int] = Right(1)
    Fx[Future].handleNonFatalWith(fa)(err => Future(Left(SomeError.someThrowable(err)))).map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test Fx[Future].handleNonFatalWithEither should return the failed result") {

    val expectedFailure                  = SomeError.message("Failed")
    val fa                               = run[Future, Either[SomeError, Int]](Left(expectedFailure))
    val expected: Either[SomeError, Int] = Left(expectedFailure)
    Fx[Future].handleNonFatalWith(fa)(_ => Future(Right(1))).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test Fx[Future].handleEitherNonFatalWith should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))

    val failedResult = Fx[Future]
      .handleEitherNonFatalWith(fa)(err => Future(Left(SomeError.someThrowable(err))))
      .map { actualFailedResult =>
        assertEquals(actualFailedResult, expectedFailedResult)
      }

    val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected: Either[SomeError, Int] = Right(1)

    val handledResult = Fx[Future].handleEitherNonFatalWith(fa2)(_ => Future(expected)).map { actual =>
      assertEquals(actual, expected)
    }

    Future.sequence(
      List(
        failedResult,
        handledResult,
      )
    )
  }

  test("test Fx[Future].handleEitherNonFatalWith should return the successful result") {

    val fa = run[Future, Either[SomeError, Int]](Right(1))

    val expected: Either[SomeError, Int] = Right(1)

    Fx[Future].handleEitherNonFatalWith(fa)(err => Future(Left(SomeError.someThrowable(err)))).map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test Fx[Future].handleEitherNonFatalWith should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](Left(expectedFailure))

    val expected: Either[SomeError, Int] = Left(expectedFailure)

    Fx[Future].handleEitherNonFatalWith(fa)(_ => Future(expected)).map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test Fx[Future].handleNonFatal should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")

    val fa       = run[Future, Int](throwThrowable[Int](expectedExpcetion))
    val expected = 1

    Fx[Future].handleNonFatal(fa)(_ => expected).map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test Fx[Future].handleNonFatal should return the successful result") {

    val fa       = run[Future, Int](1)
    val expected = 1
    Fx[Future].handleNonFatal(fa)(_ => 123).map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test Fx[Future].handleNonFatalEither should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))

    val failedResult =
      Fx[Future].handleNonFatal(fa)(err => Left(SomeError.someThrowable(err))).map { actualFailedResult =>
        assertEquals(actualFailedResult, expectedFailedResult)
      }

    val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected: Either[SomeError, Int] = Right(1)

    val handledResult = Fx[Future].handleNonFatal(fa2)(_ => expected).map { actual =>
      assertEquals(actual, expected)
    }

    Future.sequence(
      List(
        failedResult,
        handledResult,
      )
    )
  }

  test("test Fx[Future].handleNonFatalEither should return the successful result") {

    val fa = run[Future, Either[SomeError, Int]](Right(1))

    val expected: Either[SomeError, Int] = Right(1)

    Fx[Future].handleNonFatal(fa)(err => Left(SomeError.someThrowable(err))).map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test Fx[Future].handleNonFatalEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](Left(expectedFailure))

    val expected: Either[SomeError, Int] = Left(expectedFailure)
    Fx[Future].handleNonFatal(fa)(_ => Right(1)).map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test Fx[Future].handleEitherNonFatal should handle NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))

    val failedResult =
      Fx[Future].handleEitherNonFatal(fa)(err => Left(SomeError.someThrowable(err))).map { actualFailedResult =>
        assertEquals(actualFailedResult, expectedFailedResult)
      }

    val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected: Either[SomeError, Int] = Right(1)

    val handledResult = Fx[Future].handleEitherNonFatal(fa2)(_ => expected).map { actual =>
      assertEquals(actual, expected)
    }

    Future.sequence(
      List(
        failedResult,
        handledResult,
      )
    )
  }

  test("test Fx[Future].handleEitherNonFatal should return the successful result") {

    val fa = run[Future, Either[SomeError, Int]](Right(1))

    val expected: Either[SomeError, Int] = Right(1)

    Fx[Future].handleEitherNonFatal(fa)(err => Left(SomeError.someThrowable(err))).map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test Fx[Future].handleEitherNonFatal should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](Left(expectedFailure))

    val expected: Either[SomeError, Int] = Left(expectedFailure)

    Fx[Future].handleEitherNonFatal(fa)(_ => expected).map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test Fx[Future].recoverFromNonFatalWith should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")

    val fa = run[Future, Int](throwThrowable[Int](expectedExpcetion))

    val expected = 1
    Fx[Future]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) => Future(expected)
      }
      .map { actual =>
        assertEquals(actual, expected)
      }
  }

  test("test Fx[Future].recoverFromNonFatalWith should return the successful result") {

    val fa       = run[Future, Int](1)
    val expected = 1

    Fx[Future]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(_) => Future(123)
      }
      .map { actual =>
        assertEquals(actual, expected)
      }
  }

  test("test Fx[Future].recoverFromNonFatalWithEither should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))

    val failedResult = Fx[Future]
      .recoverFromNonFatalWith(fa) {
        case err => Future(Left(SomeError.someThrowable(err)))
      }
      .map { actualFailedResult =>
        assertEquals(actualFailedResult, expectedFailedResult)
      }

    val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected: Either[SomeError, Int] = Right(1)

    val handledResult = Fx[Future]
      .recoverFromNonFatalWith(fa2) {
        case NonFatal(`expectedExpcetion`) => Future(expected)
      }
      .map { actual =>
        assertEquals(actual, expected)
      }

    Future.sequence(
      List(
        failedResult,
        handledResult,
      )
    )
  }

  test("test Fx[Future].recoverFromNonFatalWithEither should return the successful result") {

    val fa = run[Future, Either[SomeError, Int]](Right(1))

    val expected: Either[SomeError, Int] = Right(1)

    Fx[Future]
      .recoverFromNonFatalWith(fa) {
        case err => Future(Left(SomeError.someThrowable(err)))
      }
      .map { actual =>
        assertEquals(actual, expected)
      }
  }

  test("test Fx[Future].recoverFromNonFatalWithEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](Left(expectedFailure))

    val expected: Either[SomeError, Int] = Left(expectedFailure)

    Fx[Future]
      .recoverFromNonFatalWith(fa) {
        case NonFatal(_) => Future(Right(1))
      }
      .map { actual =>
        assertEquals(actual, expected)
      }
  }

  test("test Fx[Future].recoverEitherFromNonFatalWith should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

    val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))

    val failedResult = Fx[Future]
      .recoverEitherFromNonFatalWith(fa) {
        case err => Future(Left(SomeError.someThrowable(err)))
      }
      .map { actualFailedResult =>
        assertEquals(actualFailedResult, expectedFailedResult)
      }

    val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected: Either[SomeError, Int] = Right(1)

    val handledResult = Fx[Future]
      .recoverEitherFromNonFatalWith(fa2) {
        case err @ _ => Future(expected)
      }
      .map { actual =>
        assertEquals(actual, expected)
      }

    Future.sequence(
      List(
        failedResult,
        handledResult,
      )
    )
  }

  test("test Fx[Future].recoverEitherFromNonFatalWith should return the successful result") {

    val fa = run[Future, Either[SomeError, Int]](Right(1))

    val expected: Either[SomeError, Int] = Right(1)

    Fx[Future]
      .recoverEitherFromNonFatalWith(fa) {
        case err => Future(Left(SomeError.someThrowable(err)))
      }
      .map { actual =>
        assertEquals(actual, expected)
      }
  }

  test("test Fx[Future].recoverEitherFromNonFatalWith should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](Left(expectedFailure))

    val expected: Either[SomeError, Int] = Left(expectedFailure)

    Fx[Future]
      .recoverEitherFromNonFatalWith(fa) {
        case NonFatal(_) => Future(expected)
      }
      .map { actual =>
        assertEquals(actual, expected)
      }
  }

  // /

  test("test Fx[Future].recoverFromNonFatal should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")

    val fa       = run[Future, Int](throwThrowable[Int](expectedExpcetion))
    val expected = 1

    Fx[Future].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expected }.map { actual =>
      assertEquals(actual, expected)
    }
  }

  test("test Fx[Future].recoverFromNonFatal should return the successful result") {

    val fa       = run[Future, Int](1)
    val expected = 1

    Fx[Future].recoverFromNonFatal(fa) { case NonFatal(_) => 123 }.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test Fx[Future].recoverFromNonFatalEither should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))

    val failedResult = Fx[Future]
      .recoverFromNonFatal(fa) {
        case err => Left(SomeError.someThrowable(err))
      }
      .map { actualFailedResult =>
        assertEquals(actualFailedResult, expectedFailedResult)
      }

    val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected: Either[SomeError, Int] = Right(1)

    val handledResult =
      Fx[Future].recoverFromNonFatal(fa2) { case NonFatal(`expectedExpcetion`) => expected }.map { actual =>
        assertEquals(actual, expected)
      }

    Future.sequence(
      List(
        failedResult,
        handledResult,
      )
    )
  }

  test("test Fx[Future].recoverFromNonFatalEither should return the successful result") {

    val fa = run[Future, Either[SomeError, Int]](Right(1))

    val expected: Either[SomeError, Int] = Right(1)

    Fx[Future]
      .recoverFromNonFatal(fa) {
        case err => Left(SomeError.someThrowable(err))
      }
      .map { actual =>
        assertEquals(actual, expected)
      }
  }

  test("test Fx[Future].recoverFromNonFatalEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](Left(expectedFailure))

    val expected: Either[SomeError, Int] = Left(expectedFailure)

    Fx[Future].recoverFromNonFatal(fa) { case NonFatal(_) => Right(1) }.map { actual =>
      assertEquals(actual, expected)
    }

  }

  test("test Fx[Future].recoverEitherFromNonFatal should catch NonFatal") {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expectedFailedResult: Either[SomeError, Int] = Left(SomeError.someThrowable(expectedExpcetion))

    val failedResult = Fx[Future]
      .recoverEitherFromNonFatal(fa) {
        case err => Left(SomeError.someThrowable(err))
      }
      .map { actualFailedResult =>
        assertEquals(actualFailedResult, expectedFailedResult)
      }

    val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected: Either[SomeError, Int] = Right(1)

    val handledResult = Fx[Future].recoverEitherFromNonFatal(fa2) { case err @ _ => expected }.map { actual =>
      assertEquals(actual, expected)
    }

    Future.sequence(
      List(
        failedResult,
        handledResult,
      )
    )
  }

  test("test Fx[Future].recoverEitherFromNonFatal should return the successful result") {

    val fa = run[Future, Either[SomeError, Int]](Right(1))

    val expected: Either[SomeError, Int] = Right(1)

    Fx[Future]
      .recoverEitherFromNonFatal(fa) {
        case err => Left(SomeError.someThrowable(err))
      }
      .map { actual =>
        assertEquals(actual, expected)
      }
  }

  test("test Fx[Future].recoverEitherFromNonFatal should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Future, Either[SomeError, Int]](Left(expectedFailure))

    val expected: Either[SomeError, Int] = Left(expectedFailure)

    Fx[Future].recoverEitherFromNonFatal(fa) { case NonFatal(_) => expected }.map { actual =>
      assertEquals(actual, expected)
    }
  }

}
