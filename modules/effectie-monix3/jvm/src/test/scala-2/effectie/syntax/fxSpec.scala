package effectie.syntax

import cats.syntax.all._
import effectie.core.{Fx, FxCtor}
import effectie.instances.monix3.fx._
import effectie.syntax.fx._
import effectie.testing.tools.expectThrowable
import effectie.testing.types.SomeThrowableError
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog._
import hedgehog.runner._

import monix.eval.Task

/** @author Kevin Lee
  * @since 2021-05-16
  */
object fxSpec extends Properties {
  implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = List(
    property("test fx.{effectOf, pureOf, unitOf} for Task", TaskSpec.testAll),
    property("test fx.effectOf[Task]", TaskSpec.testEffectOf),
    property("test fx.fromEffect[Task](Task[A])", TaskSpec.testFromEffect),
    property("test fx.fromEffect[Task](Task.pure[A])", TaskSpec.testFromEffectWithPure),
    property("test fx.pureOf[Task]", TaskSpec.testPureOf),
    property("test fx.pureOrError[Task](success case)", TaskSpec.testPureOrErrorSuccessCase),
    example("test fx.pureOrError[Task](error case)", TaskSpec.testPureOrErrorErrorCase),
    example("test fx.unitOf[Task]", TaskSpec.testUnitOf),
    example("test fx.errorOf[Task]", TaskSpec.testErrorOf),
    property("test fx.pureOfOption[Task]", TaskSpec.testPureOfOption),
    property("test fx.pureOfSome[Task]", TaskSpec.testPureOfSome),
    example("test fx.pureOfNone[Task]", TaskSpec.testPureOfNone),
    property("test fx.pureOfRight[Task]", TaskSpec.testPureOfRight),
    property("test fx.pureOfLeft[Task]", TaskSpec.testPureOfLeft),
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
  )

  trait FxCtorClient[F[*]] {
    def eftOf[A](a: A): F[A]
    def of[A](a: A): F[A]
    def unit: F[Unit]
  }
  object FxCtorClient {
    def apply[F[*]: FxCtorClient]: FxCtorClient[F]         = implicitly[FxCtorClient[F]]
    implicit def eftClientF[F[*]: FxCtor]: FxCtorClient[F] = new FxCtorClientF[F]
    final class FxCtorClientF[F[*]: FxCtor] extends FxCtorClient[F] {
      override def eftOf[A](a: A): F[A] = effectOf[F](a)
      override def of[A](a: A): F[A]    = pureOf[F](a)
      override def unit: F[Unit]        = unitOf[F]
    }
  }

  trait FxClient[F[*]] {
    def eftOf[A](a: A): F[A]
    def of[A](a: A): F[A]
    def unit: F[Unit]
  }
  object FxClient {
    def apply[F[*]: FxClient]: FxClient[F]         =
      implicitly[FxClient[F]]
    implicit def eftClientF[F[*]: Fx]: FxClient[F] = new FxClientF[F]
    final class FxClientF[F[*]: Fx] extends FxClient[F] {
      override def eftOf[A](a: A): F[A] = effectOf[F](a)
      override def of[A](a: A): F[A]    = pureOf[F](a)
      override def unit: F[Unit]        = unitOf[F]
    }
  }

  object TaskSpec {
    import monix.execution.Scheduler.Implicits.global

    @SuppressWarnings(Array("org.wartremover.warts.Any", "org.wartremover.warts.Nothing"))
    def testAll: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual                  = before // scalafix:ok DisableSyntax.var
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual2                 = before // scalafix:ok DisableSyntax.var
      val testBefore              = actual ==== before
      val testBefore2             = actual2 ==== before
      val eftClient               = FxCtorClient[Task]
      val effectConstructorClient = FxClient[Task]
      val io                      =
        for {
          _  <- effectOf[Task]({ actual = after; () })
          _  <- pureOf[Task]({ actual2 = after; () })
          n  <- eftClient.eftOf(1)
          n2 <- eftClient.of(n)
          i  <- effectConstructorClient.eftOf(n2)
          _  <- effectConstructorClient.of(i)
          _  <- eftClient.unit
          _  <- effectConstructorClient.unit
        } yield ()
      val testBeforeRun           = actual ==== before
      val testBeforeRun2          = actual2 ==== before
      io.runSyncUnsafe()
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

    @SuppressWarnings(Array("org.wartremover.warts.Any", "org.wartremover.warts.Nothing"))
    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual        = before // scalafix:ok DisableSyntax.var
      val testBefore    = actual ==== before
      val io            = effectOf[Task]({ actual = after; () })
      val testBeforeRun = actual ==== before
      io.runSyncUnsafe()
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
      var actual        = before // scalafix:ok DisableSyntax.var
      val testBefore    = (actual ==== before).log("before effectOf")
      val io            = effectOf({ actual = after; () })
      val testBeforeRun = (actual ==== before).log("after effectOf but before fromEffect")
      val fromIo        = fromEffect(io)

      val testAfterFromEffect = (actual ==== before).log("after fromEffect but before run")

      fromIo.runSyncUnsafe()
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
      val fromPure   = fromEffect(pureOf({ actual = after; () }))

      val testAfterFromEffect = (actual ==== before).log("after fromEffect but before run")
      fromPure.runSyncUnsafe()

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
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual        = before // scalafix:ok DisableSyntax.var
      val testBefore    = actual ==== before
      val io            = pureOf[Task]({ actual = after; () })
      val testBeforeRun = actual ==== after
      io.runSyncUnsafe()
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
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual        = before // scalafix:ok DisableSyntax.var
      val testBefore    = actual ==== before
      val io            = pureOrError[Task]({ actual = after; () })
      val testBeforeRun = actual ==== after
      io.runSyncUnsafe()
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

      val io = pureOrError[Task](throw expectedError) // scalafix:ok DisableSyntax.throw
      expectThrowable(io.runSyncUnsafe(), expectedError)
    }

    def testUnitOf: Result = {
      val io             = unitOf[Task]
      val expected: Unit = ()
      val actual: Unit   = io.runSyncUnsafe()
      actual ==== expected
    }

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      val io = errorOf[Task](expectedError)
      expectThrowable(io.runSyncUnsafe(), expectedError)
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

        val io  = input.pureOfOption[Task]
        val io2 = pureOfOption[Task](input)
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
          .runSyncUnsafe()
      }

    def testPureOfSome: Property =
      for {
        s <- Gen
               .string(Gen.unicode, Range.linear(1, 10))
               .log("s")
      } yield {
        val expected = s.some

        val io  = s.pureOfSome[Task]
        val io2 = pureOfSome[Task](s)
        (for {
          actual  <- io
          actual2 <- io2
        } yield Result.all(
          List(
            actual ==== expected,
            actual2 ==== expected,
          )
        ))
          .runSyncUnsafe()
      }

    def testPureOfNone: Result = {
      val expected = none[String]

      val io = pureOfNone[Task, String]
      (for {
        actual <- io
      } yield actual ==== expected)
        .runSyncUnsafe()
    }

    def testPureOfRight: Property =
      for {
        n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
      } yield {
        val expected = n.asRight[String]

        val io  = n.pureOfRight[Task, String]
        val io2 = pureOfRight[Task, String](n)
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
          .runSyncUnsafe()
      }

    def testPureOfLeft: Property =
      for {
        s <- Gen
               .string(Gen.unicode, Range.linear(1, 10))
               .log("s")
      } yield {
        val expected = s.asLeft[Int]

        val io  = s.pureOfLeft[Task, Int]
        val io2 = pureOfLeft[Task, Int](s)
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
          .runSyncUnsafe()
      }

  }

  object FutureSpec {

    import effectie.instances.future.fx._

    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration._
    import scala.concurrent.{ExecutionContext, Future}

    val waitFor = WaitFor(1.second)

    def testAll: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual                  = before // scalafix:ok DisableSyntax.var
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
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
          i  <- effectConstructorClient.eftOf(n2)
          _  <- effectConstructorClient.of(i)
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
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
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
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
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
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
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

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val future = pureOrError[Future][Unit](throw expectedError) // scalafix:ok DisableSyntax.throw
      expectThrowable(ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future), expectedError)
    }

    def testUnitOf: Result = {
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)
      val future                                    = unitOf[Future]
      val expected: Unit                            = ()
      val actual: Unit = ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)
      actual ==== expected
    }

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val future = errorOf[Future][Unit](expectedError)
      expectThrowable(ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future), expectedError)
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
        val future2  = pureOfOption[Future](input)
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
      val future2  = pureOfSome[Future](s)
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
        val future2 = pureOfRight[Future, String](n)
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
        val future2 = pureOfLeft[Future, Int](s)
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

}
