package effectie.monix

import hedgehog._
import hedgehog.runner._

/** @author Kevin Lee
  * @since 2021-07-19
  */
object EitherTSupportSpec extends Properties {

  override def tests: List[Test] = List(
    property(
      "EitherTFEitherOpsSpec.testEitherT",
      EitherTFEitherOpsSpec.testEitherT
    ),
    property(
      "EitherTEitherOpsSpec.testEitherT",
      EitherTEitherOpsSpec.testEitherT
    ),
    property(
      "EitherTFAOpsSpec.testRightT",
      EitherTFAOpsSpec.testRightT
    ),
    property(
      "EitherTFAOpsSpec.testLeftT",
      EitherTFAOpsSpec.testLeftT
    ),
    property(
      "EitherTAOpsSpec.testRightTF",
      EitherTAOpsSpec.testRightTF
    ),
    property(
      "EitherTAOpsSpec.testLeftTF",
      EitherTAOpsSpec.testLeftTF
    ),
    property(
      "testAll with EitherTSupport._",
      EitherTSupportAllSpec.testAll
    )
  )

  object EitherTFEitherOpsSpec {

    import cats.data._
    import cats.syntax.either._
    import effectie.monix.EitherTSupport.EitherTFEitherOps
    import monix.eval.Task
    import monix.execution.Scheduler.Implicits.global

    def fab[F[_]: FxCtor, A, B](ab: Either[A, B]): F[Either[A, B]] = FxCtor[F].effectOf(ab)

    def testEitherT: Property = for {
      n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
    } yield {
      val input       = fab[Task, String, Int](n.asRight[String])
      val expected    = EitherT(input)
      val actual      = input.eitherT
      val actualValue = actual.value.runSyncUnsafe()
      Result.all(
        List(
          Result.assert(actualValue.isRight).log(s"actualValue should be Right. actualValue: $actualValue"),
          actualValue ==== expected.value.runSyncUnsafe()
        )
      )
    }

  }

  object EitherTEitherOpsSpec {

    import cats._
    import cats.data.EitherT
    import cats.syntax.either._
    import effectie.monix.EitherTSupport.EitherTEitherOps
    import monix.eval.Task
    import monix.execution.Scheduler.Implicits.global

    def testEitherT: Property = for {
      n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
    } yield {
      val input: Either[String, Int] = n.asRight[String]

      val expected = EitherT(Applicative[Task].pure(input))

      val actual      = input.eitherT[Task]
      val actualValue = actual.value.runSyncUnsafe()
      Result.all(
        List(
          Result.assert(actualValue.isRight).log(s"actualValue should be Right. actualValue: $actualValue"),
          actualValue ==== expected.value.runSyncUnsafe()
        )
      )
    }
  }

  object EitherTFAOpsSpec {

    import cats.data.EitherT
    import cats.syntax.either._
    import effectie.monix.EitherTSupport.EitherTFAOps
    import monix.eval.Task
    import monix.execution.Scheduler.Implicits.global

    def testRightT: Property = for {
      n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
    } yield {
      val input = Task(n)

      val expected = EitherT(input.map(_.asRight[String]))

      val actual = input.rightT[String]

      val actualValue = actual.value.runSyncUnsafe()
      Result.all(
        List(
          Result.assert(actualValue.isRight).log(s"actualValue should be Right. actualValue: $actualValue"),
          actualValue ==== expected.value.runSyncUnsafe()
        )
      )
    }

    def testLeftT: Property = for {
      s <- Gen.string(Gen.alphaNum, Range.linear(0, 20)).log("S")
    } yield {
      val input = Task(s)

      val expected = EitherT(input.map(_.asLeft[Int]))

      val actual = input.leftT[Int]

      val actualValue = actual.value.runSyncUnsafe()
      Result.all(
        List(
          Result.assert(actualValue.isLeft).log(s"actualValue should be Left. actualValue: $actualValue"),
          actualValue ==== expected.value.runSyncUnsafe()
        )
      )
    }

  }

  object EitherTAOpsSpec {

    import cats.data.EitherT
    import cats.syntax.either._
    import effectie.monix.EitherTSupport.EitherTAOps
    import monix.eval.Task
    import monix.execution.Scheduler.Implicits.global

    def testRightTF: Property = for {
      n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
    } yield {
      val input = n

      val expected = EitherT(Task(input.asRight[String]))

      val actual = input.rightTF[Task, String]

      val actualValue = actual.value.runSyncUnsafe()
      Result.all(
        List(
          Result.assert(actualValue.isRight).log(s"actualValue should be Right. actualValue: $actualValue"),
          actualValue ==== expected.value.runSyncUnsafe()
        )
      )
    }

    def testLeftTF: Property = for {
      s <- Gen.string(Gen.alphaNum, Range.linear(0, 20)).log("S")
    } yield {
      val input = s

      val expected = EitherT(Task(input.asLeft[Int]))

      val actual = input.leftTF[Task, Int]

      val actualValue = actual.value.runSyncUnsafe()
      Result.all(
        List(
          Result.assert(actualValue.isLeft).log(s"actualValue should be Left. actualValue: $actualValue"),
          actualValue ==== expected.value.runSyncUnsafe()
        )
      )
    }

  }

  object EitherTSupportAllSpec {
    def testAll: Property = for {
      n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
      s <- Gen.string(Gen.alphaNum, Range.linear(0, 20)).log("S")
    } yield {
      import EitherTSupport._
      import cats.Applicative
      import cats.data.EitherT
      import cats.syntax.all._
      import monix.eval.Task
      import monix.execution.Scheduler.Implicits.global

      val input1         = EitherTFEitherOpsSpec.fab[Task, String, Int](n.asRight[String])
      val expected1      = EitherT(input1)
      val expected1Value = expected1.value.runSyncUnsafe()

      val actual1      = input1.eitherT
      val actual1Value = actual1.value.runSyncUnsafe()

      val input2: Either[String, Int] = n.asRight[String]
      val expected2                   = EitherT(Applicative[Task].pure(input2))
      val expected2Value              = expected2.value.runSyncUnsafe()

      val actual2      = input2.eitherT[Task]
      val actual2Value = actual2.value.runSyncUnsafe()

      val input3         = Task(n)
      val expected3      = EitherT(input3.map(_.asRight[String]))
      val expected3Value = expected3.value.runSyncUnsafe()

      val actual3      = input3.rightT[String]
      val actual3Value = actual3.value.runSyncUnsafe()

      val input4         = Task(s)
      val expected4      = EitherT(input4.map(_.asLeft[Int]))
      val expected4Value = expected4.value.runSyncUnsafe()

      val actual4      = input4.leftT[Int]
      val actual4Value = actual4.value.runSyncUnsafe()

      val input5         = n
      val expected5      = EitherT(Task(input5.asRight[String]))
      val expected5Value = expected5.value.runSyncUnsafe()

      val actual5      = input5.rightTF[Task, String]
      val actual5Value = actual5.value.runSyncUnsafe()

      val input6         = s
      val expected6      = EitherT(Task(input6.asLeft[Int]))
      val expected6Value = expected6.value.runSyncUnsafe()

      val actual6      = input6.leftTF[Task, Int]
      val actual6Value = actual6.value.runSyncUnsafe()

      Result.all(
        List(
          Result.assert(actual1Value.isRight).log(s"actual1Value should be Right. actual1Value: $actual1Value"),
          actual1Value ==== expected1Value,
          Result.assert(actual2Value.isRight).log(s"actual2Value should be Right. actual2Value: $actual2Value"),
          actual2Value ==== expected2Value,
          Result.assert(actual3Value.isRight).log(s"actual3Value should be Right. actual3Value: $actual3Value"),
          actual3Value ==== expected3Value,
          Result.assert(actual4Value.isLeft).log(s"actual4Value should be Left. actual4Value: $actual4Value"),
          actual4Value ==== expected4Value,
          Result.assert(actual5Value.isRight).log(s"actual5Value should be Right. actual5Value: $actual5Value"),
          actual5Value ==== expected5Value,
          Result.assert(actual6Value.isLeft).log(s"actual6Value should be Left. actual6Value: $actual6Value"),
          actual6Value ==== expected6Value
        )
      )
    }

  }

}
