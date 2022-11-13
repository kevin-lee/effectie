package effectie.resource

import cats.syntax.all._
import effectie.resource.data.TestErrors.TestException
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
    )
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
          .testFromAutoCloseable[Future, ReleasableResource.UnusedHandleError](
            content,
            _ => Future.successful(()),
            none,
            ReleasableFutureResource.apply,
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
          .testFromAutoCloseable[Future, ReleasableResource.UnusedHandleError](
            content,
            _ => Future.failed(TestException(123)),
            Option({
              case TestException(123) => Result.success
              case ex: Throwable =>
                Result
                  .failure
                  .log(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
            }),
            ReleasableFutureResource.apply,
          )
      )

    }

}
