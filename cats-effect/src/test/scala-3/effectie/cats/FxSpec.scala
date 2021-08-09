package effectie.cats

import cats.effect.*
import cats.{Eq, Id}
import effectie.ConcurrentSupport
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
    property("test Fx[IO] Monad laws", IoSpec.testMonadLaws),
    property("test Fx[Future].effectOf", FutureSpec.testEffectOf),
    property("test Fx[Future].pureOf", FutureSpec.testPureOf),
    example("test Fx[Future].unitOf", FutureSpec.testUnitOf),
    property("test Fx[Future] Monad laws", FutureSpec.testMonadLaws),
    property("test Fx[Id].effectOf", IdSpec.testEffectOf),
    property("test Fx[Id].pureOf", IdSpec.testPureOf),
    example("test Fx[Id].unitOf", IdSpec.testUnitOf),
    property("test Fx[Id] Monad laws", IdSpec.testMonadLaws),
  )

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

    def testMonadLaws: Property = {
      import cats.syntax.eq.*

      implicit val eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).unsafeRunSync()

      implicit val ioFx: Fx[IO] = Fx.ioFx

      MonadSpec.testMonadLaws[IO]
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
      given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

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
      given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

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
      given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)
      val future                             = Fx[Future].unitOf
      val expected: Unit                     = ()
      val actual: Unit                       = ConcurrentSupport.futureToValueAndTerminate(future, waitFor)
      actual ==== expected
    }

    def testMonadLaws: Property = {
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

      MonadSpec.testMonadLaws[Future]
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

    def testMonadLaws: Property = MonadSpec.testMonadLaws[Id]

  }

}
