package effectie.instances.ce3

import cats.effect._
import effectie.core.FromFuture
import effectie.testing.RandomGens
import munit.Assertions

import scala.concurrent.Future

/** @author Kevin Lee
  * @since 2020-09-22
  */
class fromFutureSpec extends munit.CatsEffectSuite {
  import effectie.instances.ce3.fromFuture.fromFutureToIo

  test("test FromFuture[IO].toEffect") {
    val a = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)

    val expected = a
    lazy val fa  = Future(a)

    FromFuture[IO].toEffect(fa).map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

}
