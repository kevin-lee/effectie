package effectie.scalaz

import scalaz._
import Scalaz._
import effectie.ConcurrentSupport
import effectie.testing.types.SomeThrowableError
import hedgehog._
import hedgehog.runner._
import scalaz.effect._

import scala.concurrent.Await

import effectie.testing.tools._

/** @author Kevin Lee
  * @since 2020-12-06
  */
object FxSpec extends Properties {
  override def tests: List[Test] = List(
    property("test Fx[IO].effectOf", IoSpec.testEffectOf),
    property("test Fx[IO].pureOf", IoSpec.testPureOf),
    example("test Fx[IO].unitOf", IoSpec.testUnitOf),
    example("test Fx[IO].errorOf", IoSpec.testErrorOf),
    property("test Fx[IO] Monad laws", IoSpec.testMonadLaws),
    property("test Fx[Future].effectOf", FutureSpec.testEffectOf),
    property("test Fx[Future].pureOf", FutureSpec.testPureOf),
    example("test Fx[Future].unitOf", FutureSpec.testUnitOf),
    example("test Fx[Future].errorOf", FutureSpec.testErrorOf),
    property("test Fx[Future] Monad laws", FutureSpec.testMonadLaws),
    property("test Fx[Id].effectOf", IdSpec.testEffectOf),
    property("test Fx[Id].pureOf", IdSpec.testPureOf),
    example("test Fx[Id].unitOf", IdSpec.testUnitOf),
    example("test Fx[Id].errorOf", IdSpec.testErrorOf),
    property("test Fx[Id] Monad laws", IdSpec.testMonadLaws),
  )

  object IoSpec {

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual        = before
      val testBefore    = actual ==== before
      val io            = Fx[IO].effectOf({ actual = after; () })
      val testBeforeRun = actual ==== before
      io.unsafePerformIO()
      val testAfterRun  = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testBeforeRun.log("testBeforeRun"),
          testAfterRun.log("testAfterRun")
        )
      )
    }

    def testPureOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual        = before
      val testBefore    = actual ==== before
      val io            = Fx[IO].pureOf({ actual = after; () })
      val testBeforeRun = actual ==== after
      io.unsafePerformIO()
      val testAfterRun  = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testBeforeRun.log("testBeforeRun"),
          testAfterRun.log("testAfterRun")
        )
      )
    }

    def testUnitOf: Result = {
      val io             = Fx[IO].unitOf
      val expected: Unit = ()
      val actual: Unit   = io.unsafePerformIO()
      actual ==== expected
    }

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      val io = Fx[IO].errorOf[Unit](expectedError)
      expectThrowable(io.unsafePerformIO(), expectedError)
    }

    def testMonadLaws: Property = {

      implicit val eqIo: Equal[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).unsafePerformIO()

      implicit val ioFx: Fx[IO] = Fx.IoFx

      MonadSpec.testMonadLaws[IO]
    }

  }

  object FutureSpec {
    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration._
    import scala.concurrent.{ExecutionContext, Future}

    val waitFor: FiniteDuration = 1.second

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual               = before
      val testBefore           = actual ==== before
      val future: Future[Unit] = Fx[Future].effectOf({ actual = after; () })
      ConcurrentSupport.futureToValueAndTerminate(future, waitFor)
      val testAfterRun         = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfterRun.log("testAfterRun")
        )
      )
    }

    def testPureOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual       = before
      val testBefore   = actual ==== before
      val future       = Fx[Future].pureOf({ actual = after; () })
      ConcurrentSupport.futureToValueAndTerminate(future, waitFor)
      val testAfterRun = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfterRun.log("testAfterRun")
        )
      )
    }

    def testUnitOf: Result = {
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)
      val future                                    = Fx[Future].unitOf
      val expected: Unit                            = ()
      val actual: Unit                              = ConcurrentSupport.futureToValueAndTerminate(future, waitFor)
      actual ==== expected
    }

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(1)
      implicit val ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val future = Fx[Future].errorOf[Unit](expectedError)
      expectThrowable(ConcurrentSupport.futureToValueAndTerminate(future, waitFor), expectedError)
    }

    def testMonadLaws: Property = {

      implicit val ec: scala.concurrent.ExecutionContext                   = scala.concurrent.ExecutionContext.global
      implicit def futureEqual[A](implicit EQ: Equal[A]): Equal[Future[A]] =
        (x, y) => Await.result(x.flatMap(a => y.map(b => EQ.equal(a, b))), 1.second)

      implicit val eqFuture: Equal[Future[Int]] =
        (x, y) => {
          val future = x.flatMap(xx => y.map(_ === xx))
          Await.result(future, waitFor)
        }

      MonadSpec.testMonadLaws[Future]
    }

  }

  object IdSpec {

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual     = before
      val testBefore = actual ==== before
      Fx[Id].effectOf({ actual = after; () })
      val testAfter  = actual ==== after
      testBefore.log("testBefore") ==== testAfter.log("testAfter")
    }

    def testPureOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual     = before
      val testBefore = actual ==== before
      Fx[Id].pureOf({ actual = after; () })
      val testAfter  = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfter.log("testAfter")
        )
      )
    }

    def testUnitOf: Result = {
      val expected: Unit = ()
      val actual         = Fx[Id].unitOf
      actual ==== expected
    }

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      lazy val actual = Fx[Id].errorOf[Unit](expectedError)
      expectThrowable(actual, expectedError)
    }

    def testMonadLaws: Property = {
      implicit val idInstance: Monad[Id] = scalaz.Scalaz.id
      MonadSpec.testMonadLaws[Id]
    }

  }

}
