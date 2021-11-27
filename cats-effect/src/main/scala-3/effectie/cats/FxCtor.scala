package effectie.cats

import cats.Id
import cats.effect.IO

import scala.concurrent.{ExecutionContext, Future}

object FxCtor {
  type FxCtor[F[*]] = effectie.FxCtor[F]

  given ioFxCtor: FxCtor[IO] = Fx.ioFx

  given futureFxCtor(using EC: ExecutionContext): FxCtor[Future] =
    effectie.Fx.fxFuture

  given idFxCtor: FxCtor[Id] = Fx.idFx

}
