package effectie

trait CanCatch[F[_]] {
  type Xor[A, B]
  def catchNonFatal[A, B](fb: => F[B])(f: Throwable => A): F[Xor[A, B]]
}

object CanCatch {
  def apply[F[_]: CanCatch]: CanCatch[F] = implicitly[CanCatch[F]]
}
