package effectie.resource

import cats.syntax.all._
import cats.{Eq, Functor}
import effectie.testing.cats.Specs
import hedgehog._
import hedgehog.runner._

import monix.eval.Task

/** @author Kevin Lee
  * @since 2023-05-22
  */
object Ce2ResourceFunctorSpec extends Properties {
  import monix.execution.Scheduler.Implicits.global

  implicit def releasableResourceEq[F[*]](implicit eq: Eq[F[Int]], toF: Int => F[Int]): Eq[ReleasableResource[F, Int]] =
    (resource1, resource2) => eq.eqv(resource1.use(toF), resource2.use(toF))

  override def tests: List[Test] = {
    implicit val resourceMaker: ResourceMaker[Task] = Ce2ResourceMaker.maker[Task]

    implicit val toF: Int => Task[Int] = Task.delay(_)
    implicit def eqF: Eq[Task[Int]] = (fa: Task[Int], fb: Task[Int]) => fa.flatMap(a => fb.map(_ === a)).runSyncUnsafe()

    List(
      property(
        "test Functor Law - Identity for ReleasableResource[Task, *]",
        Specs
          .FunctorLaws
          .identity[ReleasableResource[Task, *]](genReleasableResourceFunctor[Task](Ce2Resource.pure[Task, Int])),
      ),
      property(
        "test Functor Law - Composition for ReleasableResource[Task, *]",
        Specs
          .FunctorLaws
          .composition[ReleasableResource[Task, *]](
            genReleasableResourceFunctor[Task](Ce2Resource.pure[Task, Int]),
            genFunction,
          ),
      ),
      property(
        "test ReleasableResource[Task, *].map",
        testMap[Task](
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
