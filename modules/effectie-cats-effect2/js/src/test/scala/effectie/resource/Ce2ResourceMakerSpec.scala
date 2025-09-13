package effectie.resource

import cats.effect._
import cats.syntax.all._
import effectie.instances.ce2.fx.ioFx
import effectie.resource.data.TestErrors.TestException
import effectie.resource.data.{TestResource, TestResourceNoAutoClose, TestableResource}
import effectie.testing.{FutureTools, RandomGens}
import munit.Assertions

import scala.concurrent._
import scala.concurrent.duration._

/** @author Kevin Lee
  * @since 2022-11-06
  */
class Ce2ResourceMakerSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  override val munitTimeout: FiniteDuration = 200.milliseconds

  type F[A] = IO[A]
  val F: IO.type = IO

  test("test Ce2ResourceMaker.forAutoCloseable") {
    val content = RandomGens.genAlphaStringList(100, 10).toVector

    implicit val resourceMaker: ResourceMaker[F] = Ce2ResourceMaker.maker

    ResourceMakerForMUnit
      .testForAutoCloseable[F](TestResource.apply)(
        content,
        _ => F.pure(()),
        none,
      )
      .unsafeToFuture()
  }

  test("test Ce2ResourceMaker.forAutoCloseable - error case") {
    val content = RandomGens.genAlphaStringList(100, 10).toVector

    implicit val resourceMaker: ResourceMaker[F] = Ce2ResourceMaker.maker

    ResourceMakerForMUnit
      .testForAutoCloseable[F](TestResource.apply)(
        content,
        _ => F.raiseError(TestException(123)),
        Option({
          case TestException(123) => ()
          case ex: Throwable =>
            Assertions.fail(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
        }),
      )
      .unsafeToFuture()
  }

  test("test Ce2ResourceMaker.make") {
    val content = RandomGens.genAlphaStringList(100, 10).toVector

    implicit val resourceMaker: ResourceMaker[F] = Ce2ResourceMaker.maker

    ResourceMakerForMUnit
      .testForMake[F](TestResourceNoAutoClose.apply)(
        _.release(),
        content,
        _ => F.pure(()),
        none,
      )
      .unsafeToFuture()
  }

  test("test Ce2ResourceMaker.make - error case") {
    val content = RandomGens.genAlphaStringList(100, 10).toVector

    implicit val resourceMaker: ResourceMaker[F] = Ce2ResourceMaker.maker

    ResourceMakerForMUnit
      .testForMake[F](TestResourceNoAutoClose.apply)(
        _.release(),
        content,
        _ => F.raiseError(TestException(123)),
        Option({
          case TestException(123) => ()
          case ex: Throwable =>
            Assertions.fail(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
        }),
      )
      .unsafeToFuture()
  }

  test("test Ce2ResourceMaker.make - error case in closing") {
    val content = RandomGens.genAlphaStringList(100, 10).toVector

    implicit val resourceMaker: ResourceMaker[F] = Ce2ResourceMaker.maker

    val expectedClosingExceptionMessage = "Test error in closing resource. It's only for testing so please ignore."
    val expectedException               = new RuntimeException(expectedClosingExceptionMessage)

    ResourceMakerForMUnit
      .testForMake[F](TestResourceNoAutoClose.apply)(
        release = { resource =>
          resource.release()
          throw expectedException // scalafix:ok DisableSyntax.throw
        },
        content = content,
        useF = _ => F.pure(()),
        errorTest = { (ex: Throwable) =>
          Assertions.assertEquals(ex, expectedException)
          Assertions.assertEquals(ex.getMessage, expectedClosingExceptionMessage)
        }.some,
      )
      .unsafeToFuture()
  }

  test("test Ce2ResourceMaker.pure") {
    val content = RandomGens.genAlphaStringList(100, 10).toVector

    implicit val resourceMaker: ResourceMaker[F] = Ce2ResourceMaker.maker

    ResourceMakerForMUnit
      .testFor[F](TestResourceNoAutoClose.apply)(
        resourceMaker.pure,
        content,
        _ => F.pure(()),
        closeStatus => Assertions.assertEquals(closeStatus, TestableResource.CloseStatus.NotClosed),
        none,
      )
      .unsafeToFuture()
  }

  test("test Ce2ResourceMaker.pure - error case") {
    val content = RandomGens.genAlphaStringList(100, 10).toVector

    implicit val resourceMaker: ResourceMaker[F] = Ce2ResourceMaker.maker

    ResourceMakerForMUnit
      .testFor[F](TestResourceNoAutoClose.apply)(
        resourceMaker.pure,
        content,
        _ => F.raiseError(TestException(123)),
        closeStatus => Assertions.assertEquals(closeStatus, TestableResource.CloseStatus.NotClosed),
        Option({
          case TestException(123) => ()
          case ex: Throwable =>
            Assertions.fail(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
        }),
      )
      .unsafeToFuture()
  }

  test("test Ce2ResourceMaker.eval") {
    val content = RandomGens.genAlphaStringList(100, 10).toVector

    implicit val resourceMaker: ResourceMaker[F] = Ce2ResourceMaker.maker

    ResourceMakerForMUnit
      .testFor[F](TestResourceNoAutoClose.apply)(
        testResource => resourceMaker.eval(F.delay(testResource)),
        content,
        _ => F.pure(()),
        closeStatus => Assertions.assertEquals(closeStatus, TestableResource.CloseStatus.NotClosed),
        none,
      )
      .unsafeToFuture()
  }

  test("test Ce2ResourceMaker.eval - error case") {
    val content = RandomGens.genAlphaStringList(100, 10).toVector

    implicit val resourceMaker: ResourceMaker[F] = Ce2ResourceMaker.maker

    ResourceMakerForMUnit
      .testFor[F](TestResourceNoAutoClose.apply)(
        testResource => resourceMaker.eval(F.delay(testResource)),
        content,
        _ => F.raiseError(TestException(123)),
        closeStatus => Assertions.assertEquals(closeStatus, TestableResource.CloseStatus.NotClosed),
        Option({
          case TestException(123) => ()
          case ex: Throwable =>
            Assertions.fail(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
        }),
      )
      .unsafeToFuture()
  }

}
