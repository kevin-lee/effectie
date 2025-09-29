package effectie.resource

import cats.syntax.all._
import cats.{Eq, Functor}
import effectie.testing.cats.LawsF
import effectie.testing.cats.LawsF.EqF
import effectie.testing.{FutureTools, RandomGens}
import munit.Assertions

import scala.concurrent._
import scala.concurrent.duration._

import monix.eval.Task

/** @author Kevin Lee
  * @since 2023-05-22
  */
class Ce2ResourceFunctorSpec extends munit.FunSuite with FutureTools {

  implicit val ec: ExecutionContext = globalExecutionContext

  import monix.execution.Scheduler.Implicits.global

  override val munitTimeout: FiniteDuration = 200.milliseconds

  final type F[A] = Task[A]
  final val F = Task // scalafix:ok DisableSyntax.noFinalVal

  implicit def releasableResourceEq[G[*]](implicit eq: Eq[G[Int]], toF: Int => G[Int]): Eq[ReleasableResource[G, Int]] =
    (resource1, resource2) => eq.eqv(resource1.use(toF), resource2.use(toF))

  implicit val resourceMaker: ResourceMaker[F] = Ce2ResourceMaker.maker[F]

  implicit val toF: Int => F[Int] = F.delay(_)

  implicit def eqF: EqF[ReleasableResource[F, *], Int] =
    (a, b) => a.flatMap(aVal => b.map(aVal === _))

  test("test Functor Law - Identity for ReleasableResource[Task, *]") {
    val n = 123
    LawsF
      .FunctorLaws
      .identity[ReleasableResource[F, *], Int](
        Ce2Resource.pure[F, Int](n)
      )
      .use[Unit](result => F.pure(Assertions.assert(result)))
      .runToFuture
  }

  test("test Functor Law - Composition for ReleasableResource[Task, *]") {
    LawsF
      .FunctorLaws
      .composition[ReleasableResource[F, *], Int, Int, Int](
        genReleasableResourceFunctor[F](Ce2Resource.pure[F, Int]),
        genA(),
        genA(),
      )
      .use[Unit](result => F.pure(Assertions.assert(result)))
      .runToFuture
  }

  test("test ReleasableResource[Task, *].map") {
    testMap[F](
      Ce2Resource.pure,
      F.pure,
    )
      .handleError(err => Assertions.fail(s"Error: ${err.getMessage}"))
      .runToFuture
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
