package effectie.cats

import cats.effect._
import cats.effect.testkit.TestContext
import cats.{Eq, Id, Monad}
import effectie.ConcurrentSupport
import hedgehog._
import hedgehog.runner._

import scala.concurrent.Await
import effectie.testing.tools._
import effectie.testing.types.SomeThrowableError

/** @author Kevin Lee
  * @since 2020-12-06
  */
object FxSpec extends Properties {
  override def tests: List[Test] = List(
    property("test Fx[IO].effectOf", IoSpec.testEffectOf),
    property("test Fx[IO].pureOf", IoSpec.testPureOf),
    example("test Fx[IO].unitOf", IoSpec.testUnitOf),
    example("test Fx[IO].errorOf", IoSpec.testErrorOf),
    property("test Fx[IO] Monad laws - Identity", IoSpec.testMonadLaws1_Identity),
    property("test Fx[IO] Monad laws - Composition", IoSpec.testMonadLaws2_Composition),
    property("test Fx[IO] Monad laws - IdentityAp", IoSpec.testMonadLaws3_IdentityAp),
    property("test Fx[IO] Monad laws - Homomorphism", IoSpec.testMonadLaws4_Homomorphism),
    property("test Fx[IO] Monad laws - Interchange", IoSpec.testMonadLaws5_Interchange),
    property("test Fx[IO] Monad laws - CompositionAp", IoSpec.testMonadLaws6_CompositionAp),
    property("test Fx[IO] Monad laws - LeftIdentity", IoSpec.testMonadLaws7_LeftIdentity),
    property("test Fx[IO] Monad laws - RightIdentity", IoSpec.testMonadLaws8_RightIdentity),
    property("test Fx[IO] Monad laws - Associativity", IoSpec.testMonadLaws9_Associativity),
    property("test Fx[Future].effectOf", FutureSpec.testEffectOf),
    property("test Fx[Future].pureOf", FutureSpec.testPureOf),
    example("test Fx[Future].unitOf", FutureSpec.testUnitOf),
    example("test Fx[Future].errorOf", FutureSpec.testErrorOf),
    property("test Fx[Future] Monad laws - Identity", FutureSpec.testMonadLaws1_Identity),
    property("test Fx[Future] Monad laws - Composition", FutureSpec.testMonadLaws2_Composition),
    property("test Fx[Future] Monad laws - IdentityAp", FutureSpec.testMonadLaws3_IdentityAp),
    property("test Fx[Future] Monad laws - Homomorphism", FutureSpec.testMonadLaws4_Homomorphism),
    property("test Fx[Future] Monad laws - Interchange", FutureSpec.testMonadLaws5_Interchange),
    property("test Fx[Future] Monad laws - CompositionAp", FutureSpec.testMonadLaws6_CompositionAp),
    property("test Fx[Future] Monad laws - LeftIdentity", FutureSpec.testMonadLaws7_LeftIdentity),
    property("test Fx[Future] Monad laws - RightIdentity", FutureSpec.testMonadLaws8_RightIdentity),
    property("test Fx[Future] Monad laws - Associativity", FutureSpec.testMonadLaws9_Associativity),
    property("test Fx[Id].effectOf", IdSpec.testEffectOf),
    property("test Fx[Id].pureOf", IdSpec.testPureOf),
    example("test Fx[Id].unitOf", IdSpec.testUnitOf),
    example("test Fx[Id].errorOf", IdSpec.testErrorOf),
    property("test Fx[Id] Monad laws - Identity", IdSpec.testMonadLaws1_Identity),
    property("test Fx[Id] Monad laws - Composition", IdSpec.testMonadLaws2_Composition),
    property("test Fx[Id] Monad laws - IdentityAp", IdSpec.testMonadLaws3_IdentityAp),
    property("test Fx[Id] Monad laws - Homomorphism", IdSpec.testMonadLaws4_Homomorphism),
    property("test Fx[Id] Monad laws - Interchange", IdSpec.testMonadLaws5_Interchange),
    property("test Fx[Id] Monad laws - CompositionAp", IdSpec.testMonadLaws6_CompositionAp),
    property("test Fx[Id] Monad laws - LeftIdentity", IdSpec.testMonadLaws7_LeftIdentity),
    property("test Fx[Id] Monad laws - RightIdentity", IdSpec.testMonadLaws8_RightIdentity),
    property("test Fx[Id] Monad laws - Associativity", IdSpec.testMonadLaws9_Associativity),
  )

  object IoSpec {

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      import CatsEffectRunner._
      implicit val ticket: Ticker = Ticker(TestContext())

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual        = before
      val testBefore    = actual ==== before
      val io            = Fx[IO].effectOf({ actual = after; () })
      val testBeforeRun = actual ==== before

      val done         = io.completeAs(())
      val testAfterRun = actual ==== after

      Result.all(
        List(
          done,
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
      import CatsEffectRunner._
      implicit val ticket: Ticker = Ticker(TestContext())

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual        = before
      val testBefore    = actual ==== before
      val io            = Fx[IO].pureOf({ actual = after; () })
      val testBeforeRun = actual ==== after

      val done         = io.completeAs(())
      val testAfterRun = actual ==== after
      Result.all(
        List(
          done,
          testBefore.log("testBefore"),
          testBeforeRun.log("testBeforeRun"),
          testAfterRun.log("testAfterRun")
        )
      )
    }

    def testUnitOf: Result = {
      import CatsEffectRunner._
      implicit val ticket: Ticker = Ticker(TestContext())
      val io                      = Fx[IO].unitOf
      val expected: Unit          = ()
      io.completeAs(expected)
    }

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      import CatsEffectRunner._
      implicit val ticket: Ticker = Ticker(TestContext())

      val io            = Fx[IO].errorOf[Unit](expectedError)
      io.expectError(expectedError)
    }

    def testMonadLaws1_Identity: Property = {
      import CatsEffectRunner._
      import cats.syntax.eq._
      implicit val ticket: Ticker = Ticker(TestContext())

      implicit val eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).completeAndEqualTo(true)

      implicit val ioFx: Fx[IO] = Fx.IoFx

      MonadSpec.test1_Identity[IO]
    }

    def testMonadLaws2_Composition: Property = {
      import CatsEffectRunner._
      import cats.syntax.eq._
      implicit val ticket: Ticker = Ticker(TestContext())

      implicit val eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).completeAndEqualTo(true)

      implicit val ioFx: Fx[IO] = Fx.IoFx

      MonadSpec.test2_Composition[IO]
    }

    def testMonadLaws3_IdentityAp: Property = {
      import CatsEffectRunner._
      import cats.syntax.eq._
      implicit val ticket: Ticker = Ticker(TestContext())

      implicit val eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).completeAndEqualTo(true)

      implicit val ioFx: Fx[IO] = Fx.IoFx

      MonadSpec.test3_IdentityAp[IO]
    }

    def testMonadLaws4_Homomorphism: Property = {
      import CatsEffectRunner._
      import cats.syntax.eq._
      implicit val ticket: Ticker = Ticker(TestContext())

      implicit val eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).completeAndEqualTo(true)

      implicit val ioFx: Fx[IO] = Fx.IoFx

      MonadSpec.test4_Homomorphism[IO]
    }

    def testMonadLaws5_Interchange: Property = {
      import CatsEffectRunner._
      import cats.syntax.eq._
      implicit val ticket: Ticker = Ticker(TestContext())

      implicit val eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).completeAndEqualTo(true)

      implicit val ioFx: Fx[IO] = Fx.IoFx

      MonadSpec.test5_Interchange[IO]
    }

    def testMonadLaws6_CompositionAp: Property = {
      import CatsEffectRunner._
      import cats.syntax.eq._
      implicit val ticket: Ticker = Ticker(TestContext())

      implicit val eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).completeAndEqualTo(true)

      implicit val ioFx: Fx[IO] = Fx.IoFx

      MonadSpec.test6_CompositionAp[IO]
    }

    def testMonadLaws7_LeftIdentity: Property = {
      import CatsEffectRunner._
      import cats.syntax.eq._
      implicit val ticket: Ticker = Ticker(TestContext())

      implicit val eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).completeAndEqualTo(true)

      implicit val ioFx: Fx[IO] = Fx.IoFx

      MonadSpec.test7_LeftIdentity[IO]
    }

    def testMonadLaws8_RightIdentity: Property = {
      import CatsEffectRunner._
      import cats.syntax.eq._
      implicit val ticket: Ticker = Ticker(TestContext())

      implicit val eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).completeAndEqualTo(true)

      implicit val ioFx: Fx[IO] = Fx.IoFx

      MonadSpec.test8_RightIdentity[IO]
    }

    def testMonadLaws9_Associativity: Property = {
      import CatsEffectRunner._
      import cats.syntax.eq._
      implicit val ticket: Ticker = Ticker(TestContext())

      implicit val eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).completeAndEqualTo(true)

      implicit val ioFx: Fx[IO] = Fx.IoFx

      MonadSpec.test9_Associativity[IO]
    }

  }

  object FutureSpec {
    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration._
    import scala.concurrent.{ExecutionContext, Future}

    val waitFor: FiniteDuration = 1.second

    implicit def futureEqual[A: Eq](
      implicit ec: scala.concurrent.ExecutionContext
    ): Eq[Future[A]] =
      (x: Future[A], y: Future[A]) => Await.result(x.flatMap(a => y.map(b => Eq[A].eqv(a, b))), waitFor)

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

    def testMonadLaws1_Identity: Property = {
      implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

      MonadSpec.test1_Identity[Future]
    }

    def testMonadLaws2_Composition: Property = {
      implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

      MonadSpec.test2_Composition[Future]
    }

    def testMonadLaws3_IdentityAp: Property = {
      implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

      MonadSpec.test3_IdentityAp[Future]
    }

    def testMonadLaws4_Homomorphism: Property = {
      implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

      MonadSpec.test4_Homomorphism[Future]
    }

    def testMonadLaws5_Interchange: Property = {
      implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

      MonadSpec.test5_Interchange[Future]
    }

    def testMonadLaws6_CompositionAp: Property = {
      implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

      MonadSpec.test6_CompositionAp[Future]
    }

    def testMonadLaws7_LeftIdentity: Property = {
      implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

      MonadSpec.test7_LeftIdentity[Future]
    }

    def testMonadLaws8_RightIdentity: Property = {
      implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

      MonadSpec.test8_RightIdentity[Future]
    }

    def testMonadLaws9_Associativity: Property = {
      implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

      MonadSpec.test9_Associativity[Future]
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

    implicit val idInstance: Monad[Id] = cats.catsInstancesForId

    def testMonadLaws1_Identity: Property =
      MonadSpec.test1_Identity[Id]

    def testMonadLaws2_Composition: Property =
      MonadSpec.test2_Composition[Id]

    def testMonadLaws3_IdentityAp: Property =
      MonadSpec.test3_IdentityAp[Id]

    def testMonadLaws4_Homomorphism: Property =
      MonadSpec.test4_Homomorphism[Id]

    def testMonadLaws5_Interchange: Property =
      MonadSpec.test5_Interchange[Id]

    def testMonadLaws6_CompositionAp: Property =
      MonadSpec.test6_CompositionAp[Id]

    def testMonadLaws7_LeftIdentity: Property =
      MonadSpec.test7_LeftIdentity[Id]

    def testMonadLaws8_RightIdentity: Property =
      MonadSpec.test8_RightIdentity[Id]

    def testMonadLaws9_Associativity: Property =
      MonadSpec.test9_Associativity[Id]

  }

}
