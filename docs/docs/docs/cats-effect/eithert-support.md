---
layout: docs
title: "EitherTSupport - Cats"
---

# EitherTSupport - Cats

```scala mdoc:reset-object
import cats._
import cats.implicits._

import effectie.Effectful._
import effectie.cats._
import effectie.cats.EitherTSupport._

trait Something[F[_]] {
  def foo(a: Int): F[Either[String, Int]]
  def bar(a: Either[String, Int]): F[Either[String, Int]]
}

object Something {
  def apply[F[_]: Something]: Something[F] =
    implicitly[Something[F]]

  implicit def something[F[_]: EffectConstructor: Monad]: Something[F] =
    new SomethingF[F]

  final class SomethingF[F[_]: EffectConstructor: Monad]
    extends Something[F] {

    def foo(a: Int): F[Either[String, Int]] = (for {
      x <- eitherTRightPure(a) // == EitherT.liftF(effectOfPure(a))
      y <- eitherTRight(x + 10) // == EitherT.liftF(effectOf(x + 10))
      y2 <- if (y > 100) eitherTLeft("Error - Bigger than 100") else eitherTRightPure(y)
         // ↑ if y > 100 EitherT(effectOf("Error - Bigger than 100").map(_.asLeft[Int]))
      z <- eitherTRightF[String](effectOf(y + 100)) // == EitherT.lieftF(effectOf(y + 100))
    } yield z).value

    def bar(a: Either[String, Int]): F[Either[String, Int]] = (for {
      x <- eitherTOfPure(a) // == EitherT(effectOfPure(a: Either[String, Int]))
      y <- eitherTOf((x + 999).asRight[String])  // == EitherT(effectOf((x + 999).asRight[String]))
    } yield y).value
  }

}

import cats.effect._

Something[IO].foo(1).unsafeRunSync()
Something[IO].foo(10).unsafeRunSync()

Something[IO].bar(1.asRight[String]).unsafeRunSync()
Something[IO].bar("No number".asLeft[Int]).unsafeRunSync()
```