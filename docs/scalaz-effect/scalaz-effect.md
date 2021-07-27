---
layout: docs
title: "For Scalaz Effect"
---

## Effectie for Scalaz Effect

* [Fx](fx)
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
  def baz[A: Semigroup, B: Semigroup](a: A \/ B): F[A \/ B]
}

object Something {

  def apply[F[_]: Something]: Something[F] =
    implicitly[Something[F]]

  implicit def something[F[_]: Fx: ConsoleEffect: Monad]: Something[F] =
    new SomethingF[F]

  final class SomethingF[F[_]: Fx: ConsoleEffect: Monad]
    extends Something[F] {

    override def foo[A: Semigroup](a: A): F[A] =
      for {
        n    <- effectOf(a)
        blah <- pureOf("blah blah")
        _    <- effectOf(println(s"n: $n / BLAH: $blah"))
        x    <- effectOf(n |+| n)
        _    <- putStrLn(s"x: $x")
      } yield x

    override def bar[A: Semigroup](a: Option[A]): F[Option[A]] =
      (for {
        aa   <- a.optionT[F] // OptionT(Applicative[F].pure(a))
        blah <- "blah blah".someTF[F] // OptionT(Applicative[F].pure(Some("blah blah")))
        _    <- effectOf(
                  println(s"a: $a / BLAH: $blah")
                ).someT // OptionT(effectOf(Some(println(s"a: $a / BLAH: $blah"))))
        x    <- effectOf(a |+| a).optionT // OptionT(effectOf(a |+| a))
        _    <- effectOf(putStrLn(s"x: $x")).someT // OptionT(effectOf(Some(putStrLn(s"x: $x"))))
      } yield x).run

    override def baz[A: Semigroup, B: Semigroup](ab: A \/ B): F[A \/ B] =
      (for {
        b    <- ab.eitherT[F] // EitherT(Applicative[F].pure(ab))
        blah <- "blah blah"
                  .right[A]
                  .eitherT[F] // EitherT(Applicative[F].pure("blah blah".right[A]))
        _    <- effectOf(
                  println(s"b: $b / BLAH: $blah")
                ).rightT[A] // EitherT(effectOf(\/-(println(s"b: $b / BLAH: $blah"))))
        x    <- effectOf(ab |+| ab).eitherT // EitherT(effectOf(ab |+| ab))
        _    <- effectOf(
                  putStrLn(s"x: $x")
                ).rightT[A] // EitherT(effectOf(putStrLn(s"x: $x").right[A]))
      } yield x).run
  }
}

println(Something[IO].foo(1).unsafePerformIO())

println(Something[IO].bar(2.some).unsafePerformIO())
println(Something[IO].bar(none[String]).unsafePerformIO())

println(Something[IO].baz(2.right[String]).unsafePerformIO())
println(Something[IO].baz("ERROR!!!".left[Int]).unsafePerformIO())

```
