package effectie.testing.cats

import cats.{Eq, Monad}
import hedgehog._
import hedgehog.runner._

/** @author Kevin Lee
  * @since 2021-08-04
  */
object MonadSpec {
  def testAllLaws[F[*]: Monad](fName: String)(implicit eqF: Eq[F[Int]]): List[Test] =
    List(
      property(s"test Monad laws for $fName - Identity", test1_Identity),
      property(s"test Monad laws for $fName - Composition", test2_Composition),
      property(s"test Monad laws for $fName - IdentityAp", test3_IdentityAp),
      property(s"test Monad laws for $fName - Homomorphism", test4_Homomorphism),
      property(s"test Monad laws for $fName - Interchange", test5_Interchange),
      property(s"test Monad laws for $fName - CompositionAp", test6_CompositionAp),
      property(s"test Monad laws for $fName - LeftIdentity", test7_LeftIdentity),
      property(s"test Monad laws for $fName - RightIdentity", test8_RightIdentity),
      property(s"test Monad laws for $fName - Associativity", test9_Associativity),
    )

  def test1_Identity[F[*]: Monad](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .MonadLaws
      .identity[F](
        Gens.genFA[F, Int](Gens.genInt(Int.MinValue, Int.MaxValue)),
      )

  def test2_Composition[F[*]: Monad](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .MonadLaws
      .composition[F](
        Gens.genFA[F, Int](Gens.genInt(Int.MinValue, Int.MaxValue)),
        Gens.genIntToInt,
      )

  def test3_IdentityAp[F[*]: Monad](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .MonadLaws
      .identityAp[F](
        Gens.genFA[F, Int](Gens.genInt(Int.MinValue, Int.MaxValue)),
      )

  def test4_Homomorphism[F[*]: Monad](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .MonadLaws
      .homomorphism[F](
        Gens.genIntFromMinToMax,
        Gens.genIntToInt,
      )

  def test5_Interchange[F[*]: Monad](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .MonadLaws
      .interchange[F](
        Gens.genIntFromMinToMax,
        Gens.genIntToInt,
      )

  def test6_CompositionAp[F[*]: Monad](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .MonadLaws
      .compositionAp[F](
        Gens.genFA[F, Int](Gens.genInt(Int.MinValue, Int.MaxValue)),
        Gens.genIntToInt,
      )

  def test7_LeftIdentity[F[*]: Monad](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .MonadLaws
      .leftIdentity[F](
        Gens.genIntFromMinToMax,
        Gens.genAToMonadA(Gens.genIntToInt)
      )

  def test8_RightIdentity[F[*]: Monad](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .MonadLaws
      .rightIdentity[F](
        Gens.genFA[F, Int](Gens.genInt(Int.MinValue, Int.MaxValue)),
      )

  def test9_Associativity[F[*]: Monad](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .MonadLaws
      .associativity[F](
        Gens.genFA[F, Int](Gens.genInt(Int.MinValue, Int.MaxValue)),
        Gens.genAToMonadA(Gens.genIntToInt)
      )

}
