---
id: optiont-support
title: "OptionTSupport"
---

## OptionTSupport

```scala mdoc:reset-object
import cats._
import cats.syntax.all._

import effectie.cats.Effectful._
import effectie.cats._
import effectie.cats.OptionTSupport._

trait Something[F[_]] {
  def foo(a: Int): F[Option[Int]]
  def bar(a: Option[Int]): F[Option[Int]]
}

object Something {
  def apply[F[_]: Something]: Something[F] =
    implicitly[Something[F]]

  implicit def something[F[_]: Fx: Monad]: Something[F] =
    new SomethingF[F]

  final class SomethingF[F[_]: Fx: Monad]
    extends Something[F] {

    def foo(a: Int): F[Option[Int]] = (for {
      x <- a.someTF[F] // == OptionT.liftF(Applicative[F].pure(a))
      y <- (x + 10).someTF[F] // == OptionT.liftF(Applicative[F].pure(x + 10))
      z <- effectOf(y + 100).someT // == OptionT.lieftF(effectOf(y + 100))
    } yield z).value

    def bar(a: Option[Int]): F[Option[Int]] = (for {
      x <- a.optionT[F] // == OptionT(pureOf(a: Option[Int]))
      y <- effectOf((x + 999).some).optionT  // == OptionT(effectOf((x + 999).some))
    } yield y).value
  }

}

import cats.effect._

Something[IO].foo(1).unsafeRunSync()
Something[IO].foo(10).unsafeRunSync()

Something[IO].bar(1.some).unsafeRunSync()
Something[IO].bar(none[Int]).unsafeRunSync()

```