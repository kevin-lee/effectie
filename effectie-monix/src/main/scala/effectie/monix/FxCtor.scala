package effectie.monix

import cats.Id
import cats.effect.IO
import monix.eval.Task

/** @author Kevin Lee
  * @since 2021-05-16
  */
object FxCtor {
  type FxCtor[F[_]] = effectie.FxCtor[F]

  implicit final val taskFxCtor: FxCtor[Task] = Fx.TaskFx

  implicit final val ioFxCtor: FxCtor[IO] = Fx.IoFx

  implicit final val idFxCtor: FxCtor[Id] = Fx.IdFx

}
