package effectie.cats

import cats.Id
import cats.effect.IO

//import scala.concurrent.{ExecutionContext, Future}

//trait FxCtor[F[_]] extends effectie.FxCtor[F]

object FxCtor {
  type FxCtor[F[_]] = effectie.FxCtor[F]

  def apply[F[_]: FxCtor]: FxCtor[F] = implicitly[FxCtor[F]]

  implicit final val ioFxCtor: FxCtor[IO] = Fx.IoFx

//  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
//  implicit def futureFxCtor(implicit EC: ExecutionContext): FxCtor[Future] =
//    Fx.futureFx

  implicit final val idFxCtor: FxCtor[Id] = Fx.IdFx

}
