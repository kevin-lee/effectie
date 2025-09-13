package effectie.resource

import cats.syntax.all._
import effectie.testing.cats.LawsF
import effectie.testing.cats.LawsF.EqF
import effectie.testing.{FutureTools, RandomGens}
import munit.Assertions

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2023-05-17
  */
class ReleasableResourceApplicativeFutureSpec
    extends munit.FunSuite
    with FutureTools
    with ReleasableResourceApplicativeForMunit {

  implicit val ec: ExecutionContext = globalExecutionContext

  override val munitTimeout: FiniteDuration = 200.milliseconds

  implicit val toF: Int => Future[Int] = Future.successful

  implicit def eqF: EqF[ReleasableResource[Future, *], Int] =
    (a, b) => a.flatMap(aVal => b.map(aVal === _))

  implicit val resourceMaker: ResourceMaker[Future] = ResourceMaker.futureResourceMaker

  test("test Applicative Law - Identity for ReleasableResource[Future, *]") {

    val n = 123
    LawsF
      .ApplicativeLaws
      .identity[ReleasableResource[Future, *], Int](ReleasableResource.pureFuture[Int](n))
      .use[Unit](result => Future.successful(Assertions.assert(result)))

  }
  test("test Applicative Law - Composition for ReleasableResource[Future, *]") {

    LawsF
      .ApplicativeLaws
      .composition[ReleasableResource[Future, *], Int, Int, Int](
        genReleasableResourceFunctor[Future](ReleasableResource.pureFuture[Int]),
        genA(),
        genA(),
      )
      .use(result => Future.successful(Assertions.assert(result)))
  }
  test("test Applicative Law - IdentityAp for ReleasableResource[Future, *]") {

    val n = 123
    LawsF
      .ApplicativeLaws
      .identityAp[ReleasableResource[Future, *], Int](ReleasableResource.pureFuture[Int](n))
      .use(result => Future.successful(Assertions.assert(result)))
  }
  test("test Applicative Law - Homomorphism for ReleasableResource[Future, *]") {
    val n = RandomGens.genRandomInt()
    LawsF
      .ApplicativeLaws
      .homomorphism[ReleasableResource[Future, *], Int, Int](
        genA(),
        n,
      )
      .use(result => Future.successful(Assertions.assert(result)))
  }
  test("test Applicative Law - Interchange for ReleasableResource[Future, *]") {
    val n = RandomGens.genRandomInt()
    LawsF
      .ApplicativeLaws
      .interchange[ReleasableResource[Future, *], Int, Int](
        n,
        ReleasableResource.pureFuture(genA()),
      )
      .use(result => Future.successful(Assertions.assert(result)))
  }
  test("test Applicative Law - CompositionAp for ReleasableResource[Future, *]") {
    LawsF
      .ApplicativeLaws
      .compositionAp[ReleasableResource[Future, *], Int, Int, Int](
        genReleasableResourceFunctor[Future](ReleasableResource.pureFuture[Int]),
        ReleasableResource.pureFuture(genA()),
        ReleasableResource.pureFuture(genA()),
      )
      .use(result => Future.successful(Assertions.assert(result)))
  }
  test("test ReleasableResource[Future, *].map") {
    implicit val resourceMaker: ResourceMaker[Future] = ResourceMaker.futureResourceMaker
    testMap[Future](
      ReleasableResource.pureFuture,
      Future.successful,
    )
  }
  test("test ReleasableResource[Future, *].ap") {
    implicit val resourceMaker: ResourceMaker[Future] = ResourceMaker.futureResourceMaker
    testAp[Future](
      ReleasableResource.pureFuture,
      Future.successful,
    )
  }

}
