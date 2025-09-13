package effectie.instances.ce2

import cats._
import cats.effect._
import effectie.core.ToFuture
import effectie.testing.{FutureTools, RandomGens}
import munit.Assertions
import toFuture.ioToFuture

import scala.concurrent._
import scala.concurrent.duration._

/** @author Kevin Lee
  * @since 2020-09-23
  */
class toFutureSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  override val munitTimeout: FiniteDuration = 200.milliseconds

  test("test ToFuture[IO].unsafeToFuture") {
    val a = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)

    val expected = a
    val fa       = IO(expected)

    val future = ToFuture[IO].unsafeToFuture(fa)
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
