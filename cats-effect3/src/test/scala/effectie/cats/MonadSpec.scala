package effectie.cats

import cats.{Eq, Monad}
import effectie.testing.cats.{Gens, Specs}
import hedgehog.Property

object MonadSpec {
  def test1_Identity[F[_]: Fx: Monad](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .MonadLaws
      .identity[F](
        Gens.genFA[F, Int](Gens.genInt(Int.MinValue, Int.MaxValue)),
      )

  def test2_Composition[F[_]: Fx: Monad](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .MonadLaws
      .composition[F](
        Gens.genFA[F, Int](Gens.genInt(Int.MinValue, Int.MaxValue)),
        Gens.genIntToInt,
      )

  def test3_IdentityAp[F[_]: Fx: Monad](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .MonadLaws
      .identityAp[F](
        Gens.genFA[F, Int](Gens.genInt(Int.MinValue, Int.MaxValue)),
      )

  def test4_Homomorphism[F[_]: Fx: Monad](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .MonadLaws
      .homomorphism[F](
        Gens.genIntFromMinToMax,
        Gens.genIntToInt,
      )

  def test5_Interchange[F[_]: Fx: Monad](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .MonadLaws
      .interchange[F](
        Gens.genIntFromMinToMax,
        Gens.genIntToInt,
      )

  def test6_CompositionAp[F[_]: Fx: Monad](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .MonadLaws
      .compositionAp[F](
        Gens.genFA[F, Int](Gens.genInt(Int.MinValue, Int.MaxValue)),
        Gens.genIntToInt,
      )

  def test7_LeftIdentity[F[_]: Fx: Monad](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .MonadLaws
      .leftIdentity[F](
        Gens.genIntFromMinToMax,
        Gens.genAToMonadA(Gens.genIntToInt)
      )

  def test8_RightIdentity[F[_]: Fx: Monad](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .MonadLaws
      .rightIdentity[F](
        Gens.genFA[F, Int](Gens.genInt(Int.MinValue, Int.MaxValue)),
      )

  def test9_Associativity[F[_]: Fx: Monad](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .MonadLaws
      .associativity[F](
        Gens.genFA[F, Int](Gens.genInt(Int.MinValue, Int.MaxValue)),
        Gens.genAToMonadA(Gens.genIntToInt)
      )

}