package effectie.instances.ce3.testing

import cats.effect.unsafe.{IORuntime, IORuntimeConfig}
import hedgehog.core.Result

import java.util.concurrent.ExecutorService
import scala.annotation.nowarn

/** @author Kevin Lee
  * @since 2021-07-22
  */
object IoAppUtils {

  def runWithRuntime(runtime: IORuntime)(test: IORuntime => Result): Result = {
    try test(runtime)
    finally runtime.shutdown()
  }

  def computeWorkerThreadCount: Int = {
    val num = Math.max(2, Runtime.getRuntime.availableProcessors())
    println(s"Worker thread count: ${num.toString}")
    num
  }

  @nowarn
  def runtime(es: ExecutorService): IORuntime = runtime()

  def runtime(): IORuntime = {
    lazy val runtime: IORuntime = {

      val (compute, poller, compDown) =
        IORuntime.createWorkStealingComputeThreadPool()

      val (blocking, blockDown) =
        IORuntime.createDefaultBlockingExecutionContext()

      val (scheduler, schedDown) =
        IORuntime.createDefaultScheduler()

      IORuntime(
        compute,
        blocking,
        scheduler,
        List(poller),
        { () =>
          compDown()
          blockDown()
          schedDown()
        },
        IORuntimeConfig(),
      )
    }
    runtime
  }
}
