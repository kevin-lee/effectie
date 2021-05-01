---
id: effect-constructor
title: "EffectConstructor"
---

## EffectConstructor

If you use Monix and write tagless final code, and look for a generic way to construct `F[A]`, `EffectConstructor` can help you.

```scala mdoc:reset-object
import effectie.monix._

trait Something[F[_]] {
  def get[A](a: => A): F[A]
}

object Something {
  def apply[F[_]: Something]: Something[F] =
    implicitly[Something[F]]

  implicit def something[F[_]: EffectConstructor]: Something[F] =
    new SomethingF[F]

  final class SomethingF[F[_]: EffectConstructor]
    extends Something[F] {

    def get[A](a: => A): F[A] =
      EffectConstructor[F].effectOf(a)
  }
}

import monix.eval._
import monix.execution.Scheduler.Implicits.global

val get1 = Something[Task].get(1)

get1.runSyncUnsafe()
```

If you feel it's too cumbersome to repeat `EffectConstructor[F].effectOf()`, consider using [Effectful](#effectful)


## Effectful

If you're sick of repeating `EffectConstructor[F].effectOf()` and looking for more convenient ways?, use `Effectful` instead.

```scala mdoc:reset-object
import effectie.monix.Effectful._
import effectie.monix._

trait Something[F[_]] {
  def get[A](a: => A): F[A]
}

object Something {
  def apply[F[_]: Something]: Something[F] =
    implicitly[Something[F]]

  implicit def something[F[_]: EffectConstructor]: Something[F] =
    new SomethingF[F]

  final class SomethingF[F[_]: EffectConstructor]
    extends Something[F] {

    def get[A](a: => A): F[A] =
      effectOf(a)
      // No more EffectConstructor[F].effectOf(a)
  }
}

import monix.eval._
import monix.execution.Scheduler.Implicits.global

val get1 = Something[Task].get(1)

get1.runSyncUnsafe()
```
