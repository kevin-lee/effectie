package effectie.resource

import cats._
import cats.syntax.all._
import effectie.core.{CanCatch, FxCtor}
import effectie.resource.data.TestableResource
import hedgehog._

/** @author Kevin Lee
  * @since 2022-11-06
  */
object ResourceMakerSpec {
  def testForAutoCloseable[F[*]]: TestForAutoCloseable1[F] = new TestForAutoCloseable1[F]

  final class TestForAutoCloseable1[F[*]](private val dummy: Boolean = true) extends AnyVal {
    def apply[A <: TestableResource with AutoCloseable](testResourceConstructor: () => A): TestForAutoCloseable2[F, A] =
      new TestForAutoCloseable2(testResourceConstructor)
  }

  final class TestForAutoCloseable2[F[*], A <: TestableResource with AutoCloseable](
    private val testResourceConstructor: () => A
  ) extends AnyVal {
    def apply(
      content: Vector[String],
      useF: A => F[Unit],
      errorTest: Option[Throwable => Result],
    )(implicit FC: FxCtor[F], CC: CanCatch[F], M: Monad[F], RM: ResourceMaker[F]): F[Result] =
      testForAutoCloseable0(
        testResourceConstructor,
        content,
        useF,
        errorTest,
      )
  }

  private def testForAutoCloseable0[
    F[*]: FxCtor: CanCatch: Monad: ResourceMaker,
    A <: TestableResource with AutoCloseable,
  ](
    testResourceConstructor: () => A,
    content: Vector[String],
    useF: A => F[Unit],
    errorTest: Option[Throwable => Result],
  ): F[Result] = {

    val testResource      = testResourceConstructor()
    var closeStatusBefore = none[TestableResource.CloseStatus] // scalafix:ok DisableSyntax.var
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

  def testForMake[F[*]]: TestForMake1[F] =
    new TestForMake1[F]

  final class TestForMake1[F[*]](private val dummy: Boolean = true) extends AnyVal {
    def apply[A <: TestableResource](testResourceConstructor: () => A): TestForMake2[F, A] =
      new TestForMake2(testResourceConstructor)
  }

  final class TestForMake2[F[*], A <: TestableResource](private val testResourceConstructor: () => A) extends AnyVal {
    def apply(
      release: A => Unit,
      content: Vector[String],
      useF: A => F[Result],
      errorTest: Option[Throwable => Result],
    )(implicit FF: FxCtor[F], CC: CanCatch[F], M: Monad[F], RM: ResourceMaker[F]) =
      testForMake0(
        testResourceConstructor,
        release,
        content,
        useF,
        errorTest,
      )
  }

  private def testForMake0[F[*]: FxCtor: CanCatch: Monad: ResourceMaker, A <: TestableResource](
    testResourceConstructor: () => A,
    release: A => Unit,
    content: Vector[String],
    useF: A => F[Result],
    errorTest: Option[Throwable => Result],
  ): F[Result] = {

    val testResource      = testResourceConstructor()
    var closeStatusBefore = none[TestableResource.CloseStatus] // scalafix:ok DisableSyntax.var
    var contentBefore     = none[Vector[String]] // scalafix:ok DisableSyntax.var

    CanCatch[F]
      .catchNonFatal(
        ResourceMaker[F]
          .make(FxCtor[F].effectOf(testResource))(a => FxCtor[F].effectOf(release(testResource)))
          .use { resource =>
            for {
              _      <- FxCtor[F].effectOf {
                          closeStatusBefore = resource.closeStatus.some
                          ()
                        }
              _      <- FxCtor[F].effectOf {
                          contentBefore = resource.content.some
                          ()
                        }
              _      <- FxCtor[F].effectOf(content.foreach(resource.write))
              result <- useF(resource)
            } yield result
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

        case Right(result) =>
          Result.all(
            List(
              (closeStatusBefore ==== TestableResource.CloseStatus.notClosed.some)
                .log("Failed: closeStatusBefore ==== TestableResource.CloseStatus.notClosed.some"),
              (contentBefore ==== Vector.empty.some).log("Failed: contentBefore ==== Vector.empty.some"),
              (testResource.closeStatus ==== TestableResource.CloseStatus.closed)
                .log("Failed: testResource.closeStatus ==== TestableResource.CloseStatus.closed"),
              (testResource.content ==== content).log("Failed: testResource.content ==== content"),
              result.log("result"),
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
