package effectie.monix

import cats.Id
import monix.eval.Task

import scala.concurrent.{ExecutionContext, Future}

/**
 * @author Kevin Lee
 * @since 2021-05-16
 */
trait Eft[F[_]] extends effectie.CommonEft[F]

object Eft {
  def apply[F[_]: Eft]: Eft[F] = implicitly[Eft[F]]

  implicit final val taskEft: Eft[Task] = new Eft[Task] {

    override def effectOf[A](a: => A): Task[A] = Task(a)

    override def pureOf[A](a: A): Task[A] = Task.now(a)

    override def unitOf: Task[Unit] = Task.unit
  }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def futureEft(implicit EC: ExecutionContext): Eft[Future] =
    new FutureEft(EC)

  final class FutureEft(override val EC0: ExecutionContext)
    extends Eft[Future]
      with effectie.CommonEft.CommonFutureEft

  implicit final val idEft: Eft[Id] = new Eft[Id] {

    @inline override def effectOf[A](a: => A): Id[A] = a

    @inline override def pureOf[A](a: A): Id[A] = effectOf(a)

    @inline override def unitOf: Id[Unit] = ()
  }

}