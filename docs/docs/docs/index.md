---
layout: docs
title: Getting Started
---

# Effectie
A set of type-classes and utils for functional effect libraries (i.e. Scalaz and Cats Effect)

Why Effectie? Please read ["Why?"](#why) section.

# Getting Started
## For Cats Effect

In `build.sbt`,

```scala
libraryDependencies += "io.kevinlee" %% "effectie-cats-effect" % "0.2.0"
```
then import

```scala
import effectie.ConsoleEffectful._
import effectie.Effectful._

import effectie.cats.EitherTSupport._
import effectie.cats.OptionTSupport._
import effectie.cats._
```

For more details, check out [Effectie for Cats Effect](cats-effect).


## For Scalaz Effect

In `build.sbt`,

```scala
libraryDependencies += "io.kevinlee" %% "effectie-scalaz-effect" % "0.2.0"
```
then import

```scala
import effectie.ConsoleEffectful._
import effectie.Effectful._

import effectie.scalaz.EitherTSupport._
import effectie.scalaz.OptionTSupport._
import effectie.scalaz._
```

For more details, check out [Effectie for Scalaz Effect](scalaz-effect).


# Why?
Tagless final gives us power to defer the decision of the implementations of contexts we're binding and functional effect libraries like Cats Effect and Scalaz Effect give us referential transparency. There might be an issue though with constructing an effect type data. It is actually an issue with Cats Effect as Cats Effect `IO`'s `pure` (or `Monad.pure`) is not referentially transparent. 

Let's check out some code examples.

e.g.) Cats Effect
```scala mdoc:reset-object
import cats.effect._

// or cats.Monad[IO].pure(println("a"))
val io = IO.pure(println("a"))
// It is not referentially transparent so immediately evaluates println("a") 

io.unsafeRunSync()
io.unsafeRunSync()
```

e.g.) Scalaz Effect
```scala mdoc:reset-object
import scalaz._, scalaz.effect._

val io = Monad[IO].pure(println("a"))
// It is referentially transparent so println("a") is not evaluated here.

io.unsafePerformIO()
io.unsafePerformIO()
```

So to have referential transparency when using Cats Effect, `IO.apply()` should be used.
```scala mdoc:reset-object
import cats.effect._

val io = IO(println("a"))
// Now it is referentially transparent so println("a") is not evaluated here. 

io.unsafeRunSync()
io.unsafeRunSync()
```

Now, let's use Cats Effect with tagless final.
```scala
import cats.effect._

trait Foo[F[_]] {
  def get[A](a: => A): F[A]
}

class Bar[F[_]] extends Foo[F] {
  def get[A](a: => A): F[A] =
    // How would you construct F[A]?
}

// call-site
val bar = new Bar[IO]
bar.get(1)
bar.get(println("a"))
```
How would you construct `F[A]`? You could probably do `Applicative[F].pure` or `Monad[F].pure(a)`. 
```scala
import cats._

class Bar[F[_] : Applicative] extends Foo[F] { // or [F[_] : Monad]
  def get[A](a: => A): F[A] =
    Applicative[F].pure(a) // or Monad[F].pure(a)
}
```
However, neither `Applicative.pure` nor `Monad.pure` in Cats are referentially transparent when it's mixed with impure code (e.g. some side-effect code like `println("a")`).
So If you do this,
```scala
val bar = new Bar[IO]
val iou = bar.get(println("a"))
// a is printed here
// and you get IO[Unit]

iou.unsafeRunSync() // This does not print anything but returns ()
```

With Effectie you can do this.
```scala mdoc:reset-object
import cats.effect._

import effectie.cats._

trait Foo[F[_]] {
  def get[A](a: => A): F[A]
}

class Bar[F[_] : EffectConstructor] extends Foo[F] {
  def get[A](a: => A): F[A] =
    EffectConstructor[F].effectOf(a)
}

// call-site
val bar = new Bar[IO]
val iou = bar.get(println("a"))
// This does not print anything here.

iou.unsafeRunSync()
iou.unsafeRunSync()
// Now you get "a" printed whenever the iou (`IO[Unit]`) is evaluated.
```

Or a more convenient way like
```scala mdoc:reset-object
import cats.effect._

import effectie.Effectful._
import effectie.cats._

trait Foo[F[_]] {
  def get[A](a: => A): F[A]
}

class Bar[F[_] : EffectConstructor] extends Foo[F] {
  def get[A](a: => A): F[A] =
    effectOf(a) // no more EffectConstructor[F].effectOf
}

// call-site
val bar = new Bar[IO]
val iou = bar.get(println("a"))
// This does not print anything here.

iou.unsafeRunSync()
iou.unsafeRunSync()
// Now you get "a" printed whenever the iou (`IO[Unit]`) is evaluated.
```

Check out
* [Effectie for Cats Effect](cats-effect)
* [Effectie for Scalaz Effect](scalaz-effect)
