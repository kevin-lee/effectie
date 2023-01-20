---
sidebar_position: 7
id: console-effect
title: "ConsoleEffect"
---

## ConsoleEffect

```scala mdoc:compile-only
import cats._
import cats.syntax.all._

import effectie.monix._
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

import monix.eval._
import monix.execution.Scheduler.Implicits.global

val foo = Something[Task].foo()
foo.runSyncUnsafe()
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


## ConsoleEffectful

```scala mdoc:compile-only
import cats._
import cats.syntax.all._

import effectie.monix.ConsoleEffectful._
import effectie.monix._
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

import monix.eval._
import monix.execution.Scheduler.Implicits.global

val foo = Something[Task].foo()
foo.runSyncUnsafe()
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
