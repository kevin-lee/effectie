package effectie.resource

import cats.effect._
import cats.syntax.all._
import effectie.instances.ce2.fx.ioFx
import effectie.resource.data.TestErrors.TestException
import hedgehog._
import hedgehog.runner._

/** @author Kevin Lee
  * @since 2022-11-06
  */
object Ce2ResourceSpec extends Properties {
  type F[A] = IO[A]
  val F: IO.type = IO

  override def tests: List[Test] = List(
    property(
      "test Ce2Resource.fromAutoCloseable",
      testFromAutoCloseable,
    ),
    property(
      "test Ce2Resource.fromAutoCloseable - error case",
      testFromAutoCloseableErrorCase,
    ),
  )

  def testFromAutoCloseable: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield ReleasableResourceSpec
      .testFromAutoCloseable[F](content, _ => F.unit, none, Ce2Resource.fromAutoCloseable)
      .unsafeRunSync()

  def testFromAutoCloseableErrorCase: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield ReleasableResourceSpec
      .testFromAutoCloseable[F](
        content,
        _ => F.raiseError(TestException(123)),
        Option({
          case TestException(123) => Result.success
          case ex: Throwable =>
            Result
              .failure
              .log(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
        }),
        Ce2Resource.fromAutoCloseable,
      )
      .unsafeRunSync()

}
