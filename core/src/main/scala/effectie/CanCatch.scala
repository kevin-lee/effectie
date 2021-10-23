package effectie

trait CanCatch[F[_]] {
  type Xor[A, B]
  type XorT[A, B]

  def catchNonFatalThrowable[A](fa: => F[A]): F[Xor[Throwable, A]]

  def catchNonFatal[A, B](fb: => F[B])(f: Throwable => A): F[Xor[A, B]]

  def catchNonFatalEither[A, AA >: A, B](fab: => F[Xor[A, B]])(f: Throwable => AA): F[Xor[AA, B]]

  def catchNonFatalEitherT[A, AA >: A, B](fab: => XorT[A, B])(f: Throwable => AA): XorT[AA, B]
}

object CanCatch {
  def apply[F[_]: CanCatch]: CanCatch[F] = implicitly[CanCatch[F]]
}
