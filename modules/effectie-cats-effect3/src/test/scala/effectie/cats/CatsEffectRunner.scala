package effectie.cats

import cats.effect.{IO, Outcome, SyncIO, unsafe}
import cats.syntax.all._
import cats.{Eq, Id, Order, Show, ~>}
import effectie.testing.tools._
import hedgehog._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/** The code inside CatsEffectRunner was not entirely but mostly copied from
  * https://git.io/JDcCP and was modified for Hedgehog
  *
  * @author Kevin Lee
  * @since 2021-08-05
  */
object CatsEffectRunner {

  type TestContext = cats.effect.kernel.testkit.TestContext
  val TestContext = cats.effect.kernel.testkit.TestContext

  final case class Ticker(ctx: TestContext = TestContext())

  implicit lazy val eqThrowable: Eq[Throwable] =
    Eq.fromUniversalEquals[Throwable]

  implicit lazy val showThrowable: Show[Throwable] =
    Show.fromToString[Throwable]

  implicit lazy val eqExecutionContext: Eq[ExecutionContext] =
    Eq.fromUniversalEquals[ExecutionContext]

  implicit def orderIoFiniteDuration(implicit ticker: Ticker): Order[IO[FiniteDuration]] =
    Order by { ioa => unsafeRun(ioa).fold(None, _ => None, fa => fa) }

  implicit def eqIOA[A: Eq](implicit ticker: Ticker): Eq[IO[A]] =
    Eq.by(unsafeRun(_))

  private val someK: Id ~> Option =
    new ~>[Id, Option] { def apply[A](a: A) = a.some }

  def unsafeRun[A](ioa: IO[A])(implicit ticker: Ticker): Outcome[Option, Throwable, A] =
    try {
      var results: Outcome[Option, Throwable, A] = Outcome.Succeeded(None)

      ioa
        .flatMap(IO.pure(_))
        .handleErrorWith(IO.raiseError(_))
        .unsafeRunAsyncOutcome { oc => results = oc.mapK(someK) }(
          unsafe
            .IORuntime(ticker.ctx, ticker.ctx, scheduler, () => (), unsafe.IORuntimeConfig())
        )

      ticker.ctx.tickAll(1.second)

      results
    } catch {
      case t: Throwable =>
        t.printStackTrace()
        throw t
    }

  def unsafeRunSync[A](ioa: SyncIO[A]): Outcome[Id, Throwable, A] =
    try Outcome.succeeded[Id, Throwable, A](ioa.unsafeRunSync())
    catch {
      case t: Throwable => Outcome.errored(t)
    }

  implicit def materializeRuntime(implicit ticker: Ticker): unsafe.IORuntime =
    unsafe.IORuntime(ticker.ctx, ticker.ctx, scheduler, () => (), unsafe.IORuntimeConfig())

  def scheduler(implicit ticker: Ticker): unsafe.Scheduler =
    new unsafe.Scheduler {
      import ticker.ctx

      def sleep(delay: FiniteDuration, action: Runnable): Runnable = {
        val cancel = ctx.schedule(delay, action)
        () => cancel()
      }

      def nowMillis(): Long      = ctx.now().toMillis
      def monotonicNanos(): Long = ctx.now().toNanos
    }

  implicit final class IoOps[A](private val ioa: IO[A]) extends AnyVal {

    def tickTo(expected: Outcome[Option, Throwable, A])(
      implicit ticker: Ticker,
      eq: Eq[A],
      sh: Show[A]
    ): Boolean = {
      val oc = unsafeRun(ioa)
      oc eqv expected
    }

    def tickToResult(expected: Outcome[Option, Throwable, A])(
      implicit ticker: Ticker,
      eq: Eq[A],
      sh: Show[A]
    ): Result = {
      val oc = unsafeRun(ioa)
      Result.assert(oc eqv expected).log(s"${oc.show} !== ${expected.show}")
    }

    def completeAs(expected: A)(implicit ticker: Ticker, eq: Eq[A], sh: Show[A]): Result =
      tickToResult(Outcome.Succeeded(expected.some))

    def completeAndEqualTo(expected: A)(implicit ticker: Ticker, eq: Eq[A], sh: Show[A]): Boolean =
      tickTo(Outcome.Succeeded(Some(expected)))

    def expectError(expected: Throwable*)(implicit ticker: Ticker): Result = {
      val moreThanOne              = expected.length > 1
      val expectedThrowableMessage =
        s"${if (moreThanOne) "One of " else ""}${expected.map(_.getClass.getName).mkString("[", ", ", "]")} " +
          s"${if (moreThanOne) "were" else "was"} expected"

      unsafeRun(ioa) match {
        case Outcome.Errored(e) =>
          Result
            .assert(expected.contains(e))
            .log(expectedThrowableMessage + s" but ${e.getClass.getName} was thrown instead.\n${e.stackTraceString}")

        case _ =>
          Result.failure.log(expectedThrowableMessage + " but no Throwable was thrown.")
      }
    }
  }

  implicit final class SyncIoOps[A](private val ioa: SyncIO[A]) extends AnyVal {
    def completeAsSync(expected: A)(implicit eq: Eq[A], sh: Show[A]): Result = {
      val a = ioa.unsafeRunSync()
      Result.assert(a eqv expected).log(s"${a.show} !== ${expected.show}")
    }
    def completeAsEqualToSync(expected: A)(implicit eq: Eq[A]): Boolean      = {
      val a = ioa.unsafeRunSync()
      a eqv expected
    }
  }
}
