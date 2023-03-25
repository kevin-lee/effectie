package effectie.instances.future

import effectie.core.FxCtor

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/** @author Kevin Lee
  * @since 2021-11-03
  */
object fxCtor {

  trait FutureFxCtor extends FxCtor[Future] with Compat {

    implicit def EC0: ExecutionContext

    @inline override final def effectOf[A](a: => A): Future[A] = newFuture(a)

    @inline override final def fromEffect[A](fa: => Future[A]): Future[A] = delegateFuture(fa)

    @inline override final def pureOf[A](a: A): Future[A] = Future.successful(a)

    @inline override def pureOrError[A](a: => A): Future[A] = Future.fromTry(Try(a))

    @inline override final def unitOf: Future[Unit] = pureOf(())

    @inline override final def errorOf[A](throwable: Throwable): Future[A] = Future.failed[A](throwable)

    @inline override final def fromEither[A](either: Either[Throwable, A]): Future[A] = either.fold(errorOf, pureOf)

    @inline override final def fromOption[A](option: Option[A])(orElse: => Throwable): Future[A] =
      option.fold(errorOf[A](orElse))(pureOf)

    @inline override final def fromTry[A](tryA: Try[A]): Future[A] = Future.fromTry(tryA)

    @inline override final def flatMapFa[A, B](fa: Future[A])(f: A => Future[B]): Future[B] = fa.flatMap(f)
  }

  final class FxCtorFuture(override implicit val EC0: ExecutionContext) extends FutureFxCtor

  implicit def fxCtorFuture(implicit EC: ExecutionContext): FxCtor[Future] = new FxCtorFuture

}
