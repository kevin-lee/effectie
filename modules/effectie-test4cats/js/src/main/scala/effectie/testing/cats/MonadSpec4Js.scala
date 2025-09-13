package effectie.testing.cats

import cats.Monad
import effectie.testing.RandomGens
import effectie.testing.cats.LawsF.EqF

/** @author Kevin Lee
  * @since 2021-08-04
  */
trait MonadSpec4Js {
//  implicit def eqF[F[*]: Monad]: EqF[F, Int] =
//    (a, b) => a.flatMap(aVal => b.map(aVal === _))

  def testAllLaws[F[*]: Monad](fName: String)(implicit eqF: EqF[F, Int]): List[(String, () => F[Unit])] =
    List(
      test1_Identity(fName),
      test2_Composition(fName),
      test3_IdentityAp(fName),
      test4_Homomorphism(fName),
      test5_Interchange(fName),
      test6_CompositionAp(fName),
      test7_LeftIdentity(fName),
      test8_RightIdentity(fName),
      test9_Associativity(fName),
    )

  def test1_Identity[F[*]: Monad](fName: String)(implicit eqF: EqF[F, Int]): (String, () => F[Unit]) =
    s"test Monad laws for $fName - Identity" -> { () =>
      Specs4MUnit
        .MonadLaws
        .identity[F](() => Monad[F].pure(RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)))
    }

  def test2_Composition[F[*]: Monad](fName: String)(implicit eqF: EqF[F, Int]): (String, () => F[Unit]) =
    s"test Monad laws for $fName - Composition" -> { () =>
      Specs4MUnit
        .MonadLaws
        .composition[F](
          () => Monad[F].pure(RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)),
          () => RandomGens.genRandomIntToInt(),
        )
    }

  def test3_IdentityAp[F[*]: Monad](fName: String)(implicit eqF: EqF[F, Int]): (String, () => F[Unit]) =
    s"test Monad laws for $fName - IdentityAp" -> { () =>
      Specs4MUnit
        .MonadLaws
        .identityAp[F](() => Monad[F].pure(RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)))
    }

  def test4_Homomorphism[F[*]: Monad](fName: String)(implicit eqF: EqF[F, Int]): (String, () => F[Unit]) =
    s"test Monad laws for $fName - Homomorphism" -> { () =>
      Specs4MUnit
        .MonadLaws
        .homomorphism[F](
          () => RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue),
          () => RandomGens.genRandomIntToInt(),
        )
    }

  def test5_Interchange[F[*]: Monad](fName: String)(implicit eqF: EqF[F, Int]): (String, () => F[Unit]) =
    s"test Monad laws for $fName - Interchange" -> { () =>
      Specs4MUnit
        .MonadLaws
        .interchange[F](
          () => RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue),
          () => RandomGens.genRandomIntToInt(),
        )
    }

  def test6_CompositionAp[F[*]: Monad](fName: String)(implicit eqF: EqF[F, Int]): (String, () => F[Unit]) =
    s"test Monad laws for $fName - CompositionAp" -> { () =>
      Specs4MUnit
        .MonadLaws
        .compositionAp[F](
          () => Monad[F].pure(RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)),
          () => RandomGens.genRandomIntToInt(),
        )
    }

  def test7_LeftIdentity[F[*]: Monad](fName: String)(implicit eqF: EqF[F, Int]): (String, () => F[Unit]) =
    s"test Monad laws for $fName - LeftIdentity" -> { () =>
      Specs4MUnit
        .MonadLaws
        .leftIdentity[F](
          () => RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue),
          () => Monad[F].pure(_: Int),
        )
    }

  def test8_RightIdentity[F[*]: Monad](fName: String)(implicit eqF: EqF[F, Int]): (String, () => F[Unit]) =
    s"test Monad laws for $fName - RightIdentity" -> { () =>
      Specs4MUnit
        .MonadLaws
        .rightIdentity[F](() => Monad[F].pure(RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)))
    }

  def test9_Associativity[F[*]: Monad](fName: String)(implicit eqF: EqF[F, Int]): (String, () => F[Unit]) =
    s"test Monad laws for $fName - Associativity" -> { () =>
      Specs4MUnit
        .MonadLaws
        .associativity[F](
          () => Monad[F].pure(RandomGens.genRandomIntWithMinMax(Int.MinValue, Int.MaxValue)),
          () => Monad[F].pure(_: Int),
        )
    }
}
