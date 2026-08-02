package effectie.resource

import cats.effect._
import effectie.instances.ce2.fx.ioFx
import effectie.instances.ce2.resource.ioUseResource
import effectie.resource.data.TestErrors.TestException
import effectie.resource.data.{TestResource, TestableResource}
import hedgehog._
import hedgehog.runner._

/** UseResource[IO] via translation to cats-effect 2 Resource: LIFO release order and ExitCase delivery.
  *
  * @author Kevin Lee
  * @since 2026-08-02
  */
object Ce2UseResourceSpec extends Properties {

  override def tests: List[Test] = List(
    example(
      "test UseResource[IO] flatMap chain: LIFO release order and ExitCase through translation",
      testFlatMapChainLifoReleaseAndExitCase,
    ),
    example(
      "test ReleasableResource.fromAutoCloseable[IO, A]: close() is called on success and on failure",
      testFromAutoCloseableClosesTheResource,
    ),
    example(
      "test Ce2UseResource interop with cats.effect.Resource: an AutoCloseable is closed in both directions",
      testCatsEffectResourceInteropClosesTheResource,
    ),
  )

  def testFromAutoCloseableClosesTheResource: Result = {
    val successResource = TestResource()
    val failureResource = TestResource()

    val beforeUse = (successResource.closeStatus ==== TestableResource.CloseStatus.notClosed)
      .log("the resource should not be closed before use")

    val successResult =
      ReleasableResource
        .fromAutoCloseable[IO, TestResource](IO(successResource))
        .use(resource =>
          IO {
            resource.write("written")
            resource.closeStatus
          }
        )
        .unsafeRunSync()

    val failureResult =
      ReleasableResource
        .fromAutoCloseable[IO, TestResource](IO(failureResource))
        .use(_ => IO.raiseError[Unit](TestException(1)))
        .attempt
        .unsafeRunSync()

    Result.all(
      List(
        beforeUse,
        (successResult ==== TestableResource.CloseStatus.notClosed)
          .log("the resource should still be open while the use function runs"),
        (successResource.content ==== Vector("written")).log("content written during use"),
        (successResource.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("close() should be called when the use function succeeds"),
        (failureResult ==== Left(TestException(1))).log("the use error should propagate"),
        (failureResource.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("close() should be called when the use function fails"),
      )
    )
  }

  def testCatsEffectResourceInteropClosesTheResource: Result = {
    val toCatsResource   = TestResource()
    val fromCatsResource = TestResource()

    // a ReleasableResource run by cats-effect's own Resource.use
    val toCatsResult =
      Ce2UseResource
        .toCatsEffectResource(ReleasableResource.fromAutoCloseable[IO, TestResource](IO(toCatsResource)))
        .use(resource =>
          IO {
            resource.write("to-cats")
            resource.closeStatus
          }
        )
        .unsafeRunSync()

    // a cats-effect Resource run by the effectie UseResource interpreter
    val fromCatsResult =
      Ce2UseResource
        .fromCatsEffectResource(Resource.fromAutoCloseable[IO, TestResource](IO(fromCatsResource)))
        .use(resource =>
          IO {
            resource.write("from-cats")
            resource.closeStatus
          }
        )
        .unsafeRunSync()

    Result.all(
      List(
        (toCatsResult ==== TestableResource.CloseStatus.notClosed)
          .log("the resource should still be open while cats-effect Resource.use runs"),
        (toCatsResource.content ==== Vector("to-cats")).log("toCatsEffectResource content"),
        (toCatsResource.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("cats-effect Resource.use should close the underlying AutoCloseable"),
        (fromCatsResult ==== TestableResource.CloseStatus.notClosed)
          .log("the resource should still be open while the interpreter runs the use function"),
        (fromCatsResource.content ==== Vector("from-cats")).log("fromCatsEffectResource content"),
        (fromCatsResource.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("the UseResource interpreter should close a cats-effect Resource's AutoCloseable"),
      )
    )
  }

  private def exitCaseName(exitCase: ReleasableResource.ExitCase): String =
    exitCase match {
      case ReleasableResource.ExitCase.Completed => "Completed"
      case ReleasableResource.ExitCase.Errored(err) => s"Errored(${err.toString})"
      case ReleasableResource.ExitCase.Canceled => "Canceled"
    }

  def testFlatMapChainLifoReleaseAndExitCase: Result = {
    var eventLog = Vector.empty[String] // scalafix:ok DisableSyntax.var

    def resource(name: String): ReleasableResource[IO, String] =
      ReleasableResource.makeCase(IO {
        eventLog = eventLog :+ s"acquire:$name"
        name
      }) { (n, exitCase) =>
        IO {
          eventLog = eventLog :+ s"release:$n:${exitCaseName(exitCase)}"
          ()
        }
      }

    val combined =
      for {
        a <- resource("a")
        b <- resource("b")
        c <- resource("c")
      } yield s"$a$b$c"

    val successResult = combined.use(all => IO.pure(all)).unsafeRunSync()
    val successLog    = eventLog

    eventLog = Vector.empty[String]

    val erroredCombined =
      for {
        a <- resource("a")
        b <- resource("b")
      } yield s"$a$b"

    val erroredResult =
      erroredCombined.use(_ => IO.raiseError[String](TestException(9))).attempt.unsafeRunSync()

    Result.all(
      List(
        (successResult ==== "abc").log("use result"),
        (successLog ==== Vector(
          "acquire:a",
          "acquire:b",
          "acquire:c",
          "release:c:Completed",
          "release:b:Completed",
          "release:a:Completed",
        )).log("acquisition and LIFO release order through translation"),
        (erroredResult ==== Left(TestException(9))).log("errored use result"),
        (eventLog ==== Vector(
          "acquire:a",
          "acquire:b",
          s"release:b:Errored(${TestException(9).toString})",
          s"release:a:Errored(${TestException(9).toString})",
        )).log("LIFO release with ExitCase.Errored through translation"),
      )
    )
  }

}
