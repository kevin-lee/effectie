package effectie.scalaz

import effectie.{CommonFx, OldEffectConstructor}
import scalaz.Scalaz.Id
import scalaz.effect.IO

import scala.concurrent.{ExecutionContext, Future}

trait Fx[F[_]] extends EffectConstructor[F] with FxCtor[F] with CommonFx[F] with OldEffectConstructor[F]

object Fx {
  def apply[F[_]: Fx]: Fx[F] = implicitly[Fx[F]]

  implicit object IoFx extends Fx[IO] {

    @inline override def effectOf[A](a: => A): IO[A] = IO(a)

    @inline override def pureOf[A](a: A): IO[A] = effectOf(a)

    @inline override val unitOf: IO[Unit] = IO.ioUnit

    @inline override def errorOf[A](throwable: Throwable): IO[A] = IO.throwIO(throwable)

  }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def futureFx(implicit EC: ExecutionContext): Fx[Future] =
    new FutureFx

  final class FutureFx(implicit override val EC0: ExecutionContext)
      extends Fx[Future]
      with EffectConstructor[Future]
      with FxCtor[Future]
      with CommonFx.CommonFutureFx
      with OldEffectConstructor.OldFutureEffectConstructor

  implicit object IdFx extends Fx[Id] {

    @inline override def effectOf[A](a: => A): Id[A] = a

    @inline override def pureOf[A](a: A): Id[A] = a

    @inline override val unitOf: Id[Unit] = ()

    @inline override def errorOf[A](throwable: Throwable): Id[A] = throw throwable
  }

}
