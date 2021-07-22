package effectie.cats

import hedgehog._
import hedgehog.runner._

/** @author Kevin Lee
  * @since 2021-07-20
  */
object OptionTSupportSpec extends Properties {
  override def tests: List[Test] = List(
    property(
      "OptionTFOptionOpsSpec.testOptionT",
      OptionTFOptionOpsSpec.testOptionT
    ),
    property(
      "OptionTOptionOpsSpec.testOptionT",
      OptionTOptionOpsSpec.testOptionT
    ),
    property(
      "OptionTFAOpsSpec.testOptionT",
      OptionTFAOpsSpec.testOptionT
    ),
    property(
      "OptionTAOpsSpec.testOptionTF",
      OptionTAOpsSpec.testOptionTF
    ),
    property(
      "OptionTSupportAllSpec.testAll",
      OptionTSupportAllSpec.testAll
    )
  )

  object OptionTFOptionOpsSpec {

    import cats.data._
    import cats.effect._
    import cats.syntax.option._
    import effectie.cats.OptionTSupport.OptionTFOptionOps

    def fab[F[_]: Fx, A](oa: Option[A]): F[Option[A]] = Fx[F].effectOf(oa)

    def testOptionT: Property = for {
      n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
    } yield {
      val input         = fab[IO, Int](n.some)
      val expected      = OptionT(input)
      val expectedValue = expected.value.unsafeRunSync()

      val actual      = input.optionT
      val actualValue = actual.value.unsafeRunSync()

      Result.all(
        List(
          Result.assert(actualValue.isDefined).log(s"actualValue should be Some. actualValue: $actualValue"),
          actualValue ==== expectedValue
        )
      )
    }

  }

  object OptionTOptionOpsSpec {

    import cats.Applicative
    import cats.data.OptionT
    import cats.effect.IO
    import cats.syntax.option._
    import effectie.cats.OptionTSupport.OptionTOptionOps

    def testOptionT: Property = for {
      n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
    } yield {
      val input         = n.some
      val expected      = OptionT(Applicative[IO].pure(input))
      val expectedValue = expected.value.unsafeRunSync()

      val actual      = input.optionT[IO]
      val actualValue = actual.value.unsafeRunSync()

      Result.all(
        List(
          Result.assert(actualValue.isDefined).log(s"actualValue should be Some. actualValue: $actualValue"),
          actualValue ==== expectedValue
        )
      )
    }

  }

  object OptionTFAOpsSpec {

    import cats.Applicative
    import cats.data.OptionT
    import cats.effect.IO
    import effectie.cats.OptionTSupport.OptionTFAOps

    def testOptionT: Property = for {
      n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
    } yield {
      val input         = Applicative[IO].pure(n)
      val expected      = OptionT.liftF(input)
      val expectedValue = expected.value.unsafeRunSync()

      val actual      = input.someT
      val actualValue = actual.value.unsafeRunSync()

      Result.all(
        List(
          Result.assert(actualValue.isDefined).log(s"actualValue should be Some. actualValue: $actualValue"),
          actualValue ==== expectedValue
        )
      )
    }

  }

  object OptionTAOpsSpec {

    import cats.Applicative
    import cats.data.OptionT
    import cats.effect.IO
    import cats.syntax.option._
    import effectie.cats.OptionTSupport.OptionTAOps

    def testOptionTF: Property = for {
      n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
    } yield {
      val input         = n
      val expected      = OptionT(Applicative[IO].pure(input.some))
      val expectedValue = expected.value.unsafeRunSync()

      val actual      = input.someTF[IO]
      val actualValue = actual.value.unsafeRunSync()

      Result.all(
        List(
          Result.assert(actualValue.isDefined).log(s"actualValue should be Some. actualValue: $actualValue"),
          actualValue ==== expectedValue
        )
      )
    }

  }

  object OptionTSupportAllSpec {

    import cats.Applicative
    import cats.data._
    import cats.effect._
    import cats.syntax.option._
    import effectie.cats.OptionTSupport._

    def fab[F[_]: Fx, A](oa: Option[A]): F[Option[A]] = Fx[F].effectOf(oa)

    def testAll: Property = for {
      n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
    } yield {
      val input1         = fab[IO, Int](n.some)
      val expected1      = OptionT(input1)
      val expected1Value = expected1.value.unsafeRunSync()

      val actual1      = input1.optionT
      val actual1Value = actual1.value.unsafeRunSync()

      val input2         = n.some
      val expected2      = OptionT(Applicative[IO].pure(input2))
      val expected2Value = expected2.value.unsafeRunSync()

      val actual2      = input2.optionT[IO]
      val actual2Value = actual2.value.unsafeRunSync()

      val input3         = Applicative[IO].pure(n)
      val expected3      = OptionT.liftF(input3)
      val expected3Value = expected3.value.unsafeRunSync()

      val actual3      = input3.someT
      val actual3Value = actual3.value.unsafeRunSync()

      val input4         = n
      val expected4      = OptionT(Applicative[IO].pure(input4.some))
      val expected4Value = expected4.value.unsafeRunSync()

      val actual4      = input4.someTF[IO]
      val actual4Value = actual4.value.unsafeRunSync()

      Result.all(
        List(
          Result.assert(actual1Value.isDefined).log(s"actual1Value should be Some. actual1Value: $actual1Value"),
          actual1Value ==== expected1Value,
          Result.assert(actual2Value.isDefined).log(s"actual2Value should be Some. actual2Value: $actual2Value"),
          actual2Value ==== expected2Value,
          Result.assert(actual3Value.isDefined).log(s"actual3Value should be Some. actual3Value: $actual3Value"),
          actual3Value ==== expected3Value,
          Result.assert(actual4Value.isDefined).log(s"actual4Value should be Some. actual4Value: $actual4Value"),
          actual4Value ==== expected4Value
        )
      )

    }

  }

}
