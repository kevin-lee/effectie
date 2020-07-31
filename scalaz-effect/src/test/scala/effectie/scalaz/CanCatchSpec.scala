package effectie.scalaz

import scalaz._
import Scalaz._
import scalaz.effect._

import effectie.Effectful._
import effectie.{ExecutorServiceOps, SomeControlThrowable}

import hedgehog._
import hedgehog.runner._

import scala.util.control.ControlThrowable

/**
 * @author Kevin Lee
 * @since 2020-07-31
 */
object CanCatchSpec extends Properties {

  override def tests: List[Test] = List(
    example("test CanCatch[IO]catchNonFatal should catch NonFatal", testCanCatch_IO_catchNonFatalShouldCatchNonFatal),
    example("test CanCatch[IO]catchNonFatal should not catch Fatal", testCanCatch_IO_catchNonFatalShouldNotCatchFatal),
    example("test CanCatch[IO]catchNonFatal should return the successful result", testCanCatch_IO_catchNonFatalShouldReturnSuccessfulResult),

    example("test CanCatch[IO]catchNonFatalEither should catch NonFatal", testCanCatch_IO_catchNonFatalEitherShouldCatchNonFatal),
    example("test CanCatch[IO]catchNonFatalEither should not catch Fatal", testCanCatch_IO_catchNonFatalEitherShouldNotCatchFatal),
    example("test CanCatch[IO]catchNonFatalEither should return the successful result", testCanCatch_IO_catchNonFatalEitherShouldReturnSuccessfulResult),
    example("test CanCatch[IO]catchNonFatalEither should return the failed result", testCanCatch_IO_catchNonFatalEitherShouldReturnFailedResult),

    example("test CanCatch[IO]catchNonFatalEitherT should catch NonFatal", testCanCatch_IO_catchNonFatalEitherTShouldCatchNonFatal),
    example("test CanCatch[IO]catchNonFatalEitherT should not catch Fatal", testCanCatch_IO_catchNonFatalEitherTShouldNotCatchFatal),
    example("test CanCatch[IO]catchNonFatalEitherT should return the successful result", testCanCatch_IO_catchNonFatalEitherTShouldReturnSuccessfulResult),
    example("test CanCatch[IO]catchNonFatalEitherT should return the failed result", testCanCatch_IO_catchNonFatalEitherTShouldReturnFailedResult),

    /* Future */

    example("test CanCatch[Future]catchNonFatal should catch NonFatal", FutureSpec.testCanCatch_Future_catchNonFatalShouldCatchNonFatal),
    example("test CanCatch[Future]catchNonFatal should return the successful result", FutureSpec.testCanCatch_Future_catchNonFatalShouldReturnSuccessfulResult),

    example("test CanCatch[Future]catchNonFatalEither should catch NonFatal", FutureSpec.testCanCatch_Future_catchNonFatalEitherShouldCatchNonFatal),
    example("test CanCatch[Future]catchNonFatalEither should return the successful result", FutureSpec.testCanCatch_Future_catchNonFatalEitherShouldReturnSuccessfulResult),
    example("test CanCatch[Future]catchNonFatalEither should return the failed result", FutureSpec.testCanCatch_Future_catchNonFatalEitherShouldReturnFailedResult),

    example("test CanCatch[Future]catchNonFatalEitherT should catch NonFatal", FutureSpec.testCanCatch_Future_catchNonFatalEitherTShouldCatchNonFatal),
    example("test CanCatch[Future]catchNonFatalEitherT should return the successful result", FutureSpec.testCanCatch_Future_catchNonFatalEitherTShouldReturnSuccessfulResult),
    example("test CanCatch[Future]catchNonFatalEitherT should return the failed result", FutureSpec.testCanCatch_Future_catchNonFatalEitherTShouldReturnFailedResult),


    /* Id */
    example("test CanCatch[Id]catchNonFatal should catch NonFatal", IdSpec.testCanCatch_Id_catchNonFatalShouldCatchNonFatal),
    example("test CanCatch[Id]catchNonFatal should not catch Fatal", IdSpec.testCanCatch_Id_catchNonFatalShouldNotCatchFatal),
    example("test CanCatch[Id]catchNonFatal should return the successful result", IdSpec.testCanCatch_Id_catchNonFatalShouldReturnSuccessfulResult),

    example("test CanCatch[Id]catchNonFatalEither should catch NonFatal", IdSpec.testCanCatch_Id_catchNonFatalEitherShouldCatchNonFatal),
    example("test CanCatch[Id]catchNonFatalEither should not catch Fatal", IdSpec.testCanCatch_Id_catchNonFatalEitherShouldNotCatchFatal),
    example("test CanCatch[Id]catchNonFatalEither should return the successful result", IdSpec.testCanCatch_Id_catchNonFatalEitherShouldReturnSuccessfulResult),
    example("test CanCatch[Id]catchNonFatalEither should return the failed result", IdSpec.testCanCatch_Id_catchNonFatalEitherShouldReturnFailedResult),

    example("test CanCatch[Id]catchNonFatalEitherT should catch NonFatal", IdSpec.testCanCatch_Id_catchNonFatalEitherTShouldCatchNonFatal),
    example("test CanCatch[Id]catchNonFatalEitherT should not catch Fatal", IdSpec.testCanCatch_Id_catchNonFatalEitherTShouldNotCatchFatal),
    example("test CanCatch[Id]catchNonFatalEitherT should return the successful result", IdSpec.testCanCatch_Id_catchNonFatalEitherTShouldReturnSuccessfulResult),
    example("test CanCatch[Id]catchNonFatalEitherT should return the failed result", IdSpec.testCanCatch_Id_catchNonFatalEitherTShouldReturnFailedResult)
  )

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: Throwable): A =
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

  def testCanCatch_IO_catchNonFatalShouldCatchNonFatal: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[IO, Int](throwThrowable[Int](expectedExpcetion))
    val expected = SomeError.someThrowable(expectedExpcetion).left[Int]
    val actual = CanCatch[IO].catchNonFatal(fa)(SomeError.someThrowable).unsafePerformIO()

    actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanCatch_IO_catchNonFatalShouldNotCatchFatal: Result = {

    val fatalExpcetion = new SomeControlThrowable("Something's wrong")
    val fa = run[IO, Int](throwThrowable[Int](fatalExpcetion))

    try {
      val actual = CanCatch[IO].catchNonFatal(fa)(SomeError.someThrowable).unsafePerformIO()
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== fatalExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanCatch_IO_catchNonFatalShouldReturnSuccessfulResult: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[IO, Int](1)
    val expected = 1.right[SomeError]
    val actual = CanCatch[IO].catchNonFatal(fa)(SomeError.someThrowable).unsafePerformIO()

    actual ==== expected
  }


  def testCanCatch_IO_catchNonFatalEitherShouldCatchNonFatal: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[IO, SomeError \/ Int](throwThrowable[SomeError \/ Int](expectedExpcetion))
    val expected = SomeError.someThrowable(expectedExpcetion).left[Int]
    val actual = CanCatch[IO].catchNonFatalEither(fa)(SomeError.someThrowable).unsafePerformIO()

    actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanCatch_IO_catchNonFatalEitherShouldNotCatchFatal: Result = {

    val fatalExpcetion = new SomeControlThrowable("Something's wrong")
    val fa = run[IO, SomeError \/ Int](throwThrowable[SomeError \/ Int](fatalExpcetion))

    try {
      val actual = CanCatch[IO].catchNonFatalEither(fa)(SomeError.someThrowable).unsafePerformIO()
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== fatalExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanCatch_IO_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = run[IO, SomeError \/ Int](1.right[SomeError])
    val expected = 1.right[SomeError]
    val actual = CanCatch[IO].catchNonFatalEither(fa)(SomeError.someThrowable).unsafePerformIO()

    actual ==== expected
  }

  def testCanCatch_IO_catchNonFatalEitherShouldReturnFailedResult: Result = {

    val expectedFailure = SomeError.message("Failed")
    val fa = run[IO, SomeError \/ Int](expectedFailure.left[Int])
    val expected = expectedFailure.left[Int]
    val actual = CanCatch[IO].catchNonFatalEither(fa)(SomeError.someThrowable).unsafePerformIO()

    actual ==== expected
  }


  def testCanCatch_IO_catchNonFatalEitherTShouldCatchNonFatal: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = EitherT(run[IO, SomeError \/ Int](throwThrowable[SomeError \/ Int](expectedExpcetion)))
    val expected = SomeError.someThrowable(expectedExpcetion).left[Int]
    val actual = CanCatch[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).run.unsafePerformIO()

    actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testCanCatch_IO_catchNonFatalEitherTShouldNotCatchFatal: Result = {

    val fatalExpcetion = new SomeControlThrowable("Something's wrong")
    val fa = EitherT(run[IO, SomeError \/ Int](throwThrowable[SomeError \/ Int](fatalExpcetion)))

    try {
      val actual = CanCatch[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).run.unsafePerformIO()
      Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
    } catch {
      case ex: ControlThrowable =>
        ex ==== fatalExpcetion

      case ex: Throwable =>
        Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  def testCanCatch_IO_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = {

    val expectedExpcetion = new RuntimeException("Something's wrong")
    val fa = EitherT(run[IO, SomeError \/ Int](1.right[SomeError]))
    val expected = 1.right[SomeError]
    val actual = CanCatch[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).run.unsafePerformIO()

    actual ==== expected
  }

  def testCanCatch_IO_catchNonFatalEitherTShouldReturnFailedResult: Result = {

    val expectedFailure = SomeError.message("Failed")
    val fa = EitherT(run[IO, SomeError \/ Int](expectedFailure.left[Int]))
    val expected = expectedFailure.left[Int]
    val actual = CanCatch[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).run.unsafePerformIO()

    actual ==== expected
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

    def testCanCatch_Future_catchNonFatalShouldCatchNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Int](throwThrowable[Int](expectedExpcetion))
      val expected = SomeError.someThrowable(expectedExpcetion).left[Int]
      val actual = futureToValue(CanCatch[Future].catchNonFatal(fa)(SomeError.someThrowable))

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, Int](1)
      val expected = 1.right[SomeError]
      val actual = futureToValue(CanCatch[Future].catchNonFatal(fa)(SomeError.someThrowable))

      actual ==== expected
    }


    def testCanCatch_Future_catchNonFatalEitherShouldCatchNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, SomeError \/ Int](throwThrowable[SomeError \/ Int](expectedExpcetion))
      val expected = SomeError.someThrowable(expectedExpcetion).left[Int]
      val actual = futureToValue(CanCatch[Future].catchNonFatalEither(fa)(SomeError.someThrowable))

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Future, SomeError \/ Int](1.right[SomeError])
      val expected = 1.right[SomeError]
      val actual = futureToValue(CanCatch[Future].catchNonFatalEither(fa)(SomeError.someThrowable))

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalEitherShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val expectedFailure = SomeError.message("Failed")
      val fa = run[Future, SomeError \/ Int](expectedFailure.left[Int])
      val expected = expectedFailure.left[Int]
      val actual = futureToValue(CanCatch[Future].catchNonFatalEither(fa)(SomeError.someThrowable))

      actual ==== expected
    }


    def testCanCatch_Future_catchNonFatalEitherTShouldCatchNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Future, SomeError \/ Int](throwThrowable[SomeError \/ Int](expectedExpcetion)))
      val expected = SomeError.someThrowable(expectedExpcetion).left[Int]
      val actual = futureToValue(CanCatch[Future].catchNonFatalEitherT(fa)(SomeError.someThrowable).run)

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Future, SomeError \/ Int](1.right[SomeError]))
      val expected = 1.right[SomeError]
      val actual = futureToValue(CanCatch[Future].catchNonFatalEitherT(fa)(SomeError.someThrowable).run)

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalEitherTShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = executionContextExecutor(executorService)

      val expectedFailure = SomeError.message("Failed")
      val fa = EitherT(run[Future, SomeError \/ Int](expectedFailure.left[Int]))
      val expected = expectedFailure.left[Int]
      val actual = futureToValue(CanCatch[Future].catchNonFatalEitherT(fa)(SomeError.someThrowable).run)

      actual ==== expected
    }
  }


  object IdSpec {

    def testCanCatch_Id_catchNonFatalShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa = run[Id, Int](throwThrowable[Int](expectedExpcetion))
      val expected = SomeError.someThrowable(expectedExpcetion).left[Int]
      val actual = CanCatch[Id].catchNonFatal(fa)(SomeError.someThrowable)

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanCatch_Id_catchNonFatalShouldNotCatchFatal: Result = {

      val fatalExpcetion = new SomeControlThrowable("Something's wrong")
      lazy val fa = run[Id, Int](throwThrowable[Int](fatalExpcetion))

      try {
        val actual = CanCatch[Id].catchNonFatal(fa)(SomeError.someThrowable)
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_Id_catchNonFatalShouldReturnSuccessfulResult: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Id, Int](1)
      val expected = 1.right[SomeError]
      val actual = CanCatch[Id].catchNonFatal(fa)(SomeError.someThrowable)

      actual ==== expected
    }


    def testCanCatch_Id_catchNonFatalEitherShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa = run[Id, SomeError \/ Int](throwThrowable[SomeError \/ Int](expectedExpcetion))
      val expected = SomeError.someThrowable(expectedExpcetion).left[Int]
      val actual = CanCatch[Id].catchNonFatalEither(fa)(SomeError.someThrowable)

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanCatch_Id_catchNonFatalEitherShouldNotCatchFatal: Result = {

      val fatalExpcetion = new SomeControlThrowable("Something's wrong")
      lazy val fa = run[Id, SomeError \/ Int](throwThrowable[SomeError \/ Int](fatalExpcetion))

      try {
        val actual = CanCatch[Id].catchNonFatalEither(fa)(SomeError.someThrowable)
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_Id_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = run[Id, SomeError \/ Int](1.right[SomeError])
      val expected = 1.right[SomeError]
      val actual = CanCatch[Id].catchNonFatalEither(fa)(SomeError.someThrowable)

      actual ==== expected
    }

    def testCanCatch_Id_catchNonFatalEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa = run[Id, SomeError \/ Int](expectedFailure.left[Int])
      val expected = expectedFailure.left[Int]
      val actual = CanCatch[Id].catchNonFatalEither(fa)(SomeError.someThrowable)

      actual ==== expected
    }


    def testCanCatch_Id_catchNonFatalEitherTShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa = EitherT(run[Id, SomeError \/ Int](throwThrowable[SomeError \/ Int](expectedExpcetion)))
      val expected = SomeError.someThrowable(expectedExpcetion).left[Int]
      val actual = CanCatch[Id].catchNonFatalEitherT(fa)(SomeError.someThrowable).run

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanCatch_Id_catchNonFatalEitherTShouldNotCatchFatal: Result = {

      val fatalExpcetion = new SomeControlThrowable("Something's wrong")
      lazy val fa = EitherT(run[Id, SomeError \/ Int](throwThrowable[SomeError \/ Int](fatalExpcetion)))

      try {
        val actual = CanCatch[Id].catchNonFatalEitherT(fa)(SomeError.someThrowable).run
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_Id_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa = EitherT(run[Id, SomeError \/ Int](1.right[SomeError]))
      val expected = 1.right[SomeError]
      val actual = CanCatch[Id].catchNonFatalEitherT(fa)(SomeError.someThrowable).run

      actual ==== expected
    }

    def testCanCatch_Id_catchNonFatalEitherTShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa = EitherT(run[Id, SomeError \/ Int](expectedFailure.left[Int]))
      val expected = expectedFailure.left[Int]
      val actual = CanCatch[Id].catchNonFatalEitherT(fa)(SomeError.someThrowable).run

      actual ==== expected
    }

  }
}
