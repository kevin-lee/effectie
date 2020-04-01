# effectie

```scala
import cats._
import cats.effect._
import cats.implicits._

import effectie.cats.EffectConstructor
import effectie.Effectful._
import effectie.cats.EitherTSupport._
import effectie.cats.OptionTSupport._

trait Something[F[_]] {
  def foo[A : Semigroup](a: A): F[A]
  def bar[A : Semigroup](a: Option[A]): F[Option[A]]
  def baz[A, B : Semigroup](a: Either[A, B]): F[Either[A, B]]
}

object Something {

  def apply[F[_] :EffectConstructor : Monad] = implicitly[Something[F]]

  implicit def foo[F[_] : EffectConstructor : Monad]: Something[F] = new SomethingF[F]

  class SomethingF[F[_] : EffectConstructor : Monad] extends Something[F] {
    override def foo[A : Semigroup](a: A): F[A] = for {
      n <- effectOf(a)
      blah <- pureEffect("blah blah")
      _ <- effectOf(println(s"n: $n / BLAH: $blah"))
      x <- effectOf(n |+| n)
    } yield x

    override def bar[A : Semigroup](a: Option[A]): F[Option[A]] = (for {
      a <- optionTPureEffect(a)
      blah <- optionTPureEffect("blah blah".some)
      _ <- optionTLiftEffect(println(s"a: $a / BLAH: $blah"))
      x <- optionTLiftF(effectOf(a |+| a))
    } yield x).value

    override def baz[A, B : Semigroup](ab: Either[A, B]): F[Either[A, B]] = (for {
      b <- eitherTEffect(ab)
      blah <- eitherTPureEffect("blah blah".asRight[A])
      _ <- eitherTLiftEffect(println(s"b: $b / BLAH: $blah"))
      x <- eitherTLiftF[F, A, B](effectOf(b |+| b))
    } yield x).value
  }
}

object TestMainApp extends App {
  println(Something[IO].foo(1).unsafeRunSync())
  println("=====")

  println(Something[IO].bar(2.some).unsafeRunSync())
  println(Something[IO].bar(none[String]).unsafeRunSync())
  println("=====")

  println(Something[IO].baz(2.asRight[String]).unsafeRunSync())
  println(Something[IO].baz("ERROR!!!".asLeft[Int]).unsafeRunSync())
  println("=====")
}

```
```
n: 1 / BLAH: blah blah
2
=====
a: 2 / BLAH: blah blah
Some(4)
None
=====
b: 2 / BLAH: blah blah
Right(4)
Left(ERROR!!!)
=====
```