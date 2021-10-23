package effectie.monix

import cats._
import cats.data.EitherT
import cats.instances.all._
import cats.syntax.all._
import effectie.monix.Effectful._
import effectie.{ConcurrentSupport, SomeControlThrowable}
import hedgehog._
import hedgehog.runner._
import monix.eval.Task

import scala.util.control.ControlThrowable

/** @author Kevin Lee
  * @since 2020-07-31
  */
object CanCatchSpec extends Properties {

  override def tests: List[Test] = List(
    /* Task */
    example(
      "test CanCatch[Task].catchNonFatalThrowable should catch NonFatal Throwable",
      TaskSpec.testCanCatch_Task_catchNonFatalShouldCatchNonFatalThrowable
    ),
    example(
      "test CanCatch[Task].catchNonFatal should catch NonFatal",
      TaskSpec.testCanCatch_Task_catchNonFatalShouldCatchNonFatal
    ),
    example(
      "test CanCatch[Task].catchNonFatal should not catch Fatal",
      TaskSpec.testCanCatch_Task_catchNonFatalShouldNotCatchFatal
    ),
    example(
      "test CanCatch[Task].catchNonFatal should return the successful result",
      TaskSpec.testCanCatch_Task_catchNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[Task].catchNonFatalEither should catch NonFatal",
      TaskSpec.testCanCatch_Task_catchNonFatalEitherShouldCatchNonFatal
    ),
    example(
      "test CanCatch[Task].catchNonFatalEither should not catch Fatal",
      TaskSpec.testCanCatch_Task_catchNonFatalEitherShouldNotCatchFatal
    ),
    example(
      "test CanCatch[Task].catchNonFatalEither should return the successful result",
      TaskSpec.testCanCatch_Task_catchNonFatalEitherShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[Task].catchNonFatalEither should return the failed result",
      TaskSpec.testCanCatch_Task_catchNonFatalEitherShouldReturnFailedResult
    ),
    example(
      "test CanCatch[Task].catchNonFatalEitherT should catch NonFatal",
      TaskSpec.testCanCatch_Task_catchNonFatalEitherTShouldCatchNonFatal
    ),
    example(
      "test CanCatch[Task].catchNonFatalEitherT should not catch Fatal",
      TaskSpec.testCanCatch_Task_catchNonFatalEitherTShouldNotCatchFatal
    ),
    example(
      "test CanCatch[Task].catchNonFatalEitherT should return the successful result",
      TaskSpec.testCanCatch_Task_catchNonFatalEitherTShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[Task].catchNonFatalEitherT should return the failed result",
      TaskSpec.testCanCatch_Task_catchNonFatalEitherTShouldReturnFailedResult
    ),

    /* Future */
    example(
      "test CanCatch[Future].catchNonFatalThrowable should catch NonFatal Throwable",
      FutureSpec.testCanCatch_Future_catchNonFatalShouldCatchNonFatalThrowable
    ),
    example(
      "test CanCatch[Future].catchNonFatal should catch NonFatal",
      FutureSpec.testCanCatch_Future_catchNonFatalShouldCatchNonFatal
    ),
    example(
      "test CanCatch[Future].catchNonFatal should return the successful result",
      FutureSpec.testCanCatch_Future_catchNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[Future].catchNonFatalEither should catch NonFatal",
      FutureSpec.testCanCatch_Future_catchNonFatalEitherShouldCatchNonFatal
    ),
    example(
      "test CanCatch[Future].catchNonFatalEither should return the successful result",
      FutureSpec.testCanCatch_Future_catchNonFatalEitherShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[Future].catchNonFatalEither should return the failed result",
      FutureSpec.testCanCatch_Future_catchNonFatalEitherShouldReturnFailedResult
    ),
    example(
      "test CanCatch[Future].catchNonFatalEitherT should catch NonFatal",
      FutureSpec.testCanCatch_Future_catchNonFatalEitherTShouldCatchNonFatal
    ),
    example(
      "test CanCatch[Future].catchNonFatalEitherT should return the successful result",
      FutureSpec.testCanCatch_Future_catchNonFatalEitherTShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[Future].catchNonFatalEitherT should return the failed result",
      FutureSpec.testCanCatch_Future_catchNonFatalEitherTShouldReturnFailedResult
    ),

    /* Id */
    example(
      "test CanCatch[Id].catchNonFatalThrowable should catch NonFatal Throwable",
      IdSpec.testCanCatch_Id_catchNonFatalShouldCatchNonFatalThrowable
    ),
    example(
      "test CanCatch[Id].catchNonFatal should catch NonFatal",
      IdSpec.testCanCatch_Id_catchNonFatalShouldCatchNonFatal
    ),
    example(
      "test CanCatch[Id].catchNonFatal should not catch Fatal",
      IdSpec.testCanCatch_Id_catchNonFatalShouldNotCatchFatal
    ),
    example(
      "test CanCatch[Id].catchNonFatal should return the successful result",
      IdSpec.testCanCatch_Id_catchNonFatalShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[Id].catchNonFatalEither should catch NonFatal",
      IdSpec.testCanCatch_Id_catchNonFatalEitherShouldCatchNonFatal
    ),
    example(
      "test CanCatch[Id].catchNonFatalEither should not catch Fatal",
      IdSpec.testCanCatch_Id_catchNonFatalEitherShouldNotCatchFatal
    ),
    example(
      "test CanCatch[Id].catchNonFatalEither should return the successful result",
      IdSpec.testCanCatch_Id_catchNonFatalEitherShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[Id].catchNonFatalEither should return the failed result",
      IdSpec.testCanCatch_Id_catchNonFatalEitherShouldReturnFailedResult
    ),
    example(
      "test CanCatch[Id].catchNonFatalEitherT should catch NonFatal",
      IdSpec.testCanCatch_Id_catchNonFatalEitherTShouldCatchNonFatal
    ),
    example(
      "test CanCatch[Id].catchNonFatalEitherT should not catch Fatal",
      IdSpec.testCanCatch_Id_catchNonFatalEitherTShouldNotCatchFatal
    ),
    example(
      "test CanCatch[Id].catchNonFatalEitherT should return the successful result",
      IdSpec.testCanCatch_Id_catchNonFatalEitherTShouldReturnSuccessfulResult
    ),
    example(
      "test CanCatch[Id].catchNonFatalEitherT should return the failed result",
      IdSpec.testCanCatch_Id_catchNonFatalEitherTShouldReturnFailedResult
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

  object TaskSpec {
    import monix.execution.Scheduler.Implicits.global

    def testCanCatch_Task_catchNonFatalShouldCatchNonFatalThrowable: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = expectedExpcetion.asLeft[Int]
      val actual            = CanCatch[Task].catchNonFatalThrowable(fa).runSyncUnsafe()

      actual ==== expected
    }

    def testCanCatch_Task_catchNonFatalShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Task, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = CanCatch[Task].catchNonFatal(fa)(SomeError.someThrowable).runSyncUnsafe()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanCatch_Task_catchNonFatalShouldNotCatchFatal: Result = {

      val fatalExpcetion = new SomeControlThrowable("Something's wrong")
      val fa             = run[Task, Int](throwThrowable[Int](fatalExpcetion))

      try {
        val actual = CanCatch[Task].catchNonFatal(fa)(SomeError.someThrowable).runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_Task_catchNonFatalShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Int](1)
      val expected = 1.asRight[SomeError]
      val actual   = CanCatch[Task].catchNonFatal(fa)(SomeError.someThrowable).runSyncUnsafe()

      actual ==== expected
    }

    def testCanCatch_Task_catchNonFatalEitherShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = CanCatch[Task].catchNonFatalEither(fa)(SomeError.someThrowable).runSyncUnsafe()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanCatch_Task_catchNonFatalEitherShouldNotCatchFatal: Result = {

      val fatalExpcetion = new SomeControlThrowable("Something's wrong")
      val fa             = run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

      try {
        val actual = CanCatch[Task].catchNonFatalEither(fa)(SomeError.someThrowable).runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_Task_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

      val fa       = run[Task, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanCatch[Task].catchNonFatalEither(fa)(SomeError.someThrowable).runSyncUnsafe()

      actual ==== expected
    }

    def testCanCatch_Task_catchNonFatalEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanCatch[Task].catchNonFatalEither(fa)(SomeError.someThrowable).runSyncUnsafe()

      actual ==== expected
    }

    def testCanCatch_Task_catchNonFatalEitherTShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = CanCatch[Task].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.runSyncUnsafe()

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanCatch_Task_catchNonFatalEitherTShouldNotCatchFatal: Result = {

      val fatalExpcetion = new SomeControlThrowable("Something's wrong")
      val fa             = EitherT(run[Task, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

      try {
        val actual = CanCatch[Task].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.runSyncUnsafe()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_Task_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = {

      val fa       = EitherT(run[Task, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = CanCatch[Task].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.runSyncUnsafe()

      actual ==== expected
    }

    def testCanCatch_Task_catchNonFatalEitherTShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Task, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanCatch[Task].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.runSyncUnsafe()

      actual ==== expected
    }

  }

  object FutureSpec {
    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration._
    import scala.concurrent.{ExecutionContext, Future}

    val waitFor: FiniteDuration = 1.second

    def testCanCatch_Future_catchNonFatalShouldCatchNonFatalThrowable: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = expectedExpcetion.asLeft[Int]
      val actual            = ConcurrentSupport.futureToValueAndTerminate(
        CanCatch[Future].catchNonFatalThrowable(fa),
        waitFor
      )

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalShouldCatchNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = ConcurrentSupport.futureToValueAndTerminate(
        CanCatch[Future].catchNonFatal(fa)(SomeError.someThrowable),
        waitFor
      )

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val fa       = run[Future, Int](1)
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        CanCatch[Future].catchNonFatal(fa)(SomeError.someThrowable),
        waitFor
      )

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalEitherShouldCatchNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = ConcurrentSupport.futureToValueAndTerminate(
        CanCatch[Future].catchNonFatalEither(fa)(SomeError.someThrowable),
        waitFor
      )

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        CanCatch[Future].catchNonFatalEither(fa)(SomeError.someThrowable),
        waitFor
      )

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalEitherShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = ConcurrentSupport.futureToValueAndTerminate(
        CanCatch[Future].catchNonFatalEither(fa)(SomeError.someThrowable),
        waitFor
      )

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalEitherTShouldCatchNonFatal: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = ConcurrentSupport.futureToValueAndTerminate(
        CanCatch[Future].catchNonFatalEitherT(fa)(SomeError.someThrowable).value,
        waitFor
      )

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = ConcurrentSupport.futureToValueAndTerminate(
        CanCatch[Future].catchNonFatalEitherT(fa)(SomeError.someThrowable).value,
        waitFor
      )

      actual ==== expected
    }

    def testCanCatch_Future_catchNonFatalEitherTShouldReturnFailedResult: Result = {

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          = ConcurrentSupport.futureToValueAndTerminate(
        CanCatch[Future].catchNonFatalEitherT(fa)(SomeError.someThrowable).value,
        waitFor
      )

      actual ==== expected
    }
  }

  object IdSpec {

    def testCanCatch_Id_catchNonFatalShouldCatchNonFatalThrowable: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = expectedExpcetion.asLeft[Int]
      val actual            = CanCatch[Id].catchNonFatalThrowable(fa)

      actual ==== expected
    }

    def testCanCatch_Id_catchNonFatalShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = CanCatch[Id].catchNonFatal(fa)(SomeError.someThrowable)

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanCatch_Id_catchNonFatalShouldNotCatchFatal: Result = {

      val fatalExpcetion = new SomeControlThrowable("Something's wrong")
      lazy val fa        = run[Id, Int](throwThrowable[Int](fatalExpcetion))

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

      val fa       = run[Id, Int](1)
      val expected = 1.asRight[SomeError]
      val actual   = CanCatch[Id].catchNonFatal(fa)(SomeError.someThrowable)

      actual ==== expected
    }

    def testCanCatch_Id_catchNonFatalEitherShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa           = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = CanCatch[Id].catchNonFatalEither(fa)(SomeError.someThrowable)

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanCatch_Id_catchNonFatalEitherShouldNotCatchFatal: Result = {

      val fatalExpcetion = new SomeControlThrowable("Something's wrong")
      lazy val fa        = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

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

      val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
      val expected = 1.asRight[SomeError]
      val actual   = CanCatch[Id].catchNonFatalEither(fa)(SomeError.someThrowable)

      actual ==== expected
    }

    def testCanCatch_Id_catchNonFatalEitherShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanCatch[Id].catchNonFatalEither(fa)(SomeError.someThrowable)

      actual ==== expected
    }

    def testCanCatch_Id_catchNonFatalEitherTShouldCatchNonFatal: Result = {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      lazy val fa           = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
      val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
      val actual            = CanCatch[Id].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testCanCatch_Id_catchNonFatalEitherTShouldNotCatchFatal: Result = {

      val fatalExpcetion = new SomeControlThrowable("Something's wrong")
      lazy val fa        = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

      try {
        val actual = CanCatch[Id].catchNonFatalEitherT(fa)(SomeError.someThrowable).value
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          ex ==== fatalExpcetion

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testCanCatch_Id_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = {

      val fa       = EitherT(run[Id, Either[SomeError, Int]](1.asRight[SomeError]))
      val expected = 1.asRight[SomeError]
      val actual   = CanCatch[Id].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

      actual ==== expected
    }

    def testCanCatch_Id_catchNonFatalEitherTShouldReturnFailedResult: Result = {

      val expectedFailure = SomeError.message("Failed")
      val fa              = EitherT(run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
      val expected        = expectedFailure.asLeft[Int]
      val actual          = CanCatch[Id].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

      actual ==== expected
    }

  }

}
