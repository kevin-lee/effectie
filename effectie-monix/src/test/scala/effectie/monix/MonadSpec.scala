package effectie.monix

import cats.Eq
import effectie.testing.cats.{Gens, Specs}
import hedgehog.Property

object MonadSpec {
  def testMonadLaws[F[_]: Fx](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .monadLaws
      .laws[F](
        Gens.genFA[F, Int](Gens.genInt(Int.MinValue, Int.MaxValue)),
        Gens.genIntFromMinToMax,
        Gens.genIntToInt,
        Gens.genAToMonadA(Gens.genIntToInt)
      )
}
