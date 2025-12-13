package effectie.resource

import cats.effect._
import cats.syntax.all._
import effectie.instances.ce3.fx.ioFx
import effectie.resource.data.TestErrors.TestException
import effectie.resource.data.{TestResource, TestResourceNoAutoClose, TestableResource}
import effectie.syntax.all._
import hedgehog._
import hedgehog.runner._

/** @author Kevin Lee
  * @since 2022-11-06
  */
object Ce3ResourceMakerSpec extends Properties {
  type F[A] = IO[A]
  val F: IO.type = IO

  import cats.effect.unsafe.implicits.global

  override def tests: List[Test] = List(
    property(
      "test Ce3ResourceMaker.forAutoCloseable",
      testForAutoCloseable,
    ),
    property(
      "test Ce3ResourceMaker.forAutoCloseable - error case",
      testForAutoCloseableErrorCase,
    ),
    property(
      "test Ce3ResourceMaker.make",
      testMake,
    ),
    property(
      "test Ce3ResourceMaker.make - error case",
      testMakeErrorCase,
    ),
    property(
      "test Ce3ResourceMaker.pure",
      testPure,
    ),
    property(
      "test Ce3ResourceMaker.pure - error case",
      testPureErrorCase,
    ),
    property(
      "test Ce3ResourceMaker.eval",
      testEval,
    ),
    property(
      "test Ce3ResourceMaker.eval - error case",
      testEvalErrorCase,
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
      implicit val resourceMaker: ResourceMaker[F] = Ce3ResourceMaker.maker

      ResourceMakerSpec
        .testForAutoCloseable[F](TestResource.apply)(
          content,
          _ => F.unit,
          none,
        )
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
      implicit val resourceMaker: ResourceMaker[F] = Ce3ResourceMaker.maker

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

  def testMake: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {
      implicit val resourceMaker: ResourceMaker[F] = Ce3ResourceMaker.maker

      ResourceMakerSpec
        .testForMake[F](TestResourceNoAutoClose.apply)(
          _.release(),
          content,
          _ => F.delay(Result.success),
          none,
        )
        .unsafeRunSync()
    }

  def testMakeErrorCase: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {
      implicit val resourceMaker: ResourceMaker[F] = Ce3ResourceMaker.maker

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

  def testPure: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {
      implicit val resourceMaker: ResourceMaker[F] = Ce3ResourceMaker.maker

      ResourceMakerSpec
        .testFor[F](TestResourceNoAutoClose.apply)(
          resourceMaker.pure,
          content,
          _ => F.delay(Result.success),
          closeStatus => closeStatus ==== TestableResource.CloseStatus.NotClosed,
          none,
        )
        .unsafeRunSync()
    }

  def testPureErrorCase: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {
      implicit val resourceMaker: ResourceMaker[F] = Ce3ResourceMaker.maker

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

  def testEval: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {
      implicit val resourceMaker: ResourceMaker[F] = Ce3ResourceMaker.maker

      ResourceMakerSpec
        .testFor[F](TestResourceNoAutoClose.apply)(
          testResource => resourceMaker.eval(effectOf(testResource)),
          content,
          _ => F.delay(Result.success),
          closeStatus => closeStatus ==== TestableResource.CloseStatus.NotClosed,
          none,
        )
        .unsafeRunSync()
    }

  def testEvalErrorCase: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {
      implicit val resourceMaker: ResourceMaker[F] = Ce3ResourceMaker.maker

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
