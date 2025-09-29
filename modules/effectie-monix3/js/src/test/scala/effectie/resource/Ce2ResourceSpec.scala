package effectie.resource

import cats.effect.Resource
import cats.syntax.all._
import effectie.instances.monix3.fx.taskFx
import effectie.resource.data.TestErrors.TestException
import effectie.resource.data.{TestResource, TestResourceNoAutoClose, TestableResource}
import effectie.testing.{FutureTools, RandomGens}
import munit.Assertions

import scala.concurrent._
import scala.concurrent.duration._
import monix.eval.Task

/** @author Kevin Lee
  * @since 2022-11-06
  */
class Ce2ResourceSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  import monix.execution.Scheduler.Implicits.global

  override val munitTimeout: FiniteDuration = 200.milliseconds

  type F[A] = Task[A]
  val F: Task.type = Task

  test("test Ce2Resource.fromAutoCloseable") {
    val content = RandomGens.genAlphaStringList(100, 10).toVector

    ReleasableResourceForMUnit
      .testReleasableResourceUse[F](TestResource.apply)(content, _ => F.unit, none, Ce2Resource.fromAutoCloseable)
      .runToFuture
  }

  test("test Ce2Resource.fromAutoCloseable - error case") {
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
        Ce2Resource.fromAutoCloseable,
      )
      .runToFuture
  }

  test("test Ce2Resource.make") {
    val content = RandomGens.genAlphaStringList(100, 10).toVector

    ReleasableResourceForMUnit
      .testReleasableResourceUse[F](TestResourceNoAutoClose.apply)(
        content,
        _ => F.unit,
        none,
        Ce2Resource.make(_)(a => F.delay(a.release())),
      )
      .runToFuture
  }

  test("test Ce2Resource.make - error case") {
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
        Ce2Resource.make(_)(a => F.delay(a.release())),
      )
      .runToFuture
  }

  test("test Ce2Resource.pure") {
    val content = RandomGens.genAlphaStringList(100, 10).toVector

    ReleasableResourceForMUnit
      .testReleasableResourceUse[F](TestResourceNoAutoClose.apply)
      .withPure(
        content,
        _ => F.unit,
        none,
        Ce2Resource.pure(_),
      )
      .runToFuture
  }

  test("test Ce2Resource.pure - error case") {
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
        Ce2Resource.pure(_),
      )
      .runToFuture
  }

  test("test Ce2Resource.map") {
    val content  = RandomGens.genAlphaStringList(100, 10).toVector
    val content2 = RandomGens.genAlphaStringList(100, 10).toVector
    val content3 = RandomGens.genAlphaStringList(100, 10).toVector

    val expectedContent = content ++ content2 ++ content3
    val testResource    = TestResource.withContent(content)

    val resource = Ce2Resource.fromAutoCloseable(F.delay(testResource))

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
      .runToFuture
  }

  test("test Ce2Resource.map error case") {
    val content  = RandomGens.genAlphaStringList(100, 10).toVector
    val content2 = RandomGens.genAlphaStringList(100, 10).toVector
    val content3 = RandomGens.genAlphaStringList(100, 10).toVector

    val expectedContent = content ++ content2 ++ content3
    val testResource    = TestResource.withContent(content)

    val resource      = Ce2Resource.fromAutoCloseable(F.delay(testResource))
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
      .runToFuture
  }

  test("test Ce2Resource.flatMap") {
    val content  = RandomGens.genAlphaStringList(100, 10).toVector
    val content2 = RandomGens.genAlphaStringList(100, 10).toVector
    val content3 = RandomGens.genAlphaStringList(100, 10).toVector

    val expectedContent = content ++ content2 ++ content3
    val testResource    = TestResource.withContent(content)
    val testResource2   = TestResource.withContent(content2)
    val testResource3   = TestResource.withContent(content3)

    val resource = Ce2Resource.fromAutoCloseable(F.delay(testResource))

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
        Ce2Resource.fromAutoCloseable(F.delay(testResource2))
      }
      .flatMap(_ => Ce2Resource(Resource.pure[F, TestableResource](testResource3)))
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
      .runToFuture
  }

  test("test Ce2Resource.flatMap error case") {
    val content  = RandomGens.genAlphaStringList(100, 10).toVector
    val content2 = RandomGens.genAlphaStringList(100, 10).toVector
    val content3 = RandomGens.genAlphaStringList(100, 10).toVector

    val expectedContent = content ++ content2 ++ content3
    val testResource    = TestResource.withContent(content)
    val testResource2   = TestResource.withContent(content2)
    val testResource3   = TestResource.withContent(content3)

    val resource      = Ce2Resource.fromAutoCloseable(F.delay(testResource))
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
      .flatMap(_ => Ce2Resource.fromAutoCloseable(F.delay(testResource2)))
      .flatMap(_ => Ce2Resource(Resource.pure[F, TestableResource](testResource3)))
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
      .runToFuture
  }

  test("test Ce2Resource.flatMap and Ce2Resource.map") {
    val content  = RandomGens.genAlphaStringList(100, 10).toVector
    val content2 = RandomGens.genAlphaStringList(100, 10).toVector
    val content3 = RandomGens.genAlphaStringList(100, 10).toVector

    val expectedContent = content ++ content2 ++ content3
    val testResource    = TestResource.withContent(content)
    val testResource2   = TestResource.withContent(content2)
    val testResource3   = TestResource()

    val resource = Ce2Resource.fromAutoCloseable(F.delay(testResource))

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
      .runToFuture
  }

  test("test Ce2Resource.flatMap and Ce2Resource.map error case") {
    val content  = RandomGens.genAlphaStringList(100, 10).toVector
    val content2 = RandomGens.genAlphaStringList(100, 10).toVector
    val content3 = RandomGens.genAlphaStringList(100, 10).toVector

    val expectedContent = content ++ content2 ++ content3
    val testResource    = TestResource.withContent(content)
    val testResource2   = TestResource.withContent(content2)
    val testResource3   = TestResource()

    val resource      = Ce2Resource.fromAutoCloseable(F.delay(testResource))
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
      .runToFuture
  }

}
