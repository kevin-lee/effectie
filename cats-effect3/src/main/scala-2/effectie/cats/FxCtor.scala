package effectie.cats

import cats.Id
import cats.effect.IO

object FxCtor {
  type FxCtor[F[_]] = effectie.FxCtor[F]

  def apply[F[_]: FxCtor]: FxCtor[F] = implicitly[FxCtor[F]]

  implicit final val ioFxCtor: FxCtor[IO] = Fx.IoFx

  implicit final val idFxCtor: FxCtor[Id] = Fx.IdFx

}
