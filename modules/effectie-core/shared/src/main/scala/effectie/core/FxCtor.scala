package effectie.core

import scala.concurrent.{ExecutionContext, Future}

trait FxCtor[F[*]] {
  def effectOf[A](a: => A): F[A]
  def pureOf[A](a: A): F[A]
  def unitOf: F[Unit]

  def errorOf[A](throwable: Throwable): F[A]
}

object FxCtor {

  def apply[F[*]: FxCtor]: FxCtor[F] = implicitly[FxCtor[F]]

  trait FutureFxCtor extends FxCtor[Future] {

    implicit def EC0: ExecutionContext

    @inline override final def effectOf[A](a: => A): Future[A] = Future(a)

    @inline override final def pureOf[A](a: A): Future[A] = Future.successful(a)

    @inline override final def unitOf: Future[Unit] = pureOf(())

    @inline override final def errorOf[A](throwable: Throwable): Future[A] = Future.failed[A](throwable)

  }

  final class FxCtorFuture(override implicit val EC0: ExecutionContext) extends FutureFxCtor

  implicit def fxCtorFuture(implicit EC: ExecutionContext): FxCtor[Future] = new FxCtorFuture

}
