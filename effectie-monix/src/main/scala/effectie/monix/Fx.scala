package effectie.monix

import cats.effect.IO
import cats.Id
import monix.eval.Task

import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2021-05-16
  */
trait Fx[F[_]] extends EffectConstructor[F] with FxCtor[F] with effectie.CommonFx[F]

object Fx {
  def apply[F[_]: Fx]: Fx[F] = implicitly[Fx[F]]

  implicit object TaskFx extends Fx[Task] {

    @inline override def effectOf[A](a: => A): Task[A] = Task(a)

    @inline override def pureOf[A](a: A): Task[A] = Task.now(a)

    @inline override val unitOf: Task[Unit] = Task.unit

    @inline override def errorOf[A](throwable: Throwable): Task[A] = Task.raiseError(throwable)

  }

  implicit object IoFx extends Fx[IO] {

    @inline override def effectOf[A](a: => A): IO[A] = IO(a)

    @inline override def pureOf[A](a: A): IO[A] = IO.pure(a)

    @inline override val unitOf: IO[Unit] = IO.unit

    @inline override def errorOf[A](throwable: Throwable): IO[A] = IO.raiseError(throwable)

  }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def futureFx(implicit EC: ExecutionContext): Fx[Future] = new FutureFx

  final class FutureFx(implicit override val EC0: ExecutionContext)
      extends Fx[Future]
      with EffectConstructor[Future]
      with FxCtor[Future]
      with effectie.CommonFx.CommonFutureFx

  implicit object IdFx extends Fx[Id] {

    @inline override def effectOf[A](a: => A): Id[A] = a

    @inline override def pureOf[A](a: A): Id[A] = a

    @inline override val unitOf: Id[Unit] = ()

    @inline override def errorOf[A](throwable: Throwable): Id[A] = throw throwable

  }

}
