package effectie.scalaz

import scalaz.Monad
import scalaz.effect.IO

trait EffectConstructor[F[_]] extends effectie.EffectConstructor[F]

object EffectConstructor {
  def apply[F[_] : EffectConstructor]: EffectConstructor[F] = implicitly[EffectConstructor[F]]

  implicit val ioEffectConstructor: EffectConstructor[IO] = new EffectConstructor[IO] {

    override def effectOf[A](a: => A): IO[A] = IO(a)

    override def pureEffect[A](a: A): IO[A] = Monad[IO].pure(a)

    override def unit: IO[Unit] = IO.ioUnit
  }

}