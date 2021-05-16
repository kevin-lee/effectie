package effectie.monix

import cats.Id
import monix.eval.Task

import scala.concurrent.{ExecutionContext, Future}

trait EffectConstructor[F[_]] extends Eft[F] with effectie.CommonEft[F]

object EffectConstructor {
  def apply[F[_]: EffectConstructor]: EffectConstructor[F] = implicitly[EffectConstructor[F]]

  implicit final val taskEffectConstructor: EffectConstructor[Task] = new EffectConstructor[Task] {

    private val eft: Eft[Task] = Eft.taskEft

    override def effectOf[A](a: => A): Task[A] = eft.effectOf(a)

    override def pureOf[A](a: A): Task[A] = eft.pureOf(a)

    override def unitOf: Task[Unit] = eft.unitOf
  }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def futureEffectConstructor(implicit EC: ExecutionContext): EffectConstructor[Future] =
    new FutureEffectConstructor(EC)

  final class FutureEffectConstructor(override val EC0: ExecutionContext)
    extends EffectConstructor[Future]
      with Eft[Future]
      with effectie.CommonEft.CommonFutureEft

  implicit final val idEffectConstructor: EffectConstructor[Id] = new EffectConstructor[Id] {

    @inline override def effectOf[A](a: => A): Id[A] = a

    @inline override def pureOf[A](a: A): Id[A] = effectOf(a)

    @inline override def unitOf: Id[Unit] = ()
  }

}