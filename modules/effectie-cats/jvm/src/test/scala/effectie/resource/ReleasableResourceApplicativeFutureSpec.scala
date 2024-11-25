package effectie.resource

import cats.Eq
import cats.syntax.all._
import effectie.testing.cats.Specs
import hedgehog._
import hedgehog.runner._

import scala.concurrent.Future
import scala.concurrent.duration._

/** @author Kevin Lee
  * @since 2023-05-17
  */
object ReleasableResourceApplicativeFutureSpec extends Properties with ReleasableResourceApplicativeSpec {

  override def tests: List[Test] = {
    implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

    implicit val toF: Int => Future[Int] = Future.successful

    implicit def eqF: Eq[Future[Int]] =
      (a, b) => scala.concurrent.Await.result(a.flatMap(aVal => b.map(aVal === _)), 1.second)

    implicit val resourceMaker: ResourceMaker[Future] = ResourceMaker.futureResourceMaker

    List(
      property(
        "test Applicative Law - Identity for ReleasableResource[Future, *]",
        Specs
          .ApplicativeLaws
          .identity[ReleasableResource[Future, *]](
            genReleasableResourceFunctor[Future](ReleasableResource.pureFuture[Int])
          ),
      ),
      property(
        "test Applicative Law - Composition for ReleasableResource[Future, *]",
        Specs
          .ApplicativeLaws
          .composition[ReleasableResource[Future, *]](
            genReleasableResourceFunctor[Future](ReleasableResource.pureFuture[Int]),
            genF,
          ),
      ),
      property(
        "test Applicative Law - IdentityAp for ReleasableResource[Future, *]",
        Specs
          .ApplicativeLaws
          .identityAp[ReleasableResource[Future, *]](
            genReleasableResourceFunctor[Future](ReleasableResource.pureFuture[Int])
          ),
      ),
      property(
        "test Applicative Law - Homomorphism for ReleasableResource[Future, *]",
        Specs
          .ApplicativeLaws
          .homomorphism[ReleasableResource[Future, *]](
            Gen.int(Range.linear(Int.MinValue, Int.MaxValue)),
            genF,
          ),
      ),
      property(
        "test Applicative Law - Interchange for ReleasableResource[Future, *]",
        Specs
          .ApplicativeLaws
          .interchange[ReleasableResource[Future, *]](
            Gen.int(Range.linear(Int.MinValue, Int.MaxValue)),
            genF,
          ),
      ),
      property(
        "test Applicative Law - CompositionAp for ReleasableResource[Future, *]",
        Specs
          .ApplicativeLaws
          .compositionAp[ReleasableResource[Future, *]](
            genReleasableResourceFunctor[Future](ReleasableResource.pureFuture[Int]),
            genF,
          ),
      ),
      property(
        "test ReleasableResource[Future, *].map", {
          implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
          implicit val resourceMaker: ResourceMaker[Future]  = ResourceMaker.futureResourceMaker
          testMap[Future](
            ReleasableResource.pureFuture,
            Future.successful,
            scala.concurrent.Await.result(_, 1.second),
          )
        },
      ),
      property(
        "test ReleasableResource[Future, *].ap", {
          implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
          implicit val resourceMaker: ResourceMaker[Future]  = ResourceMaker.futureResourceMaker
          testAp[Future](
            ReleasableResource.pureFuture,
            Future.successful,
            scala.concurrent.Await.result(_, 1.second),
          )
        },
      ),
    )
  }

}
