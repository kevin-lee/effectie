package effectie.resource

import cats.effect._
import cats.syntax.all._
import effectie.instances.ce2.fx.ioFx
import effectie.resource.data.TestErrors.TestException
import effectie.resource.data.{TestResource, TestResourceNoAutoClose}
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
    property(
      "test Ce2Resource.make",
      testMake,
    ),
    property(
      "test Ce2Resource.make - error case",
      testMakeErrorCase,
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
      .testReleasableResourceUse[F](TestResource.apply)(content, _ => F.unit, none, Ce2Resource.fromAutoCloseable)
      .unsafeRunSync()

  def testFromAutoCloseableErrorCase: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield ReleasableResourceSpec
      .testReleasableResourceUse[F](TestResource.apply)(
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

  def testMake: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield ReleasableResourceSpec
      .testReleasableResourceUse[F](TestResourceNoAutoClose.apply)(
        content,
        _ => F.unit,
        none,
        Ce2Resource.make(_)(a => F.delay(a.release())),
      )
      .unsafeRunSync()

  def testMakeErrorCase: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield ReleasableResourceSpec
      .testReleasableResourceUse[F](TestResourceNoAutoClose.apply)(
        content,
        _ => F.raiseError(TestException(123)),
        Option({
          case TestException(123) => Result.success
          case ex: Throwable =>
            Result
              .failure
              .log(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
        }),
        Ce2Resource.make(_)(a => F.delay(a.release())),
      )
      .unsafeRunSync()

}
