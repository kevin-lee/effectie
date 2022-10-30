package effectie.monix3

import canHandleError._
import cats._
import cats.data.EitherT
import cats.effect.IO
import cats.instances.all._
import cats.syntax.all._
import effectie.SomeControlThrowable
import effectie.core._
import effectie.syntax.error._
import effectie.syntax.fx._
import effectie.testing.types.SomeError
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import fxCtor._
import hedgehog._
import hedgehog.runner._
import monix.eval.Task

import scala.util.control.{ControlThrowable, NonFatal}

/** @author Kevin Lee
  * @since 2020-08-17
  */
object canHandleErrorSpec extends Properties {
  implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = taskSpecs ++ ioSpecs ++ futureSpecs ++ idSpecs

  /* Task */
  private val taskSpecs = List(
    example(
      "test CanHandleError[Task].handleNonFatalWith should handle NonFatal",
      TaskSpec.testCanHandleError_Task_handleNonFatalWithShouldHandleNonFatalWith
    ),
    example(
      "test CanHandleError[Task].handleNonFatalWith should not handle Fatal",
      TaskSpec.testCanHandleError_Task_handleNonFatalWithShouldNotHandleFatalWith
    ),
    example(
      "test CanHandleError[Task].handleNonFatalWith should return the successful result",
      TaskSpec.testCanHandleError_Task_handleNonFatalWithShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[Task].handleNonFatalWithEither should handle NonFatal",
      TaskSpec.testCanHandleError_Task_handleNonFatalWithEitherShouldHandleNonFatalWith
    ),
    example(
      "test CanHandleError[Task].handleNonFatalWithEither should not handle Fatal",
      TaskSpec.testCanHandleError_Task_handleNonFatalWithEitherShouldNotHandleFatalWith
    ),
    example(
      "test CanHandleError[Task].handleNonFatalWithEither should return the successful result",
      TaskSpec.testCanHandleError_Task_handleNonFatalWithEitherShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[Task].handleNonFatalWithEither should return the failed result",
      TaskSpec.testCanHandleError_Task_handleNonFatalWithEitherShouldReturnFailedResult
    ),
    example(
      "test CanHandleError[Task].handleEitherNonFatalWith should handle NonFatal",
      TaskSpec.testCanHandleError_Task_handleEitherNonFatalWithShouldHandleNonFatalWith
    ),
    example(
      "test CanHandleError[Task].handleEitherNonFatalWith should not handle Fatal",
      TaskSpec.testCanHandleError_Task_handleEitherNonFatalWithShouldNotHandleFatalWith
    ),
    example(
      "test CanHandleError[Task].handleEitherNonFatalWith should return the successful result",
      TaskSpec.testCanHandleError_Task_handleEitherNonFatalWithShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[Task].handleEitherNonFatalWith should return the failed result",
      TaskSpec.testCanHandleError_Task_handleEitherNonFatalWithShouldReturnFailedResult
    ),
    example(
      "test CanHandleError[Task].handleEitherTNonFatalWith should handle NonFatal",
      TaskSpec.testCanHandleError_Task_handleEitherTNonFatalWithShouldHandleNonFatalWith
    ),
    example(
      "test CanHandleError[Task].handleEitherTNonFatalWith should not handle Fatal",
      TaskSpec.testCanHandleError_Task_handleEitherTNonFatalWithShouldNotHandleFatalWith
    ),
    example(
      "test CanHandleError[Task].handleEitherTNonFatalWith should return the successful result",
      TaskSpec.testCanHandleError_Task_handleEitherTNonFatalWithShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[Task].handleEitherTNonFatalWith should return the failed result",
      TaskSpec.testCanHandleError_Task_handleEitherTNonFatalWithShouldReturnFailedResult
    ),
    example(
      "test CanHandleError[Task].handleNonFatal should handle NonFatal",
      TaskSpec.testCanHandleError_Task_handleNonFatalShouldHandleNonFatal
    ),
    example(
      "test CanHandleError[Task].handleNonFatal should not handle Fatal",
      TaskSpec.testCanHandleError_Task_handleNonFatalShouldNotHandleFatal
    ),
    example(
      "test CanHandleError[Task].handleNonFatal should return the successful result",
      TaskSpec.testCanHandleError_Task_handleNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[Task].handleNonFatalEither should handle NonFatal",
      TaskSpec.testCanHandleError_Task_handleNonFatalEitherShouldHandleNonFatal
    ),
    example(
      "test CanHandleError[Task].handleNonFatalEither should not handle Fatal",
      TaskSpec.testCanHandleError_Task_handleNonFatalEitherShouldNotHandleFatal
    ),
    example(
      "test CanHandleError[Task].handleNonFatalEither should return the successful result",
      TaskSpec.testCanHandleError_Task_handleNonFatalEitherShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[Task].handleNonFatalEither should return the failed result",
      TaskSpec.testCanHandleError_Task_handleNonFatalEitherShouldReturnFailedResult
    ),
    example(
      "test CanHandleError[Task].handleEitherNonFatal should handle NonFatal",
      TaskSpec.testCanHandleError_Task_handleEitherNonFatalShouldHandleNonFatal
    ),
    example(
      "test CanHandleError[Task].handleEitherNonFatal should not handle Fatal",
      TaskSpec.testCanHandleError_Task_handleEitherNonFatalShouldNotHandleFatal
    ),
    example(
      "test CanHandleError[Task].handleEitherNonFatal should return the successful result",
      TaskSpec.testCanHandleError_Task_handleEitherNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[Task].handleEitherNonFatal should return the failed result",
      TaskSpec.testCanHandleError_Task_handleEitherNonFatalShouldReturnFailedResult
    ),
    example(
      "test CanHandleError[Task].handleEitherTNonFatal should handle NonFatal",
      TaskSpec.testCanHandleError_Task_handleEitherTNonFatalShouldHandleNonFatal
    ),
    example(
      "test CanHandleError[Task].handleEitherTNonFatal should not handle Fatal",
      TaskSpec.testCanHandleError_Task_handleEitherTNonFatalShouldNotHandleFatal
    ),
    example(
      "test CanHandleError[Task].handleEitherTNonFatal should return the successful result",
      TaskSpec.testCanHandleError_Task_handleEitherTNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[Task].handleEitherTNonFatal should return the failed result",
      TaskSpec.testCanHandleError_Task_handleEitherTNonFatalShouldReturnFailedResult
    ),
  )

  /* IO */
  private val ioSpecs = List(
    example(
      "test CanHandleError[IO].handleNonFatalWith should handle NonFatal",
      IoSpec.testCanHandleError_IO_handleNonFatalWithShouldHandleNonFatalWith
    ),
    example(
      "test CanHandleError[IO].handleNonFatalWith should not handle Fatal",
      IoSpec.testCanHandleError_IO_handleNonFatalWithShouldNotHandleFatalWith
    ),
    example(
      "test CanHandleError[IO].handleNonFatalWith should return the successful result",
      IoSpec.testCanHandleError_IO_handleNonFatalWithShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[IO].handleNonFatalWithEither should handle NonFatal",
      IoSpec.testCanHandleError_IO_handleNonFatalWithEitherShouldHandleNonFatalWith
    ),
    example(
      "test CanHandleError[IO].handleNonFatalWithEither should not handle Fatal",
      IoSpec.testCanHandleError_IO_handleNonFatalWithEitherShouldNotHandleFatalWith
    ),
    example(
      "test CanHandleError[IO].handleNonFatalWithEither should return the successful result",
      IoSpec.testCanHandleError_IO_handleNonFatalWithEitherShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[IO].handleNonFatalWithEither should return the failed result",
      IoSpec.testCanHandleError_IO_handleNonFatalWithEitherShouldReturnFailedResult
    ),
    example(
      "test CanHandleError[IO].handleEitherNonFatalWith should handle NonFatal",
      IoSpec.testCanHandleError_IO_handleEitherNonFatalWithShouldHandleNonFatalWith
    ),
    example(
      "test CanHandleError[IO].handleEitherNonFatalWith should not handle Fatal",
      IoSpec.testCanHandleError_IO_handleEitherNonFatalWithShouldNotHandleFatalWith
    ),
    example(
      "test CanHandleError[IO].handleEitherNonFatalWith should return the successful result",
      IoSpec.testCanHandleError_IO_handleEitherNonFatalWithShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[IO].handleEitherNonFatalWith should return the failed result",
      IoSpec.testCanHandleError_IO_handleEitherNonFatalWithShouldReturnFailedResult
    ),
    example(
      "test CanHandleError[IO].handleEitherTNonFatalWith should handle NonFatal",
      IoSpec.testCanHandleError_IO_handleEitherTNonFatalWithShouldHandleNonFatalWith
    ),
    example(
      "test CanHandleError[IO].handleEitherTNonFatalWith should not handle Fatal",
      IoSpec.testCanHandleError_IO_handleEitherTNonFatalWithShouldNotHandleFatalWith
    ),
    example(
      "test CanHandleError[IO].handleEitherTNonFatalWith should return the successful result",
      IoSpec.testCanHandleError_IO_handleEitherTNonFatalWithShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[IO].handleEitherTNonFatalWith should return the failed result",
      IoSpec.testCanHandleError_IO_handleEitherTNonFatalWithShouldReturnFailedResult
    ),
    example(
      "test CanHandleError[IO].handleNonFatal should handle NonFatal",
      IoSpec.testCanHandleError_IO_handleNonFatalShouldHandleNonFatal
    ),
    example(
      "test CanHandleError[IO].handleNonFatal should not handle Fatal",
      IoSpec.testCanHandleError_IO_handleNonFatalShouldNotHandleFatal
    ),
    example(
      "test CanHandleError[IO].handleNonFatal should return the successful result",
      IoSpec.testCanHandleError_IO_handleNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[IO].handleNonFatalEither should handle NonFatal",
      IoSpec.testCanHandleError_IO_handleNonFatalEitherShouldHandleNonFatal
    ),
    example(
      "test CanHandleError[IO].handleNonFatalEither should not handle Fatal",
      IoSpec.testCanHandleError_IO_handleNonFatalEitherShouldNotHandleFatal
    ),
    example(
      "test CanHandleError[IO].handleNonFatalEither should return the successful result",
      IoSpec.testCanHandleError_IO_handleNonFatalEitherShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[IO].handleNonFatalEither should return the failed result",
      IoSpec.testCanHandleError_IO_handleNonFatalEitherShouldReturnFailedResult
    ),
    example(
      "test CanHandleError[IO].handleEitherNonFatal should handle NonFatal",
      IoSpec.testCanHandleError_IO_handleEitherNonFatalShouldHandleNonFatal
    ),
    example(
      "test CanHandleError[IO].handleEitherNonFatal should not handle Fatal",
      IoSpec.testCanHandleError_IO_handleEitherNonFatalShouldNotHandleFatal
    ),
    example(
      "test CanHandleError[IO].handleEitherNonFatal should return the successful result",
      IoSpec.testCanHandleError_IO_handleEitherNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[IO].handleEitherNonFatal should return the failed result",
      IoSpec.testCanHandleError_IO_handleEitherNonFatalShouldReturnFailedResult
    ),
    example(
      "test CanHandleError[IO].handleEitherTNonFatal should handle NonFatal",
      IoSpec.testCanHandleError_IO_handleEitherTNonFatalShouldHandleNonFatal
    ),
    example(
      "test CanHandleError[IO].handleEitherTNonFatal should not handle Fatal",
      IoSpec.testCanHandleError_IO_handleEitherTNonFatalShouldNotHandleFatal
    ),
    example(
      "test CanHandleError[IO].handleEitherTNonFatal should return the successful result",
      IoSpec.testCanHandleError_IO_handleEitherTNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[IO].handleEitherTNonFatal should return the failed result",
      IoSpec.testCanHandleError_IO_handleEitherTNonFatalShouldReturnFailedResult
    ),
  )

  /* Future */
  private val futureSpecs = effectie.core.CanHandleErrorSpec.futureSpecs ++ List(
    example(
      "test CanHandleError[Future].handleEitherTNonFatalWith should handle NonFatal",
      FutureSpec.testCanHandleError_Future_handleEitherTNonFatalWithShouldHandleNonFatalWith
    ),
    example(
      "test CanHandleError[Future].handleEitherTNonFatalWith should return the successful result",
      FutureSpec.testCanHandleError_Future_handleEitherTNonFatalWithShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[Future].handleEitherTNonFatalWith should return the failed result",
      FutureSpec.testCanHandleError_Future_handleEitherTNonFatalWithShouldReturnFailedResult
    ),
    example(
      "test CanHandleError[Future].handleEitherTNonFatal should handle NonFatal",
      FutureSpec.testCanHandleError_Future_handleEitherTNonFatalShouldHandleNonFatal
    ),
    example(
      "test CanHandleError[Future].handleEitherTNonFatal should return the successful result",
      FutureSpec.testCanHandleError_Future_handleEitherTNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[Future].handleEitherTNonFatal should return the failed result",
      FutureSpec.testCanHandleError_Future_handleEitherTNonFatalShouldReturnFailedResult
    ),
  )

  /* Id */
  private val idSpecs = List(
    example(
      "test CanHandleError[Id].handleNonFatalWith should handle NonFatal",
      IdSpec.testCanHandleError_Id_handleNonFatalWithShouldHandleNonFatalWith
    ),
    example(
      "test CanHandleError[Id].handleNonFatalWith should not handle Fatal",
      IdSpec.testCanHandleError_Id_handleNonFatalWithShouldNotHandleFatalWith
    ),
    example(
      "test CanHandleError[Id].handleNonFatalWith should return the successful result",
      IdSpec.testCanHandleError_Id_handleNonFatalWithShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[Id].handleNonFatalWithEither should handle NonFatal",
      IdSpec.testCanHandleError_Id_handleNonFatalWithEitherShouldHandleNonFatalWith
    ),
    example(
      "test CanHandleError[Id].handleNonFatalWithEither should not handle Fatal",
      IdSpec.testCanHandleError_Id_handleNonFatalWithEitherShouldNotHandleFatalWith
    ),
    example(
      "test CanHandleError[Id].handleNonFatalWithEither should return the successful result",
      IdSpec.testCanHandleError_Id_handleNonFatalWithEitherShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[Id].handleNonFatalWithEither should return the failed result",
      IdSpec.testCanHandleError_Id_handleNonFatalWithEitherShouldReturnFailedResult
    ),
    example(
      "test CanHandleError[Id].handleEitherNonFatalWith should handle NonFatal",
      IdSpec.testCanHandleError_Id_handleEitherNonFatalWithShouldHandleNonFatalWith
    ),
    example(
      "test CanHandleError[Id].handleEitherNonFatalWith should not handle Fatal",
      IdSpec.testCanHandleError_Id_handleEitherNonFatalWithShouldNotHandleFatalWith
    ),
    example(
      "test CanHandleError[Id].handleEitherNonFatalWith should return the successful result",
      IdSpec.testCanHandleError_Id_handleEitherNonFatalWithShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[Id].handleEitherNonFatalWith should return the failed result",
      IdSpec.testCanHandleError_Id_handleEitherNonFatalWithShouldReturnFailedResult
    ),
    example(
      "test CanHandleError[Id].handleEitherTNonFatalWith should handle NonFatal",
      IdSpec.testCanHandleError_Id_handleEitherTNonFatalWithShouldHandleNonFatalWith
    ),
    example(
      "test CanHandleError[Id].handleEitherTNonFatalWith should not handle Fatal",
      IdSpec.testCanHandleError_Id_handleEitherTNonFatalWithShouldNotHandleFatalWith
    ),
    example(
      "test CanHandleError[Id].handleEitherTNonFatalWith should return the successful result",
      IdSpec.testCanHandleError_Id_handleEitherTNonFatalWithShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[Id].handleEitherTNonFatalWith should return the failed result",
      IdSpec.testCanHandleError_Id_handleEitherTNonFatalWithShouldReturnFailedResult
    ),
    example(
      "test CanHandleError[Id].handleNonFatal should handle NonFatal",
      IdSpec.testCanHandleError_Id_handleNonFatalShouldHandleNonFatal
    ),
    example(
      "test CanHandleError[Id].handleNonFatal should not handle Fatal",
      IdSpec.testCanHandleError_Id_handleNonFatalShouldNotHandleFatal
    ),
    example(
      "test CanHandleError[Id].handleNonFatal should return the successful result",
      IdSpec.testCanHandleError_Id_handleNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[Id].handleNonFatalEither should handle NonFatal",
      IdSpec.testCanHandleError_Id_handleNonFatalEitherShouldHandleNonFatal
    ),
    example(
      "test CanHandleError[Id].handleNonFatalEither should not handle Fatal",
      IdSpec.testCanHandleError_Id_handleNonFatalEitherShouldNotHandleFatal
    ),
    example(
      "test CanHandleError[Id].handleNonFatalEither should return the successful result",
      IdSpec.testCanHandleError_Id_handleNonFatalEitherShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[Id].handleNonFatalEither should return the failed result",
      IdSpec.testCanHandleError_Id_handleNonFatalEitherShouldReturnFailedResult
    ),
    example(
      "test CanHandleError[Id].handleEitherNonFatal should handle NonFatal",
      IdSpec.testCanHandleError_Id_handleEitherNonFatalShouldHandleNonFatal
    ),
    example(
      "test CanHandleError[Id].handleEitherNonFatal should not handle Fatal",
      IdSpec.testCanHandleError_Id_handleEitherNonFatalShouldNotHandleFatal
    ),
    example(
      "test CanHandleError[Id].handleEitherNonFatal should return the successful result",
      IdSpec.testCanHandleError_Id_handleEitherNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[Id].handleEitherNonFatal should return the failed result",
      IdSpec.testCanHandleError_Id_handleEitherNonFatalShouldReturnFailedResult
    ),
    example(
      "test CanHandleError[Id].handleEitherTNonFatal should handle NonFatal",
      IdSpec.testCanHandleError_Id_handleEitherTNonFatalShouldHandleNonFatal
    ),
    example(
      "test CanHandleError[Id].handleEitherTNonFatal should not handle Fatal",
      IdSpec.testCanHandleError_Id_handleEitherTNonFatalShouldNotHandleFatal
    ),
    example(
      "test CanHandleError[Id].handleEitherTNonFatal should return the successful result",
      IdSpec.testCanHandleError_Id_handleEitherTNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[Id].handleEitherTNonFatal should return the failed result",
      IdSpec.testCanHandleError_Id_handleEitherTNonFatalShouldReturnFailedResult
    )
  )

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor: Functor, A](a: => A): F[A] =
    effectOf[F](a)

  object TaskSpec {
    import monix.execution.Scheduler.Implicits.global

    def testCanHandleError_Task_handleNonFatalWithShouldHandleNonFatalWith: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 123
      val actual            = CanHandleError[Task]
        .handleNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) =>
            Task.pure(expected)
        }
        .runSyncUnsafe()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Task_handleNonFatalWithShouldNotHandleFatalWith: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[Task, Int](throwThrowable[Int](fatalExpcetion))

      try {
        val actual = CanHandleError[Task].handleNonFatalWith(fa)(_ => Task.pure(123)).runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Task_handleNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Int](1)
      val expected = 1
      val actual   = CanHandleError[Task].handleNonFatalWith(fa)(_ => Task.pure(999)).runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleNonFatalWithEitherShouldHandleNonFatalWith: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
      val actualFailedResult   =
        CanHandleError[Task].handleNonFatalWith(fa)(_ => Task.pure(expectedFailedResult)).runSyncUnsafe()

      val expectedSuccessResult = 1.asRight[SomeError]
      val actualSuccessResult   =
        CanHandleError[Task].handleNonFatalWith(fa)(_ => Task.pure(1.asRight[SomeError])).runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Task_handleNonFatalWithEitherShouldNotHandleFatalWith: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual = CanHandleError[Task].handleNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError])).runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Task_handleNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanHandleError[Task].handleNonFatalWith(fa)(_ => Task(999.asRight[SomeError])).runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleNonFatalWithEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual = CanHandleError[Task].handleNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError])).runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleEitherNonFatalWithShouldHandleNonFatalWith: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult    = CanHandleError[Task]
        .handleEitherNonFatalWith(fa)(err => Task.pure(SomeError.someThrowable(err).asLeft[Int]))
        .runSyncUnsafe()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        CanHandleError[Task].handleEitherNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError])).runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Task_handleEitherNonFatalWithShouldNotHandleFatalWith: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual =
          CanHandleError[Task]
            .handleEitherNonFatalWith(fa)(err => Task.pure(SomeError.someThrowable(err).asLeft[Int]))
            .runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Task_handleEitherNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   =
        CanHandleError[Task].handleEitherNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError])).runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleEitherNonFatalWithShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        CanHandleError[Task].handleEitherNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError])).runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleEitherTNonFatalWithShouldHandleNonFatalWith: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult    = CanHandleError[Task]
        .handleEitherTNonFatalWith(fa)(err => Task.pure(SomeError.someThrowable(err).asLeft[Int]))
        .value
        .runSyncUnsafe()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        CanHandleError[Task].handleEitherTNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError])).value.runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Task_handleEitherTNonFatalWithShouldNotHandleFatalWith: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

      try {
        val actual =
          CanHandleError[Task]
            .handleEitherTNonFatalWith(fa)(err => Task.pure(SomeError.someThrowable(err).asLeft[Int]))
            .value
            .runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Task_handleEitherTNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   =
        CanHandleError[Task].handleEitherTNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError])).value.runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleEitherTNonFatalWithShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        CanHandleError[Task].handleEitherTNonFatalWith(fa)(_ => Task.pure(123.asRight[SomeError])).value.runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleNonFatalShouldHandleNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 123
      val actual            = CanHandleError[Task]
        .handleNonFatal(fa) {
          case NonFatal(`expectedExpcetion`) =>
            expected
        }
        .runSyncUnsafe()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Task_handleNonFatalShouldNotHandleFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[Task, Int](throwThrowable[Int](fatalExpcetion))

      try {
        val actual = CanHandleError[Task].handleNonFatal(fa)(_ => 123).runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Task_handleNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Int](1)
      val expected = 1
      val actual   = CanHandleError[Task].handleNonFatal(fa)(_ => 999).runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleNonFatalEitherShouldHandleNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
      val actualFailedResult   = CanHandleError[Task].handleNonFatal(fa)(_ => expectedFailedResult).runSyncUnsafe()

      val expectedSuccessResult = 1.asRight[SomeError]
      val actualSuccessResult   = CanHandleError[Task].handleNonFatal(fa)(_ => 1.asRight[SomeError]).runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Task_handleNonFatalEitherShouldNotHandleFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual = CanHandleError[Task].handleNonFatal(fa)(_ => 123.asRight[SomeError]).runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Task_handleNonFatalEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanHandleError[Task].handleNonFatal(fa)(_ => 999.asRight[SomeError]).runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleNonFatalEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanHandleError[Task].handleNonFatal(fa)(_ => 123.asRight[SomeError]).runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleEitherNonFatalShouldHandleNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult    = CanHandleError[Task]
        .handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
        .runSyncUnsafe()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        CanHandleError[Task].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError]).runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Task_handleEitherNonFatalShouldNotHandleFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual =
          CanHandleError[Task]
            .handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
            .runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Task_handleEitherNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanHandleError[Task]
        .handleEitherNonFatal(fa)(_ => 123.asRight[SomeError])
        .runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleEitherNonFatalShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        CanHandleError[Task].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError]).runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleEitherTNonFatalShouldHandleNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult    = CanHandleError[Task]
        .handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
        .value
        .runSyncUnsafe()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        CanHandleError[Task].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value.runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Task_handleEitherTNonFatalShouldNotHandleFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

      try {
        val actual =
          CanHandleError[Task]
            .handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
            .value
            .runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Task_handleEitherTNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = CanHandleError[Task].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value.runSyncUnsafe()

      actual ==== expected
    }

    def testCanHandleError_Task_handleEitherTNonFatalShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        CanHandleError[Task].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value.runSyncUnsafe()

      actual ==== expected
    }

  }

  object IoSpec {
    import effectie.ce2.fxCtor.ioFxCtor

    def testCanHandleError_IO_handleNonFatalWithShouldHandleNonFatalWith: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 123
      val actual            = CanHandleError[IO]
        .handleNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) =>
            IO.pure(expected)
        }
        .unsafeRunSync()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_IO_handleNonFatalWithShouldNotHandleFatalWith: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))

      try {
        val actual = CanHandleError[IO].handleNonFatalWith(fa)(_ => IO.pure(123)).unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_IO_handleNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = run[IO, Int](1)
      val expected = 1
      val actual   = CanHandleError[IO].handleNonFatalWith(fa)(_ => IO.pure(999)).unsafeRunSync()

      actual ==== expected
    }

    def testCanHandleError_IO_handleNonFatalWithEitherShouldHandleNonFatalWith: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
      val actualFailedResult   =
        CanHandleError[IO].handleNonFatalWith(fa)(_ => IO.pure(expectedFailedResult)).unsafeRunSync()

      val expectedSuccessResult = 1.asRight[SomeError]
      val actualSuccessResult   =
        CanHandleError[IO].handleNonFatalWith(fa)(_ => IO.pure(1.asRight[SomeError])).unsafeRunSync()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_IO_handleNonFatalWithEitherShouldNotHandleFatalWith: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual = CanHandleError[IO].handleNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_IO_handleNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanHandleError[IO].handleNonFatalWith(fa)(_ => IO(999.asRight[SomeError])).unsafeRunSync()

      actual ==== expected
    }

    def testCanHandleError_IO_handleNonFatalWithEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual = CanHandleError[IO].handleNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).unsafeRunSync()

      actual ==== expected
    }

    def testCanHandleError_IO_handleEitherNonFatalWithShouldHandleNonFatalWith: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult    = CanHandleError[IO]
        .handleEitherNonFatalWith(fa)(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
        .unsafeRunSync()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        CanHandleError[IO].handleEitherNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).unsafeRunSync()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_IO_handleEitherNonFatalWithShouldNotHandleFatalWith: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual =
          CanHandleError[IO]
            .handleEitherNonFatalWith(fa)(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
            .unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_IO_handleEitherNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   =
        CanHandleError[IO].handleEitherNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).unsafeRunSync()

      actual ==== expected
    }

    def testCanHandleError_IO_handleEitherNonFatalWithShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        CanHandleError[IO].handleEitherNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).unsafeRunSync()

      actual ==== expected
    }

    def testCanHandleError_IO_handleEitherTNonFatalWithShouldHandleNonFatalWith: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult    = CanHandleError[IO]
        .handleEitherTNonFatalWith(fa)(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
        .value
        .unsafeRunSync()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        CanHandleError[IO].handleEitherTNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).value.unsafeRunSync()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_IO_handleEitherTNonFatalWithShouldNotHandleFatalWith: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

      try {
        val actual =
          CanHandleError[IO]
            .handleEitherTNonFatalWith(fa)(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
            .value
            .unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_IO_handleEitherTNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   =
        CanHandleError[IO].handleEitherTNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).value.unsafeRunSync()

      actual ==== expected
    }

    def testCanHandleError_IO_handleEitherTNonFatalWithShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        CanHandleError[IO].handleEitherTNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).value.unsafeRunSync()

      actual ==== expected
    }

    def testCanHandleError_IO_handleNonFatalShouldHandleNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 123
      val actual            = CanHandleError[IO]
        .handleNonFatal(fa) {
          case NonFatal(`expectedExpcetion`) =>
            expected
        }
        .unsafeRunSync()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_IO_handleNonFatalShouldNotHandleFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))

      try {
        val actual = CanHandleError[IO].handleNonFatal(fa)(_ => 123).unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_IO_handleNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = run[IO, Int](1)
      val expected = 1
      val actual   = CanHandleError[IO].handleNonFatal(fa)(_ => 999).unsafeRunSync()

      actual ==== expected
    }

    def testCanHandleError_IO_handleNonFatalEitherShouldHandleNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
      val actualFailedResult   = CanHandleError[IO].handleNonFatal(fa)(_ => expectedFailedResult).unsafeRunSync()

      val expectedSuccessResult = 1.asRight[SomeError]
      val actualSuccessResult   = CanHandleError[IO].handleNonFatal(fa)(_ => 1.asRight[SomeError]).unsafeRunSync()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_IO_handleNonFatalEitherShouldNotHandleFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual = CanHandleError[IO].handleNonFatal(fa)(_ => 123.asRight[SomeError]).unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_IO_handleNonFatalEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanHandleError[IO].handleNonFatal(fa)(_ => 999.asRight[SomeError]).unsafeRunSync()

      actual ==== expected
    }

    def testCanHandleError_IO_handleNonFatalEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanHandleError[IO].handleNonFatal(fa)(_ => 123.asRight[SomeError]).unsafeRunSync()

      actual ==== expected
    }

    def testCanHandleError_IO_handleEitherNonFatalShouldHandleNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult    = CanHandleError[IO]
        .handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
        .unsafeRunSync()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        CanHandleError[IO].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError]).unsafeRunSync()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_IO_handleEitherNonFatalShouldNotHandleFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual =
          CanHandleError[IO]
            .handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
            .unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_IO_handleEitherNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanHandleError[IO]
        .handleEitherNonFatal(fa)(_ => 123.asRight[SomeError])
        .unsafeRunSync()

      actual ==== expected
    }

    def testCanHandleError_IO_handleEitherNonFatalShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        CanHandleError[IO].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError]).unsafeRunSync()

      actual ==== expected
    }

    def testCanHandleError_IO_handleEitherTNonFatalShouldHandleNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult    = CanHandleError[IO]
        .handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
        .value
        .unsafeRunSync()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult   =
        CanHandleError[IO].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value.unsafeRunSync()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_IO_handleEitherTNonFatalShouldNotHandleFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

      try {
        val actual =
          CanHandleError[IO]
            .handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
            .value
            .unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_IO_handleEitherTNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = CanHandleError[IO].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value.unsafeRunSync()

      actual ==== expected
    }

    def testCanHandleError_IO_handleEitherTNonFatalShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        CanHandleError[IO].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value.unsafeRunSync()

      actual ==== expected
    }

  }

  object FutureSpec {
    import effectie.instances.future.canHandleError._
    import effectie.instances.future.fxCtor._

    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration._
    import scala.concurrent.{ExecutionContext, Future}

    val waitFor = WaitFor(1.second)

    def testCanHandleError_Future_handleNonFatalWithShouldHandleNonFatalWith: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 1
      val actual            = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(CanHandleError[Future].handleNonFatalWith(fa)(_ => Future(expected)))

      actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalWithShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = run[Future, Int](1)
      val expected = 1
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(CanHandleError[Future].handleNonFatalWith(fa)(_ => Future(123)))

      actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalWithEitherShouldHandleNonFatalWith: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   =
        ConcurrentSupport.futureToValue(
          CanHandleError[Future].handleNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int])),
          waitFor
        )

      val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(CanHandleError[Future].handleNonFatalWith(fa2)(_ => Future(expected)))

      expectedFailedResult ==== actualFailedResult and actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(CanHandleError[Future].handleNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int])))

      actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalWithEitherShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(CanHandleError[Future].handleNonFatalWith(fa)(_ => Future(1.asRight[SomeError])))

      actual ==== expected
    }

    def testCanHandleError_Future_handleEitherNonFatalWithShouldHandleNonFatalWith: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   = ConcurrentSupport.futureToValue(
        CanHandleError[Future]
          .handleEitherNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int])),
        waitFor
      )

      val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(CanHandleError[Future].handleEitherNonFatalWith(fa2)(err => Future(expected)))

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanHandleError_Future_handleEitherNonFatalWithShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(
        CanHandleError[Future]
          .handleEitherNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int]))
      )

      actual ==== expected
    }

    def testCanHandleError_Future_handleEitherNonFatalWithShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(CanHandleError[Future].handleEitherNonFatalWith(fa)(_ => Future(expected)))

      actual ==== expected
    }

    def testCanHandleError_Future_handleEitherTNonFatalWithShouldHandleNonFatalWith: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   = ConcurrentSupport.futureToValue(
        CanHandleError[Future]
          .handleEitherTNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int]))
          .value,
        waitFor
      )

      val fa2 = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(CanHandleError[Future].handleEitherTNonFatalWith(fa2)(err => Future(expected)).value)

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanHandleError_Future_handleEitherTNonFatalWithShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(
        CanHandleError[Future]
          .handleEitherTNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int]))
          .value
      )

      actual ==== expected
    }

    def testCanHandleError_Future_handleEitherTNonFatalWithShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(CanHandleError[Future].handleEitherTNonFatalWith(fa)(_ => Future(expected)).value)

      actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalShouldHandleNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 1
      val actual            = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(CanHandleError[Future].handleNonFatal(fa)(_ => expected))

      actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = run[Future, Int](1)
      val expected = 1
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(CanHandleError[Future].handleNonFatal(fa)(_ => 123))

      actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalEitherShouldHandleNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   =
        ConcurrentSupport.futureToValue(
          CanHandleError[Future].handleNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]),
          waitFor
        )

      val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(CanHandleError[Future].handleNonFatal(fa2)(_ => expected))

      expectedFailedResult ==== actualFailedResult and actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalEitherShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(CanHandleError[Future].handleNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]))

      actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalEitherShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(CanHandleError[Future].handleNonFatal(fa)(_ => 1.asRight[SomeError]))

      actual ==== expected
    }

    def testCanHandleError_Future_handleEitherNonFatalShouldHandleNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   = ConcurrentSupport.futureToValue(
        CanHandleError[Future].handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]),
        waitFor
      )

      val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(CanHandleError[Future].handleEitherNonFatal(fa2)(err => expected))

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanHandleError_Future_handleEitherNonFatalShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(CanHandleError[Future].handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]))

      actual ==== expected
    }

    def testCanHandleError_Future_handleEitherNonFatalShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(CanHandleError[Future].handleEitherNonFatal(fa)(_ => expected))

      actual ==== expected
    }

    def testCanHandleError_Future_handleEitherTNonFatalShouldHandleNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   = ConcurrentSupport.futureToValue(
        CanHandleError[Future].handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]).value,
        waitFor
      )

      val fa2 = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected = 1.asRight[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(CanHandleError[Future].handleEitherTNonFatal(fa2)(err => expected).value)

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanHandleError_Future_handleEitherTNonFatalShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor
      )(CanHandleError[Future].handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]).value)

      actual ==== expected
    }

    def testCanHandleError_Future_handleEitherTNonFatalShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(CanHandleError[Future].handleEitherTNonFatal(fa)(_ => expected).value)

      actual ==== expected
    }

  }

  object IdSpec {
    import effectie.instances.id.fxCtor._

    def testCanHandleError_Id_handleNonFatalWithShouldHandleNonFatalWith: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 1
      val actual: Id[Int]   = CanHandleError[Id].handleNonFatalWith(fa)(_ => expected)

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Id_handleNonFatalWithShouldNotHandleFatalWith: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      lazy val fa        = run[Id, Int](throwThrowable[Int](fatalExpcetion))

      try {
        val actual: Id[Int] = CanHandleError[Id].handleNonFatalWith(fa)(_ => 1)
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Id_handleNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa              = run[Id, Int](1)
      val expected        = 1
      val actual: Id[Int] = CanHandleError[Id].handleNonFatalWith(fa)(_ => 123)

      actual ==== expected
    }

    def testCanHandleError_Id_handleNonFatalWithEitherShouldHandleNonFatalWith: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa           = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   =
        CanHandleError[Id].handleNonFatalWith(fa)(err => SomeError.someThrowable(err).asLeft[Int])

      lazy val fa2 = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual   = CanHandleError[Id].handleNonFatalWith(fa2)(_ => expected)

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Id_handleNonFatalWithEitherShouldNotHandleFatalWith: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      lazy val fa        = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual = CanHandleError[Id].handleNonFatalWith(fa)(_ => 1.asRight[SomeError])
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Id_handleNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanHandleError[Id].handleNonFatalWith(fa)(err => SomeError.someThrowable(err).asLeft[Int])

      actual ==== expected
    }

    def testCanHandleError_Id_handleNonFatalWithEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanHandleError[Id].handleNonFatalWith(fa)(_ => 1.asRight[SomeError])

      actual ==== expected
    }

    def testCanHandleError_Id_handleEitherNonFatalWithShouldHandleNonFatalWith: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa           = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   =
        CanHandleError[Id].handleEitherNonFatalWith(fa)(err => SomeError.someThrowable(err).asLeft[Int])

      lazy val fa2 = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual   = CanHandleError[Id].handleEitherNonFatalWith(fa2)(_ => expected)

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Id_handleEitherNonFatalWithShouldNotHandleFatalWith: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      lazy val fa        = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual = CanHandleError[Id].handleEitherNonFatalWith(fa)(_ => 1.asRight[SomeError])
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Id_handleEitherNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   =
        CanHandleError[Id].handleEitherNonFatalWith(fa)(err => SomeError.someThrowable(err).asLeft[Int])

      actual ==== expected
    }

    def testCanHandleError_Id_handleEitherNonFatalWithShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanHandleError[Id].handleEitherNonFatalWith(fa)(_ => 1.asRight[SomeError])

      actual ==== expected
    }

    def testCanHandleError_Id_handleEitherTNonFatalWithShouldHandleNonFatalWith: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   =
        CanHandleError[Id].handleEitherTNonFatalWith(fa)(err => SomeError.someThrowable(err).asLeft[Int]).value

      lazy val fa2 = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected = 1.asRight[SomeError]
      val actual   = CanHandleError[Id].handleEitherTNonFatalWith(fa2)(_ => expected).value

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Id_handleEitherTNonFatalWithShouldNotHandleFatalWith: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

      try {
        val actual = CanHandleError[Id].handleEitherTNonFatalWith(fa)(_ => 1.asRight[SomeError]).value
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Id_handleEitherTNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = EitherT(run[Id, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   =
        CanHandleError[Id].handleEitherTNonFatalWith(fa)(err => SomeError.someThrowable(err).asLeft[Int]).value

      actual ==== expected
    }

    def testCanHandleError_Id_handleEitherTNonFatalWithShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanHandleError[Id].handleEitherTNonFatalWith(fa)(_ => 1.asRight[SomeError]).value

      actual ==== expected
    }

    def testCanHandleError_Id_handleNonFatalShouldHandleNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 1
      val actual: Id[Int]   = CanHandleError[Id].handleNonFatal(fa)(_ => expected)

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Id_handleNonFatalShouldNotHandleFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      lazy val fa        = run[Id, Int](throwThrowable[Int](fatalExpcetion))

      try {
        val actual: Id[Int] = CanHandleError[Id].handleNonFatal(fa)(_ => 1)
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Id_handleNonFatalShouldReturnSuccessfulResult: Result = {

      val fa              = run[Id, Int](1)
      val expected        = 1
      val actual: Id[Int] = CanHandleError[Id].handleNonFatal(fa)(_ => 123)

      actual ==== expected
    }

    def testCanHandleError_Id_handleNonFatalEitherShouldHandleNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa           = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   = CanHandleError[Id].handleNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])

      lazy val fa2 = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual   = CanHandleError[Id].handleNonFatal(fa2)(_ => expected)

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Id_handleNonFatalEitherShouldNotHandleFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      lazy val fa        = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual = CanHandleError[Id].handleNonFatal(fa)(_ => 1.asRight[SomeError])
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Id_handleNonFatalEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanHandleError[Id].handleNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])

      actual ==== expected
    }

    def testCanHandleError_Id_handleNonFatalEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanHandleError[Id].handleNonFatal(fa)(_ => 1.asRight[SomeError])

      actual ==== expected
    }

    def testCanHandleError_Id_handleEitherNonFatalShouldHandleNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa           = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   =
        CanHandleError[Id].handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])

      lazy val fa2 = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual   = CanHandleError[Id].handleEitherNonFatal(fa2)(_ => expected)

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Id_handleEitherNonFatalShouldNotHandleFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      lazy val fa        = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual = CanHandleError[Id].handleEitherNonFatal(fa)(_ => 1.asRight[SomeError])
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Id_handleEitherNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanHandleError[Id].handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])

      actual ==== expected
    }

    def testCanHandleError_Id_handleEitherNonFatalShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanHandleError[Id].handleEitherNonFatal(fa)(_ => 1.asRight[SomeError])

      actual ==== expected
    }

    def testCanHandleError_Id_handleEitherTNonFatalShouldHandleNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult   =
        CanHandleError[Id].handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]).value

      lazy val fa2 = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected = 1.asRight[SomeError]
      val actual   = CanHandleError[Id].handleEitherTNonFatal(fa2)(_ => expected).value

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Id_handleEitherTNonFatalShouldNotHandleFatal: Result = {

      val fatalExpcetion = SomeControlThrowable("Something's wrong")
      lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

      try {
        val actual = CanHandleError[Id].handleEitherTNonFatal(fa)(_ => 1.asRight[SomeError]).value
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Id_handleEitherTNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = EitherT(run[Id, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = CanHandleError[Id].handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]).value

      actual ==== expected
    }

    def testCanHandleError_Id_handleEitherTNonFatalShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanHandleError[Id].handleEitherTNonFatal(fa)(_ => 1.asRight[SomeError]).value

      actual ==== expected
    }

  }

}
