package effectie.cats

import cats.Id
import cats.effect.IO
import effectie.{CommonFx, OldEffectConstructor}

import scala.concurrent.{ExecutionContext, Future}

trait FxCtor[F[_]] extends CommonFx[F] with OldEffectConstructor[F]

object FxCtor {
  def apply[F[_]: FxCtor]: FxCtor[F] = summon[FxCtor[F]]

  given ioFxCtor: FxCtor[IO] = Fx.ioFx

  given futureFxCtor(using EC: ExecutionContext): FxCtor[Future] =
    Fx.futureFx

  given idFxCtor: FxCtor[Id] = Fx.idFx

}
