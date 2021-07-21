package effectie.monix

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
    import cats.syntax.option._
    import effectie.monix.OptionTSupport.OptionTFOptionOps
    import monix.eval.Task
    import monix.execution.Scheduler.Implicits.global

    def fab[F[_]: Eft, A](oa: Option[A]): F[Option[A]] = Eft[F].effectOf(oa)

    def testOptionT: Property = for {
      n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
    } yield {
      val input         = fab[Task, Int](n.some)
      val expected      = OptionT(input)
      val expectedValue = expected.value.runSyncUnsafe()

      val actual      = input.optionT
      val actualValue = actual.value.runSyncUnsafe()

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
    import cats.syntax.option._
    import effectie.monix.OptionTSupport.OptionTOptionOps
    import monix.eval.Task
    import monix.execution.Scheduler.Implicits.global

    def testOptionT: Property = for {
      n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
    } yield {
      val input         = n.some
      val expected      = OptionT(Applicative[Task].pure(input))
      val expectedValue = expected.value.runSyncUnsafe()

      val actual      = input.optionT[Task]
      val actualValue = actual.value.runSyncUnsafe()

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
    import effectie.monix.OptionTSupport.OptionTFAOps
    import monix.eval.Task
    import monix.execution.Scheduler.Implicits.global

    def testOptionT: Property = for {
      n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
    } yield {
      val input         = Applicative[Task].pure(n)
      val expected      = OptionT.liftF(input)
      val expectedValue = expected.value.runSyncUnsafe()

      val actual      = input.someT
      val actualValue = actual.value.runSyncUnsafe()

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
    import cats.syntax.option._
    import effectie.monix.OptionTSupport.OptionTAOps
    import monix.eval.Task
    import monix.execution.Scheduler.Implicits.global

    def testOptionTF: Property = for {
      n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
    } yield {
      val input         = n
      val expected      = OptionT(Applicative[Task].pure(input.some))
      val expectedValue = expected.value.runSyncUnsafe()

      val actual      = input.someTF[Task]
      val actualValue = actual.value.runSyncUnsafe()

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
    import cats.syntax.option._
    import effectie.monix.OptionTSupport._
    import monix.eval.Task
    import monix.execution.Scheduler.Implicits.global

    def fab[F[_]: Eft, A](oa: Option[A]): F[Option[A]] = Eft[F].effectOf(oa)

    def testAll: Property = for {
      n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
    } yield {
      val input1         = fab[Task, Int](n.some)
      val expected1      = OptionT(input1)
      val expected1Value = expected1.value.runSyncUnsafe()

      val actual1      = input1.optionT
      val actual1Value = actual1.value.runSyncUnsafe()

      val input2         = n.some
      val expected2      = OptionT(Applicative[Task].pure(input2))
      val expected2Value = expected2.value.runSyncUnsafe()

      val actual2      = input2.optionT[Task]
      val actual2Value = actual2.value.runSyncUnsafe()

      val input3         = Applicative[Task].pure(n)
      val expected3      = OptionT.liftF(input3)
      val expected3Value = expected3.value.runSyncUnsafe()

      val actual3      = input3.someT
      val actual3Value = actual3.value.runSyncUnsafe()

      val input4         = n
      val expected4      = OptionT(Applicative[Task].pure(input4.some))
      val expected4Value = expected4.value.runSyncUnsafe()

      val actual4      = input4.someTF[Task]
      val actual4Value = actual4.value.runSyncUnsafe()

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
