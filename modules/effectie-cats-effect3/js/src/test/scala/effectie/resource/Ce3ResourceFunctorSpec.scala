package effectie.resource

import cats.effect.IO
import cats.syntax.all._
import cats.{Eq, Functor}
import effectie.testing.RandomGens
import effectie.testing.cats.LawsF
import effectie.testing.cats.LawsF.EqF
import munit.Assertions

/** @author Kevin Lee
  * @since 2023-05-22
  */
class Ce3ResourceFunctorSpec extends munit.CatsEffectSuite {

  final type F[A] = IO[A]
  final val F = IO // scalafix:ok DisableSyntax.noFinalVal

  implicit def releasableResourceEq[G[*]](implicit eq: Eq[G[Int]], toF: Int => G[Int]): Eq[ReleasableResource[G, Int]] =
    (resource1, resource2) => eq.eqv(resource1.use(toF), resource2.use(toF))

  implicit val resourceMaker: ResourceMaker[F] = Ce3ResourceMaker.maker[F]

  implicit val toF: Int => F[Int] = F.delay(_)

  implicit def eqF: EqF[ReleasableResource[F, *], Int] =
    (a, b) => a.flatMap(aVal => b.map(aVal === _))

  test("test Functor Law - Identity for ReleasableResource[IO, *]") {
    val n = 123
    LawsF
      .FunctorLaws
      .identity[ReleasableResource[F, *], Int](
        Ce3Resource.pure[F, Int](n)
      )
      .use[Unit](result => F.pure(Assertions.assert(result)))
      .unsafeToFuture()
  }

  test("test Functor Law - Composition for ReleasableResource[IO, *]") {
    LawsF
      .FunctorLaws
      .composition[ReleasableResource[F, *], Int, Int, Int](
        genReleasableResourceFunctor[F](Ce3Resource.pure[F, Int]),
        genA(),
        genA(),
      )
      .use[Unit](result => F.pure(Assertions.assert(result)))
      .unsafeToFuture()
  }

  test("test ReleasableResource[IO, *].map") {
    testMap[F](
      Ce3Resource.pure,
      F.pure,
    )
      .handleError(err => Assertions.fail(s"Error: ${err.getMessage}"))
      .unsafeToFuture()
  }

  def testMap[G[*]: ResourceMaker](
    ctor: Int => ReleasableResource[G, Int],
    toF: Unit => G[Unit],
  ): G[Unit] = {
    val n        = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)
    val f        = genA()
    val resource = ctor(n)
    val expected = f(n)
    mapF[ReleasableResource[G, *]](resource)(f)
      .map(Assertions.assertEquals(_, expected))
      .use(toF)
  }

  def genReleasableResourceFunctor[G[*]](ctor: Int => ReleasableResource[G, Int]): ReleasableResource[G, Int] =
    ctor(RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue))

  val int2IntList: List[Int => Int] = List(_ * 2, _ + 100, _ / 2)

  def genA(): Int => Int = int2IntList(scala.util.Random.nextInt(int2IntList.length))

  def mapF[G[*]: Functor](r: G[Int])(f: Int => Int) = r.map(f)

}
