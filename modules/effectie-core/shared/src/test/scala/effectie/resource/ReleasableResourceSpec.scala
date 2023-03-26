package effectie.resource

import cats._
import cats.syntax.all._
import effectie.core.{CanCatch, FxCtor}
import effectie.resource.data.TestableResource
import hedgehog._

/** @author Kevin Lee
  * @since 2022-11-06
  */
object ReleasableResourceSpec {

  def testReleasableResourceUse[F[*]]: TestReleasableResourceUse1[F] = new TestReleasableResourceUse1[F]

  def testReleasableResourceUseByName[F[*]]: TestReleasableResourceUseByName1[F] =
    new TestReleasableResourceUseByName1[F]

  class TestReleasableResourceUse1[F[*]](private val dummy: Boolean = true) extends AnyVal {
    def apply[A <: TestableResource](
      testResourceConstructor: () => A
    ): TestReleasableResourceUse2[F, A] =
      new TestReleasableResourceUse2[F, A](testResourceConstructor)
  }

  class TestReleasableResourceUse2[F[*], A <: TestableResource](private val testResourceConstructor: () => A)
      extends AnyVal {
    def apply(
      content: Vector[String],
      useF: A => F[Unit],
      errorTest: Option[Throwable => Result],
      releasableResourceConstructor: F[A] => ReleasableResource[F, A],
    )(implicit FC: FxCtor[F], CC: CanCatch[F], M: Monad[F]): F[Result] =
      testReleasableResourceUse0[F, A](
        testResourceConstructor,
        content,
        useF,
        errorTest,
        () => releasableResourceConstructor,
      )
  }

  class TestReleasableResourceUseByName1[F[*]](private val dummy: Boolean = true) extends AnyVal {
    def apply[A <: TestableResource](
      testResourceConstructor: () => A
    ): TestReleasableResourceUseByName2[F, A] =
      new TestReleasableResourceUseByName2[F, A](testResourceConstructor)
  }

  class TestReleasableResourceUseByName2[F[*], A <: TestableResource](private val testResourceConstructor: () => A)
      extends AnyVal {
    def apply(
      content: Vector[String],
      useF: A => F[Unit],
      errorTest: Option[Throwable => Result],
      releasableResourceConstructor: (=> F[A]) => ReleasableResource[F, A],
    )(implicit FC: FxCtor[F], CC: CanCatch[F], M: Monad[F]): F[Result] =
      testReleasableResourceUse0[F, A](
        testResourceConstructor,
        content,
        useF,
        errorTest,
        () => fa => releasableResourceConstructor(fa),
      )
  }

  private def testReleasableResourceUse0[F[*]: FxCtor: CanCatch: Monad, A <: TestableResource](
    testResourceConstructor: () => A,
    content: Vector[String],
    useF: A => F[Unit],
    errorTest: Option[Throwable => Result],
    releasableResourceConstructor: () => F[A] => ReleasableResource[F, A],
  ): F[Result] = {

    val testResource      = testResourceConstructor()
    var closeStatusBefore = none[TestableResource.CloseStatus] // scalafix:ok DisableSyntax.var
    var contentBefore     = none[Vector[String]] // scalafix:ok DisableSyntax.var

    CanCatch[F]
      .catchNonFatal(
        releasableResourceConstructor()(FxCtor[F].effectOf(testResource))
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
              (closeStatusBefore ==== TestableResource.CloseStatus.notClosed.some)
                .log("Before: TestableResource.closeStatus should be NotClosed but it is not."),
              (contentBefore ==== Vector.empty.some)
                .log("Before: TestableResource.content should be empty but it is not."),
              (testResource.closeStatus ==== TestableResource.CloseStatus.closed)
                .log("After: TestableResource.closeStatus should Closed but it is not."),
              (testResource.content ==== content)
                .log("After: TestableResource.content should have the expected content but it is empty."),
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
              closeStatusBefore ==== TestableResource.CloseStatus.notClosed.some,
              contentBefore ==== Vector.empty.some,
              testResource.closeStatus ==== TestableResource.CloseStatus.closed,
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
