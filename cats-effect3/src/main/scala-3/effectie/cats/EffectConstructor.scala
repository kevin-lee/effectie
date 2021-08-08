package effectie.cats

import cats.Id
import cats.effect.IO
import effectie.{CommonFx, OldEffectConstructor}

import scala.concurrent.{ExecutionContext, Future}

trait EffectConstructor[F[_]] extends FxCtor[F] with CommonFx[F] with OldEffectConstructor[F]

object EffectConstructor {
  def apply[F[_]: EffectConstructor]: EffectConstructor[F] = summon[EffectConstructor[F]]

  given ioEffectConstructor: EffectConstructor[IO] = Fx.ioFx

  given futureEffectConstructor(using EC: ExecutionContext): EffectConstructor[Future] = Fx.futureFx

  given idEffectConstructor: EffectConstructor[Id] = Fx.idFx

}
