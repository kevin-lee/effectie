package effectie.specs

import cats.Monad
import effectie.testing.cats.LawsF.EqF

object MonadSpec4Js extends effectie.testing.cats.MonadSpec4Js {
  type Fx[F[*]] = effectie.core.Fx[F]

  def testMonadLaws[F[*]: Monad](name: String)(implicit eqF: EqF[F, Int]): List[(String, () => F[Unit])] =
    testAllLaws[F](s"Fx[$name]")
}
