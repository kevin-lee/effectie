package effectie.monix


import cats._
import cats.syntax.all._
import cats.instances.all._
import cats.data.EitherT
import monix.eval.Task

import effectie.monix.Effectful._
import effectie.SomeControlThrowable
import effectie.concurrent.ExecutorServiceOps

import hedgehog._
import hedgehog.runner._

import scala.util.control.{ControlThrowable, NonFatal}

/**
 * @author Kevin Lee
 * @since 2020-08-17
 */
object CanRecoverSpec extends Properties {

  override def tests: List[Test] = List(
    /* Task */
    example("test CanRecover[Task].recoverFromNonFatalWith should catch NonFatal", TaskSpec.testCanRecover_IO_recoverFromNonFatalWithShouldRecoverFromNonFatal),
    example("test CanRecover[Task].recoverFromNonFatalWith should not catch Fatal", TaskSpec.testCanRecover_IO_recoverFromNonFatalWithShouldNotCatchFatal),
    example("test CanRecover[Task].recoverFromNonFatalWith should return the successful result", TaskSpec.testCanRecover_IO_recoverFromNonFatalWithShouldReturnSuccessfulResult),

    example("test CanRecover[Task].recoverFromNonFatalWithEither should catch NonFatal", TaskSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal),
    example("test CanRecover[Task].recoverFromNonFatalWithEither should not catch Fatal", TaskSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldNotCatchFatal),
    example("test CanRecover[Task].recoverFromNonFatalWithEither should return the successful result", TaskSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult),
    example("test CanRecover[Task].recoverFromNonFatalWithEither should return the failed result", TaskSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnFailedResult),

    example("test CanRecover[Task].recoverEitherTFromNonFatalWith should catch NonFatal", TaskSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal),
    example("test CanRecover[Task].recoverEitherTFromNonFatalWith should not catch Fatal", TaskSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldNotCatchFatal),
    example("test CanRecover[Task].recoverEitherTFromNonFatalWith should return the successful result", TaskSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult),
    example("test CanRecover[Task].recoverEitherTFromNonFatalWith should return the failed result", TaskSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnFailedResult),


    example("test CanRecover[Task].recoverFromNonFatal should catch NonFatal", TaskSpec.testCanRecover_IO_recoverFromNonFatalShouldRecoverFromNonFatal),
    example("test CanRecover[Task].recoverFromNonFatal should not catch Fatal", TaskSpec.testCanRecover_IO_recoverFromNonFatalShouldNotCatchFatal),
    example("test CanRecover[Task].recoverFromNonFatal should return the successful result", TaskSpec.testCanRecover_IO_recoverFromNonFatalShouldReturnSuccessfulResult),

    example("test CanRecover[Task].recoverFromNonFatalEither should catch NonFatal", TaskSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldRecoverFromNonFatal),
    example("test CanRecover[Task].recoverFromNonFatalEither should not catch Fatal", TaskSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldNotCatchFatal),
    example("test CanRecover[Task].recoverFromNonFatalEither should return the successful result", TaskSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldReturnSuccessfulResult),
    example("test CanRecover[Task].recoverFromNonFatalEither should return the failed result", TaskSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldReturnFailedResult),

    example("test CanRecover[Task].recoverEitherTFromNonFatal should catch NonFatal", TaskSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldRecoverFromNonFatal),
    example("test CanRecover[Task].recoverEitherTFromNonFatal should not catch Fatal", TaskSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldNotCatchFatal),
    example("test CanRecover[Task].recoverEitherTFromNonFatal should return the successful result", TaskSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnSuccessfulResult),
    example("test CanRecover[Task].recoverEitherTFromNonFatal should return the failed result", TaskSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnFailedResult),

    /* Future */

    example("test CanRecover[Future].recoverFromNonFatalWith should catch NonFatal", FutureSpec.testCanRecover_Future_recoverFromNonFatalWithShouldRecoverFromNonFatal),
    example("test CanRecover[Future].recoverFromNonFatalWith should return the successful result", FutureSpec.testCanRecover_Future_recoverFromNonFatalWithShouldReturnSuccessfulResult),

    example("test CanRecover[Future].recoverFromNonFatalWithEither should catch NonFatal", FutureSpec.testCanRecover_Future_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal),
    example("test CanRecover[Future].recoverFromNonFatalWithEither should return the successful result", FutureSpec.testCanRecover_Future_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult),
    example("test CanRecover[Future].recoverFromNonFatalWithEither should return the failed result", FutureSpec.testCanRecover_Future_recoverFromNonFatalWithEitherShouldReturnFailedResult),

    example("test CanRecover[Future].recoverEitherTFromNonFatalWith should catch NonFatal", FutureSpec.testCanRecover_Future_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal),
    example("test CanRecover[Future].recoverEitherTFromNonFatalWith should return the successful result", FutureSpec.testCanRecover_Future_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult),
    example("test CanRecover[Future].recoverEitherTFromNonFatalWith should return the failed result", FutureSpec.testCanRecover_Future_recoverEitherTFromNonFatalWithShouldReturnFailedResult),


    example("test CanRecover[Future].recoverFromNonFatal should catch NonFatal", FutureSpec.testCanRecover_Future_recoverFromNonFatalShouldRecoverFromNonFatal),
    example("test CanRecover[Future].recoverFromNonFatal should return the successful result", FutureSpec.testCanRecover_Future_recoverFromNonFatalShouldReturnSuccessfulResult),

    example("test CanRecover[Future].recoverFromNonFatalEither should catch NonFatal", FutureSpec.testCanRecover_Future_recoverFromNonFatalEitherShouldRecoverFromNonFatal),
    example("test CanRecover[Future].recoverFromNonFatalEither should return the successful result", FutureSpec.testCanRecover_Future_recoverFromNonFatalEitherShouldReturnSuccessfulResult),
    example("test CanRecover[Future].recoverFromNonFatalEither should return the failed result", FutureSpec.testCanRecover_Future_recoverFromNonFatalEitherShouldReturnFailedResult),

    example("test CanRecover[Future].recoverEitherTFromNonFatal should catch NonFatal", FutureSpec.testCanRecover_Future_recoverEitherTFromNonFatalShouldRecoverFromNonFatal),
    example("test CanRecover[Future].recoverEitherTFromNonFatal should return the successful result", FutureSpec.testCanRecover_Future_recoverEitherTFromNonFatalShouldReturnSuccessfulResult),
    example("test CanRecover[Future].recoverEitherTFromNonFatal should return the failed result", FutureSpec.testCanRecover_Future_recoverEitherTFromNonFatalShouldReturnFailedResult),


    /* Id */
    example("test CanRecover[Id].recoverFromNonFatalWith should catch NonFatal", IdSpec.testCanRecover_Id_recoverFromNonFatalWithShouldRecoverFromNonFatal),
    example("test CanRecover[Id].recoverFromNonFatalWith should not catch Fatal", IdSpec.testCanRecover_Id_recoverFromNonFatalWithShouldNotCatchFatal),
    example("test CanRecover[Id].recoverFromNonFatalWith should return the successful result", IdSpec.testCanRecover_Id_recoverFromNonFatalWithShouldReturnSuccessfulResult),

    example("test CanRecover[Id].recoverFromNonFatalWithEither should catch NonFatal", IdSpec.testCanRecover_Id_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal),
    example("test CanRecover[Id].recoverFromNonFatalWithEither should not catch Fatal", IdSpec.testCanRecover_Id_recoverFromNonFatalWithEitherShouldNotCatchFatal),
    example("test CanRecover[Id].recoverFromNonFatalWithEither should return the successful result", IdSpec.testCanRecover_Id_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult),
    example("test CanRecover[Id].recoverFromNonFatalWithEither should return the failed result", IdSpec.testCanRecover_Id_recoverFromNonFatalWithEitherShouldReturnFailedResult),

    example("test CanRecover[Id].recoverEitherTFromNonFatalWith should catch NonFatal", IdSpec.testCanRecover_Id_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal),
    example("test CanRecover[Id].recoverEitherTFromNonFatalWith should not catch Fatal", IdSpec.testCanRecover_Id_recoverEitherTFromNonFatalWithShouldNotCatchFatal),
    example("test CanRecover[Id].recoverEitherTFromNonFatalWith should return the successful result", IdSpec.testCanRecover_Id_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult),
    example("test CanRecover[Id].recoverEitherTFromNonFatalWith should return the failed result", IdSpec.testCanRecover_Id_recoverEitherTFromNonFatalWithShouldReturnFailedResult),



    example("test CanRecover[Id].recoverFromNonFatal should catch NonFatal", IdSpec.testCanRecover_Id_recoverFromNonFatalShouldRecoverFromNonFatal),
    example("test CanRecover[Id].recoverFromNonFatal should not catch Fatal", IdSpec.testCanRecover_Id_recoverFromNonFatalShouldNotCatchFatal),
    example("test CanRecover[Id].recoverFromNonFatal should return the successful result", IdSpec.testCanRecover_Id_recoverFromNonFatalShouldReturnSuccessfulResult),

    example("test CanRecover[Id].recoverFromNonFatalEither should catch NonFatal", IdSpec.testCanRecover_Id_recoverFromNonFatalEitherShouldRecoverFromNonFatal),
    example("test CanRecover[Id].recoverFromNonFatalEither should not catch Fatal", IdSpec.testCanRecover_Id_recoverFromNonFatalEitherShouldNotCatchFatal),
    example("test CanRecover[Id].recoverFromNonFatalEither should return the successful result", IdSpec.testCanRecover_Id_recoverFromNonFatalEitherShouldReturnSuccessfulResult),
    example("test CanRecover[Id].recoverFromNonFatalEither should return the failed result", IdSpec.testCanRecover_Id_recoverFromNonFatalEitherShouldReturnFailedResult),

    example("test CanRecover[Id].recoverEitherTFromNonFatal should catch NonFatal", IdSpec.testCanRecover_Id_recoverEitherTFromNonFatalShouldRecoverFromNonFatal),
    example("test CanRecover[Id].recoverEitherTFromNonFatal should not catch Fatal", IdSpec.testCanRecover_Id_recoverEitherTFromNonFatalShouldNotCatchFatal),
    example("test CanRecover[Id].recoverEitherTFromNonFatal should return the successful result", IdSpec.testCanRecover_Id_recoverEitherTFromNonFatalShouldReturnSuccessfulResult),
    example("test CanRecover[Id].recoverEitherTFromNonFatal should return the failed result", IdSpec.testCanRecover_Id_recoverEitherTFromNonFatalShouldReturnFailedResult)

  )

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable

  def run[F[_]: EffectConstructor: Functor, A](a: => A): F[A] =
    effectOf[F](a)

  sealed trait SomeError
  object SomeError {

    final case class SomeThrowable(throwable: Throwable) extends SomeError
    final case class Message(message: String) extends SomeError

    def someThrowable(throwable: Throwable): SomeError = SomeThrowable(throwable)

    def message(message: String): SomeError = Message(message)

  }

  object TaskSpec {
    import monix.execution.Scheduler.Implicits.global

    def testCanRecover_IO_recoverFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Task, Int](throwThrowable[Int](expectedExpcetion))
      val expected = 123
      val actual = CanRecover[Task].recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) =>
          Task.pure(expected)
      }.runSyncUnsafe()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_IO_recoverFromNonFatalWithShouldNotCatchFatal: Result = {

      val expectedExpcetion = new SomeControlThrowable("Something's wrong")
      val fa = run[Task, Int](throwThrowable[Int](expectedExpcetion))

      val task = CanRecover[Task].recoverFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => Task.pure(123) }
      try {
        val actual = task.runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa = run[Task, Int](1)
      val expected = 1
      val actual = CanRecover[Task].recoverFromNonFatalWith(fa) {
        case NonFatal(_) => Task.pure(999)
      }.runSyncUnsafe()

      actual ==== expected
    }


    def testCanRecover_IO_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
      val actualFailedResult = CanRecover[Task].recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) => Task.pure(expectedFailedResult)
      }.runSyncUnsafe()

      val expectedSuccessResult = 1.asRight[SomeError]
      val actualSuccessResult = CanRecover[Task].recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) => Task.pure(1.asRight[SomeError])
      }.runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_IO_recoverFromNonFatalWithEitherShouldNotCatchFatal: Result = {

      val expectedExpcetion = new SomeControlThrowable("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

      val task = CanRecover[Task].recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) => Task.pure(123.asRight[SomeError])
      }
      try {
        val actual = task.runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

      val fa = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual = CanRecover[Task].recoverFromNonFatalWith(fa) {
        case NonFatal(_) => Task(999.asRight[SomeError])
      }.runSyncUnsafe()

      actual ==== expected
    }

    def testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected = expectedFailure.asLeft[Int]
      val actual = CanRecover[Task].recoverFromNonFatalWith(fa) {
        case NonFatal(_) => Task.pure(123.asRight[SomeError])
      }.runSyncUnsafe()

      actual ==== expected
    }


    def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult = CanRecover[Task].recoverEitherTFromNonFatalWith(fa) {
        case err => Task.pure(SomeError.someThrowable(err).asLeft[Int])
      }.value.runSyncUnsafe()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult = CanRecover[Task].recoverEitherTFromNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) => Task.pure(123.asRight[SomeError])
      }.value.runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldNotCatchFatal: Result = {

      val expectedExpcetion = new SomeControlThrowable("Something's wrong")
      val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))

      val task = CanRecover[Task].recoverEitherTFromNonFatalWith(fa) {
        case err => Task.pure(SomeError.someThrowable(err).asLeft[Int])
      }
      try {
        val actual = task.value.runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual = CanRecover[Task].recoverEitherTFromNonFatalWith(fa) {
        case NonFatal(_) => Task.pure(123.asRight[SomeError])
      }.value.runSyncUnsafe()

      actual ==== expected
    }

    def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected = expectedFailure.asLeft[Int]
      val actual =
        CanRecover[Task].recoverEitherTFromNonFatalWith(fa) {
          case NonFatal(_) => Task.pure(123.asRight[SomeError])
        }.value.runSyncUnsafe()

      actual ==== expected
    }

    ///

    def testCanRecover_IO_recoverFromNonFatalShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Task, Int](throwThrowable[Int](expectedExpcetion))
      val expected = 123
      val actual = CanRecover[Task].recoverFromNonFatal(fa) {
        case NonFatal(`expectedExpcetion`) =>
          expected
      }.runSyncUnsafe()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_IO_recoverFromNonFatalShouldNotCatchFatal: Result = {

      val expectedExpcetion = new SomeControlThrowable("Something's wrong")
      val fa = run[Task, Int](throwThrowable[Int](expectedExpcetion))

      val task = CanRecover[Task].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123 }
      try {
        val actual = task.runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverFromNonFatalShouldReturnSuccessfulResult: Result = {

      val fa = run[Task, Int](1)
      val expected = 1
      val actual = CanRecover[Task].recoverFromNonFatal(fa) { case NonFatal(_) => 999 }.runSyncUnsafe()

      actual ==== expected
    }


    def testCanRecover_IO_recoverFromNonFatalEitherShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
      val actualFailedResult = CanRecover[Task].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expectedFailedResult }.runSyncUnsafe()

      val expectedSuccessResult = 1.asRight[SomeError]
      val actualSuccessResult = CanRecover[Task].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }.runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_IO_recoverFromNonFatalEitherShouldNotCatchFatal: Result = {

      val expectedExpcetion = new SomeControlThrowable("Something's wrong")
      val fa = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

      val task = CanRecover[Task].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }
      try {
        val actual = task.runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverFromNonFatalEitherShouldReturnSuccessfulResult: Result = {

      val fa = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual = CanRecover[Task].recoverFromNonFatal(fa) { case NonFatal(_) => 999.asRight[SomeError] }.runSyncUnsafe()

      actual ==== expected
    }

    def testCanRecover_IO_recoverFromNonFatalEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected = expectedFailure.asLeft[Int]
      val actual = CanRecover[Task].recoverFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }.runSyncUnsafe()

      actual ==== expected
    }


    def testCanRecover_IO_recoverEitherTFromNonFatalShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult =
        CanRecover[Task].recoverEitherTFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }.value.runSyncUnsafe()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult =
        CanRecover[Task].recoverEitherTFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }.value.runSyncUnsafe()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_IO_recoverEitherTFromNonFatalShouldNotCatchFatal: Result = {

      val expectedExpcetion = new SomeControlThrowable("Something's wrong")
      val fa = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))

      val task =
        CanRecover[Task].recoverEitherTFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
      try {
        val actual = task.value.runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnSuccessfulResult: Result = {

      val fa = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual =
        CanRecover[Task].recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }.value.runSyncUnsafe()

      actual ==== expected
    }

    def testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected = expectedFailure.asLeft[Int]
      val actual =
        CanRecover[Task].recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }.value.runSyncUnsafe()

      actual ==== expected
    }

  }


  object FutureSpec {
    import java.util.concurrent.{ExecutorService, Executors, TimeoutException}

    import scala.concurrent.duration._
    import scala.concurrent.{Await, ExecutionContext, Future}
    import scala.util.control.NonFatal

    @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter", "org.wartremover.warts.Throw"))
    def futureToValue[A](fa: Future[A])(implicit executorService: ExecutorService, waitFor: FiniteDuration): A =
      try {
        Await.result(fa, waitFor)
      } catch {
        case ex: TimeoutException =>
          @SuppressWarnings(Array("org.wartremover.warts.ToString"))
          val message = ex.toString
          println(s"ex: $message")
          throw ex
      }

    @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter", "org.wartremover.warts.Throw"))
    def futureToValueAndTerminate[A](fa: Future[A])(implicit executorService: ExecutorService, waitFor: FiniteDuration): A =
      try {
        futureToValue(fa)
      } finally {
        try {
          ExecutorServiceOps.shutdownAndAwaitTerminationWithLogger(executorService, waitFor)(println(_))
        } catch {
          case NonFatal(ex) =>
            @SuppressWarnings(Array("org.wartremover.warts.ToString"))
            val message = ex.toString
            println(s"NonFatal: $message")
        }
      }


    def executionContextExecutor(executorService: ExecutorService): ExecutionContext =
      ExecutionContext.fromExecutor(executorService)


    implicit val waitFor: FiniteDuration = 1.second

    def testCanRecover_Future_recoverFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Int](throwThrowable[Int](expectedExpcetion))
      val expected = 1
      val actual = futureToValueAndTerminate[Int](CanRecover[Future].recoverFromNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) => Future(expected)
        })

      actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val fa = run[Future, Int](1)
      val expected = 1
      val actual = futureToValueAndTerminate(CanRecover[Future].recoverFromNonFatalWith(fa) {
          case NonFatal(_) => Future(123)
        })

      actual ==== expected
    }


    def testCanRecover_Future_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult =
        futureToValue(CanRecover[Future].recoverFromNonFatalWith(fa) {
            case err => Future(SomeError.someThrowable(err).asLeft[Int])
          })

      val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual =
        futureToValueAndTerminate(CanRecover[Future].recoverFromNonFatalWith(fa2) {
          case NonFatal(`expectedExpcetion`) => Future(expected)
        })

      expectedFailedResult ==== actualFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val fa = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual =
        futureToValueAndTerminate(CanRecover[Future].recoverFromNonFatalWith(fa) {
          case err => Future(SomeError.someThrowable(err).asLeft[Int])
        })

      actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalWithEitherShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val expectedFailure = SomeError.message("Failed")
      val fa = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected = expectedFailure.asLeft[Int]
      val actual =
        futureToValueAndTerminate(CanRecover[Future].recoverFromNonFatalWith(fa) {
          case NonFatal(_) => Future(1.asRight[SomeError])
        })

      actual ==== expected
    }


    def testCanRecover_Future_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult = futureToValue(
        CanRecover[Future].recoverEitherTFromNonFatalWith(fa) {
          case err => Future(SomeError.someThrowable(err).asLeft[Int])
        }.value
      )

      val fa2 = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected = 1.asRight[SomeError]
      val actual =
        futureToValueAndTerminate(
          CanRecover[Future].recoverEitherTFromNonFatalWith(fa2) {
            case err => Future(expected)
          }.value
        )

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val fa = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual = futureToValueAndTerminate(
        CanRecover[Future].recoverEitherTFromNonFatalWith(fa) {
            case err => Future(SomeError.someThrowable(err).asLeft[Int])
          }.value
        )

      actual ==== expected
    }

    def testCanRecover_Future_recoverEitherTFromNonFatalWithShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val expectedFailure = SomeError.message("Failed")
      val fa = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected = expectedFailure.asLeft[Int]
      val actual =
        futureToValueAndTerminate(CanRecover[Future].recoverEitherTFromNonFatalWith(fa) {
          case NonFatal(_) => Future(expected)
        }.value)

      actual ==== expected
    }

    ///

    def testCanRecover_Future_recoverFromNonFatalShouldRecoverFromNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Int](throwThrowable[Int](expectedExpcetion))
      val expected = 1
      val actual = futureToValueAndTerminate[Int](CanRecover[Future].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expected })

      actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val fa = run[Future, Int](1)
      val expected = 1
      val actual = futureToValueAndTerminate(CanRecover[Future].recoverFromNonFatal(fa) { case NonFatal(_) => 123 })

      actual ==== expected
    }


    def testCanRecover_Future_recoverFromNonFatalEitherShouldRecoverFromNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult =
        futureToValue(CanRecover[Future].recoverFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        })

      val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual = futureToValueAndTerminate(CanRecover[Future].recoverFromNonFatal(fa2) { case NonFatal(`expectedExpcetion`) => expected })

      expectedFailedResult ==== actualFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalEitherShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val fa = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual =
        futureToValueAndTerminate(CanRecover[Future].recoverFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        })

      actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalEitherShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val expectedFailure = SomeError.message("Failed")
      val fa = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected = expectedFailure.asLeft[Int]
      val actual = futureToValueAndTerminate(CanRecover[Future].recoverFromNonFatal(fa) { case NonFatal(_) => 1.asRight[SomeError] })

      actual ==== expected
    }


    def testCanRecover_Future_recoverEitherTFromNonFatalShouldRecoverFromNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult = futureToValue(
          CanRecover[Future].recoverEitherTFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }.value
        )

      val fa2 = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected = 1.asRight[SomeError]
      val actual =
        futureToValueAndTerminate(CanRecover[Future].recoverEitherTFromNonFatal(fa2) { case err => expected }.value)

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverEitherTFromNonFatalShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val fa = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual = futureToValueAndTerminate(
          CanRecover[Future].recoverEitherTFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }.value
        )

      actual ==== expected
    }

    def testCanRecover_Future_recoverEitherTFromNonFatalShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val expectedFailure = SomeError.message("Failed")
      val fa = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected = expectedFailure.asLeft[Int]
      val actual =
        futureToValueAndTerminate(CanRecover[Future].recoverEitherTFromNonFatal(fa) { case NonFatal(_) => expected }.value)

      actual ==== expected
    }
  }


  object IdSpec {

    def testCanRecover_Id_recoverFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa = run[Id, Int](throwThrowable[Int](expectedExpcetion))
      val expected = 1
      val actual: Id[Int] = CanRecover[Id].recoverFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => expected }

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_Id_recoverFromNonFatalWithShouldNotCatchFatal: Result = {

      val expectedExpcetion = new SomeControlThrowable("Something's wrong")
      lazy val fa = run[Id, Int](throwThrowable[Int](expectedExpcetion))

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

      val fa = run[Id, Int](1)
      val expected = 1
      val actual: Id[Int] = CanRecover[Id].recoverFromNonFatalWith(fa) { case NonFatal(_) => 123 }

      actual ==== expected
    }


    def testCanRecover_Id_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult = CanRecover[Id].recoverFromNonFatalWith(fa) {
        case err => SomeError.someThrowable(err).asLeft[Int]
      }

      val expected = 1.asRight[SomeError]
      val actual = CanRecover[Id].recoverFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => expected }

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_Id_recoverFromNonFatalWithEitherShouldNotCatchFatal: Result = {

      val expectedExpcetion = new SomeControlThrowable("Something's wrong")
      lazy val fa = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

      try {
        val actual = CanRecover[Id].recoverFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Id_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

      val fa = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual =
        CanRecover[Id].recoverFromNonFatalWith(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }

      actual ==== expected
    }

    def testCanRecover_Id_recoverFromNonFatalWithEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected = expectedFailure.asLeft[Int]
      val actual = CanRecover[Id].recoverFromNonFatalWith(fa) { case NonFatal(_) => 1.asRight[SomeError] }

      actual ==== expected
    }


    def testCanRecover_Id_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult =
        CanRecover[Id].recoverEitherTFromNonFatalWith(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }.value

      val expected = 1.asRight[SomeError]
      val actual = CanRecover[Id].recoverEitherTFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => expected }.value

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_Id_recoverEitherTFromNonFatalWithShouldNotCatchFatal: Result = {

      val expectedExpcetion = new SomeControlThrowable("Something's wrong")
      lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))

      try {
        val actual = CanRecover[Id].recoverEitherTFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }.value
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Id_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa = EitherT(run[Id, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual = CanRecover[Id].recoverEitherTFromNonFatalWith(fa) {
        case err => SomeError.someThrowable(err).asLeft[Int]
      }.value

      actual ==== expected
    }

    def testCanRecover_Id_recoverEitherTFromNonFatalWithShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa = EitherT(run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected = expectedFailure.asLeft[Int]
      val actual =
        CanRecover[Id].recoverEitherTFromNonFatalWith(fa) { case NonFatal(_) => 1.asRight[SomeError] }.value

      actual ==== expected
    }

    ///

    def testCanRecover_Id_recoverFromNonFatalShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa = run[Id, Int](throwThrowable[Int](expectedExpcetion))
      val expected = 1
      val actual: Id[Int] = CanRecover[Id].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expected }

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_Id_recoverFromNonFatalShouldNotCatchFatal: Result = {

      val expectedExpcetion = new SomeControlThrowable("Something's wrong")
      lazy val fa = run[Id, Int](throwThrowable[Int](expectedExpcetion))

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

      val fa = run[Id, Int](1)
      val expected = 1
      val actual: Id[Int] = CanRecover[Id].recoverFromNonFatal(fa) { case NonFatal(_) => 123 }

      actual ==== expected
    }


    def testCanRecover_Id_recoverFromNonFatalEitherShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult =
        CanRecover[Id].recoverFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }

      val expected = 1.asRight[SomeError]
      val actual = CanRecover[Id].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expected }

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_Id_recoverFromNonFatalEitherShouldNotCatchFatal: Result = {

      val expectedExpcetion = new SomeControlThrowable("Something's wrong")
      lazy val fa = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

      try {
        val actual = CanRecover[Id].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Id_recoverFromNonFatalEitherShouldReturnSuccessfulResult: Result = {

      val fa = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual =
        CanRecover[Id].recoverFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }

      actual ==== expected
    }

    def testCanRecover_Id_recoverFromNonFatalEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected = expectedFailure.asLeft[Int]
      val actual = CanRecover[Id].recoverFromNonFatal(fa) { case NonFatal(_) => 1.asRight[SomeError] }

      actual ==== expected
    }


    def testCanRecover_Id_recoverEitherTFromNonFatalShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult =
        CanRecover[Id].recoverEitherTFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }.value

      val expected = 1.asRight[SomeError]
      val actual = CanRecover[Id].recoverEitherTFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expected }.value

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_Id_recoverEitherTFromNonFatalShouldNotCatchFatal: Result = {

      val expectedExpcetion = new SomeControlThrowable("Something's wrong")
      lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))

      try {
        val actual = CanRecover[Id].recoverEitherTFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }.value
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_Id_recoverEitherTFromNonFatalShouldReturnSuccessfulResult: Result = {

      val fa = EitherT(run[Id, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual = CanRecover[Id].recoverEitherTFromNonFatal(fa) {
        case err => SomeError.someThrowable(err).asLeft[Int]
      }.value

      actual ==== expected
    }

    def testCanRecover_Id_recoverEitherTFromNonFatalShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa = EitherT(run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected = expectedFailure.asLeft[Int]
      val actual = CanRecover[Id].recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 1.asRight[SomeError] }.value

      actual ==== expected
    }

  }

}
