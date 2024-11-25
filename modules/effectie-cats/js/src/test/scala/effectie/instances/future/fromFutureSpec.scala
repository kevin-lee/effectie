package effectie.instances.future

import effectie.core.FromFuture
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

  test("test FromFuture[Future].toEffect") {
    import effectie.instances.future.fromFuture._
    val a = RandomGens.genRandomInt()

    val expected = a

    lazy val fa = Future(a)

    FromFuture[Future].toEffect(fa).map { actual =>
      Assertions.assertEquals(actual, expected)
    }

  }

}
