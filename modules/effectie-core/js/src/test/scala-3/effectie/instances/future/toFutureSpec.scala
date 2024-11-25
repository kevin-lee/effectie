//package effectie.instances.future
//
//import effectie.core.ToFuture
//import extras.concurrent.testing.ConcurrentSupport
//import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
//import hedgehog.*
//import hedgehog.runner.*
//
//import java.util.concurrent.ExecutorService
//import scala.concurrent.duration.*
//import scala.concurrent.{ExecutionContext, Future}
//
///** @author Kevin Lee
//  * @since 2020-09-23
//  */
//class toFutureSpec extends munit.FunSuite {
//  private given errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger
//
//  override val munitTimeout: FiniteDuration = 200.milliseconds
//
//  import effectie.instances.future.toFuture._
//
//    property("test ToFuture[Future].unsafeToFuture") {
//      given es: ExecutorService  = ConcurrentSupport.newExecutorService(2)
//      given ec: ExecutionContext =
//        ConcurrentSupport.newExecutionContextWithLogger(es, ErrorLogger.printlnExecutionContextErrorLogger)
//      ConcurrentSupport.runAndShutdown(es, WaitFor(300.milliseconds)) {
//
//        val a = scala.util.Random.nextInt()
//
//        val expected = Future(a)
//        val fa       = Future(a)
//
//        val future = ToFuture[Future].unsafeToFuture(fa)
//
//        future.map { actual =>
//          assertEquals(actual, a)
//        }
//      }
//    }
//
//
//}
