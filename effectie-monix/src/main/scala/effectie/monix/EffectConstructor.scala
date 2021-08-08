package effectie.monix

import cats.Id
import cats.effect.IO
import monix.eval.Task

import scala.concurrent.{ExecutionContext, Future}

trait EffectConstructor[F[_]] extends FxCtor[F] with effectie.CommonFx[F]

object EffectConstructor {
  def apply[F[_]: EffectConstructor]: EffectConstructor[F] = implicitly[EffectConstructor[F]]

  implicit final val taskEffectConstructor: EffectConstructor[Task] = Fx.TaskFx

  implicit final val ioEffectConstructor: EffectConstructor[IO] = Fx.IoFx

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def futureEffectConstructor(implicit EC: ExecutionContext): EffectConstructor[Future] =
    Fx.futureFx

  implicit final val idEffectConstructor: EffectConstructor[Id] = Fx.IdFx

}
