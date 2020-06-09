package effectie

trait Attempt[F[_]] {
  type Xor[A, B]
  def attempt[A, B](fb: F[B])(f: Throwable => A): F[Xor[A, B]]
}

object Attempt {
  def apply[F[_]: Attempt]: Attempt[F] = implicitly[Attempt[F]]
}
