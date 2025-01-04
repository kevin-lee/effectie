package effectie.testing

import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.ErrorLogger

import java.util.concurrent.{ExecutorService, TimeoutException}
import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2024-12-08
  */
trait FutureTools {
  type WaitFor = extras.concurrent.testing.types.WaitFor
  val WaitFor = extras.concurrent.testing.types.WaitFor

  implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  def globalExecutionContext: ExecutionContext = ExecutionContext.Implicits.global

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter", "org.wartremover.warts.Throw"))
  def futureToValue[A](fa: Future[A], waitFor: WaitFor)(implicit errorLogger: ErrorLogger[TimeoutException]): A =
    ConcurrentSupport.futureToValue(fa, waitFor)(errorLogger)

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter", "org.wartremover.warts.Throw"))
  def futureToValueAndTerminate[A](executorService: ExecutorService, waitFor: WaitFor)(
    fa: => Future[A]
  )(implicit errorLogger: ErrorLogger[Throwable]): A =
    ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(fa)(errorLogger)
}
