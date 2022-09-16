package effectie.core

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

trait FxCtor[F[*]] {
  def effectOf[A](a: => A): F[A]
  def pureOf[A](a: A): F[A]
  def pureOrError[A](a: => A): F[A]

  def unitOf: F[Unit]

  def errorOf[A](throwable: Throwable): F[A]

  def fromEither[A](either: Either[Throwable, A]): F[A]
  def fromOption[A](option: Option[A])(orElse: => Throwable): F[A]
  def fromTry[A](tryA: scala.util.Try[A]): F[A]
}

object FxCtor {

  def apply[F[*]: FxCtor]: FxCtor[F] = implicitly[FxCtor[F]]

  trait FutureFxCtor extends FxCtor[Future] {

    implicit def EC0: ExecutionContext

    @inline override final def effectOf[A](a: => A): Future[A] = Future(a)

    @inline override final def pureOf[A](a: A): Future[A] = Future.successful(a)

    @inline override def pureOrError[A](a: => A): Future[A] = Future.fromTry(Try(a))

    @inline override final def unitOf: Future[Unit] = pureOf(())

    @inline override final def errorOf[A](throwable: Throwable): Future[A] = Future.failed[A](throwable)

    @inline override def fromEither[A](either: Either[Throwable, A]): Future[A] = either.fold(errorOf, pureOf)

    @inline override def fromOption[A](option: Option[A])(orElse: => Throwable): Future[A] =
      option.fold(errorOf[A](orElse))(pureOf)

    @inline override def fromTry[A](tryA: Try[A]): Future[A] = Future.fromTry(tryA)
  }

  final class FxCtorFuture(override implicit val EC0: ExecutionContext) extends FutureFxCtor

  implicit def fxCtorFuture(implicit EC: ExecutionContext): FxCtor[Future] = new FxCtorFuture

}
