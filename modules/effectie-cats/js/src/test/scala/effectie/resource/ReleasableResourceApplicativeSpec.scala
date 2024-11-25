package effectie.resource

import cats.syntax.all._
import cats.{Applicative, Eq}
import hedgehog._

/** @author Kevin Lee
  * @since 2023-05-17
  */
trait ReleasableResourceApplicativeSpec {

  implicit def releasableResourceEq[F[*]](implicit eq: Eq[F[Int]], toF: Int => F[Int]): Eq[ReleasableResource[F, Int]] =
    (resource1, resource2) => eq.eqv(resource1.use(toF), resource2.use(toF))

  def testMap[F[*]: ResourceMaker](
    ctor: Int => ReleasableResource[F, Int],
    toF: Result => F[Result],
    get: F[Result] => Result,
  ): Property =
    for {
      n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
      resource = ctor(n)
      f <- genF.log("f")
    } yield {
      val expected = f(n)
      get(
        mapF[ReleasableResource[F, *]](resource)(f)
          .map(_ ==== expected)
          .use(toF)
      )
    }

  def testAp[F[*]: ResourceMaker](
    ctor: Int => ReleasableResource[F, Int],
    toF: Result => F[Result],
    get: F[Result] => Result,
  ): Property =
    for {
      n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
      resource = ctor(n)
      f <- genF.log("f")
    } yield {
      val expected = f(n)
      get(
        apF[ReleasableResource[F, *]](ResourceMaker[F].pure(f))(resource)
          .map(_ ==== expected)
          .use(toF)
      )
    }

  def genReleasableResourceFunctor[F[*]](ctor: Int => ReleasableResource[F, Int]): Gen[ReleasableResource[F, Int]] =
    Gen
      .int(Range.linear(Int.MinValue, Int.MaxValue))
      .map(ctor)

  def genF: Gen[Int => Int] =
    Gen.element1(_ * 2, _ + 100, _ / 2)

  def mapF[G[*]: Applicative](r: G[Int])(f: Int => Int) =
    r.map(f)

  def apF[G[*]: Applicative](r: G[Int => Int])(fa: G[Int]) =
    r.ap(fa)

}
