package effectie.cats

import cats.effect.IO
import cats.{Id, Monad}
import effectie.{CommonFx, OldEffectConstructor}

import scala.concurrent.{ExecutionContext, Future}

trait Fx[F[_]] extends EffectConstructor[F] with FxCtor[F] with CommonFx[F] with OldEffectConstructor[F] with Monad[F]

object Fx {
  def apply[F[_]: Fx]: Fx[F] = implicitly[Fx[F]]

  implicit object IoFx extends Fx[IO] {

    @inline private val syncIo: Monad[IO] = cats.effect.Sync[IO]

    @inline override def pure[A](x: A): IO[A] = pureOf(x)

    @inline override def flatMap[A, B](fa: IO[A])(f: A => IO[B]): IO[B] =
      syncIo.flatMap(fa)(f)

    @inline override def tailRecM[A, B](a: A)(f: A => IO[Either[A, B]]): IO[B] =
      syncIo.tailRecM(a)(f)

    @inline override def effectOf[A](a: => A): IO[A] = IO(a)

    @inline override def pureOf[A](a: A): IO[A] = IO.pure(a)

    @inline override val unitOf: IO[Unit] = IO.unit
  }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def futureFx(implicit EC: ExecutionContext): Fx[Future] = new FutureFx

  final class FutureFx(implicit override val EC0: ExecutionContext)
      extends Fx[Future]
      with EffectConstructor[Future]
      with FxCtor[Future]
      with CommonFx.CommonFutureFx
      with OldEffectConstructor.OldFutureEffectConstructor {

    @inline private val futureInstance: Monad[Future] = cats.instances.future.catsStdInstancesForFuture

    @inline override def pure[A](x: A): Future[A] =
      pureOf(x)

    @inline override def flatMap[A, B](fa: Future[A])(f: A => Future[B]): Future[B] =
      futureInstance.flatMap(fa)(f)

    @inline override def tailRecM[A, B](a: A)(f: A => Future[Either[A, B]]): Future[B] =
      futureInstance.tailRecM(a)(f)

  }

  implicit object IdFx extends Fx[Id] {

    @inline private val idInstance: Monad[Id] = cats.catsInstancesForId

    @inline override def pure[A](x: A): Id[A] = pureOf(x)

    @inline override def flatMap[A, B](fa: Id[A])(f: A => Id[B]): Id[B] =
      idInstance.flatMap(fa)(f)

    @inline override def tailRecM[A, B](a: A)(f: A => Id[Either[A, B]]): Id[B] =
      idInstance.tailRecM(a)(f)

    @inline override def effectOf[A](a: => A): Id[A] = a

    @inline override def pureOf[A](a: A): Id[A] = a

    @inline override val unitOf: Id[Unit] = ()
  }

}
