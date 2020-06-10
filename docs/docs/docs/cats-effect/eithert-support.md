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
      x <- eitherTLiftEffectOfPure(a) // == eitherT.liftF(effectOfPure(a))
      y <- eitherTLiftEffectOf(x + 10) // == eitherT.liftF(effectOf(x + 10))
      z <- eitherTLiftF[F, String, Int](effectOf(y + 100)) // == eitherT.lieftF(effectOf(y + 100))
    } yield z).value

    def bar(a: Either[String, Int]): F[Either[String, Int]] = (for {
      x <- eitherTEffectOfPure(a) // == eitherT(effectOfPure(a: Either[String, Int]))
      y <- eitherTEffectOf[F, String, Int]((x + 999).asRight[String])  // == eitherT(effectOf((x + 999).asRight[String]))
    } yield y).value
  }

}

import cats.effect._

Something[IO].foo(1).unsafeRunSync()
Something[IO].foo(10).unsafeRunSync()

Something[IO].bar(1.asRight[String]).unsafeRunSync()
Something[IO].bar("No number".asLeft[Int]).unsafeRunSync()
```