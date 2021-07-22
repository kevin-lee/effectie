package effectie.monix

import cats.Id
import cats.effect.IO
import monix.eval.Task

import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2021-05-16
  */
trait Eft[F[_]] extends effectie.CommonEft[F]

object Eft {
  def apply[F[_]: Eft]: Eft[F] = implicitly[Eft[F]]

  implicit final val taskEft: Eft[Task] = EffectConstructor.taskEffectConstructor

  implicit final val ioEft: Eft[IO] = EffectConstructor.ioEffectConstructor

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def futureEft(implicit EC: ExecutionContext): Eft[Future] =
    EffectConstructor.futureEffectConstructor(EC)

  implicit final val idEft: Eft[Id] = EffectConstructor.idEffectConstructor

}
