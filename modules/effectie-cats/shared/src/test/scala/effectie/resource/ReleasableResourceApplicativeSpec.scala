package effectie.resource

import cats.syntax.all._
import cats.{Applicative, Eq}
import effectie.testing.cats.Specs
import hedgehog._
import hedgehog.runner._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Try

/** @author Kevin Lee
  * @since 2023-05-17
  */
object ReleasableResourceApplicativeSpec extends Properties {

  implicit def releasableResourceEq[F[*]](implicit eq: Eq[F[Int]], toF: Int => F[Int]): Eq[ReleasableResource[F, Int]] =
    (resource1, resource2) => eq.eqv(resource1.use(toF), resource2.use(toF))

  override def tests: List[Test] = {
    implicit val toF: Int => Try[Int] = Try(_)
    implicit def eqF: Eq[Try[Int]]    = Eq.fromUniversalEquals

    implicit val resourceMaker: ResourceMaker[Try] = ResourceMaker.tryResourceMaker

    List(
      property(
        "test Applicative Law - Identity for ReleasableResource[Try, *]",
        Specs
          .ApplicativeLaws
          .identity[ReleasableResource[Try, *]](genReleasableResourceFunctor[Try](ReleasableResource.pureTry[Int])),
      ),
      property(
        "test Applicative Law - Composition for ReleasableResource[Try, *]",
        Specs
          .ApplicativeLaws
          .composition[ReleasableResource[Try, *]](
            genReleasableResourceFunctor[Try](ReleasableResource.pureTry[Int]),
            genF,
          ),
      ),
      property(
        "test Applicative Law - IdentityAp for ReleasableResource[Try, *]",
        Specs
          .ApplicativeLaws
          .identityAp[ReleasableResource[Try, *]](
            genReleasableResourceFunctor[Try](ReleasableResource.pureTry[Int])
          ),
      ),
      property(
        "test Applicative Law - Homomorphism for ReleasableResource[Try, *]",
        Specs
          .ApplicativeLaws
          .homomorphism[ReleasableResource[Try, *]](
            Gen.int(Range.linear(Int.MinValue, Int.MaxValue)),
            genF,
          ),
      ),
      property(
        "test Applicative Law - Interchange for ReleasableResource[Try, *]",
        Specs
          .ApplicativeLaws
          .interchange[ReleasableResource[Try, *]](
            Gen.int(Range.linear(Int.MinValue, Int.MaxValue)),
            genF,
          ),
      ),
      property(
        "test Applicative Law - CompositionAp for ReleasableResource[Try, *]",
        Specs
          .ApplicativeLaws
          .compositionAp[ReleasableResource[Try, *]](
            genReleasableResourceFunctor[Try](ReleasableResource.pureTry[Int]),
            genF,
          ),
      ),
      property(
        "test ReleasableResource[Try, *].map",
        testMap[Try](
          ReleasableResource.pureTry,
          Try(_),
          _.fold(
            err => Result.failure.log(s"Error: ${err.getMessage}"),
            identity,
          ),
        ),
      ),
      property(
        "test ReleasableResource[Try, *].ap",
        testAp[Try](
          ReleasableResource.pureTry,
          Try(_),
          _.fold(
            err => Result.failure.log(s"Error: ${err.getMessage}"),
            identity,
          ),
        ),
      ),
    )
  } ++ {
    implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

    implicit val toF: Int => Future[Int] = Future.successful
    implicit def eqF: Eq[Future[Int]]    =
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
