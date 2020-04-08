---
layout: docs
title: "EffectConstructor - Scalaz"
---

# EffectConstructor
If you use Scalaz Effect and write tagless final code, and look for a generic way to construct `F[A]`, `EffectConstructor` can help you.

```scala mdoc:reset-object
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
      EffectConstructor[F].effectOf(a)
  }
}

import scalaz.effect._

val get1 = Something[IO].get(1)

get1.unsafePerformIO()
```

If you feel it's too cumbersome to repeat `EffectConstructor[F].effectOf()`, consider using [Effectful](effectful.md)
