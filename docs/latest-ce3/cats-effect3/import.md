---
sidebar_position: 1
id: 'import'
title: "What to Import"
---
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

# What to Import

## Most Places
```scala
import effectie.core._
import effectie.syntax.all._
```

```scala
import effectie.core._ // for Fx
import effectie.syntax.all._ // for effectOf(), pureOf(), errorOf(), etc.

def foo[F[_]: Fx](n: Int): F[int] =
  for {
    n2 <- bar(n)
  } yield n + n2
```

## For Main Method
You need instances for `Fx` when you actually run your program.
It's usually only one place where you put the `main` method.

### `Fx[F]` Instance

For the instance of `Fx[F]`,
```scala
import effectie.instances.ce3.fx.ioFx
```

## Example

```scala mdoc:reset-object
import cats._
import cats.syntax.all._

import effectie.core._
import effectie.syntax.all._

trait Foo[F[_]] {
  def foo(n: Int): F[Int]
}
object Foo {
  def apply[F[_]: Fx: Monad]: Foo[F] = new FooF[F]
  
  private class FooF[F[_]: Fx: Monad] extends Foo[F] {
    def foo(n: Int): F[Int] = {
      for {
        n2 <- 
          if (n > 0)
            pureOf(n * 2)
          else
            pureOf(n * n)
        n3 <- pureOf(n2 * 3)
        n4 <- pureOf(n2 * 2)
      } yield n3 + n4
    }
  }
}
```
```scala mdoc
import cats.effect._

object MyApp extends IOApp.Simple {

  import effectie.instances.ce3.fx.ioFx
  
  def run: IO[Unit] = {
    for {
      n <- Foo[IO].foo(123)
      _ <- putStrLn(s"Result: ${n.toString}")
    } yield ()
  }

}

MyApp.main(Array.empty)
```
