package effectie.cats

import cats.effect.testkit.TestInstances
import cats.syntax.all._
import cats.{Eq, Show}
import cats.effect.{IO, Outcome, SyncIO}
import hedgehog._
import effectie.testing.tools._

/** @author Kevin Lee
  * @since 2021-08-05
  */
object CatsEffectRunner extends TestInstances {

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
      tickToResult(Outcome.Succeeded(Some(expected)))

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
    def completeAsEqualToSync(expected: A)(implicit eq: Eq[A], sh: Show[A]): Boolean = {
      val a = ioa.unsafeRunSync()
      a eqv expected
    }
  }
}
