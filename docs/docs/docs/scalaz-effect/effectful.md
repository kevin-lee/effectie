---
layout: docs
title: "Effectful - Scalaz"
---

# Effectful - Scalaz

If you're sick of repeating `EffectConstructor[F].effectOf()` and looking for more convenient ways?, use `Effectful` instead.

```scala mdoc:reset-object
import effectie.Effectful._
import effectie.scalaz._

trait Something[F[_]] {
  def get[A](a: => A): F[A]
}

object Something {
  def apply[F[_] : Something]: Something[F] =
    implicitly[Something[F]]

  implicit def something[F[_] : EffectConstructor]: Something[F] =
    new SomethingF[F]

  final class SomethingF[F[_] : EffectConstructor]
    extends Something[F] {

    def get[A](a: => A): F[A] =
      effectOf(a)
      // No more EffectConstructor[F].effectOf(a)
  }
}

import scalaz.effect._

val get1 = Something[IO].get(1)

get1.unsafePerformIO()
```
