package effectie.resource

import cats.instances.all._
import cats.syntax.all._
import effectie.resource.data.TestErrors.TestException
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
      "test ResourceMaker.usingResourceMaker: ResourceMaker[Try] error case",
      testUsingResourceMakerErrorCase,
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
        .testForAutoCloseable[Try](
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
        .testForAutoCloseable[Try](
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

}
