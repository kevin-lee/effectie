package effectie.resource

import cats._
import cats.syntax.all._
import effectie.core.{CanCatch, FxCtor}
import effectie.resource.data.TestableResource
import munit.Assertions

/** @author Kevin Lee
  * @since 2022-11-06
  */
object ResourceMakerForMUnit {
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
      errorTest: Option[Throwable => Unit],
    )(implicit FC: FxCtor[F], CC: CanCatch[F], M: Monad[F], RM: ResourceMaker[F]): F[Unit] =
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
    errorTest: Option[Throwable => Unit],
  ): F[Unit] = {

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
          Assertions.assertEquals(closeStatusBefore, TestableResource.CloseStatus.notClosed.some)
          Assertions.assertEquals(contentBefore, Vector.empty.some)
          Assertions.assertEquals(testResource.closeStatus, TestableResource.CloseStatus.closed)
          Assertions.assertEquals(testResource.content, content)
          errorTest.fold(())(err => Assertions.fail(s"No error was expected but error found. Error: ${err.toString}"))
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
      useF: A => F[Unit],
      errorTest: Option[Throwable => Unit],
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
    useF: A => F[Unit],
    errorTest: Option[Throwable => Unit],
  ): F[Unit] = {

    val testResource      = testResourceConstructor()
    var closeStatusBefore = none[TestableResource.CloseStatus] // scalafix:ok DisableSyntax.var
    var contentBefore     = none[Vector[String]] // scalafix:ok DisableSyntax.var

    CanCatch[F]
      .catchNonFatal(
        ResourceMaker[F]
          .make(FxCtor[F].effectOf(testResource))(a => FxCtor[F].effectOf(release(a)))
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
          Assertions.assertEquals(
            closeStatusBefore,
            TestableResource.CloseStatus.notClosed.some,
            "Failed: closeStatusBefore ==== TestableResource.CloseStatus.notClosed.some",
          )
          Assertions.assertEquals(contentBefore, Vector.empty.some, "Failed: contentBefore ==== Vector.empty.some")
          Assertions.assertEquals(
            testResource.closeStatus,
            TestableResource.CloseStatus.closed,
            "Failed: testResource.closeStatus ==== TestableResource.CloseStatus.closed",
          )
          Assertions.assertEquals(testResource.content, content, "Failed: testResource.content ==== content")

          errorTest.fold(())(err => Assertions.fail(s"No error was expected but error found. Error: ${err.toString}"))
        case Left(failedResult) =>
          failedResult
      }

  }

  def testFor[F[*]]: TestFor1[F] =
    new TestFor1[F]

  final class TestFor1[F[*]](private val dummy: Boolean = true) extends AnyVal {
    def apply[A <: TestableResource](testResourceConstructor: () => A): TestFor2[F, A] =
      new TestFor2(testResourceConstructor)
  }

  final class TestFor2[F[*], A <: TestableResource](private val testResourceConstructor: () => A) extends AnyVal {
    def apply(
      maker: A => ReleasableResource[F, A],
      content: Vector[String],
      useF: A => F[Unit],
      closeStatusTest: TestableResource.CloseStatus => Unit,
      errorTest: Option[Throwable => Unit],
    )(implicit FF: FxCtor[F], CC: CanCatch[F], M: Monad[F]) =
      testFor0(
        testResourceConstructor,
        maker,
        content,
        useF,
        closeStatusTest,
        errorTest,
      )
  }

  private def testFor0[F[*]: FxCtor: CanCatch: Monad, A <: TestableResource](
    testResourceConstructor: () => A,
    maker: A => ReleasableResource[F, A],
    content: Vector[String],
    useF: A => F[Unit],
    closeStatusTest: TestableResource.CloseStatus => Unit,
    errorTest: Option[Throwable => Unit],
  ): F[Unit] = {

    val testResource      = testResourceConstructor()
    var closeStatusBefore = none[TestableResource.CloseStatus] // scalafix:ok DisableSyntax.var
    var contentBefore     = none[Vector[String]] // scalafix:ok DisableSyntax.var

    CanCatch[F]
      .catchNonFatal(
        maker(testResource)
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
          closeStatusTest(testResource.closeStatus)
          Assertions.assertEquals(
            testResource.content,
            content,
            "After: TestableResource.content should have the expected content but it is empty.",
          )
          errorTest.fold(
            Assertions.fail(s"Error was expected but no expected error was given. Error: ${err.toString}")
          )(_(err))
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
          Assertions.assertEquals(
            closeStatusBefore,
            TestableResource.CloseStatus.notClosed.some,
            "Failed: closeStatusBefore ==== TestableResource.CloseStatus.notClosed.some",
          )
          Assertions.assertEquals(contentBefore, Vector.empty.some, "Failed: contentBefore ==== Vector.empty.some")
          closeStatusTest(testResource.closeStatus)
          Assertions.assertEquals(testResource.content, content, "Failed: testResource.content ==== content")

          errorTest.fold(())(err => Assertions.fail(s"No error was expected but error found. Error: ${err.toString}"))
        case Left(failedResult) =>
          failedResult
      }

  }

}
