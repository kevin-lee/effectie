package effectie.instances.future

import cats._
import effectie.core.ToFuture
import effectie.instances.id.toFuture._
import effectie.testing.{FutureTools, RandomGens}
import munit.Assertions

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2020-09-23
  */
class toFutureSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  override val munitTimeout: FiniteDuration = 200.milliseconds

  test("test ToFuture[Future].unsafeToFuture") {
    val a = RandomGens.genRandomInt()

    val expected = a

    val fa = Future(expected)

    import effectie.instances.future.toFuture._

    val future = ToFuture[Future]
    future.unsafeToFuture(fa).map(actual => Assertions.assertEquals(actual, expected))

  }

  test("test ToFuture[Id].unsafeToFuture") {
    val a  = RandomGens.genRandomInt()
    val fa = a

    val expected = Future(a)

    val future = ToFuture[Id].unsafeToFuture(fa)
    val actual = future

    Assertions.assert(
      future.isInstanceOf[Future[Int]], // scalafix:ok DisableSyntax.isInstanceOf
      s"future is not an instance of Future[Int]. future.getClass: ${future.getClass.toString}",
    )

    actual.flatMap(actual => expected.map(expected => Assertions.assertEquals(actual, expected)))

  }

}
