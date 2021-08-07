package effectie.testing.cats

import cats._
import hedgehog._

object Specs {

  object functorLaws {
    def laws[F[_]](
        genM: Gen[F[Int]]
      , genF: Gen[Int => Int]
      )(implicit functor: Functor[F]
      , eqM: Eq[F[Int]]
      ): Property = for {
      m <- genM.log("m: F[Int]")
      f <- genF.log("f: Int => Int")
      f2 <- genF.log("f2: Int => Int")
    } yield {
      Result.all(List(
        (Laws.FunctorLaws.identity[F, Int](m) ==== true)
          .log("functorLaw.identity")
      , (Laws.FunctorLaws.composition[F, Int, Int, Int](m, f, f2) ==== true)
          .log("functorLaw.composition")
      ))
    }
  }

  object applicativeLaws {
    def laws[F[_]](
        genM: Gen[F[Int]]
      , genInt: Gen[Int]
      , genF: Gen[Int => Int]
    )(implicit applicative: Applicative[F]
    , eqM: Eq[F[Int]]
    ): Property = for {
      m <- genM.log("m: F[Int]")
      x <- genInt.log("x: Int")
      f <- genF.log("f: Int => Int")
      f2 <- genF.log("f2: Int => Int")
    } yield {
      Result.all(List(
        (Laws.ApplicativeLaws.identity[F, Int](m) ==== true)
          .log("functorLaw.identity")
        , (Laws.ApplicativeLaws.composition[F, Int, Int, Int](m, f, f2) ==== true)
          .log("functorLaw.composition")
        , (Laws.ApplicativeLaws.identityAp[F, Int](m) ==== true)
          .log("applicativeLaw.identityAp")
        , (Laws.ApplicativeLaws.homomorphism[F, Int, Int](f, x) ==== true)
          .log("applicativeLaw.homomorphism")
        , (Laws.ApplicativeLaws.interchange[F, Int, Int](x, applicative.pure(f)) ==== true)
          .log("applicativeLaw.interchange")
        , (Laws.ApplicativeLaws.compositionAp[F, Int, Int, Int](m, applicative.pure(f), applicative.pure(f2)) ==== true)
          .log("applicativeLaw.compositionAp")
      ))
    }
  }

  object monadLaws {
    def laws[F[_]](
        genM: Gen[F[Int]]
      , genInt: Gen[Int]
      , genF: Gen[Int => Int]
      , genFm: Gen[Int => F[Int]]
      )(implicit monad: Monad[F]
      , eqM: Eq[F[Int]]
      ): Property = for {
      m <- genM.log("m: F[Int]")
      x <- genInt.log("x: Int")
      f <- genF.log("f: Int => Int")
      f2 <- genF.log("f2: Int => Int")
      fm <- genFm.log("fm: Int => F[Int]")
      fm2 <- genFm.log("fm2: Int => F[Int]")
    } yield {
      Result.all(List(
        (Laws.MonadLaws.identity[F, Int](m) ==== true)
          .log("functorLaw.identity")
      , (Laws.MonadLaws.composition[F, Int, Int, Int](m, f, f2) ==== true)
          .log("functorLaw.composition")
      , (Laws.MonadLaws.identityAp[F, Int](m) ==== true)
          .log("applicativeLaw.identityAp")
      , (Laws.MonadLaws.homomorphism[F, Int, Int](f, x) ==== true)
          .log("applicativeLaw.homomorphism")
      , (Laws.MonadLaws.interchange[F, Int, Int](x, monad.pure(f)) ==== true)
          .log("applicativeLaw.interchange")
      , (Laws.MonadLaws.compositionAp[F, Int, Int, Int](m, monad.pure(f), monad.pure(f2)) ==== true)
          .log("applicativeLaw.compositionAp")
      , (Laws.MonadLaws.leftIdentity[F, Int, Int](x, fm) ==== true)
          .log("monadLaw.leftIdentity")
      , (Laws.MonadLaws.rightIdentity[F, Int](m) ==== true)
          .log("monadLaw.rightIdentity")
      , (Laws.MonadLaws.associativity[F, Int, Int, Int](m, fm, fm2) ==== true)
          .log("monadLaw.associativity")
      ))
    }
  }

}
