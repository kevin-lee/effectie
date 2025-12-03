package effectie.instances.ce3

import cats.effect.IO
import cats.syntax.all._
import effectie.SomeControlThrowable
import effectie.core._
import effectie.syntax.fx._
import extras.concurrent.testing.types.ErrorLogger
import extras.hedgehog.ce3.syntax.runner._
import hedgehog._
import hedgehog.runner._

import scala.util.control.{ControlThrowable, NonFatal}

/** @author Kevin Lee
  * @since 2020-08-17
  */
object onNonFatalSpec extends Properties {
  implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = ioSpecs

  /* IO */
  val ioSpecs = List(
    example(
      "test OnNonFatal[IO].onNonFatalWith should do something for NonFatal",
      IOSpec.testOnNonFatal_IO_onNonFatalWithShouldRecoverFromNonFatal,
    ),
    example(
      "test OnNonFatal[IO].onNonFatalWith should not do anything for Fatal",
      IOSpec.testOnNonFatal_IO_onNonFatalWithShouldNotCatchFatal,
    ),
    example(
      "test OnNonFatal[IO].onNonFatalWith should not do anything for the successful result",
      IOSpec.testOnNonFatal_IO_onNonFatalWithShouldReturnSuccessfulResult,
    ),
  )

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor, A](a: => A): F[A] =
    effectOf[F](a)

  object IOSpec {

    import effectie.instances.ce3.canHandleError.ioCanHandleError
    import effectie.instances.ce3.fxCtor.ioFxCtor

    def testOnNonFatal_IO_onNonFatalWithShouldRecoverFromNonFatal: Result = runIO {

      val expectedExpcetion = new RuntimeException("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
      val expected          = 123.some
      var actual            = none[Int] // scalafix:ok DisableSyntax.var

      try {
        OnNonFatal[IO]
          .onNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) =>
              IO.delay {
                actual = expected
              } *> IO.unit
          }
          .map { actual =>
            Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
          }
          .recover {
            case NonFatal(`expectedExpcetion`) =>
              actual ==== expected
          }
      } catch {
        case ex: Throwable =>
          throw ex // scalafix:ok DisableSyntax.throw
      }

    }

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    def testOnNonFatal_IO_onNonFatalWithShouldNotCatchFatal: Result = {
      val expectedExpcetion = SomeControlThrowable("Something's wrong")

      var actual = none[Int] // scalafix:ok DisableSyntax.var

      try {
        runIO {

          val fa = run[IO, Int](throwThrowable[Int](expectedExpcetion))

          OnNonFatal[IO]
            .onNonFatalWith(fa) {
              case NonFatal(`expectedExpcetion`) =>
                IO.delay {
                  actual = 123.some
                  ()
                } *> IO.unit
            }
            .map { actual =>
              Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
            }

        }
      } catch {
        case ex: ControlThrowable =>
          Result.all(
            List(
              actual ==== none[Int],
              ex ==== expectedExpcetion,
            )
          )

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }
    }

    def testOnNonFatal_IO_onNonFatalWithShouldReturnSuccessfulResult: Result = runIO {

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
          Result.all(
            List(
              actualResult ==== expectedResult,
              actual ==== expected,
            )
          )
        }

    }

  }

}
