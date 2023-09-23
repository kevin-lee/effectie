---
id: cats-effect2
title: "For Cats Effect"
---

## Effectie for Cats Effect

* [What to Import](import.md)
* [Fx](fx/fx.md)
* [ConsoleFx](console-fx.md)
* [CanCatch](fx/error-handling/can-catch.md)
* [CanHandleError](fx/error-handling/can-handle-error.md)
* [FromFuture](from-future.md)

## All in One Example

```scala mdoc:reset-object

import cats._
import cats.syntax.all._
import cats.effect._

import effectie.core._
import effectie.syntax.all._

import extras.cats.syntax.all._

trait Something[F[_]] {
  def foo[A: Semigroup](a: A): F[A]
  def bar[A: Semigroup](a: Option[A]): F[Option[A]]
  def baz[A, B: Semigroup](a: Either[A, B]): F[Either[A, B]]
}

object Something {

  def apply[F[_]: Something]: Something[F] =
    implicitly[Something[F]]

  implicit def something[F[_]: Fx: Monad]: Something[F] =
    new SomethingF[F]

  final class SomethingF[F[_]: Fx: Monad]
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

import effectie.instances.ce2.fx._

println(Something[IO].foo(1).unsafeRunSync())

println(Something[IO].bar(2.some).unsafeRunSync())
println(Something[IO].bar(none[String]).unsafeRunSync())

println(Something[IO].baz(2.asRight[String]).unsafeRunSync())
println(Something[IO].baz("ERROR!!!".asLeft[Int]).unsafeRunSync())

```
