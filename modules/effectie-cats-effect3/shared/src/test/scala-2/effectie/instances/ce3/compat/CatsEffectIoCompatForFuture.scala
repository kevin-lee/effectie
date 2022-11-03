package effectie.instances.ce3.compat

import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.ErrorLogger

import java.util.concurrent.ExecutorService
import scala.concurrent.ExecutionContext

/** @author Kevin Lee
  * @since 2021-04-18
  */
final class CatsEffectIoCompatForFuture {
  val es: ExecutorService           = ConcurrentSupport.newExecutorService(2)
  implicit val ec: ExecutionContext =
    ConcurrentSupport.newExecutionContextWithLogger(es, ErrorLogger.printlnExecutionContextErrorLogger)

}
