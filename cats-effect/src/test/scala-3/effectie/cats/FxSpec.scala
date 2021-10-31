package effectie.cats

import cats.effect.*
import cats.{Eq, Id, Monad}
import effectie.ConcurrentSupport
import effectie.testing.tools.*
import effectie.testing.types.SomeThrowableError
import hedgehog.*
import hedgehog.runner.*

import scala.concurrent.Await

/** @author Kevin Lee
  * @since 2020-12-06
  */
object FxSpec extends Properties {
  override def tests: List[Test] = List(
    property("test Fx[IO].effectOf", IoSpec.testEffectOf),
    property("test Fx[IO].pureOf", IoSpec.testPureOf),
    example("test Fx[IO].unitOf", IoSpec.testUnitOf),
    example("test Fx[IO].errorOf", IoSpec.testErrorOf),
  ) ++
    IoSpec.testMonadLaws ++
    List(
      property("test Fx[Future].effectOf", FutureSpec.testEffectOf),
      property("test Fx[Future].pureOf", FutureSpec.testPureOf),
      example("test Fx[Future].unitOf", FutureSpec.testUnitOf),
      example("test Fx[Future].errorOf", FutureSpec.testErrorOf),
    ) ++
    FutureSpec.testMonadLaws ++
    List(
      property("test Fx[Id].effectOf", IdSpec.testEffectOf),
      property("test Fx[Id].pureOf", IdSpec.testPureOf),
      example("test Fx[Id].unitOf", IdSpec.testUnitOf),
      example("test Fx[Id].errorOf", IdSpec.testErrorOf),
    ) ++
    IdSpec.testMonadLaws

  object IoSpec {

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      var actual        = before
      val testBefore    = actual ==== before
      val io            = Fx[IO].effectOf({ actual = after; () })
      val testBeforeRun = actual ==== before
      io.unsafeRunSync()
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
      var actual        = before
      val testBefore    = actual ==== before
      val io            = Fx[IO].pureOf({ actual = after; () })
      val testBeforeRun = actual ==== after
      io.unsafeRunSync()
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
      val actual: Unit   = io.unsafeRunSync()
      actual ==== expected
    }

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      val io = Fx[IO].errorOf(expectedError)
      expectThrowable(io.unsafeRunSync(), expectedError)
    }

    def testMonadLaws: List[Test] = {
      import cats.syntax.eq.*

      implicit val eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).unsafeRunSync()

      implicit val ioFx: Fx[IO] = Fx.ioFx

      MonadSpec.testMonadLaws[IO]("IO")
    }

  }

  object FutureSpec {
    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration.*
    import scala.concurrent.{ExecutionContext, Future}

    val waitFor: FiniteDuration = 1.second

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ExecutionContext                 = ConcurrentSupport.newExecutionContext(executorService)

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
      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ExecutionContext                 = ConcurrentSupport.newExecutionContext(executorService)

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
      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ExecutionContext                 = ConcurrentSupport.newExecutionContext(executorService)
      val future                             = Fx[Future].unitOf
      val expected: Unit                     = ()
      val actual: Unit                       = ConcurrentSupport.futureToValueAndTerminate(future, waitFor)
      actual ==== expected
    }

    def testErrorOf: Result = {
      val expectedMessage                    = "This is a throwable test error."
      val expectedError                      = SomeThrowableError.message(expectedMessage)
      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ExecutionContext                 = ConcurrentSupport.newExecutionContext(executorService)

      val future = Fx[Future].errorOf(expectedError)
      expectThrowable(ConcurrentSupport.futureToValueAndTerminate(future, waitFor), expectedError)
    }

    def testMonadLaws: List[Test] = {
      import cats.syntax.eq.*

      implicit val ec: scala.concurrent.ExecutionContext             = scala.concurrent.ExecutionContext.global
      implicit def futureEqual[A](implicit EQ: Eq[A]): Eq[Future[A]] = new Eq[Future[A]] {
        override def eqv(x: Future[A], y: Future[A]): Boolean =
          Await.result(x.flatMap(a => y.map(b => EQ.eqv(a, b))), 1.second)
      }
      implicit val eqFuture: Eq[Future[Int]]                         =
        (x, y) => {
          val future = x.flatMap(xx => y.map(_ === xx))
          Await.result(future, waitFor)
        }

      MonadSpec.testMonadLaws[Future]("Future")
    }

  }

  object IdSpec {

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
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
      lazy val actual     = Fx[Id].errorOf(expectedError)
      expectThrowable(actual, expectedError)
    }

    def testMonadLaws: List[Test] = {
      given idMonad: Monad[Id] = cats.catsInstancesForId
      MonadSpec.testMonadLaws[Id]("Id")
    }

  }

}
