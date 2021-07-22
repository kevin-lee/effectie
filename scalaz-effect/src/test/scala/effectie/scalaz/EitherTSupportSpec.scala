package effectie.scalaz

import hedgehog._
import hedgehog.runner._

/** @author Kevin Lee
  * @since 2021-07-18
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

    import scalaz._
    import Scalaz._
    import effectie.scalaz.EitherTSupport.EitherTFEitherOps
    import scalaz.effect._

    def fab[F[_]: Fx, A, B](ab: A \/ B): F[A \/ B] = Fx[F].effectOf(ab)

    def testEitherT: Property = for {
      n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
    } yield {
      val input       = fab[IO, String, Int](n.right[String])
      val expected    = EitherT(input)
      val actual      = input.eitherT
      val actualValue = actual.run.unsafePerformIO()
      Result.all(
        List(
          Result.assert(actualValue.isRight).log(s"actualValue should be Right. actualValue: $actualValue"),
          actualValue ==== expected.run.unsafePerformIO()
        )
      )
    }

  }

  object EitherTEitherOpsSpec {

    import scalaz._
    import Scalaz._
    import effectie.scalaz.EitherTSupport.EitherTEitherOps
    import scalaz.effect._

    def testEitherT: Property = for {
      n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
    } yield {
      val input: String \/ Int = n.right[String]

      val expected = EitherT(Applicative[IO].pure(input))

      val actual      = input.eitherT[IO]
      val actualValue = actual.run.unsafePerformIO()
      Result.all(
        List(
          Result.assert(actualValue.isRight).log(s"actualValue should be Right. actualValue: $actualValue"),
          actualValue ==== expected.run.unsafePerformIO()
        )
      )
    }
  }

  object EitherTFAOpsSpec {

    import scalaz._
    import Scalaz._
    import effectie.scalaz.EitherTSupport.EitherTFAOps
    import scalaz.effect._

    def testRightT: Property = for {
      n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
    } yield {
      val input = IO(n)

      val expected = EitherT(input.map(_.right[String]))

      val actual = input.rightT[String]

      val actualValue = actual.run.unsafePerformIO()
      Result.all(
        List(
          Result.assert(actualValue.isRight).log(s"actualValue should be Right. actualValue: $actualValue"),
          actualValue ==== expected.run.unsafePerformIO()
        )
      )
    }

    def testLeftT: Property = for {
      s <- Gen.string(Gen.alphaNum, Range.linear(0, 20)).log("S")
    } yield {
      val input = IO(s)

      val expected = EitherT(input.map(_.left[Int]))

      val actual = input.leftT[Int]

      val actualValue = actual.run.unsafePerformIO()
      Result.all(
        List(
          Result.assert(actualValue.isLeft).log(s"actualValue should be Left. actualValue: $actualValue"),
          actualValue ==== expected.run.unsafePerformIO()
        )
      )
    }

  }

  object EitherTAOpsSpec {

    import scalaz._
    import Scalaz._
    import effectie.scalaz.EitherTSupport.EitherTAOps
    import scalaz.effect._

    def testRightTF: Property = for {
      n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
    } yield {
      val input = n

      val expected = EitherT(IO(input.right[String]))

      val actual = input.rightTF[IO, String]

      val actualValue = actual.run.unsafePerformIO()
      Result.all(
        List(
          Result.assert(actualValue.isRight).log(s"actualValue should be Right. actualValue: $actualValue"),
          actualValue ==== expected.run.unsafePerformIO()
        )
      )
    }

    def testLeftTF: Property = for {
      s <- Gen.string(Gen.alphaNum, Range.linear(0, 20)).log("S")
    } yield {
      val input = s

      val expected = EitherT(IO(input.left[Int]))

      val actual = input.leftTF[IO, Int]

      val actualValue = actual.run.unsafePerformIO()
      Result.all(
        List(
          Result.assert(actualValue.isLeft).log(s"actualValue should be Left. actualValue: $actualValue"),
          actualValue ==== expected.run.unsafePerformIO()
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
      import scalaz._
      import Scalaz._
      import scalaz.effect._

      val input1         = EitherTFEitherOpsSpec.fab[IO, String, Int](n.right[String])
      val expected1      = EitherT(input1)
      val expected1Value = expected1.run.unsafePerformIO()

      val actual1      = input1.eitherT
      val actual1Value = actual1.run.unsafePerformIO()

      val input2: String \/ Int = n.right[String]
      val expected2             = EitherT(Applicative[IO].pure(input2))
      val expected2Value        = expected2.run.unsafePerformIO()

      val actual2      = input2.eitherT[IO]
      val actual2Value = actual2.run.unsafePerformIO()

      val input3         = IO(n)
      val expected3      = EitherT(input3.map(_.right[String]))
      val expected3Value = expected3.run.unsafePerformIO()

      val actual3      = input3.rightT[String]
      val actual3Value = actual3.run.unsafePerformIO()

      val input4         = IO(s)
      val expected4      = EitherT(input4.map(_.left[Int]))
      val expected4Value = expected4.run.unsafePerformIO()

      val actual4      = input4.leftT[Int]
      val actual4Value = actual4.run.unsafePerformIO()

      val input5         = n
      val expected5      = EitherT(IO(input5.right[String]))
      val expected5Value = expected5.run.unsafePerformIO()

      val actual5      = input5.rightTF[IO, String]
      val actual5Value = actual5.run.unsafePerformIO()

      val input6         = s
      val expected6      = EitherT(IO(input6.left[Int]))
      val expected6Value = expected6.run.unsafePerformIO()

      val actual6      = input6.leftTF[IO, Int]
      val actual6Value = actual6.run.unsafePerformIO()

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
