package effectie.instances.ce2

import cats.effect._
import effectie.core.FromFuture
import effectie.instances.ce2.compat.CatsEffectIoCompatForFuture
import effectie.testing.{FutureTools, RandomGens}
import fromFuture.fromFutureToIo
import munit.Assertions

import scala.concurrent._
import scala.concurrent.duration._

/** @author Kevin Lee
  * @since 2020-09-22
  */
class fromFutureSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  override val munitTimeout: FiniteDuration = 200.milliseconds

  test("test FromFuture[IO].toEffect") {
    val a = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)

    val compat = new CatsEffectIoCompatForFuture
    import compat.cs

    val expected = a
    lazy val fa  = Future(a)

    FromFuture[IO].toEffect(fa).map { actual =>
      Assertions.assertEquals(actual, expected)
    }
  }

}
