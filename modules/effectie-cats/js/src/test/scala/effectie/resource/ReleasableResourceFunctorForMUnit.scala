package effectie.resource

import cats.syntax.all._
import cats.{Eq, Functor}
import effectie.testing.cats.LawsF
import effectie.testing.cats.LawsF.EqF
import effectie.testing.{FutureTools, RandomGens}
import munit.Assertions

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2023-05-17
  */
class ReleasableResourceFunctorForMUnit extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  override val munitTimeout: FiniteDuration = 200.milliseconds

  implicit def releasableResourceEq[F[*]](implicit eq: Eq[F[Int]], toF: Int => F[Int]): Eq[ReleasableResource[F, Int]] =
    (resource1, resource2) => eq.eqv(resource1.use(toF), resource2.use(toF))

  implicit val resourceMaker: ResourceMaker[Future] = ResourceMaker.futureResourceMaker

  implicit val toF: Int => Future[Int] = Future.successful

  implicit def eqF: EqF[ReleasableResource[Future, *], Int] =
    (a, b) => a.flatMap(aVal => b.map(aVal === _))

  test("test Functor Law - Identity for ReleasableResource[Future, *]") {
    val n = 123
    LawsF
      .FunctorLaws
      .identity[ReleasableResource[Future, *], Int](
        ReleasableResource.pureFuture[Int](n)
      )
      .use[Unit](result => Future.successful(Assertions.assert(result)))
  }
  test("test Functor Law - Composition for ReleasableResource[Future, *]") {
    LawsF
      .FunctorLaws
      .composition[ReleasableResource[Future, *], Int, Int, Int](
        genReleasableResourceFunctor[Future](ReleasableResource.pureFuture[Int]),
        genF(),
        genF(),
      )
  }
  test("test ReleasableResource[Future, *].map") {
    testMap[Future](
      ReleasableResource.pureFuture,
      Future.successful,
    )
  }

  def testMap[F[*]: ResourceMaker](
    ctor: Int => ReleasableResource[F, Int],
    toF: Unit => F[Unit],
  ): F[Unit] = {
    val n        = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)
    val f        = genF()
    val resource = ctor(n)
    val expected = f(n)
    mapF[ReleasableResource[F, *]](resource)(f)
      .map(Assertions.assertEquals(_, expected))
      .use(toF)
  }

  def genReleasableResourceFunctor[F[*]](ctor: Int => ReleasableResource[F, Int]): ReleasableResource[F, Int] =
    ctor(RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue))

  val int2IntList: List[Int => Int] = List(_ * 2, _ + 100, _ / 2)

  def genF(): Int => Int = int2IntList(scala.util.Random.nextInt(int2IntList.length))

  def mapF[G[*]: Functor](r: G[Int])(f: Int => Int) = r.map(f)

}
