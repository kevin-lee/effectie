package effectie

import scala.concurrent.{ExecutionContext, Future}

trait EffectConstructor[F[_]] {
  def effectOf[A](a: => A): F[A]
  @deprecated(message = "Use EffectConstructor[F].pureOf instead", since = "1.4.0")
  def effectOfPure[A](a: A): F[A]
  def pureOf[A](a: A): F[A]
  @deprecated(message = "Use EffectConstructor[F].unitOf instead", since = "1.4.0")
  def effectOfUnit: F[Unit]
  def unitOf: F[Unit]
}

object EffectConstructor {
  def apply[F[_]: EffectConstructor]: EffectConstructor[F] = implicitly[EffectConstructor[F]]

  trait FutureEffectConstructor extends EffectConstructor[Future] {

    implicit def EC0: ExecutionContext

    override def effectOf[A](a: => A): Future[A] = Future(a)

    override def effectOfPure[A](a: A): Future[A] = pureOf(a)

    override def pureOf[A](a: A): Future[A] = effectOf(a)

    override def effectOfUnit: Future[Unit] = unitOf

    override def unitOf: Future[Unit] = Future(())
  }
}