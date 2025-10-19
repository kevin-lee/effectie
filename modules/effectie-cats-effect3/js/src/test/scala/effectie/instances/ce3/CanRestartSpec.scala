package effectie.instances.ce3

import cats.syntax.all._
import cats.effect.IO
import effectie.core._
import effectie.testing.RandomGens
import munit.Assertions

import scala.util.control.NoStackTrace

/** @author Kevin Lee
  * @since 2023-09-07
  */
class CanRestartSpec extends munit.CatsEffectSuite {

  type F[A] = IO[A]
  val F = IO

  def canRestartRestartWhile[G[*]: Fx](ga: G[Int], num: Array[Int]): G[Int] =
    CanRestart[G].restartWhile(ga) { n =>
      if (n != num(0)) {
        num(0) = num(0) + 1
        true
      } else {
        false
      }
    }

  def canRestartRestartUntil[G[*]: Fx](ga: G[Int], num: Array[Int]): G[Int] =
    CanRestart[G].restartUntil(ga) { n =>
      if (n === num(0)) {
        true
      } else {
        num(0) = num(0) + 1
        false
      }
    }

  def canRestartRestartOnError[G[*]: Fx](ga: G[Unit], maxRetries: Long): G[Unit] =
    CanRestart[G].restartOnError(ga)(maxRetries)

  def canRestartRestartOnErrorIfTrue[G[*]: Fx](ga: G[Unit], p: Throwable => Boolean): G[Unit] =
    CanRestart[G].restartOnErrorIfTrue(ga)(p)

  import effectie.instances.ce3.fx.ioFx

  test("CanRestart[F].restartWhile") {
    val n = RandomGens.genRandomIntWithMinMax(0, 10)

    val num = Array.fill(1)(0)
    canRestartRestartWhile(F(n), num)
      .map(Assertions.assertEquals(_, num(0)))

  }

  test("CanRestart[F].restartWhile (heavy recursion: 1,000,000 times)") {
    val n   = 1000000
    val num = Array.fill(1)(0)
    canRestartRestartWhile(F(n), num)
      .map(Assertions.assertEquals(_, num(0)))

  }

  test("CanRestart[F].restartUntil") {
    val n = RandomGens.genRandomIntWithMinMax(0, 10)

    val num = Array.fill(1)(0)
    canRestartRestartUntil(F(n), num)
      .map(Assertions.assertEquals(_, num(0)))

  }

  test("CanRestart[F].restartUntil (heavy recursion: 1,000,000 times)") {
    val n   = 1000000
    val num = Array.fill(1)(0)
    canRestartRestartUntil(F(n), num)
      .map(Assertions.assertEquals(_, num(0)))

  }

  test("CanRestart[F].restartOnError") {
    val n = RandomGens.genRandomLongWithMinMax(0L, 10L)

    val expected = (n + 1) * 3

    val num = Array.fill(1)(0L)

    canRestartRestartOnError(
      F[Unit] {
        num(0) += 1L
        ()
      }.map { _ =>
        num(0) += 1L
        ()
      }.flatMap { _ =>
        F {
          num(0) += 1L
          ()
        }
      }.flatMap { _ =>
        F.raiseError[Unit](ExpectedException)
      },
      n,
    )
      .handleErrorWith {
        case ExpectedException => F.unit
        case ex => F.raiseError(ex)
      }
      .map(_ => Assertions.assertEquals(num(0), expected))

  }

  test("CanRestart[F].restartOnError (heavy recursion: 1,000,000 times)") {
    val n = 1000000L

    val expected = (n + 1) * 3

    val num = Array.fill(1)(0L)

    canRestartRestartOnError(
      F[Unit] {
        num(0) += 1L
        ()
      }.map { _ =>
        num(0) += 1L
        ()
      }.flatMap { _ =>
        F {
          num(0) += 1L
          ()
        }
      }.flatMap { _ =>
        F.raiseError[Unit](ExpectedException)
      },
      n,
    )
      .handleErrorWith {
        case ExpectedException => F.unit
        case ex => F.raiseError(ex)
      }
      .map(_ => Assertions.assertEquals(num(0), expected))

  }

  test("CanRestart[F].restartOnErrorIf") {
    val n = RandomGens.genRandomIntWithMinMax(0, 10)

    val expected = (n + 1) * 3
    val num      = Array.fill(1)(0)

    canRestartRestartOnErrorIfTrue(
      F[Unit] {
        num(0) += 1
        ()
      }.map { _ =>
        num(0) += 1
        ()
      }.flatMap { _ =>
        F {
          num(0) += 1
          ()
        }
      }.map[Unit] { _ =>
        if (num(0) < expected)
          throw KeepGoing // scalafix:ok DisableSyntax.throw
        else
          throw End // scalafix:ok DisableSyntax.throw
      },
      {
        case KeepGoing => true
        case End => false
        case err => throw err // scalafix:ok DisableSyntax.throw
      },
    )
      .handleErrorWith {
        case End => F.unit
        case ex => F.raiseError(ex)
      }
      .map(_ => Assertions.assertEquals(num(0), expected))

  }

  test("CanRestart[F].restartOnErrorIf (heavy recursion: 1,000,000 times)") {
    val expected = 1000000 * 3
    val num      = Array.fill(1)(0)

    canRestartRestartOnErrorIfTrue(
      F[Unit] {
        num(0) += 1
        ()
      }.map { _ =>
        num(0) += 1
        ()
      }.flatMap { _ =>
        F {
          num(0) += 1
          ()
        }
      }.map[Unit] { _ =>
        if (num(0) < expected)
          throw KeepGoing // scalafix:ok DisableSyntax.throw
        else
          throw End // scalafix:ok DisableSyntax.throw
      },
      {
        case KeepGoing => true
        case End => false
        case err => throw err // scalafix:ok DisableSyntax.throw
      },
    )
      .handleErrorWith {
        case End => F.unit
        case ex => F.raiseError(ex)
      }
      .map(_ => Assertions.assertEquals(num(0), expected))

  }

  case object KeepGoing extends NoStackTrace
  case object End extends NoStackTrace

  case object ExpectedException extends NoStackTrace

}
