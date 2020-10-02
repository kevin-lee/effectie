---
layout: docs
title: "For Scalaz Effect"
---

## Effectie for Scalaz Effect

* [EffectConstructor](effect-constructor)
* [ConsoleEffect](console-effect)
* [OptionTSupport](optiont-support)
* [EitherTSupport](eithert-support)

## All in One Example

```scala mdoc:reset-object
import scalaz._
import Scalaz._
import scalaz.effect._

import effectie.scalaz.ConsoleEffectful._
import effectie.scalaz.Effectful._

import effectie.scalaz.EitherTSupport._
import effectie.scalaz.OptionTSupport._
import effectie.scalaz._

trait Something[F[_]] {
  def foo[A: Semigroup](a: A): F[A]
  def bar[A: Semigroup](a: Option[A]): F[Option[A]]
  def baz[A, B: Semigroup](a: A \/ B): F[A \/ B]
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
        blah <- pureOf("blah blah")
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
      } yield x).run

    override def baz[A, B: Semigroup](ab: A \/ B): F[A \/ B] =
      (for {
        b <- eitherTOf(ab)
        blah <- eitherTOfPure("blah blah".right[A])
        _ <- eitherTRight(println(s"b: $b / BLAH: $blah"))
        x <- eitherTRightF(effectOf(b |+| b))
        _ <- eitherTRightF[A](putStrLn(s"x: $x"))
      } yield x).run
  }
}

println(Something[IO].foo(1).unsafePerformIO())

println(Something[IO].bar(2.some).unsafePerformIO())
println(Something[IO].bar(none[String]).unsafePerformIO())

println(Something[IO].baz(2.right[String]).unsafePerformIO())
println(Something[IO].baz("ERROR!!!".left[Int]).unsafePerformIO())

```
