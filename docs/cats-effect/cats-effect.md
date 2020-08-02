---
id: cats-effect
title: "For Cats Effect"
---

## Effectie for Cats Effect

* [EffectConstructor](effect-constructor)
* [ConsoleEffect](console-effect)
* [CanCatch](can-catch)
* [OptionTSupport](optiont-support)
* [EitherTSupport](eithert-support)

## All in One Example

```scala mdoc:reset-object

import cats._
import cats.implicits._
import cats.effect._

import effectie.ConsoleEffectful._
import effectie.Effectful._

import effectie.cats.EitherTSupport._
import effectie.cats.OptionTSupport._
import effectie.cats._

trait Something[F[_]] {
  def foo[A: Semigroup](a: A): F[A]
  def bar[A: Semigroup](a: Option[A]): F[Option[A]]
  def baz[A, B: Semigroup](a: Either[A, B]): F[Either[A, B]]
}

object Something {

  def apply[F[_]: Something]: Something[F] =
    implicitly[Something[F]]

  implicit def something[F[_]: EffectConstructor: ConsoleEffect: Monad]: Something[F] =
    new SomethingF[F]

  final class SomethingF[F[_]: EffectConstructor: ConsoleEffect: Monad]
    extends Something[F] {

    override def foo[A: Semigroup](a: A): F[A] =
      for {
        n <- effectOf(a)
        blah <- effectOfPure("blah blah")
        _ <- effectOf(println(s"n: $n / BLAH: $blah"))
        x <- effectOf(n |+| n)
        _ <- putStrLn(s"x: $x")
      } yield x

    override def bar[A: Semigroup](a: Option[A]): F[Option[A]] =
      (for {
        a <- optionTOfPure(a)
        blah <- optionTOfPure("blah blah".some)
        _ <- optionTSome(println(s"a: $a / BLAH: $blah"))
        x <- optionTSomeF(effectOf(a |+| a))
        _ <- optionTSomeF(putStrLn(s"x: $x"))
      } yield x).value

    override def baz[A, B: Semigroup](ab: Either[A, B]): F[Either[A, B]] =
      (for {
        b <- eitherTOf(ab)
        blah <- eitherTOfPure("blah blah".asRight[A])
        _ <- eitherTRight(println(s"b: $b / BLAH: $blah"))
        x <- eitherTRightF(effectOf(b |+| b))
        _ <- eitherTRightF[A](putStrLn(s"x: $x"))
      } yield x).value
  }
}

println(Something[IO].foo(1).unsafeRunSync())

println(Something[IO].bar(2.some).unsafeRunSync())
println(Something[IO].bar(none[String]).unsafeRunSync())

println(Something[IO].baz(2.asRight[String]).unsafeRunSync())
println(Something[IO].baz("ERROR!!!".asLeft[Int]).unsafeRunSync())

```
