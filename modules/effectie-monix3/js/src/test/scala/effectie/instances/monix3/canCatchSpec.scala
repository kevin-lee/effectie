package effectie.instances.monix3

import canCatch.canCatchTask
import cats.data.EitherT

import cats.syntax.all._
import effectie.SomeControlThrowable
import effectie.core._
import effectie.syntax.error._
import effectie.syntax.fx._
import effectie.testing.FutureTools
import effectie.testing.types._
import fxCtor.taskFxCtor
import munit.Assertions

import scala.concurrent._
import scala.concurrent.duration._

import monix.eval.Task

/** @author Kevin Lee
  * @since 2020-07-31
  */
class canCatchSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  import monix.execution.Scheduler.Implicits.global

  override val munitTimeout: FiniteDuration = 200.milliseconds

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A = throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor, A](a: => A): F[A] = effectOf[F](a)

  /* Task */

  test("test CanCatch[Task].catchNonFatalThrowable should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Int](throwThrowable[Int](expectedException))
    val expected          = expectedException.asLeft[Int]

    CanCatch[Task]
      .catchNonFatalThrowable(fa)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanCatch[Task].catchNonFatalThrowable should not catch Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[Task, Int](throwThrowable[Int](fatalExpcetion))

    try {
      CanCatch[Task]
        .catchNonFatalThrowable(fa)
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .runToFuture
    } catch {
      case ex: SomeControlThrowable =>
        Assertions.assertEquals(ex.getMessage, fatalExpcetion.getMessage)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test CanCatch[Task].catchNonFatalThrowable should return the successful result") {

    val fa       = run[Task, Int](1)
    val expected = 1.asRight[Throwable]

    CanCatch[Task]
      .catchNonFatalThrowable(fa)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanCatch[Task]catchNonFatal should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Int](throwThrowable[Int](expectedException))
    val expected          = SomeError.someThrowable(expectedException).asLeft[Int]

    CanCatch[Task]
      .catchNonFatal(fa)(SomeError.someThrowable)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanCatch[Task]catchNonFatal should not catch Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[Task, Int](throwThrowable[Int](fatalExpcetion))

    try {
      CanCatch[Task]
        .catchNonFatal(fa)(SomeError.someThrowable)
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .runToFuture
    } catch {
      case ex: SomeControlThrowable =>
        Assertions.assertEquals(ex.getMessage, fatalExpcetion.getMessage)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test CanCatch[Task]catchNonFatal should return the successful result") {

    val fa       = run[Task, Int](1)
    val expected = 1.asRight[SomeError]

    CanCatch[Task]
      .catchNonFatal(fa)(SomeError.someThrowable)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanCatch[Task]catchNonFatalEither should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
    val expected          = SomeError.someThrowable(expectedException).asLeft[Int]

    CanCatch[Task]
      .catchNonFatalEither(fa)(SomeError.someThrowable)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanCatch[Task]catchNonFatalEither should not catch Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

    try {
      CanCatch[Task]
        .catchNonFatalEither(fa)(SomeError.someThrowable)
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .runToFuture
    } catch {
      case ex: SomeControlThrowable =>
        Assertions.assertEquals(ex.getMessage, fatalExpcetion.getMessage)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test CanCatch[Task]catchNonFatalEither should return the successful result") {

    val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    CanCatch[Task]
      .catchNonFatalEither(fa)(SomeError.someThrowable)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanCatch[Task]catchNonFatalEither should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected        = expectedFailure.asLeft[Int]

    CanCatch[Task]
      .catchNonFatalEither(fa)(SomeError.someThrowable)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanCatch[Task]catchNonFatalEitherT should catch NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa       = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
    val expected = SomeError.someThrowable(expectedException).asLeft[Int]

    CanCatch[Task]
      .catchNonFatalEitherT(fa)(SomeError.someThrowable)
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanCatch[Task]catchNonFatalEitherT should not catch Fatal") {

    val fatalExpcetion = SomeControlThrowable("Something's wrong")
    val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

    try {
      CanCatch[Task]
        .catchNonFatalEitherT(fa)(SomeError.someThrowable)
        .value
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .runToFuture
    } catch {
      case ex: SomeControlThrowable =>
        Assertions.assertEquals(ex.getMessage, fatalExpcetion.getMessage)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }
  }

  test("test CanCatch[Task]catchNonFatalEitherT should return the successful result") {

    val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    CanCatch[Task]
      .catchNonFatalEitherT(fa)(SomeError.someThrowable)
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  test("test CanCatch[Task]catchNonFatalEitherT should return the failed result") {

    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    CanCatch[Task]
      .catchNonFatalEitherT(fa)(SomeError.someThrowable)
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

  /* Future */

  import effectie.instances.future.canCatch.canCatchFuture
  import effectie.instances.future.fxCtor.fxCtorFuture

  import scala.concurrent._

//    def testCanCatch_Future_catchNonFatalThrowableShouldCatchNonFatal: Result = {
//
//      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
//      implicit val ec: ExecutionContext             =
//        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)
//
//      val expectedException = new RuntimeException("Something's wrong")
//      val fa                = run[Future, Int](throwThrowable[Int](expectedException))
//      val expected          = expectedException.asLeft[Int]
//      val actual            = ConcurrentSupport.futureToValueAndTerminate(
//        executorService,
//        waitFor
//      )(CanCatch[Future].catchNonFatalThrowable(fa))
//
//      actual ==== expected
//    }
//
//    def testCanCatch_Future_catchNonFatalShouldCatchNonFatal: Result = {
//
//      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
//      implicit val ec: ExecutionContext             =
//        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)
//
//      val expectedException = new RuntimeException("Something's wrong")
//      val fa                = run[Future, Int](throwThrowable[Int](expectedException))
//      val expected          = SomeError.someThrowable(expectedException).asLeft[Int]
//      val actual            = ConcurrentSupport.futureToValueAndTerminate(
//        executorService,
//        waitFor
//      )(CanCatch[Future].catchNonFatal(fa)(SomeError.someThrowable))
//
//      actual ==== expected
//    }
//
//    def testCanCatch_Future_catchNonFatalThrowableShouldReturnSuccessfulResult: Result = {
//
//      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
//      implicit val ec: ExecutionContext             =
//        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)
//
//      val fa       = run[Future, Int](1)
//      val expected = 1.asRight[Throwable]
//      val actual   = ConcurrentSupport.futureToValueAndTerminate(
//        executorService,
//        waitFor
//      )(CanCatch[Future].catchNonFatalThrowable(fa))
//
//      actual ==== expected
//    }
//
//    def testCanCatch_Future_catchNonFatalShouldReturnSuccessfulResult: Result = {
//
//      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
//      implicit val ec: ExecutionContext             =
//        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)
//
//      val fa       = run[Future, Int](1)
//      val expected = 1.asRight[SomeError]
//      val actual   = ConcurrentSupport.futureToValueAndTerminate(
//        executorService,
//        waitFor
//      )(CanCatch[Future].catchNonFatal(fa)(SomeError.someThrowable))
//
//      actual ==== expected
//    }
//
//    def testCanCatch_Future_catchNonFatalEitherShouldCatchNonFatal: Result = {
//
//      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
//      implicit val ec: ExecutionContext             =
//        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)
//
//      val expectedException = new RuntimeException("Something's wrong")
//      val fa       = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException))
//      val expected = SomeError.someThrowable(expectedException).asLeft[Int]
//      val actual   = ConcurrentSupport.futureToValueAndTerminate(
//        executorService,
//        waitFor
//      )(CanCatch[Future].catchNonFatalEither(fa)(SomeError.someThrowable))
//
//      actual ==== expected
//    }
//
//    def testCanCatch_Future_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {
//
//      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
//      implicit val ec: ExecutionContext             =
//        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)
//
//      val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
//      val expected = 1.asRight[SomeError]
//      val actual   = ConcurrentSupport.futureToValueAndTerminate(
//        executorService,
//        waitFor
//      )(CanCatch[Future].catchNonFatalEither(fa)(SomeError.someThrowable))
//
//      actual ==== expected
//    }
//
//    def testCanCatch_Future_catchNonFatalEitherShouldReturnFailedResult: Result = {
//
//      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
//      implicit val ec: ExecutionContext             =
//        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)
//
//      val expectedFailure = SomeError.message("Failed")
//      val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
//      val expected        = expectedFailure.asLeft[Int]
//      val actual          = ConcurrentSupport.futureToValueAndTerminate(
//        executorService,
//        waitFor
//      )(CanCatch[Future].catchNonFatalEither(fa)(SomeError.someThrowable))
//
//      actual ==== expected
//    }

  test("test CanCatch[Future]catchNonFatalEitherT should catch NonFatal") {
    val expectedException = new RuntimeException("Something's wrong")
    val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedException)))
    val expected = SomeError.someThrowable(expectedException).asLeft[Int]

    CanCatch[Future]
      .catchNonFatalEitherT(fa)(SomeError.someThrowable)
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanCatch[Future]catchNonFatalEitherT should return the successful result") {
    val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]

    CanCatch[Future]
      .catchNonFatalEitherT(fa)(SomeError.someThrowable)
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

  test("test CanCatch[Future]catchNonFatalEitherT should return the failed result") {
    val expectedFailure = SomeError.message("Failed")
    val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected        = expectedFailure.asLeft[Int]

    CanCatch[Future]
      .catchNonFatalEitherT(fa)(SomeError.someThrowable)
      .value
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
  }

}
