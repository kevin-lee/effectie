package effectie.cats

import cats._
import cats.data.EitherT
import cats.effect.IO
import cats.instances.all._
import cats.syntax.all._

import effectie.cats.Effectful._
import effectie.{ConcurrentSupport, SomeControlThrowable}

import hedgehog._
import hedgehog.runner._

import scala.util.control.{ControlThrowable, NonFatal}

/**
 * @author Kevin Lee
 * @since 2020-08-17
 */
object CanRecoverSpec extends Properties {

  override def tests: List[Test] = List(
    /* IO */
    example("test CanRecover[IO].recoverFromNonFatalWith should catch NonFatal", IOSpec.testCanRecover_IO_recoverFromNonFatalWithShouldRecoverFromNonFatal),
    example("test CanRecover[IO].recoverFromNonFatalWith should not catch Fatal", IOSpec.testCanRecover_IO_recoverFromNonFatalWithShouldNotCatchFatal),
    example("test CanRecover[IO].recoverFromNonFatalWith should return the successful result", IOSpec.testCanRecover_IO_recoverFromNonFatalWithShouldReturnSuccessfulResult),

    example("test CanRecover[IO].recoverFromNonFatalWithEither should catch NonFatal", IOSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal),
    example("test CanRecover[IO].recoverFromNonFatalWithEither should not catch Fatal", IOSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldNotCatchFatal),
    example("test CanRecover[IO].recoverFromNonFatalWithEither should return the successful result", IOSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult),
    example("test CanRecover[IO].recoverFromNonFatalWithEither should return the failed result", IOSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnFailedResult),

    example("test CanRecover[IO].recoverEitherTFromNonFatalWith should catch NonFatal", IOSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal),
    example("test CanRecover[IO].recoverEitherTFromNonFatalWith should not catch Fatal", IOSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldNotCatchFatal),
    example("test CanRecover[IO].recoverEitherTFromNonFatalWith should return the successful result", IOSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult),
    example("test CanRecover[IO].recoverEitherTFromNonFatalWith should return the failed result", IOSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnFailedResult),


    example("test CanRecover[IO].recoverFromNonFatal should catch NonFatal", IOSpec.testCanRecover_IO_recoverFromNonFatalShouldRecoverFromNonFatal),
    example("test CanRecover[IO].recoverFromNonFatal should not catch Fatal", IOSpec.testCanRecover_IO_recoverFromNonFatalShouldNotCatchFatal),
    example("test CanRecover[IO].recoverFromNonFatal should return the successful result", IOSpec.testCanRecover_IO_recoverFromNonFatalShouldReturnSuccessfulResult),

    example("test CanRecover[IO].recoverFromNonFatalEither should catch NonFatal", IOSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldRecoverFromNonFatal),
    example("test CanRecover[IO].recoverFromNonFatalEither should not catch Fatal", IOSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldNotCatchFatal),
    example("test CanRecover[IO].recoverFromNonFatalEither should return the successful result", IOSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldReturnSuccessfulResult),
    example("test CanRecover[IO].recoverFromNonFatalEither should return the failed result", IOSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldReturnFailedResult),

    example("test CanRecover[IO].recoverEitherTFromNonFatal should catch NonFatal", IOSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldRecoverFromNonFatal),
    example("test CanRecover[IO].recoverEitherTFromNonFatal should not catch Fatal", IOSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldNotCatchFatal),
    example("test CanRecover[IO].recoverEitherTFromNonFatal should return the successful result", IOSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnSuccessfulResult),
    example("test CanRecover[IO].recoverEitherTFromNonFatal should return the failed result", IOSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnFailedResult),

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

  object IOSpec {

    def testCanRecover_IO_recoverFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[IO, Int](throwThrowable[Int](expectedExpcetion))
      val expected = 123
      val actual = CanRecover[IO].recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) =>
          IO.pure(expected)
      }.unsafeRunSync()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_IO_recoverFromNonFatalWithShouldNotCatchFatal: Result = {

      val expectedExpcetion = new SomeControlThrowable("Something's wrong")
      val fa = run[IO, Int](throwThrowable[Int](expectedExpcetion))

      val io = CanRecover[IO].recoverFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => IO.pure(123) }
      try {
        val actual = io.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa = run[IO, Int](1)
      val expected = 1
      val actual = CanRecover[IO].recoverFromNonFatalWith(fa) {
        case NonFatal(_) => IO.pure(999)
      }.unsafeRunSync()

      actual ==== expected
    }


    def testCanRecover_IO_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
      val actualFailedResult = CanRecover[IO].recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) => IO.pure(expectedFailedResult)
      }.unsafeRunSync()

      val expectedSuccessResult = 1.asRight[SomeError]
      val actualSuccessResult = CanRecover[IO].recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) => IO.pure(1.asRight[SomeError])
      }.unsafeRunSync()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_IO_recoverFromNonFatalWithEitherShouldNotCatchFatal: Result = {

      val expectedExpcetion = new SomeControlThrowable("Something's wrong")
      val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

      val io = CanRecover[IO].recoverFromNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) => IO.pure(123.asRight[SomeError])
      }
      try {
        val actual = io.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

      val fa = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual = CanRecover[IO].recoverFromNonFatalWith(fa) {
        case NonFatal(_) => IO(999.asRight[SomeError])
      }.unsafeRunSync()

      actual ==== expected
    }

    def testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected = expectedFailure.asLeft[Int]
      val actual = CanRecover[IO].recoverFromNonFatalWith(fa) {
        case NonFatal(_) => IO.pure(123.asRight[SomeError])
      }.unsafeRunSync()

      actual ==== expected
    }


    def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult = CanRecover[IO].recoverEitherTFromNonFatalWith(fa) {
        case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
      }.value.unsafeRunSync()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult = CanRecover[IO].recoverEitherTFromNonFatalWith(fa) {
        case NonFatal(`expectedExpcetion`) => IO.pure(123.asRight[SomeError])
      }.value.unsafeRunSync()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldNotCatchFatal: Result = {

      val expectedExpcetion = new SomeControlThrowable("Something's wrong")
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))

      val io = CanRecover[IO].recoverEitherTFromNonFatalWith(fa) {
        case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
      }
      try {
        val actual = io.value.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual = CanRecover[IO].recoverEitherTFromNonFatalWith(fa) {
        case NonFatal(_) => IO.pure(123.asRight[SomeError])
      }.value.unsafeRunSync()

      actual ==== expected
    }

    def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected = expectedFailure.asLeft[Int]
      val actual =
        CanRecover[IO].recoverEitherTFromNonFatalWith(fa) {
          case NonFatal(_) => IO.pure(123.asRight[SomeError])
        }.value.unsafeRunSync()

      actual ==== expected
    }

    ///

    def testCanRecover_IO_recoverFromNonFatalShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[IO, Int](throwThrowable[Int](expectedExpcetion))
      val expected = 123
      val actual = CanRecover[IO].recoverFromNonFatal(fa) {
        case NonFatal(`expectedExpcetion`) =>
          expected
      }.unsafeRunSync()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_IO_recoverFromNonFatalShouldNotCatchFatal: Result = {

      val expectedExpcetion = new SomeControlThrowable("Something's wrong")
      val fa = run[IO, Int](throwThrowable[Int](expectedExpcetion))

      val io = CanRecover[IO].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123 }
      try {
        val actual = io.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverFromNonFatalShouldReturnSuccessfulResult: Result = {

      val fa = run[IO, Int](1)
      val expected = 1
      val actual = CanRecover[IO].recoverFromNonFatal(fa) { case NonFatal(_) => 999 }.unsafeRunSync()

      actual ==== expected
    }


    def testCanRecover_IO_recoverFromNonFatalEitherShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
      val actualFailedResult = CanRecover[IO].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expectedFailedResult }.unsafeRunSync()

      val expectedSuccessResult = 1.asRight[SomeError]
      val actualSuccessResult = CanRecover[IO].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }.unsafeRunSync()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_IO_recoverFromNonFatalEitherShouldNotCatchFatal: Result = {

      val expectedExpcetion = new SomeControlThrowable("Something's wrong")
      val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

      val io = CanRecover[IO].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }
      try {
        val actual = io.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverFromNonFatalEitherShouldReturnSuccessfulResult: Result = {

      val fa = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual = CanRecover[IO].recoverFromNonFatal(fa) { case NonFatal(_) => 999.asRight[SomeError] }.unsafeRunSync()

      actual ==== expected
    }

    def testCanRecover_IO_recoverFromNonFatalEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected = expectedFailure.asLeft[Int]
      val actual = CanRecover[IO].recoverFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }.unsafeRunSync()

      actual ==== expected
    }


    def testCanRecover_IO_recoverEitherTFromNonFatalShouldRecoverFromNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult =
        CanRecover[IO].recoverEitherTFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }.value.unsafeRunSync()
      val expectedSuccessResult = 123.asRight[SomeError]
      val actualSuccessResult =
        CanRecover[IO].recoverEitherTFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }.value.unsafeRunSync()

      actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanRecover_IO_recoverEitherTFromNonFatalShouldNotCatchFatal: Result = {

      val expectedExpcetion = new SomeControlThrowable("Something's wrong")
      val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))

      val io =
        CanRecover[IO].recoverEitherTFromNonFatal(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }
      try {
        val actual = io.value.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== expectedExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnSuccessfulResult: Result = {

      val fa = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual =
        CanRecover[IO].recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }.value.unsafeRunSync()

      actual ==== expected
    }

    def testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected = expectedFailure.asLeft[Int]
      val actual =
        CanRecover[IO].recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }.value.unsafeRunSync()

      actual ==== expected
    }

  }


  object FutureSpec {
    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration._
    import scala.concurrent.{ExecutionContext, Future}
    import scala.util.control.NonFatal

    val waitFor: FiniteDuration = 1.second

    def testCanRecover_Future_recoverFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = ConcurrentSupport.newExecutionContext(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Int](throwThrowable[Int](expectedExpcetion))
      val expected = 1
      val actual = ConcurrentSupport.futureToValueAndTerminate[Int](CanRecover[Future].recoverFromNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) => Future(expected)
        },
        waitFor
      )

      actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = ConcurrentSupport.newExecutionContext(executorService)

      val fa = run[Future, Int](1)
      val expected = 1
      val actual = ConcurrentSupport.futureToValueAndTerminate(CanRecover[Future].recoverFromNonFatalWith(fa) {
          case NonFatal(_) => Future(123)
        },
        waitFor
      )

      actual ==== expected
    }


    def testCanRecover_Future_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = ConcurrentSupport.newExecutionContext(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult =
        ConcurrentSupport.futureToValue(CanRecover[Future].recoverFromNonFatalWith(fa) {
            case err => Future(SomeError.someThrowable(err).asLeft[Int])
          },
          waitFor
        )

      val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual =
        ConcurrentSupport.futureToValueAndTerminate(CanRecover[Future].recoverFromNonFatalWith(fa2) {
            case NonFatal(`expectedExpcetion`) => Future(expected)
          },
          waitFor
        )

      expectedFailedResult ==== actualFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = ConcurrentSupport.newExecutionContext(executorService)

      val fa = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual =
        ConcurrentSupport.futureToValueAndTerminate(CanRecover[Future].recoverFromNonFatalWith(fa) {
            case err => Future(SomeError.someThrowable(err).asLeft[Int])
          },
          waitFor
        )

      actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalWithEitherShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = ConcurrentSupport.newExecutionContext(executorService)

      val expectedFailure = SomeError.message("Failed")
      val fa = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected = expectedFailure.asLeft[Int]
      val actual =
        ConcurrentSupport.futureToValueAndTerminate(CanRecover[Future].recoverFromNonFatalWith(fa) {
            case NonFatal(_) => Future(1.asRight[SomeError])
          },
          waitFor
        )

      actual ==== expected
    }


    def testCanRecover_Future_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = ConcurrentSupport.newExecutionContext(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult = ConcurrentSupport.futureToValue(
        CanRecover[Future].recoverEitherTFromNonFatalWith(fa) {
          case err => Future(SomeError.someThrowable(err).asLeft[Int])
        }.value,
        waitFor
      )

      val fa2 = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected = 1.asRight[SomeError]
      val actual =
        ConcurrentSupport.futureToValueAndTerminate(
          CanRecover[Future].recoverEitherTFromNonFatalWith(fa2) {
            case err => Future(expected)
          }.value,
          waitFor
        )

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = ConcurrentSupport.newExecutionContext(executorService)

      val fa = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual = ConcurrentSupport.futureToValueAndTerminate(
        CanRecover[Future].recoverEitherTFromNonFatalWith(fa) {
            case err => Future(SomeError.someThrowable(err).asLeft[Int])
          }.value,
          waitFor
        )

      actual ==== expected
    }

    def testCanRecover_Future_recoverEitherTFromNonFatalWithShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = ConcurrentSupport.newExecutionContext(executorService)

      val expectedFailure = SomeError.message("Failed")
      val fa = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected = expectedFailure.asLeft[Int]
      val actual =
        ConcurrentSupport.futureToValueAndTerminate(CanRecover[Future].recoverEitherTFromNonFatalWith(fa) {
            case NonFatal(_) => Future(expected)
          }.value,
          waitFor
        )

      actual ==== expected
    }

    ///

    def testCanRecover_Future_recoverFromNonFatalShouldRecoverFromNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = ConcurrentSupport.newExecutionContext(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Int](throwThrowable[Int](expectedExpcetion))
      val expected = 1
      val actual = ConcurrentSupport.futureToValue[Int](
        CanRecover[Future].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expected },
        waitFor
      )

      actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = ConcurrentSupport.newExecutionContext(executorService)

      val fa = run[Future, Int](1)
      val expected = 1
      val actual = ConcurrentSupport.futureToValueAndTerminate(
        CanRecover[Future].recoverFromNonFatal(fa) { case NonFatal(_) => 123 },
        waitFor
      )

      actual ==== expected
    }


    def testCanRecover_Future_recoverFromNonFatalEitherShouldRecoverFromNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = ConcurrentSupport.newExecutionContext(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult =
        ConcurrentSupport.futureToValue(CanRecover[Future].recoverFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          },
          waitFor
        )

      val fa2 = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = 1.asRight[SomeError]
      val actual = ConcurrentSupport.futureToValueAndTerminate(
        CanRecover[Future].recoverFromNonFatal(fa2) { case NonFatal(`expectedExpcetion`) => expected },
        waitFor
      )

      expectedFailedResult ==== actualFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalEitherShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = ConcurrentSupport.newExecutionContext(executorService)

      val fa = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual =
        ConcurrentSupport.futureToValueAndTerminate(
          CanRecover[Future].recoverFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          },
          waitFor
        )

      actual ==== expected
    }

    def testCanRecover_Future_recoverFromNonFatalEitherShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = ConcurrentSupport.newExecutionContext(executorService)

      val expectedFailure = SomeError.message("Failed")
      val fa = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected = expectedFailure.asLeft[Int]
      val actual = ConcurrentSupport.futureToValueAndTerminate(
        CanRecover[Future].recoverFromNonFatal(fa) { case NonFatal(_) => 1.asRight[SomeError] },
        waitFor
      )

      actual ==== expected
    }


    def testCanRecover_Future_recoverEitherTFromNonFatalShouldRecoverFromNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = ConcurrentSupport.newExecutionContext(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actualFailedResult = ConcurrentSupport.futureToValue(
          CanRecover[Future].recoverEitherTFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }.value,
          waitFor
        )

      val fa2 = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected = 1.asRight[SomeError]
      val actual =
        ConcurrentSupport.futureToValueAndTerminate(
          CanRecover[Future].recoverEitherTFromNonFatal(fa2) { case err => expected }.value,
          waitFor
        )

      actualFailedResult ==== expectedFailedResult and actual ==== expected
    }

    def testCanRecover_Future_recoverEitherTFromNonFatalShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = ConcurrentSupport.newExecutionContext(executorService)

      val fa = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual = ConcurrentSupport.futureToValueAndTerminate(
          CanRecover[Future].recoverEitherTFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }.value,
          waitFor
        )

      actual ==== expected
    }

    def testCanRecover_Future_recoverEitherTFromNonFatalShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = ConcurrentSupport.newExecutionContext(executorService)

      val expectedFailure = SomeError.message("Failed")
      val fa = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected = expectedFailure.asLeft[Int]
      val actual =
        ConcurrentSupport.futureToValueAndTerminate(
          CanRecover[Future].recoverEitherTFromNonFatal(fa) { case NonFatal(_) => expected }.value,
          waitFor
        )

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
