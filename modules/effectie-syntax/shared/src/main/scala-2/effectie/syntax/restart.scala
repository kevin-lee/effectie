package effectie.syntax

import effectie.core.CanRestart
import effectie.syntax.restart.CanRestartOps

/** @author Kevin Lee
  * @since 2023-09-07
  */
trait restart {

  implicit def canRestart[F[*], A](fa: F[A]): CanRestartOps[F[*], A] = new CanRestartOps(fa)

}
object restart extends restart {

  final class CanRestartOps[F[*], A](private val fa: F[A]) extends AnyVal {
    def restartWhile(p: A => Boolean)(implicit canRestart: CanRestart[F]): F[A] =
      canRestart.restartWhile(fa)(p)

    def restartUntil(p: A => Boolean)(implicit canRestart: CanRestart[F]): F[A] =
      canRestart.restartUntil(fa)(p)

    def restartOnError(maxRetries: Long)(implicit canRestart: CanRestart[F]): F[A] =
      canRestart.restartOnError(fa)(maxRetries)

    def restartOnErrorIfTrue(p: Throwable => Boolean)(implicit canRestart: CanRestart[F]): F[A] =
      canRestart.restartOnErrorIfTrue(fa)(p)
  }

}
