package effectie.resource

import cats.syntax.all._
import effectie.resource.data.TestErrors.TestException
import effectie.resource.data.{TestResource, TestResourceNoAutoClose, TestableResource}
import effectie.testing.FutureTools
import munit.Assertions

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/** @author Kevin Lee
  * @since 2022-11-12
  */
class FutureResourceMakerSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  override val munitTimeout: FiniteDuration = 200.milliseconds

  test("test ResourceMaker.futureResourceMaker: ResourceMaker[Future].forAutoCloseable") {
    val content = Vector("blah", "blah blah", "blah blah blah")

    import effectie.instances.future.fx._

    import scala.concurrent.Future

    implicit val resourceMaker: ResourceMaker[Future] = ResourceMaker.futureResourceMaker

    ResourceMakerForMUnit
      .testForAutoCloseable[Future](TestResource.apply)(
        content,
        _ => Future.successful(()),
        none,
      )

  }

  test("test ResourceMaker.futureResourceMaker: ResourceMaker[Future].forAutoCloseable - error case") {
    import effectie.instances.future.fx._

    import scala.concurrent.Future

    val content = Vector("blah", "blah blah", "blah blah blah")

    implicit val resourceMaker: ResourceMaker[Future] = ResourceMaker.futureResourceMaker

    ResourceMakerForMUnit
      .testForAutoCloseable[Future](TestResource.apply)(
        content,
        _ => Future.failed(TestException(123)),
        Option({
          case TestException(123) => ()
          case ex: Throwable =>
            Assertions.fail(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
        }),
      )

  }

  test("test ResourceMaker.futureResourceMaker: ResourceMaker[Future] with make") {
    import effectie.instances.future.fx._

    import scala.concurrent.Future

    val content = Vector("blah", "blah blah", "blah blah blah")

    implicit val resourceMaker: ResourceMaker[Future] = ResourceMaker.futureResourceMaker

    ResourceMakerForMUnit
      .testForMake[Future](TestResourceNoAutoClose.apply)(
        _.release(),
        content,
        _ => Future.successful(()),
        none,
      )

  }

  test("test ResourceMaker.futureResourceMaker: ResourceMaker[Future] with make - error case") {
    import effectie.instances.future.fx._

    import scala.concurrent.Future

    val content = Vector("blah", "blah blah", "blah blah blah")

    implicit val resourceMaker: ResourceMaker[Future] = ResourceMaker.futureResourceMaker

    ResourceMakerForMUnit
      .testForMake[Future](TestResourceNoAutoClose.apply)(
        _.release(),
        content,
        _ => Future.failed(TestException(123)),
        Option({
          case TestException(123) => ()
          case ex: Throwable =>
            Assertions.fail(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
        }),
      )

  }

  test("test ResourceMaker.futureResourceMaker: ResourceMaker[Future] with make - error case in closing") {
    import effectie.instances.future.fx._

    import scala.concurrent.Future

    val content = Vector("blah", "blah blah", "blah blah blah")

    implicit val resourceMaker: ResourceMaker[Future] = ResourceMaker.futureResourceMaker

    ResourceMakerForMUnit
      .testForMake[Future](TestResourceNoAutoClose.apply)(
        { resource =>
          resource.release()
          throw new RuntimeException(
            "Test error in closing resource. It's only for testing so please ignore."
          ) // scalafix:ok DisableSyntax.throw
        },
        content,
        _ => Future.successful(()),
        none,
      )

  }

  test("test ResourceMaker.futureResourceMaker: ResourceMaker[Future] with pure") {
    import effectie.instances.future.fx._

    import scala.concurrent.Future

    val content = Vector("blah", "blah blah", "blah blah blah")

    implicit val resourceMaker: ResourceMaker[Future] = ResourceMaker.futureResourceMaker

    ResourceMakerForMUnit
      .testFor[Future](TestResourceNoAutoClose.apply)(
        resourceMaker.pure,
        content,
        _ => Future.successful(()),
        closeStatus => Assertions.assertEquals(closeStatus, TestableResource.CloseStatus.NotClosed),
        none,
      )

  }

  test("test ResourceMaker.futureResourceMaker: ResourceMaker[Future] with pure - error case") {
    import effectie.instances.future.fx._

    import scala.concurrent.Future

    val content = Vector("blah", "blah blah", "blah blah blah")

    implicit val resourceMaker: ResourceMaker[Future] = ResourceMaker.futureResourceMaker

    ResourceMakerForMUnit
      .testFor[Future](TestResourceNoAutoClose.apply)(
        resourceMaker.pure,
        content,
        _ => Future.failed(TestException(123)),
        closeStatus => Assertions.assertEquals(closeStatus, TestableResource.CloseStatus.NotClosed),
        Option({
          case TestException(123) => ()
          case ex: Throwable =>
            Assertions.fail(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
        }),
      )

  }

  test("test ResourceMaker.futureResourceMaker: ResourceMaker[Future] with eval") {
    import effectie.instances.future.fx._

    import scala.concurrent.Future

    val content = Vector("blah", "blah blah", "blah blah blah")

    implicit val resourceMaker: ResourceMaker[Future] = ResourceMaker.futureResourceMaker

    ResourceMakerForMUnit
      .testFor[Future](TestResourceNoAutoClose.apply)(
        testResource => resourceMaker.eval(Future(testResource)),
        content,
        _ => Future.successful(()),
        closeStatus => Assertions.assertEquals(closeStatus, TestableResource.CloseStatus.NotClosed),
        none,
      )

  }

  test("test ResourceMaker.futureResourceMaker: ResourceMaker[Future] with eval - error case") {
    import effectie.instances.future.fx._

    import scala.concurrent.Future

    val content = Vector("blah", "blah blah", "blah blah blah")

    implicit val resourceMaker: ResourceMaker[Future] = ResourceMaker.futureResourceMaker

    ResourceMakerForMUnit
      .testFor[Future](TestResourceNoAutoClose.apply)(
        testResource => resourceMaker.eval(Future(testResource)),
        content,
        _ => Future.failed(TestException(123)),
        closeStatus => Assertions.assertEquals(closeStatus, TestableResource.CloseStatus.NotClosed),
        Option({
          case TestException(123) => ()
          case ex: Throwable =>
            Assertions.fail(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
        }),
      )

  }

}
