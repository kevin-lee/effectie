---
layout: docs
title: "EitherTSupport - Scalaz"
---

## EitherTSupport

```scala mdoc:reset-object
import scalaz._
import Scalaz._

import effectie.Effectful._
import effectie.scalaz._
import effectie.scalaz.EitherTSupport._

trait Something[F[_]] {
  def foo(a: Int): F[String \/ Int]
  def bar(a: String \/ Int): F[String \/ Int]
}

object Something {
  def apply[F[_]: Something]: Something[F] =
    implicitly[Something[F]]

  implicit def something[F[_]: EffectConstructor: Monad]: Something[F] =
    new SomethingF[F]

  final class SomethingF[F[_]: EffectConstructor: Monad]
    extends Something[F] {

    def foo(a: Int): F[String \/ Int] = (for {
      x <- eitherTRightPure(a) // == EitherT(effectOfPure(a).map(_.right[String]))
      y <- eitherTRight(x + 10) // == EitherT(effectOf(x + 10).map(_.right[String]))
      y2 <- if (y > 100)
          eitherTLeft[F, Int]("Error - Bigger than 100")
        else
          eitherTRightPure[F, String](y)
        // ↑ if (y > 100)
        //     EitherT(effectOf("Error - Bigger than 100").map(_.left[Int]))
        //   else
        //     EitherT(effectOfPure(y).map(_.right[String]))
      z <- eitherTRightF[String](effectOf(y + 100)) // == EitherT(effectOf(y + 100).map(_.right))
    } yield z).run

    def bar(a: String \/ Int): F[String \/ Int] = (for {
      x <- eitherTOfPure(a) // == EitherT(effectOfPure(a: String \/ Int))
      y <- eitherTOf((x + 999).right[String])  // == EitherT(effectOf((x + 999).right[String]))
    } yield y).run
  }

}

import scalaz.effect._

Something[IO].foo(1).unsafePerformIO()
Something[IO].foo(10).unsafePerformIO()

Something[IO].bar(1.right[String]).unsafePerformIO()
Something[IO].bar("No number".left[Int]).unsafePerformIO()
```
