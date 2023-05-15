package effectie.resource

import cats.instances.all._
import cats.syntax.all._
import effectie.resource.data.TestErrors.TestException
import effectie.resource.data.{TestResource, TestResourceNoAutoClose, TestableResource}
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
    property(
      "test UsingResource.pure[Try, A]",
      testUsingResourcePure,
    ),
    property(
      "test UsingResource.pure[Try, A] error case",
      testUsingResourcePureErrorCase,
    ),
    property(
      "test UsingResource.map",
      testUsingResourceMap,
    ),
    property(
      "test UsingResource.map error case",
      testUsingResourceMapErrorCase,
    ),
    property(
      "test UsingResource.flatMap",
      testUsingResourceFlatMap,
    ),
    property(
      "test UsingResource.flatMap error case",
      testUsingResourceFlatMapErrorCase,
    ),
    property(
      "test UsingResource.flatMap and UsingResource.map",
      testUsingResourceFlatMapAndMap,
    ),
    property(
      "test UsingResource.flatMap and UsingResource.map error case",
      testUsingResourceFlatMapAndMapErrorCase,
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

  def testUsingResourcePure: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {

      Result.all(
        List(
          ReleasableResourceSpec
            .testReleasableResourceUse[Try](TestResourceNoAutoClose.apply)
            .withPure(
              content,
              _ => Try(()),
              none,
              UsingResource.pure,
            )
            .fold(err => Result.failure.log(s"Unexpected error: ${err.toString}"), identity),
          ReleasableResourceSpec
            .testReleasableResourceUse[Try](TestResourceNoAutoClose.apply)
            .withPure(
              content,
              _ => Try(()),
              none,
              ReleasableResource.pureTry,
            )
            .fold(err => Result.failure.log(s"Unexpected error: ${err.toString}"), identity),
        )
      )
    }

  def testUsingResourcePureErrorCase: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {

      Result.all(
        List(
          ReleasableResourceSpec
            .testReleasableResourceUse[Try](TestResourceNoAutoClose.apply)
            .withPure(
              content,
              _ => Failure(TestException(123)),
              Option({
                case TestException(123) => Result.success
                case ex: Throwable =>
                  Result
                    .failure
                    .log(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
              }),
              UsingResource.pure,
            )
            .fold(err => Result.failure.log(s"Unexpected error: ${err.toString}"), identity),
          ReleasableResourceSpec
            .testReleasableResourceUse[Try](TestResourceNoAutoClose.apply)
            .withPure(
              content,
              _ => Failure(TestException(123)),
              Option({
                case TestException(123) => Result.success
                case ex: Throwable =>
                  Result
                    .failure
                    .log(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
              }),
              ReleasableResource.pureTry,
            )
            .fold(err => Result.failure.log(s"Unexpected error: ${err.toString}"), identity),
        )
      )
    }

  def testUsingResourceMap: Property =
    for {
      content  <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content")
      content2 <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content2")
      content3 <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content3")
    } yield {

      val expectedContent = content ++ content2 ++ content3
      val testResource    = TestResource.withContent(content)

      val resource = ReleasableResource.usingResource[TestResource](testResource)

      var actualContent = Vector.empty[String] // scalafix:ok DisableSyntax.var

      val before = List(
        (testResource.content ==== content).log("[B] content does not match"),
        (testResource.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[B] closeStatus does not match"),
      )

      resource
        .map(_.content ++ content2 ++ content3)
        .use { content =>
          actualContent = content
          Try(())
        }
      val after = List(
        (actualContent ==== expectedContent).log("[A] content does not match"),
        (testResource.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("[A] closeStatus does not match"),
      )

      Result.all(before ++ after)
    }

  def testUsingResourceMapErrorCase: Property =
    for {
      content  <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content")
      content2 <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content2")
      content3 <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content3")
    } yield {
      val expectedContent = content ++ content2 ++ content3
      val testResource    = TestResource.withContent(content)

      val resource      = ReleasableResource.usingResource[TestResource](testResource)
      var actualContent = Vector.empty[String] // scalafix:ok DisableSyntax.var

      val before = List(
        (testResource.content ==== content).log("[B] content does not match"),
        (testResource.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[B] closeStatus does not match"),
      )

      val actual = resource
        .map(_.content ++ content2 ++ content3)
        .use { content =>
          actualContent = content
          Failure[Unit](TestException(123))
        }
        .fold(
          {
            case TestException(123) => Result.success
            case ex: Throwable =>
              Result
                .failure
                .log(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
          },
          _ => Result.failure.log(s"Error was expected but no expected error was given"),
        )

      val after = List(
        (actualContent ==== expectedContent).log("[A] content does not match"),
        (testResource.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("[A] closeStatus does not match"),
      )

      Result.all(before ++ List(actual) ++ after)
    }

  def testUsingResourceFlatMap: Property =
    for {
      content  <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content")
      content2 <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content2")
      content3 <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content3")
    } yield {

      val expectedContent = content ++ content2 ++ content3
      val testResource    = TestResource.withContent(content)
      val testResource2   = TestResource.withContent(content2)
      val testResource3   = TestResource.withContent(content3)

      val resource = UsingResource[TestResource](testResource)

      var actualContent = Vector.empty[String] // scalafix:ok DisableSyntax.var

      val before = List(
        (testResource.content ==== content).log("[B] testResource.content does not match"),
        (testResource.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[B] testResource.closeStatus does not match"),
        (testResource2.content ==== content2).log("[B] testResource2.content does not match"),
        (testResource2.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[B] testResource2.closeStatus does not match"),
        (testResource3.content ==== content3).log("[B] testResource3.content does not match"),
        (testResource3.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[B] testResource3.closeStatus does not match"),
      )

      val result = resource
        .flatMap { _ =>
          UsingResource(testResource2)
        }
        .flatMap(_ => UsingResource.pure(testResource3))
        .use { newResource =>
          actualContent = content ++ content2 ++ newResource.content
          Try(())
        }
        .fold(
          { err =>
            Result.failure.log(s"No error expected but got ${err.toString}")
          },
          _ => Result.success,
        )

      val after = List(
        (actualContent ==== expectedContent).log("[A] content does not match"),
        (testResource.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("[A] testResource.closeStatus does not match"),
        (testResource2.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("[A] testResource2.closeStatus does not match"),
        (testResource3.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[A] testResource3.closeStatus does not match"),
      )

      Result.all(before ++ List(result) ++ after)
    }

  def testUsingResourceFlatMapErrorCase: Property =
    for {
      content  <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content")
      content2 <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content2")
      content3 <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content3")
    } yield {
      val expectedContent = content ++ content2 ++ content3
      val testResource    = TestResource.withContent(content)
      val testResource2   = TestResource.withContent(content2)
      val testResource3   = TestResource.withContent(content3)

      val resource      = ReleasableResource.usingResource[TestResource](testResource)
      var actualContent = Vector.empty[String] // scalafix:ok DisableSyntax.var

      val before = List(
        (testResource.content ==== content).log("[B] testResource.content does not match"),
        (testResource.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[B] testResource.closeStatus does not match"),
        (testResource2.content ==== content2).log("[B] testResource2.content does not match"),
        (testResource2.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[B] testResource2.closeStatus does not match"),
        (testResource3.content ==== content3).log("[B] testResource3.content does not match"),
        (testResource3.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[B] testResource3.closeStatus does not match"),
      )

      val result = resource
        .flatMap(_ => UsingResource(testResource2))
        .flatMap(_ => UsingResource.pure(testResource3))
        .use { newResource =>
          actualContent = content ++ content2 ++ newResource.content
          Failure[Unit](TestException(123))
        }
        .fold(
          {
            case TestException(123) => Result.success
            case ex: Throwable =>
              Result
                .failure
                .log(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
          },
          _ => Result.failure.log(s"Error was expected but no expected error was given"),
        )

      val after = List(
        (actualContent ==== expectedContent).log("[A] content does not match"),
        (testResource.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("[A] testResource.closeStatus does not match"),
        (testResource2.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("[A] testResource2.closeStatus does not match"),
        (testResource3.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[A] testResource3.closeStatus does not match"),
      )

      Result.all(before ++ List(result) ++ after)
    }

  def testUsingResourceFlatMapAndMap: Property =
    for {
      content  <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content")
      content2 <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content2")
      content3 <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content3")
    } yield {

      val expectedContent = content ++ content2 ++ content3
      val testResource    = TestResource.withContent(content)
      val testResource2   = TestResource.withContent(content2)
      val testResource3   = TestResource()

      val resource = UsingResource[TestResource](testResource)

      var actualContent = Vector.empty[String] // scalafix:ok DisableSyntax.var

      val before = List(
        (testResource.content ==== content).log("[B] testResource.content does not match"),
        (testResource.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[B] testResource.closeStatus does not match"),
        (testResource2.content ==== content2).log("[B] testResource2.content does not match"),
        (testResource2.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[B] testResource2.closeStatus does not match"),
        (testResource3.content ==== Vector.empty).log("[B] testResource3.content does not match"),
        (testResource3.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[B] testResource3.closeStatus does not match"),
      )

      val result = resource
        .flatMap { _ =>
          UsingResource(testResource2)
        }
        .map(_.content ++ content3)
        .flatMap { content =>
          content.foreach(testResource3.write)
          UsingResource.pure(testResource3)
        }
        .use { newResource =>
          actualContent = content ++ newResource.content
          Try(())
        }
        .fold(
          { err =>
            Result.failure.log(s"No error expected but got ${err.toString}")
          },
          _ => Result.success,
        )

      val after = List(
        (actualContent ==== expectedContent).log("[A] content does not match"),
        (testResource.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("[A] testResource.closeStatus does not match"),
        (testResource2.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("[A] testResource2.closeStatus does not match"),
        (testResource3.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[A] testResource3.closeStatus does not match"),
      )

      Result.all(before ++ List(result) ++ after)
    }

  def testUsingResourceFlatMapAndMapErrorCase: Property =
    for {
      content  <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content")
      content2 <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content2")
      content3 <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content3")
    } yield {
      val expectedContent = content ++ content2 ++ content3
      val testResource    = TestResource.withContent(content)
      val testResource2   = TestResource.withContent(content2)
      val testResource3   = TestResource()

      val resource      = ReleasableResource.usingResource[TestResource](testResource)
      var actualContent = Vector.empty[String] // scalafix:ok DisableSyntax.var

      val before = List(
        (testResource.content ==== content).log("[B] testResource.content does not match"),
        (testResource.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[B] testResource.closeStatus does not match"),
        (testResource2.content ==== content2).log("[B] testResource2.content does not match"),
        (testResource2.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[B] testResource2.closeStatus does not match"),
        (testResource3.content ==== Vector.empty).log("[B] testResource3.content does not match"),
        (testResource3.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[B] testResource3.closeStatus does not match"),
      )

      val result = resource
        .flatMap(_ => UsingResource(testResource2))
        .map(_.content ++ content3)
        .flatMap { content =>
          content.foreach(testResource3.write)
          UsingResource.pure(testResource3)
        }
        .use { newResource =>
          actualContent = content ++ newResource.content
          Failure[Unit](TestException(123))
        }
        .fold(
          {
            case TestException(123) => Result.success
            case ex: Throwable =>
              Result
                .failure
                .log(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
          },
          _ => Result.failure.log(s"Error was expected but no expected error was given"),
        )

      val after = List(
        (actualContent ==== expectedContent).log("[A] content does not match"),
        (testResource.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("[A] testResource.closeStatus does not match"),
        (testResource2.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("[A] testResource2.closeStatus does not match"),
        (testResource3.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[A] testResource3.closeStatus does not match"),
      )

      Result.all(before ++ List(result) ++ after)
    }

}
