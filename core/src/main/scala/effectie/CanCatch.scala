package effectie

import scala.concurrent.{ExecutionContext, Future}

trait CanCatch[F[_]] {

  type XorT[A, B]

  protected def xorT[A, B](fab: F[Either[A, B]]): XorT[A, B]
  protected def xorT2FEither[A, B](efab: XorT[A, B]): F[Either[A, B]]

  def mapFa[A, B](fa: F[A])(f: A => B): F[B]

  def catchNonFatalThrowable[A](fa: => F[A]): F[Either[Throwable, A]]

  final def catchNonFatal[A, B](fb: => F[B])(f: Throwable => A): F[Either[A, B]] =
    mapFa(catchNonFatalThrowable[B](fb))(ab => ab.left.map(f))

  final def catchNonFatalEither[A, AA >: A, B](fab: => F[Either[A, B]])(f: Throwable => AA): F[Either[AA, B]] =
    mapFa(catchNonFatal(fab)(f))(_.joinRight)

  final def catchNonFatalEitherT[A, AA >: A, B](fab: => XorT[A, B])(f: Throwable => AA): XorT[AA, B] =
    xorT(catchNonFatalEither[A, AA, B](xorT2FEither(fab))(f))

}

object CanCatch {
  def apply[F[_]: CanCatch]: CanCatch[F] = implicitly[CanCatch[F]]

  trait CanCatchFuture extends CanCatch[Future] {
    def EC0: ExecutionContext
    @inline override final def mapFa[A, B](fa: Future[A])(f: A => B): Future[B] =
      fa.map(f)(EC0)

  }

}
