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

  implicit def something[F[_]: Fx: Monad]: Something[F] =
    new SomethingF[F]

  final class SomethingF[F[_]: Fx: Monad]
    extends Something[F] {

    def foo(a: Int): F[Either[String, Int]] = (for {
      x <- a.rightTF[F, String] // == EitherT.liftF(Applicative[F].pure(a))
      y <- (x + 10).rightTF[F, String] // == EitherT.liftF(Applicative[F].pure(x + 10))
      y2 <- if (y > 100)
              "Error - Bigger than 100".leftTF[F, Int]
            else
              y.rightTF[F, String]
       // ↑ if (y > 100)
       //     EitherT(pureOf("Error - Bigger than 100").map(_.asLeft[Int]))
       //   else
       //     EitherT(pureOf(y).map(_.asRight[String]))
      z <- effectOf(y2 + 100).rightT[String] // == EitherT.lieftF(effectOf(y + 100))
    } yield z).value

    def bar(a: Either[String, Int]): F[Either[String, Int]] = (for {
      x <- a.eitherT[F] // == EitherT(pureOf(a: Either[String, Int]))
      y <- effectOf((x + 999).asRight[String]).eitherT  // == EitherT(effectOf((x + 999).asRight[String]))
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