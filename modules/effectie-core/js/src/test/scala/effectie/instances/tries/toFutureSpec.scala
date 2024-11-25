package effectie.instances.tries

import effectie.core.ToFuture
import effectie.testing.FutureTools

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import scala.util.Try

/** @author Kevin Lee
  * @since 2023-03-17
  */
class toFutureSpec extends munit.FunSuite with FutureTools {
  implicit val ec: ExecutionContext = globalExecutionContext

  import effectie.instances.tries.toFuture._

  override val munitTimeout: FiniteDuration = 200.milliseconds

  test("test ToFuture[Try].unsafeToFuture") {

    val a  = scala.util.Random.nextInt()
    val fa = Try(a)

    val expected = a

    val future = ToFuture[Try].unsafeToFuture(fa)
    future.map { actual =>
      assertEquals(actual, expected)
    }

  }

}
