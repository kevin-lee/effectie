package effectie.syntax

import cats.syntax.all._
import effectie.core._
import effectie.syntax.all._
import effectie.testing.{FutureTools, RandomGens}
import munit._

import scala.concurrent._
import scala.concurrent.duration._
import scala.util.control.NoStackTrace

import monix.eval.Task

/** @author Kevin Lee
  * @since 2023-09-07
  */
class restartSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  import monix.execution.Scheduler.Implicits.global

  override val munitTimeout: FiniteDuration = 200.milliseconds

  type F[A] = Task[A]
  val F = Task

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

  import effectie.instances.monix3.fx.taskFx

  test("CanRestart[F].restartWhile") {
    val n = RandomGens.genRandomIntWithMinMax(0, 10)

    val num = Array.fill(1)(0)

    canRestartRestartWhile(F(n), num)
      .map(Assertions.assertEquals(_, num(0)))
      .runToFuture
  }

  test("CanRestart[F].restartUntil") {
    val n = RandomGens.genRandomIntWithMinMax(0, 10)

    val num = Array.fill(1)(0)
    canRestartRestartUntil(F(n), num)
      .map(Assertions.assertEquals(_, num(0)))
      .runToFuture
  }

  test("CanRestart[F].restartOnError") {
    val n = RandomGens.genRandomIntWithMinMax(0, 10).toLong

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
      .map { _ =>
        Assertions.assertEquals(num(0), expected)
      }
      .runToFuture
  }

  test("CanRestart[F].restartOnErrorIf") {
    val n = RandomGens.genRandomIntWithMinMax(0, 10)

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
      .map(_ => Assertions.assertEquals(num(0), expected))
      .runToFuture
  }

  case object KeepGoing extends NoStackTrace
  case object End extends NoStackTrace

  case object ExpectedException extends NoStackTrace

}
