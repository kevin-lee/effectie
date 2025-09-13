package effectie.testing.cats

import cats._
import cats.syntax.all._
import effectie.testing.cats.LawsF.EqF
import munit.Assertions

object Specs4MUnit {

  object FunctorLaws {
    def identity[F[*]](
      genM: () => F[Int]
    )(implicit functor: Functor[F], eqM: EqF[F, Int]): F[Unit] = {
      val m = genM()

      LawsF.FunctorLaws.identity[F, Int](m).map(Assertions.assert(_, "functorLaw.identity"))
    }

    def composition[F[*]](
      genM: () => F[Int],
      genF: () => Int => Int,
    )(implicit functor: Functor[F], eqM: EqF[F, Int]): F[Unit] = {
      val m  = genM()
      val f  = genF()
      val f2 = genF()

      LawsF.FunctorLaws.composition[F, Int, Int, Int](m, f, f2).map(Assertions.assert(_, "functorLaw.composition"))
    }
  }

  object ApplicativeLaws {
    def identity[F[*]](
      genM: () => F[Int]
    )(implicit applicative: Applicative[F], eqM: EqF[F, Int]): F[Unit] = {
      val m = genM()

      LawsF.ApplicativeLaws.identity[F, Int](m).map(Assertions.assert(_, "functorLaw.identity"))
    }
    def composition[F[*]](
      genM: () => F[Int],
      genF: () => Int => Int,
    )(implicit applicative: Applicative[F], eqM: EqF[F, Int]): F[Unit] = {
      val m  = genM()
      val f  = genF()
      val f2 = genF()

      LawsF.ApplicativeLaws.composition[F, Int, Int, Int](m, f, f2).map(Assertions.assert(_, "functorLaw.composition"))
    }
    def identityAp[F[*]](
      genM: () => F[Int]
    )(implicit applicative: Applicative[F], eqM: EqF[F, Int]): F[Unit] = {
      val m = genM()

      LawsF.ApplicativeLaws.identityAp[F, Int](m).map(Assertions.assert(_, "applicativeLaw.identityAp"))
    }
    def homomorphism[F[*]](
      genInt: () => Int,
      genF: () => Int => Int,
    )(implicit applicative: Applicative[F], eqM: EqF[F, Int]): F[Unit] = {
      val x = genInt()
      val f = genF()

      LawsF.ApplicativeLaws.homomorphism[F, Int, Int](f, x).map(Assertions.assert(_, "applicativeLaw.homomorphism"))
    }
    def interchange[F[*]](
      genInt: () => Int,
      genF: () => Int => Int,
    )(implicit applicative: Applicative[F], eqM: EqF[F, Int]): F[Unit] = {
      val x = genInt()
      val f = genF()

      LawsF
        .ApplicativeLaws
        .interchange[F, Int, Int](x, applicative.pure(f))
        .map(Assertions.assert(_, "applicativeLaw.interchange"))
    }
    def compositionAp[F[*]](
      genM: () => F[Int],
      genF: () => Int => Int,
    )(implicit applicative: Applicative[F], eqM: EqF[F, Int]): F[Unit] = {
      val m  = genM()
      val f  = genF()
      val f2 = genF()

      LawsF
        .ApplicativeLaws
        .compositionAp[F, Int, Int, Int](m, applicative.pure(f), applicative.pure(f2))
        .map(Assertions.assert(_, "applicativeLaw.compositionAp"))
    }
  }

  object MonadLaws {
    def identity[F[*]](
      genM: () => F[Int]
    )(implicit monad: Monad[F], eqM: EqF[F, Int]): F[Unit] = {
      val m = genM()

      LawsF.MonadLaws.identity[F, Int](m).map(Assertions.assert(_, "functorLaw.identity"))
    }

    def composition[F[*]](
      genM: () => F[Int],
      genF: () => Int => Int,
    )(implicit monad: Monad[F], eqM: EqF[F, Int]): F[Unit] = {
      val m  = genM()
      val f  = genF()
      val f2 = genF()

      LawsF.MonadLaws.composition[F, Int, Int, Int](m, f, f2).map(Assertions.assert(_, "functorLaw.composition"))
    }

    def identityAp[F[*]](
      genM: () => F[Int]
    )(implicit monad: Monad[F], eqM: EqF[F, Int]): F[Unit] = {
      val m = genM()

      LawsF.MonadLaws.identityAp[F, Int](m).map(Assertions.assert(_, "applicativeLaw.identityAp"))
    }

    def homomorphism[F[*]](
      genInt: () => Int,
      genF: () => Int => Int,
    )(implicit monad: Monad[F], eqM: EqF[F, Int]): F[Unit] = {
      val x = genInt()
      val f = genF()

      LawsF.MonadLaws.homomorphism[F, Int, Int](f, x).map(Assertions.assert(_, "applicativeLaw.homomorphism"))
    }

    def interchange[F[*]](
      genInt: () => Int,
      genF: () => Int => Int,
    )(implicit monad: Monad[F], eqM: EqF[F, Int]): F[Unit] = {
      val x = genInt()
      val f = genF()

      LawsF.MonadLaws.interchange[F, Int, Int](x, monad.pure(f)).map(Assertions.assert(_, "applicativeLaw.interchange"))
    }

    def compositionAp[F[*]](
      genM: () => F[Int],
      genF: () => Int => Int,
    )(implicit monad: Monad[F], eqM: EqF[F, Int]): F[Unit] = {
      val m  = genM()
      val f  = genF()
      val f2 = genF()

      LawsF
        .MonadLaws
        .compositionAp[F, Int, Int, Int](m, monad.pure(f), monad.pure(f2))
        .map(Assertions.assert(_, "applicativeLaw.compositionAp"))
    }

    def leftIdentity[F[*]](
      genInt: () => Int,
      genFm: () => Int => F[Int],
    )(implicit monad: Monad[F], eqM: EqF[F, Int]): F[Unit] = {
      val x  = genInt()
      val fm = genFm()

      LawsF.MonadLaws.leftIdentity[F, Int, Int](x, fm).map(Assertions.assert(_, "monadLaw.leftIdentity"))
    }

    def rightIdentity[F[*]](
      genM: () => F[Int]
    )(implicit monad: Monad[F], eqM: EqF[F, Int]): F[Unit] = {
      val m = genM()

      LawsF.MonadLaws.rightIdentity[F, Int](m).map(Assertions.assert(_, "monadLaw.rightIdentity"))
    }

    def associativity[F[*]](
      genM: () => F[Int],
      genFm: () => Int => F[Int],
    )(implicit monad: Monad[F], eqM: EqF[F, Int]): F[Unit] = {
      val m   = genM()
      val fm  = genFm()
      val fm2 = genFm()

      LawsF.MonadLaws.associativity[F, Int, Int, Int](m, fm, fm2).map(Assertions.assert(_, "monadLaw.associativity"))
    }

  }

}
