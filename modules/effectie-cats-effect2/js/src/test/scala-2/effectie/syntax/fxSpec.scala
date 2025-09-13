package effectie.syntax

import cats.syntax.all._
import effectie.core.{Fx, FxCtor}
import effectie.instances.ce2.fx._
import effectie.syntax.fx._
import effectie.testing.types.SomeThrowableError
import effectie.testing.{FutureTools, RandomGens}
import munit.Assertions

import scala.concurrent._
import scala.concurrent.duration._
import scala.util.control.NonFatal

/** @author Kevin Lee
  * @since 2021-05-16
  */
class fxSpec extends munit.FunSuite with FutureTools with CommonFxSpec with fxSpec.fxIoSpec with fxSpec.fxFutureSpec
trait CommonFxSpec {
  self: munit.FunSuite with FutureTools =>

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

  def expectThrowableMUnit[F[*]: cats.MonadThrow, A](fa: => F[A], expected: Throwable*): F[Unit] = {
    val moreThanOne              = expected.length > 1
    val expectedThrowableMessage =
      s"${if (moreThanOne) "One of " else ""}${expected.map(_.getClass.getName).mkString("[", ", ", "]")} " +
        s"${if (moreThanOne) "were" else "was"} expected"

    import cats.syntax.all._
    import munit.Assertions
    fa.map[Unit](a =>
      Assertions.fail(s"$expectedThrowableMessage but no Throwable was not thrown, and got $a instead.")
    ).recoverWith {
      case NonFatal(ex) =>
        import effectie.testing.tools.ThrowableOps
        cats
          .Applicative[F]
          .pure(
            Assertions
              .assert(
                expected.contains(ex),
                expectedThrowableMessage +
                  s" but ${ex.getClass.getName} was thrown instead.\n${ex.stackTraceString}",
              )
          )
    }

  }

}
object fxSpec {
  trait fxIoSpec {
    self: munit.FunSuite with FutureTools with CommonFxSpec =>

    import cats.effect.IO

    test("test fx.{effectOf, pureOf, unitOf} for IO") {
      val before = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)
      val after  = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue) + before

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual  = before // scalafix:ok DisableSyntax.var
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual2 = before // scalafix:ok DisableSyntax.var

      Assertions.assertEquals(actual, before, "testBefore")
      Assertions.assertEquals(actual2, before, "testBefore2")

      val eftClient               = FxCtorClient[IO]
      val effectConstructorClient = FxClient[IO]
      val io                      =
        for {
          _  <- effectOf[IO]({
                  actual = after; ()
                })
          _  <- pureOf[IO]({
                  actual2 = after; ()
                })
          n  <- eftClient.eftOf(1)
          n2 <- eftClient.of(n)
          i  <- effectConstructorClient.eftOf(n2)
          _  <- effectConstructorClient.of(i)
          _  <- eftClient.unit
          _  <- effectConstructorClient.unit
        } yield ()

      Assertions.assertEquals(actual, before, "testBeforeRun")
      Assertions.assertEquals(actual2, before, "testBeforeRun2")
      io.map { _ =>
        Assertions.assertEquals(actual, after, "testAfterRun")
        Assertions.assertEquals(actual2, after, "testAfterRun2")
      }.unsafeToFuture()
    }

    test("test fx.effectOf[IO]") {
      val before = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)
      val after  = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue) + before

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual = before // scalafix:ok DisableSyntax.var
      Assertions.assertEquals(actual, before, "testBefore")

      val io = effectOf[IO]({
        actual = after; ()
      })
      Assertions.assertEquals(actual, before, "testBeforeRun")

      io.map { _ =>
        Assertions.assertEquals(actual, after, "testAfterRun")
      }.unsafeToFuture()
    }

    test("test fx.fromEffect[IO](IO[A])") {
      val before = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)
      val after  = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue) + before

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual = before // scalafix:ok DisableSyntax.var

      Assertions.assertEquals(actual, before, "before effectOf")

      val io = effectOf({
        actual = after; ()
      })
      Assertions.assertEquals(actual, before, "after effectOf but before fromEffect")

      val fromIo = fromEffect(io)

      Assertions.assertEquals(actual, before, "after fromEffect but before run")

      fromIo
        .map { _ =>
          Assertions.assertEquals(actual, after, "after fromEffect and run")
        }
        .unsafeToFuture()
    }

    test("test fx.fromEffect[IO](IO.pure[A])") {
      val before = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)
      val after  = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue) + before

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual = before // scalafix:ok DisableSyntax.var
      Assertions.assertEquals(actual, before, "before fromEffect")

      val fromPure = fromEffect(pureOf({
        actual = after; ()
      }))

      Assertions.assertEquals(actual, before, "after fromEffect but before run")

      fromPure
        .map { _ =>
          Assertions.assertEquals(actual, after, "after fromEffect and run")
        }
        .unsafeToFuture()
    }

    test("test fx.pureOf[IO]") {
      val before = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)
      val after  = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue) + before

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual = before // scalafix:ok DisableSyntax.var

      Assertions.assertEquals(actual, before, "before pureOf")

      val io = pureOf[IO]({
        actual = after; ()
      })

      Assertions.assertEquals(actual, after, "after pureOf but before run")

      io.map { _ =>
        Assertions.assertEquals(actual, after, "after pureOf and run")
      }.unsafeToFuture()
    }

    test("test fx.pureOrError[IO](success case)") {
      val before = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)
      val after  = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue) + before

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual = before // scalafix:ok DisableSyntax.var

      Assertions.assertEquals(actual, before, "before pureOrError")

      val io = pureOrError[IO]({
        actual = after; ()
      })

      Assertions.assertEquals(actual, after, "after pureOrError but before run")

      io.map { _ =>
        Assertions.assertEquals(actual, after, "after pureOrError and run")
      }.unsafeToFuture()
    }

    test("test fx.pureOrError[IO](error case)") {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      val io = pureOrError[IO](throw expectedError) // scalafix:ok DisableSyntax.throw
      expectThrowableMUnit(io, expectedError)
    }

    test("test fx.unitOf[IO]") {
      val io             = unitOf[IO]
      val expected: Unit = ()

      io.map { actual =>
        Assertions.assertEquals(actual, expected)
      }.unsafeToFuture()
    }

    test("test fx.errorOf[IO]") {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      val io = errorOf[IO](expectedError)
      expectThrowableMUnit(io, expectedError)
    }

    test("test fx.pureOfOption[IO] Some case") {
      val s = RandomGens.genAlphaNumericString(10)

      val expected = s.some

      val input = s

      val io  = input.pureOfOption[IO]
      val io2 = pureOfOption[IO](input)
      (for {
        actual  <- io
        actual2 <- io2
      } yield {
        Assertions.assertEquals(actual, expected)
        Assertions.assertEquals(actual2, expected)
      })
        .unsafeToFuture()
    }

    test("test fx.pureOfOption[IO] None case") {
      val s = none[String]

      val expected = s

      val input = s.orNull

      val io  = input.pureOfOption[IO]
      val io2 = pureOfOption[IO](input)
      (for {
        actual  <- io
        actual2 <- io2
      } yield {
        Assertions.assertEquals(actual, expected)
        Assertions.assertEquals(actual2, expected)
      })
        .unsafeToFuture()
    }

    test("test fx.pureOfSome[IO]") {
      val s = RandomGens.genAlphaNumericString(10)

      val expected = s.some

      val io  = s.pureOfSome[IO]
      val io2 = pureOfSome[IO](s)
      (for {
        actual  <- io
        actual2 <- io2
      } yield {
        Assertions.assertEquals(actual, expected)
        Assertions.assertEquals(actual2, expected)
      })
        .unsafeToFuture()
    }

    test("test fx.pureOfNone[IO]") {
      val expected = none[String]

      val io = pureOfNone[IO, String]
      (for {
        actual <- io
      } yield {
        Assertions.assertEquals(actual, expected)
      })
        .unsafeToFuture()
    }

    test("test fx.pureOfRight[IO]") {
      val n = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)

      val expected = n.asRight[String]

      val io  = n.pureOfRight[IO, String]
      val io2 = pureOfRight[IO, String](n)
      (for {
        actual  <- io
        actual2 <- io2
      } yield {
        Assertions.assertEquals(actual, expected)
        Assertions.assertEquals(actual2, expected)
      })
        .unsafeToFuture()
    }

    test("test fx.pureOfLeft[IO]") {
      val s = RandomGens.genAlphaNumericString(10)

      val expected = s.asLeft[Int]

      val io  = s.pureOfLeft[IO, Int]
      val io2 = pureOfLeft[IO, Int](s)
      (for {
        actual  <- io
        actual2 <- io2
      } yield {
        Assertions.assertEquals(actual, expected)
        Assertions.assertEquals(actual2, expected)
      })
        .unsafeToFuture()
    }
  }

  trait fxFutureSpec {
    self: munit.FunSuite with FutureTools with CommonFxSpec =>

    import effectie.instances.future.fx.futureFx

    implicit val ec: ExecutionContext = globalExecutionContext

    override val munitTimeout: FiniteDuration = 200.milliseconds

    test("test fx.{effectOf, pureOf, unitOf} for Future") {
      val before = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)
      val after  = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue) + before

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual  = before // scalafix:ok DisableSyntax.var
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual2 = before // scalafix:ok DisableSyntax.var

      Assertions.assertEquals(actual, before, "before effectOf")
      Assertions.assertEquals(actual2, before, "before pureOf")

      val eftClient               = FxCtorClient[Future]
      val effectConstructorClient = FxClient[Future]

      val future =
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

      future.map { _ =>
        Assertions.assertEquals(actual, after, "after effectOf and after run")
        Assertions.assertEquals(actual2, after, "after pureOf and after run")
      }
    }

    test("test fx.effectOf[Future]") {
      val before = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)
      val after  = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue) + before

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual = before // scalafix:ok DisableSyntax.var
      Assertions.assertEquals(actual, before, "before effectOf")

      val future: Future[Unit] = effectOf[Future]({ actual = after; () })

      future.map { _ =>
        Assertions.assertEquals(actual, after, "after effectOf and after run")
      }
    }

    test("test fx.pureOf[Future]") {
      val before = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)
      val after  = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue) + before

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual = before // scalafix:ok DisableSyntax.var
      Assertions.assertEquals(actual, before, "before pureOf")

      val future = pureOf[Future]({ actual = after; () })
      future.map { _ =>
        Assertions.assertEquals(actual, after, "after pureOf and after run")
      }
    }

    test("test fx.pureOrError[Future](success case)") {
      val before = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)
      val after  = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue) + before

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual = before // scalafix:ok DisableSyntax.var
      Assertions.assertEquals(actual, before, "before pureOrError")

      val future = pureOrError[Future]({ actual = after; () })
      future.map { _ =>
        Assertions.assertEquals(actual, after, "after pureOrError and after run")
      }
    }

    test("test fx.pureOrError[Future](error case)") {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      expectThrowableMUnit(
        pureOrError[Future][Unit](throw expectedError),
        expectedError,
      ) // scalafix:ok DisableSyntax.throw
    }

    test("test fx.unitOf[Future]") {
      val future         = unitOf[Future]
      val expected: Unit = ()

      future.map { actual =>
        Assertions.assertEquals(actual, expected, "after unitOf and after run")
      }
    }

    test("test fx.errorOf[Future]") {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      expectThrowableMUnit(errorOf[Future][Unit](expectedError), expectedError)
    }

    test("test fx.pureOfOption[Future] - Some case") {
      val s = RandomGens.genAlphaNumericString(10)

      val expected = s.some
      val input    = s
      val future   = input.pureOfOption[Future]
      val future2  = pureOfOption[Future](input)

      Future.sequence(
        List(
          future.map { actual =>
            Assertions.assertEquals(actual, expected, "future: after pureOfOption and after run")
          },
          future2.map { actual2 =>
            Assertions.assertEquals(actual2, expected, "future2: after pureOfOption and after run")
          },
        )
      )
    }

    test("test fx.pureOfOption[Future] - None case") {
      val s = none[String]

      val expected = s
      val input    = s.orNull
      val future   = input.pureOfOption[Future]
      val future2  = pureOfOption[Future](input)

      Future.sequence(
        List(
          future.map { actual =>
            Assertions.assertEquals(actual, expected, "future: after pureOfOption and after run")
          },
          future2.map { actual2 =>
            Assertions.assertEquals(actual2, expected, "future2: after pureOfOption and after run")
          },
        )
      )
    }

    test("test fx.pureOfSome[Future]") {
      val s = RandomGens.genAlphaNumericString(10)

      val expected = s.some
      val future   = s.pureOfSome[Future]
      val future2  = pureOfSome[Future](s)

      Future.sequence(
        List(
          future.map { actual =>
            Assertions.assertEquals(actual, expected, "future: after pureOfSome and after run")
          },
          future2.map { actual2 =>
            Assertions.assertEquals(actual2, expected, "future2: after pureOfSome and after run")
          },
        )
      )
    }

    test("test fx.pureOfNone[Future]") {
      val expected = none[String]

      val future = pureOfNone[Future, String]
      future.map { actual =>
        Assertions.assertEquals(actual, expected, "future: after pureOfNone and after run")
      }
    }

    test("test fx.pureOfRight[Future]") {
      val n = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)

      val expected = n.asRight[String]

      val future  = n.pureOfRight[Future, String]
      val future2 = pureOfRight[Future, String](n)

      Future.sequence(
        List(
          future.map { actual =>
            Assertions.assertEquals(actual, expected, "future: after pureOfRight and after run")
          },
          future2.map { actual2 =>
            Assertions.assertEquals(actual2, expected, "future2: after pureOfRight and after run")
          },
        )
      )
    }

    test("test fx.pureOfLeft[Future]") {
      val s = RandomGens.genAlphaNumericString(10)

      val expected = s.asLeft[Int]

      val future  = s.pureOfLeft[Future, Int]
      val future2 = pureOfLeft[Future, Int](s)

      Future.sequence(
        List(
          future.map { actual =>
            Assertions.assertEquals(actual, expected, "future: after pureOfLeft and after run")
          },
          future2.map { actual2 =>
            Assertions.assertEquals(actual2, expected, "future2: after pureOfLeft and after run")
          },
        )
      )

    }
  }

}
