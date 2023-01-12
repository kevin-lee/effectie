package effectie.instances.ce2

import cats.effect.*
import cats.syntax.all.*
import effectie.core.*
import effectie.instances.ce2.fx.ioFx
import extras.concurrent.testing.{ConcurrentSupport, types}
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog.*
import hedgehog.runner.*

import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.*

/** @author Kevin Lee
  * @since 2023-01-07
  */
object canBlockSpec extends Properties {
  override def tests: List[Test] = List(
    property("test without blockingOf", testWithoutBlockOf).withTests(3),
    property("test blockingOf", testBlockingOf).withTests(3),
  )

  private val sleepFor: FiniteDuration = 100.milliseconds

  def testWithoutBlockOf: Property =
    for {
      _ <- Gen.int(Range.linear(0, Int.MaxValue)).log("n")
    } yield {

//      implicit val executorService: ExecutorService               = Executors.newWorkStealingPool(10)
      implicit val executorService: ExecutorService               = Executors.newFixedThreadPool(2)
      implicit val errorLogger: types.ExecutionContextErrorLogger = ErrorLogger.printlnExecutionContextErrorLogger

      implicit val executionContext: ExecutionContext =
        ConcurrentSupport.newExecutionContextWithLogger(executorService, errorLogger)

//      implicit val timer: Timer[IO] = IO.timer(executionContext)

      implicit val contextShift: ContextShift[IO] = IO.contextShift(executionContext)

      ConcurrentSupport.runAndShutdown(executorService, WaitFor(200.milliseconds)) {
        Blocker[IO]
          .use { implicit blocker =>
            //          import effectie.instances.ce2.canBlock._
//            Fx[IO]
//              .effectOf {
//            Timer[IO].sleep(sleepFor) *>
            contextShift.shift *>
              Fx[IO]
                .effectOf(Thread.currentThread().getName)
                .product(
                  contextShift.shift *> Fx[IO].effectOf(Thread.currentThread().getName)
                )
            //            )
            //            .flatMap(identity)
            //          Fx[IO]
            //            .effectOf {
            //              for {
            //                id1 <- Fx[IO].effectOf(Thread.currentThread().getName)
            //                _   <- Timer[IO].sleep(sleepFor)
            //                id2 <- Fx[IO].effectOf(Thread.currentThread().getName)
            //                _ = println(s"> $id1, $id2")
            //              } yield (id1, id2)
            //
//              }
//              .flatMap(identity)
          }
          .map {
            case (id1, id2) =>
              println(s"   IO>> $id1, $id2")
              Result.diffNamed("id1 should not equal to id2", id1, id2)(_ =!= _)
          }
          .unsafeRunSync()
      }
    }

  def testBlockingOf: Property =
    for {
      n <- Gen.int(Range.linear(0, Int.MaxValue)).log("n")
    } yield {
      import effectie.instances.ce2.canBlock._

//      implicit val executorService: ExecutorService               = Executors.newWorkStealingPool(10)
      implicit val executorService: ExecutorService               = Executors.newFixedThreadPool(2)
      implicit val errorLogger: types.ExecutionContextErrorLogger = ErrorLogger.printlnExecutionContextErrorLogger

      implicit val executionContext: ExecutionContext =
        ConcurrentSupport.newExecutionContextWithLogger(executorService, errorLogger)

//      implicit val timer: Timer[IO]               = IO.timer(executionContext)
      implicit val contextShift: ContextShift[IO] = IO.contextShift(executionContext)

      ConcurrentSupport.runAndShutdown(executorService, WaitFor(200.milliseconds)) {
        Blocker[IO]
          .use { implicit blocker =>
            CanBlock[IO]
//              .blockingOf(
//                CanBlock[IO]
              .blockingOf(Thread.currentThread().getName)
              .product(
                contextShift.shift *> CanBlock[IO]
                  .blockingOf(Thread.currentThread().getName)
              )
//              )
//              .flatMap(identity)

          //          CanBlock[IO]
          //            .blockingOf {
          //              for {
          //                id1 <- Fx[IO].effectOf(Thread.currentThread().getName)
          //                _   <- Timer[IO].sleep(sleepFor)
          //                id2 <- Fx[IO].effectOf(Thread.currentThread().getName)
          //                _ = println(s">> $id1, $id2")
          //              } yield (id1, id2)
          //
          //            }
          //            .flatMap(identity)
          }
          .map {
            case (id1, id2) =>
              println(s"Block>> $id1, $id2")
              Result.diffNamed("id1 should equal to id2", id1, id2)(_ === _)
          }
          .unsafeRunSync()
      }
    }

}
