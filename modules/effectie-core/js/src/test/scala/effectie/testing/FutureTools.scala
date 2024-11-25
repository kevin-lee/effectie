package effectie.testing

//import extras.tools._

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
//import scala.scalajs.js.timers._
//import scala.util.{Failure, Success}

/** @author Kevin Lee
  * @since 2024-12-08
  */
trait FutureTools {

  type WaitFor = FutureTools.WaitFor
  val WaitFor = FutureTools.WaitFor

  val globalExecutionContext: ExecutionContext =
    org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits.global

//  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter", "org.wartremover.warts.Throw"))
//  def futureToValue[A](fa: Future[A], waitFor: WaitFor): A =
//    try {
//      Await.result(fa, waitFor.waitFor)
//    } catch {
//      case ex: TimeoutException =>
//        ex.printStackTrace()
//        throw ex // scalafix:ok DisableSyntax.throw
//    }

//  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter", "org.wartremover.warts.Throw"))
//  def futureToValue[A](fa: Future[A], waitFor: WaitFor)(implicit ec: ExecutionContext): A = {
//
//    var result = Option.empty[A]
//    val now    = System.currentTimeMillis()
//    fa.foreach { value =>
//      result = Some(value)
//      println(">>>>>>>>> SUCCESS!!!!!!!")
//      val justNow = System.currentTimeMillis()
//      println(
//        s"""    now: ${now.toString}
//           |justNow: ${justNow.toString}
//           |${(justNow - now).toString}
//           |result: ${result.toString}
//           |""".stripMargin
//      )
//    }
////    fa.onComplete {
////      case Success(value) =>
////        println(">>>>>>>>> SUCCESS!!!!!!!")
////        result = Some(value)
////        val justNow = System.currentTimeMillis()
////        println(
////          s"""    now: ${now.toString}
////             |justNow: ${justNow.toString}
////             |${(justNow - now).toString}
////             |result: ${result.toString}
////             |""".stripMargin)
////      case Failure(ex) =>
////        println(ex.stackTraceString)
////        throw ex // scalafix:ok DisableSyntax.throw
////    }
//
//    @scala.annotation.tailrec
//    def getValue(fa: Future[A], startAt: Long, waitFor: Long)(implicit ec: ExecutionContext): A = {
//      result match {
//        case None =>
//          val justNow = System.currentTimeMillis()
//          if ((justNow - startAt) < waitFor) {
//            FutureTools.sleep(10)
////            println(
////              s"""startAt: ${startAt.toString}
////                 |justNow: ${justNow.toString}
////                 |waitFor: ${waitFor.toString}
////                 |result: ${result.toString}
////                 |""".stripMargin
////            )
//            getValue(fa, startAt, waitFor)
//          } else {
//            throw new TimeoutException(s"Timeout after $waitFor milliseconds")
//          }
//        case Some(value) =>
//          value
//      }
//    }
//
//    getValue(fa, now, waitFor.waitFor.toMillis)
//  }

//  import scalajs._

//  // Convert Future to JavaScript Promise
//  def futureToJSPromise[A](future: Future[A])(implicit ec: ExecutionContext): js.Promise[A] = {
//    new js.Promise[A]((resolve, reject) => {
//      future.onComplete {
//        case Success(value) => resolve(value)
//        case Failure(ex) => reject(ex)
//      }
//    })
//  }
//
//  // Convert Future to Synchronous Value
//  def futureToValue[A](fa: Future[A], waitFor: WaitFor)(implicit ec: ExecutionContext): A = {
//    val promise = Promise[A]()
//    val startAt = System.currentTimeMillis()
//
//    def poll(): Unit = {
//      fa.value match {
//        case Some(Success(value)) =>
//          promise.success(value)
//        case Some(Failure(ex)) =>
//          promise.failure(ex)
//        case None =>
//          val now = System.currentTimeMillis()
//          if ((now - startAt) > waitFor.waitFor.toMillis) {
//            promise.failure(new TimeoutException(s"Timeout after ${waitFor.waitFor.toMillis} milliseconds"))
//          } else {
//            val _ = setTimeout(10) {
//              poll()
//              (): Unit // Explicit Unit return
//            }
//            ()
//          }
//      }
//    }
//
//    poll()
//
//    val jsPromise = futureToJSPromise(promise.future)
//
//    var result: Option[A]        = None
//    var error: Option[Throwable] = None
//
//    jsPromise.`then`[Unit](
//      { (value: A) =>
//        result = Some(value)
//        (): Unit
//      }: js.Function1[A, Unit],
//      { (ex: Any) =>
//        error = Some(new RuntimeException(ex.toString))
//        (): Unit
//      }: js.Function1[Any, Unit],
//    )
//
//    // Busy wait while yielding control
//    while (result.isEmpty && error.isEmpty) {
//      setTimeout(10) {
//        (): Unit // Explicit Unit return
//      }
//    }
//
//    error.foreach(throw _)
//    result.get
//  }

//  def futureToValue[A](fa: Future[A], waitFor: WaitFor)(implicit ec: ExecutionContext): A = {
//    @volatile var result: Option[A] = None
//    @volatile var error: Option[Throwable] = None
//    @volatile var done = false
//
//    fa.onComplete {
//      case Success(value) =>
//        result = Some(value)
//        done = true
//      case Failure(ex) =>
//        error = Some(ex)
//        done = true
//    }
//
//    val startTime = System.nanoTime()
//    val waitNs = waitFor.waitFor.toNanos
//
//    // Spin until the result is ready or until we time out
//    while (!done && (System.nanoTime() - startTime) < waitNs) {
////      FutureTools.sleep(10L)
//      scalajs.js.timers.setTimeout(10) {}
//      // This loop will freeze the JavaScript thread in a browser environment
//      // and is strongly discouraged for anything but testing in Node.js or similar.
//    }
//
//    error.foreach(throw _)
//
//    result.getOrElse {
//      throw new TimeoutException(s"Future did not complete within ${waitFor.waitFor.toSeconds} s")
//    }
//  }

  implicit def futureOps[A](future: Future[A]): FutureTools.FutureOps[A] = new FutureTools.FutureOps(future)

}
object FutureTools {
  final case class WaitFor(waitFor: FiniteDuration) extends AnyVal

  def sleep(millis: Long): Unit = {
    val startTime = System.currentTimeMillis()
    val buffer    = new StringBuilder
    while ((System.currentTimeMillis() - startTime) < millis) {
      buffer.clear()
      for (i <- 1 to 100)
        buffer.append(i.toString)
    }
  }

  class FutureOps[A](val future: Future[A]) extends AnyVal {
    def toJSPromise(implicit ec: ExecutionContext): scalajs.js.Promise[A] = {
      new scalajs.js.Promise[A]((resolve, reject) => {
        future.onComplete {
          case Success(value) => resolve(value)
          case Failure(exception) => reject(exception)
        }
      })
    }
  }
}
