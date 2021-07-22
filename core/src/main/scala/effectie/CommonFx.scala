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

    override def effectOf[A](a: => A): Future[A] = Future(a)

    override def pureOf[A](a: A): Future[A] = effectOf(a)

    override def unitOf: Future[Unit] = Future(())
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
