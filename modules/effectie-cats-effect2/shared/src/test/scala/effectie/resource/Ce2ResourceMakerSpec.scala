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
object Ce2ResourceMakerSpec extends Properties {
  type F[A] = IO[A]
  val F: IO.type = IO

  override def tests: List[Test] = List(
    property(
      "test Ce2ResourceMaker.forAutoCloseable",
      testForAutoCloseable,
    ),
    property(
      "test Ce2Resource.fromAutoCloseable - error case",
      testForAutoCloseableErrorCase,
    ),
  )

  def testForAutoCloseable: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {
      implicit val resourceMaker: ResourceMaker[F] = Ce2ResourceMaker.forAutoCloseable
      ResourceMakerSpec
        .testForAutoCloseable[F](content, _ => F.unit, none)
        .unsafeRunSync()
    }

  def testForAutoCloseableErrorCase: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {
      implicit val resourceMaker: ResourceMaker[F] = Ce2ResourceMaker.forAutoCloseable

      ResourceMakerSpec
        .testForAutoCloseable[F](
          content,
          _ => F.raiseError(TestException(123)),
          Option({
            case TestException(123) => Result.success
            case ex: Throwable =>
              Result
                .failure
                .log(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
          }),
        )
        .unsafeRunSync()
    }

}
