package effectie.scalaz


import scalaz._
import Scalaz._
import scalaz.effect._

import effectie.ConcurrentSupport

import hedgehog._
import hedgehog.runner._

/**
  * @author Kevin Lee
  * @since 2020-12-06
  */
object EffectConstructorSpec extends Properties {
  override def tests: List[Test] = List(
    property("test EffectConstructor[IO].effectOf", IoSpec.testEffectOf),
    property("test EffectConstructor[IO].pureOf", IoSpec.testPureOf),
    example("test EffectConstructor[IO].unitOf", IoSpec.testUnitOf),

    property("test EffectConstructor[Future].effectOf", FutureSpec.testEffectOf),
    property("test EffectConstructor[Future].pureOf", FutureSpec.testPureOf),
    example("test EffectConstructor[Future].unitOf", FutureSpec.testUnitOf),

    property("test EffectConstructor[Id].effectOf", IdSpec.testEffectOf),
    property("test EffectConstructor[Id].pureOf", IdSpec.testPureOf),
    example("test EffectConstructor[Id].unitOf", IdSpec.testUnitOf)
  )

  object IoSpec {

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual = before
      val testBefore = actual ==== before
      val io = EffectConstructor[IO].effectOf({ actual = after; ()})
      val testBeforeRun = actual ==== before
      io.unsafePerformIO()
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
      val io = EffectConstructor[IO].pureOf({ actual = after; ()})
      val testBeforeRun = actual ==== after
      io.unsafePerformIO()
      val testAfterRun = actual ==== after
      Result.all(List(
        testBefore.log("testBefore"),
        testBeforeRun.log("testBeforeRun"),
        testAfterRun.log("testAfterRun")
      ))
    }

    def testUnitOf: Result = {
      val io = EffectConstructor[IO].unitOf
      val expected: Unit = ()
      val actual: Unit = io.unsafePerformIO()
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
      val future: Future[Unit] = EffectConstructor[Future].effectOf({ actual = after; ()})
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
      val future = EffectConstructor[Future].pureOf({ actual = after; ()})
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
      val future = EffectConstructor[Future].unitOf
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
      EffectConstructor[Id].effectOf({ actual = after; ()})
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
      EffectConstructor[Id].pureOf({ actual = after; ()})
      val testAfter = actual ==== after
      Result.all(List(
        testBefore.log("testBefore"),
        testAfter.log("testAfter")
      ))
    }

    def testUnitOf: Result = {
      val expected: Unit = ()
      val actual: Unit = EffectConstructor[Id].unitOf
      actual ==== expected
    }

  }

}
