---
sidebar_position: 4
layout: docs
title: "EitherTSupport"
---

## EitherTSupport

```scala mdoc:reset-object
import scalaz._
import Scalaz._

import effectie.scalaz.Effectful._
import effectie.scalaz._
import effectie.scalaz.EitherTSupport._

trait Something[F[_]] {
  def foo(a: Int): F[String \/ Int]
  def bar(a: String \/ Int): F[String \/ Int]
}

object Something {
  def apply[F[_]: Something]: Something[F] =
    implicitly[Something[F]]

  implicit def something[F[_]: Fx: Monad]: Something[F] =
    new SomethingF[F]

  final class SomethingF[F[_]: Fx: Monad]
    extends Something[F] {

    def foo(a: Int): F[String \/ Int] = (for {
      x <- a.rightTF[F, String] // == EitherT(Applicative[F].pure(a).map(_.right[String]))
      y <- (x + 10).rightTF[F, String] // == EitherT(Applicative[F].pure(x + 10).map(_.right[String]))
      y2 <- if (y > 100)
              eitherTLeft[Int]("Error - Bigger than 100")
            else
              eitherTRightPure[String](y)
       // ↑ if (y > 100)
       //     EitherT(pureOF("Error - Bigger than 100").map(_.left[Int]))
       //   else
       //     EitherT(pureOf(y).map(_.right[String]))
      z <- effectOf(y2 + 100).rightT[String] // == EitherT(effectOf(y + 100).map(_.right))
    } yield z).run

    def bar(a: String \/ Int): F[String \/ Int] = (for {
      x <- a.eitherT[F] // == EitherT(pureOf(a: String \/ Int))
      y <- effectOf((x + 999).right[String]).eitherT  // == EitherT(effectOf((x + 999).right[String]))
    } yield y).run
  }

}

import scalaz.effect._

Something[IO].foo(1).unsafePerformIO()
Something[IO].foo(10).unsafePerformIO()

Something[IO].bar(1.right[String]).unsafePerformIO()
Something[IO].bar("No number".left[Int]).unsafePerformIO()
```
