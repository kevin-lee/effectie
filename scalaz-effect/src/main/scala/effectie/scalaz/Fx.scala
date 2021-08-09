package effectie.scalaz

import effectie.{CommonFx, OldEffectConstructor}
import scalaz.Scalaz.Id
import scalaz.effect.IO
import scalaz.{Monad, Scalaz}

import scala.concurrent.{ExecutionContext, Future}

trait Fx[F[_]] extends EffectConstructor[F] with FxCtor[F] with CommonFx[F] with OldEffectConstructor[F] with Monad[F]

object Fx {
  def apply[F[_]: Fx]: Fx[F] = implicitly[Fx[F]]

  implicit object IoFx extends Fx[IO] {

    @inline private val ioMonad: Monad[IO] = IO.ioMonad

    @inline override def point[A](a: => A): IO[A] = pureOf(a)

    @inline override def bind[A, B](fa: IO[A])(f: A => IO[B]): IO[B] = ioMonad.bind(fa)(f)

    @inline override def effectOf[A](a: => A): IO[A] = IO(a)

    @inline override def pureOf[A](a: A): IO[A] = ioMonad.pure(a)

    @inline override val unitOf: IO[Unit] = IO.ioUnit
  }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def futureFx(implicit EC: ExecutionContext): Fx[Future] =
    new FutureFx

  final class FutureFx(implicit override val EC0: ExecutionContext)
      extends Fx[Future]
      with EffectConstructor[Future]
      with FxCtor[Future]
      with CommonFx.CommonFutureFx
      with OldEffectConstructor.OldFutureEffectConstructor {

    @inline private val futureInstance: Monad[Future] = scalaz.Scalaz.futureInstance

    @inline override def bind[A, B](fa: Future[A])(f: A => Future[B]): Future[B] =
      futureInstance.bind(fa)(f)

    @inline override def point[A](a: => A): Future[A] = futureInstance.point(a)

  }

  implicit object IdFx extends Fx[Id] {

    @inline private val idInstance: Monad[Id] = scalaz.Scalaz.id

    @inline override def point[A](a: => A): Scalaz.Id[A] = idInstance.point(a)

    @inline override def bind[A, B](fa: Scalaz.Id[A])(f: A => Scalaz.Id[B]): Scalaz.Id[B] =
      idInstance.bind(fa)(f)

    @inline override def effectOf[A](a: => A): Id[A] = a

    @inline override def pureOf[A](a: A): Id[A] = a

    @inline override val unitOf: Id[Unit] = ()

  }

}
