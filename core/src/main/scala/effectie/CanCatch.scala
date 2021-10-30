package effectie

import scala.concurrent.{ExecutionContext, Future}

trait CanCatch[F[_]] {

  type Xor[+A, +B]
  type XorT[A, B]

  protected def xorT[A, B](fab: F[Xor[A, B]]): XorT[A, B]
  protected def xorT2FXor[A, B](efab: XorT[A, B]): F[Xor[A, B]]

  protected def mapFa[A, B](fa: F[A])(f: A => B): F[B]

  protected def leftMapXor[A, AA, B](aOrB: Xor[A, B])(f: A => AA): Xor[AA, B]

  protected def xorJoinRight[A, AA >: A, B](aOrB: Xor[AA, Xor[A, B]]): Xor[AA, B]

  def catchNonFatalThrowable[A](fa: => F[A]): F[Xor[Throwable, A]]

  final def catchNonFatal[A, B](fb: => F[B])(f: Throwable => A): F[Xor[A, B]] =
    mapFa(catchNonFatalThrowable[B](fb))(fab => leftMapXor[Throwable, A, B](fab)(f))

  final def catchNonFatalEither[A, AA >: A, B](fab: => F[Xor[A, B]])(f: Throwable => AA): F[Xor[AA, B]] =
    mapFa(catchNonFatal(fab)(f))(xorJoinRight)

  final def catchNonFatalEitherT[A, AA >: A, B](fab: => XorT[A, B])(f: Throwable => AA): XorT[AA, B] =
    xorT(catchNonFatalEither[A, AA, B](xorT2FXor(fab))(f))

}

object CanCatch {
  def apply[F[_]: CanCatch]: CanCatch[F] = implicitly[CanCatch[F]]

  trait EitherBasedCanCatch[F[_]] extends CanCatch[F] {
    final type Xor[+A, +B] = Either[A, B]

    @inline override final protected def leftMapXor[A, AA, B](aOrB: Either[A, B])(f: A => AA): Either[AA, B] =
      aOrB.left.map(f)

    @inline override final protected def xorJoinRight[A, AA >: A, B](
      aOrB: Either[AA, Either[A, B]]
    ): Either[AA, B] =
      aOrB.joinRight
  }

  abstract class CanCatchFuture(val EC0: ExecutionContext) extends CanCatch[Future] {
    @inline override final protected def mapFa[A, B](fa: Future[A])(f: A => B): Future[B] =
      fa.map(f)(EC0)

  }

  abstract class EitherBasedCanCatchFuture(override val EC0: ExecutionContext)
      extends CanCatchFuture(EC0)
      with EitherBasedCanCatch[Future]

}
