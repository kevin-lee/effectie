package effectie.resource

import cats.syntax.all._
import cats.{Applicative, Eq}
import effectie.testing.RandomGens
import munit.Assertions

/** @author Kevin Lee
  * @since 2023-05-17
  */
trait ReleasableResourceApplicativeForMunit {

  implicit def releasableResourceEq[F[*]](implicit eq: Eq[F[Int]], toF: Int => F[Int]): Eq[ReleasableResource[F, Int]] =
    (resource1, resource2) => eq.eqv(resource1.use(toF), resource2.use(toF))

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

  def testAp[F[*]: ResourceMaker](
    ctor: Int => ReleasableResource[F, Int],
    toF: Unit => F[Unit],
  ): F[Unit] = {
    val n        = RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)
    val f        = genF()
    val resource = ctor(n)
    val expected = f(n)
    apF[ReleasableResource[F, *]](ResourceMaker[F].pure(f))(resource)
      .map(Assertions.assertEquals(_, expected))
      .use(toF)
  }

  def genReleasableResourceFunctor[F[*]](ctor: Int => ReleasableResource[F, Int]): ReleasableResource[F, Int] =
    ctor(RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue))

  val int2IntList: List[Int => Int] = List(_ * 2, _ + 100, _ / 2)

  def genF(): Int => Int = int2IntList(scala.util.Random.nextInt(int2IntList.length))

  def mapF[G[*]: Applicative](r: G[Int])(f: Int => Int) = r.map(f)

  def apF[G[*]: Applicative](r: G[Int => Int])(fa: G[Int]) = r.ap(fa)

}
