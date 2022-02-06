package effectie.testing.cats

import cats._
import hedgehog._

object Specs {

  object FunctorLaws {
    def identity[F[_]](
      genM: Gen[F[Int]]
    )(implicit functor: Functor[F], eqM: Eq[F[Int]]): Property = for {
      m <- genM.log("m: F[Int]")
    } yield {
      (Laws.FunctorLaws.identity[F, Int](m) ==== true)
        .log("functorLaw.identity")
    }

    def composition[F[_]](
      genM: Gen[F[Int]],
      genF: Gen[Int => Int]
    )(implicit functor: Functor[F], eqM: Eq[F[Int]]): Property = for {
      m  <- genM.log("m: F[Int]")
      f  <- genF.log("f: Int => Int")
      f2 <- genF.log("f2: Int => Int")
    } yield {
      (Laws.FunctorLaws.composition[F, Int, Int, Int](m, f, f2) ==== true)
        .log("functorLaw.composition")
    }
  }

  object ApplicativeLaws {
    def identity[F[_]](
      genM: Gen[F[Int]]
    )(implicit applicative: Applicative[F], eqM: Eq[F[Int]]): Property = for {
      m <- genM.log("m: F[Int]")
    } yield {
      (Laws.ApplicativeLaws.identity[F, Int](m) ==== true)
        .log("functorLaw.identity")
    }
    def composition[F[_]](
      genM: Gen[F[Int]],
      genF: Gen[Int => Int]
    )(implicit applicative: Applicative[F], eqM: Eq[F[Int]]): Property = for {
      m  <- genM.log("m: F[Int]")
      f  <- genF.log("f: Int => Int")
      f2 <- genF.log("f2: Int => Int")
    } yield {
      (Laws.ApplicativeLaws.composition[F, Int, Int, Int](m, f, f2) ==== true)
        .log("functorLaw.composition")
    }
    def identityAp[F[_]](
      genM: Gen[F[Int]]
    )(implicit applicative: Applicative[F], eqM: Eq[F[Int]]): Property = for {
      m <- genM.log("m: F[Int]")
    } yield {
      (Laws.ApplicativeLaws.identityAp[F, Int](m) ==== true)
        .log("applicativeLaw.identityAp")
    }
    def homomorphism[F[_]](
      genInt: Gen[Int],
      genF: Gen[Int => Int]
    )(implicit applicative: Applicative[F], eqM: Eq[F[Int]]): Property = for {
      x <- genInt.log("x: Int")
      f <- genF.log("f: Int => Int")
    } yield {
      (Laws.ApplicativeLaws.homomorphism[F, Int, Int](f, x) ==== true)
        .log("applicativeLaw.homomorphism")
    }
    def interchange[F[_]](
      genInt: Gen[Int],
      genF: Gen[Int => Int]
    )(implicit applicative: Applicative[F], eqM: Eq[F[Int]]): Property = for {
      x <- genInt.log("x: Int")
      f <- genF.log("f: Int => Int")
    } yield {
      (Laws.ApplicativeLaws.interchange[F, Int, Int](x, applicative.pure(f)) ==== true)
        .log("applicativeLaw.interchange")
    }
    def compositionAp[F[_]](
      genM: Gen[F[Int]],
      genF: Gen[Int => Int]
    )(implicit applicative: Applicative[F], eqM: Eq[F[Int]]): Property = for {
      m  <- genM.log("m: F[Int]")
      f  <- genF.log("f: Int => Int")
      f2 <- genF.log("f2: Int => Int")
    } yield {
      (Laws.ApplicativeLaws.compositionAp[F, Int, Int, Int](m, applicative.pure(f), applicative.pure(f2)) ==== true)
        .log("applicativeLaw.compositionAp")
    }
  }

  object MonadLaws {
    def identity[F[_]](
      genM: Gen[F[Int]],
    )(implicit monad: Monad[F], eqM: Eq[F[Int]]): Property = for {
      m <- genM.log("m: F[Int]")
    } yield {
      (Laws.MonadLaws.identity[F, Int](m) ==== true)
        .log("functorLaw.identity")
    }

    def composition[F[_]](
      genM: Gen[F[Int]],
      genF: Gen[Int => Int],
    )(implicit monad: Monad[F], eqM: Eq[F[Int]]): Property = for {
      m  <- genM.log("m: F[Int]")
      f  <- genF.log("f: Int => Int")
      f2 <- genF.log("f2: Int => Int")
    } yield {
      (Laws.MonadLaws.composition[F, Int, Int, Int](m, f, f2) ==== true)
        .log("functorLaw.composition")
    }

    def identityAp[F[_]](
      genM: Gen[F[Int]],
    )(implicit monad: Monad[F], eqM: Eq[F[Int]]): Property = for {
      m <- genM.log("m: F[Int]")
    } yield {
      (Laws.MonadLaws.identityAp[F, Int](m) ==== true)
        .log("applicativeLaw.identityAp")
    }

    def homomorphism[F[_]](
      genInt: Gen[Int],
      genF: Gen[Int => Int],
    )(implicit monad: Monad[F], eqM: Eq[F[Int]]): Property = for {
      x <- genInt.log("x: Int")
      f <- genF.log("f: Int => Int")
    } yield {
      (Laws.MonadLaws.homomorphism[F, Int, Int](f, x) ==== true)
        .log("applicativeLaw.homomorphism")
    }

    def interchange[F[_]](
      genInt: Gen[Int],
      genF: Gen[Int => Int],
    )(implicit monad: Monad[F], eqM: Eq[F[Int]]): Property = for {
      x <- genInt.log("x: Int")
      f <- genF.log("f: Int => Int")
    } yield {
      (Laws.MonadLaws.interchange[F, Int, Int](x, monad.pure(f)) ==== true)
        .log("applicativeLaw.interchange")
    }

    def compositionAp[F[_]](
      genM: Gen[F[Int]],
      genF: Gen[Int => Int],
    )(implicit monad: Monad[F], eqM: Eq[F[Int]]): Property = for {
      m  <- genM.log("m: F[Int]")
      f  <- genF.log("f: Int => Int")
      f2 <- genF.log("f2: Int => Int")
    } yield {
      (Laws.MonadLaws.compositionAp[F, Int, Int, Int](m, monad.pure(f), monad.pure(f2)) ==== true)
        .log("applicativeLaw.compositionAp")
    }

    def leftIdentity[F[_]](
      genInt: Gen[Int],
      genFm: Gen[Int => F[Int]]
    )(implicit monad: Monad[F], eqM: Eq[F[Int]]): Property = for {
      x  <- genInt.log("x: Int")
      fm <- genFm.log("fm: Int => F[Int]")
    } yield {
      (Laws.MonadLaws.leftIdentity[F, Int, Int](x, fm) ==== true)
        .log("monadLaw.leftIdentity")
    }

    def rightIdentity[F[_]](
      genM: Gen[F[Int]],
    )(implicit monad: Monad[F], eqM: Eq[F[Int]]): Property = for {
      m <- genM.log("m: F[Int]")
    } yield {
      (Laws.MonadLaws.rightIdentity[F, Int](m) ==== true)
        .log("monadLaw.rightIdentity")
    }

    def associativity[F[_]](
      genM: Gen[F[Int]],
      genFm: Gen[Int => F[Int]]
    )(implicit monad: Monad[F], eqM: Eq[F[Int]]): Property = for {
      m   <- genM.log("m: F[Int]")
      fm  <- genFm.log("fm: Int => F[Int]")
      fm2 <- genFm.log("fm2: Int => F[Int]")
    } yield {
      (Laws.MonadLaws.associativity[F, Int, Int, Int](m, fm, fm2) ==== true)
        .log("monadLaw.associativity")
    }

  }

}
