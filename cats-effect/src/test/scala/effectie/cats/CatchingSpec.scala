package effectie.cats

import Catching._

import cats._
import cats.data.EitherT
import cats.effect._
import cats.implicits._

import effectie.Effectful.effectOf
import effectie.{ExecutorServiceOps, SomeControlThrowable}

import hedgehog._
import hedgehog.runner._

import scala.util.control.ControlThrowable

/**
 * @author Kevin Lee
 * @since 2020-07-31
 */
object CatchingSpec extends Properties {

  override def tests: List[Test] = List(
    /* IO */
    example("test Catching.catchNonFatal[IO] should catch NonFatal", testCatching_IO_catchNonFatalShouldCatchNonFatal),
    example("test Catching.catchNonFatal[IO] should not catch Fatal", testCatching_IO_catchNonFatalShouldNotCatchFatal),
    example("test Catching.catchNonFatal[IO] should return the successful result", testCatching_IO_catchNonFatalShouldReturnSuccessfulResult),

    example("test Catching.catchNonFatalF[IO] should catch NonFatal", testCatching_IO_catchNonFatalFShouldCatchNonFatal),
    example("test Catching.catchNonFatalF[IO] should not catch Fatal", testCatching_IO_catchNonFatalFShouldNotCatchFatal),
    example("test Catching.catchNonFatalF[IO] should return the successful result", testCatching_IO_catchNonFatalFShouldReturnSuccessfulResult),

    example("test Catching.catchNonFatalEither[IO] should catch NonFatal", testCatching_IO_catchNonFatalEitherShouldCatchNonFatal),
    example("test Catching.catchNonFatalEither[IO] should not catch Fatal", testCatching_IO_catchNonFatalEitherShouldNotCatchFatal),
    example("test Catching.catchNonFatalEither[IO] should return the successful result", testCatching_IO_catchNonFatalEitherShouldReturnSuccessfulResult),
    example("test Catching.catchNonFatalEither[IO] should return the failed result", testCatching_IO_catchNonFatalEitherShouldReturnFailedResult),

    example("test Catching.catchNonFatalEitherF[IO] should catch NonFatal", testCatching_IO_catchNonFatalEitherFShouldCatchNonFatal),
    example("test Catching.catchNonFatalEitherF[IO] should not catch Fatal", testCatching_IO_catchNonFatalEitherFShouldNotCatchFatal),
    example("test Catching.catchNonFatalEitherF[IO] should return the successful result", testCatching_IO_catchNonFatalEitherFShouldReturnSuccessfulResult),
    example("test Catching.catchNonFatalEitherF[IO] should return the failed result", testCatching_IO_catchNonFatalEitherFShouldReturnFailedResult),

    example("test Catching.catchNonFatalEitherT[IO] should catch NonFatal", testCatching_IO_catchNonFatalEitherTShouldCatchNonFatal),
    example("test Catching.catchNonFatalEitherT[IO] should not catch Fatal", testCatching_IO_catchNonFatalEitherTShouldNotCatchFatal),
    example("test Catching.catchNonFatalEitherT[IO] should return the successful result", testCatching_IO_catchNonFatalEitherTShouldReturnSuccessfulResult),
    example("test Catching.catchNonFatalEitherT[IO] should return the failed result", testCatching_IO_catchNonFatalEitherTShouldReturnFailedResult),

    /* Future */

    example("test Catching.catchNonFatal[Future] should catch NonFatal", FutureSpec.testCatching_Future_catchNonFatalShouldCatchNonFatal),
    example("test Catching.catchNonFatal[Future] should return the successful result", FutureSpec.testCatching_Future_catchNonFatalShouldReturnSuccessfulResult),

    example("test Catching.catchNonFatalF[Future] should catch NonFatal", FutureSpec.testCatching_Future_catchNonFatalFShouldCatchNonFatal),
    example("test Catching.catchNonFatalF[Future] should return the successful result", FutureSpec.testCatching_Future_catchNonFatalFShouldReturnSuccessfulResult),

    example("test Catching.catchNonFatalEither[Future] should catch NonFatal", FutureSpec.testCatching_Future_catchNonFatalEitherShouldCatchNonFatal),
    example("test Catching.catchNonFatalEither[Future] should return the successful result", FutureSpec.testCatching_Future_catchNonFatalEitherShouldReturnSuccessfulResult),
    example("test Catching.catchNonFatalEither[Future] should return the failed result", FutureSpec.testCatching_Future_catchNonFatalEitherShouldReturnFailedResult),

    example("test Catching.catchNonFatalEitherF[Future] should catch NonFatal", FutureSpec.testCatching_Future_catchNonFatalEitherFShouldCatchNonFatal),
    example("test Catching.catchNonFatalEitherF[Future] should return the successful result", FutureSpec.testCatching_Future_catchNonFatalEitherFShouldReturnSuccessfulResult),
    example("test Catching.catchNonFatalEitherF[Future] should return the failed result", FutureSpec.testCatching_Future_catchNonFatalEitherFShouldReturnFailedResult),

    example("test Catching.catchNonFatalEitherT[Future] should catch NonFatal", FutureSpec.testCatching_Future_catchNonFatalEitherTShouldCatchNonFatal),
    example("test Catching.catchNonFatalEitherT[Future] should return the successful result", FutureSpec.testCatching_Future_catchNonFatalEitherTShouldReturnSuccessfulResult),
    example("test Catching.catchNonFatalEitherT[Future] should return the failed result", FutureSpec.testCatching_Future_catchNonFatalEitherTShouldReturnFailedResult),


    /* Id */
    example("test Catching.catchNonFatal[Id] should catch NonFatal", IdSpec.testCatching_Id_catchNonFatalShouldCatchNonFatal),
    example("test Catching.catchNonFatal[Id] should not catch Fatal", IdSpec.testCatching_Id_catchNonFatalShouldNotCatchFatal),
    example("test Catching.catchNonFatal[Id] should return the successful result", IdSpec.testCatching_Id_catchNonFatalShouldReturnSuccessfulResult),

    example("test Catching.catchNonFatalF[Id] should catch NonFatal", IdSpec.testCatching_Id_catchNonFatalFShouldCatchNonFatal),
    example("test Catching.catchNonFatalF[Id] should not catch Fatal", IdSpec.testCatching_Id_catchNonFatalFShouldNotCatchFatal),
    example("test Catching.catchNonFatalF[Id] should return the successful result", IdSpec.testCatching_Id_catchNonFatalFShouldReturnSuccessfulResult),

    example("test Catching.catchNonFatalEither[Id] should catch NonFatal", IdSpec.testCatching_Id_catchNonFatalEitherShouldCatchNonFatal),
    example("test Catching.catchNonFatalEither[Id] should not catch Fatal", IdSpec.testCatching_Id_catchNonFatalEitherShouldNotCatchFatal),
    example("test Catching.catchNonFatalEither[Id] should return the successful result", IdSpec.testCatching_Id_catchNonFatalEitherShouldReturnSuccessfulResult),
    example("test Catching.catchNonFatalEither[Id] should return the failed result", IdSpec.testCatching_Id_catchNonFatalEitherShouldReturnFailedResult),

    example("test Catching.catchNonFatalEitherT[Id] should catch NonFatal", IdSpec.testCatching_Id_catchNonFatalEitherTShouldCatchNonFatal),
    example("test Catching.catchNonFatalEitherT[Id] should not catch Fatal", IdSpec.testCatching_Id_catchNonFatalEitherTShouldNotCatchFatal),
    example("test Catching.catchNonFatalEitherT[Id] should return the successful result", IdSpec.testCatching_Id_catchNonFatalEitherTShouldReturnSuccessfulResult),
    example("test Catching.catchNonFatalEitherT[Id] should return the failed result", IdSpec.testCatching_Id_catchNonFatalEitherTShouldReturnFailedResult)
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

  def testCatching_IO_catchNonFatalShouldCatchNonFatal: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[IO, Int](throwThrowable[Int](expectedExpcetion))
    val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actual = catchNonFatal(fa)(SomeError.someThrowable).unsafeRunSync()

    actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCatching_IO_catchNonFatalShouldNotCatchFatal: Result = {

    val fatalExpcetion = new SomeControlThrowable("Something's wrong")
    val fa = run[IO, Int](throwThrowable[Int](fatalExpcetion))

    try {
      val actual = catchNonFatal(fa)(SomeError.someThrowable).unsafeRunSync()
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== fatalExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCatching_IO_catchNonFatalShouldReturnSuccessfulResult: Result = {

    val fa = run[IO, Int](1)
    val expected = 1.asRight[SomeError]
    val actual = catchNonFatal(fa)(SomeError.someThrowable).unsafeRunSync()

    actual ==== expected
  }


  def testCatching_IO_catchNonFatalFShouldCatchNonFatal: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actual = catchNonFatalF[IO](throwThrowable[Int](expectedExpcetion))(SomeError.someThrowable).unsafeRunSync()

    actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCatching_IO_catchNonFatalFShouldNotCatchFatal: Result = {

    val fatalExpcetion = new SomeControlThrowable("Something's wrong")

    try {
      val actual = catchNonFatalF[IO](throwThrowable[Int](fatalExpcetion))(SomeError.someThrowable).unsafeRunSync()
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== fatalExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCatching_IO_catchNonFatalFShouldReturnSuccessfulResult: Result = {

    val expected = 1.asRight[SomeError]
    val actual = catchNonFatalF[IO](1)(SomeError.someThrowable).unsafeRunSync()

    actual ==== expected
  }


  def testCatching_IO_catchNonFatalEitherShouldCatchNonFatal: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
    val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actual = catchNonFatalEither(fa)(SomeError.someThrowable).unsafeRunSync()

    actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCatching_IO_catchNonFatalEitherShouldNotCatchFatal: Result = {

    val fatalExpcetion = new SomeControlThrowable("Something's wrong")
    val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

    try {
      val actual = catchNonFatalEither(fa)(SomeError.someThrowable).unsafeRunSync()
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== fatalExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCatching_IO_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

    val fa = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
    val expected = 1.asRight[SomeError]
    val actual = catchNonFatalEither(fa)(SomeError.someThrowable).unsafeRunSync()

    actual ==== expected
  }

  def testCatching_IO_catchNonFatalEitherShouldReturnFailedResult: Result = {

    val expectedFailure = SomeError.message("Failed")
    val fa = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
    val expected = expectedFailure.asLeft[Int]
    val actual = catchNonFatalEither(fa)(SomeError.someThrowable).unsafeRunSync()

    actual ==== expected
  }


  def testCatching_IO_catchNonFatalEitherFShouldCatchNonFatal: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actual = catchNonFatalEitherF[IO](throwThrowable[Either[SomeError, Int]](expectedExpcetion))(SomeError.someThrowable).unsafeRunSync()

    actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCatching_IO_catchNonFatalEitherFShouldNotCatchFatal: Result = {

    val fatalExpcetion = new SomeControlThrowable("Something's wrong")

    try {
      val actual = catchNonFatalEitherF[IO](throwThrowable[Either[SomeError, Int]](fatalExpcetion))(SomeError.someThrowable).unsafeRunSync()
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== fatalExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCatching_IO_catchNonFatalEitherFShouldReturnSuccessfulResult: Result = {

    val expected = 1.asRight[SomeError]
    val actual = catchNonFatalEitherF[IO](1.asRight[SomeError])(SomeError.someThrowable).unsafeRunSync()

    actual ==== expected
  }

  def testCatching_IO_catchNonFatalEitherFShouldReturnFailedResult: Result = {

    val expectedFailure = SomeError.message("Failed")
    val expected = expectedFailure.asLeft[Int]
    val actual = catchNonFatalEitherF[IO](expectedFailure.asLeft[Int])(SomeError.someThrowable).unsafeRunSync()

    actual ==== expected
  }


  def testCatching_IO_catchNonFatalEitherTShouldCatchNonFatal: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
    val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
    val actual =
      catchNonFatalEitherT[IO](fa)(SomeError.someThrowable).value.unsafeRunSync()

    actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCatching_IO_catchNonFatalEitherTShouldNotCatchFatal: Result = {

    val fatalExpcetion = new SomeControlThrowable("Something's wrong")
    val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

    try {
      val actual = catchNonFatalEitherT[IO](fa)(SomeError.someThrowable).value.unsafeRunSync()
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== fatalExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCatching_IO_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
    val expected = 1.asRight[SomeError]
    val actual = catchNonFatalEitherT[IO](fa)(SomeError.someThrowable).value.unsafeRunSync()

    actual ==== expected
  }

  def testCatching_IO_catchNonFatalEitherTShouldReturnFailedResult: Result = {

    val expectedFailure = SomeError.message("Failed")
    val fa = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
    val expected = expectedFailure.asLeft[Int]
    val actual = catchNonFatalEitherT[IO](fa)(SomeError.someThrowable).value.unsafeRunSync()

    actual ==== expected
  }


  /////////

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
      } finally {
        try {
          ExecutorServiceOps.shutdownAndAwaitTermination(executorService, waitFor)
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

    def testCatching_Future_catchNonFatalShouldCatchNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Int](throwThrowable[Int](expectedExpcetion))
      val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual = futureToValue(catchNonFatal(fa)(SomeError.someThrowable))

      actual ==== expected
    }

    def testCatching_Future_catchNonFatalShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val fa = run[Future, Int](1)
      val expected = 1.asRight[SomeError]
      val actual = futureToValue(catchNonFatal(fa)(SomeError.someThrowable))

      actual ==== expected
    }


    def testCatching_Future_catchNonFatalFShouldCatchNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual = futureToValue(catchNonFatalF[Future](throwThrowable[Int](expectedExpcetion))(SomeError.someThrowable))

      actual ==== expected
    }

    def testCatching_Future_catchNonFatalFShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val expected = 1.asRight[SomeError]
      val actual = futureToValue(catchNonFatalF[Future](1)(SomeError.someThrowable))

      actual ==== expected
    }


    def testCatching_Future_catchNonFatalEitherShouldCatchNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual = futureToValue(catchNonFatalEither(fa)(SomeError.someThrowable))

      actual ==== expected
    }

    def testCatching_Future_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val fa = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual = futureToValue(catchNonFatalEither(fa)(SomeError.someThrowable))

      actual ==== expected
    }

    def testCatching_Future_catchNonFatalEitherShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val expectedFailure = SomeError.message("Failed")
      val fa = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected = expectedFailure.asLeft[Int]
      val actual = futureToValue(catchNonFatalEither(fa)(SomeError.someThrowable))

      actual ==== expected
    }


    def testCatching_Future_catchNonFatalEitherFShouldCatchNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual =
        futureToValue(
          catchNonFatalEitherF[Future](
            throwThrowable[Either[SomeError, Int]](expectedExpcetion)
          )(SomeError.someThrowable)
        )

      actual ==== expected
    }

    def testCatching_Future_catchNonFatalEitherFShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val expected = 1.asRight[SomeError]
      val actual =
        futureToValue(catchNonFatalEitherF[Future](1.asRight[SomeError])(SomeError.someThrowable))

      actual ==== expected
    }

    def testCatching_Future_catchNonFatalEitherFShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val expectedFailure = SomeError.message("Failed")
      val expected = expectedFailure.asLeft[Int]
      val actual = futureToValue(catchNonFatalEitherF[Future](expectedFailure.asLeft[Int])(SomeError.someThrowable))

      actual ==== expected
    }


    def testCatching_Future_catchNonFatalEitherTShouldCatchNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual = futureToValue(catchNonFatalEitherT[Future](fa)(SomeError.someThrowable).value)

      actual ==== expected
    }

    def testCatching_Future_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val fa = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual = futureToValue(catchNonFatalEitherT[Future](fa)(SomeError.someThrowable).value)

      actual ==== expected
    }

    def testCatching_Future_catchNonFatalEitherTShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val expectedFailure = SomeError.message("Failed")
      val fa = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected = expectedFailure.asLeft[Int]
      val actual = futureToValue(catchNonFatalEitherT[Future](fa)(SomeError.someThrowable).value)

      actual ==== expected
    }
  }


  object IdSpec {

    def testCatching_Id_catchNonFatalShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa = run[Id, Int](throwThrowable[Int](expectedExpcetion))
      val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual = catchNonFatal(fa)(SomeError.someThrowable)

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCatching_Id_catchNonFatalShouldNotCatchFatal: Result = {

      val fatalExpcetion = new SomeControlThrowable("Something's wrong")
      lazy val fa = run[Id, Int](throwThrowable[Int](fatalExpcetion))

      try {
        val actual = catchNonFatal(fa)(SomeError.someThrowable)
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCatching_Id_catchNonFatalShouldReturnSuccessfulResult: Result = {

      val fa = run[Id, Int](1)
      val expected = 1.asRight[SomeError]
      val actual = catchNonFatal(fa)(SomeError.someThrowable)

      actual ==== expected
    }


    def testCatching_Id_catchNonFatalFShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual = catchNonFatalF[Id](throwThrowable[Int](expectedExpcetion))(SomeError.someThrowable)

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCatching_Id_catchNonFatalFShouldNotCatchFatal: Result = {

      val fatalExpcetion = new SomeControlThrowable("Something's wrong")

      try {
        val actual = catchNonFatalF[Id](throwThrowable[Int](fatalExpcetion))(SomeError.someThrowable)
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCatching_Id_catchNonFatalFShouldReturnSuccessfulResult: Result = {

      val expected = 1.asRight[SomeError]
      val actual = catchNonFatalF[Id](1)(SomeError.someThrowable)

      actual ==== expected
    }


    def testCatching_Id_catchNonFatalEitherShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual = catchNonFatalEither(fa)(SomeError.someThrowable)

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCatching_Id_catchNonFatalEitherShouldNotCatchFatal: Result = {

      val fatalExpcetion = new SomeControlThrowable("Something's wrong")
      lazy val fa = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual = catchNonFatalEither(fa)(SomeError.someThrowable)
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCatching_Id_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

      val fa = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual = catchNonFatalEither(fa)(SomeError.someThrowable)

      actual ==== expected
    }

    def testCatching_Id_catchNonFatalEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected = expectedFailure.asLeft[Int]
      val actual = catchNonFatalEither(fa)(SomeError.someThrowable)

      actual ==== expected
    }


    def testCatching_Id_catchNonFatalEitherTShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual = catchNonFatalEitherT[Id](fa)(SomeError.someThrowable).value

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCatching_Id_catchNonFatalEitherTShouldNotCatchFatal: Result = {

      val fatalExpcetion = new SomeControlThrowable("Something's wrong")
      lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

      try {
        val actual = catchNonFatalEitherT[Id](fa)(SomeError.someThrowable).value
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCatching_Id_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = {

      val fa = EitherT(run[Id, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual = catchNonFatalEitherT[Id](fa)(SomeError.someThrowable).value

      actual ==== expected
    }

    def testCatching_Id_catchNonFatalEitherTShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa = EitherT(run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected = expectedFailure.asLeft[Int]
      val actual = catchNonFatalEitherT[Id](fa)(SomeError.someThrowable).value

      actual ==== expected
    }

  }

}
