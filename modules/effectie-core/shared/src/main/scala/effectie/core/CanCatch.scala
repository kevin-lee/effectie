package effectie.core

import scala.concurrent.{ExecutionContext, Future}

trait CanCatch[F[*]] {

  def mapFa[A, B](fa: F[A])(f: A => B): F[B]

  def catchNonFatalThrowable[A](fa: => F[A]): F[Either[Throwable, A]]

  @inline final def catchNonFatal[A, B](fb: => F[B])(f: Throwable => A): F[Either[A, B]] =
    mapFa(catchNonFatalThrowable[B](fb))(ab => ab.left.map(f))

  @inline final def catchNonFatalEither[A, AA >: A, B](fab: => F[Either[A, B]])(f: Throwable => AA): F[Either[AA, B]] =
    mapFa(catchNonFatal(fab)(f))(_.joinRight)

}

object CanCatch {
  def apply[F[*]: CanCatch]: CanCatch[F] = implicitly[CanCatch[F]]

  trait FutureCanCatch extends CanCatch[Future] {
    implicit def EC0: ExecutionContext
    @inline override final def mapFa[A, B](fa: Future[A])(f: A => B): Future[B] =
      fa.map(f)(EC0)

    @inline override final def catchNonFatalThrowable[A](fa: => Future[A]): Future[Either[Throwable, A]] =
      fa.transform {
        case scala.util.Success(a) =>
          scala.util.Try[Either[Throwable, A]](Right(a))

        case scala.util.Failure(scala.util.control.NonFatal(ex)) =>
          scala.util.Try[Either[Throwable, A]](Left(ex))

        case scala.util.Failure(ex) =>
          throw ex // scalafix:ok DisableSyntax.throw
      }

  }

  final class CanCatchFuture(override implicit val EC0: ExecutionContext) extends FutureCanCatch

  implicit def canCatchFuture(implicit EC: ExecutionContext): CanCatch[Future] = new CanCatchFuture

}