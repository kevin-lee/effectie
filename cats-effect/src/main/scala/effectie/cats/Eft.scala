package effectie.cats

import cats.Id
import cats.effect.IO
import effectie.{CommonEft, OldEffectConstructor}

import scala.concurrent.{ExecutionContext, Future}

trait Eft[F[_]] extends CommonEft[F] with OldEffectConstructor[F]

object Eft {
  def apply[F[_]: Eft]: Eft[F] = implicitly[Eft[F]]

  implicit final val ioEft: Eft[IO] =
    new Eft[IO] {

      override def effectOf[A](a: => A): IO[A] = IO(a)

      override def pureOf[A](a: A): IO[A] = IO.pure(a)

      override def unitOf: IO[Unit] = IO.unit
    }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def futureEft(implicit EC: ExecutionContext): Eft[Future] =
    new FutureEft(EC)

  final class FutureEft(override val EC0: ExecutionContext)
      extends Eft[Future]
      with CommonEft.CommonFutureEft
      with OldEffectConstructor.OldFutureEffectConstructor

  implicit final val idEft: Eft[Id] =
    new Eft[Id] {

      @inline override def effectOf[A](a: => A): Id[A] = a

      @inline override def pureOf[A](a: A): Id[A] = effectOf(a)

      @inline override def unitOf: Id[Unit] = ()
    }

}
