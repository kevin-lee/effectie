package effectie.resource

import cats._
import cats.syntax.all._
import effectie.core.{CanCatch, FxCtor}
import effectie.resource.data.TestableResource
import munit.Assertions

/** @author Kevin Lee
  * @since 2022-11-06
  */
object ReleasableResourceForMUnit {

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
      errorTest: Option[Throwable => Unit],
      releasableResourceConstructor: F[A] => ReleasableResource[F, A],
    )(implicit FC: FxCtor[F], CC: CanCatch[F], M: Monad[F]): F[Unit] =
      testReleasableResourceUse0[F, A](
        testResourceConstructor,
        content,
        useF,
        errorTest,
        () => releasableResourceConstructor,
      )

    def withPure(
      content: Vector[String],
      useF: A => F[Unit],
      errorTest: Option[Throwable => Unit],
      releasableResourceConstructor: A => ReleasableResource[F, A],
    )(implicit FC: FxCtor[F], CC: CanCatch[F], M: Monad[F]): F[Unit] =
      testReleasableResourceUseWithPure0[F, A](
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
      errorTest: Option[Throwable => Unit],
      releasableResourceConstructor: (=> F[A]) => ReleasableResource[F, A],
    )(implicit FC: FxCtor[F], CC: CanCatch[F], M: Monad[F]): F[Unit] =
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
    errorTest: Option[Throwable => Unit],
    releasableResourceConstructor: () => F[A] => ReleasableResource[F, A],
  ): F[Unit] = _testReleasableResourceUse(
    testResourceConstructor,
    content,
    useF,
    errorTest,
    () => (a: A) => releasableResourceConstructor()(FxCtor[F].effectOf(a)),
    ShouldTestClose.yes,
  )

  private def testReleasableResourceUseWithPure0[F[*]: FxCtor: CanCatch: Monad, A <: TestableResource](
    testResourceConstructor: () => A,
    content: Vector[String],
    useF: A => F[Unit],
    errorTest: Option[Throwable => Unit],
    releasableResourceConstructor: () => A => ReleasableResource[F, A],
  ): F[Unit] =
    _testReleasableResourceUse(
      testResourceConstructor,
      content,
      useF,
      errorTest,
      () => releasableResourceConstructor(),
      ShouldTestClose.no,
    )

  private def _testReleasableResourceUse[F[*]: FxCtor: CanCatch: Monad, A <: TestableResource](
    testResourceConstructor: () => A,
    content: Vector[String],
    useF: A => F[Unit],
    errorTest: Option[Throwable => Unit],
    releasableResourceConstructor: () => A => ReleasableResource[F, A],
    shouldTestClose: ShouldTestClose,
  ): F[Unit] = {

    val testResource      = testResourceConstructor()
    var closeStatusBefore = none[TestableResource.CloseStatus] // scalafix:ok DisableSyntax.var
    var contentBefore     = none[Vector[String]] // scalafix:ok DisableSyntax.var

    CanCatch[F]
      .catchNonFatal(
        releasableResourceConstructor()(testResource)
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
          shouldTestClose match {
            case ShouldTestClose.Yes =>
//              Result.all(
//                List(
              Assertions.assertEquals(
                closeStatusBefore,
                TestableResource.CloseStatus.notClosed.some,
                "Before: TestableResource.closeStatus should be NotClosed but it is not.",
              )
              Assertions.assertEquals(
                contentBefore,
                Vector.empty.some,
                "Before: TestableResource.content should be empty but it is not.",
              )
              Assertions.assertEquals(
                testResource.closeStatus,
                TestableResource.CloseStatus.closed,
                "After: TestableResource.closeStatus should Closed but it is not.",
              )
              Assertions.assertEquals(
                testResource.content,
                content,
                "After: TestableResource.content should have the expected content but it is empty.",
              )
              errorTest.fold(
                Assertions.fail(s"Error was expected but no expected error was given. Error: ${err.toString}")
              )(_(err))
//                )
//              )
            case ShouldTestClose.No =>
//              Result.all(
//                List(
              Assertions.assertEquals(
                contentBefore,
                Vector.empty.some,
                "Before: TestableResource.content should be empty but it is not.",
              )
              Assertions.assertEquals(
                testResource.content,
                content,
                "After: TestableResource.content should have the expected content but it is empty.",
              )
              errorTest.fold(
                Assertions.fail(s"Error was expected but no expected error was given. Error: ${err.toString}")
              )(_(err))
//                )
//              )
          }
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
          shouldTestClose match {
            case ShouldTestClose.Yes =>
//              Result.all(
//                List(
              Assertions.assertEquals(closeStatusBefore, TestableResource.CloseStatus.notClosed.some)
              Assertions.assertEquals(contentBefore, Vector.empty.some)
              Assertions.assertEquals(testResource.closeStatus, TestableResource.CloseStatus.closed)
              Assertions.assertEquals(testResource.content, content)
              errorTest.fold(())(err =>
                Assertions.fail(s"No error was expected but error found. Error: ${err.toString}")
              )
//                )
//              )
            case ShouldTestClose.No =>
//              Result.all(
//                List(
              Assertions.assertEquals(contentBefore, Vector.empty.some)
              Assertions.assertEquals(testResource.content, content)
              errorTest.fold(())(err =>
                Assertions.fail(s"No error was expected but error found. Error: ${err.toString}")
              )
//                )
//              )

          }
        case Left(failedResult) =>
          failedResult
      }
  }

  sealed trait ShouldTestClose
  object ShouldTestClose {
    case object Yes extends ShouldTestClose
    case object No extends ShouldTestClose

    def yes: ShouldTestClose = Yes
    def no: ShouldTestClose  = No
  }

}
