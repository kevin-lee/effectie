package effectie.monix

import cats.Id
import cats.effect.IO
import monix.eval.Task

import scala.concurrent.{ExecutionContext, Future}

trait EffectConstructor[F[_]] extends Fx[F] with effectie.CommonFx[F]

object EffectConstructor {
  def apply[F[_]: EffectConstructor]: EffectConstructor[F] = implicitly[EffectConstructor[F]]

  implicit final val taskEffectConstructor: EffectConstructor[Task] = new EffectConstructor[Task] {

    override def effectOf[A](a: => A): Task[A] = Task(a)

    override def pureOf[A](a: A): Task[A] = Task.now(a)

    override val unitOf: Task[Unit] = Task.unit
  }

  implicit final val ioEffectConstructor: EffectConstructor[IO] = new EffectConstructor[IO] {

    override def effectOf[A](a: => A): IO[A] = IO(a)

    override def pureOf[A](a: A): IO[A] = IO.pure(a)

    override val unitOf: IO[Unit] = IO.unit
  }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def futureEffectConstructor(implicit EC: ExecutionContext): EffectConstructor[Future] =
    new FutureEffectConstructor(EC)

  final class FutureEffectConstructor(override val EC0: ExecutionContext)
      extends EffectConstructor[Future]
      with Fx[Future]
      with effectie.CommonFx.CommonFutureFx

  implicit final val idEffectConstructor: EffectConstructor[Id] = new EffectConstructor[Id] {

    @inline override def effectOf[A](a: => A): Id[A] = a

    @inline override def pureOf[A](a: A): Id[A] = effectOf(a)

    @inline override def unitOf: Id[Unit] = ()
  }

}
