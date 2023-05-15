package effectie.resource

import cats.syntax.all._
import effectie.resource.data.TestErrors.TestException
import effectie.resource.data.{TestResource, TestResourceNoAutoClose, TestableResource}
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog._
import hedgehog.runner._

import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.ExecutionContext

/** @author Kevin Lee
  * @since 2022-11-12
  */
object ReleasableFutureResourceSpec extends Properties {

  override def tests: List[Test] = List(
    property(
      "test ReleasableFutureResource[A]",
      testReleasableFutureResource,
    ),
    property(
      "test ReleasableFutureResource[A] - error case",
      testReleasableFutureResourceErrorCase,
    ),
    property(
      "test ReleasableFutureResource[A].make",
      testReleasableFutureResourceMake,
    ),
    property(
      "test ReleasableFutureResource[A].make - error case",
      testReleasableFutureResourceMakeErrorCase,
    ),
    property(
      "test ReleasableFutureResource[A].pure",
      testReleasableFutureResourcePure,
    ),
    property(
      "test ReleasableFutureResource[A].pure - error case",
      testReleasableFutureResourcePureErrorCase,
    ),
    property(
      "test ReleasableFutureResource.map",
      testReleasableFutureResourceMap,
    ),
    property(
      "test ReleasableFutureResource.map error case",
      testReleasableFutureResourceMapErrorCase,
    ),
    property(
      "test ReleasableFutureResource.flatMap",
      testReleasableFutureResourceFlatMap,
    ),
    property(
      "test ReleasableFutureResource.flatMap error case",
      testReleasableFutureResourceFlatMapErrorCase,
    ),
    property(
      "test ReleasableFutureResource.flatMap and ReleasableFutureResource.map",
      testReleasableFutureResourceFlatMapAndMap,
    ),
    property(
      "test ReleasableFutureResource.flatMap and ReleasableFutureResource.map error case",
      testReleasableFutureResourceFlatMapAndMapErrorCase,
    ),
  )

  def testReleasableFutureResource: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {
      import effectie.instances.future.fx._
      import extras.concurrent.testing.ConcurrentSupport

      import scala.concurrent.Future
      import scala.concurrent.duration._

      implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

      val waitFor                                   = WaitFor(200.milliseconds)
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(3)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(
        ReleasableResourceSpec
          .testReleasableResourceUse[Future](TestResource.apply)(
            content,
            _ => Future.successful(()),
            none,
            ReleasableResource.futureResource,
          )
      )

    }

  def testReleasableFutureResourceErrorCase: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {
      import effectie.instances.future.fx._
      import extras.concurrent.testing.ConcurrentSupport

      import scala.concurrent.Future
      import scala.concurrent.duration._

      implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

      val waitFor                                   = WaitFor(200.milliseconds)
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(3)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(
        ReleasableResourceSpec
          .testReleasableResourceUse[Future](TestResource.apply)(
            content,
            _ => Future.failed(TestException(123)),
            Option({
              case TestException(123) => Result.success
              case ex: Throwable =>
                Result
                  .failure
                  .log(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
            }),
            ReleasableResource.futureResource,
          )
      )

    }

  def testReleasableFutureResourceMake: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {
      import effectie.instances.future.fx._
      import extras.concurrent.testing.ConcurrentSupport

      import scala.concurrent.Future
      import scala.concurrent.duration._

      implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

      val waitFor                                   = WaitFor(200.milliseconds)
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(3)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(
        Future
          .sequence(
            List(
              ReleasableResourceSpec
                .testReleasableResourceUse[Future](TestResourceNoAutoClose.apply)(
                  content,
                  _ => Future.successful(()),
                  none,
                  ReleasableFutureResource.make(_)(a => Future(a.release())),
                ),
              ReleasableResourceSpec
                .testReleasableResourceUse[Future](TestResourceNoAutoClose.apply)(
                  content,
                  _ => Future.successful(()),
                  none,
                  ReleasableResource.makeFuture(_)(a => Future(a.release())),
                ),
            )
          )
          .map(Result.all)
      )

    }

  def testReleasableFutureResourceMakeErrorCase: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {
      import effectie.instances.future.fx._
      import extras.concurrent.testing.ConcurrentSupport

      import scala.concurrent.Future
      import scala.concurrent.duration._

      implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

      val waitFor                                   = WaitFor(200.milliseconds)
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(3)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(
        Future
          .sequence(
            List(
              ReleasableResourceSpec
                .testReleasableResourceUse[Future](TestResourceNoAutoClose.apply)(
                  content,
                  _ => Future.failed(TestException(123)),
                  Option({
                    case TestException(123) => Result.success
                    case ex: Throwable =>
                      Result
                        .failure
                        .log(
                          s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}"
                        )
                  }),
                  ReleasableFutureResource.make(_)(a => Future(a.release())),
                ),
              ReleasableResourceSpec
                .testReleasableResourceUse[Future](TestResourceNoAutoClose.apply)(
                  content,
                  _ => Future.failed(TestException(123)),
                  Option({
                    case TestException(123) => Result.success
                    case ex: Throwable =>
                      Result
                        .failure
                        .log(
                          s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}"
                        )
                  }),
                  ReleasableResource.makeFuture(_)(a => Future(a.release())),
                ),
            )
          )
          .map(Result.all)
      )

    }

  def testReleasableFutureResourcePure: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {
      import effectie.instances.future.fx._
      import extras.concurrent.testing.ConcurrentSupport

      import scala.concurrent.Future
      import scala.concurrent.duration._

      implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

      val waitFor                                   = WaitFor(200.milliseconds)
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(3)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(
        Future
          .sequence(
            List(
              ReleasableResourceSpec
                .testReleasableResourceUse[Future](TestResourceNoAutoClose.apply)
                .withPure(
                  content,
                  _ => Future.successful(()),
                  none,
                  ReleasableFutureResource.pure(_),
                ),
              ReleasableResourceSpec
                .testReleasableResourceUse[Future](TestResourceNoAutoClose.apply)
                .withPure(
                  content,
                  _ => Future.successful(()),
                  none,
                  ReleasableResource.pureFuture(_),
                ),
            )
          )
          .map(Result.all)
      )

    }

  def testReleasableFutureResourcePureErrorCase: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {
      import effectie.instances.future.fx._
      import extras.concurrent.testing.ConcurrentSupport

      import scala.concurrent.Future
      import scala.concurrent.duration._

      implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

      val waitFor                                   = WaitFor(200.milliseconds)
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(3)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(
        Future
          .sequence(
            List(
              ReleasableResourceSpec
                .testReleasableResourceUse[Future](TestResourceNoAutoClose.apply)
                .withPure(
                  content,
                  _ => Future.failed(TestException(123)),
                  Option({
                    case TestException(123) => Result.success
                    case ex: Throwable =>
                      Result
                        .failure
                        .log(
                          s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}"
                        )
                  }),
                  ReleasableFutureResource.pure(_),
                ),
              ReleasableResourceSpec
                .testReleasableResourceUse[Future](TestResourceNoAutoClose.apply)
                .withPure(
                  content,
                  _ => Future.failed(TestException(123)),
                  Option({
                    case TestException(123) => Result.success
                    case ex: Throwable =>
                      Result
                        .failure
                        .log(
                          s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}"
                        )
                  }),
                  ReleasableResource.pureFuture(_),
                ),
            )
          )
          .map(Result.all)
      )

    }

  def testReleasableFutureResourceMap: Property =
    for {
      content  <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content")
      content2 <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content2")
      content3 <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content3")
    } yield {

      val expectedContent = content ++ content2 ++ content3
      val testResource    = TestResource.withContent(content)

      import extras.concurrent.testing.ConcurrentSupport

      import scala.concurrent.Future
      import scala.concurrent.duration._

      implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

      val waitFor                                   = WaitFor(200.milliseconds)
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(3)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val resource = ReleasableFutureResource[TestResource](Future(testResource))

      var actualContent = Vector.empty[String] // scalafix:ok DisableSyntax.var

      val before = List(
        (testResource.content ==== content).log("[B] content does not match"),
        (testResource.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[B] closeStatus does not match"),
      )

      ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      ) {
        resource
          .map(_.content ++ content2)
          .map(_ ++ content3)
          .use { content =>
            actualContent = content
            Future.successful(())
          }
      }
      val after = List(
        (actualContent ==== expectedContent).log("[A] content does not match"),
        (testResource.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("[A] closeStatus does not match"),
      )

      Result.all(before ++ after)
    }

  def testReleasableFutureResourceMapErrorCase: Property =
    for {
      content  <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content")
      content2 <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content2")
      content3 <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content3")
    } yield {
      val expectedContent = content ++ content2 ++ content3
      val testResource    = TestResource.withContent(content)

      import extras.concurrent.testing.ConcurrentSupport

      import scala.concurrent.Future
      import scala.concurrent.duration._

      implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

      val waitFor                                   = WaitFor(200.milliseconds)
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(3)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val resource      = ReleasableFutureResource[TestResource](Future(testResource))
      var actualContent = Vector.empty[String] // scalafix:ok DisableSyntax.var

      val before = List(
        (testResource.content ==== content).log("[B] content does not match"),
        (testResource.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[B] closeStatus does not match"),
      )

      val actual =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        ) {
          resource
            .map(_.content ++ content2)
            .map(_ ++ content3)
            .use { content =>
              actualContent = content
              Future.failed[Unit](TestException(123))
            }
            .map(_ => Result.failure.log(s"Error was expected but no expected error was given"))
            .recover {
              case TestException(123) => Result.success
              case ex: Throwable =>
                Result
                  .failure
                  .log(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
            }
        }

      val after = List(
        (actualContent ==== expectedContent).log("[A] content does not match"),
        (testResource.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("[A] closeStatus does not match"),
      )

      Result.all(before ++ List(actual) ++ after)
    }

  def testReleasableFutureResourceFlatMap: Property =
    for {
      content  <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content")
      content2 <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content2")
      content3 <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content3")
    } yield {

      val expectedContent = content ++ content2 ++ content3
      val testResource    = TestResource.withContent(content)
      val testResource2   = TestResource.withContent(content2)
      val testResource3   = TestResource.withContent(content3)

      import extras.concurrent.testing.ConcurrentSupport

      import scala.concurrent.Future
      import scala.concurrent.duration._

      implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

      val waitFor                                   = WaitFor(200.milliseconds)
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(3)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val resource = ReleasableFutureResource(Future(testResource))

      var actualContent = Vector.empty[String] // scalafix:ok DisableSyntax.var

      val before = List(
        (testResource.content ==== content).log("[B] testResource.content does not match"),
        (testResource.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[B] testResource.closeStatus does not match"),
        (testResource2.content ==== content2).log("[B] testResource2.content does not match"),
        (testResource2.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[B] testResource2.closeStatus does not match"),
        (testResource3.content ==== content3).log("[B] testResource3.content does not match"),
        (testResource3.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[B] testResource3.closeStatus does not match"),
      )

      val result =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        ) {
          resource
            .flatMap { _ =>
              ReleasableFutureResource(Future(testResource2))
            }
            .flatMap(_ => ReleasableFutureResource.pure(testResource3))
            .use { newResource =>
              actualContent = content ++ content2 ++ newResource.content
              Future.unit
            }
            .map(_ => Result.success)
            .recover {
              case err =>
                Result.failure.log(s"No error expected but got ${err.toString}")
            }

        }

      val after = List(
        (actualContent ==== expectedContent).log("[A] content does not match"),
        (testResource.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("[A] testResource.closeStatus does not match"),
        (testResource2.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("[A] testResource2.closeStatus does not match"),
        (testResource3.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[A] testResource3.closeStatus does not match"),
      )

      Result.all(before ++ List(result) ++ after)
    }

  def testReleasableFutureResourceFlatMapErrorCase: Property =
    for {
      content  <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content")
      content2 <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content2")
      content3 <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content3")
    } yield {
      val expectedContent = content ++ content2 ++ content3
      val testResource    = TestResource.withContent(content)
      val testResource2   = TestResource.withContent(content2)
      val testResource3   = TestResource.withContent(content3)

      import extras.concurrent.testing.ConcurrentSupport

      import scala.concurrent.Future
      import scala.concurrent.duration._

      implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

      val waitFor                                   = WaitFor(200.milliseconds)
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(3)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val resource = ReleasableFutureResource(Future(testResource))

      var actualContent = Vector.empty[String] // scalafix:ok DisableSyntax.var

      val before = List(
        (testResource.content ==== content).log("[B] testResource.content does not match"),
        (testResource.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[B] testResource.closeStatus does not match"),
        (testResource2.content ==== content2).log("[B] testResource2.content does not match"),
        (testResource2.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[B] testResource2.closeStatus does not match"),
        (testResource3.content ==== content3).log("[B] testResource3.content does not match"),
        (testResource3.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[B] testResource3.closeStatus does not match"),
      )

      val result =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        ) {
          resource
            .flatMap(_ => ReleasableFutureResource(Future(testResource2)))
            .flatMap(_ => ReleasableFutureResource.pure(testResource3))
            .use { newResource =>
              actualContent = content ++ content2 ++ newResource.content
              Future.failed[Unit](TestException(123))
            }
            .map(_ => Result.failure.log(s"Error was expected but no expected error was given"))
            .recover {
              case TestException(123) => Result.success
              case ex: Throwable =>
                Result
                  .failure
                  .log(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
            }
        }

      val after = List(
        (actualContent ==== expectedContent).log("[A] content does not match"),
        (testResource.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("[A] testResource.closeStatus does not match"),
        (testResource2.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("[A] testResource2.closeStatus does not match"),
        (testResource3.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[A] testResource3.closeStatus does not match"),
      )

      Result.all(before ++ List(result) ++ after)
    }

  def testReleasableFutureResourceFlatMapAndMap: Property =
    for {
      content  <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content")
      content2 <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content2")
      content3 <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content3")
    } yield {

      val expectedContent = content ++ content2 ++ content3
      val testResource    = TestResource.withContent(content)
      val testResource2   = TestResource.withContent(content2)
      val testResource3   = TestResource()

      import extras.concurrent.testing.ConcurrentSupport

      import scala.concurrent.Future
      import scala.concurrent.duration._

      implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

      val waitFor                                   = WaitFor(200.milliseconds)
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(3)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val resource = ReleasableFutureResource(Future(testResource))

      var actualContent = Vector.empty[String] // scalafix:ok DisableSyntax.var

      val before = List(
        (testResource.content ==== content).log("[B] testResource.content does not match"),
        (testResource.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[B] testResource.closeStatus does not match"),
        (testResource2.content ==== content2).log("[B] testResource2.content does not match"),
        (testResource2.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[B] testResource2.closeStatus does not match"),
        (testResource3.content ==== Vector.empty).log("[B] testResource3.content does not match"),
        (testResource3.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[B] testResource3.closeStatus does not match"),
      )

      val result =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        ) {
          resource
            .flatMap { _ =>
              ReleasableFutureResource(Future(testResource2))
            }
            .map(_.content ++ content3)
            .flatMap { content =>
              ReleasableFutureResource
                .make(Future(content.foreach(testResource3.write)))(_ => Future.unit)
                .flatMap(_ => ReleasableFutureResource.pure(testResource3))
            }
            .use { newResource =>
              actualContent = content ++ newResource.content
              Future.unit
            }
            .map(_ => Result.success)
            .recover {
              case err =>
                Result.failure.log(s"No error expected but got ${err.toString}")
            }

        }

      val after = List(
        (actualContent ==== expectedContent).log("[A] content does not match"),
        (testResource.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("[A] testResource.closeStatus does not match"),
        (testResource2.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("[A] testResource2.closeStatus does not match"),
        (testResource3.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[A] testResource3.closeStatus does not match"),
      )

      Result.all(before ++ List(result) ++ after)
    }

  def testReleasableFutureResourceFlatMapAndMapErrorCase: Property =
    for {
      content  <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content")
      content2 <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content2")
      content3 <- Gen
                    .string(Gen.unicode, Range.linear(1, 100))
                    .list(Range.linear(1, 10))
                    .map(_.toVector)
                    .log("content3")
    } yield {
      val expectedContent = content ++ content2 ++ content3
      val testResource    = TestResource.withContent(content)
      val testResource2   = TestResource.withContent(content2)
      val testResource3   = TestResource()

      import extras.concurrent.testing.ConcurrentSupport

      import scala.concurrent.Future
      import scala.concurrent.duration._

      implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

      val waitFor                                   = WaitFor(200.milliseconds)
      implicit val executorService: ExecutorService = Executors.newFixedThreadPool(3)
      implicit val ec: ExecutionContext             =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val resource = ReleasableFutureResource(Future(testResource))

      var actualContent = Vector.empty[String] // scalafix:ok DisableSyntax.var

      val before = List(
        (testResource.content ==== content).log("[B] testResource.content does not match"),
        (testResource.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[B] testResource.closeStatus does not match"),
        (testResource2.content ==== content2).log("[B] testResource2.content does not match"),
        (testResource2.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[B] testResource2.closeStatus does not match"),
        (testResource3.content ==== Vector.empty).log("[B] testResource3.content does not match"),
        (testResource3.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[B] testResource3.closeStatus does not match"),
      )

      val result =
        ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor,
        ) {
          resource
            .flatMap(_ => ReleasableFutureResource(Future(testResource2)))
            .map(_.content ++ content3)
            .flatMap { content =>
              ReleasableFutureResource
                .make(Future(content.foreach(testResource3.write)))(_ => Future.unit)
                .flatMap(_ => ReleasableFutureResource.pure(testResource3))
            }
            .use { newResource =>
              actualContent = content ++ newResource.content
              Future.failed[Unit](TestException(123))
            }
            .map(_ => Result.failure.log(s"Error was expected but no expected error was given"))
            .recover {
              case TestException(123) => Result.success
              case ex: Throwable =>
                Result
                  .failure
                  .log(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
            }
        }

      val after = List(
        (actualContent ==== expectedContent).log("[A] content does not match"),
        (testResource.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("[A] testResource.closeStatus does not match"),
        (testResource2.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("[A] testResource2.closeStatus does not match"),
        (testResource3.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("[A] testResource3.closeStatus does not match"),
      )

      Result.all(before ++ List(result) ++ after)
    }

}
