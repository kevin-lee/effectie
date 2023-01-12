package effectie.instances.future

import cats.syntax.all._
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog._
import hedgehog.runner._

import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent._
import scala.concurrent.duration._

/** @author Kevin Lee
  * @since 2023-01-01
  */
object canBlockSpec extends Properties {
  override def tests: List[Test] = List(
    example("test without CanBlockBlock[Future].blockingOf", testWithoutBlockingOf),
    example("test CanBlock[Future].blockingOf", testBlockingOf),
  )

  def takes(howMany: Long): Unit = {
    val start = System.nanoTime()
    println(s"takes>> [B] ID: ${Thread.currentThread().getId}")
    var sum   = 0 // scalafix:ok DisableSyntax.var
    var n     = 0 // scalafix:ok DisableSyntax.var
    while (n < howMany) { // scalafix:ok DisableSyntax.while
      sum += 1
      n += 1
    }
    println(s"takes>> [A] ID: ${Thread.currentThread().getId} : Took ${(System.nanoTime() - start).nanos.toMillis} ms")
    println(s"sum=$sum")
  }

  implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  private val waitFor300Millis = WaitFor(6000.milliseconds)

  def testWithoutBlockingOf: Result = {

//      implicit val es: ExecutorService  = ConcurrentSupport.newExecutorService(1)
//    implicit val es: ExecutorService = Executors.newFixedThreadPool(5)
    implicit val es: ExecutorService = Executors.newWorkStealingPool(5)

    implicit val ec: ExecutionContext =
      ConcurrentSupport.newExecutionContextWithLogger(es, ErrorLogger.printlnExecutionContextErrorLogger)

    ConcurrentSupport.futureToValueAndTerminate(es, waitFor300Millis) {

      Future(Thread.currentThread().getName)
        .map { name =>
          Thread.sleep(3000L)
          name
        }
        .map { name1 =>
          (name1, Thread.currentThread().getName)
        }
        .map {
          case (name1, name2) =>
            println(s"NonBl>> $name1, $name2")
            Result.diffNamed("name1 should equal to name2", name1, name2)(_ =!= _)
        }
    }

  }

  def testBlockingOf: Result = {

//      implicit val es: ExecutorService  = ConcurrentSupport.newExecutorService(1)
//    implicit val es: ExecutorService = Executors.newFixedThreadPool(5)
    implicit val es: ExecutorService = Executors.newWorkStealingPool(5)

    implicit val ec: ExecutionContext =
//      ExecutionContext.global
      ConcurrentSupport.newExecutionContextWithLogger(es, ErrorLogger.printlnExecutionContextErrorLogger)

//      var value = Option.empty[Int]

    import effectie.core.CanBlock
    import effectie.instances.future.canBlock._

    ConcurrentSupport.futureToValueAndTerminate(es, waitFor300Millis) {

      CanBlock[Future]
        .blockingOf {
          CanBlock[Future]
            .blockingOf {
              val name = Thread.currentThread().getName
              Thread.sleep(3000L)
              name
            }
            .flatMap { name1 =>
              CanBlock[Future].blockingOf((name1, Thread.currentThread().getName))
            }
        }
        .flatMap(identity)
        .map {
          case (name1, name2) =>
            println(s"Block>> $name1, $name2")
            Result.diffNamed("name1 should equal to name2", name1, name2)(_ === _)
        }

    }

  }
}
