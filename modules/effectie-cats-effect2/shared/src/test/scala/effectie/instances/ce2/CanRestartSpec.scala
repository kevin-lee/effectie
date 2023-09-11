package effectie.instances.ce2

import cats.syntax.all._
import cats.effect.IO

import effectie.core._

import hedgehog._
import hedgehog.runner._

import scala.util.control.NoStackTrace

/** @author Kevin Lee
  * @since 2023-09-07
  */
object CanRestartSpec extends Properties {
  override def tests: List[Test] = List(
    property("CanRestart[F].restartWhile", testRestartWhile),
    example("CanRestart[F].restartWhile (heavy recursion: 1,000,000 times)", testRestartWhileHeavyRecursion),
    property("CanRestart[F].restartUntil", testRestartUntil),
    example("CanRestart[F].restartUntil (heavy recursion: 1,000,000 times)", testRestartUntilHeavyRecursion),
    property("CanRestart[F].restartOnError", testRestartOnError),
    example("CanRestart[F].restartOnError (heavy recursion: 1,000,000 times)", testRestartOnErrorHeavyRecursion),
    property("CanRestart[F].restartOnErrorIf", testRestartOnErrorIf),
    example("CanRestart[F].restartOnErrorIf (heavy recursion: 1,000,000 times)", testRestartOnErrorIfHeavyRecursion),
  )

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

  import effectie.instances.ce2.fx.ioFx

  def testRestartWhile: Property =
    for {
      n <- Gen.int(Range.linear(0, 10)).log("n")
    } yield {
      val num = Array.fill(1)(0)
      canRestartRestartWhile(F(n), num)
        .map(_ ==== num(0))
        .unsafeRunSync()
    }

  def testRestartWhileHeavyRecursion: Result = {
    val n   = 1000000
    val num = Array.fill(1)(0)
    canRestartRestartWhile(F(n), num)
      .map(_ ==== num(0))
      .unsafeRunSync()
  }

  def testRestartUntil: Property =
    for {
      n <- Gen.int(Range.linear(0, 10)).log("n")
    } yield {
      val num = Array.fill(1)(0)
      canRestartRestartUntil(F(n), num)
        .map(_ ==== num(0))
        .unsafeRunSync()
    }

  def testRestartUntilHeavyRecursion: Result = {
    val n   = 1000000
    val num = Array.fill(1)(0)
    canRestartRestartUntil(F(n), num)
      .map(_ ==== num(0))
      .unsafeRunSync()
  }

  def testRestartOnError: Property =
    for {
      n <- Gen.long(Range.linear(0L, 10L)).log("n")
    } yield {
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
        .unsafeRunSync()
      num(0) ==== expected
    }

  def testRestartOnErrorHeavyRecursion: Result = {
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
      .unsafeRunSync()
    num(0) ==== expected
  }

  def testRestartOnErrorIf: Property =
    for {
      n <- Gen.int(Range.linear(0, 10)).log("n")
    } yield {
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
        },
      )
        .handleErrorWith {
          case End => F.unit
          case ex => F.raiseError(ex)
        }
        .unsafeRunSync()
      num(0) ==== expected
    }

  def testRestartOnErrorIfHeavyRecursion: Result = {
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
      },
    )
      .handleErrorWith {
        case End => F.unit
        case ex => F.raiseError(ex)
      }
      .unsafeRunSync()
    num(0) ==== expected
  }

  case object KeepGoing extends NoStackTrace
  case object End extends NoStackTrace

  case object ExpectedException extends NoStackTrace

}
