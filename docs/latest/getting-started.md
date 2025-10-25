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

|               Project |                                                                               Maven Central                                                                               | JVM | Scala.js |
|----------------------:|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:---:|:--------:|
| effectie-cats-effect3 | [![Maven Central](https://img.shields.io/maven-central/v/io.kevinlee/effectie-cats-effect3_3)](https://central.sonatype.com/artifact/io.kevinlee/effectie-cats-effect3_3) |  ✅  |    ✅     |
| effectie-cats-effect2 | [![Maven Central](https://img.shields.io/maven-central/v/io.kevinlee/effectie-cats-effect2_3)](https://central.sonatype.com/artifact/io.kevinlee/effectie-cats-effect2_3) |  ✅  |    ✅     |
|       effectie-monix3 |       [![Maven Central](https://img.shields.io/maven-central/v/io.kevinlee/effectie-monix3_3)](https://central.sonatype.com/artifact/io.kevinlee/effectie-monix3_3)       |  ✅  |    ✅     |

:::info

### Supported Scala Versions:

* JVM: @SUPPORTED_SCALA_VERSIONS@
* [![Scala.js](https://www.scala-js.org/assets/badges/scalajs-1.18.0.svg)](https://www.scala-js.org):
  @SUPPORTED_SCALA_VERSIONS_FOR_SCALA_JS@

:::

A set of type-classes and utils for functional effect libraries (i.e. Cats
Effect, Monix and Scalaz's Effect).

Why Effectie? Please read ["Why?"](#why) section.

## Getting Started

### For Cats Effect 3

<Tabs
groupId="cats-effect3"
defaultValue="cats-effect3-sbt"
values={[
{label: 'sbt', value: 'cats-effect3-sbt'},
{label: 'sbt (with libraryDependencies)', value: 'cats-effect3-sbt-lib'},
{label: 'scala-cli', value: 'cats-effect3-scala-cli'},
]}>
<TabItem value="cats-effect3-sbt">

In `build.sbt`,

```scala
"io.kevinlee" %% "effectie-cats-effect3" % "@VERSION@"
```

For Scala.js,
```scala
"io.kevinlee" %%% "effectie-cats-effect3" % "@VERSION@"
```

  </TabItem>

  <TabItem value="cats-effect3-sbt-lib">

In `build.sbt`,

```scala
libraryDependencies += "io.kevinlee" %% "effectie-cats-effect3" % "@VERSION@"
```

For Scala.js,
```scala
libraryDependencies += "io.kevinlee" %%% "effectie-cats-effect3" % "@VERSION@"
```

  </TabItem>

  <TabItem value="cats-effect3-scala-cli">

```scala
//> using dep "io.kevinlee::effectie-cats-effect3:@VERSION@"
```

  </TabItem>
</Tabs>

### For Cats Effect 2

<Tabs
groupId="cats-effect2"
defaultValue="cats-effect2-sbt"
values={[
{label: 'sbt', value: 'cats-effect2-sbt'},
{label: 'sbt (with libraryDependencies)', value: 'cats-effect2-sbt-lib'},
{label: 'scala-cli', value: 'cats-effect2-scala-cli'},
]}>
<TabItem value="cats-effect2-sbt">

In `build.sbt`,

```scala
"io.kevinlee" %% "effectie-cats-effect2" % "@VERSION@"
```

For Scala.js,
```scala
"io.kevinlee" %%% "effectie-cats-effect2" % "@VERSION@"
```

  </TabItem>

  <TabItem value="cats-effect2-sbt-lib">

In `build.sbt`,

```scala
libraryDependencies += "io.kevinlee" %% "effectie-cats-effect2" % "@VERSION@"
```

For Scala.js,
```scala
libraryDependencies += "io.kevinlee" %%% "effectie-cats-effect2" % "@VERSION@"
```

  </TabItem>

  <TabItem value="cats-effect2-scala-cli">

```scala
//> using dep "io.kevinlee::effectie-cats-effect2:@VERSION@"
```

  </TabItem>
</Tabs>

For more details, check
out [Effectie for Cats Effect](cats-effect2/cats-effect2.md).

### For Monix

<Tabs
groupId="monix3"
defaultValue="monix3-sbt"
values={[
{label: 'sbt', value: 'monix3-sbt'},
{label: 'sbt (with libraryDependencies)', value: 'monix3-sbt-lib'},
{label: 'scala-cli', value: 'monix3-scala-cli'},
]}>
<TabItem value="monix3-sbt">

In `build.sbt`,

```scala
"io.kevinlee" %% "effectie-monix3" % "@VERSION@"
```

For Scala.js,
```scala
"io.kevinlee" %%% "effectie-monix3" % "@VERSION@"
```

  </TabItem>

  <TabItem value="monix3-sbt-lib">

In `build.sbt`,

```scala
libraryDependencies += "io.kevinlee" %% "effectie-monix3" % "@VERSION@"
```

For Scala.js,
```scala
libraryDependencies += "io.kevinlee" %%% "effectie-monix3" % "@VERSION@"
```

  </TabItem>

  <TabItem value="monix3-scala-cli">

```scala
//> using dep "io.kevinlee::effectie-monix3:@VERSION@"
```

  </TabItem>
</Tabs>


For more details, check out [Effectie for Monix](monix3/monix3.md).

## Why?

Tagless final gives us power to defer the decision of the implementations of
contexts we're binding and functional effect libraries like Cats Effect and
Monix give us referential transparency (and more). There might be an issue
though with writing implementation for the abstraction which is supposed to
support not only effect libraries like Cats Effect but also `Future`. You may
end up writing exactly the same code with only an exception to how you construct
effect data type (e.g. `IO` vs `Future`).

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
  def apply[F[_] : Sync](): Foo[F] = new Foo[F] {
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

Then for some reason, your company uses another tech stack with `Future` so you
need to repeat the same logic with `Future` like this.

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

Now you need to continuously spend more time to maintain two code bases for the
same operation.

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
  def apply[F[_] : Fx : Monad](): Foo[F] = new Foo[F] {
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

With just one code base above, you can use `IO` or `Future` as you wish like
this.

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
