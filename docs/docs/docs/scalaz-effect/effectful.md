---
layout: docs
title: "Effectful - Scalaz"
---

# Effectful
If you're sick of repeating `EffectConstructor[F].effectOf()` and looking for more convenient ways?, use `Effectful` instead.

```scala mdoc:reset-object
import effectie.Effectful._
import effectie.scalaz._

trait Something[F[_]] {
  def get[A](a: => A): F[A]
}

class SomethingF[F[_] : EffectConstructor] extends Something[F] {
  def get[A](a: => A): F[A] =
    effectOf(a)
    // No more EffectConstructor[F].effectOf(a)
}
```
