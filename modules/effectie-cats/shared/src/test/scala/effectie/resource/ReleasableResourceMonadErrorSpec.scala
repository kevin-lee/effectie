package effectie.resource

import cats.MonadError
import cats.syntax.all._
import effectie.resource.data.TestErrors.TestException
import hedgehog._
import hedgehog.runner._

import scala.util.{Success, Try}

/** The constraint-free MonadError instance for ReleasableResource (no ResourceMaker required).
  *
  * The instance builds pure data, so the laws do not depend on the effect type; they are verified through the
  * automatic UseResource[Try] interpreter.
  *
  * @author Kevin Lee
  * @since 2026-08-02
  */
object ReleasableResourceMonadErrorSpec extends Properties {

  override def tests: List[Test] = List(
    example(
      "test MonadError[ReleasableResource[Try, *], Throwable] resolves without ResourceMaker",
      testInstanceResolves,
    ),
    property("test functor identity", testFunctorIdentity),
    property("test functor composition", testFunctorComposition),
    property("test monad left identity", testMonadLeftIdentity),
    property("test monad right identity", testMonadRightIdentity),
    property("test monad associativity", testMonadAssociativity),
    example("test raiseError / handleErrorWith round-trip", testRaiseErrorHandleErrorWith),
    example("test tailRecM stack safety (10,000 iterations)", testTailRecMStackSafety),
  )

  private val monadError: MonadError[ReleasableResource[Try, *], Throwable] =
    MonadError[ReleasableResource[Try, *], Throwable]

  private def run[A](resource: ReleasableResource[Try, A]): Try[A] =
    resource.use(a => Try(a))

  def testInstanceResolves: Result = {
    val summoned = implicitly[MonadError[ReleasableResource[Try, *], Throwable]]
    Result
      .assert(summoned ne null)
      .log("MonadError instance should resolve with no other implicits") // scalafix:ok DisableSyntax.null
  }

  def testFunctorIdentity: Property =
    for {
      n <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("n")
    } yield {
      val resource = ReleasableResource.pure[Try, Int](n)
      run(resource.map(identity)) ==== run(resource)
    }

  def testFunctorComposition: Property =
    for {
      n <- Gen.int(Range.linear(-10000, 10000)).log("n")
      x <- Gen.int(Range.linear(-10000, 10000)).log("x")
      y <- Gen.int(Range.linear(-10000, 10000)).log("y")
    } yield {
      val f: Int => Int = _ + x
      val g: Int => Int = _ * y

      val resource = ReleasableResource.pure[Try, Int](n)
      run(resource.map(f).map(g)) ==== run(resource.map(f.andThen(g)))
    }

  def testMonadLeftIdentity: Property =
    for {
      n <- Gen.int(Range.linear(-10000, 10000)).log("n")
      x <- Gen.int(Range.linear(-10000, 10000)).log("x")
    } yield {
      val f: Int => ReleasableResource[Try, Int] = m => ReleasableResource.pure(m + x)

      run(monadError.pure(n).flatMap(f)) ==== run(f(n))
    }

  def testMonadRightIdentity: Property =
    for {
      n <- Gen.int(Range.linear(-10000, 10000)).log("n")
    } yield {
      val resource = ReleasableResource.pure[Try, Int](n)
      run(resource.flatMap(monadError.pure)) ==== run(resource)
    }

  def testMonadAssociativity: Property =
    for {
      n <- Gen.int(Range.linear(-10000, 10000)).log("n")
      x <- Gen.int(Range.linear(-10000, 10000)).log("x")
      y <- Gen.int(Range.linear(-10000, 10000)).log("y")
    } yield {
      val f: Int => ReleasableResource[Try, Int] = m => ReleasableResource.pure(m + x)
      val g: Int => ReleasableResource[Try, Int] = m => ReleasableResource.pure(m * y)

      val resource = ReleasableResource.pure[Try, Int](n)
      run(resource.flatMap(f).flatMap(g)) ==== run(resource.flatMap(m => f(m).flatMap(g)))
    }

  def testRaiseErrorHandleErrorWith: Result = {
    val recovered =
      monadError
        .raiseError[Int](TestException(123))
        .handleErrorWith {
          case TestException(123) => ReleasableResource.pure[Try, Int](1)
          case err => ReleasableResource.raiseError[Try](err)
        }

    (run(recovered) ==== Success(1)).log("raiseError should be recovered by handleErrorWith")
  }

  def testTailRecMStackSafety: Result = {
    val depth = 10000

    val counted =
      monadError.tailRecM(0) { i =>
        if (i < depth) ReleasableResource.pure[Try, Either[Int, Int]]((i + 1).asLeft[Int])
        else ReleasableResource.pure[Try, Either[Int, Int]](i.asRight[Int])
      }

    (run(counted) ==== Success(depth)).log(s"tailRecM with ${depth.toString} iterations")
  }

}
