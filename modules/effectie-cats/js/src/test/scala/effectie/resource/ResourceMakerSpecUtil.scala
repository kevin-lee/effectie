package effectie.resource

import cats.syntax.all._
import effectie.core.{CanCatch, FxCtor}
import effectie.resource.data.TestableResource
import effectie.testing.FutureTools
import munit.Assertions

import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2022-11-06
  */
object ResourceMakerSpecUtil extends FutureTools {

  def testForAutoCloseable: TestForAutoCloseable1 = new TestForAutoCloseable1

  final class TestForAutoCloseable1(private val dummy: Boolean = true) extends AnyVal {
    def apply[A <: TestableResource with AutoCloseable](testResourceConstructor: () => A): TestForAutoCloseable2[A] =
      new TestForAutoCloseable2(testResourceConstructor)
  }

  final class TestForAutoCloseable2[A <: TestableResource with AutoCloseable](
    private val testResourceConstructor: () => A
  ) extends AnyVal {
    def apply(
      content: Vector[String],
      useF: A => Future[Unit],
      errorTest: Option[Throwable => Unit],
    )(
      implicit FC: FxCtor[Future],
      CC: CanCatch[Future],
      RM: ResourceMaker[Future],
      EC: ExecutionContext,
    ): Future[Unit] =
      testForAutoCloseable0(
        testResourceConstructor,
        content,
        useF,
        errorTest,
      )
  }

  private def testForAutoCloseable0[
    A <: TestableResource with AutoCloseable
  ](
    testResourceConstructor: () => A,
    content: Vector[String],
    useF: A => Future[Unit],
    errorTest: Option[Throwable => Unit],
  )(
    implicit FC: FxCtor[Future],
    CC: CanCatch[Future],
    RM: ResourceMaker[Future],
    EC: ExecutionContext,
  ): Future[Unit] = {

    val testResource      = testResourceConstructor()
    var closeStatusBefore = none[TestableResource.CloseStatus] // scalafix:ok DisableSyntax.var
    var contentBefore     = none[Vector[String]] // scalafix:ok DisableSyntax.var

    CanCatch[Future]
      .catchNonFatal(
        ResourceMaker[Future]
          .forAutoCloseable(FxCtor[Future].effectOf(testResource))
          .use { resource =>
            for {
              _ <- FxCtor[Future].effectOf {
                     closeStatusBefore = resource.closeStatus.some
                     ()
                   }
              _ <- FxCtor[Future].effectOf {
                     contentBefore = resource.content.some
                     ()
                   }
              _ <- FxCtor[Future].effectOf(content.foreach(resource.write))
              _ <- useF(resource)
            } yield ()
          }
      ) {
        case err =>
//          Result.all(
//            List(
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
//            )
//          )
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
//          Result.all(
//            List(
          Assertions.assertEquals(closeStatusBefore, TestableResource.CloseStatus.notClosed.some)
          Assertions.assertEquals(contentBefore, Vector.empty.some)
          Assertions.assertEquals(testResource.closeStatus, TestableResource.CloseStatus.closed)
          Assertions.assertEquals(testResource.content, content)
          errorTest.fold(())(err => Assertions.fail(s"No error was expected but error found. Error: ${err.toString}"))
//            )
//          )
        case Left(failedResult) =>
          failedResult
      }

  }

  def testForMake: TestForMake1 =
    new TestForMake1

  final class TestForMake1(private val dummy: Boolean = true) extends AnyVal {
    def apply[A <: TestableResource](testResourceConstructor: () => A): TestForMake2[A] =
      new TestForMake2(testResourceConstructor)
  }

  final class TestForMake2[A <: TestableResource](private val testResourceConstructor: () => A) extends AnyVal {
    def apply(
      release: A => Unit,
      content: Vector[String],
      useF: A => Future[Unit],
      errorTest: Option[Throwable => Unit],
    )(
      implicit FF: FxCtor[Future],
      CC: CanCatch[Future],
      RM: ResourceMaker[Future],
      EC: ExecutionContext,
    ): Future[Unit] =
      testForMake0(
        testResourceConstructor,
        release,
        content,
        useF,
        errorTest,
      )
  }

  private def testForMake0[A <: TestableResource](
    testResourceConstructor: () => A,
    release: A => Unit,
    content: Vector[String],
    useF: A => Future[Unit],
    errorTest: Option[Throwable => Unit],
  )(
    implicit FC: FxCtor[Future],
    CC: CanCatch[Future],
    RM: ResourceMaker[Future],
    EC: ExecutionContext,
  ): Future[Unit] = {

    val testResource      = testResourceConstructor()
    var closeStatusBefore = none[TestableResource.CloseStatus] // scalafix:ok DisableSyntax.var
    var contentBefore     = none[Vector[String]] // scalafix:ok DisableSyntax.var

    CanCatch[Future]
      .catchNonFatal(
        ResourceMaker[Future]
          .make(FxCtor[Future].effectOf(testResource))(a => FxCtor[Future].effectOf(release(a)))
          .use { resource =>
            for {
              _      <- FxCtor[Future].effectOf {
                          closeStatusBefore = resource.closeStatus.some
                          ()
                        }
              _      <- FxCtor[Future].effectOf {
                          contentBefore = resource.content.some
                          ()
                        }
              _      <- FxCtor[Future].effectOf(content.foreach(resource.write))
              result <- useF(resource)
            } yield result
          }
      ) {
        case err =>
//          Result.all(
//            List(
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
//            )
//          )
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

        case Right(()) =>
//          Result.all(
//            List(
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
//            )
//          )
        case Left(failedResult) =>
          failedResult
      }

  }

  def testFor[F[*]]: TestFor1[Future] =
    new TestFor1[Future]

  final class TestFor1[F[*]](private val dummy: Boolean = true) extends AnyVal {
    def apply[A <: TestableResource](testResourceConstructor: () => A): TestFor2[F, A] =
      new TestFor2(testResourceConstructor)
  }

  final class TestFor2[F[*], A <: TestableResource](private val testResourceConstructor: () => A) extends AnyVal {
    def apply(
      maker: A => ReleasableResource[Future, A],
      content: Vector[String],
      useF: A => Future[Unit],
      closeStatusTest: TestableResource.CloseStatus => Unit,
      errorTest: Option[Throwable => Unit],
    )(
      implicit FF: FxCtor[Future],
      CC: CanCatch[Future],
      EC: ExecutionContext,
    ) =
      testFor0(
        testResourceConstructor,
        maker,
        content,
        useF,
        closeStatusTest,
        errorTest,
      )
  }

  private def testFor0[A <: TestableResource](
    testResourceConstructor: () => A,
    maker: A => ReleasableResource[Future, A],
    content: Vector[String],
    useF: A => Future[Unit],
    closeStatusTest: TestableResource.CloseStatus => Unit,
    errorTest: Option[Throwable => Unit],
  )(
    implicit FF: FxCtor[Future],
    CC: CanCatch[Future],
    EC: ExecutionContext,
  ): Future[Unit] = {

    val testResource      = testResourceConstructor()
    var closeStatusBefore = none[TestableResource.CloseStatus] // scalafix:ok DisableSyntax.var
    var contentBefore     = none[Vector[String]] // scalafix:ok DisableSyntax.var

    CanCatch[Future]
      .catchNonFatal(
        maker(testResource)
          .use { resource =>
            for {
              _      <- FxCtor[Future].effectOf {
                          closeStatusBefore = resource.closeStatus.some
                          ()
                        }
              _      <- FxCtor[Future].effectOf {
                          contentBefore = resource.content.some
                          ()
                        }
              _      <- FxCtor[Future].effectOf(content.foreach(resource.write))
              result <- useF(resource)
            } yield result
          }
      ) {
        case err =>
//          Result.all(
//            List(
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
//            )
//          )
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

        case Right(()) =>
//          Result.all(
//            List(
          Assertions.assertEquals(
            closeStatusBefore,
            TestableResource.CloseStatus.notClosed.some,
            "Failed: closeStatusBefore ==== TestableResource.CloseStatus.notClosed.some",
          )
          Assertions.assertEquals(contentBefore, Vector.empty.some, "Failed: contentBefore ==== Vector.empty.some")
          closeStatusTest(testResource.closeStatus)
          Assertions.assertEquals(testResource.content, content, "Failed: testResource.content ==== content")
          errorTest.fold(())(err => Assertions.fail(s"No error was expected but error found. Error: ${err.toString}"))
//            )
//          )
        case Left(failedResult) =>
          failedResult
      }

  }

}
