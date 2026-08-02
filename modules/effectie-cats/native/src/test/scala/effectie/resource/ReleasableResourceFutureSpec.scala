package effectie.resource

import effectie.resource.data.TestErrors.TestException
import effectie.resource.data.{TestResource, TestResourceNoAutoClose, TestableResource}
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog._
import hedgehog.runner._

import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

/** Semantics of the ReleasableResource ADT interpreted with the automatic UseResource[Future] instance.
  *
  * @author Kevin Lee
  * @since 2026-08-02
  */
object ReleasableResourceFutureSpec extends Properties {

  override def tests: List[Test] = List(
    example(
      "test ReleasableResource[Future] laziness: construction does not acquire, each use re-acquires",
      testLazyAcquisitionAndReuse,
    ),
    example(
      "test ReleasableResource[Future]: synchronous throw in use still releases the resource",
      testSyncThrowInUseStillReleases,
    ),
    example(
      "test ReleasableResource[Future] release error: propagated when use succeeded",
      testReleaseErrorPropagatedOnUseSuccess,
    ),
    example(
      "test ReleasableResource[Future] ExitCase: Completed on success, Errored on use failure",
      testExitCases,
    ),
    example(
      "test ReleasableResource[Future] flatMap chain: acquisition order and LIFO release with ExitCase.Completed",
      testFlatMapChainLifoRelease,
    ),
    example(
      "test ReleasableResource.fromAutoCloseable[Future, A]: close() is called on success and on failure",
      testFromAutoCloseableClosesTheResource,
    ),
  )

  private def exitCaseName(exitCase: ReleasableResource.ExitCase): String =
    exitCase match {
      case ReleasableResource.ExitCase.Completed => "Completed"
      case ReleasableResource.ExitCase.Errored(err) => s"Errored(${err.toString})"
      case ReleasableResource.ExitCase.Canceled => "Canceled"
    }

  def testLazyAcquisitionAndReuse: Result = {
    implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

    val waitFor                                   = WaitFor(400.milliseconds)
    implicit val executorService: ExecutorService = Executors.newFixedThreadPool(3)
    implicit val ec: ExecutionContext             =
      ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

    var acquireCount = 0 // scalafix:ok DisableSyntax.var
    var releaseCount = 0 // scalafix:ok DisableSyntax.var

    val resource =
      ReleasableResource.make[Future, Int](Future {
        acquireCount += 1
        acquireCount
      })(_ =>
        Future {
          releaseCount += 1
          ()
        }
      )

    val beforeUse = List(
      (acquireCount ==== 0).log("acquired before use"),
      (releaseCount ==== 0).log("released before use"),
    )

    val useResults =
      ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(
        for {
          r1 <- resource.use(n => Future.successful(n))
          r2 <- resource.use(n => Future.successful(n))
        } yield (r1, r2)
      )

    useResults match {
      case (firstResult, secondResult) =>
        Result.all(
          beforeUse ++ List(
            (firstResult ==== 1).log("first use result"),
            (secondResult ==== 2).log("second use result (fresh acquisition)"),
            (acquireCount ==== 2).log("acquireCount after both uses"),
            (releaseCount ==== 2).log("releaseCount after both uses"),
          )
        )
    }
  }

  def testSyncThrowInUseStillReleases: Result = {
    implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

    val waitFor                                   = WaitFor(400.milliseconds)
    implicit val executorService: ExecutorService = Executors.newFixedThreadPool(3)
    implicit val ec: ExecutionContext             =
      ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

    val testResource = TestResourceNoAutoClose()

    val resource =
      ReleasableResource.make[Future, TestResourceNoAutoClose](Future(testResource))(a => Future(a.release()))

    val useResult =
      ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(
        resource
          .use[Unit](_ => throw TestException(123)) // scalafix:ok DisableSyntax.throw
          .map(_ => Result.failure.log("An error was expected but the use succeeded"))
          .recover {
            case TestException(123) => Result.success
            case ex: Throwable =>
              Result.failure.log(s"TestException was expected but got ${ex.toString}")
          }
      )

    Result.all(
      List(
        useResult,
        (testResource.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("the resource should be released even when the use function throws synchronously"),
      )
    )
  }

  def testReleaseErrorPropagatedOnUseSuccess: Result = {
    implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

    val waitFor                                   = WaitFor(400.milliseconds)
    implicit val executorService: ExecutorService = Executors.newFixedThreadPool(3)
    implicit val ec: ExecutionContext             =
      ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

    val resource =
      ReleasableResource.make[Future, Int](Future.successful(1))(_ => Future.failed[Unit](TestException(1)))

    val useResult =
      ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(
        resource
          .use(n => Future.successful(n))
          .map(n => Result.failure.log(s"TestException(1) was expected but got Success(${n.toString})"))
          .recover {
            case TestException(1) => Result.success
            case ex: Throwable =>
              Result.failure.log(s"TestException(1) was expected but got ${ex.toString}")
          }
      )

    useResult.log("release error should propagate when use succeeded")
  }

  def testExitCases: Result = {
    implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

    val waitFor                                   = WaitFor(400.milliseconds)
    implicit val executorService: ExecutorService = Executors.newFixedThreadPool(3)
    implicit val ec: ExecutionContext             =
      ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

    var exitCases = Vector.empty[String] // scalafix:ok DisableSyntax.var

    def resource: ReleasableResource[Future, Int] =
      ReleasableResource.makeCase(Future(1)) { (_, exitCase) =>
        Future {
          exitCases = exitCases :+ exitCaseName(exitCase)
          ()
        }
      }

    val useResults =
      ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(
        for {
          r1 <- resource.use(n => Future.successful(n))
          r2 <- resource.use(_ => Future.failed[Int](TestException(9))).recover { case TestException(9) => -1 }
        } yield (r1, r2)
      )

    useResults match {
      case (firstResult, secondResult) =>
        Result.all(
          List(
            (firstResult ==== 1).log("first use result"),
            (secondResult ==== -1).log("second use result"),
            (exitCases ==== Vector("Completed", s"Errored(${TestException(9).toString})"))
              .log("exit cases seen by release"),
          )
        )
    }
  }

  def testFromAutoCloseableClosesTheResource: Result = {
    import effectie.instances.future.fxCtor._

    implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

    val waitFor                                   = WaitFor(400.milliseconds)
    implicit val executorService: ExecutorService = Executors.newFixedThreadPool(3)
    implicit val ec: ExecutionContext             =
      ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

    val successResource = TestResource()
    val failureResource = TestResource()
    val throwResource   = TestResource()

    val beforeUse = Result.all(
      List(
        (successResource.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("the resource should not be closed before use"),
        (failureResource.closeStatus ==== TestableResource.CloseStatus.notClosed)
          .log("the resource should not be closed before use"),
      )
    )

    val results =
      ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(
        for {
          successResult <- ReleasableResource
                             .fromAutoCloseable[Future, TestResource](Future(successResource))
                             .use(resource => Future(resource.write("written")))
                             .map(_ => Result.success)
          failureResult <- ReleasableResource
                             .fromAutoCloseable[Future, TestResource](Future(failureResource))
                             .use(_ => Future.failed[Unit](TestException(1)))
                             .map(_ => Result.failure.log("An error was expected but the use succeeded"))
                             .recover {
                               case TestException(1) => Result.success
                               case ex: Throwable =>
                                 Result.failure.log(s"TestException(1) was expected but got ${ex.toString}")
                             }
          throwResult   <- ReleasableResource
                             .fromAutoCloseable[Future, TestResource](Future(throwResource))
                             .use[Unit](_ => throw TestException(2)) // scalafix:ok DisableSyntax.throw
                             .map(_ => Result.failure.log("An error was expected but the use succeeded"))
                             .recover {
                               case TestException(2) => Result.success
                               case ex: Throwable =>
                                 Result.failure.log(s"TestException(2) was expected but got ${ex.toString}")
                             }
        } yield Result.all(List(successResult, failureResult, throwResult))
      )

    Result.all(
      List(
        beforeUse,
        results,
        (successResource.content ==== Vector("written")).log("content written during use"),
        (successResource.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("close() should be called when the use function succeeds"),
        (failureResource.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("close() should be called when the use function returns a failed Future"),
        (throwResource.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("close() should be called when the use function throws synchronously"),
      )
    )
  }

  def testFlatMapChainLifoRelease: Result = {
    implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

    val waitFor                                   = WaitFor(400.milliseconds)
    implicit val executorService: ExecutorService = Executors.newFixedThreadPool(3)
    implicit val ec: ExecutionContext             =
      ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

    var eventLog = Vector.empty[String] // scalafix:ok DisableSyntax.var

    def resource(name: String): ReleasableResource[Future, String] =
      ReleasableResource.makeCase(Future {
        eventLog = eventLog :+ s"acquire:$name"
        name
      }) { (n, exitCase) =>
        Future {
          eventLog = eventLog :+ s"release:$n:${exitCaseName(exitCase)}"
          ()
        }
      }

    val combined =
      for {
        a <- resource("a")
        b <- resource("b")
        c <- resource("c")
      } yield s"$a$b$c"

    val result =
      ConcurrentSupport.futureToValueAndTerminate(
        executorService,
        waitFor,
      )(
        combined.use(all => Future.successful(all))
      )

    Result.all(
      List(
        (result ==== "abc").log("use result"),
        (eventLog ==== Vector(
          "acquire:a",
          "acquire:b",
          "acquire:c",
          "release:c:Completed",
          "release:b:Completed",
          "release:a:Completed",
        )).log("acquisition and LIFO release order"),
      )
    )
  }

}
