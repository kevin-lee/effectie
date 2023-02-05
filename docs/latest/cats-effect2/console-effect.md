---
sidebar_position: 5
id: console-effect
title: "ConsoleEffect"
---

## ConsoleEffect

```scala mdoc:compile-only
import cats._
import cats.syntax.all._

import effectie.core._
import effectie.core.YesNo

trait Something[F[_]] {
  def foo[A](): F[Unit]
}

object Something {

  def apply[F[_]: Something]: Something[F] =
    implicitly[Something[F]]

  implicit def something[F[_]: ConsoleEffect: Monad]: Something[F] =
    new SomethingF[F]

  final class SomethingF[F[_]: ConsoleEffect: Monad]
    extends Something[F] {

    def foo[A](): F[Unit] = for {
      _ <- ConsoleEffect[F].putStrLn("Hello")
      answer <- ConsoleEffect[F].readYesNo("Would you like to proceed?")
      result = answer match {
            case YesNo.Yes =>
              "Done"
            case YesNo.No =>
              "Cancelled"
          }
      _ <- ConsoleEffect[F].putStrLn(result)
    } yield ()
  }
}

import cats.effect._
import effectie.instances.ce2.fx._
import effectie.instances.console._

val foo = Something[IO].foo()
foo.unsafeRunSync()
```

```
Hello
Would you like to proceed?
n
Cancelled
```
```
Hello
Would you like to proceed?
y
Done
```


## Syntax

```scala modc:compile-only
import cats._
import cats.syntax.all._

import effectie.core._
import effectie.syntax.all._
import effectie.YesNo

trait Something[F[_]] {
  def foo[A](): F[Unit]
}

object Something {

  def apply[F[_]: Something]: Something[F] =
    implicitly[Something[F]]

  implicit def something[F[_]: ConsoleEffect: Monad]: Something[F] =
    new SomethingF[F]

  final class SomethingF[F[_]: ConsoleEffect: Monad]
    extends Something[F] {

    def foo[A](): F[Unit] = for {
      _ <- putStrLn("Hello")
      answer <- readYesNo("Would you like to proceed?")
      result = answer match {
            case YesNo.Yes =>
              "Done"
            case YesNo.No =>
              "Cancelled"
          }
      _ <- putStrLn(result)
    } yield ()
  }
}

import cats.effect._

val foo = Something[IO].foo()
foo.unsafeRunSync()
```

```
Hello
Would you like to proceed?
n
Cancelled
```
```
Hello
Would you like to proceed?
y
Done
```
