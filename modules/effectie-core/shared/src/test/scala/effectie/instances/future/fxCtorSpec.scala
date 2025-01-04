package effectie.instances.future

import cats.syntax.all._
import effectie.core.FxCtor
import effectie.testing.{FutureTools, tools}
import effectie.testing.types.SomeThrowableError

import hedgehog._
import hedgehog.runner._

import scala.util.Try

/** @author Kevin Lee
  * @since 2022-01-06
  */
object fxCtorSpec extends Properties {

  override def tests: List[Test] = futureSpecs

  val futureSpecs: List[Test] = List(
    property("test FxCtor[Future].effectOf", FutureSpec.testEffectOf),
    property("test FxCtor[Future].fromEffect(effectOf)", FutureSpec.testFromEffect),
    property("test FxCtor[Future].fromEffect(pureOf)", FutureSpec.testFromEffectWithPure),
    property("test FxCtor[Future].pureOf", FutureSpec.testPureOf),
    property("test FxCtor[Future].pureOrCatchNonFatal(success case)", FutureSpec.testPureOrErrorSuccessCase),
    example("test FxCtor[Future].pureOrCatchNonFatal(error case)", FutureSpec.testPureOrErrorErrorCase),
    example("test FxCtor[Future].unitOf", FutureSpec.testUnitOf),
    example("test FxCtor[Future].errorOf", FutureSpec.testErrorOf),
    property("test fx.pureOfOption[Future]", FutureSpec.testPureOfOption),
    property("test fx.pureOfSome[Future]", FutureSpec.testPureOfSome),
    example("test fx.pureOfNone[Future]", FutureSpec.testPureOfNone),
    property("test fx.pureOfRight[Future]", FutureSpec.testPureOfRight),
    property("test fx.pureOfLeft[Future]", FutureSpec.testPureOfLeft),
    property("test FxCtor[Future].fromEither(Right)", FutureSpec.testFromEitherRightCase),
    property("test FxCtor[Future].fromEither(Left)", FutureSpec.testFromEitherLeftCase),
    property("test FxCtor[Future].fromOption(Some)", FutureSpec.testFromOptionSomeCase),
    property("test FxCtor[Future].fromOption(None)", FutureSpec.testFromOptionNoneCase),
    property("test FxCtor[Future].fromTry(Success)", FutureSpec.testFromTrySuccessCase),
    property("test FxCtor[Future].fromTry(Failure)", FutureSpec.testFromTryFailureCase),
  )

  object FutureSpec extends FutureTools {
    import effectie.instances.future.fxCtor._

    import scala.concurrent.duration._
    import scala.concurrent.{ExecutionContext, Future}

    implicit val ec: ExecutionContext = globalExecutionContext

    val waitFor = WaitFor(1.second)

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual               = before // scalafix:ok DisableSyntax.var
      val testBefore           = actual ==== before
      val future: Future[Unit] = FxCtor[Future].effectOf({ actual = after; () })
      futureToValue(future, waitFor)
      val testAfterRun         = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfterRun.log("testAfterRun"),
        )
      )
    }

    def testFromEffect: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual     = before // scalafix:ok DisableSyntax.var
      val testBefore = actual ==== before

      lazy val fromFuture = FxCtor[Future].fromEffect(FxCtor[Future].effectOf({ actual = after; () }))

      val testAfterFrom = actual ==== before
      futureToValue(fromFuture, waitFor)
      val testAfterRun  = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfterFrom.log("testAfterFrom"),
          testAfterRun.log("testAfterRun"),
        )
      )
    }

    def testFromEffectWithPure: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual       = before // scalafix:ok DisableSyntax.var
      val testBefore   = actual ==== before
      val fromFuture   = FxCtor[Future].fromEffect(FxCtor[Future].pureOf({ actual = after; () }))
      futureToValue(fromFuture, waitFor)
      val testAfterRun = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfterRun.log("testAfterRun"),
        )
      )
    }

    def testPureOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual       = before // scalafix:ok DisableSyntax.var
      val testBefore   = actual ==== before
      val future       = FxCtor[Future].pureOf({ actual = after; () })
      futureToValue(future, waitFor)
      val testAfterRun = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfterRun.log("testAfterRun"),
        )
      )
    }

    def testPureOrErrorSuccessCase: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual       = before // scalafix:ok DisableSyntax.var
      val testBefore   = actual ==== before
      val future       = FxCtor[Future].pureOrError({ actual = after; () })
      futureToValue(future, waitFor)
      val testAfterRun = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfterRun.log("testAfterRun"),
        )
      )
    }

    def testPureOrErrorErrorCase: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      val future = FxCtor[Future].pureOrError[Unit](throw expectedError) // scalafix:ok DisableSyntax.throw
      tools.expectThrowable(
        futureToValue(future, waitFor),
        expectedError,
      )
    }

    def testUnitOf: Result = {

      val future         = FxCtor[Future].unitOf
      val expected: Unit = ()
      val actual: Unit   = futureToValue(future, waitFor)
      actual ==== expected
    }

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      val future = FxCtor[Future].errorOf[Unit](expectedError)
      tools.expectThrowable(
        futureToValue(future, waitFor),
        expectedError,
      )
    }

    def testPureOfOption: Property =
      for {
        s <- Gen
               .string(Gen.unicode, Range.linear(1, 10))
               .option
               .log("s")
      } yield {

        val expected = s
        val input    = s.orNull
        val future   = FxCtor[Future].pureOfOption(input)
        val actual   = futureToValue(future, waitFor)

        Result.all(
          List(
            actual ==== expected
          )
        )
      }

    def testPureOfSome: Property = for {
      s <- Gen
             .string(Gen.unicode, Range.linear(1, 10))
             .log("s")
    } yield {

      val expected = s.some
      val future   = FxCtor[Future].pureOfSome(s)
      val actual   = futureToValue(future, waitFor)

      Result.all(
        List(
          actual ==== expected
        )
      )
    }

    def testPureOfNone: Result = {
      val expected = none[String]

      val future = FxCtor[Future].pureOfNone[String]
      val actual = futureToValue(future, waitFor)

      actual ==== expected
    }

    def testPureOfRight: Property =
      for {
        n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
      } yield {
        val expected = n.asRight[String]

        val future = FxCtor[Future].pureOfRight[String](n)
        val actual = futureToValue(future, waitFor)

        Result.all(
          List(
            actual ==== expected
          )
        )
      }

    def testPureOfLeft: Property =
      for {
        s <- Gen
               .string(Gen.unicode, Range.linear(1, 10))
               .log("s")
      } yield {
        val expected = s.asLeft[Int]

        val future = FxCtor[Future].pureOfLeft[Int](s)
        val actual = futureToValue(future, waitFor)

        Result.all(
          List(
            actual ==== expected
          )
        )
      }

    def testFromEitherRightCase: Property = for {
      n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
    } yield {

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      val expected            = n.asRight[SomeThrowableError]
      val future: Future[Int] = FxCtor[Future].fromEither(expected)

      val actual = Try(futureToValue(future, waitFor)).toEither
      (actual ==== expected).log(s"$actual does not equal to $expected")
    }

    def testFromEitherLeftCase: Property = for {
      errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
    } yield {

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      val expected            = SomeThrowableError.message(errorMessage).asLeft[Int]
      val future: Future[Int] = FxCtor[Future].fromEither(expected)

      val actual = Try(futureToValue(future, waitFor)).toEither
      (actual ==== expected).log(s"$actual does not equal to $expected")
    }

    def testFromOptionSomeCase: Property = for {
      n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
    } yield {

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      val expected            = n.asRight[SomeThrowableError]
      val input               = n.some
      val future: Future[Int] =
        FxCtor[Future].fromOption(input)(SomeThrowableError.Message("This should never happen!"))

      val actual = Try(futureToValue(future, waitFor)).toEither
      (actual ==== expected).log(s"$actual does not equal to $expected")
    }

    def testFromOptionNoneCase: Property = for {
      errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
    } yield {

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      val expected            = SomeThrowableError.message(errorMessage).asLeft[Int]
      val future: Future[Int] = FxCtor[Future].fromOption(none[Int])(SomeThrowableError.Message(errorMessage))

      val actual = Try(futureToValue(future, waitFor)).toEither
      (actual ==== expected).log(s"$actual does not equal to $expected")
    }

    def testFromTrySuccessCase: Property = for {
      n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
    } yield {

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      val expected            = n.asRight[SomeThrowableError]
      val input: Try[Int]     = scala.util.Success(n)
      val future: Future[Int] = FxCtor[Future].fromTry(input)

      val actual = Try(futureToValue(future, waitFor)).toEither
      (actual ==== expected).log(s"$actual does not equal to $expected")
    }

    def testFromTryFailureCase: Property = for {
      errorMessage <- Gen.string(Gen.unicode, Range.linear(1, 10)).log("errorMessage")
    } yield {

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      val expected            = SomeThrowableError.message(errorMessage).asLeft[Int]
      val input: Try[Int]     = scala.util.Failure(SomeThrowableError.message(errorMessage))
      val future: Future[Int] = FxCtor[Future].fromTry(input)

      val actual = Try(futureToValue(future, waitFor)).toEither
      (actual ==== expected).log(s"$actual does not equal to $expected")
    }

  }

}
