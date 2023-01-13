package effectie.resource

import cats.instances.all._
import cats.syntax.all._
import effectie.core.{CanCatch, FxCtor}
import effectie.resource.data.TestErrors.TestException
import hedgehog._
import hedgehog.runner._

import scala.util.{Failure, Try}

/** @author Kevin Lee
  * @since 2022-11-12
  */
object UsingResourceMakerSpec extends Properties {

  override def tests: List[Test] = List(
    property(
      "test ResourceMaker.usingResourceMaker: ResourceMaker[Try]",
      testUsingResourceMaker,
    ),
    property(
      "test ResourceMaker.usingResourceMaker: ResourceMaker[Try] error case",
      testUsingResourceMakerErrorCase,
    ),
  )

  implicit val tryFxCtor: FxCtor[Try] with CanCatch[Try] = TryFxCtor

  def testUsingResourceMaker: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {

      implicit val resourceMaker: ResourceMaker[Try] = ResourceMaker.usingResourceMaker

      ResourceMakerSpec
        .testForAutoCloseable[Try](
          content,
          _ => Try(()),
          none,
        )
        .fold(err => Result.failure.log(s"Unexpected error: ${err.toString}"), identity)
    }

  def testUsingResourceMakerErrorCase: Property =
    for {
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 100))
                   .list(Range.linear(1, 10))
                   .map(_.toVector)
                   .log("content")
    } yield {
      implicit val resourceMaker: ResourceMaker[Try] = ResourceMaker.usingResourceMaker

      ResourceMakerSpec
        .testForAutoCloseable[Try](
          content,
          _ => Failure(TestException(123)),
          Option({
            case TestException(123) => Result.success
            case ex: Throwable =>
              Result
                .failure
                .log(s"TestException was expected but it is ${ex.getClass.getSimpleName}. Error: ${ex.toString}")
          }),
        )
        .fold(err => Result.failure.log(s"Unexpected error: ${err.toString}"), identity)
    }

  object TryFxCtor extends FxCtor[Try] with CanCatch[Try] {
    override def effectOf[A](a: => A): Try[A] = Try(a)

    override def pureOf[A](a: A): Try[A] = Try(a)

    override def pureOrError[A](a: => A): Try[A] = Try(a)

    override def unitOf: Try[Unit] = Try(())

    override def errorOf[A](throwable: Throwable): Try[A] = scala.util.Failure(throwable)

    override def fromEither[A](either: Either[Throwable, A]): Try[A] = either.toTry

    override def fromOption[A](option: Option[A])(orElse: => Throwable): Try[A] =
      option.fold[Try[A]](scala.util.Failure(orElse))(Try(_))

    override def fromTry[A](tryA: Try[A]): Try[A] = tryA

    override def flatMapFa[A, B](fa: Try[A])(f: A => Try[B]): Try[B] = fa.flatMap(f)

    override def catchNonFatalThrowable[A](fa: => Try[A]): Try[Either[Throwable, A]] =
      fa.fold(err => Try(err.asLeft[A]), a => Try(a.asRight[Throwable]))
  }
}
