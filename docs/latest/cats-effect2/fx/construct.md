---
sidebar_position: 1
id: construct
title: "Construct - F[A]"
---
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

# Fx

If you use Cats Effect and write tagless final code, and look for a generic way to construct `F[A]`, `Fx` can help you.

## pureOf
To construct `F[A]` for a pure value `A`, you can use `pureOf`.

<Tabs
  groupId="fx"
  defaultValue="syntax"
  values={[
    {label: 'with syntax', value: 'syntax'},
    {label: 'without syntax', value: 'no-syntax'},
  ]}>
  <TabItem value="syntax">

```scala mdoc:reset
import effectie.core._
import effectie.syntax.all._

def foo[F[_]: Fx]: F[Int] = pureOf(1)
```

  </TabItem>
  
  <TabItem value="no-syntax">

```scala mdoc:reset
import effectie.core._

def foo[F[_]: Fx]: F[Int] = Fx[F].pureOf(1)
```

  </TabItem>
</Tabs>

<Tabs
  groupId="fx"
  defaultValue="syntax"
  values={[
    {label: 'with syntax', value: 'syntax'},
    {label: 'without syntax', value: 'no-syntax'},
  ]}>
  <TabItem value="syntax">

```scala mdoc:reset
import cats.effect._
import effectie.syntax.all._

import effectie.instances.ce2.fx._

pureOf[IO](1)
```

  </TabItem>
  
  <TabItem value="no-syntax">

```scala mdoc:reset
import cats.effect._
import effectie.core._

import effectie.instances.ce2.fx._

Fx[IO].pureOf(1)
```

  </TabItem>
</Tabs>


## effectOf
To construct `F[A]` for an operation with referential transparency, you can use `effectOf`.

<Tabs
  groupId="fx"
  defaultValue="syntax"
  values={[
    {label: 'with syntax', value: 'syntax'},
    {label: 'without syntax', value: 'no-syntax'},
  ]}>
  <TabItem value="syntax">

```scala mdoc:reset
import effectie.core._
import effectie.syntax.all._

def foo[F[_]: Fx](): F[Unit] = effectOf(println("Hello"))
```

```scala mdoc:nest
import cats.effect._

import effectie.instances.ce2.fx._

(for {
  _ <- foo[IO]()
  _ <- foo[IO]()
  _ <- foo[IO]()
} yield ()).unsafeRunSync()
```

  </TabItem>
  
  <TabItem value="no-syntax">

```scala mdoc:reset
import effectie.core._

def foo[F[_]: Fx](): F[Unit] = Fx[F].effectOf(println("Hello"))
```

```scala mdoc:nest
import cats.effect._

import effectie.instances.ce2.fx._

(for {
  _ <- foo[IO]()
  _ <- foo[IO]()
  _ <- foo[IO]()
} yield ()).unsafeRunSync()
```

  </TabItem>
</Tabs>

<Tabs
  groupId="fx"
  defaultValue="syntax"
  values={[
    {label: 'with syntax', value: 'syntax'},
    {label: 'without syntax', value: 'no-syntax'},
  ]}>
  <TabItem value="syntax">

```scala mdoc:reset:height=4
import cats.effect._
import effectie.syntax.all._

import effectie.instances.ce2.fx._

effectOf[IO](println("Hello"))

// effectOf can handle exception properly.
effectOf[IO][Int](throw new RuntimeException("ERROR"))
```

  </TabItem>
  
  <TabItem value="no-syntax">

```scala mdoc:reset:height=4
import cats.effect._
import effectie.core._

import effectie.instances.ce2.fx._

Fx[IO].effectOf(println("Hello"))

// effectOf can handle exception properly.
Fx[IO].effectOf[Int](throw new RuntimeException("ERROR"))
```

  </TabItem>
</Tabs>

## pureOrError
To construct `F[A]` for a pure value, but it can also throw an exception, you can use `pureOrError` instead of `effectOf`.

If an expression returns a pure value, and it's always the same so there's no point in using `effectOf` for referential transparency, you can use `pureOf`. However, if that expression can also throw an exception, `pureOf` cannot handle it properly. In this case, `pureOrError` is the right one.
 
e.g.)
```scala
val s: String = "abc"
pureOf[IO](s.substring(5))
// This immediately throws a StringIndexOutOfBoundsException even though F[_] here is IO.
```

```scala
val s: String = "abc"
pureOrError[IO](s.substring(5))
// StringIndexOutOfBoundsException is now captured by IO.
```


<Tabs
groupId="fx"
defaultValue="syntax"
values={[
{label: 'with syntax', value: 'syntax'},
{label: 'without syntax', value: 'no-syntax'},
]}>
<TabItem value="syntax">

```scala mdoc:reset
import effectie.core._
import effectie.syntax.all._

def foo[F[_]: Fx]: F[Int] = pureOrError(1)
def bar[F[_]: Fx](s: String): F[String] = pureOrError(s.substring(5))
```

  </TabItem>

  <TabItem value="no-syntax">

```scala mdoc:reset
import effectie.core._

def foo[F[_]: Fx]: F[Int] = Fx[F].pureOrError(1)
def bar[F[_]: Fx](s: String): F[String] = Fx[F].pureOrError(s.substring(5))
```

  </TabItem>
</Tabs>

<Tabs
groupId="fx"
defaultValue="syntax"
values={[
{label: 'with syntax', value: 'syntax'},
{label: 'without syntax', value: 'no-syntax'},
]}>
<TabItem value="syntax">

```scala mdoc:reset
import cats.effect._
import effectie.syntax.all._

import effectie.instances.ce2.fx._

pureOrError[IO](1)

pureOrError[IO]("abc".substring(5))
```

  </TabItem>

  <TabItem value="no-syntax">

```scala mdoc:reset
import cats.effect._
import effectie.core._

import effectie.instances.ce2.fx._

Fx[IO].pureOrError(1)

Fx[IO].pureOrError("abc".substring(5))
```

  </TabItem>
</Tabs>


***

## unitOf
If you just return `F[Unit]`, you can use `unitOf`.

e.g.)
```scala
unitOf[IO] // IO[Unit]
```

<Tabs
groupId="fx"
defaultValue="syntax"
values={[
{label: 'with syntax', value: 'syntax'},
{label: 'without syntax', value: 'no-syntax'},
]}>
<TabItem value="syntax">

```scala mdoc:reset
import cats._
import cats.syntax.all._

import effectie.core._
import effectie.syntax.all._

def foo[F[_]: Fx]: F[Unit] = unitOf

def bar[F[_]: Fx: Monad]: F[Unit] =
  effectOf(println("Hello")) *> unitOf // You can do effectOf(println("Hello")).void instead
```

  </TabItem>

  <TabItem value="no-syntax">

```scala mdoc:reset
import cats._
import cats.syntax.all._

import effectie.core._

def foo[F[_]: Fx]: F[Unit] = Fx[F].unitOf

def bar[F[_]: Fx: Monad]: F[Unit] =
  Fx[F].effectOf(println("Hello")) *> Fx[F].unitOf // You can do Fx[F].effectOf(println("Hello")).void instead
```

  </TabItem>
</Tabs>

<Tabs
groupId="fx"
defaultValue="syntax"
values={[
{label: 'with syntax', value: 'syntax'},
{label: 'without syntax', value: 'no-syntax'},
]}>
<TabItem value="syntax">

```scala mdoc:reset
import cats.effect._
import effectie.syntax.all._

import effectie.instances.ce2.fx._

unitOf[IO]
```

  </TabItem>

  <TabItem value="no-syntax">

```scala mdoc:reset
import cats.effect._
import effectie.core._

import effectie.instances.ce2.fx._

Fx[IO].unitOf
```

  </TabItem>
</Tabs>


***

## Example

<Tabs
groupId="fx"
defaultValue="syntax"
values={[
{label: 'with syntax', value: 'syntax'},
{label: 'without syntax', value: 'no-syntax'},
]}>
<TabItem value="syntax">

```scala mdoc:reset-object:height=4
import effectie.core._
import effectie.syntax.all._

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

import cats.effect._
import effectie.instances.ce2.fx._

val get1 = Something[IO].get(1)

get1.unsafeRunSync()
```


  </TabItem>

  <TabItem value="no-syntax">

```scala mdoc:reset-object:height=4
import effectie.core._

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

import cats.effect._
import effectie.instances.ce2.fx._

val get1 = Something[IO].get(1)

get1.unsafeRunSync()
```

  </TabItem>
</Tabs>
