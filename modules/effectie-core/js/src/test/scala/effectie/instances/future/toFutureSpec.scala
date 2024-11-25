package effectie.instances.future

import effectie.core.ToFuture
import effectie.testing.FutureTools

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2020-09-23
  */
class toFutureSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  override val munitTimeout: FiniteDuration = 200.milliseconds

  import effectie.instances.future.toFuture._

  test("test ToFuture[Future].unsafeToFuture") {

    val a  = scala.util.Random.nextInt()
    val fa = Future(a)

    lazy val future = ToFuture[Future].unsafeToFuture(fa)

    val expected = a

    future.map { actual =>
      assertEquals(actual, expected)
    }

  }

}
