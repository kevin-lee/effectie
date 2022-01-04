package effectie.cats

import cats.data.EitherT
import cats.effect.*
import cats.syntax.all.*
import cats.{Eq, Functor, Id, Monad}
import effectie.testing.tools.*
import effectie.cats.Fx.given
import effectie.testing.types.{SomeError, SomeThrowableError}
import effectie.{Fx, SomeControlThrowable}
import extras.concurrent.testing.ConcurrentSupport
import extras.concurrent.testing.types.{ErrorLogger, WaitFor}
import hedgehog.*
import hedgehog.runner.*

import scala.concurrent.Await
import scala.util.control.{ControlThrowable, NonFatal}

/** @author Kevin Lee
  * @since 2020-12-06
  */
object FxSpec extends Properties {
  override def tests: List[Test] = ioSpecs ++ futureSpecs ++ idSpecs

  /* IO */
  private val ioSpecs = List(
    property("test Fx[IO].effectOf", IoSpec.testEffectOf),
    property("test Fx[IO].pureOf", IoSpec.testPureOf),
    example("test Fx[IO].unitOf", IoSpec.testUnitOf),
    example("test Fx[IO].errorOf", IoSpec.testErrorOf),
  ) ++
    IoSpec.testMonadLaws ++
    List(
      example(
        "test Fx[IO]catchNonFatalThrowable should catch NonFatal",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalThrowableShouldCatchNonFatal
      ),
      example(
        "test Fx[IO]catchNonFatalThrowable should not catch Fatal",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalThrowableShouldNotCatchFatal
      ),
      example(
        "test Fx[IO]catchNonFatalThrowable should return the successful result",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalThrowableShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[IO]catchNonFatal should catch NonFatal",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalShouldCatchNonFatal
      ),
      example(
        "test Fx[IO]catchNonFatal should not catch Fatal",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalShouldNotCatchFatal
      ),
      example(
        "test Fx[IO]catchNonFatal should return the successful result",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[IO]catchNonFatalEither should catch NonFatal",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalEitherShouldCatchNonFatal
      ),
      example(
        "test Fx[IO]catchNonFatalEither should not catch Fatal",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalEitherShouldNotCatchFatal
      ),
      example(
        "test Fx[IO]catchNonFatalEither should return the successful result",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalEitherShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[IO]catchNonFatalEither should return the failed result",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalEitherShouldReturnFailedResult
      ),
      example(
        "test Fx[IO]catchNonFatalEitherT should catch NonFatal",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalEitherTShouldCatchNonFatal
      ),
      example(
        "test Fx[IO]catchNonFatalEitherT should not catch Fatal",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalEitherTShouldNotCatchFatal
      ),
      example(
        "test Fx[IO]catchNonFatalEitherT should return the successful result",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalEitherTShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[IO]catchNonFatalEitherT should return the failed result",
        IoSpec.CanCatchSpec.testCanCatch_IO_catchNonFatalEitherTShouldReturnFailedResult
      ),
    ) ++
    /* IO */
    List(
      example(
        "test Fx[IO].handleNonFatalWith should handle NonFatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalWithShouldHandleNonFatalWith
      ),
      example(
        "test Fx[IO].handleNonFatalWith should not handle Fatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalWithShouldNotHandleFatalWith
      ),
      example(
        "test Fx[IO].handleNonFatalWith should return the successful result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalWithShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[IO].handleNonFatalWithEither should handle NonFatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalWithEitherShouldHandleNonFatalWith
      ),
      example(
        "test Fx[IO].handleNonFatalWithEither should not handle Fatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalWithEitherShouldNotHandleFatalWith
      ),
      example(
        "test Fx[IO].handleNonFatalWithEither should return the successful result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalWithEitherShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[IO].handleNonFatalWithEither should return the failed result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalWithEitherShouldReturnFailedResult
      ),
      example(
        "test Fx[IO].handleEitherNonFatalWith should handle NonFatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherNonFatalWithShouldHandleNonFatalWith
      ),
      example(
        "test Fx[IO].handleEitherNonFatalWith should not handle Fatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherNonFatalWithShouldNotHandleFatalWith
      ),
      example(
        "test Fx[IO].handleEitherNonFatalWith should return the successful result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherNonFatalWithShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[IO].handleEitherNonFatalWith should return the failed result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherNonFatalWithShouldReturnFailedResult
      ),
      example(
        "test Fx[IO].handleEitherTNonFatalWith should handle NonFatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherTNonFatalWithShouldHandleNonFatalWith
      ),
      example(
        "test Fx[IO].handleEitherTNonFatalWith should not handle Fatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherTNonFatalWithShouldNotHandleFatalWith
      ),
      example(
        "test Fx[IO].handleEitherTNonFatalWith should return the successful result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherTNonFatalWithShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[IO].handleEitherTNonFatalWith should return the failed result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherTNonFatalWithShouldReturnFailedResult
      ),
      example(
        "test Fx[IO].handleNonFatal should handle NonFatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalShouldHandleNonFatal
      ),
      example(
        "test Fx[IO].handleNonFatal should not handle Fatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalShouldNotHandleFatal
      ),
      example(
        "test Fx[IO].handleNonFatal should return the successful result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[IO].handleNonFatalEither should handle NonFatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalEitherShouldHandleNonFatal
      ),
      example(
        "test Fx[IO].handleNonFatalEither should not handle Fatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalEitherShouldNotHandleFatal
      ),
      example(
        "test Fx[IO].handleNonFatalEither should return the successful result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalEitherShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[IO].handleNonFatalEither should return the failed result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleNonFatalEitherShouldReturnFailedResult
      ),
      example(
        "test Fx[IO].handleEitherNonFatal should handle NonFatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherNonFatalShouldHandleNonFatal
      ),
      example(
        "test Fx[IO].handleEitherNonFatal should not handle Fatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherNonFatalShouldNotHandleFatal
      ),
      example(
        "test Fx[IO].handleEitherNonFatal should return the successful result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherNonFatalShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[IO].handleEitherNonFatal should return the failed result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherNonFatalShouldReturnFailedResult
      ),
      example(
        "test Fx[IO].handleEitherTNonFatal should handle NonFatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherTNonFatalShouldHandleNonFatal
      ),
      example(
        "test Fx[IO].handleEitherTNonFatal should not handle Fatal",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherTNonFatalShouldNotHandleFatal
      ),
      example(
        "test Fx[IO].handleEitherTNonFatal should return the successful result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherTNonFatalShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[IO].handleEitherTNonFatal should return the failed result",
        IoSpec.CanHandleErrorSpec.testCanHandleError_IO_handleEitherTNonFatalShouldReturnFailedResult
      ),
    ) ++ List(
      example(
        "test Fx[IO].recoverFromNonFatalWith should catch NonFatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalWithShouldRecoverFromNonFatal
      ),
      example(
        "test Fx[IO].recoverFromNonFatalWith should not catch Fatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalWithShouldNotCatchFatal
      ),
      example(
        "test Fx[IO].recoverFromNonFatalWith should return the successful result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalWithShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[IO].recoverFromNonFatalWithEither should catch NonFatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal
      ),
      example(
        "test Fx[IO].recoverFromNonFatalWithEither should not catch Fatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldNotCatchFatal
      ),
      example(
        "test Fx[IO].recoverFromNonFatalWithEither should return the successful result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[IO].recoverFromNonFatalWithEither should return the failed result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnFailedResult
      ),
      example(
        "test Fx[IO].recoverEitherFromNonFatalWith should catch NonFatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal
      ),
      example(
        "test Fx[IO].recoverEitherFromNonFatalWith should not catch Fatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherFromNonFatalWithShouldNotCatchFatal
      ),
      example(
        "test Fx[IO].recoverEitherFromNonFatalWith should return the successful result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[IO].recoverEitherFromNonFatalWith should return the failed result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherFromNonFatalWithShouldReturnFailedResult
      ),
      example(
        "test Fx[IO].recoverEitherTFromNonFatalWith should catch NonFatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal
      ),
      example(
        "test Fx[IO].recoverEitherTFromNonFatalWith should not catch Fatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldNotCatchFatal
      ),
      example(
        "test Fx[IO].recoverEitherTFromNonFatalWith should return the successful result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[IO].recoverEitherTFromNonFatalWith should return the failed result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnFailedResult
      ),
      example(
        "test Fx[IO].recoverFromNonFatal should catch NonFatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalShouldRecoverFromNonFatal
      ),
      example(
        "test Fx[IO].recoverFromNonFatal should not catch Fatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalShouldNotCatchFatal
      ),
      example(
        "test Fx[IO].recoverFromNonFatal should return the successful result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[IO].recoverFromNonFatalEither should catch NonFatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldRecoverFromNonFatal
      ),
      example(
        "test Fx[IO].recoverFromNonFatalEither should not catch Fatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldNotCatchFatal
      ),
      example(
        "test Fx[IO].recoverFromNonFatalEither should return the successful result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[IO].recoverFromNonFatalEither should return the failed result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverFromNonFatalEitherShouldReturnFailedResult
      ),
      example(
        "test Fx[IO].recoverEitherFromNonFatal should catch NonFatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherFromNonFatalShouldRecoverFromNonFatal
      ),
      example(
        "test Fx[IO].recoverEitherFromNonFatal should not catch Fatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherFromNonFatalShouldNotCatchFatal
      ),
      example(
        "test Fx[IO].recoverEitherFromNonFatal should return the successful result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherFromNonFatalShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[IO].recoverEitherFromNonFatal should return the failed result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherFromNonFatalShouldReturnFailedResult
      ),
      example(
        "test Fx[IO].recoverEitherTFromNonFatal should catch NonFatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldRecoverFromNonFatal
      ),
      example(
        "test Fx[IO].recoverEitherTFromNonFatal should not catch Fatal",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldNotCatchFatal
      ),
      example(
        "test Fx[IO].recoverEitherTFromNonFatal should return the successful result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[IO].recoverEitherTFromNonFatal should return the failed result",
        IoSpec.CanRecoverSpec.testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnFailedResult
      )
    )

  /* Future */
  private val futureSpecs = List(
    property("test Fx[Future].effectOf", FutureSpec.testEffectOf),
    property("test Fx[Future].pureOf", FutureSpec.testPureOf),
    example("test Fx[Future].unitOf", FutureSpec.testUnitOf),
    example("test Fx[Future].errorOf", FutureSpec.testErrorOf),
  ) ++
    FutureSpec.testMonadLaws ++
    List(
      example(
        "test Fx[Future]catchNonFatalThrowable should catch NonFatal",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalThrowableShouldCatchNonFatal
      ),
      example(
        "test Fx[Future]catchNonFatalThrowable should return the successful result",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalThrowableShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future]catchNonFatal should catch NonFatal",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalShouldCatchNonFatal
      ),
      example(
        "test Fx[Future]catchNonFatal should return the successful result",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future]catchNonFatalEither should catch NonFatal",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalEitherShouldCatchNonFatal
      ),
      example(
        "test Fx[Future]catchNonFatalEither should return the successful result",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalEitherShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future]catchNonFatalEither should return the failed result",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalEitherShouldReturnFailedResult
      ),
      example(
        "test Fx[Future]catchNonFatalEitherT should catch NonFatal",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalEitherTShouldCatchNonFatal
      ),
      example(
        "test Fx[Future]catchNonFatalEitherT should return the successful result",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalEitherTShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future]catchNonFatalEitherT should return the failed result",
        FutureSpec.CanCatchSpec.testCanCatch_Future_catchNonFatalEitherTShouldReturnFailedResult
      ),
    ) ++
    List(
      example(
        "test Fx[Future].handleNonFatalWith should handle NonFatal",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleNonFatalWithShouldHandleNonFatalWith
      ),
      example(
        "test Fx[Future].handleNonFatalWith should return the successful result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleNonFatalWithShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future].handleNonFatalWithEither should handle NonFatal",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleNonFatalWithEitherShouldHandleNonFatalWith
      ),
      example(
        "test Fx[Future].handleNonFatalWithEither should return the successful result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleNonFatalWithEitherShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future].handleNonFatalWithEither should return the failed result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleNonFatalWithEitherShouldReturnFailedResult
      ),
      example(
        "test Fx[Future].handleEitherNonFatalWith should handle NonFatal",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleEitherNonFatalWithShouldHandleNonFatalWith
      ),
      example(
        "test Fx[Future].handleEitherNonFatalWith should return the successful result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleEitherNonFatalWithShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future].handleEitherNonFatalWith should return the failed result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleEitherNonFatalWithShouldReturnFailedResult
      ),
      example(
        "test Fx[Future].handleEitherTNonFatalWith should handle NonFatal",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleEitherTNonFatalWithShouldHandleNonFatalWith
      ),
      example(
        "test Fx[Future].handleEitherTNonFatalWith should return the successful result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleEitherTNonFatalWithShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future].handleEitherTNonFatalWith should return the failed result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleEitherTNonFatalWithShouldReturnFailedResult
      ),
      example(
        "test Fx[Future].handleNonFatal should handle NonFatal",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleNonFatalShouldHandleNonFatal
      ),
      example(
        "test Fx[Future].handleNonFatal should return the successful result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleNonFatalShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future].handleNonFatalEither should handle NonFatal",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleNonFatalEitherShouldHandleNonFatal
      ),
      example(
        "test Fx[Future].handleNonFatalEither should return the successful result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleNonFatalEitherShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future].handleNonFatalEither should return the failed result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleNonFatalEitherShouldReturnFailedResult
      ),
      example(
        "test Fx[Future].handleEitherNonFatal should handle NonFatal",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleEitherNonFatalShouldHandleNonFatal
      ),
      example(
        "test Fx[Future].handleEitherNonFatal should return the successful result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleEitherNonFatalShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future].handleEitherNonFatal should return the failed result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleEitherNonFatalShouldReturnFailedResult
      ),
      example(
        "test Fx[Future].handleEitherTNonFatal should handle NonFatal",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleEitherTNonFatalShouldHandleNonFatal
      ),
      example(
        "test Fx[Future].handleEitherTNonFatal should return the successful result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleEitherTNonFatalShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future].handleEitherTNonFatal should return the failed result",
        FutureSpec.CanHandleErrorSpec.testCanHandleError_Future_handleEitherTNonFatalShouldReturnFailedResult
      ),
    ) ++ List(
      example(
        "test Fx[Future].recoverFromNonFatalWith should catch NonFatal",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverFromNonFatalWithShouldRecoverFromNonFatal
      ),
      example(
        "test Fx[Future].recoverFromNonFatalWith should return the successful result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverFromNonFatalWithShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future].recoverFromNonFatalWithEither should catch NonFatal",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal
      ),
      example(
        "test Fx[Future].recoverFromNonFatalWithEither should return the successful result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future].recoverFromNonFatalWithEither should return the failed result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverFromNonFatalWithEitherShouldReturnFailedResult
      ),
      example(
        "test Fx[Future].recoverEitherFromNonFatalWith should catch NonFatal",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal
      ),
      example(
        "test Fx[Future].recoverEitherFromNonFatalWith should return the successful result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future].recoverEitherFromNonFatalWith should return the failed result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverEitherFromNonFatalWithShouldReturnFailedResult
      ),
      example(
        "test Fx[Future].recoverEitherTFromNonFatalWith should catch NonFatal",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal
      ),
      example(
        "test Fx[Future].recoverEitherTFromNonFatalWith should return the successful result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future].recoverEitherTFromNonFatalWith should return the failed result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverEitherTFromNonFatalWithShouldReturnFailedResult
      ),
      example(
        "test Fx[Future].recoverFromNonFatal should catch NonFatal",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverFromNonFatalShouldRecoverFromNonFatal
      ),
      example(
        "test Fx[Future].recoverFromNonFatal should return the successful result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverFromNonFatalShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future].recoverFromNonFatalEither should catch NonFatal",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverFromNonFatalEitherShouldRecoverFromNonFatal
      ),
      example(
        "test Fx[Future].recoverFromNonFatalEither should return the successful result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverFromNonFatalEitherShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future].recoverFromNonFatalEither should return the failed result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverFromNonFatalEitherShouldReturnFailedResult
      ),
      example(
        "test Fx[Future].recoverEitherFromNonFatal should catch NonFatal",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverEitherFromNonFatalShouldRecoverFromNonFatal
      ),
      example(
        "test Fx[Future].recoverEitherFromNonFatal should return the successful result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverEitherFromNonFatalShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future].recoverEitherFromNonFatal should return the failed result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverEitherFromNonFatalShouldReturnFailedResult
      ),
      example(
        "test Fx[Future].recoverEitherTFromNonFatal should catch NonFatal",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverEitherTFromNonFatalShouldRecoverFromNonFatal
      ),
      example(
        "test Fx[Future].recoverEitherTFromNonFatal should return the successful result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverEitherTFromNonFatalShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Future].recoverEitherTFromNonFatal should return the failed result",
        FutureSpec.CanRecoverSpec.testCanRecover_Future_recoverEitherTFromNonFatalShouldReturnFailedResult
      )
    )

  /* Id */
  private val idSpecs = List(
    property("test Fx[Id].effectOf", IdSpec.testEffectOf),
    property("test Fx[Id].pureOf", IdSpec.testPureOf),
    example("test Fx[Id].unitOf", IdSpec.testUnitOf),
    example("test Fx[Id].errorOf", IdSpec.testErrorOf),
  ) ++
    IdSpec.testMonadLaws ++
    List(
      example(
        "test Fx[Id]catchNonFatalThrowable should catch NonFatal",
        IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalThrowableShouldCatchNonFatal
      ),
      example(
        "test Fx[Id]catchNonFatalThrowable should not catch Fatal",
        IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalThrowableShouldNotCatchFatal
      ),
      example(
        "test Fx[Id]catchNonFatalThrowable should return the successful result",
        IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalThrowableShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Id]catchNonFatal should catch NonFatal",
        IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalShouldCatchNonFatal
      ),
      example(
        "test Fx[Id]catchNonFatal should not catch Fatal",
        IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalShouldNotCatchFatal
      ),
      example(
        "test Fx[Id]catchNonFatal should return the successful result",
        IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Id]catchNonFatalEither should catch NonFatal",
        IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalEitherShouldCatchNonFatal
      ),
      example(
        "test Fx[Id]catchNonFatalEither should not catch Fatal",
        IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalEitherShouldNotCatchFatal
      ),
      example(
        "test Fx[Id]catchNonFatalEither should return the successful result",
        IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalEitherShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Id]catchNonFatalEither should return the failed result",
        IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalEitherShouldReturnFailedResult
      ),
      example(
        "test Fx[Id]catchNonFatalEitherT should catch NonFatal",
        IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalEitherTShouldCatchNonFatal
      ),
      example(
        "test Fx[Id]catchNonFatalEitherT should not catch Fatal",
        IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalEitherTShouldNotCatchFatal
      ),
      example(
        "test Fx[Id]catchNonFatalEitherT should return the successful result",
        IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalEitherTShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Id]catchNonFatalEitherT should return the failed result",
        IdSpec.CanCatchSpec.testCanCatch_Id_catchNonFatalEitherTShouldReturnFailedResult
      )
    ) ++ List(
      example(
        "test Fx[Id].handleNonFatalWith should handle NonFatal",
        IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleNonFatalWithShouldHandleNonFatalWith
      ),
      example(
        "test Fx[Id].handleNonFatalWith should not handle Fatal",
        IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleNonFatalWithShouldNotHandleFatalWith
      ),
      example(
        "test Fx[Id].handleNonFatalWith should return the successful result",
        IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleNonFatalWithShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Id].handleNonFatalWithEither should handle NonFatal",
        IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleNonFatalWithEitherShouldHandleNonFatalWith
      ),
      example(
        "test Fx[Id].handleNonFatalWithEither should not handle Fatal",
        IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleNonFatalWithEitherShouldNotHandleFatalWith
      ),
      example(
        "test Fx[Id].handleNonFatalWithEither should return the successful result",
        IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleNonFatalWithEitherShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Id].handleNonFatalWithEither should return the failed result",
        IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleNonFatalWithEitherShouldReturnFailedResult
      ),
      example(
        "test Fx[Id].handleEitherNonFatalWith should handle NonFatal",
        IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherNonFatalWithShouldHandleNonFatalWith
      ),
      example(
        "test Fx[Id].handleEitherNonFatalWith should not handle Fatal",
        IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherNonFatalWithShouldNotHandleFatalWith
      ),
      example(
        "test Fx[Id].handleEitherNonFatalWith should return the successful result",
        IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherNonFatalWithShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Id].handleEitherNonFatalWith should return the failed result",
        IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherNonFatalWithShouldReturnFailedResult
      ),
      example(
        "test Fx[Id].handleEitherTNonFatalWith should handle NonFatal",
        IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherTNonFatalWithShouldHandleNonFatalWith
      ),
      example(
        "test Fx[Id].handleEitherTNonFatalWith should not handle Fatal",
        IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherTNonFatalWithShouldNotHandleFatalWith
      ),
      example(
        "test Fx[Id].handleEitherTNonFatalWith should return the successful result",
        IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherTNonFatalWithShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Id].handleEitherTNonFatalWith should return the failed result",
        IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherTNonFatalWithShouldReturnFailedResult
      ),
      example(
        "test Fx[Id].handleNonFatal should handle NonFatal",
        IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleNonFatalShouldHandleNonFatal
      ),
      example(
        "test Fx[Id].handleNonFatal should not handle Fatal",
        IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleNonFatalShouldNotHandleFatal
      ),
      example(
        "test Fx[Id].handleNonFatal should return the successful result",
        IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleNonFatalShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Id].handleNonFatalEither should handle NonFatal",
        IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleNonFatalEitherShouldHandleNonFatal
      ),
      example(
        "test Fx[Id].handleNonFatalEither should not handle Fatal",
        IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleNonFatalEitherShouldNotHandleFatal
      ),
      example(
        "test Fx[Id].handleNonFatalEither should return the successful result",
        IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleNonFatalEitherShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Id].handleNonFatalEither should return the failed result",
        IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleNonFatalEitherShouldReturnFailedResult
      ),
      example(
        "test Fx[Id].handleEitherNonFatal should handle NonFatal",
        IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherNonFatalShouldHandleNonFatal
      ),
      example(
        "test Fx[Id].handleEitherNonFatal should not handle Fatal",
        IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherNonFatalShouldNotHandleFatal
      ),
      example(
        "test Fx[Id].handleEitherNonFatal should return the successful result",
        IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherNonFatalShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Id].handleEitherNonFatal should return the failed result",
        IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherNonFatalShouldReturnFailedResult
      ),
      example(
        "test Fx[Id].handleEitherTNonFatal should handle NonFatal",
        IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherTNonFatalShouldHandleNonFatal
      ),
      example(
        "test Fx[Id].handleEitherTNonFatal should not handle Fatal",
        IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherTNonFatalShouldNotHandleFatal
      ),
      example(
        "test Fx[Id].handleEitherTNonFatal should return the successful result",
        IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherTNonFatalShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Id].handleEitherTNonFatal should return the failed result",
        IdSpec.CanHandleErrorSpec.testCanHandleError_Id_handleEitherTNonFatalShouldReturnFailedResult
      )
    ) ++ List(
      example(
        "test Fx[Id].recoverFromNonFatalWith should catch NonFatal",
        IdSpec.CanRecoverSpec.testCanRecover_Id_recoverFromNonFatalWithShouldRecoverFromNonFatal
      ),
      example(
        "test Fx[Id].recoverFromNonFatalWith should not catch Fatal",
        IdSpec.CanRecoverSpec.testCanRecover_Id_recoverFromNonFatalWithShouldNotCatchFatal
      ),
      example(
        "test Fx[Id].recoverFromNonFatalWith should return the successful result",
        IdSpec.CanRecoverSpec.testCanRecover_Id_recoverFromNonFatalWithShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Id].recoverFromNonFatalWithEither should catch NonFatal",
        IdSpec.CanRecoverSpec.testCanRecover_Id_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal
      ),
      example(
        "test Fx[Id].recoverFromNonFatalWithEither should not catch Fatal",
        IdSpec.CanRecoverSpec.testCanRecover_Id_recoverFromNonFatalWithEitherShouldNotCatchFatal
      ),
      example(
        "test Fx[Id].recoverFromNonFatalWithEither should return the successful result",
        IdSpec.CanRecoverSpec.testCanRecover_Id_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Id].recoverFromNonFatalWithEither should return the failed result",
        IdSpec.CanRecoverSpec.testCanRecover_Id_recoverFromNonFatalWithEitherShouldReturnFailedResult
      ),
      example(
        "test Fx[Id].recoverEitherFromNonFatalWith should catch NonFatal",
        IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal
      ),
      example(
        "test Fx[Id].recoverEitherFromNonFatalWith should not catch Fatal",
        IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherFromNonFatalWithShouldNotCatchFatal
      ),
      example(
        "test Fx[Id].recoverEitherFromNonFatalWith should return the successful result",
        IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Id].recoverEitherFromNonFatalWith should return the failed result",
        IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherFromNonFatalWithShouldReturnFailedResult
      ),
      example(
        "test Fx[Id].recoverEitherTFromNonFatalWith should catch NonFatal",
        IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal
      ),
      example(
        "test Fx[Id].recoverEitherTFromNonFatalWith should not catch Fatal",
        IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherTFromNonFatalWithShouldNotCatchFatal
      ),
      example(
        "test Fx[Id].recoverEitherTFromNonFatalWith should return the successful result",
        IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Id].recoverEitherTFromNonFatalWith should return the failed result",
        IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherTFromNonFatalWithShouldReturnFailedResult
      ),
      example(
        "test Fx[Id].recoverFromNonFatal should catch NonFatal",
        IdSpec.CanRecoverSpec.testCanRecover_Id_recoverFromNonFatalShouldRecoverFromNonFatal
      ),
      example(
        "test Fx[Id].recoverFromNonFatal should not catch Fatal",
        IdSpec.CanRecoverSpec.testCanRecover_Id_recoverFromNonFatalShouldNotCatchFatal
      ),
      example(
        "test Fx[Id].recoverFromNonFatal should return the successful result",
        IdSpec.CanRecoverSpec.testCanRecover_Id_recoverFromNonFatalShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Id].recoverFromNonFatalEither should catch NonFatal",
        IdSpec.CanRecoverSpec.testCanRecover_Id_recoverFromNonFatalEitherShouldRecoverFromNonFatal
      ),
      example(
        "test Fx[Id].recoverFromNonFatalEither should not catch Fatal",
        IdSpec.CanRecoverSpec.testCanRecover_Id_recoverFromNonFatalEitherShouldNotCatchFatal
      ),
      example(
        "test Fx[Id].recoverFromNonFatalEither should return the successful result",
        IdSpec.CanRecoverSpec.testCanRecover_Id_recoverFromNonFatalEitherShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Id].recoverFromNonFatalEither should return the failed result",
        IdSpec.CanRecoverSpec.testCanRecover_Id_recoverFromNonFatalEitherShouldReturnFailedResult
      ),
      example(
        "test Fx[Id].recoverEitherFromNonFatal should catch NonFatal",
        IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherFromNonFatalShouldRecoverFromNonFatal
      ),
      example(
        "test Fx[Id].recoverEitherFromNonFatal should not catch Fatal",
        IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherFromNonFatalShouldNotCatchFatal
      ),
      example(
        "test Fx[Id].recoverEitherFromNonFatal should return the successful result",
        IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherFromNonFatalShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Id].recoverEitherFromNonFatal should return the failed result",
        IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherFromNonFatalShouldReturnFailedResult
      ),
      example(
        "test Fx[Id].recoverEitherTFromNonFatal should catch NonFatal",
        IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherTFromNonFatalShouldRecoverFromNonFatal
      ),
      example(
        "test Fx[Id].recoverEitherTFromNonFatal should not catch Fatal",
        IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherTFromNonFatalShouldNotCatchFatal
      ),
      example(
        "test Fx[Id].recoverEitherTFromNonFatal should return the successful result",
        IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherTFromNonFatalShouldReturnSuccessfulResult
      ),
      example(
        "test Fx[Id].recoverEitherTFromNonFatal should return the failed result",
        IdSpec.CanRecoverSpec.testCanRecover_Id_recoverEitherTFromNonFatalShouldReturnFailedResult
      )
    )

  def throwThrowable[A](throwable: => Throwable): A =
    throw throwable

  def run[F[*]: Fx: Functor, A](a: => A): F[A] =
    Fx[F].effectOf(a)

  object IoSpec {

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      var actual        = before
      val testBefore    = actual ==== before
      val io            = Fx[IO].effectOf({ actual = after; () })
      val testBeforeRun = actual ==== before
      io.unsafeRunSync()
      val testAfterRun  = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testBeforeRun.log("testBeforeRun"),
          testAfterRun.log("testAfterRun")
        )
      )
    }

    def testPureOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      var actual        = before
      val testBefore    = actual ==== before
      val io            = Fx[IO].pureOf({ actual = after; () })
      val testBeforeRun = actual ==== after
      io.unsafeRunSync()
      val testAfterRun  = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testBeforeRun.log("testBeforeRun"),
          testAfterRun.log("testAfterRun")
        )
      )
    }

    def testUnitOf: Result = {
      val io             = Fx[IO].unitOf
      val expected: Unit = ()
      val actual: Unit   = io.unsafeRunSync()
      actual ==== expected
    }

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      val io = Fx[IO].errorOf(expectedError)
      expectThrowable(io.unsafeRunSync(), expectedError)
    }

    def testMonadLaws: List[Test] = {
      import cats.syntax.eq.*

      given eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).unsafeRunSync()

      given ioFx: Fx[IO] = effectie.cats.Fx.ioFx

      MonadSpec.testMonadLaws[IO]("IO")
    }

    object CanCatchSpec {

      def testCanCatch_IO_catchNonFatalThrowableShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = expectedExpcetion.asLeft[Int]
        val actual            = Fx[IO].catchNonFatalThrowable(fa).unsafeRunSync()

        actual ==== expected
      }

      def testCanCatch_IO_catchNonFatalThrowableShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))

        try {
          val actual = Fx[IO].catchNonFatalThrowable(fa).unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: SomeControlThrowable =>
            ex.getMessage ==== fatalExpcetion.getMessage

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanCatch_IO_catchNonFatalThrowableShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Int](1)
        val expected = 1.asRight[Throwable]
        val actual   = Fx[IO].catchNonFatalThrowable(fa).unsafeRunSync()

        actual ==== expected
      }

      def testCanCatch_IO_catchNonFatalShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual            = Fx[IO].catchNonFatal(fa)(SomeError.someThrowable).unsafeRunSync()

        actual ==== expected
      }

      def testCanCatch_IO_catchNonFatalShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))

        try {
          val actual = Fx[IO].catchNonFatal(fa)(SomeError.someThrowable).unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: SomeControlThrowable =>
            ex.getMessage ==== fatalExpcetion.getMessage

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanCatch_IO_catchNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Int](1)
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].catchNonFatal(fa)(SomeError.someThrowable).unsafeRunSync()

        actual ==== expected
      }

      def testCanCatch_IO_catchNonFatalEitherShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa       = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual   = Fx[IO].catchNonFatalEither(fa)(SomeError.someThrowable).unsafeRunSync()

        actual ==== expected
      }

      def testCanCatch_IO_catchNonFatalEitherShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

        try {
          val actual = Fx[IO].catchNonFatalEither(fa)(SomeError.someThrowable).unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: SomeControlThrowable =>
            ex.getMessage ==== fatalExpcetion.getMessage

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanCatch_IO_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].catchNonFatalEither(fa)(SomeError.someThrowable).unsafeRunSync()

        actual ==== expected
      }

      def testCanCatch_IO_catchNonFatalEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[IO].catchNonFatalEither(fa)(SomeError.someThrowable).unsafeRunSync()

        actual ==== expected
      }

      def testCanCatch_IO_catchNonFatalEitherTShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
        val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual   = Fx[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.unsafeRunSync()

        actual ==== expected
      }

      def testCanCatch_IO_catchNonFatalEitherTShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

        try {
          val actual = Fx[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: SomeControlThrowable =>
            ex.getMessage ==== fatalExpcetion.getMessage

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanCatch_IO_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.unsafeRunSync()

        actual ==== expected
      }

      def testCanCatch_IO_catchNonFatalEitherTShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value.unsafeRunSync()

        actual ==== expected
      }

    }

    object CanHandleErrorSpec {

      def testCanHandleError_IO_handleNonFatalWithShouldHandleNonFatalWith: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 123
        val actual            = Fx[IO]
          .handleNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) =>
              IO.pure(expected)
          }
          .unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleNonFatalWithShouldNotHandleFatalWith: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))

        try {
          val actual = Fx[IO].handleNonFatalWith(fa)(_ => IO.pure(123)).unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_IO_handleNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Int](1)
        val expected = 1
        val actual   = Fx[IO].handleNonFatalWith(fa)(_ => IO.pure(999)).unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleNonFatalWithEitherShouldHandleNonFatalWith: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
        val actualFailedResult   =
          Fx[IO].handleNonFatalWith(fa)(_ => IO.pure(expectedFailedResult)).unsafeRunSync()

        val expectedSuccessResult = 1.asRight[SomeError]
        val actualSuccessResult   =
          Fx[IO].handleNonFatalWith(fa)(_ => IO.pure(1.asRight[SomeError])).unsafeRunSync()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      def testCanHandleError_IO_handleNonFatalWithEitherShouldNotHandleFatalWith: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

        try {
          val actual = Fx[IO].handleNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: SomeControlThrowable =>
            ex.getMessage ==== fatalExpcetion.getMessage

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_IO_handleNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].handleNonFatalWith(fa)(_ => IO(999.asRight[SomeError])).unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleNonFatalWithEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[IO].handleNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherNonFatalWithShouldHandleNonFatalWith: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult    = Fx[IO]
          .handleEitherNonFatalWith(fa)(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
          .unsafeRunSync()
        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[IO].handleEitherNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).unsafeRunSync()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      def testCanHandleError_IO_handleEitherNonFatalWithShouldNotHandleFatalWith: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

        try {
          val actual =
            Fx[IO]
              .handleEitherNonFatalWith(fa)(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
              .unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_IO_handleEitherNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[IO].handleEitherNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherNonFatalWithShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[IO].handleEitherNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherTNonFatalWithShouldHandleNonFatalWith: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
        val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult    = Fx[IO]
          .handleEitherTNonFatalWith(fa)(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
          .value
          .unsafeRunSync()
        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[IO].handleEitherTNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).value.unsafeRunSync()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      def testCanHandleError_IO_handleEitherTNonFatalWithShouldNotHandleFatalWith: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

        try {
          val actual =
            Fx[IO]
              .handleEitherTNonFatalWith(fa)(err => IO.pure(SomeError.someThrowable(err).asLeft[Int]))
              .value
              .unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_IO_handleEitherTNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[IO].handleEitherTNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).value.unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherTNonFatalWithShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[IO].handleEitherTNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).value.unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleNonFatalShouldHandleNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 123
        val actual            = Fx[IO]
          .handleNonFatal(fa) {
            case NonFatal(`expectedExpcetion`) =>
              expected
          }
          .unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleNonFatalShouldNotHandleFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[IO, Int](throwThrowable[Int](fatalExpcetion))

        try {
          val actual = Fx[IO].handleNonFatal(fa)(_ => 123).unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_IO_handleNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Int](1)
        val expected = 1
        val actual   = Fx[IO].handleNonFatal(fa)(_ => 999).unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleNonFatalEitherShouldHandleNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
        val actualFailedResult   = Fx[IO].handleNonFatal(fa)(_ => expectedFailedResult).unsafeRunSync()

        val expectedSuccessResult = 1.asRight[SomeError]
        val actualSuccessResult   = Fx[IO].handleNonFatal(fa)(_ => 1.asRight[SomeError]).unsafeRunSync()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      def testCanHandleError_IO_handleNonFatalEitherShouldNotHandleFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

        try {
          val actual = Fx[IO].handleNonFatal(fa)(_ => 123.asRight[SomeError]).unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_IO_handleNonFatalEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].handleNonFatal(fa)(_ => 999.asRight[SomeError]).unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleNonFatalEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[IO].handleNonFatal(fa)(_ => 123.asRight[SomeError]).unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherNonFatalShouldHandleNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult    = Fx[IO]
          .handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
          .unsafeRunSync()
        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[IO]
            .handleEitherNonFatal(fa)(_ => 123.asRight[SomeError])
            .unsafeRunSync()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      def testCanHandleError_IO_handleEitherNonFatalShouldNotHandleFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

        try {
          val actual =
            Fx[IO]
              .handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
              .unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_IO_handleEitherNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError]).unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherNonFatalShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[IO].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError]).unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherTNonFatalShouldHandleNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
        val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult    = Fx[IO]
          .handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
          .value
          .unsafeRunSync()
        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[IO].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value.unsafeRunSync()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      def testCanHandleError_IO_handleEitherTNonFatalShouldNotHandleFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

        try {
          val actual =
            Fx[IO]
              .handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
              .value
              .unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_IO_handleEitherTNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value.unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherTNonFatalShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[IO].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value.unsafeRunSync()

        actual ==== expected
      }

    }

    object CanRecoverSpec {

      def testCanRecover_IO_recoverFromNonFatalWithShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 123
        val actual            = Fx[IO]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) =>
              IO.pure(expected)
          }
          .unsafeRunSync()

        actual ==== expected
      }

      def testCanRecover_IO_recoverFromNonFatalWithShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))

        val io = Fx[IO].recoverFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => IO.pure(123) }
        try {
          val actual = io.unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_IO_recoverFromNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Int](1)
        val expected = 1
        val actual   = Fx[IO]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(_) => IO.pure(999)
          }
          .unsafeRunSync()

        actual ==== expected
      }

      def testCanRecover_IO_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
        val actualFailedResult   = Fx[IO]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) => IO.pure(expectedFailedResult)
          }
          .unsafeRunSync()

        val expectedSuccessResult = 1.asRight[SomeError]
        val actualSuccessResult   = Fx[IO]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) => IO.pure(1.asRight[SomeError])
          }
          .unsafeRunSync()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      def testCanRecover_IO_recoverFromNonFatalWithEitherShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

        val io = Fx[IO].recoverFromNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) => IO.pure(123.asRight[SomeError])
        }
        try {
          val actual = io.unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(_) => IO(999.asRight[SomeError])
          }
          .unsafeRunSync()

        actual ==== expected
      }

      def testCanRecover_IO_recoverFromNonFatalWithEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[IO]
          .recoverFromNonFatalWith(fa) {
            case NonFatal(_) => IO.pure(123.asRight[SomeError])
          }
          .unsafeRunSync()

        actual ==== expected
      }

      def testCanRecover_IO_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult    = Fx[IO]
          .recoverEitherFromNonFatalWith(fa) {
            case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
          }
          .unsafeRunSync()
        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   = Fx[IO]
          .recoverEitherFromNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) => IO.pure(123.asRight[SomeError])
          }
          .unsafeRunSync()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      def testCanRecover_IO_recoverEitherFromNonFatalWithShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

        val io = Fx[IO].recoverEitherFromNonFatalWith(fa) {
          case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
        }
        try {
          val actual = io.unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_IO_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO]
          .recoverEitherFromNonFatalWith(fa) {
            case NonFatal(_) => IO.pure(123.asRight[SomeError])
          }
          .unsafeRunSync()

        actual ==== expected
      }

      def testCanRecover_IO_recoverEitherFromNonFatalWithShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[IO]
            .recoverEitherFromNonFatalWith(fa) {
              case NonFatal(_) => IO.pure(123.asRight[SomeError])
            }
            .unsafeRunSync()

        actual ==== expected
      }

      def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
        val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult    = Fx[IO]
          .recoverEitherTFromNonFatalWith(fa) {
            case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
          }
          .value
          .unsafeRunSync()
        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   = Fx[IO]
          .recoverEitherTFromNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) => IO.pure(123.asRight[SomeError])
          }
          .value
          .unsafeRunSync()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))

        val io = Fx[IO].recoverEitherTFromNonFatalWith(fa) {
          case err => IO.pure(SomeError.someThrowable(err).asLeft[Int])
        }
        try {
          val actual = io.value.unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO]
          .recoverEitherTFromNonFatalWith(fa) {
            case NonFatal(_) => IO.pure(123.asRight[SomeError])
          }
          .value
          .unsafeRunSync()

        actual ==== expected
      }

      def testCanRecover_IO_recoverEitherTFromNonFatalWithShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[IO]
            .recoverEitherTFromNonFatalWith(fa) {
              case NonFatal(_) => IO.pure(123.asRight[SomeError])
            }
            .value
            .unsafeRunSync()

        actual ==== expected
      }

      // /

      def testCanRecover_IO_recoverFromNonFatalShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 123
        val actual            = Fx[IO]
          .recoverFromNonFatal(fa) {
            case NonFatal(`expectedExpcetion`) =>
              expected
          }
          .unsafeRunSync()

        actual ==== expected
      }

      def testCanRecover_IO_recoverFromNonFatalShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))

        val io = Fx[IO].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123 }
        try {
          val actual = io.unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_IO_recoverFromNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Int](1)
        val expected = 1
        val actual   = Fx[IO].recoverFromNonFatal(fa) { case NonFatal(_) => 999 }.unsafeRunSync()

        actual ==== expected
      }

      def testCanRecover_IO_recoverFromNonFatalEitherShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
        val actualFailedResult   = Fx[IO]
          .recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expectedFailedResult }
          .unsafeRunSync()

        val expectedSuccessResult = 1.asRight[SomeError]
        val actualSuccessResult   = Fx[IO]
          .recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }
          .unsafeRunSync()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      def testCanRecover_IO_recoverFromNonFatalEitherShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

        val io = Fx[IO].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }
        try {
          val actual = io.unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_IO_recoverFromNonFatalEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].recoverFromNonFatal(fa) { case NonFatal(_) => 999.asRight[SomeError] }.unsafeRunSync()

        actual ==== expected
      }

      def testCanRecover_IO_recoverFromNonFatalEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual = Fx[IO].recoverFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }.unsafeRunSync()

        actual ==== expected
      }

      def testCanRecover_IO_recoverEitherFromNonFatalShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult    =
          Fx[IO]
            .recoverEitherFromNonFatal(fa) {
              case err => SomeError.someThrowable(err).asLeft[Int]
            }
            .unsafeRunSync()
        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[IO]
            .recoverEitherFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }
            .unsafeRunSync()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      def testCanRecover_IO_recoverEitherFromNonFatalShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        val fa = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

        val io =
          Fx[IO].recoverEitherFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }
        try {
          val actual = io.unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_IO_recoverEitherFromNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[IO]
            .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
            .unsafeRunSync()

        actual ==== expected
      }

      def testCanRecover_IO_recoverEitherFromNonFatalShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[IO]
            .recoverEitherFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
            .unsafeRunSync()

        actual ==== expected
      }

      def testCanRecover_IO_recoverEitherTFromNonFatalShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
        val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult    =
          Fx[IO]
            .recoverEitherTFromNonFatal(fa) {
              case err => SomeError.someThrowable(err).asLeft[Int]
            }
            .value
            .unsafeRunSync()
        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[IO]
            .recoverEitherTFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 123.asRight[SomeError] }
            .value
            .unsafeRunSync()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      def testCanRecover_IO_recoverEitherTFromNonFatalShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        val fa = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))

        val io =
          Fx[IO].recoverEitherTFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }
        try {
          val actual = io.value.unsafeRunSync()
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[IO]
            .recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
            .value
            .unsafeRunSync()

        actual ==== expected
      }

      def testCanRecover_IO_recoverEitherTFromNonFatalShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[IO]
            .recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 123.asRight[SomeError] }
            .value
            .unsafeRunSync()

        actual ==== expected
      }

    }

  }

  object FutureSpec {
    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration.*
    import scala.concurrent.{ExecutionContext, Future}

    private given errorLogger: ErrorLogger[Throwable] = ErrorLogger.printlnDefaultErrorLogger

    private val waitFor = WaitFor(1.second)

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ExecutionContext                 =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      var actual               = before
      val testBefore           = actual ==== before
      val future: Future[Unit] = Fx[Future].effectOf({ actual = after; () })
      ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)

      val testAfterRun = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfterRun.log("testAfterRun")
        )
      )
    }

    def testPureOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ExecutionContext                 =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      var actual       = before
      val testBefore   = actual ==== before
      val future       = Fx[Future].pureOf({ actual = after; () })
      ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)
      val testAfterRun = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfterRun.log("testAfterRun")
        )
      )
    }

    def testUnitOf: Result = {
      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ExecutionContext                 =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)
      val future                             = Fx[Future].unitOf
      val expected: Unit                     = ()
      val actual: Unit = ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future)
      actual ==== expected
    }

    def testErrorOf: Result = {
      val expectedMessage                    = "This is a throwable test error."
      val expectedError                      = SomeThrowableError.message(expectedMessage)
      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ExecutionContext                 =
        ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

      val future = Fx[Future].errorOf(expectedError)
      expectThrowable(ConcurrentSupport.futureToValueAndTerminate(executorService, waitFor)(future), expectedError)
    }

    def testMonadLaws: List[Test] = {
      import cats.syntax.eq.*

      given ec: scala.concurrent.ExecutionContext                    = scala.concurrent.ExecutionContext.global
      implicit def futureEqual[A](implicit EQ: Eq[A]): Eq[Future[A]] = new Eq[Future[A]] {
        override def eqv(x: Future[A], y: Future[A]): Boolean =
          Await.result(x.flatMap(a => y.map(b => EQ.eqv(a, b))), 1.second)
      }
      given eqFuture: Eq[Future[Int]]                                =
        (x, y) => {
          val future = x.flatMap(xx => y.map(_ === xx))
          Await.result(future, waitFor.waitFor)
        }

      MonadSpec.testMonadLaws[Future]("Future")
    }

    object CanCatchSpec {
      import java.util.concurrent.{ExecutorService, Executors}
      import scala.concurrent.duration.*
      import scala.concurrent.{ExecutionContext, Future}

      val waitFor = WaitFor(1.second)

      def testCanCatch_Future_catchNonFatalThrowableShouldCatchNonFatal: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = expectedExpcetion.asLeft[Int]
        val actual            = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(Fx[Future].catchNonFatalThrowable(fa))

        actual ==== expected
      }

      def testCanCatch_Future_catchNonFatalThrowableShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa       = run[Future, Int](1)
        val expected = 1.asRight[SomeError]
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(Fx[Future].catchNonFatalThrowable(fa))

        actual ==== expected
      }

      def testCanCatch_Future_catchNonFatalShouldCatchNonFatal: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual            = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(Fx[Future].catchNonFatal(fa)(SomeError.someThrowable))

        actual ==== expected
      }

      def testCanCatch_Future_catchNonFatalShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa       = run[Future, Int](1)
        val expected = 1.asRight[SomeError]
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(Fx[Future].catchNonFatal(fa)(SomeError.someThrowable))

        actual ==== expected
      }

      def testCanCatch_Future_catchNonFatalEitherShouldCatchNonFatal: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa       = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(Fx[Future].catchNonFatalEither(fa)(SomeError.someThrowable))

        actual ==== expected
      }

      def testCanCatch_Future_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(Fx[Future].catchNonFatalEither(fa)(SomeError.someThrowable))

        actual ==== expected
      }

      def testCanCatch_Future_catchNonFatalEitherShouldReturnFailedResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(Fx[Future].catchNonFatalEither(fa)(SomeError.someThrowable))

        actual ==== expected
      }

      def testCanCatch_Future_catchNonFatalEitherTShouldCatchNonFatal: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
        val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(Fx[Future].catchNonFatalEitherT(fa)(SomeError.someThrowable).value)

        actual ==== expected
      }

      def testCanCatch_Future_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(Fx[Future].catchNonFatalEitherT(fa)(SomeError.someThrowable).value)

        actual ==== expected
      }

      def testCanCatch_Future_catchNonFatalEitherTShouldReturnFailedResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(Fx[Future].catchNonFatalEitherT(fa)(SomeError.someThrowable).value)

        actual ==== expected
      }
    }

    object CanHandleErrorSpec {
      import java.util.concurrent.{ExecutorService, Executors}
      import scala.concurrent.duration.*
      import scala.concurrent.{ExecutionContext, Future}

      val waitFor = WaitFor(1.second)

      def testCanHandleError_Future_handleNonFatalWithShouldHandleNonFatalWith: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 1
        val actual            = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(Fx[Future].handleNonFatalWith(fa)(_ => Future(expected)))

        actual ==== expected
      }

      def testCanHandleError_Future_handleNonFatalWithShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa       = run[Future, Int](1)
        val expected = 1
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(Fx[Future].handleNonFatalWith(fa)(_ => Future(123)))

        actual ==== expected
      }

      def testCanHandleError_Future_handleNonFatalWithEitherShouldHandleNonFatalWith: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   =
          ConcurrentSupport.futureToValue(
            Fx[Future].handleNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int])),
            waitFor
          )

        val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected = 1.asRight[SomeError]
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(Fx[Future].handleNonFatalWith(fa2)(_ => Future(expected)))

        expectedFailedResult ==== actualFailedResult and actual ==== expected
      }

      def testCanHandleError_Future_handleNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor
          )(Fx[Future].handleNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int])))

        actual ==== expected
      }

      def testCanHandleError_Future_handleNonFatalWithEitherShouldReturnFailedResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(Fx[Future].handleNonFatalWith(fa)(_ => Future(1.asRight[SomeError])))

        actual ==== expected
      }

      def testCanHandleError_Future_handleEitherNonFatalWithShouldHandleNonFatalWith: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   = ConcurrentSupport.futureToValue(
          Fx[Future]
            .handleEitherNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int])),
          waitFor
        )

        val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected = 1.asRight[SomeError]
        val actual   =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor
          )(Fx[Future].handleEitherNonFatalWith(fa2)(err => Future(expected)))

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      def testCanHandleError_Future_handleEitherNonFatalWithShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(
          Fx[Future]
            .handleEitherNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int]))
        )

        actual ==== expected
      }

      def testCanHandleError_Future_handleEitherNonFatalWithShouldReturnFailedResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor
          )(Fx[Future].handleEitherNonFatalWith(fa)(_ => Future(expected)))

        actual ==== expected
      }

      def testCanHandleError_Future_handleEitherTNonFatalWithShouldHandleNonFatalWith: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   = ConcurrentSupport.futureToValue(
          Fx[Future]
            .handleEitherTNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int]))
            .value,
          waitFor
        )

        val fa2      = EitherT(
          run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        )
        val expected = 1.asRight[SomeError]
        val actual   =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor
          )(Fx[Future].handleEitherTNonFatalWith(fa2)(err => Future(expected)).value)

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      def testCanHandleError_Future_handleEitherTNonFatalWithShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(
          Fx[Future]
            .handleEitherTNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int]))
            .value
        )

        actual ==== expected
      }

      def testCanHandleError_Future_handleEitherTNonFatalWithShouldReturnFailedResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor
          )(Fx[Future].handleEitherTNonFatalWith(fa)(_ => Future(expected)).value)

        actual ==== expected
      }

      def testCanHandleError_Future_handleNonFatalShouldHandleNonFatal: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 1
        val actual            = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(Fx[Future].handleNonFatal(fa)(_ => expected))

        actual ==== expected
      }

      def testCanHandleError_Future_handleNonFatalShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa       = run[Future, Int](1)
        val expected = 1
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(Fx[Future].handleNonFatal(fa)(_ => 123))

        actual ==== expected
      }

      def testCanHandleError_Future_handleNonFatalEitherShouldHandleNonFatal: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   =
          ConcurrentSupport.futureToValue(
            Fx[Future].handleNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]),
            waitFor
          )

        val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected = 1.asRight[SomeError]
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(Fx[Future].handleNonFatal(fa2)(_ => expected))

        expectedFailedResult ==== actualFailedResult and actual ==== expected
      }

      def testCanHandleError_Future_handleNonFatalEitherShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor
          )(Fx[Future].handleNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]))

        actual ==== expected
      }

      def testCanHandleError_Future_handleNonFatalEitherShouldReturnFailedResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(Fx[Future].handleNonFatal(fa)(_ => 1.asRight[SomeError]))

        actual ==== expected
      }

      def testCanHandleError_Future_handleEitherNonFatalShouldHandleNonFatal: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   = ConcurrentSupport.futureToValue(
          Fx[Future].handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]),
          waitFor
        )

        val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected = 1.asRight[SomeError]
        val actual   =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor
          )(Fx[Future].handleEitherNonFatal(fa2)(err => expected))

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      def testCanHandleError_Future_handleEitherNonFatalShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(Fx[Future].handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]))

        actual ==== expected
      }

      def testCanHandleError_Future_handleEitherNonFatalShouldReturnFailedResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor
          )(Fx[Future].handleEitherTNonFatal(fa)(_ => expected).value)

        actual ==== expected
      }

      def testCanHandleError_Future_handleEitherTNonFatalShouldHandleNonFatal: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   = ConcurrentSupport.futureToValue(
          Fx[Future].handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]).value,
          waitFor
        )

        val fa2      = EitherT(
          run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        )
        val expected = 1.asRight[SomeError]
        val actual   =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor
          )(Fx[Future].handleEitherTNonFatal(fa2)(err => expected).value)

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      def testCanHandleError_Future_handleEitherTNonFatalShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(Fx[Future].handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]).value)

        actual ==== expected
      }

      def testCanHandleError_Future_handleEitherTNonFatalShouldReturnFailedResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor
          )(Fx[Future].handleEitherTNonFatal(fa)(_ => expected).value)

        actual ==== expected
      }

    }

    object CanRecoverSpec {
      import java.util.concurrent.{ExecutorService, Executors}
      import scala.concurrent.duration.*
      import scala.concurrent.{ExecutionContext, Future}
      import scala.util.control.NonFatal

      val waitFor = WaitFor(1.second)

      def testCanRecover_Future_recoverFromNonFatalWithShouldRecoverFromNonFatal: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 1
        val actual            = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(Fx[Future].recoverFromNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) => Future(expected)
        })

        actual ==== expected
      }

      def testCanRecover_Future_recoverFromNonFatalWithShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa       = run[Future, Int](1)
        val expected = 1
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(Fx[Future].recoverFromNonFatalWith(fa) {
          case NonFatal(_) => Future(123)
        })

        actual ==== expected
      }

      def testCanRecover_Future_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   =
          ConcurrentSupport.futureToValue(
            Fx[Future].recoverFromNonFatalWith(fa) {
              case err => Future(SomeError.someThrowable(err).asLeft[Int])
            },
            waitFor
          )

        val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected = 1.asRight[SomeError]
        val actual   =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor
          )(Fx[Future].recoverFromNonFatalWith(fa2) {
            case NonFatal(`expectedExpcetion`) => Future(expected)
          })

        expectedFailedResult ==== actualFailedResult and actual ==== expected
      }

      def testCanRecover_Future_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor
          )(Fx[Future].recoverFromNonFatalWith(fa) {
            case err => Future(SomeError.someThrowable(err).asLeft[Int])
          })

        actual ==== expected
      }

      def testCanRecover_Future_recoverFromNonFatalWithEitherShouldReturnFailedResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor
          )(Fx[Future].recoverFromNonFatalWith(fa) {
            case NonFatal(_) => Future(1.asRight[SomeError])
          })

        actual ==== expected
      }

      def testCanRecover_Future_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   = ConcurrentSupport.futureToValue(
          Fx[Future]
            .recoverEitherFromNonFatalWith(fa) {
              case err => Future(SomeError.someThrowable(err).asLeft[Int])
            },
          waitFor
        )

        val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected = 1.asRight[SomeError]
        val actual   =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor
          )(
            Fx[Future]
              .recoverEitherFromNonFatalWith(fa2) {
                case err => Future(expected)
              }
          )

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      def testCanRecover_Future_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(
          Fx[Future]
            .recoverEitherFromNonFatalWith(fa) {
              case err => Future(SomeError.someThrowable(err).asLeft[Int])
            }
        )

        actual ==== expected
      }

      def testCanRecover_Future_recoverEitherFromNonFatalWithShouldReturnFailedResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor
          )(
            Fx[Future]
              .recoverEitherFromNonFatalWith(fa) {
                case NonFatal(_) => Future(expected)
              }
          )

        actual ==== expected
      }

      def testCanRecover_Future_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   = ConcurrentSupport.futureToValue(
          Fx[Future]
            .recoverEitherTFromNonFatalWith(fa) {
              case err => Future(SomeError.someThrowable(err).asLeft[Int])
            }
            .value,
          waitFor
        )

        val fa2      = EitherT(
          run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        )
        val expected = 1.asRight[SomeError]
        val actual   =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor
          )(
            Fx[Future]
              .recoverEitherTFromNonFatalWith(fa2) {
                case err => Future(expected)
              }
              .value
          )

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      def testCanRecover_Future_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(
          Fx[Future]
            .recoverEitherTFromNonFatalWith(fa) {
              case err => Future(SomeError.someThrowable(err).asLeft[Int])
            }
            .value
        )

        actual ==== expected
      }

      def testCanRecover_Future_recoverEitherTFromNonFatalWithShouldReturnFailedResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor
          )(
            Fx[Future]
              .recoverEitherTFromNonFatalWith(fa) {
                case NonFatal(_) => Future(expected)
              }
              .value
          )

        actual ==== expected
      }

      // /

      def testCanRecover_Future_recoverFromNonFatalShouldRecoverFromNonFatal: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 1
        val actual            = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(Fx[Future].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expected })

        actual ==== expected
      }

      def testCanRecover_Future_recoverFromNonFatalShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa       = run[Future, Int](1)
        val expected = 1
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(Fx[Future].recoverFromNonFatal(fa) { case NonFatal(_) => 123 })

        actual ==== expected
      }

      def testCanRecover_Future_recoverFromNonFatalEitherShouldRecoverFromNonFatal: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   =
          ConcurrentSupport.futureToValue(
            Fx[Future].recoverFromNonFatal(fa) {
              case err => SomeError.someThrowable(err).asLeft[Int]
            },
            waitFor
          )

        val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected = 1.asRight[SomeError]
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(Fx[Future].recoverFromNonFatal(fa2) { case NonFatal(`expectedExpcetion`) => expected })

        expectedFailedResult ==== actualFailedResult and actual ==== expected
      }

      def testCanRecover_Future_recoverFromNonFatalEitherShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor
          )(Fx[Future].recoverFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          })

        actual ==== expected
      }

      def testCanRecover_Future_recoverFromNonFatalEitherShouldReturnFailedResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(Fx[Future].recoverFromNonFatal(fa) { case NonFatal(_) => 1.asRight[SomeError] })

        actual ==== expected
      }

      def testCanRecover_Future_recoverEitherFromNonFatalShouldRecoverFromNonFatal: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   = ConcurrentSupport.futureToValue(
          Fx[Future]
            .recoverEitherFromNonFatal(fa) {
              case err => SomeError.someThrowable(err).asLeft[Int]
            },
          waitFor
        )

        val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected = 1.asRight[SomeError]
        val actual   =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor
          )(Fx[Future].recoverEitherFromNonFatal(fa2) { case err => expected })

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      def testCanRecover_Future_recoverEitherFromNonFatalShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(
          Fx[Future]
            .recoverEitherFromNonFatal(fa) {
              case err => SomeError.someThrowable(err).asLeft[Int]
            }
        )

        actual ==== expected
      }

      def testCanRecover_Future_recoverEitherFromNonFatalShouldReturnFailedResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor
          )(Fx[Future].recoverEitherFromNonFatal(fa) { case NonFatal(_) => expected })

        actual ==== expected
      }

      def testCanRecover_Future_recoverEitherTFromNonFatalShouldRecoverFromNonFatal: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   = ConcurrentSupport.futureToValue(
          Fx[Future]
            .recoverEitherTFromNonFatal(fa) {
              case err => SomeError.someThrowable(err).asLeft[Int]
            }
            .value,
          waitFor
        )

        val fa2      = EitherT(
          run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        )
        val expected = 1.asRight[SomeError]
        val actual   =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor
          )(Fx[Future].recoverEitherTFromNonFatal(fa2) { case err => expected }.value)

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      def testCanRecover_Future_recoverEitherTFromNonFatalShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          executorService,
          waitFor
        )(
          Fx[Future]
            .recoverEitherTFromNonFatal(fa) {
              case err => SomeError.someThrowable(err).asLeft[Int]
            }
            .value
        )

        actual ==== expected
      }

      def testCanRecover_Future_recoverEitherTFromNonFatalShouldReturnFailedResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             =
          ConcurrentSupport.newExecutionContext(executorService, ErrorLogger.printlnExecutionContextErrorLogger)

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          ConcurrentSupport.futureToValueAndTerminate(
            executorService,
            waitFor
          )(Fx[Future].recoverEitherTFromNonFatal(fa) { case NonFatal(_) => expected }.value)

        actual ==== expected
      }
    }
  }

  object IdSpec {

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      var actual     = before
      val testBefore = actual ==== before
      Fx[Id].effectOf({ actual = after; () })
      val testAfter  = actual ==== after
      testBefore.log("testBefore") ==== testAfter.log("testAfter")
    }

    def testPureOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      var actual     = before
      val testBefore = actual ==== before
      Fx[Id].pureOf({ actual = after; () })
      val testAfter  = actual ==== after
      Result.all(
        List(
          testBefore.log("testBefore"),
          testAfter.log("testAfter")
        )
      )
    }

    def testUnitOf: Result = {
      val expected: Unit = ()
      val actual         = Fx[Id].unitOf
      actual ==== expected
    }

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)
      lazy val actual     = Fx[Id].errorOf(expectedError)
      expectThrowable(actual, expectedError)
    }

    def testMonadLaws: List[Test] = {
      given idMonad: Monad[Id] = cats.catsInstancesForId
      MonadSpec.testMonadLaws[Id]("Id")
    }

    object CanCatchSpec {

      def testCanCatch_Id_catchNonFatalThrowableShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = expectedExpcetion.asLeft[Int]
        val actual            = Fx[Id].catchNonFatalThrowable(fa)

        actual ==== expected
      }

      def testCanCatch_Id_catchNonFatalThrowableShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa        = run[Id, Int](throwThrowable[Int](fatalExpcetion))

        try {
          val actual = Fx[Id].catchNonFatalThrowable(fa)
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanCatch_Id_catchNonFatalThrowableShouldReturnSuccessfulResult: Result = {

        val fa       = run[Id, Int](1)
        val expected = 1.asRight[Throwable]
        val actual   = Fx[Id].catchNonFatalThrowable(fa)

        actual ==== expected
      }

      def testCanCatch_Id_catchNonFatalShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual            = Fx[Id].catchNonFatal(fa)(SomeError.someThrowable)

        actual ==== expected
      }

      def testCanCatch_Id_catchNonFatalShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa        = run[Id, Int](throwThrowable[Int](fatalExpcetion))

        try {
          val actual = Fx[Id].catchNonFatal(fa)(SomeError.someThrowable)
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanCatch_Id_catchNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = run[Id, Int](1)
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id].catchNonFatal(fa)(SomeError.someThrowable)

        actual ==== expected
      }

      def testCanCatch_Id_catchNonFatalEitherShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa  = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual   = Fx[Id].catchNonFatalEither(fa)(SomeError.someThrowable)

        actual ==== expected
      }

      def testCanCatch_Id_catchNonFatalEitherShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa        = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

        try {
          val actual = Fx[Id].catchNonFatalEither(fa)(SomeError.someThrowable)
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanCatch_Id_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id].catchNonFatalEither(fa)(SomeError.someThrowable)

        actual ==== expected
      }

      def testCanCatch_Id_catchNonFatalEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Id].catchNonFatalEither(fa)(SomeError.someThrowable)

        actual ==== expected
      }

      def testCanCatch_Id_catchNonFatalEitherTShouldCatchNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa           = EitherT(
          run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        )
        val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual            = Fx[Id].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

        actual ==== expected
      }

      def testCanCatch_Id_catchNonFatalEitherTShouldNotCatchFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

        try {
          val actual = Fx[Id].catchNonFatalEitherT(fa)(SomeError.someThrowable).value
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanCatch_Id_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[Id, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

        actual ==== expected
      }

      def testCanCatch_Id_catchNonFatalEitherTShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Id].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

        actual ==== expected
      }

    }

    object CanHandleErrorSpec {

      def testCanHandleError_Id_handleNonFatalWithShouldHandleNonFatalWith: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 1
        val actual: Id[Int]   = Fx[Id].handleNonFatalWith(fa)(_ => expected)

        actual ==== expected
      }

      def testCanHandleError_Id_handleNonFatalWithShouldNotHandleFatalWith: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa        = run[Id, Int](throwThrowable[Int](fatalExpcetion))

        try {
          val actual: Id[Int] = Fx[Id].handleNonFatalWith(fa)(_ => 1)
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_Id_handleNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa              = run[Id, Int](1)
        val expected        = 1
        val actual: Id[Int] = Fx[Id].handleNonFatalWith(fa)(_ => 123)

        actual ==== expected
      }

      def testCanHandleError_Id_handleNonFatalWithEitherShouldHandleNonFatalWith: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   =
          Fx[Id].handleNonFatalWith(fa)(err => SomeError.someThrowable(err).asLeft[Int])

        lazy val fa2 = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id].handleNonFatalWith(fa2)(_ => expected)

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      def testCanHandleError_Id_handleNonFatalWithEitherShouldNotHandleFatalWith: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa        = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

        try {
          val actual = Fx[Id].handleNonFatalWith(fa)(_ => 1.asRight[SomeError])
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_Id_handleNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id].handleNonFatalWith(fa)(err => SomeError.someThrowable(err).asLeft[Int])

        actual ==== expected
      }

      def testCanHandleError_Id_handleNonFatalWithEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Id].handleNonFatalWith(fa)(_ => 1.asRight[SomeError])

        actual ==== expected
      }

      def testCanHandleError_Id_handleEitherNonFatalWithShouldHandleNonFatalWith: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   =
          Fx[Id].handleEitherNonFatalWith(fa)(err => SomeError.someThrowable(err).asLeft[Int])

        lazy val fa2 = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id].handleEitherNonFatalWith(fa2)(_ => expected)

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      def testCanHandleError_Id_handleEitherNonFatalWithShouldNotHandleFatalWith: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa        = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

        try {
          val actual = Fx[Id].handleEitherNonFatalWith(fa)(_ => 1.asRight[SomeError])
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_Id_handleEitherNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[Id].handleEitherNonFatalWith(fa)(err => SomeError.someThrowable(err).asLeft[Int])

        actual ==== expected
      }

      def testCanHandleError_Id_handleEitherNonFatalWithShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Id].handleEitherNonFatalWith(fa)(_ => 1.asRight[SomeError])

        actual ==== expected
      }

      def testCanHandleError_Id_handleEitherTNonFatalWithShouldHandleNonFatalWith: Result = {

        val expectedExpcetion    = new RuntimeException("Something's wrong")
        lazy val fa              = EitherT(
          run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        )
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   =
          Fx[Id].handleEitherTNonFatalWith(fa)(err => SomeError.someThrowable(err).asLeft[Int]).value

        lazy val fa2 = EitherT(
          run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        )
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id].handleEitherTNonFatalWith(fa2)(_ => expected).value

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      def testCanHandleError_Id_handleEitherTNonFatalWithShouldNotHandleFatalWith: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

        try {
          val actual = Fx[Id].handleEitherTNonFatalWith(fa)(_ => 1.asRight[SomeError]).value
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_Id_handleEitherTNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[Id, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[Id].handleEitherTNonFatalWith(fa)(err => SomeError.someThrowable(err).asLeft[Int]).value

        actual ==== expected
      }

      def testCanHandleError_Id_handleEitherTNonFatalWithShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Id].handleEitherTNonFatalWith(fa)(_ => 1.asRight[SomeError]).value

        actual ==== expected
      }

      def testCanHandleError_Id_handleNonFatalShouldHandleNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 1
        val actual: Id[Int]   = Fx[Id].handleNonFatal(fa)(_ => expected)

        actual ==== expected
      }

      def testCanHandleError_Id_handleNonFatalShouldNotHandleFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa        = run[Id, Int](throwThrowable[Int](fatalExpcetion))

        try {
          val actual: Id[Int] = Fx[Id].handleNonFatal(fa)(_ => 1)
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_Id_handleNonFatalShouldReturnSuccessfulResult: Result = {

        val fa              = run[Id, Int](1)
        val expected        = 1
        val actual: Id[Int] = Fx[Id].handleNonFatal(fa)(_ => 123)

        actual ==== expected
      }

      def testCanHandleError_Id_handleNonFatalEitherShouldHandleNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   = Fx[Id].handleNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])

        lazy val fa2 = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id].handleNonFatal(fa2)(_ => expected)

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      def testCanHandleError_Id_handleNonFatalEitherShouldNotHandleFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa        = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

        try {
          val actual = Fx[Id].handleNonFatal(fa)(_ => 1.asRight[SomeError])
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_Id_handleNonFatalEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id].handleNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])

        actual ==== expected
      }

      def testCanHandleError_Id_handleNonFatalEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Id].handleNonFatal(fa)(_ => 1.asRight[SomeError])

        actual ==== expected
      }

      def testCanHandleError_Id_handleEitherNonFatalShouldHandleNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   =
          Fx[Id].handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])

        lazy val fa2 = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id].handleEitherNonFatal(fa2)(_ => expected)

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      def testCanHandleError_Id_handleEitherNonFatalShouldNotHandleFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa        = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion))

        try {
          val actual = Fx[Id].handleEitherNonFatal(fa)(_ => 1.asRight[SomeError])
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_Id_handleEitherNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id].handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])

        actual ==== expected
      }

      def testCanHandleError_Id_handleEitherNonFatalShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Id].handleEitherNonFatal(fa)(_ => 1.asRight[SomeError])

        actual ==== expected
      }

      def testCanHandleError_Id_handleEitherTNonFatalShouldHandleNonFatal: Result = {

        val expectedExpcetion    = new RuntimeException("Something's wrong")
        lazy val fa              = EitherT(
          run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        )
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   =
          Fx[Id].handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]).value

        lazy val fa2 = EitherT(
          run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        )
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id].handleEitherTNonFatal(fa2)(_ => expected).value

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      def testCanHandleError_Id_handleEitherTNonFatalShouldNotHandleFatal: Result = {

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

        try {
          val actual = Fx[Id].handleEitherTNonFatal(fa)(_ => 1.asRight[SomeError]).value
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== fatalExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanHandleError_Id_handleEitherTNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[Id, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id].handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]).value

        actual ==== expected
      }

      def testCanHandleError_Id_handleEitherTNonFatalShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Id].handleEitherTNonFatal(fa)(_ => 1.asRight[SomeError]).value

        actual ==== expected
      }

    }

    object CanRecoverSpec {

      def testCanRecover_Id_recoverFromNonFatalWithShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 1
        val actual: Id[Int]   = Fx[Id].recoverFromNonFatalWith(fa) {
          case NonFatal(`expectedExpcetion`) => expected
        }

        actual ==== expected
      }

      def testCanRecover_Id_recoverFromNonFatalWithShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))

        try {
          val actual: Id[Int] = Fx[Id].recoverFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => 1 }
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_Id_recoverFromNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa              = run[Id, Int](1)
        val expected        = 1
        val actual: Id[Int] = Fx[Id].recoverFromNonFatalWith(fa) { case NonFatal(_) => 123 }

        actual ==== expected
      }

      def testCanRecover_Id_recoverFromNonFatalWithEitherShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   = Fx[Id].recoverFromNonFatalWith(fa) {
          case err => SomeError.someThrowable(err).asLeft[Int]
        }

        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id].recoverFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => expected }

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      def testCanRecover_Id_recoverFromNonFatalWithEitherShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

        try {
          val actual = Fx[Id].recoverFromNonFatalWith(fa) {
            case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError]
          }
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_Id_recoverFromNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[Id].recoverFromNonFatalWith(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }

        actual ==== expected
      }

      def testCanRecover_Id_recoverFromNonFatalWithEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Id].recoverFromNonFatalWith(fa) { case NonFatal(_) => 1.asRight[SomeError] }

        actual ==== expected
      }

      def testCanRecover_Id_recoverEitherFromNonFatalWithShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   =
          Fx[Id]
            .recoverEitherFromNonFatalWith(fa) {
              case err => SomeError.someThrowable(err).asLeft[Int]
            }

        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[Id].recoverEitherFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => expected }

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      def testCanRecover_Id_recoverEitherFromNonFatalWithShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

        try {
          val actual = Fx[Id]
            .recoverEitherFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }

          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_Id_recoverEitherFromNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id]
          .recoverEitherFromNonFatalWith(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }

        actual ==== expected
      }

      def testCanRecover_Id_recoverEitherFromNonFatalWithShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[Id].recoverEitherFromNonFatalWith(fa) { case NonFatal(_) => 1.asRight[SomeError] }

        actual ==== expected
      }

      def testCanRecover_Id_recoverEitherTFromNonFatalWithShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion    = new RuntimeException("Something's wrong")
        lazy val fa              = EitherT(
          run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        )
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   =
          Fx[Id]
            .recoverEitherTFromNonFatalWith(fa) {
              case err => SomeError.someThrowable(err).asLeft[Int]
            }
            .value

        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[Id].recoverEitherTFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => expected }.value

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      def testCanRecover_Id_recoverEitherTFromNonFatalWithShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa           = EitherT(
          run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        )

        try {
          val actual = Fx[Id]
            .recoverEitherTFromNonFatalWith(fa) { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }
            .value
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_Id_recoverEitherTFromNonFatalWithShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[Id, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id]
          .recoverEitherTFromNonFatalWith(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }
          .value

        actual ==== expected
      }

      def testCanRecover_Id_recoverEitherTFromNonFatalWithShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[Id].recoverEitherTFromNonFatalWith(fa) { case NonFatal(_) => 1.asRight[SomeError] }.value

        actual ==== expected
      }

      // /

      def testCanRecover_Id_recoverFromNonFatalShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 1
        val actual: Id[Int]   = Fx[Id].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expected }

        actual ==== expected
      }

      def testCanRecover_Id_recoverFromNonFatalShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa           = run[Id, Int](throwThrowable[Int](expectedExpcetion))

        try {
          val actual: Id[Int] = Fx[Id].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 1 }
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_Id_recoverFromNonFatalShouldReturnSuccessfulResult: Result = {

        val fa              = run[Id, Int](1)
        val expected        = 1
        val actual: Id[Int] = Fx[Id].recoverFromNonFatal(fa) { case NonFatal(_) => 123 }

        actual ==== expected
      }

      def testCanRecover_Id_recoverFromNonFatalEitherShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   =
          Fx[Id].recoverFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }

        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id].recoverFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expected }

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      def testCanRecover_Id_recoverFromNonFatalEitherShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

        try {
          val actual = Fx[Id].recoverFromNonFatal(fa) {
            case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError]
          }
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_Id_recoverFromNonFatalEitherShouldReturnSuccessfulResult: Result = {

        val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[Id].recoverFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }

        actual ==== expected
      }

      def testCanRecover_Id_recoverFromNonFatalEitherShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Id].recoverFromNonFatal(fa) { case NonFatal(_) => 1.asRight[SomeError] }

        actual ==== expected
      }

      def testCanRecover_Id_recoverEitherFromNonFatalShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion = new RuntimeException("Something's wrong")
        lazy val fa = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   =
          Fx[Id]
            .recoverEitherFromNonFatal(fa) {
              case err => SomeError.someThrowable(err).asLeft[Int]
            }

        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[Id].recoverEitherFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expected }

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      def testCanRecover_Id_recoverEitherFromNonFatalShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))

        try {
          val actual = Fx[Id]
            .recoverEitherFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }

          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_Id_recoverEitherFromNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = run[Id, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id]
          .recoverEitherFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }

        actual ==== expected
      }

      def testCanRecover_Id_recoverEitherFromNonFatalShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Id].recoverEitherFromNonFatal(fa) { case NonFatal(_) => 1.asRight[SomeError] }

        actual ==== expected
      }

      def testCanRecover_Id_recoverEitherTFromNonFatalShouldRecoverFromNonFatal: Result = {

        val expectedExpcetion    = new RuntimeException("Something's wrong")
        lazy val fa              = EitherT(
          run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        )
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   =
          Fx[Id]
            .recoverEitherTFromNonFatal(fa) {
              case err => SomeError.someThrowable(err).asLeft[Int]
            }
            .value

        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[Id].recoverEitherTFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => expected }.value

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      def testCanRecover_Id_recoverEitherTFromNonFatalShouldNotCatchFatal: Result = {

        val expectedExpcetion = SomeControlThrowable("Something's wrong")
        lazy val fa           = EitherT(
          run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        )

        try {
          val actual = Fx[Id]
            .recoverEitherTFromNonFatal(fa) { case NonFatal(`expectedExpcetion`) => 1.asRight[SomeError] }
            .value
          Result.failure.log(s"The expected fatal exception was not thrown. actual: ${actual.toString}")
        } catch {
          case ex: ControlThrowable =>
            ex ==== expectedExpcetion

          case ex: Throwable =>
            Result.failure.log(s"Unexpected Throwable: ${ex.toString}")
        }

      }

      def testCanRecover_Id_recoverEitherTFromNonFatalShouldReturnSuccessfulResult: Result = {

        val fa       = EitherT(run[Id, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[Id]
          .recoverEitherTFromNonFatal(fa) {
            case err => SomeError.someThrowable(err).asLeft[Int]
          }
          .value

        actual ==== expected
      }

      def testCanRecover_Id_recoverEitherTFromNonFatalShouldReturnFailedResult: Result = {

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[Id, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[Id].recoverEitherTFromNonFatal(fa) { case NonFatal(_) => 1.asRight[SomeError] }.value

        actual ==== expected
      }

    }

  }

}
