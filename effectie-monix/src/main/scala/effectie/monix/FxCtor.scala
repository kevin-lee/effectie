package effectie.monix

import cats.Id
import cats.effect.IO
import monix.eval.Task

import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2021-05-16
  */
trait FxCtor[F[_]] extends effectie.FxCtor[F]

object FxCtor {
  def apply[F[_]: FxCtor]: FxCtor[F] = implicitly[FxCtor[F]]

  implicit final val taskFxCtor: FxCtor[Task] = Fx.TaskFx

  implicit final val ioFxCtor: FxCtor[IO] = Fx.IoFx

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def futureFxCtor(implicit EC: ExecutionContext): FxCtor[Future] =
    Fx.futureFx

  implicit final val idFxCtor: FxCtor[Id] = Fx.IdFx

}
