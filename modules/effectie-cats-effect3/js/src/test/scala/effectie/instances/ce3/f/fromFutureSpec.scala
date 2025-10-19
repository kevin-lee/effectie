package effectie.instances.ce3.f

import cats.effect._
import effectie.core.FromFuture
import effectie.testing.RandomGens
import fromFuture._
import munit.Assertions

import scala.concurrent.Future

/** @author Kevin Lee
  * @since 2020-09-22
  */
class FromFutureSpec extends munit.CatsEffectSuite {

  test("test FromFuture[IO].toEffect") {
    val a        = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)
    val expected = a

    lazy val fa = Future(a)
    FromFuture[IO].toEffect(fa).map(Assertions.assertEquals(_, expected))
  }

}
