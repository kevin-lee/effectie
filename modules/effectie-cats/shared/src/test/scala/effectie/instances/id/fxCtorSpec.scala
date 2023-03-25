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

  private val futureSpecs = effectie.instances.future.fxCtorSpec.futureSpecs

  import effectie.instances.id.fxCtor._

  private val idSpecs = IdSpecs.idSpecs

}
