package effectie.instances.tries

import effectie.specs.fxCtorSpec.TrySpecs
import hedgehog.runner._

/** @author Kevin Lee
  * @since 2023-03-17
  */
object fxCtorSpec extends Properties {

  override def tests: List[Test] =
    trySpecs

  import effectie.instances.tries.fxCtor._

  private val trySpecs = List(
    property("test FxCtor[Try].effectOf", TrySpecs.testEffectOf),
    property("test FxCtor[Try].pureOf", TrySpecs.testPureOf),
    property("test FxCtor[Try].pureOrError(success case)", TrySpecs.testPureOrErrorSuccessCase),
    example("test FxCtor[Try].pureOrError(error case)", TrySpecs.testPureOrErrorErrorCase),
    example("test FxCtor[Try].unitOf", TrySpecs.testUnitOf),
    example("test FxCtor[Try].errorOf", TrySpecs.testErrorOf),
    property("test FxCtor[Try].fromEither(Right)", TrySpecs.testFromEitherRightCase),
    property("test FxCtor[Try].fromEither(Left)", TrySpecs.testFromEitherLeftCase),
    property("test FxCtor[Try].fromOption(Some)", TrySpecs.testFromOptionSomeCase),
    property("test FxCtor[Try].fromOption(None)", TrySpecs.testFromOptionNoneCase),
    property("test FxCtor[Try].fromTry(Success)", TrySpecs.testFromTrySuccessCase),
    property("test FxCtor[Try].fromTry(Failure)", TrySpecs.testFromTryFailureCase),
  )

}
