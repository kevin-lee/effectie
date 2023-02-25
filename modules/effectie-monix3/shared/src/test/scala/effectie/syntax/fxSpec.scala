package effectie.syntax

import cats._
import cats.syntax.all._
import effectie.core.{Fx, FxCtor}
import effectie.instances.monix3.fx._
import effectie.syntax.fx._
import effectie.testing.tools.{dropResult, expectThrowable}
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
  private implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = List(
    property("test fx.{effectOf, pureOf, unitOf} for Task", TaskSpec.testAll),
    property("test fx.effectOf[Task]", TaskSpec.testEffectOf),
    property("test fx.pureOf[Task]", TaskSpec.testPureOf),
    example("test fx.unitOf[Task]", TaskSpec.testUnitOf),
    example("test fx.errorOf[Task]", TaskSpec.testErrorOf),
    property("test fx.pureOfOption[Task]", TaskSpec.testPureOfOption),
    property("test fx.pureOfSome[Task]", TaskSpec.testPureOfSome),
    example("test fx.pureOfNone[Task]", TaskSpec.testPureOfNone),
    property("test fx.pureOfRight[Task]", TaskSpec.testPureOfRight),
    property("test fx.pureOfLeft[Task]", TaskSpec.testPureOfLeft),
    //
    property("test fx.{effectOf, pureOf, unitOf} for IO", TaskSpec.testAll),
    property("test fx.effectOf[IO]", IoSpec.testEffectOf),
    property("test fx.pureOf[IO]", IoSpec.testPureOf),
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
    def errOf[A](throwable: Throwable): F[A]
  }
  object FxCtorClient {
    def apply[F[*]: FxCtorClient]: FxCtorClient[F]         = implicitly[FxCtorClient[F]]
    implicit def eftClientF[F[*]: FxCtor]: FxCtorClient[F] = new FxCtorClientF[F]
    final class FxCtorClientF[F[*]: FxCtor] extends FxCtorClient[F] {
      override def eftOf[A](a: A): F[A]                 = effectOf[F](a)
      override def of[A](a: A): F[A]                    = pureOf[F](a)
      override def unit: F[Unit]                        = unitOf[F]
      override def errOf[A](throwable: Throwable): F[A] = errorOf[F](throwable)
    }
  }

  trait FxClient[F[*]] {
    def eftOf[A](a: A): F[A]
    def of[A](a: A): F[A]
    def unit: F[Unit]
    def errOf[A](throwable: Throwable): F[A]
  }
  object FxClient {
    def apply[F[*]: FxClient]: FxClient[F]         =
      implicitly[FxClient[F]]
    implicit def eftClientF[F[*]: Fx]: FxClient[F] = new FxClientF[F]
    final class FxClientF[F[*]: Fx] extends FxClient[F] {
      override def eftOf[A](a: A): F[A]                 = effectOf[F](a)
      override def of[A](a: A): F[A]                    = pureOf[F](a)
      override def unit: F[Unit]                        = unitOf[F]
      override def errOf[A](throwable: Throwable): F[A] = errorOf[F](throwable)
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
          i  <- effectConstructorClient.eftOf(1)
          i2 <- effectConstructorClient.of(1)
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

    def testUnitOf: Result = {
      val io             = unitOf[Task]
      val expected: Unit = ()
      val actual: Unit   = io.runSyncUnsafe()
      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.Any", "org.wartremover.warts.Nothing"))
    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      val io = errorOf[Task][Unit](expectedError)
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

        val io = input.pureOfOption[Task]
        (for {
          actual <- io
        } yield {
          Result.all(
            List(
              actual ==== expected
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

        val io = s.pureOfSome[Task]
        (for {
          actual <- io
        } yield Result.all(
          List(
            actual ==== expected
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

        val io = n.pureOfRight[Task, String]
        (for {
          actual <- io
        } yield {
          Result.all(
            List(
              actual ==== expected
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

        val io = s.pureOfLeft[Task, Int]
        (for {
          actual <- io
        } yield {
          Result.all(
            List(
              actual ==== expected
            )
          )
        })
          .runSyncUnsafe()
      }

  }

  object IoSpec {
    import cats.effect.IO
    import effectie.instances.ce2.fx.ioFx

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
          testBefore2.log("testBefore2"),
          testBeforeRun.log("testBeforeRun"),
          testBeforeRun2.log("testBeforeRun2"),
          testAfterRun.log("testAfterRun"),
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

    def testPureOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
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

    def testUnitOf: Result = {
      val io             = unitOf[IO]
      val expected: Unit = ()
      val actual: Unit   = io.unsafeRunSync()
      actual ==== expected
    }

    @SuppressWarnings(Array("org.wartremover.warts.Any", "org.wartremover.warts.Nothing"))
    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      val io = errorOf[IO][Unit](expectedError)
      expectThrowable(io.unsafeRunSync(), expectedError)
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

        val io = input.pureOfOption[IO]
        (for {
          actual <- io
        } yield {
          Result.all(
            List(
              actual ==== expected
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

        val io = s.pureOfSome[IO]
        (for {
          actual <- io
        } yield Result.all(
          List(
            actual ==== expected
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

        val io = n.pureOfRight[IO, String]
        (for {
          actual <- io
        } yield {
          Result.all(
            List(
              actual ==== expected
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

        val io = s.pureOfLeft[IO, Int]
        (for {
          actual <- io
        } yield {
          Result.all(
            List(
              actual ==== expected
            )
          )
        })
          .unsafeRunSync()
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
        val actual   = ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)

        Result.all(
          List(
            actual ==== expected
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
      val actual   = ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)

      Result.all(
        List(
          actual ==== expected
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

        val future = n.pureOfRight[Future, String]
        val actual = ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)

        Result.all(
          List(
            actual ==== expected
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

        val future = s.pureOfLeft[Future, Int]
        val actual = ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)

        Result.all(
          List(
            actual ==== expected
          )
        )
      }

  }

  object IdSpec {
    import effectie.instances.id.fx._

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
      eftClient.unit
      effectConstructorClient.unit
      val testAfter  = actual ==== after
      val testAfter2 = actual2 ==== after
      testBefore.log("testBefore") ==== testAfter.log("testAfter") and
        testBefore2.log("testBefore2") ==== testAfter2.log("testAfter2")
    }

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
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
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
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

        val actual = input.pureOfOption[Id]
        Result.all(
          List(
            actual ==== expected
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

        val actual = s.pureOfSome[Id]
        Result.all(
          List(
            actual ==== expected
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

        val actual = n.pureOfRight[Id, String]
        Result.all(
          List(
            actual ==== expected
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

        val actual = s.pureOfLeft[Id, Int]
        Result.all(
          List(
            actual ==== expected
          )
        )
      }

  }

}
