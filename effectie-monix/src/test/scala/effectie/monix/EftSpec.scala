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
object EftSpec extends Properties {
  override def tests: List[Test] = List(
    property("test Eft[Task].effectOf", TaskSpec.testEffectOf),
    property("test Eft[Task].pureOf", TaskSpec.testPureOf),
    example("test Eft[Task].unitOf", TaskSpec.testUnitOf),

    property("test Eft[Future].effectOf", FutureSpec.testEffectOf),
    property("test Eft[Future].pureOf", FutureSpec.testPureOf),
    example("test Eft[Future].unitOf", FutureSpec.testUnitOf),

    property("test Eft[Id].effectOf", IdSpec.testEffectOf),
    property("test Eft[Id].pureOf", IdSpec.testPureOf),
    example("test Eft[Id].unitOf", IdSpec.testUnitOf)
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
      val task = Eft[Task].effectOf({ actual = after; ()})
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
      val task = Eft[Task].pureOf({ actual = after; ()})
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
      val task = Eft[Task].unitOf
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
      val future: Future[Unit] = Eft[Future].effectOf({ actual = after; ()})
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
      val future = Eft[Future].pureOf({ actual = after; ()})
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
      val future = Eft[Future].unitOf
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
      Eft[Id].effectOf({ actual = after; ()})
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
      Eft[Id].pureOf({ actual = after; ()})
      val testAfter = actual ==== after
      Result.all(List(
        testBefore.log("testBefore"),
        testAfter.log("testAfter")
      ))
    }

    def testUnitOf: Result = {
      val expected: Unit = ()
      val actual = Eft[Id].unitOf
      actual ==== expected
    }

  }

}
