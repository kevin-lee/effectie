package effectie.cats

import cats.data.EitherT
import cats.effect._
import cats.syntax.all._
import cats.{Eq, Functor, Id, Monad}
import effectie.testing.tools._
import effectie.testing.types.{SomeError, SomeThrowableError}
import effectie.{ConcurrentSupport, SomeControlThrowable}
import hedgehog._
import hedgehog.runner._

import scala.concurrent.Await
import scala.util.control.ControlThrowable

/** @author Kevin Lee
  * @since 2020-12-06
  */
object FxSpec extends Properties {
  override def tests: List[Test] = List(
    property("test Fx[IO].effectOf", IoSpec.testEffectOf),
    property("test Fx[IO].pureOf", IoSpec.testPureOf),
    example("test Fx[IO].unitOf", IoSpec.testUnitOf),
    example("test Fx[IO].errorOf", IoSpec.testErrorOf),
  ) ++
    IoSpec.testMonadLaws ++
    List(
      /* IO */
      example(
        "test Fx[IO]catchNonFatalThrowable should catch NonFatal",
        IoSpec.CanCatchSpec.testFx_IO_catchNonFatalThrowableShouldCatchNonFatal
      ),
      example(
        "test Fx[IO]catchNonFatalThrowable should not catch Fatal",
        IoSpec.CanCatchSpec.testFx_IO_catchNonFatalThrowableShouldNotCatchFatal
      ),
      example(
        "test Fx[IO]catchNonFatalThrowable should return the successful result",
        IoSpec.CanCatchSpec.testFx_IO_catchNonFatalThrowableShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[IO]catchNonFatal should catch NonFatal",
        IoSpec.CanCatchSpec.testFx_IO_catchNonFatalShouldCatchNonFatal
      ),
      example(
        "test Fx[IO]catchNonFatal should not catch Fatal",
        IoSpec.CanCatchSpec.testFx_IO_catchNonFatalShouldNotCatchFatal
      ),
      example(
        "test Fx[IO]catchNonFatal should return the successful result",
        IoSpec.CanCatchSpec.testFx_IO_catchNonFatalShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[IO]catchNonFatalEither should catch NonFatal",
        IoSpec.CanCatchSpec.testFx_IO_catchNonFatalEitherShouldCatchNonFatal
      ),
      example(
        "test Fx[IO]catchNonFatalEither should not catch Fatal",
        IoSpec.CanCatchSpec.testFx_IO_catchNonFatalEitherShouldNotCatchFatal
      ),
      example(
        "test Fx[IO]catchNonFatalEither should return the successful result",
        IoSpec.CanCatchSpec.testFx_IO_catchNonFatalEitherShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[IO]catchNonFatalEither should return the failed result",
        IoSpec.CanCatchSpec.testFx_IO_catchNonFatalEitherShouldReturnFailedResult
      ),
      example(
        "test Fx[IO]catchNonFatalEitherT should catch NonFatal",
        IoSpec.CanCatchSpec.testFx_IO_catchNonFatalEitherTShouldCatchNonFatal
      ),
      example(
        "test Fx[IO]catchNonFatalEitherT should not catch Fatal",
        IoSpec.CanCatchSpec.testFx_IO_catchNonFatalEitherTShouldNotCatchFatal
      ),
      example(
        "test Fx[IO]catchNonFatalEitherT should return the successful result",
        IoSpec.CanCatchSpec.testFx_IO_catchNonFatalEitherTShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[IO]catchNonFatalEitherT should return the failed result",
        IoSpec.CanCatchSpec.testFx_IO_catchNonFatalEitherTShouldReturnFailedResult
      ),
    ) ++
    List(
      property("test Fx[Future].effectOf", FutureSpec.testEffectOf),
      property("test Fx[Future].pureOf", FutureSpec.testPureOf),
      example("test Fx[Future].unitOf", FutureSpec.testUnitOf),
      example("test Fx[Future].errorOf", FutureSpec.testErrorOf),
    ) ++
    FutureSpec.testMonadLaws ++
    List(
      /* Future */
      example(
        "test Fx[Future]catchNonFatalThrowable should catch NonFatal",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalThrowableShouldCatchNonFatal
      ),
      example(
        "test Fx[Future]catchNonFatalThrowable should return the successful result",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalThrowableShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future]catchNonFatal should catch NonFatal",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalShouldCatchNonFatal
      ),
      example(
        "test Fx[Future]catchNonFatal should return the successful result",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future]catchNonFatalEither should catch NonFatal",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalEitherShouldCatchNonFatal
      ),
      example(
        "test Fx[Future]catchNonFatalEither should return the successful result",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalEitherShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future]catchNonFatalEither should return the failed result",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalEitherShouldReturnFailedResult
      ),
      example(
        "test Fx[Future]catchNonFatalEitherT should catch NonFatal",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalEitherTShouldCatchNonFatal
      ),
      example(
        "test Fx[Future]catchNonFatalEitherT should return the successful result",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalEitherTShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future]catchNonFatalEitherT should return the failed result",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalEitherTShouldReturnFailedResult
      ),
    ) ++
    List(
      property("test Fx[Id].effectOf", IdSpec.testEffectOf),
      property("test Fx[Id].pureOf", IdSpec.testPureOf),
      example("test Fx[Id].unitOf", IdSpec.testUnitOf),
      example("test Fx[Id].errorOf", IdSpec.testErrorOf),
    ) ++
    IdSpec.testMonadLaws ++
    List(
      /* Id */
      example(
        "test Fx[Id]catchNonFatalThrowable should catch NonFatal",
        IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalThrowableShouldCatchNonFatal
      ),
      example(
        "test Fx[Id]catchNonFatalThrowable should not catch Fatal",
        IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalThrowableShouldNotCatchFatal
      ),
      example(
        "test Fx[Id]catchNonFatalThrowable should return the successful result",
        IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalThrowableShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Id]catchNonFatal should catch NonFatal",
        IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalShouldCatchNonFatal
      ),
      example(
        "test Fx[Id]catchNonFatal should not catch Fatal",
        IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalShouldNotCatchFatal
      ),
      example(
        "test Fx[Id]catchNonFatal should return the successful result",
        IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Id]catchNonFatalEither should catch NonFatal",
        IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalEitherShouldCatchNonFatal
      ),
      example(
        "test Fx[Id]catchNonFatalEither should not catch Fatal",
        IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalEitherShouldNotCatchFatal
      ),
      example(
        "test Fx[Id]catchNonFatalEither should return the successful result",
        IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalEitherShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Id]catchNonFatalEither should return the failed result",
        IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalEitherShouldReturnFailedResult
      ),
      example(
        "test Fx[Id]catchNonFatalEitherT should catch NonFatal",
        IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalEitherTShouldCatchNonFatal
      ),
      example(
        "test Fx[Id]catchNonFatalEitherT should not catch Fatal",
        IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalEitherTShouldNotCatchFatal
      ),
      example(
        "test Fx[Id]catchNonFatalEitherT should return the successful result",
        IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalEitherTShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Id]catchNonFatalEitherT should return the failed result",
        IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalEitherTShouldReturnFailedResult
      )
    )

  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable

  def run[F[_]: Fx: Functor, A](a: => A): F[A] =
    Fx[F].effectOf(a)

  object IoSpec {

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual        = before
      val testBefore    = actual ==== before
      val io            = Fx[IO].effectOf({ actual = after; () })
      val testBeforeRun = actual ==== before
      io.unsafeRunSync()
      val testAfterRun  = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testBeforeRun.log("testBeforeRun"),
          testAfterRun.log("testAfterRun")
        )
      )
    }

    def testPureOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual        = before
      val testBefore    = actual ==== before
      val io            = Fx[IO].pureOf({ actual = after; () })
      val testBeforeRun = actual ==== after
      io.unsafeRunSync()
      val testAfterRun  = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testBeforeRun.log("testBeforeRun"),
          testAfterRun.log("testAfterRun")
        )
      )
    }

    def testUnitOf: Result = {
      val io             = Fx[IO].unitOf
      val expected: Unit = ()
      val actual: Unit   = io.unsafeRunSync()
      actual ==== expected
    }

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      val io = Fx[IO].errorOf(expectedError)
      expectThrowable(io.unsafeRunSync(), expectedError)
    }

    def testMonadLaws: List[Test] = {
      import cats.syntax.eq._

      implicit val eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).unsafeRunSync()

      implicit val ioFx: Fx[IO] = Fx.IoFx

      MonadSpec.testMonadLaws[IO]("IO")
    }

    object CanCatchSpec {

      def testFx_IO_catchNonFatalThrowableShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = expectedExpcetion.asLeft[Int]
        val actual            = Fx[IO].catchNonFatalThrowable(fa).unsafeRunSync()

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testFx_IO_catchNonFatalThrowableShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))

        try {
          val actual = Fx[IO].catchNonFatalThrowable(fa).unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: SomeControlThrowable =>
            ex.getMessage ==== fatalExpcetion.getMessage

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testFx_IO_catchNonFatalThrowableShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Int](1)
        val expected = 1.asRight[Throwable]
        val actual   = Fx[IO].catchNonFatalThrowable(fa).unsafeRunSync()

        actual ==== expected
      }

      def testFx_IO_catchNonFatalShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual            = Fx[IO].catchNonFatal(fa)(SomeError.someThrowable).unsafeRunSync()

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testFx_IO_catchNonFatalShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))

        try {
          val actual = Fx[IO].catchNonFatal(fa)(SomeError.someThrowable).unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: SomeControlThrowable =>
            ex.getMessage ==== fatalExpcetion.getMessage

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testFx_IO_catchNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Int](1)
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].catchNonFatal(fa)(SomeError.someThrowable).unsafeRunSync()

        actual ==== expected
      }

      def testFx_IO_catchNonFatalEitherShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual            = Fx[IO].catchNonFatalEither(fa)(SomeError.someThrowable).unsafeRunSync()

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testFx_IO_catchNonFatalEitherShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

        try {
          val actual = Fx[IO].catchNonFatalEither(fa)(SomeError.someThrowable).unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: SomeControlThrowable =>
            ex.getMessage ==== fatalExpcetion.getMessage

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testFx_IO_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].catchNonFatalEither(fa)(SomeError.someThrowable).unsafeRunSync()

        actual ==== expected
      }

      def testFx_IO_catchNonFatalEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[IO].catchNonFatalEither(fa)(SomeError.someThrowable).unsafeRunSync()

        actual ==== expected
      }

      def testFx_IO_catchNonFatalEitherTShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
        val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual            = Fx[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.unsafeRunSync()

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testFx_IO_catchNonFatalEitherTShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

        try {
          val actual = Fx[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: SomeControlThrowable =>
            ex.getMessage ==== fatalExpcetion.getMessage

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testFx_IO_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.unsafeRunSync()

        actual ==== expected
      }

      def testFx_IO_catchNonFatalEitherTShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.unsafeRunSync()

        actual ==== expected
      }

    }
  }

  object FutureSpec {
    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration._
    import scala.concurrent.{ExecutionContext, Future}

    val waitFor: FiniteDuration = 1.second

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual               = before
      val testBefore           = actual ==== before
      val future: Future[Unit] = Fx[Future].effectOf({ actual = after; () })
      ConcurrentSupport.futureToValueAndTerminate(future, waitFor)
      val testAfterRun         = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfterRun.log("testAfterRun")
        )
      )
    }

    def testPureOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual       = before
      val testBefore   = actual ==== before
      val future       = Fx[Future].pureOf({ actual = after; () })
      ConcurrentSupport.futureToValueAndTerminate(future, waitFor)
      val testAfterRun = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfterRun.log("testAfterRun")
        )
      )
    }

    def testUnitOf: Result = {
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)
      val future                                    = Fx[Future].unitOf
      val expected: Unit                            = ()
      val actual: Unit                              = ConcurrentSupport.futureToValueAndTerminate(future, waitFor)
      actual ==== expected
    }

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val future = Fx[Future].errorOf[Unit](expectedError)
      expectThrowable(ConcurrentSupport.futureToValueAndTerminate(future, waitFor), expectedError)
    }

    def testMonadLaws: List[Test] = {
      import cats.syntax.eq._

      implicit val ec: scala.concurrent.ExecutionContext             = scala.concurrent.ExecutionContext.global
      implicit def futureEqual[A](implicit EQ: Eq[A]): Eq[Future[A]] = new Eq[Future[A]] {
        override def eqv(x: Future[A], y: Future[A]): Boolean =
          Await.result(x.flatMap(a => y.map(b => EQ.eqv(a, b))), 1.second)
      }
      implicit val eqFuture: Eq[Future[Int]]                         =
        (x, y) => {
          val future = x.flatMap(xx => y.map(_ === xx))
          Await.result(future, waitFor)
        }

      MonadSpec.testMonadLaws[Future]("Future")
    }

    object CanCatchSpec {
      import java.util.concurrent.{ExecutorService, Executors}
      import scala.concurrent.duration._
      import scala.concurrent.{ExecutionContext, Future}

      val waitFor: FiniteDuration = 1.second

      def testCanCatch_Future_catchNonFatalThrowableShouldCatchNonFatal: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = expectedExpcetion.asLeft[Int]
        val actual            = ConcurrentSupport.futureToValueAndTerminate(
          Fx[Future].catchNonFatalThrowable(fa),
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
          Fx[Future].catchNonFatal(fa)(SomeError.someThrowable),
          waitFor
        )

        actual ==== expected
      }

      def testCanCatch_Future_catchNonFatalThrowableShouldReturnSuccessfulResult: Result = {

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val fa       = run[Future, Int](1)
        val expected = 1.asRight[Throwable]
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          Fx[Future].catchNonFatalThrowable(fa),
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
          Fx[Future].catchNonFatal(fa)(SomeError.someThrowable),
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
          Fx[Future].catchNonFatalEither(fa)(SomeError.someThrowable),
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
          Fx[Future].catchNonFatalEither(fa)(SomeError.someThrowable),
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
          Fx[Future].catchNonFatalEither(fa)(SomeError.someThrowable),
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
          Fx[Future].catchNonFatalEitherT(fa)(SomeError.someThrowable).value,
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
          Fx[Future].catchNonFatalEitherT(fa)(SomeError.someThrowable).value,
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
          Fx[Future].catchNonFatalEitherT(fa)(SomeError.someThrowable).value,
          waitFor
        )

        actual ==== expected
      }
    }

  }

  object IdSpec {

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual     = before
      val testBefore = actual ==== before
      Fx[Id].effectOf({ actual = after; () })
      val testAfter  = actual ==== after
      testBefore.log("testBefore") ==== testAfter.log("testAfter")
    }

    def testPureOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual     = before
      val testBefore = actual ==== before
      Fx[Id].pureOf({ actual = after; () })
      val testAfter  = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfter.log("testAfter")
        )
      )
    }

    def testUnitOf: Result = {
      val expected: Unit = ()
      val actual         = Fx[Id].unitOf
      actual ==== expected
    }

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      lazy val actual = Fx[Id].errorOf[Unit](expectedError)
      expectThrowable(actual, expectedError)
    }

    def testMonadLaws: List[Test] = {
      val idInstance: Monad[Id] = cats.catsInstancesForId
      MonadSpec.testMonadLaws[Id]("Id")
    }

    object CanCatchSpec {

      def testCanCatch_Id_catchNonFatalThrowableShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = expectedExpcetion.asLeft[Int]
        val actual            = Fx[Id].catchNonFatalThrowable(fa)

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanCatch_Id_catchNonFatalThrowableShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa        = run[Id, Int](throwThrowable[Int](fatalExpcetion))

        try {
          val actual = Fx[Id].catchNonFatalThrowable(fa)
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanCatch_Id_catchNonFatalThrowableShouldReturnSuccessfulResult: Result = {

        val fa       = run[Id, Int](1)
        val expected = 1.asRight[Throwable]
        val actual   = Fx[Id].catchNonFatalThrowable(fa)

        actual ==== expected
      }

      def testCanCatch_Id_catchNonFatalShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual            = Fx[Id].catchNonFatal(fa)(SomeError.someThrowable)

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanCatch_Id_catchNonFatalShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa        = run[Id, Int](throwThrowable[Int](fatalExpcetion))

        try {
          val actual = Fx[Id].catchNonFatal(fa)(SomeError.someThrowable)
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
        val actual   = Fx[Id].catchNonFatal(fa)(SomeError.someThrowable)

        actual ==== expected
      }

      def testCanCatch_Id_catchNonFatalEitherShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa           = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual            = Fx[Id].catchNonFatalEither(fa)(SomeError.someThrowable)

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanCatch_Id_catchNonFatalEitherShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa        = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

        try {
          val actual = Fx[Id].catchNonFatalEither(fa)(SomeError.someThrowable)
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
        val actual   = Fx[Id].catchNonFatalEither(fa)(SomeError.someThrowable)

        actual ==== expected
      }

      def testCanCatch_Id_catchNonFatalEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Id].catchNonFatalEither(fa)(SomeError.someThrowable)

        actual ==== expected
      }

      def testCanCatch_Id_catchNonFatalEitherTShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa           = EitherT(
          run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        )
        val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual            = Fx[Id].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

        actual ==== expected
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanCatch_Id_catchNonFatalEitherTShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa        = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

        try {
          val actual = Fx[Id].catchNonFatalEitherT(fa)(SomeError.someThrowable).value
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
        val actual   = Fx[Id].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

        actual ==== expected
      }

      def testCanCatch_Id_catchNonFatalEitherTShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Id].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

        actual ==== expected
      }

    }

  }

}
