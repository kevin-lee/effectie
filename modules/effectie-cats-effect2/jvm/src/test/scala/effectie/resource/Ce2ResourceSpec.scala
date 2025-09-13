package effectie.resource

import cats.effect._
import cats.syntax.all._
import effectie.instances.ce2.fx.ioFx
import effectie.resource.data.TestErrors.TestException
import effectie.resource.data.{TestResource, TestResourceNoAutoClose, TestableResource}
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
    property(
      "test Ce2Resource.pure",
      testPure,
    ),
    property(
      "test Ce2Resource.pure - error case",
      testPureErrorCase,
    ),
    property(
      "test Ce2Resource.map",
      testCe2ResourceMap,
    ),
    property(
      "test Ce2Resource.map error case",
      testCe2ResourceMapErrorCase,
    ),
    property(
      "test Ce2Resource.flatMap",
      testCe2ResourceFlatMap,
    ),
    property(
      "test Ce2Resource.flatMap error case",
      testCe2ResourceFlatMapErrorCase,
    ),
    property(
      "test Ce2Resource.flatMap and Ce2Resource.map",
      testCe2ResourceFlatMapAndMap,
    ),
    property(
      "test Ce2Resource.flatMap and Ce2Resource.map error case",
      testCe2ResourceFlatMapAndMapErrorCase,
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

  def testPure: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield ReleasableResourceSpec
      .testReleasableResourceUse[F](TestResourceNoAutoClose.apply)
      .withPure(
        content,
        _ => F.unit,
        none,
        Ce2Resource.pure(_),
      )
      .unsafeRunSync()

  def testPureErrorCase: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield ReleasableResourceSpec
      .testReleasableResourceUse[F](TestResourceNoAutoClose.apply)
      .withPure(
        content,
        _ => F.raiseError(TestException(123)),
        Option({
          case TestException(123) => Result.success
          case ex: Throwable =>
            Result
              .failure
              .log(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
        }),
        Ce2Resource.pure(_),
      )
      .unsafeRunSync()

  def testCe2ResourceMap: Property =
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

      val resource = Ce2Resource.fromAutoCloseable(F.delay(testResource))

      var actualContent = Vector.empty[String] // scalafix:ok DisableSyntax.var

      val before = List(
        (testResource.content ==== content).log("[B] content does not match"),
        (testResource.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[B] closeStatus does not match"),
      )

      resource
        .map(_.content ++ content2)
        .map(_ ++ content3)
        .use { content =>
          actualContent = content
          F.unit
        }
        .unsafeRunSync()

      val after = List(
        (actualContent ==== expectedContent).log("[A] content does not match"),
        (testResource.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("[A] closeStatus does not match"),
      )

      Result.all(before ++ after)
    }

  def testCe2ResourceMapErrorCase: Property =
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

      val resource      = Ce2Resource.fromAutoCloseable(F.delay(testResource))
      var actualContent = Vector.empty[String] // scalafix:ok DisableSyntax.var

      val before = List(
        (testResource.content ==== content).log("[B] content does not match"),
        (testResource.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[B] closeStatus does not match"),
      )

      val actual = resource
        .map(_.content ++ content2)
        .map(_ ++ content3)
        .use { content =>
          actualContent = content
          F.raiseError[Unit](TestException(123))
        }
        .map(_ => Result.failure.log(s"Error was expected but no expected error was given"))
        .recover {
          case TestException(123) => Result.success
          case ex: Throwable =>
            Result
              .failure
              .log(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
        }
        .unsafeRunSync()

      val after = List(
        (actualContent ==== expectedContent).log("[A] content does not match"),
        (testResource.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("[A] closeStatus does not match"),
      )

      Result.all(before ++ List(actual) ++ after)
    }

  def testCe2ResourceFlatMap: Property =
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

      val resource = Ce2Resource.fromAutoCloseable(F.delay(testResource))

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
          Ce2Resource.fromAutoCloseable(F.delay(testResource2))
        }
        .flatMap(_ => Ce2Resource(Resource.pure[F, TestableResource](testResource3)))
        .use { newResource =>
          actualContent = content ++ content2 ++ newResource.content
          F.unit
        }
        .map(_ => Result.success)
        .handleError(err => Result.failure.log(s"No error expected but got ${err.toString}"))
        .unsafeRunSync()

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

  def testCe2ResourceFlatMapErrorCase: Property =
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

      val resource      = Ce2Resource.fromAutoCloseable(F.delay(testResource))
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
        .flatMap(_ => Ce2Resource.fromAutoCloseable(F.delay(testResource2)))
        .flatMap(_ => Ce2Resource(Resource.pure[F, TestableResource](testResource3)))
        .use { newResource =>
          actualContent = content ++ content2 ++ newResource.content
          F.raiseError[Unit](TestException(123))
        }
        .map(_ => Result.failure.log(s"Error was expected but no expected error was given"))
        .recover {
          case TestException(123) => Result.success
          case ex: Throwable =>
            Result
              .failure
              .log(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
        }
        .unsafeRunSync()

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

  def testCe2ResourceFlatMapAndMap: Property =
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

      val resource = Ce2Resource.fromAutoCloseable(F.delay(testResource))

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
          Ce2Resource.fromAutoCloseable(F.delay(testResource2))
        }
        .map(_.content ++ content3)
        .flatMap { content =>
          Ce2Resource
            .make(F.delay(content.foreach(testResource3.write)))(_ => F.unit)
            .flatMap { _ =>
              Ce2Resource(Resource.pure[F, TestableResource](testResource3))
            }
        }
        .use { newResource =>
          actualContent = content ++ newResource.content
          F.unit
        }
        .map(_ => Result.success)
        .handleError(err => Result.failure.log(s"No error expected but got ${err.toString}"))
        .unsafeRunSync()

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

  def testCe2ResourceFlatMapAndMapErrorCase: Property =
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

      val resource      = Ce2Resource.fromAutoCloseable(F.delay(testResource))
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
        .flatMap(_ => Ce2Resource.fromAutoCloseable(F.delay(testResource2)))
        .map(_.content ++ content3)
        .flatMap { content =>
          Ce2Resource
            .make(F.delay(content.foreach(testResource3.write)))(_ => F.unit)
            .flatMap { _ =>
              Ce2Resource(Resource.pure[F, TestableResource](testResource3))
            }
        }
        .use { newResource =>
          actualContent = content ++ newResource.content
          F.raiseError[Unit](TestException(123))
        }
        .map(_ => Result.failure.log(s"Error was expected but no expected error was given"))
        .recover {
          case TestException(123) => Result.success
          case ex: Throwable =>
            Result
              .failure
              .log(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
        }
        .unsafeRunSync()

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
