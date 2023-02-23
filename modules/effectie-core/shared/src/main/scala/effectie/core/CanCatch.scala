package effectie.core

import scala.annotation.implicitNotFound

@implicitNotFound(
  """
  Could not find an implicit CanCatch[${F}]. You can probably find it from the effectie.instances package.
  It is recommended to use an instance for Fx instead of an instance for CanCatch.""" +
    compileTimeMessages.ListOfFxInstances
)
trait CanCatch[F[*]] {

  implicit protected def fxCtor: FxCtor[F]

  def flatMapFa[A, B](fa: F[A])(f: A => F[B]): F[B]

  def catchNonFatalThrowable[A](fa: => F[A]): F[Either[Throwable, A]]

  @inline final def catchNonFatal[A, B](
    fb: => F[B]
  )(
    f: PartialFunction[Throwable, A]
  ): F[Either[A, B]] =
    flatMapFa(catchNonFatalThrowable[B](fb)) { ab =>
      ab.fold[F[Either[A, B]]](
        err => {
          f.andThen(a => fxCtor.pureOf(Left(a): Either[A, B]))
            .applyOrElse(err, fxCtor.errorOf)
        },
        b => fxCtor.pureOf(Right(b)),
      )
    }

  @inline final def catchNonFatalEither[A, AA >: A, B](
    fab: => F[Either[A, B]]
  )(
    f: PartialFunction[Throwable, AA]
  ): F[Either[AA, B]] =
    flatMapFa(catchNonFatalThrowable(fab)) { ab =>
      ab.fold(
        err =>
          f.andThen(aa => fxCtor.pureOf(Left(aa): Either[AA, B]))
            .applyOrElse(err, fxCtor.errorOf),
        fxCtor.pureOf,
      )
    }

}

object CanCatch {
  def apply[F[*]: CanCatch]: CanCatch[F] = implicitly[CanCatch[F]]

}
