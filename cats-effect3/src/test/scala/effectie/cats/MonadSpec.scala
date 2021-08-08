package effectie.cats

import cats.Eq
import effectie.testing.cats.Gens
import hedgehog.Property

object MonadSpec {
  def test1_Identity[F[_]: Fx](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .monadLaws
      .identity[F](
        Gens.genFA[F, Int](Gens.genInt(Int.MinValue, Int.MaxValue)),
      )

  def test2_Composition[F[_]: Fx](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .monadLaws
      .composition[F](
        Gens.genFA[F, Int](Gens.genInt(Int.MinValue, Int.MaxValue)),
        Gens.genIntToInt,
      )

  def test3_IdentityAp[F[_]: Fx](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .monadLaws
      .identityAp[F](
        Gens.genFA[F, Int](Gens.genInt(Int.MinValue, Int.MaxValue)),
      )

  def test4_Homomorphism[F[_]: Fx](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .monadLaws
      .homomorphism[F](
        Gens.genIntFromMinToMax,
        Gens.genIntToInt,
      )

  def test5_Interchange[F[_]: Fx](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .monadLaws
      .interchange[F](
        Gens.genIntFromMinToMax,
        Gens.genIntToInt,
      )

  def test6_CompositionAp[F[_]: Fx](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .monadLaws
      .compositionAp[F](
        Gens.genFA[F, Int](Gens.genInt(Int.MinValue, Int.MaxValue)),
        Gens.genIntToInt,
      )

  def test7_LeftIdentity[F[_]: Fx](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .monadLaws
      .leftIdentity[F](
        Gens.genIntFromMinToMax,
        Gens.genAToMonadA(Gens.genIntToInt)
      )

  def test8_RightIdentity[F[_]: Fx](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .monadLaws
      .rightIdentity[F](
        Gens.genFA[F, Int](Gens.genInt(Int.MinValue, Int.MaxValue)),
      )

  def test9_Associativity[F[_]: Fx](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .monadLaws
      .associativity[F](
        Gens.genFA[F, Int](Gens.genInt(Int.MinValue, Int.MaxValue)),
        Gens.genAToMonadA(Gens.genIntToInt)
      )

}
