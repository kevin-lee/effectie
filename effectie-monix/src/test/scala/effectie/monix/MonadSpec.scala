package effectie.monix

import cats.{Eq, Monad}
import effectie.Fx
import hedgehog.runner.Test

object MonadSpec {
  def testMonadLaws[F[_]: Fx: Monad](name: String)(implicit eqF: Eq[F[Int]]): List[Test] =
    effectie.testing.cats.MonadSpec.testAllLaws[F](s"Fx[$name]")
}
