package effectie

trait CanCatch[F[_]] {
  type Xor[A, B]
  type XorT[A, B]

  def catchNonFatalThrowable[A](fa: => F[A]): F[Xor[Throwable, A]]

  def catchNonFatal[A, B](fb: => F[B])(f: Throwable => A): F[Xor[A, B]]

  def catchNonFatalEither[A, B](fab: => F[Xor[A, B]])(f: Throwable => A): F[Xor[A, B]]

  def catchNonFatalEitherT[A, B](fab: => XorT[A, B])(f: Throwable => A): XorT[A, B]
}

object CanCatch {
  def apply[F[_]: CanCatch]: CanCatch[F] = implicitly[CanCatch[F]]
}
