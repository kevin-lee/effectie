package effectie.core

/** @author Kevin Lee
  * @since 2023-09-07
  */
trait CanRestart[F[*]] {
  def restartWhile[A](fa: F[A])(p: A => Boolean): F[A]

  def restartUntil[A](fa: F[A])(p: A => Boolean): F[A]

  def restartOnError[A](fa: F[A])(maxRetries: Long): F[A]

  def restartOnErrorIfTrue[A](fa: F[A])(p: Throwable => Boolean): F[A]
}
object CanRestart {

  def apply[F[*]: CanRestart]: CanRestart[F] = implicitly[CanRestart[F]]

  implicit def canRestart[F[*]: FxCtor: CanHandleError]: CanRestart[F] = new CanRestart[F] {

    @inline override final def restartWhile[A](fa: F[A])(p: A => Boolean): F[A] =
      FxCtor[F].flatMapFa(fa) { a =>
        if (p(a))
          restartWhile(fa)(p)
        else
          FxCtor[F].pureOf(a)
      }

    @inline override final def restartUntil[A](fa: F[A])(p: A => Boolean): F[A] =
      FxCtor[F].flatMapFa(fa) { a =>
        if (p(a))
          FxCtor[F].pureOf(a)
        else
          restartUntil(fa)(p)
      }

    @inline override final def restartOnError[A](fa: F[A])(maxRetries: Long): F[A] =
      CanHandleError[F].handleNonFatalWith(fa) { err =>
        if (maxRetries > 0)
          restartOnError(fa)(maxRetries - 1)
        else
          FxCtor[F].errorOf(err)
      }

    @inline override final def restartOnErrorIfTrue[A](fa: F[A])(p: Throwable => Boolean): F[A] =
      CanHandleError[F].handleNonFatalWith(fa) { err =>
        if (p(err))
          restartOnErrorIfTrue(fa)(p)
        else
          FxCtor[F].errorOf(err)
      }
  }

}
