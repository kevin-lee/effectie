package effectie.cats

import cats.Eq
import hedgehog.runner.Test

object MonadSpec {
  def testMonadLaws[F[_]: Fx](implicit eqF: Eq[F[Int]]): List[Test] =
    effectie.testing.cats.MonadSpec.testAllLaws[F]
}
