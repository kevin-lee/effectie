package effectie.resource

import cats.syntax.all._
import effectie.resource.data.TestErrors.TestException
import effectie.resource.data.{TestResource, TestResourceNoAutoClose, TestableResource}
import hedgehog._
import hedgehog.runner._

import scala.annotation.nowarn
import scala.util.{Failure, Success, Try}

/** Semantics of the ReleasableResource ADT interpreted with the automatic UseResource[Try] instance.
  *
  * @author Kevin Lee
  * @since 2026-08-02
  */
object ReleasableResourceTrySpec extends Properties {

  override def tests: List[Test] = List(
    example(
      "test ReleasableResource[Try] laziness: construction does not acquire, use acquires, second use re-acquires",
      testLazyAcquisitionAndReuse,
    ),
    example(
      "test ReleasableResource[Try] flatMap: acquisition order and LIFO release with ExitCase.Completed",
      testLifoReleaseAndCompletedExitCase,
    ),
    example(
      "test ReleasableResource[Try] use error: release gets ExitCase.Errored and the error propagates",
      testErroredExitCaseOnUseFailure,
    ),
    example(
      "test ReleasableResource[Try].onFinalizeCase: runs after own release with the scope's ExitCase",
      testOnFinalizeCaseOrderAndExitCase,
    ),
    example(
      "test ReleasableResource[Try] release error: propagated when use succeeded",
      testReleaseErrorPropagatedOnUseSuccess,
    ),
    example(
      "test ReleasableResource[Try] release error: suppressed under the use error when both fail",
      testReleaseErrorSuppressedUnderUseError,
    ),
    example(
      "test ReleasableResource[Try].attempt: covers acquisition errors only",
      testAttemptScope,
    ),
    example(
      "test ReleasableResource[Try].handleErrorWith: recovers acquisition errors; already-acquired finalizers stay in scope",
      testHandleErrorWithAmbientScope,
    ),
    example(
      "test ReleasableResource[Try] evalMap / evalTap / flatTap / surround / use_",
      testDerivedCombinators,
    ),
    property(
      "test ReleasableResource.fromAutoCloseable[Try, A]: closes the resource",
      testFromAutoCloseable,
    ),
    example(
      "test ReleasableResource[Try] stack safety: 10,000 deep flatMap chain",
      testStackSafetyFlatMapChain,
    ),
    example(
      "test ReleasableResource[Try] multi-combinator pipeline: flatMap.map.evalMap.onFinalizeCase.handleErrorWith.use",
      testMultiCombinatorPipelineSuccess,
    ),
    example(
      "test ReleasableResource[Try] multi-combinator pipeline: eval failure recovered by handleErrorWith then map.evalTap.use",
      testMultiCombinatorPipelineErrorPath,
    ),
    property(
      "test deprecated ResourceMaker.tryResourceMaker still works (new lazy semantics)",
      testDeprecatedTryResourceMaker,
    ),
    example(
      "test deprecated ResourceMaker.tryResourceMaker: release error now propagates",
      testDeprecatedTryResourceMakerReleaseError,
    ),
  )

  private def exitCaseName(exitCase: ReleasableResource.ExitCase): String =
    exitCase match {
      case ReleasableResource.ExitCase.Completed => "Completed"
      case ReleasableResource.ExitCase.Errored(err) => s"Errored(${err.toString})"
      case ReleasableResource.ExitCase.Canceled => "Canceled"
    }

  def testLazyAcquisitionAndReuse: Result = {
    var acquireCount = 0 // scalafix:ok DisableSyntax.var
    var releaseCount = 0 // scalafix:ok DisableSyntax.var

    val resource =
      ReleasableResource.make[Try, Int](Try {
        acquireCount += 1
        acquireCount
      })(_ =>
        Try {
          releaseCount += 1
        }
      )

    val beforeUse = List(
      (acquireCount ==== 0).log("acquired before use"),
      (releaseCount ==== 0).log("released before use"),
    )

    val firstResult  = resource.use(n => Try(n))
    val afterFirst   = List(
      (firstResult ==== Success(1)).log("first use result"),
      (acquireCount ==== 1).log("acquireCount after first use"),
      (releaseCount ==== 1).log("releaseCount after first use"),
    )
    val secondResult = resource.use(n => Try(n))
    val afterSecond  = List(
      (secondResult ==== Success(2)).log("second use result (fresh acquisition)"),
      (acquireCount ==== 2).log("acquireCount after second use"),
      (releaseCount ==== 2).log("releaseCount after second use"),
    )

    Result.all(beforeUse ++ afterFirst ++ afterSecond)
  }

  def testLifoReleaseAndCompletedExitCase: Result = {
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

    val combined =
      for {
        a <- resource("a")
        b <- resource("b")
        c <- resource("c")
      } yield s"$a$b$c"

    val result = combined.use(all => Try(all))

    Result.all(
      List(
        (result ==== Success("abc")).log("use result"),
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

  def testErroredExitCaseOnUseFailure: Result = {
    var exitCases = Vector.empty[String] // scalafix:ok DisableSyntax.var

    val resource =
      ReleasableResource.makeCase(Try(1)) { (_, exitCase) =>
        Try {
          exitCases = exitCases :+ exitCaseName(exitCase)
        }
      }

    val result = resource.use(_ => Failure[Int](TestException(123)))

    Result.all(
      List(
        (result ==== Failure(TestException(123))).log("use result"),
        (exitCases ==== Vector(s"Errored(${TestException(123).toString})")).log("exit case seen by release"),
      )
    )
  }

  def testOnFinalizeCaseOrderAndExitCase: Result = {
    var eventLog = Vector.empty[String] // scalafix:ok DisableSyntax.var

    val resource =
      ReleasableResource
        .makeCase(Try {
          eventLog = eventLog :+ "acquire"
          1
        }) { (_, exitCase) =>
          Try {
            eventLog = eventLog :+ s"release:${exitCaseName(exitCase)}"
          }
        }
        .onFinalizeCase(exitCase =>
          Try {
            eventLog = eventLog :+ s"onFinalize:${exitCaseName(exitCase)}"
          }
        )

    val result = resource.use(n => Try(n))

    Result.all(
      List(
        (result ==== Success(1)).log("use result"),
        (eventLog ==== Vector("acquire", "release:Completed", "onFinalize:Completed"))
          .log("onFinalizeCase runs after the resource's own release"),
      )
    )
  }

  def testReleaseErrorPropagatedOnUseSuccess: Result = {
    val resource =
      ReleasableResource.make[Try, Int](Try(1))(_ => Failure[Unit](TestException(1)))

    val result = resource.use(n => Try(n))

    (result ==== Failure(TestException(1))).log("release error should propagate when use succeeded")
  }

  def testReleaseErrorSuppressedUnderUseError: Result = {
    val resource =
      ReleasableResource.make[Try, Int](Try(1))(_ => Failure[Unit](TestException(1)))

    val result = resource.use(_ => Failure[Int](TestException(2)))

    result match {
      case Failure(err) =>
        Result.all(
          List(
            (err ==== TestException(2)).log("use error should win"),
            Result
              .assert(err.getSuppressed.toList.exists {
                case TestException(1) => true
                case _ => false
              })
              .log(
                s"release error should be suppressed under the use error but suppressed = ${err.getSuppressed.toList.toString}"
              ),
          )
        )
      case Success(n) =>
        Result.failure.log(s"Failure was expected but got Success(${n.toString})")
    }
  }

  def testAttemptScope: Result = {
    var released = 0 // scalafix:ok DisableSyntax.var

    val failingAcquisition =
      ReleasableResource.make[Try, Int](Failure[Int](TestException(1)))(_ =>
        Try {
          released += 1
        }
      )

    val attempted = failingAcquisition.attempt.use(either => Try(either))

    val acquisitionCovered = List(
      (attempted ==== Success(Left(TestException(1)))).log("attempt should cover acquisition errors"),
      (released ==== 0).log("nothing was acquired so nothing should be released"),
    )

    val useNotCovered =
      ReleasableResource.pure[Try, Int](1).attempt.use(_ => Failure[Int](TestException(2)))

    Result.all(
      acquisitionCovered :+
        (useNotCovered ==== Failure(TestException(2))).log("attempt should not cover errors from the use function")
    )
  }

  def testHandleErrorWithAmbientScope: Result = {
    var eventLog = Vector.empty[String] // scalafix:ok DisableSyntax.var

    val acquired =
      ReleasableResource.makeCase(Try {
        eventLog = eventLog :+ "acquire:a"
        "a"
      }) { (n, exitCase) =>
        Try {
          eventLog = eventLog :+ s"release:$n:${exitCaseName(exitCase)}"
        }
      }

    val combined =
      acquired
        .flatMap(_ => ReleasableResource.eval[Try, Int](Failure[Int](TestException(3))))
        .handleErrorWith(_ => ReleasableResource.pure[Try, Int](99))

    val result = combined.use(n => Try(n))

    Result.all(
      List(
        (result ==== Success(99)).log("handler result should be used"),
        (eventLog ==== Vector("acquire:a", "release:a:Completed"))
          .log("already-acquired finalizer stays in the ambient scope and runs at the end"),
      )
    )
  }

  def testDerivedCombinators: Result = {
    var eventLog = Vector.empty[String] // scalafix:ok DisableSyntax.var

    def base(n: Int): ReleasableResource[Try, Int] =
      ReleasableResource.make[Try, Int](Try {
        eventLog = eventLog :+ s"acquire:${n.toString}"
        n
      })(m =>
        Try {
          eventLog = eventLog :+ s"release:${m.toString}"
        }
      )

    val evalMapResult  = base(1).evalMap(n => Try(n + 1)).use(n => Try(n))
    val evalTapResult  = base(2)
      .evalTap(n =>
        Try {
          eventLog = eventLog :+ s"evalTap:${n.toString}"
        }
      )
      .use(n => Try(n))
    val flatTapResult  = base(3).flatTap(n => base(n + 10)).use(n => Try(n))
    val surroundResult = base(4).surround(Try(42))
    val useUnitResult  = base(5).use_

    Result.all(
      List(
        (evalMapResult ==== Success(2)).log("evalMap"),
        (evalTapResult ==== Success(2)).log("evalTap keeps the value"),
        (flatTapResult ==== Success(3)).log("flatTap keeps the value"),
        (surroundResult ==== Success(42)).log("surround"),
        (useUnitResult ==== Success(())).log("use_"),
        (eventLog ==== Vector(
          "acquire:1",
          "release:1",
          "acquire:2",
          "evalTap:2",
          "release:2",
          "acquire:3",
          "acquire:13",
          "release:13",
          "release:3",
          "acquire:4",
          "release:4",
          "acquire:5",
          "release:5",
        )).log("acquisition/release event log"),
      )
    )
  }

  def testFromAutoCloseable: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {
      import effectie.instances.tries.fxCtor._

      val testResource = TestResource()

      val result =
        ReleasableResource
          .fromAutoCloseable[Try, TestResource](Try(testResource))
          .use { resource =>
            Try(content.foreach(resource.write))
          }

      Result.all(
        List(
          (result ==== Success(())).log("use result"),
          (testResource.content ==== content).log("content"),
          (testResource.closeStatus ==== TestableResource.CloseStatus.closed).log("closeStatus"),
        )
      )
    }

  def testStackSafetyFlatMapChain: Result = {
    val depth = 10000

    val chained =
      (1 to depth).foldLeft(ReleasableResource.pure[Try, Int](0)) { (resource, _) =>
        resource.flatMap(n => ReleasableResource.pure[Try, Int](n + 1))
      }

    (chained.use(n => Try(n)) ==== Success(depth)).log(s"${depth.toString} deep flatMap chain")
  }

  @nowarn("cat=deprecation")
  def testDeprecatedTryResourceMaker: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {
      import effectie.instances.tries.fx._

      implicit val resourceMaker: ResourceMaker[Try] = ResourceMaker.tryResourceMaker

      Result.all(
        List(
          ResourceMakerSpec
            .testForAutoCloseable[Try](TestResource.apply)(
              content,
              _ => Try(()),
              none,
            )
            .fold(err => Result.failure.log(s"Unexpected error: ${err.toString}"), identity),
          ResourceMakerSpec
            .testForMake[Try](TestResourceNoAutoClose.apply)(
              _.release(),
              content,
              _ => Try(Result.success),
              none,
            )
            .fold(err => Result.failure.log(s"Unexpected error: ${err.toString}"), identity),
        )
      )
    }

  @nowarn("cat=deprecation")
  def testDeprecatedTryResourceMakerReleaseError: Result = {
    implicit val resourceMaker: ResourceMaker[Try] = ResourceMaker.tryResourceMaker

    val testResource = TestResourceNoAutoClose()

    val result =
      resourceMaker
        .make(Try(testResource)) { resource =>
          Try {
            resource.release()
            throw TestException(999) // scalafix:ok DisableSyntax.throw
          }
        }
        .use(_ => Try(()))

    Result.all(
      List(
        (result ==== Failure(TestException(999)))
          .log("release error should now propagate (2.5.0 semantics change)"),
        (testResource.closeStatus ==== TestableResource.CloseStatus.closed).log("closeStatus"),
      )
    )
  }

  def testMultiCombinatorPipelineSuccess: Result = {
    var eventLog = Vector.empty[String] // scalafix:ok DisableSyntax.var

    def res(name: String): ReleasableResource[Try, String] =
      ReleasableResource.makeCase(Try {
        eventLog = eventLog :+ s"acquire:$name"
        name
      }) { (n, exitCase) =>
        Try {
          eventLog = eventLog :+ s"release:$n:${exitCaseName(exitCase)}"
        }
      }

    val pipeline =
      res("a")
        .flatMap(a => res("b").map(b => a + b))
        .evalMap(ab =>
          Try {
            eventLog = eventLog :+ s"evalMap:$ab"
            ab.toUpperCase
          }
        )
        .onFinalizeCase(exitCase =>
          Try {
            eventLog = eventLog :+ s"onFinalize:${exitCaseName(exitCase)}"
          }
        )
        .handleErrorWith(_ => res("x"))

    val result = pipeline.use(v =>
      Try {
        eventLog = eventLog :+ s"use:$v"
        v
      }
    )

    Result.all(
      List(
        (result ==== Success("AB")).log("use result"),
        (eventLog ==== Vector(
          "acquire:a",
          "acquire:b",
          "evalMap:ab",
          "use:AB",
          "release:b:Completed",
          "release:a:Completed",
          "onFinalize:Completed",
        )).log("multi-combinator pipeline event log"),
      )
    )
  }

  def testMultiCombinatorPipelineErrorPath: Result = {
    var eventLog = Vector.empty[String] // scalafix:ok DisableSyntax.var

    def res(name: String): ReleasableResource[Try, String] =
      ReleasableResource.makeCase(Try {
        eventLog = eventLog :+ s"acquire:$name"
        name
      }) { (n, exitCase) =>
        Try {
          eventLog = eventLog :+ s"release:$n:${exitCaseName(exitCase)}"
        }
      }

    val pipeline =
      res("a")
        .flatMap(_ => ReleasableResource.eval[Try, String](Failure[String](TestException(1))))
        .handleErrorWith(_ => res("fallback"))
        .map(v => s"got:$v")
        .evalTap(v =>
          Try {
            eventLog = eventLog :+ s"evalTap:$v"
          }
        )

    val result = pipeline.use(v =>
      Try {
        eventLog = eventLog :+ s"use:$v"
        v
      }
    )

    Result.all(
      List(
        (result ==== Success("got:fallback")).log("use result"),
        (eventLog ==== Vector(
          "acquire:a",
          "acquire:fallback",
          "evalTap:got:fallback",
          "use:got:fallback",
          "release:fallback:Completed",
          "release:a:Completed",
        )).log("error-path pipeline event log"),
      )
    )
  }

}
