package effectie.scalaz

import effectie.testing.{Gens, Specs}
import hedgehog.Property
import scalaz.Equal

object MonadSpec {
  def testMonadLaws[F[_]: Fx](implicit eqF: Equal[F[Int]]): Property =
    Specs
      .monadLaws
      .laws[F](
        Gens.genFA[F, Int](Gens.genInt(Int.MinValue, Int.MaxValue)),
        Gens.genIntFromMinToMax,
        Gens.genIntToInt,
        Gens.genAToMonadA(Gens.genIntToInt)
      )
}
