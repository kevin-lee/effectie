package effectie.cats

import java.io.{PrintWriter, StringWriter}
import java.util.concurrent.{ExecutorService, Executors, TimeoutException}

import effectie.concurrent.ExecutorServiceOps

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import scala.util.control.NonFatal

/**
 * @author Kevin Lee
 * @since 2020-09-22
 */
trait ConcurrentSupport {
  def newExecutorService(): ExecutorService =
    Executors.newFixedThreadPool(math.max(1, Runtime.getRuntime.availableProcessors() >> 1))

  def newExecutionContext(executorService: ExecutorService, logger: String => Unit): ExecutionContext =
    scala.concurrent.ExecutionContext.fromExecutor(
      executorService,
      { th =>
        val stringWriter = new StringWriter()
        val printWriter = new PrintWriter(stringWriter)
        th.printStackTrace(printWriter)
        logger(s"Error in ThreadPoolExecutor: ${stringWriter.toString}")
      }
    )

  def runAndShutdown[A](executorService: ExecutorService, waitFor: FiniteDuration)(a: => A): A =
    try a
    finally {
      try {
        ExecutorServiceOps.shutdownAndAwaitTermination(executorService, waitFor)
      } catch {
        case NonFatal(ex) =>
          @SuppressWarnings(Array("org.wartremover.warts.ToString"))
          val message = ex.toString
          println(s"NonFatal: $message")
      }
    }


  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter", "org.wartremover.warts.Throw"))
  def futureToValue[A](fa: Future[A], waitFor: FiniteDuration): A =
    try {
      Await.result(fa, waitFor)
    } catch {
      case ex: TimeoutException =>
        @SuppressWarnings(Array("org.wartremover.warts.ToString"))
        val message = ex.toString
        println(s"ex: $message")
        throw ex
    }

}

object ConcurrentSupport extends ConcurrentSupport
