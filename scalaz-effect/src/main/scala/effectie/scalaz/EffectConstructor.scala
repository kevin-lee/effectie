package effectie.scalaz

import effectie.{CommonFx, OldEffectConstructor}
import scalaz.Scalaz.Id
import scalaz.effect.IO

import scala.concurrent.{ExecutionContext, Future}

trait EffectConstructor[F[_]] extends FxCtor[F] with CommonFx[F] with OldEffectConstructor[F]

object EffectConstructor {
  def apply[F[_]: EffectConstructor]: EffectConstructor[F] = implicitly[EffectConstructor[F]]

  implicit final val ioEffectConstructor: EffectConstructor[IO] = Fx.IoFx

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def futureEffectConstructor(implicit EC: ExecutionContext): EffectConstructor[Future] =
    Fx.futureFx

  implicit final val idEffectConstructor: EffectConstructor[Id] = Fx.IdFx

}
