package effectie.monix

import cats.Id
import monix.eval.Task

import scala.concurrent.{ExecutionContext, Future}

trait EffectConstructor[F[_]] extends effectie.CommonEffectConstructor[F]

object EffectConstructor {
  def apply[F[_]: EffectConstructor]: EffectConstructor[F] = implicitly[EffectConstructor[F]]

  implicit val ioEffectConstructor: EffectConstructor[Task] = new EffectConstructor[Task] {

    override def effectOf[A](a: => A): Task[A] = Task(a)

    override def pureOf[A](a: A): Task[A] = Task.now(a)

    override def unitOf: Task[Unit] = Task.unit
  }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def futureEffectConstructor(implicit EC: ExecutionContext): EffectConstructor[Future] =
    new FutureEffectConstructor(EC)

  final class FutureEffectConstructor(override val EC0: ExecutionContext)
    extends EffectConstructor[Future]
      with effectie.CommonEffectConstructor.CommonFutureEffectConstructor

  implicit final val idEffectConstructor: EffectConstructor[Id] = new EffectConstructor[Id] {

    @inline override def effectOf[A](a: => A): Id[A] = a

    @inline override def pureOf[A](a: A): Id[A] = effectOf(a)

    @inline override def unitOf: Id[Unit] = ()
  }

}