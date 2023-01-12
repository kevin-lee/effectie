package effectie.instances.ce3.testing

import cats.effect.unsafe.{IORuntime, IORuntimeConfig}
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.ErrorLogger

import java.util.concurrent.ExecutorService

/** @author Kevin Lee
  * @since 2021-07-22
  */
object IoAppUtils {

  def runtime(es: ExecutorService): IORuntime = runtime()

  def runtime(): IORuntime = {
    lazy val runtime: IORuntime = {

      val (compute, compDown) =
        IORuntime.createDefaultComputeThreadPool(runtime)

      val (blocking, blockDown) =
        IORuntime.createDefaultBlockingExecutionContext()

      val (scheduler, schedDown) =
        IORuntime.createDefaultScheduler()

      IORuntime(
        compute,
        blocking,
        scheduler,
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
