package effectie.resource

import cats.Eq
import cats.syntax.all._
import effectie.testing.cats.Specs
import hedgehog.runner._

import scala.concurrent.Future
import scala.concurrent.duration._

/** @author Kevin Lee
  * @since 2023-05-17
  */
object ReleasableResourceFunctorFutureSpec extends Properties with ReleasableResourceFunctorSpec {

  override def tests: List[Test] = {
    implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

    implicit val resourceMaker: ResourceMaker[Future] = ResourceMaker.futureResourceMaker

    implicit val toF: Int => Future[Int] = Future.successful
    implicit def eqF: Eq[Future[Int]]    =
      (a, b) => scala.concurrent.Await.result(a.flatMap(aVal => b.map(aVal === _)), 1.second)

    List(
      property(
        "test Functor Law - Identity for ReleasableResource[Future, *]",
        Specs
          .FunctorLaws
          .identity[ReleasableResource[Future, *]](
            genReleasableResourceFunctor[Future](ReleasableResource.pureFuture[Int])
          ),
      ),
      property(
        "test Functor Law - Composition for ReleasableResource[Future, *]",
        Specs
          .FunctorLaws
          .composition[ReleasableResource[Future, *]](
            genReleasableResourceFunctor[Future](ReleasableResource.pureFuture[Int]),
            genF,
          ),
      ),
      property(
        "test ReleasableResource[Future, *].map", {
          implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
          testMap[Future](
            ReleasableResource.pureFuture,
            Future.successful,
            scala.concurrent.Await.result(_, 1.second),
          )
        },
      ),
    )
  }

}
