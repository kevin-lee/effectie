package effectie.instances.ce3.testing

import cats.effect.unsafe.{IORuntime, IORuntimeConfig}
import hedgehog.core.Result

/** @author Kevin Lee
  * @since 2021-07-22
  */
object IoAppUtils {

  def runWithRuntime(runtime: IORuntime)(test: IORuntime => Result): Result = {
    try test(runtime)
    finally runtime.shutdown()
  }

  def withNewRuntime(test: IORuntime => Result): Result = {
    val rt = runtime()
    try test(rt)
    finally rt.shutdown()
  }

  private def runtime(): IORuntime = {

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
}
