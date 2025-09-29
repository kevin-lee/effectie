package effectie.instances.monix3.compat

import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.ErrorLogger

import cats.effect.ContextShift

import java.util.concurrent.ExecutorService
import scala.concurrent.ExecutionContext

import monix.eval.Task

/** @author Kevin Lee
  * @since 2021-04-18
  */
final class CatsEffectIoCompatForFuture {
  val es: ExecutorService = ConcurrentSupport.newExecutorService(2)

  given ec: ExecutionContext =
    ConcurrentSupport.newExecutionContextWithLogger(es, ErrorLogger.printlnExecutionContextErrorLogger)

  given cs: ContextShift[Task] = Task.contextShift(monix.execution.Scheduler.Implicits.global)

}
