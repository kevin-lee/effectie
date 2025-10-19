package effectie.resource

import cats.effect.{Resource, _}
import cats.syntax.all._
import effectie.instances.ce3.fx.ioFx
import effectie.resource.data.TestErrors.TestException
import effectie.resource.data.{TestResource, TestResourceNoAutoClose, TestableResource}
import effectie.testing.RandomGens
import munit.Assertions

/** @author Kevin Lee
  * @since 2022-11-06
  */
class Ce3ResourceSpec extends munit.CatsEffectSuite {
  type F[A] = IO[A]
  val F: IO.type = IO

  test("test Ce3Resource.fromAutoCloseable") {
    val content = RandomGens.genAlphaStringList(100, 10).toVector

    ReleasableResourceForMUnit
      .testReleasableResourceUse[F](TestResource.apply)(content, _ => F.unit, none, Ce3Resource.fromAutoCloseable)
      .unsafeToFuture()
  }

  test("test Ce3Resource.fromAutoCloseable - error case") {
    val content = RandomGens.genAlphaStringList(100, 10).toVector

    ReleasableResourceForMUnit
      .testReleasableResourceUse[F](TestResource.apply)(
        content,
        _ => F.raiseError(TestException(123)),
        Option({
          case TestException(123) => ()
          case ex: Throwable =>
            Assertions.fail(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
        }),
        Ce3Resource.fromAutoCloseable,
      )
      .unsafeToFuture()
  }

  test("test Ce3Resource.make") {
    val content = RandomGens.genAlphaStringList(100, 10).toVector

    ReleasableResourceForMUnit
      .testReleasableResourceUse[F](TestResourceNoAutoClose.apply)(
        content,
        _ => F.unit,
        none,
        Ce3Resource.make(_)(a => F.delay(a.release())),
      )
      .unsafeToFuture()
  }

  test("test Ce3Resource.make - error case") {
    val content = RandomGens.genAlphaStringList(100, 10).toVector

    ReleasableResourceForMUnit
      .testReleasableResourceUse[F](TestResourceNoAutoClose.apply)(
        content,
        _ => F.raiseError(TestException(123)),
        Option({
          case TestException(123) => ()
          case ex: Throwable =>
            Assertions.fail(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
        }),
        Ce3Resource.make(_)(a => F.delay(a.release())),
      )
      .unsafeToFuture()
  }

  test("test Ce3Resource.pure") {
    val content = RandomGens.genAlphaStringList(100, 10).toVector

    ReleasableResourceForMUnit
      .testReleasableResourceUse[F](TestResourceNoAutoClose.apply)
      .withPure(
        content,
        _ => F.unit,
        none,
        Ce3Resource.pure(_),
      )
      .unsafeToFuture()
  }

  test("test Ce3Resource.pure - error case") {
    val content = RandomGens.genAlphaStringList(100, 10).toVector

    ReleasableResourceForMUnit
      .testReleasableResourceUse[F](TestResourceNoAutoClose.apply)
      .withPure(
        content,
        _ => F.raiseError(TestException(123)),
        Option({
          case TestException(123) => ()
          case ex: Throwable =>
            Assertions.fail(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
        }),
        Ce3Resource.pure(_),
      )
      .unsafeToFuture()
  }

  test("test Ce3Resource.map") {
    val content  = RandomGens.genAlphaStringList(100, 10).toVector
    val content2 = RandomGens.genAlphaStringList(100, 10).toVector
    val content3 = RandomGens.genAlphaStringList(100, 10).toVector

    val expectedContent = content ++ content2 ++ content3
    val testResource    = TestResource.withContent(content)

    val resource = Ce3Resource.fromAutoCloseable(F.delay(testResource))

    var actualContent = Vector.empty[String] // scalafix:ok DisableSyntax.var

    Assertions.assertEquals(testResource.content, content, "[B] content does not match")
    Assertions.assertEquals(
      testResource.closeStatus,
      TestableResource.CloseStatus.notClosed,
      "[B] closeStatus does not match",
    )

    resource
      .map(_.content ++ content2)
      .map(_ ++ content3)
      .use { content =>
        actualContent = content
        F.unit
      }
      .map { _ =>
        Assertions.assertEquals(actualContent, expectedContent, "[A] content does not match")
        Assertions.assertEquals(
          testResource.closeStatus,
          TestableResource.CloseStatus.closed,
          "[A] closeStatus does not match",
        )
      }
      .unsafeToFuture()
  }

  test("test Ce3Resource.map error case") {
    val content  = RandomGens.genAlphaStringList(100, 10).toVector
    val content2 = RandomGens.genAlphaStringList(100, 10).toVector
    val content3 = RandomGens.genAlphaStringList(100, 10).toVector

    val expectedContent = content ++ content2 ++ content3
    val testResource    = TestResource.withContent(content)

    val resource      = Ce3Resource.fromAutoCloseable(F.delay(testResource))
    var actualContent = Vector.empty[String] // scalafix:ok DisableSyntax.var

    Assertions.assertEquals(testResource.content, content, "[B] content does not match")
    Assertions.assertEquals(
      testResource.closeStatus,
      TestableResource.CloseStatus.notClosed,
      "[B] closeStatus does not match",
    )

    resource
      .map(_.content ++ content2)
      .map(_ ++ content3)
      .use { content =>
        actualContent = content
        F.raiseError[Unit](TestException(123))
      }
      .map[Unit](_ => Assertions.fail(s"Error was expected but no expected error was given"))
      .recover {
        case TestException(123) => ()
        case ex: Throwable =>
          Assertions.fail(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
      }
      .map { _ =>
        Assertions.assertEquals(actualContent, expectedContent, "[A] content does not match")
        Assertions.assertEquals(
          testResource.closeStatus,
          TestableResource.CloseStatus.closed,
          "[A] closeStatus does not match",
        )
      }
      .unsafeToFuture()
  }

  test("test Ce3Resource.flatMap") {
    val content  = RandomGens.genAlphaStringList(100, 10).toVector
    val content2 = RandomGens.genAlphaStringList(100, 10).toVector
    val content3 = RandomGens.genAlphaStringList(100, 10).toVector

    val expectedContent = content ++ content2 ++ content3
    val testResource    = TestResource.withContent(content)
    val testResource2   = TestResource.withContent(content2)
    val testResource3   = TestResource.withContent(content3)

    val resource = Ce3Resource.fromAutoCloseable(F.delay(testResource))

    var actualContent = Vector.empty[String] // scalafix:ok DisableSyntax.var

    Assertions.assertEquals(testResource.content, content, "[B] testResource.content does not match")
    Assertions.assertEquals(
      testResource.closeStatus,
      TestableResource.CloseStatus.notClosed,
      "[B] testResource.closeStatus does not match",
    )
    Assertions.assertEquals(testResource2.content, content2, "[B] testResource2.content does not match")
    Assertions.assertEquals(
      testResource2.closeStatus,
      TestableResource.CloseStatus.notClosed,
      "[B] testResource2.closeStatus does not match",
    )
    Assertions.assertEquals(testResource3.content, content3, "[B] testResource3.content does not match")
    Assertions.assertEquals(
      testResource3.closeStatus,
      TestableResource.CloseStatus.notClosed,
      "[B] testResource3.closeStatus does not match",
    )

    resource
      .flatMap { _ =>
        Ce3Resource.fromAutoCloseable(F.delay(testResource2))
      }
      .flatMap(_ => Ce3Resource(Resource.pure[F, TestableResource](testResource3)))
      .use { newResource =>
        actualContent = content ++ content2 ++ newResource.content
        F.unit
      }
      .map(_ => ())
      .handleError(err => Assertions.fail(s"No error expected but got ${err.toString}"))
      .map { _ =>

        Assertions.assertEquals(actualContent, expectedContent, "[A] content does not match")
        Assertions.assertEquals(
          testResource.closeStatus,
          TestableResource.CloseStatus.closed,
          "[A] testResource.closeStatus does not match",
        )
        Assertions.assertEquals(
          testResource2.closeStatus,
          TestableResource.CloseStatus.closed,
          "[A] testResource2.closeStatus does not match",
        )
        Assertions.assertEquals(
          testResource3.closeStatus,
          TestableResource.CloseStatus.notClosed,
          "[A] testResource3.closeStatus does not match",
        )
      }
      .unsafeToFuture()
  }

  test("test Ce3Resource.flatMap error case") {
    val content  = RandomGens.genAlphaStringList(100, 10).toVector
    val content2 = RandomGens.genAlphaStringList(100, 10).toVector
    val content3 = RandomGens.genAlphaStringList(100, 10).toVector

    val expectedContent = content ++ content2 ++ content3
    val testResource    = TestResource.withContent(content)
    val testResource2   = TestResource.withContent(content2)
    val testResource3   = TestResource.withContent(content3)

    val resource      = Ce3Resource.fromAutoCloseable(F.delay(testResource))
    var actualContent = Vector.empty[String] // scalafix:ok DisableSyntax.var

    Assertions.assertEquals(testResource.content, content, "[B] testResource.content does not match")
    Assertions.assertEquals(
      testResource.closeStatus,
      TestableResource.CloseStatus.notClosed,
      "[B] testResource.closeStatus does not match",
    )
    Assertions.assertEquals(testResource2.content, content2, "[B] testResource2.content does not match")
    Assertions.assertEquals(
      testResource2.closeStatus,
      TestableResource.CloseStatus.notClosed,
      "[B] testResource2.closeStatus does not match",
    )
    Assertions.assertEquals(testResource3.content, content3, "[B] testResource3.content does not match")
    Assertions.assertEquals(
      testResource3.closeStatus,
      TestableResource.CloseStatus.notClosed,
      "[B] testResource3.closeStatus does not match",
    )

    resource
      .flatMap(_ => Ce3Resource.fromAutoCloseable(F.delay(testResource2)))
      .flatMap(_ => Ce3Resource(Resource.pure[F, TestableResource](testResource3)))
      .use { newResource =>
        actualContent = content ++ content2 ++ newResource.content
        F.raiseError[Unit](TestException(123))
      }
      .map[Unit](_ => Assertions.fail(s"Error was expected but no expected error was given"))
      .recover {
        case TestException(123) => ()
        case ex: Throwable =>
          Assertions.fail(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
      }
      .map { _ =>
        Assertions.assertEquals(actualContent, expectedContent, "[A] content does not match")
        Assertions.assertEquals(
          testResource.closeStatus,
          TestableResource.CloseStatus.closed,
          "[A] testResource.closeStatus does not match",
        )
        Assertions.assertEquals(
          testResource2.closeStatus,
          TestableResource.CloseStatus.closed,
          "[A] testResource2.closeStatus does not match",
        )
        Assertions.assertEquals(
          testResource3.closeStatus,
          TestableResource.CloseStatus.notClosed,
          "[A] testResource3.closeStatus does not match",
        )
      }
      .unsafeToFuture()
  }

  test("test Ce3Resource.flatMap and Ce3Resource.map") {
    val content  = RandomGens.genAlphaStringList(100, 10).toVector
    val content2 = RandomGens.genAlphaStringList(100, 10).toVector
    val content3 = RandomGens.genAlphaStringList(100, 10).toVector

    val expectedContent = content ++ content2 ++ content3
    val testResource    = TestResource.withContent(content)
    val testResource2   = TestResource.withContent(content2)
    val testResource3   = TestResource()

    val resource = Ce3Resource.fromAutoCloseable(F.delay(testResource))

    var actualContent = Vector.empty[String] // scalafix:ok DisableSyntax.var

    Assertions.assertEquals(testResource.content, content, "[B] testResource.content does not match")
    Assertions.assertEquals(
      testResource.closeStatus,
      TestableResource.CloseStatus.notClosed,
      "[B] testResource.closeStatus does not match",
    )
    Assertions.assertEquals(testResource2.content, content2, "[B] testResource2.content does not match")
    Assertions.assertEquals(
      testResource2.closeStatus,
      TestableResource.CloseStatus.notClosed,
      "[B] testResource2.closeStatus does not match",
    )
    Assertions.assertEquals(testResource3.content, Vector.empty, "[B] testResource3.content does not match")
    Assertions.assertEquals(
      testResource3.closeStatus,
      TestableResource.CloseStatus.notClosed,
      "[B] testResource3.closeStatus does not match",
    )

    resource
      .flatMap { _ =>
        Ce3Resource.fromAutoCloseable(F.delay(testResource2))
      }
      .map(_.content ++ content3)
      .flatMap { content =>
        Ce3Resource
          .make(F.delay(content.foreach(testResource3.write)))(_ => F.unit)
          .flatMap { _ =>
            Ce3Resource(Resource.pure[F, TestableResource](testResource3))
          }
      }
      .use { newResource =>
        actualContent = content ++ newResource.content
        F.unit
      }
      .map(_ => ())
      .handleError(err => Assertions.fail(s"No error expected but got ${err.toString}"))
      .map { _ =>

        Assertions.assertEquals(actualContent, expectedContent, "[A] content does not match")
        Assertions.assertEquals(
          testResource.closeStatus,
          TestableResource.CloseStatus.closed,
          "[A] testResource.closeStatus does not match",
        )
        Assertions.assertEquals(
          testResource2.closeStatus,
          TestableResource.CloseStatus.closed,
          "[A] testResource2.closeStatus does not match",
        )
        Assertions.assertEquals(
          testResource3.closeStatus,
          TestableResource.CloseStatus.notClosed,
          "[A] testResource3.closeStatus does not match",
        )
      }
      .unsafeToFuture()
  }

  test("test Ce3Resource.flatMap and Ce3Resource.map error case") {
    val content  = RandomGens.genAlphaStringList(100, 10).toVector
    val content2 = RandomGens.genAlphaStringList(100, 10).toVector
    val content3 = RandomGens.genAlphaStringList(100, 10).toVector

    val expectedContent = content ++ content2 ++ content3
    val testResource    = TestResource.withContent(content)
    val testResource2   = TestResource.withContent(content2)
    val testResource3   = TestResource()

    val resource      = Ce3Resource.fromAutoCloseable(F.delay(testResource))
    var actualContent = Vector.empty[String] // scalafix:ok DisableSyntax.var

    Assertions.assertEquals(testResource.content, content, "[B] testResource.content does not match")
    Assertions.assertEquals(
      testResource.closeStatus,
      TestableResource.CloseStatus.notClosed,
      "[B] testResource.closeStatus does not match",
    )
    Assertions.assertEquals(testResource2.content, content2, "[B] testResource2.content does not match")
    Assertions.assertEquals(
      testResource2.closeStatus,
      TestableResource.CloseStatus.notClosed,
      "[B] testResource2.closeStatus does not match",
    )
    Assertions.assertEquals(testResource3.content, Vector.empty, "[B] testResource3.content does not match")
    Assertions.assertEquals(
      testResource3.closeStatus,
      TestableResource.CloseStatus.notClosed,
      "[B] testResource3.closeStatus does not match",
    )

    resource
      .flatMap(_ => Ce3Resource.fromAutoCloseable(F.delay(testResource2)))
      .map(_.content ++ content3)
      .flatMap { content =>
        Ce3Resource
          .make(F.delay(content.foreach(testResource3.write)))(_ => F.unit)
          .flatMap { _ =>
            Ce3Resource(Resource.pure[F, TestableResource](testResource3))
          }
      }
      .use { newResource =>
        actualContent = content ++ newResource.content
        F.raiseError[Unit](TestException(123))
      }
      .map[Unit](_ => Assertions.fail(s"Error was expected but no expected error was given"))
      .recover {
        case TestException(123) => ()
        case ex: Throwable =>
          Assertions.fail(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
      }
      .map { _ =>

        Assertions.assertEquals(actualContent, expectedContent, "[A] content does not match")
        Assertions.assertEquals(
          testResource.closeStatus,
          TestableResource.CloseStatus.closed,
          "[A] testResource.closeStatus does not match",
        )
        Assertions.assertEquals(
          testResource2.closeStatus,
          TestableResource.CloseStatus.closed,
          "[A] testResource2.closeStatus does not match",
        )
        Assertions.assertEquals(
          testResource3.closeStatus,
          TestableResource.CloseStatus.notClosed,
          "[A] testResource3.closeStatus does not match",
        )
      }
      .unsafeToFuture()
  }

}
