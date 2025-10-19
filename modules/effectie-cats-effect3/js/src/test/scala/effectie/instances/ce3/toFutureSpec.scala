package effectie.instances.ce3

import cats.effect._
import effectie.core.ToFuture
import effectie.testing.RandomGens
import munit.Assertions
import toFuture.ioToFuture

import scala.concurrent.Future

/** @author Kevin Lee
  * @since 2020-09-23
  */
class toFutureSpec extends munit.CatsEffectSuite {

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

}
