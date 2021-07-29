package effectie.cats.compat

import cats.effect.*
import cats.effect.unsafe.IORuntime
import effectie.ConcurrentSupport

import java.util.concurrent.ExecutorService
import scala.concurrent.ExecutionContext

/** @author Kevin Lee
  * @since 2021-04-18
  */
final class CatsEffectIoCompatForFuture {
  // TODO: Use it for Cats Effect 3
  val es: ExecutorService           = ConcurrentSupport.newExecutorService()
  given ec: ExecutionContext = ConcurrentSupport.newExecutionContextWithLogger(es, println(_))


//  val es: ExecutorService = ConcurrentSupport.newExecutorService()
//  given ec: ExecutionContext = ConcurrentSupport.newExecutionContextWithLogger(es, println(_))
//  given ioRuntime: IORuntime = cats.effect.unsafe.implicits.global
}
