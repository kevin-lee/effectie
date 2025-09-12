package effectie.syntax

import cats.Id
import cats.syntax.all.*
import cats.effect.IO
import effectie.instances.ce2.fx.given
import effectie.syntax.fx.*
import effectie.testing.tools.*
import effectie.testing.types.SomeThrowableError
import effectie.core.{Fx, FxCtor}
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog.*
import hedgehog.runner.*

/** @author Kevin Lee
  * @since 2021-05-16
  */
object fxSpec extends Properties {
  override def tests: List[Test] = List(
    property("test fx.{effectOf, pureOf, unitOf} for IO", IoSpec.testAll),
    property("test fx.effectOf[IO]", IoSpec.testEffectOf),
    property("test fx.fromEffect[IO](IO[A])", IoSpec.testFromEffect),
    property("test fx.fromEffect[IO](IO.pure[A])", IoSpec.testFromEffectWithPure),
    property("test fx.pureOf[IO]", IoSpec.testPureOf),
    property("test fx.pureOrError[IO](success case)", IoSpec.testPureOrErrorSuccessCase),
    example("test fx.pureOrError[IO](error case)", IoSpec.testPureOrErrorErrorCase),
    example("test fx.unitOf[IO]", IoSpec.testUnitOf),
    example("test fx.errorOf[IO]", IoSpec.testErrorOf),
    property("test fx.pureOfOption[IO]", IoSpec.testPureOfOption),
    property("test fx.pureOfSome[IO]", IoSpec.testPureOfSome),
    example("test fx.pureOfNone[IO]", IoSpec.testPureOfNone),
    property("test fx.pureOfRight[IO]", IoSpec.testPureOfRight),
    property("test fx.pureOfLeft[IO]", IoSpec.testPureOfLeft),
    //
    property("test fx.{effectOf, pureOf, unitOf} for Future", FutureSpec.testAll),
    property("test fx.effectOf[Future]", FutureSpec.testEffectOf),
    property("test fx.pureOf[Future]", FutureSpec.testPureOf),
    property("test fx.pureOrError[Future](success case)", FutureSpec.testPureOrErrorSuccessCase),
    example("test fx.pureOrError[Future](error case)", FutureSpec.testPureOrErrorErrorCase),
    example("test fx.unitOf[Future]", FutureSpec.testUnitOf),
    example("test fx.errorOf[Future]", FutureSpec.testErrorOf),
    property("test fx.pureOfOption[Future]", FutureSpec.testPureOfOption),
    property("test fx.pureOfSome[Future]", FutureSpec.testPureOfSome),
    example("test fx.pureOfNone[Future]", FutureSpec.testPureOfNone),
    property("test fx.pureOfRight[Future]", FutureSpec.testPureOfRight),
    property("test fx.pureOfLeft[Future]", FutureSpec.testPureOfLeft),
    //
    property("test fx.{effectOf, pureOf, unitOf} for Id", IdSpec.testAll),
    property("test fx.effectOf[Id]", IdSpec.testEffectOf),
    property("test fx.pureOf[Id]", IdSpec.testPureOf),
    property("test fx.pureOrError[Id](success case)", IdSpec.testPureOrErrorSuccessCase),
    example("test fx.pureOrError[Id](error case)", IdSpec.testPureOrErrorErrorCase),
    example("test fx.unitOf[Id]", IdSpec.testUnitOf),
    example("test fx.errorOf[Id]", IdSpec.testErrorOf),
    property("test fx.pureOfOption[Id]", IdSpec.testPureOfOption),
    property("test fx.pureOfSome[Id]", IdSpec.testPureOfSome),
    example("test fx.pureOfNone[Id]", IdSpec.testPureOfNone),
    property("test fx.pureOfRight[Id]", IdSpec.testPureOfRight),
    property("test fx.pureOfLeft[Id]", IdSpec.testPureOfLeft),
  )

  trait FxCtorClient[F[*]] {
    def eftOf[A](a: A): F[A]
    def of[A](a: A): F[A]
    def unit: F[Unit]
  }
  object FxCtorClient {
    def apply[F[*]: FxCtorClient]: FxCtorClient[F] = summon[FxCtorClient[F]]
    given eftClientF[F[*]: FxCtor]: FxCtorClient[F] with {
      override def eftOf[A](a: A): F[A] = effectOf(a)
      override def of[A](a: A): F[A]    = pureOf(a)
      override def unit: F[Unit]        = unitOf
    }
  }

  trait FxClient[F[*]] {
    def eftOf[A](a: A): F[A]
    def of[A](a: A): F[A]
    def unit: F[Unit]
  }
  object FxClient {
    def apply[F[*]: FxClient]: FxClient[F] =
      summon[FxClient[F]]
    given eftClientF[F[*]: Fx]: FxClient[F] with {
      override def eftOf[A](a: A): F[A] = effectOf(a)
      override def of[A](a: A): F[A]    = pureOf(a)
      override def unit: F[Unit]        = unitOf
    }
  }

  object IoSpec {

    def testAll: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      var actual                  = before // scalafix:ok DisableSyntax.var
      var actual2                 = before // scalafix:ok DisableSyntax.var
      val testBefore              = actual ==== before
      val testBefore2             = actual2 ==== before
      val eftClient               = FxCtorClient[IO]
      val effectConstructorClient = FxClient[IO]
      val io                      =
        for {
          _  <- effectOf[IO]({ actual = after; () })
          _  <- pureOf[IO]({ actual2 = after; () })
          n  <- eftClient.eftOf(1)
          n2 <- eftClient.of(n)
          i  <- effectConstructorClient.eftOf(1)
          i2 <- effectConstructorClient.of(1)
          _  <- eftClient.unit
          _  <- effectConstructorClient.unit
        } yield ()
      val testBeforeRun           = actual ==== before
      val testBeforeRun2          = actual2 ==== before
      io.unsafeRunSync()
      val testAfterRun            = actual ==== after
      val testAfterRun2           = actual2 ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testBeforeRun.log("testBeforeRun"),
          testAfterRun.log("testAfterRun"),
          testBefore2.log("testBefore2"),
          testBeforeRun2.log("testBeforeRun2"),
          testAfterRun2.log("testAfterRun2"),
        )
      )
    }

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      var actual        = before // scalafix:ok DisableSyntax.var
      val testBefore    = actual ==== before
      val io            = effectOf[IO]({ actual = after; () })
      val testBeforeRun = actual ==== before
      io.unsafeRunSync()
      val testAfterRun  = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testBeforeRun.log("testBeforeRun"),
          testAfterRun.log("testAfterRun"),
        )
      )
    }

    def testFromEffect: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual     = before // scalafix:ok DisableSyntax.var
      val testBefore = (actual ==== before).log("before effectOf")
      val io         = effectOf({ actual = after; () })

      val testBeforeRun = (actual ==== before).log("after effectOf but before fromEffect")
      val fromIo        = fromEffect(io)

      val testAfterFromEffect = (actual ==== before).log("after fromEffect but before run")

      fromIo.unsafeRunSync()
      val testAfterRun = (actual ==== after).log("after fromEffect and run")
      Result.all(
        List(
          testBefore.log("testBefore"),
          testBeforeRun.log("testBeforeRun"),
          testAfterFromEffect.log("testAfterFromEffect"),
          testAfterRun.log("testAfterRun"),
        )
      )
    }

    def testFromEffectWithPure: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual     = before // scalafix:ok DisableSyntax.var
      val testBefore = (actual ==== before).log("before fromEffect")
      val fromPure   = fromEffect(pureOf({
        actual = after; ()
      }))

      val testAfterFromEffect = (actual ==== before).log("after fromEffect but before run")
      fromPure.unsafeRunSync()

      val testAfterRun = (actual ==== after).log("after fromEffect and run")
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfterFromEffect.log("testAfterFromEffect"),
          testAfterRun.log("testAfterRun"),
        )
      )
    }

    def testPureOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      var actual        = before // scalafix:ok DisableSyntax.var
      val testBefore    = actual ==== before
      val io            = pureOf[IO]({ actual = after; () })
      val testBeforeRun = actual ==== after
      io.unsafeRunSync()
      val testAfterRun  = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testBeforeRun.log("testBeforeRun"),
          testAfterRun.log("testAfterRun"),
        )
      )
    }

    def testPureOrErrorSuccessCase: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      var actual        = before // scalafix:ok DisableSyntax.var
      val testBefore    = actual ==== before
      val io            = pureOrError[IO]({ actual = after; () })
      val testBeforeRun = actual ==== after
      io.unsafeRunSync()
      val testAfterRun  = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testBeforeRun.log("testBeforeRun"),
          testAfterRun.log("testAfterRun"),
        )
      )
    }

    def testPureOrErrorErrorCase: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      val io = pureOrError[IO][Unit](throw expectedError)

      expectThrowable(
        io.unsafeRunSync(),
        expectedError,
      )
    }

    def testUnitOf: Result = {
      val io             = unitOf[IO]
      val expected: Unit = ()
      val actual: Unit   = io.unsafeRunSync()
      actual ==== expected
    }

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      val io = errorOf[IO][Unit](expectedError)

      expectThrowable(
        io.unsafeRunSync(),
        expectedError,
      )
    }

    def testPureOfOption: Property =
      for {
        s <- Gen
               .string(Gen.unicode, Range.linear(1, 10))
               .option
               .log("s")
      } yield {
        val expected = s

        val input = s.orNull

        val io  = input.pureOfOption[IO]
        val io2 = pureOfOption(input)[IO]
        (for {
          actual  <- io
          actual2 <- io2
        } yield {
          Result.all(
            List(
              actual ==== expected,
              actual2 ==== expected,
            )
          )
        })
          .unsafeRunSync()
      }

    def testPureOfSome: Property =
      for {
        s <- Gen
               .string(Gen.unicode, Range.linear(1, 10))
               .log("s")
      } yield {
        val expected = s.some

        val io  = s.pureOfSome[IO]
        val io2 = pureOfSome(s)[IO]
        (for {
          actual  <- io
          actual2 <- io2
        } yield Result.all(
          List(
            actual ==== expected,
            actual2 ==== expected,
          )
        ))
          .unsafeRunSync()
      }

    def testPureOfNone: Result = {
      val expected = none[String]

      val io = pureOfNone[IO, String]
      (for {
        actual <- io
      } yield actual ==== expected)
        .unsafeRunSync()
    }

    def testPureOfRight: Property =
      for {
        n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
      } yield {
        val expected = n.asRight[String]

        val io  = n.pureOfRight[IO, String]
        val io2 = pureOfRight(n)[IO, String]
        (for {
          actual  <- io
          actual2 <- io2
        } yield {
          Result.all(
            List(
              actual ==== expected,
              actual2 ==== expected,
            )
          )
        })
          .unsafeRunSync()
      }

    def testPureOfLeft: Property =
      for {
        s <- Gen
               .string(Gen.unicode, Range.linear(1, 10))
               .log("s")
      } yield {
        val expected = s.asLeft[Int]

        val io  = s.pureOfLeft[IO, Int]
        val io2 = pureOfLeft(s)[IO, Int]
        (for {
          actual  <- io
          actual2 <- io2
        } yield {
          Result.all(
            List(
              actual ==== expected,
              actual2 ==== expected,
            )
          )
        })
          .unsafeRunSync()
      }

  }

  object FutureSpec {

    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration.*
    import scala.concurrent.{ExecutionContext, Future}
    import effectie.instances.future.fx.*

    private implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

    private val waitFor = WaitFor(1.second)

    def testAll: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      var actual                  = before // scalafix:ok DisableSyntax.var
      var actual2                 = before // scalafix:ok DisableSyntax.var
      val testBefore              = actual ==== before
      val testBefore2             = actual2 ==== before
      val eftClient               = FxCtorClient[Future]
      val effectConstructorClient = FxClient[Future]
      val future                  =
        for {
          _  <- effectOf[Future]({ actual = after; () })
          _  <- pureOf[Future]({ actual2 = after; () })
          n  <- eftClient.eftOf(1)
          n2 <- eftClient.of(n)
          i  <- effectConstructorClient.eftOf(1)
          i2 <- effectConstructorClient.of(1)
          _  <- eftClient.unit
          _  <- effectConstructorClient.unit
        } yield ()
      ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)
      val testAfterRun            = actual ==== after
      val testAfterRun2           = actual2 ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfterRun.log("testAfterRun"),
          testBefore2.log("testBefore2"),
          testAfterRun2.log("testAfterRun2"),
        )
      )
    }

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      var actual               = before // scalafix:ok DisableSyntax.var
      val testBefore           = actual ==== before
      val future: Future[Unit] = effectOf[Future]({ actual = after; () })
      ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)
      val testAfterRun         = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfterRun.log("testAfterRun"),
        )
      )
    }

    def testPureOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      var actual       = before // scalafix:ok DisableSyntax.var
      val testBefore   = actual ==== before
      val future       = pureOf[Future]({ actual = after; () })
      ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)
      val testAfterRun = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfterRun.log("testAfterRun"),
        )
      )
    }

    def testPureOrErrorSuccessCase: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      var actual       = before // scalafix:ok DisableSyntax.var
      val testBefore   = actual ==== before
      val future       = pureOrError[Future]({ actual = after; () })
      ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)
      val testAfterRun = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfterRun.log("testAfterRun"),
        )
      )
    }

    def testPureOrErrorErrorCase: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)

      given ec: ExecutionContext =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val future = pureOrError[Future][Unit](throw expectedError)

      expectThrowable(
        ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future),
        expectedError,
      )
    }

    def testUnitOf: Result = {
      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)
      val future                             = unitOf[Future]
      val expected: Unit                     = ()
      val actual: Unit = ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)
      actual ==== expected
    }

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)
      val future                             = errorOf[Future][Unit](expectedError)

      expectThrowable(
        ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future),
        expectedError,
      )
    }

    def testPureOfOption: Property =
      for {
        s <- Gen
               .string(Gen.unicode, Range.linear(1, 10))
               .option
               .log("s")
      } yield {
        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expected = s
        val input    = s.orNull
        val future   = input.pureOfOption[Future]
        val future2  = pureOfOption(input)[Future]
        val actual   = ConcurrentSupport.futureToValue(future, waitFor)
        val actual2  = ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future2)

        Result.all(
          List(
            actual ==== expected,
            actual2 ==== expected,
          )
        )
      }

    def testPureOfSome: Property = for {
      s <- Gen
             .string(Gen.unicode, Range.linear(1, 10))
             .log("s")
    } yield {
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val expected = s.some
      val future   = s.pureOfSome[Future]
      val future2  = pureOfSome(s)[Future]
      val actual   = ConcurrentSupport.futureToValue(future, waitFor)
      val actual2  = ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future2)

      Result.all(
        List(
          actual ==== expected,
          actual2 ==== expected,
        )
      )
    }

    def testPureOfNone: Result = {
      val expected = none[String]

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val future = pureOfNone[Future, String]
      val actual = ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)

      actual ==== expected
    }

    def testPureOfRight: Property =
      for {
        n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
      } yield {
        val expected = n.asRight[String]

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val future  = n.pureOfRight[Future, String]
        val future2 = pureOfRight(n)[Future, String]
        val actual  = ConcurrentSupport.futureToValue(future, waitFor)
        val actual2 = ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future2)

        Result.all(
          List(
            actual ==== expected,
            actual2 ==== expected,
          )
        )
      }

    def testPureOfLeft: Property =
      for {
        s <- Gen
               .string(Gen.unicode, Range.linear(1, 10))
               .log("s")
      } yield {
        val expected = s.asLeft[Int]

        implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
        implicit val ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val future  = s.pureOfLeft[Future, Int]
        val future2 = pureOfLeft(s)[Future, Int]
        val actual  = ConcurrentSupport.futureToValue(future, waitFor)
        val actual2 = ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future2)

        Result.all(
          List(
            actual ==== expected,
            actual2 ==== expected,
          )
        )
      }

  }

  object IdSpec {
    import effectie.instances.id.fx.*

    def testAll: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      var actual                  = before // scalafix:ok DisableSyntax.var
      var actual2                 = before // scalafix:ok DisableSyntax.var
      val testBefore              = actual ==== before
      val testBefore2             = actual2 ==== before
      val eftClient               = FxCtorClient[Id]
      val effectConstructorClient = FxClient[Id]
      effectOf[Id]({ actual = after; () })
      pureOf[Id]({ actual2 = after; () })
      val n: Int                  = eftClient.eftOf(1)

      dropResult {
        eftClient.of(n)
      }
      dropResult {
        effectConstructorClient.eftOf(1)
      }
      dropResult {
        effectConstructorClient.of(1)
      }
      val _ = eftClient.unit
      val _ = effectConstructorClient.unit
      val testAfter  = actual ==== after
      val testAfter2 = actual2 ==== after
      testBefore.log("testBefore") ==== testAfter.log("testAfter") and
        testBefore2.log("testBefore2") ==== testAfter2.log("testAfter2")
    }

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      var actual     = before // scalafix:ok DisableSyntax.var
      val testBefore = actual ==== before
      effectOf[Id]({ actual = after; () })
      val testAfter  = actual ==== after
      testBefore.log("testBefore") ==== testAfter.log("testAfter")
    }

    def testPureOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      var actual     = before // scalafix:ok DisableSyntax.var
      val testBefore = actual ==== before
      pureOf[Id]({ actual = after; () })
      val testAfter  = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfter.log("testAfter"),
        )
      )
    }

    def testPureOrErrorSuccessCase: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      var actual     = before // scalafix:ok DisableSyntax.var
      val testBefore = actual ==== before
      pureOrError[Id]({ actual = after; () })
      val testAfter  = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfter.log("testAfter"),
        )
      )
    }

    def testPureOrErrorErrorCase: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      lazy val actual = pureOrError[Id][Unit](throw expectedError)

      expectThrowable(actual, expectedError)
    }

    def testUnitOf: Result = {
      val expected: Unit = ()
      val actual         = unitOf[Id]
      actual ==== expected
    }

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      lazy val actual = errorOf[Id][Unit](expectedError)

      expectThrowable(actual, expectedError)
    }

    def testPureOfOption: Property =
      for {
        s <- Gen
               .string(Gen.unicode, Range.linear(1, 10))
               .option
               .log("s")
      } yield {
        val expected = s

        val input = s.orNull

        val actual  = input.pureOfOption[Id]
        val actual2 = pureOfOption(input)[Id]
        Result.all(
          List(
            actual ==== expected,
            actual2 ==== expected,
          )
        )
      }

    def testPureOfSome: Property =
      for {
        s <- Gen
               .string(Gen.unicode, Range.linear(1, 10))
               .log("s")
      } yield {
        val expected = s.some

        val actual  = s.pureOfSome[Id]
        val actual2 = pureOfSome(s)[Id]
        Result.all(
          List(
            actual ==== expected,
            actual2 ==== expected,
          )
        )
      }

    def testPureOfNone: Result = {
      val expected = none[String]

      val actual = pureOfNone[Id, String]
      actual ==== expected
    }

    def testPureOfRight: Property =
      for {
        n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
      } yield {
        val expected = n.asRight[String]

        val actual  = n.pureOfRight[Id, String]
        val actual2 = pureOfRight(n)[Id, String]
        Result.all(
          List(
            actual ==== expected,
            actual2 ==== expected,
          )
        )
      }

    def testPureOfLeft: Property =
      for {
        s <- Gen
               .string(Gen.unicode, Range.linear(1, 10))
               .log("s")
      } yield {
        val expected = s.asLeft[Int]

        val actual  = s.pureOfLeft[Id, Int]
        val actual2 = pureOfLeft(s)[Id, Int]
        Result.all(
          List(
            actual ==== expected,
            actual2 ==== expected,
          )
        )
      }

  }

}
