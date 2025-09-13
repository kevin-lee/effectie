package effectie.instances.future

import effectie.core.FromFuture
import effectie.testing.FutureTools

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2020-09-22
  */
class fromFutureSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  override val munitTimeout: FiniteDuration = 200.milliseconds

  import effectie.instances.future.fromFuture._

  test("test FromFuture[Future].toEffect") {
    val a = scala.util.Random.nextInt()

    lazy val fa = Future(a)

    val expected = a

    FromFuture[Future].toEffect(fa).map { actual =>
      assertEquals(actual, expected)
    }

  }

}
