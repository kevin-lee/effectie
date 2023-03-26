package effectie.resource

import cats.instances.all._
import cats.syntax.all._
import effectie.resource.data.TestErrors.TestException
import effectie.resource.data.{TestResource, TestResourceNoAutoClose}
import hedgehog._
import hedgehog.runner._

import scala.util.{Failure, Try}

/** @author Kevin Lee
  * @since 2022-11-12
  */
object UsingResourceMakerSpec extends Properties {

  override def tests: List[Test] = List(
    property(
      "test ResourceMaker.usingResourceMaker: ResourceMaker[Try]",
      testUsingResourceMaker,
    ),
    property(
      "test ResourceMaker.usingResourceMaker: ResourceMaker[Try] - error case",
      testUsingResourceMakerErrorCase,
    ),
    property(
      "test ResourceMaker.usingResourceMaker: ResourceMaker[Try].make",
      testUsingResourceMakerMake,
    ),
    property(
      "test ResourceMaker.usingResourceMaker: ResourceMaker[Try].make - error case",
      testUsingResourceMakerMakeErrorCase,
    ),
    property(
      "test ResourceMaker.usingResourceMaker: ResourceMaker[Try].make - error case in closing",
      testUsingResourceMakerMakeErrorCaseInClosing,
    ),
  )

  import effectie.instances.tries.fx._

  def testUsingResourceMaker: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {

      implicit val resourceMaker: ResourceMaker[Try] = ResourceMaker.usingResourceMaker

      ResourceMakerSpec
        .testForAutoCloseable[Try](TestResource.apply)(
          content,
          _ => Try(()),
          none,
        )
        .fold(err => Result.failure.log(s"Unexpected error: ${err.toString}"), identity)
    }

  def testUsingResourceMakerErrorCase: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {
      implicit val resourceMaker: ResourceMaker[Try] = ResourceMaker.usingResourceMaker

      ResourceMakerSpec
        .testForAutoCloseable[Try](TestResource.apply)(
          content,
          _ => Failure(TestException(123)),
          Option({
            case TestException(123) => Result.success
            case ex: Throwable =>
              Result
                .failure
                .log(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
          }),
        )
        .fold(err => Result.failure.log(s"Unexpected error: ${err.toString}"), identity)
    }

  def testUsingResourceMakerMake: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {

      implicit val resourceMaker: ResourceMaker[Try] = ResourceMaker.usingResourceMaker

      ResourceMakerSpec
        .testForMake[Try](TestResourceNoAutoClose.apply)(
          _.release(),
          content,
          _ => Try(Result.success),
          none,
        )
        .fold(err => Result.failure.log(s"Unexpected error: ${err.toString}"), identity)
    }

  def testUsingResourceMakerMakeErrorCase: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {
      implicit val resourceMaker: ResourceMaker[Try] = ResourceMaker.usingResourceMaker

      ResourceMakerSpec
        .testForMake[Try](TestResourceNoAutoClose.apply)(
          _.release(),
          content,
          _ => Failure(TestException(123)),
          Option({
            case TestException(123) => Result.success
            case ex: Throwable =>
              Result
                .failure
                .log(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
          }),
        )
        .fold(err => Result.failure.log(s"Unexpected error: ${err.toString}"), identity)
    }

  def testUsingResourceMakerMakeErrorCaseInClosing: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {
      implicit val resourceMaker: ResourceMaker[Try] = ResourceMaker.usingResourceMaker

      ResourceMakerSpec
        .testForMake[Try](TestResourceNoAutoClose.apply)(
          { resource =>
            resource.release()
            throw new RuntimeException(
              "Test error in closing resource. It's only for testing so please ignore."
            ) // scalafix:ok DisableSyntax.throw
          },
          content,
          _ => Try(Result.success),
          none,
        )
        .fold(err => Result.failure.log(s"Unexpected error: ${err.toString}"), identity)
    }

}
