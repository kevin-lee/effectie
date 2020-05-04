package effectie

import scala.concurrent.{ExecutionContext, Future}

trait EffectConstructor[F[_]] {
  def effectOf[A](a: => A): F[A]
  def effectOfPure[A](a: A): F[A]
  def effectOfUnit: F[Unit]
}

object EffectConstructor {
  def apply[F[_] : EffectConstructor]: EffectConstructor[F] = implicitly[EffectConstructor[F]]

  trait FutureEffectConstructor extends EffectConstructor[Future] {

    implicit def EC0: ExecutionContext

    override def effectOf[A](a: => A): Future[A] = Future(a)

    override def effectOfPure[A](a: A): Future[A] = effectOf(a)

    override def effectOfUnit: Future[Unit] = Future(())
  }
}