package effectie.core

/** @author Kevin Lee
  * @since 2022-12-31
  */
trait CanBlock[F[*]] {
  def blockingOf[A](a: => A): F[A]
}
object CanBlock {
  def apply[F[*]: CanBlock]: CanBlock[F] = implicitly[CanBlock[F]]
}
