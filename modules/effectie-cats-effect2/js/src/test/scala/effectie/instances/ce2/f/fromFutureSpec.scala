package effectie.instances.ce2.f

import cats.effect._
import effectie.core.FromFuture
import effectie.instances.ce2.compat.CatsEffectIoCompatForFuture
import effectie.instances.ce2.f.fromFuture.fromFutureToAsync
import effectie.testing.{FutureTools, RandomGens}
import munit.Assertions

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

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

    lazy val fa = Future(a)
    FromFuture[IO]
      .toEffect(fa)
      .map { actual =>
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()

  }

}
