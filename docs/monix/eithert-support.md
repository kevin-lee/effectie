---
id: eithert-support
title: "EitherTSupport"
---

## EitherTSupport

```scala mdoc:reset-object
import cats._
import cats.syntax.all._

import effectie.monix.Effectful._
import effectie.monix._
import effectie.monix.EitherTSupport._

trait Something[F[_]] {
  def foo(a: Int): F[Either[String, Int]]
  def bar(a: Either[String, Int]): F[Either[String, Int]]
}

object Something {
  def apply[F[_]: Something]: Something[F] =
    implicitly[Something[F]]

  implicit def something[F[_]: EffectConstructor: Monad]: Something[F] =
    new SomethingF[F]

  final class SomethingF[F[_]: EffectConstructor: Monad]
    extends Something[F] {

    def foo(a: Int): F[Either[String, Int]] = (for {
      x <- eitherTRightPure(a) // == EitherT.liftF(pureOf(a))
      y <- eitherTRight(x + 10) // == EitherT.liftF(effectOf(x + 10))
      y2 <- if (y > 100)
          eitherTLeft("Error - Bigger than 100")
        else
          eitherTRightPure(y)
        // ↑ if (y > 100)
        //     EitherT(effectOf("Error - Bigger than 100").map(_.asLeft[Int]))
        //   else
        //     EitherT(pureOf(y).map(_.asRight[String]))
      z <- eitherTRightF[String](effectOf(y2 + 100)) // == EitherT.lieftF(effectOf(y + 100))
    } yield z).value

    def bar(a: Either[String, Int]): F[Either[String, Int]] = (for {
      x <- eitherTOfPure(a) // == EitherT(pureOf(a: Either[String, Int]))
      y <- eitherTOf((x + 999).asRight[String])  // == EitherT(effectOf((x + 999).asRight[String]))
    } yield y).value
  }

}

import monix.eval._
import monix.execution.Scheduler.Implicits.global

Something[Task].foo(1).runSyncUnsafe()
Something[Task].foo(10).runSyncUnsafe()

Something[Task].bar(1.asRight[String]).runSyncUnsafe()
Something[Task].bar("No number".asLeft[Int]).runSyncUnsafe()
```