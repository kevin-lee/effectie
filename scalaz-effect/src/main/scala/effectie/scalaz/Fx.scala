package effectie.scalaz

import scalaz.Scalaz.Id
import scalaz.effect.IO

import scala.concurrent.{ExecutionContext, Future}

trait Fx[F[_]] extends effectie.Fx[F] with FxCtor[F] with effectie.FxCtor[F]

object Fx {
  def apply[F[_]: Fx]: Fx[F] = implicitly[Fx[F]]

  implicit object IoFx extends Fx[IO] {

    @inline override final def effectOf[A](a: => A): IO[A] = IO(a)

    @inline override final def pureOf[A](a: A): IO[A] = effectOf(a)

    @inline override final val unitOf: IO[Unit] = IO.ioUnit

    @inline override final def errorOf[A](throwable: Throwable): IO[A] = IO.throwIO(throwable)

  }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def futureFx(implicit EC: ExecutionContext): Fx[Future] =
    new FutureFx

  final class FutureFx(implicit override val EC0: ExecutionContext)
      extends Fx[Future]
      with FxCtor[Future]
      with effectie.FxCtor.FutureFxCtor

  implicit object IdFx extends Fx[Id] {

    @inline override final def effectOf[A](a: => A): Id[A] = a

    @inline override final def pureOf[A](a: A): Id[A] = a

    @inline override final val unitOf: Id[Unit] = ()

    @inline override final def errorOf[A](throwable: Throwable): Id[A] = throw throwable
  }

}
