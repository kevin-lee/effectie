package effectie.core

import scala.annotation.implicitNotFound

@implicitNotFound(
  """
  Could not find an implicit CanCatch[${F}]. You can probably find it from the effectie.instance package.
  ---
  If you want to use IO from cats-effect 2, try effectie-cats-effect2.
    import effectie.instances.ce2.canCatch._
    // for Scala 3
    import effectie.instances.ce2.canCatch.given

  For cats-effect 3, try effectie-cats-effect3.
    import effectie.instances.ce3.canCatch._
    // for Scala 3
    import effectie.instances.ce3.canCatch.given

  If you want to use Task from Monix 3, try effectie-monix3.
    import effectie.instances.monix3.canCatch._
    // for Scala 3
    import effectie.instances.monix3.canCatch.given

  For Scala's Future, It is just
    import effectie.instances.future.canCatch._
    // for Scala 3
    import effectie.instances.future.canCatch.given

  If you don't want to use any effect but the raw data, you can use the instance for cats.Id
    import effectie.instances.id.canCatch._
    // for Scala 3
    import effectie.instances.id.canCatch.given
  ---
  """
)
trait CanCatch[F[*]] {

  def flatMapFa[A, B](fa: F[A])(f: A => F[B]): F[B]

  def catchNonFatalThrowable[A](fa: => F[A]): F[Either[Throwable, A]]

  @inline final def catchNonFatal[A, B](
    fb: => F[B]
  )(f: PartialFunction[Throwable, A])(implicit fxCtor: FxCtor[F]): F[Either[A, B]] =
    flatMapFa(catchNonFatalThrowable[B](fb)) { ab =>
      ab.fold[F[Either[A, B]]](
        err => {
          f.andThen(a => fxCtor.pureOf(Left(a): Either[A, B]))
            .applyOrElse(err, fxCtor.errorOf)
        },
        b => fxCtor.pureOf(Right(b)),
      )
    }

  @inline final def catchNonFatalEither[A, AA >: A, B](fab: => F[Either[A, B]])(
    f: PartialFunction[Throwable, AA]
  )(implicit fxCtor: FxCtor[F]): F[Either[AA, B]] =
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
