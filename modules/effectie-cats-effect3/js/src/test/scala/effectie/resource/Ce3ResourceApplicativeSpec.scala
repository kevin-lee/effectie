package effectie.resource

import cats.effect.IO
import cats.syntax.all._
import cats.{Applicative, Eq}
import effectie.testing.RandomGens
import effectie.testing.cats.LawsF
import effectie.testing.cats.LawsF.EqF
import munit.Assertions

/** @author Kevin Lee
  * @since 2023-05-22
  */
class Ce3ResourceApplicativeSpec extends munit.CatsEffectSuite {

  final type F[A] = IO[A]
  final val F = IO // scalafix:ok DisableSyntax.noFinalVal

  implicit def releasableResourceEq[G[*]](implicit eq: Eq[G[Int]], toF: Int => G[Int]): Eq[ReleasableResource[G, Int]] =
    (resource1, resource2) => eq.eqv(resource1.use(toF), resource2.use(toF))

  implicit val resourceMaker: ResourceMaker[F] = Ce3ResourceMaker.maker[F]

  implicit val toF: Int => F[Int] = F.delay(_)

  implicit def eqF: EqF[ReleasableResource[F, *], Int] =
    (a, b) => a.flatMap(aVal => b.map(aVal === _))

  test("test Applicative Law - Identity for ReleasableResource[F, *]") {
    val n = 123

    LawsF
      .ApplicativeLaws
      .identity[ReleasableResource[F, *], Int](Ce3Resource.pure[F, Int](n))
      .use[Unit](result => F.pure(Assertions.assert(result)))
      .unsafeToFuture()
  }

  test("test Applicative Law - Composition for ReleasableResource[F, *]") {
    LawsF
      .ApplicativeLaws
      .composition[ReleasableResource[F, *], Int, Int, Int](
        genReleasableResourceFunctor[F](Ce3Resource.pure[F, Int]),
        genA(),
        genA(),
      )
      .use(result => F.pure(Assertions.assert(result)))
      .unsafeToFuture()
  }

  test("test Applicative Law - IdentityAp for ReleasableResource[F, *]") {
    val n = 123
    LawsF
      .ApplicativeLaws
      .identityAp[ReleasableResource[F, *], Int](Ce3Resource.pure[F, Int](n))
      .use(result => F.pure(Assertions.assert(result)))
      .unsafeToFuture()
  }

  test("test Applicative Law - Homomorphism for ReleasableResource[F, *]") {
    val n = RandomGens.genRandomInt()
    LawsF
      .ApplicativeLaws
      .homomorphism[ReleasableResource[F, *], Int, Int](
        genA(),
        n,
      )
      .use(result => F.pure(Assertions.assert(result)))
      .unsafeToFuture()
  }

  test("test Applicative Law - Interchange for ReleasableResource[F, *]") {
    val n = RandomGens.genRandomInt()
    LawsF
      .ApplicativeLaws
      .interchange[ReleasableResource[F, *], Int, Int](
        n,
        Ce3Resource.pure(genA()),
      )
      .use(result => F.pure(Assertions.assert(result)))
      .unsafeToFuture()
  }

  test("test Applicative Law - CompositionAp for ReleasableResource[F, *]") {
    LawsF
      .ApplicativeLaws
      .compositionAp[ReleasableResource[F, *], Int, Int, Int](
        genReleasableResourceFunctor[F](Ce3Resource.pure[F, Int]),
        Ce3Resource.pure(genA()),
        Ce3Resource.pure(genA()),
      )
      .use(result => F.pure(Assertions.assert(result)))
      .unsafeToFuture()
  }

  /////
  test("test ReleasableResource[F, *].map") {
    implicit val resourceMaker: ResourceMaker[F] = Ce3ResourceMaker.maker[F]
    testMap[F](
      Ce3Resource.pure[F, Int],
      F.delay(_),
    )
      .handleError(err => Assertions.fail(s"Error: ${err.getMessage}"))
      .unsafeToFuture()
  }

  test("test ReleasableResource[F, *].ap") {
    implicit val resourceMaker: ResourceMaker[F] = Ce3ResourceMaker.maker[F]
    testAp[F](
      Ce3Resource.pure[F, Int],
      F.delay(_),
    ).handleError(err => Assertions.fail(s"Error: ${err.getMessage}"))
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

  def testAp[G[*]: ResourceMaker](
    ctor: Int => ReleasableResource[G, Int],
    toF: Unit => G[Unit],
  ): G[Unit] = {
    val n        = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)
    val f        = genA()
    val resource = ctor(n)
    val expected = f(n)
    apF[ReleasableResource[G, *]](ResourceMaker[G].pure(f))(resource)
      .map(Assertions.assertEquals(_, expected))
      .use(toF)
  }

  def genReleasableResourceFunctor[G[*]](ctor: Int => ReleasableResource[G, Int]): ReleasableResource[G, Int] =
    ctor(RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue))

  val int2IntList: List[Int => Int] = List(_ * 2, _ + 100, _ / 2)

  def genA(): Int => Int = int2IntList(scala.util.Random.nextInt(int2IntList.length))

  def mapF[G[*]: Applicative](r: G[Int])(f: Int => Int) = r.map(f)

  def apF[G[*]: Applicative](r: G[Int => Int])(fa: G[Int]) = r.ap(fa)

}
