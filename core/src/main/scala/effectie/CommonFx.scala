package effectie

import scala.concurrent.{ExecutionContext, Future}

trait CommonFx[F[_]] {
  def effectOf[A](a: => A): F[A]
  def pureOf[A](a: A): F[A]
  def unitOf: F[Unit]

  def errorOf[A](throwable: Throwable): F[A]
}

object CommonFx {

  trait CommonFutureFx extends CommonFx[Future] {

    implicit def EC0: ExecutionContext

    @inline override final def effectOf[A](a: => A): Future[A] = Future(a)

    @inline override final def pureOf[A](a: A): Future[A] = Future.successful(a)

    @inline override final def unitOf: Future[Unit] = pureOf(())

    @inline override final def errorOf[A](throwable: Throwable): Future[A] = Future.failed[A](throwable)

  }
}

trait OldEffectConstructor[F[_]] extends CommonFx[F] {
  @deprecated(message = "Use EffectConstructor[F].pureOf instead", since = "1.4.0")
  @inline final def effectOfPure[A](a: A): F[A] = pureOf[A](a)

  @deprecated(message = "Use EffectConstructor[F].unitOf instead", since = "1.4.0")
  @inline final def effectOfUnit: F[Unit]       = unitOf
}

object OldEffectConstructor {

  trait OldFutureEffectConstructor extends CommonFx.CommonFutureFx with OldEffectConstructor[Future]
}
