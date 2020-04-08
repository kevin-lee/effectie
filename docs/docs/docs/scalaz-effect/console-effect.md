---
layout: docs
title: "ConsoleEffect - Scalaz"
---

# ConsoleEffect

```scala
import scalaz._
import Scalaz._

import effectie.scalaz._
import effectie.YesNo

trait Something[F[_]] {
  def foo[A](): F[Unit]
}

object Something {

  def apply[F[_] : Something]: Something[F] =
    implicitly[Something[F]]

  implicit def something[F[_] : ConsoleEffect : Monad]: Something[F] =
    new SomethingF[F]

  final class SomethingF[F[_] : ConsoleEffect : Monad]
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

import scalaz.effect._

val foo = Something[IO].foo()
foo.unsafePerformIO()
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
