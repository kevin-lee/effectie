package effectie.instances.monix3

import cats._

import effectie.core.ToFuture
import effectie.testing.{FutureTools, RandomGens}
import munit.Assertions
import toFuture.taskToFuture

import scala.concurrent._
import scala.concurrent.duration._

import monix.eval.Task

/** @author Kevin Lee
  * @since 2020-09-23
  */
class toFutureSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  import monix.execution.Scheduler.Implicits.global

  override val munitTimeout: FiniteDuration = 200.milliseconds

  test("test ToFuture[Task].unsafeToFuture") {
    val a = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)

    val expected = a
    val fa       = Task(expected)

    val future = ToFuture[Task].unsafeToFuture(fa)
    Assertions.assert(
      future.isInstanceOf[Future[Int]], // scalafix:ok DisableSyntax.isInstanceOf
      s"future is not an instance of Future[Int]. future.getClass: ${future.getClass.toString}",
    )
    future.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

  test("test ToFuture[Id].unsafeToFuture") {
    import effectie.instances.id.toFuture._
    val a = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)

    val fa = a

    val expected = a

    val future = ToFuture[Id].unsafeToFuture(fa)

    Assertions.assert(
      future.isInstanceOf[Future[Int]], // scalafix:ok DisableSyntax.isInstanceOf
      s"future is not an instance of Future[Int]. future.getClass: ${future.getClass.toString}",
    )
    future.map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

}
