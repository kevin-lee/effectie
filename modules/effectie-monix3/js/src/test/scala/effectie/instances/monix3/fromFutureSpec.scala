package effectie.instances.monix3

import effectie.core.FromFuture
import effectie.testing.{FutureTools, RandomGens}
import fromFuture.fromFutureToTask
import monix.eval.Task
import munit.Assertions

import scala.concurrent._
import scala.concurrent.duration._

/** @author Kevin Lee
  * @since 2020-09-22
  */
class fromFutureSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  import monix.execution.Scheduler.Implicits.global

  override val munitTimeout: FiniteDuration = 200.milliseconds

  test("test FromFuture[Task].toEffect") {
    val a = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)

    val expected = a
    lazy val fa  = Future(a)

    FromFuture[Task]
      .toEffect(fa)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .runToFuture
  }

}
