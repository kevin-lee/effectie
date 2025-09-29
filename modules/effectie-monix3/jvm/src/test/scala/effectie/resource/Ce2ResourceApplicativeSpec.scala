package effectie.resource

import cats.syntax.all._
import cats.{Applicative, Eq}
import effectie.testing.cats.Specs
import hedgehog._
import hedgehog.runner._

import monix.eval.Task

/** @author Kevin Lee
  * @since 2023-05-22
  */
object Ce2ResourceApplicativeSpec extends Properties {
  import monix.execution.Scheduler.Implicits.global

  implicit def releasableResourceEq[F[*]](implicit eq: Eq[F[Int]], toF: Int => F[Int]): Eq[ReleasableResource[F, Int]] =
    (resource1, resource2) => eq.eqv(resource1.use(toF), resource2.use(toF))

  override def tests: List[Test] = {
    implicit val resourceMaker: ResourceMaker[Task] = Ce2ResourceMaker.maker[Task]

    implicit val toF: Int => Task[Int] = Task.delay(_)
    implicit def eqF: Eq[Task[Int]] = (fa: Task[Int], fb: Task[Int]) => fa.flatMap(a => fb.map(_ === a)).runSyncUnsafe()

    List(
      property(
        "test Applicative Law - Identity for ReleasableResource[Task, *]",
        Specs
          .ApplicativeLaws
          .identity[ReleasableResource[Task, *]](genReleasableResourceFunctor[Task](Ce2Resource.pure[Task, Int])),
      ),
      property(
        "test Applicative Law - Composition for ReleasableResource[Task, *]",
        Specs
          .ApplicativeLaws
          .composition[ReleasableResource[Task, *]](
            genReleasableResourceFunctor[Task](Ce2Resource.pure[Task, Int]),
            genFunction,
          ),
      ),
      property(
        "test Applicative Law - IdentityAp for ReleasableResource[Task, *]",
        Specs
          .ApplicativeLaws
          .identityAp[ReleasableResource[Task, *]](
            genReleasableResourceFunctor[Task](Ce2Resource.pure[Task, Int])
          ),
      ),
      property(
        "test Applicative Law - Homomorphism for ReleasableResource[Task, *]",
        Specs
          .ApplicativeLaws
          .homomorphism[ReleasableResource[Task, *]](
            Gen.int(Range.linear(Int.MinValue, Int.MaxValue)),
            genFunction,
          ),
      ),
      property(
        "test Applicative Law - Interchange for ReleasableResource[Task, *]",
        Specs
          .ApplicativeLaws
          .interchange[ReleasableResource[Task, *]](
            Gen.int(Range.linear(Int.MinValue, Int.MaxValue)),
            genFunction,
          ),
      ),
      property(
        "test Applicative Law - CompositionAp for ReleasableResource[Task, *]",
        Specs
          .ApplicativeLaws
          .compositionAp[ReleasableResource[Task, *]](
            genReleasableResourceFunctor[Task](Ce2Resource.pure[Task, Int]),
            genFunction,
          ),
      ),
      /////
      property(
        "test ReleasableResource[Task, *].map",
        testMap[Task](
          Ce2Resource.pure[Task, Int],
          Task.delay(_),
          _.handleError(err => Result.failure.log(s"Error: ${err.getMessage}"))
            .runSyncUnsafe(),
        ),
      ),
      property(
        "test ReleasableResource[Task, *].ap",
        testAp[Task](
          Ce2Resource.pure[Task, Int],
          Task.delay(_),
          _.handleError(err => Result.failure.log(s"Error: ${err.getMessage}"))
            .runSyncUnsafe(),
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
