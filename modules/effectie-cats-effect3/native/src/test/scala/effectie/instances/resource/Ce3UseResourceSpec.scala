package effectie.resource

import cats.MonadError
import cats.effect._
import cats.effect.kernel.Outcome
import cats.syntax.all._
import effectie.instances.ce3.resource.ioUseResource
import effectie.resource.data.TestErrors.TestException
import hedgehog._
import hedgehog.runner._

/** UseResource[IO] via translation to cats-effect 3 Resource: cancellation safety, interop, stack safety.
  *
  * @author Kevin Lee
  * @since 2026-08-02
  */
object Ce3UseResourceSpec extends Properties {

  import cats.effect.unsafe.implicits.global

  override def tests: List[Test] = List(
    example(
      "test UseResource[IO]: finalizer runs with ExitCase.Canceled when the use is canceled",
      testCanceledExitCase,
    ),
    example(
      "test Ce3UseResource.toCatsEffectResource and fromCatsEffectResource round-trip",
      testToAndFromCatsEffectResource,
    ),
    example(
      "test UseResource[IO] stack safety: tailRecM(10,000) through translation",
      testStackSafetyTailRecM,
    ),
    example(
      "test UseResource[IO] stack safety: 1,000 deep flatMap chain",
      testStackSafetyFlatMapChain,
    ),
    example(
      "test UseResource[IO] flatMap chain: LIFO release order and ExitCase through translation",
      testFlatMapChainLifoReleaseAndExitCase,
    ),
  )

  private def exitCaseName(exitCase: ReleasableResource.ExitCase): String =
    exitCase match {
      case ReleasableResource.ExitCase.Completed => "Completed"
      case ReleasableResource.ExitCase.Errored(err) => s"Errored(${err.toString})"
      case ReleasableResource.ExitCase.Canceled => "Canceled"
    }

  def testCanceledExitCase: Result = {
    var exitCases = Vector.empty[ReleasableResource.ExitCase] // scalafix:ok DisableSyntax.var

    val resource =
      ReleasableResource.makeCase(IO.pure(1)) { (_, exitCase) =>
        IO {
          exitCases = exitCases :+ exitCase
          ()
        }
      }

    val outcome =
      resource
        .use(_ => IO.canceled *> IO.pure(0))
        .start
        .flatMap(_.join)
        .unsafeRunSync()

    val outcomeResult = outcome match {
      case Outcome.Canceled() => Result.success
      case Outcome.Succeeded(_) =>
        Result.failure.log("Expected Canceled outcome but got Succeeded")
      case Outcome.Errored(err) =>
        Result.failure.log(s"Expected Canceled outcome but got Errored(${err.toString})")
    }

    Result.all(
      List(
        outcomeResult,
        (exitCases ==== Vector(ReleasableResource.ExitCase.canceled))
          .log("finalizer should run with ExitCase.Canceled"),
      )
    )
  }

  def testToAndFromCatsEffectResource: Result = {
    var eventLog = Vector.empty[String] // scalafix:ok DisableSyntax.var

    val releasable =
      ReleasableResource.make(IO {
        eventLog = eventLog :+ "acquire:releasable"
        1
      })(_ =>
        IO {
          eventLog = eventLog :+ "release:releasable"
          ()
        }
      )

    val toCatsResult = Ce3UseResource.toCatsEffectResource(releasable).use(n => IO.pure(n)).unsafeRunSync()

    val fromCatsResult =
      Ce3UseResource
        .fromCatsEffectResource(
          Resource.make(IO {
            eventLog = eventLog :+ "acquire:cats"
            2
          })(_ =>
            IO {
              eventLog = eventLog :+ "release:cats"
              ()
            }
          )
        )
        .use(n => IO.pure(n))
        .unsafeRunSync()

    Result.all(
      List(
        (toCatsResult ==== 1).log("toCatsEffectResource use result"),
        (fromCatsResult ==== 2).log("fromCatsEffectResource use result"),
        (eventLog ==== Vector("acquire:releasable", "release:releasable", "acquire:cats", "release:cats"))
          .log("acquisition and release through interop"),
      )
    )
  }

  def testStackSafetyTailRecM: Result = {
    val depth = 10000

    val monadError = MonadError[ReleasableResource[IO, *], Throwable]

    val counted =
      monadError.tailRecM(0) { i =>
        if (i < depth) ReleasableResource.pure[IO, Either[Int, Int]]((i + 1).asLeft[Int])
        else ReleasableResource.pure[IO, Either[Int, Int]](i.asRight[Int])
      }

    (counted.use(n => IO.pure(n)).unsafeRunSync() ==== depth).log(s"tailRecM(${depth.toString})")
  }

  def testStackSafetyFlatMapChain: Result = {
    val depth = 1000

    val chained =
      (1 to depth).foldLeft(ReleasableResource.pure[IO, Int](0)) { (resource, _) =>
        resource.flatMap(n => ReleasableResource.pure[IO, Int](n + 1))
      }

    (chained.use(n => IO.pure(n)).unsafeRunSync() ==== depth).log(s"${depth.toString} deep flatMap chain")
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
