package effectie.instances.monix3

import cats.{Eq, Monad}
import hedgehog.runner.Test

object MonadSpec {
  def testMonadLaws[F[*]: Monad](name: String)(implicit eqF: Eq[F[Int]]): List[Test] =
    effectie.testing.cats.MonadSpec.testAllLaws[F](s"Fx[$name]")
}
