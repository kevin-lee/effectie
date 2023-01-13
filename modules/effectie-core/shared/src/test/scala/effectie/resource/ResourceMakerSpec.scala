package effectie.resource

import cats._
import cats.syntax.all._
import effectie.core.{CanCatch, FxCtor}
import effectie.resource.data.TestResource
import hedgehog._

/** @author Kevin Lee
  * @since 2022-11-06
  */
object ResourceMakerSpec {
  def testForAutoCloseable[F[*]: FxCtor: CanCatch: Monad: ResourceMaker](
    content: Vector[String],
    useF: TestResource => F[Unit],
    errorTest: Option[Throwable => Result],
  ): F[Result] =
    testForAutoCloseable0[F](content, useF, errorTest)

  def testForAutoCloseable0[F[*]: FxCtor: CanCatch: Monad: ResourceMaker](
    content: Vector[String],
    useF: TestResource => F[Unit],
    errorTest: Option[Throwable => Result],
  ): F[Result] = {

    val testResource      = TestResource()
    var closeStatusBefore = none[TestResource.CloseStatus] // scalafix:ok DisableSyntax.var
    var contentBefore     = none[Vector[String]] // scalafix:ok DisableSyntax.var

    CanCatch[F]
      .catchNonFatal(
        ResourceMaker[F]
          .forAutoCloseable(FxCtor[F].effectOf(testResource))
          .use { resource =>
            for {
              _ <- FxCtor[F].effectOf {
                     closeStatusBefore = resource.closeStatus.some
                     ()
                   }
              _ <- FxCtor[F].effectOf {
                     contentBefore = resource.content.some
                     ()
                   }
              _ <- FxCtor[F].effectOf(content.foreach(resource.write))
              _ <- useF(resource)
            } yield ()
          }
      ) {
        case err =>
          Result.all(
            List(
              (closeStatusBefore ==== TestResource.CloseStatus.notClosed.some)
                .log("Before: TestResource.closeStatus should be NotClosed but it is not."),
              (contentBefore ==== Vector.empty.some)
                .log("Before: TestResource.content should be empty but it is not."),
              (testResource.closeStatus ==== TestResource.CloseStatus.closed)
                .log("After: TestResource.closeStatus should Closed but it is not."),
              (testResource.content ==== content)
                .log("After: TestResource.content should have the expected content but it is empty."),
              errorTest.fold(
                Result.failure.log(s"Error was expected but no expected error was given. Error: ${err.toString}")
              )(_(err)),
            )
          )
      }
      .map {
        //          println(
        //            s"""     closeStatusBefore: $closeStatusBefore
        //               |         contentBefore: $contentBefore
        //               |closeStatusBeforeAfter: ${resource.closeStatus}
        //               |          contentAfter: ${resource.content}
        //               |---
        //               |""".stripMargin
        //          )

        case Right(_) =>
          Result.all(
            List(
              closeStatusBefore ==== TestResource.CloseStatus.notClosed.some,
              contentBefore ==== Vector.empty.some,
              testResource.closeStatus ==== TestResource.CloseStatus.closed,
              testResource.content ==== content,
              errorTest.fold(Result.success)(err =>
                Result.failure.log(s"No error was expected but error found. Error: ${err.toString}")
              ),
            )
          )
        case Left(failedResult) =>
          failedResult
      }

  }
}
