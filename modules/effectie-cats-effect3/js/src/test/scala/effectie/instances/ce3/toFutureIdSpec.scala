package effectie.instances.ce3

import cats._
import effectie.core.ToFuture
import effectie.testing.{FutureTools, RandomGens}
import munit.Assertions

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/** @author Kevin Lee
  * @since 2020-09-23
  */
class toFutureIdSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  override val munitTimeout: FiniteDuration = 200.milliseconds

  import effectie.instances.id.toFuture.idToFuture

  test("test ToFuture[Id].unsafeToFuture") {
    val a = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)

    val fa = a

    val expected = a

    val future = ToFuture[Id].unsafeToFuture(fa)

    future.map(Assertions.assertEquals(_, expected))

  }
}
