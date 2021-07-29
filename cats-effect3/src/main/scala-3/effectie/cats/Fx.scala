package effectie.cats

import cats.Id
import cats.effect.IO
import effectie.{CommonFx, OldEffectConstructor}

import scala.concurrent.{ExecutionContext, Future}

trait Fx[F[_]] extends CommonFx[F] with OldEffectConstructor[F]

object Fx {
  def apply[F[_]: Fx]: Fx[F] = summon[Fx[F]]

  given ioFx: Fx[IO] = EffectConstructor.ioEffectConstructor

  given futureFx(using EC: ExecutionContext): Fx[Future] = EffectConstructor.futureEffectConstructor

  given idFx: Fx[Id] = EffectConstructor.idEffectConstructor

}
