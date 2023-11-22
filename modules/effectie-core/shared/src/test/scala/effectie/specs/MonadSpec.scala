package effectie.specs

import cats.{Eq, Monad}
import hedgehog.runner.Test

object MonadSpec {
  type Fx[F[*]] = effectie.core.Fx[F]

  def testMonadLaws[F[*]: Monad](name: String)(implicit eqF: Eq[F[Int]]): List[Test] =
    effectie.testing.cats.MonadSpec.testAllLaws[F](s"Fx[$name]")
}
