package effectie.resource

import effectie.resource.data.TestErrors.TestException
import effectie.resource.data.{TestResource, TestResourceNoAutoClose, TestableResource}
import hedgehog._
import hedgehog.runner._

import scala.annotation.nowarn
import scala.util.{Failure, Success, Try}

/** Semantics of the generic UseResource interpreter (UseResource.MonadThrowUseResource) exercised through the automatic
  * UseResource[Try] instance.
  *
  * This covers the parts of the interpreter that ReleasableResourceTrySpec does not: synchronous throw capture
  * (`safely`), running every finalizer even when earlier ones fail (`runFinalizers`), error suppression
  * (`suppressOnto`), control-stack skipping, and the ADT members with no coverage elsewhere (`raiseError`, `unit`, `ap`,
  * `onFinalize`).
  *
  * Assertions about the *order* of suppressed exceptions live in the JVM-only UseResourceSuppressionSpec, since
  * `Throwable.addSuppressed` ordering is not guaranteed to be identical on Scala.js and Scala Native. Here only
  * membership is asserted.
  *
  * @author Kevin Lee
  * @since 2026-08-02
  */
object UseResourceTryInterpreterSpec extends Properties {

  override def tests: List[Test] = List(
    example(
      "test ReleasableResource.raiseError[Try]: fails the scope and still releases acquired resources",
      testRaiseErrorStandalone,
    ),
    example(
      "test ReleasableResource.unit[Try]",
      testUnit,
    ),
    example(
      "test ReleasableResource[Try].ap: result, acquisition order and error propagation",
      testAp,
    ),
    example(
      "test ReleasableResource[Try].onFinalize: runs after the resource's own release on success and on error",
      testOnFinalize,
    ),
    example(
      "test ReleasableResource[Try] by-name constructors: eval and makeCase are lazy and re-run on each use",
      testByNameConstructorLaziness,
    ),
    example(
      "test UseResource[Try]: every finalizer runs even when earlier ones fail (use succeeded)",
      testAllFinalizersRunWhenEarlyOnesFail,
    ),
    example(
      "test UseResource[Try]: every finalizer runs even when earlier ones fail (use failed)",
      testAllFinalizersRunUnderUseError,
    ),
    example(
      "test UseResource[Try]: the same Throwable as use error and release error is not self-suppressed",
      testSelfSuppressionGuard,
    ),
    example(
      "test UseResource[Try]: synchronous throws from acquire, eval, finalizer and use are captured",
      testSafelyCatchesSyncThrows,
    ),
    example(
      "test UseResource[Try]: synchronous throws from flatMap and handleErrorWith functions are captured",
      testSafelyCatchesContinuationThrow,
    ),
    example(
      "test UseResource[Try]: handleErrorWith handler is skipped when the source succeeds",
      testHandlerSkippedOnSuccessPath,
    ),
    example(
      "test UseResource[Try]: flatMap continuation is skipped when the source fails",
      testContSkippedOnErrorPath,
    ),
    example(
      "test ReleasableResource[Try] surround / use_ on the error path",
      testUseUnitAndSurroundOnErrorPath,
    ),
    example(
      "test ReleasableResource[Try].attempt: success, eval error and raiseError",
      testAttemptEvalErrorAndSuccess,
    ),
    example(
      "test UseResource[Try]: summoner and unitOf",
      testUseResourceSummonerAndUnitOf,
    ),
    example(
      "test ReleasableResource[Try].handleErrorWith: nested handlers and rethrow from within a handler",
      testNestedHandleErrorWith,
    ),
    example(
      "test ReleasableResource[Try] evalTap / flatTap failures release the source with ExitCase.Errored",
      testTapFailures,
    ),
    example(
      "test ReleasableResource.ExitCase smart constructors",
      testExitCaseSmartConstructors,
    ),
    example(
      "test ReleasableResource.fromAutoCloseable[Try, A]: close() is called when the use function fails or throws",
      testFromAutoCloseableClosesOnUseFailure,
    ),
    property(
      "test deprecated ReleasableResource Try shims: usingResource / usingResourceFromTry / makeTry / pureTry",
      testDeprecatedTryShims,
    ),
  )

  private def exitCaseName(exitCase: ReleasableResource.ExitCase): String =
    exitCase match {
      case ReleasableResource.ExitCase.Completed => "Completed"
      case ReleasableResource.ExitCase.Errored(err) => s"Errored(${err.toString})"
      case ReleasableResource.ExitCase.Canceled => "Canceled"
    }

  /** `ReleasableResource.raiseError` returns `ReleasableResource[F, Nothing]`. That is only a problem for combinators
    * whose function parameter is typed `A` — `use` and `flatMap` — because the lambda then takes a `Nothing` and its
    * body is unreachable, which `-Xfatal-warnings` rejects. This widens the element type for those cases.
    *
    * It is not needed elsewhere: `handleErrorWith` takes a `Throwable`, and `ap` takes the function inside a resource,
    * so both accept `raiseError` directly (with an explicit type argument where inference would otherwise pick
    * `Nothing`).
    */
  private def raiseErrorAs[A](error: Throwable): ReleasableResource[Try, A] =
    ReleasableResource.raiseError[Try](error)

  private def containsSuppressed(err: Throwable, expected: Throwable): Result =
    Result
      .assert(err.getSuppressed.toList.exists(_ == expected))
      .log(
        s"${expected.toString} should be suppressed under ${err.toString} " +
          s"but suppressed = ${err.getSuppressed.toList.toString}"
      )

  def testRaiseErrorStandalone: Result = {
    val standalone = raiseErrorAs[Int](TestException(1)).use(n => Try(n))

    var eventLog = Vector.empty[String] // scalafix:ok DisableSyntax.var

    val withAcquiredResource =
      ReleasableResource
        .makeCase(Try {
          eventLog = eventLog :+ "acquire"
          1
        }) { (_, exitCase) =>
          Try {
            eventLog = eventLog :+ s"release:${exitCaseName(exitCase)}"
          }
        }
        .flatMap[Int](_ => ReleasableResource.raiseError[Try](TestException(2)))
        .use(n => Try(n))

    Result.all(
      List(
        (standalone ==== Failure(TestException(1))).log("standalone raiseError should fail the scope"),
        (withAcquiredResource ==== Failure(TestException(2))).log("raiseError after acquisition should fail the scope"),
        (eventLog ==== Vector("acquire", s"release:Errored(${TestException(2).toString})"))
          .log("an already-acquired resource should still be released with the raised error as its ExitCase"),
      )
    )
  }

  def testUnit: Result =
    Result.all(
      List(
        (ReleasableResource.unit[Try].use(u => Try(u)) ==== Success(())).log("unit.use"),
        (ReleasableResource.unit[Try].use_ ==== Success(())).log("unit.use_"),
      )
    )

  def testAp: Result = {
    val simple =
      ReleasableResource
        .pure[Try, Int](3)
        .ap(ReleasableResource.pure[Try, Int => String](n => s"v${n.toString}"))
        .use(s => Try(s))

    var eventLog = Vector.empty[String] // scalafix:ok DisableSyntax.var

    def resource[A](name: String, a: A): ReleasableResource[Try, A] =
      ReleasableResource.make[Try, A](Try {
        eventLog = eventLog :+ s"acquire:$name"
        a
      })(_ =>
        Try {
          eventLog = eventLog :+ s"release:$name"
        }
      )

    val valueResource    = resource("a", 1)
    val functionResource = resource[Int => String]("f", n => s"f${n.toString}")

    val ordered = valueResource.ap(functionResource).use(s => Try(s))

    // `raiseError` gives ReleasableResource[Try, Nothing]. Because ReleasableResource is covariant in A and
    // Function1 is contravariant in its parameter, `Int => Int` conforms to `Nothing => B` with B = Int, so the
    // whole expression is typed ReleasableResource[Try, Int] and nothing downstream is dead code. Annotating the
    // function as `Nothing => Int` instead is what makes its body unreachable.
    val valueRaiseErrorCase =
      ReleasableResource
        .raiseError[Try](TestException(1))
        .ap(ReleasableResource.pure[Try, Int => Int](identity))
        .use(n => Try(n))

    val valueEvalErrorCase =
      ReleasableResource
        .eval[Try, Int](Failure[Int](TestException(2)))
        .ap(ReleasableResource.pure[Try, Int => Int](identity))
        .use(n => Try(n))

    val functionErrorCase =
      ReleasableResource
        .pure[Try, Int](1)
        .ap[Int](ReleasableResource.raiseError[Try](TestException(3)))
        .use(n => Try(n))

    Result.all(
      List(
        (simple ==== Success("v3")).log("ap result"),
        (ordered ==== Success("f1")).log("ap result with acquisition"),
        (eventLog ==== Vector("acquire:f", "acquire:a", "release:a", "release:f"))
          .log("ap is ff.flatMap(f => map(f)), so the function resource is acquired first and released last"),
        (valueRaiseErrorCase ==== Failure(TestException(1)))
          .log("ap should propagate a raised error from the value resource"),
        (valueEvalErrorCase ==== Failure(TestException(2)))
          .log("ap should propagate an eval error from the value resource"),
        (functionErrorCase ==== Failure(TestException(3)))
          .log("ap should propagate a raised error from the function resource"),
      )
    )
  }

  def testOnFinalize: Result = {
    var eventLog = Vector.empty[String] // scalafix:ok DisableSyntax.var

    def resource: ReleasableResource[Try, Int] =
      ReleasableResource
        .make[Try, Int](Try {
          eventLog = eventLog :+ "acquire"
          1
        })(_ =>
          Try {
            eventLog = eventLog :+ "release"
          }
        )
        .onFinalize(Try {
          eventLog = eventLog :+ "onFinalize"
        })

    val successResult = resource.use(n => Try(n))
    val successLog    = eventLog

    eventLog = Vector.empty[String]

    val errorResult = resource.use(_ => Failure[Int](TestException(5)))

    Result.all(
      List(
        (successResult ==== Success(1)).log("use result"),
        (successLog ==== Vector("acquire", "release", "onFinalize"))
          .log("onFinalize runs after the resource's own release"),
        (errorResult ==== Failure(TestException(5))).log("use error should propagate"),
        (eventLog ==== Vector("acquire", "release", "onFinalize"))
          .log("onFinalize should also run when the use function fails"),
      )
    )
  }

  def testByNameConstructorLaziness: Result = {
    var evalCount = 0 // scalafix:ok DisableSyntax.var

    val evalResource = ReleasableResource.eval[Try, Int](Try {
      evalCount += 1
      evalCount
    })

    val evalBeforeUse = (evalCount ==== 0).log("eval should not run its body at construction")

    val evalFirst  = evalResource.use(n => Try(n))
    val evalSecond = evalResource.use(n => Try(n))

    var acquireCount = 0 // scalafix:ok DisableSyntax.var

    val makeCaseResource = ReleasableResource.makeCase[Try, Int](Try {
      acquireCount += 1
      acquireCount
    })((_, _) => Try(()))

    val makeCaseBeforeUse = (acquireCount ==== 0).log("makeCase should not acquire at construction")

    val makeCaseFirst  = makeCaseResource.use(n => Try(n))
    val makeCaseSecond = makeCaseResource.use(n => Try(n))

    Result.all(
      List(
        evalBeforeUse,
        (evalFirst ==== Success(1)).log("first eval use"),
        (evalSecond ==== Success(2)).log("eval body should re-run on each use"),
        (evalCount ==== 2).log("evalCount after two uses"),
        makeCaseBeforeUse,
        (makeCaseFirst ==== Success(1)).log("first makeCase use"),
        (makeCaseSecond ==== Success(2)).log("makeCase should re-acquire on each use"),
        (acquireCount ==== 2).log("acquireCount after two uses"),
      )
    )
  }

  private def threeFailingFinalizers(
    eventLog: () => Vector[String],
    appendToLog: String => Unit,
  ): ReleasableResource[Try, String] = {
    val _ = eventLog

    def resource(name: String, error: Throwable): ReleasableResource[Try, String] =
      ReleasableResource.make[Try, String](Try {
        appendToLog(s"acquire:$name")
        name
      })(_ =>
        Try {
          appendToLog(s"release:$name")
        }.flatMap(_ => Failure[Unit](error))
      )

    for {
      a <- resource("a", TestException(1))
      b <- resource("b", TestException(2))
      c <- resource("c", TestException(3))
    } yield s"$a$b$c"
  }

  def testAllFinalizersRunWhenEarlyOnesFail: Result = {
    var eventLog = Vector.empty[String] // scalafix:ok DisableSyntax.var

    val combined = threeFailingFinalizers(() => eventLog, s => eventLog = eventLog :+ s)

    val result = combined.use(all => Try(all))

    val logResult =
      (eventLog ==== Vector("acquire:a", "acquire:b", "acquire:c", "release:c", "release:b", "release:a"))
        .log("every finalizer should run in LIFO order even though each one fails")

    result match {
      case Failure(err) =>
        Result.all(
          List(
            logResult,
            (err ==== TestException(3))
              .log("the innermost (LIFO-first) finalizer error should be the primary one"),
            containsSuppressed(err, TestException(2)),
            containsSuppressed(err, TestException(1)),
          )
        )
      case Success(value) =>
        Result.all(List(logResult, Result.failure.log(s"Failure was expected but got Success(${value.toString})")))
    }
  }

  def testAllFinalizersRunUnderUseError: Result = {
    var eventLog = Vector.empty[String] // scalafix:ok DisableSyntax.var

    val combined = threeFailingFinalizers(() => eventLog, s => eventLog = eventLog :+ s)

    val result = combined.use(_ => Failure[String](TestException(99)))

    val logResult =
      (eventLog ==== Vector("acquire:a", "acquire:b", "acquire:c", "release:c", "release:b", "release:a"))
        .log("every finalizer should run in LIFO order even when the use function failed")

    result match {
      case Failure(err) =>
        Result.all(
          List(
            logResult,
            (err ==== TestException(99)).log("the use error should be the primary one"),
            containsSuppressed(err, TestException(3)),
            containsSuppressed(err, TestException(2)),
            containsSuppressed(err, TestException(1)),
          )
        )
      case Success(value) =>
        Result.all(List(logResult, Result.failure.log(s"Failure was expected but got Success(${value.toString})")))
    }
  }

  def testSelfSuppressionGuard: Result = {
    val shared = TestException(7)

    val resource = ReleasableResource.make[Try, Int](Try(1))(_ => Failure[Unit](shared))

    val result = resource.use(_ => Failure[Int](shared))

    result match {
      case Failure(err) =>
        Result.all(
          List(
            Result.assert(err eq shared).log("the shared Throwable instance should be the raised error"),
            (err.getSuppressed.toList ==== List.empty[Throwable])
              .log("a Throwable must not be suppressed under itself (addSuppressed would throw)"),
          )
        )
      case Success(n) =>
        Result.failure.log(s"Failure was expected but got Success(${n.toString})")
    }
  }

  def testSafelyCatchesSyncThrows: Result = {
    var releaseCount = 0 // scalafix:ok DisableSyntax.var

    val throwingAcquire =
      ReleasableResource
        .makeCase[Try, Int](throw TestException(1)) // scalafix:ok DisableSyntax.throw
        { (_, _) =>
          Try {
            releaseCount += 1
          }
        }
        .use(n => Try(n))

    val throwingEval =
      ReleasableResource
        .eval[Try, Int](throw TestException(2)) // scalafix:ok DisableSyntax.throw
        .use(n => Try(n))

    val throwingFinalizer =
      ReleasableResource
        .make[Try, Int](Try(1))(_ => throw TestException(3)) // scalafix:ok DisableSyntax.throw
        .use(n => Try(n))

    val throwingUse =
      ReleasableResource
        .pure[Try, Int](1)
        .use[Int](_ => throw TestException(4)) // scalafix:ok DisableSyntax.throw

    Result.all(
      List(
        (throwingAcquire ==== Failure(TestException(1))).log("a synchronous throw from acquire should be captured"),
        (releaseCount ==== 0).log("nothing was acquired, so the release must not run"),
        (throwingEval ==== Failure(TestException(2))).log("a synchronous throw from eval should be captured"),
        (throwingFinalizer ==== Failure(TestException(3)))
          .log("a synchronous throw from a finalizer should be captured"),
        (throwingUse ==== Failure(TestException(4)))
          .log("a synchronous throw from the use function should be captured"),
      )
    )
  }

  def testSafelyCatchesContinuationThrow: Result = {
    val throwingFlatMap =
      ReleasableResource
        .pure[Try, Int](1)
        .flatMap[Int](_ => throw TestException(5)) // scalafix:ok DisableSyntax.throw
        .use(n => Try(n))

    val throwingHandler =
      ReleasableResource
        .raiseError[Try](TestException(6))
        .handleErrorWith[Int](_ => throw TestException(7)) // scalafix:ok DisableSyntax.throw
        .use(n => Try(n))

    var eventLog = Vector.empty[String] // scalafix:ok DisableSyntax.var

    val throwingFlatMapAfterAcquisition =
      ReleasableResource
        .makeCase(Try {
          eventLog = eventLog :+ "acquire"
          1
        }) { (_, exitCase) =>
          Try {
            eventLog = eventLog :+ s"release:${exitCaseName(exitCase)}"
          }
        }
        .flatMap[Int](_ => throw TestException(8)) // scalafix:ok DisableSyntax.throw
        .use(n => Try(n))

    Result.all(
      List(
        (throwingFlatMap ==== Failure(TestException(5)))
          .log("a synchronous throw from a flatMap continuation should be captured"),
        (throwingHandler ==== Failure(TestException(7)))
          .log("a synchronous throw from a handleErrorWith handler should be captured"),
        (throwingFlatMapAfterAcquisition ==== Failure(TestException(8))).log("the thrown error should propagate"),
        (eventLog ==== Vector("acquire", s"release:Errored(${TestException(8).toString})"))
          .log("a resource acquired before a throwing continuation should still be released"),
      )
    )
  }

  def testHandlerSkippedOnSuccessPath: Result = {
    var handlerCalls = 0 // scalafix:ok DisableSyntax.var

    val result =
      ReleasableResource
        .pure[Try, Int](1)
        .handleErrorWith { _ =>
          handlerCalls += 1
          ReleasableResource.pure[Try, Int](99)
        }
        .use(n => Try(n))

    Result.all(
      List(
        (result ==== Success(1)).log("the source value should be used"),
        (handlerCalls ==== 0).log("the handler must not be invoked when the source succeeds"),
      )
    )
  }

  def testContSkippedOnErrorPath: Result = {
    var contCalls = 0 // scalafix:ok DisableSyntax.var

    val skipped =
      raiseErrorAs[Int](TestException(1))
        .flatMap { _ =>
          contCalls += 1
          ReleasableResource.pure[Try, Int](99)
        }
        .use(n => Try(n))

    val afterSkipped = contCalls

    var handlerCalls = 0 // scalafix:ok DisableSyntax.var

    val recovered =
      raiseErrorAs[Int](TestException(2))
        .flatMap { _ =>
          contCalls += 1
          ReleasableResource.pure[Try, Int](99)
        }
        .handleErrorWith { _ =>
          handlerCalls += 1
          ReleasableResource.pure[Try, Int](7)
        }
        .use(n => Try(n))

    Result.all(
      List(
        (skipped ==== Failure(TestException(1))).log("the error should propagate past the flatMap"),
        (afterSkipped ==== 0).log("the continuation must not be invoked when the source fails"),
        (recovered ==== Success(7)).log("the handler should recover"),
        (contCalls ==== 0).log("the continuation must still be skipped when a handler follows"),
        (handlerCalls ==== 1).log("the handler should be invoked exactly once"),
      )
    )
  }

  def testUseUnitAndSurroundOnErrorPath: Result = {
    var eventLog = Vector.empty[String] // scalafix:ok DisableSyntax.var

    val surroundResult =
      ReleasableResource
        .make[Try, Int](Try {
          eventLog = eventLog :+ "acquire"
          1
        })(_ =>
          Try {
            eventLog = eventLog :+ "release"
          }
        )
        .surround(Failure[Int](TestException(1)))

    val useUnitOnRaiseError = ReleasableResource.raiseError[Try](TestException(2)).use_

    val useUnitOnReleaseError =
      ReleasableResource.make[Try, Int](Try(1))(_ => Failure[Unit](TestException(3))).use_

    Result.all(
      List(
        (surroundResult ==== Failure(TestException(1))).log("surround should propagate the error of its body"),
        (eventLog ==== Vector("acquire", "release")).log("surround should still release the resource"),
        (useUnitOnRaiseError ==== Failure(TestException(2))).log("use_ should propagate a raised error"),
        (useUnitOnReleaseError ==== Failure(TestException(3))).log("use_ should propagate a release error"),
      )
    )
  }

  def testAttemptEvalErrorAndSuccess: Result = {
    val successCase = ReleasableResource.pure[Try, Int](1).attempt.use(either => Try(either))

    val evalErrorCase =
      ReleasableResource.eval[Try, Int](Failure[Int](TestException(1))).attempt.use(either => Try(either))

    val raiseErrorCase =
      ReleasableResource.raiseError[Try](TestException(2)).attempt.use(either => Try(either))

    Result.all(
      List(
        (successCase ==== Success(Right(1))).log("attempt should give Right for a successful resource"),
        (evalErrorCase ==== Success(Left(TestException(1)))).log("attempt should cover eval errors"),
        (raiseErrorCase ==== Success(Left(TestException(2)))).log("attempt should cover raiseError"),
      )
    )
  }

  def testUseResourceSummonerAndUnitOf: Result = {
    val summoned = UseResource[Try]

    val direct = summoned.use(ReleasableResource.pure[Try, Int](1))(n => Try(n))

    Result.all(
      List(
        Result.assert(summoned eq UseResource.tryUseResource).log("the summoner should return the implicit instance"),
        (summoned.unitOf ==== Success(())).log("unitOf"),
        (direct ==== Success(1)).log("UseResource[Try].use called directly"),
      )
    )
  }

  def testNestedHandleErrorWith: Result = {
    val recoveredByOuterHandler =
      ReleasableResource
        .raiseError[Try](TestException(1))
        .handleErrorWith[Int](_ => ReleasableResource.raiseError[Try](TestException(2)))
        .handleErrorWith {
          case TestException(2) => ReleasableResource.pure[Try, Int](42)
          case err => ReleasableResource.raiseError[Try](err)
        }
        .use(n => Try(n))

    val unrecoveredRethrow =
      ReleasableResource
        .raiseError[Try](TestException(1))
        .handleErrorWith[Int](_ => ReleasableResource.raiseError[Try](TestException(2)))
        .use(n => Try(n))

    var eventLog = Vector.empty[String] // scalafix:ok DisableSyntax.var

    val withAmbientFinalizer =
      ReleasableResource
        .makeCase(Try {
          eventLog = eventLog :+ "acquire:a"
          "a"
        }) { (n, exitCase) =>
          Try {
            eventLog = eventLog :+ s"release:$n:${exitCaseName(exitCase)}"
          }
        }
        .flatMap(_ => ReleasableResource.raiseError[Try](TestException(1)))
        .handleErrorWith(_ => ReleasableResource.raiseError[Try](TestException(2)))
        .handleErrorWith(_ => ReleasableResource.pure[Try, Int](7))
        .use(n => Try(n))

    Result.all(
      List(
        (recoveredByOuterHandler ==== Success(42))
          .log("an error rethrown from a handler should be recoverable by an outer handler"),
        (unrecoveredRethrow ==== Failure(TestException(2)))
          .log("an unrecovered rethrow from a handler should propagate"),
        (withAmbientFinalizer ==== Success(7)).log("the outer handler should recover"),
        (eventLog ==== Vector("acquire:a", "release:a:Completed"))
          .log("the ambient finalizer survives both handlers and sees the scope's Completed exit case"),
      )
    )
  }

  def testTapFailures: Result = {
    var eventLog = Vector.empty[String] // scalafix:ok DisableSyntax.var

    def resource(name: String): ReleasableResource[Try, String] =
      ReleasableResource.makeCase(Try {
        eventLog = eventLog :+ s"acquire:$name"
        name
      }) { (n, exitCase) =>
        Try {
          eventLog = eventLog :+ s"release:$n:${exitCaseName(exitCase)}"
        }
      }

    val evalTapFailure = resource("a").evalTap(_ => Failure[Unit](TestException(1))).use(s => Try(s))
    val evalTapLog     = eventLog

    eventLog = Vector.empty[String]

    val flatTapFailure =
      resource("b").flatTap(_ => ReleasableResource.raiseError[Try](TestException(2))).use(s => Try(s))
    val flatTapLog     = eventLog

    eventLog = Vector.empty[String]

    val flatTapAcquisitionFailure =
      resource("c")
        .flatTap(_ => ReleasableResource.make[Try, Int](Failure[Int](TestException(3)))(_ => Try(())))
        .use(s => Try(s))

    Result.all(
      List(
        (evalTapFailure ==== Failure(TestException(1))).log("a failing evalTap should fail the scope"),
        (evalTapLog ==== Vector("acquire:a", s"release:a:Errored(${TestException(1).toString})"))
          .log("the source should be released with the tap error as its ExitCase"),
        (flatTapFailure ==== Failure(TestException(2))).log("a failing flatTap should fail the scope"),
        (flatTapLog ==== Vector("acquire:b", s"release:b:Errored(${TestException(2).toString})"))
          .log("the source should be released with the tap error as its ExitCase"),
        (flatTapAcquisitionFailure ==== Failure(TestException(3)))
          .log("a failing acquisition inside flatTap should fail the scope"),
        (eventLog ==== Vector("acquire:c", s"release:c:Errored(${TestException(3).toString})"))
          .log("the source should still be released when the tapped acquisition fails"),
      )
    )
  }

  def testExitCaseSmartConstructors: Result = {
    val completed: ReleasableResource.ExitCase = ReleasableResource.ExitCase.Completed
    val canceled: ReleasableResource.ExitCase  = ReleasableResource.ExitCase.Canceled
    val errored: ReleasableResource.ExitCase   = ReleasableResource.ExitCase.Errored(TestException(1))

    Result.all(
      List(
        (ReleasableResource.ExitCase.completed ==== completed).log("completed"),
        (ReleasableResource.ExitCase.canceled ==== canceled).log("canceled"),
        (ReleasableResource.ExitCase.errored(TestException(1)) ==== errored).log("errored"),
        Result
          .assert(ReleasableResource.ExitCase.completed != ReleasableResource.ExitCase.canceled)
          .log("Completed and Canceled should not be equal"),
      )
    )
  }

  def testFromAutoCloseableClosesOnUseFailure: Result = {
    import effectie.instances.tries.fxCtor._

    val failedResource = TestResource()

    val beforeUse = (failedResource.closeStatus ==== TestableResource.CloseStatus.notClosed)
      .log("the resource should not be closed before use")

    val failedResult =
      ReleasableResource
        .fromAutoCloseable[Try, TestResource](Try(failedResource))
        .use(_ => Failure[Unit](TestException(1)))

    val thrownResource = TestResource()

    val thrownResult =
      ReleasableResource
        .fromAutoCloseable[Try, TestResource](Try(thrownResource))
        .use[Unit](_ => throw TestException(2)) // scalafix:ok DisableSyntax.throw

    Result.all(
      List(
        beforeUse,
        (failedResult ==== Failure(TestException(1))).log("the use error should propagate"),
        (failedResource.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("close() should be called when the use function returns a Failure"),
        (thrownResult ==== Failure(TestException(2))).log("the thrown error should propagate"),
        (thrownResource.closeStatus ==== TestableResource.CloseStatus.closed)
          .log("close() should be called when the use function throws synchronously"),
      )
    )
  }

  @nowarn("cat=deprecation")
  def testDeprecatedTryShims: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {
      var acquireCount                                = 0 // scalafix:ok DisableSyntax.var
      var usingResourceInstance: Option[TestResource] = None // scalafix:ok DisableSyntax.var

      val usingResourceResource = ReleasableResource.usingResource {
        acquireCount += 1
        val acquired = TestResource()
        usingResourceInstance = Some(acquired)
        acquired
      }

      val usingResourceBeforeUse = (acquireCount ==== 0).log("usingResource should not acquire at construction")

      val usingResourceResult = usingResourceResource.use(resource => Try(content.foreach(resource.write)))

      val fromTryResource = TestResource()
      val fromTryResult   = ReleasableResource
        .usingResourceFromTry(Try(fromTryResource))
        .use(resource => Try(content.foreach(resource.write)))

      val makeTryResource = TestResourceNoAutoClose()
      val makeTryResult   = ReleasableResource
        .makeTry(Try(makeTryResource))(resource => Try(resource.release()))
        .use(resource => Try(content.foreach(resource.write)))

      val pureTryResult = ReleasableResource.pureTry(42).use(n => Try(n))

      Result.all(
        List(
          usingResourceBeforeUse,
          (usingResourceResult ==== Success(())).log("usingResource use result"),
          (acquireCount ==== 1).log("usingResource should acquire once on use"),
          (usingResourceInstance.map(_.content) ==== Some(content)).log("usingResource content"),
          (usingResourceInstance.map(_.closeStatus) ==== Some(TestableResource.CloseStatus.closed))
            .log("usingResource should close the acquired resource"),
          (fromTryResult ==== Success(())).log("usingResourceFromTry use result"),
          (fromTryResource.content ==== content).log("usingResourceFromTry content"),
          (fromTryResource.closeStatus ==== TestableResource.CloseStatus.closed)
            .log("usingResourceFromTry closeStatus"),
          (makeTryResult ==== Success(())).log("makeTry use result"),
          (makeTryResource.content ==== content).log("makeTry content"),
          (makeTryResource.closeStatus ==== TestableResource.CloseStatus.closed).log("makeTry closeStatus"),
          (pureTryResult ==== Success(42)).log("pureTry use result"),
        )
      )
    }

}
