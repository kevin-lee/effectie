package effectie.core

import scala.annotation.implicitNotFound

/** @author Kevin Lee
  * @since 2020-08-17
  */
@implicitNotFound(
  """
  Could not find an implicit CanRecover[${F}]. You can probably find it from the effectie.instances package.
  It is recommended to use an instance for Fx instead of an instance for CanRecover.""" +
    compileTimeMessages.ListOfFxInstances
)
trait CanRecover[F[*]] {

  def recoverFromNonFatalWith[A, AA >: A](fa: => F[A])(handleError: PartialFunction[Throwable, F[AA]]): F[AA]

  @inline final def recoverEitherFromNonFatalWith[A, AA >: A, B, BB >: B](
    fab: => F[Either[A, B]]
  )(
    handleError: PartialFunction[Throwable, F[Either[AA, BB]]]
  ): F[Either[AA, BB]] =
    recoverFromNonFatalWith[Either[A, B], Either[AA, BB]](fab)(handleError)

  def recoverFromNonFatal[A, AA >: A](fa: => F[A])(handleError: PartialFunction[Throwable, AA]): F[AA]

  @inline final def recoverEitherFromNonFatal[A, AA >: A, B, BB >: B](
    fab: => F[Either[A, B]]
  )(
    handleError: PartialFunction[Throwable, Either[AA, BB]]
  ): F[Either[AA, BB]] =
    recoverFromNonFatal[Either[A, B], Either[AA, BB]](fab)(handleError)

}

object CanRecover {

  def apply[F[*]: CanRecover]: CanRecover[F] = implicitly[CanRecover[F]]

}
