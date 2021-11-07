package effectie

import scala.concurrent.{ExecutionContext, Future}

trait FxCtor[F[_]] {
  def effectOf[A](a: => A): F[A]
  def pureOf[A](a: A): F[A]
  def unitOf: F[Unit]

  def errorOf[A](throwable: Throwable): F[A]
}

object FxCtor {

  trait FutureFxCtor extends FxCtor[Future] {

    implicit def EC0: ExecutionContext

    @inline override final def effectOf[A](a: => A): Future[A] = Future(a)

    @inline override final def pureOf[A](a: A): Future[A] = Future.successful(a)

    @inline override final def unitOf: Future[Unit] = pureOf(())

    @inline override final def errorOf[A](throwable: Throwable): Future[A] = Future.failed[A](throwable)

  }
}
