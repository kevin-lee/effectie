package effectie.cats

import cats.Id
import cats.effect.IO

object FxCtor {
  type FxCtor[F[_]] = effectie.FxCtor[F]

  implicit final val ioFxCtor: FxCtor[IO] = Fx.IoFx

  implicit final val idFxCtor: FxCtor[Id] = Fx.IdFx

}
