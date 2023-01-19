---
sidebar_position: 1
layout: docs
id: fx
title: "Fx"
---

## Fx

If you use Scalaz Effect and write tagless final code, and look for a generic way to construct `F[A]`, `Fx` can help you.

```scala mdoc:reset-object
import effectie.scalaz._

trait Something[F[_]] {
  def get[A](a: => A): F[A]
}

object Something {
  def apply[F[_]: Something]: Something[F] =
    implicitly[Something[F]]

  implicit def something[F[_]: Fx]: Something[F] =
    new SomethingF[F]

  final class SomethingF[F[_]: Fx]
    extends Something[F] {

    def get[A](a: => A): F[A] =
      Fx[F].effectOf(a)
  }
}

import scalaz.effect._

val get1 = Something[IO].get(1)

get1.unsafePerformIO()
```

If you feel it's too cumbersome to repeat `Fx[F].effectOf()`, consider using [Effectful](#effectful)


## Effectful

If you're sick of repeating `Fx[F].effectOf()` and looking for more convenient ways?, use `Effectful` instead.

```scala mdoc:reset-object
import effectie.scalaz.Effectful._
import effectie.scalaz._

trait Something[F[_]] {
  def get[A](a: => A): F[A]
}

object Something {
  def apply[F[_]: Something]: Something[F] =
    implicitly[Something[F]]

  implicit def something[F[_]: Fx]: Something[F] =
    new SomethingF[F]

  final class SomethingF[F[_]: Fx]
    extends Something[F] {

    def get[A](a: => A): F[A] =
      effectOf(a)
      // No more Fx[F].effectOf(a)
  }
}

import scalaz.effect._

val get1 = Something[IO].get(1)

get1.unsafePerformIO()
```
