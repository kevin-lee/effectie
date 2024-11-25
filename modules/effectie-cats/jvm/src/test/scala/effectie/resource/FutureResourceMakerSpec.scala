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
object FutureResourceMakerSpec extends Properties {

  override def tests: List[Test] = List(
    property(
      "test ResourceMaker.futureResourceMaker: ResourceMaker[Future]",
      testFutureResourceMaker,
    ),
    property(
      "test ResourceMaker.futureResourceMaker: ResourceMaker[Future] - error case",
      testFutureResourceMakerErrorCase,
    ),
    property(
      "test ResourceMaker.futureResourceMaker: ResourceMaker[Future] with make",
      testFutureResourceMakerMake,
    ),
    property(
      "test ResourceMaker.futureResourceMaker: ResourceMaker[Future] with make - error case",
      testFutureResourceMakerMakeErrorCase,
    ),
    property(
      "test ResourceMaker.futureResourceMaker: ResourceMaker[Future] with make - error case in closing",
      testFutureResourceMakerMakeErrorCaseInClosing,
    ),
    property(
      "test ResourceMaker.futureResourceMaker: ResourceMaker[Future] with pure",
      testFutureResourceMakerPure,
    ),
    property(
      "test ResourceMaker.futureResourceMaker: ResourceMaker[Future] with pure - error case",
      testFutureResourceMakerPureErrorCase,
    ),
    property(
      "test ResourceMaker.futureResourceMaker: ResourceMaker[Future] with eval",
      testFutureResourceMakerEval,
    ),
    property(
      "test ResourceMaker.futureResourceMaker: ResourceMaker[Future] with eval - error case",
      testFutureResourceMakerEvalErrorCase,
    ),
  )

  def testFutureResourceMaker: Property =
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

      implicit val resourceMaker: ResourceMaker[Future] = ResourceMaker.futureResourceMaker

      ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(
        ResourceMakerSpec
          .testForAutoCloseable[Future](TestResource.apply)(
            content,
            _ => Future.successful(()),
            none,
          )
      )

    }

  def testFutureResourceMakerErrorCase: Property =
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

      implicit val resourceMaker: ResourceMaker[Future] = ResourceMaker.futureResourceMaker

      ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(
        ResourceMakerSpec
          .testForAutoCloseable[Future](TestResource.apply)(
            content,
            _ => Future.failed(TestException(123)),
            Option({
              case TestException(123) => Result.success
              case ex: Throwable =>
                Result
                  .failure
                  .log(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
            }),
          )
      )

    }

  def testFutureResourceMakerMake: Property =
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

      implicit val resourceMaker: ResourceMaker[Future] = ResourceMaker.futureResourceMaker

      ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(
        ResourceMakerSpec
          .testForMake[Future](TestResourceNoAutoClose.apply)(
            _.release(),
            content,
            _ => Future.successful(Result.success),
            none,
          )
      )

    }

  def testFutureResourceMakerMakeErrorCase: Property =
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

      implicit val resourceMaker: ResourceMaker[Future] = ResourceMaker.futureResourceMaker

      ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(
        ResourceMakerSpec
          .testForMake[Future](TestResourceNoAutoClose.apply)(
            _.release(),
            content,
            _ => Future.failed(TestException(123)),
            Option({
              case TestException(123) => Result.success
              case ex: Throwable =>
                Result
                  .failure
                  .log(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
            }),
          )
      )

    }

  def testFutureResourceMakerMakeErrorCaseInClosing: Property =
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

      implicit val resourceMaker: ResourceMaker[Future] = ResourceMaker.futureResourceMaker

      ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(
        ResourceMakerSpec
          .testForMake[Future](TestResourceNoAutoClose.apply)(
            { resource =>
              resource.release()
              throw new RuntimeException(
                "Test error in closing resource. It's only for testing so please ignore."
              ) // scalafix:ok DisableSyntax.throw
            },
            content,
            _ => Future.successful(Result.success),
            none,
          )
      )

    }

  def testFutureResourceMakerPure: Property =
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

      implicit val resourceMaker: ResourceMaker[Future] = ResourceMaker.futureResourceMaker

      ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(
        ResourceMakerSpec
          .testFor[Future](TestResourceNoAutoClose.apply)(
            resourceMaker.pure,
            content,
            _ => Future.successful(Result.success),
            closeStatus => closeStatus ==== TestableResource.CloseStatus.NotClosed,
            none,
          )
      )

    }

  def testFutureResourceMakerPureErrorCase: Property =
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

      implicit val resourceMaker: ResourceMaker[Future] = ResourceMaker.futureResourceMaker

      ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(
        ResourceMakerSpec
          .testFor[Future](TestResourceNoAutoClose.apply)(
            resourceMaker.pure,
            content,
            _ => Future.failed(TestException(123)),
            closeStatus => closeStatus ==== TestableResource.CloseStatus.NotClosed,
            Option({
              case TestException(123) => Result.success
              case ex: Throwable =>
                Result
                  .failure
                  .log(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
            }),
          )
      )

    }

  def testFutureResourceMakerEval: Property =
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

      implicit val resourceMaker: ResourceMaker[Future] = ResourceMaker.futureResourceMaker

      ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(
        ResourceMakerSpec
          .testFor[Future](TestResourceNoAutoClose.apply)(
            testResource => resourceMaker.eval(Future(testResource)),
            content,
            _ => Future.successful(Result.success),
            closeStatus => closeStatus ==== TestableResource.CloseStatus.NotClosed,
            none,
          )
      )

    }

  def testFutureResourceMakerEvalErrorCase: Property =
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

      implicit val resourceMaker: ResourceMaker[Future] = ResourceMaker.futureResourceMaker

      ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(
        ResourceMakerSpec
          .testFor[Future](TestResourceNoAutoClose.apply)(
            testResource => resourceMaker.eval(Future(testResource)),
            content,
            _ => Future.failed(TestException(123)),
            closeStatus => closeStatus ==== TestableResource.CloseStatus.NotClosed,
            Option({
              case TestException(123) => Result.success
              case ex: Throwable =>
                Result
                  .failure
                  .log(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
            }),
          )
      )

    }

}
