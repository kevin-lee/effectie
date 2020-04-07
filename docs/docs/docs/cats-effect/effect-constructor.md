---
layout: docs
title: "EffectConstructor - Cats"
---

# EffectConstructor
If you use Cats Effect and write tagless final code, and look for a generic way to construct `F[A]`, `EffectConstructor` can help you.

```scala mdoc:reset-object
import effectie.cats._

trait Something[F[_]] {
  def get[A](a: => A): F[A]
}

class SomethingF[F[_] : EffectConstructor] extends Something[F] {
  def get[A](a: => A): F[A] =
    EffectConstructor[F].effectOf(a)
}
```

If you feel it's too cumbersome to repeat `EffectConstructor[F].effectOf()`, consider using [`Effectful`](effectful.md)
