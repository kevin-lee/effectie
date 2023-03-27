package effectie.resource

import cats.effect._
import cats.syntax.all._
import effectie.instances.ce2.fx.ioFx
import effectie.resource.data.TestErrors.TestException
import effectie.resource.data.{TestResource, TestResourceNoAutoClose, TestableResource}
import effectie.syntax.all._
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
      "test Ce2ResourceMaker.fromAutoCloseable - error case",
      testForAutoCloseableErrorCase,
    ),
    property(
      "test Ce2ResourceMaker.make",
      testForMake,
    ),
    property(
      "test Ce2ResourceMaker.make - error case",
      testForMakeErrorCase,
    ),
    property(
      "test Ce2ResourceMaker.pure",
      testForPure,
    ),
    property(
      "test Ce2ResourceMaker.pure - error case",
      testForPureErrorCase,
    ),
    property(
      "test Ce2ResourceMaker.eval",
      testForEval,
    ),
    property(
      "test Ce2ResourceMaker.eval - error case",
      testForEvalErrorCase,
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
      implicit val resourceMaker: ResourceMaker[F] = Ce2ResourceMaker.maker
      ResourceMakerSpec
        .testForAutoCloseable[F](TestResource.apply)(content, _ => F.unit, none)
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
      implicit val resourceMaker: ResourceMaker[F] = Ce2ResourceMaker.maker

      ResourceMakerSpec
        .testForAutoCloseable[F](TestResource.apply)(
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

  def testForMake: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {
      implicit val resourceMaker: ResourceMaker[F] = Ce2ResourceMaker.maker
      ResourceMakerSpec
        .testForMake[F](TestResourceNoAutoClose.apply)(_.release(), content, _ => F.pure(Result.success), none)
        .unsafeRunSync()
    }

  def testForMakeErrorCase: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {
      implicit val resourceMaker: ResourceMaker[F] = Ce2ResourceMaker.maker

      ResourceMakerSpec
        .testForMake[F](TestResourceNoAutoClose.apply)(
          _.release(),
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

  def testForPure: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {
      implicit val resourceMaker: ResourceMaker[F] = Ce2ResourceMaker.maker
      ResourceMakerSpec
        .testFor[F](TestResourceNoAutoClose.apply)(
          resourceMaker.pure,
          content,
          _ => F.pure(Result.success),
          closeStatus => closeStatus ==== TestableResource.CloseStatus.NotClosed,
          none,
        )
        .unsafeRunSync()
    }

  def testForPureErrorCase: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {
      implicit val resourceMaker: ResourceMaker[F] = Ce2ResourceMaker.maker

      ResourceMakerSpec
        .testFor[F](TestResourceNoAutoClose.apply)(
          resourceMaker.pure,
          content,
          _ => F.raiseError(TestException(123)),
          closeStatus => closeStatus ==== TestableResource.CloseStatus.NotClosed,
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

  def testForEval: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {
      implicit val resourceMaker: ResourceMaker[F] = Ce2ResourceMaker.maker
      ResourceMakerSpec
        .testFor[F](TestResourceNoAutoClose.apply)(
          testResource => resourceMaker.eval(effectOf(testResource)),
          content,
          _ => F.pure(Result.success),
          closeStatus => closeStatus ==== TestableResource.CloseStatus.NotClosed,
          none,
        )
        .unsafeRunSync()
    }

  def testForEvalErrorCase: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {
      implicit val resourceMaker: ResourceMaker[F] = Ce2ResourceMaker.maker

      ResourceMakerSpec
        .testFor[F](TestResourceNoAutoClose.apply)(
          testResource => resourceMaker.eval(effectOf(testResource)),
          content,
          _ => F.raiseError(TestException(123)),
          closeStatus => closeStatus ==== TestableResource.CloseStatus.NotClosed,
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
