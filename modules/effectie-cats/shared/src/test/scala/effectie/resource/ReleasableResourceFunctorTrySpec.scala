package effectie.resource

import cats.Eq
import effectie.testing.cats.Specs
import hedgehog._
import hedgehog.runner._

import scala.util.Try

/** @author Kevin Lee
  * @since 2023-05-17
  */
object ReleasableResourceFunctorTrySpec extends Properties with ReleasableResourceFunctorSpec {

  override def tests: List[Test] = {
    implicit val toF: Int => Try[Int] = Try(_)
    implicit def eqF: Eq[Try[Int]]    = Eq.fromUniversalEquals

    implicit val resourceMaker: ResourceMaker[Try] = ResourceMaker.tryResourceMaker

    List(
      property(
        "test Functor Law - Identity for ReleasableResource[Try, *]",
        Specs
          .FunctorLaws
          .identity[ReleasableResource[Try, *]](genReleasableResourceFunctor[Try](ReleasableResource.pureTry[Int])),
      ),
      property(
        "test Functor Law - Composition for ReleasableResource[Try, *]",
        Specs
          .FunctorLaws
          .composition[ReleasableResource[Try, *]](
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
    )
  }

}
