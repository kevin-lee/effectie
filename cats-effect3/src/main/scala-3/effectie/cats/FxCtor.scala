package effectie.cats

import cats.Id
import cats.effect.IO

import scala.concurrent.{ExecutionContext, Future}

object FxCtor {
  type FxCtor[F[*]] = effectie.FxCtor[F]

  given ioFxCtor: FxCtor[IO] = Fx.ioFx

  given idFxCtor: FxCtor[Id] = Fx.idFx

}
