package effectie.concurrent

import java.util.concurrent.{ExecutorService, TimeUnit}

import scala.concurrent.duration.FiniteDuration

/**
 * @author Kevin Lee
 * @since 2020-07-31
 */
trait ExecutorServiceOps {

  def shutdownAndAwaitTermination(executorService: ExecutorService, waitFor: FiniteDuration): Unit = {
    executorService.shutdown()

    try {
      if (!executorService.awaitTermination(waitFor.toSeconds, TimeUnit.SECONDS)) {
        val _ = executorService.shutdownNow()

        if (!executorService.awaitTermination(waitFor.toSeconds, TimeUnit.SECONDS)) {
          println("ExecutorService did not terminate")
        } else {
          ()
        }
      } else {
        ()
      }
    } catch {
      case ie: InterruptedException =>
        val _ = executorService.shutdownNow()
        Thread.currentThread.interrupt()
    }
  }

}

object ExecutorServiceOps extends ExecutorServiceOps