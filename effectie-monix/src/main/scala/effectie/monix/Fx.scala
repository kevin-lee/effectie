package effectie.monix

import cats.Id
import cats.effect.IO
import monix.eval.Task

import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2021-05-16
  */
trait Fx[F[_]] extends effectie.CommonFx[F]

object Fx {
  def apply[F[_]: Fx]: Fx[F] = implicitly[Fx[F]]

  implicit final val taskFx: Fx[Task] = EffectConstructor.taskEffectConstructor

  implicit final val ioFx: Fx[IO] = EffectConstructor.ioEffectConstructor

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def futureFx(implicit EC: ExecutionContext): Fx[Future] =
    EffectConstructor.futureEffectConstructor(EC)

  implicit final val idFx: Fx[Id] = EffectConstructor.idEffectConstructor

}
