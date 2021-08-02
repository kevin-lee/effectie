package effectie

import scala.concurrent.{ExecutionContext, Future}

trait CommonFx[F[_]] {
  def effectOf[A](a: => A): F[A]
  def pureOf[A](a: A): F[A]
  def unitOf: F[Unit]
}

object CommonFx {

  trait CommonFutureFx extends CommonFx[Future] {

    implicit def EC0: ExecutionContext

    @inline override def effectOf[A](a: => A): Future[A] = Future(a)

    @inline override def pureOf[A](a: A): Future[A] = Future.successful(a)

    @inline override def unitOf: Future[Unit] = pureOf(())
  }
}

trait OldEffectConstructor[F[_]] extends CommonFx[F] {
  @deprecated(message = "Use EffectConstructor[F].pureOf instead", since = "1.4.0")
  @inline def effectOfPure[A](a: A): F[A] = pureOf[A](a)
  @deprecated(message = "Use EffectConstructor[F].unitOf instead", since = "1.4.0")
  @inline def effectOfUnit: F[Unit] = unitOf
}

object OldEffectConstructor {

  trait OldFutureEffectConstructor
    extends CommonFx.CommonFutureFx
       with OldEffectConstructor[Future]
}
