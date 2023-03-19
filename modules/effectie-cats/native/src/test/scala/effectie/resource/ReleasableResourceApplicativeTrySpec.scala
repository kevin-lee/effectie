package effectie.resource

import cats.Eq
import effectie.testing.cats.Specs
import hedgehog._
import hedgehog.runner._

import scala.util.Try

/** @author Kevin Lee
  * @since 2023-05-17
  */
object ReleasableResourceApplicativeTrySpec extends Properties with ReleasableResourceApplicativeSpec {

  override def tests: List[Test] = {
    implicit val toF: Int => Try[Int] = Try(_)
    implicit def eqF: Eq[Try[Int]]    = Eq.fromUniversalEquals

    implicit val resourceMaker: ResourceMaker[Try] = ResourceMaker.tryResourceMaker

    List(
      property(
        "test Applicative Law - Identity for ReleasableResource[Try, *]",
        Specs
          .ApplicativeLaws
          .identity[ReleasableResource[Try, *]](genReleasableResourceFunctor[Try](ReleasableResource.pureTry[Int])),
      ),
      property(
        "test Applicative Law - Composition for ReleasableResource[Try, *]",
        Specs
          .ApplicativeLaws
          .composition[ReleasableResource[Try, *]](
            genReleasableResourceFunctor[Try](ReleasableResource.pureTry[Int]),
            genF,
          ),
      ),
      property(
        "test Applicative Law - IdentityAp for ReleasableResource[Try, *]",
        Specs
          .ApplicativeLaws
          .identityAp[ReleasableResource[Try, *]](
            genReleasableResourceFunctor[Try](ReleasableResource.pureTry[Int])
          ),
      ),
      property(
        "test Applicative Law - Homomorphism for ReleasableResource[Try, *]",
        Specs
          .ApplicativeLaws
          .homomorphism[ReleasableResource[Try, *]](
            Gen.int(Range.linear(Int.MinValue, Int.MaxValue)),
            genF,
          ),
      ),
      property(
        "test Applicative Law - Interchange for ReleasableResource[Try, *]",
        Specs
          .ApplicativeLaws
          .interchange[ReleasableResource[Try, *]](
            Gen.int(Range.linear(Int.MinValue, Int.MaxValue)),
            genF,
          ),
      ),
      property(
        "test Applicative Law - CompositionAp for ReleasableResource[Try, *]",
        Specs
          .ApplicativeLaws
          .compositionAp[ReleasableResource[Try, *]](
            genReleasableResourceFunctor[Try](ReleasableResource.pureTry[Int]),
            genF,
          ),
      ),
      property(
        "test ReleasableResource[Try, *].map",
        testMap[Try](
          ReleasableResource.pureTry,
          Try(_),
          _.fold(
            err => Result.failure.log(s"Error: ${err.getMessage}"),
            identity,
          ),
        ),
      ),
      property(
        "test ReleasableResource[Try, *].ap",
        testAp[Try](
          ReleasableResource.pureTry,
          Try(_),
          _.fold(
            err => Result.failure.log(s"Error: ${err.getMessage}"),
            identity,
          ),
        ),
      ),
    )
  }

}
