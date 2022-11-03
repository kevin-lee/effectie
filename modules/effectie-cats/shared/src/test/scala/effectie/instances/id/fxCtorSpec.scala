package effectie.instances.id

import effectie.specs.fxCtorSpec.IdSpecs
import hedgehog.runner._

/** @author Kevin Lee
  * @since 2020-12-06
  */
object fxCtorSpec extends Properties {

  override def tests: List[Test] =
    futureSpecs ++
      idSpecs

  private val futureSpecs = effectie.core.FxCtorSpec.futureSpecs

  import effectie.instances.id.fxCtor._

  private val idSpecs = List(
    property("test FxCtor[Id].effectOf", IdSpecs.testEffectOf),
    property("test FxCtor[Id].pureOf", IdSpecs.testPureOf),
    property("test FxCtor[Id].pureOrError(success case)", IdSpecs.testPureOrErrorSuccessCase),
    example("test FxCtor[Id].pureOrError(error case)", IdSpecs.testPureOrErrorErrorCase),
    example("test FxCtor[Id].unitOf", IdSpecs.testUnitOf),
    example("test FxCtor[Id].errorOf", IdSpecs.testErrorOf),
    property("test FxCtor[Id].fromEither(Right)", IdSpecs.testFromEitherRightCase),
    property("test FxCtor[Id].fromEither(Left)", IdSpecs.testFromEitherLeftCase),
    property("test FxCtor[Id].fromOption(Some)", IdSpecs.testFromOptionSomeCase),
    property("test FxCtor[Id].fromOption(None)", IdSpecs.testFromOptionNoneCase),
    property("test FxCtor[Id].fromTry(Success)", IdSpecs.testFromTrySuccessCase),
    property("test FxCtor[Id].fromTry(Failure)", IdSpecs.testFromTryFailureCase),
  )

}
