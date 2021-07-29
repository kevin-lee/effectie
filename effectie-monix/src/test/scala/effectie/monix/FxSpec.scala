package effectie.monix

import cats.Id
import effectie.ConcurrentSupport
import hedgehog._
import hedgehog.runner._
import monix.eval.Task

/**
  * @author Kevin Lee
  * @since 2020-12-06
  */
object FxSpec extends Properties {
  override def tests: List[Test] = List(
    property("test Fx[Task].effectOf", TaskSpec.testEffectOf),
    property("test Fx[Task].pureOf", TaskSpec.testPureOf),
    example("test Fx[Task].unitOf", TaskSpec.testUnitOf),

    property("test Fx[Future].effectOf", FutureSpec.testEffectOf),
    property("test Fx[Future].pureOf", FutureSpec.testPureOf),
    example("test Fx[Future].unitOf", FutureSpec.testUnitOf),

    property("test Fx[Id].effectOf", IdSpec.testEffectOf),
    property("test Fx[Id].pureOf", IdSpec.testPureOf),
    example("test Fx[Id].unitOf", IdSpec.testUnitOf)
  )

  object TaskSpec {
    import monix.execution.Scheduler.Implicits.global

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual = before
      val testBefore = actual ==== before
      val task = Fx[Task].effectOf({ actual = after; ()})
      val testBeforeRun = actual ==== before
      task.runSyncUnsafe()
      val testAfterRun = actual ==== after
      Result.all(List(
        testBefore.log("testBefore"),
        testBeforeRun.log("testBeforeRun"),
        testAfterRun.log("testAfterRun")
      ))
    }

    def testPureOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual = before
      val testBefore = actual ==== before
      val task = Fx[Task].pureOf({ actual = after; ()})
      val testBeforeRun = actual ==== after
      task.runSyncUnsafe()
      val testAfterRun = actual ==== after
      Result.all(List(
        testBefore.log("testBefore"),
        testBeforeRun.log("testBeforeRun"),
        testAfterRun.log("testAfterRun")
      ))
    }

    def testUnitOf: Result = {
      val task = Fx[Task].unitOf
      val expected: Unit = ()
      val actual: Unit = task.runSyncUnsafe()
      actual ==== expected
    }

  }

  object FutureSpec {
    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration._
    import scala.concurrent.{ExecutionContext, Future}

    val waitFor: FiniteDuration = 1.second

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = ConcurrentSupport.newExecutionContext(executorService)

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual = before
      val testBefore = actual ==== before
      val future: Future[Unit] = Fx[Future].effectOf({ actual = after; ()})
      ConcurrentSupport.futureToValueAndTerminate(future, waitFor)
      val testAfterRun = actual ==== after
      Result.all(List(
        testBefore.log("testBefore"),
        testAfterRun.log("testAfterRun")
      ))
    }

    def testPureOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = ConcurrentSupport.newExecutionContext(executorService)

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual = before
      val testBefore = actual ==== before
      val future = Fx[Future].pureOf({ actual = after; ()})
      ConcurrentSupport.futureToValueAndTerminate(future, waitFor)
      val testAfterRun = actual ==== after
      Result.all(List(
        testBefore.log("testBefore"),
        testAfterRun.log("testAfterRun")
      ))
    }

    def testUnitOf: Result = {
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext = ConcurrentSupport.newExecutionContext(executorService)
      val future = Fx[Future].unitOf
      val expected: Unit = ()
      val actual: Unit = ConcurrentSupport.futureToValueAndTerminate(future, waitFor)
      actual ==== expected
    }

  }

  object IdSpec {

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual = before
      val testBefore = actual ==== before
      Fx[Id].effectOf({ actual = after; ()})
      val testAfter = actual ==== after
      testBefore.log("testBefore") ==== testAfter.log("testAfter")
    }

    def testPureOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual = before
      val testBefore = actual ==== before
      Fx[Id].pureOf({ actual = after; ()})
      val testAfter = actual ==== after
      Result.all(List(
        testBefore.log("testBefore"),
        testAfter.log("testAfter")
      ))
    }

    def testUnitOf: Result = {
      val expected: Unit = ()
      val actual = Fx[Id].unitOf
      actual ==== expected
    }

  }

}