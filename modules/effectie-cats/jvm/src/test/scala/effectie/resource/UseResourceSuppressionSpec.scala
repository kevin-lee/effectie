package effectie.resource

import effectie.resource.data.TestErrors.TestException
import hedgehog._
import hedgehog.runner._

import scala.util.{Failure, Success, Try}

/** Exact ordering of the suppressed exceptions attached by the UseResource interpreter (`suppressOnto`).
  *
  * This is JVM-only on purpose: `Throwable.addSuppressed` / `getSuppressed` preserving insertion order is a JVM
  * guarantee, and it is not verified for Scala.js and Scala Native. The cross-platform
  * [[UseResourceTryInterpreterSpec]] asserts membership only.
  *
  * @author Kevin Lee
  * @since 2026-08-02
  */
object UseResourceSuppressionSpec extends Properties {

  override def tests: List[Test] = List(
    example(
      "test UseResource[Try] suppressed order: innermost finalizer error is primary, the rest follow in run order",
      testSuppressedOrderOnUseSuccess,
    ),
    example(
      "test UseResource[Try] suppressed order: use error is primary, finalizer errors follow in run order",
      testSuppressedOrderOnUseError,
    ),
  )

  private def threeFailingFinalizers: ReleasableResource[Try, String] = {

    def resource(name: String, error: Throwable): ReleasableResource[Try, String] =
      ReleasableResource.make[Try, String](Try(name))(_ => Failure[Unit](error))

    for {
      a <- resource("a", TestException(1))
      b <- resource("b", TestException(2))
      c <- resource("c", TestException(3))
    } yield s"$a$b$c"
  }

  def testSuppressedOrderOnUseSuccess: Result =
    threeFailingFinalizers.use(all => Try(all)) match {
      case Failure(err) =>
        Result.all(
          List(
            (err ==== TestException(3)).log("the innermost (LIFO-first) finalizer error should be primary"),
            (err.getSuppressed.toList ==== List[Throwable](TestException(2), TestException(1)))
              .log("the remaining finalizer errors should be suppressed in LIFO run order"),
          )
        )
      case Success(value) =>
        Result.failure.log(s"Failure was expected but got Success(${value.toString})")
    }

  def testSuppressedOrderOnUseError: Result =
    threeFailingFinalizers.use(_ => Failure[String](TestException(99))) match {
      case Failure(err) =>
        Result.all(
          List(
            (err ==== TestException(99)).log("the use error should be primary"),
            (err.getSuppressed.toList ==== List[Throwable](TestException(3), TestException(2), TestException(1)))
              .log("all finalizer errors should be suppressed in LIFO run order"),
          )
        )
      case Success(value) =>
        Result.failure.log(s"Failure was expected but got Success(${value.toString})")
    }

}
