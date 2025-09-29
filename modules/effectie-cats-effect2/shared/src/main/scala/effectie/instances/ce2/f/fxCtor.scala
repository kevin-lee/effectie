package effectie.instances.ce2.f

import cats.effect.Sync
import effectie.core.FxCtor

import scala.util.Try
import scala.util.control.NonFatal

object fxCtor {

  implicit def syncFxCtor[F[*]: Sync]: FxCtor[F] = new FxCtor[F] {

    @inline override final def effectOf[A](a: => A): F[A] = Sync[F].delay(a)

    @inline override final def fromEffect[A](fa: => F[A]): F[A] = Sync[F].defer(fa)

    @inline override final def pureOf[A](a: A): F[A] = Sync[F].pure(a)

    override final def pureOrError[A](a: => A): F[A] =
      try pureOf(a)
      catch {
        case NonFatal(e) => errorOf(e)
      }

    @inline override val unitOf: F[Unit] = Sync[F].unit

    @inline override final def errorOf[A](throwable: Throwable): F[A] = Sync[F].raiseError(throwable)

    @inline override final def fromEither[A](either: Either[Throwable, A]): F[A] = Sync[F].fromEither(either)

    @inline override final def fromOption[A](option: Option[A])(orElse: => Throwable): F[A] =
      Sync[F].fromOption(option, orElse)

    @inline override final def fromTry[A](tryA: Try[A]): F[A] = Sync[F].fromTry(tryA)

    @inline override final def flatMapFa[A, B](fa: F[A])(f: A => F[B]): F[B] = Sync[F].flatMap(fa)(f)
  }

}
