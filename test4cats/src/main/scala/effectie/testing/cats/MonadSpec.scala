package effectie.testing.cats

import cats.{Eq, Monad}
import hedgehog._

/** @author Kevin Lee
  * @since 2021-08-04
  */
object MonadSpec {
  def testLaws[F[_]: Monad](implicit eqF: Eq[F[Int]]): Property =
    Specs
      .monadLaws
      .laws[F](
        Gens.genFA[F, Int](Gens.genInt(Int.MinValue, Int.MaxValue)),
        Gens.genIntFromMinToMax,
        Gens.genIntToInt,
        Gens.genAToMonadA(Gens.genIntToInt)
      )
}
