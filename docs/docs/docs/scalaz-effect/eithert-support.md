---
layout: docs
title: "EitherTSupport - Scalaz"
---

# EitherTSupport - Scalaz

```scala mdoc:reset-object
import scalaz._
import Scalaz._

import effectie.Effectful._
import effectie.scalaz._
import effectie.scalaz.EitherTSupport._

trait Something[F[_]] {
  def foo(a: Int): F[String \/ Int]
  def bar(a: String \/ Int): F[String \/ Int]
}

object Something {
  def apply[F[_] : Something]: Something[F] =
    implicitly[Something[F]]

  implicit def something[F[_] : EffectConstructor : Monad]: Something[F] =
    new SomethingF[F]

  final class SomethingF[F[_] : EffectConstructor : Monad]
    extends Something[F] {

    def foo(a: Int): F[String \/ Int] = (for {
      x <- eitherTLiftEffectOfPure(a) // == eitherT.liftF(effectOfPure(a))
      y <- eitherTLiftEffectOf(x + 10) // == eitherT.liftF(effectOf(x + 10))
      z <- eitherTLiftF[F, String, Int](effectOf(y + 100)) // == eitherT.lieftF(effectOf(y + 100))
    } yield z).run

    def bar(a: String \/ Int): F[String \/ Int] = (for {
      x <- eitherTEffectOfPure(a) // == eitherT(effectOfPure(a: String \/ Int))
      y <- eitherTEffectOf[F, String, Int]((x + 999).right[String])  // == eitherT(effectOf((x + 999).right[String]))
    } yield y).run
  }

}

import scalaz.effect._

Something[IO].foo(1).unsafePerformIO()
Something[IO].foo(10).unsafePerformIO()

Something[IO].bar(1.right[String]).unsafePerformIO()
Something[IO].bar("No number".left[Int]).unsafePerformIO()
```
