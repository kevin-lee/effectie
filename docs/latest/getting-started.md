---
sidebar_position: 1
id: getting-started
title: Getting Started
slug: "/"
---

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

## ![Effectie Logo](/img/effectie-logo-96x96.png) Effectie

[![Build Status](https://github.com/Kevin-Lee/effectie/workflows/Build-All/badge.svg)](https://github.com/Kevin-Lee/effectie/actions?workflow=Build-All)
[![Release Status](https://github.com/Kevin-Lee/effectie/workflows/Release/badge.svg)](https://github.com/Kevin-Lee/effectie/actions?workflow=Release)
[![Latest version](https://index.scala-lang.org/kevin-lee/effectie/latest.svg)](https://index.scala-lang.org/kevin-lee/effectie)


|               Project | Maven Central                                                                                                                                                                                   |
|----------------------:|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| effectie-cats-effect3 | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.kevinlee/effectie-cats-effect3_2.13/badge.svg)](https://search.maven.org/artifact/io.kevinlee/effectie-cats-effect3_2.13) |
| effectie-cats-effect2 | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.kevinlee/effectie-cats-effect2_2.13/badge.svg)](https://search.maven.org/artifact/io.kevinlee/effectie-cats-effect2_2.13) |
|       effectie-monix3 | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.kevinlee/effectie-monix3_2.13/badge.svg)](https://search.maven.org/artifact/io.kevinlee/effectie-monix3_2.13)             |

* Supported Scala Versions: @SUPPORTED_SCALA_VERSIONS@

A set of type-classes and utils for functional effect libraries (i.e.  Cats Effect, Monix and Scalaz's Effect).

Why Effectie? Please read ["Why?"](#why) section.

## Getting Started
### For Cats Effect

In `build.sbt`,

<Tabs
groupId="cats-effect"
defaultValue="cats-effect"
values={[
{label: 'Cats Effect 3', value: 'cats-effect3'},
{label: 'Cats Effect 2', value: 'cats-effect'},
]}>
<TabItem value="cats-effect3">

```scala
libraryDependencies += "io.kevinlee" %% "effectie-cats-effect3" % "@VERSION@"
```

  </TabItem>

  <TabItem value="cats-effect">

```scala
libraryDependencies += "io.kevinlee" %% "effectie-cats-effect2" % "@VERSION@"
```

  </TabItem>
</Tabs>

For more details, check out [Effectie for Cats Effect](cats-effect2/cats-effect2.md).


### For Monix

In `build.sbt`,

```scala
libraryDependencies += "io.kevinlee" %% "effectie-monix" % "@VERSION@"
```

For more details, check out [Effectie for Monix](monix3/monix3.md).



## Why?
Tagless final gives us power to defer the decision of the implementations of contexts we're binding and functional effect libraries like Cats Effect and Monix give us referential transparency (and more). There might be an issue though with writing implementation for the abstraction which is supposed to support not only effect libraries like Cats Effect but also `Future`. You may end up writing exactly the same code with only an exception to how you construct effect data type (e.g. `IO` vs `Future`). 

Let's check out some code examples.

### Problem: Duplicate Implementations
If you use Cats Effect, you may write code like this.
```scala mdoc:reset-object:height=1
import cats.syntax.all._
import cats.effect._

trait Foo[F[_]] {
  def foo(a: Int, b: Int): F[Int]
}
object Foo {
  def apply[F[_]: Sync](): Foo[F] = new Foo[F] {
    def foo(a: Int, b: Int): F[Int] =
      for {
        n1 <- bar(a)
        n2 <- bar(b)
      } yield n1 + n2

    private def bar(n: Int): F[Int] = for {
      n2 <- Sync[F].delay(math.abs(n))
      result <- if (n2 < 0)
                  Sync[F].raiseError(
                    new IllegalArgumentException("n is Int.MinValue so abs doesn't work for it.")
                  )
                else
                  Sync[F].pure(n2)
    } yield result
  }
}

val foo = Foo[IO]()

foo.foo(1, 2).unsafeRunSync()
```

Then for some reason, your company uses another tech stack with `Future` so you need to repeat the same logic with `Future` like this.
```scala mdoc:nest:height=1
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

class FooFuture extends Foo[Future] {
  def foo(a: Int, b: Int): Future[Int] =
    for {
      n1 <- bar(a)
      n2 <- bar(b)
    } yield n1 + n2

  private def bar(n: Int): Future[Int] = for {
    n2 <- Future(math.abs(n))
    result <- if (n2 < 0)
      Future.failed(
        new IllegalArgumentException("n is Int.MinValue so abs doesn't work for it.")
      )
    else
      Future.successful(n2)
  } yield result
}

import scala.concurrent.duration._

val foo = new FooFuture
Await.result(
  foo.foo(1, 2),
  Duration.Inf
)
```
Now you need to continuously spend more time to maintain two code bases for the same operation.

***

### No More Duplicates with Effectie

This issue can be solved easily with Effectie.
```scala mdoc:reset-object:height=1
import cats._
import cats.syntax.all._
import effectie.core._
import effectie.syntax.all._

trait Foo[F[_]] {
  def foo(a: Int, b: Int): F[Int]
}
object Foo {
  def apply[F[_]: Fx: Monad](): Foo[F] = new Foo[F] {
    def foo(a: Int, b: Int): F[Int] =
      for {
        n1 <- bar(a)
        n2 <- bar(b)
      } yield n1 + n2

    private def bar(n: Int): F[Int] = for {
      n2 <- effectOf(math.abs(n))
      result <- if (n2 < 0)
                  errorOf(
                    new IllegalArgumentException("n is Int.MinValue so abs doesn't work for it.")
                  )
                else
                  pureOf(n2)
    } yield result
  }
}
```
With just one code base above, you can use `IO` or `Future` as you wish like this.
```scala mdoc:nest:height=1
import cats.effect._
import effectie.instances.ce2.fx._

val foo = Foo[IO]()
foo.foo(1, 2).unsafeRunSync()
```
```scala mdoc:nest:height=1
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.duration._

import effectie.instances.future.fx._
val foo2 = Foo[Future]()
Await.result(
  foo2.foo(1, 2),
  Duration.Inf
)
```
As you can see, you can use the same `Foo` for both `IO` and `Future`.


Check out
* [Effectie for Cats Effect 2](cats-effect2/cats-effect2.md)
* Effectie for Cats Effect 3 (Writing docs WIP)
* [Effectie for Monix 3](monix3/monix3.md)
