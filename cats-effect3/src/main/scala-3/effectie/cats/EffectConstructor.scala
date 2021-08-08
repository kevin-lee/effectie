package effectie.cats

import cats.Id
import cats.effect.IO
import effectie.{CommonFx, OldEffectConstructor}

import scala.concurrent.{ExecutionContext, Future}

trait EffectConstructor[F[_]] extends Fx[F] with CommonFx[F] with OldEffectConstructor[F]

object EffectConstructor {
  def apply[F[_]: EffectConstructor]: EffectConstructor[F] = summon[EffectConstructor[F]]

  given ioEffectConstructor: EffectConstructor[IO] with {

    override def effectOf[A](a: => A): IO[A] = IO(a)

    override def pureOf[A](a: A): IO[A] = IO.pure(a)

    override val unitOf: IO[Unit] = IO.unit
  }

  given futureEffectConstructor(using EC: ExecutionContext): EffectConstructor[Future] =
    new FutureEffectConstructor(EC)

  final class FutureEffectConstructor(override val EC0: ExecutionContext)
      extends EffectConstructor[Future]
      with Fx[Future]
      with CommonFx.CommonFutureFx
      with OldEffectConstructor.OldFutureEffectConstructor

  given idEffectConstructor: EffectConstructor[Id] with {

    @inline override def effectOf[A](a: => A): Id[A] = a

    @inline override def pureOf[A](a: A): Id[A] = effectOf(a)

    @inline override def unitOf: Id[Unit] = ()
  }

}
