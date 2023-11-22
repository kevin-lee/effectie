package effectie.syntax

import cats.effect.IO
import cats.syntax.all._

import effectie.core._
import effectie.syntax.all._

import extras.hedgehog.ce3.syntax.runner._

import hedgehog._
import hedgehog.runner._

import scala.util.control.NoStackTrace

/** @author Kevin Lee
  * @since 2023-09-07
  */
object restartSpec extends Properties {
  override def tests: List[Test] = List(
    property("CanRestart[F].restartWhile", testRestartWhile),
    property("CanRestart[F].restartUntil", testRestartUntil),
    property("CanRestart[F].restartOnError", testRestartOnError),
    property("CanRestart[F].restartOnErrorIf", testRestartOnErrorIf),
  )

  type F[A] = IO[A]
  val F = IO

  def canRestartRestartWhile[G[*]: Fx](ga: G[Int], num: Array[Int]): G[Int] =
    ga.restartWhile { n =>
      if (n != num(0)) {
        num(0) = num(0) + 1
        true
      } else {
        false
      }
    }

  def canRestartRestartUntil[G[*]: Fx](ga: G[Int], num: Array[Int]): G[Int] =
    ga.restartUntil { n =>
      if (n === num(0)) {
        true
      } else {
        num(0) = num(0) + 1
        false
      }
    }

  def canRestartRestartOnError[G[*]: Fx](ga: G[Unit], maxRetries: Long): G[Unit] =
    ga.restartOnError(maxRetries)

  def canRestartRestartOnErrorIfTrue[G[*]: Fx](ga: G[Unit], p: Throwable => Boolean): G[Unit] =
    ga.restartOnErrorIfTrue(p)

  import effectie.instances.ce3.fx.ioFx

  def testRestartWhile: Property =
    for {
      n <- Gen.int(Range.linear(0, 10)).log("n")
    } yield runIO {
      val num = Array.fill(1)(0)
      canRestartRestartWhile(F(n), num)
        .map(_ ==== num(0))
    }

  def testRestartUntil: Property =
    for {
      n <- Gen.int(Range.linear(0, 10)).log("n")
    } yield runIO {
      val num = Array.fill(1)(0)
      canRestartRestartUntil(F(n), num)
        .map(_ ==== num(0))
    }

  def testRestartOnError: Property =
    for {
      n <- Gen.long(Range.linear(0L, 10L)).log("n")
    } yield withIO { implicit ticker =>
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
        .completeThen(_ => num(0) ==== expected)
    }

  def testRestartOnErrorIf: Property =
    for {
      n <- Gen.int(Range.linear(0, 10)).log("n")
    } yield withIO { implicit ticker =>
      val expected = (n + 1) * 3

      val num = Array.fill(1)(0)

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
        .completeThen(_ => num(0) ==== expected)
    }

  case object KeepGoing extends NoStackTrace
  case object End extends NoStackTrace

  case object ExpectedException extends NoStackTrace

}
