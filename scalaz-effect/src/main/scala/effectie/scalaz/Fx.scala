package effectie.scalaz

import effectie.{CommonFx, OldEffectConstructor}
import scalaz.Scalaz.Id
import scalaz.effect.IO

import scala.concurrent.{ExecutionContext, Future}

trait Fx[F[_]] extends CommonFx[F] with OldEffectConstructor[F]

object Fx {
  def apply[F[_]: Fx]: Fx[F] = implicitly[Fx[F]]

  implicit val ioFx: Fx[IO] = EffectConstructor.ioEffectConstructor

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def futureFx(implicit EC: ExecutionContext): Fx[Future] =
    EffectConstructor.futureEffectConstructor(EC)

  implicit final val idFx: Fx[Id] = EffectConstructor.idEffectConstructor

}
