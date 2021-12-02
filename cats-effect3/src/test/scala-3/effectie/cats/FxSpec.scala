package effectie.cats

import cats.data.EitherT
import cats.effect.*
import cats.effect.testkit.TestContext
import cats.effect.unsafe.IORuntime
import cats.instances.either.*
import cats.syntax.all.*
import cats.{Eq, Functor, Id, Monad, Show}
import effectie.cats.compat.CatsEffectIoCompatForFuture
import effectie.cats.Fx.given
import effectie.testing.tools.*
import effectie.testing.types.{SomeError, SomeThrowableError}
import effectie.{ConcurrentSupport, Fx, SomeControlThrowable}
import hedgehog.*
import hedgehog.runner.*

import java.util.concurrent.ExecutorService
import scala.concurrent.Await
import scala.util.control.{ControlThrowable, NonFatal}

/** @author Kevin Lee
  * @since 2020-12-06
  */
object FxSpec extends Properties {

  val Fx: effectie.Fx.type = effectie.Fx

  given eqSomeError: Eq[SomeError]     = Eq.fromUniversalEquals[SomeError]
  given shosSomeError: Show[SomeError] = _.toString

  override def tests: List[Test] = ioSpecs ++ futureSpecs ++ idSpecs

  /* IO */
  private val ioSpecs = List(
    property("test Fx[IO].effectOf", IoSpec.testEffectOf),
    property("test Fx[IO].pureOf", IoSpec.testPureOf),
    example("test Fx[IO].unitOf", IoSpec.testUnitOf),
    example("test Fx[IO].errorOf", IoSpec.testErrorOf),
    property("test Fx[IO] Monad laws - Identity", IoSpec.testMonadLaws1_Identity),
    property("test Fx[IO] Monad laws - Composition", IoSpec.testMonadLaws2_Composition),
    property("test Fx[IO] Monad laws - IdentityAp", IoSpec.testMonadLaws3_IdentityAp),
    property("test Fx[IO] Monad laws - Homomorphism", IoSpec.testMonadLaws4_Homomorphism),
    property("test Fx[IO] Monad laws - Interchange", IoSpec.testMonadLaws5_Interchange),
    property("test Fx[IO] Monad laws - CompositionAp", IoSpec.testMonadLaws6_CompositionAp),
    property("test Fx[IO] Monad laws - LeftIdentity", IoSpec.testMonadLaws7_LeftIdentity),
    property("test Fx[IO] Monad laws - RightIdentity", IoSpec.testMonadLaws8_RightIdentity),
    property("test Fx[IO] Monad laws - Associativity", IoSpec.testMonadLaws9_Associativity),
  ) ++
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
    List(
      /* IO */
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
    )

  /* Future */
  private val futureSpecs = List(
    property("test Fx[Future].effectOf", FutureSpec.testEffectOf),
    property("test Fx[Future].pureOf", FutureSpec.testPureOf),
    example("test Fx[Future].unitOf", FutureSpec.testUnitOf),
    example("test Fx[Future].errorOf", FutureSpec.testErrorOf),
    property("test Fx[Future] Monad laws - Identity", FutureSpec.testMonadLaws1_Identity),
    property("test Fx[Future] Monad laws - Composition", FutureSpec.testMonadLaws2_Composition),
    property("test Fx[Future] Monad laws - IdentityAp", FutureSpec.testMonadLaws3_IdentityAp),
    property("test Fx[Future] Monad laws - Homomorphism", FutureSpec.testMonadLaws4_Homomorphism),
    property("test Fx[Future] Monad laws - Interchange", FutureSpec.testMonadLaws5_Interchange),
    property("test Fx[Future] Monad laws - CompositionAp", FutureSpec.testMonadLaws6_CompositionAp),
    property("test Fx[Future] Monad laws - LeftIdentity", FutureSpec.testMonadLaws7_LeftIdentity),
    property("test Fx[Future] Monad laws - RightIdentity", FutureSpec.testMonadLaws8_RightIdentity),
    property("test Fx[Future] Monad laws - Associativity", FutureSpec.testMonadLaws9_Associativity),
  ) ++
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
    )

  /* Id */
  private val idSpecs = List(
    property("test Fx[Id].effectOf", IdSpec.testEffectOf),
    property("test Fx[Id].pureOf", IdSpec.testPureOf),
    example("test Fx[Id].unitOf", IdSpec.testUnitOf),
    example("test Fx[Id].errorOf", IdSpec.testErrorOf),
    property("test Fx[Id] Monad laws - Identity", IdSpec.testMonadLaws1_Identity),
    property("test Fx[Id] Monad laws - Composition", IdSpec.testMonadLaws2_Composition),
    property("test Fx[Id] Monad laws - IdentityAp", IdSpec.testMonadLaws3_IdentityAp),
    property("test Fx[Id] Monad laws - Homomorphism", IdSpec.testMonadLaws4_Homomorphism),
    property("test Fx[Id] Monad laws - Interchange", IdSpec.testMonadLaws5_Interchange),
    property("test Fx[Id] Monad laws - CompositionAp", IdSpec.testMonadLaws6_CompositionAp),
    property("test Fx[Id] Monad laws - LeftIdentity", IdSpec.testMonadLaws7_LeftIdentity),
    property("test Fx[Id] Monad laws - RightIdentity", IdSpec.testMonadLaws8_RightIdentity),
    property("test Fx[Id] Monad laws - Associativity", IdSpec.testMonadLaws9_Associativity),
  ) ++
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
      import CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      @SuppressWarnings(Array("org.wartremover.warts.Var"))
      var actual        = before
      val testBefore    = actual ==== before
      val io            = Fx[IO].effectOf({ actual = after; () })
      val testBeforeRun = actual ==== before

      val done         = io.completeAs(())
      val testAfterRun = actual ==== after

      Result.all(
        List(
          done,
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
      import CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())

      var actual        = before
      val testBefore    = actual ==== before
      val io            = Fx[IO].pureOf({ actual = after; () })
      val testBeforeRun = actual ==== after

      val done         = io.completeAs(())
      val testAfterRun = actual ==== after
      Result.all(
        List(
          done,
          testBefore.log("testBefore"),
          testBeforeRun.log("testBeforeRun"),
          testAfterRun.log("testAfterRun")
        )
      )
    }

    def testUnitOf: Result = {
      import CatsEffectRunner.*
      given ticket: Ticker = Ticker(TestContext())
      val io               = Fx[IO].unitOf
      val expected: Unit   = ()
      io.completeAs(expected)
    }

    def testErrorOf: Result = {
      import CatsEffectRunner.*

      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      given ticket: Ticker = Ticker(TestContext())

      val io = Fx[IO].errorOf(expectedError)

      io.expectError(expectedError)
    }

    def testMonadLaws1_Identity: Property = {
      import CatsEffectRunner.*
      import cats.syntax.eq.*
      given ticket: Ticker = Ticker(TestContext())

      given eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).completeAndEqualTo(true)

      MonadSpec.test1_Identity[IO]
    }

    def testMonadLaws2_Composition: Property = {
      import CatsEffectRunner.*
      import cats.syntax.eq.*
      given ticket: Ticker = Ticker(TestContext())

      given eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).completeAndEqualTo(true)

      MonadSpec.test2_Composition[IO]
    }

    def testMonadLaws3_IdentityAp: Property = {
      import CatsEffectRunner.*
      import cats.syntax.eq.*
      given ticket: Ticker = Ticker(TestContext())

      given eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).completeAndEqualTo(true)

      MonadSpec.test3_IdentityAp[IO]
    }

    def testMonadLaws4_Homomorphism: Property = {
      import CatsEffectRunner.*
      import cats.syntax.eq.*
      given ticket: Ticker = Ticker(TestContext())

      given eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).completeAndEqualTo(true)

      MonadSpec.test4_Homomorphism[IO]
    }

    def testMonadLaws5_Interchange: Property = {
      import CatsEffectRunner.*
      import cats.syntax.eq.*
      given ticket: Ticker = Ticker(TestContext())

      given eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).completeAndEqualTo(true)

      MonadSpec.test5_Interchange[IO]
    }

    def testMonadLaws6_CompositionAp: Property = {
      import CatsEffectRunner.*
      import cats.syntax.eq.*
      given ticket: Ticker = Ticker(TestContext())

      given eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).completeAndEqualTo(true)

      MonadSpec.test6_CompositionAp[IO]
    }

    def testMonadLaws7_LeftIdentity: Property = {
      import CatsEffectRunner.*
      import cats.syntax.eq.*
      given ticket: Ticker = Ticker(TestContext())

      given eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).completeAndEqualTo(true)

      MonadSpec.test7_LeftIdentity[IO]
    }

    def testMonadLaws8_RightIdentity: Property = {
      import CatsEffectRunner.*
      import cats.syntax.eq.*
      given ticket: Ticker = Ticker(TestContext())

      given eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).completeAndEqualTo(true)

      MonadSpec.test8_RightIdentity[IO]
    }

    def testMonadLaws9_Associativity: Property = {
      import CatsEffectRunner.*
      import cats.syntax.eq.*
      given ticket: Ticker = Ticker(TestContext())

      given eqIo: Eq[IO[Int]] =
        (x, y) => x.flatMap(xx => y.map(_ === xx)).completeAndEqualTo(true)

      MonadSpec.test9_Associativity[IO]
    }

    object CanCatchSpec {

      def testCanCatch_IO_catchNonFatalThrowableShouldCatchNonFatal: Result = {
        import CatsEffectRunner.*
        given ticket: Ticker = Ticker(TestContext())

        val es: ExecutorService    = ConcurrentSupport.newExecutorService()
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = expectedExpcetion.asLeft[Int]

        Fx[IO].catchNonFatalThrowable(fa).completeAs(expected)
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanCatch_IO_catchNonFatalThrowableShouldNotCatchFatal: Result = {

        val es: ExecutorService    = ConcurrentSupport.newExecutorService()
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

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
        import CatsEffectRunner.*
        given ticket: Ticker = Ticker(TestContext())

        val es: ExecutorService    = ConcurrentSupport.newExecutorService()
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

        val fa: IO[Int] = run[IO, Int](1)
        val expected    = 1.asRight[Throwable]
        Fx[IO].catchNonFatalThrowable(fa).completeAs(expected)
      }

      def testCanCatch_IO_catchNonFatalShouldCatchNonFatal: Result = {
        import CatsEffectRunner.*
        given ticket: Ticker = Ticker(TestContext())

        val es: ExecutorService    = ConcurrentSupport.newExecutorService()
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[IO, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]

        Fx[IO].catchNonFatal(fa)(SomeError.someThrowable).completeAs(expected)
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanCatch_IO_catchNonFatalShouldNotCatchFatal: Result = {

        val es: ExecutorService    = ConcurrentSupport.newExecutorService()
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

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
        import CatsEffectRunner.*
        given ticket: Ticker = Ticker(TestContext())

        val es: ExecutorService    = ConcurrentSupport.newExecutorService()
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

        val fa: IO[Int] = run[IO, Int](1)
        val expected    = 1.asRight[SomeError]
        val actual      = Fx[IO].catchNonFatal(fa)(SomeError.someThrowable)

        actual.completeAs(expected)
      }

      def testCanCatch_IO_catchNonFatalEitherShouldCatchNonFatal: Result = {
        import CatsEffectRunner.*
        given ticket: Ticker = Ticker(TestContext())

        val es: ExecutorService    = ConcurrentSupport.newExecutorService()
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual            = Fx[IO].catchNonFatalEither(fa)(SomeError.someThrowable)

        actual.completeAs(expected)
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanCatch_IO_catchNonFatalEitherShouldNotCatchFatal: Result = {

        val es: ExecutorService    = ConcurrentSupport.newExecutorService()
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

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
        import CatsEffectRunner.*
        given ticket: Ticker = Ticker(TestContext())

        val es: ExecutorService    = ConcurrentSupport.newExecutorService()
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].catchNonFatalEither(fa)(SomeError.someThrowable)

        actual.completeAs(expected)
      }

      def testCanCatch_IO_catchNonFatalEitherShouldReturnFailedResult: Result = {
        import CatsEffectRunner.*
        given ticket: Ticker = Ticker(TestContext())

        val es: ExecutorService    = ConcurrentSupport.newExecutorService()
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[IO].catchNonFatalEither(fa)(SomeError.someThrowable)

        actual.completeAs(expected)
      }

      def testCanCatch_IO_catchNonFatalEitherTShouldCatchNonFatal: Result = {
        import CatsEffectRunner.*
        given ticket: Ticker = Ticker(TestContext())

        val es: ExecutorService    = ConcurrentSupport.newExecutorService()
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
        val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual            = Fx[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

        actual.completeAs(expected)
      }

      @SuppressWarnings(Array("org.wartremover.warts.ToString"))
      def testCanCatch_IO_catchNonFatalEitherTShouldNotCatchFatal: Result = {

        val es: ExecutorService    = ConcurrentSupport.newExecutorService()
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

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
        import CatsEffectRunner.*
        given ticket: Ticker = Ticker(TestContext())

        val es: ExecutorService    = ConcurrentSupport.newExecutorService()
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

        val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

        actual.completeAs(expected)
      }

      def testCanCatch_IO_catchNonFatalEitherTShouldReturnFailedResult: Result = {
        import CatsEffectRunner.*
        given ticket: Ticker = Ticker(TestContext())

        val es: ExecutorService    = ConcurrentSupport.newExecutorService()
        implicit val rt: IORuntime = testing.IoAppUtils.runtime(es)

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[IO].catchNonFatalEitherT(fa)(SomeError.someThrowable).value

        actual.completeAs(expected)
      }

    }

    object CanHandleErrorSpec {

      def testCanHandleError_IO_handleNonFatalWithShouldHandleNonFatalWith: Result = {

        val es: ExecutorService = ConcurrentSupport.newExecutorService()
        given rt: IORuntime     = testing.IoAppUtils.runtime(es)

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

        val es: ExecutorService = ConcurrentSupport.newExecutorService()
        given rt: IORuntime     = testing.IoAppUtils.runtime(es)

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

        val es: ExecutorService = ConcurrentSupport.newExecutorService()
        given rt: IORuntime     = testing.IoAppUtils.runtime(es)

        val fa       = run[IO, Int](1)
        val expected = 1
        val actual   = Fx[IO].handleNonFatalWith(fa)(_ => IO.pure(999)).unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleNonFatalWithEitherShouldHandleNonFatalWith: Result = {

        val es: ExecutorService = ConcurrentSupport.newExecutorService()
        given rt: IORuntime     = testing.IoAppUtils.runtime(es)

        val expectedExpcetion    = new RuntimeException("Something's wrong")
        val fa                   = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
        val actualFailedResult   =
          Fx[IO].handleNonFatalWith(fa)(_ => IO.pure(expectedFailedResult)).unsafeRunSync()

        val expectedSuccessResult = 1.asRight[SomeError]
        val actualSuccessResult   =
          Fx[IO].handleNonFatalWith(fa)(_ => IO.pure(1.asRight[SomeError])).unsafeRunSync()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      def testCanHandleError_IO_handleNonFatalWithEitherShouldNotHandleFatalWith: Result = {

        val es: ExecutorService = ConcurrentSupport.newExecutorService()
        given rt: IORuntime     = testing.IoAppUtils.runtime(es)

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

        val es: ExecutorService = ConcurrentSupport.newExecutorService()
        given rt: IORuntime     = testing.IoAppUtils.runtime(es)

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].handleNonFatalWith(fa)(_ => IO(999.asRight[SomeError])).unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleNonFatalWithEitherShouldReturnFailedResult: Result = {

        val es: ExecutorService = ConcurrentSupport.newExecutorService()
        given rt: IORuntime     = testing.IoAppUtils.runtime(es)

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[IO].handleNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherNonFatalWithShouldHandleNonFatalWith: Result = {

        val es: ExecutorService = ConcurrentSupport.newExecutorService()
        given rt: IORuntime     = testing.IoAppUtils.runtime(es)

        val expectedExpcetion     = new RuntimeException("Something's wrong")
        val fa                    = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
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

        val es: ExecutorService = ConcurrentSupport.newExecutorService()
        given rt: IORuntime     = testing.IoAppUtils.runtime(es)

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

        val es: ExecutorService = ConcurrentSupport.newExecutorService()
        given rt: IORuntime     = testing.IoAppUtils.runtime(es)

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[IO].handleEitherNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherNonFatalWithShouldReturnFailedResult: Result = {

        val es: ExecutorService = ConcurrentSupport.newExecutorService()
        given rt: IORuntime     = testing.IoAppUtils.runtime(es)

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[IO].handleEitherNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherTNonFatalWithShouldHandleNonFatalWith: Result = {

        val es: ExecutorService = ConcurrentSupport.newExecutorService()
        given rt: IORuntime     = testing.IoAppUtils.runtime(es)

        val expectedExpcetion     = new RuntimeException("Something's wrong")
        val fa                    = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
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

        val es: ExecutorService = ConcurrentSupport.newExecutorService()
        given rt: IORuntime     = testing.IoAppUtils.runtime(es)

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

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

        val es: ExecutorService = ConcurrentSupport.newExecutorService()
        given rt: IORuntime     = testing.IoAppUtils.runtime(es)

        val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   =
          Fx[IO].handleEitherTNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).value.unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherTNonFatalWithShouldReturnFailedResult: Result = {

        val es: ExecutorService = ConcurrentSupport.newExecutorService()
        given rt: IORuntime     = testing.IoAppUtils.runtime(es)

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[IO].handleEitherTNonFatalWith(fa)(_ => IO.pure(123.asRight[SomeError])).value.unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleNonFatalShouldHandleNonFatal: Result = {

        val es: ExecutorService = ConcurrentSupport.newExecutorService()
        given rt: IORuntime     = testing.IoAppUtils.runtime(es)

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

        val es: ExecutorService = ConcurrentSupport.newExecutorService()
        given rt: IORuntime     = testing.IoAppUtils.runtime(es)

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

        val es: ExecutorService = ConcurrentSupport.newExecutorService()
        given rt: IORuntime     = testing.IoAppUtils.runtime(es)

        val fa       = run[IO, Int](1)
        val expected = 1
        val actual   = Fx[IO].handleNonFatal(fa)(_ => 999).unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleNonFatalEitherShouldHandleNonFatal: Result = {

        val es: ExecutorService = ConcurrentSupport.newExecutorService()
        given rt: IORuntime     = testing.IoAppUtils.runtime(es)

        val expectedExpcetion    = new RuntimeException("Something's wrong")
        val fa                   = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.message("Recovered Error").asLeft[Int]
        val actualFailedResult   = Fx[IO].handleNonFatal(fa)(_ => expectedFailedResult).unsafeRunSync()

        val expectedSuccessResult = 1.asRight[SomeError]
        val actualSuccessResult   = Fx[IO].handleNonFatal(fa)(_ => 1.asRight[SomeError]).unsafeRunSync()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      def testCanHandleError_IO_handleNonFatalEitherShouldNotHandleFatal: Result = {

        val es: ExecutorService = ConcurrentSupport.newExecutorService()
        given rt: IORuntime     = testing.IoAppUtils.runtime(es)

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

        val es: ExecutorService = ConcurrentSupport.newExecutorService()
        given rt: IORuntime     = testing.IoAppUtils.runtime(es)

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].handleNonFatal(fa)(_ => 999.asRight[SomeError]).unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleNonFatalEitherShouldReturnFailedResult: Result = {

        val es: ExecutorService = ConcurrentSupport.newExecutorService()
        given rt: IORuntime     = testing.IoAppUtils.runtime(es)

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = Fx[IO].handleNonFatal(fa)(_ => 123.asRight[SomeError]).unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherNonFatalShouldHandleNonFatal: Result = {

        val es: ExecutorService = ConcurrentSupport.newExecutorService()
        given rt: IORuntime     = testing.IoAppUtils.runtime(es)

        val expectedExpcetion     = new RuntimeException("Something's wrong")
        val fa                    = run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult  = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult    = Fx[IO]
          .handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int])
          .unsafeRunSync()
        val expectedSuccessResult = 123.asRight[SomeError]
        val actualSuccessResult   =
          Fx[IO].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError]).unsafeRunSync()

        actualFailedResult ==== expectedFailedResult and actualSuccessResult ==== expectedSuccessResult
      }

      def testCanHandleError_IO_handleEitherNonFatalShouldNotHandleFatal: Result = {

        val es: ExecutorService = ConcurrentSupport.newExecutorService()
        given rt: IORuntime     = testing.IoAppUtils.runtime(es)

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

        val es: ExecutorService = ConcurrentSupport.newExecutorService()
        given rt: IORuntime     = testing.IoAppUtils.runtime(es)

        val fa       = run[IO, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError]).unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherNonFatalShouldReturnFailedResult: Result = {

        val es: ExecutorService = ConcurrentSupport.newExecutorService()
        given rt: IORuntime     = testing.IoAppUtils.runtime(es)

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[IO].handleEitherNonFatal(fa)(_ => 123.asRight[SomeError]).unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherTNonFatalShouldHandleNonFatal: Result = {

        val es: ExecutorService = ConcurrentSupport.newExecutorService()
        given rt: IORuntime     = testing.IoAppUtils.runtime(es)

        val expectedExpcetion     = new RuntimeException("Something's wrong")
        val fa                    = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
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

        val es: ExecutorService = ConcurrentSupport.newExecutorService()
        given rt: IORuntime     = testing.IoAppUtils.runtime(es)

        val fatalExpcetion = SomeControlThrowable("Something's wrong")
        val fa             = EitherT(run[IO, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

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

        val es: ExecutorService = ConcurrentSupport.newExecutorService()
        given rt: IORuntime     = testing.IoAppUtils.runtime(es)

        val fa       = EitherT(run[IO, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = Fx[IO].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value.unsafeRunSync()

        actual ==== expected
      }

      def testCanHandleError_IO_handleEitherTNonFatalShouldReturnFailedResult: Result = {

        val es: ExecutorService = ConcurrentSupport.newExecutorService()
        given rt: IORuntime     = testing.IoAppUtils.runtime(es)

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[IO, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          Fx[IO].handleEitherTNonFatal(fa)(_ => 123.asRight[SomeError]).value.unsafeRunSync()

        actual ==== expected
      }

    }

  }

  object FutureSpec {
    import java.util.concurrent.{ExecutorService, Executors}
    import scala.concurrent.duration.*
    import scala.concurrent.{ExecutionContext, Future}

    val waitFor: FiniteDuration = 1.second

    implicit def futureEqual[A: Eq](
      implicit ec: scala.concurrent.ExecutionContext
    ): Eq[Future[A]] =
      (x: Future[A], y: Future[A]) => Await.result(x.flatMap(a => y.map(b => Eq[A].eqv(a, b))), waitFor)

    def testEffectOf: Property = for {
      before <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("before")
      after  <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).map(_ + before).log("after")
    } yield {
      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      var actual               = before
      val testBefore           = actual ==== before
      val future: Future[Unit] = Fx[Future].effectOf({ actual = after; () })
      ConcurrentSupport.futureToValueAndTerminate(future, waitFor)
      val testAfterRun         = actual ==== after
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
      given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      var actual       = before
      val testBefore   = actual ==== before
      val future       = Fx[Future].pureOf({ actual = after; () })
      ConcurrentSupport.futureToValueAndTerminate(future, waitFor)
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
      given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)
      val future                             = Fx[Future].unitOf
      val expected: Unit                     = ()
      val actual: Unit                       = ConcurrentSupport.futureToValueAndTerminate(future, waitFor)
      actual ==== expected
    }

    def testErrorOf: Result = {
      val expectedMessage = "This is a throwable test error."
      val expectedError   = SomeThrowableError.message(expectedMessage)

      given executorService: ExecutorService = Executors.newFixedThreadPool(1)
      given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

      val future = Fx[Future].errorOf[Unit](expectedError)
      expectThrowable(ConcurrentSupport.futureToValueAndTerminate(future, waitFor), expectedError)
    }

    def testMonadLaws1_Identity: Property = {
      given ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

      MonadSpec.test1_Identity[Future]
    }

    def testMonadLaws2_Composition: Property = {
      given ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

      MonadSpec.test2_Composition[Future]
    }

    def testMonadLaws3_IdentityAp: Property = {
      given ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

      MonadSpec.test3_IdentityAp[Future]
    }

    def testMonadLaws4_Homomorphism: Property = {
      given ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

      MonadSpec.test4_Homomorphism[Future]
    }

    def testMonadLaws5_Interchange: Property = {
      given ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

      MonadSpec.test5_Interchange[Future]
    }

    def testMonadLaws6_CompositionAp: Property = {
      given ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

      MonadSpec.test6_CompositionAp[Future]
    }

    def testMonadLaws7_LeftIdentity: Property = {
      given ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

      MonadSpec.test7_LeftIdentity[Future]
    }

    def testMonadLaws8_RightIdentity: Property = {
      given ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

      MonadSpec.test8_RightIdentity[Future]
    }

    def testMonadLaws9_Associativity: Property = {
      given ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

      MonadSpec.test9_Associativity[Future]
    }

    object CanCatchSpec {
      import java.util.concurrent.{ExecutorService, Executors}
      import scala.concurrent.duration.*
      import scala.concurrent.{ExecutionContext, Future}

      val waitFor: FiniteDuration = 1.second

      def testCanCatch_Future_catchNonFatalThrowableShouldCatchNonFatal: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = expectedExpcetion.asLeft[Int]
        val actual            = ConcurrentSupport.futureToValueAndTerminate(
          Fx[Future].catchNonFatalThrowable(fa),
          waitFor
        )

        actual ==== expected
      }

      def testCanCatch_Future_catchNonFatalThrowableShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val fa       = run[Future, Int](1)
        val expected = 1.asRight[SomeError]
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          Fx[Future].catchNonFatalThrowable(fa),
          waitFor
        )

        actual ==== expected
      }

      def testCanCatch_Future_catchNonFatalShouldCatchNonFatal: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual            = ConcurrentSupport.futureToValueAndTerminate(
          Fx[Future].catchNonFatal(fa)(SomeError.someThrowable),
          waitFor
        )

        actual ==== expected
      }

      def testCanCatch_Future_catchNonFatalShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val fa       = run[Future, Int](1)
        val expected = 1.asRight[SomeError]
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          Fx[Future].catchNonFatal(fa)(SomeError.someThrowable),
          waitFor
        )

        actual ==== expected
      }

      def testCanCatch_Future_catchNonFatalEitherShouldCatchNonFatal: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual            = ConcurrentSupport.futureToValueAndTerminate(
          Fx[Future].catchNonFatalEither(fa)(SomeError.someThrowable),
          waitFor
        )

        actual ==== expected
      }

      def testCanCatch_Future_catchNonFatalEitherShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          Fx[Future].catchNonFatalEither(fa)(SomeError.someThrowable),
          waitFor
        )

        actual ==== expected
      }

      def testCanCatch_Future_catchNonFatalEitherShouldReturnFailedResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = ConcurrentSupport.futureToValueAndTerminate(
          Fx[Future].catchNonFatalEither(fa)(SomeError.someThrowable),
          waitFor
        )

        actual ==== expected
      }

      def testCanCatch_Future_catchNonFatalEitherTShouldCatchNonFatal: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
        val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual            = ConcurrentSupport.futureToValueAndTerminate(
          Fx[Future].catchNonFatalEitherT(fa)(SomeError.someThrowable).value,
          waitFor
        )

        actual ==== expected
      }

      def testCanCatch_Future_catchNonFatalEitherTShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          Fx[Future].catchNonFatalEitherT(fa)(SomeError.someThrowable).value,
          waitFor
        )

        actual ==== expected
      }

      def testCanCatch_Future_catchNonFatalEitherTShouldReturnFailedResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          = ConcurrentSupport.futureToValueAndTerminate(
          Fx[Future].catchNonFatalEitherT(fa)(SomeError.someThrowable).value,
          waitFor
        )

        actual ==== expected
      }
    }

    object CanHandleErrorSpec {
      import java.util.concurrent.{ExecutorService, Executors}
      import scala.concurrent.duration.*
      import scala.concurrent.{ExecutionContext, Future}

      val waitFor: FiniteDuration = 1.second

      def testCanHandleError_Future_handleNonFatalWithShouldHandleNonFatalWith: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 1
        val actual            = ConcurrentSupport.futureToValueAndTerminate[Int](
          Fx[Future].handleNonFatalWith(fa)(_ => Future(expected)),
          waitFor
        )

        actual ==== expected
      }

      def testCanHandleError_Future_handleNonFatalWithShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val fa       = run[Future, Int](1)
        val expected = 1
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          Fx[Future].handleNonFatalWith(fa)(_ => Future(123)),
          waitFor
        )

        actual ==== expected
      }

      def testCanHandleError_Future_handleNonFatalWithEitherShouldHandleNonFatalWith: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val expectedExpcetion    = new RuntimeException("Something's wrong")
        val fa                   = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   =
          ConcurrentSupport.futureToValue(
            Fx[Future].handleNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int])),
            waitFor
          )

        val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected = 1.asRight[SomeError]
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          Fx[Future].handleNonFatalWith(fa2)(_ => Future(expected)),
          waitFor
        )

        expectedFailedResult ==== actualFailedResult and actual ==== expected
      }

      def testCanHandleError_Future_handleNonFatalWithEitherShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   =
          ConcurrentSupport.futureToValueAndTerminate(
            Fx[Future].handleNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int])),
            waitFor
          )

        actual ==== expected
      }

      def testCanHandleError_Future_handleNonFatalWithEitherShouldReturnFailedResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = ConcurrentSupport.futureToValueAndTerminate(
          Fx[Future].handleNonFatalWith(fa)(_ => Future(1.asRight[SomeError])),
          waitFor
        )

        actual ==== expected
      }

      def testCanHandleError_Future_handleEitherNonFatalWithShouldHandleNonFatalWith: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val expectedExpcetion    = new RuntimeException("Something's wrong")
        val fa                   = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
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
            Fx[Future].handleEitherNonFatalWith(fa2)(err => Future(expected)),
            waitFor
          )

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      def testCanHandleError_Future_handleEitherNonFatalWithShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          Fx[Future]
            .handleEitherNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int])),
          waitFor
        )

        actual ==== expected
      }

      def testCanHandleError_Future_handleEitherNonFatalWithShouldReturnFailedResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          ConcurrentSupport.futureToValueAndTerminate(
            Fx[Future].handleEitherNonFatalWith(fa)(_ => Future(expected)),
            waitFor
          )

        actual ==== expected
      }

      def testCanHandleError_Future_handleEitherTNonFatalWithShouldHandleNonFatalWith: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val expectedExpcetion    = new RuntimeException("Something's wrong")
        val fa                   = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
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
            Fx[Future].handleEitherTNonFatalWith(fa2)(err => Future(expected)).value,
            waitFor
          )

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      def testCanHandleError_Future_handleEitherTNonFatalWithShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          Fx[Future]
            .handleEitherTNonFatalWith(fa)(err => Future(SomeError.someThrowable(err).asLeft[Int]))
            .value,
          waitFor
        )

        actual ==== expected
      }

      def testCanHandleError_Future_handleEitherTNonFatalWithShouldReturnFailedResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          ConcurrentSupport.futureToValueAndTerminate(
            Fx[Future].handleEitherTNonFatalWith(fa)(_ => Future(expected)).value,
            waitFor
          )

        actual ==== expected
      }

      def testCanHandleError_Future_handleNonFatalShouldHandleNonFatal: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val expectedExpcetion = new RuntimeException("Something's wrong")
        val fa                = run[Future, Int](throwThrowable[Int](expectedExpcetion))
        val expected          = 1
        val actual            = ConcurrentSupport.futureToValueAndTerminate[Int](
          Fx[Future].handleNonFatal(fa)(_ => expected),
          waitFor
        )

        actual ==== expected
      }

      def testCanHandleError_Future_handleNonFatalShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val fa       = run[Future, Int](1)
        val expected = 1
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          Fx[Future].handleNonFatal(fa)(_ => 123),
          waitFor
        )

        actual ==== expected
      }

      def testCanHandleError_Future_handleNonFatalEitherShouldHandleNonFatal: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val expectedExpcetion    = new RuntimeException("Something's wrong")
        val fa                   = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   =
          ConcurrentSupport.futureToValue(
            Fx[Future].handleNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]),
            waitFor
          )

        val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected = 1.asRight[SomeError]
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          Fx[Future].handleNonFatal(fa2)(_ => expected),
          waitFor
        )

        expectedFailedResult ==== actualFailedResult and actual ==== expected
      }

      def testCanHandleError_Future_handleNonFatalEitherShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   =
          ConcurrentSupport.futureToValueAndTerminate(
            Fx[Future].handleNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]),
            waitFor
          )

        actual ==== expected
      }

      def testCanHandleError_Future_handleNonFatalEitherShouldReturnFailedResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          = ConcurrentSupport.futureToValueAndTerminate(
          Fx[Future].handleNonFatal(fa)(_ => 1.asRight[SomeError]),
          waitFor
        )

        actual ==== expected
      }

      def testCanHandleError_Future_handleEitherNonFatalShouldHandleNonFatal: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val expectedExpcetion    = new RuntimeException("Something's wrong")
        val fa                   = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expectedFailedResult = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actualFailedResult   = ConcurrentSupport.futureToValue(
          Fx[Future].handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]),
          waitFor
        )

        val fa2      = run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected = 1.asRight[SomeError]
        val actual   =
          ConcurrentSupport.futureToValueAndTerminate(
            Fx[Future].handleEitherNonFatal(fa2)(err => expected),
            waitFor
          )

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      def testCanHandleError_Future_handleEitherNonFatalShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val fa       = run[Future, Either[SomeError, Int]](1.asRight[SomeError])
        val expected = 1.asRight[SomeError]
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          Fx[Future].handleEitherNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]),
          waitFor
        )

        actual ==== expected
      }

      def testCanHandleError_Future_handleEitherNonFatalShouldReturnFailedResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val expectedFailure = SomeError.message("Failed")
        val fa              = run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int])
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          ConcurrentSupport.futureToValueAndTerminate(
            Fx[Future].handleEitherNonFatal(fa)(_ => expected),
            waitFor
          )

        actual ==== expected
      }

      def testCanHandleError_Future_handleEitherTNonFatalShouldHandleNonFatal: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val expectedExpcetion    = new RuntimeException("Something's wrong")
        val fa                   = EitherT(run[Future, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion)))
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
            Fx[Future].handleEitherTNonFatal(fa2)(err => expected).value,
            waitFor
          )

        actualFailedResult ==== expectedFailedResult and actual ==== expected
      }

      def testCanHandleError_Future_handleEitherTNonFatalShouldReturnSuccessfulResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val fa       = EitherT(run[Future, Either[SomeError, Int]](1.asRight[SomeError]))
        val expected = 1.asRight[SomeError]
        val actual   = ConcurrentSupport.futureToValueAndTerminate(
          Fx[Future].handleEitherTNonFatal(fa)(err => SomeError.someThrowable(err).asLeft[Int]).value,
          waitFor
        )

        actual ==== expected
      }

      def testCanHandleError_Future_handleEitherTNonFatalShouldReturnFailedResult: Result = {

        given executorService: ExecutorService = Executors.newFixedThreadPool(1)
        given ec: ExecutionContext             = ConcurrentSupport.newExecutionContext(executorService)

        val expectedFailure = SomeError.message("Failed")
        val fa              = EitherT(run[Future, Either[SomeError, Int]](expectedFailure.asLeft[Int]))
        val expected        = expectedFailure.asLeft[Int]
        val actual          =
          ConcurrentSupport.futureToValueAndTerminate(
            Fx[Future].handleEitherTNonFatal(fa)(_ => expected).value,
            waitFor
          )

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

      lazy val actual = Fx[Id].errorOf[Unit](expectedError)
      expectThrowable(actual, expectedError)
    }

    given idInstance: Monad[Id] = cats.catsInstancesForId

    def testMonadLaws1_Identity: Property =
      MonadSpec.test1_Identity[Id]

    def testMonadLaws2_Composition: Property =
      MonadSpec.test2_Composition[Id]

    def testMonadLaws3_IdentityAp: Property =
      MonadSpec.test3_IdentityAp[Id]

    def testMonadLaws4_Homomorphism: Property =
      MonadSpec.test4_Homomorphism[Id]

    def testMonadLaws5_Interchange: Property =
      MonadSpec.test5_Interchange[Id]

    def testMonadLaws6_CompositionAp: Property =
      MonadSpec.test6_CompositionAp[Id]

    def testMonadLaws7_LeftIdentity: Property =
      MonadSpec.test7_LeftIdentity[Id]

    def testMonadLaws8_RightIdentity: Property =
      MonadSpec.test8_RightIdentity[Id]

    def testMonadLaws9_Associativity: Property =
      MonadSpec.test9_Associativity[Id]

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
        lazy val fa           = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
        val expected          = SomeError.someThrowable(expectedExpcetion).asLeft[Int]
        val actual            = Fx[Id].catchNonFatalEither(fa)(SomeError.someThrowable)

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
        lazy val fa        = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

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
      import effectie.cats.Fx.given
      import effectie.cats.CanHandleError.idCanHandleError

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

        val expectedExpcetion    = new RuntimeException("Something's wrong")
        lazy val fa              = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
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

        val expectedExpcetion    = new RuntimeException("Something's wrong")
        lazy val fa              = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
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
        lazy val fa        = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

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

        val expectedExpcetion    = new RuntimeException("Something's wrong")
        lazy val fa              = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
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

        val expectedExpcetion    = new RuntimeException("Something's wrong")
        lazy val fa              = run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](expectedExpcetion))
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
        lazy val fa        = EitherT(run[Id, Either[SomeError, Int]](throwThrowable[Either[SomeError, Int]](fatalExpcetion)))

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

  }

}
