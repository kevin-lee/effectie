package effectie.instances.tries

import cats.syntax.all._
import effectie.SomeControlThrowable
import effectie.core._
import hedgehog._
import hedgehog.runner._

import scala.util.control.{ControlThrowable, NonFatal}
import scala.util.{Success, Try}

/** @author Kevin Lee
  * @since 2025-11-16
  */
object onNonFatalSpec extends Properties {

  override def tests: List[Test] =
    trySpecs

  /* Try */
  val trySpecs = List(
    example(
      "test OnNonFatal[Try].onNonFatalWith should do something for NonFatal",
      TrySpec.testOnNonFatal_Try_onNonFatalWithShouldRecoverFromNonFatal,
    ),
    example(
      "test OnNonFatal[Try].onNonFatalWith should not do anything for Fatal",
      TrySpec.testOnNonFatal_Try_onNonFatalWithShouldNotCatchFatal,
    ),
    example(
      "test OnNonFatal[Try].onNonFatalWith should not do anything for the successful result",
      TrySpec.testOnNonFatal_Try_onNonFatalWithShouldReturnSuccessfulResult,
    ),
  )

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor, A](a: => A): F[A] =
    FxCtor[F].effectOf(a)

  object TrySpec {
    import effectie.instances.tries.fxCtor.fxCtorTry
    import effectie.instances.tries.canHandleError.canHandleErrorTry

    def testOnNonFatal_Try_onNonFatalWithShouldRecoverFromNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong 1")
      val fa                = run[Try, Int](throwThrowable[Int](expectedException))
      val expected          = 123.some

      var actual = none[Int] // scalafix:ok DisableSyntax.var

      val result = OnNonFatal[Try]
        .onNonFatalWith(fa) {
          case NonFatal(`expectedException`) =>
            FxCtor[Try].pureOf {
              actual = expected
              ()
            } *> FxCtor[Try].unitOf
        }

      Result.all(
        List(
          result ==== scala.util.Failure(expectedException),
          actual ==== expected,
        )
      )
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testOnNonFatal_Try_onNonFatalWithShouldNotCatchFatal: Result = {

      val expectedException = SomeControlThrowable("Something's wrong 2")

      val expected = none[Int]
      var actual   = none[Int] // scalafix:ok DisableSyntax.var

      try {
        val fa     = run[Try, Int](throwThrowable[Int](expectedException))
        val result = OnNonFatal[Try].onNonFatalWith(fa) {
          case NonFatal(`expectedException`) =>
            FxCtor[Try].pureOf {
              actual = 123.some
              ()
            }
        }
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${result.toString}")
      } catch {
        case ex: ControlThrowable =>
          Result.all(
            List(
              ex ==== expectedException,
              actual ==== expected,
            )
          )

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testOnNonFatal_Try_onNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = run[Try, Int](1)
      val expected = none[Int]
      var actual   = none[Int] // scalafix:ok DisableSyntax.var

      val result = OnNonFatal[Try]
        .onNonFatalWith(fa) {
          case NonFatal(_) =>
            FxCtor[Try].pureOf {
              actual = 123.some
              ()
            }
        }

      Result.all(
        List(
          result ==== Success(1),
          actual ==== expected,
        )
      )
    }

  }

}
