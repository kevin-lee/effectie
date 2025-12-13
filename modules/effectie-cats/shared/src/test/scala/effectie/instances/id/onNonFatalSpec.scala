package effectie.instances.id

import cats._
import cats.syntax.all._
import effectie.SomeControlThrowable
import effectie.core._
import hedgehog._
import hedgehog.runner._

import scala.util.control.{ControlThrowable, NonFatal}

/** @author Kevin Lee
  * @since 2025-11-16
  */
object onNonFatalSpec extends Properties {

  override def tests: List[Test] =
    trySpecs

  /* Id */
  val trySpecs = List(
    example(
      "test OnNonFatal[Id].onNonFatalWith should do something for NonFatal",
      IdSpec.testOnNonFatal_Id_onNonFatalWithShouldRecoverFromNonFatal,
    ),
    example(
      "test OnNonFatal[Id].onNonFatalWith should not do anything for Fatal",
      IdSpec.testOnNonFatal_Id_onNonFatalWithShouldNotCatchFatal,
    ),
    example(
      "test OnNonFatal[Id].onNonFatalWith should not do anything for the successful result",
      IdSpec.testOnNonFatal_Id_onNonFatalWithShouldReturnSuccessfulResult,
    ),
  )

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor, A](a: => A): F[A] =
    FxCtor[F].effectOf(a)

  object IdSpec {

    import effectie.instances.id.fxCtor.idFxCtor
    import effectie.instances.id.canHandleError.idCanHandleError

    def testOnNonFatal_Id_onNonFatalWithShouldRecoverFromNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong 1")

      lazy val fa  = run[Id, Int](throwThrowable[Int](expectedException))
      val expected = 123.some

      var actual = none[Int] // scalafix:ok DisableSyntax.var

      val result =
        try {
          val r: Id[Int] = OnNonFatal[Id]
            .onNonFatalWith(fa) {
              case NonFatal(`expectedException`) =>
                FxCtor[Id].pureOf {
                  actual = expected
                  ()
                } *> FxCtor[Id].unitOf
            }
          new AssertionError(s"Should have thrown NonFatal, but it was $r")
        } catch {
          case NonFatal(ex) =>
            ex
        }

      Result.all(
        List(
          result ==== expectedException,
          actual ==== expected,
        )
      )
    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testOnNonFatal_Id_onNonFatalWithShouldNotCatchFatal: Result = {

      val expectedException = SomeControlThrowable("Something's wrong 2")

      val expected = none[Int]
      var actual   = none[Int] // scalafix:ok DisableSyntax.var

      try {
        val fa              = run[Id, Int](throwThrowable[Int](expectedException))
        val result: Id[Int] = OnNonFatal[Id].onNonFatalWith(fa) {
          case NonFatal(`expectedException`) =>
            FxCtor[Id].pureOf {
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

    def testOnNonFatal_Id_onNonFatalWithShouldReturnSuccessfulResult: Result = {

      val fa       = run[Id, Int](1)
      val expected = none[Int]
      var actual   = none[Int] // scalafix:ok DisableSyntax.var

      val result: Id[Int] = OnNonFatal[Id]
        .onNonFatalWith(fa) {
          case NonFatal(_) =>
            FxCtor[Id].pureOf {
              actual = 123.some
              ()
            }
        }

      Result.all(
        List(
          result ==== 1,
          actual ==== expected,
        )
      )
    }

  }

}
