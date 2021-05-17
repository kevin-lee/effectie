package effectie.cats

import cats.Id
import cats.effect.IO
import effectie.{CommonEft, OldEffectConstructor}

import scala.concurrent.{ExecutionContext, Future}

trait EffectConstructor[F[_]] extends Eft[F] with CommonEft[F] with OldEffectConstructor[F]

object EffectConstructor {
  def apply[F[_]: EffectConstructor]: EffectConstructor[F] = implicitly[EffectConstructor[F]]

  implicit final val ioEffectConstructor: EffectConstructor[IO] = new EffectConstructor[IO] {

    private val eft: Eft[IO] = Eft.ioEft

    override def effectOf[A](a: => A): IO[A] = eft.effectOf(a)

    override def pureOf[A](a: A): IO[A] = eft.pureOf(a)

    override def unitOf: IO[Unit] = eft.unitOf
  }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def futureEffectConstructor(implicit EC: ExecutionContext): EffectConstructor[Future] =
    new FutureEffectConstructor(EC)

  final class FutureEffectConstructor(override val EC0: ExecutionContext)
    extends EffectConstructor[Future]
    with Eft[Future]
    with CommonEft.CommonFutureEft
    with OldEffectConstructor.OldFutureEffectConstructor

  implicit final val idEffectConstructor: EffectConstructor[Id] = new EffectConstructor[Id] {

    @inline override def effectOf[A](a: => A): Id[A] = a

    @inline override def pureOf[A](a: A): Id[A] = effectOf(a)

    @inline override def unitOf: Id[Unit] = ()
  }

}