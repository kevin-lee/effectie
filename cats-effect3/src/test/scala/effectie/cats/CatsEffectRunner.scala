package effectie.cats

import cats.effect.testkit.TestInstances
import cats.syntax.all._
import cats.{Eq, Show}
import cats.effect.{IO, Outcome, SyncIO}
import hedgehog._

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
