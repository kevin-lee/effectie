package effectie.testing.cats

import cats.{Eq, Monad}
import hedgehog._
import hedgehog.runner._

/** @author Kevin Lee
  * @since 2021-08-04
  */
object MonadSpec {
  def testAllLaws[F[_]: Monad](implicit eqF: Eq[F[Int]]): List[Test] =
    List(
      property("test Monad laws - Identity", test1_Identity),
      property("test Monad laws - Composition", test2_Composition),
      property("test Monad laws - IdentityAp", test3_IdentityAp),
      property("test Monad laws - Homomorphism", test4_Homomorphism),
      property("test Monad laws - Interchange", test5_Interchange),
      property("test Monad laws - CompositionAp", test6_CompositionAp),
      property("test Monad laws - LeftIdentity", test7_LeftIdentity),
      property("test Monad laws - RightIdentity", test8_RightIdentity),
      property("test Monad laws - Associativity", test9_Associativity),
    )

  def test1_Identity[F[_]: Monad](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .MonadLaws
      .identity[F](
        Gens.genFA[F, Int](Gens.genInt(Int.MinValue, Int.MaxValue)),
      )

  def test2_Composition[F[_]: Monad](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .MonadLaws
      .composition[F](
        Gens.genFA[F, Int](Gens.genInt(Int.MinValue, Int.MaxValue)),
        Gens.genIntToInt,
      )

  def test3_IdentityAp[F[_]: Monad](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .MonadLaws
      .identityAp[F](
        Gens.genFA[F, Int](Gens.genInt(Int.MinValue, Int.MaxValue)),
      )

  def test4_Homomorphism[F[_]: Monad](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .MonadLaws
      .homomorphism[F](
        Gens.genIntFromMinToMax,
        Gens.genIntToInt,
      )

  def test5_Interchange[F[_]: Monad](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .MonadLaws
      .interchange[F](
        Gens.genIntFromMinToMax,
        Gens.genIntToInt,
      )

  def test6_CompositionAp[F[_]: Monad](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .MonadLaws
      .compositionAp[F](
        Gens.genFA[F, Int](Gens.genInt(Int.MinValue, Int.MaxValue)),
        Gens.genIntToInt,
      )

  def test7_LeftIdentity[F[_]: Monad](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .MonadLaws
      .leftIdentity[F](
        Gens.genIntFromMinToMax,
        Gens.genAToMonadA(Gens.genIntToInt)
      )

  def test8_RightIdentity[F[_]: Monad](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .MonadLaws
      .rightIdentity[F](
        Gens.genFA[F, Int](Gens.genInt(Int.MinValue, Int.MaxValue)),
      )

  def test9_Associativity[F[_]: Monad](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .MonadLaws
      .associativity[F](
        Gens.genFA[F, Int](Gens.genInt(Int.MinValue, Int.MaxValue)),
        Gens.genAToMonadA(Gens.genIntToInt)
      )

}
