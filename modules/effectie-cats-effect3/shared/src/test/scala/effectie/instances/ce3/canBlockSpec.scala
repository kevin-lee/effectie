package effectie.instances.ce3

import cats.effect.*
import cats.effect.unsafe.IORuntime
import cats.syntax.all.*
import effectie.core.*
import effectie.instances.ce3.fx.ioFx
import effectie.instances.ce3.testing.IoAppUtils
import effectie.syntax.all.*
import hedgehog.*
import hedgehog.runner.*

//import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.duration._

/** @author Kevin Lee
  * @since 2023-01-08
  */
object canBlockSpec extends Properties {
  override def tests: List[Test] = List(
    property("test without blockingOf", testWithoutBlockOf).withTests(5),
    property("test blockingOf", testBlockingOf).withTests(5),
  )

  private val sleepFor: FiniteDuration = 50.milliseconds

  def testWithoutBlockOf: Property =
    for {
      n <- Gen.int(Range.linear(0, Int.MaxValue)).log("n")
    } yield {

//      implicit val executorService: ExecutorService = Executors.newWorkStealingPool(IoAppUtils.computeWorkerThreadCount)

//      implicit val executionContext: ExecutionContext =
//        ConcurrentSupport.newExecutionContextWithLogger(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      implicit val runtime: IORuntime = IoAppUtils.runtime()
//      implicit val runtime: IORuntime = IoAppUtils.runtime(executorService)
//      implicit val runtime: IORuntime = cats.effect.unsafe.IORuntime.global

      val result = Fx[IO]
        .effectOf {
          for {
            id1 <- effectOf(Thread.currentThread().getName).start
            _   <- Temporal[IO].sleep(sleepFor)
            id2 <- effectOf(Thread.currentThread().getName).start
            r1  <- id1.join
            r2  <- id2.join
            rr1 <- r1 match {
                     case Outcome.Succeeded(id) => id
                     case Outcome.Errored(err) => errorOf(err)
                     case Outcome.Canceled() => errorOf(new RuntimeException("Cancelled"))
                   }
            rr2 <- r2 match {
                     case Outcome.Succeeded(id) => id
                     case Outcome.Errored(err) => errorOf(err)
                     case Outcome.Canceled() => errorOf(new RuntimeException("Cancelled"))
                   }
          } yield (rr1, rr2)
        }
        .flatMap(identity)
//        .effectOf(Thread.currentThread().getName)
//        .product(Temporal[IO].sleep(sleepFor) *> Fx[IO].effectOf(Thread.currentThread().getName))
        .map {
          case (id1, id2) =>
            println(s"   IO>> $id1, $id2")
            Result.diffNamed("id1 should equal to id2", id1, id2)(_ =!= _)
        }
        .unsafeRunSync()

//      val result = Fx[IO]
//        .effectOf {
////          val id1 = Thread.currentThread().getId
////          Thread.sleep(1000)
////          val id2 = Thread.currentThread().getId
////          (id1, id2)
//          for {
//            id1 <- Fx[IO].effectOf(Thread.currentThread().getName)
//            _ = println(s"${n.toString}-id1: $id1")
//            _   <- Temporal[IO].sleep(sleepFor)
//            id2 <- Fx[IO].effectOf(Thread.currentThread().getName)
//            _ = println(s"${n.toString}-id2: $id2")
//          } yield (id1, id2)
//        }
//        .flatMap(identity)
//        .map { case (id1, id2) => Result.diffNamed("id1 should not equal to id2", id1, id2)(_ =!= _) }
//        .unsafeRunSync()
//      runtime.shutdown()
      result
    }

  def testBlockingOf: Property =
    for {
      n <- Gen.int(Range.linear(0, Int.MaxValue)).log("n")
    } yield {
      import effectie.instances.ce3.canBlock._

//      implicit val executorService: ExecutorService = Executors.newWorkStealingPool(2)

//      implicit val executionContext: ExecutionContext =
//        ConcurrentSupport.newExecutionContextWithLogger(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

//      implicit val runtime: IORuntime = IoAppUtils.runtime(executorService)
//      implicit val runtime: IORuntime = cats.effect.unsafe.IORuntime.global
      implicit val runtime: IORuntime = IoAppUtils.runtime()

      val result =
//        CanBlock[IO].blockingOf {
        (for {
          id1 <- CanBlock[IO].blockingOf(Thread.currentThread().getName).start
//              _ <- Temporal[IO].sleep(sleepFor)
          id2 <- CanBlock[IO].blockingOf(Thread.currentThread().getName).start
          r1  <- id1.join
          r2  <- id2.join
          rr1 <- r1 match {
                   case Outcome.Succeeded(id) => id
                   case Outcome.Errored(err) => errorOf(err)
                   case Outcome.Canceled() => errorOf(new RuntimeException("Cancelled"))
                 }
          rr2 <- r2 match {
                   case Outcome.Succeeded(id) => id
                   case Outcome.Errored(err) => errorOf(err)
                   case Outcome.Canceled() => errorOf(new RuntimeException("Cancelled"))
                 }
        } yield (rr1, rr2))
          .map {
            //            Fx[IO]
            //              .effectOf(Thread.currentThread().getName)
            ////        .product(Temporal[IO].sleep(sleepFor))
            //              .product(Temporal[IO].sleep(sleepFor) *> Fx[IO].effectOf(Thread.currentThread().getName))
//          }
//          .flatMap(identity)
            case (id1, id2) =>
              println(s"Block>> $id1, $id2")
              Result.diffNamed("id1 should equal to id2", id1, id2)(_ === _)
          }
          .unsafeRunSync()

//      val result = CanBlock[IO]
//        .blockingOf {
////          val id1 = Thread.currentThread().getId
////          Thread.sleep(100)
////          val id2 = Thread.currentThread().getId
////          (id1, id2)
//          for {
//            id1 <- Fx[IO].effectOf(Thread.currentThread().getName)
//            _ = println(s">> ${n.toString}-id1: ${id1.toString}")
//            _   <- Temporal[IO].sleep(sleepFor)
//            id2 <- Fx[IO].effectOf(Thread.currentThread().getName)
//            _ = println(s">> ${n.toString}-id2: ${id2.toString}")
//          } yield (id1, id2)
//        }
//        .flatMap(identity)
//        .map { case (id1, id2) => Result.diffNamed("id1 should equal to id2", id1, id2)(_ === _) }
//        .unsafeRunSync()
//      runtime.shutdown()
      result
    }

}
