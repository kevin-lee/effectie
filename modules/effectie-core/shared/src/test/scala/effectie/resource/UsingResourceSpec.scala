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
object UsingResourceSpec extends Properties {

  override def tests: List[Test] = List(
    property(
      "test UsingResource.fromAutoCloseable[Try, A]",
      testUsingResourceFromAutoCloseable,
    ),
    property(
      "test UsingResource.fromAutoCloseable[Try, A] error case",
      testUsingResourceFromAutoCloseableErrorCase,
    ),
    property(
      "test UsingResource.make[Try, A]",
      testUsingResourceMake,
    ),
    property(
      "test UsingResource.make[Try, A] error case",
      testUsingResourceMakeErrorCase,
    ),
  )

  import effectie.instances.tries.fx._

  def testUsingResourceFromAutoCloseable: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {

      ReleasableResourceSpec
        .testReleasableResourceUse[Try](TestResource.apply)(
          content,
          _ => Try(()),
          none,
          ReleasableResource.usingResourceFromTry[TestResource],
        )
        .fold(err => Result.failure.log(s"Unexpected error: ${err.toString}"), identity)
    }

  def testUsingResourceFromAutoCloseableErrorCase: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {

      ReleasableResourceSpec
        .testReleasableResourceUse[Try](TestResource.apply)(
          content,
          _ => Failure(TestException(123)),
          Option({
            case TestException(123) => Result.success
            case ex: Throwable =>
              Result
                .failure
                .log(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
          }),
          UsingResource.fromTry[TestResource],
        )
        .fold(err => Result.failure.log(s"Unexpected error: ${err.toString}"), identity)
    }

  def testUsingResourceMake: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {

      ReleasableResourceSpec
        .testReleasableResourceUse[Try](TestResourceNoAutoClose.apply)(
          content,
          _ => Try(()),
          none,
          ReleasableResource.makeTry(_)(a => Try(a.release())),
        )
        .fold(err => Result.failure.log(s"Unexpected error: ${err.toString}"), identity)
    }

  def testUsingResourceMakeErrorCase: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {

      ReleasableResourceSpec
        .testReleasableResourceUse[Try](TestResourceNoAutoClose.apply)(
          content,
          _ => Failure(TestException(123)),
          Option({
            case TestException(123) => Result.success
            case ex: Throwable =>
              Result
                .failure
                .log(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
          }),
          UsingResource.make(_)(a => Try(a.release())),
        )
        .fold(err => Result.failure.log(s"Unexpected error: ${err.toString}"), identity)
    }

}
