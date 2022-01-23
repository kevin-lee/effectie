package effectie.monix

import cats.{Eq, Monad}
import hedgehog.runner.Test

object MonadSpec {
  def testMonadLaws[F[_]: effectie.core.Fx: Monad](name: String)(implicit eqF: Eq[F[Int]]): List[Test] =
    effectie.testing.cats.MonadSpec.testAllLaws[F](s"Fx[$name]")
}
