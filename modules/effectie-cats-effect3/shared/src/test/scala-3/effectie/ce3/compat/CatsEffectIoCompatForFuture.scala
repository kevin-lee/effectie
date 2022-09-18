package effectie.ce3.compat

import cats.effect.*
import cats.effect.unsafe.IORuntime
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.ErrorLogger

import java.util.concurrent.ExecutorService
import scala.concurrent.ExecutionContext

/** @author Kevin Lee
  * @since 2021-04-18
  */
final class CatsEffectIoCompatForFuture {
  // TODO: Use it for Cats Effect 3
  val es: ExecutorService    = ConcurrentSupport.newExecutorService(2)
  given ec: ExecutionContext =
    ConcurrentSupport.newExecutionContextWithLogger(es, ErrorLogger.printlnExecutionContextErrorLogger)

//  val es: ExecutorService = ConcurrentSupport.newExecutorService(2)
//  given ec: ExecutionContext = ConcurrentSupport.newExecutionContextWithLogger(es, println(_))
//  given ioRuntime: IORuntime = cats.effect.unsafe.implicits.global
}
