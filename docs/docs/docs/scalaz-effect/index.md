---
layout: docs
title: "For Scalaz Effect"
---

# Effectie for Scalaz Effect

* [EffectConstructor](effect-constructor)
* [Effectful](effectful)
* [ConsoleEffect](console-effect)
* [ConsoleEffectful](console-effectful)
* [OptionTSupport](optiont-support)
* [EitherTSupport](eithert-support)

# All in One Example

```scala mdoc:reset-object
import scalaz._
import Scalaz._
import scalaz.effect._

import effectie.ConsoleEffectful._
import effectie.Effectful._

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
        blah <- effectOfPure("blah blah")
        _ <- effectOf(println(s"n: $n / BLAH: $blah"))
        x <- effectOf(n |+| n)
        _ <- putStrLn(s"x: $x")
      } yield x

    override def bar[A: Semigroup](a: Option[A]): F[Option[A]] =
      (for {
        a <- optionTEffectOfPure(a)
        blah <- optionTEffectOfPure("blah blah".some)
        _ <- optionTLiftEffectOf(println(s"a: $a / BLAH: $blah"))
        x <- optionTLiftF(effectOf(a |+| a))
        _ <- optionTLiftF(putStrLn(s"x: $x"))
      } yield x).run

    override def baz[A, B: Semigroup](ab: A \/ B): F[A \/ B] =
      (for {
        b <- eitherTEffectOf(ab)
        blah <- eitherTEffectOfPure("blah blah".right[A])
        _ <- eitherTLiftEffectOf(println(s"b: $b / BLAH: $blah"))
        x <- eitherTLiftF[F, A, B](effectOf(b |+| b))
        _ <- eitherTLiftF(putStrLn(s"x: $x"))
      } yield x).run
  }
}

println(Something[IO].foo(1).unsafePerformIO())

println(Something[IO].bar(2.some).unsafePerformIO())
println(Something[IO].bar(none[String]).unsafePerformIO())

println(Something[IO].baz(2.right[String]).unsafePerformIO())
println(Something[IO].baz("ERROR!!!".left[Int]).unsafePerformIO())

```
