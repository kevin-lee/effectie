---
id: monix
title: "For Monix"
---

## Effectie for Monix

* [Fx](fx.md)
* [FromFuture](from-future.md)

## All in One Example

```scala mdoc:reset-object

import cats._
import cats.syntax.all._
import monix.eval._

import effectie.syntax.all._

import extras.cats.syntax.all._
import effectie.core._

trait Something[F[_]] {
  def foo[A: Semigroup](a: A): F[A]
  def bar[A: Semigroup](a: Option[A]): F[Option[A]]
  def baz[A, B: Semigroup](a: Either[A, B]): F[Either[A, B]]
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
      } yield x).value

    override def baz[A, B: Semigroup](ab: Either[A, B]): F[Either[A, B]] =
      (for {
        b    <- ab.eitherT[F] // EitherT(Applicative[F].pure(ab))
        blah <- "blah blah"
                  .asRight[A]
                  .eitherT[F] // EitherT(Applicative[F].pure("blah blah".asRight[A]))
        _    <- effectOf(
                  println(s"b: $b / BLAH: $blah")
                ).rightT[A] // EitherT(effectOf(Right(println(s"b: $b / BLAH: $blah"))))
        x    <- effectOf(ab |+| ab).eitherT // EitherT(effectOf(ab |+| ab))
        _    <- effectOf(
                  putStrLn(s"x: $x")
                ).rightT[A] // EitherT(effectOf(putStrLn(s"x: $x").asRight[A]))
      } yield x).value
  }
}
import monix.execution.Scheduler.Implicits.global

import effectie.instances.monix3.fx._
import effectie.instances.console._

println(Something[Task].foo(1).runSyncUnsafe())

println(Something[Task].bar(2.some).runSyncUnsafe())
println(Something[Task].bar(none[String]).runSyncUnsafe())

println(Something[Task].baz(2.asRight[String]).runSyncUnsafe())
println(Something[Task].baz("ERROR!!!".asLeft[Int]).runSyncUnsafe())

```
