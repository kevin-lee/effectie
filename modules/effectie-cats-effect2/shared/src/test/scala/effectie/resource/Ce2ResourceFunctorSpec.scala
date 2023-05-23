package effectie.resource

import cats.effect.IO
import cats.syntax.all._
import cats.{Eq, Functor}
import effectie.testing.cats.Specs
import hedgehog._
import hedgehog.runner._

/** @author Kevin Lee
  * @since 2023-05-22
  */
object Ce2ResourceFunctorSpec extends Properties {
  implicit def releasableResourceEq[F[*]](implicit eq: Eq[F[Int]], toF: Int => F[Int]): Eq[ReleasableResource[F, Int]] =
    (resource1, resource2) => eq.eqv(resource1.use(toF), resource2.use(toF))

  override def tests: List[Test] = {
    implicit val toF: Int => IO[Int] = IO.delay(_)
    implicit def eqF: Eq[IO[Int]]    = (fa: IO[Int], fb: IO[Int]) => fa.flatMap(a => fb.map(_ === a)).unsafeRunSync()

    List(
      property(
        "test Functor Law - Identity for ReleasableResource[IO, *]",
        Specs
          .FunctorLaws
          .identity[ReleasableResource[IO, *]](genReleasableResourceFunctor[IO](Ce2Resource.pure[IO, Int])),
      ),
      property(
        "test Functor Law - Composition for ReleasableResource[IO, *]",
        Specs
          .FunctorLaws
          .composition[ReleasableResource[IO, *]](
            genReleasableResourceFunctor[IO](Ce2Resource.pure[IO, Int]),
            genFunction,
          ),
      ),
    )
  } ++ List(
    property(
      "test ReleasableResource[IO, *].map",
      testMap[IO](
        Ce2Resource.pure[IO, Int],
        IO.delay(_),
        _.handleError(err => Result.failure.log(s"Error: ${err.getMessage}"))
          .unsafeRunSync(),
      ),
    )
  )

  def testMap[F[*]](
    ctor: Int => ReleasableResource[F, Int],
    toF: Result => F[Result],
    get: F[Result] => Result,
  ): Property =
    for {
      n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
      resource = ctor(n)
      f <- genFunction.log("f")
    } yield {
      val expected = f(n)
      get(
        foo[ReleasableResource[F, *]](resource)(f)
          .map(_ ==== expected)
          .use(toF)
      )
    }

  def genReleasableResourceFunctor[F[*]](ctor: Int => ReleasableResource[F, Int]): Gen[ReleasableResource[F, Int]] =
    Gen
      .int(Range.linear(Int.MinValue, Int.MaxValue))
      .map(ctor)

  def genFunction: Gen[Int => Int] =
    Gen.element1(_ * 2, _ + 100, _ / 2)

  def foo[F[*]: Functor](r: F[Int])(f: Int => Int) =
    r.map(f)

}
