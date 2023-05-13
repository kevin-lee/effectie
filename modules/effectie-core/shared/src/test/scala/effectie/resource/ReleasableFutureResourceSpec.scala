package effectie.resource

import cats.syntax.all._
import effectie.resource.data.TestErrors.TestException
import effectie.resource.data.{TestResource, TestResourceNoAutoClose}
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

}
