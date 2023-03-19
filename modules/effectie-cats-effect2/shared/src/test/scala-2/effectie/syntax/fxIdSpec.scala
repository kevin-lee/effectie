package effectie.syntax

import cats.Id
import cats.syntax.all._
import effectie.core.{Fx, FxCtor}
import effectie.syntax.fx._
import effectie.testing.tools.{dropResult, expectThrowable}
import effectie.testing.types.SomeThrowableError
import hedgehog._
import hedgehog.runner._

/** @author Kevin Lee
  * @since 2021-05-16
  */
object fxIdSpec extends Properties {

  override def tests: List[Test] = List(
    property("test fx.{effectOf, pureOf, unitOf} for Id", IdSpec.testAll),
    property("test fx.effectOf[Id]", IdSpec.testEffectOf),
    property("test fx.pureOf[Id]", IdSpec.testPureOf),
    property("test fx.pureOrError[Id](success case)", IdSpec.testPureOrErrorSuccessCase),
    example("test fx.pureOrError[Id](error case)", IdSpec.testPureOrErrorErrorCase),
    example("test fx.unitOf[Id]", IdSpec.testUnitOf),
    example("test fx.errorOf[Id]", IdSpec.testErrorOf),
    property("test fx.pureOfOption[Id]", IdSpec.testPureOfOption),
    property("test fx.pureOfSome[Id]", IdSpec.testPureOfSome),
    example("test fx.pureOfNone[Id]", IdSpec.testPureOfNone),
    property("test fx.pureOfRight[Id]", IdSpec.testPureOfRight),
    property("test fx.pureOfLeft[Id]", IdSpec.testPureOfLeft),
  )

  trait FxCtorClient[F[*]] {
    def eftOf[A](a: A): F[A]
    def of[A](a: A): F[A]
    def unit: F[Unit]
  }
  object FxCtorClient {
    def apply[F[*]: FxCtorClient]: FxCtorClient[F]         = implicitly[FxCtorClient[F]]
    implicit def eftClientF[F[*]: FxCtor]: FxCtorClient[F] = new FxCtorClientF[F]
    final class FxCtorClientF[F[*]: FxCtor] extends FxCtorClient[F] {
      override def eftOf[A](a: A): F[A] = effectOf[F](a)
      override def of[A](a: A): F[A]    = pureOf[F](a)
      override def unit: F[Unit]        = unitOf[F]
    }
  }

  trait FxClient[F[*]] {
    def eftOf[A](a: A): F[A]
    def of[A](a: A): F[A]
    def unit: F[Unit]
  }
  object FxClient {
    def apply[F[*]: FxClient]: FxClient[F]         =
      implicitly[FxClient[F]]
    implicit def eftClientF[F[*]: Fx]: FxClient[F] = new FxClientF[F]
    final class FxClientF[F[*]: Fx] extends FxClient[F] {
      override def eftOf[A](a: A): F[A] = effectOf[F](a)
      override def of[A](a: A): F[A]    = pureOf[F](a)
      override def unit: F[Unit]        = unitOf[F]
    }
  }

  object IdSpec {
    import effectie.instances.id.fx._

    def testAll: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual                  = before // scalafix:ok DisableSyntax.var
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual2                 = before // scalafix:ok DisableSyntax.var
      val testBefore              = actual ==== before
      val testBefore2             = actual2 ==== before
      val eftClient               = FxCtorClient[Id]
      val effectConstructorClient = FxClient[Id]
      effectOf[Id]({ actual = after; () })
      pureOf[Id]({ actual2 = after; () })
      val n: Int                  = eftClient.eftOf(1)

      dropResult {
        eftClient.of(n)
      }
      dropResult {
        effectConstructorClient.eftOf(1)
      }
      dropResult {
        effectConstructorClient.of(1)
      }
      dropResult {

        eftClient.unit
      }
      dropResult {
        effectConstructorClient.unit
      }
      val testAfter  = actual ==== after
      val testAfter2 = actual2 ==== after
      testBefore.log("testBefore") ==== testAfter.log("testAfter") and
        testBefore2.log("testBefore2") ==== testAfter2.log("testAfter2")
    }

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual     = before // scalafix:ok DisableSyntax.var
      val testBefore = actual ==== before
      effectOf[Id]({ actual = after; () })
      val testAfter  = actual ==== after
      testBefore.log("testBefore") ==== testAfter.log("testAfter")
    }

    def testPureOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual     = before // scalafix:ok DisableSyntax.var
      val testBefore = actual ==== before
      pureOf[Id]({ actual = after; () })
      val testAfter  = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfter.log("testAfter"),
        )
      )
    }

    def testPureOrErrorSuccessCase: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual     = before // scalafix:ok DisableSyntax.var
      val testBefore = actual ==== before
      pureOrError[Id]({ actual = after; () })
      val testAfter  = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfter.log("testAfter"),
        )
      )
    }

    def testPureOrErrorErrorCase: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      lazy val actual = pureOrError[Id][Unit](throw expectedError) // scalafix:ok DisableSyntax.throw
      expectThrowable(actual, expectedError)
    }

    def testUnitOf: Result = {
      val expected: Unit = ()
      val actual         = unitOf[Id]
      actual ==== expected
    }

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      lazy val actual = errorOf[Id][Unit](expectedError)
      expectThrowable(actual, expectedError)
    }

    def testPureOfOption: Property =
      for {
        s <- Gen
               .string(Gen.unicode, Range.linear(1, 10))
               .option
               .log("s")
      } yield {
        val expected = s

        val input = s.orNull

        val actual  = input.pureOfOption[Id]
        val actual2 = pureOfOption[Id](input)
        Result.all(
          List(
            actual ==== expected,
            actual2 ==== expected,
          )
        )
      }

    def testPureOfSome: Property =
      for {
        s <- Gen
               .string(Gen.unicode, Range.linear(1, 10))
               .log("s")
      } yield {
        val expected = s.some

        val actual  = s.pureOfSome[Id]
        val actual2 = pureOfSome[Id](s)
        Result.all(
          List(
            actual ==== expected,
            actual2 ==== expected,
          )
        )
      }

    def testPureOfNone: Result = {
      val expected = none[String]

      val actual = pureOfNone[Id, String]
      actual ==== expected
    }

    def testPureOfRight: Property =
      for {
        n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
      } yield {
        val expected = n.asRight[String]

        val actual  = n.pureOfRight[Id, String]
        val actual2 = pureOfRight[Id, String](n)
        Result.all(
          List(
            actual ==== expected,
            actual2 ==== expected,
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

        val actual  = s.pureOfLeft[Id, Int]
        val actual2 = pureOfLeft[Id, Int](s)
        Result.all(
          List(
            actual ==== expected,
            actual2 ==== expected,
          )
        )
      }

  }

}
