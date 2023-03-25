package effectie.instances.tries

import effectie.specs.fxCtorSpec.TrySpecs
import hedgehog.runner._

/** @author Kevin Lee
  * @since 2023-03-17
  */
object fxCtorSpec extends Properties {

  override def tests: List[Test] =
    trySpecs

  private val trySpecs = {
    import effectie.instances.tries.fxCtor._
    TrySpecs.trySpecs
  }
}
