---
id: console-effect
title: "ConsoleEffect - Cats"
---

## ConsoleEffect

```scala
import cats._
import cats.implicits._

import effectie.cats._
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


## ConsoleEffectful

```scala
import cats._
import cats.implicits._

import effectie.cats.ConsoleEffectful._
import effectie.cats._
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
