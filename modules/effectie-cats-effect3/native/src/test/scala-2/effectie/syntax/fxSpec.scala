package effectie.syntax

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import cats.syntax.all._
import effectie.core.{Fx, FxCtor}
import effectie.instances.ce3.compat.CatsEffectIoCompatForFuture
import effectie.instances.ce3.fx._
import effectie.instances.ce3.testing.IoAppUtils
import effectie.syntax.fx._
import effectie.testing.tools.expectThrowable
import effectie.testing.types.SomeThrowableError
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import extras.hedgehog.ce3.syntax.runner._
import hedgehog._
import hedgehog.runner._

/** @author Kevin Lee
  * @since 2021-05-16
  */
object fxSpec extends Properties {

  private implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = List(
    property("test fx.{effectOf, pureOf, unitOf} for IO", IoSpec.testAll),
    property("test fx.effectOf[IO]", IoSpec.testEffectOf),
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

  object IoSpec {

    val compat                 = new CatsEffectIoCompatForFuture
    implicit val rt: IORuntime = IoAppUtils.runtime(compat.es)

    @SuppressWarnings(Array("org.wartremover.warts.Any", "org.wartremover.warts.Nothing"))
    def testAll: Property =
      for {
        before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
        after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
      } yield withIO { implicit ticker =>
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
            i  <- effectConstructorClient.eftOf(n2)
            _  <- effectConstructorClient.of(i)
            _  <- eftClient.unit
            _  <- effectConstructorClient.unit
          } yield ()
        val testBeforeRun           = actual ==== before
        val testBeforeRun2          = actual2 ==== before
        val runResult               = io.completeAs(())
        val testAfterRun            = actual ==== after
        val testAfterRun2           = actual2 ==== after
        Result.all(
          List(
            testBefore.log("testBefore"),
            testBefore2.log("testBefore2"),
            testBeforeRun.log("testBeforeRun"),
            testBeforeRun2.log("testBeforeRun2"),
            runResult,
            testAfterRun.log("testAfterRun"),
            testAfterRun2.log("testAfterRun2"),
          )
        )
      }

    @SuppressWarnings(Array("org.wartremover.warts.Any", "org.wartremover.warts.Nothing"))
    def testEffectOf: Property =
      for {
        before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
        after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
      } yield withIO { implicit ticker =>
        @SuppressWarnings(Array("org.wartremover.warts.Var"))
        var actual        = before // scalafix:ok DisableSyntax.var
        val testBefore    = actual ==== before
        val io            = effectOf[IO]({ actual = after; () })
        val testBeforeRun = actual ==== before

        val runResult = io.completeAs(())

        val testAfterRun = actual ==== after
        Result.all(
          List(
            testBefore.log("testBefore"),
            testBeforeRun.log("testBeforeRun"),
            runResult,
            testAfterRun.log("testAfterRun"),
          )
        )
      }

    def testPureOf: Property =
      for {
        before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
        after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
      } yield withIO { implicit ticker =>
        @SuppressWarnings(Array("org.wartremover.warts.Var"))
        var actual        = before // scalafix:ok DisableSyntax.var
        val testBefore    = actual ==== before
        val io            = pureOf[IO]({ actual = after; () })
        val testBeforeRun = actual ==== after

        val runResult = io.completeAs(())

        val testAfterRun = actual ==== after
        Result.all(
          List(
            testBefore.log("testBefore"),
            testBeforeRun.log("testBeforeRun"),
            runResult,
            testAfterRun.log("testAfterRun"),
          )
        )
      }

    def testPureOrErrorSuccessCase: Property =
      for {
        before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
        after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
      } yield withIO { implicit ticker =>
        @SuppressWarnings(Array("org.wartremover.warts.Var"))
        var actual        = before // scalafix:ok DisableSyntax.var
        val testBefore    = actual ==== before
        val io            = pureOrError[IO]({ actual = after; () })
        val testBeforeRun = actual ==== after

        val runResult = io.completeAs(())

        val testAfterRun = actual ==== after
        Result.all(
          List(
            testBefore.log("testBefore"),
            testBeforeRun.log("testBeforeRun"),
            runResult,
            testAfterRun.log("testAfterRun"),
          )
        )
      }

    @SuppressWarnings(Array("org.wartremover.warts.Any", "org.wartremover.warts.Nothing"))
    def testPureOrErrorErrorCase: Result = withIO { implicit ticker =>
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      val io = pureOrError[IO][Unit](throw expectedError) // scalafix:ok DisableSyntax.throw

      io.expectError(expectedError)
    }

    def testUnitOf: Result = withIO { implicit ticker =>
      val io             = unitOf[IO]
      val expected: Unit = ()

      io.completeAs(expected)
    }

    @SuppressWarnings(Array("org.wartremover.warts.Any", "org.wartremover.warts.Nothing"))
    def testErrorOf: Result = withIO { implicit ticker =>
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      val io = errorOf[IO][Unit](expectedError)

      io.expectError(expectedError)
    }

    def testPureOfOption: Property =
      for {
        s <- Gen
               .string(Gen.unicode, Range.linear(1, 10))
               .option
               .log("s")
      } yield runIO {
        val expected = s

        val input = s.orNull

        val io  = input.pureOfOption[IO]
        val io2 = pureOfOption[IO](input)
        for {
          actual  <- io
          actual2 <- io2
        } yield {
          Result.all(
            List(
              actual ==== expected,
              actual2 ==== expected,
            )
          )
        }
      }

    def testPureOfSome: Property =
      for {
        s <- Gen
               .string(Gen.unicode, Range.linear(1, 10))
               .log("s")
      } yield runIO {
        val expected = s.some

        val io  = s.pureOfSome[IO]
        val io2 = pureOfSome[IO](s)
        for {
          actual  <- io
          actual2 <- io2
        } yield Result.all(
          List(
            actual ==== expected,
            actual2 ==== expected,
          )
        )

      }

    def testPureOfNone: Result = runIO {
      val expected = none[String]

      val io = pureOfNone[IO, String]
      for {
        actual <- io
      } yield actual ==== expected

    }

    def testPureOfRight: Property =
      for {
        n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
      } yield runIO {
        val expected = n.asRight[String]

        val io  = n.pureOfRight[IO, String]
        val io2 = pureOfRight[IO, String](n)
        for {
          actual  <- io
          actual2 <- io2
        } yield {
          Result.all(
            List(
              actual ==== expected,
              actual2 ==== expected,
            )
          )
        }

      }

    def testPureOfLeft: Property =
      for {
        s <- Gen
               .string(Gen.unicode, Range.linear(1, 10))
               .log("s")
      } yield runIO {
        val expected = s.asLeft[Int]

        val io  = s.pureOfLeft[IO, Int]
        val io2 = pureOfLeft[IO, Int](s)
        for {
          actual  <- io
          actual2 <- io2
        } yield {
          Result.all(
            List(
              actual ==== expected,
              actual2 ==== expected,
            )
          )
        }

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
