package effectie.resource

import cats.effect.IO
import cats.syntax.all._
import cats.{Applicative, Eq}
import effectie.testing.cats.Specs
import hedgehog._
import hedgehog.runner._

/** @author Kevin Lee
  * @since 2023-05-22
  */
object Ce2ResourceApplicativeSpec extends Properties {
  implicit def releasableResourceEq[F[*]](implicit eq: Eq[F[Int]], toF: Int => F[Int]): Eq[ReleasableResource[F, Int]] =
    (resource1, resource2) => eq.eqv(resource1.use(toF), resource2.use(toF))

  override def tests: List[Test] = {
    implicit val resourceMaker: ResourceMaker[IO] = Ce2ResourceMaker.maker[IO]

    implicit val toF: Int => IO[Int] = IO.delay(_)
    implicit def eqF: Eq[IO[Int]]    = (fa: IO[Int], fb: IO[Int]) => fa.flatMap(a => fb.map(_ === a)).unsafeRunSync()

    List(
      property(
        "test Applicative Law - Identity for ReleasableResource[IO, *]",
        Specs
          .ApplicativeLaws
          .identity[ReleasableResource[IO, *]](genReleasableResourceFunctor[IO](Ce2Resource.pure[IO, Int])),
      ),
      property(
        "test Applicative Law - Composition for ReleasableResource[IO, *]",
        Specs
          .ApplicativeLaws
          .composition[ReleasableResource[IO, *]](
            genReleasableResourceFunctor[IO](Ce2Resource.pure[IO, Int]),
            genFunction,
          ),
      ),
      property(
        "test Applicative Law - IdentityAp for ReleasableResource[IO, *]",
        Specs
          .ApplicativeLaws
          .identityAp[ReleasableResource[IO, *]](
            genReleasableResourceFunctor[IO](Ce2Resource.pure[IO, Int])
          ),
      ),
      property(
        "test Applicative Law - Homomorphism for ReleasableResource[IO, *]",
        Specs
          .ApplicativeLaws
          .homomorphism[ReleasableResource[IO, *]](
            Gen.int(Range.linear(Int.MinValue, Int.MaxValue)),
            genFunction,
          ),
      ),
      property(
        "test Applicative Law - Interchange for ReleasableResource[IO, *]",
        Specs
          .ApplicativeLaws
          .interchange[ReleasableResource[IO, *]](
            Gen.int(Range.linear(Int.MinValue, Int.MaxValue)),
            genFunction,
          ),
      ),
      property(
        "test Applicative Law - CompositionAp for ReleasableResource[IO, *]",
        Specs
          .ApplicativeLaws
          .compositionAp[ReleasableResource[IO, *]](
            genReleasableResourceFunctor[IO](Ce2Resource.pure[IO, Int]),
            genFunction,
          ),
      ),
      /////
      property(
        "test ReleasableResource[IO, *].map",
        testMap[IO](
          Ce2Resource.pure[IO, Int],
          IO.delay(_),
          _.handleError(err => Result.failure.log(s"Error: ${err.getMessage}"))
            .unsafeRunSync(),
        ),
      ),
      property(
        "test ReleasableResource[IO, *].ap",
        testAp[IO](
          Ce2Resource.pure[IO, Int],
          IO.delay(_),
          _.handleError(err => Result.failure.log(s"Error: ${err.getMessage}"))
            .unsafeRunSync(),
        ),
      ),
    )
  }

  def testMap[F[*]: ResourceMaker](
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
      f <- genFunction.log("f")
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

  def genFunction: Gen[Int => Int] =
    Gen.element1(_ * 2, _ + 100, _ / 2)

  def mapF[F[*]: Applicative](fa: F[Int])(f: Int => Int) =
    fa.map(f)

  def apF[F[*]: Applicative](ff: F[Int => Int])(fa: F[Int]) =
    ff.ap(fa)

}
