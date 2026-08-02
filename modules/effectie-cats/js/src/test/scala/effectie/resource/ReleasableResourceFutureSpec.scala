package effectie.resource

import effectie.resource.data.TestErrors.TestException
import effectie.resource.data.{TestResource, TestResourceNoAutoClose, TestableResource}
import effectie.testing.FutureTools
import munit.Assertions

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

/** Semantics of the ReleasableResource ADT interpreted with the automatic UseResource[Future] instance.
  *
  * @author Kevin Lee
  * @since 2026-08-02
  */
class ReleasableResourceFutureSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  override val munitTimeout: FiniteDuration = 200.milliseconds

  test("test ReleasableResource[Future] laziness: construction does not acquire, each use re-acquires") {
    var acquireCount = 0 // scalafix:ok DisableSyntax.var
    var releaseCount = 0 // scalafix:ok DisableSyntax.var

    val resource =
      ReleasableResource.make[Future, Int](Future {
        acquireCount += 1
        acquireCount
      })(_ =>
        Future {
          releaseCount += 1
          ()
        }
      )

    Assertions.assertEquals(acquireCount, 0, "acquired before use")
    Assertions.assertEquals(releaseCount, 0, "released before use")

    for {
      firstResult  <- resource.use(n => Future.successful(n))
      secondResult <- resource.use(n => Future.successful(n))
    } yield {
      Assertions.assertEquals(firstResult, 1, "first use result")
      Assertions.assertEquals(secondResult, 2, "second use result (fresh acquisition)")
      Assertions.assertEquals(acquireCount, 2, "acquireCount after both uses")
      Assertions.assertEquals(releaseCount, 2, "releaseCount after both uses")
    }
  }

  test("test ReleasableResource[Future]: synchronous throw in use still releases the resource") {
    val testResource = TestResourceNoAutoClose()

    val resource =
      ReleasableResource.make[Future, TestResourceNoAutoClose](Future(testResource))(a => Future(a.release()))

    resource
      .use[Unit](_ => throw TestException(123)) // scalafix:ok DisableSyntax.throw
      .map(_ => Assertions.fail("An error was expected but the use succeeded"))
      .recover {
        case TestException(123) => ()
        case ex: Throwable =>
          Assertions.fail(s"TestException was expected but got ${ex.toString}")
      }
      .map { _ =>
        Assertions.assertEquals(
          testResource.closeStatus,
          TestableResource.CloseStatus.closed,
          "the resource should be released even when the use function throws synchronously",
        )
      }
  }

  test("test ReleasableResource[Future] release error: propagated when use succeeded") {
    val resource =
      ReleasableResource.make[Future, Int](Future.successful(1))(_ => Future.failed[Unit](TestException(1)))

    resource
      .use(n => Future.successful(n))
      .map(n => Assertions.fail(s"TestException(1) was expected but got Success(${n.toString})"))
      .recover {
        case TestException(1) => ()
        case ex: Throwable =>
          Assertions.fail(s"TestException(1) was expected but got ${ex.toString}")
      }
  }

  private def exitCaseName(exitCase: ReleasableResource.ExitCase): String =
    exitCase match {
      case ReleasableResource.ExitCase.Completed => "Completed"
      case ReleasableResource.ExitCase.Errored(err) => s"Errored(${err.toString})"
      case ReleasableResource.ExitCase.Canceled => "Canceled"
    }

  test("test ReleasableResource.fromAutoCloseable[Future, A]: close() is called on success and on failure") {
    import effectie.instances.future.fxCtor._

    val successResource = TestResource()
    val failureResource = TestResource()
    val throwResource   = TestResource()

    Assertions.assertEquals(
      successResource.closeStatus,
      TestableResource.CloseStatus.notClosed,
      "the resource should not be closed before use",
    )

    for {
      _ <- ReleasableResource
             .fromAutoCloseable[Future, TestResource](Future(successResource))
             .use(resource => Future(resource.write("written")))
      _ <- ReleasableResource
             .fromAutoCloseable[Future, TestResource](Future(failureResource))
             .use(_ => Future.failed[Unit](TestException(1)))
             .map(_ => Assertions.fail("An error was expected but the use succeeded"))
             .recover {
               case TestException(1) => ()
               case ex: Throwable => Assertions.fail(s"TestException(1) was expected but got ${ex.toString}")
             }
      _ <- ReleasableResource
             .fromAutoCloseable[Future, TestResource](Future(throwResource))
             .use[Unit](_ => throw TestException(2)) // scalafix:ok DisableSyntax.throw
             .map(_ => Assertions.fail("An error was expected but the use succeeded"))
             .recover {
               case TestException(2) => ()
               case ex: Throwable => Assertions.fail(s"TestException(2) was expected but got ${ex.toString}")
             }
    } yield {
      Assertions.assertEquals(successResource.content, Vector("written"), "content written during use")
      Assertions.assertEquals(
        successResource.closeStatus,
        TestableResource.CloseStatus.closed,
        "close() should be called when the use function succeeds",
      )
      Assertions.assertEquals(
        failureResource.closeStatus,
        TestableResource.CloseStatus.closed,
        "close() should be called when the use function returns a failed Future",
      )
      Assertions.assertEquals(
        throwResource.closeStatus,
        TestableResource.CloseStatus.closed,
        "close() should be called when the use function throws synchronously",
      )
    }
  }

  test("test ReleasableResource[Future] ExitCase: Completed on success, Errored on use failure") {
    var exitCases = Vector.empty[String] // scalafix:ok DisableSyntax.var

    def resource: ReleasableResource[Future, Int] =
      ReleasableResource.makeCase(Future(1)) { (_, exitCase) =>
        Future {
          exitCases = exitCases :+ exitCaseName(exitCase)
          ()
        }
      }

    for {
      firstResult  <- resource.use(n => Future.successful(n))
      secondResult <- resource.use(_ => Future.failed[Int](TestException(9))).recover { case TestException(9) => -1 }
    } yield {
      Assertions.assertEquals(firstResult, 1, "first use result")
      Assertions.assertEquals(secondResult, -1, "second use result")
      Assertions.assertEquals(
        exitCases,
        Vector("Completed", s"Errored(${TestException(9).toString})"),
        "exit cases seen by release",
      )
    }
  }

  test("test ReleasableResource[Future] flatMap chain: LIFO release with ExitCase.Completed") {
    var eventLog = Vector.empty[String] // scalafix:ok DisableSyntax.var

    def resource(name: String): ReleasableResource[Future, String] =
      ReleasableResource.makeCase(Future {
        eventLog = eventLog :+ s"acquire:$name"
        name
      }) { (n, exitCase) =>
        Future {
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

    combined.use(all => Future.successful(all)).map { result =>
      Assertions.assertEquals(result, "abc", "use result")
      Assertions.assertEquals(
        eventLog,
        Vector(
          "acquire:a",
          "acquire:b",
          "acquire:c",
          "release:c:Completed",
          "release:b:Completed",
          "release:a:Completed",
        ),
        "acquisition and LIFO release order",
      )
    }
  }

}
