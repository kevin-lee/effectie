package effectie.scalaz

import scalaz.Monad
import scalaz.Scalaz.Id
import scalaz.effect.IO

import scala.concurrent.{ExecutionContext, Future}

trait EffectConstructor[F[_]] extends effectie.EffectConstructor[F]

object EffectConstructor {
  def apply[F[_] : EffectConstructor]: EffectConstructor[F] = implicitly[EffectConstructor[F]]

  implicit val ioEffectConstructor: EffectConstructor[IO] = new EffectConstructor[IO] {

    override def effectOf[A](a: => A): IO[A] = IO(a)

    override def effectOfPure[A](a: A): IO[A] = Monad[IO].pure(a)

    override def effectOfUnit: IO[Unit] = IO.ioUnit
  }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def futureEffectConstructor(implicit EC: ExecutionContext): EffectConstructor[Future] =
    new FutureEffectConstructor(EC)

  final class FutureEffectConstructor(override val EC0: ExecutionContext)
    extends EffectConstructor[Future]
    with effectie.EffectConstructor.FutureEffectConstructor

  implicit final val idEffectConstructor: EffectConstructor[Id] = new EffectConstructor[Id] {

    override def effectOf[A](a: => A): Id[A] = a

    override def effectOfPure[A](a: A): Id[A] = a

    override def effectOfUnit: Id[Unit] = ()
  }

}