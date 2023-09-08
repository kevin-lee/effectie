package effectie.syntax

import effectie.core.CanRestart

/** @author Kevin Lee
  * @since 2023-09-07
  */
trait restart {

  extension [F[*], A](fa: F[A]) {
    def restartUntil(p: A => Boolean)(implicit canRestart: CanRestart[F]): F[A] =
      canRestart.restartUntil(fa)(p)

    def restartWhile(p: A => Boolean)(implicit canRestart: CanRestart[F]): F[A] =
      canRestart.restartWhile(fa)(p)

    def restartOnError(maxRetries: Long)(implicit canRestart: CanRestart[F]): F[A] =
      canRestart.restartOnError(fa)(maxRetries)

    def restartOnErrorIfTrue(p: Throwable => Boolean)(implicit canRestart: CanRestart[F]): F[A] =
      canRestart.restartOnErrorIfTrue(fa)(p)
  }
}
object restart extends restart
