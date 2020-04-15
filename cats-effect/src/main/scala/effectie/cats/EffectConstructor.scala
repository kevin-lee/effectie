package effectie.cats

import cats.effect.IO

trait EffectConstructor[F[_]] extends effectie.EffectConstructor[F]

object EffectConstructor {
  def apply[F[_] : EffectConstructor]: EffectConstructor[F] = implicitly[EffectConstructor[F]]

  implicit val ioEffectConstructor: EffectConstructor[IO] = new EffectConstructor[IO] {

    override def effectOf[A](a: => A): IO[A] = IO(a)

    override def effectOfPure[A](a: A): IO[A] = IO.pure(a)

    override def effectOfUnit: IO[Unit] = IO.unit
  }

}