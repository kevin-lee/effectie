package effectie.scalaz

import scalaz._
import Scalaz._

import effectie.scalaz.Effectful._
import effectie.{ConcurrentSupport, SomeControlThrowable}

import hedgehog._
import hedgehog.runner._

import scalaz.effect._

import scala.util.control.{ControlThrowable, NonFatal}

/** @author Kevin Lee
  * @since 2020-08-17
  */
object CanHandleErrorSpec extends Properties {

  override def tests: List[Test] = List(
    /* IO */
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
    /* Future */

    example(
      "test CanHandleError[Future].handleNonFatalWith should handle NonFatal",
      FutureSpec.testCanHandleError_Future_handleNonFatalWithShouldHandleNonFatalWith
    ),
    example(
      "test CanHandleError[Future].handleNonFatalWith should return the successful result",
      FutureSpec.testCanHandleError_Future_handleNonFatalWithShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[Future].handleNonFatalWithEither should handle NonFatal",
      FutureSpec.testCanHandleError_Future_handleNonFatalWithEitherShouldHandleNonFatalWith
    ),
    example(
      "test CanHandleError[Future].handleNonFatalWithEither should return the successful result",
      FutureSpec.testCanHandleError_Future_handleNonFatalWithEitherShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[Future].handleNonFatalWithEither should return the failed result",
      FutureSpec.testCanHandleError_Future_handleNonFatalWithEitherShouldReturnFailedResult
    ),
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
      "test CanHandleError[Future].handleNonFatal should handle NonFatal",
      FutureSpec.testCanHandleError_Future_handleNonFatalShouldHandleNonFatal
    ),
    example(
      "test CanHandleError[Future].handleNonFatal should return the successful result",
      FutureSpec.testCanHandleError_Future_handleNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[Future].handleNonFatalEither should handle NonFatal",
      FutureSpec.testCanHandleError_Future_handleNonFatalEitherShouldHandleNonFatal
    ),
    example(
      "test CanHandleError[Future].handleNonFatalEither should return the successful result",
      FutureSpec.testCanHandleError_Future_handleNonFatalEitherShouldReturnSuccessfulResult
    ),
    example(
      "test CanHandleError[Future].handleNonFatalEither should return the failed result",
      FutureSpec.testCanHandleError_Future_handleNonFatalEitherShouldReturnFailedResult
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
    /* Id */
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
    throw throwable

  def run[F[_]: EffectConstructor: Functor, A](a: => A): F[A] =
    effectOf[F](a)

  sealed trait SomeError
  object SomeError {

    final case class SomeThrowable(throwable: Throwable) extends SomeError
    final case class Message(message: String)            extends SomeError

    def someThrowable(throwable: Throwable): SomeError = SomeThrowable(throwable)

    def message(message: String): SomeError = Message(message)

  }

  object IoSpec {

    def testCanHandleError_IO_handleNonFatalWithShouldHandleNonFatalWith: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 123
      val actual            = CanHandleError[IO]
        .handleNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) =>
            IO(expected)
        }
        .unsafePerformIO()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_IO_handleNonFatalWithShouldNotHandleFatalWith: Result = {

      val fatalExpcetion = new SomeControlThrowable("Something's wrong")
      val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))

      try {
        val actual = CanHandleError[IO].handleNonFatalWith(fa)(_ => IO(123)).unsafePerformIO()
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
      val actual   = CanHandleError[IO].handleNonFatalWith(fa)(_ => IO(999)).unsafePerformIO()

      actual ==== expected
    }

    def testCanHandleError_IO_handleNonFatalWithEitherShouldHandleNonFatalWith: Result = {

      val expectedExpcetion    = new RuntimeException("Something's wrong")
      val fa                   = run[IO, SomeError \/ Int](throwThrowable[SomeError \/ Int](expectedExpcetion))
      val expectedFailedResult = SomeError.message("Recovered Error").left[Int]
      val actualFailedResult   =
        CanHandleError[IO].handleNonFatalWith(fa)(_ => IO(expectedFailedResult)).unsafePerformIO()

      val expectedSuccessResult = 1.right[SomeError]
      val actualSuccessResult   = CanHandleError[IO].handleNonFatalWith(fa)(_ => IO(1.right[SomeError])).unsafePerformIO()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_IO_handleNonFatalWithEitherShouldNotHandleFatalWith: Result = {

      val fatalExpcetion = new SomeControlThrowable("Something's wrong")
      val fa             = run[IO, SomeError \/ Int](throwThrowable[SomeError \/ Int](fatalExpcetion))

      try {
        val actual = CanHandleError[IO].handleNonFatalWith(fa)(_ => IO(123.right[SomeError])).unsafePerformIO()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_IO_handleNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[IO, SomeError \/ Int](1.right[SomeError])
      val expected = 1.right[SomeError]
      val actual   = CanHandleError[IO].handleNonFatalWith(fa)(_ => IO(999.right[SomeError])).unsafePerformIO()

      actual ==== expected
    }

    def testCanHandleError_IO_handleNonFatalWithEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[IO, SomeError \/ Int](expectedFailure.left[Int])
      val expected        = expectedFailure.left[Int]
      val actual          = CanHandleError[IO].handleNonFatalWith(fa)(_ => IO(123.right[SomeError])).unsafePerformIO()

      actual ==== expected
    }

    def testCanHandleError_IO_handleEitherTNonFatalWithShouldHandleNonFatalWith: Result = {

      val expectedExpcetion     = new RuntimeException("Something's wrong")
      val fa                    = EitherT(run[IO, SomeError \/ Int](throwThrowable[SomeError \/ Int](expectedExpcetion)))
      val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).left[Int]
      val actualFailedResult    = CanHandleError[IO]
        .handleEitherTNonFatalWith(fa)(err => IO(SomeError.someThrowable(err).left[Int]))
        .run
        .unsafePerformIO()
      val expectedSuccessResult = 123.right[SomeError]
      val actualSuccessResult   =
        CanHandleError[IO].handleEitherTNonFatalWith(fa)(_ => IO(123.right[SomeError])).run.unsafePerformIO()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_IO_handleEitherTNonFatalWithShouldNotHandleFatalWith: Result = {

      val fatalExpcetion = new SomeControlThrowable("Something's wrong")
      val fa             = EitherT(run[IO, SomeError \/ Int](throwThrowable[SomeError \/ Int](fatalExpcetion)))

      try {
        val actual =
          CanHandleError[IO]
            .handleEitherTNonFatalWith(fa)(err => IO(SomeError.someThrowable(err).left[Int]))
            .run
            .unsafePerformIO()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_IO_handleEitherTNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = EitherT(run[IO, SomeError \/ Int](1.right[SomeError]))
      val expected = 1.right[SomeError]
      val actual   = CanHandleError[IO].handleEitherTNonFatalWith(fa)(_ => IO(123.right[SomeError])).run.unsafePerformIO()

      actual ==== expected
    }

    def testCanHandleError_IO_handleEitherTNonFatalWithShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[IO, SomeError \/ Int](expectedFailure.left[Int]))
      val expected        = expectedFailure.left[Int]
      val actual          =
        CanHandleError[IO].handleEitherTNonFatalWith(fa)(_ => IO(123.right[SomeError])).run.unsafePerformIO()

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
        .unsafePerformIO()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_IO_handleNonFatalShouldNotHandleFatal: Result = {

      val fatalExpcetion = new SomeControlThrowable("Something's wrong")
      val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))

      try {
        val actual = CanHandleError[IO].handleNonFatal(fa)(_ => 123).unsafePerformIO()
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
      val actual   = CanHandleError[IO].handleNonFatal(fa)(_ => 999).unsafePerformIO()

      actual ==== expected
    }

    def testCanHandleError_IO_handleNonFatalEitherShouldHandleNonFatal: Result = {

      val expectedExpcetion    = new RuntimeException("Something's wrong")
      val fa                   = run[IO, SomeError \/ Int](throwThrowable[SomeError \/ Int](expectedExpcetion))
      val expectedFailedResult = SomeError.message("Recovered Error").left[Int]
      val actualFailedResult   = CanHandleError[IO].handleNonFatal(fa)(_ => expectedFailedResult).unsafePerformIO()

      val expectedSuccessResult = 1.right[SomeError]
      val actualSuccessResult   = CanHandleError[IO].handleNonFatal(fa)(_ => 1.right[SomeError]).unsafePerformIO()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_IO_handleNonFatalEitherShouldNotHandleFatal: Result = {

      val fatalExpcetion = new SomeControlThrowable("Something's wrong")
      val fa             = run[IO, SomeError \/ Int](throwThrowable[SomeError \/ Int](fatalExpcetion))

      try {
        val actual = CanHandleError[IO].handleNonFatal(fa)(_ => 123.right[SomeError]).unsafePerformIO()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_IO_handleNonFatalEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[IO, SomeError \/ Int](1.right[SomeError])
      val expected = 1.right[SomeError]
      val actual   = CanHandleError[IO].handleNonFatal(fa)(_ => 999.right[SomeError]).unsafePerformIO()

      actual ==== expected
    }

    def testCanHandleError_IO_handleNonFatalEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[IO, SomeError \/ Int](expectedFailure.left[Int])
      val expected        = expectedFailure.left[Int]
      val actual          = CanHandleError[IO].handleNonFatal(fa)(_ => 123.right[SomeError]).unsafePerformIO()

      actual ==== expected
    }

    def testCanHandleError_IO_handleEitherTNonFatalShouldHandleNonFatal: Result = {

      val expectedExpcetion     = new RuntimeException("Something's wrong")
      val fa                    = EitherT(run[IO, SomeError \/ Int](throwThrowable[SomeError \/ Int](expectedExpcetion)))
      val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).left[Int]
      val actualFailedResult    = CanHandleError[IO]
        .handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).left[Int])
        .run
        .unsafePerformIO()
      val expectedSuccessResult = 123.right[SomeError]
      val actualSuccessResult   =
        CanHandleError[IO].handleEitherTNonFatal(fa)(_ => 123.right[SomeError]).run.unsafePerformIO()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_IO_handleEitherTNonFatalShouldNotHandleFatal: Result = {

      val fatalExpcetion = new SomeControlThrowable("Something's wrong")
      val fa             = EitherT(run[IO, SomeError \/ Int](throwThrowable[SomeError \/ Int](fatalExpcetion)))

      try {
        val actual =
          CanHandleError[IO]
            .handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).left[Int])
            .run
            .unsafePerformIO()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_IO_handleEitherTNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = EitherT(run[IO, SomeError \/ Int](1.right[SomeError]))
      val expected = 1.right[SomeError]
      val actual   = CanHandleError[IO].handleEitherTNonFatal(fa)(_ => 123.right[SomeError]).run.unsafePerformIO()

      actual ==== expected
    }

    def testCanHandleError_IO_handleEitherTNonFatalShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[IO, SomeError \/ Int](expectedFailure.left[Int]))
      val expected        = expectedFailure.left[Int]
      val actual          =
        CanHandleError[IO].handleEitherTNonFatal(fa)(_ => 123.right[SomeError]).run.unsafePerformIO()

      actual ==== expected
    }

  }

  object FutureSpec {
    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration._
    import scala.concurrent.{ExecutionContext, Future}

    val waitFor: FiniteDuration = 1.second

    def testCanHandleError_Future_handleNonFatalWithShouldHandleNonFatalWith: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 1
      val actual            = ConcurrentSupport.futureToValueAndTerminate[Int](
        CanHandleError[Future].handleNonFatalWith(fa)(_ => Future(expected)),
        waitFor
      )

      actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalWithShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val fa       = run[Future, Int](1)
      val expected = 1
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        CanHandleError[Future].handleNonFatalWith(fa)(_ => Future(123)),
        waitFor
      )

      actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalWithEitherShouldHandleNonFatalWith: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val expectedExpcetion    = new RuntimeException("Something's wrong")
      val fa                   = run[Future, SomeError \/ Int](throwThrowable[SomeError \/ Int](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).left[Int]
      val actualFailedResult   =
        ConcurrentSupport.futureToValue(
          CanHandleError[Future].handleNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).left[Int])),
          waitFor
        )

      val fa2      = run[Future, SomeError \/ Int](throwThrowable[SomeError \/ Int](expectedExpcetion))
      val expected = 1.right[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        CanHandleError[Future].handleNonFatalWith(fa2)(_ => Future(expected)),
        waitFor
      )

      expectedFailedResult ==== actualFailedResult and actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val fa       = run[Future, SomeError \/ Int](1.right[SomeError])
      val expected = 1.right[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          CanHandleError[Future].handleNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).left[Int])),
          waitFor
        )

      actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalWithEitherShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Future, SomeError \/ Int](expectedFailure.left[Int])
      val expected        = expectedFailure.left[Int]
      val actual          = ConcurrentSupport.futureToValueAndTerminate(
        CanHandleError[Future].handleNonFatalWith(fa)(_ => Future(1.right[SomeError])),
        waitFor
      )

      actual ==== expected
    }

    def testCanHandleError_Future_handleEitherTNonFatalWithShouldHandleNonFatalWith: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val expectedExpcetion    = new RuntimeException("Something's wrong")
      val fa                   = EitherT(run[Future, SomeError \/ Int](throwThrowable[SomeError \/ Int](expectedExpcetion)))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).left[Int]
      val actualFailedResult   = ConcurrentSupport.futureToValue(
        CanHandleError[Future].handleEitherTNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).left[Int])).run,
        waitFor
      )

      val fa2      = EitherT(run[Future, SomeError \/ Int](throwThrowable[SomeError \/ Int](expectedExpcetion)))
      val expected = 1.right[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          CanHandleError[Future].handleEitherTNonFatalWith(fa2)(err => Future(expected)).run,
          waitFor
        )

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanHandleError_Future_handleEitherTNonFatalWithShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val fa       = EitherT(run[Future, SomeError \/ Int](1.right[SomeError]))
      val expected = 1.right[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        CanHandleError[Future].handleEitherTNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).left[Int])).run,
        waitFor
      )

      actual ==== expected
    }

    def testCanHandleError_Future_handleEitherTNonFatalWithShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Future, SomeError \/ Int](expectedFailure.left[Int]))
      val expected        = expectedFailure.left[Int]
      val actual          =
        ConcurrentSupport.futureToValueAndTerminate(
          CanHandleError[Future].handleEitherTNonFatalWith(fa)(_ => Future(expected)).run,
          waitFor
        )

      actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalShouldHandleNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 1
      val actual            = ConcurrentSupport.futureToValueAndTerminate[Int](
        CanHandleError[Future].handleNonFatal(fa)(_ => expected),
        waitFor
      )

      actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val fa       = run[Future, Int](1)
      val expected = 1
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        CanHandleError[Future].handleNonFatal(fa)(_ => 123),
        waitFor
      )

      actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalEitherShouldHandleNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val expectedExpcetion    = new RuntimeException("Something's wrong")
      val fa                   = run[Future, SomeError \/ Int](throwThrowable[SomeError \/ Int](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).left[Int]
      val actualFailedResult   =
        ConcurrentSupport.futureToValue(
          CanHandleError[Future].handleNonFatal(fa)(err => SomeError.someThrowable(err).left[Int]),
          waitFor
        )

      val fa2      = run[Future, SomeError \/ Int](throwThrowable[SomeError \/ Int](expectedExpcetion))
      val expected = 1.right[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        CanHandleError[Future].handleNonFatal(fa2)(_ => expected),
        waitFor
      )

      expectedFailedResult ==== actualFailedResult and actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalEitherShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val fa       = run[Future, SomeError \/ Int](1.right[SomeError])
      val expected = 1.right[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          CanHandleError[Future].handleNonFatal(fa)(err => SomeError.someThrowable(err).left[Int]),
          waitFor
        )

      actual ==== expected
    }

    def testCanHandleError_Future_handleNonFatalEitherShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Future, SomeError \/ Int](expectedFailure.left[Int])
      val expected        = expectedFailure.left[Int]
      val actual          = ConcurrentSupport.futureToValueAndTerminate(
        CanHandleError[Future].handleNonFatal(fa)(_ => 1.right[SomeError]),
        waitFor
      )

      actual ==== expected
    }

    def testCanHandleError_Future_handleEitherTNonFatalShouldHandleNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val expectedExpcetion    = new RuntimeException("Something's wrong")
      val fa                   = EitherT(run[Future, SomeError \/ Int](throwThrowable[SomeError \/ Int](expectedExpcetion)))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).left[Int]
      val actualFailedResult   = ConcurrentSupport.futureToValue(
        CanHandleError[Future].handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).left[Int]).run,
        waitFor
      )

      val fa2      = EitherT(run[Future, SomeError \/ Int](throwThrowable[SomeError \/ Int](expectedExpcetion)))
      val expected = 1.right[SomeError]
      val actual   =
        ConcurrentSupport.futureToValueAndTerminate(
          CanHandleError[Future].handleEitherTNonFatal(fa2)(err => expected).run,
          waitFor
        )

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanHandleError_Future_handleEitherTNonFatalShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val fa       = EitherT(run[Future, SomeError \/ Int](1.right[SomeError]))
      val expected = 1.right[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        CanHandleError[Future].handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).left[Int]).run,
        waitFor
      )

      actual ==== expected
    }

    def testCanHandleError_Future_handleEitherTNonFatalShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Future, SomeError \/ Int](expectedFailure.left[Int]))
      val expected        = expectedFailure.left[Int]
      val actual          =
        ConcurrentSupport.futureToValueAndTerminate(
          CanHandleError[Future].handleEitherTNonFatal(fa)(_ => expected).run,
          waitFor
        )

      actual ==== expected
    }

  }

  object IdSpec {

    def testCanHandleError_Id_handleNonFatalWithShouldHandleNonFatalWith: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 1
      val actual: Id[Int]   = CanHandleError[Id].handleNonFatalWith(fa)(_ => expected)

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Id_handleNonFatalWithShouldNotHandleFatalWith: Result = {

      val fatalExpcetion = new SomeControlThrowable("Something's wrong")
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

      val expectedExpcetion    = new RuntimeException("Something's wrong")
      lazy val fa              = run[Id, SomeError \/ Int](throwThrowable[SomeError \/ Int](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).left[Int]
      val actualFailedResult   = CanHandleError[Id].handleNonFatalWith(fa)(err => SomeError.someThrowable(err).left[Int])

      lazy val fa2 = run[Id, SomeError \/ Int](throwThrowable[SomeError \/ Int](expectedExpcetion))
      val expected = 1.right[SomeError]
      val actual   = CanHandleError[Id].handleNonFatalWith(fa2)(_ => expected)

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Id_handleNonFatalWithEitherShouldNotHandleFatalWith: Result = {

      val fatalExpcetion = new SomeControlThrowable("Something's wrong")
      lazy val fa        = run[Id, SomeError \/ Int](throwThrowable[SomeError \/ Int](fatalExpcetion))

      try {
        val actual = CanHandleError[Id].handleNonFatalWith(fa)(_ => 1.right[SomeError])
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Id_handleNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[Id, SomeError \/ Int](1.right[SomeError])
      val expected = 1.right[SomeError]
      val actual   = CanHandleError[Id].handleNonFatalWith(fa)(err => SomeError.someThrowable(err).left[Int])

      actual ==== expected
    }

    def testCanHandleError_Id_handleNonFatalWithEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Id, SomeError \/ Int](expectedFailure.left[Int])
      val expected        = expectedFailure.left[Int]
      val actual          = CanHandleError[Id].handleNonFatalWith(fa)(_ => 1.right[SomeError])

      actual ==== expected
    }

    def testCanHandleError_Id_handleEitherTNonFatalWithShouldHandleNonFatalWith: Result = {

      val expectedExpcetion    = new RuntimeException("Something's wrong")
      lazy val fa              = EitherT(run[Id, SomeError \/ Int](throwThrowable[SomeError \/ Int](expectedExpcetion)))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).left[Int]
      val actualFailedResult   =
        CanHandleError[Id].handleEitherTNonFatalWith(fa)(err => SomeError.someThrowable(err).left[Int]).run

      lazy val fa2 = EitherT(run[Id, SomeError \/ Int](throwThrowable[SomeError \/ Int](expectedExpcetion)))
      val expected = 1.right[SomeError]
      val actual   = CanHandleError[Id].handleEitherTNonFatalWith(fa2)(_ => expected).run

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Id_handleEitherTNonFatalWithShouldNotHandleFatalWith: Result = {

      val fatalExpcetion = new SomeControlThrowable("Something's wrong")
      lazy val fa        = EitherT(run[Id, SomeError \/ Int](throwThrowable[SomeError \/ Int](fatalExpcetion)))

      try {
        val actual = CanHandleError[Id].handleEitherTNonFatalWith(fa)(_ => 1.right[SomeError]).run
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Id_handleEitherTNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = EitherT(run[Id, SomeError \/ Int](1.right[SomeError]))
      val expected = 1.right[SomeError]
      val actual   = CanHandleError[Id].handleEitherTNonFatalWith(fa)(err => SomeError.someThrowable(err).left[Int]).run

      actual ==== expected
    }

    def testCanHandleError_Id_handleEitherTNonFatalWithShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Id, SomeError \/ Int](expectedFailure.left[Int]))
      val expected        = expectedFailure.left[Int]
      val actual          = CanHandleError[Id].handleEitherTNonFatalWith(fa)(_ => 1.right[SomeError]).run

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

      val fatalExpcetion = new SomeControlThrowable("Something's wrong")
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

      val expectedExpcetion    = new RuntimeException("Something's wrong")
      lazy val fa              = run[Id, SomeError \/ Int](throwThrowable[SomeError \/ Int](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).left[Int]
      val actualFailedResult   = CanHandleError[Id].handleNonFatal(fa)(err => SomeError.someThrowable(err).left[Int])

      lazy val fa2 = run[Id, SomeError \/ Int](throwThrowable[SomeError \/ Int](expectedExpcetion))
      val expected = 1.right[SomeError]
      val actual   = CanHandleError[Id].handleNonFatal(fa2)(_ => expected)

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Id_handleNonFatalEitherShouldNotHandleFatal: Result = {

      val fatalExpcetion = new SomeControlThrowable("Something's wrong")
      lazy val fa        = run[Id, SomeError \/ Int](throwThrowable[SomeError \/ Int](fatalExpcetion))

      try {
        val actual = CanHandleError[Id].handleNonFatal(fa)(_ => 1.right[SomeError])
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Id_handleNonFatalEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[Id, SomeError \/ Int](1.right[SomeError])
      val expected = 1.right[SomeError]
      val actual   = CanHandleError[Id].handleNonFatal(fa)(err => SomeError.someThrowable(err).left[Int])

      actual ==== expected
    }

    def testCanHandleError_Id_handleNonFatalEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Id, SomeError \/ Int](expectedFailure.left[Int])
      val expected        = expectedFailure.left[Int]
      val actual          = CanHandleError[Id].handleNonFatal(fa)(_ => 1.right[SomeError])

      actual ==== expected
    }

    def testCanHandleError_Id_handleEitherTNonFatalShouldHandleNonFatal: Result = {

      val expectedExpcetion    = new RuntimeException("Something's wrong")
      lazy val fa              = EitherT(run[Id, SomeError \/ Int](throwThrowable[SomeError \/ Int](expectedExpcetion)))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).left[Int]
      val actualFailedResult   =
        CanHandleError[Id].handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).left[Int]).run

      lazy val fa2 = EitherT(run[Id, SomeError \/ Int](throwThrowable[SomeError \/ Int](expectedExpcetion)))
      val expected = 1.right[SomeError]
      val actual   = CanHandleError[Id].handleEitherTNonFatal(fa2)(_ => expected).run

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanHandleError_Id_handleEitherTNonFatalShouldNotHandleFatal: Result = {

      val fatalExpcetion = new SomeControlThrowable("Something's wrong")
      lazy val fa        = EitherT(run[Id, SomeError \/ Int](throwThrowable[SomeError \/ Int](fatalExpcetion)))

      try {
        val actual = CanHandleError[Id].handleEitherTNonFatal(fa)(_ => 1.right[SomeError]).run
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanHandleError_Id_handleEitherTNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = EitherT(run[Id, SomeError \/ Int](1.right[SomeError]))
      val expected = 1.right[SomeError]
      val actual   = CanHandleError[Id].handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).left[Int]).run

      actual ==== expected
    }

    def testCanHandleError_Id_handleEitherTNonFatalShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Id, SomeError \/ Int](expectedFailure.left[Int]))
      val expected        = expectedFailure.left[Int]
      val actual          = CanHandleError[Id].handleEitherTNonFatal(fa)(_ => 1.right[SomeError]).run

      actual ==== expected
    }

  }

}
