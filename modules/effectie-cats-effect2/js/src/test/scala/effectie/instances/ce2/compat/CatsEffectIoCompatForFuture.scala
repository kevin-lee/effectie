package effectie.instances.ce2.compat

import cats.effect._
import effectie.testing.FutureTools
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.ErrorLogger

import java.util.concurrent.ExecutorService
import scala.concurrent.ExecutionContext

/** @author Kevin Lee
  * @since 2021-04-18
  */
final class CatsEffectIoCompatForFuture extends FutureTools {
  implicit val ec: ExecutionContext = globalExecutionContext
  implicit val cs: ContextShift[IO] = IO.contextShift(ec)

}
