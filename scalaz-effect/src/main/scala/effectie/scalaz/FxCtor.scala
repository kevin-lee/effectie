package effectie.scalaz

import effectie.{CommonFx, OldEffectConstructor}
import scalaz.Scalaz.Id
import scalaz.effect.IO

import scala.concurrent.{ExecutionContext, Future}

trait FxCtor[F[_]] extends CommonFx[F] with OldEffectConstructor[F]

object FxCtor {
  def apply[F[_]: FxCtor]: FxCtor[F] = implicitly[FxCtor[F]]

  implicit val ioFxCtor: FxCtor[IO] = Fx.IoFx

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def futureFxCtor(implicit EC: ExecutionContext): FxCtor[Future] =
    Fx.futureFx

  implicit final val idFxCtor: FxCtor[Id] = Fx.IdFx

}
