package effectie.scalaz

import effectie.{CommonEft, OldEffectConstructor}
import scalaz.Scalaz.Id
import scalaz.effect.IO

import scala.concurrent.{ExecutionContext, Future}

trait Eft[F[_]] extends CommonEft[F] with OldEffectConstructor[F]

object Eft {
  def apply[F[_]: Eft]: Eft[F] = implicitly[Eft[F]]

  implicit val ioEft: Eft[IO] = EffectConstructor.ioEffectConstructor

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def futureEft(implicit EC: ExecutionContext): Eft[Future] =
    EffectConstructor.futureEffectConstructor(EC)

  implicit final val idEft: Eft[Id] = EffectConstructor.idEffectConstructor

}
