//package effectie.instances.future
//
//import effectie.core.FromFuture
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
//  * @since 2020-09-22
//  */
//class fromFutureSpec extends munit.FunSuite {
//  implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger
//
//  override val munitTimeout: FiniteDuration = 200.milliseconds
//
//  import effectie.instances.future.fromFuture._
//
//  test("test FromFuture[Future].toEffect") {
//    val a = scala.util.Random.nextInt()
//
//    given es: ExecutorService  = ConcurrentSupport.newExecutorService(2)
//    given ec: ExecutionContext =
//      ConcurrentSupport.newExecutionContextWithLogger(es, ErrorLogger.printlnExecutionContextErrorLogger)
//
//    ConcurrentSupport.runAndShutdown(es, WaitFor(300.milliseconds)) {
//      lazy val fa  = Future(a)
//      val expected = a
//
//      FromFuture[Future].toEffect(fa).map { actual =>
//        assertEquals(actual, expected)
//      }
//
//    }
//  }
//
//}
