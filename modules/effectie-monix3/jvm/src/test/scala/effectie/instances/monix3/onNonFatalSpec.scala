package effectie.instances.monix3

import cats.syntax.all._
import effectie.SomeControlThrowable
import effectie.core._
import effectie.syntax.fx._
import extras.concurrent.testing.types.ErrorLogger
import hedgehog._
import hedgehog.runner._
import monix.eval.Task

import scala.util.control.{ControlThrowable, NonFatal}

/** @author Kevin Lee
  * @since 2020-08-17
  */
object onNonFatalSpec extends Properties {
  import monix.execution.Scheduler.Implicits.global

  implicit val errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

  override def tests: List[Test] = taskSpecs

  /* Task */
  val taskSpecs = List(
    example(
      "test OnNonFatal[Task].onNonFatalWith should do something for NonFatal",
      TaskSpec.testOnNonFatal_Task_onNonFatalWithShouldRecoverFromNonFatal,
    ),
    example(
      "test OnNonFatal[Task].onNonFatalWith should not do anything for Fatal",
      TaskSpec.testOnNonFatal_Task_onNonFatalWithShouldNotCatchFatal,
    ),
    example(
      "test OnNonFatal[Task].onNonFatalWith should not do anything for the successful result",
      TaskSpec.testOnNonFatal_Task_onNonFatalWithShouldReturnSuccessfulResult,
    ),
  )

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable // scalafix:ok DisableSyntax.throw

  def run[F[*]: FxCtor, A](a: => A): F[A] =
    effectOf[F](a)

  object TaskSpec {

    import effectie.instances.monix3.canHandleError.taskCanHandleError
    import effectie.instances.monix3.fxCtor.taskFxCtor

    def testOnNonFatal_Task_onNonFatalWithShouldRecoverFromNonFatal: Result = {

      val expectedException = new RuntimeException("Something's wrong")
      val fa                = run[Task, Int](throwThrowable[Int](expectedException))
      val expected          = 123.some
      var actual            = none[Int] // scalafix:ok DisableSyntax.var

      val result =
        try {
          val r = OnNonFatal[Task]
            .onNonFatalWith(fa) {
              case NonFatal(`expectedException`) =>
                Task.delay {
                  actual = expected
                } *> Task.unit
            }
            .runSyncUnsafe()
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
    def testOnNonFatal_Task_onNonFatalWithShouldNotCatchFatal: Result = {

      val expectedException = SomeControlThrowable("Something's wrong")
      val fa                = run[Task, Int](throwThrowable[Int](expectedException))
      var actual            = none[Int] // scalafix:ok DisableSyntax.var

      val io = OnNonFatal[Task].onNonFatalWith(fa) {
        case NonFatal(`expectedException`) =>
          Task.delay {
            actual = 123.some
            ()
          } *> Task.unit
      }
      try {
        val actual = io.runSyncUnsafe()
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

    def testOnNonFatal_Task_onNonFatalWithShouldReturnSuccessfulResult: Result = {

      val expectedResult = 999
      val fa             = run[Task, Int](expectedResult)

      val expected = none[Int]
      var actual   = none[Int] // scalafix:ok DisableSyntax.var

      val result = OnNonFatal[Task]
        .onNonFatalWith(fa) {
          case NonFatal(_) =>
            Task.delay {
              actual = 123.some
            } *> Task.unit
        }
        .runSyncUnsafe()

      Result.all(
        List(
          result ==== expectedResult,
          actual ==== expected,
        )
      )
    }

  }

}
