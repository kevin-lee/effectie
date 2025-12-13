package effectie.instances.ce2.f

import cats.effect.IO
import cats.syntax.all._
import effectie.SomeControlThrowable
import effectie.core._
import effectie.syntax.fx._
import extras.concurrent.testing.types.ErrorLogger
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
    import effectie.instances.ce2.f.fxCtor.syncFxCtor
    import effectie.instances.ce2.f.canHandleError.syncCanHandleError

    def testOnNonFatal_IO_onNonFatalWithShouldRecoverFromNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedException))
      val expected          = 123.some
      var actual            = none[Int] // scalafix:ok DisableSyntax.var

      val result =
        try {
          val r = OnNonFatal[IO]
            .onNonFatalWith(fa) {
              case NonFatal(`expectedException`) =>
                IO.delay {
                  actual = expected
                } *> IO.unit
            }
            .unsafeRunSync()
          new AssertionError(s"Should have thrown an exception, but it was ${r.toString}.")
        } catch {
          case ex: Throwable =>
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
    def testOnNonFatal_IO_onNonFatalWithShouldNotCatchFatal: Result = {

      val expectedException = SomeControlThrowable("Something's wrong")
      val fa                = run[IO, Int](throwThrowable[Int](expectedException))
      var actual            = none[Int] // scalafix:ok DisableSyntax.var

      val io = OnNonFatal[IO].onNonFatalWith(fa) {
        case NonFatal(`expectedException`) =>
          IO.delay {
            actual = 123.some
            ()
          } *> IO.unit
      }
      try {
        val actual = io.unsafeRunSync()
        Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
      } catch {
        case ex: ControlThrowable =>
          Result.all(
            List(
              actual ==== none[Int],
              ex ==== expectedException,
            )
          )

        case ex: Throwable =>
          Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
      }

    }

    def testOnNonFatal_IO_onNonFatalWithShouldReturnSuccessfulResult: Result = {

      val expectedResult = 999
      val fa             = run[IO, Int](expectedResult)

      val expected = none[Int]
      var actual   = none[Int] // scalafix:ok DisableSyntax.var

      val result = OnNonFatal[IO]
        .onNonFatalWith(fa) {
          case NonFatal(_) =>
            IO.delay {
              actual = 123.some
            } *> IO.unit
        }
        .unsafeRunSync()

      Result.all(
        List(
          result ==== expectedResult,
          actual ==== expected,
        )
      )
    }

  }

}
