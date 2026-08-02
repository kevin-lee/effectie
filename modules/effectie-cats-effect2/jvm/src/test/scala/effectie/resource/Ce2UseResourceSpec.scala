package effectie.resource

import cats.effect._
import effectie.instances.ce2.resource.ioUseResource
import effectie.resource.data.TestErrors.TestException
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
    )
  )

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
