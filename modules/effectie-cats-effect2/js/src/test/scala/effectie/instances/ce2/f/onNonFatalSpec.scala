package effectie.instances.ce2.f

import cats.effect.IO
import cats.syntax.all._
import effectie.SomeControlThrowable
import effectie.core._
import effectie.syntax.fx._
import effectie.testing.FutureTools
import munit.Assertions

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration
import scala.util.control.{ControlThrowable, NonFatal}

import scala.concurrent.duration._

/** @author Kevin Lee
  * @since 2020-08-17
  */
class onNonFatalSpec extends munit.FunSuite with FutureTools {
  implicit val ec: ExecutionContext = globalExecutionContext

  override val munitTimeout: FiniteDuration = 200.milliseconds

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor, A](a: => A): F[A] =
    effectOf[F](a)

  import effectie.instances.ce2.f.canHandleError.syncCanHandleError
  import effectie.instances.ce2.f.fxCtor.syncFxCtor

  test("test OnNonFatal[IO].onNonFatalWith should do something for NonFatal") {

    val expectedException = new RuntimeException("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedException))
    val expected          = 123.some
    var actual            = none[Int] // scalafix:ok DisableSyntax.var

    try {
      OnNonFatal[IO]
        .onNonFatalWith(fa) {
          case NonFatal(`expectedException`) =>
            IO.delay {
              actual = expected
            } *> IO.unit
        }
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}"): Unit
        }
        .recover {
          case NonFatal(`expectedException`) =>
            Assertions.assertEquals(actual, expected)
        }
        .unsafeToFuture()
    } catch {
      case ex: Throwable =>
        ex
    }

  }

  test("test OnNonFatal[IO].onNonFatalWith should not do anything for Fatal") {

    val expectedException = SomeControlThrowable("Something's wrong")
    val fa                = run[IO, Int](throwThrowable[Int](expectedException))
    var actual            = none[Int] // scalafix:ok DisableSyntax.var

    try {
      OnNonFatal[IO]
        .onNonFatalWith(fa) {
          case NonFatal(`expectedException`) =>
            IO.delay {
              actual = 123.some
              ()
            } *> IO.unit
        }
        .map { actual =>
          Assertions.fail(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        }
        .unsafeToFuture()
    } catch {
      case ex: ControlThrowable =>
        Assertions.assertEquals(ex, expectedException)

      case ex: Throwable =>
        Assertions.fail(s"Unexpected Throwable: ${ex.toString}")
    }

  }

  test("test OnNonFatal[IO].onNonFatalWith should not do anything for the successful result") {

    val expectedResult = 999
    val fa             = run[IO, Int](expectedResult)

    val expected = none[Int]
    var actual   = none[Int] // scalafix:ok DisableSyntax.var

    OnNonFatal[IO]
      .onNonFatalWith(fa) {
        case NonFatal(_) =>
          IO.delay {
            actual = 123.some
          } *> IO.unit
      }
      .map { actualResult =>
        Assertions.assertEquals(actualResult, expectedResult)
        Assertions.assertEquals(actual, expected)
      }
      .unsafeToFuture()

  }

}
