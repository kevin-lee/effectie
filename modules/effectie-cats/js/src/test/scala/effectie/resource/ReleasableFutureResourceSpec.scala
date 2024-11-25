package effectie.resource

import cats.syntax.all._
import effectie.resource.data.TestErrors.TestException
import effectie.resource.data.{TestResource, TestResourceNoAutoClose, TestableResource}
import effectie.testing.{FutureTools, RandomGens}
import munit.Assertions

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/** @author Kevin Lee
  * @since 2022-11-12
  */
class ReleasableFutureResourceSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  override val munitTimeout: FiniteDuration = 200.milliseconds

  test("test ReleasableFutureResource[A]") {
    val content = RandomGens.genAlphaStringList(100, 10).toVector

    import effectie.instances.future.fx._

    import scala.concurrent.Future

    ReleasableResourceForMUnit
      .testReleasableResourceUse[Future](TestResource.apply)(
        content,
        _ => Future.successful(()),
        none,
        ReleasableResource.futureResource,
      )

  }

  test("test ReleasableFutureResource[A] - error case") {
    val content = RandomGens.genAlphaStringList(100, 10).toVector

    import effectie.instances.future.fx._

    import scala.concurrent.Future

    ReleasableResourceForMUnit
      .testReleasableResourceUse[Future](TestResource.apply)(
        content,
        _ => Future.failed(TestException(123)),
        Option({
          case TestException(123) => ()
          case ex: Throwable =>
            Assertions.fail(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
        }),
        ReleasableResource.futureResource,
      )

  }

  test("test ReleasableFutureResource[A].make") {
    val content = RandomGens.genAlphaStringList(100, 10).toVector

    import effectie.instances.future.fx._

    import scala.concurrent.Future

    Future
      .sequence(
        List(
          ReleasableResourceForMUnit
            .testReleasableResourceUse[Future](TestResourceNoAutoClose.apply)(
              content,
              _ => Future.successful(()),
              none,
              ReleasableFutureResource.make(_)(a => Future(a.release())),
            ),
          ReleasableResourceForMUnit
            .testReleasableResourceUse[Future](TestResourceNoAutoClose.apply)(
              content,
              _ => Future.successful(()),
              none,
              ReleasableResource.makeFuture(_)(a => Future(a.release())),
            ),
        )
      )

  }

  test("test ReleasableFutureResource[A].make - error case") {
    val content = RandomGens.genAlphaStringList(100, 10).toVector

    import effectie.instances.future.fx._

    import scala.concurrent.Future

    Future
      .sequence(
        List(
          ReleasableResourceForMUnit
            .testReleasableResourceUse[Future](TestResourceNoAutoClose.apply)(
              content,
              _ => Future.failed(TestException(123)),
              Option({
                case TestException(123) => ()
                case ex: Throwable =>
                  Assertions.fail(
                    s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}"
                  )
              }),
              ReleasableFutureResource.make(_)(a => Future(a.release())),
            ),
          ReleasableResourceForMUnit
            .testReleasableResourceUse[Future](TestResourceNoAutoClose.apply)(
              content,
              _ => Future.failed(TestException(123)),
              Option({
                case TestException(123) => ()
                case ex: Throwable =>
                  Assertions.fail(
                    s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}"
                  )
              }),
              ReleasableResource.makeFuture(_)(a => Future(a.release())),
            ),
        )
      )

  }

  test("test ReleasableFutureResource[A].pure") {
    val content = RandomGens.genAlphaStringList(100, 10).toVector

    import effectie.instances.future.fx._

    import scala.concurrent.Future

    Future
      .sequence(
        List(
          ReleasableResourceForMUnit
            .testReleasableResourceUse[Future](TestResourceNoAutoClose.apply)
            .withPure(
              content,
              _ => Future.successful(()),
              none,
              ReleasableFutureResource.pure(_),
            ),
          ReleasableResourceForMUnit
            .testReleasableResourceUse[Future](TestResourceNoAutoClose.apply)
            .withPure(
              content,
              _ => Future.successful(()),
              none,
              ReleasableResource.pureFuture(_),
            ),
        )
      )

  }

  test("test ReleasableFutureResource[A].pure - error case") {
    val content = RandomGens.genAlphaStringList(100, 10).toVector

    import effectie.instances.future.fx._

    import scala.concurrent.Future

    Future
      .sequence(
        List(
          ReleasableResourceForMUnit
            .testReleasableResourceUse[Future](TestResourceNoAutoClose.apply)
            .withPure(
              content,
              _ => Future.failed(TestException(123)),
              Option({
                case TestException(123) => ()
                case ex: Throwable =>
                  Assertions.fail(
                    s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}"
                  )
              }),
              ReleasableFutureResource.pure(_),
            ),
          ReleasableResourceForMUnit
            .testReleasableResourceUse[Future](TestResourceNoAutoClose.apply)
            .withPure(
              content,
              _ => Future.failed(TestException(123)),
              Option({
                case TestException(123) => ()
                case ex: Throwable =>
                  Assertions.fail(
                    s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}"
                  )
              }),
              ReleasableResource.pureFuture(_),
            ),
        )
      )

  }

  test("test ReleasableFutureResource.map") {
    val content  = RandomGens.genAlphaStringList(100, 10).toVector
    val content2 = RandomGens.genAlphaStringList(100, 10).toVector
    val content3 = RandomGens.genAlphaStringList(100, 10).toVector

    val expectedContent = content ++ content2 ++ content3
    val testResource    = TestResource.withContent(content)

    import scala.concurrent.Future

    val resource = ReleasableFutureResource[TestResource](Future(testResource))

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
        Future.successful(())
      }
      .map { _ =>
        Assertions.assertEquals(actualContent, expectedContent, "[A] content does not match")
        Assertions.assertEquals(
          testResource.closeStatus,
          TestableResource.CloseStatus.closed,
          "[A] closeStatus does not match",
        )
      }

  }

  test("test ReleasableFutureResource.map error case") {
    val content  = RandomGens.genAlphaStringList(100, 10).toVector
    val content2 = RandomGens.genAlphaStringList(100, 10).toVector
    val content3 = RandomGens.genAlphaStringList(100, 10).toVector

    val expectedContent = content ++ content2 ++ content3
    val testResource    = TestResource.withContent(content)

    import scala.concurrent.Future

    val resource      = ReleasableFutureResource[TestResource](Future(testResource))
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
        Future.failed[Unit](TestException(123))
      }
      .map(_ => Assertions.fail(s"Error was expected but no expected error was given"))
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

  }

  test("test ReleasableFutureResource.flatMap") {
    val content  = RandomGens.genAlphaStringList(100, 10).toVector
    val content2 = RandomGens.genAlphaStringList(100, 10).toVector
    val content3 = RandomGens.genAlphaStringList(100, 10).toVector

    val expectedContent = content ++ content2 ++ content3
    val testResource    = TestResource.withContent(content)
    val testResource2   = TestResource.withContent(content2)
    val testResource3   = TestResource.withContent(content3)

    import scala.concurrent.Future

    val resource = ReleasableFutureResource(Future(testResource))

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
        ReleasableFutureResource(Future(testResource2))
      }
      .flatMap(_ => ReleasableFutureResource.pure(testResource3))
      .use { newResource =>
        actualContent = content ++ content2 ++ newResource.content
        Future.unit
      }
      .map(_ => ())
      .recover {
        case err =>
          Assertions.fail(s"No error expected but got ${err.toString}")
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

  }

  test("test ReleasableFutureResource.flatMap error case") {
    val content  = RandomGens.genAlphaStringList(100, 10).toVector
    val content2 = RandomGens.genAlphaStringList(100, 10).toVector
    val content3 = RandomGens.genAlphaStringList(100, 10).toVector

    val expectedContent = content ++ content2 ++ content3
    val testResource    = TestResource.withContent(content)
    val testResource2   = TestResource.withContent(content2)
    val testResource3   = TestResource.withContent(content3)

    import scala.concurrent.Future

    val resource = ReleasableFutureResource(Future(testResource))

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
      .flatMap(_ => ReleasableFutureResource(Future(testResource2)))
      .flatMap(_ => ReleasableFutureResource.pure(testResource3))
      .use { newResource =>
        actualContent = content ++ content2 ++ newResource.content
        Future.failed[Unit](TestException(123))
      }
      .map(_ => Assertions.fail(s"Error was expected but no expected error was given"))
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
  }

  test("test ReleasableFutureResource.flatMap and ReleasableFutureResource.map") {
    val content  = RandomGens.genAlphaStringList(100, 10).toVector
    val content2 = RandomGens.genAlphaStringList(100, 10).toVector
    val content3 = RandomGens.genAlphaStringList(100, 10).toVector

    val expectedContent = content ++ content2 ++ content3
    val testResource    = TestResource.withContent(content)
    val testResource2   = TestResource.withContent(content2)
    val testResource3   = TestResource()

    import scala.concurrent.Future

    val resource = ReleasableFutureResource(Future(testResource))

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
        ReleasableFutureResource(Future(testResource2))
      }
      .map(_.content ++ content3)
      .flatMap { content =>
        ReleasableFutureResource
          .make(Future(content.foreach(testResource3.write)))(_ => Future.unit)
          .flatMap(_ => ReleasableFutureResource.pure(testResource3))
      }
      .use { newResource =>
        actualContent = content ++ newResource.content
        Future.unit
      }
      .map(_ => ())
      .recover {
        case err =>
          Assertions.fail(s"No error expected but got ${err.toString}")
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

  }

  test("test ReleasableFutureResource.flatMap and ReleasableFutureResource.map error case") {
    val content  = RandomGens.genAlphaStringList(100, 10).toVector
    val content2 = RandomGens.genAlphaStringList(100, 10).toVector
    val content3 = RandomGens.genAlphaStringList(100, 10).toVector

    val expectedContent = content ++ content2 ++ content3
    val testResource    = TestResource.withContent(content)
    val testResource2   = TestResource.withContent(content2)
    val testResource3   = TestResource()

    import scala.concurrent.Future

    val resource = ReleasableFutureResource(Future(testResource))

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
      .flatMap(_ => ReleasableFutureResource(Future(testResource2)))
      .map(_.content ++ content3)
      .flatMap { content =>
        ReleasableFutureResource
          .make(Future(content.foreach(testResource3.write)))(_ => Future.unit)
          .flatMap(_ => ReleasableFutureResource.pure(testResource3))
      }
      .use { newResource =>
        actualContent = content ++ newResource.content
        Future.failed[Unit](TestException(123))
      }
      .map(_ => Assertions.fail(s"Error was expected but no expected error was given"))
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
  }

}
