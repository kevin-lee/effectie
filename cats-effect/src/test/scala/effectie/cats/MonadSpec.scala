package effectie.cats

import cats.{Eq, Monad}
import hedgehog.runner.Test

import effectie.Fx

object MonadSpec {
  def testMonadLaws[F[_]: Fx: Monad](name: String)(implicit eqF: Eq[F[Int]]): List[Test] =
    effectie.testing.cats.MonadSpec.testAllLaws[F](s"Fx[$name]")
}
